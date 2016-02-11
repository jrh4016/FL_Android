package com.skeds.android.phone.business.Utilities.General.ClassObjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TimesWorked implements Serializable {
    private String totalHours;
    public List<TimeSpan> timeSpan = new ArrayList<TimeSpan>();

    public String getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(String value) {
        this.totalHours = value;
    }

    public TimesWorked() {
        setTotalHours("");
    }
}