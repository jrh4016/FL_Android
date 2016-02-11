package com.skeds.android.phone.business.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.skeds.android.phone.business.R;

public class DialogLocationScanAndAdd extends Dialog {

    Activity activity;

    TextView headerText;
    TextView bodyText;
    TextView buttonYes;
    TextView buttonNo;

    public boolean updatingBarcode = false;
    public boolean marryLocation = false;

    public DialogLocationScanAndAdd(Context context, Activity act) {
        super(context);
        activity = act;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_layout_yes_no_response);

        headerText = (TextView) findViewById(R.id.dialog_yes_no_response_textview_title);
        bodyText = (TextView) findViewById(R.id.dialog_yes_no_response_textview_body);
        buttonYes = (TextView) findViewById(R.id.dialog_yes_no_response_button_yes);
        buttonNo = (TextView) findViewById(R.id.dialog_yes_no_response_button_no);

        buttonYes.setText(getContext().getResources().getString(
                R.string.button_string_yes));
        buttonNo.setText(getContext().getResources().getString(
                R.string.button_string_no));

        buttonYes.setOnClickListener(new yesButtonListener());
        buttonNo.setOnClickListener(new noButtonListener());

        headerText.setText(getContext().getResources().getString(
                R.string.dialog_header_string_associate_location));

        if (updatingBarcode)
            bodyText.setText(getContext().getResources().getString(
                    R.string.dialog_body_string_pair_new_barcode_to_location));
        else
            bodyText.setText(getContext().getResources().getString(
                    R.string.dialog_body_string_pair_blank_barcode_to_location));
    }

    private class yesButtonListener implements
            android.view.View.OnClickListener {

        @Override
        public void onClick(View v) {

            marryLocation = true;
            // Stop the dialog
            DialogLocationScanAndAdd.this.dismiss();
        }
    }

    ;

    private class noButtonListener implements android.view.View.OnClickListener {

        @Override
        public void onClick(View v) {

            marryLocation = false;

            // Stop the dialog
            DialogLocationScanAndAdd.this.dismiss();
        }
    }

    ;
}