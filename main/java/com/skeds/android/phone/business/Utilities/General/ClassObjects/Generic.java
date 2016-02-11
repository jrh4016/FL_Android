package com.skeds.android.phone.business.Utilities.General.ClassObjects;

import java.io.Serializable;

public class Generic implements Serializable {

    private String name;
    private int id;
    private String type;

    public Generic() {
        setName("");
        setId(0);
        setType("");
    }

    public String getName() {
        return name;
    }

    public void setName(String value) {
        this.name = value;
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
        this.type = value;
    }

}