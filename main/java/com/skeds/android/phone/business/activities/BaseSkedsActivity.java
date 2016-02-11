package com.skeds.android.phone.business.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;

import com.google.analytics.tracking.android.EasyTracker;
import com.skeds.android.phone.business.AsyncTasks.FlushTask;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.StatusBuffer;
import com.skeds.android.phone.business.Utilities.General.CommonUtilities;
import com.skeds.android.phone.business.Utilities.General.Constants;
import com.skeds.android.phone.business.Utilities.General.UserUtilitiesSingleton;
import com.skeds.android.phone.business.core.SkedsApplication;

public class BaseSkedsActivity extends FragmentActivity {

    private static Activity mActivity;

    protected Class loginClass;

    @Override
    public void onStart() {
        super.onStart();
        mActivity = BaseSkedsActivity.this;

        if (SkedsApplication.getInstance().getApplicationMode() == Constants.APPLICATION_MODE_PHONE_SERVICE)
            loginClass = ActivityLoginMobile.class;
        else loginClass = ActivityLoginTablet.class;

        EasyTracker.getInstance().activityStart(this);

        if (!UserUtilitiesSingleton.getInstance().user.isLoggedIn()) {
            if (!(this instanceof ActivityLoginMobile || this instanceof ActivityLoginTablet)) {
                Intent i = new Intent(this, ActivityLoginMobile.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            }
        }

        if (CommonUtilities.isNetworkAvailable(mActivity)) {
            if (StatusBuffer.instance().haveQueue())
                new FlushTask(this).execute();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        if (!(this instanceof ActivityLoginMobile || this instanceof ActivitySettingsView || this instanceof ActivityLoginTablet))
            SkedsApplication.getInstance().saveAppAndUserDataIntoFile();
    }

    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance().activityStop(this);
    }

    protected static void reloadActivity() {

        Intent intent = mActivity.getIntent();
        mActivity.overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        mActivity.finish();

        mActivity.overridePendingTransition(0, 0);
        mActivity.startActivity(intent);
    }
}
