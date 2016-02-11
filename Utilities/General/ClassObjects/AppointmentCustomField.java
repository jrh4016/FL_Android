package com.skeds.android.phone.business.Utilities.General.ClassObjects;

import java.io.Serializable;


// Used on Appointment screen. Call RestAppointment

public class AppointmentCustomField implements Serializable {

    private String name = "";

    private String value = "";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
