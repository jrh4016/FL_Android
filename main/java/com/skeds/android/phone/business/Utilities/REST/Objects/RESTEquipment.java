package com.skeds.android.phone.business.Utilities.REST.Objects;

import android.text.TextUtils;

import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.NonfatalException;
import com.skeds.android.phone.business.Utilities.REST.RestConnector;
import com.skeds.android.phone.business.core.SkedsApplication;
import com.skeds.android.phone.business.model.Equipment;

import org.jdom2.Document;
import org.jdom2.Element;

import java.util.List;

public class RESTEquipment {

    public static Equipment update(final String name, final String serialNumber, final String modelNumber,
                                   final String installDate, final String warrantyDate, final String serviceCallDate,
                                   final String laborWarrantyDate, final long appointmentTypeId, final String locationId,
                                   final long manufacturerId, final String manufacturerName, final String filter,
                                   final String warrantyContractHolder, final String warrantyContractNumber, final String info1,
                                   final String info2, final String info3, final String customCode, String adress, Equipment equipment) throws NonfatalException {

        final boolean isNew = equipment == null;
        Element rootNode;
        final String url;
        if (isNew) {
            url = SkedsApplication.getContext().getString(R.string.add_customer_equipment_url, AppDataSingleton.getInstance().getCustomer().getId());
            rootNode = new Element("addEquipment");
            equipment = new Equipment();
        } else {
            url = SkedsApplication.getContext().getString(R.string.update_customer_equipment_url, equipment.getId());
            rootNode = new Element("editEquipmentDetails");
        }

        final Element equipmentNameNode = new Element("equipmentName");
        equipmentNameNode.setText(name);
        equipment.setName(name);
        rootNode.addContent(equipmentNameNode);

        final Element equipmentSerialNumberNode = new Element("equipmentSerialNumber");
        equipmentSerialNumberNode.setText(serialNumber);
        equipment.setSerialNumber(serialNumber);
        rootNode.addContent(equipmentSerialNumberNode);

        final Element equipmentModelNumberNode = new Element("equipmentModelNumber");
        equipmentModelNumberNode.setText(modelNumber);
        equipment.setModelNumber(modelNumber);
        rootNode.addContent(equipmentModelNumberNode);

        if (!TextUtils.isEmpty(warrantyDate)) {
            final Element equipmentWarrantyDateNode = new Element(
                    "equipmentWarrantyExpDate");
            equipmentWarrantyDateNode.setText(warrantyDate);
            equipment.setWarrantyExpirationDate(warrantyDate);
            rootNode.addContent(equipmentWarrantyDateNode);
        }

        if (!TextUtils.isEmpty(installDate)) {
            final Element equipmentInstallationDateNode = new Element("equipmentInstallationDate");
            equipmentInstallationDateNode.setText(installDate);
            equipment.setInstallationDate(installDate);
            rootNode.addContent(equipmentInstallationDateNode);
        }

        if (!TextUtils.isEmpty(serviceCallDate)) {
            final Element equipmentNextServiceCallDateNode = new Element(
                    "equipmentNextServiceCallDate");
            equipmentNextServiceCallDateNode.setText(serviceCallDate);
            equipment.setNextServiceCallDate(serviceCallDate);
            rootNode.addContent(equipmentNextServiceCallDateNode);
        }

        if (!TextUtils.isEmpty(laborWarrantyDate)) {
            final Element laborWarrantyDateNode = new Element(
                    "equipmentLaborWarrantyExpDate");
            laborWarrantyDateNode.setText(laborWarrantyDate);
            equipment.setLaborWarrantyExpirationDate(laborWarrantyDate);
            rootNode.addContent(laborWarrantyDateNode);
        }

        if (!TextUtils.isEmpty(warrantyContractHolder)) {
            rootNode.addContent(new Element("equipmentWarrantyContractHolder")
                    .setText(warrantyContractHolder));
            equipment.setWarrantyContractHolder(warrantyContractHolder);
        }
        if (!TextUtils.isEmpty(warrantyContractNumber)) {
            rootNode.addContent(new Element("equipmentWarrantyContractNumber")
                    .setText(warrantyContractNumber));
            equipment.setWarrantyContractNumber(warrantyContractNumber);
        }

        final Element filterNode = new Element("equipmentFilter");
        filterNode.setText(filter);
        equipment.setFilter(filter);
        rootNode.addContent(filterNode);

        if (appointmentTypeId != -1) {
            Element unscheduledAppointmentNode = new Element(
                    "appointmentTypeId");
            unscheduledAppointmentNode.setText(String
                    .valueOf(appointmentTypeId));
            equipment.setAppointmentTypeId(appointmentTypeId);
            rootNode.addContent(unscheduledAppointmentNode);
        }

        final Element locationNode = new Element("locationId");
        locationNode.setText(locationId);
        equipment.setLocationId(Long.valueOf(locationId));
        equipment.setLocationAddress(adress);
        rootNode.addContent(locationNode);

        if (manufacturerId != -1) {
            final Element manufacturerNode = new Element("equipmentManufacturerId");
            manufacturerNode.setText(String.valueOf(manufacturerId));
            equipment.setManufacturerId(manufacturerId);
            rootNode.addContent(manufacturerNode);
        }

        if (!TextUtils.isEmpty(manufacturerName)) {
            Element manufacturerNode = new Element("equipmentManufacturerName");
            manufacturerNode.setText(manufacturerName);
            equipment.setManufacturerName(manufacturerName);
            rootNode.addContent(manufacturerNode);
        }

        final Element customInfo1 = new Element("equipmentCustomField1");
        customInfo1.setText(info1);
        equipment.setCustomInfo1(info1);
        rootNode.addContent(customInfo1);

        final Element customInfo2 = new Element("equipmentCustomField2");
        customInfo2.setText(info2);
        equipment.setCustomInfo2(info2);
        rootNode.addContent(customInfo2);

        final Element customInfo3 = new Element("equipmentCustomField3");
        customInfo3.setText(info3);
        equipment.setCustomInfo3(info3);
        rootNode.addContent(customInfo3);

        final Element customCodeNode = new Element("equipmentCustomCode");
        customCodeNode.setText(customCode);
        equipment.setCustomCode(customCode);
        rootNode.addContent(customCodeNode);

        final Document document = RestConnector.getInstance().httpPostCheckSuccess(new Document(rootNode), url);

        if (isNew) {
            final String id = document.getRootElement().getChild("response").getAttribute("id").getValue();
            equipment.setId(Integer.parseInt(id));
        }
        return equipment;
    }

