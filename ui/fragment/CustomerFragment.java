package com.skeds.android.phone.business.ui.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.skeds.android.phone.business.AsyncTasks.BaseUiReportTask;
import com.skeds.android.phone.business.AsyncTasks.SubmitCustomerNotesTask;
import com.skeds.android.phone.business.AsyncTasks.ViewGalleryTask;
import com.skeds.android.phone.business.Dialogs.DialogCustomerNotesAdd;
import com.skeds.android.phone.business.Dialogs.DialogEmailAddressAddEdit;
import com.skeds.android.phone.business.Dialogs.DialogPhoneNumberAddEdit;
import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.AppSettingsUtilities;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Customer;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Location;
import com.skeds.android.phone.business.Utilities.General.Constants;
import com.skeds.android.phone.business.Utilities.General.UserUtilitiesSingleton;
import com.skeds.android.phone.business.Utilities.IntentExtras;
import com.skeds.android.phone.business.Utilities.REST.Objects.RESTCustomer;
import com.skeds.android.phone.business.activities.ActivityAppointmentAdd;
import com.skeds.android.phone.business.activities.ActivityCustomerAddEdit;
import com.skeds.android.phone.business.activities.ActivityCustomerLocationListView;
import com.skeds.android.phone.business.activities.ActivityCustomerPastAppointmentsListView;
import com.skeds.android.phone.business.activities.ActivityCustomerServiceAgreementsListView;
import com.skeds.android.phone.business.activities.ActivityEstimateListFragment;
import com.skeds.android.phone.business.activities.ActivityPdfDocumentsFragment;
import com.skeds.android.phone.business.activities.CustomerEquipmentActivity;

public class CustomerFragment extends BasePhotoFragment {

    private BaseUiReportTask<String> currentTask;

    public final static String CUSTOMER_ID = "id";

    private int customerId;
    private static final String DEBUG_TAG = "[Customer]";

    public static boolean submitOnDialogClose;
    public static String addedNotes;

    /* Customer info area */
    private TextView textCustomerName;
    private TextView textCustomerAddress;
    private TextView textCustomerAddressName;

    /* Dashboard Layout */
    private ImageView buttonLocations;
    private ImageView buttonEquipment;
    private ImageView buttonAddAppointment;
    private ImageView buttonHistory;
    private ImageView buttonAgreements;
    private ImageView buttonEstimates;

    private ImageView buttonPhotoGallery;
    private ImageView buttonPdf;

    /* Notes, email, phone layout */
    private ImageView buttonAddPhoneNumber;
    private LinearLayout layoutPhoneNumbers;
    private ImageView buttonAddEmailAddress;
    private LinearLayout layoutEmailAddresses;
    private ImageView buttonAddCustomerNotes;
    private TextView edittextCustomerNotes;
    private TextView textCustomerReliable;
    private TextView buttonExpandCollapseNotes;

    private LinearLayout buttonEditCustomer;

