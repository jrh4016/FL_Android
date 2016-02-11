package com.skeds.android.phone.business.Utilities.REST.Objects;

import android.text.TextUtils;

import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Appointment;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.AppointmentCustomField;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Comment;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.CustomQuestion;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Customer;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.LeadSource;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Location;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.NewAppointment;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Participant;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Phone;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Status;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.StatusBuffer;
import com.skeds.android.phone.business.Utilities.General.Constants;
import com.skeds.android.phone.business.Utilities.General.UserUtilitiesSingleton;
import com.skeds.android.phone.business.Utilities.NonfatalException;
import com.skeds.android.phone.business.Utilities.REST.RestConnector;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;

import java.math.BigDecimal;
import java.util.List;
import java.util.TimeZone;

public class RESTAppointment {

    /* The appointment being viewed */
    public static void query(int id) throws NonfatalException {
        Document doc = RestConnector.getInstance().httpGet("getappointment/" + id);
        parse(doc);
    }

    public static int querySearchMode(int id) throws NonfatalException {
        Document document = RestConnector.getInstance().httpGet(
                "getappointment/" + id);
        Element appointmentNode = document.getRootElement().getChild(
                "appointment");
        if (appointmentNode != null) {
            String pbSearchModeValue = appointmentNode.getAttributeValue("pbSearchModeOverride");
            if (pbSearchModeValue != null) {
                String mode = pbSearchModeValue;

                if (mode.equals("one_tier"))
                    return Constants.PRICE_BOOK_SEARCH_MODE_ONE_TIER;
                else if (mode.equals("two_tier"))
                    return Constants.PRICE_BOOK_SEARCH_MODE_TWO_TIER;

            }
        }
        return -1;
    }

