package com.skeds.android.phone.business.Utilities.REST.Objects;

import android.text.TextUtils;

import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Agreement;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Generic;
import com.skeds.android.phone.business.Utilities.NonfatalException;
import com.skeds.android.phone.business.Utilities.REST.RestConnector;

import org.jdom2.Document;
import org.jdom2.Element;

import java.util.List;

public class RESTAgreement {

    /**
     * Retrive agreement by id
     *
     * @param agreementId
     * @throws NonfatalException on error
     */
    public static void query(int agreementId) throws NonfatalException {
        if (agreementId == 0)
            return;
        Document document = RestConnector.getInstance().httpGet(
                "getagreement/" + agreementId);
        Agreement agreement = parseAgreement(document);

        AppDataSingleton.getInstance().setServiceAgreement(agreement);
    }

    private static Agreement parseAgreement(Document document) {
        Agreement agreement = new Agreement();

        Element agreementNode = document.getRootElement().getChild("agreement");
        // FIXME: if( agreementNode==null ) return;

        if (agreementNode.getAttribute("id") != null)
            agreement.setId(
                    Integer.parseInt(agreementNode.getAttributeValue("id")));

        if (agreementNode.getChild("status") != null)
            agreement.setStatus(
                    agreementNode.getChild("status").getText());

        if (agreementNode.getChild("description") != null)
            agreement.setDescription(
                    agreementNode.getChild("description").getText());

        if (agreementNode.getChild("paymentType") != null)
            agreement.setPaymentType(
                    agreementNode.getChild("paymentType").getText());

        if (agreementNode.getChild("contractNumber") != null)
            agreement.setContractNumber(
                    agreementNode.getChild("contractNumber").getText());

        if (agreementNode.getChild("salesPerson") != null)
            agreement.setSalesPerson(
                    agreementNode.getChild("salesPerson").getText());

        if (agreementNode.getChild("numberOfSystems") != null)
            agreement.setNumberOfSystems(
                    Integer.parseInt(agreementNode.getChild("numberOfSystems")
                            .getText()));

        if (agreementNode.getChild("servicePlan") != null) {
            agreement.setServicePlanId(
                    Integer.parseInt(agreementNode.getChild("servicePlan")
                            .getAttributeValue("id")));
            agreement.setServicePlanName(
                    agreementNode.getChild("servicePlan").getText());
        }

        if (agreementNode.getChild("startDate") != null)
            agreement.setStartDate(
                    agreementNode.getChild("startDate").getText());

        if (agreementNode.getChild("endDate") != null)
            agreement.setEndDate(
                    agreementNode.getChild("endDate").getText());

        if (agreementNode.getChildren("location") != null) {
            List<Element> locationNodes = agreementNode.getChildren("location");
            if (!locationNodes.isEmpty()) {
                for (int i = 0; i < locationNodes.size(); i++) {
                    agreement.locationAndEquipment
                            .add(new Generic());

                    int objectIndex = (agreement.locationAndEquipment
                            .size() - 1);

                    Element currentLocationNode = (Element) locationNodes
                            .get(i);

                    if (currentLocationNode.getAttributeValue("id") != null)
                        agreement.locationAndEquipment.get(
                                objectIndex).setId(
                                Integer.parseInt(currentLocationNode
                                        .getAttributeValue("id")));

                    if (!TextUtils.isEmpty(currentLocationNode.getText())) {
                        agreement.locationAndEquipment.get(
                                objectIndex).setName(
                                currentLocationNode.getText());

                        agreement.locationAndEquipment.get(
                                objectIndex).setType("location");
                    }

                    List<Element> equipmentNodes = currentLocationNode
                            .getChildren("equipment");
                    if (!equipmentNodes.isEmpty()) {
                        for (int x = 0; x < equipmentNodes.size(); x++) {
                            agreement.locationAndEquipment
                                    .add(new Generic());

                            Element currentEquipmentNode = (Element) equipmentNodes
                                    .get(x);
                            objectIndex = (agreement.locationAndEquipment
                                    .size() - 1);
                            if (currentEquipmentNode.getAttributeValue("id") != null)
                                agreement.locationAndEquipment
                                        .get(objectIndex)
                                        .setId(Integer.parseInt(currentEquipmentNode
                                                .getAttributeValue("id")));

                            if (currentEquipmentNode.getAttributeValue("name") != null) {
                                agreement.locationAndEquipment
                                        .get(objectIndex)
                                        .setName(
                                                currentEquipmentNode
                                                        .getAttributeValue("name"));

                                agreement.locationAndEquipment
                                        .get(objectIndex).setType("equipment");
                            }
                        }
                    }
                }
            }
        }

        if (agreementNode.getChildren("equipment") != null) {
            List<Element> equipmentNodes = agreementNode
                    .getChildren("equipment");
            if (!equipmentNodes.isEmpty()) {
                for (int i = 0; i < equipmentNodes.size(); i++) {

                    agreement.equipment.add(new Generic());
                    Element currentEquipmentNode = (Element) equipmentNodes
                            .get(i);

                    if (currentEquipmentNode.getAttributeValue("id") != null) {
                        agreement.equipment.get(i).setId(
                                Integer.parseInt(currentEquipmentNode
                                        .getAttributeValue("id")));
                    }

                    if (!TextUtils.isEmpty(currentEquipmentNode.getText())) {
                        agreement.equipment.get(i).setName(
                                currentEquipmentNode.getText());
                    }

                    // TODO - There's also model number, and serial
                    // number

                    // Don't need to set "type", since it's obviously
                    // equipment
                }
            }
        }
        return agreement;
    }

