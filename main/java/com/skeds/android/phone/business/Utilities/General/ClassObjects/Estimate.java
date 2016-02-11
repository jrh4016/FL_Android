package com.skeds.android.phone.business.Utilities.General.ClassObjects;

import java.math.BigDecimal;

public class Estimate extends AbstractInvoice {
    private int servicePlanId;
    private String servicePlanName = "No Service Plan";

    private boolean needSignature;

    private boolean showAgreementComparison;
    private boolean servicePlanUsedForPricing = true;
    public BigDecimal serviceAgreementSavedAmount;

    private int customerId = -1;

    public Estimate() {
        setNumber("");
        setAppointmentType("");

        setNeedSignature(false);
    }

    public void setAgreementComparison(boolean comprassion) {
        showAgreementComparison = comprassion;
    }

    public boolean isAgreementComparison() {
        return showAgreementComparison;
    }

    public void setServicePlanUsedForPricing(boolean pricing) {
        servicePlanUsedForPricing = pricing;
    }

    public boolean isServicePlanUsedForPricing() {
        return servicePlanUsedForPricing;
    }

    public boolean isNeedSignature() {
        return needSignature;
    }

    public void setNeedSignature(boolean value) {
        this.needSignature = value;
    }

    public String getServicePlanName() {
        return servicePlanName;
    }

    public void setServicePlanName(String value) {
        this.servicePlanName = value;
    }

    public int getServicePlanId() {
        return servicePlanId;
    }

    public void setServicePlanId(int value) {
        this.servicePlanId = value;
    }

    public void setCustomerId(String s) {
        try {
            customerId = Integer.parseInt(s);
        } catch (Exception e) {
            customerId = -1;
        }
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }
}