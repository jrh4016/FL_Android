package com.skeds.android.phone.business.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.skeds.android.phone.business.AsyncTasks.BaseUiReportTask;
import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.REST.Objects.RESTCustomer;

public class DialogPhoneNumberAddEdit extends Dialog {

    private Context context;

    public int editItemNumber;
    public boolean editMode = false;

    LinearLayout dialogTypeLayout;

    EditText edittextDescription;
    EditText edittextValue;
    TextView textValueLabel;
    Spinner spinnerType;

    TextView buttonSave;
    TextView buttonCancel;

    public DialogPhoneNumberAddEdit(Context context) {
        super(context);

        this.context = context;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_layout_phones);

        dialogTypeLayout = (LinearLayout) findViewById(R.id.dialog_item_and_description_linearlayout_type);
        dialogTypeLayout.setVisibility(View.VISIBLE);

        buttonSave = (TextView) findViewById(R.id.dialog_item_and_description_button_save);
        buttonCancel = (TextView) findViewById(R.id.dialog_item_and_description_button_cancel);

        edittextDescription = (EditText) findViewById(R.id.dialog_item_and_description_edittext_description);
        edittextValue = (EditText) findViewById(R.id.dialog_item_and_description_edittext_value);
        textValueLabel = (TextView) findViewById(R.id.dialog_item_and_description_textview_value);
        spinnerType = (Spinner) findViewById(R.id.dialog_item_and_description_spinner_type);

        textValueLabel.setText("Phone Number:");
        buttonSave.setOnClickListener(new saveButtonListener());
        buttonCancel.setOnClickListener(new cancelButtonListener());

        if (editMode) {
            if (AppDataSingleton.getInstance().getCustomer().phone.get(editItemNumber) != null)
                edittextValue.setText(AppDataSingleton.getInstance().getCustomer().phone.get(
                        editItemNumber).toString());

            if (AppDataSingleton.getInstance().getCustomer().phoneDescription.get(editItemNumber) != null)
                edittextDescription
                        .setText(AppDataSingleton.getInstance().getCustomer().phoneDescription.get(
                                editItemNumber).toString());
            String type = AppDataSingleton.getInstance().getCustomer().phoneType.get(editItemNumber);
            if (type != null) {
                String[] typesArray = context.getResources().getStringArray(R.array.phone_types);
                for (int i = 0; i < typesArray.length; i++) {
                    if (typesArray[i].toLowerCase().equals(type.toLowerCase())) {
                        spinnerType.setSelection(i);
                        break;
                    }
                }
            }

        }
    }

    private class saveButtonListener
            implements
            android.view.View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (edittextValue.getText().toString().replaceAll("\\D+", "").length() < 10) {
                Toast.makeText(context, "Phone Number Should Contain 10 Digits", Toast.LENGTH_SHORT).show();
                return;
            }
            new SubmitPhoneEmailTask().execute();

        }
    }

    ;

    private class cancelButtonListener
            implements
            android.view.View.OnClickListener {

        @Override
        public void onClick(View v) {

            DialogPhoneNumberAddEdit.this.dismiss();
        }
    }

    ;

    private class SubmitPhoneEmailTask extends BaseUiReportTask<String> {
        SubmitPhoneEmailTask() {
            super(getOwnerActivity(), editMode
                    ? "Updating phone number..."
                    : "Add new phone number...");
        }

        @Override
        protected void onSuccess() {
            if (editMode) {
                AppDataSingleton.getInstance().getCustomer().phone.set(editItemNumber, edittextValue
                        .getText().toString());
                AppDataSingleton.getInstance().getCustomer().phoneDescription.set(editItemNumber,
                        edittextDescription.getText().toString());
                AppDataSingleton.getInstance().getCustomer().phoneType.set(editItemNumber,
                        spinnerType.getSelectedItem().toString().toUpperCase());
            } else {
                AppDataSingleton.getInstance().getCustomer().phone.add(edittextValue.getText()
                        .toString());
                AppDataSingleton.getInstance().getCustomer().phoneDescription.add(edittextDescription
                        .getText().toString());
                AppDataSingleton.getInstance().getCustomer().phoneType.add(spinnerType
                        .getSelectedItem().toString().toUpperCase());
            }
            DialogPhoneNumberAddEdit.this.dismiss();
        }

        @Override
        protected boolean taskBody(final String... args) throws Exception {
            if (editMode)
                // new to the phone/email id list
                RESTCustomer.updatePhoneNumber(AppDataSingleton.getInstance().getCustomer().phoneId
                                .get(editItemNumber), edittextValue.getText()
                                .toString().replaceAll("\\D+", ""), edittextDescription.getText().toString(),
                        spinnerType.getSelectedItem().toString().toUpperCase());
            else
                RESTCustomer.addPhoneNumber(AppDataSingleton.getInstance().getCustomer().getId(),
                        edittextValue.getText().toString().replaceAll("\\D+", ""), edittextDescription
                                .getText().toString(), spinnerType
                                .getSelectedItem().toString().toUpperCase());

            return true;
        }
    }
}