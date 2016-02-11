package com.skeds.android.phone.business.Utilities.REST.Objects;

import com.skeds.android.phone.business.Utilities.General.ClassObjects.InvoicePartOrder;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.PartOrder;
import com.skeds.android.phone.business.Utilities.NonfatalException;
import com.skeds.android.phone.business.Utilities.REST.RestConnector;

import org.jdom2.Document;
import org.jdom2.Element;

import java.util.ArrayList;
import java.util.List;

public class RESTPartOrderList {

    /**
     * Query part orders for owner
     *
     * @param ownerId owner id
     * @return list of invocie's part orders
     * @throws NonfatalException on error
     */
    public static List<InvoicePartOrder> query(int ownerId)
            throws NonfatalException {
        RestConnector conn = RestConnector.getInstance();
        return parse(conn.httpGet("getpartorders/" + ownerId));
    }

    /**
     * Retrive part orders list for specified appointment
     *
     * @param appointmentId appt id
     * @return list of part orders
     * @throws NonfatalException on error
     */
    public static List<PartOrder> queryForAppointment(int appointmentId)
            throws NonfatalException {
        RestConnector conn = RestConnector.getInstance();
        return parse4appt(conn.httpGet("getpartordersforappointment/"
                + appointmentId));
    }

    private static List<InvoicePartOrder> parse(Document document) {
        Element rootNode = document.getRootElement();

        if (rootNode.getChildren().isEmpty()
                || rootNode.getChild("partOrders") == null)
            return new ArrayList<InvoicePartOrder>(1);
        final List<Element> invoiceListNode = rootNode.getChild("partOrders")
                .getChildren("invoice");
        if (invoiceListNode.isEmpty())
            return new ArrayList<InvoicePartOrder>(1);

        final List<InvoicePartOrder> partOrderList = new ArrayList<InvoicePartOrder>(
                64);

        for (int i = 0; i < invoiceListNode.size(); i++) {
            Element invoiceNode = (Element) invoiceListNode.get(i);
            InvoicePartOrder thisPartOrder = new InvoicePartOrder();

            if (invoiceNode.getAttribute("id") != null)
                thisPartOrder.setId(Integer.parseInt(invoiceNode
                        .getAttributeValue("id")));

            if (invoiceNode.getAttribute("invoiceNumber") != null)
                thisPartOrder.setInvoiceNumber(invoiceNode
                        .getAttributeValue("invoiceNumber"));

            if (invoiceNode.getAttribute("customer") != null)
                thisPartOrder.setCustomerName(invoiceNode
                        .getAttributeValue("customer"));

            if (invoiceNode.getAttribute("invoiceDate") != null)
                thisPartOrder.setInvoiceDate(invoiceNode
                        .getAttributeValue("invoiceDate"));

            for (Element el : invoiceNode.getChildren("partOrder"))
                thisPartOrder.partOrder.add(parsePartOrder(el));

            partOrderList.add(thisPartOrder);
        }
        return partOrderList;
    }

