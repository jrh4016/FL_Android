package com.skeds.android.phone.business.Utilities.REST.Objects;

import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.LineItem;
import com.skeds.android.phone.business.Utilities.NonfatalException;
import com.skeds.android.phone.business.Utilities.REST.RestConnector;

import org.jdom2.Document;
import org.jdom2.Element;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class RESTOnTruckList {

    /* On truck items to be used with appointments */
    public static void query(int ownerId) throws NonfatalException {
        Document document = RestConnector.getInstance().httpGet(
                "gettruckproducts/" + ownerId);
        AppDataSingleton.getInstance().getOnTruckPartsList().clear();

        Element el = document.getRootElement().getChild("products");
        if (el == null)
            return;
        List<Element> productsList = el.getChildren("product");

        // CommonUtilities.mTruckItem = new
        // LineItem[productsList.size()];
        for (int typeIterator = 0; typeIterator < productsList.size(); typeIterator++) {

            AppDataSingleton.getInstance().getOnTruckPartsList().add(new LineItem());

            // CommonUtilities.mTruckItem[typeIterator] = new
            // LineItem();

            Element productNode = (Element) productsList.get(typeIterator);

            if (productNode.getAttributeValue("id") != null)
                AppDataSingleton.getInstance().getOnTruckPartsList()
                        .get(typeIterator)
                        .setId(Integer.parseInt(productNode
                                .getAttributeValue("id")));

            if (productNode.getAttributeValue("taxable") != null)
                AppDataSingleton.getInstance().getOnTruckPartsList()
                        .get(typeIterator)
                        .setTaxable(
                                Boolean.parseBoolean(productNode
                                        .getAttributeValue("taxable")));

            if (productNode.getChild("productName") != null)
                AppDataSingleton.getInstance().getOnTruckPartsList().get(typeIterator)
                        .setName(productNode.getChild("productName").getText());

            if (productNode.getChild("productDescription") != null)
                AppDataSingleton.getInstance().getOnTruckPartsList()
                        .get(typeIterator)
                        .setDescription(
                                productNode.getChild("productDescription")
                                        .getText());

            if (productNode.getChild("productCost") != null)
                AppDataSingleton.getInstance().getOnTruckPartsList().get(typeIterator).cost = new BigDecimal(
                        productNode.getChild("productCost").getText());

            // .setCost(
            // CommonUtilities.roundDouble(
            // Double.parseDouble(productNode
            // .getChild(
            // "productCost")
            // .getText()), 2,
            // BigDecimal.ROUND_HALF_UP));
        }
    }

    public static void add(int serviceTypeId, int appointmentId,
                           int serviceProviderId, double quantity, boolean addToInvoice)
            throws NonfatalException {
        Element rootNode = new Element("useOnTruckRequest");

        Element appointmentIdNode = new Element("appointmentId");
        Element serviceProviderIdNode = new Element("serviceProviderId");
        Element quantityNode = new Element("quantity");
        Element addToInvoiceNode = new Element("addToInvoice");

        appointmentIdNode.setText(String.valueOf(appointmentId));
        serviceProviderIdNode.setText(String.valueOf(serviceProviderId));
        quantityNode.setText(String.valueOf(quantity));
        addToInvoiceNode.setText(String.valueOf(addToInvoice));

        rootNode.addContent(appointmentIdNode);
        rootNode.addContent(serviceProviderIdNode);
        rootNode.addContent(quantityNode);
        rootNode.addContent(addToInvoiceNode);

        RestConnector.getInstance().httpPostCheckSuccess(new Document(rootNode),
                "useontruckproduct/" + serviceTypeId);
    }

    public static void queryAdded(int apptId) throws NonfatalException {

        if (apptId == 0)
            apptId = AppDataSingleton.getInstance().getAppointment().getId();

        Document document = RestConnector.getInstance().httpGet(
                "getappointmentontruckproducts/" + apptId);

        List<LineItem> newOnTruckList = new ArrayList<LineItem>();
        AppDataSingleton.getInstance().getAppointment().setOnTruckList(newOnTruckList);


        Element el = document.getRootElement().getChild("productsAlreadyUsed");
        if (el == null)
            return;

        List<Element> list = el.getChildren("product");

        for (Element node : list) {
            LineItem productItem = new LineItem();

            if (node.getAttributeValue("id") != null)
                productItem.setId(Integer.parseInt(node.getAttributeValue("id")));

            if (node.getAttributeValue("serviceTypeId") != null)
                productItem.setServiceTypeId(Integer.parseInt(node.getAttributeValue("serviceTypeId")));

            if (node.getChild("quantity") != null)
                productItem.setQuantity(new BigDecimal(node.getChild("quantity").getText()));

            if (node.getChild("productName") != null)
                productItem.setName(node.getChild("productName").getText());

            newOnTruckList.add(productItem);
        }

        AppDataSingleton.getInstance().getAppointment().setOnTruckList(newOnTruckList);

    }

}