    public static void parse(Document document) {
        Element appointmentNode = document.getRootElement().getChild(
                "appointment");
        if (appointmentNode == null)
            return;

        Appointment incomingAppointment = new Appointment();

        String idValue = appointmentNode.getAttributeValue("id");
        if (idValue != null)
            incomingAppointment.setId(Integer.parseInt(idValue));

        if (appointmentNode.getAttributeValue("leadSourceId") != null) {
            incomingAppointment.setLeadSourceId(appointmentNode.getAttributeValue("leadSourceId"));
        }

        String workOrderNumberValue = appointmentNode.getAttributeValue("workOrderNumber");
        if (workOrderNumberValue != null)
            incomingAppointment.setWorkOrderNumber(workOrderNumberValue);

        String workOrderNumberPrefixValue = appointmentNode.getAttributeValue("workOrderNumberPrefix");
        if (workOrderNumberPrefixValue != null)
            incomingAppointment.setWorkOrderNumberPrefix(workOrderNumberPrefixValue);

        String multiDayAppointmentValue = appointmentNode.getAttributeValue("multiDayAppointmentString");
        if (multiDayAppointmentValue != null)
            incomingAppointment.setMultiDay(multiDayAppointmentValue);

        String pbSearchModeValue = appointmentNode.getAttributeValue("pbSearchModeOverride");
        if (pbSearchModeValue != null) {
            String mode = pbSearchModeValue;
            if (mode.equals("one_tier"))
                incomingAppointment.setSearchModeOverride(Constants.PRICE_BOOK_SEARCH_MODE_ONE_TIER);
            else if (mode.equals("two_tier"))
                incomingAppointment.setSearchModeOverride(Constants.PRICE_BOOK_SEARCH_MODE_TWO_TIER);
            else if (mode.equals("simple"))
                incomingAppointment.setSearchModeOverride(Constants.PRICE_BOOK_SEARCH_MODE_DEFAULT);
        }

        String usingInvoicesValue = appointmentNode.getAttributeValue("usingInvoices");
        if (usingInvoicesValue != null)
            incomingAppointment.setUsingInvoices(Boolean
                    .parseBoolean(usingInvoicesValue));

        String everybodyElseFinishedValue = appointmentNode.getAttributeValue("everybodyElseFinished");
        if (everybodyElseFinishedValue != null)
            incomingAppointment.setEverybodyElseFinished(Boolean
                    .parseBoolean(everybodyElseFinishedValue));

        String invoiceIdValue = appointmentNode.getAttributeValue("invoiceId");
        if (invoiceIdValue != null)
            incomingAppointment.setInvoiceId(Integer.parseInt(invoiceIdValue));

        String canUpdateValue = appointmentNode.getAttributeValue("canUpdate");
        if (canUpdateValue != null)
            incomingAppointment.setCanUpdate(Boolean.getBoolean(canUpdateValue));

        String hasCustomQuestionsValue = appointmentNode.getAttributeValue("hasCustomQuestions");
        if (hasCustomQuestionsValue != null)
            incomingAppointment.setHasCustomQuestions(Boolean.getBoolean(hasCustomQuestionsValue));

        Element customInvoiceFieldChild = appointmentNode.getChild("customInvoiceField");
        if (customInvoiceFieldChild != null) {
            AppointmentCustomField appointmentCustomField = new AppointmentCustomField();
            Element customFieldNameChild = customInvoiceFieldChild.getChild("name");
            if (customFieldNameChild != null)
                appointmentCustomField.setName(customFieldNameChild.getText());
            Element customFieldValueChild = customInvoiceFieldChild.getChild("value");
            if (customFieldValueChild != null)
                appointmentCustomField.setValue(customFieldValueChild.getText());
            incomingAppointment.setCustomField(appointmentCustomField);
        }

        Element customSecondInvoiceFieldChild = appointmentNode.getChild("secondCustomInvoiceField");
        if (customSecondInvoiceFieldChild != null) {
            AppointmentCustomField appointmentCustomField = new AppointmentCustomField();
            Element customFieldNameChild = customSecondInvoiceFieldChild.getChild("name");
            if (customFieldNameChild != null)
                appointmentCustomField.setName(customFieldNameChild.getText());
            Element customFieldValueChild = customSecondInvoiceFieldChild.getChild("value");
            if (customFieldValueChild != null)
                appointmentCustomField.setValue(customFieldValueChild.getText());
            incomingAppointment.setSecondCustomField(appointmentCustomField);
        }

        // Appointment Status
        String statusValue = appointmentNode.getAttributeValue("status");
        if (statusValue != null) {
            String skedStatus = statusValue
                    .toString();
            if (skedStatus.equals("ON_ROUTE")) {
                incomingAppointment
                        .setStatus(Status.ON_ROUTE);
            } else if (skedStatus.equals("START_APPOINTMENT")) {
                incomingAppointment
                        .setStatus(Status.START_APPOINTMENT);
            } else if (skedStatus.equals("SUSPEND_APPOINTMENT")) {
                incomingAppointment
                        .setStatus(Status.SUSPEND_APPOINTMENT);
            } else if (skedStatus.equals("RESTART_APPOINTMENT")) {
                incomingAppointment
                        .setStatus(Status.RESTART_APPOINTMENT);
            } else if (skedStatus.equals("FINISH_APPOINTMENT")) {
                incomingAppointment
                        .setStatus(Status.FINISH_APPOINTMENT);
            } else if (skedStatus.equals("MOVE_APPOINTMENT")) {
                incomingAppointment
                        .setStatus(Status.MOVE_APPOINTMENT);
            } else if (skedStatus.equals("CLOSE_APPOINTMENT")) {
                incomingAppointment
                        .setStatus(Status.CLOSE_APPOINTMENT);
            } else if (skedStatus.equals("NOT_STARTED")) {
                incomingAppointment
                        .setStatus(Status.NOT_STARTED);
            } else if (skedStatus.equals("PARTS_RUN_APPOINTMENT")) {
                incomingAppointment
                        .setStatus(Status.PARTS_RUN_APPOINTMENT);
            }
        }

        String participantIdValue = appointmentNode.getAttributeValue("participantId");
        if (participantIdValue != null)
            incomingAppointment.setParticipantId(Integer
                    .parseInt(participantIdValue));

        if (pbSearchModeValue != null) {
            String mode = pbSearchModeValue;
            if (mode.equals("one_tier"))
                AppDataSingleton.getInstance().setPriceBookSearchModeOverride("one_tier");
            else if (mode.equals("two_tier"))
                AppDataSingleton.getInstance().setPriceBookSearchModeOverride("two_tier");
            else if (mode.equals("simple"))
                AppDataSingleton.getInstance().setPriceBookSearchModeOverride("simple");
        } else
            AppDataSingleton.getInstance().setPriceBookSearchModeOverride("");


        Element ownerNode = appointmentNode.getChild("owner");
        if (ownerNode != null) {

            String nameValue = ownerNode.getAttributeValue("name");
            if (nameValue != null)
                incomingAppointment.setOwnerName(nameValue);

            String ownerIdValue = ownerNode.getAttributeValue("id");
            if (ownerIdValue != null)
                incomingAppointment.setOwnerId(Integer.parseInt(ownerIdValue));

            String timeTrackingEnabledValue = ownerNode.getAttributeValue("timeTrackingEnabled");
            if (timeTrackingEnabledValue != null)
                incomingAppointment.setOwnerTimeTrackingEnabled(Boolean
                        .parseBoolean(timeTrackingEnabledValue));


            String forceWorkOrderNumberInputValue = ownerNode.getAttributeValue("forceWorkOrderNumberInput");
            if (forceWorkOrderNumberInputValue != null)
                incomingAppointment
                        .setOwnerForceWorkOrderNumberInput(Boolean.parseBoolean(forceWorkOrderNumberInputValue));
        }


        Element appointmentType = appointmentNode.getChild("apptType");
        if (appointmentType != null) {
            AppDataSingleton.getInstance().setNewAppointment(new NewAppointment());
            NewAppointment newAppointment = AppDataSingleton.getInstance().getNewAppointment();

            String appointmentTypeIdValue = appointmentType.getAttributeValue("id");
            if (appointmentTypeIdValue != null)
                incomingAppointment.setApptTypeId(Integer
                        .parseInt(appointmentTypeIdValue));

            String apptTypeNameText = appointmentType.getChildText("apptTypeName");
            if (apptTypeNameText != null)
                incomingAppointment.setApptTypeName(apptTypeNameText);

            String apptTypeMinimumValue = appointmentType.getAttributeValue("apptTypeMinimum");
            if (apptTypeMinimumValue != null)
                newAppointment.setEarliestTimeMinutes(
                        Integer.parseInt(apptTypeMinimumValue));

            String apptTypeMaximumValue = appointmentType.getAttributeValue("apptTypeMaximum");
            if (apptTypeMaximumValue != null)
                newAppointment.setLatestTimeMinutes(Integer.parseInt(apptTypeMaximumValue));

            String apptTypeDurationValue = appointmentType.getAttributeValue("apptTypeDuration");
            if (apptTypeDurationValue != null)
                NewAppointment.singleAppointmentDuration = Integer.parseInt(apptTypeDurationValue);
        }

        String startDateText = appointmentNode.getChildText("startDate");
        if (startDateText != null)
            incomingAppointment.setDate(startDateText);

        String startTimeText = appointmentNode.getChildText("startTime");
        if (startTimeText != null)
            incomingAppointment.setStartTime(startTimeText);

        String endTimeText = appointmentNode.getChildText("endTime");
        if (endTimeText != null)
            incomingAppointment.setEndTime(endTimeText);

        String appointmentNoteText = appointmentNode.getChildText("appointmentNote");
        if (appointmentNoteText != null)
            incomingAppointment.setNotes(appointmentNoteText);

        final Element appointmentLocation = appointmentNode.getChild("appointmentLocation");
        if (appointmentLocation != null) {
            String locationNameValue = appointmentLocation.getAttributeValue("name");
            if (locationNameValue != null)
                incomingAppointment.setLocationName(locationNameValue);

            incomingAppointment.setLocationTaxable(Boolean.valueOf(appointmentLocation.getAttributeValue("taxable")));

            if (appointmentLocation.getAttributeValue("timeZone") != null)
                incomingAppointment.setTimeZone(TimeZone.getTimeZone(appointmentLocation.getAttributeValue("timeZone")));

            String locationIdValue = appointmentLocation.getAttributeValue("id");
            if (locationIdValue != null)
                incomingAppointment.setLocationId(Integer
                        .parseInt(locationIdValue));

            String latitudeValue = appointmentLocation.getAttributeValue("latitude");
            if (latitudeValue != null)
                incomingAppointment.setLocationLatitude(latitudeValue);

            String longitudeValue = appointmentLocation.getAttributeValue("longitude");
            if (longitudeValue != null)
                incomingAppointment.setLocationLongitude(longitudeValue);

            final String phone1 = appointmentLocation.getAttributeValue("phone1");
            if (phone1 != null)
                incomingAppointment.setPhone1(phone1);

            final String phone1Desc = appointmentLocation.getAttributeValue("phone1Description");
            if (phone1Desc != null)
                incomingAppointment.setPhone1Description(phone1Desc);

            final String phone2 = appointmentLocation.getAttributeValue("phone2");
            if (phone1 != null)
                incomingAppointment.setPhone2(phone2);

            final String phone2Desc = appointmentLocation.getAttributeValue("phone2Description");
            if (phone2Desc != null)
                incomingAppointment.setPhone2Description(phone2Desc);
        }

        String appointmentLocationText = appointmentNode.getChildText("appointmentLocation");
        if (appointmentLocationText != null) {
            incomingAppointment.setLocationValue(appointmentLocationText);

            Attribute customCodeAttribute = appointmentNode.getChild("appointmentLocation").getAttribute("customCode");
            if (customCodeAttribute != null)
                incomingAppointment.setAppointmentLocationCustomCode(appointmentNode
                        .getChild("appointmentLocation").getAttributeValue("customCode"));
        }

        Element participantsNode = appointmentNode.getChild("participants");
        if (participantsNode != null) {

            List<Element> participantList = participantsNode
                    .getChildren("participant");
            incomingAppointment.getParticipantList().clear();

            if (!participantList.isEmpty()) {
                for (int i = 0; i < participantList.size(); i++) {
                    incomingAppointment.getParticipantList().add(new Participant());
                    Participant participant = incomingAppointment.getParticipantList().get(i);

                    Element participantNode = (Element) participantList.get(i);

                    String particIdValue = participantNode.getAttributeValue("id");
                    if (particIdValue != null)
                        participant.setId(
                                Integer.parseInt(particIdValue));

                    String participantFirstNameText = participantNode.getChildText("participantFirstName");
                    if (participantFirstNameText != null)
                        participant.setFirstName(participantFirstNameText);


                    String participantLastNameText = participantNode.getChildText("participantLastName");
                    if (participantLastNameText != null)
                        participant.setLastName(participantLastNameText);

                    Element participantTypeNode = participantNode.getChild("participantType");
                    if (participantTypeNode != null) {
                        String particTypeIdValue = participantTypeNode.getAttributeValue("id");
                        if (particTypeIdValue != null)
                            participant.setTypeId(Integer.parseInt(particTypeIdValue));

                        String participantTypeNameText = participantTypeNode.getChildText("participantTypeName");
                        if (participantTypeNameText != null)
                            participant.setTypeName(participantTypeNameText);

                        String emailText = participantNode.getChildText("email");
                        if (emailText != null)
                            participant.setEmail(emailText);
                    }
                }
            }
        }

        Element agreementNode = appointmentNode.getChild("serviceAgreement");
        if (agreementNode != null) {
            incomingAppointment.setSelectedAgreementId(Integer.parseInt(agreementNode.getAttributeValue("id")));
            incomingAppointment.setSelectedAgreementDescription(agreementNode.getAttributeValue("description"));
            incomingAppointment.setSelectedAgreementName(agreementNode.getText());
        }
        Customer customer = null;
        Element customersNode = appointmentNode.getChild("customers");
        if (customersNode != null) {
            List<Element> customerList = customersNode.getChildren("customer");
            if (!customerList.isEmpty()) {
                for (int i = 0; i < customerList.size(); i++) {
                    Element customerNode = customerList.get(i);
                    AppDataSingleton.getInstance().setCustomer(new Customer());
                    customer = AppDataSingleton.getInstance().getCustomer();

                    String customerIdValue = customerNode.getAttributeValue("id");
                    if (customerIdValue != null)
                        customer.setId(Integer.parseInt(customerIdValue));

                    String customerTypeValue = customerNode.getAttributeValue("type");
                    if (customerTypeValue != null)
                        customer.setType(customerTypeValue);

                    String unreliableValue = customerNode.getAttributeValue("unreliable");
                    if (unreliableValue != null)
                        customer.setUnreliable(Boolean.parseBoolean(unreliableValue));

                    customer.setTaxable(Boolean.valueOf(customerNode.getAttributeValue("taxable")));

                    String unreliableFlagTextValue = customerNode.getAttributeValue("unreliableFlagText");
                    if (unreliableFlagTextValue != null)
                        customer.setReliableNotes(unreliableFlagTextValue);

                    String taxRateValue = customerNode.getChildText("taxRate");
                    if (taxRateValue != null) {
                        AppDataSingleton.getInstance().setCustomerEstimateTaxRate(new BigDecimal(taxRateValue)
                                .setScale(4,
                                        BigDecimal.ROUND_HALF_UP));
                    } else {
                        AppDataSingleton.getInstance().setCustomerEstimateTaxRate(BigDecimal.ZERO);
                    }

                    String customerFirstNameText = customerNode.getChildText("customerFirstName");
                    if (customerFirstNameText != null)
                        customer.setFirstName(customerFirstNameText);

                    String customerLastNameText = customerNode.getChildText("customerLastName");
                    if (customerLastNameText != null)
                        customer.setLastName(customerLastNameText);

//					// Parse agreement list
//					incomingAppointment.getAgreementList().clear();
//					Element agreementsNode = appointmentNode.getChild("agreements");
//					if (agreementsNode != null) {
//						List<Element> agreementList = agreementsNode.getChildren("agreement");
//						if (agreementList != null)
//							if (!agreementList.isEmpty()) {
//								Agreement agr = new Agreement();
//								String customerIdValue = customerNode.getAttributeValue("id");
//								if (customerIdValue != null)
//									customer.setId(Integer.parseInt(customerIdValue));
//							}
//					}


                    // Customer E-Mail
                    int x = 0;
                    customer.email.clear();
                    if (customerNode.getChildText("email") != null) {
                        customer.email.add(customerNode
                                .getChildText("email"));

                        if (customerNode.getChild("email").getAttributeValue(
                                "description") != null)
                            customer.emailDescription
                                    .add(customerNode.getChild("email")
                                            .getAttributeValue("description"));
                        else
                            customer.emailDescription.add(null);
                    }

                    // Additional Emails
                    x = 2;
                    while (true) {
                        if (customerNode.getChildText("email" + x) != null) {
                            customer.email.add(customerNode
                                    .getChildText("email" + x));

                            if (customerNode.getChild("email" + x)
                                    .getAttributeValue("description") != null) {
                                customer.emailDescription
                                        .add(customerNode.getChild("email" + x)
                                                .getAttributeValue(
                                                        "description"));
                            } else
                                customer.emailDescription
                                        .add(null);

                            x++;
                        } else {
                            break;
                        }
                    }

                    customer.phone.clear();
                    customer.getPhones().clear();
                    x = 1;
                    while (true) {
                        if (customerNode.getChildText("phone" + x) != null) {

                            Phone p = new Phone();

                            if (customerNode.getChildText("phone" + x) != null) {
                                p.setNumber(customerNode.getChildText("phone"
                                        + x));
                            }
                            if (customerNode.getChild("phone" + x)
                                    .getAttributeValue("type") != null) {
                                p.setType(customerNode.getChild("phone" + x)
                                        .getAttributeValue("type"));
                            }

                            if (customerNode.getChild("phone" + x)
                                    .getAttributeValue("description") != null)
                                p.setDescription(customerNode.getChild(
                                        "phone" + x).getAttributeValue(
                                        "description"));
                            customer.getPhones().add(p);
                            customer.phone.add(p.getNumber());
                            x++;
                        } else {
                            break;
                        }
                    }


                    // Customer Organization
                    if (customerNode.getChildText("orgName") != null)
                        customer.setOrganizationName(
                                customerNode.getChildText("orgName"));

                    // Customer Notes
                    if (customerNode.getChildText("internalCustomerInfo") != null)
                        customer.setNotes(
                                customerNode
                                        .getChildText("internalCustomerInfo"));

                    List<Element> locationsList = customerNode
                            .getChildren("locations");
                    Element locationsNode = (Element) locationsList.get(0);
                    List<Element> locationList = locationsNode
                            .getChildren("location");
                    customer.locationList.clear();

                    if (locationsNode.getAttributeValue("count") != null) {
                        AppDataSingleton.getInstance().getCustomer().locationsCount = Integer.parseInt(locationsNode.getAttributeValue("count"));
                    }

                    if (!locationList.isEmpty()) {
                        for (int locationIterator = 0; locationIterator < locationList
                                .size(); locationIterator++) {

                            customer.locationList
                                    .add(new Location());

                            Element locationNode = locationList.get(locationIterator);

                            // Id
                            if (locationNode.getAttributeValue("id") != null)
                                customer.locationList.get(
                                        locationIterator).setId(
                                        Integer.parseInt(locationNode
                                                .getAttributeValue("id")));

                            // Latitude
                            if (locationNode.getAttributeValue("latitude") != null)
                                customer.locationList.get(
                                        locationIterator).setLatitude(
                                        locationNode
                                                .getAttributeValue("latitude"));

                            // Longitude
                            if (locationNode.getAttributeValue("longitude") != null)
                                customer.locationList
                                        .get(locationIterator)
                                        .setLongitude(
                                                locationNode
                                                        .getAttributeValue("longitude"));

                            // Name
                            if (locationNode.getChild("name") != null
                                    && locationNode.getChild("name").getValue() != null)
                                customer.locationList.get(
                                        locationIterator).setName(
                                        locationNode.getChild("name")
                                                .getValue());

                            // Address 1
                            if (locationNode.getChild("address1") != null
                                    && locationNode.getChild("address1")
                                    .getValue() != null)
                                customer.locationList.get(
                                        locationIterator).setAddress1(
                                        locationNode.getChild("address1")
                                                .getValue());

                            // Address 2
                            if (locationNode.getChild("address2") != null
                                    && locationNode.getChild("address2")
                                    .getValue() != null)
                                customer.locationList.get(
                                        locationIterator).setAddress2(
                                        locationNode.getChild("address2")
                                                .getValue());

                            // City
                            if (locationNode.getChild("city") != null
                                    && locationNode.getChild("city").getValue() != null)
                                customer.locationList.get(
                                        locationIterator).setCity(
                                        locationNode.getChild("city")
                                                .getValue());

                            // State
                            if (locationNode.getChild("state") != null
                                    && locationNode.getChild("state")
                                    .getValue() != null)
                                customer.locationList.get(
                                        locationIterator).setState(
                                        locationNode.getChild("state")
                                                .getValue());

                            // Zip
                            if (locationNode.getChild("zip") != null
                                    && locationNode.getChild("zip").getValue() != null)
                                customer.locationList.get(
                                        locationIterator)
                                        .setZip(locationNode.getChild("zip")
                                                .getValue());

                            customer.locationList.get(locationIterator).setTaxable(Boolean.valueOf(locationNode.getAttributeValue("taxable")));
                        }
                    }
                }
            }
        }

        incomingAppointment.setCustomer(customer);

        incomingAppointment.getCommentList().clear();
        List<Element> commentsList = appointmentNode.getChildren("comments");
        if (!commentsList.isEmpty()) {

            Element commentsNode = (Element) commentsList.get(0);
            List<Element> commentList = commentsNode.getChildren("comment");

            // AppDataSingleton.getInstance().getAppointment().setCommentArraySize(commentList
            // .size());
            for (int commentIterator = 0; commentIterator < commentList.size(); commentIterator++) {

                incomingAppointment.getCommentList().add(new Comment());
                // AppDataSingleton.getInstance().getAppointment().comments[commentIterator]
                // = new Comment();

                Element commentNode = (Element) commentList
                        .get(commentIterator);

                // Comment ID
                if (commentNode.getAttributeValue("id") != null)
                    incomingAppointment.getCommentList().get(commentIterator).setId(
                            Integer.parseInt(commentNode
                                    .getAttributeValue("id")));
                // AppDataSingleton.getInstance().getAppointment().comments[commentIterator]
                // .setId(Integer.parseInt(commentNode
                // .getAttributeValue("id")));

                // Comment Text
                if (commentNode.getChildText("text") != null)
                    incomingAppointment.getCommentList().get(commentIterator)
                            .setText(commentNode.getChildText("text"));

                // Hop into the "poster" section
                List<Element> posterList = commentNode.getChildren("poster");
                Element poster = (Element) posterList.get(0);

                // Poster Name
                if (poster.getChildText("posterName") != null)
                    incomingAppointment.getCommentList().get(commentIterator)
                            .setPosterName(poster.getChildText("posterName"));

                // Poster TimeDate
                if (poster.getChildText("postDateTime") != null)
                    incomingAppointment.getCommentList().get(commentIterator)
                            .setPosterDateTime(
                                    poster.getChildText("postDateTime"));

                // Poster Email
                if (poster.getChildText("email") != null)
                    incomingAppointment.getCommentList().get(commentIterator)
                            .setPosterEmail(poster.getChildText("email"));
            }
        }

        incomingAppointment.getEquipmentList().clear();
        if (appointmentNode.getChild("piecesOfequipment") != null) {

            List<Element> equipmentListNode = appointmentNode.getChild(
                    "piecesOfequipment").getChildren("equipment");

            for (int equipmentIterator = 0; equipmentIterator < equipmentListNode
                    .size(); equipmentIterator++) {

                Appointment.pieceOfEquipment pieceOfEquipment = incomingAppointment.new pieceOfEquipment();

                Element equipmentNode = (Element) equipmentListNode
                        .get(equipmentIterator);

                // ID
                if (equipmentNode.getAttributeValue("id") != null)
                    pieceOfEquipment.setId(Integer.parseInt(equipmentNode
                            .getAttributeValue("id")));

                // Name
                if (equipmentNode.getChild("name") != null)
                    pieceOfEquipment.setName(equipmentNode.getChild("name")
                            .getText());

                // Model Number
                if (equipmentNode.getChild("modelNumber") != null)
                    pieceOfEquipment.setModelNumber(equipmentNode.getChild(
                            "modelNumber").getText());

                // Serial Number
                if (equipmentNode.getChild("serialNumber") != null)
                    pieceOfEquipment.setSerialNumber(equipmentNode.getChild(
                            "serialNumber").getText());

                // Manufacturer Id
                if (equipmentNode.getChild("manufacturer") != null)
                    pieceOfEquipment.setManufacturerId(Integer
                            .parseInt(equipmentNode.getChild("manufacturer")
                                    .getAttributeValue("id")));

                // Manufacturer Name
                if (equipmentNode.getChild("manufacturer") != null)
                    pieceOfEquipment.setManufacturer(equipmentNode.getChild(
                            "manufacturer").getText());

                incomingAppointment.getEquipmentList().add(pieceOfEquipment);
            }
        }

        AppDataSingleton.getInstance().getCustomQuestionList().clear();
        if (appointmentNode.getChild("customQuestions") != null) {
            List<Element> questionNodes = appointmentNode.getChild(
                    "customQuestions").getChildren("question");

            if (!questionNodes.isEmpty()) {
                for (int i = 0; i < questionNodes.size(); i++) {

                    AppDataSingleton.getInstance().getCustomQuestionList().add(new CustomQuestion());
                    Element questionNode = (Element) questionNodes.get(i);

                    if (questionNode.getAttribute("id") != null)
                        AppDataSingleton.getInstance().getCustomQuestionList()
                                .get(i)
                                .setId(Integer.parseInt(questionNode
                                        .getAttributeValue("id")));

                    if (questionNode.getChild("text") != null)
                        AppDataSingleton.getInstance().getCustomQuestionList().get(i)
                                .setText(questionNode.getChildText("text"));

                    if (questionNode.getChild("metaType") != null)
                        AppDataSingleton.getInstance().getCustomQuestionList()
                                .get(i)
                                .setMetaType(
                                        questionNode.getChildText("metaType"));

                    if (questionNode.getChild("required") != null)
                        AppDataSingleton.getInstance().getCustomQuestionList()
                                .get(i)
                                .setRequired(
                                        Boolean.parseBoolean(questionNode
                                                .getChildText("required")));

                    if (questionNode.getChild("answer") != null) {
                        AppDataSingleton.getInstance().getCustomQuestionList()
                                .get(i)
                                .setAnswer(
                                        questionNode.getChild("answer")
                                                .getText());
                    }

                }
            }
        }
        AppDataSingleton.getInstance().setAppointment(incomingAppointment); // Set it!
    }

