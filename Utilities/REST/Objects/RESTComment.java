package com.skeds.android.phone.business.Utilities.REST.Objects;

import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.NonfatalException;
import com.skeds.android.phone.business.Utilities.REST.RestConnector;

import org.jdom2.Document;
import org.jdom2.Element;

public class RESTComment {

    // TODO - Need POST and UPDATE REST functions
    public static void add(int participantId, String comment)
            throws NonfatalException {
        Element rootNode = new Element("addAppointmentComment");

        Element participantIdNode = new Element("participantId");
        participantIdNode.setText(String.valueOf(participantId));
        rootNode.addContent(participantIdNode);

        Element commentBodyNode = new Element("commentBody");
        commentBodyNode.setText(comment);
        rootNode.addContent(commentBodyNode);

        RestConnector.getInstance().httpPostCheckSuccess(new Document(rootNode),
                "addappointmentcomment/" + AppDataSingleton.getInstance().getAppointment().getId());
    }
}