package com.skeds.android.phone.business.Utilities.General.ClassObjects;

import java.io.Serializable;

// participantType
public class ParticipantType implements Serializable {

    // Variables
    private int id;
    private String participantTypeName;

    // Mutators
    public void setId(int value) {
        this.id = value;
    }

    public void setParticipantTypeName(String value) {
        this.participantTypeName = value;
    }

    public int getId() {
        return id;
    }

    public String getParticipantTypeName() {
        return participantTypeName;
    }

    // Default Constructor
    public ParticipantType() {
        setId(0);
        setParticipantTypeName("");
    }
}