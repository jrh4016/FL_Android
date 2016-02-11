package com.skeds.android.phone.business.data.xml;

import com.skeds.android.phone.business.data.xml.api.XmlParcelable;
import com.skeds.android.phone.business.data.xml.api.XmlParseUtils;
import com.skeds.android.phone.business.model.ServiceCall;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class ServiceCallXml extends ServiceCall implements XmlParcelable {

    @Override
    public ServiceCall fromXml(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, KEY_SERVICE_CALL);
        final String tag = parser.getName();
        final String id = parser.getAttributeValue(null, KEY_SERVICE_CALL_ID);
        if (tag.equals(KEY_SERVICE_CALL)) {
            setId(Long.parseLong(id));
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                final String name = parser.getName();
                if (name.equals(KEY_SERVICE_CALL_DATE)) {
                    setDate(XmlParseUtils.readText(parser));
                } else if (name.equals(KEY_SERVICE_CALL_DESCRIPTION)) {
                    setDescription(XmlParseUtils.readText(parser));
                } else if (name.equals(KEY_SERVICE_CALL_EQUIPMENT_CONDITION)) {
                    setCondition(XmlParseUtils.readText(parser));
                } else if (name.equals(KEY_SERVICE_CALL_TECHNICIAN)) {
                    readTechnician(parser);
                }
            }
        }
        return this;
    }

    // Processes link tags in the feed.
    private void readTechnician(final XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, KEY_SERVICE_CALL_TECHNICIAN);
        final String tag = parser.getName();
        final String technicianId = parser.getAttributeValue(null, KEY_SERVICE_CALL_TECHNICIAN_ID);
        if (tag.equals(KEY_SERVICE_CALL_TECHNICIAN)) {
            setTechnicianName(XmlParseUtils.readText(parser));
            setTechnicianId(Integer.parseInt(technicianId));
        }
        parser.require(XmlPullParser.END_TAG, null, KEY_SERVICE_CALL_TECHNICIAN);
    }
}
