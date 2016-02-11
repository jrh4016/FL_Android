package com.skeds.android.phone.business.Utilities.REST.Objects;

import android.text.TextUtils;

import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Customer;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.LeadSource;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Location;
import com.skeds.android.phone.business.Utilities.NonfatalException;
import com.skeds.android.phone.business.Utilities.REST.RestConnector;

import org.jdom2.Document;
import org.jdom2.Element;

import java.math.BigDecimal;
import java.util.List;
import java.util.TimeZone;

public class RESTCustomer {

    /* The customer being viewed */
    public static void query(int customerId) throws NonfatalException {

        if (customerId == 0)
            customerId = AppDataSingleton.getInstance().getCustomer().getId();

        Document document = RestConnector.getInstance().httpGet(
                "getcustomer/" + customerId);
        AppDataSingleton.getInstance().setCustomer(new Customer());
        Element customerNode = document.getRootElement().getChild("customer");

        if (customerNode == null)
            throw new NonfatalException("XML",
                    "Bad server response: no customer data");
        if (customerNode.getAttributeValue("id") != null)
            AppDataSingleton.getInstance().getCustomer().setId(
                    Integer.parseInt(customerNode.getAttributeValue("id")));
        if (customerNode.getAttributeValue("type") != null)
            AppDataSingleton.getInstance().getCustomer().setType(
                    customerNode.getAttributeValue("type"));
        if (customerNode.getAttributeValue("unreliable") != null)
            AppDataSingleton.getInstance().getCustomer().setUnreliable(
                    Boolean.parseBoolean(customerNode
                            .getAttributeValue("unreliable")));

        AppDataSingleton.getInstance().getCustomer().setTaxable(Boolean.valueOf(customerNode.getAttributeValue("taxable")));

        // Customer Reliability Notes
        if (customerNode.getAttributeValue("unreliableFlagText") != null)
            AppDataSingleton.getInstance().getCustomer().setReliableNotes(
                    customerNode.getAttributeValue("unreliableFlagText"));

        if (customerNode.getChildText("customerFirstName") != null)
            AppDataSingleton.getInstance().getCustomer().setFirstName(
                    customerNode.getChildText("customerFirstName"));
        if (customerNode.getChildText("customerLastName") != null)
            AppDataSingleton.getInstance().getCustomer().setLastName(
                    customerNode.getChildText("customerLastName"));
        if (customerNode.getChild("taxRate") != null)
            AppDataSingleton.getInstance().setCustomerEstimateTaxRate(new BigDecimal(customerNode
                    .getChild("taxRate").getText()));
        else {
            AppDataSingleton.getInstance().setCustomerEstimateTaxRate(BigDecimal.ZERO);

        }

        // Parse primary location for customer with locations > 20
        Element locNode = customerNode.getChild("customerLocation");

        if (locNode.getAttributeValue("taxable") != null) {
            AppDataSingleton.getInstance().getCustomer()
                    .setLocationTaxable(Boolean.parseBoolean(locNode.getAttributeValue("taxable")));
        }

        if (locNode.getChildText("name") != null) {
            AppDataSingleton.getInstance().getCustomer()
                    .setAddress(locNode.getChildText("name"));
        }

        if (locNode.getChildText("address1") != null) {
            AppDataSingleton.getInstance().getCustomer()
                    .setAddress1(locNode.getChildText("address1"));
        }

        if (locNode.getChildText("address2") != null) {
            AppDataSingleton.getInstance().getCustomer()
                    .setAddress2(locNode.getChildText("address2"));
        }

        if (locNode.getChildText("city") != null) {
            AppDataSingleton.getInstance().getCustomer()
                    .setAddressCity(locNode.getChildText("city"));
        }

        if (locNode.getChildText("state") != null) {
            AppDataSingleton.getInstance().getCustomer()
                    .setAddressState(locNode.getChildText("state"));
        }

        if (locNode.getChildText("zip") != null) {
            AppDataSingleton.getInstance().getCustomer()
                    .setAddressPostalCode(locNode.getChildText("zip"));
        }

        if (locNode.getAttribute("timeZone") != null) {
            AppDataSingleton.getInstance().getCustomer()
                    .setTimeZone(TimeZone.getTimeZone(locNode.getAttribute("timeZone").getValue()));
        }

        if (customerNode.getChildText("customerBillingAddress") != null)
            AppDataSingleton.getInstance().getCustomer().setAddress(
                    customerNode.getChildText("customerBillingAddress"));
        if (customerNode.getChildText("orgName") != null)
            AppDataSingleton.getInstance().getCustomer().setOrganizationName(
                    customerNode.getChildText("orgName"));

        AppDataSingleton.getInstance().getCustomer().email.clear(); // Empty this
        AppDataSingleton.getInstance().getCustomer().emailDescription.clear();
        AppDataSingleton.getInstance().getCustomer().emailId.clear();
        if (customerNode.getChildText("email") != null) {

            AppDataSingleton.getInstance().getCustomer().email.add(customerNode.getChildText("email"));

            if (customerNode.getChild("email").getAttributeValue("description") != null)
                AppDataSingleton.getInstance().getCustomer().emailDescription.add(customerNode
                        .getChild("email").getAttributeValue("description"));
            else
                AppDataSingleton.getInstance().getCustomer().emailDescription.add(null);

            if (customerNode.getChild("email").getAttributeValue("id") != null)
                AppDataSingleton.getInstance().getCustomer().emailId.add(Integer.parseInt(customerNode
                        .getChild("email").getAttributeValue("id")));
        }

        int i = 2;
        while (true) {
            if (customerNode.getChildText("email" + i) != null) {
                AppDataSingleton.getInstance().getCustomer().email.add(customerNode
                        .getChildText("email" + i));

                if (customerNode.getChild("email" + i).getAttributeValue("id") != null)
                    AppDataSingleton.getInstance().getCustomer().emailId.add(Integer
                            .parseInt(customerNode.getChild("email" + i)
                                    .getAttributeValue("id")));

                if (customerNode.getChild("email" + i).getAttributeValue(
                        "description") != null)
                    AppDataSingleton.getInstance().getCustomer().emailDescription.add(customerNode
                            .getChild("email" + i).getAttributeValue(
                                    "description"));
                else
                    AppDataSingleton.getInstance().getCustomer().emailDescription.add(null);

                i++;
            } else {
                break;
            }
        }

        i = 1;
        AppDataSingleton.getInstance().getCustomer().phone.clear(); // Empty this
        AppDataSingleton.getInstance().getCustomer().phoneDescription.clear();
        AppDataSingleton.getInstance().getCustomer().phoneId.clear();
        while (true) {
            if (customerNode.getChildText("phone" + i) != null) {

                if (customerNode.getChildText("phone" + i) != null)
                    AppDataSingleton.getInstance().getCustomer().phone.add(customerNode
                            .getChildText("phone" + i));

                if (customerNode.getChild("phone" + i)
                        .getAttributeValue("type") != null)
                    AppDataSingleton.getInstance().getCustomer().phoneType.add(customerNode.getChild(
                            "phone" + i).getAttributeValue("type"));

                if (customerNode.getChild("phone" + i).getAttributeValue("id") != null)
                    AppDataSingleton.getInstance().getCustomer().phoneId.add(Integer
                            .parseInt(customerNode.getChild("phone" + i)
                                    .getAttributeValue("id")));

                if (customerNode.getChild("phone" + i).getAttributeValue(
                        "description") != null)
                    AppDataSingleton.getInstance().getCustomer().phoneDescription.add(customerNode
                            .getChild("phone" + i).getAttributeValue(
                                    "description"));
                else
                    AppDataSingleton.getInstance().getCustomer().phoneDescription.add(null);

                i++;
            } else {
                break;
            }
        }

		/* Notes */
        if (customerNode.getChildText("internalCustomerInfo") != null)
            AppDataSingleton.getInstance().getCustomer().setNotes(
                    customerNode.getChildText("internalCustomerInfo"));

        final Element customerLocationNode = customerNode.getChild("customerLocation");
        if (customerLocationNode != null) {
            AppDataSingleton.getInstance().getCustomer().setLocationTaxable(
                    Boolean.valueOf(customerLocationNode.getAttributeValue("taxable")));
        }

        List<Element> locationsList = customerNode.getChildren("locations");
        Element locationsNode = locationsList.get(0);

        if (locationsNode.getAttributeValue("count") != null) {
            AppDataSingleton.getInstance().getCustomer().locationsCount = Integer.parseInt(locationsNode.getAttributeValue("count"));
        }

        List<Element> locationList = locationsNode.getChildren("location");

        AppDataSingleton.getInstance().getCustomer().locationList.clear();

        if (!locationList.isEmpty()) {
            for (int locationIterator = 0; locationIterator < locationList
                    .size(); locationIterator++) {

                AppDataSingleton.getInstance().getCustomer().locationList.add(new Location());
                Element locationNode = locationList.get(locationIterator);

                Location currentLocation = AppDataSingleton.getInstance().getCustomer().locationList.get(locationIterator);

                // Id
                if (locationNode.getAttributeValue("id") != null)
                    currentLocation
                            .setId(Integer.parseInt(locationNode
                                    .getAttributeValue("id")));

                // Latitude
                if (locationNode.getAttributeValue("latitude") != null)
                    currentLocation
                            .setLatitude(
                                    locationNode.getAttributeValue("latitude"));

                // Longitude
                if (locationNode.getAttributeValue("longitude") != null)
                    currentLocation
                            .setLongitude(
                                    locationNode.getAttributeValue("longitude"));

                // Longitude
                if (locationNode.getAttributeValue("timeZone") != null)
                    currentLocation
                            .setTimeZone(TimeZone.getTimeZone(
                                    locationNode.getAttributeValue("timeZone")));

                // Name
                if (locationNode.getChildText("name") != null)
                    currentLocation
                            .setName(locationNode.getChildText("name"));

                // Address 1
                if (locationNode.getChild("address1") != null
                        && locationNode.getChild("address1").getValue() != null)
                    currentLocation
                            .setAddress1(
                                    locationNode.getChild("address1")
                                            .getValue());

                // Address 2
                if (locationNode.getChild("address2") != null
                        && locationNode.getChild("address2").getValue() != null)
                    currentLocation
                            .setAddress2(
                                    locationNode.getChild("address2")
                                            .getValue());

                if (locationNode.getChild("multinational") != null
                        && locationNode.getChild("multinational").getAttributeValue("countryId") != null)
                    currentLocation
                            .setCountryId(Integer.parseInt(locationNode.getChild("multinational").getAttributeValue("countryId")));

                // City
                if (locationNode.getChild("city") != null
                        && locationNode.getChild("city").getValue() != null)
                    currentLocation
                            .setCity(locationNode.getChild("city").getValue());

                // State
                if (locationNode.getChild("state") != null
                        && locationNode.getChild("state").getValue() != null)
                    currentLocation
                            .setState(locationNode.getChild("state").getValue());

                // Zip
                if (locationNode.getChild("zip") != null
                        && locationNode.getChild("zip").getValue() != null)
                    currentLocation
                            .setZip(locationNode.getChild("zip").getValue());

                // email
                if (locationNode.getChild("email") != null
                        && locationNode.getChild("email").getValue() != null)
                    currentLocation
                            .setEmail(locationNode.getChild("email").getValue());

                // Check if lat long only
                if (currentLocation.getAddress1().isEmpty() && currentLocation.getAddress2().isEmpty() && currentLocation.getCity().isEmpty())
                    currentLocation.setLatLongOnly(true);

                // Phone 1
                if (locationNode.getChild("phone1") != null
                        && locationNode.getChild("phone1").getValue() != null)
                    currentLocation
                            .setPhone1(
                                    locationNode.getChild("phone1").getValue());

                // Phone 2
                if (locationNode.getChild("phone2") != null
                        && locationNode.getChild("phone2").getValue() != null)
                    currentLocation
                            .setPhone2(
                                    locationNode.getChild("phone2").getValue());
                // description
                String text = locationNode.getChildTextTrim("description");
                if (text != null)
                    currentLocation
                            .setDescription(text);

                currentLocation.setTaxable(Boolean.valueOf(locationNode.getAttributeValue("taxable")));
            }
        }
    }

