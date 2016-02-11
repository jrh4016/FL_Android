package com.skeds.android.phone.business.Utilities.General.ClassObjects.PriceBook;

import java.io.Serializable;

public class Manufacturer implements Serializable {

    private int id = -1;
    private String name = "";
    private String description = "";

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

}