    /* Creates a new service agreement for a given customer */
    public static void add(int customerId, int servicePlanId, String status,
                           String description, String paymentType, String contractNumber,
                           String salesPerson, int numberOfSystems, String startDate,
                           String endDate, List<Integer> locationId, List<Integer> equipmentId)
            throws NonfatalException {
        Element rootNode = addAgreementRequest(servicePlanId, status, description, paymentType, contractNumber, salesPerson, numberOfSystems, startDate, endDate, locationId, equipmentId);

        RestConnector.getInstance().httpPostCheckSuccess(new Document(rootNode),
                "addagreement/" + customerId);
    }

    private static Element addAgreementRequest(int servicePlanId, String status, String description, String paymentType, String contractNumber, String salesPerson, int numberOfSystems, String startDate, String endDate, List<Integer> locationId, List<Integer> equipmentId) {
        Element rootNode = new Element("addAgreementRequest");

		/* Populate elements */
        Element servicePlanIdNode = new Element("servicePlanId");
        servicePlanIdNode.setText(String.valueOf(servicePlanId));
        rootNode.addContent(servicePlanIdNode);

        Element statusNode = new Element("status");
        statusNode.setText(status);
        rootNode.addContent(statusNode);

        Element descriptionNode = new Element("description");
        descriptionNode.setText(description);
        rootNode.addContent(descriptionNode);

        Element paymentTypeNode = new Element("paymentType");
        paymentTypeNode.setText(paymentType);
        rootNode.addContent(paymentTypeNode);

        Element contractNumberNode = new Element("contractNumber");
        contractNumberNode.setText(contractNumber);
        rootNode.addContent(contractNumberNode);

        Element salesPersonNode = new Element("salesPerson");
        salesPersonNode.setText(salesPerson);
        rootNode.addContent(salesPersonNode);

        Element numberOfSystemsNode = new Element("numberOfSystems");
        numberOfSystemsNode.setText(String.valueOf(numberOfSystems));
        rootNode.addContent(numberOfSystemsNode);

        Element startDateNode = new Element("startDate");
        startDateNode.setText(startDate);
        rootNode.addContent(startDateNode);

        Element endDateNode = new Element("endDate");
        endDateNode.setText(endDate);
        rootNode.addContent(endDateNode);

        // Locations loop
        if (!locationId.isEmpty()) {
            for (int i = 0; i < locationId.size(); i++) {
                Element locationIdNode = new Element("locationId");
                locationIdNode.setText(String.valueOf(locationId.get(i)));
                rootNode.addContent(locationIdNode);
            }
        }

        // equipment loop
        if (!equipmentId.isEmpty()) {
            for (int i = 0; i < equipmentId.size(); i++) {
                Element equipmentIdNode = new Element("equipmentId");
                equipmentIdNode.setText(String.valueOf(equipmentId.get(i)));
                rootNode.addContent(equipmentIdNode);
            }
        }
        return rootNode;
    }

