package com.skeds.android.phone.business.Utilities.General.ClassObjects;



import java.io.Serializable;

public class TimeSpan implements Serializable {
    public enum SpanType {IN, INTERVAL, OUT};
    private String timeWorked= "";
    private String customerName="";
    private String fromTo="";
    private String status="";

    SpanType spanType = SpanType.INTERVAL;

    public String getTimeWorked() {
        return this.timeWorked;
    }

    public void setTimeWorked(String value) {
        timeWorked = value;
    }

    public String getCustomerName() {
        return this.customerName;
    }

    public void setCustomerName(String value) {
        customerName = value;
    }

    public String getFromTo() {
        return this.fromTo;
    }

    public void setFromTo(String value) {

        if (value.startsWith("Started at"))
            spanType = SpanType.IN;

        fromTo = value;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String value) {
        status = value;
    }

    public SpanType getSpanType() {
        return spanType;
    }

    public TimeSpan() {

    }
}