    // TODO - We need POST and UPDATE functions
    public static void updatePhoneNumber(int customerPhoneId,
                                         String phoneNumber, String description, String type)
            throws NonfatalException {
        Element root = new Element("phone");

        // Make sure phone number has info
        if (!phoneNumber.equals("")) {
            Element phoneNumberElement = new Element("number");
            phoneNumberElement.setText(phoneNumber);
            root.addContent(phoneNumberElement);
        }

        // Make sure description has info before creating element
        if (!description.equals("")) {
            Element descriptionElement = new Element("description");
            descriptionElement.setText(description);
            root.addContent(descriptionElement);
        }

        // Make sure type has a description before creating everything for it
        if (!type.equals("")) {
            Element typeElement = new Element("type");
            typeElement.setText(type);
            root.addContent(typeElement);
        }

        RestConnector.getInstance().httpPostCheckSuccess(new Document(root),
                "editcustomerphone/" + customerPhoneId);
    }

    // TODO - We need POST and UPDATE functions
    public static void addPhoneNumber(int customerId, String phoneNumber,
                                      String description, String type) throws NonfatalException {
        Element root = new Element("phone");

        Element phoneNumberElement = new Element("number");
        Element descriptionElement = new Element("description");
        Element typeElement = new Element("type");

        phoneNumberElement.setText(phoneNumber);
        descriptionElement.setText(description);
        typeElement.setText(type);

        root.addContent(phoneNumberElement);
        root.addContent(descriptionElement);
        root.addContent(typeElement);

        Document respxml = RestConnector.getInstance().httpPostCheckSuccess(
                new Document(root), "addcustomerphone/" + customerId);
        String id = respxml.getRootElement().getChild("response")
                .getAttributeValue("id");
        if (id != null)
            AppDataSingleton.getInstance().getCustomer().phoneId.add(Integer.parseInt(id));
    }

