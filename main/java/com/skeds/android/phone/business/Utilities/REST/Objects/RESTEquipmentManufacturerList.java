package com.skeds.android.phone.business.Utilities.REST.Objects;

import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Manufacturer;
import com.skeds.android.phone.business.Utilities.NonfatalException;
import com.skeds.android.phone.business.Utilities.REST.RestConnector;

import org.jdom2.Document;
import org.jdom2.Element;

import java.util.List;

public class RESTEquipmentManufacturerList {

    /* Manufacturers for Equipment */
    public static void query(int ownerId) throws NonfatalException {
        Document document = RestConnector.getInstance().httpGet(
                "getequipmentmanufacturers/" + ownerId);
        AppDataSingleton.getInstance().getEquipmentManufacturerList().clear();
        AppDataSingleton.getInstance().getEquipmentManufacturerList().add(new Manufacturer());
        Element manufacturersNode = document.getRootElement().getChild(
                "equipmentManufacturers");

        if (manufacturersNode == null)
            return;

        List<Element> manufacturerList = manufacturersNode
                .getChildren("equipmentManufacturer");

        for (int i = 0; i < manufacturerList.size(); i++) {

            AppDataSingleton.getInstance().getEquipmentManufacturerList().add(new Manufacturer());

            Element manufacturerNode = (Element) manufacturerList.get(i);

            if (manufacturerNode != null) {
                if (manufacturerNode.getAttributeValue("id") != null)
                    AppDataSingleton.getInstance().getEquipmentManufacturerList()
                            .get(i)
                            .setId(Integer.parseInt(manufacturerNode
                                    .getAttributeValue("id")));

                if (manufacturerNode.getChild("name") != null)
                    AppDataSingleton.getInstance().getEquipmentManufacturerList()
                            .get(i)
                            .setName(
                                    manufacturerNode.getChild("name").getText());

                if (manufacturerNode.getChild("description") != null)
                    AppDataSingleton.getInstance().getEquipmentManufacturerList()
                            .get(i)
                            .setDescription(
                                    manufacturerNode.getChild("description")
                                            .getText());
            }

        }
    }
}