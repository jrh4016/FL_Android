package com.skeds.android.phone.business.Utilities.REST.Objects;

import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Location;
import com.skeds.android.phone.business.Utilities.NonfatalException;
import com.skeds.android.phone.business.Utilities.REST.RestConnector;

import org.jdom2.Document;
import org.jdom2.Element;

import java.util.List;

public class RESTLargeLocationsList {

    /* Large Customer search query */
    public static void query(String searchString) throws NonfatalException {
        int customerId = AppDataSingleton.getInstance().getCustomer().getId();
        Element rootNode = new Element("customerSearch");

        Element startsWithNode = new Element("query");
        startsWithNode.addContent(String.valueOf(searchString));
        rootNode.addContent(startsWithNode);

        Document document = RestConnector.getInstance().httpPost(
                new Document(rootNode), "getlocationsforcustomer/" + customerId);
        AppDataSingleton.getInstance().getCustomer().locationList.clear();

        rootNode = document.getRootElement();
        List<Element> locationsList = rootNode.getChildren("l");

        for (Element locationNode : locationsList) {
            Location loc = new Location();
            if (locationNode.getAttributeValue("id") != null) {
                loc.setId(Integer.parseInt(locationNode.getAttributeValue("id")));
            }
            if (locationNode.getAttributeValue("n") != null) {
                loc.setAddress1(locationNode.getAttributeValue("n"));
            }
            AppDataSingleton.getInstance().getCustomer().locationList.add(loc);
        }

    }
}