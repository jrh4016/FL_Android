package com.skeds.android.phone.business.Utilities.General.ClassObjects;

import com.skeds.android.phone.business.Utilities.General.Constants;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Invoice extends AbstractInvoice {
    private String appointmentStatus = "";

    private boolean determinePaymentType;
    private boolean allowBillLater;
    private boolean allowPrepaid;
    private boolean acceptCreditCards;
    private boolean forceSignatureOnInvoice;

    private BigDecimal discountPercentageValue = BigDecimal.ZERO;

    private String locationEmail = "";

    private boolean meFinished = true;
    private boolean othersFinished = true;


    private BigDecimal amountDue;

    private List<Payment> paymentsList = new ArrayList<Payment>();

    private List<PieceOfEquipment> equipmentList = new ArrayList<PieceOfEquipment>();

    public String getAppointmentStatus() {
        return this.appointmentStatus;
    }

    public boolean getDeterminePaymentType() {
        return this.determinePaymentType;
    }

    public boolean getAllowBillLater() {
        return this.allowBillLater;
    }

    public boolean getAcceptCreditCards() {
        return this.acceptCreditCards;
    }

    public boolean getForceSignatureOnInvoice() {
        return this.forceSignatureOnInvoice;
    }

    public boolean isMeFinished() {
        return meFinished;
    }

    public boolean isOthersFinished() {
        return othersFinished;
    }

    public void setAppointmentStatus(String value) {
        appointmentStatus = value;
    }

    public void setDeterminePaymentType(boolean value) {
        determinePaymentType = value;
    }

    public void setAllowBillLater(boolean value) {
        allowBillLater = value;
    }

    public void setAcceptCreditCards(boolean value) {
        acceptCreditCards = value;
    }

    public void setForceSignatureOnInvoice(boolean value) {
        forceSignatureOnInvoice = value;
    }

    public void setMeFinished(boolean value) {
        meFinished = value;
    }

    public void setOthersFinished(boolean value) {
        othersFinished = value;
    }

    public Invoice() {
        setNumber("");
        setAppointmentType("");

        setDeterminePaymentType(false);
        setAllowBillLater(false);
        setAcceptCreditCards(false);
        setForceSignatureOnInvoice(false);

    }


    public BigDecimal getAmountDue() {
        return amountDue;
    }

    public void setAmountDue(BigDecimal amountDue) {
        this.amountDue = amountDue;
    }

    public List<Payment> getPaymentsList() {
        return paymentsList;
    }

    public void setPaymentsList(List<Payment> paymentsList) {
        this.paymentsList = paymentsList;
    }

    public List<PieceOfEquipment> getEquipmentList() {
        return equipmentList;
    }

    public void setEquipmentList(List<PieceOfEquipment> equipmentList) {
        this.equipmentList = equipmentList;
    }

    public boolean isAllowPrepaid() {
        return allowPrepaid;
    }

    public void setAllowPrepaid(boolean allowPrepaid) {
        this.allowPrepaid = allowPrepaid;
    }

    public String getLocationEmail() {
        return locationEmail;
    }

    public void setLocationEmail(String locationEmail) {
        this.locationEmail = locationEmail;
    }

    public BigDecimal getDiscountPercentageValue() {
        return discountPercentageValue;
    }

    public void setDiscountPercentageValue(BigDecimal discountPercentageValue) {
        this.discountPercentageValue = discountPercentageValue;
    }

}