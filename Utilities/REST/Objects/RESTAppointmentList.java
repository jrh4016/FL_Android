package com.skeds.android.phone.business.Utilities.REST.Objects;

import android.text.TextUtils;

import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.AppointmentListItem;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Status;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.StatusBuffer;
import com.skeds.android.phone.business.Utilities.General.UserUtilitiesSingleton;
import com.skeds.android.phone.business.Utilities.NonfatalException;
import com.skeds.android.phone.business.Utilities.REST.RestConnector;

import org.jdom2.Document;
import org.jdom2.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

public class RESTAppointmentList {

    /* List of all appointments */
    public static void query(/* AsyncTask<?, String, ?> task */)
            throws NonfatalException {
        StatusBuffer.instance().flush();
        parse(RestConnector.getInstance().httpGet(
                "getprovidertasks/" + UserUtilitiesSingleton.getInstance().user.getId()));
    }

    private static void parse(Document document) {
        List<AppointmentListItem> tempList = new ArrayList<AppointmentListItem>();

        for (Element scheduleNode : document.getRootElement().getChildren(
                "scheduleDate")) {
            for (Element appointmentsNode : scheduleNode
                    .getChildren("appointments")) {
                for (Element appointmentNode : appointmentsNode
                        .getChildren("appointment")) {

                    AppointmentListItem listItem = new AppointmentListItem();

                    // Appointment ID
                    if (appointmentNode.getAttributeValue("id") != null)
                        listItem.setId(Integer.parseInt(appointmentNode
                                .getAttributeValue("id")));

                    // Date
                    if (appointmentNode.getChild("startDate") != null)
                        listItem.setStartDate(appointmentNode
                                .getChildText("startDate"));


                    // Start Time
                    if (appointmentNode.getChildText("startDateTime") != null)
                        listItem.setStartTime(appointmentNode
                                .getChildText("startDateTime"));

                    // end Time
                    if (appointmentNode.getChildText("endDateTime") != null)
                        listItem.setEndTime(appointmentNode
                                .getChildText("endDateTime"));

                    // workOrderNumber
                    if (appointmentNode.getAttributeValue("workOrderNumber") != null)
                        listItem.setWorkOrderNumber(appointmentNode.getAttributeValue("workOrderNumber"));


                    // Location Name
                    List<Element> customersList = appointmentNode
                            .getChildren("customers");
                    Element customersNode = (Element) customersList.get(0);
                    List<Element> customerList = customersNode
                            .getChildren("customer");
                    Element customerNode = (Element) customerList.get(0);

                    Element appointmentLocationNode = appointmentNode.getChild("appointmentLocation");
                    if (appointmentLocationNode != null) {

                        listItem.setLocation(!appointmentLocationNode.getText().isEmpty() ?
                                appointmentLocationNode.getText() :
                                appointmentLocationNode.getAttributeValue("name"));

                        if (appointmentLocationNode.getAttribute("timeZone") != null)
                            listItem.setTimeZone(TimeZone.getTimeZone(appointmentLocationNode.getAttribute("timeZone").getValue()));
                    }


                    // Label to use
                    if (customerNode.getAttributeValue("type") != null
                            && customerNode.getAttributeValue("type").equals(
                            "org"))
                        listItem.setLabel(customerNode.getChildText("orgName"));
                    else {
                        String childTextCustomer = customerNode.getChildText("customerFirstName");
                        listItem.setLabel((TextUtils.isEmpty(childTextCustomer) ? "" : childTextCustomer) + " " + customerNode.getChildText("customerLastName"));
                    }

                    // Status
                    if (appointmentNode.getAttributeValue("status") != null) {
                        String skedStatus = appointmentNode.getAttributeValue(
                                "status").toString();
                        if (skedStatus.equals("ON_ROUTE"))
                            listItem.setStatus(Status.ON_ROUTE);
                        else if (skedStatus.equals("START_APPOINTMENT"))
                            listItem.setStatus(Status.START_APPOINTMENT);
                        else if (skedStatus.equals("SUSPEND_APPOINTMENT"))
                            listItem.setStatus(Status.SUSPEND_APPOINTMENT);
                        else if (skedStatus.equals("RESTART_APPOINTMENT"))
                            listItem.setStatus(Status.RESTART_APPOINTMENT);
                        else if (skedStatus.equals("FINISH_APPOINTMENT"))
                            listItem.setStatus(Status.FINISH_APPOINTMENT);
                        else if (skedStatus.equals("MOVE_APPOINTMENT"))
                            listItem.setStatus(Status.MOVE_APPOINTMENT);
                        else if (skedStatus.equals("CLOSE_APPOINTMENT"))
                            listItem.setStatus(Status.CLOSE_APPOINTMENT);
                        else if (skedStatus.equals("NOT_STARTED"))
                            listItem.setStatus(Status.NOT_STARTED);
                        else if (skedStatus.equals("PARTS_RUN_APPOINTMENT"))
                            listItem.setStatus(Status.PARTS_RUN_APPOINTMENT);
                    }

                    tempList.add(listItem);
                }
            }
        }
        AppDataSingleton.getInstance().getAppointmentList().clear();
        AppDataSingleton.getInstance().getAppointmentList().addAll(tempList);
    }
}