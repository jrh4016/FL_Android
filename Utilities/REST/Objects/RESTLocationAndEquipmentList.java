package com.skeds.android.phone.business.Utilities.REST.Objects;

import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Generic;
import com.skeds.android.phone.business.Utilities.NonfatalException;
import com.skeds.android.phone.business.Utilities.REST.RestConnector;

import org.jdom2.Document;
import org.jdom2.Element;

import java.util.List;

public class RESTLocationAndEquipmentList {

    /* Single customer estimate */
    public static void query(int customerId) throws NonfatalException {
        Document document = RestConnector.getInstance().httpGet(
                "getlocationsandequipment/" + customerId);
        AppDataSingleton.getInstance().getSingleAgreementEquipmentList().clear();
        AppDataSingleton.getInstance().getSingleAgreementLocationAndEquipmentList().clear();

        Element rootNode = document.getRootElement();
        if (rootNode != null) {

            if (rootNode.getChild("locations") != null) {

                if (!rootNode.getChild("locations").getChildren("location")
                        .isEmpty()) {

                    List<Element> locationNodes = rootNode
                            .getChild("locations").getChildren("location");

                    for (int i = 0; i < locationNodes.size(); i++) {
                        AppDataSingleton.getInstance().getSingleAgreementLocationAndEquipmentList()
                                .add(new Generic());

                        int objectIndex = (AppDataSingleton.getInstance()
                                .getSingleAgreementLocationAndEquipmentList()
                                .size() - 1);

                        Element currentLocationNode = (Element) locationNodes
                                .get(i);

                        if (currentLocationNode.getAttributeValue("id") != null)
                            AppDataSingleton.getInstance().getSingleAgreementLocationAndEquipmentList()
                                    .get(objectIndex)
                                    .setId(Integer.parseInt(currentLocationNode
                                            .getAttributeValue("id")));

                        if (currentLocationNode.getAttributeValue("name") != null) {
                            AppDataSingleton.getInstance().getSingleAgreementLocationAndEquipmentList()
                                    .get(objectIndex)
                                    .setName(
                                            currentLocationNode
                                                    .getAttributeValue("name"));

                            AppDataSingleton.getInstance().getSingleAgreementLocationAndEquipmentList()
                                    .get(objectIndex).setType("location");
                        }

                        List<Element> equipmentNodes = currentLocationNode
                                .getChildren("equipment");
                        if (!equipmentNodes.isEmpty()) {
                            for (int x = 0; x < equipmentNodes.size(); x++) {
                                AppDataSingleton.getInstance().getSingleAgreementLocationAndEquipmentList()
                                        .add(new Generic());

                                Element currentEquipmentNode = (Element) equipmentNodes
                                        .get(x);
                                objectIndex = (AppDataSingleton.getInstance()
                                        .getSingleAgreementLocationAndEquipmentList()
                                        .size() - 1);

                                if (currentEquipmentNode
                                        .getAttributeValue("id") != null)
                                    AppDataSingleton.getInstance().getSingleAgreementLocationAndEquipmentList()
                                            .get(objectIndex)
                                            .setId(Integer
                                                    .parseInt(currentEquipmentNode
                                                            .getAttributeValue("id")));

                                if (currentEquipmentNode
                                        .getAttributeValue("name") != null) {
                                    AppDataSingleton.getInstance().getSingleAgreementLocationAndEquipmentList()
                                            .get(objectIndex)
                                            .setName(
                                                    currentEquipmentNode
                                                            .getAttributeValue("name"));

                                    AppDataSingleton.getInstance().getSingleAgreementLocationAndEquipmentList()
                                            .get(objectIndex)
                                            .setType("equipment");
                                }
                            }
                        }
                    }
                }
            }
        }

        if (rootNode.getChildren("equipment") != null) {
            List<Element> equipmentNodes = rootNode.getChildren("equipment");
            if (!equipmentNodes.isEmpty()) {
                for (int i = 0; i < equipmentNodes.size(); i++) {

                    AppDataSingleton.getInstance().getSingleAgreementEquipmentList()
                            .add(new Generic());
                    Element currentEquipmentNode = (Element) equipmentNodes
                            .get(i);

                    if (currentEquipmentNode.getAttributeValue("id") != null) {
                        AppDataSingleton.getInstance().getSingleAgreementEquipmentList()
                                .get(i)
                                .setId(Integer.parseInt(currentEquipmentNode
                                        .getAttributeValue("id")));
                    }

                    if (currentEquipmentNode.getAttributeValue("name") != null) {
                        AppDataSingleton.getInstance().getSingleAgreementEquipmentList()
                                .get(i)
                                .setName(
                                        currentEquipmentNode
                                                .getAttributeValue("name"));
                    }

                    // TODO - There's also model number, and serial
                    // number

                    // Don't need to set "type", since it's
                    // obviously
                    // equipment
                }
            }
        }
    }
}