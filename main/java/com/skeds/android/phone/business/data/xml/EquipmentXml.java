package com.skeds.android.phone.business.data.xml;

import com.skeds.android.phone.business.data.xml.api.XmlParcelable;
import com.skeds.android.phone.business.data.xml.api.XmlParseUtils;
import com.skeds.android.phone.business.model.Equipment;
import com.skeds.android.phone.business.model.ServiceCall;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EquipmentXml extends Equipment implements XmlParcelable {

    @Override
    public Equipment fromXml(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, KEY_EQUIPMENT);
        final String tag = parser.getName();
        final String id = parser.getAttributeValue(null, KEY_EQUIPMENT_ID);
        if (tag.equals(KEY_EQUIPMENT)) {
            setId(Long.parseLong(id));
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                String name = parser.getName();
                if (name.equals(KEY_EQUIPMENT_NAME)) {
                    setName(XmlParseUtils.readText(parser));
                } else if (name.equals(KEY_EQUIPMENT_MODEL_NUMBER)) {
                    setModelNumber(XmlParseUtils.readText(parser));
                } else if (name.equals(KEY_EQUIPMENT_SERIAL_NUMBER)) {
                    setSerialNumber(XmlParseUtils.readText(parser));
                } else if (name.equals(KEY_EQUIPMENT_FILTER)) {
                    setFilter(XmlParseUtils.readText(parser));
                } else if (name.equals(KEY_EQUIPMENT_INSTALLATION_DATE)) {
                    setInstallationDate(XmlParseUtils.readText(parser));
                } else if (name.equals(KEY_EQUIPMENT_NEXT_SERVICE_CALL_DATE)) {
                    setNextServiceCallDate(XmlParseUtils.readText(parser));
                } else if (name.equals(KEY_EQUIPMENT_WARRANTY_EXP_DATE)) {
                    setWarrantyExpirationDate(XmlParseUtils.readText(parser));
                } else if (name.equals(KEY_EQUIPMENT_LABOR_WARRANTY_DATE)) {
                    setLaborWarrantyExpirationDate(XmlParseUtils.readText(parser));
                } else if (name.equals(KEY_EQUIPMENT_LOCATION_ID)) {
                    setLocationId(Long.parseLong(XmlParseUtils.readText(parser)));
                } else if (name.equals(KEY_EQUIPMENT_LOCATION_NAME)) {
                    setLocationName(XmlParseUtils.readText(parser));
                } else if (name.equals(KEY_EQUIPMENT_LOCATION_ADDRESS)) {
                    setLocationAddress(XmlParseUtils.readText(parser));
                } else if (name.equals(KEY_EQUIPMENT_CUSTOM_CODE)) {
                    setCustomCode(XmlParseUtils.readText(parser));
                } else if (name.equals(KEY_MANUFACTURER_ID)) {
                    setManufacturerId(Long.parseLong(XmlParseUtils.readText(parser)));
                } else if (name.equals(KEY_MANUFACTURER_NAME)) {
                    setManufacturerName(XmlParseUtils.readText(parser));
                } else if (name.equals(KEY_EQUIPMENT_WARRANTY_CONTRACT_HOLDER)) {
                    setWarrantyContractHolder(XmlParseUtils.readText(parser));
                } else if (name.equals(KEY_EQUIPMENT_WARRANTY_CONTRACT_NUMBER)) {
                    setWarrantyContractNumber(XmlParseUtils.readText(parser));
                } else if (name.equals(KEY_EQUIPMENT_NEXT_SERVICE_APPOINTMENT_DATE)) {
                    setNextServiceAppointmentDate(XmlParseUtils.readText(parser));
                } else if (name.equals(KEY_EQUIPMENT_APPOINTMENT_TYPE_ID)) {
                    setAppointmentTypeId(Long.parseLong(XmlParseUtils.readText(parser)));
                } else if (name.equals(KEY_EQUIPMENT_APPOINTMENT_TYPE_NAME)) {
                    setAppointmentTypeName(XmlParseUtils.readText(parser));
                } else if (name.equals(KEY_EQUIPMENT_CUSTOM_FIELD_1)) {
                    setCustomInfo1(XmlParseUtils.readText(parser));
                } else if (name.equals(KEY_EQUIPMENT_CUSTOM_FIELD_2)) {
                    setCustomInfo2(XmlParseUtils.readText(parser));
                } else if (name.equals(KEY_EQUIPMENT_CUSTOM_FIELD_3)) {
                    setCustomInfo3(XmlParseUtils.readText(parser));
                } else if (name.equals(KEY_EQUIPMENT_SERVICE_CALL_LIST)) {
                    setServiceCallList(readServiceCallList(parser));
                } else {
                    XmlParseUtils.skip(parser);
                }
            }
        }
        return this;
    }

    private List<ServiceCall> readServiceCallList(final XmlPullParser parser) throws IOException, XmlPullParserException {
        final List<ServiceCall> list = new ArrayList<ServiceCall>();
        parser.require(XmlPullParser.START_TAG, null, KEY_EQUIPMENT_SERVICE_CALL_LIST);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals(ServiceCall.KEY_SERVICE_CALL)) {
                final ServiceCallXml serviceCall = new ServiceCallXml();
                list.add(serviceCall.fromXml(parser));
            } else {
                XmlParseUtils.skip(parser);
            }
        }

        return list;
    }
}
