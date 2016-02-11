package com.skeds.android.phone.business.Utilities.General;

import com.skeds.android.phone.business.Utilities.General.ClassObjects.AbstractInvoice;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Country;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.LineItem;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.PriceBook.TaxValue;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.TaxRate;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.TaxRateType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author vshukaila, daliashkevich
 */
public class TaxAmountCalculator {

    private static final String LOG_DEBUG = "TAX_AMOUNT_CALCULATOR";

    private static final int LARGE_DECIMAL_SCALE = 10;
    private static final int DECIMAL_SCALE = 2;


    /**
     * Calculates actual tax amount for the {@link AbstractInvoice}
     *
     * @param invoice
     * @return
     */
    public <T extends AbstractInvoice> BigDecimal calculateTaxAmount(T invoice) {
        if (!invoice.isTaxable() || !invoice.isLocationTaxable())
            return BigDecimal.ZERO;

        if (AppDataSingleton.getInstance().getAppointment().getCustomer() != null)
            if (!AppDataSingleton.getInstance().getAppointment().getCustomer().isTaxable() ||
                    !AppDataSingleton.getInstance().getAppointment().isLocationTaxable())
                return BigDecimal.ZERO;

        return calculateTax(invoice);
    }

    private <T extends AbstractInvoice> BigDecimal calculateTax(T invoice) {
        Country ownerCountry = UserUtilitiesSingleton.getInstance().user.getCountryInfo();
        if (ownerCountry != null) {
            boolean useExtendedTax = ownerCountry.isUseExtendedTax();
            if (useExtendedTax) {
                return calculateTaxExtended(invoice);
            }
        }
        return calculateTaxUS(invoice);
    }

    /**
     * Finds the right tax rate for full {@link AbstractInvoice}. Same tax rate for all taxable line items on invoice
     *
     * @param invoice
     * @return
     */
    private <T extends AbstractInvoice> BigDecimal findTaxRate(T invoice) {
        BigDecimal taxRate = invoice.getTaxRate();
        if (taxRate.compareTo(BigDecimal.ZERO) == 0) {
            taxRate = AppDataSingleton.getInstance().getCustomerEstimateTaxRate();
        }
        return taxRate;
    }

    /**
     * Calculates tax amount using extended tax strategy. This means few tax rates for one line item
     *
     * @param invoice
     * @param savedAmountCalculator
     * @return
     */
    private BigDecimal calculateTaxExtended(AbstractInvoice invoice) {
        BigDecimal totalNonTaxable = BigDecimal.ZERO.setScale(DECIMAL_SCALE, RoundingMode.HALF_UP);
        BigDecimal totalTaxable = BigDecimal.ZERO.setScale(DECIMAL_SCALE, RoundingMode.HALF_UP);
        //this is taxes grouped by different tax rates
        Map<TaxRate, BigDecimal> totalTaxableGroups = new HashMap<TaxRate, BigDecimal>();

        //init rates map for quick access
        List<TaxRate> taxRates = UserUtilitiesSingleton.getInstance().user.getCountryInfo().getTaxRates();
        Map<Long, TaxRate> taxRatesMap = new HashMap<Long, TaxRate>();
        for (TaxRate rate : taxRates) {
            taxRatesMap.put(rate.getId(), rate);
        }

        //group line items amounts by custom tax rates
        for (LineItem lineItem : invoice.getLineItems()) {
            if (lineItem != null) {
                if (lineItem.getRecommendation() == LineItem.Recommendation.DECLINED)
                    continue;
                BigDecimal lineItemTotalCost = getLineItemTotalCost(lineItem);
                //this logic only for parts or for repair items if separateOutSalesTax flag is not set
                if (lineItem.getTaxable()) {
                    List<Long> rateIds = lineItem.getRateIds();
                    boolean addedToTaxable = false;
                    for (Long rateId : rateIds) {
                        if (rateId > 0) {
                            TaxRate taxRate = taxRatesMap.get(rateId);
                            //add amount to necessary group
                            if (lineItemTotalCost != null) {
                                BigDecimal totalForGroup = totalTaxableGroups.get(taxRate);
                                if (totalForGroup == null) {
                                    totalForGroup = BigDecimal.ZERO;
                                }
                                totalForGroup = totalForGroup.add(lineItemTotalCost);
                                totalTaxableGroups.put(taxRate, totalForGroup);
                                //also add to total taxable value
                                if (!addedToTaxable) {
                                    totalTaxable = totalTaxable.add(lineItemTotalCost);
                                    addedToTaxable = true;
                                }
                            }
                        }
                    }
                    //add to necessary groups of items for tax rates
                } else {
                    totalNonTaxable = totalNonTaxable.add(lineItemTotalCost);
                }
            }
        }
        boolean applySalesTaxToDiscount = AppDataSingleton.getInstance().isCalculateSalesTaxFirst();
        if (!isBigDecimalEmpty(invoice.getDiscount()) && !applySalesTaxToDiscount) {
            substractDiscountOnTaxable(totalTaxableGroups, totalTaxable, totalNonTaxable, invoice.getDiscount());
        }
        //count total tax and assign tax value for invoice
        BigDecimal taxValue = BigDecimal.ZERO;

        if (isGroupTaxesByColumns(taxRatesMap)) {
            //if the tax column names are empty than we should use names of TaxRate objects
            taxValue = calculateTaxesByRatesForTaxable(totalTaxableGroups, invoice);
        } else {
            //in that case only taxRateType grouping should be applied to invoice
            taxValue = calculateTaxesByTypesForTaxable(totalTaxableGroups, invoice);
        }

        invoice.setExtendedInfoTypes(totalTaxableGroups);
        return roundBigDecimal(taxValue);
    }

