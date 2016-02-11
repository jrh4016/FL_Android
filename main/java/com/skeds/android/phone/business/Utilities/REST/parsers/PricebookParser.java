package com.skeds.android.phone.business.Utilities.REST.parsers;

import android.content.Context;
import android.text.TextUtils;

import com.google.analytics.tracking.android.Log;
import com.skeds.android.phone.business.Pricebook.PricebookDatabase;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.PriceBook.Cost;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.PriceBook.GroupCode;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.PriceBook.Manufacturer;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.PriceBook.Product;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.PriceBook.ServicePlan;
import com.skeds.android.phone.business.core.SkedsApplication;

import org.jdom2.Document;
import org.jdom2.Element;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to parse pricebook xml
 *
 * @author Den Oleshkevich
 */

public class PricebookParser {

    private PricebookDatabase pricebookDatabase;

    private long iterator = 0;

    public PricebookParser(Context context) {
        pricebookDatabase = PricebookDatabase.getInstance();
    }

    public void parse(Document document) {

        // Service Plans Parser

        if (document.getRootElement().getChild("servicePlans") != null)
            if (document.getRootElement().getChild("servicePlans")
                    .getChildren("plan") != null) {
                List<Element> servicePlansList = document.getRootElement()
                        .getChild("servicePlans").getChildren("plan");

                List<ServicePlan> spTmp = new ArrayList<ServicePlan>();

                for (Element p : servicePlansList) {
                    ServicePlan sp = new ServicePlan();

                    if (p.getAttributeValue("id") != null)
                        sp.setId(Integer.parseInt(p.getAttributeValue("id")));

                    if (p.getAttributeValue("name") != null)
                        sp.setName(p.getAttributeValue("name"));

                    if (p.getAttributeValue("priceType") != null)
                        sp.setPriceType(p.getAttributeValue("priceType"));

                    spTmp.add(sp);

                    // TODO: add the sp to database
                    Log.d("Added service plan " + sp.getId() + " with name " + sp.getName());
                    iterator++;
                }
                pricebookDatabase.getServicePlans().addAll(spTmp);
            }

        // Products Parser

        if (document.getRootElement().getChild("products") != null)
            if (document.getRootElement().getChild("products")
                    .getChildren("product") != null) {
                List<Element> productsList = document.getRootElement()
                        .getChild("products").getChildren("product");

                List<Product> productsTmp = new ArrayList<Product>();

                for (Element p : productsList) {
                    Product product = new Product();

                    if (p.getAttributeValue("id") != null)
                        product.setId(Integer.parseInt(p
                                .getAttributeValue("id")));

                    if (p.getAttributeValue("taxable") != null)
                        product.setTaxable(Boolean.parseBoolean(p
                                .getAttributeValue("taxable")));

                    if (p.getAttributeValue("deleted") != null)
                        product.setDeleted(Boolean.parseBoolean(p
                                .getAttributeValue("deleted")));

                    if (p.getChild("productName") != null)
                        product.setName(p.getChild("productName").getText());

                    if (p.getChild("productDescription") != null)
                        product.setDescription(p.getChild("productDescription")
                                .getText());

                    if (p.getChild("manufacturerId") != null)
                        product.setManufacturerId(Integer.parseInt(p.getChild(
                                "manufacturerId").getText()));

                    if (p.getChild("groupCodeId") != null)
                        product.setGroupCodeId(Integer.parseInt(p.getChild(
                                "groupCodeId").getText()));

                    if (p.getChild("productCosts") != null)
                        if (p.getChild("productCosts").getChildren(
                                "productCost") != null) {
                            List<Element> costsList = p
                                    .getChild("productCosts").getChildren(
                                            "productCost");

                            for (Element c : costsList) {
                                Cost cost = new Cost();

                                if (c.getAttributeValue("type") != null)
                                    cost.setType(c.getAttributeValue("type"));

                                if (c.getAttributeValue("displayName") != null)
                                    cost.setName(c
                                            .getAttributeValue("displayName"));

                                if (c.getAttributeValue("price") != null)
                                    cost.setPrice(new BigDecimal(c
                                            .getAttributeValue("price")));

                                product.getProductCosts().add(cost);
                            }
                        }

                    productsTmp.add(product);
                    Log.d("Added product " + product.getId() + " with name " + product.getName());
                    iterator++;
                }
                pricebookDatabase.getProducts().addAll(productsTmp);
            }

        // Manufacturers Parser

        if (document.getRootElement().getChild("manufacturers") != null)
            if (document.getRootElement().getChild("manufacturers")
                    .getChildren("manufacturer") != null) {
                List<Element> manufacturersList = document.getRootElement()
                        .getChild("manufacturers").getChildren("manufacturer");

                List<Manufacturer> manufTmp = new ArrayList<Manufacturer>();

                for (Element m : manufacturersList) {
                    Manufacturer manuf = new Manufacturer();

                    if (m.getAttributeValue("id") != null)
                        manuf.setId(Integer.parseInt(m.getAttributeValue("id")));

                    if (m.getAttributeValue("name") != null)
                        manuf.setName(m.getAttributeValue("name"));

                    if (m.getAttributeValue("description") != null)
                        manuf.setDescription(m.getAttributeValue("description"));

                    manufTmp.add(manuf);

                    Log.d("Added manufacturer " + manuf.getId() + " with name " + manuf.getName());
                    iterator++;
                }
                pricebookDatabase.getManufacturers().addAll(manufTmp);
            }

        // Groups Parser

        if (document.getRootElement().getChild("groups") != null)
            if (document.getRootElement().getChild("groups")
                    .getChildren("group") != null) {
                List<Element> groupsList = document.getRootElement()
                        .getChild("groups").getChildren("group");

                List<GroupCode> codesTmp = new ArrayList<GroupCode>();

                for (Element g : groupsList) {
                    GroupCode code = new GroupCode();

                    if (g.getAttributeValue("id") != null)
                        code.setId(Integer.parseInt(g.getAttributeValue("id")));

                    if (g.getAttributeValue("name") != null)
                        code.setName(g.getAttributeValue("name"));

                    if (g.getAttributeValue("description") != null)
                        code.setDescription(g.getAttributeValue("description"));

                    String[] manufIds;
                    if (g.getAttributeValue("manufacturerIds") != null) {
                        manufIds = TextUtils.split(
                                g.getAttributeValue("manufacturerIds"), ",");

                        for (String manufId : manufIds)
                            code.getManufacturerIds().add(
                                    Integer.parseInt(manufId));
                    }
                    codesTmp.add(code);

                    Log.d("Added Group Code " + code.getId() + " with name " + code.getName());
                    iterator++;
                }
                pricebookDatabase.getGroupCodes().addAll(codesTmp);
            }
        Log.d("Total notes amount: " + iterator);
    }

    public void closeDataBases() {
        SkedsApplication.getInstance().savePriceBookDbToFile();
    }

}
