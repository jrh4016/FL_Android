package com.skeds.android.phone.business.Utilities.REST.Objects;

import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Country;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Region;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.TaxRate;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.TaxRateType;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.User;
import com.skeds.android.phone.business.Utilities.General.Constants;
import com.skeds.android.phone.business.Utilities.General.UserUtilitiesSingleton;
import com.skeds.android.phone.business.Utilities.NonfatalException;
import com.skeds.android.phone.business.Utilities.REST.RestConnector;
import com.skeds.android.phone.business.core.SkedsApplication;

import org.jdom2.Document;
import org.jdom2.Element;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

public class RESTAccount {

    private static final String KEY_DEVICE_INFO = "deviceInfo";
    private static final String KEY_OS_TYPE = "osType";
    private static final String KEY_ANDROID = "android";
    private static final String KEY_OS_VERSION = "osVersion";
    private static final String KEY_APP_VERSION = "appVersion";
    private static final String KEY_DEVICE_DETAILS = "deviceDetails";
    private static final String KEY_MANUFACTURER = "manufacturer";
    private static final String KEY_MODEL = "model";
    private static final String KEY_DEVICE = "device";
    private static final String KEY_DISPLAY = "display";
    private static final String KEY_PROVIDER = "provider";
    private static final String KEY_DEVICE_TOKEN = "deviceToken";
    private static final String KEY_TZ = "tz";
    private static final String KEY_USER = "user";
    private static final String KEY_ID = "id";
    private static final String KEY_FIRST_NAME = "firstName";
    private static final String KEY_LAST_NAME = "lastName";
    private static final String KEY_USE_LARGE_SCALE_SEARCH = "useLargeScaleSearch";
    private static final String KEY_ACCEPTED_TOS = "acceptedTos";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_ALLOW_VIEW_ALL_CUSTOMERS = "allowViewAllCustomers";
    private static final String KEY_ENABLED = "enabled";
    private static final String KEY_ALLOW_EDIT_PRICE = "allowEditPrice";
    private static final String KEY_SERVICE_PROVIDER_ID = "serviceProviderId";
    private static final String KEY_REQUIRE_SIGNER_NAME_ON_INVOICE = "requireSignerNameOnInvoice";
    private static final String KEY_OFFER_ESTIMATE_UPON_FINISH = "offerEstimateUponFinish";
    private static final String KEY_USE_BAR_CODES_FOR_EQUIPMENT = "useBarCodesForEquipment";
    private static final String KEY_USE_BAR_CODES_FOR_LOCATIONS = "useBarCodesForLocations";
    private static final String KEY_REMOVE_TERM_LOCATION_FROM_MOBILE = "removeTermLocationFromMobile";
    private static final String KEY_ENABLE_TIME_CLOCK = "enableTimeClock";
    private static final String KEY_USER_PERMISSIONS = "userPermissions";
    private static final String KEY_OWNER = "owner";
    private static final String KEY_TAX_RATES = "taxRates";
    private static final String KEY_RATE = "rate";
    private static final String KEY_NAME = "name";
    private static final String KEY_RATE_TYPE = "rateType";
    private static final String KEY_RATE_VALUE = "rateValue";
    private static final String KEY_TAX_RATE_TYPES = "taxRateTypes";
    private static final String KEY_TYPE = "type";
    private static final String KEY_COUNTRY = "country";
    private static final String KEY_LOCALE_CODE = "localeCode";
    private static final String KEY_CURRENCY_SYMBOL = "currencySymbol";
    private static final String KEY_SHORT_CODE = "shortCode";
    private static final String KEY_PROVINCE_LABEL = "provinceLabel";
    private static final String KEY_USE_EXTENDED_TAX = "useExtendedTax";
    private static final String KEY_REGIONS = "regions";
    private static final String KEY_REGION = "region";
    private static final String KEY_LABEL = "label";
    private static final String KEY_ZIP_CODE_PATTERN = "zipCodePattern";
    private static final String KEY_LENGTH = "length";
    private static final String KEY_ZIP_CODE_LABEL = "zipCodeLabel";
    private static final String KEY_PATTERN = "pattern";
    private static final String KEY_USA = "USA";
    private static final String KEY_PRICEBOOK_SEARCH_MODE = "priceBookSearchMode";
    private static final String KEY_ONE_TIER = "one_tier";
    private static final String KEY_TWO_TIER = "two_tier";
    private static final String KEY_ALLOW_ADD_EDIT_APPOINTMENTS = "allowAddEditAppointments";
    private static final String KEY_ANDROID_VERSION = "androidVersion";
    private static final String KEY_TIMETRACKING = "timeTracking";
    private static final String KEY_FORCE_SIGNATURE_ON_ESTIMATE = "forceSignatureOnEstimate";
    private static final String KEY_ALLOW_PART_ORDERING = "allowPartOrdering";
    private static final String KEY_DISCLAIMER = "disclaimer";
    private static final String KEY_ESTIMATE_DISCLAIMER = "estimateDisclaimer";
    private static final String KEY_PART_ORDER_CUSTOM_FIELD_NAME1 = "partOrderCustomFieldName1";
    private static final String KEY_PART_ORDER_CUSTOM_FIELD_NAME2 = "partOrderCustomFieldName2";
    private static final String KEY_PART_ORDER_CUSTOM_FIELD_NAME3 = "partOrderCustomFieldName3";
    private static final String KEY_EQUIPMENT_CUSTOM_FIELD_NAME1 = "equipmentCustomFieldName1";
    private static final String KEY_EQUIPMENT_CUSTOM_FIELD_NAME2 = "equipmentCustomFieldName2";
    private static final String KEY_EQUIPMENT_CUSTOM_FIELD_NAME3 = "equipmentCustomFieldName3";
    private static final String KEY_NEW_PASSWORD = "newPassword";
    private static final String KEY_COUNTRIES = "countries";
    private static final String KEY_ZIPCODE_LABEL = "zipCodeLabel";
    private static final String KEY_TIMEZONEINFO = "timeZoneInfo";
    private static final String KEY_ALLOW_SAVE_PHOTO_TO_INVOICE_PDF = "allowAddToInvoicePDF";


