package com.skeds.android.phone.business.ui.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SlidingDrawer;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.skeds.android.phone.business.AsyncTasks.BaseUiReportTask;
import com.skeds.android.phone.business.AsyncTasks.SendInvoiceTask;
import com.skeds.android.phone.business.Dialogs.DialogEmailSelectForSendInvoice;
import com.skeds.android.phone.business.Dialogs.DialogErrorPopup;
import com.skeds.android.phone.business.Dialogs.DialogLineItemCustom;
import com.skeds.android.phone.business.Dialogs.DialogLineItemModify;
import com.skeds.android.phone.business.Dialogs.DialogLineItemModifyOrRemove;
import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Appointment;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Customer;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Invoice;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.LineItem;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Payment;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.PriceBook.TaxValue;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.TaxRate;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.TaxRateType;
import com.skeds.android.phone.business.Utilities.General.CommonUtilities;
import com.skeds.android.phone.business.Utilities.General.Constants;
import com.skeds.android.phone.business.Utilities.General.NumberFormatTool;
import com.skeds.android.phone.business.Utilities.General.TaxAmountCalculator;
import com.skeds.android.phone.business.Utilities.General.UserUtilitiesSingleton;
import com.skeds.android.phone.business.Utilities.REST.Objects.RESTInvoice;
import com.skeds.android.phone.business.activities.ActivityLineItemAdd;
import com.skeds.android.phone.business.activities.ActivityPaymentOptionTypesView;
import com.skeds.android.phone.business.activities.ActivityPaymentSignature;
import com.skeds.android.phone.business.activities.ActivityPricebookListView;
import com.skeds.android.phone.business.core.SkedsApplication;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InvoiceFragment extends BaseSkedsFragment {

    public static final String INVOICE_ID = "invoice_id";

    public static final String EXTRA_ORDER_NUMBER = "manualWorkOrderNumber";

    public static final String EXTRA_NEW_INVOICE = "new_invoice";

    public static final String EXTRA_LOADED_FROM_APPT = "from_appointment";

    private NumberFormatTool currencyFormatTool;

    private NumberFormatTool percentFormatTool;

    public static boolean isReadOnly;

    // public static int CREDIT_CARD_TYPE = 0;
    private String customerPrimaryEmail = "";


    public static boolean hasAnyChanges = false;

    private final static String mPercentageString = "Using percentage for discount";



    private final static String mNonPercentageString = "Using no percentage for discount";

    private Activity mActivity;

    private TextView textInvoiceNumber;

    private TextView textCustomerName;

    private String invoiceNumber;

    private TextView textPaymentHistory;

    private TextView textRemainingBalance;

    private TextView textCustomerPhoneNumber;

    private TextView textCustomerEmailAddress;

    private TextView textInvoiceAddress;

    private TextView textAppointmentType;

    private static TextView textInvoiceDate;

    private TextView textServiceProvider;

    private TextView textTotal;

    private TextView textDiscount;

    private LinearLayout taxTitleContainer;

    private LinearLayout taxValueContainer;

    private TextView textNetTotal;

    private TextView buttonNext;

    private TextView buttonSendInvoiceToCustomer;

    private TextView buttonCompleteInvoice;

    private TextView buttonSaveInvoice;

    private TextView buttonPartialInvoice;

    private TextView buttonSetDate;

    private TextView buttonAddLineItem;

    private TextView buttonAddDiscount;

    private TextView textAppointmentAddedEquipment;

    private static EditText edittextDescription;

    private static EditText edittextRecommendation;

    // Sliding Drawer
    private SlidingDrawer slidingDrawer;

    // For Date Dialog
    private Dialog dialogSelectDate;

    private DatePicker dialogSelectDateDatePicker;

    private TextView dialogSelectDateButtonSave;

    private TextView dialogSelectDateButtonCancel;

    // For Discount Dialog
    private Dialog dialogDiscount;

    private EditText dialogDiscountEditTextDiscount;

    private TextView dialogDiscountButtonSave, dialogDiscountButtonCancel;

    private ImageView dialogDiscountNavButtonDollars,
            dialogDiscountNavButtonPercentage;

    // For Standard/Custom Add Line Item Selection Dialog
    private Dialog dialogAddLineItemType;

    // private double mTotalPriceValue;
    // private int mCurrentAddedItemIterator;

    // This is all derived from the ViewAppointmentTrackable Class
    public static boolean mFromAppointment;

    private String date;

    /*
     * Primary Email Dialog
     */
    private Dialog dialogPrimaryEmail;

    private TextView dialogPrimaryEmailButtonSave,
            dialogPrimaryEmailButtonCancel;

    private EditText dialogPrimaryEmailEditTextEmail;

    private boolean mUpdateFromDialog;

    private boolean isNewInvoice;

    private boolean isLoadedFromAppt;

    private TextWatcher descriptionWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!s.toString().equals(AppDataSingleton.getInstance().getInvoice().getDescription()))
                hasAnyChanges = true;
            AppDataSingleton.getInstance().getInvoice().setDescription(s.toString());
        }
    };

    private TextWatcher recomendationsWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!s.toString().equals(AppDataSingleton.getInstance().getInvoice().getRecommendation()))
                hasAnyChanges = true;
            AppDataSingleton.getInstance().getInvoice().setRecommendation(s.toString());
        }
    };

    private OnClickListener buttonListener = new OnClickListener() {

        @Override
        public void onClick(View v) {

            switch (v.getId()) {

				/* 'Send email to customer' button */
                case R.id.activity_invoice_button_send_email_to_customer:
                    // Check if customer has an email, if they do, submit task,
                    // if
                    // not, primary email and submit
                    final DialogEmailSelectForSendInvoice emailDialog = new DialogEmailSelectForSendInvoice(
                            mActivity);
                    emailDialog.show();
                    emailDialog.setOnDismissListener(new OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {

                            if (emailDialog.submitOnComplete) {
                                EmailInvoiceToCustomerTask submitTask = new EmailInvoiceToCustomerTask();
                                submitTask.emailAddress = emailDialog.emailToUse;
                                submitTask.execute();
                            }
                        }
                    });
                    break;

				/* 'Set Date' button */
                case R.id.activity_invoice_button_set_date:
                    showDateTimeDialog();
                    // Check if I need to move or copy somewhere else
                    mUpdateFromDialog = true;
                    break;

				/* 'Add Discount' button */
                case R.id.activity_invoice_button_add_discount:
                    showDiscountDialog();

                    // Check if I need to move or copy somewhere else
                    mUpdateFromDialog = true;
                    break;

				/* 'Add Line Item' button */
                case R.id.activity_invoice_button_add_line_item:
                    // Check if I need to move or copy somewhere else
                    mUpdateFromDialog = true;
                    if (!TextUtils.isEmpty(edittextDescription.getText()))
                        AppDataSingleton.getInstance().getInvoice().setDescription(
                                edittextDescription.getText().toString());

                    if (!TextUtils.isEmpty(edittextRecommendation.getText()))
                        AppDataSingleton.getInstance().getInvoice().setRecommendation(
                                edittextRecommendation.getText().toString());

                    showAddLineItemDialog();
                    break;

				/* 'Next' button */
                case R.id.activity_invoice_button_next:

                    if (!CommonUtilities.isNetworkAvailable(getActivity())) {
                        Toast.makeText(getActivity(), "Network connection unavailable.",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Service.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edittextDescription.getWindowToken(), 0);
                    imm.hideSoftInputFromWindow(edittextRecommendation.getWindowToken(), 0);

                    AppDataSingleton.getInstance().getInvoice().setDescription(
                            edittextDescription.getText().toString());
                    AppDataSingleton.getInstance().getInvoice().setRecommendation(
                            edittextRecommendation.getText().toString());
                    AppDataSingleton.getInstance().getInvoice().setDate(
                            textInvoiceDate.getText().toString());
                    AppDataSingleton.getInstance().getInvoice().setCustomerEmail(customerPrimaryEmail);

                    if (slidingDrawer.isOpened()) {
                        slidingDrawer.animateClose();
                    } else {
                        slidingDrawer.animateOpen();
                    }
                    break;

				/* 'Complete Invoice' button */
                case R.id.activity_invoice_button_complete_invoice:
                    if (isShowingWarningDialog())
                        return;

                    if (!AppDataSingleton.getInstance().getInvoice().getLineItems().isEmpty()
                            || AppDataSingleton.getInstance().getInvoice().getLineItems() != null) {
                        ActivityPaymentOptionTypesView.mDescription = edittextDescription
                                .getText().toString();
                        ActivityPaymentOptionTypesView.mRecommendation = edittextRecommendation
                                .getText().toString();
                        ActivityPaymentOptionTypesView.mTodaysDate = textInvoiceDate
                                .getText().toString();

                        if (AppDataSingleton.getInstance().getInvoice().getForceSignatureOnInvoice()) {
                            new UpdateInvoiceTaskToPayments(false).execute();
                        } else {
                            if (AppDataSingleton.getInstance().getInvoice().getDeterminePaymentType()) {
                                new UpdateInvoiceTaskToPayments(false)
                                        .execute();
                            } else {

                                if (AppDataSingleton.getInstance().getCustomer().email.size() == 0) {
                                    if (TextUtils.isEmpty(customerPrimaryEmail)) {
                                        Toast.makeText(
                                                mActivity,
                                                "Customer does not have an email associated to receive digital invoice.",
                                                Toast.LENGTH_LONG).show();
                                        showPrimaryEmailDialog();
                                    } else {
                                        new SendInvoiceTask(mActivity)
                                                .execute();
                                    }
                                } else
                                    new SendInvoiceTask(mActivity).execute();

                            }
                        }
                    } else {
                        Toast.makeText(mActivity,
                                "Cannot submit invoice with no items.",
                                Toast.LENGTH_LONG).show();
                    }
                    break;

				/* 'Save Invoice' button */
                case R.id.activity_invoice_button_save_invoice:
                    new UpdateInvoiceTask().execute();
                    break;
                case R.id.activity_invoice_button_save_with_partial_payment:
                    new UpdateInvoiceTaskToPayments(true).execute();
                    break;
                default:
                    // Nothing
                    break;
            }
        }
    };


    /* 'Select Date' Dialog Buttons */
    private OnClickListener selectDateButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.dialog_date_picker_button_save:
                    /* 'Save' button */
                    String dateToUse = "";

                    dateToUse = (dialogSelectDateDatePicker.getMonth() + 1)
                            + "/" + dialogSelectDateDatePicker.getDayOfMonth()
                            + "/" + dialogSelectDateDatePicker.getYear();

                    textInvoiceDate.setText(dateToUse);

                    date = dateToUse;

                    if (dialogSelectDate.isShowing())
                        dialogSelectDate.dismiss();
                    break;
                /* 'Cancel' button */
                case R.id.dialog_date_picker_button_cancel:
                    if (dialogSelectDate.isShowing())
                        dialogSelectDate.dismiss();
                    break;
                default:
                    // Nothing
                    break;
            }
        }
    };
    private OnClickListener mDiscountDialogListener = new OnClickListener() {
        @Override
        public void onClick(View v) {

            Invoice invoice = AppDataSingleton.getInstance().getInvoice();
            switch (v.getId()) {
                case R.id.dialog_add_discount_button_save:

                    BigDecimal amount = new BigDecimal("0.00").setScale(2,
                            BigDecimal.ROUND_HALF_UP);
                    amount = new BigDecimal(dialogDiscountEditTextDiscount.getText()
                            .toString()).setScale(2, BigDecimal.ROUND_HALF_UP);


                    if (AppDataSingleton.getInstance().getInvoice().getDescription().contains(mPercentageString)) {

                        String description = mPercentageString + " " + amount + "%";
                        BigDecimal subtotal = calculateLineItemTotal(false);
                        AppDataSingleton.getInstance().getInvoice().setDescription(description);
                        AppDataSingleton.getInstance().getInvoice().setDiscount(subtotal.multiply(amount).divide(new BigDecimal("100.0"), 2, BigDecimal.ROUND_HALF_UP));

                        if (!edittextDescription.getText().toString()
                                .contains(mPercentageString))

                            edittextDescription.setText(description);


                    } else {
                        edittextDescription.setText(mNonPercentageString);
                        AppDataSingleton.getInstance().getInvoice().setDiscount(amount);
                    }

                    if (AppDataSingleton.getInstance().getInvoice().getDescription().contains(mPercentageString)) {

                        BigDecimal discountTotal = new BigDecimal("0.00");
                        discountTotal.setScale(3, BigDecimal.ROUND_HALF_UP);

                        if (amount.compareTo(new BigDecimal("100")) == -1) {


                           setupUI(true);
                            // amount

                            if (dialogDiscount.isShowing())
                                dialogDiscount.dismiss();

                        } else {
                            Toast.makeText(
                                    getActivity(),
                                    "Discount must not be larger than the subtotal.",
                                    Toast.LENGTH_LONG).show();
                        }

                    } else {

                        if (amount.compareTo(AppDataSingleton.getInstance()
                                .getInvoice().getNetTotal()) == -1
                                || amount.compareTo(AppDataSingleton.getInstance()
                                .getInvoice().getNetTotal()) == 0) {
                            // if (amount <=
                            // AppDataSingleton.getInstance().getInvoice().getNetTotal())
                            // {

                            textDiscount.setText(currencyFormatTool.format(amount));

                            setupUI(true); // Needs to refresh the total
                            // amount

                            if (dialogDiscount.isShowing())
                                dialogDiscount.dismiss();
                        } else {
                            Toast.makeText(
                                    getActivity(),
                                    "Discount must not be larger than the subtotal.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }

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


                    AppDataSingleton.getInstance().getInvoice().setDescription(mNonPercentageString);
                    break;

                case R.id.dialog_add_discount_imageview_percentage:

                    dialogDiscountNavButtonDollars
                            .setImageResource(R.drawable.custom_nav_button_invoice_discount_currency);

                    dialogDiscountNavButtonPercentage
                            .setImageResource(R.drawable.nav_invoice_discount_percentage_pressed);

                    AppDataSingleton.getInstance().getInvoice().setDescription(mPercentageString);
                    break;
                default:
                    // Nothing
                    break;
            }
        }
    };

    private OnClickListener mAddLineItemDialogListener = new OnClickListener() {
        @Override
        public void onClick(View v) {

            if (dialogAddLineItemType.isShowing())
                dialogAddLineItemType.dismiss();

            switch (v.getId()) {
                case R.id.dialog_yes_no_response_button_yes: // Standard

                    Intent i = null;
                    int targetSearchMode = AppDataSingleton.getInstance().getAppointment().getSearchModeOverride();

                    if (targetSearchMode == -1)
                        targetSearchMode = AppDataSingleton.getInstance().getPriceBookSearchMode();

                    switch (targetSearchMode) {
                        case Constants.PRICE_BOOK_SEARCH_MODE_DEFAULT:
                            i = new Intent(mActivity, ActivityLineItemAdd.class);
                            i.putExtra(Constants.EXTRA_LINE_ITEM_ADD_VIEW_MODE, Constants.LINE_ITEM_ADD_VIEW_FROM_INVOICE);
                            break;
                        case Constants.PRICE_BOOK_SEARCH_MODE_ONE_TIER:
                        case Constants.PRICE_BOOK_SEARCH_MODE_TWO_TIER:

                            i = new Intent(mActivity,
                                    ActivityPricebookListView.class);
                            i.putExtra(Constants.EXTRA_PRICEBOOK_LIST_VIEW_MODE, Constants.PRICEBOOK_LIST_VIEW_FROM_INVOICE);
                            break;
                    }

                    mActivity.startActivity(i);
                    break;
                case R.id.dialog_yes_no_response_button_no:
                    final DialogLineItemCustom customDialog = new DialogLineItemCustom(
                            mActivity, Constants.LINE_ITEM_ADD_VIEW_FROM_INVOICE);

                    customDialog
                            .setOnDismissListener(new DialogInterface.OnDismissListener() {

                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    setupUI(false);
                                }
                            });

                    customDialog.show();

                    // showCustomLineItemDialog();
                    break;
                default:
                    // Nothing
                    break;
            }
        }
    };

    private View.OnTouchListener touchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (slidingDrawer.isOpened()) {
                slidingDrawer.animateClose();
            }
            return false;
        }

    };

    View mViewToRemove;

    // double priceToRemove;
    int itemNoToRemove;

    boolean isShowing = false;

    private OnClickListener mLineItemRowListener = new OnClickListener() {

        @Override
        public void onClick(View v) {

            if (isReadOnly)
                return;

            itemNoToRemove = Integer.parseInt(v.getTag().toString());

            if (AppDataSingleton.getInstance().getInvoice().getLineItems()
                    .size()==0)
                return;

            if (AppDataSingleton.getInstance().getInvoice().getLineItems()
                    .size()<itemNoToRemove)
                return;

            mViewToRemove = v;

            final DialogLineItemModifyOrRemove itemDialog = new DialogLineItemModifyOrRemove(
                    mActivity, AppDataSingleton.getInstance().getInvoice().getLineItems()
                    .get(itemNoToRemove).getName());
            itemDialog.show();
            itemDialog.setOnDismissListener(new OnDismissListener() {

                @Override
                public void onDismiss(DialogInterface dialog) {
                    switch (itemDialog.dialogResult) {
                        case 0: // Modify
                            if (!AppDataSingleton.getInstance().getInvoice().getLineItems().get(itemNoToRemove).isCustomLineItem())
                                if (!UserUtilitiesSingleton.getInstance().user.isAllowEditPrice()) {
                                    Toast.makeText(mActivity, "Line Item Is Not Editable", Toast.LENGTH_SHORT).show();
                                    break;
                                }
                            showModifyDialog();
                            break;
                        case 1: // Remove
                            if (AppDataSingleton.getInstance().getInvoice().getLineItems().get(itemNoToRemove).getRemovable())
                                AppDataSingleton.getInstance().getInvoice().getLineItems()
                                        .remove(itemNoToRemove);
                            else
                                Toast.makeText(mActivity, "Line Item Is Not Removeable", Toast.LENGTH_SHORT).show();
                            setupUI(false);
                            break;
                        default:
                            // Nothing
                            break;
                    }

                }
            });
        }
    };

    int itemToRemove;

    private void showModifyDialog() {
        final DialogLineItemModify modifyDialog = new DialogLineItemModify(
                mActivity, Constants.LINE_ITEM_ADD_VIEW_FROM_INVOICE,
                itemNoToRemove);
        modifyDialog.show();
        modifyDialog.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                setupUI(false);
            }
        });
    }

    private OnClickListener mPrimaryEmailDialogListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.dialog_primary_email_button_save:
                    if (!TextUtils.isEmpty(dialogPrimaryEmailEditTextEmail.getText())) {
                        customerPrimaryEmail = dialogPrimaryEmailEditTextEmail
                                .getText().toString();
                        new SendInvoiceTask(mActivity).execute();
                    } else {
                        new SendInvoiceTask(mActivity).execute();
                    }
                    break;
                case R.id.dialog_primary_email_button_cancel:
                    new SendInvoiceTask(mActivity).execute();
                    break;
                default:
                    // Nothing
                    break;
            }
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_invoice_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        hasAnyChanges = false;

        currencyFormatTool = NumberFormatTool.getCurrencyFormat();
        percentFormatTool = NumberFormatTool.getPercentFormat();


        Bundle args = getArguments();

        if (args != null) {
            isNewInvoice = args.getBoolean(EXTRA_NEW_INVOICE);
            invoiceNumber = args.getString(EXTRA_ORDER_NUMBER);
            isLoadedFromAppt = args.getBoolean(EXTRA_LOADED_FROM_APPT);
        }

        mActivity = getActivity();
        if (isNewInvoice)
            date = AppDataSingleton.getInstance().getAppointment().getDate();
        else
            date = AppDataSingleton.getInstance().getInvoice().getDate();

        slidingDrawer = (SlidingDrawer) mActivity
                .findViewById(R.id.activity_invoice_slidingdrawer);

        textInvoiceNumber = (TextView) mActivity
                .findViewById(R.id.activity_invoice_textview_invoice_number);
        textCustomerName = (TextView) mActivity
                .findViewById(R.id.activity_invoice_textview_customer_name);
        textPaymentHistory = (TextView) mActivity
                .findViewById(R.id.activity_invoice_textview_payment_history);
        textRemainingBalance = (TextView) mActivity
                .findViewById(R.id.activity_invoice_textview_remaining_balance);
        textCustomerPhoneNumber = (TextView) mActivity
                .findViewById(R.id.activity_invoice_textview_customer_phone_number);
        textCustomerEmailAddress = (TextView) mActivity
                .findViewById(R.id.activity_invoice_textview_customer_email_address);
        textInvoiceAddress = (TextView) mActivity
                .findViewById(R.id.activity_invoice_textview_customer_address);
        textAppointmentType = (TextView) mActivity
                .findViewById(R.id.activity_invoice_textview_appointment_type);
        textInvoiceDate = (TextView) mActivity
                .findViewById(R.id.activity_invoice_textview_invoice_date);
        textServiceProvider = (TextView) mActivity
                .findViewById(R.id.activity_invoice_textview_service_provider);
        textTotal = (TextView) mActivity
                .findViewById(R.id.activity_invoice_textview_total);
        textNetTotal = (TextView) mActivity
                .findViewById(R.id.activity_invoice_textview_net_total);

        taxTitleContainer = (LinearLayout) mActivity.findViewById(R.id.tax_title_container);
        taxValueContainer = (LinearLayout) mActivity.findViewById(R.id.tax_value_container);

        textDiscount = (TextView) mActivity
                .findViewById(R.id.activity_invoice_textview_discount);
        buttonSendInvoiceToCustomer = (TextView) mActivity
                .findViewById(R.id.activity_invoice_button_send_email_to_customer);
        buttonNext = (TextView) mActivity.findViewById(R.id.activity_invoice_button_next);



        buttonCompleteInvoice = (TextView) slidingDrawer
                .findViewById(R.id.activity_invoice_button_complete_invoice);

        buttonSaveInvoice = (TextView) slidingDrawer
                .findViewById(R.id.activity_invoice_button_save_invoice);
        buttonPartialInvoice = (TextView) slidingDrawer
                .findViewById(R.id.activity_invoice_button_save_with_partial_payment);

        buttonSetDate = (TextView) mActivity
                .findViewById(R.id.activity_invoice_button_set_date);
        buttonAddLineItem = (TextView) mActivity
                .findViewById(R.id.activity_invoice_button_add_line_item);
        buttonAddDiscount = (TextView) mActivity
                .findViewById(R.id.activity_invoice_button_add_discount);

        edittextDescription = (EditText) mActivity
                .findViewById(R.id.activity_invoice_edittext_description);
        edittextDescription.setOnTouchListener(touchListener);
        edittextRecommendation = (EditText) mActivity
                .findViewById(R.id.activity_invoice_edittext_recommendation);
        edittextRecommendation.setOnTouchListener(touchListener);

        textAppointmentAddedEquipment = (TextView) mActivity
                .findViewById(R.id.activity_appointment_textview_added_equipment);

        customerPrimaryEmail = "";


        if (isNewInvoice) {
            isReadOnly = false;

            final Invoice invoice = new Invoice();
            AppDataSingleton.getInstance().setInvoice(invoice);
            final Customer customer = AppDataSingleton.getInstance().getCustomer();
            AppDataSingleton.getInstance().getInvoice().setCustomer(customer);
            AppDataSingleton.getInstance().setAllTechniciansFinished(true);
            buttonSaveInvoice.setText("Create Invoice");
            buttonCompleteInvoice.setVisibility(View.GONE);
            buttonPartialInvoice.setVisibility(View.GONE);

            final Appointment appointment = AppDataSingleton.getInstance().getAppointment();
            if (appointment != null) {
                invoice.setLocationTaxable(appointment.isLocationTaxable());

                //do not have possibility to create invoice from customer screen, that's why check is inside appt
                if (customer != null) {
                    invoice.setTaxable(customer.isTaxable());
                }
            }
        } else {
            final Invoice invoice = AppDataSingleton.getInstance().getInvoice();
            final Customer customer = invoice.getCustomer();
            invoice.setLocationTaxable(customer.isLocationTaxable());
            invoice.setTaxable(customer.isTaxable());
        }

        // setup invoice order number
        if (invoiceNumber != null)
            if (!TextUtils.isEmpty(invoiceNumber))
                AppDataSingleton.getInstance().getInvoice().setNumber(invoiceNumber);
    }


    @Override
    public void onResume() {
        super.onResume();
        setupUI(true);
    }

    private String formatPaymentType(String type) {
        String[] paymentTypesLinedUp = getResources().getStringArray(R.array.agreement_payment_types_lined_up);
        String[] paymentTypes = getResources().getStringArray(R.array.agreement_payment_types);

        for (int i = 0; i < paymentTypes.length; i++) {
            if (paymentTypesLinedUp[i].equals(type))
                return paymentTypes[i];
        }
        return type;
    }

    private String getFullAdress() {
        String address1;
        String address2;
        String addressCity;
        String addressState;
        String addressPostalCode;

        address1 = AppDataSingleton.getInstance().getInvoice().getCustomer().getAddress1();
        address2 = AppDataSingleton.getInstance().getInvoice().getCustomer().getAddress2();
        addressCity = AppDataSingleton.getInstance().getInvoice().getCustomer().getAddressCity();
        addressState = AppDataSingleton.getInstance().getInvoice().getCustomer().getAddressState();
        addressPostalCode = AppDataSingleton.getInstance().getInvoice().getCustomer().getAddressPostalCode();

        if (!address1.isEmpty() && !address2.isEmpty()) {
            return getString(R.string.adress_format_address1_adress2, address1, address2, addressCity, addressState, addressPostalCode);
        } else {
            return getString(R.string.adress_format_address1, address1, addressCity, addressState, addressPostalCode);
        }
    }

    private void setupUI(boolean refreshAppointmentData) {

        edittextDescription.setText(AppDataSingleton.getInstance().getInvoice()
                .getDescription());
        
        // This will stop the keyboard from automatically popping up
        mActivity.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // DecimalFormat format = new DecimalFormat("#0.00"); // To Make sure
        // prices appear
        // correctly

        textInvoiceNumber.setText(AppDataSingleton.getInstance().getInvoice().getNumber());

        if (AppDataSingleton.getInstance().getInvoice().getPaymentsList().isEmpty()) {
            textPaymentHistory.setText("");
        } else {
            StringBuilder builderBodyText = new StringBuilder();

            for (Payment model : AppDataSingleton.getInstance().getInvoice().getPaymentsList()) {
                builderBodyText.append(model.date + "  " + currencyFormatTool.format(model.paymentAmount) + "  " + formatPaymentType(model.getPaymentType())
                        + "\n");
            }
            textPaymentHistory.setText(builderBodyText.toString());
        }

        textRemainingBalance.setText(currencyFormatTool.format(AppDataSingleton.getInstance().getInvoice().getAmountDue()));


        // Hide these if we're in view-only
        if (isReadOnly) {
            buttonSetDate.setVisibility(View.INVISIBLE);
            buttonAddDiscount.setVisibility(View.INVISIBLE);
            buttonAddLineItem.setVisibility(View.INVISIBLE);
            buttonNext.setVisibility(View.GONE);
            edittextDescription.setEnabled(false);
            edittextRecommendation.setEnabled(false);

        } else {
            edittextDescription.addTextChangedListener(descriptionWatcher);
            edittextRecommendation.addTextChangedListener(recomendationsWatcher);
        }

        if (AppDataSingleton.getInstance().getInvoice().getId() != 0) {
            buttonSendInvoiceToCustomer.setVisibility(View.VISIBLE);
            buttonSendInvoiceToCustomer.setOnClickListener(buttonListener);
        } else {
            buttonSendInvoiceToCustomer.setVisibility(View.GONE);
        }

        if ("org".equals(AppDataSingleton.getInstance().getInvoice().getCustomer().getType()))
            textCustomerName.setText(AppDataSingleton.getInstance().getInvoice().getCustomer()
                    .getOrganizationName());
        else
            textCustomerName.setText(AppDataSingleton.getInstance().getInvoice().getCustomer()
                    .getFirstName()
                    + " "
                    + AppDataSingleton.getInstance().getInvoice().getCustomer().getLastName());

        String phone = "", email = "";
        if (!AppDataSingleton.getInstance().getInvoice().getCustomer().phone.isEmpty())
            phone = AppDataSingleton.getInstance().getInvoice().getCustomer().phone.get(0);
        if (!AppDataSingleton.getInstance().getInvoice().getCustomer().email.isEmpty())
            email = AppDataSingleton.getInstance().getInvoice().getCustomer().email.get(0);

        if (!TextUtils.isEmpty(phone))
            textCustomerPhoneNumber.setText(phone + " - "); // Add a dividing
            // bullet
            // point
        else
            textCustomerPhoneNumber.setText("");

        if (!email.isEmpty())
            textCustomerEmailAddress.setText(email);
        else
            textCustomerEmailAddress.setVisibility(View.GONE);

        textDiscount.setText(currencyFormatTool.format(AppDataSingleton.getInstance().getInvoice().getDiscount()));
        textInvoiceAddress.setText(isNewInvoice?AppDataSingleton.getInstance().getAppointment().getLocationName():getFullAdress());
        textAppointmentType.setText(AppDataSingleton.getInstance().getInvoice().getAppointmentType());
        textInvoiceDate.setText(date);

        if (!AppDataSingleton.getInstance().getInvoice().getServiceProviderList().isEmpty())
            textServiceProvider.setText(AppDataSingleton.getInstance().getInvoice().getServiceProviderList()
                    .get(0).getName().toString());

        textTotal.setText(currencyFormatTool.format(AppDataSingleton.getInstance().getInvoice().getTotal()));
        textNetTotal.setText(currencyFormatTool.format(AppDataSingleton.getInstance().getInvoice().getNetTotal()));

        if (refreshAppointmentData) {
            edittextDescription.setText(AppDataSingleton.getInstance().getInvoice().getDescription());
            edittextRecommendation.setText(AppDataSingleton.getInstance().getInvoice()
                    .getRecommendation());
        }

        // Adds the Line Items that were already included (not editable)
		/* Find Tablelayout */
        TableLayout tl = (TableLayout) mActivity.findViewById(R.id.activity_invoice_tablelayout_line_items);
        tl.removeAllViews();

        // set header if no line items
        TextView noItems = new TextView(mActivity);
        noItems.setText("No Items");
        noItems.setTextSize(17);
        noItems.setTextColor(Color.rgb(0, 0, 0));
        noItems.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 0f));
        noItems.setPadding(18, 0, 0, 0);
        noItems.setTypeface(null, Typeface.BOLD);
        noItems.setMaxLines(1);
        tl.addView(noItems);

		/* Adds header (QTY/DESC, ETC) */
		/* Create a new row to be added. */
        TableRow trHeader = new TableRow(mActivity);
        trHeader.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
                LayoutParams.WRAP_CONTENT));

        TextView quantityLabelHeader = new TextView(mActivity);

        quantityLabelHeader.setText("Qty");
        quantityLabelHeader.setTextSize(13);
        quantityLabelHeader.setTextColor(Color.rgb(0, 0, 0));
        quantityLabelHeader.setLayoutParams(new LayoutParams(
                LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 0f)); // Width,
        // Height,
        // Weight
        quantityLabelHeader.setTypeface(null, Typeface.BOLD);
        quantityLabelHeader.setMaxLines(1);

        View[] dividerHeader = new View[3];
        for (int x = 0; x < 3; x++) {
            dividerHeader[x] = new View(mActivity);
            dividerHeader[x].setLayoutParams(new LayoutParams(1,
                    LayoutParams.FILL_PARENT)); // Width,
            // Height
            dividerHeader[x].setBackgroundColor(Color.rgb(198, 198, 198));
        }

        TextView detailsLabelHeader = new TextView(mActivity);
        detailsLabelHeader.setText("Description");
        detailsLabelHeader.setTextSize(13);
        detailsLabelHeader.setTextColor(Color.rgb(0, 0, 0));

        detailsLabelHeader.setLayoutParams(new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 35f)); // Width,
        // Height,
        // Weight
        detailsLabelHeader.setPadding(4, 0, 0, 0);
        detailsLabelHeader.setTypeface(null, Typeface.BOLD);
        detailsLabelHeader.setMaxLines(1);

        List<TextView> taxHeaders = new ArrayList<TextView>();
        if (UserUtilitiesSingleton.getInstance().user.getCountryInfo().isUseExtendedTax()) {
            for (TaxRateType trt : UserUtilitiesSingleton.getInstance().user.getCountryInfo().getTaxRateTypes()) {
                TextView taxHeader = new TextView(mActivity);
                taxHeader.setText(trt.getName());
                taxHeader.setTextSize(13);
                taxHeader.setTextColor(Color.rgb(0, 0, 0));
                taxHeader.setLayoutParams(new LayoutParams(
                        LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 0f)); // Width,
                // Height,
                // Weight
                taxHeader.setTypeface(null, Typeface.BOLD);
                taxHeader.setMaxLines(1);

                taxHeaders.add(taxHeader);
            }
        }

        TextView priceLabelHeader = new TextView(mActivity);
        priceLabelHeader.setText("Unit Price");
        priceLabelHeader.setTextSize(13);
        priceLabelHeader.setTextColor(Color.rgb(0, 0, 0));
        priceLabelHeader.setLayoutParams(new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0f)); // Width,
        // Height,
        // Weight
        priceLabelHeader.setTypeface(null, Typeface.BOLD);
        priceLabelHeader.setMaxLines(1);

        TextView totalLabelHeader = new TextView(mActivity);
        totalLabelHeader.setText("Price");
        totalLabelHeader.setTextSize(13);
        totalLabelHeader.setTextColor(Color.rgb(0, 0, 0));
        // totalLabel.setTextColor(android.R.color.black);
        totalLabelHeader.setLayoutParams(new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0f)); // Width,
        // Height,
        // Weight
        totalLabelHeader.setTypeface(null, Typeface.BOLD);
        totalLabelHeader.setMaxLines(1);

		/* Add Button to row. */
        trHeader.addView(quantityLabelHeader);
        trHeader.addView(dividerHeader[0]);
        trHeader.addView(detailsLabelHeader);
        trHeader.addView(dividerHeader[1]);
        for (TextView taxHeader : taxHeaders) {
            trHeader.addView(taxHeader);
            View divider = new View(mActivity);
            divider.setLayoutParams(new LayoutParams(1,
                    LayoutParams.MATCH_PARENT)); // Width, Height
            divider.setBackgroundColor(Color.rgb(198, 198, 198));
            trHeader.addView(divider);
        }
        trHeader.addView(priceLabelHeader);
        trHeader.addView(dividerHeader[2]);
        trHeader.addView(totalLabelHeader);


        if (AppDataSingleton.getInstance().getInvoice().getLineItems() != null) {
            if (!AppDataSingleton.getInstance().getInvoice().getLineItems().isEmpty()) {
				
				/* Add row to TableLayout. */
                tl.removeAllViews();
                tl.addView(trHeader, new TableLayout.LayoutParams(
                        LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

                View hDividerHeader = new View(mActivity);
                hDividerHeader = new View(mActivity);
                hDividerHeader.setLayoutParams(new LayoutParams(
                        LayoutParams.MATCH_PARENT, 1)); // Width, Height
                hDividerHeader.setBackgroundColor(Color.rgb(198, 198, 198));
                tl.addView(hDividerHeader);

                for (int i = 0; i < AppDataSingleton.getInstance().getInvoice().getLineItems().size(); i++) {
					/* Create a new row to be added. */
                    TableRow tr = new TableRow(mActivity);
                    tr.setLayoutParams(new LayoutParams(
                            LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

                    TextView quantityLabel = new TextView(mActivity);
                    quantityLabel.setContentDescription("line_" + i + "_quantity");
                    // quantityLabel.setText(String
                    // .valueOf(AppDataSingleton.getInstance().getInvoice().lineItem[i]
                    // .getQuantity()));
                    quantityLabel
                            .setText(String.valueOf(AppDataSingleton.getInstance().getInvoice().getLineItems()
                                    .get(i).getQuantity()));
                    quantityLabel.setTextColor(Color.rgb(0, 0, 0));
                    quantityLabel.setLayoutParams(new LayoutParams(
                            LayoutParams.FILL_PARENT,
                            LayoutParams.WRAP_CONTENT, 0f)); // Width,
                    // Height,
                    // Weight
                    quantityLabel.setPadding(0, 15, 0, 15);
                    quantityLabel.setMaxLines(2);
                    quantityLabel.setTextSize(13);
                    quantityLabel.setTypeface(null, Typeface.BOLD);
                    quantityLabel.setMarqueeRepeatLimit(0);
                    quantityLabel.setEllipsize(TruncateAt.MARQUEE);
                    quantityLabel.setGravity(Gravity.RIGHT);

                    View[] divider = new View[3];
                    for (int x = 0; x < 3; x++) {
                        divider[x] = new View(mActivity);
                        divider[x].setLayoutParams(new LayoutParams(1,
                                LayoutParams.FILL_PARENT)); // Width,
                        // Height
                        divider[x].setBackgroundColor(Color.rgb(198, 198, 198));
                    }

                    LinearLayout descriptionLayout = new LinearLayout(mActivity);
                    descriptionLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                            LayoutParams.WRAP_CONTENT, 35f));
                    descriptionLayout.setOrientation(LinearLayout.VERTICAL);
                    descriptionLayout.setPadding(4, 15, 0, 15);

                    TextView detailsLabel = new TextView(mActivity);
                    detailsLabel.setContentDescription("line_" + i + "_details");

                    String detailsText = AppDataSingleton.getInstance().getInvoice().getLineItems().get(i).getName().trim();
                    detailsLabel.setText(detailsText);
                    detailsLabel.setTextSize(13);
                    detailsLabel.setTextColor(Color.rgb(0, 0, 0));
                    detailsLabel.setLayoutParams(new LayoutParams(
                            LayoutParams.MATCH_PARENT,
                            LayoutParams.WRAP_CONTENT, 35f));
                    detailsLabel.setTypeface(null, Typeface.BOLD);
                    detailsLabel.setMarqueeRepeatLimit(0);

                    if (!detailsText.contains(" ")) {
                        detailsLabel.setEllipsize(TruncateAt.END);
                        detailsLabel.setSingleLine();
                    }

                    // setup TAXES
                    List<TextView> taxViews = new ArrayList<TextView>();
                    if (UserUtilitiesSingleton.getInstance().user.getCountryInfo().isUseExtendedTax()) {
                        int iterator = 1;
                        for (TaxRateType trt : UserUtilitiesSingleton.getInstance().user.getCountryInfo().getTaxRateTypes()) {
                            TextView taxView = new TextView(mActivity);

                            TaxRate selectedRate = new TaxRate();
                            for (Long rateId : AppDataSingleton.getInstance().getInvoice().getLineItems().get(i).rateIds) {

                                for (TaxRate rate : UserUtilitiesSingleton.getInstance().user.getCountryInfo().getTaxRates()) {
                                    if (rate.getId() == rateId && rate.getType().equals(trt.getType())) {
                                        selectedRate = rate;
                                        break;
                                    }

                                }
                            }
                            taxView.setContentDescription("line_" + i + "_vat" + (iterator++));

                            if (selectedRate.getId() > 0)
                                taxView.setText(getString(R.string.invoice_tax_format, selectedRate.getName(), percentFormatTool.format(selectedRate.getValue().doubleValue())));
                            else taxView.setText(" - ");
                            taxView.setTextSize(13);
//							taxView.setEms(4);
                            taxView.setGravity(Gravity.CENTER_HORIZONTAL);
                            taxView.setTextColor(Color.rgb(0, 0, 0));
                            taxView.setLayoutParams(new LayoutParams(
                                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 0f)); // Width,
                            // Height,
                            // Weight
                            taxView.setTypeface(null, Typeface.BOLD);
//							taxView.setMaxLines(2);

                            taxViews.add(taxView);
                        }
                    }

                    TextView realDetailsLabel = new TextView(mActivity);
                    realDetailsLabel.setContentDescription("line_" + i + "_real_details");

                    realDetailsLabel.setText(AppDataSingleton.getInstance().getInvoice().getLineItems().get(i).getDescription());
                    realDetailsLabel.setTextSize(13);
                    realDetailsLabel.setTextColor(Color.rgb(0, 0, 0));
                    realDetailsLabel.setLayoutParams(new LayoutParams(
                            LayoutParams.MATCH_PARENT,
                            LayoutParams.WRAP_CONTENT, 35f));
                    realDetailsLabel.setEms(7);
                    realDetailsLabel.setTypeface(null, Typeface.ITALIC);
                    realDetailsLabel.setMarqueeRepeatLimit(0);
                    realDetailsLabel.setEllipsize(TruncateAt.END);


                    switch (((SkedsApplication) getActivity().getApplication()).getLineItemsMode()) {
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

                    TextView priceLabel = new TextView(mActivity);
                    priceLabel.setContentDescription("line_" + i + "_price");

                    priceLabel.setText(currencyFormatTool.format(
                            AppDataSingleton.getInstance().getInvoice().getLineItems().get(i).cost
                                    .setScale(2, RoundingMode.HALF_UP)));
                    priceLabel.setTextSize(13);
                    priceLabel.setTextColor(Color.rgb(0, 0, 0));
                    priceLabel.setLayoutParams(new LayoutParams(
                            LayoutParams.WRAP_CONTENT,
                            LayoutParams.WRAP_CONTENT, 0f));
                    priceLabel.setPadding(0, 0, 6, 0);
                    priceLabel.setTypeface(null, Typeface.BOLD);
                    priceLabel.setMaxLines(1);
                    priceLabel.setMarqueeRepeatLimit(0);
                    priceLabel.setEllipsize(TruncateAt.MARQUEE);
                    priceLabel.setGravity(Gravity.RIGHT);

                    TextView totalLabel = new TextView(mActivity);
                    totalLabel.setContentDescription("line_" + i + "_total");

                    totalLabel.setText(currencyFormatTool.format(
                            AppDataSingleton.getInstance().getInvoice().getLineItems().get(i).finalCost
                                    .setScale(2, RoundingMode.HALF_UP)));
                    totalLabel.setTextSize(13);
                    totalLabel.setTextColor(Color.rgb(0, 0, 0));
                    // totalLabel.setTextColor(android.R.color.black);
                    totalLabel.setLayoutParams(new LayoutParams(
                            LayoutParams.WRAP_CONTENT,
                            LayoutParams.WRAP_CONTENT, 0f));
                    totalLabel.setPadding(0, 0, 6, 0);
                    totalLabel.setTypeface(null, Typeface.BOLD);
                    totalLabel.setMaxLines(1);
                    totalLabel.setMarqueeRepeatLimit(0);
                    totalLabel.setEllipsize(TruncateAt.MARQUEE);
                    totalLabel.setGravity(Gravity.RIGHT);

					/* Add Button to row. */
                    // tr.addView(b);
                    tr.addView(quantityLabel);
                    tr.addView(divider[0]);
                    tr.addView(descriptionLayout);
                    tr.addView(divider[1]);
                    for (TextView taxView : taxViews) {
                        tr.addView(taxView);
                        View divider1 = new View(mActivity);
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
                    // (AppDataSingleton.getInstance().getInvoice().lineItem[i].getRemovable())

                    if (!AppDataSingleton.getInstance().getInvoice().getLineItems().get(i).isLabor())
                        tr.setOnClickListener(mLineItemRowListener);

					/* Add Tag so we know which to remove */
                    tr.setTag(i);

					/* Add row to TableLayout. */
                    tl.addView(tr,
                            new TableLayout.LayoutParams(
                                    LayoutParams.FILL_PARENT,
                                    LayoutParams.WRAP_CONTENT));
                }
            }
        }

        buttonNext.setOnClickListener(buttonListener);
        buttonSetDate.setOnClickListener(buttonListener);
        buttonAddLineItem.setOnClickListener(buttonListener);
        buttonAddDiscount.setOnClickListener(buttonListener);
        buttonCompleteInvoice.setOnClickListener(buttonListener);
        buttonSaveInvoice.setOnClickListener(buttonListener);
        buttonPartialInvoice.setOnClickListener(buttonListener);


        edittextDescription.clearFocus();
        edittextRecommendation.clearFocus();

        // updateTable();
        calculateTotal();
        calculateRemainingTotal();

        String addedEquipment = "";
        if (!AppDataSingleton.getInstance().getInvoice().getEquipmentList()
                .isEmpty()) {
            StringBuilder output = new StringBuilder();
            textAppointmentAddedEquipment.setVisibility(View.VISIBLE);
            for (int i = 0; i < AppDataSingleton.getInstance().getInvoice().getEquipmentList()
                    .size(); i++) {
                output.append(AppDataSingleton.getInstance().getInvoice().getEquipmentList()
                        .get(i).getName());
                output.append(" Model: ");
                output.append(AppDataSingleton.getInstance().getInvoice().getEquipmentList()
                        .get(i).getModelNumber());
                output.append(" Serial: ");
                output.append(AppDataSingleton.getInstance().getInvoice().getEquipmentList()
                        .get(i).getSerialNumber());
                output.append("\n");

            }
            addedEquipment = output.toString();
            textAppointmentAddedEquipment.setText(addedEquipment);
        } else {
            textAppointmentAddedEquipment.setVisibility(View.GONE);
            ((View) mActivity.findViewById(R.id.activity_appointment_textview_added_equipment_title)).setVisibility(View.GONE);
        }
    }

    private void showDateTimeDialog() {
        // Create the dialog
        hasAnyChanges = true;

        dialogSelectDate = new Dialog(mActivity);
        dialogSelectDate.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSelectDate.setContentView(R.layout.dialog_layout_date_picker);
        dialogSelectDate.setTitle("Select Date");

        dialogSelectDateButtonSave = (TextView) dialogSelectDate
                .findViewById(R.id.dialog_date_picker_button_save);
        dialogSelectDateButtonCancel = (TextView) dialogSelectDate
                .findViewById(R.id.dialog_date_picker_button_cancel);

        dialogSelectDateDatePicker = (DatePicker) dialogSelectDate
                .findViewById(R.id.dialog_date_picker_datepicker);

        dialogSelectDateButtonSave.setOnClickListener(selectDateButtonListener);
        dialogSelectDateButtonCancel
                .setOnClickListener(selectDateButtonListener);

        dialogSelectDate.show();
    }

    private void showDiscountDialog() {
        // Create the dialog
        hasAnyChanges = true;

        dialogDiscount = new Dialog(mActivity);
        dialogDiscount.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogDiscount.setContentView(R.layout.dialog_layout_add_discount);
        dialogDiscount.setTitle("Set Discount");

        dialogDiscountButtonSave = (TextView) dialogDiscount
                .findViewById(R.id.dialog_add_discount_button_save);
        dialogDiscountButtonCancel = (TextView) dialogDiscount
                .findViewById(R.id.dialog_add_discount_button_cancel);

        dialogDiscountEditTextDiscount = (EditText) dialogDiscount
                .findViewById(R.id.dialog_add_discount_edittext_value);

        Invoice invoice = AppDataSingleton.getInstance().getInvoice();
        String discount = invoice.getDiscount().toString();
        BigDecimal discountPercentageValue = invoice.getDiscountPercentageValue();


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


        if (AppDataSingleton.getInstance().getInvoice().getDescription().contains(mPercentageString))
            dialogDiscountNavButtonPercentage.callOnClick();
        else dialogDiscountNavButtonDollars.callOnClick();

        dialogDiscount.show();
    }


    private boolean isShowingWarningDialog() {

        if (!AppDataSingleton.getInstance().getInvoice().isOthersFinished()) {
            DialogErrorPopup errorPopup = new DialogErrorPopup(mActivity, "Notice",
                    "Unable to Complete Invoice. " +
                            "There are either technicians still working on this appointment or there are other appointments with the same work order number still open", null);
            errorPopup.show();
            return true;
        } else if (!AppDataSingleton.getInstance().getInvoice().isMeFinished()) {
            DialogErrorPopup errorPopup = new DialogErrorPopup(mActivity, "Notice",
                    "Unable to Complete Invoice. " +
                            "Please update the job status of all previous appointments for this invoice to 'Finished", null);
            errorPopup.show();
            return true;
        }
        return false;
    }

    private void showAddLineItemDialog() {
        hasAnyChanges = true;

        dialogAddLineItemType = new Dialog(mActivity);
        dialogAddLineItemType.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogAddLineItemType
                .setContentView(R.layout.dialog_layout_yes_no_response);

        TextView textTitle = (TextView) dialogAddLineItemType
                .findViewById(R.id.dialog_yes_no_response_textview_title);
        TextView textBody = (TextView) dialogAddLineItemType
                .findViewById(R.id.dialog_yes_no_response_textview_body);
        TextView buttonStandard = (TextView) dialogAddLineItemType
                .findViewById(R.id.dialog_yes_no_response_button_yes);
        TextView buttonCustom = (TextView) dialogAddLineItemType
                .findViewById(R.id.dialog_yes_no_response_button_no);

        textTitle.setText("Add Line Item");
        textBody.setText("Select the type of item to add");
        buttonStandard.setText("Standard");
        buttonCustom.setText("Custom");

        buttonStandard.setOnClickListener(mAddLineItemDialogListener);
        buttonCustom.setOnClickListener(mAddLineItemDialogListener);

        dialogAddLineItemType.show();
    }

    private void showPrimaryEmailDialog() {
        dialogPrimaryEmail = new Dialog(mActivity);
        dialogPrimaryEmail.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogPrimaryEmail.setContentView(R.layout.dialog_layout_primary_email);

        dialogPrimaryEmailButtonSave = (TextView) dialogPrimaryEmail
                .findViewById(R.id.dialog_primary_email_button_save);
        dialogPrimaryEmailButtonCancel = (TextView) dialogPrimaryEmail
                .findViewById(R.id.dialog_primary_email_button_cancel);

        dialogPrimaryEmailEditTextEmail = (EditText) dialogPrimaryEmail
                .findViewById(R.id.dialog_primary_email_edittext_email);

        dialogPrimaryEmailButtonSave
                .setOnClickListener(mPrimaryEmailDialogListener);
        dialogPrimaryEmailButtonCancel
                .setOnClickListener(mPrimaryEmailDialogListener);

        dialogPrimaryEmail.show();
    }

    int addedRowNum = 0;

    private BigDecimal calculateLineItemTotal(boolean includeDeclined) {

        // double result = 0.0;
        BigDecimal result = new BigDecimal("0.00");
        result.setScale(2, RoundingMode.HALF_UP);
        final List<LineItem> lineItems = AppDataSingleton.getInstance().getInvoice().getLineItems();

        for (int i = 0; i < lineItems.size(); i++) {
            final LineItem lineItem = AppDataSingleton.getInstance().getInvoice().getLineItems().get(i);
            if (!includeDeclined)
                if (lineItem.getRecommendation() == LineItem.Recommendation.DECLINED)
                    continue;

            final BigDecimal quantity = lineItem.getQuantity();
            quantity.setScale(2, RoundingMode.HALF_UP);
            final BigDecimal lineItemCost;
            final BigDecimal itemCost;

            if (lineItem.isUserAdded()) {
                if (lineItem.isUsingAdditionalCost()) {
                    itemCost = lineItem.additionalCost;
                } else {
                    itemCost = lineItem.cost;
                }
            } else {
                itemCost = lineItem.cost;
            }

            lineItemCost = itemCost.multiply(quantity);
            result = result.add(lineItemCost);
        }

        AppDataSingleton.getInstance().getInvoice().setTotal(result);
        return result;
    }

    /**
     * Calculate total tax for invoice
     *
     * @return Summary of taxes
     */
    private BigDecimal calculateTotalTax() {

        final List<LineItem> lineItems = AppDataSingleton.getInstance().getInvoice().getLineItems();

        // Summary of all taxable line items
        BigDecimal totalTaxable = new BigDecimal("0.00");
        totalTaxable.setScale(2, RoundingMode.HALF_UP);
        // Summary of all non taxable line items
        BigDecimal totalNonTaxable = new BigDecimal("0.00");
        totalNonTaxable.setScale(2, RoundingMode.HALF_UP);
        // Summary of Taxes
        BigDecimal result = new BigDecimal("0.00");
        result.setScale(2, RoundingMode.HALF_UP);

        final int linesCount = lineItems.size();
        for (int i = 0; i < linesCount; i++) {

            final LineItem lineItem = lineItems.get(i);
            BigDecimal itemCost;
            BigDecimal quantity = lineItem.getQuantity();
            quantity.setScale(2, RoundingMode.HALF_UP);
            BigDecimal lineItemCost;

            if (lineItem.isUserAdded()) {
                if (lineItem.isUsingAdditionalCost()) {
                    itemCost = lineItem.additionalCost;
                } else {
                    itemCost = lineItem.cost;
                }
            } else {
                itemCost = lineItem.cost;
            }

            lineItemCost = itemCost.multiply(quantity);
            if (lineItem.getTaxable()) {
                totalTaxable = totalTaxable.add(lineItemCost);
            } else {
                totalNonTaxable = totalNonTaxable.add(lineItemCost);
            }

        }

        // Dan's FIX I'm not sure that this is really needed
        // if (!AppDataSingleton.getInstance().isCalculateSalesTaxFirst()) {
        // if (AppDataSingleton.getInstance().getInvoice().discount.compareTo(totalNonTaxable) == 1) {
        //
        // BigDecimal discountOverNonTaxable = new BigDecimal("0.00")
        // .setScale(2, BigDecimal.ROUND_HALF_UP);
        // discountOverNonTaxable = AppDataSingleton.getInstance().getInvoice().discount
        // .subtract(totalNonTaxable);
        // totalTaxable = totalTaxable.subtract(discountOverNonTaxable);
        // }
        // }

        final BigDecimal discount = AppDataSingleton.getInstance().getInvoice().getDiscount();
        final BigDecimal total = totalTaxable.add(totalNonTaxable);
        final BigDecimal taxableOfTotal = totalTaxable.divide(total, 2,
                RoundingMode.HALF_UP);

        // This can be used or not
        final BigDecimal discountOverTaxable = discount
                .multiply(taxableOfTotal);

        result = totalTaxable;

        if (!AppDataSingleton.getInstance().isCalculateSalesTaxFirst())
            // if (AppDataSingleton.getInstance().getInvoice().discount.compareTo(totalNonTaxable)
            // ==
            // 1)
            result = result.subtract(discountOverTaxable);

        BigDecimal taxRate = AppDataSingleton.getInstance().getInvoice().getTaxRate();
        if (taxRate.compareTo(new BigDecimal("0.00")) == 0)
            taxRate = AppDataSingleton.getInstance().getCustomerEstimateTaxRate();

        BigDecimal totalTax = result.multiply(taxRate).setScale(2,
                RoundingMode.HALF_UP);

        return totalTax;
    }

    /**
     * Calculate total amount for entire invoice:
     * <p/>
     * taxable = 75.88 nontaxable = 85.00 total = 160.88 taxable of total = 0.47
     * total discount = 160.88 * 0.2 = 32.18 amount to be taxed 75.88 - 32.18 *
     * 0.47 = 60.7554 tax = 60.7554 * 0.0825 = 5.01232 total + tax = 128.70 +
     * 5.01232 = 133.712 now it seems to be ok
     */
    private void calculateTotal() {

        BigDecimal subtotalIncludeDiclined = new BigDecimal("0.00").setScale(2, RoundingMode.HALF_UP);
        BigDecimal runningTax = new BigDecimal("0.00").setScale(2, RoundingMode.HALF_UP);

        // Calculate sum of overall items not declined
        subtotalIncludeDiclined = calculateLineItemTotal(false);


        BigDecimal discount = BigDecimal.ZERO;
        if (AppDataSingleton.getInstance().getInvoice().getDescription().contains(mPercentageString)) {

            try {
                String description  = AppDataSingleton.getInstance().getInvoice().getDescription();

                String parse =  description.substring(mPercentageString.length(),description.indexOf("%"));
                BigDecimal discountPercent = new BigDecimal(parse.trim());
                discount =
                        subtotalIncludeDiclined.multiply(discountPercent)
                                .divide(new BigDecimal("100.0").setScale(2, RoundingMode.HALF_UP), 2, RoundingMode.HALF_UP);
            }catch (Exception ex){
                ex.printStackTrace();
            }
        } else {
            discount = AppDataSingleton.getInstance().getInvoice().getDiscount();
        }


        AppDataSingleton.getInstance().getInvoice().setDiscount(discount);

        if (subtotalIncludeDiclined.compareTo(new BigDecimal("0.0")) == 1) {

            runningTax = new TaxAmountCalculator()
                    .calculateTaxAmount(AppDataSingleton.getInstance().getInvoice())
                    .setScale(2, RoundingMode.HALF_UP);
        }

        if (subtotalIncludeDiclined.compareTo(discount) == -1)
            discount = subtotalIncludeDiclined;

        AppDataSingleton.getInstance().getInvoice().setNetTotal(subtotalIncludeDiclined);
        textNetTotal.setText(currencyFormatTool.format(subtotalIncludeDiclined));

        textDiscount.setText(currencyFormatTool.format(
                discount));

//		textTaxValue.setText(currencyFormatTool.format(runningTax)); // Set the amount for
//											// overall taxes

        taxTitleContainer.removeAllViews();
        taxValueContainer.removeAllViews();


        if (UserUtilitiesSingleton.getInstance().user.getCountryInfo().isUseExtendedTax()) {

            List<TaxRateType> taxRateTypesForBusiness = UserUtilitiesSingleton.getInstance().user.getCountryInfo().getTaxRateTypes();

            Map<TaxRate, BigDecimal> extendedInfoTypes = AppDataSingleton.getInstance().getInvoice().getExtendedInfoTypes();
            for (TaxRate taxRate : extendedInfoTypes.keySet()) {

                TextView title = new TextView(mActivity);
                title.setContentDescription(taxRate.getName() + "_label");

                title.setTextColor(getResources().getColor(android.R.color.black));
                title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                title.setText(taxRate.getName());
                taxTitleContainer.addView(title);

                TextView value = new TextView(mActivity);
                value.setContentDescription(taxRate.getName() + "_value");

                value.setTextColor(getResources().getColor(android.R.color.black));
                value.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
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
                sum(subtotalIncludeDiclined, AppDataSingleton.getInstance().getInvoice().getTaxes()) :
                subtotalIncludeDiclined.add(runningTax))
                .subtract(discount);

        AppDataSingleton.getInstance().getInvoice().setTotal(finalTotal);

        textTotal.setText(currencyFormatTool.format(finalTotal.setScale(2, RoundingMode.HALF_UP)));
    }

    private BigDecimal sum(BigDecimal subtotal, List<TaxValue> taxes) {
        BigDecimal sum = subtotal;
        for (TaxValue taxValue : taxes) {
            sum = sum.add(taxValue.getValue());
        }
        return sum;
    }

    private void calculateRemainingTotal() {

        BigDecimal paidAmount = new BigDecimal("0.00");

        for (Payment model : AppDataSingleton.getInstance().getInvoice().getPaymentsList()) {
            paidAmount = paidAmount.add(model.paymentAmount);
        }


        BigDecimal remain = AppDataSingleton.getInstance().getInvoice().getTotal().subtract(paidAmount)
                .setScale(2, RoundingMode.HALF_UP);
        textRemainingBalance.setText(currencyFormatTool.format(remain));

        AppDataSingleton.getInstance().getInvoice().setNetTotal(remain);
    }


    private final class UpdateInvoiceTask extends BaseUiReportTask<String> {
        UpdateInvoiceTask() {
            super(mActivity,
                    R.string.async_task_string_updating_invoice);
            setAutocloseOnSuccess(false); // as long as we switch to dashboard on
            // success
            hasAnyChanges = false;
        }

        @Override
        protected void onSuccess() {
            getActivity().setResult(Activity.RESULT_OK);
            mActivity.onBackPressed();
        }

        @Override
        protected boolean taskBody(final String... args) throws Exception {
            Invoice invoice = AppDataSingleton.getInstance().getInvoice();
            if (isNewInvoice) {
                setProgressMessage("Creating Invoice...");
                int appointmentId = AppDataSingleton.getInstance().getAppointment().getId();
                int customerId = invoice.getCustomer().getId();
                RESTInvoice.addOrUpdate(invoice, customerId, appointmentId);
            } else
                RESTInvoice.update(invoice);
            return true;
        }
    }


    private final class UpdateInvoiceTaskToPayments
            extends
            BaseUiReportTask<String> {
        boolean isPartialPayment;

        UpdateInvoiceTaskToPayments(boolean isPartialPayment) {
            super(mActivity,
                    R.string.async_task_string_updating_invoice);
            this.isPartialPayment = isPartialPayment;
            hasAnyChanges = false;
        }

        @Override
        protected void onSuccess() {
            getActivity().setResult(Activity.RESULT_OK);
            if (AppDataSingleton.getInstance().getInvoice().getForceSignatureOnInvoice()) {
                AppDataSingleton.getInstance().setSignatureViewMode(Constants.SIGNATURE_VIEW_FROM_INVOICE);
                Intent i = new Intent(mActivity,
                        ActivityPaymentSignature.class);

                i.putExtra("partial_payment", isPartialPayment); // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                startActivity(i);
                mActivity.onBackPressed();
            } else {
                if (AppDataSingleton.getInstance().getInvoice().getDeterminePaymentType()) {
                    Intent i = new Intent(mActivity,
                            ActivityPaymentOptionTypesView.class);
                    i.putExtra("partial_payment", isPartialPayment); // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    startActivity(i);
                    mActivity.onBackPressed();
                }
            }
        }

        @Override
        protected boolean taskBody(final String... args) throws Exception {
            RESTInvoice.update(AppDataSingleton.getInstance().getInvoice());
            return true;
        }
    }

    private final class EmailInvoiceToCustomerTask
            extends
            BaseUiReportTask<String> {
        String emailAddress;

        EmailInvoiceToCustomerTask() {
            super(mActivity,
                    R.string.async_task_string_sending_invoice);
            hasAnyChanges = false;
        }

        @Override
        protected void onSuccess() {
            Toast.makeText(mActivity, R.string.invoice_emailed, Toast.LENGTH_SHORT).show();
            getActivity().setResult(Activity.RESULT_OK);
        }

        @Override
        protected void onFailed() {
            super.onFailed();
            Toast.makeText(mActivity, "Email Has Not Been Sent! Please try again", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected boolean taskBody(final String... args) throws Exception {
            RESTInvoice.sendToCustomer(AppDataSingleton.getInstance().getInvoice().getId(),
                    emailAddress);
            return true;
        }
    }

}
