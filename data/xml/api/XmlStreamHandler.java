package com.skeds.android.phone.business.data.xml.api;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStreamReader;

public interface XmlStreamHandler {
    void handleStream(InputStreamReader stream) throws IOException, XmlPullParserException;
}