    /* Handles all information sent in when a user attempts the login command */
    public static void login(String applicationVersion, String operatingSystemVersion, String telecomOperator, String phoneManufacturer, String phoneModel,
                             String phoneDeviceName, String phoneDisplay, String phoneProvider, String gcmRegistrationId) throws NonfatalException {


        Element rootNode = prepareRequest(applicationVersion, operatingSystemVersion, phoneManufacturer, phoneModel, phoneDeviceName, phoneDisplay, phoneProvider, gcmRegistrationId);

        Document document = RestConnector.getInstance().httpPost(new Document(rootNode), SkedsApplication.getContext().getString(R.string.powerlogin_url));
        Element userNode = document.getRootElement().getChild(KEY_USER);

        User user = parseUser(userNode);

        UserUtilitiesSingleton.getInstance().user = user;
    }

    private static User parseUser(Element userNode) {
        User user = new User();
//UserUtilitiesSingleton.getInstance().user
        if (userNode.getAttributeValue(KEY_ID) != null)
            user.setId(Integer.parseInt(userNode.getAttributeValue(KEY_ID)));

        if (userNode.getChildText(KEY_FIRST_NAME) != null)
            user.setFirstName(userNode.getChildText(KEY_FIRST_NAME));

        if (userNode.getChildText(KEY_LAST_NAME) != null)
            user.setLastName(userNode.getChildText(KEY_LAST_NAME));

        if (userNode.getChildText(KEY_USE_LARGE_SCALE_SEARCH) != null)
            user.setUseLargeScaleSearch(Boolean.parseBoolean(userNode.getChildText(KEY_USE_LARGE_SCALE_SEARCH)));

        if (userNode.getChildText(KEY_ACCEPTED_TOS) != null)
            user.setAgreedToTOS(Boolean.parseBoolean(userNode.getChildText(KEY_ACCEPTED_TOS)));

        if (userNode.getChildText(KEY_EMAIL) != null)
            user.setEmail(userNode.getChildText(KEY_EMAIL));

        if (userNode.getChild(KEY_ALLOW_VIEW_ALL_CUSTOMERS) != null)
            user.setAllowViewAllCustomers(Boolean.parseBoolean(userNode.getChild(KEY_ALLOW_VIEW_ALL_CUSTOMERS)
                    .getAttributeValue(KEY_ENABLED)));

        if (userNode.getChild(KEY_ALLOW_EDIT_PRICE) != null)
            user.setAllowEditPrice(Boolean.parseBoolean(userNode.getChild(KEY_ALLOW_EDIT_PRICE).getAttributeValue(KEY_ENABLED)));

        if (userNode.getChildText(KEY_SERVICE_PROVIDER_ID) != null)
            user.setServiceProviderId(Integer.parseInt(userNode.getChildText(KEY_SERVICE_PROVIDER_ID)));

        if (userNode.getChild(KEY_REQUIRE_SIGNER_NAME_ON_INVOICE) != null)
            user.setRequireSignerNameOnInvoice(Boolean.parseBoolean(userNode.getChild(KEY_REQUIRE_SIGNER_NAME_ON_INVOICE)
                    .getAttributeValue(KEY_ENABLED)));

        if (userNode.getChild(KEY_OFFER_ESTIMATE_UPON_FINISH) != null)
            user.setOfferEstimateUponFinish(Boolean.parseBoolean(userNode.getChild(KEY_OFFER_ESTIMATE_UPON_FINISH)
                    .getAttributeValue(KEY_ENABLED)));

        if (userNode.getChild("forceSelectionOfLeadSourceForAppt") != null)
            user.setForceSelectionOfLeadSourceForAppt(Boolean.parseBoolean(userNode.getChildText("forceSelectionOfLeadSourceForAppt")));

        if (userNode.getChild("forceSelectionOfLeadSourceForCustomers") != null)
            user.setForceSelectionOfLeadSourceForCustomers(Boolean.parseBoolean(userNode.getChildText("forceSelectionOfLeadSourceForCustomers")));

        if (userNode.getChild(KEY_USE_BAR_CODES_FOR_EQUIPMENT) != null)
            user.setBarcodesForEquipment(Boolean.parseBoolean(userNode.getChild(KEY_USE_BAR_CODES_FOR_EQUIPMENT)
                    .getAttributeValue(KEY_ENABLED)));

        if (userNode.getChild("displayDisclaimer") != null)
            user.setDisplayDisclaimer(Boolean.parseBoolean(userNode.getChild("displayDisclaimer")
                    .getAttributeValue("enabled")));

        if (userNode.getChild(KEY_USE_BAR_CODES_FOR_LOCATIONS) != null)
            user.setBarcodesForLocations(Boolean.parseBoolean(userNode.getChild(KEY_USE_BAR_CODES_FOR_LOCATIONS)
                    .getAttributeValue(KEY_ENABLED)));

        if (userNode.getChild(KEY_REMOVE_TERM_LOCATION_FROM_MOBILE) != null)
            user.setRemoveTermLocationFromMobile(Boolean.parseBoolean(userNode.getChild(KEY_REMOVE_TERM_LOCATION_FROM_MOBILE)
                    .getAttributeValue(KEY_ENABLED)));

        if (userNode.getChild(KEY_ENABLE_TIME_CLOCK) != null)
            user.setUsingTimeClock(Boolean.parseBoolean(userNode.getChild(KEY_ENABLE_TIME_CLOCK).getAttributeValue(KEY_ENABLED)));

        if (userNode.getChild(KEY_USER_PERMISSIONS) != null) {
            String permissions = userNode.getChild(KEY_USER_PERMISSIONS).getText();
            String[] permissionSet = permissions.split(",");
            for (int i = 0; i < permissionSet.length; i++) {
                int permissionValue = Integer.parseInt(permissionSet[i]);
                switch (permissionValue) {
                    case 0:
                        user.setPermissionOwner(true);
                        break;
                    case 1:
                        user.setPermissionDispatcher(true);
                        break;

                    case 2:
                        user.setPermissionServiceProvider(true);
                        break;

                    case 3:
                        user.setPermissionSupervisor(true);
                        break;
                    default:
                        // Nothing
                        break;
                }
            }
        }

        if (userNode.getChild(KEY_OWNER) != null)
            user.setOwnerId(Integer.parseInt(userNode.getChild(KEY_OWNER).getAttributeValue(KEY_ID)));

        if (userNode.getChild(KEY_OWNER) != null)
            user.setOwnerName(userNode.getChild(KEY_OWNER).getText());

        user.getCountryInfo().getTaxRates().clear();
        user.getCountryInfo().getTaxRateTypes().clear();
        user.getCountryInfo().getRegions().clear();

        if (userNode.getChild(KEY_OWNER) != null) {

            if (userNode.getChild(KEY_OWNER).getChild(KEY_TAX_RATES) != null) {
                if (userNode.getChild(KEY_OWNER).getChild(KEY_TAX_RATES).getChildren(KEY_RATE) != null) {
                    List<Element> rates = userNode.getChild(KEY_OWNER).getChild(KEY_TAX_RATES).getChildren(KEY_RATE);
                    for (Element rate : rates) {
                        TaxRate tr = new TaxRate();
                        if (rate.getAttributeValue(KEY_ID) != null)
                            tr.setId(Integer.parseInt(rate.getAttributeValue(KEY_ID)));
                        if (rate.getAttributeValue(KEY_NAME) != null)
                            tr.setName(rate.getAttributeValue(KEY_NAME));
                        if (rate.getAttributeValue(KEY_RATE_TYPE) != null)
                            tr.setType(rate.getAttributeValue(KEY_RATE_TYPE));
                        if (rate.getAttributeValue(KEY_RATE_VALUE) != null)
                            tr.setValue(new BigDecimal(rate.getAttributeValue(KEY_RATE_VALUE)));
                        user.getCountryInfo().getTaxRates().add(tr);
                    }
                }
            }

            if (userNode.getChild(KEY_OWNER).getChild(KEY_TAX_RATE_TYPES) != null) {
                if (userNode.getChild(KEY_OWNER).getChild(KEY_TAX_RATE_TYPES).getChildren(KEY_RATE_TYPE) != null) {
                    List<Element> rateTypes = userNode.getChild(KEY_OWNER).getChild(KEY_TAX_RATE_TYPES).getChildren(KEY_RATE_TYPE);
                    for (Element rateType : rateTypes) {
                        TaxRateType trt = new TaxRateType();
                        if (rateType.getAttributeValue(KEY_NAME) != null)
                            trt.setName(rateType.getAttributeValue(KEY_NAME));
                        if (rateType.getAttributeValue(KEY_TYPE) != null)
                            trt.setType(rateType.getAttributeValue(KEY_TYPE));
                        user.getCountryInfo().getTaxRateTypes().add(trt);
                    }
                }
            }

            if (userNode.getChild(KEY_OWNER).getChild(KEY_TIMEZONEINFO) != null) {
                Element timeZoneInfoChild = userNode.getChild(KEY_OWNER).getChild(KEY_TIMEZONEINFO);
                String id = timeZoneInfoChild.getAttributeValue("id");
                TimeZone timeZoneInfo = TimeZone.getTimeZone(id);
                user.settimeZone(timeZoneInfo);
            }

            if (userNode.getChild(KEY_OWNER).getChild(KEY_COUNTRY) != null) {
                Element countryNode = userNode.getChild(KEY_OWNER).getChild(KEY_COUNTRY);

                if (countryNode.getAttributeValue(KEY_ID) != null)
                    user.getCountryInfo().setId(Integer.parseInt(countryNode.getAttributeValue(KEY_ID)));

                if (countryNode.getAttributeValue(KEY_NAME) != null)
                    user.getCountryInfo().setName(countryNode.getAttributeValue(KEY_NAME));

                if (countryNode.getAttributeValue(KEY_LOCALE_CODE) != null)
                    user.getCountryInfo().setLocalCode(countryNode.getAttributeValue(KEY_LOCALE_CODE));

                if (countryNode.getAttributeValue(KEY_CURRENCY_SYMBOL) != null)
                    user.getCountryInfo().setCurrencySymbol(countryNode.getAttributeValue(KEY_CURRENCY_SYMBOL));

                if (countryNode.getAttributeValue(KEY_SHORT_CODE) != null)
                    user.getCountryInfo().setShortCode(countryNode.getAttributeValue(KEY_SHORT_CODE));

                if (countryNode.getAttributeValue(KEY_PROVINCE_LABEL) != null)
                    user.getCountryInfo().setProvinceLabel(countryNode.getAttributeValue(KEY_PROVINCE_LABEL));

                if (countryNode.getAttributeValue(KEY_USE_EXTENDED_TAX) != null)
                    user.getCountryInfo().setUseExtendedTax(
                            Boolean.parseBoolean(countryNode.getAttributeValue(KEY_USE_EXTENDED_TAX)));

                if (countryNode.getChild(KEY_REGIONS) != null)
                    if (countryNode.getChild(KEY_REGIONS).getChildren(KEY_REGION) != null) {
                        List<Element> regions = countryNode.getChild(KEY_REGIONS).getChildren(KEY_REGION);
                        for (Element reg : regions) {
                            Region region = new Region();

                            if (reg.getAttributeValue(KEY_ID) != null)
                                region.setId(Integer.parseInt(reg.getAttributeValue(KEY_ID)));

                            if (reg.getAttributeValue(KEY_NAME) != null)
                                region.setName(reg.getAttributeValue(KEY_NAME));

                            if (reg.getAttributeValue(KEY_LABEL) != null)
                                region.setLabel(reg.getAttributeValue(KEY_LABEL));

                            user.getCountryInfo().getRegions().add(region);
                        }
                    }

                if (countryNode.getChild(KEY_ZIP_CODE_PATTERN) != null) {
                    Element zipElement = countryNode.getChild(KEY_ZIP_CODE_PATTERN);

                    if (zipElement.getAttributeValue(KEY_LENGTH) != null)
                        user.getCountryInfo().setZipCodeLength(Integer.parseInt(zipElement.getAttributeValue(KEY_LENGTH)));

                    if (zipElement.getAttributeValue(KEY_ZIP_CODE_LABEL) != null)
                        user.getCountryInfo().setZipCodeLabel(zipElement.getAttributeValue(KEY_ZIP_CODE_LABEL));

                    if (zipElement.getAttributeValue(KEY_PATTERN) != null) {
                        user.getCountryInfo().setZipCodePattern(zipElement.getAttributeValue(KEY_PATTERN));
                        if (user.getCountryInfo().getZipCodePattern().length() != 0)
                            user.getCountryInfo().setZipCodeLength(
                                    user.getCountryInfo().getZipCodePattern().length());
                    }
                }

            }

        }

        Country c = user.getCountryInfo();

        if (userNode.getChild(KEY_COUNTRY) != null) {
            user.setCountry(userNode.getChild(KEY_COUNTRY).getText());

            if (KEY_USA.equals(user.getCountry()))
                user.setCanadian(false);
            else
                user.setCanadian(true);
        }

        if (userNode.getChild(KEY_PRICEBOOK_SEARCH_MODE) != null) {
            if (KEY_ONE_TIER.equals(userNode.getChild(KEY_PRICEBOOK_SEARCH_MODE).getText()))
                AppDataSingleton.getInstance().setPriceBookSearchMode(Constants.PRICE_BOOK_SEARCH_MODE_ONE_TIER);
            else if (KEY_TWO_TIER.equals(userNode.getChild(KEY_PRICEBOOK_SEARCH_MODE).getText()))
                AppDataSingleton.getInstance().setPriceBookSearchMode(Constants.PRICE_BOOK_SEARCH_MODE_TWO_TIER);
        } else
            AppDataSingleton.getInstance().setPriceBookSearchMode(Constants.PRICE_BOOK_SEARCH_MODE_DEFAULT);


        if (userNode.getChild(KEY_ALLOW_ADD_EDIT_APPOINTMENTS) != null)
            user.setAllowAddEditAppointments(Boolean.parseBoolean(userNode.getChild(KEY_ALLOW_ADD_EDIT_APPOINTMENTS)
                    .getAttributeValue(KEY_ENABLED)));

        if (userNode.getChild(KEY_ANDROID_VERSION) != null)
            user.setLatestAndroidVersion(userNode.getChild(KEY_ANDROID_VERSION).getText());

        if (userNode.getChild(KEY_TIMETRACKING) != null)
            user.setTimeTrackable(Boolean.parseBoolean(userNode.getChild(KEY_TIMETRACKING).getAttributeValue(KEY_ENABLED)));

        if (userNode.getChild(KEY_SERVICE_PROVIDER_ID) != null)
            AppDataSingleton.getInstance().setServiceProviderId(Integer.parseInt(userNode.getChildText(KEY_SERVICE_PROVIDER_ID)));

        if (userNode.getChild(KEY_FORCE_SIGNATURE_ON_ESTIMATE) != null)
            user.setForceSignatureOnEstimate(Boolean.parseBoolean(userNode.getChild(KEY_FORCE_SIGNATURE_ON_ESTIMATE)
                    .getAttributeValue(KEY_ENABLED)));

        if (userNode.getChild(KEY_ALLOW_PART_ORDERING) != null)
            user.setAllowPartOrdering(Boolean.parseBoolean(userNode.getChild(KEY_ALLOW_PART_ORDERING).getAttributeValue(
                    KEY_ENABLED)));

        if (userNode.getChild(KEY_DISCLAIMER) != null)
            AppDataSingleton.getInstance().setDisclaimerMessage(userNode.getChild(KEY_DISCLAIMER).getText());
        else
            AppDataSingleton.getInstance().setDisclaimerMessage("");

        if (userNode.getChild(KEY_ESTIMATE_DISCLAIMER) != null)
            AppDataSingleton.getInstance().setEstimateDisclaimerMessage(userNode.getChild(KEY_ESTIMATE_DISCLAIMER).getText());
        else
            AppDataSingleton.getInstance().setEstimateDisclaimerMessage("");

        if (userNode.getChild(KEY_PART_ORDER_CUSTOM_FIELD_NAME1) != null)
            AppDataSingleton.getInstance().setPartOrderCustomFieldName1(userNode.getChild(KEY_PART_ORDER_CUSTOM_FIELD_NAME1).getText());
        if (userNode.getChild(KEY_PART_ORDER_CUSTOM_FIELD_NAME2) != null)
            AppDataSingleton.getInstance().setPartOrderCustomFieldName2(userNode.getChild(KEY_PART_ORDER_CUSTOM_FIELD_NAME2).getText());
        if (userNode.getChild(KEY_PART_ORDER_CUSTOM_FIELD_NAME3) != null)
            AppDataSingleton.getInstance().setPartOrderCustomFieldName3(userNode.getChild(KEY_PART_ORDER_CUSTOM_FIELD_NAME3).getText());

        if (userNode.getChild(KEY_EQUIPMENT_CUSTOM_FIELD_NAME1) != null)
            AppDataSingleton.getInstance().setEquipmentCustomFieldName1(userNode.getChild(KEY_EQUIPMENT_CUSTOM_FIELD_NAME1).getText());
        if (userNode.getChild(KEY_EQUIPMENT_CUSTOM_FIELD_NAME2) != null)
            AppDataSingleton.getInstance().setEquipmentCustomFieldName2(userNode.getChild(KEY_EQUIPMENT_CUSTOM_FIELD_NAME2).getText());
        if (userNode.getChild(KEY_EQUIPMENT_CUSTOM_FIELD_NAME3) != null)
            AppDataSingleton.getInstance().setEquipmentCustomFieldName3(userNode.getChild(KEY_EQUIPMENT_CUSTOM_FIELD_NAME3).getText());

        if (userNode.getChild(KEY_ALLOW_SAVE_PHOTO_TO_INVOICE_PDF) != null)
            user.setAllowAddToInvoicePDF(Boolean.parseBoolean(userNode.getChild(KEY_ALLOW_SAVE_PHOTO_TO_INVOICE_PDF).getAttribute("enabled").getValue()));

        user.setLoggedIn(true);
        return user;
    }

