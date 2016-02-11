package com.skeds.android.phone.business.Utilities.REST.Objects;

import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.LineItem;
import com.skeds.android.phone.business.Utilities.General.UserUtilitiesSingleton;
import com.skeds.android.phone.business.Utilities.NonfatalException;
import com.skeds.android.phone.business.Utilities.REST.RestConnector;

import org.jdom2.Document;
import org.jdom2.Element;

import java.math.BigDecimal;
import java.util.List;

public class RESTLineItemList {

    /* Line items for invoices */
    public static void query() throws NonfatalException {
        AppDataSingleton.getInstance().getLineItemList().clear();
        Document document = RestConnector.getInstance().httpGet(
                "getproducts/" + UserUtilitiesSingleton.getInstance().user.getOwnerId());
        Element root = document.getRootElement().getChild("products");
        if (root == null)
            throw new NonfatalException("XML",
                    "Bad server response: no products");

        List<Element> productList = root.getChildren("product");

        for (int lineItemIterator = 0; lineItemIterator < productList.size(); lineItemIterator++) {
            AppDataSingleton.getInstance().getLineItemList().add(new LineItem());

            Element productNode = (Element) productList.get(lineItemIterator);

            if (productNode.getAttributeValue("id") != null)
                AppDataSingleton.getInstance().getLineItemList()
                        .get(lineItemIterator)
                        .setServiceTypeId(
                                Integer.parseInt(productNode
                                        .getAttributeValue("id")));
            if (productNode.getAttributeValue("taxable") != null)
                AppDataSingleton.getInstance().getLineItemList()
                        .get(lineItemIterator)
                        .setTaxable(
                                Boolean.parseBoolean(productNode
                                        .getAttributeValue("taxable")));

            AppDataSingleton.getInstance().getLineItemList().get(lineItemIterator).setCustomLineItem(false);

            if (productNode.getChildText("productName") != null)
                AppDataSingleton.getInstance().getLineItemList().get(lineItemIterator)
                        .setName(productNode.getChildText("productName"));

            if (productNode.getChildText("productDescription") != null)
                AppDataSingleton.getInstance().getLineItemList()
                        .get(lineItemIterator)
                        .setDescription(
                                productNode.getChildText("productDescription"));

            if (productNode.getChildText("productCost") != null)
                AppDataSingleton.getInstance().getLineItemList().get(lineItemIterator).cost = new BigDecimal(
                        productNode.getChildText("productCost"));

            AppDataSingleton.getInstance().getLineItemList().get(lineItemIterator).resetRates();
            if (productNode.getChild("taxes") != null) {
                if (productNode.getChild("taxes").getChildren("tax") != null) {
                    List<Element> taxes = productNode.getChild("taxes").getChildren("tax");
                    int index = 0;
                    for (Element tx : taxes)
                        if (index < AppDataSingleton.getInstance().getLineItemList().get(lineItemIterator).rateIds.size())
                            AppDataSingleton.getInstance().getLineItemList().get(lineItemIterator).rateIds.set(index++,
                                    Long.parseLong(tx.getAttributeValue("rateId")));
                }
            }

        }
    }
}