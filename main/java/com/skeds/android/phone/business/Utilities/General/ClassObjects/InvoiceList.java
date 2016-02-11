package com.skeds.android.phone.business.Utilities.General.ClassObjects;

import java.io.Serializable;

public class InvoiceList implements Serializable {

    private int id;
    private String customerName;
    private int appointmentId;
    private String date;
    private boolean closed;
    private String description;
    private String number;

    public InvoiceList() {
        setId(0);
        setCustomerName("");
        setAppointmentId(0);
        setDate("");
        setClosed(false);
        setDescription("");
        setNumber("");
    }

    public int getId() {
        return id;
    }

    public void setId(int value) {
        this.id = value;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String value) {
        this.customerName = value;
    }

    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int value) {
        this.appointmentId = value;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String value) {
        this.date = value;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean value) {
        this.closed = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String value) {
        this.description = value;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String value) {
        this.number = value;
    }
}