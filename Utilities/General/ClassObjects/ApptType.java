package com.skeds.android.phone.business.Utilities.General.ClassObjects;

import java.io.Serializable;

//AppointmentType Class
public class ApptType implements Serializable {
    // Variables
    private int id;
    private String appointmentTypeName;

    // Mutators
    public void setId(int value) {
        this.id = value;
    }

    public void setAppointmentTypeName(String value) {
        this.appointmentTypeName = value;
    }

    public int getId() {
        return id;
    }

    public String getAppointmentTypeName() {
        return appointmentTypeName;
    }

    // Default Constructor
    public ApptType() {
        setId(0);
        setAppointmentTypeName("");
    }
}