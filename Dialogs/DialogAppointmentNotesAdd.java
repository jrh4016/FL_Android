package com.skeds.android.phone.business.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.ui.fragment.AppointmentFragment;

public class DialogAppointmentNotesAdd extends Dialog {

    EditText notes;

    TextView buttonSave;
    TextView buttonCancel;

    public DialogAppointmentNotesAdd(Context context) {
        super(context);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_layout_add_notes_to_appointment);

        buttonSave = (TextView) findViewById(R.id.dialog_add_notes_to_appointment_button_save);
        buttonCancel = (TextView) findViewById(R.id.dialog_add_notes_to_appointment_button_cancel);
        notes = (EditText) findViewById(R.id.dialog_add_notes_to_appointment_edittext_notes);

        buttonSave.setOnClickListener(new saveButtonListener());
        buttonCancel.setOnClickListener(new cancelButtonListener());

        notes.setText(AppDataSingleton.getInstance().getAppointment().getNotes());
        AppointmentFragment.submitOnDialogClose = false;
    }

    private class saveButtonListener implements
            android.view.View.OnClickListener {

        @Override
        public void onClick(View v) {

            AppointmentFragment.addedNotes = notes.getText().toString();
            AppointmentFragment.submitOnDialogClose = true;
            // ViewAppointment.addedNotes = notes.getText().toString();
            // ViewAppointment.submitOnDialogClose = true;
            DialogAppointmentNotesAdd.this.dismiss();
        }
    }

    ;

    private class cancelButtonListener implements
            android.view.View.OnClickListener {

        @Override
        public void onClick(View v) {
            // ViewAppointmentTrackable.submitOnDialogClose = false;
            DialogAppointmentNotesAdd.this.dismiss();
        }
    }

    ;
}