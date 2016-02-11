package com.skeds.android.phone.business.Utilities.REST.Objects;

import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Customer;
import com.skeds.android.phone.business.Utilities.General.UserUtilitiesSingleton;
import com.skeds.android.phone.business.Utilities.NonfatalException;
import com.skeds.android.phone.business.Utilities.REST.RestConnector;

import org.jdom2.Document;
import org.jdom2.Element;

import java.util.List;

public class RESTLargeCustomerList {

    /* Large Customer search query */
    public static void query(String searchString) throws NonfatalException {
        int userId = UserUtilitiesSingleton.getInstance().user.getId();
        Element rootNode = new Element("customerSearch");

        Element startsWithNode = new Element("query");
        startsWithNode.addContent(String.valueOf(searchString));
        rootNode.addContent(startsWithNode);

        Document document = RestConnector.getInstance().httpPost(
                new Document(rootNode), "getcustomersearch/" + userId);

        AppDataSingleton.getInstance().getCustomerList().clear();
        rootNode = document.getRootElement();
        List<Element> customersList = rootNode.getChildren("c");
        if (customersList.isEmpty())
            throw new NonfatalException("XML", "No customers in response");

        for (int customerIterator = 0; customerIterator < customersList.size(); customerIterator++) {
            AppDataSingleton.getInstance().getCustomerList().add(new Customer());

            Element customerNode = (Element) customersList
                    .get(customerIterator);
            if (customerNode.getAttributeValue("id") != null)
                AppDataSingleton.getInstance().getCustomerList()
                        .get(customerIterator)
                        .setId(Integer.parseInt(customerNode
                                .getAttributeValue("id")));
            if (customerNode.getAttributeValue("n") != null)
                AppDataSingleton.getInstance().getCustomerList().get(customerIterator)
                        .setFirstName(customerNode.getAttributeValue("n"));
        }
    }
}