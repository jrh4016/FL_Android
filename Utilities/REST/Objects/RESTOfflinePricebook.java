package com.skeds.android.phone.business.Utilities.REST.Objects;

import com.skeds.android.phone.business.Utilities.General.UserUtilitiesSingleton;
import com.skeds.android.phone.business.Utilities.NonfatalException;
import com.skeds.android.phone.business.Utilities.REST.RestConnector;

import org.jdom2.Document;
import org.jdom2.Element;

/**
 * This class is used to GET pricebook xml
 *
 * @author Den Oleshkevich
 */

public class RESTOfflinePricebook {

    public static Document queryPricebook() throws NonfatalException {

        int ownerId = UserUtilitiesSingleton.getInstance().user.getOwnerId();
        Element rootNode = new Element("getUpdatedPriceBook");

        long unixTime = System.currentTimeMillis() / 1000L;

        Element unixTimeNode = new Element("lastUpdatedDate");
        unixTimeNode.setText(Long.toString(0));
        rootNode.addContent(unixTimeNode);

        return RestConnector.getInstance().httpPost(new Document(rootNode),
                "getupdatedpricebook/" + ownerId);
    }

}
