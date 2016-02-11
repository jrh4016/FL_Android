package com.skeds.android.phone.business.Utilities.REST.Objects;

import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.LineItem;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.PricebookProductCost;
import com.skeds.android.phone.business.Utilities.General.CommonUtilities;
import com.skeds.android.phone.business.Utilities.NonfatalException;
import com.skeds.android.phone.business.Utilities.REST.RestConnector;

import org.jdom2.Document;
import org.jdom2.Element;

import java.math.BigDecimal;
import java.util.List;

public class RESTPricebook {

    /* Pricebook query for invoices that gets the final items, Step 3 */
    public static void query(int appointmentId, int manufacturerId,
                             int groupCodeId) throws NonfatalException {

        Element manufacturerIdNode = new Element("manufacturerId");
        if (manufacturerId != -1)
            manufacturerIdNode.setText(String.valueOf(manufacturerId));
        else
            manufacturerIdNode = null;

        Element groupIdNode = new Element("groupCodeId");
        if (groupCodeId != -1)
            groupIdNode.setText(String.valueOf(groupCodeId));
        else
            groupIdNode = null;

        Element rootNode = new Element("getPriceBook");
        if (manufacturerIdNode != null)
            rootNode.addContent(manufacturerIdNode);

        if (groupIdNode != null)
            rootNode.addContent(groupIdNode);

        if (appointmentId == 0)
            appointmentId = AppDataSingleton.getInstance().getAppointment()
                    .getId();
        Document doc = RestConnector.getInstance().httpPost(
                new Document(rootNode), "getpricebook/" + appointmentId);

        parse(doc);
    }

    public static void queryForEstimate(int customerId, int manufacturerId,
                                        int groupCodeId) throws NonfatalException {

        Element manufacturerIdNode = new Element("manufacturerId");
        if (manufacturerId != -1)
            manufacturerIdNode.setText(String.valueOf(manufacturerId));
        else
            manufacturerIdNode = null;

        Element groupIdNode = new Element("groupCodeId");
        if (groupCodeId != -1)
            groupIdNode.setText(String.valueOf(groupCodeId));
        else
            groupIdNode = null;

        Element rootNode = new Element("getPriceBookForEstimate");

        if (manufacturerIdNode != null)
            rootNode.addContent(manufacturerIdNode);

        if (groupIdNode != null)
            rootNode.addContent(groupIdNode);

        Document doc = RestConnector.getInstance().httpPost(
                new Document(rootNode), "getpricebookforestimate/" + customerId);

        parse(doc);
    }

    /**
     * This query is when creating a new estimate, without an appointment id
     *
     * @param servicePlanId
     * @param ownerId
     * @return
     * @throws NonfatalException
     */
    public static void queryForServicePlan(int servicePlanId, int ownerId,
                                           int manufacturerId, int groupCodeId) throws NonfatalException {
        Element rootNode = new Element("getPriceBookForServicePlan");

        // if (ownerId != 0) {
        // Element ownerIdNode = new Element("ownerId");
        // ownerIdNode.setText(String.valueOf(ownerId));
        // rootNode.addContent(ownerIdNode);
        // }

        Element groupIdNode = new Element("groupCodeId");
        Element manufacturerIdNode = new Element("manufacturerId");

        if (manufacturerId != -1)
            manufacturerIdNode.setText(String.valueOf(manufacturerId));
        else
            manufacturerIdNode.setText("");

        if (groupCodeId != -1)
            groupIdNode.setText(String.valueOf(groupCodeId));
        else
            groupIdNode.setText("");

        rootNode.addContent(manufacturerIdNode);
        rootNode.addContent(groupIdNode);

        parse(RestConnector.getInstance().httpPost(new Document(rootNode),
                "getpricebookforserviceplan/" + servicePlanId));
    }

    /* This query is for the view-only pricebook... not on invoices */
    public static void queryForOwner(int ownerId, int manufacturerId,
                                     int groupCodeId) throws NonfatalException {
        Element rootNode = new Element("getPriceBook");
        Element groupIdNode = new Element("groupCodeId");

        Element manufacturerIdNode = new Element("manufacturerId");
        if (manufacturerId != -1)
            manufacturerIdNode.setText(String.valueOf(manufacturerId));
        else
            manufacturerIdNode.setText("");

        if (groupCodeId != -1)
            groupIdNode.setText(String.valueOf(groupCodeId));
        else
            groupIdNode.setText("");

        rootNode.addContent(manufacturerIdNode);
        rootNode.addContent(groupIdNode);

        parseForOwner(RestConnector.getInstance().httpPost(new Document(rootNode),
                "getpricebookforowner/" + ownerId));
    }

