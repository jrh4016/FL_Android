package com.skeds.android.phone.business.Utilities.General.ClassObjects;

public enum PaymentType {
    BILL_LATER("To Be Billed Later"), CASH("Cash"), CHECK("Check"), CREDIT_CARD(
            "Credit Card"), PRE_PAID("Prepaid"), DEBIT("Debit");

    private String value;

    private PaymentType(String v) {
        value = v;
    }

    public String getValue() {
        return value;
    }
}