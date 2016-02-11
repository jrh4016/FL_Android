package com.skeds.android.phone.business.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.skeds.android.phone.business.R;

public class DialogScanAndUpload extends Dialog {

    Activity activity;
    Fragment fragment;

    TextView headerText;
    TextView bodyText;
    TextView buttonInvoice;
    TextView buttonEstimate;

    public static int transferTo;
    public static final int TRANSFER_TO_INVOICE = 1;
    public static final int TRANSFER_TO_ESTIMATE = 2;

    public DialogScanAndUpload(Context context, Activity act, Fragment frag) {
        super(context);
        activity = act;
        fragment = frag;
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

        buttonInvoice.setText("(1) Scan File");
        buttonEstimate.setText("(2) Upload File");

        buttonInvoice.setOnClickListener(new invoiceButtonListener());
        buttonEstimate.setOnClickListener(new estimateButtonListener());

        headerText.setText(getContext().getResources().getString(
                R.string.dialog_header_string_scan_and_upload_file));
        bodyText.setText(getContext().getResources().getString(
                R.string.dialog_body_string_scan_and_upload_file));
    }

    private class invoiceButtonListener implements
            android.view.View.OnClickListener {

        @Override
        public void onClick(View v) {

            try {
                Intent newDocument = new Intent();
                newDocument.setComponent(new ComponentName(
                        "com.intsig.camscanner",
                        "com.intsig.camscanner.CaptureActivity"));
                if (fragment != null)
                    fragment.startActivityForResult(newDocument, 0);
                else
                    activity.startActivityForResult(newDocument, 0);
            } catch (android.content.ActivityNotFoundException ex) {
                // Potentially direct the user to the Market with a Dialog
                Toast.makeText(
                        getContext(),
                        getContext().getResources().getString(
                                R.string.app_string_camscanner_not_found),
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    ;

    private class estimateButtonListener implements
            android.view.View.OnClickListener {

        @Override
        public void onClick(View v) {

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("file/*");

            try {
                if (fragment != null)
                    fragment.startActivityForResult(
                            Intent.createChooser(intent, "Select a File to Upload"),
                            2);
                else
                    activity.startActivityForResult(
                            Intent.createChooser(intent, "Select a File to Upload"),
                            2);
            } catch (android.content.ActivityNotFoundException ex) {
                // Potentially direct the user to the Market with a Dialog
                Toast.makeText(
                        getContext(),
                        getContext().getResources().getString(
                                R.string.app_string_file_manager_not_found),
                        Toast.LENGTH_LONG).show();
            }

            // Stop the dialog
            DialogScanAndUpload.this.dismiss();
        }
    }

    ;
}