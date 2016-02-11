package com.skeds.android.phone.business.data.xml;

import com.skeds.android.phone.business.data.xml.api.BaseStreamHandler;
import com.skeds.android.phone.business.data.xml.api.ParserCallback;
import com.skeds.android.phone.business.data.xml.api.XmlParseUtils;
import com.skeds.android.phone.business.model.CustomerEquipment;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class CustomerEquipmentHandler extends BaseStreamHandler<CustomerEquipment> {

    public CustomerEquipmentHandler(ParserCallback<CustomerEquipment> callback) {
        super(callback);
    }

    @Override
    protected void handleChildren(XmlPullParser parser) throws IOException, XmlPullParserException {
        final String name = parser.getName();
        // Starts by looking for the customer tag
        if (name.equals(CustomerEquipment.KEY_CUSTOMER)) {
            final CustomerEquipmentXml customerEquipment = new CustomerEquipmentXml();
            customerEquipment.fromXml(parser);
            dispatchChildObject(customerEquipment);
        } else {
            XmlParseUtils.skip(parser);
        }
    }
}