    private static void parse(Document document) {
        AppDataSingleton.getInstance().getPricebookProductList().clear();

        if (document.getRootElement().getChild("products") == null)
            return;
        if (document.getRootElement().getChild("products")
                .getChildren("product") == null)
            return;

        List<Element> productsList = document.getRootElement()
                .getChild("products").getChildren("product");

        for (int typeIterator = 0; typeIterator < productsList.size(); typeIterator++) {

            AppDataSingleton.getInstance().getPricebookProductList()
                    .add(new LineItem());

            Element productNode = (Element) productsList.get(typeIterator);

            if (productNode.getAttributeValue("id") != null)
                AppDataSingleton
                        .getInstance()
                        .getPricebookProductList()
                        .get(typeIterator)
                        .setId(Integer.parseInt(productNode
                                .getAttributeValue("id")));

            if (productNode.getAttributeValue("id") != null)
                AppDataSingleton
                        .getInstance()
                        .getPricebookProductList()
                        .get(typeIterator)
                        .setServiceTypeId(
                                Integer.parseInt(productNode
                                        .getAttributeValue("id")));

            if (productNode.getAttributeValue("taxable") != null)
                AppDataSingleton
                        .getInstance()
                        .getPricebookProductList()
                        .get(typeIterator)
                        .setTaxable(
                                Boolean.parseBoolean(productNode
                                        .getAttributeValue("taxable")));

            AppDataSingleton.getInstance().getPricebookProductList().get(typeIterator).setCustomLineItem(false);

            if (productNode.getChild("productName") != null)
                AppDataSingleton.getInstance().getPricebookProductList()
                        .get(typeIterator)
                        .setName(productNode.getChild("productName").getText());

            if (productNode.getChild("productDescription") != null)
                AppDataSingleton
                        .getInstance()
                        .getPricebookProductList()
                        .get(typeIterator)
                        .setDescription(
                                productNode.getChild("productDescription")
                                        .getText());

            if (productNode.getChild("productCost") != null)
                AppDataSingleton.getInstance().getPricebookProductList()
                        .get(typeIterator).cost = new BigDecimal(productNode
                        .getChild("productCost").getText());

            AppDataSingleton.getInstance().getPricebookProductList().get(typeIterator).resetRates();
            if (productNode.getChild("taxes") != null) {
                if (productNode.getChild("taxes").getChildren("tax") != null) {
                    List<Element> taxes = productNode.getChild("taxes").getChildren("tax");
                    int index = 0;
                    for (Element tx : taxes)
                        if (index < AppDataSingleton.getInstance().getPricebookProductList().get(typeIterator).rateIds.size())
                            AppDataSingleton.getInstance().getPricebookProductList().get(typeIterator).rateIds.set(index++,
                                    Long.parseLong(tx.getAttributeValue("rateId")));
                }
            }


            // .setCost(
            // CommonUtilities.roundDouble(
            // Double.parseDouble(productNode
            // .getChild(
            // "productCost")
            // .getText()),
            // 2,
            // BigDecimal.ROUND_HALF_UP));

            if (productNode.getChild("additionalCost") != null)
                AppDataSingleton.getInstance().getPricebookProductList()
                        .get(typeIterator).additionalCost = new BigDecimal(
                        productNode.getChild("additionalCost").getText());

            else
                AppDataSingleton.getInstance().getPricebookProductList()
                        .get(typeIterator).additionalCost = new BigDecimal("-1");
        }
        // setAdditionalCost(-1);
    }

