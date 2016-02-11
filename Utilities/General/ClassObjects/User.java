package com.skeds.android.phone.business.Utilities.General.ClassObjects;

import java.io.Serializable;
import java.util.TimeZone;

public class User implements Serializable {

    // Variables
    private int id;
    private int type;
    private String firstName;
    private String lastName;
    private String email;
    private int numberOfUnreadBulletins;
    private boolean timeTrackable;
    private int serviceProviderId;
    private boolean allowViewAllCustomers;
    private int ownerId;
    private String ownerName;
    private String country;
    private boolean allowAddEditAppointments;
    private boolean requireSignerNameOnInvoice;
    private boolean barcodesForEquipment;
    private boolean barcodesForLocations;
    private boolean offerEstimateUponFinish;
    private boolean removeTermLocationFromMobile;
    private boolean forceSignatureOnEstimate;
    private boolean usingTimeClock;
    private boolean allowEditPrice;

    private boolean agreedToTOS;
    private String latestAndroidVersion;
    private boolean c2dmRegistered;
    private boolean loggedIn;
    private boolean needToUpdateApplication;
    private boolean useOnlyBillLater;
    private boolean useLargeScaleSearch;
    private boolean allowPartOrdering;
    private boolean canadian;
    private boolean useCardReader;

    /* User Permissions */
    private boolean permissionOwner;
    private boolean permissionDispatcher;
    private boolean permissionServiceProvider;
    private boolean permissionSupervisor;

    private boolean displayDisclaimer;

    /* country info */
    private Country countryInfo = new Country();
    private TimeZone timeZone;
    private boolean forceSelectionOfLeadSourceForAppt;
    private boolean forceSelectionOfLeadSourceForCustomers;

    // Default Constructor
    public User() {
        setId(0);
        setType(0);
        setFirstName("");
        setLastName("");
        setEmail("");
        setNumberOfUnreadBulletins(0);
        setTimeTrackable(false);
        setServiceProviderId(0);
        setAllowViewAllCustomers(false);
        setOwnerId(0);
        setOwnerName("");
        setCountry("");
        setAllowAddEditAppointments(false);
        setBarcodesForEquipment(false);
        setBarcodesForLocations(false);
        setOfferEstimateUponFinish(false);
        setRequireSignerNameOnInvoice(false);
        setRemoveTermLocationFromMobile(false);
        setForceSignatureOnEstimate(false);
        setUsingTimeClock(false);
        setPermissionOwner(false);
        setPermissionDispatcher(false);
        setPermissionServiceProvider(false);
        setPermissionSupervisor(false);
        setAllowEditPrice(true);
    }

    boolean allowAddToInvoicePDF;

    public Country getCountryInfo() {
        return countryInfo;
    }

    // Mutators
    public void setId(int value) {
        this.id = value;
    }

    public void setAllowEditPrice(boolean allow) {
        allowEditPrice = allow;
    }

    public void setFirstName(String value) {
        this.firstName = value;
    }

    public void setLastName(String value) {
        this.lastName = value;
    }

    public void setEmail(String value) {
        this.email = value;
    }

    public void setNumberOfUnreadBulletins(int value) {
        this.numberOfUnreadBulletins = value;
    }

