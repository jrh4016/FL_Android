package com.skeds.android.phone.business.Utilities.General.ClassObjects;

import com.skeds.android.phone.business.Utilities.General.UserUtilitiesSingleton;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class LineItem implements Serializable {

    private int id;
    public List<Long> rateIds = new ArrayList<Long>();
    private String name;
    private String description;

    private boolean labor = false;

    public BigDecimal cost;
    public BigDecimal additionalCost;

    public BigDecimal comparisonCost = new BigDecimal("0.00").setScale(2,
            BigDecimal.ROUND_HALF_UP);

    private boolean usingAdditionalCost;
    private BigDecimal quantity;

    public BigDecimal finalCost;

    private boolean removable = true;
    private boolean taxable;
    private int serviceTypeId;
    private boolean isCustomPrice;
    private boolean isCustomLineItem;
    private boolean isUserAdded;

    private Recommendation recommendation;

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }

    public static enum Recommendation {
        ACCEPTED,
        DECLINED,
        BLANK
    };

    boolean active=true;

    public List<PricebookProductCost> productCost = new ArrayList<PricebookProductCost>();

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public BigDecimal getQuantity() {
        return this.quantity;
    }

    public boolean getRemovable() {
        return this.removable;
    }

    public boolean getTaxable() {
        return this.taxable;
    }

    public int getServiceTypeId() {
        return this.serviceTypeId;
    }

    public void setId(int value) {
        id = value;
    }

    public void setName(String value) {
        name = value;
    }

    public void setQuantity(BigDecimal value) {
        quantity = value;
    }

    public void setRemovable(boolean value) {
        removable = value;
    }

    public void setTaxable(boolean value) {
        taxable = value;
    }

    public void setServiceTypeId(int value) {
        serviceTypeId = value;
    }

    public Recommendation getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(Recommendation Recommendation) {
        this.recommendation = Recommendation;
    }

    public LineItem() {
        setId(0);
        setName("");
        setDescription("");

        cost = new BigDecimal("0.00").setScale(2, BigDecimal.ROUND_HALF_UP);
        additionalCost = new BigDecimal("0.00").setScale(2,
                BigDecimal.ROUND_HALF_UP);
        // setCost(0.0);
        // setAdditionalCost(0.0);
        setUsingAdditionalCost(false);
        setQuantity(new BigDecimal("0.00"));
        finalCost = new BigDecimal("0.00")
                .setScale(2, BigDecimal.ROUND_HALF_UP);
        // setFinalCost(0.0);
        setRemovable(true);
        setTaxable(false);
        setServiceTypeId(0);
        setCustomPrice(false);
        setCustomLineItem(false);
        setUserAdded(false);

        resetRates();

        setRecommendation(Recommendation.BLANK);
    }

    public boolean isCustomPrice() {
        return isCustomPrice;
    }

    public void setCustomPrice(boolean value) {
        this.isCustomPrice = value;
    }

    public void setLabor(boolean value) {
        this.labor = value;
    }

    public boolean isLabor() {
        return labor;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String value) {
        this.description = value;
    }

    public boolean isUsingAdditionalCost() {
        return usingAdditionalCost;
    }

    public void setUsingAdditionalCost(boolean value) {
        this.usingAdditionalCost = value;
    }

    public boolean isCustomLineItem() {
        return isCustomLineItem;
    }

    public void setCustomLineItem(boolean value) {
        this.isCustomLineItem = value;
    }

    public boolean isUserAdded() {
        return isUserAdded;
    }

    public void setUserAdded(boolean value) {
        this.isUserAdded = value;
    }

    public LineItem copy() {
        LineItem item = new LineItem();
        item.setId(this.id);
        item.setName(this.name);
        item.setDescription(this.description);

        item.cost = this.cost;
        item.additionalCost = this.additionalCost;
        item.setUsingAdditionalCost(this.usingAdditionalCost);
        item.setQuantity(this.quantity);
        item.finalCost = this.finalCost;
        // setFinalCost(0.0);
        item.setRemovable(this.removable);
        item.setTaxable(this.taxable);
        item.setServiceTypeId(this.serviceTypeId);
        item.setCustomPrice(this.isCustomPrice);
        item.setCustomLineItem(this.isCustomLineItem);
        item.setUserAdded(this.isUserAdded);
        item.productCost = this.productCost;
        item.rateIds = this.rateIds;

        return item;
    }

    public void resetRates() {
        rateIds = new ArrayList<Long>();
        int i = 0;
        for (TaxRateType trt : UserUtilitiesSingleton.getInstance().user.getCountryInfo().getTaxRateTypes()) {
            rateIds.add(-1L * ++i);
        }
    }

    public BigDecimal getFinalCost() {
        return finalCost;
    }

    public void setFinalCost(BigDecimal finalCost) {
        this.finalCost = finalCost;
    }

    public List<Long> getRateIds() {
        return rateIds;
    }

    public void setRateIds(List<Long> rateIds) {
        this.rateIds = rateIds;
    }
}