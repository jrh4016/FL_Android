package com.skeds.android.phone.business.Utilities.General.ClassObjects;

import org.jdom2.Attribute;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

public class Customer implements Serializable {
    // Variables
    private int id;
    String firstName;
    String lastName;
    public ArrayList<String> email = new ArrayList<String>();
    public ArrayList<String> emailDescription = new ArrayList<String>();
    public ArrayList<Integer> emailId = new ArrayList<Integer>();
    public ArrayList<String> phone = new ArrayList<String>();
    public ArrayList<String> phoneType = new ArrayList<String>();
    public ArrayList<String> phoneDescription = new ArrayList<String>();
    public ArrayList<Integer> phoneId = new ArrayList<Integer>();

    public int locationsCount = 0;

    String organizationName;
    String jobTitle;
    String type;
    String customerNotes;
    String address;
    boolean unreliable;
    String reliableNotes;
    String addressLine1;
    String addressLine2;
    String addressCity;
    String addressState;
    String addressPostalCode;
    String addressCountry;

    private String locationEmail = "";

    private boolean isTaxable;

    private boolean isLocationTaxable;

    // public Location[] location;
    public List<Location> locationList = new ArrayList<Location>();

    private List<Phone> phonesList = new ArrayList<Phone>();
    private TimeZone timeZone;
    private String leadSourceId;

    // Mutators
    public int getId() {
        return this.id;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public String getOrganizationName() {
        return this.organizationName;
    }

    /**
     * @return "FirstName LastName, CompanyName" or "Customer name not found"
     */
    public String getLongName() {
        StringBuilder textbuf = new StringBuilder(80);
        textbuf.append(firstName);
        if (lastName.length() > 0) {
            if (firstName.length() > 0)
                textbuf.append(" ");
            textbuf.append(lastName);
        }
        if (organizationName.length() > 0) {
            if (textbuf.length() > 0)
                textbuf.append(", ");
            textbuf.append(organizationName);
        }
        if (textbuf.length() < 1)
            textbuf.append("Customer name not found");
        return textbuf.toString();
    }

    public String getJobTitle() {
        return this.jobTitle;
    }

    public String getType() {
        return this.type;
    }

    public String getNotes() {
        return this.customerNotes;
    }

    public String getReliableNotes() {
        return this.reliableNotes;
    }

    public String getAddress() {
        return this.address;
    }

    public boolean getUnreliable() {
        return this.unreliable;
    }

    public String getAddress1() {
        return this.addressLine1;
    }

    public String getAddress2() {
        return this.addressLine2;
    }

    public String getAddressCity() {
        return this.addressCity;
    }

    public String getAddressState() {
        return this.addressState;
    }

    public String getAddressPostalCode() {
        return this.addressPostalCode;
    }

    public String getAddressCountry() {
        return this.addressCountry;
    }

    public Location getLocationById(long locid) {
        if (locationList != null)
            for (Location loc : locationList)
                if (loc.getId() == locid)
                    return loc;
        return new Location();
    }

    public void setId(int value) {
        this.id = value;
    }

    public void setFirstName(String value) {
        this.firstName = value;
    }

    public void setLastName(String value) {
        this.lastName = value;
    }

    public void setOrganizationName(String value) {
        this.organizationName = value;
    }

    public void setJobTitle(String value) {
        this.jobTitle = value;
    }

    public void setType(String value) {
        this.type = value;
    }

    public void setNotes(String value) {
        this.customerNotes = value;
    }

    public void addNotes(String value) {
        this.customerNotes = customerNotes + "\n" + value;
    }

    public void setReliableNotes(String value) {
        this.reliableNotes = value;
    }

    public void setAddress(String value) {
        this.address = value;
    }

    public void setUnreliable(boolean value) {
        this.unreliable = value;
    }

    public void setAddress1(String value) {
        this.addressLine1 = value;
    }

    public void setAddress2(String value) {
        this.addressLine2 = value;
    }

    public void setAddressCity(String value) {
        this.addressCity = value;
    }

    public void setAddressState(String value) {
        this.addressState = value;
    }

    public void setAddressPostalCode(String value) {
        this.addressPostalCode = value;
    }

    public void setAddressCountry(String value) {
        this.addressCountry = value;
    }

    public List<Phone> getPhones() {
        return phonesList;
    }

    // Default Constructor
    public Customer() {
        setId(0);
        setFirstName("");
        setLastName("");
        setOrganizationName("");
        setJobTitle("");
        setType("");
        setNotes("");
        setUnreliable(false);
        setReliableNotes("");

        setAddress1("");
        setAddress2("");
        setAddressCity("");
        setAddressState("");
        setAddressPostalCode("");
        setAddressCountry("");
    }

    public String getLocationEmail() {
        return locationEmail;
    }

    public void setLocationEmail(String locationEmail) {
        this.locationEmail = locationEmail;
    }


    public boolean isTaxable() {
        return isTaxable;
    }

    public void setTaxable(boolean isTaxable) {
        this.isTaxable = isTaxable;
    }

    public boolean isLocationTaxable() {
        return isLocationTaxable;
    }

    public void setLocationTaxable(boolean isLocationTaxable) {
        this.isLocationTaxable = isLocationTaxable;
    }

    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }

    public TimeZone getTimeZone() {
        return timeZone;
    }

    public void setLeadSourceId(String leadSourceId) {
        this.leadSourceId = leadSourceId;
    }

    public String getLeadSourceId() {
        return leadSourceId;
    }
}