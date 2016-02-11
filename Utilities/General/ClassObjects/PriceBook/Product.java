package com.skeds.android.phone.business.Utilities.General.ClassObjects.PriceBook;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Product implements Serializable {

    private int id = -1;
    private boolean taxable = false;
    private boolean deleted = false;
    private String name = "";
    private String description = "";
    private int manufacturerId = -1;
    private int groupCodeId = -1;

    private List<Cost> productCosts = new ArrayList<Cost>();

    public void setId(int id) {
        this.id = id;
    }

    public void setTaxable(boolean taxable) {
        this.taxable = taxable;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setManufacturerId(int id) {
        manufacturerId = id;
    }

    public void setGroupCodeId(int id) {
        groupCodeId = id;
    }

    public int getId() {
        return id;
    }

    public boolean isTaxable() {
        return taxable;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getManufacturerId() {
        return manufacturerId;
    }

    public int getGroupCodeId() {
        return groupCodeId;
    }

    public List<Cost> getProductCosts() {
        return productCosts;
    }


}