    public static void statusUpdate(int appointmentId, String status,
                                    int participantId, double latitude, double longitude,
                                    double accuracy, String workOrderNumber, String timeStamp,
                                    boolean addToBuffer, TimeZone timeZone) throws NonfatalException {
        Element root = new Element("updateTrackableTask");
        root.detach();

        Element newStatus = new Element("newStatus");
        newStatus.setText(status);
        root.addContent(newStatus);

        Element participantIdNode = new Element("participantId");
        participantIdNode.setText(String.valueOf(participantId));
        root.addContent(participantIdNode);

        Element latitudeNode = new Element("latitude");
        latitudeNode.setText(String.valueOf(latitude));
        root.addContent(latitudeNode);

        Element longitudeNode = new Element("longitude");
        longitudeNode.setText(String.valueOf(longitude));
        root.addContent(longitudeNode);

        Element accuracyNode = new Element("accuracy");
        accuracyNode.setText(String.valueOf(accuracy));
        root.addContent(accuracyNode);

        if (!workOrderNumber.isEmpty()) {
            Element workOrderNumberNode = new Element("workOrderNumber");
            workOrderNumberNode.setText(workOrderNumber);
            root.addContent(workOrderNumberNode);
        }

        Element timestampNode = new Element("timestamp");
        timestampNode.setAttribute("tz", timeZone.getID());
        timestampNode.setText(timeStamp);
        root.addContent(timestampNode);

        if (!addToBuffer) {
            Document doc = new Document();
            doc.removeContent();
            doc.setContent(root);
            Document response = RestConnector.getInstance().httpPostCheckSuccess(doc,
                    "updatetrackabletask/" + appointmentId);
            Element invoiceNode = response.getRootElement().getChild(
                    "appointment").getChild("invoice");
            if (invoiceNode == null)
                return;
            if (invoiceNode.getAttributeValue("id") != null)
                AppDataSingleton.getInstance().getAppointment().setInvoiceId(Integer.parseInt(invoiceNode
                        .getAttributeValue("id")));

            // This means that they needed a buffer, which means we're just
            // going to pile it on for them, and not try and actually submit the
            // data
        } else {
            StatusBuffer.instance().append(AppDataSingleton.getInstance().getAppointment().getId(),
                    root);
        }
    }

