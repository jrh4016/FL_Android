package com.skeds.android.phone.business.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;

public class DialogAgreementStatus extends Dialog {


    private String[] statusList;

    private String[] linedUpStatuses;

    public DialogAgreementStatus(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.d_service_agreement_statuses);
        statusList = getContext().getResources().getStringArray(R.array.agreement_statuses);

        ListView agreemList = (ListView) findViewById(R.id.statuses_list);
        agreemList.setItemsCanFocus(false);
        agreemList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        agreemList.setAdapter(new ArrayAdapter<String>(getContext(), R.layout.li_agreement_status, R.id.checked_text, statusList));
        agreemList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AppDataSingleton.getInstance().getServiceAgreement().setStatus(linedUpStatuses[position]);
                DialogAgreementStatus.this.dismiss();
            }
        });

        String selectedStatus = AppDataSingleton.getInstance().getServiceAgreement().getStatus();
        linedUpStatuses = getContext().getResources().getStringArray(R.array.agreement_statuses_lined_up);
        for (int i = 0; i < linedUpStatuses.length; i++)
            if (linedUpStatuses[i].equals(selectedStatus)) {
                agreemList.setItemChecked(i, true);
                break;
            }
    }

}