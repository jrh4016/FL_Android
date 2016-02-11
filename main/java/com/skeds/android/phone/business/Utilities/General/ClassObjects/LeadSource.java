package com.skeds.android.phone.business.Utilities.General.ClassObjects;

import java.io.Serializable;

/**
 * Created by user_sca on 08.10.2014.
 */
public class LeadSource implements Serializable{

    String id;

    String campaignDateStr ="";

    String name="";

    String description="";

    String type="";

    String amountSpent="";

    String endDateStr="";

    public String getCampaignDateStr() {
        return campaignDateStr;
    }

    public void setCampaignDateStr(String campaignDateStr) {
        this.campaignDateStr = campaignDateStr;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAmountSpent() {
        return amountSpent;
    }

    public void setAmountSpent(String amountSpent) {
        this.amountSpent = amountSpent;
    }

    public String getEndDateStr() {
        return endDateStr;
    }

    public void setEndDateStr(String endDateStr) {
        this.endDateStr = endDateStr;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
