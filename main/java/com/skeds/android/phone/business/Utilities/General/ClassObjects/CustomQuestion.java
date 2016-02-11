package com.skeds.android.phone.business.Utilities.General.ClassObjects;

import java.io.Serializable;

public class CustomQuestion implements Serializable {

    private int id;
    private String metaType;
    private boolean required;
    private String text;
    private String answer;

    public CustomQuestion() {
        setId(0);
        setMetaType("");
        setRequired(false);
        setText("");
        setAnswer("");
    }

    public int getId() {
        return id;
    }

    public void setId(int value) {
        this.id = value;
    }

    public String getMetaType() {
        return metaType;
    }

    public void setMetaType(String value) {
        this.metaType = value;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean value) {
        this.required = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String value) {
        this.text = value;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String value) {
        this.answer = value;
    }

}