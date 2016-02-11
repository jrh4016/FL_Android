package com.skeds.android.phone.business.Utilities.General.ClassObjects;

import java.io.Serializable;

public class PdfDocument implements Serializable {

    private int id;
    private String name;
    private String url;
    private int apptId;
    private int equipmentId;

    private int reporterId;
    private String reporterName;

    private String description;
    private String usageType;

    public PdfDocument() {
        setId(0);
        setName("");
        setUrl("");
        setApptId(0);
        setEquipmentId(0);
        setReporterId(0);
        setReporterName("");
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setApptId(int id) {
        apptId = id;
    }

    public void setEquipmentId(int id) {
        equipmentId = id;
    }

    public void setReporterId(int id) {
        reporterId = id;
    }

    public void setReporterName(String name) {
        reporterName = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUsageType(String type) {
        usageType = type;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public int getApptId() {
        return apptId;
    }

    public int getEquipmentId() {
        return equipmentId;
    }

    public int getReporterId() {
        return reporterId;
    }

    public String getReporterName() {
        return reporterName;
    }

    public String getDescription() {
        return description;
    }

    public String getUsageType() {
        return usageType;
    }

}