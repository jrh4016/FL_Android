package com.skeds.android.phone.business.Utilities.REST.Objects;

import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Manufacturer;
import com.skeds.android.phone.business.Utilities.NonfatalException;
import com.skeds.android.phone.business.Utilities.REST.RestConnector;

import org.jdom2.Document;
import org.jdom2.Element;

import java.util.List;

public class RESTPricebookGroupCodeList {

    /* Pricebook codes for invoices, Step 2 */
    public static void query(int ownerId, int groupCodeId)
            throws NonfatalException {
        Element rootNode = new Element("getPriceBookManufacturersByGroup");

        Element groupIdNode = new Element("groupCodeId");
        if (groupCodeId != -1)
            groupIdNode.setText(String.valueOf(groupCodeId));
        else
            groupIdNode.setText("");

        rootNode.addContent(groupIdNode);

        Document document = RestConnector.getInstance().httpPost(
                new Document(rootNode),
                "getpricebookmanufacturersbygroup/" + ownerId);
        AppDataSingleton.getInstance().getPricebookManufacturerList().clear();

        Element el = document.getRootElement().getChild("manufacturers");
        if (el == null)
            return;
        List<Element> manufacturerList = el.getChildren("manufacturer");

        for (int typeIterator = 0; typeIterator < manufacturerList.size(); typeIterator++) {

            AppDataSingleton.getInstance().getPricebookManufacturerList().add(new Manufacturer());

            Element manufacturerNode = (Element) manufacturerList
                    .get(typeIterator);

            if (manufacturerNode.getAttributeValue("id") != null)
                AppDataSingleton.getInstance().getPricebookManufacturerList()
                        .get(typeIterator)
                        .setId(Integer.parseInt(manufacturerNode
                                .getAttributeValue("id")));

            if (manufacturerNode.getAttributeValue("name") != null)
                AppDataSingleton.getInstance().getPricebookManufacturerList().get(typeIterator)
                        .setName(manufacturerNode.getAttributeValue("name"));
        }
    }

}