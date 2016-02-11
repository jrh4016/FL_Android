package com.skeds.android.phone.business.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.TextView;

import com.skeds.android.phone.business.Custom.AccountMenu;
import com.skeds.android.phone.business.Custom.QuickAction;
import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Customer;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Location;
import com.skeds.android.phone.business.ui.fragment.EquipmentListFragment;

public class CustomerEquipmentActivity extends BaseSkedsActivity implements View.OnClickListener {

    public static final String EXTRA_CUSTOMER_ID = "customer_id";
    public static final String EXTRA_LOCATION_ID = "location_id";
    public static final String EXTRA_CAN_ADD_NEW_EQUIPMENT = "can_add_equipment";

    private static final String EQUIPMENT_LIST_TAG = "equipment_list_tag";

    private QuickAction mAccountMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_customer_equipment);
        initHeader();

        final Intent intent = getIntent();
        if (intent != null) {

            //todo:will refactor in future when work with customer
            final Customer customer = AppDataSingleton.getInstance().getCustomerById(intent.getIntExtra(CustomerEquipmentActivity.EXTRA_CUSTOMER_ID, -1));
            final Location location = customer.getLocationById(intent.getIntExtra(CustomerEquipmentActivity.EXTRA_LOCATION_ID, -1));

            final TextView locationLabel = (TextView) findViewById(R.id.location);
            if (location == null) {
                locationLabel.setVisibility(View.GONE);
            } else {
                locationLabel.setText(location.getAddress1());
            }

            final FragmentManager fm = getSupportFragmentManager();
            if (fm.findFragmentByTag(EQUIPMENT_LIST_TAG) == null) {
                fm.beginTransaction().add(R.id.equipment, EquipmentListFragment.newInstance(getIntent().getExtras()), EQUIPMENT_LIST_TAG).commit();
            }
        }
    }

    private void initHeader() {
        mAccountMenu = AccountMenu.setupMenu(this, this);
        View headerLayout = findViewById(R.id.activity_header);
        if (headerLayout != null) {
            headerLayout.findViewById(R.id.header_button_user).setOnClickListener(this);
            headerLayout.findViewById(R.id.header_button_back).setOnClickListener(this);
            headerLayout.findViewById(R.id.header_standard_button_right).setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.header_button_user:
                mAccountMenu.show(v);
                mAccountMenu.setAnimStyle(QuickAction.ANIM_GROW_FROM_CENTER);
                break;
            case R.id.header_button_back:
                onBackPressed();
                break;
            case R.id.header_standard_button_right:
                final Intent i = new Intent(CustomerEquipmentActivity.this, ActivityEquipmentAddEdit.class);
                i.putExtras(getIntent());
                startActivity(i);
                break;
            default:
                break;
        }
    }

}