    public void setTimeTrackable(boolean value) {
        this.timeTrackable = value;
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public boolean isAllowEditPrice() {
        return allowEditPrice;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public int getNumberOfUnreadBulletins() {
        return numberOfUnreadBulletins;
    }

    public boolean getTimeTrackable() {
        return timeTrackable;
    }

    public int getServiceProviderId() {
        return serviceProviderId;
    }

    public void setServiceProviderId(int value) {
        this.serviceProviderId = value;
    }

    public boolean isAllowViewAllCustomers() {
        return allowViewAllCustomers;
    }

    public void setAllowViewAllCustomers(boolean value) {
        this.allowViewAllCustomers = value;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int value) {
        this.ownerId = value;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String value) {
        this.ownerName = value;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String value) {
        this.country = value;
    }

    public boolean isAllowAddEditAppointments() {
        return allowAddEditAppointments;
    }

    public void setAllowAddEditAppointments(boolean value) {
        this.allowAddEditAppointments = value;
    }

    public String getLatestAndroidVersion() {
        return latestAndroidVersion;
    }

    public void setLatestAndroidVersion(String value) {
        this.latestAndroidVersion = value;
    }

    public int getType() {
        return type;
    }

    public void setType(int value) {
        this.type = value;
    }

    public boolean usesBarcodesForEquipment() {
        return barcodesForEquipment;
    }

    public void setBarcodesForEquipment(boolean value) {
        this.barcodesForEquipment = value;
    }

    public boolean usesBarcodesForLocations() {
        return barcodesForLocations;
    }

    public void setBarcodesForLocations(boolean value) {
        this.barcodesForLocations = value;
    }

    public boolean isRequireSignerNameOnInvoice() {
        return requireSignerNameOnInvoice;
    }

    public void setRequireSignerNameOnInvoice(boolean value) {
        this.requireSignerNameOnInvoice = value;
    }

    public boolean isOfferEstimateUponFinish() {
        return offerEstimateUponFinish;
    }

    public void setOfferEstimateUponFinish(boolean value) {
        this.offerEstimateUponFinish = value;
    }

    public boolean isRemoveTermLocationFromMobile() {
        return removeTermLocationFromMobile;
    }

    public void setRemoveTermLocationFromMobile(boolean value) {
        this.removeTermLocationFromMobile = value;
    }

    public boolean isForceSignatureOnEstimate() {
        return forceSignatureOnEstimate;
    }

    public void setForceSignatureOnEstimate(boolean value) {
        this.forceSignatureOnEstimate = value;
    }

    public boolean isC2dmRegistered() {
        return c2dmRegistered;
    }

    public void setC2dmRegistered(boolean value) {
        this.c2dmRegistered = value;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean value) {
        this.loggedIn = value;
    }

    public boolean isNeedToUpdateApplication() {
        return needToUpdateApplication;
    }

    public void setNeedToUpdateApplication(boolean value) {
        this.needToUpdateApplication = value;
    }

    public boolean isUseOnlyBillLater() {
        return useOnlyBillLater;
    }

    public void setUseOnlyBillLater(boolean value) {
        this.useOnlyBillLater = value;
    }

    public boolean isUseLargeScaleSearch() {
        return useLargeScaleSearch;
    }

    public void setUseLargeScaleSearch(boolean value) {
        this.useLargeScaleSearch = value;
    }

    public boolean isAllowPartOrdering() {
        return allowPartOrdering;
    }

    public void setAllowPartOrdering(boolean value) {
        this.allowPartOrdering = value;
    }

    public boolean isCanadian() {
        return canadian;
    }

    public void setCanadian(boolean value) {
        this.canadian = value;
    }

    public boolean isUseCardReader() {
        return useCardReader;
    }

    public void setUseCardReader(boolean value) {
        this.useCardReader = value;
    }

    public boolean isAgreedToTOS() {
        return agreedToTOS;
    }

    public void setAgreedToTOS(boolean value) {
        this.agreedToTOS = value;
    }

    public boolean isUsingTimeClock() {
        return usingTimeClock;
    }

    public void setUsingTimeClock(boolean value) {
        this.usingTimeClock = value;
    }

    public boolean isPermissionOwner() {
        return permissionOwner;
    }

    public void setPermissionOwner(boolean value) {
        this.permissionOwner = value;
    }

    public boolean isPermissionDispatcher() {
        return permissionDispatcher;
    }

    public void setPermissionDispatcher(boolean value) {
        this.permissionDispatcher = value;
    }

    public boolean isPermissionServiceProvider() {
        return permissionServiceProvider;
    }

    public void setPermissionServiceProvider(boolean value) {
        this.permissionServiceProvider = value;
    }

    public boolean isPermissionSupervisor() {
        return permissionSupervisor;
    }

    public void setPermissionSupervisor(boolean value) {
        this.permissionSupervisor = value;
    }

    public void settimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }

    public TimeZone getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }

    public boolean isDisplayDisclaimer() {
        return displayDisclaimer;
    }

    public void setDisplayDisclaimer(boolean displayDisclaimer) {
        this.displayDisclaimer = displayDisclaimer;
    }

    public void setForceSelectionOfLeadSourceForAppt(boolean forceSelectionOfLeadSourceForAppt) {

        this.forceSelectionOfLeadSourceForAppt = forceSelectionOfLeadSourceForAppt;
    }

    public void setForceSelectionOfLeadSourceForCustomers(boolean forceSelectionOfLeadSourceForCustomers) {

        this.forceSelectionOfLeadSourceForCustomers = forceSelectionOfLeadSourceForCustomers;
    }

    public boolean isAllowAddToInvoicePDF() {
        return allowAddToInvoicePDF;
    }

    public void setAllowAddToInvoicePDF(boolean allowAddToInvoicePDF) {
        this.allowAddToInvoicePDF = allowAddToInvoicePDF;
    }
}