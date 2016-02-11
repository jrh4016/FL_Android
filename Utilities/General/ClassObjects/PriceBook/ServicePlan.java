package com.skeds.android.phone.business.Utilities.General.ClassObjects.PriceBook;

import java.io.Serializable;

public class ServicePlan implements Serializable {

    private int id = -1;
    private String name = "";
    private String priceType = "";

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPriceType(String type) {
        this.priceType = type;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPriceType() {
        return priceType;
    }

}
