package com.skeds.android.phone.business.ui.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.skeds.android.phone.business.AsyncTasks.BaseUiReportTask;
import com.skeds.android.phone.business.AsyncTasks.FlushTask;
import com.skeds.android.phone.business.Dialogs.DialogAppointmentNotesAdd;
import com.skeds.android.phone.business.Dialogs.DialogErrorPopup;
import com.skeds.android.phone.business.Dialogs.DialogEstimateOrInvoice;
import com.skeds.android.phone.business.Dialogs.DialogLocationCallOrEdit;
import com.skeds.android.phone.business.Dialogs.DialogLocationScanAndAdd;
import com.skeds.android.phone.business.Dialogs.DialogRouteOrCustomerInfo;
import com.skeds.android.phone.business.Dialogs.DialogScanAndUpload;
import com.skeds.android.phone.business.Dialogs.DialogServiceAgreementSelect;
import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Services.RestIntentService;
import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Appointment;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Customer;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Phone;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Status;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.StatusBuffer;
import com.skeds.android.phone.business.Utilities.General.CommonUtilities;
import com.skeds.android.phone.business.Utilities.General.Constants;
import com.skeds.android.phone.business.Utilities.General.UserUtilitiesSingleton;
import com.skeds.android.phone.business.Utilities.IntentExtras;
import com.skeds.android.phone.business.Utilities.NonfatalException;
import com.skeds.android.phone.business.Utilities.REST.Objects.RESTAgreement;
import com.skeds.android.phone.business.Utilities.REST.Objects.RESTAgreementList;
import com.skeds.android.phone.business.Utilities.REST.Objects.RESTAppointment;
import com.skeds.android.phone.business.Utilities.REST.Objects.RESTInvoice;
import com.skeds.android.phone.business.Utilities.REST.Objects.RESTLocation;
import com.skeds.android.phone.business.Utilities.REST.Objects.RESTPhotoList;
import com.skeds.android.phone.business.activities.ActivityAppointmentAdd;
import com.skeds.android.phone.business.activities.ActivityAppointmentCommentsView;
import com.skeds.android.phone.business.activities.ActivityAppointmentDualFragment;
import com.skeds.android.phone.business.activities.ActivityAppointmentOnTruckAddedFragment;
import com.skeds.android.phone.business.activities.ActivityApptQuestionsFragment;
import com.skeds.android.phone.business.activities.ActivityCustomerSingleFragment;
import com.skeds.android.phone.business.activities.ActivityEstimateListFragment;
import com.skeds.android.phone.business.activities.ActivityEstimateView;
import com.skeds.android.phone.business.activities.ActivityInvoiceSingleFragment;
import com.skeds.android.phone.business.activities.ActivityLocationAddEdit;
import com.skeds.android.phone.business.activities.ActivityPartOrdersApptListViewFragment;
import com.skeds.android.phone.business.activities.ActivityPdfDocumentsFragment;
import com.skeds.android.phone.business.activities.ActivityPhotoListViewFragment;
import com.skeds.android.phone.business.activities.AppointmentEquipmentAddActivity;
import com.skeds.android.phone.business.core.SkedsApplication;
import com.skeds.android.phone.business.util.DateUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class AppointmentFragment extends BasePhotoFragment implements
        LocationListener {

    public final static String APPOINTMENT_ID = "appt_id";

    // Flags for Button Selection
    public static final int ESTIMATE_LIST_SELECTED = 1;
    public static final int NEW_INVOICE_SELECTED = 2;
    public static final int VIEW_INVOICE_SELECTED = 3;
    public static final int PART_ORDERS_SELECTED = 4;
    public static final int EQUIPMENT_ADD_SELECTED = 5;
    public static final int PHOTO_GALERY_SELECTED = 6;

    // Flags for Launch Invoice Mode in Invoice Number listener
    private static final int START_INVOICE_MANUALLY = 11;
    private static final int START_INVOICE_AFTER_FINISH = 12;

    public static Integer appointmentId;

    /* Debug output */
    private static final String DEBUG_TAG = "[Appointment]";

    /* Current job status */
    private Status status;
    /*
     * Only Shows for "Force Work Order Input"
     */
    private Dialog mInvoiceDialog;
    private TextView mInvoiceSaveButton, mInvoiceCancelButton, mInvoiceNumberPrefix;
    private EditText mInvoiceNumberText;

    private DialogServiceAgreementSelect serviceAgreementDialog;

    /* Layout that expands/collapses for status update */
    private LinearLayout linearLayoutUpdateStatus;
    private LinearLayout linearLayoutUpdateStatusButtons;
    private LinearLayout buttonStatusUpdateOne;
    private LinearLayout buttonStatusUpdateTwo;
    private LinearLayout buttonStatusUpdateThree;
    private TextView textStatusUpdateOne;
    private TextView textStatusUpdateTwo;
    private TextView textStatusUpdateThree;

    /* Customer Details */
    private LinearLayout linearLayoutCustomerDetails;

    /* Text Views */
    private TextView textAppointmentType;
    private TextView textAppointmentStatus;
    private TextView textAppointmentDateTime;
    private TextView textAppointmentCustomerName;
    private TextView textAppointmentCustomerAddress;
    private TextView textAppointmentCustomerAddressName;
    private TextView textAppointmentAddedEquipment;
    private TextView textAppointmentAddedAgreement;
    private TextView textAppointmentWorkingTechnicians;
    private TextView textAppointmentNotes;
    private TextView textAppointmentOrderNumber;

    private TextView customFieldLabel;
    private TextView customFieldValue;
    private TextView secondCustomFieldLabel;
    private TextView secondCustomFieldValue;


    private ScrollView scrollView;

    private TextView textAppointmentMultiType;

    /* Buttons */
    private ImageView buttonViewEstimate;
    private ImageView buttonViewInvoice;
    private ImageView buttonPartOrders;
    private ImageView buttonAddAgreement;
    private ImageView buttonAddEquipment;
    private ImageView buttonAddNotes;
    private ImageView buttonChecklist;
    private ImageView buttonViewComments;
    private ImageView buttonPhotoGallery;
    private ImageView buttonOnTruck;
    private ImageView buttonUploadFile;
    private ImageView buttonPdf;

    private LinearLayout buttonEditAppointment;

    public static boolean submitOnDialogClose = false;
    public static String addedNotes = "";
    public static int selectedAgreementId;

    private String appointmentTypeString = "", eventLocationString = "",
            eventServiceProviderString = "", eventNotesString = "";

    /* Users GPS data */
    private double longitude = 0, latitude = 0, accuracy;

    /* Status selected by user to update to */
    private Status statusToUse;

	/* Did the return of the status update include an invoice */

    /* Status update timer, loop controller */
    private boolean locationTimeExpired = false;

    /* Are we looking at a read-only appointment? */
    public static boolean isReadOnly;

	/* Does the status need to be buffered, due to no network */

    private LocationManager lm;

    //need to have it because activity handles orientation changes
    private Activity mActivity;

    private final int ACTIVITY_RESULT_FILE = 2;
    private final int ACTIVITY_RESULT_LOCATION_SCAN = 3;
    private final int ACTIVITY_RESULT_QUESTIONS = 4;
    private final int ACTIVITY_RESULT_ESTIMATES = 6;
    private final int ACTIVITY_RESULT_INVOICE = 7;

    private String bestProvider;

	/*
     * ==========================================================================
	 * ==========================================================================
	 * Fragment Listeners
	 */

    private OnClickListener mStatusUpdateLayoutListener = new OnClickListener() {

        @Override
        public void onClick(View v) {

            Animation animation;
            switch (linearLayoutUpdateStatusButtons.getVisibility()) {
                case View.INVISIBLE:
                case View.GONE:
                    linearLayoutUpdateStatusButtons.setVisibility(View.VISIBLE);
                    animation = AnimationUtils.loadAnimation(mActivity,
                            R.anim.slide_from_top);
                    animation.setDuration(500);

                    animation.setAnimationListener(new AnimationListener() {

                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                        }
                    });

                    linearLayoutUpdateStatusButtons.setAnimation(animation);
                    linearLayoutUpdateStatusButtons.startAnimation(animation);

                    animation.start();
                    break;
                case View.VISIBLE:
                    animation = AnimationUtils.loadAnimation(mActivity,
                            R.anim.slide_from_bottom);
                    animation.setDuration(500);
                    animation.setAnimationListener(new AnimationListener() {

                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            linearLayoutUpdateStatusButtons
                                    .setVisibility(View.GONE);
                        }
                    });

                    linearLayoutUpdateStatusButtons.setAnimation(animation);
                    linearLayoutUpdateStatusButtons.startAnimation(animation);
                    animation.start();
                    break;
                default:
                    // Nothing
                    break;
            }
        }
    };

    private OnClickListener mCustomerInfoListener = new OnClickListener() {
        @Override
        public void onClick(View v) {

            final List<String> phones = configurePhoneAndRouteList();

            final DialogRouteOrCustomerInfo dialog = new DialogRouteOrCustomerInfo(
                    mActivity, phones, new DialogRouteOrCustomerInfo.CallbackListener() {
                @Override
                public void callback(final int type, final String phone) {
                    processAction(type, phone);
                }
            }
            );
            dialog.show();
        }

        private List<String> configurePhoneAndRouteList() {
            final List<String> phones = new ArrayList<String>();
            final Appointment appointment = AppDataSingleton.getInstance().getAppointment();
            if (!TextUtils.isEmpty(appointment.getPhone1())) {
                phones.add(getString(R.string.lines_with_space, appointment.getPhone1(),
                        appointment.getPhone1Description()));
            }

            if (!TextUtils.isEmpty(appointment.getPhone2())) {
                phones.add(getString(R.string.lines_with_space, appointment.getPhone2(),
                        appointment.getPhone2Description()));
            }

            if (phones.isEmpty()) {
                final Customer customer = AppDataSingleton.getInstance().getCustomer();
                final com.skeds.android.phone.business.Utilities.General.ClassObjects.Location location = customer.getLocationById(
                        appointment.getLocationId());

                if (!TextUtils.isEmpty(location.getPhone1())) {
                    phones.add(getString(R.string.lines_with_space, location.getPhone1Type(), location.getPhone1()));
                }
                if (!TextUtils.isEmpty(location.getPhone2())) {
                    phones.add(getString(R.string.lines_with_space, location.getPhone2Type(), location.getPhone2()));
                }
                if (phones.isEmpty()) {
                    for (Phone phone : customer.getPhones()) {
                        phones.add(getString(R.string.lines_with_space, phone.getType(), phone.getNumber()));
                    }
                }
            }

            phones.add(0, getString(R.string.view_route));
            phones.add(1, getString(R.string.customer_details));
            return phones;
        }
    };
    private TextView textAppointmentDateTime1;
    private TextView textAppointmentDay;

    private void processAction(final int type, final String phone) {
        switch (type) {
            case DialogRouteOrCustomerInfo.TRANSFER_TO_ROUTE:
                final String lat = AppDataSingleton.getInstance().getAppointment().getLocationLatitude();
                final String log = AppDataSingleton.getInstance().getAppointment().getLocationLongitude();
                final String name = AppDataSingleton.getInstance().getAppointment().getLocationValue();

                String url;

                if (!TextUtils.isEmpty(name)) {
                    url = getDirectionsUrl(latitude, longitude, lat, log);
                } else {
                    url = getDirectionsToAddressUrl(latitude, longitude, name);
                }
                final Intent i = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(i);
                break;
            case DialogRouteOrCustomerInfo.TRANSFER_TO_CUSTOMER_INFO:
                final Intent intent = new Intent(mActivity, ActivityCustomerSingleFragment.class);
                intent.putExtra(CustomerFragment.CUSTOMER_ID, AppDataSingleton.getInstance().getCustomer().getId());
                AppDataSingleton.getInstance().setCustomerViewMode(Constants.CUSTOMER_VIEW_FROM_APPOINTMENT);
                startActivity(intent);
                break;
            case -1:
                break;
            default:
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse(getString(R.string.dial_number_action, phone)));
                startActivity(callIntent);
                break;
        }
    }

    private OnClickListener mStatusOneListener = new OnClickListener() {
        @Override
        public void onClick(View v) {

            if (CommonUtilities.isNetworkAvailable(mActivity)) {
                if (StatusBuffer.instance().haveQueue()) {
                    new FlushTask(mActivity).execute();
                    return;
                }
            }

            switch (status) {
                case ON_ROUTE:
                    // statusToUse = Status.ON_ROUTE;
                    // This checks if we should scan the barcode or not
                    if (UserUtilitiesSingleton.getInstance().user
                            .usesBarcodesForLocations()) {
                        try {
                            Intent intent = new Intent(
                                    "com.google.zxing.client.android.SCAN");
                            intent.putExtra("SCAN_MODE", "ONE_D_MODE");
                            startActivityForResult(intent,
                                    ACTIVITY_RESULT_LOCATION_SCAN);
                        } catch (android.content.ActivityNotFoundException ex) {
                            // Potentially direct the user to the Market with a
                            // Dialog
                            Toast.makeText(
                                    mActivity, getString(R.string.app_string_barcode_scanner_not_found),
                                    Toast.LENGTH_LONG).show();
                        }
                    } else {
                        statusToUse = Status.START_APPOINTMENT;
                        new UpdateStatusTask().execute();
                    }
                    break;
                case START_APPOINTMENT:
                case RESTART_APPOINTMENT:
                    if (!AppDataSingleton.getInstance().getCustomQuestionList()
                            .isEmpty()
                            && CommonUtilities.isNetworkAvailable(mActivity)) {
                        Intent i = new Intent(mActivity,
                                ActivityApptQuestionsFragment.class);
                        i.putExtra("haveToAnswerRequired", true);
                        startActivityForResult(i, ACTIVITY_RESULT_QUESTIONS);
                    } else {
                        statusToUse = Status.FINISH_APPOINTMENT;
                        // If they're forced to use Invoice Input or not
                        if (AppDataSingleton.getInstance().getAppointment()
                                .getOwnerForceWorkOrderNumberInput()) {
                            showInvoiceNumberDialog(START_INVOICE_AFTER_FINISH);
                        } else {
                            new UpdateStatusTask().execute();
                        }
                    }
                    break;
                case FINISH_APPOINTMENT:
                    if (!AppDataSingleton.getInstance().getAppointment()
                            .isEverybodyElseFinished()) {
                        DialogErrorPopup errorPopup = new DialogErrorPopup(
                                mActivity,
                                "Notice",
                                "Unable to close job. There are either technicians still working on this appointment or there are other appointments with the same work order number still open.",
                                null);
                        errorPopup.show();
                        break;
                    }
                    statusToUse = Status.CLOSE_APPOINTMENT;
                    new UpdateStatusTask().execute();
                    break;
                case NOT_STARTED:
                    statusToUse = Status.ON_ROUTE;
                    new UpdateStatusTask().execute();
                    break;
                case SUSPEND_APPOINTMENT:
                    statusToUse = Status.ON_ROUTE;
                    new UpdateStatusTask().execute();
                    break;
                case PARTS_RUN_APPOINTMENT:
                    statusToUse = Status.ON_ROUTE;
                    new UpdateStatusTask().execute();
                    break;
                default:
                    // Nothing
                    break;
            }
        }
    };

    private OnClickListener mStatusTwoListener = new OnClickListener() {
        @Override
        public void onClick(View v) {

            if (CommonUtilities.isNetworkAvailable(mActivity)) {
                if (StatusBuffer.instance().haveQueue()) {
                    new FlushTask(mActivity).execute();
                    return;
                }
            }

            switch (status) {
                case NOT_STARTED:
                    if (UserUtilitiesSingleton.getInstance().user
                            .usesBarcodesForLocations()) {
                        try {
                            Intent intent = new Intent(
                                    "com.google.zxing.client.android.SCAN");
                            intent.putExtra("SCAN_MODE", "ONE_D_MODE");
                            startActivityForResult(intent,
                                    ACTIVITY_RESULT_LOCATION_SCAN);
                        } catch (android.content.ActivityNotFoundException ex) {
                            // Potentially direct the user to the Market with a
                            // Dialog
                            Toast.makeText(
                                    mActivity, getString(R.string.app_string_barcode_scanner_not_found),
                                    Toast.LENGTH_LONG).show();
                        }
                    } else {
                        statusToUse = Status.START_APPOINTMENT;
                        new UpdateStatusTask().execute();
                    }
                    break;
                case SUSPEND_APPOINTMENT:
                    if (UserUtilitiesSingleton.getInstance().user
                            .usesBarcodesForLocations()) {
                        try {
                            Intent intent = new Intent(
                                    "com.google.zxing.client.android.SCAN");
                            intent.putExtra("SCAN_MODE", "ONE_D_MODE");
                            startActivityForResult(intent,
                                    ACTIVITY_RESULT_LOCATION_SCAN);
                        } catch (android.content.ActivityNotFoundException ex) {
                            // Potentially direct the user to the Market with a
                            // Dialog
                            Toast.makeText(
                                    mActivity, getString(R.string.app_string_barcode_scanner_not_found),
                                    Toast.LENGTH_LONG).show();
                        }
                    } else {
                        statusToUse = Status.START_APPOINTMENT;
                        new UpdateStatusTask().execute();
                    }
                    break;
                case ON_ROUTE:
                    if (UserUtilitiesSingleton.getInstance().user
                            .usesBarcodesForLocations()) {
                        try {
                            Intent intent = new Intent(
                                    "com.google.zxing.client.android.SCAN");
                            intent.putExtra("SCAN_MODE", "ONE_D_MODE");
                            startActivityForResult(intent,
                                    ACTIVITY_RESULT_LOCATION_SCAN);
                        } catch (android.content.ActivityNotFoundException ex) {
                            // Potentially direct the user to the Market with a
                            // Dialog
                            Toast.makeText(
                                    mActivity, getString(R.string.app_string_barcode_scanner_not_found),
                                    Toast.LENGTH_LONG).show();
                        }
                    } else {
                        statusToUse = Status.START_APPOINTMENT;
                        new UpdateStatusTask().execute();
                    }
                    break;
                case START_APPOINTMENT:
                case RESTART_APPOINTMENT:
                    statusToUse = Status.SUSPEND_APPOINTMENT;
                    new UpdateStatusTask().execute();
                    break;
                case PARTS_RUN_APPOINTMENT:
                    if (UserUtilitiesSingleton.getInstance().user
                            .usesBarcodesForLocations()) {
                        try {
                            Intent intent = new Intent(
                                    "com.google.zxing.client.android.SCAN");
                            intent.putExtra("SCAN_MODE", "ONE_D_MODE");
                            startActivityForResult(intent,
                                    ACTIVITY_RESULT_LOCATION_SCAN);
                        } catch (android.content.ActivityNotFoundException ex) {
                            // Potentially direct the user to the Market with a
                            // Dialog
                            Toast.makeText(
                                    mActivity,
                                    getString(R.string.app_string_barcode_scanner_not_found),
                                    Toast.LENGTH_LONG).show();
                        }
                    } else {
                        statusToUse = Status.START_APPOINTMENT;
                        new UpdateStatusTask().execute();
                    }
                    break;
                default:
                    // Nothing
                    break;
            }
        }
    };

    private OnClickListener mStatusThreeListener = new OnClickListener() {
        @Override
        public void onClick(View v) {

            if (CommonUtilities.isNetworkAvailable(mActivity)) {
                if (StatusBuffer.instance().haveQueue()) {
                    new FlushTask(mActivity).execute();
                    return;
                }
            }

            // This is "ALWAYS" parts-run
            statusToUse = Status.PARTS_RUN_APPOINTMENT;
            new UpdateStatusTask().execute();
        }
    };

    private OnClickListener mViewInvoiceButtonListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            startInvoiceManually();
        }

    };

    private OnClickListener mViewCommentsListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            ActivityAppointmentCommentsView.previousActivity = ActivityAppointmentCommentsView.PREVIOUS_ACTIVITY_APPOINTMENT_TRACKABLE;
            Intent i = new Intent(mActivity, ActivityAppointmentCommentsView.class);
            startActivity(i);
        }
    };

    private OnClickListener invoiceSaveListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!TextUtils.isEmpty(mInvoiceNumberText.getText().toString())) {
                String number = mInvoiceNumberText.getText().toString();
                AppDataSingleton.getInstance().getAppointment().setWorkOrderNumber(number);
                int launchMode = (Integer) v.getTag();
                switch (launchMode) {
                    case START_INVOICE_AFTER_FINISH:
                        statusToUse = Status.FINISH_APPOINTMENT;
                        new UpdateStatusTask().execute();
                        break;
                    case START_INVOICE_MANUALLY:
                        startInvoiceManually();
                        break;
                    default:
                        break;
                }
                mInvoiceDialog.dismiss();
            }
        }
    };


    private OnClickListener mInvoiceCancelListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            mInvoiceDialog.dismiss();
        }
    };

    private OnClickListener mEditAppointmentListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            // ViewAddAppointment.editMode = true;
            Intent i = new Intent(mActivity, ActivityAppointmentAdd.class);

            // Indicate how we arrived at this location
            AppDataSingleton.getInstance().setAppointmentAddViewMode(
                    Constants.APPOINTMENT_ADD_VIEW_FROM_APPOINTMENT);

            startActivity(i);
            // finish();
        }
    };

    private OnClickListener buttonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = null;

            switch (v.getId()) {
                case R.id.activity_appointment_button_on_truck_items:
                    i = new Intent(mActivity,
                            ActivityAppointmentOnTruckAddedFragment.class);
                    startActivityForResult(i, OnTruckAddedFragment.ON_TRUCK_ITEMS);
                    break;
                case R.id.activity_appointment_button_view_estimate:

                    ActivityEstimateView.estimateId = 0;
                    ActivityEstimateView.closeAppointmentOnComplete = false;
                    AppDataSingleton.getInstance().setEstimateListViewMode(
                            Constants.ESTIMATE_LIST_VIEW_FROM_APPOINTMENT);

                    if (mCallback != null) {
                        mCallback.onActionSelected(ESTIMATE_LIST_SELECTED);
                    } else {
                        i = new Intent(mActivity,
                                ActivityEstimateListFragment.class);
                        i.putExtra(IntentExtras.LOCATION_ID, AppDataSingleton
                                .getInstance().getAppointment().getLocationId());
                        startActivityForResult(i, ACTIVITY_RESULT_ESTIMATES);
                    }
                    break;

                case R.id.activity_appointment_button_add_notes:
                    DialogAppointmentNotesAdd addNotesDialog = new DialogAppointmentNotesAdd(
                            mActivity);
                    addNotesDialog
                            .setOnDismissListener(new DialogInterface.OnDismissListener() {

                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    // This means that we've finished
                                    if (submitOnDialogClose) {
                                        new SubmitAddNotesTask().execute();
                                    }
                                }
                            });

                    addNotesDialog.show();
                    break;

                case R.id.activity_appointment_button_add_equipment:
                    if (mCallback != null) {
                        mCallback.onActionSelected(EQUIPMENT_ADD_SELECTED);
                    } else {
                        i = new Intent(mActivity,
                                AppointmentEquipmentAddActivity.class);
                        startActivity(i);
                    }
                    break;

                case R.id.activity_appointment_button_upload_file:

                    DialogScanAndUpload dialog = new DialogScanAndUpload(mActivity,
                            mActivity, AppointmentFragment.this);

                    dialog.show();
                    break;

                case R.id.activity_appointment_button_add_agreement:
                    new GetServiceAgreementsListTask(true).execute();
                    break;

                case R.id.activity_appointment_button_view_gallery:
                    new ViewGalleryTask(mActivity).execute();
                    break;

                case R.id.activity_appointment_button_part_order:
                    if (mCallback != null) {
                        mCallback.onActionSelected(PART_ORDERS_SELECTED);
                    } else {
                        i = new Intent(mActivity,
                                ActivityPartOrdersApptListViewFragment.class);
                        i.putExtra("appointmentId", appointmentId);
                        i.putExtra("invoiceId", AppDataSingleton.getInstance()
                                .getAppointment().getInvoiceId());
                        startActivity(i);
                    }
                    break;

                case R.id.activity_appointment_button_checklist:
                    i = new Intent(mActivity, ActivityApptQuestionsFragment.class);
                    startActivity(i);
                    break;
                case R.id.activity_appointment_button_pdf:
                    i = new Intent(mActivity, ActivityPdfDocumentsFragment.class);
                    i.putExtra("apptId", AppDataSingleton.getInstance()
                            .getAppointment().getId());
                    i.putExtra("templateTypeMode", Constants.PDF_APPOINTMENT_MODE);
                    startActivity(i);
                    break;
                default:
                    // Nothing
                    break;
            }
        }
    };

    OnActionSelectedListener mCallback = null;

    private boolean savedOffline;

	/*
     * Fragment's lifecycle
	 */

    public interface OnActionSelectedListener {
        public void onActionSelected(int id);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            if (activity instanceof ActivityAppointmentDualFragment) {
                mCallback = (OnActionSelectedListener) activity;
            }
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        return inflater.inflate(R.layout.layout_appointment_view, container,
                false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mActivity = getActivity();
        final Bundle args = getArguments();
        if (args != null) {
            appointmentId = args.getInt(APPOINTMENT_ID);
        }

        if (savedInstanceState != null) {
            appointmentId = savedInstanceState.getInt(APPOINTMENT_ID);

            AppDataSingleton.getInstance().getAppointment()
                    .setId(appointmentId);
        }

        AppDataSingleton.getInstance().setInvoiceViewMode(
                Constants.INVOICE_VIEW_FROM_APPOINTMENT);
        AppDataSingleton.getInstance().setEstimateViewMode(
                Constants.ESTIMATE_VIEW_FROM_APPOINTMENT);

        String oldLat = AppDataSingleton.getInstance().getAppointment()
                .getLocationLatitude();
        String oldLong = AppDataSingleton.getInstance().getAppointment()
                .getLocationLongitude();

        if (!TextUtils.isEmpty(oldLat) && !TextUtils.isEmpty(oldLong)) {
            latitude = Double.parseDouble(oldLat);
            longitude = Double.parseDouble(oldLong);
        }

        initLocation();
        initLayoutResources();
    }

    @Override
    public void onResume() {
        super.onResume();
        new GetAppointmentTask().execute();
    }

    @Override
    public void onPause() {
        super.onPause();
        lm.removeUpdates(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.clear();
        outState.putInt(APPOINTMENT_ID, appointmentId);
        outState.putInt("appointmentId", AppDataSingleton.getInstance()
                .getAppointment().getId());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {

            if (resultCode == Activity.RESULT_OK) {
                switch (requestCode) {
                    case ACTIVITY_RESULT_FILE:

                        String FilePath = data.getData().getPath();
                        String fileName = FilePath.substring(FilePath.lastIndexOf("/") + 1, FilePath.lastIndexOf("."));
                        String fileExtension = FilePath.substring(FilePath.lastIndexOf(".") + 1, FilePath.length());

                        Log.e("File Info", "File Path: " + FilePath);
                        Log.e("File Info", "File Name: " + fileName);
                        Log.e("File Info", "File Ext: " + fileExtension);

                        Intent intent = new Intent(getActivity(), RestIntentService.class);
                        intent.setAction(Constants.ACTION_SEND_FILE);
                        intent.putExtra(RestIntentService.EXTRA_FILE_PATH, FilePath);
                        intent.putExtra(RestIntentService.EXTRA_CUSTOMER_ID,
                                AppDataSingleton.getInstance().getCustomer().getId());
                        intent.putExtra(RestIntentService.EXTRA_INVOICE_ID,
                                AppDataSingleton.getInstance().getAppointment().getInvoiceId());
                        intent.putExtra(RestIntentService.EXTRA_APPOINTMENT_ID,
                                AppDataSingleton.getInstance().getAppointment().getId());
                        intent.putExtra(RestIntentService.EXTRA_FILE_NAME, fileName);
                        intent.putExtra(RestIntentService.EXTRA_DISPLAY_NAME, fileName);
                        intent.putExtra(RestIntentService.EXTRA_FILE_EXTENSION, fileExtension);

                        mActivity.startService(intent);
                        break;

                    case ACTIVITY_RESULT_LOCATION_SCAN:

                        final String barcode = data.getStringExtra("SCAN_RESULT");

                        if (TextUtils.isEmpty(
                                AppDataSingleton.getInstance().getAppointment().getAppointmentLocationCustomCode())) {

                            final DialogLocationScanAndAdd addDialog = new DialogLocationScanAndAdd(
                                    mActivity, mActivity);

                            addDialog.updatingBarcode = false;
                            addDialog.show();

                            addDialog.setOnDismissListener(new OnDismissListener() {

                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    if (addDialog.marryLocation) {
                                        SendLocationPairBarcodeTask pairTask = new SendLocationPairBarcodeTask();
                                        pairTask.barcode = barcode;
                                        pairTask.locationId = AppDataSingleton
                                                .getInstance().getAppointment()
                                                .getLocationId();
                                        pairTask.execute();
                                    }

                                    statusToUse = Status.START_APPOINTMENT;
                                    new UpdateStatusTask().execute();
                                }
                            });
                            // location?
                        } else {
                            // They have a location code already
                            // Do they match?
                            if (AppDataSingleton.getInstance().getAppointment()
                                    .getAppointmentLocationCustomCode()
                                    .equals(barcode)) {
                                // / they match, good to go.
                                statusToUse = Status.START_APPOINTMENT;
                                new UpdateStatusTask().execute();
                            } else {
                                final DialogLocationScanAndAdd addDialog = new DialogLocationScanAndAdd(
                                        mActivity, mActivity);

                                addDialog.updatingBarcode = true;
                                addDialog.show();

                                addDialog
                                        .setOnDismissListener(new OnDismissListener() {

                                            @Override
                                            public void onDismiss(
                                                    DialogInterface dialog) {
                                                if (addDialog.marryLocation) {
                                                    SendLocationPairBarcodeTask pairTask = new SendLocationPairBarcodeTask();
                                                    pairTask.barcode = barcode;
                                                    pairTask.locationId = AppDataSingleton
                                                            .getInstance()
                                                            .getAppointment()
                                                            .getLocationId();
                                                    pairTask.execute();
                                                }

                                                statusToUse = Status.START_APPOINTMENT;
                                                new UpdateStatusTask().execute();
                                            }
                                        });
                            }
                        }
                        break;
                    case OnTruckAddedFragment.ON_TRUCK_ITEMS:
                        // new GetAppointmentTask().execute();
                        break;
                    case ACTIVITY_RESULT_QUESTIONS:
                        statusToUse = Status.FINISH_APPOINTMENT;
                        // If they're forced to use Invoice Input or not
                        if (AppDataSingleton.getInstance().getAppointment().getOwnerForceWorkOrderNumberInput()) {
                            if (AppDataSingleton.getInstance().getAppointment().getWorkOrderNumber().isEmpty()) {
                                showInvoiceNumberDialog(START_INVOICE_AFTER_FINISH);
                            }
                        } else {
                            new UpdateStatusTask().execute();
                        }
                        break;
                    case ACTIVITY_RESULT_ESTIMATES:
                        // new GetAppointmentTask().execute();
                        break;
                    case ACTIVITY_RESULT_INVOICE:
                        // new GetAppointmentTask().execute();
                        break;
                    default:
                        // Nothing
                        break;
                }
            }
        } catch (java.lang.OutOfMemoryError e) {
            DialogErrorPopup errorPopup = new DialogErrorPopup(
                    mActivity,
                    "Notice",
                    "Unable to Save Photo Because of High Camera Resolution. Please Go to Your Settings and Change the Image Resolution on Your Camera to 1280x720 or below. Contact Support for Help if You Continue to Have Difficulties.",
                    null);
            errorPopup.show();
        }
    }

	/*
     * ==========================================================================
	 * ==
	 * =========================================================================
	 * Inner methods
	 */

    private String getDirectionsUrl(final double fromLat, final double fromLon, final String toLat, final String toLon) {// connect to map web service
        StringBuffer urlString = new StringBuffer();
        urlString.append("http://maps.google.com/maps?f=d&hl=en");
        urlString.append("&saddr=");// from
        urlString.append(fromLat);
        urlString.append(',');
        urlString.append(fromLon);
        urlString.append("&daddr=");// to
        urlString.append(toLat);
        urlString.append(',');
        urlString.append(toLon);
        urlString.append("&ie=UTF8&0&om=0&output=kml");
        return urlString.toString();
    }

    private String getDirectionsToAddressUrl(final double fromLat, final double fromLon, final String toAddress) {// connect to map web service
        final StringBuffer urlString = new StringBuffer();
        urlString.append("http://maps.google.com/maps?f=d&hl=en");
        urlString.append("&saddr=");// from
        urlString.append(fromLat);
        urlString.append(',');
        urlString.append(fromLon);
        urlString.append("&daddr=");// to
        urlString.append(toAddress);
        urlString.append("&ie=UTF8&0&om=0&output=kml");
        return urlString.toString();
    }

    public void showAgreementDialog() {

        // Indicator for how we arrived at this view
        AppDataSingleton.getInstance().setServiceAgreementAddViewMode(
                Constants.SERVICE_AGREEMENT_ADD_VIEW_FROM_VIEW_APPOINTMENT);

        serviceAgreementDialog = new DialogServiceAgreementSelect(mActivity);
        serviceAgreementDialog
                .setOnDismissListener(new DialogInterface.OnDismissListener() {

                    @Override
                    public void onDismiss(DialogInterface dialog) {

                        if (serviceAgreementDialog.submitOnComplete) {
                            selectedAgreementId = serviceAgreementDialog.selectedAgreementId;
                            new SubmitAddAgreementTask().execute();
                        }
                    }
                });

        serviceAgreementDialog.show();
    }

    private void showInvoiceNumberDialog(int launchMode) {
        mInvoiceDialog = new Dialog(mActivity);
        mInvoiceDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mInvoiceDialog.setContentView(R.layout.dialog_layout_invoice_input);

        mInvoiceSaveButton = (TextView) mInvoiceDialog
                .findViewById(R.id.dialog_invoice_input_button_save);
        mInvoiceCancelButton = (TextView) mInvoiceDialog
                .findViewById(R.id.dialog_invoice_input_button_cancel);

        mInvoiceNumberText = (EditText) mInvoiceDialog.findViewById(R.id.dialog_invoice_input_edittext_value);


        mInvoiceNumberPrefix = (TextView) mInvoiceDialog.findViewById(R.id.dialog_invoice_input_prefix_value);
        mInvoiceNumberPrefix.setText(AppDataSingleton.getInstance().getAppointment().getWorkOrderNumberPrefix());

        mInvoiceSaveButton.setOnClickListener(invoiceSaveListener);
        mInvoiceSaveButton.setTag(launchMode);
        mInvoiceCancelButton.setOnClickListener(mInvoiceCancelListener);

        mInvoiceDialog.show();
    }

    private void updateStatus(Status status) throws NonfatalException {

        DateFormat df = null;
        df = new SimpleDateFormat("M/d/yy h:mm a");
        Date todaysDate = new Date();// get current date time with Date()
        String currentDateTime = df.format(todaysDate).replace("am", "AM").replace("pm", "PM");
        ;

        String statusToUse = "";

        switch (status) {
            case ON_ROUTE:
                statusToUse = Status.ON_ROUTE.toString();
                break;
            case START_APPOINTMENT:
                statusToUse = Status.START_APPOINTMENT.toString();
                break;
            case SUSPEND_APPOINTMENT:
                statusToUse = Status.SUSPEND_APPOINTMENT.toString();
                break;
            case RESTART_APPOINTMENT:
                statusToUse = Status.RESTART_APPOINTMENT.toString();
                break;
            case FINISH_APPOINTMENT:
                statusToUse = Status.FINISH_APPOINTMENT.toString();
                break;
            case MOVE_APPOINTMENT:
                statusToUse = Status.MOVE_APPOINTMENT.toString();
                break;
            case CLOSE_APPOINTMENT:
                statusToUse = Status.CLOSE_APPOINTMENT.toString();
                break;
            case NOT_STARTED:
                statusToUse = Status.NOT_STARTED.toString();
                break;
            case PARTS_RUN_APPOINTMENT:
                statusToUse = Status.PARTS_RUN_APPOINTMENT.toString();
                break;
            default:
                // Nothing
                break;
        }


        if (!(status == Status.CLOSE_APPOINTMENT)) {
            AppDataSingleton.getInstance().getAppointment().setStatus(status);
        }

        AppDataSingleton.getInstance().getAppointment()
                .setLocationLatitude(latitude + "");
        AppDataSingleton.getInstance().getAppointment()
                .setLocationLongitude(longitude + "");
        AppDataSingleton.getInstance().getAppointment()
                .setLocationAccuracy(accuracy + "");
        boolean buffered;

        if (CommonUtilities.isNetworkAvailable(mActivity)) {
            buffered = false;
        } else {
            buffered = true;
            savedOffline = true;
            if (statusToUse.equals(Status.FINISH_APPOINTMENT.toString())) {
                AppDataSingleton.getInstance().getAppointment()
                        .setEverybodyElseFinished(true);
            }
        }

        if (status == Status.CLOSE_APPOINTMENT) {
            if (AppDataSingleton.getInstance().getAppointment().getInvoiceId() != 0
                    || AppDataSingleton.getInstance().getAppointment()
                    .isUsingInvoices()) {
                return;
            }
        }

        RESTAppointment.statusUpdate(AppDataSingleton.getInstance()
                        .getAppointment().getId(), statusToUse, UserUtilitiesSingleton
                        .getInstance().user.getServiceProviderId(), latitude,
                longitude, accuracy, AppDataSingleton.getInstance()
                        .getAppointment().getWorkOrderNumber(), currentDateTime, buffered, TimeZone.getDefault()
        );

    }

    private void initLayoutResources() {
        /*
         * This is the "update status" and the sliding layout that accompanies
		 * it
		 */
        linearLayoutUpdateStatus = (LinearLayout) mActivity
                .findViewById(R.id.activity_appointment_linearlayout_status_update);
        linearLayoutUpdateStatusButtons = (LinearLayout) mActivity
                .findViewById(R.id.activity_appointment_linearlayout_status_update_buttons);
        buttonStatusUpdateOne = (LinearLayout) mActivity
                .findViewById(R.id.activity_appointment_linearlayout_button_status_update_one);
        buttonStatusUpdateTwo = (LinearLayout) mActivity
                .findViewById(R.id.activity_appointment_linearlayout_button_status_update_two);
        buttonStatusUpdateThree = (LinearLayout) mActivity
                .findViewById(R.id.activity_appointment_linearlayout_button_status_update_three);
        textStatusUpdateOne = (TextView) mActivity
                .findViewById(R.id.activity_appointment_textview_status_update_one);
        textStatusUpdateTwo = (TextView) mActivity
                .findViewById(R.id.activity_appointment_textview_status_update_two);
        textStatusUpdateThree = (TextView) mActivity
                .findViewById(R.id.activity_appointment_textview_status_update_three);

		/* Layout Pieces */
        /* Text Views */
        textAppointmentType = (TextView) mActivity
                .findViewById(R.id.activity_appointment_textview_appointment_type);
        textAppointmentStatus = (TextView) mActivity
                .findViewById(R.id.activity_appointment_textview_appointment_status);
        textAppointmentDateTime = (TextView) mActivity
                .findViewById(R.id.activity_appointment_textview_appointment_date_and_time);

        textAppointmentDay = (TextView)mActivity.findViewById(R.id.activity_appointment_textview_appointment_day);
        textAppointmentDateTime1 = (TextView) mActivity
                .findViewById(R.id.activity_appointment_textview_appointment_date_and_time1);
        textAppointmentCustomerName = (TextView) mActivity
                .findViewById(R.id.activity_appointment_textview_customer_name);
        textAppointmentCustomerAddress = (TextView) mActivity
                .findViewById(R.id.activity_appointment_textview_customer_address);
        textAppointmentCustomerAddressName = (TextView) mActivity
                .findViewById(R.id.activity_appointment_textview_customer_address_name);
        textAppointmentAddedEquipment = (TextView) mActivity
                .findViewById(R.id.activity_appointment_textview_added_equipment);
        textAppointmentAddedAgreement = (TextView) mActivity
                .findViewById(R.id.activity_appointment_textview_added_agreement);
        textAppointmentWorkingTechnicians = (TextView) mActivity
                .findViewById(R.id.activity_appointment_textview_participants);

		/* Customer Data */
        linearLayoutCustomerDetails = (LinearLayout) mActivity
                .findViewById(R.id.activity_appointment_linearlayout_customer_details);

		/* Edit Text(s) */
        textAppointmentNotes = (TextView) mActivity
                .findViewById(R.id.activity_appointment_textview_notes);

        textAppointmentMultiType = (TextView) mActivity
                .findViewById(R.id.activity_appointment_textview_appointment_multi_day);

        textAppointmentOrderNumber = (TextView) mActivity.findViewById(
                R.id.activity_appointment_textview_appointment_order_number);
        textAppointmentOrderNumber.setTypeface(null, Typeface.ITALIC);

        customFieldLabel = (TextView) mActivity.findViewById(R.id.activity_appointment_title_field1);
        customFieldValue = (TextView) mActivity.findViewById(R.id.activity_appointment_field1);

        secondCustomFieldLabel = (TextView) mActivity.findViewById(R.id.activity_appointment_title_field2);
        secondCustomFieldValue = (TextView) mActivity.findViewById(R.id.activity_appointment_field2);

		/* Dashboard Buttons */
        buttonPartOrders = (ImageView) mActivity
                .findViewById(R.id.activity_appointment_button_part_order);
        buttonViewComments = (ImageView) mActivity
                .findViewById(R.id.activity_appointment_button_view_comments);
        buttonViewInvoice = (ImageView) mActivity
                .findViewById(R.id.activity_appointment_button_view_invoice);
        buttonViewEstimate = (ImageView) mActivity
                .findViewById(R.id.activity_appointment_button_view_estimate);
        buttonOnTruck = (ImageView) mActivity
                .findViewById(R.id.activity_appointment_button_on_truck_items);
        buttonAddEquipment = (ImageView) mActivity
                .findViewById(R.id.activity_appointment_button_add_equipment);
        buttonUploadFile = (ImageView) mActivity
                .findViewById(R.id.activity_appointment_button_upload_file);
        buttonPhotoGallery = (ImageView) mActivity
                .findViewById(R.id.activity_appointment_button_view_gallery);
        buttonAddNotes = (ImageView) mActivity
                .findViewById(R.id.activity_appointment_button_add_notes);
        buttonAddAgreement = (ImageView) mActivity
                .findViewById(R.id.activity_appointment_button_add_agreement);
        buttonChecklist = (ImageView) mActivity
                .findViewById(R.id.activity_appointment_button_checklist);
        buttonPdf = (ImageView) mActivity
                .findViewById(R.id.activity_appointment_button_pdf);


        buttonEditAppointment = (LinearLayout) mActivity
                .findViewById(R.id.activity_appointment_button_edit_appointment);

        scrollView = (ScrollView) mActivity
                .findViewById(R.id.activity_appointment_scrollview);
    }

    private boolean isShowingAdressName(String adress) {
        String locationAdress = adress;

        String locationName = AppDataSingleton.getInstance().getAppointment()
                .getLocationName();
        locationName = locationName.replaceAll("[,;\\s]", "").toLowerCase();
        locationAdress = locationAdress.replaceAll("[,;\\s]", "").toLowerCase();

        if (locationAdress.equals(locationName)) {
            return false;
        } else {
            return true;
        }
    }

    private void initLocation() {
        lm = (LocationManager) mActivity
                .getSystemService(mActivity.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        bestProvider = lm.getBestProvider(criteria, true);
        if (bestProvider == null) {
            if (SkedsApplication.getInstance().isUseGps()) {
                bestProvider = LocationManager.GPS_PROVIDER;
            } else {
                bestProvider = LocationManager.NETWORK_PROVIDER;
            }
        }

        requestNewLocation();

        Location lastLocation = lm.getLastKnownLocation(bestProvider);

        if (lastLocation != null) {
            longitude = lastLocation.getLongitude();
            latitude = lastLocation.getLatitude();
            accuracy = lastLocation.getAccuracy();
        }
    }

    private void locationTimer() {

        new Handler().postDelayed(new Runnable() {
            // @Override
            @Override
            public void run() {
                locationTimeExpired = true;
            }
        }, 500);
    }

    private String dateGenerator(String theDate) {
        String formattedDate = "";

        theDate = theDate.trim();
        DateFormat df = new SimpleDateFormat("EEEE, MM/dd/yyyy");
        DateFormat df2 = new SimpleDateFormat("MM/dd/yyyy");

        try {
            Date today = df.parse(theDate);

            DateFormat formatter = new SimpleDateFormat("EEEE, MMMM d, yyyy");
            formattedDate = formatter.format(today);
        } catch (ParseException e) {
            try {
                Date today = df2.parse(theDate);

                DateFormat formatter = new SimpleDateFormat("EEEE, MMMM d, yyyy");
                formattedDate = formatter.format(today);
            } catch (ParseException e2) {
                Log.e("FieldLocate", "Could not parse date: " + theDate);
            }
        }
        return formattedDate;
    }

    private void showCallOrEditDialog() {

        if (AppDataSingleton.getInstance().getCustomer().locationList.isEmpty()) {
            Toast.makeText(mActivity, "No Location Info", Toast.LENGTH_SHORT).show();
            return;
        }
        final com.skeds.android.phone.business.Utilities.General.ClassObjects.Location loc = AppDataSingleton.getInstance().getCustomer().locationList.get(
                0);
        final DialogLocationCallOrEdit locationDialog = new DialogLocationCallOrEdit(mActivity, loc,
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent callIntent = new Intent(
                                Intent.ACTION_DIAL);
                        switch (v.getId()) {
                            case R.id.phone1:
                                callIntent.setData(
                                        Uri.parse(
                                                getString(
                                                        R.string.dial_number_action,
                                                        loc.getPhone1())
                                        )
                                );
                                startActivity(
                                        callIntent);
                                break;
                            case R.id.phone2:
                                callIntent.setData(
                                        Uri.parse(
                                                getString(
                                                        R.string.dial_number_action,
                                                        loc.getPhone2())
                                        )
                                );
                                startActivity(
                                        callIntent);
                                break;
                            case R.id.edit:
                                showEditDialog();
                                break;
                            default:
                                break;
                        }
                    }
                }
        );

        locationDialog.show();
    }

    private void showEditDialog() {
        // existen location
        Intent it = new Intent(mActivity,
                ActivityLocationAddEdit.class);

        int position = 0;

        for (com.skeds.android.phone.business.Utilities.General.ClassObjects.Location loc : AppDataSingleton.getInstance().getCustomer().locationList) {
            if (loc.getId() == AppDataSingleton.getInstance().getCustomer().locationList.get(0).getId()) {
                position = AppDataSingleton.getInstance().getCustomer().locationList.indexOf(loc);
            }

        }

        it.putExtra(ActivityLocationAddEdit.LOCATION_POS, position); // new
        startActivity(it);
    }

    private void startInvoiceManually() {
        if (AppDataSingleton.getInstance().getAppointment().getInvoiceId() != 0) {
            new ViewInvoiceTask().execute();
        } else {
            if (mCallback != null) {
                mCallback.onActionSelected(NEW_INVOICE_SELECTED);
            } else {
                Intent i = new Intent(mActivity,
                        ActivityInvoiceSingleFragment.class);
                i.putExtra(InvoiceFragment.EXTRA_NEW_INVOICE, true);
                i.putExtra(InvoiceFragment.EXTRA_ORDER_NUMBER, AppDataSingleton.getInstance().getAppointment().getWorkOrderNumber());
                i.putExtra(InvoiceFragment.EXTRA_LOADED_FROM_APPT, true);
                startActivityForResult(i, ACTIVITY_RESULT_INVOICE);
            }
        }
    }


    private void showDialogEstimateOrInvoice() {
        DialogEstimateOrInvoice customDialog = new DialogEstimateOrInvoice(
                mActivity);
        customDialog.show();

        customDialog.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {

                Intent i = null;
                switch (DialogEstimateOrInvoice.transferTo) {
                    case DialogEstimateOrInvoice.TRANSFER_TO_INVOICE:

                        InvoiceFragment.isReadOnly = false;

                        if (CommonUtilities.isNetworkAvailable(mActivity)) {
                            new ViewInvoiceTask().execute();
                        }
                        break;
                    case DialogEstimateOrInvoice.TRANSFER_TO_ESTIMATE:

                        ActivityEstimateView.estimateId = 0;
                        ActivityEstimateView.closeAppointmentOnComplete = false;
                        AppDataSingleton.getInstance().setEstimateListViewMode(
                                Constants.ESTIMATE_LIST_VIEW_FROM_APPOINTMENT);

                        if (mCallback != null) {
                            mCallback.onActionSelected(ESTIMATE_LIST_SELECTED);
                        } else {
                            i = new Intent(mActivity,
                                    ActivityEstimateListFragment.class);
                            i.putExtra(IntentExtras.LOCATION_ID, AppDataSingleton
                                    .getInstance().getAppointment().getLocationId());
                            startActivityForResult(i, ACTIVITY_RESULT_ESTIMATES);
                        }
                        break;
                    default:
                        // Nothing
                        break;
                }
            }
        });
    }

    private void requestNewLocation() {
        if (lm == null) {
            return;
        }

        if (SkedsApplication.getInstance().isUseGps()) {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 12000, 10f,
                    this);
        } else {
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000,
                    300f, this);
        }
    }

    private void setupUI() {
        Customer customer = AppDataSingleton.getInstance().getCustomer();

        if (AppDataSingleton.getInstance().getAppointment().getStatus() == com.skeds.android.phone.business.Utilities.General.ClassObjects.Status.CLOSE_APPOINTMENT) {
            isReadOnly = true;
        } else {
            isReadOnly = false;
        }

        statusToUse = AppDataSingleton.getInstance().getAppointment().getStatus();

        boolean canUpdateStatus = false;
        if (AppDataSingleton.getInstance().getAppointment()
                .isEverybodyElseFinished()
                || (!AppDataSingleton.getInstance().getAppointment()
                .isEverybodyElseFinished() && AppDataSingleton
                .getInstance().getAppointment().getStatus() != com.skeds.android.phone.business.Utilities.General.ClassObjects.Status.FINISH_APPOINTMENT)) {
            canUpdateStatus = true;
        } else {
            canUpdateStatus = false;
        }

        if (!isReadOnly && canUpdateStatus) {
            linearLayoutUpdateStatus.setOnClickListener(mStatusUpdateLayoutListener);
        } else {
            mActivity.findViewById(R.id.click_to_update_title).setVisibility(View.INVISIBLE);
        }


        if (!AppDataSingleton.getInstance().getCustomQuestionList().isEmpty()) {
            buttonChecklist.setEnabled(true);
        } else {
            buttonChecklist.setEnabled(false);
        }

        buttonPartOrders.setEnabled(UserUtilitiesSingleton.getInstance().user
                .isAllowPartOrdering());
        buttonChecklist.setOnClickListener(buttonListener);
        buttonPartOrders.setOnClickListener(buttonListener);

        linearLayoutCustomerDetails.setOnClickListener(mCustomerInfoListener);

        buttonStatusUpdateOne.setOnClickListener(mStatusOneListener);
        buttonStatusUpdateTwo.setOnClickListener(mStatusTwoListener);
        buttonStatusUpdateThree.setOnClickListener(mStatusThreeListener);

        buttonAddNotes.setOnClickListener(buttonListener);
        if (UserUtilitiesSingleton.getInstance().user
                .isAllowAddEditAppointments()) {
            buttonEditAppointment.setOnClickListener(mEditAppointmentListener);
        } else {
            buttonEditAppointment.setVisibility(View.GONE);
        }
        buttonAddEquipment.setOnClickListener(buttonListener);

        buttonViewComments.setOnClickListener(mViewCommentsListener);
        buttonViewEstimate.setOnClickListener(buttonListener);
        buttonPhotoGallery.setOnClickListener(buttonListener);
        buttonPdf.setOnClickListener(buttonListener);
        buttonOnTruck.setOnClickListener(buttonListener);
        buttonUploadFile.setOnClickListener(buttonListener);
        buttonAddAgreement.setOnClickListener(buttonListener);

        if (!AppDataSingleton.getInstance().getAppointment().getCustomField().getName().isEmpty()) {
            customFieldLabel.setText(AppDataSingleton.getInstance().getAppointment().getCustomField().getName());
            customFieldValue.setText(AppDataSingleton.getInstance().getAppointment().getCustomField().getValue());
        } else {
            customFieldLabel.setVisibility(View.GONE);
            customFieldValue.setVisibility(View.GONE);
        }
        if (!AppDataSingleton.getInstance().getAppointment().getSecondCustomField().getName().isEmpty()) {
            secondCustomFieldLabel.setText(
                    AppDataSingleton.getInstance().getAppointment().getSecondCustomField().getName());
            secondCustomFieldValue.setText(
                    AppDataSingleton.getInstance().getAppointment().getSecondCustomField().getValue());
        } else {
            secondCustomFieldLabel.setVisibility(View.GONE);
            secondCustomFieldValue.setVisibility(View.GONE);
        }

        String apptTypeName = AppDataSingleton.getInstance().getAppointment()
                .getApptTypeName();
        if (apptTypeName != null) {
            appointmentTypeString = apptTypeName;
        } else {
            appointmentTypeString = "";
        }

        // String location =
        // Globals.mBusinessSchedule[arrayIndex[0]].appointment[arrayIndex[1]].getLocationValue();
        String location = AppDataSingleton.getInstance().getAppointment()
                .getLocationValue();
        if (location != null) {
            eventLocationString = location;

            String eventA, eventB, finalLocation = "";
            eventA = eventLocationString.substring(0,
                    eventLocationString.indexOf(',') + 1);
            eventB = eventLocationString.substring(
                    eventLocationString.indexOf(',') + 1,
                    eventLocationString.length());

            SpannableString contentA = new SpannableString(eventA);
            contentA.setSpan(new UnderlineSpan(), 0, contentA.length(), 0);

            SpannableString contentB = new SpannableString(eventB);
            contentB.setSpan(new UnderlineSpan(), 0, contentB.length(), 0);

            finalLocation = contentA.toString().trim() + " "
                    + contentB.toString().trim();
            eventLocationString = finalLocation;
        } else {
            eventLocationString = "";
        }

        String notes = AppDataSingleton.getInstance().getAppointment()
                .getNotes();
        if (notes != null) {
            eventNotesString = notes;
        } else {
            eventNotesString = "";
        }

        eventServiceProviderString = "";
        if (!AppDataSingleton.getInstance().getAppointment().getParticipantList()
                .isEmpty()) {

            StringBuilder output = new StringBuilder();
            for (int i = 0; i < AppDataSingleton.getInstance().getAppointment().getParticipantList()
                    .size(); i++) {

                output.append(AppDataSingleton.getInstance().getAppointment().getParticipantList()
                        .get(i).getFirstName());
                output.append(" ");
                output.append(AppDataSingleton.getInstance().getAppointment().getParticipantList()
                        .get(i).getLastName());

                if (!AppDataSingleton.getInstance().getAppointment().getParticipantList()
                        .get(i).getTypeName().isEmpty()) {
                    output.append(" - ");
                }
                output.append(AppDataSingleton.getInstance().getAppointment().getParticipantList()
                        .get(i).getTypeName());

                if (i != AppDataSingleton.getInstance().getAppointment().getParticipantList()
                        .size()) {
                    output.append("\n\n");
                }
            }
            eventServiceProviderString = output.toString();
        } else {
            eventServiceProviderString = "";
        }

        textAppointmentCustomerName.setText(customer.getLongName());
        textAppointmentType.setText(appointmentTypeString);
        textAppointmentWorkingTechnicians.setText(eventServiceProviderString);

        String addedEquipment = "No Equipment Added";
        if (!AppDataSingleton.getInstance().getAppointment().getEquipmentList()
                .isEmpty()) {
            StringBuilder output = new StringBuilder();
            for (int i = 0; i < AppDataSingleton.getInstance().getAppointment().getEquipmentList()
                    .size(); i++) {
                output.append(AppDataSingleton.getInstance().getAppointment().getEquipmentList()
                        .get(i).getName());
                output.append(" Model: ");
                output.append(AppDataSingleton.getInstance().getAppointment().getEquipmentList()
                        .get(i).getModelNumber());
                output.append(" Serial: ");
                output.append(AppDataSingleton.getInstance().getAppointment().getEquipmentList()
                        .get(i).getSerialNumber());
                output.append("\n");
                addedEquipment = output.toString();
            }
        }
        textAppointmentAddedEquipment.setText(addedEquipment);

        String addedAgreement = "No Agreement Added";
        if (!(AppDataSingleton.getInstance().getAppointment()
                .getSelectedAgreementId() == 0)) {
            StringBuilder output = new StringBuilder();
            textAppointmentAddedAgreement.setVisibility(View.VISIBLE);

            int selectedAgr = AppDataSingleton.getInstance().getAppointment()
                    .getSelectedAgreementId();
            if (selectedAgr != -1) {
                String selectedName = AppDataSingleton.getInstance()
                        .getAppointment().getSelectedAgreementName();
                String selectedDescription = AppDataSingleton.getInstance()
                        .getAppointment().getSelectedAgreementDescription();

                if (selectedName != null) {
                    output.append(selectedName);
                }
                if (selectedDescription != null) {
                    output.append("\n");
                    output.append(selectedDescription);
                }
                addedAgreement = output.toString();
            }
        }
        textAppointmentAddedAgreement.setText(addedAgreement);

        if (!TextUtils.isEmpty(AppDataSingleton.getInstance().getAppointment()
                .getMultiDay())) {
            textAppointmentMultiType.setVisibility(View.VISIBLE);
            textAppointmentMultiType.setText(AppDataSingleton.getInstance()
                    .getAppointment().getMultiDay());
        }

        textAppointmentOrderNumber.setText("Work Order No: " + AppDataSingleton.getInstance()
                .getAppointment().getWorkOrderNumber());

        setDates();

        textAppointmentCustomerAddress.setText(eventLocationString);

        if (isShowingAdressName(eventLocationString)) {
            textAppointmentCustomerAddressName.setText(AppDataSingleton
                    .getInstance().getAppointment().getLocationName()+":");
        } else {
            textAppointmentCustomerAddressName.setVisibility(View.GONE);
        }

        // mTextLocation.setText(eventLocationString);
        textAppointmentNotes.setText(eventNotesString);

        // Get/set current status
        status = AppDataSingleton.getInstance().getAppointment().getStatus();
        textAppointmentStatus.setText(status.getValue());

        // Changed "Close Appointment" to "Closed Appointment" if it is already closed
        if (isReadOnly) {
            textAppointmentStatus.setText("Closed Appointment");
//            getActivity().finish();
        }

        switch (status) {
            case ON_ROUTE:
                linearLayoutUpdateStatus.setBackgroundColor(Color
                        .parseColor(Status.COLOR_ON_ROUTE)); // Blue

                buttonStatusUpdateOne.setVisibility(View.VISIBLE);
                buttonStatusUpdateTwo.setVisibility(View.VISIBLE);
                buttonStatusUpdateThree.setVisibility(View.VISIBLE);

                textStatusUpdateOne.setText(mActivity.getString(
                        R.string.job_status_string_begin_work));
                textStatusUpdateTwo.setText(mActivity.getString(
                        R.string.job_status_string_pause));
                textStatusUpdateThree.setText(mActivity.getString(
                        R.string.job_status_string_parts_run));
                break;
            case START_APPOINTMENT:
            case RESTART_APPOINTMENT:
                linearLayoutUpdateStatus.setBackgroundColor(Color
                        .parseColor(
                                Status.COLOR_START_APPOINTMENT)); // Green
                // linearLayoutUpdateStatus.setBackgroundColor(Color.rgb(62, 81,
                // 101)); // Blue

                buttonStatusUpdateOne.setVisibility(View.VISIBLE);
                buttonStatusUpdateTwo.setVisibility(View.VISIBLE);
                buttonStatusUpdateThree.setVisibility(View.VISIBLE);
                textStatusUpdateOne.setText(mActivity.getString(
                        R.string.job_status_string_work_finished));
                textStatusUpdateTwo.setText(mActivity.getString(
                        R.string.job_status_string_pause));
                textStatusUpdateThree.setText(mActivity.getString(
                        R.string.job_status_string_parts_run));
                break;
            case SUSPEND_APPOINTMENT:
                linearLayoutUpdateStatus.setBackgroundColor(Color
                        .parseColor(
                                Status.COLOR_SUSPEND_APPOINTMENT)); // Red

                buttonStatusUpdateOne.setVisibility(View.VISIBLE);
                buttonStatusUpdateTwo.setVisibility(View.VISIBLE);
                buttonStatusUpdateThree.setVisibility(View.GONE);
                textStatusUpdateOne.setText(mActivity.getString(
                        R.string.job_status_string_on_route));
                textStatusUpdateTwo.setText(mActivity.getString(
                        R.string.job_status_string_resume));
                break;

            case PARTS_RUN_APPOINTMENT:
                linearLayoutUpdateStatus.setBackgroundColor(Color
                        .parseColor(Status.COLOR_PARTS_RUN));

                buttonStatusUpdateOne.setVisibility(View.VISIBLE);
                buttonStatusUpdateTwo.setVisibility(View.VISIBLE);
                buttonStatusUpdateThree.setVisibility(View.GONE);
                textStatusUpdateOne.setText(mActivity.getString(
                        R.string.job_status_string_on_route));
                textStatusUpdateTwo.setText(mActivity.getString(
                        R.string.job_status_string_resume));
                break;
            case FINISH_APPOINTMENT:
                linearLayoutUpdateStatus.setBackgroundColor(Color
                        .parseColor(
                                Status.COLOR_FINISH_APPOINTMENT)); // Gray

                buttonStatusUpdateOne.setVisibility(View.VISIBLE);
                buttonStatusUpdateTwo.setVisibility(View.GONE);
                buttonStatusUpdateThree.setVisibility(View.GONE);
                textStatusUpdateOne.setText(mActivity.getString(
                        R.string.job_status_string_close_appointment));
                break;

            case CLOSE_APPOINTMENT:
                linearLayoutUpdateStatus.setBackgroundColor(Color
                        .parseColor(
                                Status.COLOR_CLOSE_APPOINTMENT)); // Black

                buttonStatusUpdateOne.setVisibility(View.GONE);
                buttonStatusUpdateTwo.setVisibility(View.GONE);
                buttonStatusUpdateThree.setVisibility(View.GONE);
                break;

            case NOT_STARTED:
                linearLayoutUpdateStatus.setBackgroundColor(Color
                        .parseColor(Status.COLOR_NOT_STARTED)); // Gray

                buttonStatusUpdateOne.setVisibility(View.VISIBLE);
                buttonStatusUpdateTwo.setVisibility(View.VISIBLE);
                buttonStatusUpdateThree.setVisibility(View.GONE);
                textStatusUpdateOne.setText(mActivity.getString(
                        R.string.job_status_string_on_route));
                textStatusUpdateTwo.setText(mActivity.getString(
                        R.string.job_status_string_begin_work));
                break;
            default:
                // Nothing
                break;
        }

        buttonViewInvoice.setOnClickListener(mViewInvoiceButtonListener);

        // Scroll to top of view
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_UP);
            }
        });
    }

    private void setDates() {

        String startTime = DateUtils.convertFromPatternToPattern(

                AppDataSingleton.getInstance().getAppointment().getDate() + " " + AppDataSingleton.getInstance().getAppointment().getStartTime(),
                "MM/dd/yyyy hh:mm aaa",
                "h:mm aaa",
                UserUtilitiesSingleton.getInstance().user.getTimeZone(),
                TimeZone.getDefault()
        );

        String endTime = DateUtils.convertFromPatternToPattern(

                AppDataSingleton.getInstance().getAppointment().getDate() + " " + AppDataSingleton.getInstance().getAppointment().getEndTime(),
                "MM/dd/yyyy hh:mm aaa",
                "h:mm aaa",
                UserUtilitiesSingleton.getInstance().user.getTimeZone(),
                TimeZone.getDefault()
        );

        StringBuilder builderDateAndTime = new StringBuilder();
        builderDateAndTime.append(startTime);

        builderDateAndTime.append(" - ");
        builderDateAndTime.append(endTime);

        textAppointmentDateTime.setText(builderDateAndTime.toString());


        String day = DateUtils.convertFromPatternToPattern(

                AppDataSingleton.getInstance().getAppointment().getDate() + " " + AppDataSingleton.getInstance().getAppointment().getStartTime(),
                "MM/dd/yyyy hh:mm aaa",
                "EEEE, MMMM d, yyyy",
                UserUtilitiesSingleton.getInstance().user.getTimeZone(),
                TimeZone.getDefault()
        );

        textAppointmentDay.setText(day);

        if (AppDataSingleton.getInstance().getAppointment().getTimeZone() != null)
            if ((!AppDataSingleton.getInstance().getAppointment().getTimeZone().equals(TimeZone.getDefault()))) {

                TimeZone timeZone = AppDataSingleton.getInstance().getAppointment().getTimeZone();
                String startTime1 = DateUtils.convertFromPatternToPattern(

                        AppDataSingleton.getInstance().getAppointment().getDate() + " " + AppDataSingleton.getInstance().getAppointment().getStartTime(),
                        "MM/dd/yyyy hh:mm aaa",
                        "h:mm aaa",
                        UserUtilitiesSingleton.getInstance().user.getTimeZone(),
                        timeZone

                );

                String endTime1 = DateUtils.convertFromPatternToPattern(

                        AppDataSingleton.getInstance().getAppointment().getDate() + " " + AppDataSingleton.getInstance().getAppointment().getEndTime(),
                        "MM/dd/yyyy hh:mm aaa",
                        "h:mm aaa",
                        UserUtilitiesSingleton.getInstance().user.getTimeZone(),
                        timeZone

                );

                builderDateAndTime = new StringBuilder();
                builderDateAndTime.append(" (");
                builderDateAndTime.append(startTime1);
                builderDateAndTime.append(" - ");
                builderDateAndTime.append(endTime1);
                builderDateAndTime.append(" " + timeZone.getDisplayName(false, TimeZone.SHORT));
                builderDateAndTime.append(")");
                textAppointmentDateTime1.setText(builderDateAndTime.toString());
            }

    }

    @Override
    protected String[] getAdditionalArgs() {
        return new String[]{String.valueOf(appointmentId), null};
    }

	/*
     * ==========================================================================
	 * ==
	 * ========================================================================
	 * === Location methods
	 */

    @Override
    public void onLocationChanged(Location location) {
        Log.v(DEBUG_TAG, "Location Changed");

        latitude = location.getLatitude();
        longitude = location.getLongitude();
        accuracy = location.getAccuracy();

    }

    @Override
    public void onProviderDisabled(String provider) {
        /* this is called if/when the GPS is disabled in settings */
        Log.v(DEBUG_TAG, "GPS/Network Location Disabled");

        Toast.makeText(mActivity,
                "Enable GPS and Wireless Network Location to continue",
                Toast.LENGTH_LONG).show();

		/* bring up the GPS settings */

        if (!isAdded()) return;

        Intent intent = new Intent(
                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.v(DEBUG_TAG,
                mActivity.getString(
                        R.string.toast_string_gps_enabled)
        );
        Toast.makeText(
                mActivity,
                mActivity.getString(
                        R.string.toast_string_gps_enabled), Toast.LENGTH_LONG
        )
                .show();

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        /* This is called when the GPS status alters */
        switch (status) {
            case LocationProvider.OUT_OF_SERVICE:
                Log.v(DEBUG_TAG, "Status Changed: Out of Service");
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                Log.v(DEBUG_TAG, "Status Changed: Temporarily Unavailable");
                break;
            case LocationProvider.AVAILABLE:
                Log.v(DEBUG_TAG, "Status Changed: Available");
                break;
            default:
                // Nothing
                break;
        }
    }

	/*
     * ==========================================================================
	 * ==
	 * ========================================================================
	 * === Tasks
	 */

    private final class UpdateStatusTask extends BaseUiReportTask<String> {
        UpdateStatusTask() {
            super(
                    mActivity,
                    R.string.async_task_string_waiting_for_location_and_updating_status);
            locationTimer();
            requestNewLocation();
        }

        @Override
        protected void onSuccess() {
            // Hide this again
            linearLayoutUpdateStatusButtons.setVisibility(View.GONE);

            if (savedOffline) {
                savedOffline = false;
                Toast.makeText(mActivity,
                        "Appointment was Saved in Offline Mode",
                        Toast.LENGTH_SHORT).show();
                setupUI();
                return;
            }

            if (UserUtilitiesSingleton.getInstance().user
                    .isOfferEstimateUponFinish()
                    && statusToUse.toString().equals("FINISH_APPOINTMENT")) {
                setupUI();
                showDialogEstimateOrInvoice();
                return;
            }

            if ("FINISH_APPOINTMENT".equals(statusToUse.name()) || "CLOSE_APPOINTMENT".equals(statusToUse.name())) {
                if (AppDataSingleton.getInstance().getAppointment()
                        .getInvoiceId() != 0
                        || AppDataSingleton.getInstance().getAppointment()
                        .isUsingInvoices()) {
                    new ViewInvoiceTask().execute();
                } else {
                    new GetAppointmentTask().execute();
                }
                return;
            }
            new GetAppointmentTask().execute();
        }

        @Override
        protected boolean taskBody(final String... args) throws Exception {

            updateStatus(statusToUse);
            return true;
        }

    }

    private class SubmitAddNotesTask extends BaseUiReportTask<String> {
        SubmitAddNotesTask() {
            super(mActivity, R.string.async_task_string_adding_notes);
        }

        @Override
        protected void onSuccess() {
            textAppointmentNotes.setText(addedNotes);
            AppDataSingleton.getInstance().getAppointment()
                    .setNotes(addedNotes);
            addedNotes = "";
        }

        @Override
        protected boolean taskBody(final String... args) throws Exception {
//            String formattedDate = "";
//            DateFormat df = new SimpleDateFormat("EE, MM/dd/yyyy");
//
//            try {
//                Date today = df.parse(AppDataSingleton.getInstance()
//                        .getAppointment().getDate());
//
//                DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
//                formattedDate = formatter.format(today);
//            } catch (ParseException e) {
//                formattedDate = AppDataSingleton.getInstance().getAppointment()
//                        .getDate();
//                e.printStackTrace();
//            }

            String startTime = DateUtils.convertFromPatternToPattern(

                    AppDataSingleton.getInstance().getAppointment().getDate() + " " + AppDataSingleton.getInstance().getAppointment().getStartTime(),
                    "MM/dd/yyyy hh:mm aaa",
                    "MM/dd/yyyy h:mm aaa",
                    // UserUtilitiesSingleton.getInstance().user.getTimeZone(),
                    TimeZone.getDefault()
            );

            String endTime = DateUtils.convertFromPatternToPattern(

                    AppDataSingleton.getInstance().getAppointment().getDate() + " " + AppDataSingleton.getInstance().getAppointment().getEndTime(),
                    "MM/dd/yyyy hh:mm aaa",
                    "MM/dd/yyyy h:mm aaa",
                    //UserUtilitiesSingleton.getInstance().user.getTimeZone(),
                    TimeZone.getDefault()
            );

            RESTAppointment.update(
                    String.valueOf(AppDataSingleton.getInstance()
                            .getAppointment().getApptTypeId()),
                    startTime,
                    endTime,
                    addedNotes,
                    String.valueOf(AppDataSingleton.getInstance()
                            .getAppointment().getLocationId()), false
                    , UserUtilitiesSingleton.getInstance().user.getTimeZone(),null

            );
            return true;
        }
    }

    private final class ViewInvoiceTask extends BaseUiReportTask<String> {
        ViewInvoiceTask() {
            super(mActivity, R.string.async_task_string_loading_invoice);
        }

        @Override
        protected void onSuccess() {
            InvoiceFragment.isReadOnly = AppDataSingleton.getInstance()
                    .getAppointment().getStatus() == //
                    com.skeds.android.phone.business.Utilities.General.ClassObjects.Status.CLOSE_APPOINTMENT;

            if (mCallback != null) {
                mCallback.onActionSelected(VIEW_INVOICE_SELECTED);
            } else {
                AppDataSingleton.getInstance().setInvoiceViewMode(
                        Constants.INVOICE_VIEW_FROM_APPOINTMENT);
                Intent i = new Intent(mActivity,
                        ActivityInvoiceSingleFragment.class);
                i.putExtra(InvoiceFragment.EXTRA_ORDER_NUMBER, AppDataSingleton.getInstance().getAppointment().getWorkOrderNumber());
                i.putExtra(InvoiceFragment.EXTRA_LOADED_FROM_APPT, true);
                startActivityForResult(i, ACTIVITY_RESULT_INVOICE);
            }

        }

        @Override
        protected boolean taskBody(final String... args) throws Exception {

            int invoiceId = AppDataSingleton.getInstance().getAppointment()
                    .getInvoiceId();
            if (invoiceId == 0) {
                invoiceId = AppDataSingleton.getInstance().getInvoice().getId();
            }
            if (invoiceId != 0) {
                RESTInvoice.query(AppDataSingleton.getInstance()
                        .getAppointment().getInvoiceId());
            }
            return true;
        }
    }

    private final class GetAppointmentTask extends BaseUiReportTask<String> {
        GetAppointmentTask() {
            super(mActivity, R.string.async_task_string_loading_appointment);
        }


        @Override
        protected void onSuccess() {

            if (UserUtilitiesSingleton.getInstance().user.isLoggedIn()) {
                isReadOnly = AppDataSingleton.getInstance().getAppointment()
                        .getStatus() == //
                        com.skeds.android.phone.business.Utilities.General.ClassObjects.Status.CLOSE_APPOINTMENT;
                setupUI();
            }

        }

        @Override
        protected boolean taskBody(final String... args) throws Exception {
            if (UserUtilitiesSingleton.getInstance().user.isLoggedIn()) {
                if (appointmentId != 0) {
                    RESTAppointment.query(appointmentId);
                    return true;
                }
            }
            return false;
        }
    }

    private final class ViewGalleryTask extends BaseUiReportTask<String> {
        public ViewGalleryTask(Activity parent) {
            super(parent, R.string.async_task_string_loading_photos);
        }

        @Override
        protected void onSuccess() {
            if (mCallback != null) {
                mCallback.onActionSelected(PHOTO_GALERY_SELECTED);
            } else {
                PhotoListViewFragment.returnToView = PhotoListViewFragment.VIEW_FROM_APPOINTMENT;
                startActivity(new Intent(mActivity,
                        ActivityPhotoListViewFragment.class));
            }
        }

        @Override
        protected boolean taskBody(final String... args) throws Exception {
            RESTPhotoList.query(AppDataSingleton.getInstance().getAppointment()
                    .getId(), 0);
            return true;
        }
    }

    private final class SendLocationPairBarcodeTask extends
            BaseUiReportTask<String> {

        int locationId;
        String barcode;

        SendLocationPairBarcodeTask() {
            super(mActivity, R.string.async_task_string_pairing_location);
        }

        @Override
        protected boolean taskBody(final String... args) throws Exception {
            RESTLocation.attachBarcode(locationId, barcode);
            return true;
        }
    }

    private final class GetServiceAgreementsListTask extends
            BaseUiReportTask<String> {
        boolean showDialog;

        GetServiceAgreementsListTask(boolean showDialog) {
            super(mActivity,
                    R.string.async_task_string_loading_service_agreements);
            this.showDialog = showDialog;
        }

        @Override
        protected void onSuccess() {
            if (showDialog) {
                showAgreementDialog();
            }
            setupUI();
        }

        @Override
        protected boolean taskBody(final String... args) throws Exception {
            RESTAgreementList.query(AppDataSingleton.getInstance()
                    .getCustomer().getId());
            return true;
        }
    }

    private final class SubmitAddAgreementTask extends BaseUiReportTask<String> {
        SubmitAddAgreementTask() {
            super(mActivity, "Adding Service Agreement...");
        }

        @Override
        protected void onSuccess() {
            new GetAppointmentTask().execute();
        }

        @Override
        protected boolean taskBody(final String... args) throws Exception {
            RESTAgreement.attachToAppointment(AppDataSingleton.getInstance()
                    .getAppointment().getId(), selectedAgreementId);
            return true;
        }
    }
}
