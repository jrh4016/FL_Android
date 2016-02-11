package com.skeds.android.phone.business.activities;

import android.app.Activity;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SlidingDrawer;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.skeds.android.phone.business.AsyncTasks.BaseUiReportTask;
import com.skeds.android.phone.business.Custom.AccountMenu;
import com.skeds.android.phone.business.Custom.QuickAction;
import com.skeds.android.phone.business.Dialogs.DialogAgreementList;
import com.skeds.android.phone.business.Dialogs.DialogLineItemCustom;
import com.skeds.android.phone.business.Dialogs.DialogLineItemModify;
import com.skeds.android.phone.business.Dialogs.DialogLineItemModifyOrRemove;
import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.AppSettingsUtilities;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Appointment;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Customer;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Estimate;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.LineItem;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Location;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.PriceBook.TaxValue;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.TaxRate;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.TaxRateType;
import com.skeds.android.phone.business.Utilities.General.CommonUtilities;
import com.skeds.android.phone.business.Utilities.General.Constants;
import com.skeds.android.phone.business.Utilities.General.NumberFormatTool;
import com.skeds.android.phone.business.Utilities.General.TaxAmountCalculator;
import com.skeds.android.phone.business.Utilities.General.UserUtilitiesSingleton;
import com.skeds.android.phone.business.Utilities.IntentExtras;
import com.skeds.android.phone.business.Utilities.REST.Objects.RESTAppointment;
import com.skeds.android.phone.business.Utilities.REST.Objects.RESTEstimate;
import com.skeds.android.phone.business.Utilities.REST.Objects.RESTServicePlanList;
import com.skeds.android.phone.business.core.SkedsApplication;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class ActivityEstimateView extends BaseSkedsActivity {

    public static int estimateId;

    private NumberFormatTool currencyFormatTool;

    private NumberFormatTool percentFormatTool;

    public static int CREDIT_CARD_TYPE = 0;
    public static String customerPrimaryEmail = "";

    private final static String mPercentageString = "Using percentage for discount";

    private final static String mNonPercentageString = "Using no percentage for discount";

    private static Activity mActivity;
    private static Context mContext;

    private SlidingDrawer slidingDrawer;

    private TextView textCustomerName;
    private TextView textPhoneNumber;
    private TextView textEmailAddress;
    private TextView textTotal;
    private TextView textDiscount;
    private TextView textNetTotal;

    private TextView buttonAddLineItem;
    private TextView buttonAddDiscount;

    private LinearLayout taxTitleContainer;

    private LinearLayout taxValueContainer;

    private TextView locationLabel;
    private TextView locationEditButton;

    private TextView agreementLabel;
    private TextView agreementEditButton;

    private static EditText edittextNotes;

    // For Header
    private static LinearLayout headerLayout;
    private ImageView headerButtonUser;
    private ImageView headerButtonBack;
    private TextView headerButtonSave;
    private TextView headerButtonCustomerView;

    // For Discount Dialog
    private Dialog dialogDiscount;
    private EditText dialogDiscountEditTextValue;
    private TextView dialogDiscountButtonSave, dialogDiscountButtonCancel;
    private ImageView dialogDiscountNavButtonDollars,
            dialogDiscountNavButtonPercentage;
    private Spinner locationSpinner;

    // For Standard/Custom Add Line Item Selection Dialog
    private Dialog mAddLineItemTypeDialog;

    // private double priceToRemove;
    private int itemNoToRemove;
    boolean isShowing = false;

    private static String handoffCustomerEmail = "";
    public static String handoffEstimateSignature;
    // static Double handoffEstimateDiscount = 0.0;

    /*
     * This says that we should send a "close" appointment, after this gets
     * submitted
     */
    public static boolean closeAppointmentOnComplete;

    private int priceBookSearchMode = -1;

    private Customer customer;
    private Location location = null;
    private LoadServicePlanTask loadServicePlanTask = null;

    private TextView buttonSaveEstimate;
    private TextView buttonConvertToInvoice;
    private TextView buttonEmailEstimate;
    private TextView buttonAppointmentEstimate;
    private TextView buttonInvoiceEstimate;

    private TextView buttonDelete;

    private int selectedLocationId = 0;

    private boolean signed;

    private Dialog dialogWarning;

    public static boolean hasAnyChanges = false;

    private StringBuilder taxTitleBuilder;
    private StringBuilder taxValueBuilder;


    private TextWatcher notesWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!AppDataSingleton.getInstance().getEstimate()
                    .getDescription().equals(edittextNotes.getText()))
                hasAnyChanges = true;

            AppDataSingleton.getInstance().getEstimate()
                    .setDescription(s.toString());
        }
    };
    private static BigDecimal discount = BigDecimal.ZERO;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_estimate_view);
        Intent intent = getIntent();
        customer = AppDataSingleton.getInstance().getCustomer();

        currencyFormatTool = NumberFormatTool.getCurrencyFormat();
        percentFormatTool = NumberFormatTool.getPercentFormat();

        location = customer.getLocationById(intent.getIntExtra(
                IntentExtras.LOCATION_ID, -1));

        headerLayout = (LinearLayout) findViewById(R.id.activity_header);

        mActivity = ActivityEstimateView.this;
        mContext = this;

        ActivityLineItemAdd.estimateAgreementId = 0;

        final QuickAction accountMenu;
        accountMenu = AccountMenu.setupMenu(mContext, mActivity);

        headerButtonUser = (ImageView) headerLayout
                .findViewById(R.id.header_button_user);
        headerButtonBack = (ImageView) headerLayout
                .findViewById(R.id.header_button_back);

        headerButtonUser.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                accountMenu.show(v);
                accountMenu.setAnimStyle(QuickAction.ANIM_GROW_FROM_CENTER);
            }
        });

        headerButtonBack.setOnClickListener(mGoBackListener);

        buttonDelete = (TextView) findViewById(R.id.activity_estimate_button_delete);

        // setup location spinner
        locationSpinner = (Spinner) findViewById(R.id.spinner_location);
        String locArray[] = new String[]{};

        locationLabel = (TextView) findViewById(R.id.estimate_location_label);
        locationEditButton = (TextView) findViewById(R.id.estimate_location_button_edit);

        agreementLabel = (TextView) findViewById(R.id.estimate_agreement_label);
        agreementEditButton = (TextView) findViewById(R.id.estimate_agreement_button_edit);

        if (AppDataSingleton.getInstance().getEstimateViewMode() == Constants.ESTIMATE_VIEW_FROM_APPOINTMENT) {

            // locationLabel.setVisibility(View.GONE);
            // locationTitle.setVisibility(View.GONE);
            locationEditButton.setVisibility(View.GONE);

            locationLabel.setText(AppDataSingleton.getInstance()
                    .getAppointment().getLocationName());

            locationSpinner.setEnabled(false);
            locationSpinner.setVisibility(View.GONE);
            locArray = new String[]{getApptAdress()};

            agreementEditButton.setVisibility(View.GONE);
        } else {

            locationSpinner.setVisibility(View.GONE);
            // locationTitle.setVisibility(View.GONE);
            // locationLabel.setVisibility(View.GONE);
            locationEditButton.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent i = new Intent(mContext,
                            ActivityCustomerLocationListView.class);
                    i.setAction(ActivityCustomerLocationListView.ACTION_PICK_LOCATION);
                    startActivityForResult(i, 0);
                }
            });

            agreementEditButton.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    final DialogAgreementList agreementsDialog = new DialogAgreementList(
                            mContext);
                    agreementsDialog.show();
                    agreementsDialog
                            .setOnDismissListener(new OnDismissListener() {

                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    agreementLabel.setText(AppDataSingleton
                                            .getInstance().getEstimate()
                                            .getServicePlanName());
                                    if (!agreementsDialog.refreshData())
                                        return;

                                    switch (AppDataSingleton.getInstance()
                                            .getEstimateViewType()) {
                                        case Constants.ESTIMATE_VIEW_TYPE_ADD:
                                            new SubmitNewEstimateTaskForAgreement(
                                                    mActivity).execute();
                                            break;
                                        case Constants.ESTIMATE_VIEW_TYPE_VIEW_EDIT:
                                            new SubmitUpdatedEstimateTaskForAgreement(
                                                    mActivity).execute();
                                            break;
                                        default:
                                            // Nothing
                                            break;
                                    }
                                }
                            });
                }
            });

            if (location != null) {
            	if(!location.getAddress2().isEmpty()) {
                	locationLabel.setText(location.getAddress1()+", "+
                	location.getAddress2()+", "+
                	location.getCity()+", "+
                	location.getState()+" "+
                	location.getZip());
            	}
                else{
                	locationLabel.setText(location.getAddress1()+", "+
                	location.getCity()+", "+
                	location.getState()+" "+
                	location.getZip());
                }
            } else {
                locationLabel.setText("<none>");
            }
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, locArray);
        arrayAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationSpinner.setAdapter(arrayAdapter);
        locationSpinner.setSelection(0);
        updateSelectedLocation();

        // Init priceBook search mode
        if (AppDataSingleton.getInstance().getAppointment()
                .getSearchModeOverride() != -1) {
            priceBookSearchMode = AppDataSingleton.getInstance()
                    .getAppointment().getSearchModeOverride();
        } else {
            priceBookSearchMode = AppDataSingleton.getInstance()
                    .getPriceBookSearchMode();
        }

        if (AppDataSingleton.getInstance().getEstimateListViewMode() == Constants.ESTIMATE_LIST_VIEW_FROM_APPOINTMENT) {

            if (AppDataSingleton.getInstance().getEstimate()
                    .isServicePlanUsedForPricing()) {
                String agrName = AppDataSingleton.getInstance()
                        .getAppointment().getSelectedAgreementName();


                if (!TextUtils.isEmpty(agrName))
                    agreementLabel.setText(agrName);

                int id = AppDataSingleton.getInstance().getAppointment()
                        .getSelectedAgreementId();
                ActivityLineItemAdd.estimateAgreementId = id;
                AppDataSingleton.getInstance().getEstimate()
                        .setServicePlanId(id);
                AppDataSingleton
                        .getInstance()
                        .getEstimate()
                        .setServicePlanName(
                                AppDataSingleton.getInstance().getAppointment()
                                        .getSelectedAgreementName());
            }
        } else {

            String agrName = AppDataSingleton.getInstance().getEstimate()
                    .getServicePlanName();
            if (AppDataSingleton.getInstance().getEstimate()
                    .isServicePlanUsedForPricing()) {
                if (!TextUtils.isEmpty(agrName))
                    agreementLabel.setText(agrName);
            }
            loadServicePlanTask = new LoadServicePlanTask(this);
            if (!CommonUtilities.isNetworkAvailable(mContext)) {
                Toast.makeText(mContext, "Network connection unavailable.",
                        Toast.LENGTH_SHORT).show();
            } else {
                loadServicePlanTask.execute();
            }

        }

        if (AppDataSingleton.getInstance().getEstimateListViewMode() == Constants.ESTIMATE_LIST_VIEW_FROM_APPOINTMENT
                && estimateId != 0) {

            buttonDelete.setVisibility(View.VISIBLE);
            buttonDelete.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    new deleteEstimateTask().execute();
                }
            });

            AppDataSingleton
                    .getInstance()
                    .getEstimate()
                    .setAppointmentId(
                            AppDataSingleton.getInstance().getAppointment()
                                    .getId());
        }

        headerButtonSave = (TextView) headerLayout
                .findViewById(R.id.header_standard_button_right);
        headerButtonCustomerView = (TextView) headerLayout
                .findViewById(R.id.header_standard_button_left);
        if (AppSettingsUtilities.getApplicationMode() == Constants.APPLICATION_MODE_PHONE_SERVICE) {
            headerButtonCustomerView.setVisibility(View.INVISIBLE);
        }

        slidingDrawer = (SlidingDrawer) findViewById(R.id.activity_estimate_slidingdrawer);
        buttonSaveEstimate = (TextView) slidingDrawer
                .findViewById(R.id.activity_estimate_button_save);
        buttonConvertToInvoice = (TextView) slidingDrawer
                .findViewById(R.id.activity_estimate_button_to_invoice);
        buttonEmailEstimate = (TextView) slidingDrawer
                .findViewById(R.id.activity_estimate_button_email);
        buttonAppointmentEstimate = (TextView) slidingDrawer
                .findViewById(R.id.activity_estimate_button_appointment);
        buttonInvoiceEstimate = (TextView) slidingDrawer
                .findViewById(R.id.activity_estimate_button_invoice);
        buttonInvoiceEstimate.setVisibility(View.GONE);
        buttonSaveEstimate.setOnClickListener(mSliderButtonsListener);
        buttonConvertToInvoice.setOnClickListener(mSliderButtonsListener);
        buttonEmailEstimate.setOnClickListener(mSliderButtonsListener);
        buttonAppointmentEstimate.setOnClickListener(mSliderButtonsListener);
        buttonInvoiceEstimate.setOnClickListener(mSliderButtonsListener);

        textCustomerName = (TextView) findViewById(R.id.activity_estimate_textview_customer_name);
        textPhoneNumber = (TextView) findViewById(R.id.activity_estimate_textview_customer_phone_number);
        textEmailAddress = (TextView) findViewById(R.id.activity_estimate_textview_customer_email_address);
        textTotal = (TextView) findViewById(R.id.activity_estimate_textview_total_amount);
        textNetTotal = (TextView) findViewById(R.id.activity_estimate_textview_net_total);
        taxTitleContainer = (LinearLayout) findViewById(R.id.tax_title_container);
        taxValueContainer = (LinearLayout) findViewById(R.id.tax_value_container);
        textDiscount = (TextView) findViewById(R.id.activity_estimate_textview_discount_amount);

        // fill form

        buttonAddLineItem = (TextView) findViewById(R.id.activity_estimate_button_add_line_item);
        buttonAddDiscount = (TextView) findViewById(R.id.activity_estimate_button_add_discount);

        edittextNotes = (EditText) findViewById(R.id.activity_estimate_edittext_notes);
        edittextNotes.setOnTouchListener(touchListener);
        edittextNotes.addTextChangedListener(notesWatcher);

        customerPrimaryEmail = "";


        switch (AppDataSingleton.getInstance().getEstimateViewType()) {
            case Constants.ESTIMATE_VIEW_TYPE_ADD:
                // Brand new one
                setupUI();
                Estimate estimate = new Estimate();
                AppDataSingleton.getInstance().setEstimate(estimate);
                break;
            case Constants.ESTIMATE_VIEW_TYPE_VIEW_EDIT:
                if (AppDataSingleton.getInstance().getEstimate().getId() == 0) {
                    if (!CommonUtilities.isNetworkAvailable(mContext)) {
                        Toast.makeText(mContext, "Network connection unavailable.",
                                Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        new GetSingleEstimateTask().execute();
                    }
                } else
                    setupUI();
                break;
            default:
                // Nothing
                break;
        }

        hasAnyChanges = false;

        final Estimate estimate = AppDataSingleton.getInstance().getEstimate();
        if (estimate.getAppointmentId() > 0) {
            final Appointment appointment = AppDataSingleton.getInstance().getAppointment();
            if (appointment != null) {
                estimate.setLocationTaxable(appointment.isLocationTaxable());
            }
        } else if (location != null) {
            estimate.setLocationTaxable(location.isTaxable());
        } else if (customer != null) {
            estimate.setLocationTaxable(customer.isLocationTaxable());
        }

        if (customer != null) {
            estimate.setTaxable(customer.isTaxable());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupUI();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            if (loadServicePlanTask != null) {
                loadServicePlanTask.cancel(true);
                loadServicePlanTask = null;
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (closeSlidingDialog())
            return;

        if (hasAnyChanges) {
            showWarningDialog();
            return;
        }

        super.onBackPressed();
    }

    private boolean closeSlidingDialog() {
        if (slidingDrawer.isOpened()) {
            slidingDrawer.animateClose();
            return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 0:
                if (resultCode == Activity.RESULT_OK) {
                    hasAnyChanges = true;
                    int selectedLocation = data.getIntExtra("location_number", 0);
                    final Location location = AppDataSingleton.getInstance()
                            .getCustomer().locationList.get(selectedLocation);
                    selectedLocationId = location.getId();
                    AppDataSingleton.getInstance().getEstimate().setLocationId(location.getId());

                    if(!location.getAddress2().isEmpty()) {
                        locationLabel.setText(location.getAddress1()+", "+
                                location.getAddress2()+", "+
                                location.getCity()+", "+
                                location.getState()+" "+
                                location.getZip());
                    }
                    else{
                        locationLabel.setText(location.getAddress1()+", "+
                                location.getCity()+", "+
                                location.getState()+" "+
                                location.getZip());
                    }

                    AppDataSingleton.getInstance().getEstimate().setLocationTaxable(location.isTaxable());
                }
                break;
            case 1:
                if (resultCode == RESULT_OK) {
                    signed = true;
                } else
                    signed = false;
                break;

            default:
                break;
        }

    }

    private String getApptAdress() {
        String location = AppDataSingleton.getInstance().getAppointment()
                .getLocationValue();
        String eventLocationString;
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

            finalLocation = contentA.toString().trim() + "\n"
                    + contentB.toString().trim();
            eventLocationString = finalLocation;

        } else
            eventLocationString = "";
        return eventLocationString;
    }

    private OnClickListener mSliderButtonsListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.activity_estimate_button_save:
                    if (AppDataSingleton.getInstance().getEstimate().getLineItems().isEmpty()) {
                        Toast.makeText(mContext, "Unable to save estimate without line items.", Toast.LENGTH_SHORT).show();
                        closeSlidingDialog();
                        return;
                    }

                    handoffCustomerEmail = "";
                    AppDataSingleton.getInstance()
                            .getEstimate().setDescription(edittextNotes.getText().toString());
                    handoffEstimateSignature = "";
                    //AppDataSingleton.getInstance().getEstimate().setLocationId(getSelectedLocationId());

                    AppDataSingleton.getInstance().getEstimate()
                            .setServicePlanName("");

                    if (!signed
                            || UserUtilitiesSingleton.getInstance().user
                            .isForceSignatureOnEstimate() ||
                            UserUtilitiesSingleton.getInstance().user
                                    .isDisplayDisclaimer()) {
                        AppDataSingleton.getInstance().setSignatureViewMode(
                                Constants.SIGNATURE_VIEW_FROM_ESTIMATE);
                        Intent i = new Intent(mContext,
                                ActivityPaymentSignature.class);
                        startActivity(i);
                    } else {
                        if (!CommonUtilities.isNetworkAvailable(mContext)) {
                            Toast.makeText(mContext,
                                    "Network connection unavailable.",
                                    Toast.LENGTH_SHORT).show();
                            closeSlidingDialog();
                            return;
                        }
                        switch (AppDataSingleton.getInstance()
                                .getEstimateViewType()) {
                            case Constants.ESTIMATE_VIEW_TYPE_ADD:
                                new SubmitNewEstimateTask(mActivity, true).execute();
                                closeSlidingDialog();
                                break;
                            case Constants.ESTIMATE_VIEW_TYPE_VIEW_EDIT:
                                new SubmitUpdatedEstimateTask(mActivity, true)
                                        .execute();
                                closeSlidingDialog();
                                break;
                            default:
                                // Nothing
                                break;
                        }
                    }
                    closeSlidingDialog();
                    break;

                case R.id.activity_estimate_button_email:
                    if (!CommonUtilities.isNetworkAvailable(mContext)) {
                        Toast.makeText(mContext, "Network connection unavailable.",
                                Toast.LENGTH_SHORT).show();
                        closeSlidingDialog();
                        break;
                    }
                    new EmailEstimateTask().execute();
                    closeSlidingDialog();
                    break;

                case R.id.activity_estimate_button_appointment:

                    switch (AppDataSingleton.getInstance().getEstimateViewType()) {
                        case Constants.ESTIMATE_VIEW_TYPE_ADD:
                            new SubmitNewEstimateTaskForCustomer(mActivity) {
                                @Override
                                protected void onSuccess() {
                                    startUnscheduledAppointmentActivity();
                                }
                            }.execute();

                            break;
                        case Constants.ESTIMATE_VIEW_TYPE_VIEW_EDIT:
                            new SubmitUpdatedEstimateTaskForCustomer(mActivity) {
                                @Override
                                protected void onSuccess() {
                                    startUnscheduledAppointmentActivity();
                                }
                            }.execute();
                            break;
                        default:
                            startUnscheduledAppointmentActivity();
                            break;
                    }
                    break;
                case R.id.activity_estimate_button_to_invoice:
                    if (!CommonUtilities.isNetworkAvailable(mContext)) {
                        Toast.makeText(mContext, "Network connection unavailable.",
                                Toast.LENGTH_SHORT).show();
                        closeSlidingDialog();
                        break;
                    }
                    new ConvertEstimateToInvoiceTask().execute();
                    closeSlidingDialog();
                    break;
                default:
                    break;
            }

        }

        private void startUnscheduledAppointmentActivity() {
            Intent i = new Intent(mActivity, ActivityUnscheduledAppointmentAddEdit.class);
            finish();
            startActivity(i);
        }
    };

    private OnClickListener mDiscountDialogListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            // subtotal = calculateLineItemTotal();
            switch (v.getId()) {
                case R.id.dialog_add_discount_button_save:
                    hasAnyChanges = true;


                    BigDecimal amount = new BigDecimal(dialogDiscountEditTextValue.getText()
                            .toString()).setScale(2, BigDecimal.ROUND_HALF_UP);


                    if (AppDataSingleton.getInstance().getEstimate().getDescription().contains(mPercentageString)) {

                        String description = mPercentageString + " " + amount + "%";
                        BigDecimal subtotal = calculateLineItemTotal(true);
                        AppDataSingleton.getInstance().getEstimate().setDescription(description);
                        AppDataSingleton.getInstance().getEstimate().setDiscount(subtotal.multiply(amount).divide(new BigDecimal("100.0"), 2, BigDecimal.ROUND_HALF_UP));

                        if (!edittextNotes.getText().toString()
                                .contains(mPercentageString))

                            edittextNotes.setText(description);

                    } else {
                        edittextNotes.setText(mNonPercentageString);
                        AppDataSingleton.getInstance().getEstimate().setDiscount(amount);
                    }


                    calculateTotal();

                    if (dialogDiscount.isShowing())
                        dialogDiscount.dismiss();
                    break;

                case R.id.dialog_add_discount_button_cancel:
                    if (dialogDiscount.isShowing())
                        dialogDiscount.dismiss();
                    break;

                case R.id.dialog_add_discount_imageview_dollars:

                    dialogDiscountNavButtonDollars
                            .setImageResource(R.drawable.nav_invoice_discount_currency_pressed);
                    dialogDiscountNavButtonPercentage
                            .setImageResource(R.drawable.custom_nav_button_invoice_discount_percentage);

                    AppDataSingleton.getInstance().getEstimate().setDescription(mNonPercentageString);
                    break;

                case R.id.dialog_add_discount_imageview_percentage:

                    dialogDiscountNavButtonDollars
                            .setImageResource(R.drawable.custom_nav_button_invoice_discount_currency);

                    dialogDiscountNavButtonPercentage
                            .setImageResource(R.drawable.nav_invoice_discount_percentage_pressed);

                    AppDataSingleton.getInstance().getEstimate().setDescription(mPercentageString);
                    break;
                default:
                    // Nothing
                    break;
            }
        }
    };

    private OnClickListener mAddDiscountListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            showDiscountDialog();
        }
    };

    private OnClickListener mAddLineItemListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            showAddLineItemDialog();
        }
    };

    private OnClickListener mAddLineItemDialogListener = new OnClickListener() {
        @Override
        public void onClick(View v) {

            if (mAddLineItemTypeDialog.isShowing())
                mAddLineItemTypeDialog.dismiss();

            switch (v.getId()) {
                case R.id.dialog_yes_no_response_button_yes: // Standard
                    Intent i = null;

                    switch (priceBookSearchMode) {
                        case Constants.PRICE_BOOK_SEARCH_MODE_DEFAULT:
                            i = new Intent(mActivity, ActivityLineItemAdd.class);
                            i.putExtra(Constants.EXTRA_PRICEBOOK_SEARCH_MODE,
                                    priceBookSearchMode);
                            if (AppDataSingleton.getInstance()
                                    .getEstimate().isServicePlanUsedForPricing())
                                i.putExtra("ServicePlanId", AppDataSingleton.getInstance()
                                        .getEstimate().getServicePlanId());
                            i.putExtra(Constants.EXTRA_LINE_ITEM_ADD_VIEW_MODE,
                                    Constants.LINE_ITEM_ADD_VIEW_FROM_ESTIMATE);
                            break;
                        case Constants.PRICE_BOOK_SEARCH_MODE_ONE_TIER:
                        case Constants.PRICE_BOOK_SEARCH_MODE_TWO_TIER:

                            i = new Intent(mActivity, ActivityPricebookListView.class);
                            if (AppDataSingleton.getInstance()
                                    .getEstimate().isServicePlanUsedForPricing())
                                i.putExtra("ServicePlanId", AppDataSingleton.getInstance()
                                        .getEstimate().getServicePlanId());
                            i.putExtra(Constants.EXTRA_PRICEBOOK_LIST_VIEW_MODE,
                                    Constants.PRICEBOOK_LIST_VIEW_FROM_ESTIMATE);
                            i.putExtra(Constants.EXTRA_PRICEBOOK_SEARCH_MODE,
                                    priceBookSearchMode);
                            break;
                        default:
                            // Nothing
                            break;
                    }

                    mActivity.startActivity(i);
                    break;
                case R.id.dialog_yes_no_response_button_no:
                    DialogLineItemCustom customDialog = new DialogLineItemCustom(
                            mContext, Constants.LINE_ITEM_ADD_VIEW_FROM_ESTIMATE);

                    customDialog
                            .setOnDismissListener(new DialogInterface.OnDismissListener() {

                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    setupUI();
                                }
                            });

                    customDialog.show();
                    break;
                default:
                    // Nothing
                    break;
            }
        }
    };

    private OnClickListener mLineItemRowListener = new OnClickListener() {

        @Override
        public void onClick(View v) {

            itemNoToRemove = Integer.parseInt(v.getTag().toString());

            final DialogLineItemModifyOrRemove itemDialog = new DialogLineItemModifyOrRemove(
                    mContext, AppDataSingleton.getInstance().getEstimate().getLineItems()
                    .get(itemNoToRemove).getName());
            itemDialog.show();
            itemDialog.setOnDismissListener(new OnDismissListener() {

                @Override
                public void onDismiss(DialogInterface dialog) {
                    switch (itemDialog.dialogResult) {
                        case 0: // Modify
                            showModifyDialog();
                            break;
                        case 1: // Remove
                            AppDataSingleton.getInstance().getEstimate().getLineItems().remove(itemNoToRemove);
                            setupUI();
                            break;
                        default:
                            // Nothing
                            break;
                    }

                }
            });
        }
    };

    private OnClickListener mSaveInvoiceListener = new OnClickListener() {
        @Override
        public void onClick(View v) {

            if (!CommonUtilities.isNetworkAvailable(mContext)) {
                Toast.makeText(mContext, "Network connection unavailable.",
                        Toast.LENGTH_SHORT).show();
                closeSlidingDialog();
                return;
            }

            InputMethodManager imm = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(edittextNotes.getWindowToken(), 0);

            if (!closeSlidingDialog())
                slidingDrawer.animateOpen();

        }
    };

    private OnClickListener mCustomerViewListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (AppDataSingleton.getInstance().getEstimateViewType()) {
                case Constants.ESTIMATE_VIEW_TYPE_ADD:
                    new SubmitNewEstimateTaskForCustomer(mActivity).execute();
                    break;
                case Constants.ESTIMATE_VIEW_TYPE_VIEW_EDIT:
                    new SubmitUpdatedEstimateTaskForCustomer(mActivity).execute();
                    break;
                default:
                    // Nothing
                    break;
            }
        }
    };

    private OnClickListener mGoBackListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!closeSlidingDialog())
                onBackPressed();
        }
    };

    private View.OnTouchListener touchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            closeSlidingDialog();
            return false;
        }

    };

    private void setupUI() {
        setupHeaderButton();

        init();


        // Adds the Line Items that were already included (not editable)
        /* Find Tablelayout */
        TableLayout tl = (TableLayout) findViewById(R.id.activity_estimate_tablelayout_line_items);
        TableRow trHeader = addHeader(tl);


        addLineItems(tl, trHeader);

        buttonAddLineItem.setOnClickListener(mAddLineItemListener);
        buttonAddDiscount.setOnClickListener(mAddDiscountListener);
        headerButtonSave.setOnClickListener(mSaveInvoiceListener);
        headerButtonCustomerView.setOnClickListener(mCustomerViewListener);

        // updateTable();
        calculateTotal();
    }

    private void addLineItems(TableLayout tl, TableRow trHeader) {
        if (AppDataSingleton.getInstance().getEstimate().getLineItems() != null) {
            if (!AppDataSingleton.getInstance().getEstimate().getLineItems()
                    .isEmpty()) {

				/* Add row to TableLayout. */
                tl.removeAllViews();
                tl.addView(trHeader, new TableLayout.LayoutParams(
                        LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

                View hDividerHeader = new View(mContext);
                hDividerHeader = new View(mContext);
                hDividerHeader.setLayoutParams(new LayoutParams(
                        LayoutParams.MATCH_PARENT, 1)); // Width, Height
                hDividerHeader.setBackgroundColor(Color.rgb(198, 198, 198));
                tl.addView(hDividerHeader);


                for (LineItem curlineItem : AppDataSingleton.getInstance()
                        .getEstimate().getLineItems()) {

                    inflateLine(tl, curlineItem);
                }
            }
        }
    }

    private void inflateLine(TableLayout tl, LineItem curlineItem) {
    /* Create a new row to be added. */
        TableRow tr = new TableRow(mContext);
        tr.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT));

        TextView quantityLabel = new TextView(mContext);
        // quantityLabel.setText(String
        // .valueOf(AppDataSingleton.getInstance().getEstimate().lineItem[i]
        // .getQuantity()));
        quantityLabel.setText(String.valueOf(curlineItem
                .getQuantity()));
        quantityLabel.setContentDescription("line_" + AppDataSingleton.getInstance()
                .getEstimate().getLineItems().indexOf(curlineItem) + "_quantity");
        quantityLabel.setTextColor(Color.rgb(0, 0, 0));
        quantityLabel.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT, 0f)); // Width,
        // Height,
        // Weight
        quantityLabel.setPadding(0, 15, 6, 15);
        quantityLabel.setGravity(Gravity.RIGHT);
        quantityLabel.setMaxLines(1);
        quantityLabel.setTextSize(13);
        quantityLabel.setTypeface(null, Typeface.BOLD);
        quantityLabel.setMarqueeRepeatLimit(0);
        quantityLabel.setEllipsize(TruncateAt.MARQUEE);

        View[] divider = new View[3];
        for (int x = 0; x < 3; x++) {
            divider[x] = new View(mContext);
            divider[x].setLayoutParams(new LayoutParams(1,
                    LayoutParams.MATCH_PARENT)); // Width, Height
            divider[x].setBackgroundColor(Color.rgb(198, 198, 198));
        }

        LinearLayout descriptionLayout = new LinearLayout(mActivity);
        descriptionLayout.setLayoutParams(new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT, 35f));
        descriptionLayout.setOrientation(LinearLayout.VERTICAL);
        descriptionLayout.setPadding(0, 10, 0, 10);

        TextView detailsLabel = new TextView(mContext);
        detailsLabel.setText(curlineItem.getName());
        detailsLabel.setContentDescription("line_" + AppDataSingleton.getInstance()
                .getEstimate().getLineItems().indexOf(curlineItem) + "_details");
        detailsLabel.setTextSize(13);
        detailsLabel.setTextColor(Color.rgb(0, 0, 0));
        detailsLabel.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT, 35f));