    private static Element prepareRequest(String applicationVersion, String operatingSystemVersion, String phoneManufacturer, String phoneModel, String phoneDeviceName, String phoneDisplay, String phoneProvider, String gcmRegistrationId) {
        Element rootNode = new Element(KEY_DEVICE_INFO);

        Element osTypeNode = new Element(KEY_OS_TYPE);
        osTypeNode.setText(KEY_ANDROID);
        rootNode.addContent(osTypeNode);

        // operatingSystemVersion = Build.VERSION.RELEASE;
        Element osVersionNode = new Element(KEY_OS_VERSION);
        osVersionNode.setText(operatingSystemVersion);
        rootNode.addContent(osVersionNode);

        Element appVersionNode = new Element(KEY_APP_VERSION);
        appVersionNode.setText(applicationVersion);
        rootNode.addContent(appVersionNode);

        Element deviceDetailsNode = new Element(KEY_DEVICE_DETAILS);
        deviceDetailsNode.setAttribute(KEY_MANUFACTURER, phoneManufacturer);
        deviceDetailsNode.setAttribute(KEY_MODEL, phoneModel);
        deviceDetailsNode.setAttribute(KEY_DEVICE, phoneDeviceName);
        deviceDetailsNode.setAttribute(KEY_DISPLAY, phoneDisplay);
        deviceDetailsNode.setAttribute(KEY_PROVIDER, phoneProvider);
        rootNode.addContent(deviceDetailsNode);

        Element deviceTokenNode = new Element(KEY_DEVICE_TOKEN);
        deviceTokenNode.setText(gcmRegistrationId);
        rootNode.addContent(deviceTokenNode);

        Element timeZoneNode = new Element(KEY_TZ);
        timeZoneNode.setText(TimeZone.getDefault().getID());
        rootNode.addContent(timeZoneNode);
        return rootNode;
    }

