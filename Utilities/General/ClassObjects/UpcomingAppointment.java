package com.skeds.android.phone.business.Utilities.General.ClassObjects;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class UpcomingAppointment implements Serializable {

    private int id;
    private Status status;
    private boolean complete;
    private String apptTypeName;
    private String startTime;
    private String startDate;
    private String endTime;
    private String locationLatitude;
    private String locationLongitude;
    private String locationAddress;
    private String customerOrgName;
    private String customerFirstName;
    private String customerLastName;
    private boolean isOrganization;
    private TimeZone timeZone;

    public UpcomingAppointment() {
        setId(0);
        setStatus(Status.NOT_STARTED);
        setComplete(false);
        setApptTypeName("");
        setStartTime("");
        setStartDate("");
        setEndTime("");
        setLocationLatitude("");
        setLocationLongitude("");
        setLocationAddress("");
        setCustomerOrgName("");
        setCustomerFirstName("");
        setCustomerLastName("");
        setOrganization(false);
    }

    public int getId() {
        return id;
    }

    public void setId(int value) {
        this.id = value;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status value) {
        this.status = value;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean value) {
        this.complete = value;
    }

    public String getApptTypeName() {
        return apptTypeName;
    }

    public void setApptTypeName(String value) {
        this.apptTypeName = value;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String value) {
        this.startTime = value;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String value) {
        this.startDate = value;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String value) {
        this.endTime = value;
    }

    public String getLocationLatitude() {
        return locationLatitude;
    }

    public void setLocationLatitude(String value) {
        this.locationLatitude = value;
    }

    public String getLocationLongitude() {
        return locationLongitude;
    }

    public void setLocationLongitude(String value) {
        this.locationLongitude = value;
    }

    public String getLocationAddress() {
        return locationAddress;
    }

    public void setLocationAddress(String value) {
        this.locationAddress = value;
    }

    public String getCustomerOrgName() {
        return customerOrgName;
    }

    public void setCustomerOrgName(String value) {
        this.customerOrgName = value;
    }

    public String getCustomerFirstName() {
        return customerFirstName;
    }

    public void setCustomerFirstName(String value) {
        this.customerFirstName = value;
    }

    public String getCustomerLastName() {
        return customerLastName;
    }

    public void setCustomerLastName(String value) {
        this.customerLastName = value;
    }

    public boolean isOrganization() {
        return isOrganization;
    }

    public void setOrganization(boolean value) {
        this.isOrganization = value;
    }

    public String getStartTimeWithTimeZoneOffset(TimeZone timeZone){
        return getTimeWithTimezoneOffset(startTime,startDate,timeZone);
    }

    public String getEndTimeWithTimeZoneOffset(TimeZone timeZone){
        return getTimeWithTimezoneOffset(endTime,startDate, timeZone);
    }


    public String getTimeWithTimezoneOffset(String time, String date, TimeZone timeZone) {
        String pattern = "MM/dd/yyyy h:mm aaa";
        Date parsedDate = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        //TimeZone timeZone = TimeZone.getTimeZone()


        simpleDateFormat.setTimeZone(timeZone);
        try {
            parsedDate = simpleDateFormat.parse(date + " " + time);


        } catch (ParseException e) {
            e.printStackTrace();
        }
        String format = simpleDateFormat.format(parsedDate);

        String timePattern = "h:mm aaa";

        SimpleDateFormat simpleTimeFormat= new SimpleDateFormat(timePattern);
        return simpleTimeFormat.format(parsedDate);
    }

    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }

    public TimeZone getTimeZone() {
        return timeZone;
    }
}