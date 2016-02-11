package com.skeds.android.phone.business.Utilities.REST.Objects;

import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.BusinessHourException;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.NewAppointment;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.RetrievedAppointmentType;
import com.skeds.android.phone.business.Utilities.General.UserUtilitiesSingleton;
import com.skeds.android.phone.business.Utilities.NonfatalException;
import com.skeds.android.phone.business.Utilities.REST.RestConnector;

import org.jdom2.Document;
import org.jdom2.Element;

import java.util.List;

public class RESTAppointmentTypeList {

    /* Appointment types for "new appointment" */
    public static void query() throws NonfatalException {
        Document document = RestConnector.getInstance().httpGet(
                "getappointmenttypes/" + UserUtilitiesSingleton.getInstance().user.getOwnerId());
        AppDataSingleton.getInstance().setNewAppointment(new NewAppointment());
        Element rootNode = document.getRootElement();
        Element ownerNode = rootNode.getChild("owner");
        if (ownerNode == null)
            throw new NonfatalException("XML",
                    "Wrong server response: <owner> missed");
        if (ownerNode.getAttributeValue("earliestTimeMinutes") != null)
            AppDataSingleton.getInstance().getNewAppointment().setEarliestTimeMinutes(
                    Integer.parseInt(ownerNode
                            .getAttributeValue("earliestTimeMinutes")));
        if (ownerNode.getAttributeValue("latestTimeMinutes") != null)
            AppDataSingleton.getInstance().getNewAppointment().setLatestTimeMinutes(
                    Integer.parseInt(ownerNode
                            .getAttributeValue("latestTimeMinutes")));

        Element businessHoursExceptions = rootNode.getChild("businessHoursExceptions");
        if (businessHoursExceptions != null) {
            List<Element> businessHoursExceptionList = businessHoursExceptions.getChildren("businessHoursException");
            AppDataSingleton.getInstance().getNewAppointment().businessHoursExceptionList.clear();

            for (Element element : businessHoursExceptionList) {
                BusinessHourException businessHourException = new BusinessHourException();

                businessHourException.fromTime = element.getChildText("fromTime");
                businessHourException.fromDate = element.getChildText("fromDate");
                businessHourException.toTime = element.getChildText("toTime");
                businessHourException.toDate = element.getChildText("toDate");

                if ((businessHourException.fromDate == null) || (businessHourException.fromTime == null) || (businessHourException.toTime == null) || (businessHourException.toDate == null))
                    continue;

                AppDataSingleton.getInstance().getNewAppointment().businessHoursExceptionList.add(businessHourException);
            }
        }

        Element appointmentTypes = rootNode.getChild("appointmentTypes");
        List<Element> appointmentTypesList = appointmentTypes
                .getChildren("apptType");

        AppDataSingleton.getInstance().getNewAppointment().appointmentTypeList.clear();
        for (int typeIterator = 0; typeIterator < appointmentTypesList.size(); typeIterator++) {

            AppDataSingleton.getInstance().getNewAppointment().appointmentTypeList
                    .add(new RetrievedAppointmentType());

            Element apptType = (Element) appointmentTypesList.get(typeIterator);

            if (apptType.getAttributeValue("id") != null)
                AppDataSingleton.getInstance().getNewAppointment().appointmentTypeList.get(
                        typeIterator).setId(
                        Integer.parseInt(apptType.getAttributeValue("id")));

            if (apptType.getChild("name") != null)
                AppDataSingleton.getInstance().getNewAppointment().appointmentTypeList.get(
                        typeIterator).setName(
                        apptType.getChild("name").getText());

            if (apptType.getChild("incrementMinutes") != null)
                AppDataSingleton.getInstance().getNewAppointment().appointmentTypeList.get(
                        typeIterator).setIncrementMinutes(
                        Integer.parseInt(apptType.getChild("incrementMinutes")
                                .getText()));

            if (apptType.getChild("minimumLengthMinutes") != null)
                AppDataSingleton.getInstance().getNewAppointment().appointmentTypeList.get(
                        typeIterator).setMinimumLengthMinutes(
                        Integer.parseInt(apptType.getChild(
                                "minimumLengthMinutes").getText()));

            if (apptType.getChild("maximumLengthMinutes") != null)
                AppDataSingleton.getInstance().getNewAppointment().appointmentTypeList.get(
                        typeIterator).setMaximumLengthMinutes(
                        Integer.parseInt(apptType.getChild(
                                "maximumLengthMinutes").getText()));
        }
    }
}