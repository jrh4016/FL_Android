package com.skeds.android.phone.business.Utilities.General.ClassObjects;

import java.io.Serializable;

public class Phone implements Serializable {
    private int id;
    private String type = "";
    private String description = "";
    private String number = "";

    public Phone() {
    }

    public int getId() {
        return id;
    }

    public void setId(int value) {
        this.id = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String value) {
        type = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String value) {
        description = value;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String value) {
        number = value;
    }

}