    public static void changePassword(String password) throws NonfatalException {
        Element rootNode = prepareChangePasswordRequest(password);

        RestConnector.getInstance().httpPostCheckSuccess(new Document(rootNode), SkedsApplication.getContext().getString(R.string.change_password_url,
                UserUtilitiesSingleton.getInstance().user.getId()));
    }

    private static Element prepareChangePasswordRequest(String password) {
        Element rootNode = new Element(KEY_USER);
        Element newPasswordNode = new Element(KEY_NEW_PASSWORD);

        newPasswordNode.setText(password);

        rootNode.addContent(newPasswordNode);
        return rootNode;
    }

    public static void getCountriesInfo() throws NonfatalException {

        int ownerId = UserUtilitiesSingleton.getInstance().user.getOwnerId();
        if (ownerId == 0) return;

        Document doc = RestConnector.getInstance().httpGet(SkedsApplication.getContext().getString(R.string.get_all_countries_url, ownerId));

        List<Country> countryList = parseCountryList(doc);

        AppDataSingleton.getInstance().getAllCountries().clear();
        AppDataSingleton.getInstance().getAllCountries().addAll(countryList);

    }

    private static List<Country> parseCountryList(Document doc) {
        List<Element> countriesList = doc.getRootElement().getChild(KEY_COUNTRIES).getChildren();

        List<Country> countryList = new ArrayList<Country>();

        for (Element countryNode : countriesList) {
            Country country = parseCountry(countryNode);
            countryList.add(country);
        }
        return countryList;
    }

