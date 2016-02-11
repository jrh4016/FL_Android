package com.skeds.android.phone.business.Utilities.REST.Objects;

import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Appointment;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Comment;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Participant;
import com.skeds.android.phone.business.Utilities.NonfatalException;
import com.skeds.android.phone.business.Utilities.REST.RestConnector;

import org.jdom2.Document;
import org.jdom2.Element;

import java.util.List;

public class RESTPastAppointmentList {

    /* List of past appointments for a given customer */
    public static void query(int customerId) throws NonfatalException {
        AppDataSingleton.getInstance().getPastAppointmentList().clear();

        Document document = RestConnector.getInstance().httpGet(
                "getcustomerhistory/" + customerId);
        Element customerNode = document.getRootElement().getChild("customer");
        if (customerNode == null)
            return;
        Element appointmentsNode = customerNode.getChild("appointments");

        if (appointmentsNode != null) {
            List<Element> appointmentList = appointmentsNode
                    .getChildren("appointment");

            // CommonUtilities.mPastAppointment = new
            // Appointment[appointmentList
            // .size()];

            for (int appointmentIterator = 0; appointmentIterator < appointmentList
                    .size(); appointmentIterator++) {

                // CommonUtilities.mPastAppointment[appointmentIterator]
                // =
                // new
                // Appointment();
                AppDataSingleton.getInstance().getPastAppointmentList().add(new Appointment());

                Element appointmentNode = (Element) appointmentList
                        .get(appointmentIterator);

                // If it is finished say so, if not just tag as
                // 'suspended',
                // will display as incomplete
                if (appointmentNode.getAttributeValue("id") != null)
                    AppDataSingleton.getInstance().getPastAppointmentList()
                            .get(appointmentIterator)
                            .setId(Integer.parseInt(appointmentNode
                                    .getAttributeValue("id")));

                // Invoice ID
                if (appointmentNode.getAttributeValue("invoiceId") != null)
                    AppDataSingleton.getInstance().getPastAppointmentList()
                            .get(appointmentIterator)
                            .setInvoiceId(
                                    Integer.parseInt(appointmentNode
                                            .getAttributeValue("invoiceId")));

                if (appointmentNode.getAttributeValue("invoiceid") != null)
                    AppDataSingleton.getInstance().getPastAppointmentList()
                            .get(appointmentIterator)
                            .setInvoiceId(
                                    Integer.parseInt(appointmentNode
                                            .getAttributeValue("invoiceid")));

                List<Element> appointmentTypes = appointmentNode
                        .getChildren("apptType");
                Element appointmentType = (Element) appointmentTypes.get(0);

                // Appointment Types Information
                if (appointmentType.getAttributeValue("id") != null)
                    AppDataSingleton.getInstance().getPastAppointmentList()
                            .get(appointmentIterator)
                            .setApptTypeId(
                                    Integer.parseInt(appointmentType
                                            .getAttributeValue("id")));
                if (appointmentType.getChildText("apptTypeName") != null)
                    AppDataSingleton.getInstance().getPastAppointmentList()
                            .get(appointmentIterator)
                            .setApptTypeName(
                                    appointmentType
                                            .getChildText("apptTypeName"));

                // Start/End Times
                if (appointmentNode.getChildText("startDate") != null)
                    AppDataSingleton.getInstance().getPastAppointmentList().get(appointmentIterator)
                            .setDate(appointmentNode.getChildText("startDate"));
                if (appointmentNode.getChildText("startTime") != null)
                    AppDataSingleton.getInstance().getPastAppointmentList()
                            .get(appointmentIterator)
                            .setStartTime(
                                    appointmentNode.getChildText("startTime"));
                if (appointmentNode.getChildText("endTime") != null)
                    AppDataSingleton.getInstance().getPastAppointmentList()
                            .get(appointmentIterator)
                            .setEndTime(appointmentNode.getChildText("endTime"));
                if (appointmentNode.getChildText("appointmentNote") != null)
                    AppDataSingleton.getInstance().getPastAppointmentList()
                            .get(appointmentIterator)
                            .setNotes(
                                    appointmentNode
                                            .getChildText("appointmentNote"));

                // Location Information
                Element appointmentLocation = appointmentNode
                        .getChild("appointmentLocation");
                if (appointmentLocation.getAttributeValue("name") != null)
                    AppDataSingleton.getInstance().getPastAppointmentList()
                            .get(appointmentIterator)
                            .setLocationName(
                                    appointmentLocation
                                            .getAttributeValue("name"));
                if (appointmentLocation.getAttributeValue("latitude") != null)
                    AppDataSingleton.getInstance().getPastAppointmentList()
                            .get(appointmentIterator)
                            .setLocationLatitude(
                                    appointmentLocation
                                            .getAttributeValue("latitude"));
                if (appointmentLocation.getAttributeValue("longitude") != null)
                    AppDataSingleton.getInstance().getPastAppointmentList()
                            .get(appointmentIterator)
                            .setLocationLongitude(
                                    appointmentLocation
                                            .getAttributeValue("longitude"));

                if (appointmentNode.getChildText("appointmentLocation") != null)
                    AppDataSingleton.getInstance().getPastAppointmentList()
                            .get(appointmentIterator)
                            .setLocationValue(
                                    appointmentNode
                                            .getChildText("appointmentLocation"));

                // Participants Information
                AppDataSingleton.getInstance().getPastAppointmentList().get(appointmentIterator).getParticipantList()
                        .clear();
                Element participantsNode = appointmentNode
                        .getChild("participants");
                List<Element> participantsList = participantsNode
                        .getChildren("participant");

                if (participantsNode != null) {
                    if (participantsList.size() > 0) {

                        for (int participantsIterator = 0; participantsIterator < participantsList
                                .size(); participantsIterator++) {

                            AppDataSingleton.getInstance().getPastAppointmentList().get(
                                    appointmentIterator).getParticipantList()
                                    .add(new Participant());

                            Element participantNode = (Element) participantsList
                                    .get(participantsIterator);

                            // Id Number
                            if (participantNode.getAttributeValue("id") != null)
                                AppDataSingleton.getInstance().getPastAppointmentList().get(
                                        appointmentIterator).getParticipantList()
                                        .get(participantsIterator)
                                        .setId(Integer.parseInt(participantNode
                                                .getAttributeValue("id")));

                            // First/Last Name
                            if (participantNode
                                    .getChildText("participantFirstName") != null)
                                AppDataSingleton.getInstance().getPastAppointmentList().get(
                                        appointmentIterator).getParticipantList()
                                        .get(participantsIterator)
                                        .setFirstName(
                                                participantNode
                                                        .getChildText("participantFirstName"));
                            if (participantNode
                                    .getChildText("participantLastName") != null)
                                AppDataSingleton.getInstance().getPastAppointmentList().get(
                                        appointmentIterator).getParticipantList()
                                        .get(participantsIterator)
                                        .setLastName(
                                                participantNode
                                                        .getChildText("participantLastName"));

                            // Participant Type Information
                            Element participantTypeNode = participantNode
                                    .getChild("participantType");

                            if (participantTypeNode.getAttributeValue("id") != null)
                                AppDataSingleton.getInstance().getPastAppointmentList().get(
                                        appointmentIterator).getParticipantList()
                                        .get(participantsIterator)
                                        .setTypeId(
                                                Integer.parseInt(participantTypeNode
                                                        .getAttributeValue("id")));
                            if (participantTypeNode
                                    .getChildText("participantTypeName") != null)
                                AppDataSingleton.getInstance().getPastAppointmentList().get(
                                        appointmentIterator).getParticipantList()
                                        .get(participantsIterator)
                                        .setTypeName(
                                                participantTypeNode
                                                        .getChildText("participantTypeName"));

                        }
                    }
                }

                // Comments Information
                AppDataSingleton.getInstance().getPastAppointmentList().get(appointmentIterator).getCommentList()
                        .clear();
                Element commentsNode = appointmentNode.getChild("comments");

                if (commentsNode != null) {
                    List<Element> commentsList = commentsNode
                            .getChildren("comment");

                    // CommonUtilities.mPastAppointment[appointmentIterator]
                    // .setCommentArraySize(commentsList.size());
                    if (commentsList.size() > 0) { // Verify this is not
                        // empty
                        // to avoid errors
                        for (int commentsIterator = 0; commentsIterator < commentsList
                                .size(); commentsIterator++) {
                            // CommonUtilities.mPastAppointment[appointmentIterator].comments[commentsIterator]
                            // = new Comment();
                            AppDataSingleton.getInstance().getPastAppointmentList().get(
                                    appointmentIterator).getCommentList()
                                    .add(new Comment());

                            Element commentNode = (Element) commentsList
                                    .get(commentsIterator);

                            // ID and Text
                            if (commentNode.getAttributeValue("id") != null)
                                AppDataSingleton.getInstance().getPastAppointmentList().get(
                                        appointmentIterator).getCommentList().get(
                                        commentsIterator).setId(
                                        Integer.parseInt(commentNode
                                                .getAttributeValue("id")));
                            if (commentNode.getChildText("text") != null)
                                AppDataSingleton.getInstance().getPastAppointmentList().get(
                                        appointmentIterator).getCommentList().get(
                                        commentsIterator).setText(
                                        commentNode.getChildText("text"));

                            // Poster Information
                            Element posterNode = commentNode.getChild("poster");

                            if (posterNode.getChildText("posterName") != null)
                                AppDataSingleton.getInstance().getPastAppointmentList().get(
                                        appointmentIterator).getCommentList().get(
                                        commentsIterator).setPosterName(
                                        posterNode.getChildText("posterName"));
                            if (posterNode.getChildText("postDateTime") != null)
                                AppDataSingleton.getInstance().getPastAppointmentList().get(
                                        appointmentIterator).getCommentList()
                                        .get(commentsIterator)
                                        .setPosterDateTime(
                                                posterNode
                                                        .getChildText("postDateTime"));
                            if (posterNode.getChildText("email") != null)
                                AppDataSingleton.getInstance().getPastAppointmentList().get(
                                        appointmentIterator).getCommentList().get(
                                        commentsIterator).setPosterEmail(
                                        posterNode.getChildText("email"));
                        }
                    }
                }
            }
        }
    }
}