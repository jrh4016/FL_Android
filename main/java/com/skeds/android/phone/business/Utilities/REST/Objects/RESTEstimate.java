package com.skeds.android.phone.business.Utilities.REST.Objects;

import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Estimate;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.LineItem;
import com.skeds.android.phone.business.Utilities.NonfatalException;
import com.skeds.android.phone.business.Utilities.REST.RestConnector;

import org.jdom2.Document;
import org.jdom2.Element;

import java.math.BigDecimal;
import java.util.List;

public class RESTEstimate {

    /* Single customer estimate */
    public static void query(int estimateId) throws NonfatalException {
        Document document = RestConnector.getInstance().httpGet("getestimate/" + estimateId);
        Element elem;
        Element estimateNode = document.getRootElement().getChild("estimate");

        if (estimateNode == null)
            return;
        String signature = AppDataSingleton.getInstance().getEstimate().getSignature();
        Estimate estimate = new Estimate();
        AppDataSingleton.getInstance().setEstimate(estimate);
        AppDataSingleton.getInstance().getEstimate().setSignature(signature);

        if (estimateNode.getAttributeValue("id") != null)
            AppDataSingleton.getInstance().getEstimate().setId(Integer.parseInt(estimateNode.getAttributeValue("id")));

        if (estimateNode.getAttributeValue("estimateDate") != null)
            AppDataSingleton.getInstance().getEstimate().setDate(estimateNode.getAttributeValue("estimateDate"));

        if (estimateNode.getAttributeValue("appointmentId") != null)
            AppDataSingleton.getInstance().getEstimate().setAppointmentId(Integer.parseInt(estimateNode.getAttributeValue("appointmentId")));

        if (estimateNode.getAttributeValue("applySalesTaxToDiscount") != null)
            AppDataSingleton.getInstance().setCalculateSalesTaxFirst(Boolean.parseBoolean(estimateNode.getAttributeValue("applySalesTaxToDiscount")));

        if (estimateNode.getAttributeValue("showAgreementComparison") != null)
            AppDataSingleton.getInstance().getEstimate()
                    .setAgreementComparison(Boolean.parseBoolean(estimateNode.getAttributeValue("showAgreementComparison")));

        if (estimateNode.getAttributeValue("discountAmount") != null)
            AppDataSingleton.getInstance().getEstimate().setDiscount(new BigDecimal(estimateNode.getAttributeValue("discountAmount")));

        if (estimateNode.getChild("estimateDescription") != null)
            AppDataSingleton.getInstance().getEstimate().setDescription(estimateNode.getChild("estimateDescription").getText());

        elem = estimateNode.getChild("customer");
        // load customer data
        if (elem != null) {
            final boolean isTaxable = Boolean.valueOf(elem.getAttributeValue("taxable"));
            AppDataSingleton.getInstance().getEstimate().setCustomerId(elem.getAttributeValue("id"));
            AppDataSingleton.getInstance().getEstimate().getCustomer().setTaxable(isTaxable);
            AppDataSingleton.getInstance().getEstimate().setTaxable(isTaxable);

            elem = elem.getChild("customerLocation");
            if (elem != null) {
                final boolean isLocationTaxable = Boolean.valueOf(elem.getAttributeValue("taxable"));
                AppDataSingleton.getInstance().getEstimate().setLocationId(elem.getAttributeValue("id"));
                AppDataSingleton.getInstance().getEstimate().getCustomer().setLocationTaxable(isLocationTaxable);
                AppDataSingleton.getInstance().getEstimate().setLocationTaxable(isLocationTaxable);
                AppDataSingleton.getInstance().getCustomer().setAddress1(elem.getValue());
            }
        }

        if (estimateNode.getChild("servicePlan") != null) {
            int serviceId = Integer.parseInt(estimateNode.getChild("servicePlan").getAttributeValue("id"));
            if (serviceId != -1)
                AppDataSingleton.getInstance().getEstimate().setServicePlanId(serviceId);

            if (estimateNode.getChild("servicePlan").getChild("name") != null)
                AppDataSingleton.getInstance().getEstimate().setServicePlanName(estimateNode.getChild("servicePlan").getChild("name").getText());

            if (estimateNode.getChild("servicePlan").getChild("serviceAgreementSavedAmount") != null)
                AppDataSingleton.getInstance().getEstimate().serviceAgreementSavedAmount = new BigDecimal(estimateNode.getChild("servicePlan")
                        .getChild("serviceAgreementSavedAmount").getText());

            if (estimateNode.getChild("servicePlan").getAttributeValue("servicePlanUsedForPricing") != null) {
                AppDataSingleton
                        .getInstance()
                        .getEstimate()
                        .setServicePlanUsedForPricing(Boolean.parseBoolean(estimateNode.getChild("servicePlan").getAttributeValue("servicePlanUsedForPricing")));
            } else
                AppDataSingleton.getInstance().getEstimate().setServicePlanUsedForPricing(false);
        }

        AppDataSingleton.getInstance().getEstimate().getLineItems().clear();
        if (estimateNode.getChild("lineItems") != null) {
            if (!estimateNode.getChild("lineItems").getChildren().isEmpty()) {
                List<Element> lineItemsList = estimateNode.getChild("lineItems").getChildren();

                for (int i = 0; i < lineItemsList.size(); i++) {
                    Element lineItemNode = (Element) lineItemsList.get(i);

                    AppDataSingleton.getInstance().getEstimate().getLineItems().add(new LineItem());

                    if (lineItemNode != null) {
                        if (lineItemNode.getAttributeValue("id") != null)
                            AppDataSingleton.getInstance().getEstimate().getLineItems().get(i).setId(Integer.parseInt(lineItemNode.getAttributeValue("id")));

                        if (lineItemNode.getAttributeValue("quantity") != null)
                            AppDataSingleton.getInstance().getEstimate().getLineItems().get(i).setQuantity(new BigDecimal(lineItemNode.getAttributeValue("quantity")));

                        if (lineItemNode.getAttributeValue("lineItemDescription") != null)
                            AppDataSingleton.getInstance().getEstimate().getLineItems().get(i).setName(lineItemNode.getAttributeValue("lineItemDescription"));

                        if (lineItemNode.getAttributeValue("lineItemRealDescription") != null)
                            AppDataSingleton.getInstance().getEstimate().getLineItems().get(i).setDescription(
                                    lineItemNode.getAttributeValue("lineItemRealDescription"));

                        if (lineItemNode.getAttributeValue("lineItemPrice") != null)
                            AppDataSingleton.getInstance().getEstimate().getLineItems().get(i).cost = new BigDecimal(lineItemNode.getAttributeValue("lineItemPrice"));

                        if (lineItemNode.getAttributeValue("recommendation") != null)
                            AppDataSingleton.getInstance().getEstimate().getLineItems().get(i).setRecommendation(LineItem.Recommendation.valueOf(lineItemNode.getAttributeValue("recommendation")));

                        if (lineItemNode.getAttributeValue("comparisonAlternatePricing") != null)
                            AppDataSingleton.getInstance().getEstimate().getLineItems().get(i).comparisonCost = new BigDecimal(
                                    lineItemNode.getAttributeValue("comparisonAlternatePricing"));

                        if (lineItemNode.getChild("taxes") != null) {
                            if (lineItemNode.getChild("taxes").getChildren("tax") != null) {
                                List<Element> taxes = lineItemNode.getChild("taxes").getChildren("tax");
                                int index = 0;
                                for (Element tx : taxes)
                                    if (index < AppDataSingleton.getInstance().getEstimate().getLineItems().get(i).rateIds.size())
                                        AppDataSingleton.getInstance().getEstimate().getLineItems().get(i).rateIds.set(index++, Long.parseLong(tx.getAttributeValue("rateId")));
                            }
                        }
                        //
                        //
                        // .setCost(
                        // CommonUtilities.roundDouble(
                        // Double.parseDouble(lineItemNode
                        // .getAttributeValue("lineItemPrice")),
                        // 2,
                        // BigDecimal.ROUND_HALF_UP));

                        if (lineItemNode.getAttributeValue("lineItemExtendedPrice") != null)
                            AppDataSingleton.getInstance().getEstimate().getLineItems().get(i).finalCost = new BigDecimal(
                                    lineItemNode.getAttributeValue("lineItemExtendedPrice"));

                        // .setFinalCost(
                        // CommonUtilities.roundDouble(
                        // Double.parseDouble(lineItemNode
                        // .getAttributeValue("lineItemExtendedPrice")),
                        // 2,
                        // BigDecimal.ROUND_HALF_UP));

                        if (lineItemNode.getAttributeValue("taxable") != null)
                            AppDataSingleton.getInstance().getEstimate().getLineItems().get(i).setTaxable(
                                    Boolean.parseBoolean(lineItemNode.getAttributeValue("taxable")));
                    }
                }
            }
        }
    }

