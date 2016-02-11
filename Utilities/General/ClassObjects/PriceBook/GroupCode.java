package com.skeds.android.phone.business.Utilities.General.ClassObjects.PriceBook;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GroupCode implements Serializable {

    private int id = -1;
    private String name = "";
    private String description = "";

    private List<Integer> manufacturerIds = new ArrayList<Integer>();

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

    public List<Integer> getManufacturerIds() {
        return manufacturerIds;
    }

}