    private static Country parseCountry(Element countryNode) {
        Country country = new Country();

        String attributeValueId = countryNode.getAttributeValue(KEY_ID);
        if (attributeValueId != null) {
            country.setId(Integer.parseInt(attributeValueId));
        }

        String attributeValueName = countryNode.getAttributeValue(KEY_NAME);
        if (attributeValueName != null) {
            country.setName(attributeValueName);
        }

        String attributeValueCode = countryNode.getAttributeValue(KEY_LOCALE_CODE);
        if (attributeValueCode != null) {
            country.setLocalCode(attributeValueCode);
        }

        String attributeValueCurrency = countryNode.getAttributeValue(KEY_CURRENCY_SYMBOL);
        if (attributeValueCurrency != null) {
            country.setCurrencySymbol(attributeValueCurrency);
        }

        String attributeValueShortCode = countryNode.getAttributeValue(KEY_SHORT_CODE);
        if (attributeValueShortCode != null) {
            country.setShortCode(attributeValueShortCode);
        }

        String attributeValueProvinceLbl = countryNode.getAttributeValue(KEY_PROVINCE_LABEL);
        if (attributeValueProvinceLbl != null) {
            country.setProvinceLabel(attributeValueProvinceLbl);
        }

        Element childZipCode = countryNode.getChild(KEY_ZIP_CODE_PATTERN);
        if (childZipCode != null) {

            String attributeValuePattern = childZipCode.getAttributeValue(KEY_PATTERN);
            if (attributeValuePattern != null) {
                country.setZipCodePattern(attributeValueShortCode);
            }

            String attributeValueLength = childZipCode.getAttributeValue(KEY_LENGTH);
            if (attributeValueLength != null) {
                country.setZipCodeLength(Integer.parseInt(attributeValueLength));
            }

            String attributeValueLabel = childZipCode.getAttributeValue(KEY_ZIPCODE_LABEL);
            if (attributeValueLabel != null) {
                country.setZipCodeLabel(attributeValueLabel);
            }
        }


        List<Region> regionsList = new ArrayList<Region>();

        if (countryNode.getChild(KEY_REGIONS) != null) {
            List<Element> regionNodes = countryNode.getChild(KEY_REGIONS).getChildren();
            if (regionNodes != null) {
                for (Element regionNode : regionNodes) {
                    Region region = new Region();

                    attributeValueId = regionNode.getAttributeValue(KEY_ID);
                    if (attributeValueId != null) {
                        region.setId(Integer.parseInt(attributeValueId));
                    }

                    attributeValueName = regionNode.getAttributeValue(KEY_NAME);
                    if (attributeValueName != null) {
                        region.setName(attributeValueName);
                    }

                    String attributeValueLabel = regionNode.getAttributeValue(KEY_LABEL);
                    if (attributeValueLabel != null) {
                        region.setLabel(attributeValueLabel);
                    }
                    regionsList.add(region);
                }
            }
            country.getRegions().addAll(regionsList);
        }
        return country;
    }
}