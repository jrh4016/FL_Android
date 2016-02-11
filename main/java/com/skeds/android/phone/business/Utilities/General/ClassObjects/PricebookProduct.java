package com.skeds.android.phone.business.Utilities.General.ClassObjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PricebookProduct implements Serializable {
    private int id;
    private boolean taxable;
    private String name;
    private String description;
    // public PricebookProductCost[] productCost;
    public List<PricebookProductCost> productCost = new ArrayList<PricebookProductCost>();

    public PricebookProduct() {
        setId(0);
        setTaxable(false);
        setName("");
        setDescription("");
    }

    public int getId() {
        return id;
    }

    public void setId(int value) {
        this.id = value;
    }

    public boolean isTaxable() {
        return taxable;
    }

    public void setTaxable(boolean value) {
        this.taxable = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String value) {
        this.description = value;
    }
}