//					detailsLabel.setPadding(4, 0, 0, 0);
        detailsLabel.setTypeface(null, Typeface.BOLD);
//					detailsLabel.setLines(2);
        detailsLabel.setMarqueeRepeatLimit(0);
        detailsLabel.setEllipsize(TruncateAt.END);


        // TAXES
        List<TextView> taxViews = new ArrayList<TextView>();
        if (UserUtilitiesSingleton.getInstance().user.getCountryInfo().isUseExtendedTax()) {
            int iterator = 1;
            for (TaxRateType trt : UserUtilitiesSingleton.getInstance().user.getCountryInfo().getTaxRateTypes()) {
                TextView taxView = new TextView(mContext);

                TaxRate selectedRate = getTaxRate(curlineItem, trt);
                if (selectedRate.getId() > 0)
                    taxView.setText(selectedRate.getName() + " " + percentFormatTool.format(selectedRate.getValue().doubleValue()));
                else taxView.setText(" - ");
                taxView.setContentDescription("line_" + AppDataSingleton.getInstance()
                        .getEstimate().getLineItems().indexOf(curlineItem) + "_vat" + (iterator++));
                taxView.setTextSize(13);
//							taxView.setEms(4);
                taxView.setTextColor(Color.rgb(0, 0, 0));
                taxView.setLayoutParams(new LayoutParams(
                        LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 0f)); // Width,
                // Height,
                // Weight
//							taxView.setPadding(18, 0, 0, 0);
                taxView.setTypeface(null, Typeface.BOLD);
                taxView.setMaxLines(2);

                taxViews.add(taxView);
            }
        }


        TextView realDetailsLabel = new TextView(mContext);
        realDetailsLabel.setText(curlineItem.getDescription());
        realDetailsLabel.setTextSize(13);
        realDetailsLabel.setTextColor(Color.rgb(0, 0, 0));
        realDetailsLabel.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT, 35f));
