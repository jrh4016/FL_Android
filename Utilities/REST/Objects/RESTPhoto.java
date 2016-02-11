package com.skeds.android.phone.business.Utilities.REST.Objects;

import android.text.TextUtils;

import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.NonfatalException;
import com.skeds.android.phone.business.Utilities.REST.RestConnector;
import com.skeds.android.phone.business.core.SkedsApplication;

import org.jdom2.Document;
import org.jdom2.Element;

import java.io.InputStream;

public final class RESTPhoto {

    private static final String KEY_NODE_EQUIPMENT_ID = "equipmentId";
    private static final String KEY_NODE_APPT_ID = "appointmentId";
    private static final String KEY_NODE_DESCRIPTION = "tagText";
    private static final String KEY_ROOT_PHOTO = "addPhoto";

    private RESTPhoto() {
    }

    public static void add(final String appointmentId, final String equipmentId,
                           final InputStream inputStream, final String photoDescription)
            throws NonfatalException {
        final Element root = new Element(KEY_ROOT_PHOTO);

        if (!TextUtils.isEmpty(photoDescription)) {
            final Element tagText = new Element(KEY_NODE_DESCRIPTION);
            tagText.setText(photoDescription);
            root.addContent(tagText);
        }

        if (!TextUtils.isEmpty(appointmentId)) {
            final Element appointmentIdNode = new Element(KEY_NODE_APPT_ID);
            appointmentIdNode.setText(appointmentId);
            root.addContent(appointmentIdNode);
        }

        if (!TextUtils.isEmpty(equipmentId)) {
            final Element equipmentIdNode = new Element(KEY_NODE_EQUIPMENT_ID);
            equipmentIdNode.setText(String.valueOf(equipmentId));
            root.addContent(equipmentIdNode);
        }

        RestConnector.getInstance().httpPostCheckSuccess(new Document(root), SkedsApplication.getContext().getString(
                R.string.upload_photo_url, AppDataSingleton.getInstance().getCustomer().getId()), inputStream);
    }
}