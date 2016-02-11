package com.skeds.android.phone.business.Utilities.REST.Objects;

import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Agreement;
import com.skeds.android.phone.business.Utilities.NonfatalException;
import com.skeds.android.phone.business.Utilities.REST.RestConnector;

import org.jdom2.Document;
import org.jdom2.Element;

public class RESTAgreementList {

    /* Customer agreement search/list */
    public static void query(int customerId) throws NonfatalException {
        final Document document = RestConnector.getInstance().httpGet(
                "getagreementsearch/" + customerId);

        AppDataSingleton.getInstance().getServiceAgreementList().clear();
        Element agreementsNode = document.getRootElement().getChild(
                "agreements");
        if (agreementsNode == null)
            return;
        for (Element agreementNode : agreementsNode.getChildren("agreement")) {
            Agreement agrm = new Agreement();
            AppDataSingleton.getInstance().getServiceAgreementList().add(agrm);
            if (agreementNode.getAttribute("id") != null)
                agrm.setId(Integer.parseInt(agreementNode
                        .getAttributeValue("id")));

            if (agreementNode.getAttribute("servicePlanName") != null)
                agrm.setServicePlanName(agreementNode
                        .getAttributeValue("servicePlanName"));

            if (agreementNode.getAttribute("startDate") != null)
                agrm.setStartDate(agreementNode.getAttributeValue("startDate"));

            if (agreementNode.getAttribute("description") != null)
                agrm.setDescription(agreementNode.getAttributeValue("description"));
        }
    }
}