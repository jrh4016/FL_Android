package com.skeds.android.phone.business.Utilities.REST.Objects;

import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Customer;
import com.skeds.android.phone.business.Utilities.General.UserUtilitiesSingleton;
import com.skeds.android.phone.business.Utilities.NonfatalException;
import com.skeds.android.phone.business.Utilities.REST.RestConnector;

import org.jdom2.Document;
import org.jdom2.Element;

import java.util.List;

/**
 * @author Ryan Bailey
 */
public class RESTCustomerList {

    /* List of all customers */
    public static void query() throws NonfatalException {
        int userId = UserUtilitiesSingleton.getInstance().user.getId();
        Document document = RestConnector.getInstance().httpGet(
                "getcustomersearch/" + userId);
        AppDataSingleton.getInstance().getCustomerList().clear();

        List<Element> customersList = document.getRootElement()
                .getChildren("c");
        if (customersList.isEmpty())
            throw new NonfatalException("XML",
                    "No customers");

        for (Element customerNode : customersList) {
            Customer cust = new Customer();
            AppDataSingleton.getInstance().getCustomerList().add(cust);
            if (customerNode.getAttribute("id") != null)
                cust.setId(Integer.parseInt(customerNode
                        .getAttributeValue("id")));
            if (customerNode.getAttribute("n") != null)
                cust.setFirstName(customerNode.getAttributeValue("n"));
        }
    }
}