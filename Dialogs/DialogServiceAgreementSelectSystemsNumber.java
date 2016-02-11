package com.skeds.android.phone.business.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;

import java.util.ArrayList;
import java.util.List;

public class DialogServiceAgreementSelectSystemsNumber extends Dialog {

    Spinner numberOfSystems;

    TextView buttonSave;
    TextView buttonCancel;

    public DialogServiceAgreementSelectSystemsNumber(Context context) {
        super(context);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_layout_service_agreement_number_of_systems);

        buttonSave = (TextView) findViewById(R.id.dialog_service_agreement_agreement_button_save);
        buttonCancel = (TextView) findViewById(R.id.dialog_service_agreement_agreement_button_cancel);
        numberOfSystems = (Spinner) findViewById(R.id.dialog_service_agreement_agreement_number_of_systems_spinner);

        buttonSave.setOnClickListener(new saveButtonListener());
        buttonCancel.setOnClickListener(new cancelButtonListener());

        setupUI();
    }

    private class saveButtonListener implements
            android.view.View.OnClickListener {

        @Override
        public void onClick(View v) {
            AppDataSingleton.getInstance().getServiceAgreement().setNumberOfSystems(
                    (int) numberOfSystems.getSelectedItemId());
            DialogServiceAgreementSelectSystemsNumber.this.dismiss();
        }
    }

    ;

    private class cancelButtonListener implements
            android.view.View.OnClickListener {

        @Override
        public void onClick(View v) {
            // ViewAppointmentTrackable.submitOnDialogClose = false;
            // ViewAppointment.submitOnDialogClose = false;
            DialogServiceAgreementSelectSystemsNumber.this.dismiss();
        }
    }

    ;

    private void setupUI() {

        List<String> systems = new ArrayList<String>();

        for (int i = 0; i < 26; i++)
            systems.add(String.valueOf(i));

        numberOfSystems.setAdapter(null);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                getContext(), android.R.layout.simple_spinner_dropdown_item,
                systems);
        numberOfSystems.setAdapter(arrayAdapter);
    }
}