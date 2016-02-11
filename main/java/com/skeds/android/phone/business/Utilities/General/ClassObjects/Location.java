package com.skeds.android.phone.business.Utilities.General.ClassObjects;

import java.io.Serializable;
import java.util.TimeZone;

public class Location implements Serializable {

    private int id;
    private String latitude = "";
    private String longitude = "";
    private String name = "";
    private String address1 = "";
    private String address2 = "";
    private String city = "";
    private String state = "";
    private String zip = "";
    private String email = "";
    private boolean latLongOnly;
    private String phone1 = "";
    private String phone1Description = "";
    private String phone1Type = "OTHER";
    private String phone2 = "";
    private String phone2Description = "";
    private String phone2Type = "OTHER";
    private String descr = "";
    private String code = "";
    private int countryId;

    private boolean isTaxable;
    private TimeZone timeZone;

    public void setLocation(Location l) {
        this.id = l.id;
        this.address1 = l.address1;
        this.address2 = l.address2;
        this.name = l.name;
        this.city = l.city;
        this.state = l.state;
        this.zip = l.zip;
        this.phone1 = l.phone1;
        this.phone2 = l.phone2;
        this.descr = l.descr;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String value) {
        this.latitude = value;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String value) {
        this.longitude = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String value) {
        this.address1 = value;
    }

    public String getAddress2() {
        return address2 == null ? "" : address2;
    }

    public void setAddress2(String value) {
        this.address2 = value;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String value) {
        this.city = value;
    }

    public String getState() {
        return state;
    }

    public void setState(String value) {
        this.state = value;
    }

    public void setPhone1Description(String value) {
        phone1Description = value;
    }

    public void setPhone2Description(String value) {
        phone2Description = value;
    }

    public String getPhone1Description() {
        return phone1Description;
    }

    public String getPhone2Description() {
        return phone2Description;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String value) {
        this.zip = value;
    }

    public boolean isLatLongOnly() {
        return latLongOnly;
    }

    public void setLatLongOnly(boolean value) {
        this.latLongOnly = value;
    }

    public String getPhone1() {
        return phone1;
    }

    public void setPhone1(String value) {
        this.phone1 = value;
    }

    public String getPhone2() {
        return phone2;
    }

    public void setPhone2(String value) {
        this.phone2 = value;
    }

    public String getDescription() {
        return descr;
    }

    public void setDescription(String descr) {
        this.descr = descr;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPhone1Type() {
        return phone1Type;
    }

    public void setPhone1Type(String phone1Type) {
        this.phone1Type = phone1Type;
    }

    public String getPhone2Type() {
        return phone2Type;
    }

    public void setPhone2Type(String phone2Type) {
        this.phone2Type = phone2Type;
    }

    public int getCountryId() {
        return countryId;
    }

    public void setCountryId(int countryId) {
        this.countryId = countryId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isTaxable() {
        return isTaxable;
    }

    public void setTaxable(boolean isTaxable) {
        this.isTaxable = isTaxable;
    }

    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }

    public TimeZone getTimeZone() {
        return timeZone;
    }
}