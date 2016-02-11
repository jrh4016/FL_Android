package com.skeds.android.phone.business.Utilities.REST.Objects;

import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.TimeClockTechnician;
import com.skeds.android.phone.business.Utilities.NonfatalException;
import com.skeds.android.phone.business.Utilities.REST.RestConnector;

import org.jdom2.Document;
import org.jdom2.Element;

import java.util.List;
import java.util.TimeZone;

public class RESTTimeclockList {

    public static void query(int participantId) throws NonfatalException {
        Document document = RestConnector.getInstance().httpGet(
                "gettechniciansandclockupdates/" + participantId);
        // AppDataSingleton.getInstance().getServicePlanList().clear();
        List<Element> technicianListNode = document.getRootElement()
                .getChildren("technician");
        if (technicianListNode.isEmpty())
            return;
        AppDataSingleton.getInstance().getClockTechnician().clear();

        for (int i = 0; i < technicianListNode.size(); i++) {
            TimeClockTechnician thisTech = new TimeClockTechnician();
            Element technicianNode = (Element) technicianListNode.get(i);

            if (technicianNode.getAttribute("id") != null) {
                thisTech.setId(Integer.parseInt(technicianNode
                        .getAttributeValue("id")));
            }

            if (technicianNode.getAttribute("name") != null)
                thisTech.setName(technicianNode.getAttributeValue("name"));

            if (technicianNode.getChild("timeClockRecord") != null) {
                if (technicianNode.getChild("timeClockRecord").getAttribute(
                        "method") != null)
                    thisTech.setTimeClockMethod(technicianNode.getChild(
                            "timeClockRecord").getAttributeValue("method"));

                if (technicianNode.getChild("timeClockRecord").getText() != null)
                    thisTech.setTimeClockMethodDate(technicianNode.getChild(
                            "timeClockRecord").getText());
            }
            AppDataSingleton.getInstance().getClockTechnician().add(thisTech);
        }
    }

    public static void update(int participantId, String updateType,
                              String time, String latitude, String longitude)
            throws NonfatalException {
        Element rootNode = new Element("timeClockUpdate");

        Element methodNode = new Element("method");
        Element timeNode = new Element("time");
        Element latitudeNode = new Element("latitude");
        Element longitudeNode = new Element("longitude");

        methodNode.setText(updateType.toUpperCase());
        timeNode.setAttribute("tz", TimeZone.getDefault().getID());
        timeNode.setText(time);
        latitudeNode.setText(latitude);
        longitudeNode.setText(longitude);

        rootNode.addContent(methodNode);
        rootNode.addContent(timeNode);
        rootNode.addContent(latitudeNode);
        rootNode.addContent(longitudeNode);

        RestConnector.getInstance().httpPostCheckSuccess(new Document(rootNode),
                "timeclockupdate/" + participantId);
    }
}