    @Override
    public void onClick(View v) {
        Intent i;

        switch (v.getId()) {
            case R.id.activity_customer_button_locations:
                i = new Intent(context,
                        ActivityCustomerLocationListView.class);
                i.setAction(ActivityCustomerLocationListView.ACTION_VIEW_LOCATION);
                startActivity(i);
                // finish();
                break;
            case R.id.activity_customer_button_equipment:
                i = new Intent(context,
                        CustomerEquipmentActivity.class);
                i.putExtra(CustomerEquipmentActivity.EXTRA_CUSTOMER_ID,
                        AppDataSingleton.getInstance().getCustomer().getId())
                        .putExtra(CustomerEquipmentActivity.EXTRA_CAN_ADD_NEW_EQUIPMENT, true)
                        .putExtra(IntentExtras.EDIT_ENABLE, true);
                startActivity(i);
                // finish();
                break;
            case R.id.activity_customer_button_history:
                i = new Intent(context,
                        ActivityCustomerPastAppointmentsListView.class);
                startActivity(i);
                // finish();
                break;
            case R.id.activity_customer_button_service_agreements:
                i = new Intent(context,
                        ActivityCustomerServiceAgreementsListView.class);
                startActivity(i);
                // finish();
                break;
            case R.id.activity_customer_button_work_estimates:

                AppDataSingleton.getInstance().setEstimateListViewMode(
                        Constants.ESTIMATE_LIST_VIEW_FROM_CUSTOMER);
                i = new Intent(context, ActivityEstimateListFragment.class);
//                i.putExtra(IntentExtras.LOCATION_ID, AppDataSingleton
//                        .getInstance().getCustomer().getLocationId());
                startActivity(i);
                break;
            case R.id.activity_customer_button_photo_gallery:
                new ViewGalleryTask(getActivity(), 0, 0).execute();
                break;
            case R.id.activity_customer_imageview_new_phone_number:
                DialogPhoneNumberAddEdit dialog = new DialogPhoneNumberAddEdit(context);
                dialog.show();
                dialog.setOnDismissListener(new OnDismissListener() {

                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        setupUI();
                    }
                });
                break;
            case R.id.activity_customer_imageview_new_email_address:
                DialogEmailAddressAddEdit emailDialog = new DialogEmailAddressAddEdit(context);
                DialogEmailAddressAddEdit.editMode = false;
                emailDialog.show();
                emailDialog.setOnDismissListener(new OnDismissListener() {

                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        setupUI();
                    }
                });
                break;
            case R.id.activity_customer_imageview_add_notes:
                DialogCustomerNotesAdd addNotesDialog = new DialogCustomerNotesAdd(context);
                addNotesDialog
                        .setOnDismissListener(new DialogInterface.OnDismissListener() {

                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                // This means that we've finished
                                if (submitOnDialogClose) {
                                    final SubmitCustomerNotesTask submitTask = new SubmitCustomerNotesTask(
                                            getActivity()) {

                                        @Override
                                        protected void onSuccess() {
                                            AppDataSingleton
                                                    .getInstance()
                                                    .getCustomer()
                                                    .addNotes(
                                                            addedNotes
                                                                    .toString()
                                                    );
                                            setupUI();
                                        }

                                    };
                                    submitTask.setCustomerId(AppDataSingleton
                                            .getInstance().getCustomer()
                                            .getId());
                                    submitTask.setNotes(addedNotes);
                                    submitTask.execute();

                                }
                            }
                        });

                addNotesDialog.show();
                break;
            case R.id.activity_customer_button_new_appointment:
                // ViewAddAppointment.editMode = false;
                // Indicate how we arrived at this location
                AppDataSingleton.getInstance().setAppointmentAddViewMode(
                        Constants.APPOINTMENT_ADD_VIEW_FROM_CUSTOMER);

                i = new Intent(context, ActivityAppointmentAdd.class);
                startActivity(i);
                // finish();
                break;
            case R.id.activity_customer_button_edit_customer:
                ActivityCustomerAddEdit.isEditMode = true;
                i = new Intent(context, ActivityCustomerAddEdit.class);
                startActivity(i);
