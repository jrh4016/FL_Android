package com.skeds.android.phone.business.Utilities.General.ClassObjects;

import java.io.Serializable;

public class Comment implements Serializable {

    private int id;
    private String text;

    private String posterName;
    private String posterDateTime;
    private String posterEmail;

    public int getId() {
        return id;
    }

    public void setId(int value) {
        this.id = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String value) {
        this.text = value;
    }

    public String getPosterName() {
        return posterName;
    }

    public void setPosterName(String value) {
        this.posterName = value;
    }

    public String getPosterDateTime() {
        return posterDateTime;
    }

    public void setPosterDateTime(String value) {
        this.posterDateTime = value;
    }

    public String getPosterEmail() {
        return posterEmail;
    }

    public void setPosterEmail(String value) {
        this.posterEmail = value;
    }

    public Comment() {
        setId(0);
        setText("");
        setPosterName("");
        setPosterDateTime("");
        setPosterEmail("");
    }
}