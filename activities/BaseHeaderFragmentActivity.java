package com.skeds.android.phone.business.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.skeds.android.phone.business.Custom.AccountMenu;
import com.skeds.android.phone.business.Custom.QuickAction;
import com.skeds.android.phone.business.R;

public class BaseHeaderFragmentActivity extends BaseSkedsActivity {

    static final String FRAGMENT_TAG = "fragment_tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_base_header_fragment);

        initHeader();

        final FragmentManager fm = getSupportFragmentManager();
        if (fm.findFragmentByTag(FRAGMENT_TAG) == null) {
            fm.beginTransaction().add(R.id.fragment_container, instantiateFragment(), FRAGMENT_TAG).commit();
        }
    }

    private void initHeader() {
        final View headerLayout = findViewById(R.id.activity_header);

        final QuickAction accountMenu;
        accountMenu = AccountMenu.setupMenu(this, this);

        headerLayout.findViewById(R.id.header_button_user).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accountMenu.show(v);
                accountMenu.setAnimStyle(QuickAction.ANIM_GROW_FROM_CENTER);
            }
        });
        headerLayout
                .findViewById(R.id.header_button_back).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    protected Fragment instantiateFragment() {
        throw new IllegalArgumentException("Should be implemented in activity to instantiate the right fragment!");
    }
}