    // TODO - Need POST and UDPATE functions
    public static void add(String appointmentTypeId, String startDate,
                           String endDate, String notes, String locationId,
                           boolean isAfterHours, TimeZone timeZone, LeadSource leadSource) throws NonfatalException {
        Element root = new Element("addTrackableTask");

        Element appointmentTypeNode = new Element("appointmentTypeId");
        appointmentTypeNode.setText(appointmentTypeId);
        root.addContent(appointmentTypeNode);

        Element serviceProvider = new Element("serviceProviderId");
        serviceProvider.setText(String.valueOf(UserUtilitiesSingleton.getInstance().user
                .getServiceProviderId()));
        root.addContent(serviceProvider);

        if (!TextUtils.isEmpty(startDate)) {
            Element startDateTime = new Element("startDateTime");
            startDateTime.setAttribute("tz", timeZone.getID());
            startDateTime.setText(startDate);
            root.addContent(startDateTime);

            Element unscheduled = new Element("unscheduled");
            unscheduled.setText("false");
            root.addContent(unscheduled);
        } else {
            Element unscheduled = new Element("unscheduled");
            unscheduled.setText("true");
            root.addContent(unscheduled);

            Element priority = new Element("priority");
            priority.setText("low");
            root.addContent(priority);

            Element suggestedDate = new Element("suggestedDate");
            suggestedDate.setText("01/01/2013");
            root.addContent(suggestedDate);
        }

        if (!TextUtils.isEmpty(endDate)) {
            Element endDateTime = new Element("endDateTime");
            endDateTime.setAttribute("tz", timeZone.getID());
            endDateTime.setText(endDate);
            root.addContent(endDateTime);
        }

        Element note = new Element("note");
        note.setText(notes);
        root.addContent(note);

        Element locationIdNode = new Element("locationId");
        locationIdNode.setText(locationId);
        root.addContent(locationIdNode);

        Element afterHoursNode = new Element("afterHours");
        afterHoursNode.setText(String.valueOf(isAfterHours));
        root.addContent(afterHoursNode);


        if (leadSource != null) {
            Element leadSourceId = new Element("leadSourceId");
            leadSourceId.setText(leadSource.getId());
            root.addContent(leadSourceId);
        }

        RestConnector.getInstance().httpPostCheckSuccess(new Document(root),
                "addtrackabletask/" + AppDataSingleton.getInstance().getCustomer().getId());
    }