//					realDetailsLabel.setPadding(4, 0, 0, 0);
        realDetailsLabel.setTypeface(null, Typeface.ITALIC);
//					realDetailsLabel.setFilters(new InputFilter[] {new InputFilter.LengthFilter(20)});
        realDetailsLabel.setMarqueeRepeatLimit(0);
        realDetailsLabel.setEllipsize(TruncateAt.END);

        switch (((SkedsApplication) getApplication())
                .getLineItemsMode()) {
            case Constants.SHOW_ITEM_NAME:
                descriptionLayout.addView(detailsLabel);
                break;
            case Constants.SHOW_ITEM_DESCRIPTION:
                if (!TextUtils.isEmpty(realDetailsLabel.getText()))
                    descriptionLayout.addView(realDetailsLabel);
                else
                    descriptionLayout.addView(detailsLabel);
                break;
            case Constants.SHOW_ITEM_NAME_AND_DESCRIPTION:
                descriptionLayout.addView(detailsLabel);
                if (!TextUtils.isEmpty(realDetailsLabel.getText()))
                    descriptionLayout.addView(realDetailsLabel);
                break;
            default:
                break;
        }

        TextView priceLabel = new TextView(mContext);
        priceLabel
                .setText(currencyFormatTool.format(
                        curlineItem.cost));

        priceLabel.setContentDescription("line_" + AppDataSingleton.getInstance()
                .getEstimate().getLineItems().indexOf(curlineItem) + "_price");
        priceLabel.setTextSize(13);
        priceLabel.setTextColor(Color.rgb(0, 0, 0));
        priceLabel.setLayoutParams(new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT, 0f));
        priceLabel.setPadding(0, 0, 6, 0);
        priceLabel.setGravity(Gravity.RIGHT);
        priceLabel.setTypeface(null, Typeface.BOLD);
        priceLabel.setMaxLines(1);
        priceLabel.setMarqueeRepeatLimit(0);
        priceLabel.setEllipsize(TruncateAt.MARQUEE);

        TextView totalLabel = new TextView(mContext);
        totalLabel
                .setText(currencyFormatTool.format(
                        curlineItem.finalCost));

        totalLabel.setContentDescription("line_" + AppDataSingleton.getInstance()
                .getEstimate().getLineItems().indexOf(curlineItem) + "_total");
        // +
        // format.format(AppDataSingleton.getInstance().getEstimate().lineItem
        // .get(i).getFinalCost()));
        totalLabel.setTextSize(13);
        totalLabel.setTextColor(Color.rgb(0, 0, 0));
        // totalLabel.setTextColor(android.R.color.black);
        totalLabel.setLayoutParams(new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT, 0f));
        totalLabel.setPadding(0, 0, 4, 0);
        totalLabel.setTypeface(null, Typeface.BOLD);
        totalLabel.setGravity(Gravity.RIGHT);
        totalLabel.setMaxLines(1);
        totalLabel.setMarqueeRepeatLimit(0);
        totalLabel.setEllipsize(TruncateAt.MARQUEE);

					/* Add Button to row. */
        // tr.addView(b);

        LineItem.Recommendation recommendation = curlineItem.getRecommendation();

        Drawable drawable = setIcon(curlineItem);

        quantityLabel.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
        tr.addView(quantityLabel);
        tr.addView(divider[0]);
        tr.addView(descriptionLayout);
        tr.addView(divider[1]);
        for (TextView taxView : taxViews) {
            tr.addView(taxView);
            View divider1 = new View(mContext);
            divider1.setLayoutParams(new LayoutParams(1,
                    LayoutParams.MATCH_PARENT)); // Width, Height
            divider1.setBackgroundColor(Color.rgb(198, 198, 198));
            tr.addView(divider1);
        }
        tr.addView(priceLabel);
        tr.addView(divider[2]);
        tr.addView(totalLabel);