    private boolean isGroupTaxesByColumns(Map<Long, TaxRate> taxRatesMap) {
        Map<String, Integer> ratesPerTypes = new HashMap<String, Integer>();
        List<TaxRateType> taxRateTypes = UserUtilitiesSingleton.getInstance().user.getCountryInfo().getTaxRateTypes();
        for (TaxRateType type : taxRateTypes) {
            ratesPerTypes.put(type.getType(), 0);
        }
        for (TaxRate rate : taxRatesMap.values()) {
            Integer ratePerType = ratesPerTypes.get(rate.getType());
            if (ratePerType > 1) {
                return false;
            }
            ratesPerTypes.put(rate.getType(), ratePerType + 1);
        }
        return true;
    }

    /**
     * Calculates and sets to invoice tax values grouped by {@link TaxRateType}
     *
     * @param totalTaxableGroups for
     * @param invoice
     * @param owner
     */
    private BigDecimal calculateTaxesByTypesForTaxable(Map<TaxRate, BigDecimal> totalTaxableGroups, AbstractInvoice invoice) {
        BigDecimal result = BigDecimal.ZERO;
        //this is tax grouped by types(TAX1,TAX2)
        Map<String, BigDecimal> taxesByTypes = new HashMap<String, BigDecimal>();
        for (TaxRate rate : totalTaxableGroups.keySet()) {
            BigDecimal totalForGroup = totalTaxableGroups.get(rate);
            BigDecimal taxForGroup = roundBigDecimal(totalForGroup.multiply(rate.getValue()));
            //add to total tax
            String type = rate.getType();
            BigDecimal taxByType = taxesByTypes.get(type);
            if (taxByType == null) {
                taxByType = BigDecimal.ZERO;
            }
            taxByType = taxByType.add(taxForGroup);
            taxesByTypes.put(type, taxByType);
        }
        assignTaxesOfTypesToTaxable(invoice, taxesByTypes);
        if (!taxesByTypes.isEmpty()) {
            List<TaxRateType> taxRateTypes = UserUtilitiesSingleton.getInstance().user.getCountryInfo().getTaxRateTypes();
            for (TaxRateType type : taxRateTypes) {
                BigDecimal byType = taxesByTypes.get(type.getType());
                if (byType != null) {
                    result = result.add(byType);
                }
            }
        }
        return result;
    }

