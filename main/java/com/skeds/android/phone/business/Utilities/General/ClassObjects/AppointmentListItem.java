package com.skeds.android.phone.business.Utilities.General.ClassObjects;

import com.skeds.android.phone.business.Utilities.General.UserUtilitiesSingleton;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class AppointmentListItem implements Serializable {

    private int id;
    private String startDate;
    private String label;
    private Status status;
    private String startTime;
    private String location;
    private String workOrderNumber = "";
    private String endTime;
    private TimeZone timeZone;


    public AppointmentListItem() {
        setId(0);
        setStartDate("");
        setLabel("");
        // setStatus(new Status());
        setStartTime("");
        setLocation("");
    }

    public int getId() {
        return id;
    }

    public void setId(int value) {
        this.id = value;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String value) {
        this.startDate = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String value) {
        this.label = value;
    }

    public String getWorkOrderNumber() {
        return workOrderNumber;
    }

    public void setWorkOrderNumber(String value) {
        this.workOrderNumber = value;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status value) {
        this.status = value;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String value) {
        this.startTime = value;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String value) {
        this.location = value;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getEndTime() {
        return endTime;
    }


    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }

    public TimeZone getTimeZone() {
        return timeZone;
    }
}