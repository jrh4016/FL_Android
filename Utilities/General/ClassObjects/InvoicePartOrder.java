package com.skeds.android.phone.business.Utilities.General.ClassObjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class InvoicePartOrder implements Serializable {
    private int id;
    private String invoiceNumber;
    private String customerName;
    private String invoiceDate;
    public List<PartOrder> partOrder = new ArrayList<PartOrder>();

    public InvoicePartOrder() {
        setId(0);
        setInvoiceNumber("");
        setCustomerName("");
        setInvoiceDate("");
        partOrder = new ArrayList<PartOrder>();
    }

    public int getId() {
        return id;
    }

    public void setId(int value) {
        this.id = value;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String value) {
        this.invoiceNumber = value;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String value) {
        this.customerName = value;
    }

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(String value) {
        this.invoiceDate = value;
    }
}