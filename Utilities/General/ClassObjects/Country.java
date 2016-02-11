package com.skeds.android.phone.business.Utilities.General.ClassObjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Country implements Serializable {

    private int id = 0;

    private String name = "";

    private String localCode = "";

    private String currencySymbol = "$";

    private String shortCode = "";

    private String provinceLabel = "";

    private boolean useExtendedTax = false;

    private List<Region> regions = new ArrayList<Region>();

    private String zipCodePattern = "";

    private int zipCodeLength = 20;

    private String zipCodeLabel = "";

    private List<TaxRate> taxRates = new ArrayList<TaxRate>();

    private List<TaxRateType> taxRateTypes = new ArrayList<TaxRateType>();

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLocalCode() {
        return localCode;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public String getShortCode() {
        return shortCode;
    }

    public String getProvinceLabel() {
        return provinceLabel;
    }

    public boolean isUseExtendedTax() {
        return useExtendedTax;
    }

    public String getZipCodePattern() {
        return zipCodePattern;
    }

    public int getZipCodeLength() {
        return zipCodeLength;
    }

    public String getZipCodeLabel() {
        return zipCodeLabel;
    }

    public List<TaxRate> getTaxRates() {
        return taxRates;
    }

    public List<TaxRateType> getTaxRateTypes() {
        return taxRateTypes;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLocalCode(String localCode) {
        this.localCode = localCode;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    public void setProvinceLabel(String provinceLabel) {
        this.provinceLabel = provinceLabel;
    }

    public void setUseExtendedTax(boolean useExtendedTax) {
        this.useExtendedTax = useExtendedTax;
    }

    public List<Region> getRegions() {
        return regions;
    }

    public void setZipCodePattern(String zipCodePattern) {
        this.zipCodePattern = zipCodePattern;
    }

    public void setZipCodeLength(int zipCodeLength) {
        this.zipCodeLength = zipCodeLength;
    }

    public void setZipCodeLabel(String zipCodeLabel) {
        this.zipCodeLabel = zipCodeLabel;
    }

}
