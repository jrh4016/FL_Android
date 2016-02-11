package com.skeds.android.phone.business.Utilities.REST.Objects;

import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.InvoiceList;
import com.skeds.android.phone.business.Utilities.General.UserUtilitiesSingleton;
import com.skeds.android.phone.business.Utilities.NonfatalException;
import com.skeds.android.phone.business.Utilities.REST.RestConnector;

import org.jdom2.Document;
import org.jdom2.Element;

import java.util.List;

public class RESTInvoiceList {

    /* The list of all invoices */
    public static void query() throws NonfatalException {
        int userId = UserUtilitiesSingleton.getInstance().user.getId();
        Document document = RestConnector.getInstance().httpGet(
                "getinvoicesearch/" + userId);

        AppDataSingleton.getInstance().getInvoiceList().clear();
        AppDataSingleton.getInstance().setInvoiceListOwnerId(0);
        AppDataSingleton.getInstance().setInvoiceListParticipantId(0);
        AppDataSingleton.getInstance().getInvoiceList().clear();
        Element rootNode = document.getRootElement();

        Element invoicesNode = rootNode.getChild("invoices");
        if (invoicesNode == null)
            return;

        if (invoicesNode.getAttributeValue("participantId") != null)
            AppDataSingleton.getInstance().setInvoiceListParticipantId(Integer.parseInt(invoicesNode
                    .getAttributeValue("participantId")));
        if (invoicesNode.getAttributeValue("ownerId") != null)
            AppDataSingleton.getInstance().setInvoiceListOwnerId(Integer.parseInt(invoicesNode
                    .getAttributeValue("ownerId")));

        List<Element> invoicesList = invoicesNode.getChildren("invoice");

        if (!invoicesList.isEmpty()) {

            for (int invoiceIterator = 0; invoiceIterator < invoicesList.size(); invoiceIterator++) {

                AppDataSingleton.getInstance().getInvoiceList().add(new InvoiceList());

                Element invoiceNode = (Element) invoicesList
                        .get(invoiceIterator);

                if (invoiceNode.getAttributeValue("id") != null)
                    AppDataSingleton.getInstance().getInvoiceList()
                            .get(invoiceIterator)
                            .setId(Integer.parseInt(invoiceNode
                                    .getAttributeValue("id")));

                if (invoiceNode.getAttributeValue("customerName") != null)
                    AppDataSingleton.getInstance().getInvoiceList()
                            .get(invoiceIterator)
                            .setCustomerName(
                                    invoiceNode
                                            .getAttributeValue("customerName"));

                if (invoiceNode.getAttributeValue("appointmentId") != null)
                    AppDataSingleton.getInstance().getInvoiceList()
                            .get(invoiceIterator)
                            .setAppointmentId(
                                    Integer.parseInt(invoiceNode
                                            .getAttributeValue("appointmentId")));

                if (invoiceNode.getAttributeValue("invoiceDate") != null)
                    AppDataSingleton.getInstance().getInvoiceList()
                            .get(invoiceIterator)
                            .setDate(
                                    invoiceNode
                                            .getAttributeValue("invoiceDate"));

                if (invoiceNode.getAttributeValue("invoiceNumber") != null)
                    AppDataSingleton.getInstance().getInvoiceList()
                            .get(invoiceIterator)
                            .setNumber(
                                    invoiceNode
                                            .getAttributeValue("invoiceNumber"));

                if (invoiceNode.getAttributeValue("invoiceDescription") != null)
                    AppDataSingleton.getInstance().getInvoiceList()
                            .get(invoiceIterator)
                            .setDescription(
                                    invoiceNode
                                            .getAttributeValue("invoiceDescription"));

                if (invoiceNode.getAttributeValue("invoiceClosed") != null)
                    AppDataSingleton.getInstance().getInvoiceList()
                            .get(invoiceIterator)
                            .setClosed(
                                    Boolean.parseBoolean(invoiceNode
                                            .getAttributeValue("invoiceClosed")));
            }
        }
    }
}