    // TODO - We need POST and UPDATE functions
    public static void updateEmail(int customerEmailId, String emailAddress,
                                   String description) throws NonfatalException {
        Element root = new Element("email");

        Element emailAddressElement = new Element("address");
        Element descriptionElement = new Element("description");

        emailAddressElement.setText(emailAddress);
        descriptionElement.setText(description);

        root.addContent(emailAddressElement);
        root.addContent(descriptionElement);

        RestConnector.getInstance().httpPostCheckSuccess(new Document(root),
                "editcustomeremail/" + customerEmailId);
    }

    // TODO - We need POST and UPDATE functions
    public static void addEmail(int customerId, String emailAddress,
                                String description) throws NonfatalException {
        Element root = new Element("email");

        Element emailAddressElement = new Element("address");
        Element descriptionElement = new Element("description");

        emailAddressElement.setText(emailAddress);
        descriptionElement.setText(description);

        root.addContent(emailAddressElement);
        root.addContent(descriptionElement);

        Document respxml = RestConnector.getInstance().httpPostCheckSuccess(
                new Document(root), "addcustomeremail/" + customerId);
        String id = respxml.getRootElement().getChild("response")
                .getAttributeValue("id");
        if (id != null)
            AppDataSingleton.getInstance().getCustomer().emailId.add(Integer.parseInt(id));
    }

