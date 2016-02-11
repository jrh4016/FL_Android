package com.skeds.android.phone.business.Utilities.REST.Objects;

import com.skeds.android.phone.business.Utilities.NonfatalException;
import com.skeds.android.phone.business.Utilities.REST.RestConnector;

import org.jdom2.Document;
import org.jdom2.Element;

public class RESTPartOrder {

    public static void add(int apptId, String partName, String partNumber,
                           String quantity, String price, int manufId, String statusType)
            throws NonfatalException {
        /*
		 * /ws/addpartorder/<APPOINTMENT_ID>
		 * 
		 * with the following XML payload:
		 * 
		 * <addPartOrderRequest> <partName>XXXXXXX</partName>
		 * <partNumber>XXXXXXX</partNumber> <quantity>2.0</quantity>
		 * <price>99.99</price> </addPartOrderRequest>
		 */
        final Element root = new Element("addPartOrderRequest");

        root.addContent((new Element("partName")).setText(partName));
        root.addContent((new Element("partNumber")).setText(partNumber));
        root.addContent((new Element("quantity")).setText(quantity));
        root.addContent((new Element("price")).setText(price));
        if (manufId != 0)
            root.addContent((new Element("manufacturerId")).setText(manufId + ""));
        root.addContent((new Element("statusType")).setText(statusType));

        Document doc = new Document(root);
        RestConnector conn = RestConnector.getInstance();
        conn.httpPostCheckSuccess(doc, "addpartorder/" + apptId);
    }

    public static void edit(int partorderId, String partName,
                            String partNumber, String quantity, String price, int manufId,
                            String statusType) throws NonfatalException {
		/*
		 * Here is the API to edit an existing part order:
		 * 
		 * /ws/editpartorder/<PART_ORDER_ID>
		 * 
		 * with the following XML payload:
		 * 
		 * <editPartOrderRequest> <partName>XXXXXXX</partName>
		 * <partNumber>XXXXXXX</partNumber> <quantity>2.0</quantity>
		 * <price>99.99</price>
		 * <statusType>TO_BE_ORDERED|ORDERED|ARRIVED|JOB_SCHEDULED
		 * |JOB_COMPLETED|NOT_USED|DECLINED|RETURNED|DAMAGED</statusType>
		 * </editPartOrderRequest>
		 */
        final Element root = new Element("editPartOrderRequest");

        root.addContent((new Element("partName")).setText(partName));
        root.addContent((new Element("partNumber")).setText(partNumber));
        root.addContent((new Element("quantity")).setText(quantity));
        root.addContent((new Element("price")).setText(price));
        root.addContent((new Element("statusType")).setText(statusType));
        if (manufId != 0)
            root.addContent((new Element("manufacturerId")).setText(manufId + ""));

        Document doc = new Document(root);
        RestConnector conn = RestConnector.getInstance();
        conn.httpPostCheckSuccess(doc, "editpartorder/" + partorderId);
    }

    public static void delete(int partorderId) throws NonfatalException {
        RestConnector.getInstance().httpGetCheckSuccess("deletepartorder/" + partorderId);
    }
}