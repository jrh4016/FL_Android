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

public class DialogAgreementTypes extends Dialog {

    Spinner serviceAgreements;

    TextView buttonSave;
    TextView buttonCancel;

    public DialogAgreementTypes(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_layout_service_agreement_number_of_systems);

        buttonSave = (TextView) findViewById(R.id.dialog_service_agreement_agreement_button_save);
        buttonCancel = (TextView) findViewById(R.id.dialog_service_agreement_agreement_button_cancel);
        serviceAgreements = (Spinner) findViewById(R.id.dialog_service_agreement_agreement_number_of_systems_spinner);

        buttonSave.setOnClickListener(new saveButtonListener());
        buttonCancel.setOnClickListener(new cancelButtonListener());

        setupUI();
    }

    private class saveButtonListener
            implements
            android.view.View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (AppDataSingleton.getInstance().getServicePlanList()
                    .get(serviceAgreements.getSelectedItemPosition())==null) return;
            AppDataSingleton.getInstance().getServiceAgreement().setServicePlanId(
                    AppDataSingleton.getInstance().getServicePlanList()
                            .get(serviceAgreements.getSelectedItemPosition())
                            .getId());
            AppDataSingleton.getInstance().getServiceAgreement().setServicePlanName(
                    AppDataSingleton.getInstance().getServicePlanList()
                            .get(serviceAgreements.getSelectedItemPosition())
                            .getName());
            DialogAgreementTypes.this.dismiss();
        }
    }

    ;

    private class cancelButtonListener
            implements
            android.view.View.OnClickListener {

        @Override
        public void onClick(View v) {
            // ViewAppointmentTrackable.submitOnDialogClose = false;
            // ViewAppointment.submitOnDialogClose = false;
            DialogAgreementTypes.this.dismiss();
        }
    }

    ;

    private void setupUI() {

        List<String> servicePlans = new ArrayList<String>();

        for (int i = 0; i < AppDataSingleton.getInstance().getServicePlanList().size(); i++)
            servicePlans.add(AppDataSingleton.getInstance().getServicePlanList().get(i).getName());

        serviceAgreements.setAdapter(null);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                getContext(), android.R.layout.simple_spinner_dropdown_item,
                servicePlans);
        serviceAgreements.setAdapter(arrayAdapter);
    }

}