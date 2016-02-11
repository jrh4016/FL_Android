package com.skeds.android.phone.business.activities;

import android.support.v4.app.Fragment;

import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.ui.fragment.CustomerPastAppointmentFragment;

public class CustomerPastAppointmentActivity extends BaseHeaderFragmentActivity {

    @Override
    protected Fragment instantiateFragment() {
        return CustomerPastAppointmentFragment.newInstance(AppDataSingleton.getInstance().getPastAppointment());
    }
}