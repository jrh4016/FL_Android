package com.skeds.android.phone.business.Utilities.REST.Objects;

import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.GroupCode;
import com.skeds.android.phone.business.Utilities.NonfatalException;
import com.skeds.android.phone.business.Utilities.REST.RestConnector;

import org.jdom2.Document;
import org.jdom2.Element;

import java.util.List;

public class RESTPricebookCodeList {

    /* Pricebook codes for invoices, Step 1 */
    public static void query(int ownerId) throws NonfatalException {
        Document document = RestConnector.getInstance().httpGet(
                "getpricebookcodes/" + ownerId);
        AppDataSingleton.getInstance().getPricebookGroupCodeList().clear();
        Element el = document.getRootElement().getChild("products");
        if (el == null)
            return;
        List<Element> productsList = el.getChildren("groupCode");

        for (Element groupCodeNode : productsList) {
            GroupCode grcode = new GroupCode();
            AppDataSingleton.getInstance().getPricebookGroupCodeList().add(grcode);

            if (groupCodeNode.getAttributeValue("id") != null)
                grcode.setId(Integer.parseInt(groupCodeNode
                        .getAttributeValue("id")));

            if (groupCodeNode.getAttributeValue("name") != null)
                grcode.setName(groupCodeNode.getAttributeValue("name"));
        }
    }
}