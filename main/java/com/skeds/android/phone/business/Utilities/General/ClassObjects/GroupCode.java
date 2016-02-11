package com.skeds.android.phone.business.Utilities.General.ClassObjects;

import java.io.Serializable;

public class GroupCode implements Serializable {
    private int id;
    private String name;

    public GroupCode() {
        setId(0);
        setName("");
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
}