//					tr.setBackground(getResources().getDrawable(R.drawable.custom_line_item));

					/*
                     * If the item can be removed, setup the onClickListener for
					 * it
					 */
        // if
        // (AppDataSingleton.getInstance().getEstimate().lineItem[i].getRemovable())
        tr.setOnClickListener(mLineItemRowListener);

					/* Add Tag so we know which to remove */
        tr.setTag(AppDataSingleton.getInstance()
                .getEstimate().getLineItems().indexOf(curlineItem));

					/* Add row to TableLayout. */
        tl.addView(tr, new TableLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT));
    }

    private Drawable setIcon(LineItem lineItem) {
        Integer iconRes = null;

        if (lineItem != null)
            switch (lineItem.getRecommendation()) {
                case ACCEPTED:
                    iconRes = R.drawable.tick;
                    break;
                case DECLINED:
                    iconRes = R.drawable.tick_close;
                    break;
                case BLANK:
                    iconRes = R.drawable.tick_close;
                    break;
            }
        Drawable drawable = null;

        if (iconRes != null) {
            drawable = getResources().getDrawable(iconRes);

            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();

            drawable = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 20, 20, true));

        }
        return drawable;
    }

    private TableRow addHeader(TableLayout tl) {
        tl.removeAllViews();

        // set header if no line items
        TextView noItems = new TextView(mContext);
        noItems.setText("No Items");
        noItems.setTextSize(17);
        noItems.setTextColor(Color.rgb(0, 0, 0));
        noItems.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 0f));
