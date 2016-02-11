package com.skeds.android.phone.business.activities;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.skeds.android.phone.business.Custom.AccountMenu;
import com.skeds.android.phone.business.Custom.QuickAction;
import com.skeds.android.phone.business.R;

public class AppointmentEquipmentAddActivity extends BaseSkedsActivity {

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);

        setContentView(R.layout.layout_appt_equipment_add_container);
        initHeader();
    }

    private void initHeader() {
        final QuickAction accountMenu = AccountMenu.setupMenu(this, this);

        final LinearLayout headerLayout = (LinearLayout) findViewById(R.id.activity_header);
        if (headerLayout != null) {
            headerLayout.findViewById(R.id.header_button_user).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    accountMenu.show(v);
                    accountMenu.setAnimStyle(QuickAction.ANIM_GROW_FROM_CENTER);
                }
            });
            headerLayout
                    .findViewById(R.id.header_button_back).setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
    }
}
