package com.skeds.android.phone.business.data.xml.api;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStreamReader;

public abstract class BaseStreamHandler<T> implements XmlStreamHandler {

    private static final String KEY_STATUS = "status";
    private static final String KEY_MESSAGE = "message";

    private static final String KEY_STATUS_SUCCESS = "success";

    // We don't use namespaces
    protected static final String NS = null;

    final ParserCallback<T> mCallback;

    public BaseStreamHandler(final ParserCallback<T> callback) {

        mCallback = callback;
    }

    protected abstract void handleChildren(final XmlPullParser parser) throws IOException, XmlPullParserException;

    @Override
    public void handleStream(final InputStreamReader stream) throws IOException, XmlPullParserException {
        mCallback.onStart();

        XmlPullParser parser = Xml.newPullParser();
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        parser.setInput(stream);

        // begin root
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            //check for errors
            if (parser.getAttributeCount() > 0) {
                //check if status ok or not
                final String status = parser.getAttributeValue(null, KEY_STATUS);
                if (KEY_STATUS_SUCCESS.equalsIgnoreCase(status)) {
                    parser.nextTag();
                    handleChildren(parser);
                } else {
                    throw new IOException(parser.getAttributeValue(null, KEY_MESSAGE) + ' ' + status);
                }
            } else {
                parser.nextTag();
                handleChildren(parser);
            }
        }

        mCallback.onFinish();
    }

    protected final void dispatchChildObject(T object) throws IOException {
        mCallback.onChildObject(object);
    }
}