//		noItems.setPadding(18, 0, 0, 0);
        noItems.setTypeface(null, Typeface.BOLD);
        noItems.setMaxLines(1);
        tl.addView(noItems);

		/* Adds header (QTY/DESC, ETC) */
        /* Create a new row to be added. */
        TableRow trHeader = new TableRow(mContext);
        trHeader.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT));

        TextView quantityLabelHeader = new TextView(mContext);
        quantityLabelHeader.setText("Qty");
        quantityLabelHeader.setTextSize(13);
        quantityLabelHeader.setTextColor(Color.rgb(0, 0, 0));
        quantityLabelHeader.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 0f)); // Width,
        // Height,
        // Weight
//		quantityLabelHeader.setPadding(18, 0, 0, 0);
        quantityLabelHeader.setTypeface(null, Typeface.BOLD);
        quantityLabelHeader.setMaxLines(1);

        View[] dividerHeader = new View[3];
        for (int x = 0; x < 3; x++) {
            dividerHeader[x] = new View(mContext);
            dividerHeader[x].setLayoutParams(new LayoutParams(1,
                    LayoutParams.MATCH_PARENT)); // Width, Height
            dividerHeader[x].setBackgroundColor(Color.rgb(198, 198, 198));
        }

        TextView detailsLabelHeader = new TextView(mContext);
        detailsLabelHeader.setText("Description");
        detailsLabelHeader.setTextSize(13);
        detailsLabelHeader.setTextColor(Color.rgb(0, 0, 0));

        detailsLabelHeader.setLayoutParams(new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 35f)); // Width,
        // Height,
        // Weight
//		detailsLabelHeader.setPadding(4, 0, 0, 0);
        detailsLabelHeader.setTypeface(null, Typeface.BOLD);
        detailsLabelHeader.setMaxLines(1);

        List<TextView> taxHeaders = new ArrayList<TextView>();
        if (UserUtilitiesSingleton.getInstance().user.getCountryInfo().isUseExtendedTax()) {
            for (TaxRateType trt : UserUtilitiesSingleton.getInstance().user.getCountryInfo().getTaxRateTypes()) {
                TextView taxHeader = new TextView(mContext);
                taxHeader.setText(trt.getName());
                taxHeader.setTextSize(13);
                taxHeader.setTextColor(Color.rgb(0, 0, 0));
                taxHeader.setLayoutParams(new LayoutParams(
                        LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 0f)); // Width,
                // Height,
                // Weight
//				taxHeader.setPadding(18, 0, 0, 0);
                taxHeader.setTypeface(null, Typeface.BOLD);
                taxHeader.setMaxLines(1);

                taxHeaders.add(taxHeader);
            }
        }

        TextView priceLabelHeader = new TextView(mContext);
        priceLabelHeader.setText("Unit Price");
        priceLabelHeader.setTextSize(13);
        priceLabelHeader.setTextColor(Color.rgb(0, 0, 0));
        priceLabelHeader.setLayoutParams(new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0f)); // Width,
        // Height,
        // Weight
//		priceLabelHeader.setPadding(4, 0, 0, 0);
        priceLabelHeader.setTypeface(null, Typeface.BOLD);
        priceLabelHeader.setMaxLines(1);

        TextView totalLabelHeader = new TextView(mContext);
        totalLabelHeader.setText("Price");
        totalLabelHeader.setTextSize(13);
        totalLabelHeader.setTextColor(Color.rgb(0, 0, 0));
        // totalLabel.setTextColor(android.R.color.black);
        totalLabelHeader.setLayoutParams(new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0f)); // Width,
        // Height,
        // Weight
