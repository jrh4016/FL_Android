package com.skeds.android.phone.business.Utilities.REST.Objects;

import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;

import com.skeds.android.phone.business.Utilities.General.ClassObjects.LeadSource;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.ListItem;
import com.skeds.android.phone.business.Utilities.General.UserUtilitiesSingleton;
import com.skeds.android.phone.business.Utilities.NonfatalException;
import com.skeds.android.phone.business.Utilities.REST.RestConnector;

import org.jdom2.Document;
import org.jdom2.Element;

import java.util.ArrayList;
import java.util.List;

public class RESTLeadSource {

    public static void getByOwnerId(int ownerId)
            throws NonfatalException {


        Document document = RestConnector.getInstance().httpGet(
                "getleadsources/"+ ownerId);


        Element root = document.getRootElement();
        //root = root.getChild("getLeadSourcesResponse");


        List<LeadSource> leadSourceListItem = new ArrayList<LeadSource>();
        Element leadSources = root.getChild("leadSources");


        for (Element el:leadSources.getChildren("leadSource")){
            LeadSource leadSourceElement = new LeadSource();
            leadSourceElement.setId(el.getAttribute("id").getValue());
            leadSourceElement.setCampaignDateStr(el.getAttribute("campaignDateStr").getValue());
            leadSourceElement.setName(el.getAttribute("name").getValue());
            leadSourceElement.setDescription(el.getAttribute("description").getValue());
            leadSourceElement.setType(el.getAttribute("type").getValue());
            leadSourceElement.setAmountSpent(el.getAttribute("amountSpent").getValue());
            leadSourceElement.setEndDateStr(el.getAttribute("endDateStr").getValue());
            leadSourceListItem.add(leadSourceElement);
        }

        AppDataSingleton.getInstance().setSourcesListItem(leadSourceListItem);
    }
}