    public static void add(int customerId, String estimateDescription, String estimateDiscount, String estimateSignature, String customerEmail,
                           int appointmentId, int servicePlanId) throws NonfatalException {
        /* Create all nodes to be used */
        Element rootNode = new Element("estimate");
        Element estimateDescriptionNode = new Element("estimateDescription");
        Element estimateDiscountNode = new Element("estimateDiscount");
        Element estimateSignatureNode = new Element("estimateSignature");
        Element primaryEmailElementNode = new Element("customerEmail");
        Element appointmentIdNode = new Element("appointmentId");

		/* Populate values for nodes */

        estimateDescriptionNode.setText(estimateDescription);
        estimateDiscountNode.setText(estimateDiscount);
        estimateSignatureNode.setText(estimateSignature);
        primaryEmailElementNode.setText(customerEmail);
        appointmentIdNode.setText(String.valueOf(appointmentId));

		/* Push nodes into root node */
        rootNode.addContent(estimateDescriptionNode);
        rootNode.addContent(estimateDiscountNode);
        rootNode.addContent(estimateSignatureNode);
        // Only add this if they included an email
        if (!customerEmail.equals(""))
            rootNode.addContent(primaryEmailElementNode);

        if (appointmentId != 0)
            rootNode.addContent(appointmentIdNode);
        if (servicePlanId > 0)
            if (AppDataSingleton.getInstance().getEstimate().isServicePlanUsedForPricing())
                rootNode.addContent(new Element("servicePlanId").setText("" + servicePlanId));

        int locationId = AppDataSingleton.getInstance().getEstimate().getLocationId();
        if (locationId > 0)
            rootNode.addContent(new Element("locationId").setText("" + locationId));

		/*
		 * This populates included line items, and pushes them into the root
		 * node
		 */

        if (AppDataSingleton.getInstance().getEstimate().getLineItems() != null) {
            for (int i = 0; i < AppDataSingleton.getInstance().getEstimate().getLineItems().size(); i++) {

                Element lineItemNode = new Element("lineItem");

                if (!AppDataSingleton.getInstance().getEstimate().getLineItems().get(i).rateIds.isEmpty()) {
                    Element taxes = new Element("taxes");
                    for (Long id : AppDataSingleton.getInstance().getEstimate().getLineItems().get(i).rateIds) {
                        Element tax = new Element("tax");
                        tax.setAttribute("rateId", id + "");
                        if (id > 0)
                            taxes.addContent(tax);
                    }
                    lineItemNode.addContent(taxes);
                }


                if (AppDataSingleton.getInstance().getEstimate().getLineItems().get(i).isUsingAdditionalCost())
                    lineItemNode.setAttribute("additional", "true");

                if (AppDataSingleton.getInstance().getEstimate().getLineItems().get(i).isUserAdded()) {
                    if (!AppDataSingleton.getInstance().getEstimate().getLineItems().get(i).isCustomLineItem())
                        if (AppDataSingleton.getInstance().getEstimate().getLineItems().get(i).getServiceTypeId() != 0)
                            lineItemNode.setAttribute("lineItemServiceTypeId",
                                    String.valueOf(AppDataSingleton.getInstance().getEstimate().getLineItems().get(i).getServiceTypeId()));

                    lineItemNode.setAttribute("lineItemPrice", String.valueOf(AppDataSingleton.getInstance().getEstimate().getLineItems().get(i).cost.toString()));
                    lineItemNode.setAttribute("lineItemDescription", AppDataSingleton.getInstance().getEstimate().getLineItems().get(i).getName());
                    lineItemNode.setAttribute("quantity", String.valueOf(AppDataSingleton.getInstance().getEstimate().getLineItems().get(i).getQuantity()));
                    lineItemNode.setAttribute("taxable", String.valueOf(AppDataSingleton.getInstance().getEstimate().getLineItems().get(i).getTaxable()));

                    // if (AppDataSingleton.getInstance().getEstimate().lineItem
                    // .get(i).isCustomPrice()) {
                    lineItemNode.setAttribute("newCost", "true");
                    lineItemNode.setAttribute("cost", String.valueOf(AppDataSingleton.getInstance().getEstimate().getLineItems().get(i).cost.toString()));
                    // }

                    lineItemNode.setAttribute("lineItemRealDescription", AppDataSingleton.getInstance().getEstimate().getLineItems().get(i).getDescription());
                } else {

                    lineItemNode.setAttribute("quantity", String.valueOf(AppDataSingleton.getInstance().getEstimate().getLineItems().get(i).getQuantity()));
                    if (AppDataSingleton.getInstance().getEstimate().getLineItems().get(i).getServiceTypeId() != 0)
                        lineItemNode.setAttribute("lineItemServiceTypeId",
                                String.valueOf(AppDataSingleton.getInstance().getEstimate().getLineItems().get(i).getServiceTypeId()));
                    else if (AppDataSingleton.getInstance().getEstimate().getLineItems().get(i).getId() != 0) {
                        lineItemNode.setAttribute("id", String.valueOf(AppDataSingleton.getInstance().getEstimate().getLineItems().get(i).getId()));
                    }

                }
                rootNode.addContent(lineItemNode);
            }
        }

		/* Hand-off to actual "POST" method to server */
        Document doc = RestConnector.getInstance().httpPostCheckSuccess(new Document(rootNode), "addestimate/" + customerId);

        Element responseNode = doc.getRootElement().getChild("response");
        if (responseNode.getAttributeValue("id") != null)
            AppDataSingleton.getInstance().getEstimate().setId(Integer.parseInt(responseNode.getAttributeValue("id")));

		/*
		 * These need to be emptied so on the next launch, they aren't stale
		 */
        // CommonUtilities.mInvoiceAddedLineItems = null;
        // CommonUtilities.mInvoiceAddedLineItemIsCustom = null;
    }