//		totalLabelHeader.setPadding(4, 0, 0, 0);
        totalLabelHeader.setTypeface(null, Typeface.BOLD);
        totalLabelHeader.setMaxLines(1);

		/* Add Button to row. */
        trHeader.addView(quantityLabelHeader);
        trHeader.addView(dividerHeader[0]);
        trHeader.addView(detailsLabelHeader);
        trHeader.addView(dividerHeader[1]);
        for (TextView taxHeader : taxHeaders) {
            trHeader.addView(taxHeader);
            View divider = new View(mContext);
            divider.setLayoutParams(new LayoutParams(1,
                    LayoutParams.MATCH_PARENT)); // Width, Height
            divider.setBackgroundColor(Color.rgb(198, 198, 198));
            trHeader.addView(divider);
        }
        trHeader.addView(priceLabelHeader);
        trHeader.addView(dividerHeader[2]);
        trHeader.addView(totalLabelHeader);
        return trHeader;
    }

    private void init() {
        if (AppDataSingleton.getInstance().getEstimateViewMode() == Constants.ESTIMATE_VIEW_FROM_ESTIMATE_LIST)
            buttonConvertToInvoice.setVisibility(View.GONE);

        // This will stop the keyboard from automatically popping up (I hope)
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        if ("org".equals(customer.getType()))
            textCustomerName.setText(customer.getOrganizationName());
        else
            textCustomerName.setText(customer.getFirstName() + " "
                    + customer.getLastName());

        String phone = "", email = "";
        if (!customer.phone.isEmpty())
            phone = customer.phone.get(0);
        if (!customer.email.isEmpty()) {
            email = customer.email.get(0);
        }

        if (TextUtils.isEmpty(email)
                || AppDataSingleton.getInstance().getEstimate().getId() == 0) {
            buttonEmailEstimate.setVisibility(View.GONE);
        } else {
            buttonEmailEstimate.setVisibility(View.VISIBLE);
        }

        textPhoneNumber.setText(phone);
        textEmailAddress.setText(email);

        edittextNotes.setText(AppDataSingleton.getInstance().getEstimate()
                .getDescription());
//
//        D.setText(currencyFormatTool.format(
//                AppDataSingleton.getInstance().getEstimate().getDiscount()));

        textTotal.setText(currencyFormatTool.format(AppDataSingleton.getInstance().getEstimate().getTotal()));

        textNetTotal.setText(currencyFormatTool.format(
                AppDataSingleton.getInstance().getEstimate().getNetTotal()));
    }

    private void setupHeaderButton() {
        switch (AppDataSingleton.getInstance().getEstimateViewType()) {
            case Constants.ESTIMATE_VIEW_TYPE_ADD:
                // Nothing
                buttonConvertToInvoice.setVisibility(View.GONE);
                buttonAppointmentEstimate.setVisibility(View.GONE);
                break;
            case Constants.ESTIMATE_VIEW_TYPE_VIEW_EDIT:
                if (AppDataSingleton.getInstance().getEstimate().getId() != 0) {
                    buttonAppointmentEstimate.setVisibility(View.VISIBLE);
                    buttonConvertToInvoice.setVisibility(View.VISIBLE);
                } else {
                    buttonAppointmentEstimate.setVisibility(View.GONE);
                    buttonConvertToInvoice.setVisibility(View.GONE);
                }
                break;
            default:
                // Nothing
                break;
        }
    }

    private TaxRate getTaxRate(LineItem lineItem, TaxRateType trt) {
        TaxRate selectedRate = new TaxRate();
        for (Long rateId : lineItem.rateIds) {

            for (TaxRate rate : UserUtilitiesSingleton.getInstance().user.getCountryInfo().getTaxRates()) {
                if (rate.getId() == rateId && rate.getType().equals(trt.getType())) {
                    selectedRate = rate;
                    break;
                }

            }
        }
        return selectedRate;
    }

    private void showDiscountDialog() {
        // Create the dialog
        dialogDiscount = new Dialog(mContext);
        dialogDiscount.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogDiscount.setContentView(R.layout.dialog_layout_add_discount);
        dialogDiscount.setTitle("Set Discount");


        dialogDiscountButtonSave = (TextView) dialogDiscount
                .findViewById(R.id.dialog_add_discount_button_save);
        dialogDiscountButtonCancel = (TextView) dialogDiscount
                .findViewById(R.id.dialog_add_discount_button_cancel);

        dialogDiscountEditTextValue = (EditText) dialogDiscount
                .findViewById(R.id.dialog_add_discount_edittext_value);

        dialogDiscountNavButtonDollars = (ImageView) dialogDiscount
                .findViewById(R.id.dialog_add_discount_imageview_dollars);
        dialogDiscountNavButtonPercentage = (ImageView) dialogDiscount
                .findViewById(R.id.dialog_add_discount_imageview_percentage);

        dialogDiscountNavButtonDollars
                .setOnClickListener(mDiscountDialogListener);
        dialogDiscountNavButtonPercentage
                .setOnClickListener(mDiscountDialogListener);

        dialogDiscountButtonSave.setOnClickListener(mDiscountDialogListener);
        dialogDiscountButtonCancel.setOnClickListener(mDiscountDialogListener);

        if (AppDataSingleton.getInstance().getEstimate().getDescription().contains(mPercentageString))
            dialogDiscountNavButtonPercentage.callOnClick();
        else dialogDiscountNavButtonDollars.callOnClick();

        dialogDiscount.show();
    }

    private void showModifyDialog() {
        final DialogLineItemModify modifyDialog = new DialogLineItemModify(
                mContext, Constants.LINE_ITEM_ADD_VIEW_FROM_ESTIMATE,
                itemNoToRemove);
        modifyDialog.show();
        modifyDialog.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                setupUI();
            }
        });
    }

    private void showAddLineItemDialog() {

        mAddLineItemTypeDialog = new Dialog(mContext);
        mAddLineItemTypeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mAddLineItemTypeDialog
                .setContentView(R.layout.dialog_layout_yes_no_response);

        TextView textTitle = (TextView) mAddLineItemTypeDialog
                .findViewById(R.id.dialog_yes_no_response_textview_title);
        TextView textBody = (TextView) mAddLineItemTypeDialog
                .findViewById(R.id.dialog_yes_no_response_textview_body);
        TextView buttonStandard = (TextView) mAddLineItemTypeDialog
                .findViewById(R.id.dialog_yes_no_response_button_yes);
        TextView buttonCustom = (TextView) mAddLineItemTypeDialog
                .findViewById(R.id.dialog_yes_no_response_button_no);

        textTitle.setText("Add Line Item");
        textBody.setText("Select the type of item to add");
        buttonStandard.setText("Standard");
        buttonCustom.setText("Custom");

        buttonStandard.setOnClickListener(mAddLineItemDialogListener);
        buttonCustom.setOnClickListener(mAddLineItemDialogListener);

        mAddLineItemTypeDialog.show();
    }

    private BigDecimal calculateLineItemTotal(Boolean includeDeclined) {
        // double result = 0.0;
        BigDecimal result = new BigDecimal("0.00").setScale(2,
                BigDecimal.ROUND_HALF_UP);

        if (AppDataSingleton.getInstance().getEstimate().getLineItems() != null) {
            for (int i = 0; i < AppDataSingleton.getInstance().getEstimate().getLineItems()
                    .size(); i++) {

                if (!includeDeclined)
                    if (AppDataSingleton.getInstance().getEstimate().getLineItems()
                            .get(i).getRecommendation() == LineItem.Recommendation.DECLINED)
                        continue;

                if (AppDataSingleton.getInstance().getEstimate().getLineItems()
                        .get(i).isUserAdded()) {
                    if (AppDataSingleton.getInstance().getEstimate().getLineItems()
                            .get(i).isUsingAdditionalCost()) {

                        result = result.add(AppDataSingleton.getInstance()
                                .getEstimate().getLineItems().get(i).additionalCost
                                .multiply(AppDataSingleton
                                        .getInstance().getEstimate().getLineItems()
                                        .get(i).getQuantity()));
                    } else {
                        result = result.add(AppDataSingleton.getInstance()
                                .getEstimate().getLineItems().get(i).cost
                                .multiply(AppDataSingleton
                                        .getInstance().getEstimate().getLineItems()
                                        .get(i).getQuantity()));
                    }
                } else {

                    result = result.add(AppDataSingleton.getInstance()
                            .getEstimate().getLineItems().get(i).cost
                            .multiply(AppDataSingleton
                                    .getInstance().getEstimate().getLineItems()
                                    .get(i).getQuantity()));
                }
            }
        }

        return result;
    }


    private void calculateTotal() {

        BigDecimal subtotalIncludeDiclined = new BigDecimal("0.00").setScale(2, RoundingMode.HALF_UP);
        BigDecimal runningTax = new BigDecimal("0.00").setScale(2, RoundingMode.HALF_UP);

        // Calculate sum of overall items not declined
        subtotalIncludeDiclined = calculateLineItemTotal(false);

        discount = BigDecimal.ZERO;
        if (AppDataSingleton.getInstance().getEstimate().getDescription().contains(mPercentageString)) {

            try {

                String description = AppDataSingleton.getInstance().getEstimate().getDescription();

                String parse = description.substring(mPercentageString.length(), description.indexOf("%"));

                BigDecimal discountPercent = new BigDecimal(parse.trim());
                discount =
                        subtotalIncludeDiclined.multiply(discountPercent)
                                .divide(new BigDecimal("100.0").setScale(2, RoundingMode.HALF_UP), 2, RoundingMode.HALF_UP);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            discount = AppDataSingleton.getInstance().getEstimate().getDiscount();
        }


        AppDataSingleton.getInstance().getEstimate().setDiscount(discount);

        if (subtotalIncludeDiclined.compareTo(new BigDecimal("0.0")) == 1) {

            runningTax = new TaxAmountCalculator()
                    .calculateTaxAmount(AppDataSingleton.getInstance().getEstimate())
                    .setScale(2, RoundingMode.HALF_UP);
        }

        if (subtotalIncludeDiclined.compareTo(discount) == -1)
            discount = subtotalIncludeDiclined;

        AppDataSingleton.getInstance().getEstimate().setNetTotal(subtotalIncludeDiclined);
        textNetTotal.setText(currencyFormatTool.format(subtotalIncludeDiclined));

        textDiscount.setText(currencyFormatTool.format(
                discount));

//		textTaxValue.setText(currencyFormatTool.format(runningTax)); // Set the amount for
//											// overall taxes

        taxTitleContainer.removeAllViews();
        taxValueContainer.removeAllViews();

        taxTitleBuilder = new StringBuilder();
        taxValueBuilder = new StringBuilder();


        if (UserUtilitiesSingleton.getInstance().user.getCountryInfo().isUseExtendedTax()) {

            //AppDataSingleton.getInstance().getEstimate().getTaxes().clear();
            List<TaxRateType> taxRateTypesForBusiness = UserUtilitiesSingleton.getInstance().user.getCountryInfo().getTaxRateTypes();

            Map<TaxRate, BigDecimal> extendedInfoTypes = AppDataSingleton.getInstance().getEstimate().getExtendedInfoTypes();
            for (TaxRate taxRate : extendedInfoTypes.keySet()) {

                TextView title = new TextView(mActivity);
                title.setContentDescription(taxRate.getName() + "_label");

                title.setTextColor(getResources().getColor(android.R.color.black));
                title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                title.setText(taxRate.getName());
                taxTitleContainer.addView(title);

                TextView value = new TextView(mActivity);
                value.setContentDescription(taxRate.getName() + "_value");

                value.setTextColor(getResources().getColor(android.R.color.black));
                value.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                value.setTypeface(Typeface.DEFAULT_BOLD);
                value.setText(currencyFormatTool.format(extendedInfoTypes.get(taxRate).multiply(taxRate.getValue())));
                taxValueContainer.addView(value);
            }

        } else {
            TextView title = new TextView(mActivity);
            title.setContentDescription("tax_label");

            title.setTextColor(getResources().getColor(android.R.color.black));
            title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            title.setText("Tax");
            taxTitleContainer.addView(title);

            TextView value = new TextView(mActivity);
            value.setContentDescription("tax_value");

            value.setTextColor(getResources().getColor(android.R.color.black));
            value.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            value.setTypeface(Typeface.DEFAULT_BOLD);
            value.setText(currencyFormatTool.format(runningTax.setScale(2, RoundingMode.HALF_UP)));
            taxValueContainer.addView(value);
        }

        BigDecimal finalTotal = (UserUtilitiesSingleton.getInstance().user.getCountryInfo().isUseExtendedTax() ?
                sum(subtotalIncludeDiclined, AppDataSingleton.getInstance().getEstimate().getTaxes()) :
                subtotalIncludeDiclined.add(runningTax))
                .subtract(discount);

        AppDataSingleton.getInstance().getEstimate().setTotal(finalTotal);

        textTotal.setText(currencyFormatTool.format(finalTotal.setScale(2, RoundingMode.HALF_UP)));
    }


    private BigDecimal sum(BigDecimal subtotal, List<TaxValue> taxes) {
        BigDecimal sum = subtotal;
        for (TaxValue taxValue : taxes) {
            sum = sum.add(taxValue.getValue());
        }
        return sum;
    }

    private void inflateTotalTaxItem(TaxRate taxRateType, BigDecimal calculatedValue) {
        TextView title = new TextView(mActivity);
        title.setContentDescription(taxRateType.getType() + "_label");

        title.setTextColor(getResources().getColor(android.R.color.black));
        title.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 12);

        //if (taxRateType.getId() > 0)
        title.setText(taxRateType.getName() + " " + percentFormatTool.format(taxRateType.getValue().doubleValue()));
        //else title.setText(" - ");
        taxTitleContainer.addView(title);

        taxTitleBuilder.append(taxRateType.getName());
        taxTitleBuilder.append('\n');

        TextView valueName = new TextView(mActivity);
        valueName.setContentDescription(taxRateType.getName() + "_value");

        valueName.setTextColor(getResources().getColor(android.R.color.black));
        valueName.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 12);
        valueName.setTypeface(Typeface.DEFAULT_BOLD);
        taxTitleContainer.addView(valueName);


        taxValueBuilder = new StringBuilder();
        taxValueBuilder.append(
                currencyFormatTool.format(calculatedValue));
        taxValueBuilder.append('\n');

        TextView value = new TextView(mActivity);
        //value.setContentDescription(taxRateType.getType() + "_label");

        value.setTextColor(getResources().getColor(android.R.color.black));
        value.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 12);
        value.setTypeface(Typeface.DEFAULT_BOLD);
        value.setText(taxValueBuilder);
        //taxTitleContainer.addView(value);
        taxValueContainer.addView(value);
    }

