package com.skeds.android.phone.business.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;

public class DialogSettingsAddIssue extends Dialog {

    private EditText notes;

    private TextView buttonSave;
    private TextView buttonCancel;

    private boolean isAccepted;

    public DialogSettingsAddIssue(Context context) {
        super(context);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_layout_add_issue_to_report);

        buttonSave = (TextView) findViewById(R.id.dialog_add_notes_to_appointment_button_save);
        buttonCancel = (TextView) findViewById(R.id.dialog_add_notes_to_appointment_button_cancel);
        notes = (EditText) findViewById(R.id.dialog_add_notes_to_appointment_edittext_notes);

        buttonSave.setOnClickListener(new saveButtonListener());
        buttonCancel.setOnClickListener(new cancelButtonListener());

    }

    public boolean isAcceptedToReport() {
        return isAccepted;
    }

    private class saveButtonListener implements
            android.view.View.OnClickListener {

        @Override
        public void onClick(View v) {

            if (!TextUtils.isEmpty(notes.getText())) {
                AppDataSingleton.getInstance().setErrorDescription(notes.getText().toString());
                isAccepted = true;
                DialogSettingsAddIssue.this.dismiss();
            } else {
                Toast.makeText(getContext(),
                        "Please enter notes first.",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    ;

    private class cancelButtonListener implements
            android.view.View.OnClickListener {

        @Override
        public void onClick(View v) {
            AppDataSingleton.getInstance().setErrorDescription("");
            isAccepted = false;
            DialogSettingsAddIssue.this.dismiss();
        }
    }

    ;
}