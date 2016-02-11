package com.skeds.android.phone.business.Utilities.REST.Objects;

import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.PdfDocument;
import com.skeds.android.phone.business.Utilities.General.UserUtilitiesSingleton;
import com.skeds.android.phone.business.Utilities.NonfatalException;
import com.skeds.android.phone.business.Utilities.REST.RestConnector;

import org.jdom2.Document;
import org.jdom2.Element;

import java.io.IOException;
import java.util.List;

public class RESTPdfFiles {

    public static void queryDocuments(int apptId, int equipId)
            throws NonfatalException {

        if (AppDataSingleton.getInstance().getCustomer().getId() == 0)
            return;

        StringBuilder sb = new StringBuilder();

        if (apptId != 0)
            sb.append("<appointmentId>" + apptId + "</appointmentId>");

        if (equipId != 0)
            sb.append("<equipmentId>" + equipId + "</equipmentId>");

        Document doc = RestConnector.getInstance().httpPostCheckSuccess(
                sb.toString(),
                "getdocuments/"
                        + AppDataSingleton.getInstance().getCustomer().getId());

        parseDocs(doc);
    }

    public static void queryTemplates(String type) throws NonfatalException {
        Element root = new Element("getFormTemplatesRequest");

        Element element = new Element("usageType");
        element.setText(type);
        root.addContent(element);

        Document doc = RestConnector.getInstance().httpPostCheckSuccess(
                new Document(root),
                "getformtemplates/"
                        + UserUtilitiesSingleton.getInstance().user
                        .getOwnerId());

        parseTemplates(doc);
    }

    public static void deleteDocument(int docId) throws NonfatalException {
        RestConnector.getInstance().httpGetCheckSuccess("deletedocument/" + docId);
    }

    public static void addDocument(String name, int apptId, int equipId, String filePath) throws NonfatalException,
            IOException {
        Element root = new Element("addDocumentRequest");

        Element extElement = new Element("extension");
        extElement.setText("pdf");
        root.addContent(extElement);

        Element nameElement = new Element("displayName");
        nameElement.setText(name);
        root.addContent(nameElement);

        if (apptId != 0) {
            Element apptIdElement = new Element("appointmentId");
            apptIdElement.setText(apptId + "");
            root.addContent(apptIdElement);
        }
        if (equipId != 0) {
            Element equipIdElement = new Element("equipmentId");
            equipIdElement.setText(equipId + "");
            root.addContent(equipIdElement);
        }

        RestConnector.getInstance().httpPostCheckSuccess(
                new Document(root),
                "uploaddocument/"
                        + AppDataSingleton.getInstance().getCustomer().getId(), filePath);
    }

    public static void updateDocument(String name, int docId, String filePath)
            throws NonfatalException, IOException {
        Element root = new Element("editDocumentRequest");

        root.setAttribute("id", docId + "");

        Element nameElement = new Element("displayName");
        nameElement.setText(name);
        root.addContent(nameElement);

        RestConnector.getInstance().httpPostCheckSuccess(new Document(root),
                "uploaddocument/" + AppDataSingleton.getInstance().getCustomer().getId(), filePath);
    }

    public static void parseDocs(Document doc) {
        AppDataSingleton.getInstance().getPdfDocsList().clear();
        AppDataSingleton.getInstance().getPdfDocsList().add(new PdfDocument());

        if (doc.getRootElement().getChild("documents") == null)
            return;

        List<Element> docElements = doc.getRootElement().getChild("documents")
                .getChildren("document");

        for (Element element : docElements) {

            PdfDocument pdfItem = new PdfDocument();

            if (element.getAttributeValue("id") != null)
                pdfItem.setId(Integer.parseInt(element.getAttributeValue("id")));

            if (element.getChild("url") != null)
                pdfItem.setUrl(element.getChild("url").getText());

            if (!pdfItem.getUrl().endsWith(".pdf"))
                continue;

            if (element.getChild("displayName") != null)
                pdfItem.setName(element.getChild("displayName").getText());

            if (element.getChild("equipmentId") != null)
                pdfItem.setEquipmentId(Integer.parseInt(element.getChild(
                        "equipmentId").getText()));

            if (element.getChild("appointmentId") != null)
                pdfItem.setApptId(Integer.parseInt(element.getChild(
                        "appointmentId").getText()));

            if (element.getChild("reporter") != null) {
                pdfItem.setReporterId(Integer.parseInt(element.getChild(
                        "reporter").getAttributeValue("id")));
                pdfItem.setReporterName(element.getChild("reporter").getText());
            }

            AppDataSingleton.getInstance().getPdfDocsList().add(pdfItem);
        }

    }

    public static void parseTemplates(Document doc) {
        AppDataSingleton.getInstance().getPdfTemplatesList().clear();

        if (doc.getRootElement().getChild("formTemplates") == null)
            return;

        List<Element> docElements = doc.getRootElement()
                .getChild("formTemplates").getChildren("formTemplate");
        if (docElements == null)
            return;

        for (Element element : docElements) {

            PdfDocument pdfItem = new PdfDocument();

            if (element.getAttributeValue("id") != null)
                pdfItem.setId(Integer.parseInt(element.getAttributeValue("id")));

            if (element.getChild("url") != null)
                pdfItem.setUrl(element.getChild("url").getText());

            if (!pdfItem.getUrl().endsWith(".pdf"))
                continue;

            if (element.getChild("displayName") != null)
                pdfItem.setName(element.getChild("displayName").getText());

            if (element.getChild("description") != null)
                pdfItem.setDescription(element.getChild("description")
                        .getText());

            if (element.getChild("usageType") != null)
                pdfItem.setUsageType(element.getChild("usageType").getText());

            AppDataSingleton.getInstance().getPdfTemplatesList().add(pdfItem);
        }
    }
}