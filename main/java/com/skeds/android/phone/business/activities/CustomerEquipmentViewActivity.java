package com.skeds.android.phone.business.activities;

import android.content.Intent;
import android.support.v4.app.Fragment;

import com.skeds.android.phone.business.model.Equipment;
import com.skeds.android.phone.business.ui.fragment.CustomerEquipmentFragment;

public class CustomerEquipmentViewActivity extends BaseHeaderFragmentActivity {

    public static final String EXTRA_EQUIPMENT = "equipment";

    @Override
    protected Fragment instantiateFragment() {
        final Intent intent = getIntent();
        return CustomerEquipmentFragment.newInstance((Equipment) intent.getParcelableExtra(EXTRA_EQUIPMENT),
                intent.getIntExtra(CustomerEquipmentActivity.EXTRA_CUSTOMER_ID,
                        -1),
                intent.getIntExtra(CustomerEquipmentActivity.EXTRA_LOCATION_ID,
                        -1));
    }
}