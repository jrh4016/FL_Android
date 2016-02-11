package com.skeds.android.phone.business.Utilities.REST.Objects;

import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Location;
import com.skeds.android.phone.business.Utilities.NonfatalException;
import com.skeds.android.phone.business.Utilities.REST.RestConnector;

import org.jdom2.Document;
import org.jdom2.Element;

public class RESTLocation {

    private static Document makeDocument(String rootElemName, Location loc) {
        final Element rootNode = new Element(rootElemName);

        rootNode.addContent(new Element("address1").setText(loc.getAddress1()));
        if (loc.getAddress2() != null)
            rootNode.addContent(new Element("address2").setText(loc
                    .getAddress2()));
        rootNode.addContent(new Element("city").setText(loc.getCity()));
        rootNode.addContent(new Element("state").setText(loc.getState()));
        rootNode.addContent(new Element("zip").setText(loc.getZip()));
        rootNode.addContent(new Element("name").setText(loc.getName()));
        if (loc.getPhone1() != null) {
            rootNode.addContent(new Element("phone1").setText(loc.getPhone1()));
            if (loc.getPhone1Type() != null)
                rootNode.addContent(new Element("phone1Type").setText(loc.getPhone1Type()));
            if (loc.getPhone1Description() != null)
                rootNode.addContent(new Element("phone1Description").setText(loc.getPhone1Description()));
        }

        if (loc.getPhone2() != null) {
            rootNode.addContent(new Element("phone2").setText(loc.getPhone2()));
            if (loc.getPhone2Type() != null)
                rootNode.addContent(new Element("phone2Type").setText(loc.getPhone2Type()));
            if (loc.getPhone2Description() != null)
                rootNode.addContent(new Element("phone2Description").setText(loc.getPhone2Description()));
        }

        if (loc.getDescription() != null)
            rootNode.addContent(new Element("description").setText(loc
                    .getDescription()));

        if (loc.getEmail() != null)
            rootNode.addContent(new Element("email").setText(loc
                    .getEmail()));

        if (loc.getCode() != null)
            rootNode.addContent(new Element("locationCode").setText(loc
                    .getCode()));
        return new Document(rootNode);
    }

    public static void add(int customerId, Location loc)
            throws NonfatalException {
        Document document = RestConnector.getInstance().httpPostCheckSuccess(
                makeDocument("addLocation", loc), "addlocation/" + customerId);
        Element locNode = document.getRootElement().getChild("location");

        if (locNode == null)
            return;
        loc.setId(Integer.parseInt(locNode.getAttributeValue("id")));
    }

    public static void update(Location loc) throws NonfatalException {
        RestConnector.getInstance().httpPostCheckSuccess(
                makeDocument("editlocation", loc),
                "editlocation/" + loc.getId());
    }

    public static void query(int locationPos) throws NonfatalException {
        Location loc = AppDataSingleton.getInstance().getCustomer().locationList.get(locationPos);
        Document document = RestConnector.getInstance().httpGet(
                "getlocationdetails/" + loc.getId());

        Element locationElement = document.getRootElement().getChild("location");

        String value = locationElement.getAttributeValue("latitude");
        if (value != null)
            loc.setLatitude(locationElement.getAttributeValue("latitude"));

        value = locationElement.getAttributeValue("longitude");
        if (value != null)
            loc.setLatitude(locationElement.getAttributeValue("longitude"));

        Element name = locationElement.getChild("name");
        if (name != null)
            loc.setName(locationElement.getChild("name").getText());

        Element node = locationElement.getChild("address1");
        if (node != null)
            loc.setAddress1(locationElement.getChild("address1").getText());

        node = locationElement.getChild("address2");
        if (node != null)
            loc.setAddress2(locationElement.getChild("address2").getText());

        node = locationElement.getChild("city");
        if (node != null)
            loc.setCity(locationElement.getChild("city").getText());

        node = locationElement.getChild("state");
        if (node != null)
            loc.setState(locationElement.getChild("state").getText());

        node = locationElement.getChild("zip");
        if (node != null)
            loc.setZip(locationElement.getChild("zip").getText());

        node = locationElement.getChild("phone1");
        if (node != null)
            loc.setPhone1(locationElement.getChild("phone1").getText());

        node = locationElement.getChild("phone1Type");
        if (node != null)
            loc.setPhone1Type(locationElement.getChild("phone1Type").getText());

        node = locationElement.getChild("phone1Description");
        if (node != null)
            loc.setPhone1Description(locationElement.getChild("phone1Description").getText());

        node = locationElement.getChild("phone2");
        if (node != null)
            loc.setPhone2(locationElement.getChild("phone2").getText());

        node = locationElement.getChild("phone2Type");
        if (node != null)
            loc.setPhone2Type(locationElement.getChild("phone2Type").getText());

        node = locationElement.getChild("phone2Description");
        if (node != null)
            loc.setPhone2Description(locationElement.getChild("phone2Description").getText());

        node = locationElement.getChild("email");
        if (node != null)
            loc.setEmail(locationElement.getChild("email").getText());

        node = locationElement.getChild("description");
        if (node != null)
            loc.setDescription(locationElement.getChild("description").getText());

        node = locationElement.getChild("locationCode");
        if (node != null)
            loc.setCode(locationElement.getChild("locationCode").getText());
    }

    /**
     * Queries for a single location using a scanned barcode
     *
     * @param ownerId
     * @param barcodeValue
     * @return
     * @throws NonfatalException
     */
    public static void queryByBarcode(int ownerId, String barcodeValue)
            throws NonfatalException {
        Element rootNode = new Element("getLocationByCodeRequest");
        Element locationBarcodeNode = new Element("locationCode");

        locationBarcodeNode.setText(barcodeValue);

        rootNode.addContent(locationBarcodeNode);

        RestConnector.getInstance().httpPost(new Document(rootNode),
                "getlocationbycode/" + ownerId);
    }

    /**
     * Attach a barcode to location
     *
     * @param locationId
     * @param barcodeValue
     * @return
     * @throws NonfatalException
     */
    public static void attachBarcode(int locationId, String barcodeValue)
            throws NonfatalException {
        Element rootNode = new Element("marryLocationToCodeRequest");

		/* Customer Detail Elements */
        Element locationBarcodeNode = new Element("locationCode");

        locationBarcodeNode.setText(barcodeValue);

        rootNode.addContent(locationBarcodeNode);

        RestConnector.getInstance().httpPostCheckSuccess(new Document(rootNode),
                "marrylocationtocode/" + locationId);
    }
}