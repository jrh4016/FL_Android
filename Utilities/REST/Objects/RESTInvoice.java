package com.skeds.android.phone.business.Utilities.REST.Objects;

import android.text.TextUtils;

import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Customer;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Invoice;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.LineItem;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.ParticipantType;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Payment;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.PieceOfEquipment;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.ServiceProvider;
import com.skeds.android.phone.business.Utilities.General.UserUtilitiesSingleton;
import com.skeds.android.phone.business.Utilities.NonfatalException;
import com.skeds.android.phone.business.Utilities.REST.RestConnector;

import org.jdom2.Document;
import org.jdom2.Element;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

public class RESTInvoice {

    /* The invoice being viewed */
    public static void query(int id) throws NonfatalException {
        Document document = RestConnector.getInstance()
                .httpGet("getinvoice/" + id);
        // if (serverResponse.contains("<getInvoiceResponse>")) {
        // rootNode = document.getRootElement().getChild("appointment");
        // rootNode = document.getRootElement();
        parseInvoice(document.getRootElement().getChild("invoice"));
    }

    /* The invoice being viewed */
    public static void parseInvoice(Element invoiceElement)
            throws NonfatalException {
        /*
         * Start of the "invoice" tag
		 */
        Invoice tempInvoice = new Invoice();

        if (invoiceElement.getAttributeValue("id") != null)
            tempInvoice.setId(Integer.parseInt(invoiceElement
                    .getAttributeValue("id")));

        if (invoiceElement.getAttributeValue("invoiceDate") != null)
            tempInvoice
                    .setDate(invoiceElement.getAttributeValue("invoiceDate"));

        if (invoiceElement.getAttributeValue("locationEmail") != null)
            tempInvoice
                    .setLocationEmail(invoiceElement.getAttributeValue("locationEmail"));

        if (invoiceElement.getAttributeValue("appointmentId") != null)
            tempInvoice.setAppointmentId(Integer.parseInt(invoiceElement
                    .getAttributeValue("appointmentId")));

        if (invoiceElement.getAttributeValue("appointmentStatus") != null)
            tempInvoice.setAppointmentStatus(invoiceElement.getAttributeValue("appointmentStatus"));

        if (invoiceElement.getAttributeValue("discountAmount") != null)
            tempInvoice.setDiscount(new BigDecimal(
                    invoiceElement.getAttributeValue("discountAmount"))
                    .setScale(2, BigDecimal.ROUND_HALF_UP));
        if (invoiceElement.getAttributeValue("taxRate") != null)
            if (!invoiceElement.getAttributeValue("taxRate").equals("null"))
                tempInvoice.setTaxRate(new BigDecimal(
                        invoiceElement.getAttributeValue("taxRate")));
        if (invoiceElement.getAttributeValue("taxAmount") != null)
            tempInvoice.setTax(new BigDecimal(
                    invoiceElement.getAttributeValue("taxAmount")));
        if (invoiceElement.getAttributeValue("invoiceNumber") != null)
            tempInvoice.setNumber(invoiceElement
                    .getAttributeValue("invoiceNumber"));
        if (invoiceElement.getAttributeValue("invoiceAppointmentType") != null)
            tempInvoice.setAppointmentType(invoiceElement
                    .getAttributeValue("invoiceAppointmentType"));
        if (invoiceElement.getAttributeValue("determinePaymentType") != null)
            tempInvoice.setDeterminePaymentType(Boolean
                    .parseBoolean(invoiceElement
                            .getAttributeValue("determinePaymentType")));
        if (invoiceElement.getAttributeValue("allowBillLater") != null)
            tempInvoice.setAllowBillLater(Boolean.parseBoolean(invoiceElement
                    .getAttributeValue("allowBillLater")));
        if (invoiceElement.getAttributeValue("allowPrepaid") != null)
            tempInvoice.setAllowPrepaid(Boolean.parseBoolean(invoiceElement
                    .getAttributeValue("allowPrepaid")));
        if (invoiceElement.getAttributeValue("acceptCreditCards") != null)
            tempInvoice.setAcceptCreditCards(Boolean
                    .parseBoolean(invoiceElement
                            .getAttributeValue("acceptCreditCards")));
        if (invoiceElement.getAttributeValue("forceSignatureOnInvoice") != null)
            tempInvoice.setForceSignatureOnInvoice(Boolean
                    .parseBoolean(invoiceElement
                            .getAttributeValue("forceSignatureOnInvoice")));
        if (invoiceElement.getAttributeValue("applySalesTaxToDiscount") != null)
            AppDataSingleton.getInstance().setCalculateSalesTaxFirst(
                    Boolean.parseBoolean(invoiceElement
                            .getAttributeValue("applySalesTaxToDiscount")));
        if (invoiceElement.getAttributeValue("useCreditCardReader") != null)
            UserUtilitiesSingleton.getInstance().user.setUseCardReader(Boolean
                    .parseBoolean(invoiceElement
                            .getAttributeValue("useCreditCardReader")));
        if (invoiceElement.getAttributeValue("displayDisclaimer") != null)
            AppDataSingleton.getInstance().setForceDisclaimer(
                    Boolean.parseBoolean(invoiceElement
                            .getAttributeValue("displayDisclaimer")));
        else
            AppDataSingleton.getInstance().setForceDisclaimer(false);

        if (invoiceElement.getAttributeValue("allowOnlyBillLater") != null)
            UserUtilitiesSingleton.getInstance().user
                    .setUseOnlyBillLater(Boolean.parseBoolean(invoiceElement
                            .getAttributeValue("allowOnlyBillLater")));

        if (invoiceElement.getAttributeValue("everybodyElseFinished") != null)
            AppDataSingleton.getInstance().setAllTechniciansFinished(
                    Boolean.parseBoolean(invoiceElement
                            .getAttributeValue("everybodyElseFinished")));

        if (invoiceElement.getAttributeValue("meFinished") != null)
            tempInvoice.setMeFinished(Boolean.parseBoolean(invoiceElement
                    .getAttributeValue("everybodyElseFinished")));

        if (invoiceElement.getAttributeValue("othersFinished") != null)
            tempInvoice.setOthersFinished(Boolean.parseBoolean(invoiceElement
                    .getAttributeValue("othersFinished")));

        if (invoiceElement.getAttributeValue("amountDue") != null) {

            BigDecimal amountDue = new BigDecimal(
                    invoiceElement.getAttributeValue("amountDue")).setScale(2,
                    BigDecimal.ROUND_HALF_UP);
            tempInvoice.setAmountDue(amountDue);
            // TODO - Better calculation for prices

        }

        if (invoiceElement.getAttributeValue("country") != null) {
            String type = invoiceElement.getAttributeValue("country");

            if (type.toLowerCase().equals("can")) {
                UserUtilitiesSingleton.getInstance().user.setCanadian(true);
            } else {
                UserUtilitiesSingleton.getInstance().user.setCanadian(false);
            }
        }

        if (invoiceElement.getChildText("invoiceDescription") != null)
            tempInvoice.setDescription(invoiceElement
                    .getChildText("invoiceDescription"));
        else
            tempInvoice.setDescription("");

        if (invoiceElement.getChildText("invoiceRecommendation") != null)
            tempInvoice.setRecommendation(invoiceElement
                    .getChildText("invoiceRecommendation"));
        else
            tempInvoice.setRecommendation("");

        // EQUIPMENTS USED IN APPOINTMENT

        tempInvoice.getEquipmentList().clear();

        if (invoiceElement.getChild("piecesOfequipment") != null) {
            List<Element> equipmentListNode = invoiceElement.getChild(
                    "piecesOfequipment").getChildren("equipment");


            for (Element el : equipmentListNode) {
                PieceOfEquipment pieceOfEquipment = new PieceOfEquipment();
                // ID
                if (el.getAttributeValue("id") != null)
                    pieceOfEquipment.setId(Integer.parseInt(el
                            .getAttributeValue("id")));

                // Name
                if (el.getChild("name") != null)
                    pieceOfEquipment.setName(el.getChild("name")
                            .getText());

                // Model Number
                if (el.getChild("modelNumber") != null)
                    pieceOfEquipment.setModelNumber(el.getChild(
                            "modelNumber").getText());

                // Serial Number
                if (el.getChild("serialNumber") != null)
                    pieceOfEquipment.setSerialNumber(el.getChild(
                            "serialNumber").getText());

                // Manufacturer Id
                if (el.getChild("manufacturer") != null)
                    pieceOfEquipment.setManufacturerId(Integer
                            .parseInt(el.getChild("manufacturer")
                                    .getAttributeValue("id")));

                // Manufacturer Name
                if (el.getChild("manufacturer") != null)
                    pieceOfEquipment.setManufacturer(el.getChild(
                            "manufacturer").getText());

                tempInvoice.getEquipmentList().add(pieceOfEquipment);
            }

        }

		
		/*
         * Start of the "available credit cards" tag
		 */
        if (invoiceElement.getChild("availableCreditCardOptions") != null) {
            List<Element> cardTypeList = invoiceElement.getChild(
                    "availableCreditCardOptions").getChildren();

            if (!cardTypeList.isEmpty()) {
                for (int i = 0; i < cardTypeList.size(); i++) {
                    Element cardOptionNode = (Element) cardTypeList.get(i);

                    if (cardOptionNode.getText() != null) {
                        if (cardOptionNode.getText().toString()
                                .equals("American Express"))
                            tempInvoice.setAcceptAmericanExpress(true);
                        else if (cardOptionNode.getText().toString()
                                .equals("Discover"))
                            tempInvoice.setAcceptDiscover(true);
                        else if (cardOptionNode.getText().toString()
                                .equals("Visa"))
                            tempInvoice.setAcceptVisa(true);
                        else if (cardOptionNode.getText().toString()
                                .equals("MasterCard"))
                            tempInvoice.setAcceptMasterCard(true);
                    }
                }
            }
        }

		/*
		 * Start of the Payments tag
		 */

        tempInvoice.getPaymentsList().clear();

        if (invoiceElement.getChild("payments") != null) {

            Element paymentsElement = invoiceElement.getChild("payments");

            List<Element> paymentsList = paymentsElement.getChildren("payment");

            if (!paymentsList.isEmpty()) {
                for (int i = 0; i < paymentsList.size(); i++) {
                    Element paymentElement = (Element) paymentsList.get(i);
                    tempInvoice.getPaymentsList().add(new Payment());

                    if (paymentElement.getAttributeValue("id") != null)
                        tempInvoice.getPaymentsList().get(i).setCheckNumber(
                                Integer.parseInt(paymentElement
                                        .getAttributeValue("id")));

                    if (paymentElement.getAttributeValue("amount") != null)
                        tempInvoice.getPaymentsList().get(i).paymentAmount = new BigDecimal(
                                paymentElement.getAttributeValue("amount"));

                    if (paymentElement.getAttributeValue("paymentType") != null)
                        tempInvoice.getPaymentsList()
                                .get(i)
                                .setPaymentType(
                                        paymentElement
                                                .getAttributeValue("paymentType"));

                    if (paymentElement.getAttributeValue("paymentDate") != null)
                        tempInvoice.getPaymentsList().get(i).date = paymentElement
                                .getAttributeValue("paymentDate");
                }
            }
        }

		/*
		 * Start of the Service Provider tag
		 */
        tempInvoice.getServiceProviderList().clear();
        final List<Element> serviceProvidersElement = invoiceElement.getChildren("serviceProviders");

        if (serviceProvidersElement != null && !serviceProvidersElement.isEmpty()) {
            final List<Element> serviceProvidersList = serviceProvidersElement.get(0).getChildren("serviceProvider");
            if (serviceProvidersList != null) {
                for (int i = 0; i < serviceProvidersElement.size(); i++) {

                    final Element serviceProviderElement = serviceProvidersList.get(i);

                    tempInvoice.getServiceProviderList().add(new ServiceProvider());
                    if (serviceProviderElement.getAttributeValue("id") != null)
                        tempInvoice.getServiceProviderList().get(i).setId(
                                Integer.parseInt(serviceProviderElement
                                        .getAttributeValue("id")));
                    if (serviceProviderElement
                            .getChildText("serviceProviderFirstName") != null)
                        tempInvoice.getServiceProviderList().get(i).setName(
                                serviceProviderElement
                                        .getChildText("serviceProviderFirstName"));

                    if (serviceProviderElement
                            .getChildText("serviceProviderLastName") != null) {
                        tempInvoice.getServiceProviderList()
                                .get(i)
                                .setName(
                                        tempInvoice.getServiceProviderList().get(i)
                                                .getName()
                                                + " "
                                                + serviceProviderElement
                                                .getChildText("serviceProviderLastName"));
                    }

                    tempInvoice.getServiceProviderList().get(i).participant = new ParticipantType();
                    if (serviceProviderElement.getChild("email") != null)
                        tempInvoice.getServiceProviderList().get(i).setEmail(
                                serviceProviderElement.getChildText("email"));
                }
            }
        }

		/*
		 * Start of the "customer" tag
		 */
        tempInvoice.setCustomer(new Customer());
        Element customerElement = invoiceElement.getChild("customer");

        if (customerElement.getAttributeValue("id") != null)
            tempInvoice.getCustomer().setId(Integer.parseInt(customerElement
                    .getAttributeValue("id")));
        if (customerElement.getAttributeValue("type") != null)
            tempInvoice.getCustomer().setType(customerElement
                    .getAttributeValue("type"));

        // First, Last, Phone, Email, Address
        if (customerElement.getChildText("customerFirstName") != null)
            tempInvoice.getCustomer().setFirstName(customerElement
                    .getChildText("customerFirstName"));
        if (customerElement.getChildText("customerLastName") != null)
            tempInvoice.getCustomer().setLastName(customerElement
                    .getChildText("customerLastName"));
        if (customerElement.getChildText("orgName") != null)
            tempInvoice.getCustomer().setOrganizationName(customerElement
                    .getChildText("orgName"));

        tempInvoice.getCustomer().setTaxable(Boolean.valueOf(customerElement.getAttributeValue("taxable")));

        final Element customerLocationNode = customerElement.getChild("customerLocation");
        if (customerLocationNode != null) {
            tempInvoice.setLocationId(customerLocationNode.getAttributeValue("id"));
            tempInvoice.getCustomer().setLocationTaxable(Boolean.valueOf(customerLocationNode.getAttributeValue("taxable")));
        }

        tempInvoice.getCustomer().email.clear(); // Empty this
        if (customerElement.getChildText("email") != null) {
            tempInvoice.getCustomer().email.add(customerElement
                    .getChildText("email"));

            if (customerElement.getChild("email").getAttributeValue(
                    "description") != null)
                tempInvoice.getCustomer().emailDescription.add(customerElement
                        .getChild("email").getAttributeValue("description"));
            else
                tempInvoice.getCustomer().emailDescription.add(null);
        }

        int i = 2;
        while (true) {
            if (customerElement.getChildText("email" + i) != null) {
                tempInvoice.getCustomer().email.add(customerElement
                        .getChildText("email" + i));

                if (customerElement.getChild("email" + i).getAttributeValue(
                        "description") != null) {
                    tempInvoice.getCustomer().emailDescription.add(customerElement
                            .getChild("email" + i).getAttributeValue(
                                    "description"));
                } else
                    tempInvoice.getCustomer().emailDescription.add(null);

                i++;
            } else {
                break;
            }
        }

        i = 1;
        tempInvoice.getCustomer().phone.clear(); // Empty this
        while (true) {
            if (customerElement.getChildText("phone" + i) != null) {
                if (customerElement.getChildText("phone" + i) != null)
                    tempInvoice.getCustomer().phone.add(customerElement
                            .getChildText("phone" + i));

                if (customerElement.getChild("phone" + i).getAttributeValue(
                        "type") != null)
                    tempInvoice.getCustomer().phoneType.add(customerElement
                            .getChild("phone" + i).getAttributeValue("type"));

                if (customerElement.getChild("phone" + i).getAttributeValue(
                        "description") != null)
                    tempInvoice.getCustomer().phoneDescription.add(customerElement
                            .getChild("phone" + i).getAttributeValue(
                                    "description"));
                else
                    tempInvoice.getCustomer().phoneDescription.add(null);

                i++;
            } else {
                break;
            }
        }

        if (customerElement.getChildText("address1") != null)
            tempInvoice.getCustomer().setAddress1(customerElement
                    .getChildText("address1"));
        if (customerElement.getChildText("address2") != null)
            tempInvoice.getCustomer().setAddress2(customerElement
                    .getChildText("address2"));
        if (customerElement.getChildText("city") != null)
            tempInvoice.getCustomer().setAddressCity(customerElement
                    .getChildText("city"));
        if (customerElement.getChildText("state") != null)
            tempInvoice.getCustomer().setAddressState(customerElement
                    .getChildText("state"));
        if (customerElement.getChildText("zip") != null)
            tempInvoice.getCustomer().setAddressPostalCode(customerElement
                    .getChildText("zip"));
        if (customerElement.getChildText("customerCountry") != null)
            tempInvoice.getCustomer().setAddressCountry(customerElement
                    .getChildText("customerCountry"));
		
		

		/*
		 * Start the tags for lineItems
		 */
        tempInvoice.getLineItems().clear();
        List<Element> lineItemsElement = invoiceElement
                .getChildren("lineItems");

        if (!lineItemsElement.isEmpty()) {

            Element lineItemsNode = (Element) lineItemsElement.get(0);
            List<Element> lineItemsList = lineItemsNode.getChildren("lineItem");

            // CommonUtilities.mSingleInvoice.lineItem = null;
            // CommonUtilities.mSingleInvoice.lineItem = new
            // LineItem[lineItemsList
            // .size()];

            for (Element lineItemElement : lineItemsList) {

                // CommonUtilities.mSingleInvoice.lineItem[i] = new
                // LineItem();

                if (lineItemElement.getAttributeValue("active") != null) {
//                    newLineItem.setActive(
//                            Boolean.getBoolean(lineItemElement
//                                    .getAttributeValue("active")));
                    //skip if Active == false
                    if (lineItemElement.getAttributeValue("active").equals("false"))
                        continue;

                }

                LineItem newLineItem = new LineItem();
                tempInvoice.getLineItems().add(newLineItem);

                if (lineItemElement.getAttributeValue("active") != null)
                    newLineItem.setActive(
                            Boolean.parseBoolean(lineItemElement
                                    .getAttributeValue("active")));


                if (lineItemElement.getAttributeValue("id") != null)
                    newLineItem.setId(
                            Integer.parseInt(lineItemElement
                                    .getAttributeValue("id")));

                if (lineItemElement.getChild("taxes") != null) {
                    if (lineItemElement.getChild("taxes").getChildren("tax") != null) {
                        List<Element> taxes = lineItemElement.getChild("taxes").getChildren("tax");
                        int index = 0;
                        for (Element tx : taxes) {
                            if (index < newLineItem.rateIds.size())
                                newLineItem.rateIds.set(index++, Long.parseLong(tx.getAttributeValue("rateId")));
                        }
                    }
                }

                if (lineItemElement.getAttributeValue("lineItemServiceTypeId") != null)
                    newLineItem
                            .setServiceTypeId(
                                    Integer.parseInt(lineItemElement
                                            .getAttributeValue("lineItemServiceTypeId")));

                // CommonUtilities.mSingleInvoice.lineItem[i].setId(Integer
                // .parseInt(lineItemElement
                // .getAttributeValue("id")));
                if (lineItemElement.getAttributeValue("quantity") != null)
                    newLineItem.setQuantity(new BigDecimal(lineItemElement
                            .getAttributeValue("quantity")));

                if (lineItemElement.getAttributeValue("taxable") != null)
                    newLineItem.setTaxable(
                            Boolean.parseBoolean(lineItemElement
                                    .getAttributeValue("taxable")));

                if (lineItemElement.getAttributeValue("custom") != null)
                    newLineItem.setCustomLineItem(
                            Boolean.parseBoolean(lineItemElement
                                    .getAttributeValue("custom")));

                // CommonUtilities.mSingleInvoice.lineItem[i]
                // .setQuantity(Double.parseDouble(lineItemElement
                // .getAttributeValue("quantity")));
                if (lineItemElement.getAttributeValue("lineItemDescription") != null)
                    newLineItem.setName(
                            lineItemElement
                                    .getAttributeValue("lineItemDescription"));

                if (lineItemElement
                        .getAttributeValue("lineItemRealDescription") != null)
                    newLineItem
                            .setDescription(
                                    lineItemElement
                                            .getAttributeValue("lineItemRealDescription"));

                // CommonUtilities.mSingleInvoice.lineItem[i]
                // .setName(lineItemElement
                // .getAttributeValue("lineItemDescription"));
                if (lineItemElement.getAttributeValue("lineItemPrice") != null)
                    newLineItem.cost = new BigDecimal(
                            lineItemElement.getAttributeValue("lineItemPrice"));

                // .setCost(
                // CommonUtilities.roundDouble(
                // Double.parseDouble(lineItemElement
                // .getAttributeValue("lineItemPrice")),
                // 2, BigDecimal.ROUND_HALF_UP));
                // CommonUtilities.mSingleInvoice.lineItem[i].setCost(Double
                // .parseDouble(lineItemElement
                // .getAttributeValue("lineItemPrice")));
                if (lineItemElement.getAttributeValue("lineItemExtendedPrice") != null)
                    newLineItem.finalCost = new BigDecimal(
                            lineItemElement
                                    .getAttributeValue("lineItemExtendedPrice"));

                // .setFinalCost(
                // CommonUtilities.roundDouble(
                // Double.parseDouble(lineItemElement
                // .getAttributeValue("lineItemExtendedPrice")),
                // 2, BigDecimal.ROUND_HALF_UP));
                // CommonUtilities.mSingleInvoice.lineItem[i]
                // .setFinalCost(Double.parseDouble(lineItemElement
                // .getAttributeValue("lineItemExtendedPrice")));
                if (lineItemElement.getAttributeValue("removable") != null)
                    newLineItem.setRemovable(
                            Boolean.parseBoolean(lineItemElement
                                    .getAttributeValue("removable")));

                if (lineItemElement.getAttributeValue("removable") != null)
                    newLineItem.setRemovable(
                            Boolean.parseBoolean(lineItemElement
                                    .getAttributeValue("removable")));

                // CommonUtilities.mSingleInvoice.lineItem[i]
                // .setRemovable(Boolean.parseBoolean(lineItemElement
                // .getAttributeValue("removable")));
                if (lineItemElement.getAttributeValue("labor") != null)
                    newLineItem.setLabor(
                            Boolean.parseBoolean(lineItemElement
                                    .getAttributeValue("labor")));
                // CommonUtilities.mSingleInvoice.lineItem[i]
                // .setTaxable(Boolean
                // .parseBoolean(lineItemElement
                // .getAttributeValue("taxable")));
            }
        }

        tempInvoice.setAllLineItems(new ArrayList<LineItem>(tempInvoice.getLineItems()));
        for (Iterator<LineItem> iterator = tempInvoice.getLineItems().iterator(); iterator.hasNext(); ) {
            LineItem lineitem = iterator.next();
            if (!lineitem.isActive())
                iterator.remove();
        }

        tempInvoice.getPayments().clear();// Clear this out
        AppDataSingleton.getInstance().setInvoice(tempInvoice);
    }