    /**
     * Calculates and sets to invoice tax values grouped by {@link TaxRate}
     *
     * @param totalTaxableGroups
     * @param invoice
     * @param owner
     */
    private BigDecimal calculateTaxesByRatesForTaxable(Map<TaxRate, BigDecimal> totalTaxableGroups, AbstractInvoice invoice) {
        BigDecimal result = BigDecimal.ZERO;
        //this is tax grouped by TaxRate object
        Map<TaxRate, BigDecimal> taxesByRates = new HashMap<TaxRate, BigDecimal>();
        for (TaxRate rate : totalTaxableGroups.keySet()) {
            BigDecimal totalForGroup = totalTaxableGroups.get(rate);
            BigDecimal taxForGroup = roundBigDecimal(totalForGroup.multiply(rate.getValue()));
            result = result.add(taxForGroup);
            //add to total tax
            taxesByRates.put(rate, taxForGroup);
        }
        assignTaxesOfRatesToTaxableClean(taxesByRates, invoice);
        return result;
    }

    /**
     * Calculates tax value for {@link TaxRate} of different {@link TaxRateType} and sets it to invoice as {@link TaxValue}. Each for different {@link TaxRateType}.
     * In case if there is no tax for {@link TaxRateType} than {@link TaxValue} is create with ZERO value
     *
     * @param invoice
     * @param owner
     * @param taxesByTypes
     */
    private void assignTaxesOfTypesToTaxable(AbstractInvoice invoice, Map<String, BigDecimal> taxesByTypes) {
        cleanDuplicateTypes(invoice);
        //set tax values for invoice
        List<TaxRateType> taxRateTypes = UserUtilitiesSingleton.getInstance().user.getCountryInfo().getTaxRateTypes();
        for (TaxRateType type : taxRateTypes) {
            //get tax from invoice for current tax rate type
            TaxValue tax = null;
            List<TaxValue> taxes = invoice.getTaxes();
            for (TaxValue taxValue : taxes) {
                if (taxValue.getTaxRate().getType().equals(type.getType())) {
                    tax = taxValue;
                    break;
                }
            }
            if (tax == null) {
                TaxRate rate = new TaxRate();
                rate.setType(type.getType());
                rate.setName(type.getName());
                tax = new TaxValue();
                tax.setTaxRate(rate);
                tax.setValue(BigDecimal.ZERO);
                taxes.add(tax);
            }
            BigDecimal taxValueForType = taxesByTypes.get(type.getType());
            if (taxValueForType == null) {
                taxValueForType = BigDecimal.ZERO;
            }
            tax.setValue(taxValueForType);
        }
    }

    /**
     * Calculates tax value for {@link TaxRate} of different {@link TaxRateType} and sets it to invoice as {@link TaxValue}. Each for different {@link TaxRateType}.
     * In case if there is no tax for {@link TaxRateType} than {@link TaxValue} is create with ZERO value
     *
     * @param invoice
     * @param owner
     * @param taxesByRates
     */
    private void assignTaxesOfRatesToTaxableClean(Map<TaxRate, BigDecimal> taxesByRates, AbstractInvoice invoice) {
        cleanDuplicateRates(invoice);
        //set tax values for invoice
        assignTaxesOfRatesToTaxable(taxesByRates, invoice);
    }


    private void assignTaxesOfRatesToTaxable(Map<TaxRate, BigDecimal> taxesByRates, AbstractInvoice invoice) {
        List<TaxValue> taxes = invoice.getTaxes();
        List<TaxValue> taxesToRemove = new ArrayList<TaxValue>();
        for (TaxValue tax : taxes) {
            TaxRate rate = tax.getTaxRate();
            BigDecimal value = taxesByRates.get(rate);
            if (value == null) {
                taxesToRemove.add(tax);
            }
        }
        List<TaxValue> taxesToRemain = new ArrayList<TaxValue>();
        for (TaxRate rate : taxesByRates.keySet()) {
            TaxValue tax = null;
            for (TaxValue t : taxes) {
                String type = t.getTaxRate().getType();
                if (rate.getType().equals(type)) {
                    tax = t;
                    break;
                }
            }
            if (tax == null) {
                tax = new TaxValue();
                tax.setTaxRate(rate);
                tax.setValue(BigDecimal.ZERO);
            }

            BigDecimal taxValueForRate = taxesByRates.get(rate);
            if (taxValueForRate == null) {
                taxValueForRate = BigDecimal.ZERO;
            }
            tax.setValue(taxValueForRate);
            taxesToRemain.add(tax);
        }
        invoice.setTaxes(taxesToRemain);
    }

