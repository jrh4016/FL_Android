package com.skeds.android.phone.business.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.skeds.android.phone.business.Custom.AccountMenu;
import com.skeds.android.phone.business.Custom.QuickAction;
import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.ui.fragment.PartOrderApptListFragment;

public class ActivityPartOrdersApptListViewFragment extends BaseSkedsActivity {

    private LinearLayout headerLayout;
    private ImageView headerButtonUser;
    private ImageView headerButtonBack;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);

        setContentView(R.layout.layout_partorder_appt_container);
        initHeader();

        Fragment fragment = new PartOrderApptListFragment();
        fragment.setArguments(getIntent().getExtras());

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.add(R.id.partorder_fragment_container, fragment);

        transaction.commit();
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
}
