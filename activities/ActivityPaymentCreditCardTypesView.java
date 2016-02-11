package com.skeds.android.phone.business.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.skeds.android.phone.business.AsyncTasks.BaseUiReportTask;
import com.skeds.android.phone.business.AsyncTasks.SendInvoiceTask;
import com.skeds.android.phone.business.Custom.AccountMenu;
import com.skeds.android.phone.business.Custom.QuickAction;
import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Invoice;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Payment;
import com.skeds.android.phone.business.Utilities.General.CommonUtilities;
import com.skeds.android.phone.business.Utilities.General.Constants;
import com.skeds.android.phone.business.Utilities.NonfatalException;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Locale;

public class ActivityPaymentCreditCardTypesView extends BaseSkedsActivity {

    private String DEBUG_TAG = "[Credit Card Type]";

    private LinearLayout headerLayout;
    private ImageView headerButtonUser;

    private Activity mActivity;
    private Context mContext;

    private ImageView buttonAmericanExpress;
    private ImageView buttonDiscover;
    private ImageView buttonMasterCard;
    private ImageView buttonVisa;

    private EditText cardShortNumber;
    private EditText cardExpiresMonth;
    private EditText cardExpiresYear;
    private EditText cardAuthCode;

    private int cardType;

    private TextView buttonSave;

    public static final int TYPE_AMERICAN_EXPRESS = 1, TYPE_DISCOVER = 2,
            TYPE_MASTER_CARD = 3, TYPE_VISA = 4;

    /*
     * Primary Email Dialog
     */
    private Dialog dialogPrimaryEmail;
    private TextView dialogPrimaryEmailButtonSave,
            dialogPrimaryEmailButtonCancel;
    private EditText dialogPrimaryEmailEditTextEmail;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(DEBUG_TAG, "Starting to Load Layout...");
        setContentView(R.layout.layout_creditcard_options_view);

        headerLayout = (LinearLayout) findViewById(R.id.activity_header);

        mActivity = ActivityPaymentCreditCardTypesView.this;
        mContext = this;

        final QuickAction accountMenu;
        accountMenu = AccountMenu.setupMenu(mContext, mActivity);

        headerButtonUser = (ImageView) headerLayout
                .findViewById(R.id.header_button_user);