    /**
     * Cleans {@link TaxValue} of invoice that have same {@link TaxRate}
     * The value is added to left one {@link TaxValue}
     *
     * @param invoice
     */
    private void cleanDuplicateRates(AbstractInvoice invoice) {
        List<TaxValue> taxesCollection = invoice.getTaxes();
        Map<TaxRate, Integer> taxesByRatesNumber = new HashMap<TaxRate, Integer>();
        Map<TaxRate, TaxValue> taxesByRates = new HashMap<TaxRate, TaxValue>();
        List<TaxValue> taxesToRemain = new ArrayList<TaxValue>();
        for (TaxValue tax : taxesCollection) {
            TaxValue TaxValue = taxesByRates.get(tax.getTaxRate());
            Integer number = taxesByRatesNumber.get(tax.getTaxRate());
            if (number == null) {
                number = 0;
            }
            if (TaxValue == null) {
                TaxValue = tax;
                taxesByRates.put(tax.getTaxRate(), TaxValue);
            }
            number = number + 1;
            taxesByRatesNumber.put(tax.getTaxRate(), number);
            if (number > 1) {
                TaxValue.setValue(TaxValue.getValue().add(tax.getValue()));
            } else {
                taxesToRemain.add(TaxValue);
            }
        }
        invoice.setTaxes(taxesToRemain);
    }

    /**
     * Cleans {@link TaxValue} of invoice that have same {@link TaxRate}
     * The value is added to left one {@link TaxValue}
     *
     * @param invoice
     */
    private void cleanDuplicateTypes(AbstractInvoice taxable) {
        List<TaxValue> taxesCollection = taxable.getTaxes();
        Map<String, Integer> taxesByTypesNumber = new HashMap<String, Integer>();
        Map<String, TaxValue> taxesByTypes = new HashMap<String, TaxValue>();
        List<TaxValue> taxesToRemain = new ArrayList<TaxValue>();
        for (TaxValue tax : taxesCollection) {
            String type = tax.getTaxRate().getType();
            TaxValue TaxValue = taxesByTypes.get(type);
            Integer number = taxesByTypesNumber.get(type);
            if (number == null) {
                number = 0;
            }
            if (TaxValue == null) {
                TaxValue = tax;
                taxesByTypes.put(type, TaxValue);
            }
            number = number + 1;
            taxesByTypesNumber.put(type, number);
            if (number > 1) {
                TaxValue.setValue(TaxValue.getValue().add(tax.getValue()));
            } else {
                taxesToRemain.add(TaxValue);
            }
        }
        taxable.setTaxes(taxesToRemain);
    }

    /**
     * Calculates tax amount for the countries with US simple tax strategy
     *
     * @param invoice
     * @param savedAmountCalculator
     * @return
     */
    private BigDecimal calculateTaxUS(AbstractInvoice invoice) {
        BigDecimal totalTaxable = BigDecimal.ZERO.setScale(DECIMAL_SCALE, RoundingMode.HALF_UP);
        BigDecimal totalNonTaxable = BigDecimal.ZERO.setScale(DECIMAL_SCALE, RoundingMode.HALF_UP);

        for (LineItem lineItem : invoice.getLineItems()) {
            if (lineItem != null) {
                if (lineItem.getRecommendation() == LineItem.Recommendation.DECLINED)
                    continue;
                BigDecimal totalCost = getLineItemTotalCost(lineItem);
                if (lineItem.getTaxable()) {
                    totalTaxable = totalTaxable.add(totalCost);
                } else {
                    totalNonTaxable = totalNonTaxable.add(totalCost);
                }
            }
        }
        final boolean applySalesTaxToDiscount = AppDataSingleton.getInstance().isCalculateSalesTaxFirst();
        if (!isBigDecimalEmpty(invoice.getDiscount()) && !applySalesTaxToDiscount)
            totalTaxable = substractDiscountOnTaxable(totalTaxable, totalNonTaxable, invoice.getDiscount());
        BigDecimal taxRate = findTaxRate(invoice);
        return roundBigDecimal(totalTaxable.multiply(taxRate));
    }

    private BigDecimal getLineItemTotalCost(LineItem lineItem) {
        BigDecimal itemCost = BigDecimal.ZERO;
        if (lineItem.isUserAdded()) {
            if (lineItem.isUsingAdditionalCost()) {
                itemCost = lineItem.additionalCost;
            } else {
                itemCost = lineItem.cost;
            }
        } else {
            itemCost = lineItem.cost;
        }
        itemCost = itemCost.setScale(DECIMAL_SCALE, RoundingMode.HALF_UP);
        itemCost = roundBigDecimal(itemCost.multiply(lineItem.getQuantity().setScale(DECIMAL_SCALE, RoundingMode.HALF_UP)));
        return itemCost;
    }

