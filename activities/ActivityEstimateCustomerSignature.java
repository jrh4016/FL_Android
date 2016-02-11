package com.skeds.android.phone.business.activities;

import android.content.Intent;
import android.graphics.PorterDuff.Mode;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.skeds.android.phone.business.Dialogs.DialogInvoiceDisclaimer;
import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.UserUtilitiesSingleton;

public class ActivityEstimateCustomerSignature extends ActivitySignatureDrawing {

    private TextView buttonResetSignature;

    private TextView buttonNext;

    private String finalSignature;

    DialogInvoiceDisclaimer disclaimerDialog;


    @Override
    protected void onResume() {
        super.onResume();

        setFinishOnTouchOutside(false);

        ((LinearLayout) findViewById(R.id.activity_header)).setVisibility(View.GONE);
        ((EditText) findViewById(R.id.activity_signature_edittext_customer_name)).setVisibility(View.GONE);

        buttonResetSignature = (TextView) findViewById(R.id.activity_signature_button_reset);
        buttonNext = (TextView) findViewById(R.id.activity_signature_button_view_disclaimer);
        buttonNext.setText("  Next  ");

        setupUI();

        if (AppDataSingleton.getInstance().isForceDisclaimer() ||
                UserUtilitiesSingleton.getInstance().user.isDisplayDisclaimer()) {
            if (disclaimerDialog != null) {
                disclaimerDialog.dismiss();
                disclaimerDialog = null;
            }
            disclaimerDialog = new DialogInvoiceDisclaimer(
                    this);
            disclaimerDialog.show();
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent returnIntent = new Intent();
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }

    ;


    private OnClickListener buttonListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.activity_signature_button_reset:
                    mCanvas.drawColor(0, Mode.CLEAR);
                    break;

			/* "Save" button */
                case R.id.activity_signature_button_view_disclaimer:
                    saveSignature();
                    AppDataSingleton.getInstance().getEstimate().setSignature(finalSignature);
                    Intent returnIntent = new Intent();
                    setResult(RESULT_OK, returnIntent);
                    finish();
                default:
                    // Nothing
                    break;
            }
        }
    };

    private void setupUI() {
        buttonNext.setOnClickListener(buttonListener);
        buttonResetSignature.setOnClickListener(buttonListener);

    }

    private void saveSignature() {
        finalSignature = getSignatureByteArray();
    }

}