    private static Element addElement(Element rootNode, String tagname,
                                      String content) {
        if (content != null && content.length() > 0)
            rootNode.addContent(new Element(tagname).setText(content));
        return rootNode;
    }

    private static Document invoice2xml(Invoice invoice, int appointmentId) {
        Element rootNode = new Element("invoice");

        if (invoice.getId() > 0)
            rootNode.setAttribute("id", String.valueOf(invoice.getId()));

        if (invoice.getId() <= 0 && appointmentId < 0)
            appointmentId = AppDataSingleton.getInstance().getAppointment()
                    .getId();

        addElement(rootNode, "invoiceDescription", invoice.getDescription());
        addElement(rootNode, "invoiceRecommendation",
                invoice.getRecommendation());
        addElement(rootNode, "customerEmail", invoice.getCustomerEmail());
        addElement(rootNode, "invoiceDiscount", invoice.getDiscount().toString());
        addElement(rootNode, "invoiceDate", invoice.getDate().toString());
        addElement(rootNode, "tz", TimeZone.getDefault().getID());

        if (!invoice.getNumber().equals("-1"))
            addElement(rootNode, "invoiceNumber", invoice.getNumber());

        if (appointmentId > 0)
            rootNode.addContent(new Element("appointmentId").setText(""
                    + appointmentId));

        if (invoice.getForceSignatureOnInvoice())
            addElement(rootNode, "invoiceSignature", invoice.getSignature());

        if (UserUtilitiesSingleton.getInstance().user
                .isRequireSignerNameOnInvoice())
            addElement(rootNode, "invoiceSigner", invoice.getSigner());

        if (invoice.getDeterminePaymentType() && !invoice.getPayments().isEmpty()) {
            for (Payment pm : invoice.getPayments()) {
                Element paymentNode = new Element("payment");
                Element paymentTypeNode = new Element("paymentType");
                Element paymentExtraInfoNode = null;

                if ("CREDIT_CARD".equals(pm.getPaymentType())) {
                    paymentTypeNode.setText("CREDIT_CARD");

                    if (!TextUtils.isEmpty(pm.getCardShortNumber()))
                        paymentNode.addContent(new Element(
                                "creditCardShortNumber").setText(pm
                                .getCardShortNumber()));

                    if (!TextUtils.isEmpty(pm.getCardExpirationDate()))
                        paymentNode.addContent(new Element(
                                "creditCardExpirationDate").setText(pm
                                .getCardExpirationDate()));

                    if (!TextUtils.isEmpty(pm.getCardAuthCode()))
                        paymentNode
                                .addContent(new Element("creditCardAuthCode")
                                        .setText(pm.getCardAuthCode()));

                    switch (pm.getCheckNumber()) {
                        case 0: // Default
                            paymentExtraInfoNode = new Element("alreadyPaid")
                                    .setText("true");
                            break;
                        case 1:
                            paymentExtraInfoNode = new Element("creditCardType")
                                    .setText("AMEX");
                            break;

                        case 2:
                            paymentExtraInfoNode = new Element("creditCardType")
                                    .setText("DISCOVER");
                            break;

                        case 3:
                            paymentExtraInfoNode = new Element("creditCardType")
                                    .setText("MASTERCARD");
                            break;

                        case 4:
                            paymentExtraInfoNode = new Element("creditCardType")
                                    .setText("VISA");
                            break;
                        default:
                            // Nothing
                            break;
                    }
                } else if ("CHECK".equals(pm.getPaymentType())) {
                    paymentTypeNode.setText("CHECK");
                    paymentExtraInfoNode = new Element("checkNumber")
                            .setText(String.valueOf(pm.getCheckNumber()));
                } else {
					/* Sets the payment type name */
                    paymentTypeNode.setText(pm.getPaymentType());
                }

                paymentNode.addContent(paymentTypeNode);
                if (paymentExtraInfoNode != null)
                    paymentNode.addContent(paymentExtraInfoNode);
                paymentNode.addContent(new Element("amount")
                        .setText(pm.paymentAmount.toPlainString()));
                rootNode.addContent(paymentNode);
            }
        }


//Calculate difference between all line items (beginning) and modified lineitems
        for (LineItem li : invoice.getLineItems()) {

            Element lineItemElement = new Element("lineItem");


            lineItemElement.setAttribute("active", String.valueOf(li.isActive()));

            lineItemElement.setAttribute("taxable", String.valueOf(li.getTaxable()));
            if (!li.rateIds.isEmpty()) {
                Element taxes = new Element("taxes");
                for (Long id : li.rateIds) {
                    Element tax = new Element("tax");
                    tax.setAttribute("rateId", id + "");
                    if (id > 0)
                        taxes.addContent(tax);
                }
                lineItemElement.addContent(taxes);
            }

            if (li.isUsingAdditionalCost())
                lineItemElement.setAttribute("additional", "true");

            if (li.getServiceTypeId() != 0)
                lineItemElement.setAttribute("lineItemServiceTypeId", String.valueOf(li.getServiceTypeId()));
            if (li.getId() != 0) {
                if (li.getId() != li.getServiceTypeId())
                    lineItemElement.setAttribute("id", String.valueOf(li.getId()));
            }

//            if (li.isCustomLineItem()) {

            lineItemElement.setAttribute("lineItemPrice",
                    li.cost.toString());
            lineItemElement.setAttribute("lineItemDescription",
                    li.getName());
            lineItemElement.setAttribute("quantity",
                    String.valueOf(li.getQuantity()));
            lineItemElement.setAttribute("taxable",
                    String.valueOf(li.getTaxable()));


            if (li.isCustomPrice()) {
                lineItemElement.setAttribute("newCost", "true");
                lineItemElement.setAttribute("cost", li.cost.toString());
            }
            lineItemElement.setAttribute("lineItemRealDescription",
                    li.getDescription());
//            } else {
//                lineItemElement.setAttribute("quantity",
//                        String.valueOf(li.getQuantity()));
//            }
            rootNode.addContent(lineItemElement);
        }
        return new Document(rootNode);
    }

    public static void update(Invoice invoice) throws NonfatalException {
        addOrUpdate(invoice, -1, -1);
    }

    public static void addOrUpdate(Invoice invoice, int customerId,
                                   int appointmentId) throws NonfatalException {
        RestConnector.getInstance().httpPostCheckSuccess(
                invoice2xml(invoice, appointmentId),
                invoice.getId() > 0 ? "editinvoicedetails/" + invoice.getId()
                        : "addinvoice/"
                        + AppDataSingleton.getInstance().getCustomer()
                        .getId());
    }

    /**
     * Sends Invoice via E-Mail to customer
     *
     * @param invoiceId
     * @param customerEmail
     * @return
     * @throws NonfatalException
     */
    public static void sendToCustomer(int invoiceId, String customerEmail)
            throws NonfatalException {
        Element rootNode = new Element("sendInvoiceToCustomerRequest");

        Element customerEmailNode = new Element("customerEmail");
        customerEmailNode.setText(customerEmail);

        if (!customerEmail.equals(""))
            rootNode.addContent(customerEmailNode);
        RestConnector.getInstance().httpPostCheckSuccess(new Document(rootNode),
                "sendinvoicetocustomer/" + invoiceId);
    }
}