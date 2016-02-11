package com.skeds.android.phone.business.Utilities.REST.Objects;

import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.NonfatalException;
import com.skeds.android.phone.business.Utilities.REST.RestConnector;

import org.jdom2.Document;
import org.jdom2.Element;

import java.util.List;

public class RESTEstimateList {

    /* List of all customer estimates */
    public static void query(int customerId, int appointmentId)
            throws NonfatalException {
        Element rootNode = new Element("getEstimateListRequest");
        if (appointmentId != -1) {
            Element appointmentIdNode = new Element("appointmentId");
            appointmentIdNode.setText(String.valueOf(appointmentId));
            rootNode.addContent(appointmentIdNode);
        }
        if (customerId == 0)
            customerId = AppDataSingleton.getInstance().getCustomer().getId();

        Document document = RestConnector.getInstance().httpPost(
                new Document(rootNode), "getestimatesearch/" + customerId);

        Element el = document.getRootElement().getChild("estimates");

        if (el == null) {
            AppDataSingleton.getInstance().customerEstimateId = new int[0];
            AppDataSingleton.getInstance().customerEstimateDate = new String[0];
            AppDataSingleton.getInstance().customerEstimateCost = new double[0];
            return;
        }
        List<Element> estimateList = el.getChildren();
        if (estimateList.isEmpty())
            return;
        AppDataSingleton.getInstance().customerEstimateId = new int[estimateList
                .size()];
        AppDataSingleton.getInstance().customerEstimateDate = new String[estimateList
                .size()];
        AppDataSingleton.getInstance().customerEstimateCost = new double[estimateList
                .size()];
        for (int i = 0; i < estimateList.size(); i++) {
            Element estimateNode = (Element) estimateList.get(i);

            if (estimateNode != null) {
                AppDataSingleton.getInstance().customerEstimateId[i] = Integer
                        .parseInt(estimateNode.getAttributeValue("id"));

                AppDataSingleton.getInstance().customerEstimateDate[i] = estimateNode
                        .getAttributeValue("estimateDate");

                AppDataSingleton.getInstance().customerEstimateCost[i] = Double
                        .parseDouble(estimateNode.getAttributeValue("cost"));

            }
        }
    }
}