    /*
     * D/xml-dump( 6738): <getPartOrdersForAppointmentResponse> D/xml-dump(
     * 6738): <partOrder id="201"> D/xml-dump( 6738): <name>pn1</name>
     * D/xml-dump( 6738): <status>TO_BE_ORDERED</status> D/xml-dump( 6738):
     * <number>1</number> D/xml-dump( 6738):
     * <unitSalesPrice>1.00</unitSalesPrice> D/xml-dump( 6738):
     * <partNumber>pnum1</partNumber> D/xml-dump( 6738): </partOrder>
     * D/xml-dump( 6738): <partOrder id="202"> D/xml-dump( 6738):
     * <name>pnam</name> D/xml-dump( 6738): <status>TO_BE_ORDERED</status>
     * D/xml-dump( 6738): <number>2</number> D/xml-dump( 6738):
     * <unitSalesPrice>2.00</unitSalesPrice> D/xml-dump( 6738):
     * <partNumber>pnum</partNumber> D/xml-dump( 6738): </partOrder> D/xml-dump(
     * 6738): <partOrder id="203"> D/xml-dump( 6738): <name>rrr</name>
     * D/xml-dump( 6738): <status>TO_BE_ORDERED</status> D/xml-dump( 6738):
     * <number>11</number> D/xml-dump( 6738):
     * <unitSalesPrice>22.00</unitSalesPrice> D/xml-dump( 6738):
     * <partNumber>nnn</partNumber> D/xml-dump( 6738): </partOrder> D/xml-dump(
     * 6738): <partOrder id="205"> D/xml-dump( 6738): <name>lyisa pname 1</name>
     * D/xml-dump( 6738): <status>TO_BE_ORDERED</status> D/xml-dump( 6738):
     * <number>234</number> D/xml-dump( 6738):
     * <unitSalesPrice>234.00</unitSalesPrice> D/xml-dump( 6738):
     * <partNumber>lyisa pnum 1</partNumber> D/xml-dump( 6738): </partOrder>
     * D/xml-dump( 6738): <partOrder id="206"> D/xml-dump( 6738): <name>Pname
     * 1</name> D/xml-dump( 6738): <status>TO_BE_ORDERED</status> D/xml-dump(
     * 6738): <number>700.76</number> D/xml-dump( 6738):
     * <unitSalesPrice>8743.17</unitSalesPrice> D/xml-dump( 6738):
     * <partNumber>Pname 1</partNumber> D/xml-dump( 6738):
     * <manufacturerId>4</manufacturerId> D/xml-dump( 6738): </partOrder>
     * D/xml-dump( 6738): </getPartOrdersForAppointmentResponse>
     */
    private static List<PartOrder> parse4appt(Document document)
            throws NonfatalException {
        // AppData.getServicePlanList().clear();
        Element root = document.getRootElement();
        if (root == null
                || !"getPartOrdersForAppointmentResponse".equals(root.getName()))
            throw new NonfatalException("XML", "Wrong XML data from server");
        List<Element> xmlList = root.getChildren("partOrder");
        final List<PartOrder> partOrderList = new ArrayList<PartOrder>(
                xmlList.size());

        for (Element el : xmlList)
            partOrderList.add(parsePartOrder(el));
        return partOrderList;
    }

    private static PartOrder parsePartOrder(Element partOrderNode) {
        final PartOrder thisOrder = new PartOrder();

        if (partOrderNode.getAttribute("id") != null)
            thisOrder.setId(Integer.parseInt(partOrderNode
                    .getAttributeValue("id")));

        if (partOrderNode.getChild("name") != null)
            thisOrder.setName(partOrderNode.getChild("name").getText());

        if (partOrderNode.getChild("description") != null)
            thisOrder.setDescription(partOrderNode.getChild("description")
                    .getText());

        if (partOrderNode.getChild("status") != null)
            thisOrder.setStatus(partOrderNode.getChild("status").getText());

        if (partOrderNode.getChild("deliveryDateTime") != null)
            thisOrder.setDeliveryDateTime(partOrderNode.getChild(
                    "deliveryDateTime").getText());

        if (partOrderNode.getChild("number") != null)
            thisOrder.setQuantity(partOrderNode.getChild("number").getText());

        if (partOrderNode.getChild("unitSalesPrice") != null)
            thisOrder.setPrice(partOrderNode.getChild("unitSalesPrice")
                    .getText());

        if (partOrderNode.getChild("partNumber") != null)
            thisOrder.setPartNumber(partOrderNode.getChild("partNumber")
                    .getText());

        if (partOrderNode.getChild("manufacturerId") != null)
            thisOrder.setManufacturerId(Integer.parseInt(partOrderNode.getChild("manufacturerId")
                    .getText()));

        if (partOrderNode.getChild("customField1") != null)
            thisOrder.setCustomField1(partOrderNode.getChild("customField1")
                    .getText());

        if (partOrderNode.getChild("customField2") != null)
            thisOrder.setCustomField2(partOrderNode.getChild("customField2")
                    .getText());

        if (partOrderNode.getChild("customField3") != null)
            thisOrder.setCustomField3(partOrderNode.getChild("customField3")
                    .getText());

        if (partOrderNode.getChild("trackingNumber") != null)
            thisOrder.setTrackingNumber(partOrderNode
                    .getChild("trackingNumber").getText());
        return thisOrder;
    }
}