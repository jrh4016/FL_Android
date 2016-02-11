package com.skeds.android.phone.business.Utilities.General.ClassObjects;

import java.io.Serializable;
import java.math.BigDecimal;

public class Payment implements Serializable {

    private String paymentType;
    private int checkNumber;
    public BigDecimal paymentAmount;
    public String date;

    private String creditCardShortNumber = "";
    private String creditCardExpirationDate = "";
    private String creditCardAuthCode = "";

    public Payment() {
        setPaymentType("");
        setCheckNumber(0);

        paymentAmount = new BigDecimal("0.00");
        paymentAmount.setScale(2, BigDecimal.ROUND_HALF_UP);

        date = "";
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String value) {
        this.paymentType = value;
    }

    public int getCheckNumber() {
        return checkNumber;
    }

    public void setCheckNumber(int value) {
        this.checkNumber = value;
    }

    public void setCardShortNumber(String number) {
        creditCardShortNumber = number;
    }

    public String getCardShortNumber() {
        return creditCardShortNumber;
    }

    public void setCardExpirationDate(String date) {
        creditCardExpirationDate = date;
    }

    public String getCardExpirationDate() {
        return creditCardExpirationDate;
    }

    public void setCardAuthCode(String code) {
        creditCardAuthCode = code;
    }

    public String getCardAuthCode() {
        return creditCardAuthCode;
    }

}