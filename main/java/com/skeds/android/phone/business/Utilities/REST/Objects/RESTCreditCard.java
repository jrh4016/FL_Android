package com.skeds.android.phone.business.Utilities.REST.Objects;

import android.text.TextUtils;

import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.NonfatalException;
import com.skeds.android.phone.business.Utilities.REST.RestConnector;

import org.jdom2.Document;
import org.jdom2.Element;

public class RESTCreditCard {

    public static void add(String firstName, String lastName,
                           String cardNumber, String expirationDate, String cvvNumber,
                           String address1, String address2, String city, int country,
                           String state, String zip, String amountPaid)
            throws NonfatalException {
        Element root = new Element("creditcard");

        Element creditCardFirstName = new Element("creditCardFirstName");
        if (!TextUtils.isEmpty(firstName)) {
            creditCardFirstName.setText(firstName);

            root.addContent(creditCardFirstName);
        }

        Element creditCardLastName = new Element("creditCardLastName");
        if (!TextUtils.isEmpty(lastName)) {
            creditCardLastName.setText(lastName.trim());
            root.addContent(creditCardLastName);
        }

        Element creditCardNumber = new Element("creditCardNumber");
        if (!cardNumber.equals("")) {
            creditCardNumber.setText(cardNumber);
            root.addContent(creditCardNumber);
        }

        Element creditCardExpirationDate = new Element(
                "creditCardExpirationDate");
        if (!expirationDate.equals("")) {
            creditCardExpirationDate.setText(expirationDate);
            root.addContent(creditCardExpirationDate);
        }

        Element creditCardCVV = new Element("creditCardCVV");
        if (!cvvNumber.equals("")) {
            creditCardCVV.setText(cvvNumber);
            root.addContent(creditCardCVV);
        }

        Element creditCardAddressLine1 = new Element("creditCardAddress1");
        if (!address1.equals("")) {
            creditCardAddressLine1.setText(address1);
            root.addContent(creditCardAddressLine1);
        }

        Element creditCardAddressLine2 = new Element("creditCardAddress2");
        if (!address2.equals("")) {
            creditCardAddressLine2.setText(address2);
            root.addContent(creditCardAddressLine2);
        }

        Element creditCardAddressCity = new Element("creditCardCity");
        if (!city.equals("")) {
            creditCardAddressCity.setText(city);
            root.addContent(creditCardAddressCity);
        }

        Element creditCardAddressCountry = new Element("creditCardCountry");
        Element creditCardAddressState = new Element("creditCardState");

        if (country == 0) { // US
            creditCardAddressCountry.setText("USA");
            creditCardAddressState.setText(state);
        } else { // Canada (currently only other one being served)
            creditCardAddressCountry.setText("CAN");
            creditCardAddressState.setText(state);
        }

        root.addContent(creditCardAddressState);
        root.addContent(creditCardAddressCountry);

        Element creditCardAddressZip = new Element("creditCardPostalCode");

        if (!zip.equals("")) {
            creditCardAddressZip.setText(zip);
            root.addContent(creditCardAddressZip);
        }

        if (!amountPaid.equals("")) {
            Element amountPaidNode = new Element("amountPaid");
            amountPaidNode.setText(amountPaid);
            root.addContent(amountPaidNode);
        }

        RestConnector.getInstance().httpPostCheckSuccess(new Document(root),
                "acceptcreditcardpayment/" + AppDataSingleton.getInstance().getInvoice().getId());
    }
}