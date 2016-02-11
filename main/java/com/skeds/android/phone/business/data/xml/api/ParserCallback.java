package com.skeds.android.phone.business.data.xml.api;

import java.io.IOException;

public interface ParserCallback<T> {
    void onStart() throws IOException;

    void onChildObject(T object) throws IOException;

    void onFinish() throws IOException;
}
