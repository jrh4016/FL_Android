package com.skeds.android.phone.business.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.skeds.android.phone.business.AsyncTasks.BaseUiReportTask;
import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Generic;
import com.skeds.android.phone.business.Utilities.REST.Objects.RESTAgreement;
import com.skeds.android.phone.business.activities.ActivityServiceAgreementAddEdit;

import java.util.ArrayList;
import java.util.List;

public class DialogServiceAgreementSelect extends Dialog {

    private final Context context;

    public int selectedAgreementId;
    public boolean submitOnComplete;

    private TextView textSelectedAgreement;

    public DialogServiceAgreementSelect(Context context) {
        super(context);
        this.context = context;
        selectedAgreementId = 0;
        submitOnComplete = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_layout_select_service_agreement_for_appointment);

        textSelectedAgreement = (TextView) findViewById(R.id.dialog_list_textview_selected);

        (findViewById(R.id.dialog_select_agreement_for_appointment_button_new_agreement)).setOnClickListener(buttonListener);
        (findViewById(R.id.dialog_list_button_save)).setOnClickListener(buttonListener);
        (findViewById(R.id.dialog_list_button_cancel)).setOnClickListener(buttonListener);

        List<String> servicePlans = new ArrayList<String>();

        for (int i = 0; i < AppDataSingleton.getInstance().getServiceAgreementList().size(); i++)
            servicePlans.add(AppDataSingleton.getInstance().getServiceAgreementList().get(i)
                    .getServicePlanName());

        initList(servicePlans);
    }

    private void initList(List<String> servicePlans) {
        ListView listAgreements;
        listAgreements = (ListView) findViewById(R.id.dialog_list_listview);
        listAgreements.setAdapter(new ArrayAdapter<String>(
                getContext(), android.R.layout.simple_spinner_dropdown_item, android.R.id.text1,
                servicePlans));
        listAgreements.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position,
                                    long id) {
                checkByLocationId(id);
            }
        });
    }

    private View.OnClickListener buttonListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.dialog_select_agreement_for_appointment_button_new_agreement:
                    submitOnComplete = false;
                    DialogServiceAgreementSelect.this.dismiss();
                    Intent i = new Intent(context, ActivityServiceAgreementAddEdit.class);
                    context.startActivity(i);
                    break;
                case R.id.dialog_list_button_save:
                    if (selectedAgreementId != 0)
                        submitOnComplete = true;
                    else
                        submitOnComplete = false;

                    DialogServiceAgreementSelect.this.dismiss();
                    break;
                case R.id.dialog_list_button_cancel:
                    submitOnComplete = false;
                    DialogServiceAgreementSelect.this.dismiss();
                    break;
                default:
                    break;
            }
        }
    };

    private void checkByLocationId(long id) {
        new GetSingleAgreementTask((int) id).execute();
    }

    private class GetSingleAgreementTask extends BaseUiReportTask<String> {

        private final int agreementPosition;

        protected void onSuccess() {
            List<Generic> agreementLocationsList =
                    AppDataSingleton.getInstance().getServiceAgreement().locationAndEquipment;

            boolean isAllowedLocation = false;
            int apptLocationId = AppDataSingleton.getInstance().getAppointment().getLocationId();
            for (Generic agreementLoc : agreementLocationsList) {
                if (apptLocationId == agreementLoc.getId()) {
                    isAllowedLocation = true;
                    break;
                }
            }
            verifyLocationForAgreement(isAllowedLocation);
        }

        private void verifyLocationForAgreement(boolean isAllowedLocation) {
            if (isAllowedLocation) {
                textSelectedAgreement.setText(AppDataSingleton.getInstance().getServiceAgreementList()
                        .get(agreementPosition).getServicePlanName());

                selectedAgreementId = AppDataSingleton.getInstance().getServiceAgreementList()
                        .get(agreementPosition).getId();
            } else {
                Toast.makeText(context, R.string.agreement_not_covered, Toast.LENGTH_SHORT).show();
            }
        }

        ;

        GetSingleAgreementTask(int agreementPosition) {
            super((Activity) context,
                    R.string.checking);
            this.agreementPosition = agreementPosition;
        }

        @Override
        protected boolean taskBody(final String... args) throws Exception {
            RESTAgreement.query(AppDataSingleton.getInstance().getServiceAgreementList()
                    .get(agreementPosition).getId());
            return true;
        }
    }
}