    public static void add(String appointmentType, String suggestedDate,
                           boolean isUnscheduled, String priorityStr, String notes,
                           String locationId, boolean isAfterHours, LeadSource leadSource) throws NonfatalException {
        Element root = new Element("editTrackableTask");

        Element leadSourceId = new Element("leadSourceId");
        leadSourceId.setText(leadSource.getId());
        root.addContent(leadSourceId);

        Element appointmentTypeNode = new Element("appointmentTypeId");
        appointmentTypeNode.setText(appointmentType);
        root.addContent(appointmentTypeNode);

        if (!TextUtils.isEmpty(suggestedDate) && !"HIGH".equals(priorityStr)) {
            Element suggestedDateTime = new Element("suggestedDate");
            suggestedDateTime.setText(suggestedDate);
            root.addContent(suggestedDateTime);
        }

        Element unscheduled = new Element("unscheduled");
        if (isUnscheduled) {
            unscheduled.setText("true");
        } else {
            unscheduled.setText("false");
        }
        root.addContent(unscheduled);

        if (!TextUtils.isEmpty(priorityStr)) {
            Element priority = new Element("priority");
            priority.setText(priorityStr);
            root.addContent(priority);
        }

        Element note = new Element("note");
        note.setText(notes);
        root.addContent(note);

        Element locationIdNode = new Element("locationId");
        locationIdNode.setText(locationId);
        root.addContent(locationIdNode);

        Element afterHoursNode = new Element("afterHours");
        afterHoursNode.setText(String.valueOf(isAfterHours));
        root.addContent(afterHoursNode);

        RestConnector.getInstance().httpPostCheckSuccess(new Document(root),
                "addtrackabletask/" + AppDataSingleton.getInstance().getCustomer().getId());
    }