//				mActivity.onBackPressed();
                break;

            case R.id.activity_customer_button_expand_collapse_notes:

                if (buttonExpandCollapseNotes.getTag() == null
                        || "1".equals(buttonExpandCollapseNotes.getTag().toString())) {

                    buttonExpandCollapseNotes.setText("Collapse Notes");
                    buttonExpandCollapseNotes.setTag("0");
                    int heightToUse = (edittextCustomerNotes.getLineCount() * edittextCustomerNotes
                            .getLineHeight())
                            + edittextCustomerNotes.getLineHeight();
                    edittextCustomerNotes.setHeight(heightToUse);
                } else {
                    buttonExpandCollapseNotes.setText("Expand Notes");
                    buttonExpandCollapseNotes.setTag("1");
                    int heightToUse = (7 * edittextCustomerNotes
                            .getLineHeight());
                    edittextCustomerNotes.setHeight(heightToUse);
                }
                break;
            case R.id.customer_button_pdf:
                i = new Intent(context, ActivityPdfDocumentsFragment.class);
                i.putExtra("templateTypeMode", Constants.PDF_CUSTOMER_MODE);
                startActivity(i);
                break;
            default:
                // Nothing
                super.onClick(v);
        }
    }

    private OnClickListener mPhoneRowListener = new OnClickListener() {

        @Override
        public void onClick(View v) {

            final String number = v.getTag().toString();

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("FieldLocate");
            builder.setMessage("Select Action for Phone: " + number);

            builder.setPositiveButton("Call",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Intent callIntent = new Intent(Intent.ACTION_DIAL);
                            callIntent.setData(Uri.parse("tel:" + number));
                            startActivity(callIntent);
                            return;
                        }
                    }
            );

            builder.setNegativeButton("Cancel", null);

            builder.setNeutralButton("Text",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                            smsIntent.setData(Uri.parse("sms:" + number));
                            startActivity(smsIntent);
                            return;
                        }
                    }
            );

            builder.show();

        }
    };

    private OnClickListener mPhoneAddEditListener = new OnClickListener() {

        @Override
        public void onClick(View v) {

            DialogPhoneNumberAddEdit addPhoneDialog = new DialogPhoneNumberAddEdit(context);

            switch (v.getId()) {
                case R.id.activity_customer_imageview_new_phone_number:
                    addPhoneDialog.editMode = false;
                    break;
                case R.id.rowLocationEditAddressEditButton:
                    addPhoneDialog.editMode = true;
                    addPhoneDialog.editItemNumber = Integer.parseInt(v.getTag()
                            .toString());
                default:
                    // Nothing
                    break;
            }

            addPhoneDialog
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            setupUI();
                        }
                    });

            addPhoneDialog.show();
        }
    };

    private OnClickListener mEmailAddEditListener = new OnClickListener() {

        @Override
        public void onClick(View v) {

            DialogEmailAddressAddEdit addEmailDialog = new DialogEmailAddressAddEdit(context);

            switch (v.getId()) {
                case R.id.activity_customer_imageview_new_phone_number:
                    DialogEmailAddressAddEdit.editMode = false;
                    break;
                case R.id.rowLocationEditAddressEditButton:
                    DialogEmailAddressAddEdit.editMode = true;
                    DialogEmailAddressAddEdit.editItemNumber = Integer.parseInt(v.getTag().toString());
                    break;
                default:
                    // Nothing
                    break;
            }

            addEmailDialog
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            setupUI();
                        }
                    });

            addEmailDialog.show();
        }
    };

    private OnClickListener mEmailListener = new OnClickListener() {

        @Override
        public void onClick(View v) {

            final String mEmailToContact = v.getTag().toString();

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("FieldLocate");
            builder.setMessage("Do you wish to email " + mEmailToContact);

            builder.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent emailIntent = new Intent(Intent.ACTION_SEND);
                            String[] recipient = new String[]{mEmailToContact};

                            emailIntent.putExtra(
                                    android.content.Intent.EXTRA_EMAIL,
                                    recipient);
                            emailIntent.putExtra(
                                    android.content.Intent.EXTRA_SUBJECT,
                                    "E-Mail from "
                                            + UserUtilitiesSingleton
                                            .getInstance().user
                                            .getFirstName()
                                            + " "
                                            + UserUtilitiesSingleton
                                            .getInstance().user
                                            .getLastName()
                            );
                            emailIntent.putExtra(
                                    android.content.Intent.EXTRA_TEXT, "");
                            emailIntent.setType("text/plain");
                            // callIntent.setData(Uri.parse("mailto:" +
                            // mEmailToContact));
                            // startActivity(emailIntent);
                            startActivity(Intent.createChooser(
                                    emailIntent, "Send mail..."));
                            return;
                        }
                    }
            );

            builder.setNegativeButton("Cancel", null);
            builder.show();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_customer_view, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final Bundle args = getArguments();
        if (args != null) {
            customerId = args.getInt(CUSTOMER_ID);
        }

