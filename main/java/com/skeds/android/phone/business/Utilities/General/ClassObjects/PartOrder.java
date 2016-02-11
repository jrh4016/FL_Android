package com.skeds.android.phone.business.Utilities.General.ClassObjects;

import android.text.TextUtils;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class PartOrder implements Serializable {

    private int id = 0;
    private String name = "";
    private String description = "";
    private String status = "";
    private String deliveryDateTime = "";
    private String qty = "0";
    private String price = "0.0";
    private int priceCents = -1;
    private String partNumber = "";
    private String customField1 = "";
    private String customField2 = "";
    private String customField3 = "";
    private String trackingNumber = "";
    private int manufacturerId;

    private final static Map<String, String> statusMap = new HashMap<String, String>(
            16);

    static {
        statusMap.put("TO_BE_ORDERED", "To Be Ordered");
        statusMap.put("ORDERED", "Ordered");
        statusMap.put("ARRIVED", "Arrived");
        statusMap.put("JOB_SCHEDULED", "Job Scheduled");
        statusMap.put("JOB_COMPLETED", "Job Completed");
        statusMap.put("NOT_USED", "Not Used");
        statusMap.put("DAMAGED", "Damaged");
        statusMap.put("DECLINED", "Declined");
        statusMap.put("RETURNED", "Returned");
    }

    public PartOrder() {
    }

    public int getId() {
        return id;
    }

    public int getManufacturerId() {
        return manufacturerId;
    }

    public void setId(int value) {
        this.id = value;
    }

    public void setManufacturerId(int value) {
        this.manufacturerId = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String value) {
        this.description = value;
    }

    public String getStatus() {
        return status;
    }

    public String getUserStatus() {
        String s = statusMap.get(status);
        return s == null ? status : s;
    }

    public void setStatus(String value) {
        this.status = value;
    }

    public String getDeliveryDateTime() {
        return deliveryDateTime;
    }

    public String getDeliveryDateOnly() {
        if (TextUtils.isEmpty(deliveryDateTime))
            return deliveryDateTime;

        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yy HH:mm a", Locale.ENGLISH);
        SimpleDateFormat fmtOut = new SimpleDateFormat("MM/dd/yy", Locale.ENGLISH);
        try {
            Date date = df.parse(deliveryDateTime);
            return fmtOut.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return deliveryDateTime;
    }

    public void setDeliveryDateTime(String value) {
        this.deliveryDateTime = value;
    }

    public String getQuantity() {
        return qty;
    }

    public void setQuantity(String qty) {
        this.qty = qty;
    }

    public String getPrice() {
        return price;
    }

    /**
     * @return price in cents
     */
    public int getPriceCents() {
        if (priceCents < 0) {
            priceCents = 0;

            for (int i = 0; i < price.length(); i++) {
                char c = price.charAt(i);
                if (c == '.')
                    continue;
                if (c < '0' || c > '9')
                    break;
                priceCents *= 10;
                priceCents += c - '0';
            }
        }
        return priceCents;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String value) {
        this.partNumber = value;
    }

    public String getCustomField1() {
        return customField1;
    }

    public void setCustomField1(String value) {
        this.customField1 = value;
    }

    public String getCustomField2() {
        return customField2;
    }

    public void setCustomField2(String value) {
        this.customField2 = value;
    }

    public String getCustomField3() {
        return customField3;
    }

    public void setCustomField3(String value) {
        this.customField3 = value;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String value) {
        this.trackingNumber = value;
    }

}