    // TODO - Need POST and UDPATE functions
    public static void update(String appointmentType, String startDate,
                              String endDate, String notes, String locationId,
                              boolean isAfterHours, TimeZone timeZone, LeadSource leadSource) throws NonfatalException {
        int apptId = AppDataSingleton.getInstance().getAppointment().getId();

        Element root = new Element("editTrackableTask");

        if (leadSource != null) {
            Element leadSourceId = new Element("leadSourceId");
            leadSourceId.setText(leadSource.getId());
            root.addContent(leadSourceId);
        }

        if (appointmentType != null) {
            Element appointmentTypeNode = new Element("appointmentTypeId");
            appointmentTypeNode.setText(appointmentType);
            root.addContent(appointmentTypeNode);
        }


        Element unscheduled = new Element("unscheduled");
        unscheduled.setText("false");
        root.addContent(unscheduled);

        if (!TextUtils.isEmpty(startDate)) {
            Element startDateTime = new Element("startDateTime");
            startDateTime.setAttribute("tz", timeZone.getID());
            startDateTime.setText(startDate);
            root.addContent(startDateTime);
        }

        if (!TextUtils.isEmpty(endDate)) {
            Element endDateTime = new Element("endDateTime");
            endDateTime.setAttribute("tz", timeZone.getID());
            endDateTime.setText(endDate);
            root.addContent(endDateTime);
        }

        Element note = new Element("note");

        note.setText(notes);
        root.addContent(note);

        if (locationId != null) {
            Element locationIdNode = new Element("locationId");
            locationIdNode.setText(locationId);
            root.addContent(locationIdNode);
        }

        Element afterHoursNode = new Element("afterHours");
        afterHoursNode.setText(String.valueOf(isAfterHours));
        root.addContent(afterHoursNode);

        
        RestConnector.getInstance().httpPostCheckSuccess(new Document(root),
                "edittrackabletask/" + apptId);
    }