    /* Edits existing service agreement for a given customer */
    public static void update(int agreementId, int servicePlanId,
                              String status, String description, String paymentType,
                              String contractNumber, String salesPerson, int numberOfSystems,
                              String startDate, String endDate, List<Integer> locationIds,
                              List<Integer> equipmentIds) throws NonfatalException {

        Element rootNode = updateAgreementRequest(servicePlanId, status, description, paymentType, contractNumber, salesPerson, numberOfSystems, startDate, endDate, locationIds, equipmentIds);

        RestConnector.getInstance().httpPostCheckSuccess(new Document(rootNode),
                "editagreement/" + agreementId);
    }

    private static Element updateAgreementRequest(int servicePlanId, String status, String description, String paymentType, String contractNumber, String salesPerson, int numberOfSystems, String startDate, String endDate, List<Integer> locationIds, List<Integer> equipmentIds) {
        Element rootNode = new Element("editAgreementRequest");

		/* Populate elements */
        Element servicePlanIdNode = new Element("servicePlanId");
        servicePlanIdNode.setText(String.valueOf(servicePlanId));
        rootNode.addContent(servicePlanIdNode);

        Element statusNode = new Element("status");
        statusNode.setText(status);
        rootNode.addContent(statusNode);

        Element descriptionNode = new Element("description");
        descriptionNode.setText(description);
        rootNode.addContent(descriptionNode);

        Element paymentTypeNode = new Element("paymentType");
        paymentTypeNode.setText(paymentType);
        rootNode.addContent(paymentTypeNode);

        Element contractNumberNode = new Element("contractNumber");
        contractNumberNode.setText(contractNumber);
        rootNode.addContent(contractNumberNode);

        Element salesPersonNode = new Element("salesPerson");
        salesPersonNode.setText(salesPerson);
        rootNode.addContent(salesPersonNode);

        Element numberOfSystemsNode = new Element("numberOfSystems");
        numberOfSystemsNode.setText(String.valueOf(numberOfSystems));
        rootNode.addContent(numberOfSystemsNode);

        Element startDateNode = new Element("startDate");
        startDateNode.setText(startDate);
        rootNode.addContent(startDateNode);

        Element endDateNode = new Element("endDate");
        endDateNode.setText(endDate);
        rootNode.addContent(endDateNode);

        // Locations loop
        if (!locationIds.isEmpty()) {
            for (int i = 0; i < locationIds.size(); i++) {
                Element locationIdNode = new Element("locationId");
                locationIdNode.setText(String.valueOf(locationIds.get(i)));
                rootNode.addContent(locationIdNode);
            }
        }

        // equipment loop
        if (!equipmentIds.isEmpty()) {
            for (int i = 0; i < equipmentIds.size(); i++) {
                Element equipmentIdNode = new Element("equipmentId");
                equipmentIdNode.setText(String.valueOf(equipmentIds.get(i)));
                rootNode.addContent(equipmentIdNode);
            }
        }
        return rootNode;
    }

    /**
     * Attaches an agreement to an appointment
     *
     * @param appointmentId
     * @param agreementId
     * @throws NonfatalException
     */
    public static void attachToAppointment(int appointmentId, int agreementId)
            throws NonfatalException {
        Element rootNode = attachToAppointmentRequest(appointmentId, agreementId);

        RestConnector.getInstance().httpPostCheckSuccess(new Document(rootNode),
                "addagreementtoappointment/" + agreementId);
    }

    private static Element attachToAppointmentRequest(int appointmentId, int agreementId) {
        Element rootNode = new Element("addAgreementToAppointment");

        Element appointmentIdNode = new Element("appointmentId");
        appointmentIdNode.setText(String.valueOf(appointmentId));
        rootNode.addContent(appointmentIdNode);

        Element agreementIdNode = new Element("agreementId");
        agreementIdNode.setText(String.valueOf(agreementId));
        rootNode.addContent(agreementIdNode);
        return rootNode;
    }
}