    // TODO - Needs POST-Only function
    public static void add(int ownerId, String businessName, String firstName,
                           String lastName, String address1, String address2, String city,
                           String state, String zip, String locName, LeadSource leadSource) throws NonfatalException {
        Element rootNode = new Element("addCustomer");

		/* Customer Detail Elements */
        Element companyNameNode = new Element("companyName");
        Element firstNameNode = new Element("firstName");
        Element lastNameNode = new Element("lastName");

		/* Location Detail Elements */
        Element address1Node = new Element("address1");
        Element address2Node = new Element("address2");
        Element cityNode = new Element("city");
        Element stateNode = new Element("state");
        Element zipNode = new Element("zip");
        Element id = new Element("id");




        if (leadSource != null) {
            Element leadSourceId = new Element("leadSourceId");
            leadSourceId.setText(leadSource.getId());
            rootNode.addContent(leadSourceId);
        }
        companyNameNode.setText(businessName);
        firstNameNode.setText(firstName);
        lastNameNode.setText(lastName);
        id.setText(ownerId + "");

        address1Node.setText(address1);
        address2Node.setText(address2);
        cityNode.setText(city);
        stateNode.setText(state);
        zipNode.setText(zip);

        rootNode.addContent(new Element("locationName").setText(locName));

        rootNode.addContent(companyNameNode);
        rootNode.addContent(firstNameNode);
        rootNode.addContent(lastNameNode);

        rootNode.addContent(address1Node);
        rootNode.addContent(address2Node);
        rootNode.addContent(cityNode);
        rootNode.addContent(stateNode);
        rootNode.addContent(id);

        rootNode.addContent(zipNode);

        RestConnector.getInstance().httpPostCheckSuccess(new Document(rootNode),
                "addcustomer/" + ownerId);
    }

