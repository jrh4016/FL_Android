package com.skeds.android.phone.business.Utilities.General.ClassObjects;

import java.io.Serializable;

public class UserPhoto implements Serializable {

    private String URL;
    private String tagText;
    private String date;
    private String photographer;
    private int equipmentId;
    private int appointmentId;
    private int id;

    public UserPhoto() {
        setURL("");
        setDate("");
        setTagText("");
        setPhotographer("");
        setEquipmentId(0);
        setAppointmentId(0);
        setId(0);
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String value) {
        URL = value;
    }

    public String getTagText() {
        return tagText;
    }

    public void setTagText(String value) {
        this.tagText = value;
    }

    public int getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(int value) {
        this.equipmentId = value;
    }

    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int value) {
        this.appointmentId = value;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String value) {
        this.date = value;
    }

    public String getPhotographer() {
        return photographer;
    }

    public void setPhotographer(String value) {
        this.photographer = value;
    }

    public int getId() {
        return id;
    }

    public void setId(int value) {
        this.id = value;
    }

}