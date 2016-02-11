package com.skeds.android.phone.business.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Generic;
import com.skeds.android.phone.business.activities.ActivityEstimateView;

import java.util.ArrayList;
import java.util.List;

public class DialogAgreementList extends Dialog {

    private ListView list;
    private List<Generic> agreements;

    private Context context;
    private boolean refreshData = false;

    public DialogAgreementList(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_agreements);

        list = (ListView) findViewById(R.id.agreements_listview);

        Generic noneItem = new Generic();
        noneItem.setName("<none>");
        noneItem.setId(0);

        agreements = new ArrayList<Generic>();
        agreements.add(noneItem);
        agreements.addAll(AppDataSingleton.getInstance().getServicePlanList());

        list.setAdapter(new Adapter(context));
        list.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AppDataSingleton.getInstance()
                        .getEstimate().setServicePlanUsedForPricing(true);
                AppDataSingleton.getInstance().getEstimate().setServicePlanId(agreements.get(position).getId());
                AppDataSingleton.getInstance().getEstimate().setServicePlanName(agreements.get(position).getName());
                refreshData = true;
                ActivityEstimateView.hasAnyChanges = true;
                DialogAgreementList.this.dismiss();
            }
        });
    }

    public boolean refreshData() {
        return refreshData;
    }

    private class Adapter extends ArrayAdapter<Generic> {

        public Adapter(Context context) {
            super(context, R.layout.row_agreement, agreements);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View v = convertView;

            if (v == null) {
                LayoutInflater vi = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.row_agreement, null);
            }

            ((TextView) v.findViewById(R.id.agreements_row_name)).setText(agreements.get(position).getName());
            v.setTag(agreements.get(position).getId());

            return v;
        }

    }

}