    public static void delete(long equipmentId) throws NonfatalException {
        RestConnector.getInstance().httpGetCheckSuccess(SkedsApplication.getContext().getString(R.string.delete_customer_equipment_url, equipmentId));
    }

    /**
     * Queries for a single piece of equipment using a scanned barcode
     *
     * @param ownerId
     * @param barcodeValue
     * @return
     * @throws NonfatalException
     */
    public static void queryByBarcode(int ownerId, String barcodeValue)
            throws NonfatalException {
        Element rootNode = new Element("getCustomerEquipmentByCodeRequest");
        Element equipmentBarcodeNode = new Element("equipmentCode");

        equipmentBarcodeNode.setText(barcodeValue);

        rootNode.addContent(equipmentBarcodeNode);

        RestConnector.getInstance().httpPostCheckSuccess(new Document(rootNode),
                "getcustomerequipmentbycode/" + ownerId);
    }

    /**
     * Attaches equipment to an appointment
     *
     * @param appointmentId
     * @param equipmentId
     * @return
     * @throws NonfatalException
     */
    public static void attachToAppointment(int appointmentId,
                                           List<Integer> equipmentId) throws NonfatalException {

        if (equipmentId.isEmpty())
            throw new NonfatalException("APP",
                    "Nothing to send: empty equipment list");
        Element rootNode = new Element("addEquipmentToAppointmentRequest");

		/* Customer Detail Elements */
        for (int i = 0; i < equipmentId.size(); i++) {
            Element equipmentIdNode = new Element("equipmentId");
            equipmentIdNode.setText(String.valueOf(equipmentId.get(i)));
            rootNode.addContent(equipmentIdNode);
        }

        RestConnector.getInstance().httpPost(new Document(rootNode),
                "addcustomerequipmenttoappointment/" + appointmentId);
    }

    /**
     * Attach a barcode to equipment
     *
     * @param equipmentId
     * @param barcodeValue
     * @return
     * @throws NonfatalException
     */
    public static void attachBarcode(long equipmentId, String barcodeValue)
            throws NonfatalException {
        Element rootNode = new Element("marryEquipmentToCodeRequest");

		/* Customer Detail Elements */
        Element equipmentBarcodeNode = new Element("equipmentCode");

        equipmentBarcodeNode.setText(barcodeValue);

        rootNode.addContent(equipmentBarcodeNode);

        RestConnector.getInstance().httpPost(new Document(rootNode),
                "marrycustomerequipmenttocode/" + equipmentId);
    }
}