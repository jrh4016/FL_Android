package com.skeds.android.phone.business.Utilities.REST.Objects;

import com.skeds.android.phone.business.Utilities.NonfatalException;
import com.skeds.android.phone.business.Utilities.REST.RestConnector;

import org.jdom2.Document;
import org.jdom2.Element;

public class RESTGPSPing {

    public static void add(int participantId, double longitude,
                           double latitude, double accuracy, String timeStamp)
            throws NonfatalException {
        Element rootNode = new Element("pingLocationRequest");

        Element longitudeNode = new Element("longitude");
        Element latitudeNode = new Element("latitude");
        Element accuracyNode = new Element("accuracy");
        Element timestampNode = new Element("timestamp");

        longitudeNode.setText(String.valueOf(longitude));
        latitudeNode.setText(String.valueOf(latitude));
        accuracyNode.setText(String.valueOf(accuracy));
        timestampNode.setText(timeStamp);

        rootNode.addContent(longitudeNode);
        rootNode.addContent(latitudeNode);
        rootNode.addContent(accuracyNode);
        rootNode.addContent(timestampNode);

        RestConnector.getInstance().httpPostCheckSuccess(new Document(rootNode),
                "pinglocation/" + participantId);
    }
}