package com.skeds.android.phone.business.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.skeds.android.phone.business.R;

public class DialogEstimateOrInvoice extends Dialog {

    TextView headerText;
    TextView bodyText;
    TextView buttonInvoice;
    TextView buttonEstimate;

    public static int transferTo;
    public static final int TRANSFER_TO_INVOICE = 1;
    public static final int TRANSFER_TO_ESTIMATE = 2;

    public DialogEstimateOrInvoice(Context context) {
        super(context);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_layout_yes_no_response);

        headerText = (TextView) findViewById(R.id.dialog_yes_no_response_textview_title);
        bodyText = (TextView) findViewById(R.id.dialog_yes_no_response_textview_body);
        buttonInvoice = (TextView) findViewById(R.id.dialog_yes_no_response_button_yes);
        buttonEstimate = (TextView) findViewById(R.id.dialog_yes_no_response_button_no);

        buttonInvoice.setText("Invoice");
        buttonEstimate.setText("Estimate");

        buttonInvoice.setOnClickListener(new invoiceButtonListener());
        buttonEstimate.setOnClickListener(new estimateButtonListener());

        headerText.setText("FieldLocate");
        bodyText.setText("Please select one of the following options");
    }

    private class invoiceButtonListener implements
            android.view.View.OnClickListener {

        @Override
        public void onClick(View v) {

            transferTo = TRANSFER_TO_INVOICE;

            // Stop the dialog
            DialogEstimateOrInvoice.this.dismiss();
        }
    }

    ;

    private class estimateButtonListener implements
            android.view.View.OnClickListener {

        @Override
        public void onClick(View v) {

            transferTo = TRANSFER_TO_ESTIMATE;

            // Stop the dialog
            DialogEstimateOrInvoice.this.dismiss();
        }
    }

    ;
}