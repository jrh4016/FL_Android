package com.skeds.android.phone.business.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;

import java.util.ArrayList;
import java.util.List;

public class DialogEmailSelectForSendInvoice extends Dialog {

    public boolean submitOnComplete;

    LinearLayout listCustomerEmails;
    EditText edittextCustomEmail;

    RadioButton radioStandard;
    RadioButton radioCustom;

    TextView buttonSubmit;
    TextView buttonCancel;

    Context context;

    public String emailToUse;

    public DialogEmailSelectForSendInvoice(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_layout_select_email_for_send_invoice);

        emailToUse = "";

        listCustomerEmails = (LinearLayout) findViewById(R.id.dialog_select_email_for_send_invoice_linearlayout_emails);
        edittextCustomEmail = (EditText) findViewById(R.id.dialog_select_email_for_send_invoice_edittext_custom_email);
        radioStandard = (RadioButton) findViewById(R.id.dialog_select_email_for_send_invoice_radiobutton_standard);
        radioCustom = (RadioButton) findViewById(R.id.dialog_select_email_for_send_invoice_radiobutton_custom);

        buttonSubmit = (TextView) findViewById(R.id.dialog_select_email_for_send_invoice_button_submit);
        buttonCancel = (TextView) findViewById(R.id.dialog_select_email_for_send_invoice_button_cancel);

        radioStandard.setOnCheckedChangeListener(new standardRadioListener());
        radioCustom.setOnCheckedChangeListener(new customRadioListener());

        buttonSubmit.setOnClickListener(new submitButtonListener());
        buttonCancel.setOnClickListener(new cancelButtonListener());

        setupEmailList();
    }

    private void setupEmailList() {
        radioStandard.setVisibility(View.VISIBLE);
        listCustomerEmails.setVisibility(View.VISIBLE);
        listCustomerEmails.removeAllViews();
        listCustomerEmails.setBackgroundColor(android.R.color.transparent);

        List<View> row = new ArrayList<View>();
        row.clear();
        try {
            if (!AppDataSingleton.getInstance().getInvoice().getCustomer().email.isEmpty()) {
                for (int i = 0; i < AppDataSingleton.getInstance().getInvoice().getCustomer().email.size(); i++) {
                    row.add(new View(context));
                    View thisRow = row.get(i);
                    thisRow = getLayoutInflater().inflate(
                            R.layout.row_equipment_item, null);

                    TextView emailAddress = (TextView) thisRow
                            .findViewById(R.id.equipmentListItem);
                    emailAddress.setText(AppDataSingleton.getInstance().getInvoice().getCustomer().email
                            .get(i));
                    thisRow.setTag(AppDataSingleton.getInstance().getInvoice().getCustomer().email.get(i));

                    row.set(i, thisRow);
                    row.get(i).setOnClickListener(new addressListListener());
                    listCustomerEmails.addView(row.get(i));
                }
            } else {
                radioStandard.setVisibility(View.GONE);
                listCustomerEmails.setVisibility(View.GONE);
                radioCustom.setChecked(true);
                radioStandard.setChecked(false);
                edittextCustomEmail.setEnabled(true);
            }
        } catch (Exception e) {
            radioStandard.setVisibility(View.GONE);
            listCustomerEmails.setVisibility(View.GONE);
            radioCustom.setChecked(true);
            radioStandard.setChecked(false);
            edittextCustomEmail.setEnabled(true);
        }
    }

    private class addressListListener implements
            android.view.View.OnClickListener {

        @Override
        public void onClick(View v) {
            emailToUse = v.getTag().toString();
        }
    }

    ;

    private class standardRadioListener implements
            android.widget.CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {

            if (isChecked) {
                listCustomerEmails.setEnabled(true);
                edittextCustomEmail.setEnabled(false);
                radioCustom.setChecked(false);
            } else {
                listCustomerEmails.setEnabled(false);
                edittextCustomEmail.setEnabled(true);
                radioCustom.setChecked(true);
            }
        }
    }

    private class customRadioListener implements
            android.widget.CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {

            if (isChecked) {
                listCustomerEmails.setEnabled(false);
                edittextCustomEmail.setEnabled(true);
                radioStandard.setChecked(false);
            } else {
                listCustomerEmails.setEnabled(true);
                edittextCustomEmail.setEnabled(false);
                radioStandard.setChecked(true);
            }
        }
    }

    private class submitButtonListener implements
            android.view.View.OnClickListener {

        @Override
        public void onClick(View v) {

            if (radioStandard.isChecked()) {
                submitOnComplete = true;
                DialogEmailSelectForSendInvoice.this.dismiss();
            } else {
                if (TextUtils.isEmpty(edittextCustomEmail.getText())) {
                    Toast.makeText(context,
                            "You must enter a valid email address",
                            Toast.LENGTH_LONG).show();
                } else {
                    emailToUse = edittextCustomEmail.getText().toString();
                    submitOnComplete = true;
                    DialogEmailSelectForSendInvoice.this.dismiss();
                }
            }
        }
    }

    ;

    private class cancelButtonListener implements
            android.view.View.OnClickListener {

        @Override
        public void onClick(View v) {

            submitOnComplete = false;
            DialogEmailSelectForSendInvoice.this.dismiss();
        }
    }

    ;
}