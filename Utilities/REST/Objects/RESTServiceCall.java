package com.skeds.android.phone.business.Utilities.REST.Objects;

import com.skeds.android.phone.business.Utilities.NonfatalException;
import com.skeds.android.phone.business.Utilities.REST.RestConnector;

import org.jdom2.Document;
import org.jdom2.Element;

public final class RESTServiceCall {

    private RESTServiceCall() {
    }

    public static void add(long equipmentId, long appointmentId,
                           long participantId, String notes, String condition)
            throws NonfatalException {
        Element rootNode = new Element("addServiceCall");
        Element participantIdNode = new Element("participantId");
        Element notesNode = new Element("notes");
        Element appointmentIdNode = new Element("apptId");
        Element conditionNode = new Element("equipmentCondition");

        if (appointmentId == 0) {
            participantIdNode.setText(participantId + "");
            notesNode.setText(notes);

            rootNode.addContent(participantIdNode);
            rootNode.addContent(notesNode);
        } else {

            appointmentIdNode.setText(String.valueOf(appointmentId));
            participantIdNode.setText(String.valueOf(participantId));
            notesNode.setText(notes);

            rootNode.addContent(appointmentIdNode);
            rootNode.addContent(participantIdNode);
            rootNode.addContent(notesNode);
        }

        conditionNode.setText(condition);
        rootNode.addContent(conditionNode);

        RestConnector.getInstance().httpPostCheckSuccess(new Document(rootNode),
                "addservicerecord/" + equipmentId);
    }
}