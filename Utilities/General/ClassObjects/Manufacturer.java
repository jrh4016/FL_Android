package com.skeds.android.phone.business.Utilities.General.ClassObjects;

import java.io.Serializable;

public class Manufacturer implements Serializable {
    private int id;
    private String name;
    private String description;

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

    public Manufacturer() {
        setId(0);
        setName("");
        setDescription("");
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String value) {
        this.description = value;
    }
}