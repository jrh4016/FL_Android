package com.skeds.android.phone.business.Utilities.General;

public final class Constants {

    private Constants() {
    }

    public static final String APPLICATION_DATA_FILE = "AppDataFile";
    public static final String USER_DATA_FILE = "UserDataFile";
    public static final String PDF_FILE = "file.pdf";
    public static final String PRICEBOOK_DATABASE_FILE = "PriceBook.database";

    public static final String PHOTO_FILE = "PhotoFile";

    /* Application Modes */
    public static final int APPLICATION_MODE_PHONE_SERVICE = 0;
    public static final int APPLICATION_MODE_TABLET_7_SERVICE = 1;
    public static final int APPLICATION_MODE_TABLET_101_SERVICE = 2;

    /* Pricebook Modes */
    public static final int PRICE_BOOK_SEARCH_MODE_DEFAULT = 0; // 'simple'
    public static final int PRICE_BOOK_SEARCH_MODE_ONE_TIER = 1; // 'one_tier'
    public static final int PRICE_BOOK_SEARCH_MODE_TWO_TIER = 2; // 'two_tier'

    /* Equipment Conditions */
    public static final int EQUIPMENT_CONDITION_EXCELLENT = 0;
    public static final int EQUIPMENT_CONDITION_GOOD = 1;
    public static final int EQUIPMENT_CONDITION_FAIR = 2;
    public static final int EQUIPMENT_CONDITION_POOR = 3;

    /* Appointment Mode */
    public static final int APPOINTMENT_VIEW_FROM_APPOINTMENT_LIST = 0;
    public static final int APPOINTMENT_VIEW_FROM_CUSTOMER = 1;
    public static final int APPOINTMENT_VIEW_FROM_DASHBOARD = 2;

    /* Appointment Add Mode */
    public static final int APPOINTMENT_ADD_VIEW_FROM_APPOINTMENT = 0;
    public static final int APPOINTMENT_ADD_VIEW_FROM_CUSTOMER = 1;

    /* Customer View Mode */
    public static final int CUSTOMER_VIEW_FROM_CUSTOMER_LIST = 0;
    public static final int CUSTOMER_VIEW_FROM_APPOINTMENT = 1;
    public static final int CUSTOMER_VIEW_FROM_PAST_APPOINTMENT = 2;

    /* Estimate View Mode */
    public static final int ESTIMATE_VIEW_FROM_APPOINTMENT = 0;
    public static final int ESTIMATE_VIEW_FROM_ESTIMATE_LIST = 1;

    /* Estimate View Type */
    public static final int ESTIMATE_VIEW_TYPE_ADD = 0;
    public static final int ESTIMATE_VIEW_TYPE_VIEW_EDIT = 1;

    /* Estimate List View Mode */
    public static final int ESTIMATE_LIST_VIEW_FROM_APPOINTMENT = 0;
    public static final int ESTIMATE_LIST_VIEW_FROM_CUSTOMER = 1;
    public static final int ESTIMATE_LIST_VIEW_FROM_PAST_APPOINTMENT = 2;

    /* Invoice View Mode */
    public static final int INVOICE_VIEW_FROM_INVOICE_LIST = 0;
    public static final int INVOICE_VIEW_FROM_APPOINTMENT = 1;
    public static final int INVOICE_VIEW_FROM_PAST_APPOINTMENT = 2;

    /* Line Item Add View Mode */
    public static final int LINE_ITEM_ADD_VIEW_FROM_ESTIMATE = 0;
    public static final int LINE_ITEM_ADD_VIEW_FROM_INVOICE = 1;
    public static final int LINE_ITEM_ADD_VIEW_FROM_PRICEBOOK_LIST = 2;

    public static final String EXTRA_LINE_ITEM_ADD_VIEW_MODE = "line_item_add_mode";

    /* Pricebook List View Mode */
    public static final int PRICEBOOK_LIST_VIEW_FROM_DASHBOARD = 0;
    public static final int PRICEBOOK_LIST_VIEW_FROM_ESTIMATE = 1;
    public static final int PRICEBOOK_LIST_VIEW_FROM_INVOICE = 2;

    public static final String EXTRA_PRICEBOOK_LIST_VIEW_MODE = "pricebook_list_mode";

    public static final String EXTRA_PRICEBOOK_SEARCH_MODE = "search_mode";

    /* Service Agreement Add Mode */
    public static final int SERVICE_AGREEMENT_ADD_VIEW_FROM_AGREEMENT_LIST = 0;
    public static final int SERVICE_AGREEMENT_ADD_VIEW_FROM_VIEW_APPOINTMENT = 1;
    public static final int SERVICE_AGREEMENT_ADD_VIEW_FROM_AGREEMENT = 2;

    /* Service Agreement View Mode */
    public static final int SERVICE_AGREEMENT_VIEW_FROM_CUSTOMER = 0;

    /* Signature View Mode */
    public static final int SIGNATURE_VIEW_FROM_ESTIMATE = 0;
    public static final int SIGNATURE_VIEW_FROM_INVOICE = 1;

    /* Line items showing mode */
    public static final int SHOW_ITEM_NAME = 1;
    public static final int SHOW_ITEM_DESCRIPTION = 2;
    public static final int SHOW_ITEM_NAME_AND_DESCRIPTION = 3;

    /* PDF files showing mode */
    public static final String PDF_CUSTOMER_MODE = "CUSTOMER";
    public static final String PDF_APPOINTMENT_MODE = "APPOINTMENT";
    public static final String PDF_EQUIPMENT_MODE = "EQUIPMENT";
    public static final String PDF_CUSTOMER_AND_APPOINTMENT_MODE = "CUSTOMER_AND_APPOINTMENT";
    public static final String PDF_CUSTOMER_AND_EQUIPMENT_MODE = "CUSTOMER_AND_EQUIPMENT";
    public static final String PDF_VIEW_ALL_MODE = "ALL";

    public static final String ACTION_SEND_FILE = "send_file";


    public static final String DATE_FORMAT_WITHOUT_HOURS = "MM/dd/yyyy";
    public static final String DATE_FORMAT_WITH_HOURS = DATE_FORMAT_WITHOUT_HOURS + ' ' + "hh:mm aa";
}