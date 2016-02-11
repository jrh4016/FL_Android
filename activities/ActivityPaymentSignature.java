package com.skeds.android.phone.business.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff.Mode;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.skeds.android.phone.business.AsyncTasks.BaseUiReportTask;
import com.skeds.android.phone.business.AsyncTasks.SendInvoiceTask;
import com.skeds.android.phone.business.Custom.AccountMenu;
import com.skeds.android.phone.business.Custom.QuickAction;
import com.skeds.android.phone.business.Dialogs.DialogInvoiceDisclaimer;
import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.CommonUtilities;
import com.skeds.android.phone.business.Utilities.General.Constants;
import com.skeds.android.phone.business.Utilities.General.UserUtilitiesSingleton;
import com.skeds.android.phone.business.ui.fragment.InvoiceFragment;

public class ActivityPaymentSignature extends ActivitySignatureDrawing {

    private Activity mActivity;
    private Context mContext;

    private LinearLayout headerLayout;
    private ImageView headerButtonUser;
    private ImageView headerButtonBack;

    private TextView buttonViewDisclaimer;
    private TextView buttonResetSignature;

    private TextView buttonSave;
    private String finalSignature;

    /*
     * Primary Email Dialog
     */
    private Dialog dialogPrimaryEmail;
    private TextView dialogPrimaryEmailButtonSave,
            dialogPrimaryEmailButtonCancel;
    private EditText dialogPrimaryEmailEditTextEmail;

    /*
     * Disclaimer Dialog
     */
    private DialogInvoiceDisclaimer disclaimerDialog;


    private OnClickListener buttonListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.activity_signature_button_reset:
                    mCanvas.drawColor(0, Mode.CLEAR);
                    break;

                case R.id.activity_signature_button_view_disclaimer:
                    if (disclaimerDialog != null) {
                        disclaimerDialog.dismiss();
                        disclaimerDialog = null;
                    }
                    disclaimerDialog = new DialogInvoiceDisclaimer(
                            mContext);
                    disclaimerDialog.show();
                    break;

                case R.id.header_button_back:
                    onBackPressed();
                    break;

				/* "Save" button */
                case R.id.header_standard_button_right:
                    boolean valid = false;