    public static void update(String appointmentType, String suggestedDate,
                              boolean isUnscheduled, String priorityStr, String notes,
                              String locationId, boolean isAfterHours, LeadSource leadSource) throws NonfatalException {
        int apptId = AppDataSingleton.getInstance().getAppointment().getId();

        Element root = new Element("editTrackableTask");

        Element appointmentTypeNode = new Element("appointmentTypeId");
        appointmentTypeNode.setText(appointmentType);
        root.addContent(appointmentTypeNode);

        Element leadSourceId = new Element("leadSourceId");
        leadSourceId.setText(leadSource.getId());
        root.addContent(leadSourceId);

        if (!TextUtils.isEmpty(suggestedDate) && !priorityStr.equals("HIGH")) {
            Element suggestedDateTime = new Element("suggestedDate");
            suggestedDateTime.setText(suggestedDate);
            root.addContent(suggestedDateTime);
        }

        Element unscheduled = new Element("unscheduled");
        if (isUnscheduled) {
            unscheduled.setText("true");
        } else {
            unscheduled.setText("false");
        }
        root.addContent(unscheduled);

        if (!TextUtils.isEmpty(priorityStr)) {
            Element priority = new Element("priority");
            priority.setText(priorityStr);
            root.addContent(priority);
        }

        Element note = new Element("note");

        note.setText(notes);
        root.addContent(note);

        Element locationIdNode = new Element("locationId");
        locationIdNode.setText(locationId);
        root.addContent(locationIdNode);

        Element afterHoursNode = new Element("afterHours");
        afterHoursNode.setText(String.valueOf(isAfterHours));
        root.addContent(afterHoursNode);

        RestConnector.getInstance().httpPostCheckSuccess(new Document(root),
                "edittrackabletask/" + apptId);
    }

