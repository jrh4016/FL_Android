package com.skeds.android.phone.business.Utilities.General.ClassObjects;

import java.io.Serializable;

public class Region implements Serializable {

    private int id = 0;

    private String name = "";

    private String label = "";

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLabel() {
        return label;
    }

}
