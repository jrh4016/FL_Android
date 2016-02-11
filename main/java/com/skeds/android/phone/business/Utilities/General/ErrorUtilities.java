package com.skeds.android.phone.business.Utilities.General;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.skeds.android.phone.business.Utilities.NonfatalException;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;

public class ErrorUtilities implements Serializable {

    private boolean displayMessage;
    private String message;
    private boolean errorDisplayedToUser;

    public ErrorUtilities() {
        setDisplayMessage(false);
        setMessage("");
        setErrorDisplayedToUser(false);
    }

    public boolean isDisplayMessage() {
        return displayMessage;
    }

    public void setDisplayMessage(boolean displayMessage) {
        this.displayMessage = displayMessage;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
        this.setErrorDisplayedToUser(false);
    }

    public void handleErrorMessage(NonfatalException e) {
        errorDisplayedToUser = false;
        message = e.getMessage();
        Throwable cause = e.getCause();
        if (cause instanceof IOException) {
            message += ". I/O error: " + e.getMessage();
        }
    }

    public void handleErrorMessage(String errorString) {

        this.setErrorDisplayedToUser(false); // they haven't seen it yet

        String errorOutput = "";
        Log.e("[Error Handler]", "Received error: " + errorString);

        if (errorString.contains("<html>")) {
            if (errorString.contains("HTTP Status 401") || errorString.contains("401")) {
                errorOutput = "Invalid Username/Password";
            } else if (errorString
                    .contains("503 Service Temporarily Unavailable")) {
                errorOutput = "Could not connect to http://www.skeds.com";
            } else if (errorString.contains("HTTP Status 404")) {
                errorOutput = "Invalid variable, page not found. 404 returned";
            }

            Log.e("[Error Handler HTML]", errorOutput);
            this.setMessage(errorOutput);
        } else {
            try {
                SAXBuilder builder = new SAXBuilder();
                Reader in = new StringReader(errorString);

                Document document = builder.build(in);
                Element rootNode = document.getRootElement();

                if (!rootNode.getChildren().isEmpty()) {

                    Element responseElement = rootNode.getChild("response");

                    if (responseElement != null) {
                        if (responseElement.getAttributeValue("message") != null)
                            errorOutput = responseElement
                                    .getAttributeValue("message");
                    } else {
                        Element otherResponseNode = rootNode.getChildren().get(
                                0);

                        if (otherResponseNode != null) {
                            if (otherResponseNode.getAttributeValue("message") != null)
                                errorOutput = otherResponseNode
                                        .getAttributeValue("message");
                        }
                    }
                }

            } catch (Exception e) {

                errorOutput = errorString;
                Log.e("[Error Handler]", "Handler failed: " + e.toString());
            }

            Log.e("[Error Handler Other]", errorOutput);
            this.setMessage(errorOutput);
        }
    }

    public boolean displayErrorMessageAsToast(Context context) {

        if (TextUtils.isEmpty(getMessage())) {
            if (this.isDisplayMessage()) {
                Toast.makeText(context, "An unknown error has occured",
                        Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(context, this.getMessage(), Toast.LENGTH_LONG)
                    .show();
        }

        this.setErrorDisplayedToUser(true);
        return true;
    }

    public boolean isErrorDisplayedToUser() {
        return errorDisplayedToUser;
    }

    public void setErrorDisplayedToUser(boolean errorDisplayedToUser) {
        this.errorDisplayedToUser = errorDisplayedToUser;
    }
}