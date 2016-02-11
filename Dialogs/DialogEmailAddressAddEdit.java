package com.skeds.android.phone.business.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.skeds.android.phone.business.AsyncTasks.BaseUiReportTask;
import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.REST.Objects.RESTCustomer;

public class DialogEmailAddressAddEdit extends Dialog {

    public static int editItemNumber;
    public static boolean editMode = false;

    LinearLayout typeLayout;

    EditText edittextDescription;
    EditText edittextValue;
    TextView textviewValueLabel;

    TextView buttonSave;
    TextView buttonCancel;

    public DialogEmailAddressAddEdit(Context context) {
        super(context);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_layout_item_and_description);

        setTitle("Email");
        typeLayout = (LinearLayout) findViewById(R.id.dialog_item_and_description_linearlayout_type);
        typeLayout.setVisibility(View.GONE);

        buttonCancel = (TextView) findViewById(R.id.dialog_item_and_description_button_cancel);
        buttonSave = (TextView) findViewById(R.id.dialog_item_and_description_button_save);

        edittextDescription = (EditText) findViewById(R.id.dialog_item_and_description_edittext_description);
        edittextValue = (EditText) findViewById(R.id.dialog_item_and_description_edittext_value);
        textviewValueLabel = (TextView) findViewById(R.id.dialog_item_and_description_textview_value);

        textviewValueLabel.setText("Email Address:");

        buttonSave.setOnClickListener(new saveButtonListener());
        buttonCancel.setOnClickListener(new cancelButtonListener());

        if (editMode) {
            if (AppDataSingleton.getInstance().getCustomer().email.get(editItemNumber) != null)
                edittextValue.setText(AppDataSingleton.getInstance().getCustomer().email.get(
                        editItemNumber).toString());

            if (AppDataSingleton.getInstance().getCustomer().emailDescription.get(editItemNumber) != null)
                edittextDescription
                        .setText(AppDataSingleton.getInstance().getCustomer().emailDescription.get(
                                editItemNumber).toString());
        }
    }

    private class saveButtonListener
            implements
            android.view.View.OnClickListener {

        @Override
        public void onClick(View v) {

            if (TextUtils.isEmpty(edittextValue.getText())) {
                Toast.makeText(getContext(), "email address required",
                        Toast.LENGTH_LONG).show();
            } else {
                new SubmitEmailTask().execute();
            }
        }
    }

    ;

    private class cancelButtonListener
            implements
            android.view.View.OnClickListener {

        @Override
        public void onClick(View v) {

            DialogEmailAddressAddEdit.this.dismiss();
        }
    }

    ;

    private class SubmitEmailTask extends BaseUiReportTask<String> {
        SubmitEmailTask() {
            super(getOwnerActivity(), editMode
                    ? "Updating Email Address..."
                    : "Sending New Email Address...");
        }

        @Override
        protected void onSuccess() {
            if (editMode) {
                AppDataSingleton.getInstance().getCustomer().email.set(editItemNumber, edittextValue
                        .getText().toString());
                AppDataSingleton.getInstance().getCustomer().emailDescription.set(editItemNumber,
                        edittextDescription.getText().toString());
            } else {
                AppDataSingleton.getInstance().getCustomer().email.add(edittextValue.getText()
                        .toString());
                AppDataSingleton.getInstance().getCustomer().emailDescription.add(edittextDescription
                        .getText().toString());
            }
            DialogEmailAddressAddEdit.this.dismiss();
        }

        @Override
        protected boolean taskBody(final String... args) throws Exception {
            if (editMode)
                // new to the phone/email id list
                RESTCustomer.updateEmail(AppDataSingleton.getInstance().getCustomer().emailId
                        .get(editItemNumber), edittextValue.getText()
                        .toString(), edittextDescription.getText().toString());
            else

                RESTCustomer.addEmail(AppDataSingleton.getInstance().getCustomer().getId(),
                        edittextValue.getText().toString(), edittextDescription
                                .getText().toString());
            return true;

        }
    }
}