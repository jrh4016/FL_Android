package com.skeds.android.phone.business.Utilities.REST;

import android.content.Context;
import android.content.Intent;

import com.skeds.android.phone.business.Services.RestIntentService;

/**
 * This class is used to execute high level API
 *
 * @author Den Oleshkevich
 */

public class ServiceHelper {

    private Context mContext;

    private Intent mServiceIntent;

    public ServiceHelper(Context context) {
        this.mContext = context.getApplicationContext();
        mServiceIntent = new Intent(mContext, RestIntentService.class);
    }

    public void downloadPricebook() {
        mServiceIntent.setAction("get_pricebook");
        mContext.startService(mServiceIntent);
    }

}
