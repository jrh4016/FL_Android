package com.skeds.android.phone.business.Utilities.General.ClassObjects;

import java.io.Serializable;

public class Participant implements Serializable {

    // Variables
    private int id;
    private String firstName;
    private String lastName;
    private int typeID;
    private String typeName;
    private String email;

    // Mutators
    public void setId(int value) {
        this.id = value;
    }

    public void setFirstName(String value) {
        this.firstName = value;
    }

    public void setLastName(String value) {
        this.lastName = value;
    }

    public void setTypeId(int value) {
        this.typeID = value;
    }

    public void setTypeName(String value) {
        this.typeName = value;
    }

    public void setEmail(String value) {
        this.email = value;
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getTypeId() {
        return typeID;
    }

    public String getTypeName() {
        return typeName;
    }

    public String getEmail() {
        return email;
    }

    public Participant() {
        setId(0);
        setFirstName("");
        setLastName("");
        setTypeId(0);
        setTypeName("");
        setEmail("");
    }

}