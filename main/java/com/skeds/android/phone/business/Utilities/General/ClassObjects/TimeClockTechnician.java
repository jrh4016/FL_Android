package com.skeds.android.phone.business.Utilities.General.ClassObjects;

import java.io.Serializable;

public class TimeClockTechnician implements Serializable {

    private int id;
    private String name;
    private String timeClockMethod;
    private String timeClockMethodDate;

    public TimeClockTechnician() {
        setId(0);
        setName("");
        setTimeClockMethod("");
        setTimeClockMethodDate("");
    }

    public int getId() {
        return id;
    }

    public void setId(int value) {
        this.id = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public String getTimeClockMethod() {
        return timeClockMethod;
    }

    public void setTimeClockMethod(String value) {
        this.timeClockMethod = value;
    }

    public String getTimeClockMethodDate() {
        return timeClockMethodDate;
    }

    public void setTimeClockMethodDate(String value) {
        this.timeClockMethodDate = value;
    }
}