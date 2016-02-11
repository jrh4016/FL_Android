package com.skeds.android.phone.business.Utilities.REST.Objects;

import android.text.TextUtils;

import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.UserUtilitiesSingleton;
import com.skeds.android.phone.business.Utilities.NonfatalException;
import com.skeds.android.phone.business.Utilities.REST.RestConnector;
import com.skeds.android.phone.business.core.SkedsApplication;

import org.jdom2.Document;
import org.jdom2.Element;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public final class RESTErrorReport {

    private static final String KEY_ADD_LOG = "addLog";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_NAME = "name";

    private RESTErrorReport() {
    }

    public static void add(InputStream inputStream) throws NonfatalException {

        if (inputStream==null) return;

        final Element root = new Element(KEY_ADD_LOG);

        final Element tagName = new Element(KEY_NAME);
        Calendar c = Calendar.getInstance(Locale.US);
        SimpleDateFormat df = new SimpleDateFormat("yyyy MM dd 'at' HH:mm:ss", Locale.US);

//		DateFormat.format(inFormat, System.currentTimeMillis())

        String name = df.format(c.getTime()).replace("am", "AM").replace("pm", "PM");
        ;
        tagName.setText(name);
        root.addContent(tagName);

        String errorDescription = AppDataSingleton.getInstance().getErrorDescription();
        if (!TextUtils.isEmpty(errorDescription)) {
            final Element tagText = new Element(KEY_DESCRIPTION);
            tagText.setText(errorDescription);
            root.addContent(tagText);
        }

        RestConnector.getInstance().httpPostCheckSuccess(new Document(root),
                SkedsApplication.getContext().getString(R.string.upload_mobile_log_url, UserUtilitiesSingleton.getInstance().user.getId()), inputStream);

    }

}
