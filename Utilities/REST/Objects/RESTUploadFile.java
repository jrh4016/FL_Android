package com.skeds.android.phone.business.Utilities.REST.Objects;

import android.text.TextUtils;

import com.google.analytics.tracking.android.Log;
import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.NonfatalException;
import com.skeds.android.phone.business.Utilities.REST.RestConnector;
import com.skeds.android.phone.business.core.SkedsApplication;

import org.jdom2.Document;
import org.jdom2.Element;

import java.io.IOException;

/**
 * REST helper to upload document
 *
 * @author Den Oleshkevich
 */

public class RESTUploadFile {

    private static final String ADD_DOC_REQUEST = "addDocumentRequest";
    private static final String INVOICE_ID = "invoiceId";
    private static final String APPT_ID = "appointmentId";
    private static final String FILE_NAME = "fileName";
    private static final String DISPALY_NAME = "displayName";
    private static final String EXTENSION = "extension";

    /**
     * Attaches a document to a customer
     *
     * @param customerId
     * @param invoiceId
     * @param fileName
     * @param displayName
     * @param fileExtension
     * @return
     * @throws NonfatalException
     */

    public static Document send(String filePath, int customerId, int invoiceId, int apptId, String fileName, String displayName, String fileExtension)
            throws NonfatalException, IOException {

        if (customerId == 0) {
            Log.e("No customer ID found to send document!");
            return null;
        }

        Element rootNode = new Element(ADD_DOC_REQUEST);
        Element invoiceIdNode = new Element(INVOICE_ID);
        Element apptIdNode = new Element(APPT_ID);
        Element fileNameNode = new Element(FILE_NAME);
        Element displayNameNode = new Element(DISPALY_NAME);
        Element extensionNode = new Element(EXTENSION);

        if (invoiceId != 0) {
            invoiceIdNode.setText(String.valueOf(invoiceId));
            rootNode.addContent(invoiceIdNode);
        }

        if (apptId != 0) {
            apptIdNode.setText(String.valueOf(apptId));
            rootNode.addContent(apptIdNode);
        }

        if (!TextUtils.isEmpty(fileName)) {
            fileNameNode.setText(fileName);
            rootNode.addContent(fileNameNode);
        }

        if (!TextUtils.isEmpty(displayName)) {
            displayNameNode.setText(displayName);
            rootNode.addContent(displayNameNode);
        }

        if (!TextUtils.isEmpty(fileExtension)) {
            extensionNode.setText(fileExtension);
            rootNode.addContent(extensionNode);
        }

        return RestConnector.getInstance().httpPostCheckSuccess(new Document(rootNode),
                SkedsApplication.getContext().getString(R.string.upload_document_url, customerId), filePath);

    }
}
