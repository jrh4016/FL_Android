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

public class DialogAgreementPaymentTypes extends Dialog {

    private String[] paymentList;

    private String[] linedUpPaymentTypes;

    public DialogAgreementPaymentTypes(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.d_service_agreement_payment_types);
        paymentList = getContext().getResources().getStringArray(R.array.agreement_payment_types);

        ListView paymentsList = (ListView) findViewById(R.id.payment_types_list);
        paymentsList.setItemsCanFocus(false);
        paymentsList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        paymentsList.setAdapter(new ArrayAdapter<String>(getContext(), R.layout.li_agreement_status, R.id.checked_text, paymentList));
        paymentsList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AppDataSingleton.getInstance().getServiceAgreement().setPaymentType(linedUpPaymentTypes[position]);
                DialogAgreementPaymentTypes.this.dismiss();
            }
        });

        String selectedPayment = AppDataSingleton.getInstance().getServiceAgreement().getPaymentType();
        linedUpPaymentTypes = getContext().getResources().getStringArray(R.array.agreement_payment_types_lined_up);
        for (int i = 0; i < linedUpPaymentTypes.length; i++)
            if (linedUpPaymentTypes[i].equals(selectedPayment)) {
                paymentsList.setItemChecked(i, true);
                break;
            }
    }

}