    // TODO - Needs UPDATE REST function
    public static void update(int customerId, String businessName,
                              String firstName, String lastName, String address1,
                              String address2, String city, String state, String zip, String locName, LeadSource leadSource)
            throws NonfatalException {
        Element rootNode = new Element("editCustomer");

		/* Customer Detail Elements */
        Element companyNameNode = new Element("companyName");
        Element firstNameNode = new Element("firstName");
        Element lastNameNode = new Element("lastName");

		/* Location Detail Elements */
        Element address1Node = new Element("address1");
        Element address2Node = new Element("address2");
        Element cityNode = new Element("city");
        Element stateNode = new Element("state");
        Element zipNode = new Element("zip");

        Element leadSourceId = new Element("leadSourceId");

        if (leadSource != null)
            leadSourceId.setText(leadSource.getId());
        companyNameNode.setText(businessName);
        firstNameNode.setText(firstName);
        lastNameNode.setText(lastName);

        address1Node.setText(address1);
        address2Node.setText(address2);
        cityNode.setText(city);
        stateNode.setText(state);
        zipNode.setText(zip);

        rootNode.addContent(new Element("locationName").setText(locName));

        rootNode.addContent(companyNameNode);
        rootNode.addContent(firstNameNode);
        rootNode.addContent(lastNameNode);

        rootNode.addContent(address1Node);
        rootNode.addContent(address2Node);
        rootNode.addContent(cityNode);
        rootNode.addContent(stateNode);

        rootNode.addContent(zipNode);
        rootNode.addContent(leadSourceId);

        RestConnector.getInstance().httpPostCheckSuccess(new Document(rootNode),
                "editcustomer/" + customerId);
    }

    public static void addNotes(int customerId, String notes)
            throws NonfatalException {
        Element rootNode = new Element("addToCustomerNoteRequest");
        Element newNotes = new Element("note");

        newNotes.setText(notes);

        rootNode.addContent(newNotes);

        RestConnector.getInstance().httpPostCheckSuccess(new Document(rootNode),
                "addtocustomernote/" + customerId);
    }

    /**
     * Attaches a document to a customer
     *
     * @param customerId
     * @param documentBytes
     * @param invoiceId
     * @param fileName
     * @param displayName
     * @param fileExtension
     * @return
     * @throws NonfatalException
     */
    public static void attachDocument(int customerId, String documentBytes,
                                      int invoiceId, int apptId, String fileName, String displayName,
                                      String fileExtension) throws NonfatalException {
        Element rootNode = new Element("addDocumentRequest");
        Element documentBytesNode = new Element("documentBytes");
        Element invoiceIdNode = new Element("invoiceId");
        Element apptIdNode = new Element("appointmentId");
        Element fileNameNode = new Element("fileName");
        Element displayNameNode = new Element("displayName");
        Element extensionNode = new Element("extension");

        if (!TextUtils.isEmpty(documentBytes)) {
            documentBytesNode.setText(documentBytes);
            rootNode.addContent(documentBytesNode);
        }

        if (invoiceId != 0 && invoiceId != -1) {
            invoiceIdNode.setText(String.valueOf(invoiceId));
            rootNode.addContent(invoiceIdNode);
        }

        if (apptId != 0 && invoiceId != -1) {
            apptIdNode.setText(String.valueOf(apptId));
            rootNode.addContent(apptIdNode);
        }

        if (!TextUtils.isEmpty(fileName)) {
            fileNameNode.setText(fileName);
            rootNode.addContent(fileNameNode);
        }

        if (!TextUtils.isEmpty(displayName)) {
            displayNameNode.setText(displayName);
            rootNode.addContent(displayNameNode);
        }

        if (!TextUtils.isEmpty(fileExtension)) {
            extensionNode.setText(fileExtension);
            rootNode.addContent(extensionNode);
        }

        RestConnector.getInstance().httpPostCheckSuccess(new Document(rootNode),
                "adddocument/" + customerId);
    }
}