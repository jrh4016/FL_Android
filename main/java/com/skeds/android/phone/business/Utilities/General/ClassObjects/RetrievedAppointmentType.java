package com.skeds.android.phone.business.Utilities.General.ClassObjects;

import java.io.Serializable;

public class RetrievedAppointmentType implements Serializable {

    private int id;
    private String name;
    private int incrementMinutes;
    private int minimumLengthMinutes;
    private int maximumLengthMinutes;

    public RetrievedAppointmentType() {
        setId(0);
        setName("");
        setIncrementMinutes(0);
        setMinimumLengthMinutes(0);
        setMaximumLengthMinutes(0);
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

    public int getIncrementMinutes() {
        return incrementMinutes;
    }

    public void setIncrementMinutes(int value) {
        this.incrementMinutes = value;
    }

    public int getMinimumLengthMinutes() {
        return minimumLengthMinutes;
    }

    public void setMinimumLengthMinutes(int value) {
        this.minimumLengthMinutes = value;
    }

    public int getMaximumLengthMinutes() {
        return maximumLengthMinutes;
    }

    public void setMaximumLengthMinutes(int value) {
        this.maximumLengthMinutes = value;
    }
}