    private static void parseForOwner(Document document) {
        AppDataSingleton.getInstance().getPricebookProductList().clear();
        Element el = document.getRootElement().getChild("products");
        if (el == null)
            return;
        List<Element> productsList = el.getChildren("product");

        for (int typeIterator = 0; typeIterator < productsList.size(); typeIterator++) {

            AppDataSingleton.getInstance().getPricebookProductList()
                    .add(new LineItem());
            Element productNode = (Element) productsList.get(typeIterator);

            if (productNode.getAttributeValue("id") != null) {
                AppDataSingleton
                        .getInstance()
                        .getPricebookProductList()
                        .get(typeIterator)
                        .setId(Integer.parseInt(productNode
                                .getAttributeValue("id")));

                AppDataSingleton
                        .getInstance()
                        .getPricebookProductList()
                        .get(typeIterator)
                        .setServiceTypeId(
                                Integer.parseInt(productNode
                                        .getAttributeValue("id")));
            }

            if (productNode.getAttributeValue("taxable") != null)
                AppDataSingleton
                        .getInstance()
                        .getPricebookProductList()
                        .get(typeIterator)
                        .setTaxable(
                                Boolean.parseBoolean(productNode
                                        .getAttributeValue("taxable")));

            if (productNode.getChild("productName") != null)
                AppDataSingleton.getInstance().getPricebookProductList()
                        .get(typeIterator)
                        .setName(productNode.getChild("productName").getText());

            if (productNode.getChild("productDescription") != null)
                AppDataSingleton
                        .getInstance()
                        .getPricebookProductList()
                        .get(typeIterator)
                        .setDescription(
                                productNode.getChild("productDescription")
                                        .getText());

            AppDataSingleton.getInstance().getPricebookProductList().get(typeIterator).resetRates();
            if (productNode.getChild("taxes") != null) {
                if (productNode.getChild("taxes").getChildren("tax") != null) {
                    List<Element> taxes = productNode.getChild("taxes").getChildren("tax");
                    int index = 0;
                    for (Element tx : taxes)
                        AppDataSingleton.getInstance().getPricebookProductList().get(typeIterator).rateIds.add(index++,
                                Long.parseLong(tx.getAttributeValue("rateId")));
                }
            }

            Element productCostNode = productNode.getChild("productCosts");

            AppDataSingleton.getInstance().getPricebookProductList()
                    .get(typeIterator).productCost.clear();

            if (productCostNode != null) {
                List<Element> productCostList = productCostNode
                        .getChildren("productCost");

                if (!productCostList.isEmpty()) {
                    for (int productCostIterator = 0; productCostIterator < productCostList
                            .size(); productCostIterator++) {

                        AppDataSingleton.getInstance()
                                .getPricebookProductList().get(typeIterator).productCost
                                .add(new PricebookProductCost());

                        Element specificProductCostNode = (Element) productCostList
                                .get(productCostIterator);

                        if (specificProductCostNode.getAttributeValue("name") != null)
                            AppDataSingleton.getInstance()
                                    .getPricebookProductList()
                                    .get(typeIterator).productCost.get(
                                    productCostIterator).setName(
                                    specificProductCostNode
                                            .getAttributeValue("name"));

                        if (specificProductCostNode.getAttributeValue("price") != null) {
                            AppDataSingleton.getInstance()
                                    .getPricebookProductList()
                                    .get(typeIterator).productCost
                                    .get(productCostIterator)
                                    .setCost(
                                            CommonUtilities.roundDouble(
                                                    Double.parseDouble(

                                                            specificProductCostNode
                                                                    .getAttributeValue("price")),
                                                    2, BigDecimal.ROUND_HALF_UP));

                            if (AppDataSingleton.getInstance()
                                    .getPricebookProductList()
                                    .get(typeIterator).productCost
                                    .get(productCostIterator).getName()
                                    .equals("Additional"))
                                AppDataSingleton.getInstance()
                                        .getPricebookProductList()
                                        .get(typeIterator).additionalCost = new BigDecimal(
                                        specificProductCostNode
                                                .getAttributeValue("price"));

                        }

                        if (productCostIterator == 0) {
                            AppDataSingleton.getInstance()
                                    .getPricebookProductList()
                                    .get(typeIterator).cost = new BigDecimal(
                                    specificProductCostNode
                                            .getAttributeValue("price"));

                        }
                    }
                }
            }

            if (productNode.getChild("productCost") != null)
                AppDataSingleton.getInstance().getPricebookProductList()
                        .get(typeIterator).cost = new BigDecimal(productNode
                        .getChild("productCost").getText());

            // .setCost(
            // CommonUtilities.roundDouble(
            // Double.parseDouble(productNode
            // .getChild(
            // "productCost")
            // .getText()),
            // 2,
            // BigDecimal.ROUND_HALF_UP));

            if (productNode.getChild("additionalCost") != null)
                AppDataSingleton.getInstance().getPricebookProductList()
                        .get(typeIterator).additionalCost = new BigDecimal(
                        productNode.getChild("additionalCost").getText());
            else
                AppDataSingleton.getInstance().getPricebookProductList()
                        .get(typeIterator).additionalCost = new BigDecimal("-1");

            // .setAdditionalCost(
            // CommonUtilities.roundDouble(
            // Double.parseDouble(productNode
            // .getChild(
            // "additionalCost")
            // .getText()),
            // 2,
            // BigDecimal.ROUND_HALF_UP));

        }
    }
}