//        mActivity = getActivity();
        AppDataSingleton.getInstance().setEstimateViewMode(
                Constants.ESTIMATE_VIEW_FROM_ESTIMATE_LIST);
        AppDataSingleton.getInstance().setInvoiceViewMode(
                Constants.INVOICE_VIEW_FROM_APPOINTMENT);

		/* Customer info area */
        textCustomerName = (TextView) fragmentView
                .findViewById(R.id.activity_customer_textview_customer_name);
        textCustomerAddress = (TextView) fragmentView
                .findViewById(R.id.activity_customer_textview_customer_address);
        textCustomerAddressName = (TextView) fragmentView
                .findViewById(R.id.activity_customer_textview_customer_address_name);

		/* Dashboard Layout */
        buttonLocations = (ImageView) fragmentView
                .findViewById(R.id.activity_customer_button_locations);
        buttonEquipment = (ImageView) fragmentView
                .findViewById(R.id.activity_customer_button_equipment);
        buttonAddAppointment = (ImageView) fragmentView
                .findViewById(R.id.activity_customer_button_new_appointment);
        buttonHistory = (ImageView) fragmentView
                .findViewById(R.id.activity_customer_button_history);
        buttonAgreements = (ImageView) fragmentView
                .findViewById(R.id.activity_customer_button_service_agreements);
        buttonEstimates = (ImageView) fragmentView
                .findViewById(R.id.activity_customer_button_work_estimates);

        buttonPhotoGallery = (ImageView) fragmentView
                .findViewById(R.id.activity_customer_button_photo_gallery);
        buttonPdf = (ImageView) fragmentView
                .findViewById(R.id.customer_button_pdf);

        if (AppSettingsUtilities.getApplicationMode() == Constants.APPLICATION_MODE_PHONE_SERVICE) {
//			buttonPdf.setVisibility(View.INVISIBLE);
        }

		/* Notes, email, phone layout */
        buttonAddPhoneNumber = (ImageView) fragmentView
                .findViewById(R.id.activity_customer_imageview_new_phone_number);
        layoutPhoneNumbers = (LinearLayout) fragmentView
                .findViewById(R.id.activity_customer_linearlayout_phone_table);
        buttonAddEmailAddress = (ImageView) fragmentView
                .findViewById(R.id.activity_customer_imageview_new_email_address);
        layoutEmailAddresses = (LinearLayout) fragmentView
                .findViewById(R.id.activity_customer_linearlayout_email_table);
        buttonAddCustomerNotes = (ImageView) fragmentView
                .findViewById(R.id.activity_customer_imageview_add_notes);
        edittextCustomerNotes = (TextView) fragmentView
                .findViewById(R.id.activity_customer_textview_notes);
        textCustomerReliable = (TextView) fragmentView
                .findViewById(R.id.text_redflag_descr);
        buttonExpandCollapseNotes = (TextView) fragmentView
                .findViewById(R.id.activity_customer_button_expand_collapse_notes);

        buttonEditCustomer = (LinearLayout) fragmentView
                .findViewById(R.id.activity_customer_button_edit_customer);

        // setActiveNavigation(NAV_CUSTOMER);
        currentTask = new GetSingleCustomerTask();
        currentTask.execute();
    }

    @Override
    public void onPause() {
        super.onPause();

        if (currentTask != null) {
            currentTask.terminate();
        }
    }

    private void setupUI() {
        Customer customer = AppDataSingleton.getInstance().getCustomer();

        // Button Listeners
        buttonLocations.setOnClickListener(this);
        buttonEquipment.setOnClickListener(this);
        buttonHistory.setOnClickListener(this);
        buttonAgreements.setOnClickListener(this);
        buttonEstimates.setOnClickListener(this);

        buttonPhotoGallery.setOnClickListener(this);
        buttonPdf.setOnClickListener(this);
        buttonAddPhoneNumber.setOnClickListener(this);
        buttonAddEmailAddress.setOnClickListener(this);
        buttonAddCustomerNotes.setOnClickListener(this);
        if (UserUtilitiesSingleton.getInstance().user
                .isAllowAddEditAppointments()) {
            buttonAddAppointment.setOnClickListener(this);
        } else {
            buttonAddAppointment.setVisibility(View.GONE);
        }
        buttonEditCustomer.setOnClickListener(this);

        // Populate text fields


        textCustomerName.setText(customer.getLongName());

        Location primaryLocation;
        String locName;
        String adress1;
        String adress2;
        String city;
        String state;
        String zip;

        if (!customer.locationList.isEmpty()) {
            primaryLocation = customer.locationList.get(0);
            locName = primaryLocation.getName();
            adress1 = primaryLocation.getAddress1();
            adress2 = primaryLocation.getAddress2();
            city = primaryLocation.getCity();
            state = primaryLocation.getState();
            zip = primaryLocation.getZip();
        } else {
            adress1 = customer.getAddress1();
            adress2 = customer.getAddress2();
            city = customer.getAddressCity();
            state = customer.getAddressState();
            zip = customer.getAddressPostalCode();
        }


        if (!adress1.isEmpty() && !adress2.isEmpty()) {
            textCustomerAddress.setText(getString(R.string.adress_format_address1_adress2, adress1, adress2, city, state, zip));
        } else {
            textCustomerAddress.setText(getString(R.string.adress_format_address1, adress1, city, state, zip));
        }

        textCustomerAddress.setSelected(true);

        textCustomerAddressName.setVisibility(View.GONE);

        // Populate phone numbers
        layoutPhoneNumbers = (LinearLayout) fragmentView.findViewById(R.id.activity_customer_linearlayout_phone_table);
        LayoutInflater inflater = LayoutInflater.from(context);
        layoutPhoneNumbers.removeAllViews();
        for (int i = 0; i < customer.phone.size(); i++) {
            View phoneRow = inflater.inflate(R.layout.row_item_with_edit_button, null);

            TextView phoneTextView = (TextView) phoneRow.findViewById(R.id.rowLocationEditAddressLine1Text);
            phoneTextView.setText(customer.phone.get(i));

            TextView phoneDescriptionTextView = (TextView) phoneRow.findViewById(R.id.rowLocationEditAddressLine2Text);
            if (AppDataSingleton.getInstance().getCustomer().phoneDescription.get(i) != null) {
                phoneDescriptionTextView.setText(customer.phoneDescription.get(i));
            }

            TextView phoneType = (TextView) phoneRow.findViewById(R.id.rowLocationEditAddressLine3Text);
            String type = customer.phoneType.get(i);
            phoneType.setText(type.substring(0, 1).toUpperCase() + type.substring(1).toLowerCase());

            TextView editButton = (TextView) phoneRow.findViewById(R.id.rowLocationEditAddressEditButton);

            editButton.setTag(i);
            editButton.setOnClickListener(mPhoneAddEditListener);

            phoneRow.setTag(customer.phone.get(i));
            phoneRow.setOnClickListener(mPhoneRowListener);
            layoutPhoneNumbers.addView(phoneRow);
        }

        // Populate email addresses
        layoutEmailAddresses = (LinearLayout) fragmentView
                .findViewById(R.id.activity_customer_linearlayout_email_table);
        layoutEmailAddresses.removeAllViews();
        for (int i = 0; i < customer.email
                .size(); i++) {
            View emailRow = inflater.inflate(
                    R.layout.row_item_with_edit_button, null);

            TextView emailAddressTextView = (TextView) emailRow
                    .findViewById(R.id.rowLocationEditAddressLine1Text);
            emailAddressTextView.setText(AppDataSingleton.getInstance()
                    .getCustomer().email.get(i));

            TextView emailAddressDescriptionTextView = (TextView) emailRow
                    .findViewById(R.id.rowLocationEditAddressLine2Text);
            if (AppDataSingleton.getInstance().getCustomer().emailDescription
                    .get(i) != null) {
                emailAddressDescriptionTextView.setText(AppDataSingleton
                        .getInstance().getCustomer().emailDescription.get(i));
            }

            TextView editButton = (TextView) emailRow
                    .findViewById(R.id.rowLocationEditAddressEditButton);

            editButton.setTag(i);
            editButton.setOnClickListener(mEmailAddEditListener);

            emailRow.setTag(AppDataSingleton.getInstance().getCustomer().email
                    .get(i));
            emailRow.setOnClickListener(mEmailListener);
            layoutEmailAddresses.addView(emailRow);

        }

        // Setup reliability
        if (AppDataSingleton.getInstance().getCustomer().getUnreliable()) {
            textCustomerReliable.setText("Customer is Flagged: "
                    + AppDataSingleton.getInstance().getCustomer()
                    .getReliableNotes());
            textCustomerReliable.setVisibility(View.VISIBLE);
        } else
        // if reliable do not show anything
        {
            textCustomerReliable.setVisibility(View.GONE);
        }

        // Populate Notes
        edittextCustomerNotes.setText(AppDataSingleton.getInstance()
                .getCustomer().getNotes());

        int currentNoteHeight = (edittextCustomerNotes.getLineCount() * edittextCustomerNotes
                .getLineHeight());
        int sevenLinesHeight = edittextCustomerNotes.getLineHeight() * 7;

        if (currentNoteHeight <= sevenLinesHeight) {
            buttonExpandCollapseNotes.setVisibility(View.GONE);
        } else {
            buttonExpandCollapseNotes.setVisibility(View.VISIBLE);
            edittextCustomerNotes.setHeight(sevenLinesHeight);
            buttonExpandCollapseNotes.setOnClickListener(this);
        }

        // Scroll to top of view
        final ScrollView scrollView = (ScrollView) fragmentView
                .findViewById(R.id.activity_customer_scrollview);
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_UP);
            }
        });
    }

    private boolean isShowingAdressName(String adress) {
        if (AppDataSingleton.getInstance().getCustomer().locationList.isEmpty()) {
            return false;
        }

        String locationAdress = adress;
        String locationName = AppDataSingleton.getInstance().getCustomer().locationList
                .get(0).getName();
        locationName = locationName.replaceAll("[,;\\s]", "").toLowerCase();
        locationAdress = locationAdress.replaceAll("[,;\\s]", "").toLowerCase();

        return locationAdress.equals(locationName);
    }


    private class GetSingleCustomerTask extends BaseUiReportTask<String> {

        GetSingleCustomerTask() {
            super(getActivity(), R.string.async_task_string_loading_customer);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            AppDataSingleton.getInstance().setCustomer(new Customer());
        }

        @Override
        protected void onSuccess() {
            setupUI();
        }

        @Override
        protected boolean taskBody(final String... args) throws Exception {
            RESTCustomer.query(customerId);
            return true;
        }
    }
}
