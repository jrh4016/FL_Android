package com.skeds.android.phone.business.Utilities.REST.Objects;

import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.DashboardStatus;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.UpcomingAppointment;
import com.skeds.android.phone.business.Utilities.General.UserUtilitiesSingleton;
import com.skeds.android.phone.business.Utilities.NonfatalException;
import com.skeds.android.phone.business.Utilities.REST.RestConnector;

import org.jdom2.Document;
import org.jdom2.Element;

import java.util.List;
import java.util.TimeZone;

public class RESTDashboardAppointmentList {

    /* Upcoming Appointments, and Technician Status on Dashboard */
    public static void query() throws NonfatalException {
        int userId = UserUtilitiesSingleton.getInstance().user.getId();
        Document document = RestConnector.getInstance().httpGet(
                "getdashboardinfo/" + userId);
        AppDataSingleton.getInstance().getUpcomingAppointmentsList().clear();

        Element userNode = document.getRootElement().getChild("user");
        if (userNode == null)
            throw new NonfatalException("XML",
                    "Bad server response: no user data");

        // Upcoming Appointments
        Element firstAppointmentsNode = userNode.getChild("firstAppointments");
        if (firstAppointmentsNode != null) {

            List<Element> appointmentsList = firstAppointmentsNode
                    .getChildren("appointment");

            if (appointmentsList != null && !appointmentsList.isEmpty()) {

                for (int appointmentIterator = 0; appointmentIterator < appointmentsList
                        .size(); appointmentIterator++) {

                    AppDataSingleton.getInstance().getUpcomingAppointmentsList().add(
                            new UpcomingAppointment());

                    Element appointmentNode = (Element) appointmentsList
                            .get(appointmentIterator);

                    if (appointmentNode.getAttributeValue("id") != null)
                        AppDataSingleton.getInstance().getUpcomingAppointmentsList()
                                .get(appointmentIterator)
                                .setId(Integer.parseInt(appointmentNode
                                        .getAttributeValue("id")));
                    String status = appointmentNode.getAttributeValue("status")
                            .toString();
                    if (status.equals("ON_ROUTE")) {
                        AppDataSingleton.getInstance().getUpcomingAppointmentsList()
                                .get(appointmentIterator)
                                .setStatus(
                                        com.skeds.android.phone.business.Utilities.General.ClassObjects.Status.ON_ROUTE);
                    } else if (status.equals("START_APPOINTMENT")) {
                        AppDataSingleton.getInstance().getUpcomingAppointmentsList()
                                .get(appointmentIterator)
                                .setStatus(
                                        com.skeds.android.phone.business.Utilities.General.ClassObjects.Status.START_APPOINTMENT);
                    } else if (status.equals("SUSPEND_APPOINTMENT")) {
                        AppDataSingleton.getInstance().getUpcomingAppointmentsList()
                                .get(appointmentIterator)
                                .setStatus(
                                        com.skeds.android.phone.business.Utilities.General.ClassObjects.Status.SUSPEND_APPOINTMENT);
                    } else if (status.equals("RESTART_APPOINTMENT")) {
                        AppDataSingleton.getInstance().getUpcomingAppointmentsList()
                                .get(appointmentIterator)
                                .setStatus(
                                        com.skeds.android.phone.business.Utilities.General.ClassObjects.Status.RESTART_APPOINTMENT);
                    } else if (status.equals("FINISH_APPOINTMENT")) {
                        AppDataSingleton.getInstance().getUpcomingAppointmentsList()
                                .get(appointmentIterator)
                                .setStatus(
                                        com.skeds.android.phone.business.Utilities.General.ClassObjects.Status.FINISH_APPOINTMENT);
                    } else if (status.equals("MOVE_APPOINTMENT")) {
                        AppDataSingleton.getInstance().getUpcomingAppointmentsList()
                                .get(appointmentIterator)
                                .setStatus(
                                        com.skeds.android.phone.business.Utilities.General.ClassObjects.Status.MOVE_APPOINTMENT);
                    } else if (status.equals("CLOSE_APPOINTMENT")) {
                        AppDataSingleton.getInstance().getUpcomingAppointmentsList()
                                .get(appointmentIterator)
                                .setStatus(
                                        com.skeds.android.phone.business.Utilities.General.ClassObjects.Status.CLOSE_APPOINTMENT);
                    } else if (status.equals("NOT_STARTED")) {
                        AppDataSingleton.getInstance().getUpcomingAppointmentsList()
                                .get(appointmentIterator)
                                .setStatus(
                                        com.skeds.android.phone.business.Utilities.General.ClassObjects.Status.NOT_STARTED);
                    } else if (status.equals("PARTS_RUN_APPOINTMENT")) {
                        AppDataSingleton.getInstance().getUpcomingAppointmentsList()
                                .get(appointmentIterator)
                                .setStatus(
                                        com.skeds.android.phone.business.Utilities.General.ClassObjects.Status.PARTS_RUN_APPOINTMENT);
                    }

                    if (appointmentNode.getAttributeValue("complete") != null)
                        AppDataSingleton.getInstance().getUpcomingAppointmentsList()
                                .get(appointmentIterator)
                                .setComplete(
                                        Boolean.parseBoolean(appointmentNode
                                                .getAttributeValue("complete")));

                    if (appointmentNode.getChild("apptTypeName") != null)
                        AppDataSingleton.getInstance().getUpcomingAppointmentsList()
                                .get(appointmentIterator)
                                .setApptTypeName(
                                        appointmentNode
                                                .getChild("apptTypeName")
                                                .getValue());

                    if (appointmentNode.getChild("startTime") != null)
                        AppDataSingleton.getInstance().getUpcomingAppointmentsList()
                                .get(appointmentIterator)
                                .setStartTime(
                                        appointmentNode.getChild("startTime")
                                                .getValue());
                    if (appointmentNode.getChild("startDate") != null)
                        AppDataSingleton.getInstance().getUpcomingAppointmentsList()
                                .get(appointmentIterator)
                                .setStartDate(
                                        appointmentNode.getChild("startDate")
                                                .getValue());

                    if (appointmentNode.getChild("endTime") != null)
                        AppDataSingleton.getInstance().getUpcomingAppointmentsList()
                                .get(appointmentIterator)
                                .setEndTime(
                                        appointmentNode.getChild("endTime")
                                                .getValue());

                    if (appointmentNode.getChild("appointmentLocation") != null) {
                        if (appointmentNode.getChild("appointmentLocation")
                                .getAttributeValue("latitude") != null)
                            AppDataSingleton.getInstance().getUpcomingAppointmentsList()
                                    .get(appointmentIterator)
                                    .setLocationLatitude(
                                            appointmentNode.getChild(
                                                    "appointmentLocation")
                                                    .getAttributeValue(
                                                            "latitude"));

                        if (appointmentNode.getChild("appointmentLocation")
                                .getAttributeValue("longitude") != null)
                            AppDataSingleton.getInstance().getUpcomingAppointmentsList()
                                    .get(appointmentIterator)
                                    .setLocationLongitude(
                                            appointmentNode.getChild(
                                                    "appointmentLocation")
                                                    .getAttributeValue(
                                                            "longitude"));

                        if (appointmentNode.getChild("appointmentLocation")
                                .getAttributeValue("timeZone") != null)
                            AppDataSingleton.getInstance().getUpcomingAppointmentsList()
                                    .get(appointmentIterator)
                                    .setTimeZone(TimeZone.getTimeZone(
                                            appointmentNode.getChild(
                                                    "appointmentLocation")
                                                    .getAttributeValue(
                                                            "timeZone")));

                        if (appointmentNode.getChild("appointmentLocation")
                                .getText() != null)
                            AppDataSingleton.getInstance().getUpcomingAppointmentsList()
                                    .get(appointmentIterator)
                                    .setLocationAddress(
                                            appointmentNode.getChild(
                                                    "appointmentLocation")
                                                    .getText());

                    }

                    if (appointmentNode.getChild("customerName") != null) {
                        AppDataSingleton.getInstance().getUpcomingAppointmentsList()
                                .get(appointmentIterator)
                                .setCustomerOrgName(
                                        appointmentNode
                                                .getChild("customerName")
                                                .getText());
                    }

                    if (appointmentNode.getChild("customer") != null) {
                        if (appointmentNode.getChild("customer")
                                .getAttributeValue("type") != null) {
                            if (appointmentNode.getChild("customer")
                                    .getAttributeValue("type").equals("org"))
                                AppDataSingleton.getInstance().getUpcomingAppointmentsList()
                                        .get(appointmentIterator)
                                        .setOrganization(true);
                            else
                                AppDataSingleton.getInstance().getUpcomingAppointmentsList()
                                        .get(appointmentIterator)
                                        .setOrganization(false);
                        }

                        if (appointmentNode.getChild("customer").getChild(
                                "customerFirstName") != null)
                            AppDataSingleton.getInstance().getUpcomingAppointmentsList()
                                    .get(appointmentIterator)
                                    .setCustomerFirstName(
                                            appointmentNode
                                                    .getChild("customer")
                                                    .getChild(
                                                            "customerFirstName")
                                                    .getText());

                        if (appointmentNode.getChild("customer").getChild(
                                "customerLastName") != null)
                            AppDataSingleton.getInstance().getUpcomingAppointmentsList()
                                    .get(appointmentIterator)
                                    .setCustomerLastName(
                                            appointmentNode
                                                    .getChild("customer")
                                                    .getChild(
                                                            "customerLastName")
                                                    .getText());
                    }

                }
            }
        }

		/*
         * This is "My" status at the technician
		 */
        AppDataSingleton.getInstance().setTechnicianStatus(new DashboardStatus());
        Element statusElement = userNode.getChild("currentStatus");
        if (statusElement.getAttributeValue("customerName") != null)
            AppDataSingleton.getInstance().getTechnicianStatus().setTechnicianName(
                    (statusElement.getAttributeValue("customerName")));

        if (statusElement.getText() != null)
            AppDataSingleton.getInstance().getTechnicianStatus().setTechnicianName(
                    statusElement.getText());

    }
}