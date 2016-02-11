package com.skeds.android.phone.business.Utilities.General.ClassObjects;

import com.skeds.android.phone.business.Utilities.General.ClassObjects.PriceBook.TaxValue;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AbstractInvoice implements Serializable {

    private int id;
    private String date;

    private BigDecimal total;
    private BigDecimal netTotal;
    private BigDecimal discount;
    private BigDecimal tax;
    private BigDecimal taxRate;
    private String number = "-1";
    private String appointmentType;
    private int appointmentId = 0;

    private String signature = "";
    private String signer = "";
    private String customerEmail = "";

    private String description = "";
    private String recommendation = "";

    private boolean acceptAmericanExpress;
    private boolean acceptDiscover;
    private boolean acceptMasterCard;
    private boolean acceptVisa;

    private int locationId = -1;

    private boolean isTaxable;

    private boolean isLocationTaxable;

    private Customer customer = new Customer();

    private List<ServiceProvider> serviceProviderList = new ArrayList<ServiceProvider>();

    private List<LineItem> lineItems = new ArrayList<LineItem>();

    private List<Payment> payments = new ArrayList<Payment>();

    private List<TaxValue> taxes = new ArrayList<TaxValue>();


    private ArrayList<LineItem> allLineItems;



    public AbstractInvoice() {
        setId(0);
        setDate("");

        total = new BigDecimal("0.00");
        netTotal = new BigDecimal("0.00");
        discount = new BigDecimal("0.00");
        tax = new BigDecimal("0.00");
        taxRate = new BigDecimal("0.00000");

        total.setScale(2, BigDecimal.ROUND_HALF_UP);
        total.setScale(2, BigDecimal.ROUND_HALF_UP);
        netTotal.setScale(2, BigDecimal.ROUND_HALF_UP);
        discount.setScale(2, BigDecimal.ROUND_HALF_UP);
        tax.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public int getId() {
        return this.id;
    }

    public String getDate() {
        return this.date;
    }

    public String getNumber() {
        return this.number;
    }

    public String getAppointmentType() {
        return this.appointmentType;
    }

    public void setId(int value) {
        id = value;
    }

    public void setDate(String value) {
        date = value;
    }

    public void setNumber(String value) {
        number = value;
    }

    public void setAppointmentType(String value) {
        appointmentType = value;
    }

    public List<ServiceProvider> getServiceProviderList() {
        return serviceProviderList;
    }

    public void setServiceProviderList(List<ServiceProvider> serviceProviderList) {
        this.serviceProviderList = serviceProviderList;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getNetTotal() {
        return netTotal;
    }

    public void setNetTotal(BigDecimal netTotal) {
        this.netTotal = netTotal;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }


    public BigDecimal getTax() {
        return tax;
    }

    public void setTax(BigDecimal tax) {
        this.tax = tax;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getSigner() {
        return signer;
    }

    public void setSigner(String signer) {
        this.signer = signer;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    public boolean isAcceptAmericanExpress() {
        return acceptAmericanExpress;
    }

    public void setAcceptAmericanExpress(boolean acceptAmericanExpress) {
        this.acceptAmericanExpress = acceptAmericanExpress;
    }

    public boolean isAcceptDiscover() {
        return acceptDiscover;
    }

    public void setAcceptDiscover(boolean acceptDiscover) {
        this.acceptDiscover = acceptDiscover;
    }

    public boolean isAcceptMasterCard() {
        return acceptMasterCard;
    }

    public void setAcceptMasterCard(boolean acceptMasterCard) {
        this.acceptMasterCard = acceptMasterCard;
    }

    public boolean isAcceptVisa() {
        return acceptVisa;
    }

    public void setAcceptVisa(boolean acceptVisa) {
        this.acceptVisa = acceptVisa;
    }

    public List<LineItem> getLineItems() {
        return lineItems;
    }

    public void setLineItems(List<LineItem> lineItems) {
        this.lineItems = lineItems;
    }

    public List<Payment> getPayments() {
        return payments;
    }

    public void setPayments(List<Payment> payments) {
        this.payments = payments;
    }

    public List<TaxValue> getTaxes() {
        return taxes;
    }

    public void setTaxes(List<TaxValue> taxes) {
        this.taxes = taxes;
    }

    public void setLocationId(String s) {
        try {
            locationId = Integer.parseInt(s);
        } catch (Exception e) {
            locationId = -1;
        }
    }

    Map<TaxRate, BigDecimal> totalTaxableGroups = new HashMap<TaxRate, BigDecimal>();

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public boolean isTaxable() {
        return isTaxable;
    }

    public void setTaxable(boolean isTaxable) {
        this.isTaxable = isTaxable;
    }

    public boolean isLocationTaxable() {
        return isLocationTaxable;
    }

    public void setLocationTaxable(boolean isLocationTaxable) {
        this.isLocationTaxable = isLocationTaxable;
    }

    public void setExtendedInfoTypes(Map<TaxRate, BigDecimal> totalTaxableGroups) {
        this.totalTaxableGroups = totalTaxableGroups;
    }

    public Map<TaxRate, BigDecimal>  getExtendedInfoTypes() {
       return this.totalTaxableGroups ;
    }

    public void setAllLineItems(ArrayList<LineItem> allLineItems) {
        this.allLineItems = allLineItems;
    }

    public ArrayList<LineItem> getAllLineItems() {
        return allLineItems;
    }
}
