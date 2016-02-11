package com.skeds.android.phone.business.Utilities.General.ClassObjects;

import java.io.Serializable;

public class DashboardStatus implements Serializable {

    private String technicianName;
    private String technicianStatus;

    public DashboardStatus() {
        setTechnicianName("");
        setTechnicianStatus("");
    }

    public String getTechnicianName() {
        return technicianName;
    }

    public void setTechnicianName(String value) {
        this.technicianName = value;
    }

    public String getTechnicianStatusString() {
        return technicianStatus;
    }

    public void setTechnicianStatus(String value) {
        this.technicianStatus = value;
    }
}