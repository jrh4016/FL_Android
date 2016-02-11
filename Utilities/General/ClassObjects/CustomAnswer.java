package com.skeds.android.phone.business.Utilities.General.ClassObjects;

import java.io.Serializable;

public class CustomAnswer implements Serializable {

    private int questionId;
    private int answerId;

    private String text;

    public CustomAnswer() {
        setQuestionId(0);
        setAnswerId(0);
        setText("");
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int value) {
        this.questionId = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String value) {
        this.text = value;
    }

    public int getAnswerId() {
        return answerId;
    }

    public void setAnswerId(int value) {
        this.answerId = value;
    }

}