package com.skeds.android.phone.business.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.skeds.android.phone.business.Custom.AccountMenu;
import com.skeds.android.phone.business.Custom.QuickAction;
import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Manufacturer;

import java.util.List;

public class ActivityManufacturerList extends BaseSkedsActivity {

    private LinearLayout headerLayout;
    private ImageView headerButtonUser;
    private ImageView headerButtonBack;

    private ListView manufList;

    private Activity mActivity;

    private Dialog dialog;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);

        setContentView(R.layout.layout_manufacturer_list);
        initHeader();
        manufList = (ListView) findViewById(R.id.activity_manufacturer_list_view);
        mActivity = this;

        manufList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        manufList.setAdapter(new Adapter(mActivity));
        manufList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (position == AppDataSingleton.getInstance()
                        .getEquipmentManufacturerList().size() - 1) {
                    showManufacturerDialog();
                } else {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("selected_manufacturer", position);
                    setResult(RESULT_OK, returnIntent);
                    finish();
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (dialog != null)
            if (dialog.isShowing())
                dialog.dismiss();
    }

    private void initHeader() {
        final QuickAction accountMenu;
        accountMenu = AccountMenu.setupMenu(this, this);

        headerLayout = (LinearLayout) findViewById(R.id.activity_header);
        if (headerLayout != null) {
            headerButtonUser = (ImageView) headerLayout
                    .findViewById(R.id.header_button_user);
            headerButtonBack = (ImageView) headerLayout
                    .findViewById(R.id.header_button_back);

            headerButtonUser.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    accountMenu.show(v);
                    accountMenu.setAnimStyle(QuickAction.ANIM_GROW_FROM_CENTER);
                }
            });

            headerButtonBack.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

    }

    private void showManufacturerDialog() {
        dialog = new Dialog(mActivity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.dialog_new_manufacturer);
        final TextView confirmBtn = (TextView) dialog.findViewById(R.id.dialog_manufacturer_button_confirm);
        final TextView cancelBtn = (TextView) dialog.findViewById(R.id.dialog_manufacturer_button_cancel);
        final EditText text = (EditText) dialog.findViewById(R.id.dialog_manufacturer_field);

        confirmBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Manufacturer m = new Manufacturer();
                m.setId(-1);
                m.setName(text.getText().toString());

                AppDataSingleton.getInstance().getEquipmentManufacturerList().add(AppDataSingleton
                        .getInstance().getEquipmentManufacturerList().size() - 1, m);

                manufList.setAdapter(new Adapter(mActivity));

                dialog.dismiss();
            }
        });

        cancelBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private class Adapter extends ArrayAdapter<Manufacturer> {

        private List<Manufacturer> manufacturers = AppDataSingleton.getInstance().getEquipmentManufacturerList();

        public Adapter(Context context) {
            super(context, R.layout.row_manufacturer, AppDataSingleton.getInstance().getEquipmentManufacturerList());
            notifyDataSetChanged();
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = mActivity.getLayoutInflater();
            View row;

            if (position == manufacturers.size() - 1) {
                row = inflater.inflate(R.layout.row_new_manufacturer, parent,
                        false);
            } else {
                row = inflater.inflate(
                        R.layout.row_manufacturer,
                        parent, false);

                TextView name = (TextView) row
                        .findViewById(R.id.on_truck_added_name);

                name.setText(manufacturers.get(position).getName());
            }

            return row;
        }

    }

}