                    switch (AppDataSingleton.getInstance().getSignatureViewMode()) {
                        case Constants.SIGNATURE_VIEW_FROM_ESTIMATE:
                            saveSignature();
                            // Globals.encodedImage = mFinalSignature;
                            AppDataSingleton.getInstance().getEstimate().setSignature(finalSignature);
                            switch (AppDataSingleton.getInstance().getEstimateViewType()) {
                                case Constants.ESTIMATE_VIEW_TYPE_ADD:
                                    new ActivityEstimateView.SubmitNewEstimateTask(
                                            mActivity, true).execute();
                                    break;
                                case Constants.ESTIMATE_VIEW_TYPE_VIEW_EDIT:
                                    new ActivityEstimateView.SubmitUpdatedEstimateTask(
                                            mActivity, true).execute();
                                    break;
                                default:
                                    // Nothing
                                    break;
                            }
//							finish();
                            break;
                        case Constants.SIGNATURE_VIEW_FROM_INVOICE:
                            if (UserUtilitiesSingleton.getInstance().user
                                    .isRequireSignerNameOnInvoice()) {
                                EditText customerNameField = (EditText) findViewById(R.id.activity_signature_edittext_customer_name);
                                if (!TextUtils.isEmpty(customerNameField.getText())) {
                                    valid = true;
                                    AppDataSingleton.getInstance().getInvoice().setSigner(customerNameField
                                            .getText().toString());

                                }
                            } else {
                                valid = true;
                            }

                            if (valid) {

                                saveSignature();
                                AppDataSingleton.getInstance().getInvoice().setSignature(
                                        finalSignature);

                                if (AppDataSingleton.getInstance().getCustomer().email == null) {
                                    if (AppDataSingleton.getInstance().getCustomer().email.size() == 0) {

                                        if (TextUtils.isEmpty(AppDataSingleton.getInstance().getInvoice().getCustomerEmail())) {
                                            Toast.makeText(
                                                    mContext,
                                                    "Customer does not have an email associated to receive digital invoice.",
                                                    Toast.LENGTH_LONG).show();
                                            showPrimaryEmailDialog();
                                        } else {
                                            if (!CommonUtilities.isNetworkAvailable(mContext)) {
                                                Toast.makeText(mContext, "Network connection unavailable.",
                                                        Toast.LENGTH_SHORT).show();
                                            } else
                                                new SubmitSignatureTask().execute();
                                        }
                                    }
                                } else if (!CommonUtilities.isNetworkAvailable(mContext)) {
                                    Toast.makeText(mContext, "Network connection unavailable.",
                                            Toast.LENGTH_SHORT).show();
                                } else
                                    new SubmitSignatureTask().execute();
                            } else {
                                Toast.makeText(
                                        mContext,
                                        "Please enter customer name in required field",
                                        Toast.LENGTH_LONG).show();
                            }
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
        }
    };

    private OnClickListener mPrimaryEmailDialogListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.dialog_primary_email_button_save:
                    if (!TextUtils.isEmpty(dialogPrimaryEmailEditTextEmail.getText())) {
                        AppDataSingleton.getInstance().getInvoice().setCustomerEmail(
                                dialogPrimaryEmailEditTextEmail.getText()
                                        .toString());
                        if (!CommonUtilities.isNetworkAvailable(mContext)) {
                            Toast.makeText(mContext, "Network connection unavailable.",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        new SubmitSignatureTask().execute();
                    } else {
                        Toast.makeText(mContext, "Email cannot be blank",
                                Toast.LENGTH_LONG).show();
                    }
                    break;
                case R.id.dialog_primary_email_button_cancel:
                    break;
                default:
                    // Nothing
                    break;
            }
        }
    };

    protected void onResume() {
        super.onResume();
        init();
    }

    ;


    @Override
    protected void onPause() {
        super.onPause();
        if (disclaimerDialog != null)
            disclaimerDialog.dismiss();

        if (dialogPrimaryEmail != null)
            dialogPrimaryEmail.dismiss();
    }

    private void init() {

        headerLayout = (LinearLayout) findViewById(R.id.activity_header);
        buttonSave = (TextView) headerLayout
                .findViewById(R.id.header_standard_button_right);
        headerButtonBack = (ImageView) headerLayout
                .findViewById(R.id.header_button_back);

        mActivity = this;
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

        EditText customerNameField = (EditText) findViewById(R.id.activity_signature_edittext_customer_name);
        if (UserUtilitiesSingleton.getInstance().user.isRequireSignerNameOnInvoice()
                && AppDataSingleton.getInstance().getSignatureViewMode() == Constants.SIGNATURE_VIEW_FROM_INVOICE)
            customerNameField.setVisibility(View.VISIBLE);
        else
            customerNameField.setVisibility(View.GONE);

        if (AppDataSingleton.getInstance().getSignatureViewMode() == Constants.SIGNATURE_VIEW_FROM_INVOICE) {
            String name = AppDataSingleton.getInstance().getInvoice().getCustomer().getLongName();
            customerNameField.setText(name);
        }

        buttonResetSignature = (TextView) findViewById(R.id.activity_signature_button_reset);
        buttonViewDisclaimer = (TextView) findViewById(R.id.activity_signature_button_view_disclaimer);

        buttonSave.setOnClickListener(buttonListener);
        headerButtonBack.setOnClickListener(buttonListener);
        buttonResetSignature.setOnClickListener(buttonListener);
        buttonViewDisclaimer.setOnClickListener(buttonListener);

        if (AppDataSingleton.getInstance().isForceDisclaimer() ||
                UserUtilitiesSingleton.getInstance().user.isDisplayDisclaimer()) {
            if (disclaimerDialog != null) {
                disclaimerDialog.dismiss();
                disclaimerDialog = null;
            }
            disclaimerDialog = new DialogInvoiceDisclaimer(
                    mContext);
            disclaimerDialog.show();
        }
    }

    private void saveSignature() {
        finalSignature = getSignatureByteArray();
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

    private class SubmitSignatureTask extends BaseUiReportTask<String> {
        SubmitSignatureTask() {
            super(mActivity, AppDataSingleton.getInstance().getInvoice()
                    .getDeterminePaymentType()
                    ? R.string.async_task_string_loading_payment_options
                    : R.string.async_task_string_updating_invoice);
        }

        @Override
        protected void onSuccess() {
            // If they are required to select a type of payment, they are
            // transfered up
            if (AppDataSingleton.getInstance().getInvoice().getDeterminePaymentType()) {
                Intent i = new Intent(mContext,
                        ActivityPaymentOptionTypesView.class);
                if (getIntent().getExtras() != null)
                    i.putExtras(getIntent().getExtras());
                startActivity(i);
                finish();

            } else {
                finish();
            }
        }

        @Override
        protected boolean taskBody(final String... args) throws Exception {
            // The task to execute

            if (!AppDataSingleton.getInstance().getInvoice().getDeterminePaymentType()) {
                SendInvoiceTask.updateAndClose(false);
            }

            return true;
        }
    }

}
