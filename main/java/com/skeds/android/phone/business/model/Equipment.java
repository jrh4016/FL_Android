package com.skeds.android.phone.business.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Equipment implements Parcelable {

    public static final String KEY_EQUIPMENT = "equipment";
    protected static final String KEY_EQUIPMENT_ID = "id";
    protected static final String KEY_EQUIPMENT_NAME = "equipmentName";
    protected static final String KEY_EQUIPMENT_MODEL_NUMBER = "equipmentModelNumber";
    protected static final String KEY_EQUIPMENT_SERIAL_NUMBER = "equipmentSerialNumber";
    protected static final String KEY_EQUIPMENT_FILTER = "equipmentFilter";
    protected static final String KEY_EQUIPMENT_INSTALLATION_DATE = "equipmentInstallationDate";
    protected static final String KEY_EQUIPMENT_NEXT_SERVICE_CALL_DATE = "equipmentNextServiceCallDate";
    protected static final String KEY_EQUIPMENT_WARRANTY_EXP_DATE = "equipmentWarrantyExpDate";
    protected static final String KEY_EQUIPMENT_LABOR_WARRANTY_DATE = "equipmentLaborWarrantyDate";
    protected static final String KEY_EQUIPMENT_LOCATION_ID = "locationId";
    protected static final String KEY_EQUIPMENT_LOCATION_NAME = "locationName";
    protected static final String KEY_EQUIPMENT_LOCATION_ADDRESS = "locationAddressString";
    protected static final String KEY_EQUIPMENT_CUSTOM_CODE = "equipmentCustomCode";
    protected static final String KEY_EQUIPMENT_WARRANTY_CONTRACT_HOLDER = "equipmentWarrantyContractHolder";
    protected static final String KEY_EQUIPMENT_WARRANTY_CONTRACT_NUMBER = "equipmentWarrantyContractNumber";
    protected static final String KEY_EQUIPMENT_NEXT_SERVICE_APPOINTMENT_DATE = "nextServiceCallAppointmentDate";
    protected static final String KEY_MANUFACTURER_ID = "equipmentManufacturerId";
    protected static final String KEY_MANUFACTURER_NAME = "equipmentManufacturerName";
    protected static final String KEY_EQUIPMENT_APPOINTMENT_TYPE_ID = "appointmentTypeId";
    protected static final String KEY_EQUIPMENT_APPOINTMENT_TYPE_NAME = "appointmentTypeName";
    protected static final String KEY_EQUIPMENT_CUSTOM_FIELD_1 = "equipmentCustomField1";
    protected static final String KEY_EQUIPMENT_CUSTOM_FIELD_2 = "equipmentCustomField2";
    protected static final String KEY_EQUIPMENT_CUSTOM_FIELD_3 = "equipmentCustomField3";
    protected static final String KEY_EQUIPMENT_SERVICE_CALL_LIST = "serviceCallList";

    public static final Parcelable.Creator<Equipment> CREATOR = new Parcelable.Creator<Equipment>() {
        @Override
        public Equipment[] newArray(int size) {
            return new Equipment[size];
        }

        @Override
        public Equipment createFromParcel(Parcel source) {
            return new Equipment(source);
        }
    };

    private long id;
    private String name;
    private String serialNo;
    private String modelNo;
    private String warrantyExpirationDate;
    private String installationDate;
    private String laborWarrantyExpirationDate;
    private long locationId;
    private String locationName;
    private String locationAddress;
    private long appointmentTypeId;
    private String nextServiceCallDate;
    private String appointmentTypeName;
    private String nextServiceAppointmentDate;
    private long manufacturerId;
    private String manufacturerName;
    private String filter;
    private String customCode;
    private String warrantyContractHolder;
    private String warrantyContractNumber;

    private String customInfo1;
    private String customInfo2;
    private String customInfo3;

    private List<ServiceCall> serviceCallList;

    public Equipment() {
    }

    public Equipment(Parcel source) {
        id = source.readLong();
        name = source.readString();
        serialNo = source.readString();
        modelNo = source.readString();
        warrantyExpirationDate = source.readString();
        installationDate = source.readString();
        laborWarrantyExpirationDate = source.readString();
        locationId = source.readLong();
        locationName = source.readString();
        locationAddress = source.readString();
        appointmentTypeId = source.readLong();
        nextServiceCallDate = source.readString();
        appointmentTypeName = source.readString();
        manufacturerId = source.readLong();
        manufacturerName = source.readString();
        filter = source.readString();
        customCode = source.readString();
        warrantyContractHolder = source.readString();
        warrantyContractNumber = source.readString();
        nextServiceAppointmentDate = source.readString();
        customInfo1 = source.readString();
        customInfo2 = source.readString();
        customInfo3 = source.readString();
        serviceCallList = new ArrayList<ServiceCall>();
        source.readList(serviceCallList, getClass().getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(serialNo);
        dest.writeString(modelNo);
        dest.writeString(warrantyExpirationDate);
        dest.writeString(installationDate);
        dest.writeString(laborWarrantyExpirationDate);
        dest.writeLong(locationId);
        dest.writeString(locationName);
        dest.writeString(locationAddress);
        dest.writeLong(appointmentTypeId);
        dest.writeString(nextServiceCallDate);
        dest.writeString(appointmentTypeName);
        dest.writeLong(manufacturerId);
        dest.writeString(manufacturerName);
        dest.writeString(filter);
        dest.writeString(customCode);
        dest.writeString(warrantyContractHolder);
        dest.writeString(warrantyContractNumber);
        dest.writeString(nextServiceAppointmentDate);
        dest.writeString(customInfo1);
        dest.writeString(customInfo2);
        dest.writeString(customInfo3);
        dest.writeList(serviceCallList);
    }

    public long getId() {
        return this.id;
    }

    public void setId(long value) {
        id = value;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        name = value;
    }

    public String getSerialNumber() {
        return this.serialNo;
    }

    public void setSerialNumber(String value) {
        serialNo = value;
    }

    public String getModelNumber() {
        return this.modelNo;
    }

    public void setModelNumber(String value) {
        modelNo = value;
    }

    public String getWarrantyExpirationDate() {
        return this.warrantyExpirationDate;
    }

    public void setWarrantyExpirationDate(String value) {
        warrantyExpirationDate = value;
    }

    public String getInstallationDate() {
        return this.installationDate;
    }

    public void setInstallationDate(String value) {
        installationDate = value;
    }

    public long getLocationId() {
        return locationId;
    }

    public void setLocationId(long value) {
        this.locationId = value;
    }

    public long getAppointmentTypeId() {
        return appointmentTypeId;
    }

    public void setAppointmentTypeId(long value) {
        this.appointmentTypeId = value;
    }

    public String getNextServiceCallDate() {
        return nextServiceCallDate;
    }

    public void setNextServiceCallDate(String value) {
        this.nextServiceCallDate = value;
    }

    public String getAppointmentTypeName() {
        return appointmentTypeName;
    }

    public void setAppointmentTypeName(String value) {
        this.appointmentTypeName = value;
    }

    public String getLaborWarrantyExpirationDate() {
        return laborWarrantyExpirationDate;
    }

    public void setLaborWarrantyExpirationDate(String value) {
        this.laborWarrantyExpirationDate = value;
    }

    public long getManufacturerId() {
        return manufacturerId;
    }

    public void setManufacturerId(long value) {
        this.manufacturerId = value;
    }

    public String getManufacturerName() {
        return manufacturerName;
    }

    public void setManufacturerName(String value) {
        this.manufacturerName = value;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String value) {
        this.filter = value;
    }

    public String getWarrantyContractHolder() {
        return warrantyContractHolder;
    }

    public void setWarrantyContractHolder(String warrantyContractHolder) {
        this.warrantyContractHolder = warrantyContractHolder;
    }

    public String getWarrantyContractNumber() {
        return warrantyContractNumber;
    }

    public void setWarrantyContractNumber(String warrantyContractNumber) {
        this.warrantyContractNumber = warrantyContractNumber;
    }

    public String getCustomInfo1() {
        return customInfo1;
    }

    public void setCustomInfo1(String info) {
        customInfo1 = info;
    }

    public String getCustomInfo2() {
        return customInfo2;
    }

    public void setCustomInfo2(String info) {
        customInfo2 = info;
    }

    public String getCustomInfo3() {
        return customInfo3;
    }

    public void setCustomInfo3(String info) {
        customInfo3 = info;
    }

    public List<ServiceCall> getServiceCallList() {
        return serviceCallList;
    }

    public void setServiceCallList(List<ServiceCall> serviceCallList) {
        this.serviceCallList = serviceCallList;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getLocationAddress() {
        return locationAddress;
    }

    public void setLocationAddress(String locationAddress) {
        this.locationAddress = locationAddress;
    }

    public String getCustomCode() {
        return customCode;
    }

    public void setCustomCode(String customCode) {
        this.customCode = customCode;
    }

    public String getNextServiceAppointmentDate() {
        return nextServiceAppointmentDate;
    }

    public void setNextServiceAppointmentDate(String nextServiceAppointmentDate) {
        this.nextServiceAppointmentDate = nextServiceAppointmentDate;
    }
}