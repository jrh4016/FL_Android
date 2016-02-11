package com.skeds.android.phone.business.Utilities.REST.Objects;

import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Generic;
import com.skeds.android.phone.business.Utilities.NonfatalException;
import com.skeds.android.phone.business.Utilities.REST.RestConnector;

import org.jdom2.Document;
import org.jdom2.Element;

import java.util.List;

public class RESTServicePlanList {

    public static void query(int ownerId) throws NonfatalException {
        Document document = RestConnector.getInstance().httpGet(
                "getserviceplans/" + ownerId);
        AppDataSingleton.getInstance().getServicePlanList().clear();
        Element el = document.getRootElement().getChild("servicePlans");
        if (el == null)
            return;
        List<Element> servicePlanNodes = el.getChildren("servicePlan");

        for (int i = 0; i < servicePlanNodes.size(); i++) {

            AppDataSingleton.getInstance().getServicePlanList().add(new Generic());
            Element servicePlanNode = (Element) servicePlanNodes.get(i);

            if (servicePlanNode.getAttribute("id") != null)
                AppDataSingleton.getInstance().getServicePlanList()
                        .get(i)
                        .setId(Integer.parseInt(servicePlanNode
                                .getAttributeValue("id")));
            if (servicePlanNode.getChild("name") != null)
                AppDataSingleton.getInstance().getServicePlanList().get(i)
                        .setName(servicePlanNode.getChild("name").getText());
        }
    }
}