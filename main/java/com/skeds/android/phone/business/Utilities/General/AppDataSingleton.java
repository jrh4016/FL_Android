package com.skeds.android.phone.business.Utilities.General;

import android.graphics.Bitmap;
import android.util.Log;

import com.skeds.android.phone.business.Utilities.General.ClassObjects.Agreement;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Appointment;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.AppointmentListItem;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Country;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.CustomQuestion;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Customer;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.DashboardStatus;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Estimate;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Generic;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.GroupCode;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Invoice;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.InvoiceList;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.InvoicePartOrder;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.LeadSource;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.LineItem;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Manufacturer;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.NewAppointment;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.PdfDocument;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.PricebookProduct;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.ServiceProvider;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.TimeClockTechnician;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.UpcomingAppointment;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.UserPhoto;
import com.skeds.android.phone.business.core.SkedsApplication;
import com.skeds.android.phone.business.model.CustomerEquipment;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AppDataSingleton implements Serializable {

    private static AppDataSingleton instance;
    private List<LeadSource> leadSourceListItem = new ArrayList<LeadSource>();

    private AppDataSingleton() {

    }

    public static AppDataSingleton getInstance() {

        if (instance == null) {

            AppDataSingleton data = SkedsApplication.getInstance().getAppDataFromFile();
            if (data != null) {
                Log.d("file_transaction", "App Data Have Been Retrieved From File");
                instance = data;
                return data;
            }
            Log.e("APPLICATION_DATA", "!App Data Instance Created!");
            instance = new AppDataSingleton();
        }
        return instance;
    }

    public static AppDataSingleton getReference() {
        return instance;
    }

    public static void clear() {
        instance = null;
    }

    private DashboardStatus technicianStatus; // This is the Status/Name
    // on Dashboard

    /* This is the customer being viewed at the present moment */
    private Customer customer = new Customer();

    // TODO - There's a good chance that "Status Customer" is never used
    private Customer statusCustomer = new Customer();

    /* This is the specific Appointment currently being viewed */
    private Appointment appointment = new Appointment();

    /* This is the specific Appointment in history being viewed */
    private Appointment pastAppointment = new Appointment();

    /* This is the specific Invoice being viewed */
    private Invoice invoice = new Invoice();

    /* The specific estimate being viewed */
    private Estimate estimate = new Estimate();

    /*
     * This is the specific Invoice being viewed through a selected customer's
     * history
     */
    // TODO - This is probably unnecessary
    private Invoice pastInvoice = new Invoice();

    /* This is the specific equipment being viewed */
    private CustomerEquipment equipment;

//	/* This holds the equipment list for a selected customer */
//	private List<Equipment> equipmentList = new ArrayList<Equipment>();

    /* This is the set of manufacturers for equipment */
    private List<Manufacturer> equipmentManufacturerList = new ArrayList<Manufacturer>();

    /* Custom Questions/Checklist Info */
    private List<CustomQuestion> customQuestionList = new ArrayList<CustomQuestion>();

    /* Error Handling */
    private ErrorUtilities errorUtil = new ErrorUtilities();

    /* "On Truck" Parts */
    private List<LineItem> onTruckPartsList = new ArrayList<LineItem>();

    /* New Appointments (Add Appoitnments View) */
    // TODO - is this necessary??
    private NewAppointment newAppointment;

    /* List of appointments in history (Viewed through customer information) */
    private List<Appointment> pastAppointmentList = new ArrayList<Appointment>();

    /* List of customers (when pressing the "customers" button from dashboard) */
    private List<Customer> customerList = new ArrayList<Customer>();

    private List<UpcomingAppointment> upcomingAppointmentsList = new ArrayList<UpcomingAppointment>();

    /* Supervisor's Technicians for Time Clock */
    private List<TimeClockTechnician> clockTechnician = new ArrayList<TimeClockTechnician>();

    /* "Hours Worked" container */
    // TODO - Necessary?
    private ServiceProvider hoursWorked;

    /*
     * This stores information for all of the photos on a given
     * customer/appt/equip
     */
    private List<UserPhoto> photoList = new ArrayList<UserPhoto>();

    private List<LineItem> lineItemList = new ArrayList<LineItem>();

    private List<InvoiceList> invoiceList = new ArrayList<InvoiceList>();

    private String estimateDisclaimerMessage;

    /* Pricebook Information */
    private List<LineItem> pricebookProductList = new ArrayList<LineItem>();
    private List<GroupCode> pricebookGroupCodeList = new ArrayList<GroupCode>();
    private List<Manufacturer> pricebookManufacturerList = new ArrayList<Manufacturer>();
    private List<PricebookProduct> pricebookOwnerProductList = new ArrayList<PricebookProduct>();

    /* Service Agreement Information */
    private Agreement serviceAgreement = new Agreement();
    private List<Agreement> serviceAgreementList = new ArrayList<Agreement>();
    private List<Generic> singleAgreementLocationAndEquipmentList = new ArrayList<Generic>();
    private List<Generic> singleAgreementEquipmentList = new ArrayList<Generic>();

    private BigDecimal discountAmount = new BigDecimal("0.0").setScale(2, BigDecimal.ROUND_HALF_UP);

    private int invoiceListParticipantId;
    private int invoiceListOwnerId;

    private List<Generic> servicePlanList = new ArrayList<Generic>();
    private List<InvoicePartOrder> partOrderList = new ArrayList<InvoicePartOrder>();

    private int serviceProviderId;

    /* Viewing a specific invoice modifiers and lineItems container */
    private boolean calculateSalesTaxFirst;
    private boolean allTechniciansFinished;

	/* Collection of View-Modes (path to activity) */
    // private int estimateMode;
    // private int invoiceMode;
    // private int lineItemMode;

    private int appointmentViewMode;
    private int appointmentAddViewMode;
    private int customerViewMode;
    private int estimateViewMode;
    private int estimateViewType;
    private int estimateListViewMode;
    private int equipmentAddViewMode;
    private int equipmentViewMode;
    private int invoiceViewMode;
    private int serviceAgreementAddViewMode;
    private int serviceAgreementViewMode;
    private int signatureViewMode;

    /* What type of pricebook search we're dealing with */
    private int priceBookSearchMode;
    private String priceBookSearchModeOverride;

    /* Disclaimer Data */
    private boolean forceDisclaimer;
    private String disclaimerMessage;

    private boolean haveNewAppt = false;

    /* Tax Rate */
    private BigDecimal customerEstimateTaxRate = new BigDecimal("0.00000");

    // TODO
    // TODO
    // TODO
    /* Set of stored IDs for customer estimates */
    public int[] customerEstimateId;
    public String[] customerEstimateDate;
    public double[] customerEstimateCost;
    // public double customerEstimateTaxRate;

    /* Appointment List items (List of Appointments View) */
    private List<AppointmentListItem> appointmentList = new ArrayList<AppointmentListItem>();
    private String partOrderCustomFieldName1 = "";
    private String partOrderCustomFieldName2 = "";
    private String partOrderCustomFieldName3 = "";

    private String equipmentCustomFieldName1 = "";
    private String equipmentCustomFieldName2 = "";
    private String equipmentCustomFieldName3 = "";

    private String errorDescription = "";

    private List<PdfDocument> pdfDocsList = new ArrayList<PdfDocument>();
    private List<PdfDocument> pdfTemplatesList = new ArrayList<PdfDocument>();

    private Bitmap downloadedBitmap = null;

    private List<Country> allCountries = new ArrayList<Country>();

    /**
     * * Start of mutators
     * *
     */


    public DashboardStatus getTechnicianStatus() {
        return technicianStatus;
    }

    public void setTechnicianStatus(DashboardStatus status) {
        this.technicianStatus = status;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer value) {
        this.customer = value;
    }

    public Customer getStatusCustomer() {
        return statusCustomer;
    }

    public void setStatusCustomer(Customer value) {
        this.statusCustomer = value;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment value) {
        this.appointment = value;
    }

    public Appointment getPastAppointment() {
        return pastAppointment;
    }

    public void setPastAppointment(Appointment value) {
        this.pastAppointment = value;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice value) {
        this.invoice = value;
    }

    public Estimate getEstimate() {
        return estimate;
    }

    public void setEstimate(Estimate value) {
        this.estimate = value;
    }

    public Invoice getPastInvoice() {
        return pastInvoice;
    }

    public void setPastInvoice(Invoice value) {
        this.pastInvoice = value;
    }

    public CustomerEquipment getEquipment() {
        return equipment;
    }

    public void setEquipment(CustomerEquipment value) {
        this.equipment = value;
    }

//	public  List<Equipment> getEquipmentList() {
//		return equipmentList;
//	}

//	public  void setEquipmentList(List<Equipment> list) {
//		this.equipmentList = list;
//	}

    public List<Manufacturer> getEquipmentManufacturerList() {
        return equipmentManufacturerList;
    }

    public void setEquipmentManufacturerList(List<Manufacturer> list) {
        this.equipmentManufacturerList = list;
    }

    public List<CustomQuestion> getCustomQuestionList() {
        return customQuestionList;
    }

    public void setCustomQuestionList(List<CustomQuestion> list) {
        this.customQuestionList = list;
    }

    public ErrorUtilities getErrorUtility() {
        return errorUtil;
    }

    public void setErrorUtility(ErrorUtilities value) {
        this.errorUtil = value;
    }

    public List<LineItem> getOnTruckPartsList() {
        return onTruckPartsList;
    }

    public void setOnTruckPartsList(List<LineItem> list) {
        this.onTruckPartsList = list;
    }

    public NewAppointment getNewAppointment() {
        return newAppointment;
    }

    public void setNewAppointment(NewAppointment value) {
        this.newAppointment = value;
    }

    public List<Appointment> getPastAppointmentList() {
        return pastAppointmentList;
    }

    public void setPastAppointmentList(List<Appointment> list) {
        this.pastAppointmentList = list;
    }

    public List<Customer> getCustomerList() {
        return customerList;
    }

    public Customer getCustomerById(int id) {
        if (customer != null && customer.getId() == id)
            return customer;
        if (customerList != null)
            for (Customer cust : customerList)
                if (cust.getId() == id)
                    return cust;
        return null;
    }

    public String getEsitmateDisclaimerMessage() {
        return estimateDisclaimerMessage;
    }

    public void setEstimateDisclaimerMessage(String estimateDisclaimerMessage) {
        this.estimateDisclaimerMessage = estimateDisclaimerMessage;
    }

    public void setCustomerList(List<Customer> list) {
        this.customerList = list;
    }

    public List<UpcomingAppointment> getUpcomingAppointmentsList() {
        return upcomingAppointmentsList;
    }

    public void setUpcomingAppointmentsList(
            List<UpcomingAppointment> list) {
        this.upcomingAppointmentsList = list;
    }

    public List<TimeClockTechnician> getClockTechnician() {
        return clockTechnician;
    }

    public void setClockTechnician(List<TimeClockTechnician> list) {
        this.clockTechnician = list;
        this.clockTechnician.add(0, new TimeClockTechnician());
    }

    public ServiceProvider getHoursWorked() {
        return hoursWorked;
    }

    public void setHoursWorked(ServiceProvider value) {
        this.hoursWorked = value;
    }

    public List<UserPhoto> getPhotoList() {
        return photoList;
    }

    public void setPhotoList(List<UserPhoto> list) {
        this.photoList = list;
    }

    public List<LineItem> getLineItemList() {
        return lineItemList;
    }

    public void setLineItemList(List<LineItem> list) {
        this.lineItemList = list;
    }

    public List<InvoiceList> getInvoiceList() {
        return invoiceList;
    }

    public void setInvoiceList(List<InvoiceList> list) {
        this.invoiceList = list;
    }

    public List<LineItem> getPricebookProductList() {
        return pricebookProductList;
    }

    public void setPricebookProductList(List<LineItem> list) {
        this.pricebookProductList = list;
    }

    public List<GroupCode> getPricebookGroupCodeList() {
        return pricebookGroupCodeList;
    }

    public void setPricebookGroupCodeList(List<GroupCode> list) {
        this.pricebookGroupCodeList = list;
    }

    public List<Manufacturer> getPricebookManufacturerList() {
        return pricebookManufacturerList;
    }

    public void setPricebookManufacturerList(List<Manufacturer> list) {
        this.pricebookManufacturerList = list;
    }

//	public  List<PricebookProduct> getPricebookOwnerProductList() {
//		return pricebookOwnerProductList;
//	}
//
//	public  void setPricebookOwnerProductList(List<PricebookProduct> list) {
//		this.pricebookOwnerProductList = list;
//	}

    public Agreement getServiceAgreement() {
        return serviceAgreement;
    }

    public void setServiceAgreement(Agreement value) {
        this.serviceAgreement = value;
    }

    public List<Agreement> getServiceAgreementList() {
        return serviceAgreementList;
    }

    public void setServiceAgreementList(List<Agreement> list) {
        this.serviceAgreementList = list;
    }

    public List<Generic> getSingleAgreementLocationAndEquipmentList() {
        return singleAgreementLocationAndEquipmentList;
    }

    public void setSingleAgreementLocationAndEquipmentList(
            List<Generic> list) {
        this.singleAgreementLocationAndEquipmentList = list;
    }

    public List<Generic> getSingleAgreementEquipmentList() {
        return singleAgreementEquipmentList;
    }

    public void setSingleAgreementEquipmentList(List<Generic> list) {
        this.singleAgreementEquipmentList = list;
    }

    public int getInvoiceListParticipantId() {
        return invoiceListParticipantId;
    }

    public void setInvoiceListParticipantId(int id) {
        this.invoiceListParticipantId = id;
    }

    public int getInvoiceListOwnerId() {
        return invoiceListOwnerId;
    }

    public void setInvoiceListOwnerId(int id) {
        this.invoiceListOwnerId = id;
    }

    public List<Generic> getServicePlanList() {
        return servicePlanList;
    }

    public void setServicePlanList(List<Generic> list) {
        this.servicePlanList = list;
    }

	/*
	 * public  List<InvoicePartOrder> getPartOrderList() { return
	 * partOrderList; }
	 * 
	 * public  void setPartOrderList(List<InvoicePartOrder> list) {
	 * this.partOrderList = list; }
	 */

    public int getServiceProviderId() {
        return serviceProviderId;
    }

    public void setServiceProviderId(int id) {
        this.serviceProviderId = id;
    }

    public boolean isCalculateSalesTaxFirst() {
        return calculateSalesTaxFirst;
    }

    public void setCalculateSalesTaxFirst(boolean value) {
        this.calculateSalesTaxFirst = value;
    }

    public boolean isAllTechniciansFinished() {
        return allTechniciansFinished;
    }

    public void setAllTechniciansFinished(boolean value) {
        this.allTechniciansFinished = value;
    }

    public int getPriceBookSearchMode() {
        return priceBookSearchMode;
    }

    public void setPriceBookSearchMode(int mode) {
        this.priceBookSearchMode = mode;
    }

    public String getPriceBookSearchModeOverride() {
        return priceBookSearchModeOverride;
    }

    public void setPriceBookSearchModeOverride(String mode) {
        this.priceBookSearchModeOverride = mode;
    }

    public boolean isForceDisclaimer() {
        return forceDisclaimer;
    }

    public void setForceDisclaimer(boolean value) {
        this.forceDisclaimer = value;
    }

    public String getDisclaimerMessage() {
        return disclaimerMessage;
    }

    public void setDisclaimerMessage(String message) {
        this.disclaimerMessage = message;
    }

    public void setPartOrderCustomFieldName1(String name) {
        partOrderCustomFieldName1 = name;
    }

    public String getPartOrderCustomFieldName1() {
        return partOrderCustomFieldName1;
    }

    public void setPartOrderCustomFieldName2(String name) {
        partOrderCustomFieldName2 = name;
    }

    public String getPartOrderCustomFieldName2() {
        return partOrderCustomFieldName2;
    }

    public void setPartOrderCustomFieldName3(String name) {
        partOrderCustomFieldName3 = name;
    }

    public String getPartOrderCustomFieldName3() {
        return partOrderCustomFieldName3;
    }

    // get set for equipment's custom fields

    public void setEquipmentCustomFieldName1(String name) {
        equipmentCustomFieldName1 = name;
    }

    public String getEquipmentCustomFieldName1() {
        return equipmentCustomFieldName1;
    }

    public void setEquipmentCustomFieldName2(String name) {
        equipmentCustomFieldName2 = name;
    }

    public String getEquipmentCustomFieldName2() {
        return equipmentCustomFieldName2;
    }

    public void setEquipmentCustomFieldName3(String name) {
        equipmentCustomFieldName3 = name;
    }

    public String getEquipmentCustomFieldName3() {
        return equipmentCustomFieldName3;
    }

    public BigDecimal getCustomerEstimateTaxRate() {
        return customerEstimateTaxRate;
    }

    public void setCustomerEstimateTaxRate(BigDecimal taxRate) {
        this.customerEstimateTaxRate = taxRate;
    }

    public int getCustomerViewMode() {
        return customerViewMode;
    }

    public void setCustomerViewMode(int mode) {
        this.customerViewMode = mode;
    }

    public int getAppointmentViewMode() {
        return appointmentViewMode;
    }

    public void setAppointmentViewMode(int mode) {
        this.appointmentViewMode = mode;
    }

    public int getAppointmentAddViewMode() {
        return appointmentAddViewMode;
    }

    public void setAppointmentAddViewMode(int mode) {
        this.appointmentAddViewMode = mode;
    }

    public int getServiceAgreementAddViewMode() {
        return serviceAgreementAddViewMode;
    }

    public void setServiceAgreementAddViewMode(int mode) {
        this.serviceAgreementAddViewMode = mode;
    }

    public int getServiceAgreementViewMode() {
        return serviceAgreementViewMode;
    }

    public void setServiceAgreementViewMode(int mode) {
        this.serviceAgreementViewMode = mode;
    }

    public int getSignatureViewMode() {
        return signatureViewMode;
    }

    public void setSignatureViewMode(int mode) {
        this.signatureViewMode = mode;
    }

    public int getEstimateViewMode() {
        return estimateViewMode;
    }

    public void setEstimateViewMode(int mode) {
        this.estimateViewMode = mode;
    }

    public int getEstimateListViewMode() {
        return estimateListViewMode;
    }

    public void setEstimateListViewMode(int mode) {
        this.estimateListViewMode = mode;
    }

    public int getInvoiceViewMode() {
        return invoiceViewMode;
    }

    public void setInvoiceViewMode(int mode) {
        this.invoiceViewMode = mode;
    }

    public int getEquipmentViewMode() {
        return equipmentViewMode;
    }

    public void setEquipmentViewMode(int mode) {
        this.equipmentViewMode = mode;
    }

    public int getEstimateViewType() {
        return estimateViewType;
    }

    public void setEstimateViewType(int type) {
        this.estimateViewType = type;
    }

    public List<AppointmentListItem> getAppointmentList() {
        return appointmentList;
    }

    public void setAppointmentList(List<AppointmentListItem> list) {
        this.appointmentList = list;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String descr) {
        errorDescription = descr;
    }

    public boolean isCreatedNewAppt() {
        return haveNewAppt;
    }

    public void setNewApptStatus(boolean status) {
        haveNewAppt = status;
    }

    public List<PdfDocument> getPdfDocsList() {
        return pdfDocsList;
    }

    public List<PdfDocument> getPdfTemplatesList() {
        return pdfTemplatesList;
    }

    public Bitmap getBitmap() {
        return downloadedBitmap;
    }

    public void setBitmap(Bitmap bmp) {
        downloadedBitmap = bmp;
    }

    public void recycleBitmap() {
        if (downloadedBitmap != null) {
            downloadedBitmap.recycle();
            downloadedBitmap = null;
        }
    }


    public List<Country> getAllCountries() {
        return allCountries;
    }

    public void setAllCountries(List<Country> allCountries) {
        this.allCountries = allCountries;
    }

    public void setSourcesListItem(List<LeadSource> leadSourceListItem) {

        this.leadSourceListItem = leadSourceListItem;
    }

    public List<LeadSource> getLeadSourceListItem() {
        return leadSourceListItem;
    }
}
