package com.skeds.android.phone.business.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.skeds.android.phone.business.AsyncTasks.BaseUiReportTask;
import com.skeds.android.phone.business.AsyncTasks.SendInvoiceTask;
import com.skeds.android.phone.business.Custom.AccountMenu;
import com.skeds.android.phone.business.Custom.QuickAction;
import com.skeds.android.phone.business.Dialogs.DialogSplitPayment;
import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Appointment;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Invoice;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.LineItem;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.PaymentType;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.PriceBook.TaxValue;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.TaxRateType;
import com.skeds.android.phone.business.Utilities.General.CommonUtilities;
import com.skeds.android.phone.business.Utilities.General.NumberFormatTool;
import com.skeds.android.phone.business.Utilities.General.TaxAmountCalculator;
import com.skeds.android.phone.business.Utilities.General.UserUtilitiesSingleton;
import com.skeds.android.phone.business.Utilities.NonfatalException;
import com.skeds.android.phone.business.Utilities.REST.Objects.RESTAppointment;
import com.skeds.android.phone.business.Utilities.REST.Objects.RESTInvoice;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class ActivityPaymentOptionTypesView extends BaseSkedsActivity {

    public static final BigDecimal EPSILON = new BigDecimal("0.10");
    private String DEBUG_TAG = "[Payment Options]";

    private Activity mActivity;
    private Context mContext;

    private LinearLayout headerLayout;
    private ImageView headerButtonUser;
    private ImageView headerButtonBack;

    private CheckBox mAllowBillLaterBox;

    private EditText edittextCheckNumber;
    private ImageView buttonCash, buttonCreditCard, buttonCheck,
            buttonBillLater, buttonPrepaid, buttonDebit;

    public static final int TYPE_BILL_LATER = 1, TYPE_CASH = 2, TYPE_CHECK = 3,
            TYPE_CREDIT_CARD = 4, TYPE_PREPAID = 5, TYPE_DEBIT = 6;

    public static long mCheckNumberValue;

    public static String mTodaysDate, mDescription, mRecommendation;

    public static PaymentType mPaymentType;

    private String signatureData;
    private String signatureName;

    private boolean isPartialPayment;

    private boolean closeParent;

	/* For split payments */
    // private double totalCost;
    // public static double remainingCost;
    // public static List<NameValuePair> paymentInformation;

    /*
     * Primary Email Dialog
     */
    private Dialog dialogPrimaryEmail;
    private TextView dialogPrimaryEmailButtonSave,
            dialogPrimaryEmailButtonCancel;
    private EditText dialogPrimaryEmailEditTextEmail;

    /* The amount they have paid this session */
    public static BigDecimal paidAmount = new BigDecimal("0.00");

    @Override
    public void onStop() {

        super.onStop();
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.layout_paymentoptions_view);

        headerLayout = (LinearLayout) findViewById(R.id.activity_header);

        isPartialPayment = getIntent()
                .getBooleanExtra("partial_payment", false);

        mActivity = ActivityPaymentOptionTypesView.this;
        mContext = this;

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

        headerButtonBack.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        buttonBillLater = (ImageView) findViewById(R.id.activity_payment_options_button_bill_later);
        buttonCash = (ImageView) findViewById(R.id.activity_payment_options_button_cash);
        buttonCheck = (ImageView) findViewById(R.id.activity_payment_options_button_check);
        buttonCreditCard = (ImageView) findViewById(R.id.activity_payment_options_button_credit_card);
        buttonPrepaid = (ImageView) findViewById(R.id.activity_payment_options_button_prepaid);
        buttonDebit = (ImageView) findViewById(R.id.activity_payment_options_button_debit);

        mAllowBillLaterBox = (CheckBox) findViewById(R.id.activity_payment_options_bill_later);

        edittextCheckNumber = (EditText) findViewById(R.id.activity_payment_options_edittext_check_number);

        // TODO - After querying for invoice
        // This needs to know how much has previously been paid

        if (AppDataSingleton.getInstance().getInvoice().getSignature() != null)
            signatureData = AppDataSingleton.getInstance().getInvoice().getSignature();

        if (AppDataSingleton.getInstance().getInvoice().getSigner() != null)
            signatureName = AppDataSingleton.getInstance().getInvoice().getSigner();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mPaymentType = null;

        if (!CommonUtilities.isNetworkAvailable(mContext)) {
            Toast.makeText(mContext, "Network connection unavailable.",
                    Toast.LENGTH_SHORT).show();
            finish();
        } else {
            new GetInvoiceTask().execute();
        }
    }

    private OnClickListener mButtonListener = new OnClickListener() {

        @Override
        public void onClick(View v) {

            boolean showDialog = false;

            switch (v.getId()) {
                case R.id.activity_payment_options_button_bill_later:
                    mPaymentType = PaymentType.BILL_LATER;
                    showDialog = true;
                    break;
                case R.id.activity_payment_options_button_cash:
                    mPaymentType = PaymentType.CASH;
                    showDialog = true;
                    break;
                case R.id.activity_payment_options_button_check:
                    if (!TextUtils.isEmpty(edittextCheckNumber.getText())) {

                        Log.d(DEBUG_TAG, "Check Number Value: "
                                + edittextCheckNumber.getText());
                        mPaymentType = PaymentType.CHECK;

                        mCheckNumberValue = Long.parseLong(edittextCheckNumber
                                .getText().toString());
                        showDialog = true;
                    } else {
                        Toast.makeText(mContext,
                                "Please input a valid check number.",
                                Toast.LENGTH_LONG).show();
                        showDialog = false;
                        return; // Quits this function
                    }
                    break;
                case R.id.activity_payment_options_button_credit_card:
                    mPaymentType = PaymentType.CREDIT_CARD;
                    showDialog = true;
                    break;
                case R.id.activity_payment_options_button_prepaid:
                    mPaymentType = PaymentType.PRE_PAID;
                    showDialog = true;
                    break;
                case R.id.activity_payment_options_button_debit:
                    mPaymentType = PaymentType.DEBIT;
                    showDialog = true;
                    break;
                default:
                    // Nothing
                    break;
            }

            if (showDialog) {
                final DialogSplitPayment splitPaymentDialog = new DialogSplitPayment(mContext);
                splitPaymentDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

                    @Override
                    public void onDismiss(DialogInterface dialog) {

                        closeParent = true;

                        if (splitPaymentDialog.isCanceled)
                            return;

                        if (!CommonUtilities.isNetworkAvailable(mContext)) {
                            Toast.makeText(mContext, "Network connection unavailable.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // This means that we've finished
                        //check on epsilon is dirty hack, will be fixed when new server version will be released
                        if (paidAmount.subtract(AppDataSingleton.getInstance().getInvoice().getTotal()).abs().compareTo(
                                EPSILON) <= 0 || isPartialPayment
                                || mPaymentType == PaymentType.CREDIT_CARD) {
                            // if (paidAmount == CommonUtilities.invoice
                            // .getTotal()) {

                            if (!isPartialPayment && mPaymentType == PaymentType.CREDIT_CARD) {
                                closeParent = false;
                            }

                            checkEmailAdreesToSend();
                        }

                    }

                    private void checkEmailAdreesToSend() {
                        Invoice invoice = AppDataSingleton.getInstance().getInvoice();
                        if (TextUtils.isEmpty(invoice.getCustomerEmail()) && TextUtils.isEmpty(invoice.getLocationEmail())) {
                            Toast.makeText(mContext, "Customer does not have an email associated to receive digital invoice.", Toast.LENGTH_LONG).show();
                            showPrimaryEmailDialog();
                        } else {
                            new SubmitPaymentsTask(closeParent).execute();
                        }
                    }
                });

                splitPaymentDialog.show();
            }
        }
    };

    private OnClickListener mPrimaryEmailDialogListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.dialog_primary_email_button_save:
                    if (!TextUtils.isEmpty(dialogPrimaryEmailEditTextEmail.getText())) {
                        dialogPrimaryEmail.dismiss();
                        AppDataSingleton.getInstance().getInvoice().setCustomerEmail(
                                dialogPrimaryEmailEditTextEmail.getText()
                                        .toString());
                        if (!CommonUtilities.isNetworkAvailable(mContext)) {
                            Toast.makeText(mContext, "Network connection unavailable.",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        new SubmitPaymentsTask(closeParent).execute();
                    } else {
                        Toast.makeText(mContext, "Email cannot be blank",
                                Toast.LENGTH_LONG).show();
                    }
                    break;
                case R.id.dialog_primary_email_button_cancel:
                    dialogPrimaryEmail.dismiss();
                    new SubmitPaymentsTask(closeParent).execute();
                    break;
                default:
                    // Nothing
                    break;
            }

        }
    };

    private void setupUI() {

        boolean billLater = AppDataSingleton.getInstance().getInvoice().getAllowBillLater();
        mAllowBillLaterBox.setChecked(billLater);

        // This will stop the keyboard from automatically popping up (I hope)
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        if (UserUtilitiesSingleton.getInstance().user.isUseOnlyBillLater()) {
            if (!CommonUtilities.isNetworkAvailable(mContext)) {
                Toast.makeText(mContext, "Network connection unavailable.",
                        Toast.LENGTH_SHORT).show();
            } else {
                new SubmitPaymentsTask(false).execute(); // Immediately
            }
            // send
            // it
            // off
        } else {
            buttonCash.setVisibility(View.VISIBLE);
            buttonCheck.setVisibility(View.VISIBLE);
            edittextCheckNumber.setVisibility(View.VISIBLE);
            setupPrepaid();

            if (!AppDataSingleton.getInstance().getInvoice().getAcceptCreditCards())
                buttonCreditCard.setVisibility(View.GONE);
            else
                buttonCreditCard.setVisibility(View.VISIBLE);

        }

        if (!billLater)
            buttonBillLater.setVisibility(View.GONE);
        else
            buttonBillLater.setVisibility(View.VISIBLE);

        if (UserUtilitiesSingleton.getInstance().user.isUseOnlyBillLater()) {
            buttonBillLater.setVisibility(View.VISIBLE);
            buttonCash.setVisibility(View.GONE);
            buttonCheck.setVisibility(View.GONE);
            edittextCheckNumber.setVisibility(View.GONE);
            buttonCreditCard.setVisibility(View.GONE);
            buttonPrepaid.setVisibility(View.GONE);
            buttonDebit.setVisibility(View.GONE);
        }


        buttonBillLater.setOnClickListener(mButtonListener);
        buttonCash.setOnClickListener(mButtonListener);
        buttonCheck.setOnClickListener(mButtonListener);
        buttonCreditCard.setOnClickListener(mButtonListener);
        buttonPrepaid.setOnClickListener(mButtonListener);
        buttonDebit.setOnClickListener(mButtonListener);

        // To avoid problems
        if (paidAmount.equals(AppDataSingleton.getInstance().getInvoice().getTotal())) {
            // if (paidAmount == AppDataSingleton.getInstance().getInvoice().getTotal()) {
            mPaymentType = PaymentType.BILL_LATER;
            if (!CommonUtilities.isNetworkAvailable(mContext)) {
                Toast.makeText(mContext, "Network connection unavailable.",
                        Toast.LENGTH_SHORT).show();
            } else {
                new SubmitPaymentsTask(false).execute();
            }
        }
    }

    private void setupPrepaid() {
        if (AppDataSingleton.getInstance().getInvoice().isAllowPrepaid())
            buttonPrepaid.setVisibility(View.VISIBLE);
        else
            buttonPrepaid.setVisibility(View.GONE);
    }

    private void sendPayment() throws NonfatalException {

        boolean isValid = false;

        switch (mPaymentType) {
            case BILL_LATER:
                isValid = true;
                break;
            case CASH:
                isValid = true;
                break;
            case CHECK:
                if (mCheckNumberValue != 0) {
                    isValid = true;
                }
                break;
            case CREDIT_CARD:
                isValid = false;
                break;
            case PRE_PAID:
                isValid = true;
                break;
            case DEBIT:
                isValid = true;
                break;
            default:
                // Nothing
                break;
        }

        if (isValid) {
            SendInvoiceTask.updateAndClose(isPartialPayment);
        }
    }

    private void showPrimaryEmailDialog() {
        dialogPrimaryEmail = new Dialog(mContext);
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

    private BigDecimal calculateLineItemTotal() {

        // double result = 0.0;
        BigDecimal result = new BigDecimal("0.00");
        result.setScale(2, RoundingMode.HALF_UP);
        final List<LineItem> lineItems = AppDataSingleton.getInstance().getInvoice().getLineItems();

        for (int i = 0; i < lineItems.size(); i++) {
            final LineItem lineItem = AppDataSingleton.getInstance().getInvoice().getLineItems().get(i);
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

        return result;
    }


    private void calculateTotal() {

        BigDecimal runningTotal = new BigDecimal("0.00");
        BigDecimal runningTaxable = new BigDecimal("0.00");
        BigDecimal finalTotal = new BigDecimal("0.00");

        runningTotal = calculateLineItemTotal();

        if (runningTotal.compareTo(new BigDecimal("0.00")) == 1) {
            AppDataSingleton.getInstance().getInvoice().getTaxes().clear();
            runningTaxable = new TaxAmountCalculator().calculateTaxAmount(AppDataSingleton.getInstance().getInvoice());
        } else {
            AppDataSingleton.getInstance().getInvoice().setDiscount(new BigDecimal("0.00").setScale(2,
                    BigDecimal.ROUND_HALF_UP));
        }

        AppDataSingleton.getInstance().getInvoice().setNetTotal(runningTotal.setScale(2,
                BigDecimal.ROUND_HALF_UP));


        if (UserUtilitiesSingleton.getInstance().user.getCountryInfo().isUseExtendedTax()) {
            List<TaxValue> taxesForInvoice = AppDataSingleton.getInstance().getInvoice().getTaxes();
            List<TaxRateType> taxRateTypesForBusiness = UserUtilitiesSingleton.getInstance().user.getCountryInfo().getTaxRateTypes();
            StringBuilder taxTitleBuilder = new StringBuilder();
            StringBuilder taxValueBuilder = new StringBuilder();

            NumberFormatTool currencyFormatTool = NumberFormatTool.getCurrencyFormat();

            if (taxesForInvoice.size() > taxRateTypesForBusiness.size()) {
                for (TaxValue val : taxesForInvoice) {
                    taxTitleBuilder.append(val.getTaxRate().getName() + "\n");
                    taxValueBuilder.append(currencyFormatTool.format(val.getValue()) + "\n");
                }
            } else {
                for (TaxValue val : taxesForInvoice) {
                    taxTitleBuilder.append(val.getTaxRate().getName() + "\n");
                    taxValueBuilder.append(currencyFormatTool.format(val.getValue()) + "\n");
                }
            }

        }


        finalTotal = runningTotal.add(runningTaxable).subtract(
                AppDataSingleton.getInstance().getInvoice().getDiscount());
        BigDecimal finalTotalScaled = finalTotal.setScale(2, BigDecimal.ROUND_HALF_UP);
        AppDataSingleton.getInstance().getInvoice().setTotal(finalTotalScaled);
    }

    private final class SubmitPaymentsTask extends BaseUiReportTask<String> {
        SubmitPaymentsTask(boolean isClosed) {
            super(ActivityPaymentOptionTypesView.this,
                    mPaymentType == PaymentType.CREDIT_CARD
                            ? R.string.async_task_string_preparing_card_reader
                            : R.string.async_task_string_submitting_payment);
            setAutocloseOnSuccess(isClosed);
        }

        @Override
        protected void onSuccess() {
            if (mPaymentType == PaymentType.CREDIT_CARD) {
                // Disable more button pushes
                buttonBillLater.setOnClickListener(null);
                buttonCash.setOnClickListener(null);
                buttonCheck.setOnClickListener(null);
                buttonCreditCard.setOnClickListener(null);
                buttonPrepaid.setOnClickListener(null);

                Intent i = null;
                if (AppDataSingleton.getInstance().getInvoice().isAcceptAmericanExpress() == false
                        && AppDataSingleton.getInstance().getInvoice().isAcceptDiscover() == false
                        && AppDataSingleton.getInstance().getInvoice().isAcceptMasterCard() == false
                        && AppDataSingleton.getInstance().getInvoice().isAcceptVisa() == false)
                    i = new Intent(mContext,
                            ActivityPaymentCreditCardInputView.class);
                else
                    i = new Intent(mContext,
                            ActivityPaymentCreditCardTypesView.class);

                startActivity(i);

            } else {
                Toast.makeText(mContext, R.string.payment_accepted, Toast.LENGTH_SHORT).show();


                if (!isPartialPayment)
                    new CloseApptTask().execute();

                if (isPartialPayment) {
                    onBackPressed();
                } else {
                    Intent i = new Intent(mActivity, ActivityDashboardView.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    mActivity.startActivity(i);
                }

            }

            String email = AppDataSingleton.getInstance().getInvoice().getCustomerEmail();
            if (AppDataSingleton.getInstance().getInvoice().getCustomer().email != null)
                if (!AppDataSingleton.getInstance().getInvoice().getCustomer().email.isEmpty())
                    email = AppDataSingleton.getInstance().getInvoice().getCustomer().email.get(0);

            if (!email.isEmpty()) {
                Toast.makeText(mContext, R.string.customer_emailed, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected boolean taskBody(final String... args) throws Exception {
            sendPayment();
            return true;
        }
    }

    private final class GetInvoiceTask extends BaseUiReportTask<String> {

        GetInvoiceTask() {
            super(ActivityPaymentOptionTypesView.this,
                    R.string.async_task_string_loading_invoice);
        }

        @Override
        protected void onSuccess() {
            calculateTotal();

            paidAmount.setScale(2, BigDecimal.ROUND_HALF_UP);
            paidAmount = AppDataSingleton.getInstance().getInvoice().getTotal()
                    .subtract(AppDataSingleton.getInstance().getInvoice().getAmountDue());

            if (signatureData != null)
                AppDataSingleton.getInstance().getInvoice().setSignature(signatureData);

            if (signatureName != null)
                AppDataSingleton.getInstance().getInvoice().setSigner(signatureName);
            setupUI();
        }

        @Override
        protected boolean taskBody(final String... args) throws Exception {
            RESTInvoice.query(AppDataSingleton.getInstance().getInvoice().getId());
            return true;
        }
    }


    private final class CloseApptTask extends BaseUiReportTask<String> {

        CloseApptTask() {
            super(ActivityPaymentOptionTypesView.this,
                    "Closing Appointment...");
        }

        @Override
        protected boolean taskBody(String... params) throws Exception {
            DateFormat df = null;
            df = new SimpleDateFormat("M/d/yy h:mm a");
            Date todaysDate = new Date();// get current date time with Date()
            String currentDateTime = df.format(todaysDate).replace("am", "AM").replace("pm", "PM");
            ;

            Appointment appt = AppDataSingleton.getInstance().getAppointment();

            if (appt.getStatus() == com.skeds.android.phone.business.Utilities.General.ClassObjects.Status.CLOSE_APPOINTMENT)
                RESTAppointment.statusUpdate(appt.getId(),
                        appt.getStatus().toString(), UserUtilitiesSingleton.getInstance().user.getServiceProviderId(),
                        Double.parseDouble(appt.getLocationLatitude()), Double.parseDouble(appt.getLocationLongitude())
                        , Double.parseDouble(appt.getLocationAccuracy()), appt.getWorkOrderNumber(),
                        currentDateTime, true, TimeZone.getDefault());
            return true;
        }

        @Override
        protected void onSuccess() {
            super.onSuccess();
            try {
                onBackPressed();
            } catch (IllegalStateException ex) {
                ex.printStackTrace();
            }

        }
    }

}