package com.skeds.android.phone.business.Utilities.General.ClassObjects;

import com.skeds.android.phone.business.Utilities.General.UserUtilitiesSingleton;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class Appointment implements Serializable {

    // Variables
    private int id;
    private boolean canUpdate;
    private boolean hasCustomQuestions;

    private Status status;
    private int participantID;

    private String ownerName = "";
    private int ownerID;
    private boolean ownerTimeTrackingEnabled;
    private boolean ownerForceWorkOrderNumberInput;

    private String workOrderNumber = "";
    private String workOrderNumberPrefix = "";
    private boolean usingInvoices;
    private boolean everybodyElseFinished;

    private int invoiceId = 0;

    private int selectedAgreementId = -1;
    private String selectedAgreementName = "";
    private String selectedAgreementDescription = "";

    private int apptTypeID;
    private String apptTypeName = "";

    private String appointmentLocationCustomCode = "";

    private String date = "";
    private String startTime = "";
    private String endTime = "";
    private String notes = "";
    private int locationId;
    private String locationName = "";
    private String locationValue = "";
    private String locationLatitude = "0.0";
    private String locationLongitude = "0.0";
    private String locationAccuracy = "0.0";

    private boolean isLocationTaxable;

    private String phone1;
    private String phone1Description;
    private String phone2;
    private String phone2Description;

    private String multiDay = "";

    private int searchModeOverride = -1;

    private AppointmentCustomField customField = new AppointmentCustomField();
    private AppointmentCustomField secondCustomField = new AppointmentCustomField();

    private List<Participant> participantList = new ArrayList<Participant>();

    private List<LineItem> onTruckList = new ArrayList<LineItem>();

    private List<Comment> commentList = new ArrayList<Comment>();

    private List<pieceOfEquipment> equipmentList = new ArrayList<pieceOfEquipment>();

    private List<Agreement> agreementList = new ArrayList<Agreement>();
    private TimeZone timeZone;
    private String leadSourceId;
    private Customer customer;

    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }

    public TimeZone getTimeZone() {
        return timeZone;
    }

    public void setLeadSourceId(String leadSourceId) {
        this.leadSourceId = leadSourceId;
    }

    public String getLeadSourceId() {
        return leadSourceId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }


    public class pieceOfEquipment implements Serializable {
        private int id;
        private String name = "";
        private String modelNumber = "";
        private String serialNumber = "";
        private int manufacturerId;
        private String manufacturer = "";

        public int getId() {
            return id;
        }

        public void setId(int value) {
            this.id = value;
        }


        public String getName() {
            return name;
        }

        public void setName(String value) {
            this.name = value;
        }

        public String getModelNumber() {
            return modelNumber;
        }

        public void setModelNumber(String value) {
            this.modelNumber = value;
        }

        public String getSerialNumber() {
            return serialNumber;
        }

        public void setSerialNumber(String value) {
            this.serialNumber = value;
        }

        public int getManufacturerId() {
            return manufacturerId;
        }

        public void setManufacturerId(int value) {
            this.manufacturerId = value;
        }

        public String getManufacturer() {
            return manufacturer;
        }

        public void setManufacturer(String value) {
            this.manufacturer = value;
        }
    }

    public void setId(int value) {
        this.id = value;
    }

    public void setCanUpdate(boolean value) {
        this.canUpdate = value;
    }

    public void setStatus(Status value) {
        this.status = value;
    }

    public void setParticipantId(int value) {
        this.participantID = value;
    }

    public void setOwnerName(String value) {
        this.ownerName = value;
    }

    public void setOwnerId(int value) {
        this.ownerID = value;
    }

    public void setOwnerTimeTrackingEnabled(boolean value) {
        this.ownerTimeTrackingEnabled = value;
    }

    public void setOwnerForceWorkOrderNumberInput(boolean value) {
        this.ownerForceWorkOrderNumberInput = value;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setInvoiceId(int value) {
        this.invoiceId = value;
    }

    public void setApptTypeId(int value) {
        this.apptTypeID = value;
    }

    public void setApptTypeName(String value) {
        this.apptTypeName = value;
    }

    public void setDate(String value) {
        this.date = value;
    }

    public void setStartTime(String value) {
        this.startTime = value;
    }

    public void setEndTime(String value) {
        this.endTime = value;
    }

    public void setNotes(String value) {
        this.notes = value;
    }

    public void setLocationName(String value) {
        this.locationName = value;
    }

    public void setLocationValue(String value) {
        this.locationValue = value;
    }

    public void setLocationLongitude(String value) {
        this.locationLongitude = value;
    }

    public void setLocationLatitude(String value) {
        this.locationLatitude = value;
    }

    public void setLocationAccuracy(String value) {
        this.locationAccuracy = value;
    }

    public void setMultiDay(String value) {
        this.multiDay = value;
    }

    public void setSearchModeOverride(int mode) {
        searchModeOverride = mode;
    }

    public int getId() {
        return id;
    }

    public boolean getCanUpdate() {
        return canUpdate;
    }

    public Status getStatus() {
        return status;
    }

    public int getParticipantId() {
        return participantID;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public int getOwnerId() {
        return ownerID;
    }

    public boolean getOwnerTimeTrackingEnabled() {
        return ownerTimeTrackingEnabled;
    }

    public boolean getOwnerForceWorkOrderNumberInput() {
        return ownerForceWorkOrderNumberInput;
    }

    public int getInvoiceId() {
        return this.invoiceId;
    }

    public int getApptTypeId() {
        return apptTypeID;
    }

    public String getApptTypeName() {
        return apptTypeName;
    }

    public String getDate() {
        return date;
    }


    public String getNotes() {
        return notes;
    }

    public String getLocationName() {
        return locationName;
    }

    public String getLocationValue() {
        return locationValue;
    }

    public String getLocationLongitude() {
        return locationLongitude;
    }

    public String getLocationLatitude() {
        return locationLatitude;
    }

    public String getLocationAccuracy() {
        return locationAccuracy;
    }

    public String getWorkOrderNumber() {
        return workOrderNumber;
    }

    public void setWorkOrderNumber(String value) {
        this.workOrderNumber = value;
    }

    public boolean isUsingInvoices() {
        return usingInvoices;
    }

    public void setUsingInvoices(boolean value) {
        this.usingInvoices = value;
    }

    public boolean isEverybodyElseFinished() {
        return everybodyElseFinished;
    }

    public void setEverybodyElseFinished(boolean value) {
        this.everybodyElseFinished = value;
    }

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int value) {
        this.locationId = value;
    }

    public String getAppointmentLocationCustomCode() {
        return appointmentLocationCustomCode;
    }

    public void setAppointmentLocationCustomCode(String value) {
        this.appointmentLocationCustomCode = value;
    }

    public boolean isHasCustomQuestions() {
        return hasCustomQuestions;
    }

    public void setHasCustomQuestions(boolean value) {
        this.hasCustomQuestions = value;
    }

    public void setSelectedAgreementId(int id) {
        selectedAgreementId = id;
    }

    public int getSelectedAgreementId() {
        return selectedAgreementId;
    }

    public void setSelectedAgreementName(String name) {
        selectedAgreementName = name;
    }

    public String getSelectedAgreementName() {
        return selectedAgreementName;
    }

    public void setSelectedAgreementDescription(String description) {
        selectedAgreementDescription = description;
    }

    public String getSelectedAgreementDescription() {
        return selectedAgreementDescription;
    }

    public String getMultiDay() {
        return multiDay;
    }

    public int getSearchModeOverride() {
        return searchModeOverride;
    }

    public List<LineItem> getOnTruckList() {
        return onTruckList;
    }

    public void setOnTruckList(List<LineItem> list) {
        onTruckList.clear();
        onTruckList.add(new LineItem());
        onTruckList.addAll(list);
    }

    public List<Participant> getParticipantList() {
        return participantList;
    }

    public void setParticipantList(List<Participant> participantList) {
        this.participantList = participantList;
    }

    public List<Comment> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<Comment> commentList) {
        this.commentList = commentList;
    }

    public List<pieceOfEquipment> getEquipmentList() {
        return equipmentList;
    }

    public void setEquipmentList(List<pieceOfEquipment> equipmentList) {
        this.equipmentList = equipmentList;
    }

    public AppointmentCustomField getCustomField() {
        return customField;
    }

    public void setCustomField(AppointmentCustomField customField) {
        this.customField = customField;
    }

    public AppointmentCustomField getSecondCustomField() {
        return secondCustomField;
    }

    public void setSecondCustomField(AppointmentCustomField secondCustomField) {
        this.secondCustomField = secondCustomField;
    }

    public List<Agreement> getAgreementList() {
        return agreementList;
    }

    public void setAgreementList(List<Agreement> agreementList) {
        this.agreementList = agreementList;
    }

    public String getWorkOrderNumberPrefix() {
        return workOrderNumberPrefix;
    }

    public void setWorkOrderNumberPrefix(String workOrderNumberPrefix) {
        this.workOrderNumberPrefix = workOrderNumberPrefix;
    }

    public String getPhone1() {
        return phone1;
    }

    public void setPhone1(String phone1) {
        this.phone1 = phone1;
    }

    public String getPhone1Description() {
        return phone1Description;
    }

    public void setPhone1Description(String phone1Description) {
        this.phone1Description = phone1Description;
    }

    public String getPhone2() {
        return phone2;
    }

    public void setPhone2(String phone2) {
        this.phone2 = phone2;
    }

    public String getPhone2Description() {
        return phone2Description;
    }

    public void setPhone2Description(String phone2Description) {
        this.phone2Description = phone2Description;
    }

    public boolean isLocationTaxable() {
        return isLocationTaxable;
    }

    public void setLocationTaxable(boolean isLocationTaxable) {
        this.isLocationTaxable = isLocationTaxable;
    }
}