    /**
     * Will set the appointment status to "Closed"
     *
     * @param appointmentId
     * @param serviceProviderId
     * @param latitude
     * @param longitude
     * @param accuracy
     * @param dateTime
     * @return
     * @throws NonfatalException
     */
    public static void close(int appointmentId, int serviceProviderId,
                             String latitude, String longitude, String accuracy, String dateTime, TimeZone timeZone)
            throws NonfatalException {
        Element rootNode = new Element("updateTrackableTask");

        Element newStatusNode = new Element("newStatus");
        newStatusNode.setText("CLOSE_APPOINTMENT");
        rootNode.addContent(newStatusNode);

        Element participantIdNode = new Element("participantId");
        participantIdNode.setText(String.valueOf(UserUtilitiesSingleton.getInstance().user
                .getServiceProviderId()));
        rootNode.addContent(participantIdNode);

        if (AppDataSingleton.getInstance().getInvoiceViewMode() == Constants.INVOICE_VIEW_FROM_APPOINTMENT) {
            // if (AppDataSingleton.getInstance().getInvoiceMode() ==
            // Constants.INVOICE_MODE_FROM_APPOINTMENT_DETAILS) {
            Element latitudeNode = new Element("latitude");
            latitudeNode.setText(latitude);
            rootNode.addContent(latitudeNode);

            Element longitudeNode = new Element("longitude");
            longitudeNode.setText(longitude);
            rootNode.addContent(longitudeNode);

            Element accuracyNode = new Element("accuracy");
            accuracyNode.setText(accuracy);
            rootNode.addContent(accuracyNode);

            Element timeStampNode = new Element("timestamp");
            timeStampNode.setText(dateTime);
            timeStampNode.setAttribute("tz", timeZone.getID());
            rootNode.addContent(timeStampNode);
        }

        Document doc = new Document();
        doc.removeContent();
        doc.setContent(rootNode);
        RestConnector.getInstance().httpPostCheckSuccess(doc,
                "updatetrackabletask/" + appointmentId);
    }

    /**
     * Creates a new appointment from the "estimate" view
     *
     * @param estimateId
     * @param appointmentTypeId
     * @param suggestedDate
     * @param notes
     * @param locationId
     * @return
     * @throws NonfatalException
     */
    public static void addFromEstimate(int estimateId, int appointmentTypeId,
                                       String suggestedDate, String notes, int locationId, String priority)
            throws NonfatalException {
        Element rootNode = new Element("createAppointmentForEstimate");

        Element appointmentTypeIdNode = new Element("appointmentTypeId");

        if (!TextUtils.isEmpty(suggestedDate) && !"HIGH".equals(priority)) {
            Element suggestedDateTime = new Element("suggestedDate");
            suggestedDateTime.setText(suggestedDate);
            rootNode.addContent(suggestedDateTime);
        }

        if (!TextUtils.isEmpty(priority)) {
            Element priorityElement = new Element("priority");
            priorityElement.setText(priority);
            rootNode.addContent(priorityElement);
        }

        Element noteNode = new Element("note");
        Element locationIdNode = new Element("locationId");

        appointmentTypeIdNode.setText(String.valueOf(appointmentTypeId));
        noteNode.setText(notes);
        locationIdNode.setText(String.valueOf(locationId));

        rootNode.addContent(appointmentTypeIdNode);
        rootNode.addContent(noteNode);
        rootNode.addContent(locationIdNode);

        RestConnector.getInstance().httpPostCheckSuccess(new Document(rootNode),
                "createappointmentforestimate/" + AppDataSingleton.getInstance().getEstimate().getId());
    }
}