//    /**
//     * @return location Id that currently selected
//     */
//    private int getSelectedLocationId() {
//        if (location != null)
//            return location.getId();
//
//        if (locationSpinner.isShown()) {
//            int p = locationSpinner.getSelectedItemPosition();
//            if (p < 0)
//                return -1;
//            if (!customer.locationList.isEmpty())
//                return customer.locationList.get(p).getId();
//            else
//                return -1;
//        } else
//            return selectedLocationId;
//
//    }

    private void updateSelectedLocation() {
        if (location == null) {
            int locId = AppDataSingleton.getInstance().getEstimate().getLocationId();
            if (locId > 0) {
                int i = 0;
                for (Location loc : customer.locationList) {
                    if (loc.getId() == locId) {
                        if (locationSpinner.getAdapter().getCount() > i)
                            locationSpinner.setSelection(i);

                        if(!loc.getAddress2().isEmpty()) {
                            locationLabel.setText(loc.getAddress1()+", "+
                                    loc.getAddress2()+", "+
                                    loc.getCity()+", "+
                                    loc.getState()+" "+
                                    loc.getZip());
                        }
                        else{
                            locationLabel.setText(loc.getAddress1()+", "+
                                    loc.getCity()+", "+
                                    loc.getState()+" "+
                                    loc.getZip());
                        }

                        return;
                    }
                    ++i;
                }
            }
        }
    }

    private void launchCustomerScreen() {
        Intent i = new Intent(ActivityEstimateView.this,
                ActivityEstimateCustomerView.class);


        if (AppDataSingleton.getInstance()
                .getEstimate().isServicePlanUsedForPricing())
            i.putExtra("service_agreement", AppDataSingleton.getInstance()
                    .getEstimate().getServicePlanName());

        i.putExtra("tax", taxValueBuilder.toString());
        i.putExtra("tax_label", taxTitleBuilder.toString());
        i.putExtra("discount", currencyFormatTool.parse(textDiscount.getText().toString(), true).toString());
        i.putExtra("net_total", currencyFormatTool.parse(textNetTotal.getText().toString(), true).toString());
        i.putExtra("total", currencyFormatTool.parse(textTotal.getText().toString(), true).toString());

        startActivityForResult(i, 1);
    }

    private void showWarningDialog() {
        dialogWarning = new Dialog(this);
        dialogWarning.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogWarning.setContentView(R.layout.dialog_layout_yes_no_response);
        ((TextView) dialogWarning.findViewById(R.id.dialog_yes_no_response_textview_title)).setText("Warning");

        TextView dialogWarningButtonYes = (TextView) dialogWarning
                .findViewById(R.id.dialog_yes_no_response_button_yes);
        TextView dialogWarningButtonNo = (TextView) dialogWarning
                .findViewById(R.id.dialog_yes_no_response_button_no);

        ((TextView) dialogWarning.findViewById(R.id.dialog_yes_no_response_textview_body)).setText("You Have Unsaved Data. Exit?");


        dialogWarningButtonYes.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialogWarning.dismiss();
                finish();
            }
        });
        dialogWarningButtonNo.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialogWarning.dismiss();
            }
        });

        dialogWarning.show();
    }

    private class ConvertEstimateToInvoiceTask extends BaseUiReportTask<String> {

        public ConvertEstimateToInvoiceTask() {
            super(ActivityEstimateView.this, "Converting to Invoice...");
        }

        @Override
        protected boolean taskBody(String... params) throws Exception {
            setResult(Activity.RESULT_OK);

            int estimateId = AppDataSingleton.getInstance().getEstimate()
                    .getId();

            // RESTEstimate.update(estimateId, handoffEstimateDescription,
            // AppDataSingleton.getInstance()
            // .getEstimate().discount.toString(),
            // AppDataSingleton.getInstance().getEstimate().getSignature(),
            // handoffCustomerEmail, -1, AppDataSingleton.getInstance()
            // .getEstimate().getServicePlanId());

            RESTEstimate.update(estimateId, AppDataSingleton.getInstance().getEstimate().getDescription(),
                    discount
                            .toString(), AppDataSingleton.getInstance()
                            .getEstimate().getSignature(),
                    handoffCustomerEmail, -1, AppDataSingleton.getInstance()
                            .getEstimate().getServicePlanId());

            RESTEstimate.convertToInvoice(estimateId);
            return true;
        }

        @Override
        protected void onSuccess() {
            super.onSuccess();
            Toast.makeText(mContext, "Successfully converted",
                    Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    private class GetSingleEstimateTask extends BaseUiReportTask<String> {

        GetSingleEstimateTask() {
            super(ActivityEstimateView.this,
                    R.string.async_task_string_loading_estimate);
        }

        @Override
        protected void onSuccess() {
            if (locationLabel != null)
                locationLabel.setText(AppDataSingleton.getInstance().getCustomer().getAddress1());
            String agrName = AppDataSingleton.getInstance().getEstimate()
                    .getServicePlanName();
            if (AppDataSingleton.getInstance().getEstimate()
                    .isServicePlanUsedForPricing())
                if (!TextUtils.isEmpty(agrName))
                    agreementLabel.setText(agrName);
            setupUI();

            updateSelectedLocation();
        }

        @Override
        protected boolean taskBody(final String... args) throws Exception {


            //BetaApiManager betaApiManager = new BetaApiManager();
            //betaApiManager.getService().getEstimate(estimateId);
            RESTEstimate.query(estimateId);

            return true;
        }
    }

    // TODO - Lock out button press while making an attempt
    public static class SubmitNewEstimateTask extends BaseUiReportTask<String> {
        private Customer customer = AppDataSingleton.getInstance()
                .getCustomer();

        private boolean closeUpdated;

        SubmitNewEstimateTask(Activity activity, boolean closeUpdated) {
            super(activity, R.string.async_task_string_submitting_new_estimate);
            this.closeUpdated = closeUpdated;
        }

        @Override
        protected void onSuccess() {
            switch (AppDataSingleton.getInstance().getEstimateViewType()) {
                case Constants.ESTIMATE_VIEW_TYPE_ADD:

                    setAutocloseOnSuccess(closeUpdated);
                    if (AppDataSingleton.getInstance().getEstimateViewMode() == Constants.ESTIMATE_VIEW_FROM_APPOINTMENT) {
                        if (closeAppointmentOnComplete) {
                            closeAppointmentOnComplete = false; // Reset
                            // this
                            new SubmitCloseAppointmentTask(mActivity).execute();

                        }
                    }
                    break;
                default:
                    // Nothing
                    break;
            }
            if (closeUpdated) {
                mActivity.finish();
                return;
            }
        }

        @Override
        protected boolean taskBody(final String... args) throws Exception {
            int customerId = customer.getId();
            int appointmentId = 0;

            switch (AppDataSingleton.getInstance().getEstimateViewMode()) {
                case Constants.ESTIMATE_VIEW_FROM_APPOINTMENT:
                    // This means we came from the invoice/estimate dialog
                    appointmentId = AppDataSingleton.getInstance().getAppointment()
                            .getId();
                    break;
                case Constants.ESTIMATE_VIEW_FROM_ESTIMATE_LIST:
                    // This means we were selected from the listview
                    switch (AppDataSingleton.getInstance()
                            .getEstimateListViewMode()) {
                        case Constants.ESTIMATE_LIST_VIEW_FROM_APPOINTMENT:
                        case Constants.ESTIMATE_LIST_VIEW_FROM_PAST_APPOINTMENT:
                            appointmentId = AppDataSingleton.getInstance()
                                    .getAppointment().getId();
                            break;
                        case Constants.ESTIMATE_LIST_VIEW_FROM_CUSTOMER:
                            // We saw the estimate list from the customer
                            // screen,
                            // did we add or edit?
                            if (AppDataSingleton.getInstance().getEstimateViewType() == Constants.ESTIMATE_VIEW_TYPE_ADD)
                                appointmentId = 0;
                            else if (AppDataSingleton.getInstance()
                                    .getEstimateViewType() == Constants.ESTIMATE_VIEW_TYPE_VIEW_EDIT)
                                appointmentId = AppDataSingleton.getInstance()
                                        .getEstimate().getAppointmentId();
                            break;
                        default:
                            // Nothing
                            break;
                    }
                    break;
                default:
                    // Nothing
                    break;
            }

            RESTEstimate.add(customerId, AppDataSingleton.getInstance()
                            .getEstimate().getDescription(),
                    discount
                            .toString(), AppDataSingleton.getInstance()
                            .getEstimate().getSignature(),
                    handoffCustomerEmail, appointmentId, AppDataSingleton
                            .getInstance().getEstimate().getServicePlanId());
            return true;
        }
    }

    public class SubmitNewEstimateTaskForAgreement extends
            SubmitNewEstimateTask {

        SubmitNewEstimateTaskForAgreement(Activity activity) {
            super(activity, false);
        }

        @Override
        protected void onSuccess() {
            AppDataSingleton.getInstance()
                    .setEstimateViewType(Constants.ESTIMATE_VIEW_TYPE_VIEW_EDIT);
            estimateId = AppDataSingleton.getInstance().getEstimate().getId();
            new GetSingleEstimateTask().execute();
        }

    }

    public class SubmitUpdatedEstimateTaskForAgreement extends
            SubmitUpdatedEstimateTask {

        SubmitUpdatedEstimateTaskForAgreement(Activity activity) {
            super(activity, false);
        }

        @Override
        protected void onSuccess() {
            estimateId = AppDataSingleton.getInstance().getEstimate().getId();
            new GetSingleEstimateTask().execute();
        }

    }

    private class SubmitNewEstimateTaskForCustomer extends
            BaseUiReportTask<String> {
        private Customer customer = AppDataSingleton.getInstance()
                .getCustomer();

        SubmitNewEstimateTaskForCustomer(Activity activity) {
            super(activity, R.string.async_task_string_submitting_new_estimate);
        }

        @Override
        protected void onSuccess() {
            AppDataSingleton.getInstance().setEstimateViewType(
                    Constants.ESTIMATE_VIEW_TYPE_VIEW_EDIT);
            launchCustomerScreen();
        }

        @Override
        protected boolean taskBody(final String... args) throws Exception {
            int customerId = customer.getId();
            int appointmentId = 0;

            switch (AppDataSingleton.getInstance().getEstimateViewMode()) {
                case Constants.ESTIMATE_VIEW_FROM_APPOINTMENT:
                    // This means we came from the invoice/estimate dialog
                    appointmentId = AppDataSingleton.getInstance().getAppointment()
                            .getId();
                    break;
                case Constants.ESTIMATE_VIEW_FROM_ESTIMATE_LIST:
                    // This means we were selected from the listview
                    switch (AppDataSingleton.getInstance()
                            .getEstimateListViewMode()) {
                        case Constants.ESTIMATE_LIST_VIEW_FROM_APPOINTMENT:
                        case Constants.ESTIMATE_LIST_VIEW_FROM_PAST_APPOINTMENT:
                            appointmentId = AppDataSingleton.getInstance()
                                    .getAppointment().getId();
                            break;
                        case Constants.ESTIMATE_LIST_VIEW_FROM_CUSTOMER:
                            // We saw the estimate list from the customer
                            // screen,
                            // did we add or edit?
                            if (AppDataSingleton.getInstance().getEstimateViewType() == Constants.ESTIMATE_VIEW_TYPE_ADD)
                                appointmentId = 0;
                            else if (AppDataSingleton.getInstance()
                                    .getEstimateViewType() == Constants.ESTIMATE_VIEW_TYPE_VIEW_EDIT)
                                appointmentId = AppDataSingleton.getInstance()
                                        .getEstimate().getAppointmentId();
                            break;
                        default:
                            // Nothing
                            break;
                    }
                    break;
                default:
                    // Nothing
                    break;
            }

            RESTEstimate.add(customerId, AppDataSingleton.getInstance()
                            .getEstimate().getDescription(),
                    discount
                            .toString(), AppDataSingleton.getInstance()
                            .getEstimate().getSignature(),
                    handoffCustomerEmail, appointmentId, AppDataSingleton
                            .getInstance().getEstimate().getServicePlanId());
            RESTEstimate.query(AppDataSingleton.getInstance().getEstimate()
                    .getId());
            return true;
        }
    }

    public static class SubmitUpdatedEstimateTask extends
            BaseUiReportTask<String> {

        private boolean closeUpdated;

        SubmitUpdatedEstimateTask(Activity activity, boolean closeUpdated) {
            super(activity, R.string.async_task_string_updating_estimate);
            this.closeUpdated = closeUpdated;
        }

        @Override
        protected void onSuccess() {
            if (closeAppointmentOnComplete) {
                closeAppointmentOnComplete = false; // Reset this

                if (!CommonUtilities.isNetworkAvailable(mContext)) {
                    Toast.makeText(mContext, "Network connection unavailable.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                new SubmitCloseAppointmentTask(mActivity).execute();

            } else {
                // Intent i = new Intent(context,
                // ActivityCustomerView.class);
                // mActivity.startActivity(i);
                setAutocloseOnSuccess(closeUpdated);
                if (closeUpdated) {
                    mActivity.finish();
                    return;
                } else
                    reloadActivity();
            }

        }

        @Override
        protected boolean taskBody(final String... args) throws Exception {

            int estimateId = AppDataSingleton.getInstance().getEstimate()
                    .getId();

            RESTEstimate.update(estimateId, AppDataSingleton.getInstance()
                            .getEstimate().getDescription(),
                    discount
                            .toString(), AppDataSingleton.getInstance()
                            .getEstimate().getSignature(),
                    handoffCustomerEmail, -1, AppDataSingleton.getInstance()
                            .getEstimate().getServicePlanId());
            return true;
        }
    }

    private class SubmitUpdatedEstimateTaskForCustomer extends
            BaseUiReportTask<String> {

        SubmitUpdatedEstimateTaskForCustomer(Activity activity) {
            super(activity, R.string.async_task_string_updating_estimate);
        }

        @Override
        protected void onSuccess() {
            launchCustomerScreen();
        }

        @Override
        protected boolean taskBody(final String... args) throws Exception {

            int estimateId = AppDataSingleton.getInstance().getEstimate()
                    .getId();

            RESTEstimate.update(estimateId, AppDataSingleton.getInstance()
                            .getEstimate().getDescription(),
                    discount
                            .toString(), AppDataSingleton.getInstance()
                            .getEstimate().getSignature(),
                    handoffCustomerEmail, -1, AppDataSingleton.getInstance()
                            .getEstimate().getServicePlanId());

            RESTEstimate.query(AppDataSingleton.getInstance().getEstimate()
                    .getId());
            return true;
        }
    }

    public static class SubmitCloseAppointmentTask extends
            BaseUiReportTask<String> {

        public SubmitCloseAppointmentTask(Activity activity) {
            super(activity, R.string.async_task_string_closing_appointment);
            setAutocloseOnSuccess(true);
        }

        @Override
        protected boolean taskBody(final String... args) throws Exception {
            int appointmentId = AppDataSingleton.getInstance().getAppointment()
                    .getId();

            DateFormat df = null;
            df = new SimpleDateFormat("M/d/yy h:mm a");
            Date todaysDate = new Date();// get current date time with
            // Date()
            String currentDateTime = df.format(todaysDate).replace("am", "AM").replace("pm", "PM");

            RESTAppointment
                    .statusUpdate(
                            appointmentId,
                            com.skeds.android.phone.business.Utilities.General.ClassObjects.Status.CLOSE_APPOINTMENT
                                    .name(), UserUtilitiesSingleton
                                    .getInstance().user.getServiceProviderId(),
                            0, 0, 0, AppDataSingleton.getInstance()
                                    .getAppointment().getWorkOrderNumber(), currentDateTime, false, TimeZone.getDefault());
            return true;
        }
    }

    /**
     * Load list of available service plans from server. Refill service plan
     * spinner then finished.
     */
    private class LoadServicePlanTask extends BaseUiReportTask<String> {

        public LoadServicePlanTask(Activity parent) {
            super(parent, "Loading Agreements...");
        }

        @Override
        protected void onPreExecute() {
            AppDataSingleton.getInstance().getServicePlanList().clear();
            agreementEditButton.setEnabled(false);
        }

        @Override
        protected void onSuccess() {
            super.onSuccess();
            agreementEditButton.setEnabled(true);
        }

        @Override
        protected boolean taskBody(String... params) throws Exception {
            RESTServicePlanList.query(UserUtilitiesSingleton.getInstance().user
                    .getOwnerId());
            return true;
        }
    }

    private class EmailEstimateTask extends BaseUiReportTask<String> {

        public EmailEstimateTask() {
            super(ActivityEstimateView.this, "Email Estimate...");
            setAutocloseOnSuccess(true);
        }

        @Override
        protected boolean taskBody(String... params) throws Exception {
            RESTEstimate.sendToCustomer(AppDataSingleton.getInstance()
                    .getEstimate().getId(), textEmailAddress.getText()
                    .toString());
            return true;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            closeSlidingDialog();
        }

        @Override
        protected void onSuccess() {
            super.onSuccess();
            Toast.makeText(mActivity, R.string.estimate_emailed, Toast.LENGTH_SHORT).show();
        }

    }

    private class deleteEstimateTask extends BaseUiReportTask<String> {

        public deleteEstimateTask() {
            super(mActivity, "Deleting Estimate...");
        }

        @Override
        protected void onSuccess() {
            super.onSuccess();
            Toast.makeText(
                    mActivity,
                    "This estimate has been successfully removed from this appointment. If needed, you can find this estimate in the estimate search screen for this customer.",
                    Toast.LENGTH_LONG).show();
            finish();
        }

        @Override
        protected boolean taskBody(final String... args) throws Exception {
            RESTEstimate.deleteFromAppointment(estimateId);
            return true;
        }

    }

}