    public static void update(int estimateId, String estimateDescription, String estimateDiscount, String estimateSignature, String customerEmail,
                              int appointmentId, int servicePlanId) throws NonfatalException {
		/* Create all nodes to be used */
        Element rootNode = new Element("estimate");
        Element estimateDescriptionNode = new Element("estimateDescription");
        Element estimateDiscountNode = new Element("estimateDiscount");
        Element estimateSignatureNode = new Element("estimateSignature");
        Element primaryEmailElementNode = new Element("customerEmail");
        Element appointmentIdNode = new Element("appointmentId");

		/* Populate values for nodes */

        estimateDescriptionNode.setText(estimateDescription);
        estimateDiscountNode.setText(estimateDiscount);
        estimateSignatureNode.setText(estimateSignature);
        primaryEmailElementNode.setText(customerEmail);
        appointmentIdNode.setText(String.valueOf(appointmentId));

		/* Push nodes into root node */
        rootNode.addContent(estimateDescriptionNode);
        rootNode.addContent(estimateDiscountNode);
        rootNode.addContent(estimateSignatureNode);
        // Only add this if they included an email
        if (!customerEmail.equals(""))
            rootNode.addContent(primaryEmailElementNode);
        if (appointmentId != -1)
            rootNode.addContent(appointmentIdNode);
        if (servicePlanId > 0)
            if (AppDataSingleton.getInstance().getEstimate().isServicePlanUsedForPricing())
                rootNode.addContent(new Element("servicePlanId").setText("" + servicePlanId));

        int locationId = AppDataSingleton.getInstance().getEstimate().getLocationId();
        if (locationId > 0)
            rootNode.addContent(new Element("locationId").setText("" + locationId));

		/*
		 * This populates included line items, and pushes them into the root
		 * node
		 */
        if (AppDataSingleton.getInstance().getEstimate().getLineItems() != null) {
            for (int i = 0; i < AppDataSingleton.getInstance().getEstimate().getLineItems().size(); i++) {
                Element lineItemNode = new Element("lineItem");

                if (!AppDataSingleton.getInstance().getEstimate().getLineItems().get(i).rateIds.isEmpty()) {
                    Element taxes = new Element("taxes");
                    for (Long id : AppDataSingleton.getInstance().getEstimate().getLineItems().get(i).rateIds) {
                        Element tax = new Element("tax");
                        tax.setAttribute("rateId", id + "");
                        if (id > 0)
                            taxes.addContent(tax);
                    }
                    lineItemNode.addContent(taxes);
                }

                if (AppDataSingleton.getInstance().getEstimate().getLineItems().get(i).isUsingAdditionalCost())
                    lineItemNode.setAttribute("additional", "true");

                lineItemNode.setAttribute("recommendation", String.valueOf(AppDataSingleton.getInstance().getEstimate().getLineItems().get(i).getRecommendation()));

                lineItemNode.setAttribute("taxable", String.valueOf(AppDataSingleton.getInstance().getEstimate().getLineItems().get(i).getTaxable()));

                if (AppDataSingleton.getInstance().getEstimate().getLineItems().get(i).isUserAdded()) {
                    // Added in
                    if (!AppDataSingleton.getInstance().getEstimate().getLineItems().get(i).isCustomLineItem())
                        if (AppDataSingleton.getInstance().getEstimate().getLineItems().get(i).getServiceTypeId() != 0)
                            lineItemNode.setAttribute("lineItemServiceTypeId",
                                    String.valueOf(AppDataSingleton.getInstance().getEstimate().getLineItems().get(i).getServiceTypeId()));

                    lineItemNode.setAttribute("lineItemPrice", String.valueOf(AppDataSingleton.getInstance().getEstimate().getLineItems().get(i).cost.toString()));
                    lineItemNode.setAttribute("lineItemDescription", AppDataSingleton.getInstance().getEstimate().getLineItems().get(i).getName());
                    lineItemNode.setAttribute("quantity", String.valueOf(AppDataSingleton.getInstance().getEstimate().getLineItems().get(i).getQuantity()));



                    if (AppDataSingleton.getInstance().getEstimate().getLineItems().get(i).isCustomPrice()) {
                        lineItemNode.setAttribute("newCost", "true");
                        lineItemNode.setAttribute("cost", String.valueOf(AppDataSingleton.getInstance().getEstimate().getLineItems().get(i).cost.toString()));
                    }

                    lineItemNode.setAttribute("lineItemRealDescription", AppDataSingleton.getInstance().getEstimate().getLineItems().get(i).getDescription());

                } else {
                    lineItemNode.setAttribute("quantity", String.valueOf(AppDataSingleton.getInstance().getEstimate().getLineItems().get(i).getQuantity()));
                    if (AppDataSingleton.getInstance().getEstimate().getLineItems().get(i).getServiceTypeId() != 0)
                        lineItemNode.setAttribute("lineItemServiceTypeId",
                                String.valueOf(AppDataSingleton.getInstance().getEstimate().getLineItems().get(i).getServiceTypeId()));
                    else
                        lineItemNode.setAttribute("id", String.valueOf(AppDataSingleton.getInstance().getEstimate().getLineItems().get(i).getId()));
                }
                rootNode.addContent(lineItemNode);
            }
        }

		/* Hand-off to actual "POST" method to server */
        RestConnector.getInstance().httpPostCheckSuccess(new Document(rootNode), "editestimate/" + estimateId);

        AppDataSingleton.getInstance().getEstimate().setId(estimateId);

		/*
		 * These need to be emptied so on the next launch, they aren't stale
		 */
        // CommonUtilities.mInvoiceAddedLineItems = null;
        // CommonUtilities.mInvoiceAddedLineItemIsCustom = null;
    }

    public static void sendToCustomer(int EstimateId, String customerEmail) throws NonfatalException {
        Element rootNode = new Element("sendEstimateToCustomerRequest");

        Element customerEmailNode = new Element("customerEmail");
        customerEmailNode.setText(customerEmail);

        if (!customerEmail.equals(""))
            rootNode.addContent(customerEmailNode);
        RestConnector.getInstance().httpPostCheckSuccess(new Document(rootNode), "sendestimatetocustomer/" + EstimateId);
    }

    public static void convertToInvoice(int estimateId) throws NonfatalException {
        Document document = RestConnector.getInstance().httpGetCheckSuccess("convertestimatetoinvoice/" + estimateId);
        Element estimateNode = document.getRootElement().getChild("convertEstimateToInvoiceResponse");
    }

    public static void deleteFromAppointment(int estimateId) throws NonfatalException {
        Document document = RestConnector.getInstance().httpGetCheckSuccess("removeestimatefromappointment/" + estimateId);
        Element deleteNode = document.getRootElement().getChild("convertEstimateToInvoiceResponse");
    }

}