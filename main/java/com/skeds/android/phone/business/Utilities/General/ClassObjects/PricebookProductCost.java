package com.skeds.android.phone.business.Utilities.General.ClassObjects;

import java.io.Serializable;

public class PricebookProductCost implements Serializable {
    private String name;
    private double cost;

    public PricebookProductCost() {
        setName("");
        setCost(0.0);
    }

    public String getName() {
        return name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double value) {
        this.cost = value;
    }
}