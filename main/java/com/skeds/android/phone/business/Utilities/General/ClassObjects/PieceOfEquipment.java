package com.skeds.android.phone.business.Utilities.General.ClassObjects;

import java.io.Serializable;

public class PieceOfEquipment implements Serializable {
    private int id;
    private String name;
    private String modelNumber;
    private String serialNumber;
    private int manufacturerId;
    private String manufacturer;

    public PieceOfEquipment() {
        setId(0);
        setName("");
        setModelNumber("");
        setSerialNumber("");
        setManufacturer("");
        setManufacturerId(0);
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

    public String getModelNumber() {
        return modelNumber;
    }

    public void setModelNumber(String value) {
        this.modelNumber = value;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String value) {
        this.serialNumber = value;
    }

    public int getManufacturerId() {
        return manufacturerId;
    }

    public void setManufacturerId(int value) {
        this.manufacturerId = value;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String value) {
        this.manufacturer = value;
    }
}