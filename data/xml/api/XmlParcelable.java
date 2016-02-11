package com.skeds.android.phone.business.data.xml.api;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public interface XmlParcelable {
    Object fromXml(XmlPullParser parser) throws IOException, XmlPullParserException;
}