        headerButtonUser.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                accountMenu.show(v);
                accountMenu.setAnimStyle(QuickAction.ANIM_GROW_FROM_CENTER);
            }
        });

        buttonAmericanExpress = (ImageView) findViewById(R.id.activity_cc_payment_options_button_american_express);
        buttonDiscover = (ImageView) findViewById(R.id.activity_cc_payment_options_button_discover);
        buttonMasterCard = (ImageView) findViewById(R.id.activity_cc_payment_options_button_master_card);
        buttonVisa = (ImageView) findViewById(R.id.activity_cc_payment_options_button_visa);

        cardShortNumber = (EditText) findViewById(R.id.credit_card_short_number);
        cardExpiresMonth = (EditText) findViewById(R.id.activity_credit_card_payment_edittext_expires_month);
        cardExpiresYear = (EditText) findViewById(R.id.activity_credit_card_payment_edittext_expires_year);
        cardAuthCode = (EditText) findViewById(R.id.activity_credit_card_payment_edittext_auth_code);

        buttonSave = (TextView) findViewById(R.id.payment_button_save);
        buttonSave.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!CommonUtilities.isNetworkAvailable(mContext)) {
                    Toast.makeText(mContext, "Network connection unavailable.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (cardType == 0) {
                    Toast.makeText(mContext, "Please Select Card Type",
                            Toast.LENGTH_SHORT).show();
                    return;
                } else if (cardExpiresMonth.getText().toString().isEmpty()
                        || cardExpiresYear.getText().toString().length() != 4) {
                    Toast.makeText(mContext, "Please Enter Expiration Date",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (isExpired())
                    return;

                if ("CREDIT_CARD".equals(AppDataSingleton.getInstance().getInvoice().getPayments()
                        .get(AppDataSingleton.getInstance().getInvoice().getPayments()
                                .size() - 1).getPaymentType())) {
                    BigDecimal paymentAmount = AppDataSingleton.getInstance()
                            .getInvoice().getPayments().get(AppDataSingleton
                                    .getInstance().getInvoice().getPayments().size() - 1).paymentAmount;

                    AppDataSingleton.getInstance().getInvoice().getPayments()
                            .remove(AppDataSingleton.getInstance().getInvoice().getPayments()
                                    .size() - 1);

                    Payment thisPayment = new Payment();
                    thisPayment.paymentAmount = paymentAmount;
                    thisPayment.setPaymentType("CREDIT_CARD");
                    thisPayment.setCheckNumber(cardType);

                    thisPayment.setCardShortNumber(cardShortNumber.getText()
                            .toString());
                    thisPayment.setCardExpirationDate(cardExpiresMonth
                            .getText() + "/" + cardExpiresYear.getText());
                    thisPayment.setCardAuthCode(cardAuthCode.getText()
                            .toString());

                    AppDataSingleton.getInstance().getInvoice().getPayments()
                            .add(thisPayment);


                    if (ActivityPaymentOptionTypesView.paidAmount
                            .compareTo(AppDataSingleton.getInstance()
                                    .getInvoice().getTotal()) == 0)
                        new SubmitCardTask(false).execute();
                    else
                        new SubmitCardTask(true).execute();

                }
            }
        });

        setupUI();
    }

    private OnClickListener mButtonListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.activity_cc_payment_options_button_american_express:
                    // ViewInvoice.CREDIT_CARD_TYPE = 1;
                    cardType = 1;
                    Toast.makeText(mContext, "American Express Card Selected",
                            Toast.LENGTH_SHORT).show();
                    break;
                case R.id.activity_cc_payment_options_button_discover:
                    cardType = 2;
                    Toast.makeText(mContext, "Discover Card Selected",
                            Toast.LENGTH_SHORT).show();
                    // ViewInvoice.CREDIT_CARD_TYPE = 2;
                    break;
                case R.id.activity_cc_payment_options_button_master_card:
                    cardType = 3;
                    Toast.makeText(mContext, "Master Card Selected",
                            Toast.LENGTH_SHORT).show();
                    // ViewInvoice.CREDIT_CARD_TYPE = 3;
                    break;
                case R.id.activity_cc_payment_options_button_visa:
                    cardType = 4;
                    Toast.makeText(mContext, "Visa Card Selected",
                            Toast.LENGTH_SHORT).show();
                    // ViewInvoice.CREDIT_CARD_TYPE = 4;
                    break;
                default:
                    // Nothing
                    break;
            }

        }
    };

    private OnClickListener mPrimaryEmailDialogListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (!CommonUtilities.isNetworkAvailable(mContext)) {
                Toast.makeText(mContext, "Network connection unavailable.",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            switch (v.getId()) {
                case R.id.dialog_primary_email_button_save:
                case R.id.dialog_primary_email_button_cancel:
                    //TODO: dirty hack for cents here
                    if (ActivityPaymentOptionTypesView.paidAmount.subtract(
                            AppDataSingleton.getInstance().getInvoice().getTotal()).abs().compareTo(
                            ActivityPaymentOptionTypesView.EPSILON) <= 0)
                        new SubmitCardTask(false).execute();
                    else if (ActivityPaymentOptionTypesView.paidAmount
                            .compareTo(BigDecimal.ZERO) == 0) {
                        Intent i = new Intent(mContext,
                                ActivityPaymentOptionTypesView.class);
                        startActivity(i);
                        finish();
                    } else {
                        new SubmitCardTask(true).execute();
                        dialogPrimaryEmail.dismiss();
                    }

                    break;
                default:
                    // Nothing
                    break;
            }
        }
    };

    private void setupUI() {

        // This will stop the keyboard from automatically popping up (I hope)
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        if (AppDataSingleton.getInstance().getInvoice()
                .isAcceptAmericanExpress()) {
            buttonAmericanExpress.setVisibility(View.VISIBLE);
            buttonAmericanExpress.setOnClickListener(mButtonListener);
        } else
            buttonAmericanExpress.setVisibility(View.GONE);

        if (AppDataSingleton.getInstance().getInvoice().isAcceptDiscover()) {
            buttonDiscover.setVisibility(View.VISIBLE);
            buttonDiscover.setOnClickListener(mButtonListener);
        } else
            buttonDiscover.setVisibility(View.GONE);

        if (AppDataSingleton.getInstance().getInvoice().isAcceptMasterCard()) {
            buttonMasterCard.setVisibility(View.VISIBLE);
            buttonMasterCard.setOnClickListener(mButtonListener);
        } else
            buttonMasterCard.setVisibility(View.GONE);

        if (AppDataSingleton.getInstance().getInvoice().isAcceptVisa()) {
            buttonVisa.setVisibility(View.VISIBLE);
            buttonVisa.setOnClickListener(mButtonListener);
        } else
            buttonVisa.setVisibility(View.GONE);

    }

    private void sendPayment(boolean isPartialPayment) throws NonfatalException {
        SendInvoiceTask.updateAndClose(isPartialPayment);
    }

    private boolean isExpired() {

        int month = Integer.parseInt(cardExpiresMonth.getText().toString());
        int year = Integer.parseInt(cardExpiresYear.getText().toString());

        if (month > 12 || month < 1) {
            Toast.makeText(mContext, "Incorrect Month", Toast.LENGTH_SHORT).show();
            return true;
        }
        int currentYear = Calendar.getInstance(Locale.US).get(Calendar.YEAR);
        int currentMonth = Calendar.getInstance(Locale.US).get(Calendar.MONTH) + 1;

        if (year < 2000) {
            Toast.makeText(mContext, "Credit Card is Expired", Toast.LENGTH_SHORT).show();
            return true;
        }

        if (year < currentYear) {
            Toast.makeText(mContext, "Credit Card is Expired", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (year == currentYear)
            if (month < currentMonth) {
                Toast.makeText(mContext, "Credit Card is Expired", Toast.LENGTH_SHORT).show();
                return true;
            }

        return false;
    }

    private final class SubmitCardTask extends BaseUiReportTask<String> {

        private boolean isPartialPayment;

        SubmitCardTask(boolean isPartial) {
            super(ActivityPaymentCreditCardTypesView.this,
                    R.string.async_task_string_submitting_payment);
            isPartialPayment = isPartial;

        }

        @Override
        protected void onSuccess() {
            Toast.makeText(mContext, "Payment Accepted", Toast.LENGTH_SHORT)
                    .show();

            if (!isPartialPayment) {
                Intent i = new Intent(mActivity, ActivityDashboardView.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mActivity.startActivity(i);
                AppDataSingleton.getInstance().setInvoice(new Invoice());
            } else
                finish();
        }

        @Override
        protected boolean taskBody(final String... args) throws Exception {
            sendPayment(isPartialPayment);
            return true;
        }
    }
}