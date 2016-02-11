package com.skeds.android.phone.business.data.xml;

import com.skeds.android.phone.business.data.xml.api.XmlParcelable;
import com.skeds.android.phone.business.data.xml.api.XmlParseUtils;
import com.skeds.android.phone.business.model.CustomerEquipment;
import com.skeds.android.phone.business.model.Equipment;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CustomerEquipmentXml extends CustomerEquipment implements XmlParcelable {

    @Override
    public CustomerEquipment fromXml(final XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, KEY_CUSTOMER);
        final String tag = parser.getName();
        if (tag.equals(KEY_CUSTOMER)) {
            setId(Long.parseLong(parser.getAttributeValue(null, KEY_ID)));
            setAllowEquipmentAdd(Boolean.valueOf(parser.getAttributeValue(null, KEY_ALLOW_EQUIPMENT_ADD)));
            setAllowEquipmentEdit(Boolean.valueOf(parser.getAttributeValue(null, KEY_ALLOW_EQUIPMENT_EDIT)));
        }
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            final String name = parser.getName();
            if (name.equals(KEY_EQUIPMENT_LIST)) {
                setEquipmentList(readEquipmentList(parser));
            } else {
                XmlParseUtils.skip(parser);
            }
        }
        return this;
    }

    private List<Equipment> readEquipmentList(final XmlPullParser parser) throws IOException, XmlPullParserException {
        final List<Equipment> list = new ArrayList<Equipment>();
        parser.require(XmlPullParser.START_TAG, null, KEY_EQUIPMENT_LIST);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals(Equipment.KEY_EQUIPMENT)) {
                final EquipmentXml equipment = new EquipmentXml();
                list.add(equipment.fromXml(parser));
            } else {
                XmlParseUtils.skip(parser);
            }
        }

        return list;
    }
}
