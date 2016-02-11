package com.skeds.android.phone.business.Utilities.General.ClassObjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class NewAppointment implements Serializable {

    private static int earliestTimeMinutes, latestTimeMinutes;
    public static int singleAppointmentDuration;
    public List<RetrievedAppointmentType> appointmentTypeList = new ArrayList<RetrievedAppointmentType>();
    public List<BusinessHourException> businessHoursExceptionList = new ArrayList<BusinessHourException>();

    public NewAppointment() {
        setEarliestTimeMinutes(0);
        setLatestTimeMinutes(0);
    }

    public static int getEarliestTimeMinutes() {
        return earliestTimeMinutes;
    }

    public void setEarliestTimeMinutes(int value) {
        NewAppointment.earliestTimeMinutes = value;
    }

    public static int getLatestTimeMinutes() {
        return latestTimeMinutes;
    }

    public void setLatestTimeMinutes(int value) {
        NewAppointment.latestTimeMinutes = value;
    }

}