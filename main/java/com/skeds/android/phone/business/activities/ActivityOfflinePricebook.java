package com.skeds.android.phone.business.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.skeds.android.phone.business.Custom.AccountMenu;
import com.skeds.android.phone.business.Custom.CallBackListener;
import com.skeds.android.phone.business.Custom.QuickAction;
import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.REST.CallBackManager;
import com.skeds.android.phone.business.Utilities.REST.ServiceHelper;

public class ActivityOfflinePricebook extends BaseSkedsActivity implements CallBackListener {

    private LinearLayout headerLayout;
    private ImageView headerButtonUser;
    private ImageView headerButtonBack;

    private CallBackManager callbackManager;

    private ServiceHelper helper;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.pricebook_container);
        callbackManager = CallBackManager.get();
        helper = new ServiceHelper(this);

        initHeader();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (callbackManager != null)
            callbackManager.registerListener(this);
        helper.downloadPricebook();

    }


    @Override
    protected void onPause() {
        super.onPause();
        if (callbackManager != null)
            callbackManager.unregisterListener(this);
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

    @Override
    public void onSuccess(String action) {
        if ("get_pricebook".equals(action)) {
            Log.e("PRICEBOOK", "");
            showNotification();

        }
    }

    private void showNotification() {
    }

    @Override
    public void onFailed(String action) {
        // TODO Auto-generated method stub

    }

}
