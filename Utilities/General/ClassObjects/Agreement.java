package com.skeds.android.phone.business.Utilities.General.ClassObjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Agreement implements Serializable {
    // Variables
    private int id;
    private String status;
    private int servicePlanId;
    private String description;
    private String paymentType;
    private String contractNumber;
    private String salesPerson;
    private int numberOfSystems;
    private String servicePlanName;
    private String startDate;
    private String endDate;
    public List<Generic> locationAndEquipment;
    public List<Generic> equipment;

    // Default Constructor
    public Agreement() {
        setId(0);
        setStatus("");
        setServicePlanId(0);
        setDescription("");
        setPaymentType("");
        setContractNumber("");
        setSalesPerson("");
        setNumberOfSystems(0);
        setServicePlanName("");
        setStartDate("");
        setEndDate("");
        locationAndEquipment = new ArrayList<Generic>();
        equipment = new ArrayList<Generic>();
    }

    public void setId(int value) {
        this.id = value;
    }

    public int getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String value) {
        this.status = value;
    }

    public int getServicePlanId() {
        return servicePlanId;
    }

    public void setServicePlanId(int id) {
        this.servicePlanId = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String value) {
        this.description = value;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String value) {
        this.paymentType = value;
    }

    public String getContractNumber() {
        return contractNumber;
    }

    public void setContractNumber(String value) {
        this.contractNumber = value;
    }

    public String getSalesPerson() {
        return salesPerson;
    }

    public void setSalesPerson(String value) {
        this.salesPerson = value;
    }

    public int getNumberOfSystems() {
        return numberOfSystems;
    }

    public void setNumberOfSystems(int total) {
        this.numberOfSystems = total;
    }

    public String getServicePlanName() {
        return servicePlanName;
    }

    public void setServicePlanName(String name) {
        this.servicePlanName = name;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String date) {
        this.startDate = date;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String date) {
        this.endDate = date;
    }
}