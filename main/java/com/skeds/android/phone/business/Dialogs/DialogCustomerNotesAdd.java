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
import com.skeds.android.phone.business.ui.fragment.CustomerFragment;

public class DialogCustomerNotesAdd extends Dialog {

    EditText notes;

    TextView buttonSave;
    TextView buttonCancel;

    public DialogCustomerNotesAdd(Context context) {
        super(context);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_layout_add_notes_to_customer);

        buttonSave = (TextView) findViewById(R.id.dialog_add_notes_to_customer_button_save);
        buttonCancel = (TextView) findViewById(R.id.dialog_add_notes_to_customer_button_cancel);
        notes = (EditText) findViewById(R.id.dialog_add_notes_to_customer_edittext_notes);

        buttonSave.setOnClickListener(new saveButtonListener());
        buttonCancel.setOnClickListener(new cancelButtonListener());

    }

    private class saveButtonListener implements
            android.view.View.OnClickListener {

        @Override
        public void onClick(View v) {

            if (!TextUtils.isEmpty(notes.getText())) {
                CustomerFragment.addedNotes = notes.getText().toString();
                CustomerFragment.submitOnDialogClose = true;
                DialogCustomerNotesAdd.this.dismiss();
            } else {
                Toast.makeText(getContext(),
                        "Please enter customer notes first.", Toast.LENGTH_LONG)
                        .show();
            }
        }
    }

    ;

    private class cancelButtonListener implements
            android.view.View.OnClickListener {

        @Override
        public void onClick(View v) {
            CustomerFragment.submitOnDialogClose = false;
            DialogCustomerNotesAdd.this.dismiss();
        }
    }

    ;
}