    /**
     * Counts tax for the amount of discount. Some businesses need to include amount of discount into tax
     *
     * @param totalTaxable
     * @param totalNonTaxable
     * @param discountAmount
     * @return
     */
    private void substractDiscountOnTaxable(Map<TaxRate, BigDecimal> totalTaxableGroups, BigDecimal totalTaxable, BigDecimal totalNonTaxable, BigDecimal discountAmount) {
        // if @code{applySalesTaxToDicsount} is true then we should first
        // subtract the discount
        // and only then count the tax value
        // count the rate of discount that is applied only to the taxable amount
        BigDecimal taxableDiscountRate = countTaxableDiscountRate(totalTaxable.setScale(LARGE_DECIMAL_SCALE, RoundingMode.HALF_UP), totalNonTaxable.setScale(LARGE_DECIMAL_SCALE, RoundingMode.HALF_UP));

        for (TaxRate rate : totalTaxableGroups.keySet()) {
            BigDecimal groupToTotalRate;
            BigDecimal taxableForGroup = totalTaxableGroups.get(rate);
            //count rate of this group to total taxable value to get
            //the value which we should subtract from this particular total;
            if (totalTaxable.compareTo(BigDecimal.ZERO) == 0) {
                groupToTotalRate = roundBigDecimal(BigDecimal.ZERO);
            } else {
                groupToTotalRate = roundBigDecimal(taxableForGroup).divide(roundBigDecimal(totalTaxable), LARGE_DECIMAL_SCALE, RoundingMode.HALF_UP);
            }
            //now count the value that should be subtracted from this group
            BigDecimal taxableDiscount = roundBigDecimal(discountAmount.multiply(taxableDiscountRate.multiply(groupToTotalRate)));
            taxableForGroup = roundBigDecimal(taxableForGroup.subtract(taxableDiscount));

            totalTaxableGroups.put(rate, taxableForGroup);
        }
    }

    /**
     * Counts tax for the amount of discount. Some businesses need to include amount of discount into tax
     *
     * @param totalTaxable
     * @param totalNonTaxable
     * @param discountAmount
     * @return
     */
    private BigDecimal substractDiscountOnTaxable(BigDecimal totalTaxable, BigDecimal totalNonTaxable, BigDecimal discountAmount) {
        // if @code{applySalesTaxToDicsount} is true then we should first
        // subtract the discount
        // and only then count the tax value
        // count the rate of discount that is applied only to the taxable amount
        BigDecimal taxableDiscount = countTaxableDiscountRate(totalTaxable, totalNonTaxable).multiply(discountAmount);
        // count the part of discount value that corresponds to the taxable
        // value
        return totalTaxable.subtract(taxableDiscount).setScale(DECIMAL_SCALE, RoundingMode.HALF_UP);
    }

    /**
     * Counts a rate for the taxable discount. Needs when some line items are taxable and others are not.
     *
     * @param totalTaxable
     * @param totalNonTaxable
     * @return
     */
    private BigDecimal countTaxableDiscountRate(BigDecimal totalTaxable, BigDecimal totalNonTaxable) {
        if (isBigDecimalEmpty(totalTaxable.add(totalNonTaxable))) {
            return BigDecimal.ZERO.setScale(DECIMAL_SCALE);
        }

        final BigDecimal divisor = totalTaxable.add(totalNonTaxable);
        if (isBigDecimalEmpty(divisor)) {
            return BigDecimal.ZERO.setScale(DECIMAL_SCALE);
        }

        //use large scale here to get more exact rate
        return totalTaxable.divide(divisor, LARGE_DECIMAL_SCALE, RoundingMode.HALF_UP);
    }

    private boolean isBigDecimalEmpty(BigDecimal value) {
        return value != null && value.compareTo(BigDecimal.ZERO) == 0;
    }

    private BigDecimal roundBigDecimal(BigDecimal value) {
        return value.setScale(DECIMAL_SCALE, RoundingMode.HALF_UP);
    }

}
