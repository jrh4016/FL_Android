package com.skeds.android.phone.business.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.skeds.android.phone.business.Custom.AccountMenu;
import com.skeds.android.phone.business.Custom.QuickAction;
import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.ui.fragment.CustomerFragment;
import com.skeds.android.phone.business.ui.fragment.CustomerListFragment;
import com.skeds.android.phone.business.ui.fragment.LogoPlaceholderFragment;

public class ActivityCustomerDualFragment extends BaseSkedsActivity implements
        CustomerListFragment.OnHeadlineSelectedListener {

    private LinearLayout headerLayout;
    private ImageView headerButtonUser;
    private ImageView headerButtonBack;

    private boolean isPlaceholderView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_customer_view_container);

        initHeader();

        if (findViewById(R.id.fragment_customer_list_container) != null) {

            if (savedInstanceState != null) {
                return;
            }

            CustomerListFragment firstFragment = new CustomerListFragment();

            firstFragment.setArguments(getIntent().getExtras());

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_customer_list_container,
                            firstFragment).commit();
        } else {
            LogoPlaceholderFragment fragmentLogo = new LogoPlaceholderFragment();
            FragmentTransaction transaction = getSupportFragmentManager()
                    .beginTransaction();

            transaction.replace(R.id.fragment_customer_content_container, fragmentLogo);

            // Commit the transaction
            transaction.commit();
            isPlaceholderView = true;
        }

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            int customerId = bundle.getInt(CustomerFragment.CUSTOMER_ID);
            if (customerId != 0)
                onArticleSelected(customerId);
        }

    }

    public void onArticleSelected(int selectedId) {
        FrameLayout contentFrag = (FrameLayout) findViewById(R.id.fragment_customer_content_container);

        int containerId;
        if (contentFrag != null) {
            containerId = R.id.fragment_customer_content_container;
        } else {
            containerId = R.id.fragment_customer_list_container;
        }

        CustomerFragment newFragment = new CustomerFragment();
        Bundle args = new Bundle();
        args.putInt(CustomerFragment.CUSTOMER_ID, selectedId);
        newFragment.setArguments(args);
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();

        transaction.replace(containerId, newFragment);

        if (containerId == R.id.fragment_customer_list_container)
            transaction.addToBackStack(null);
        else if (isPlaceholderView) {
            transaction.addToBackStack(null);
            isPlaceholderView = false;
        }

        // Commit the transaction
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        isPlaceholderView = !isPlaceholderView;
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
