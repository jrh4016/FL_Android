package com.skeds.android.phone.business.data.loader;

import android.content.Context;

import com.skeds.android.phone.business.Utilities.Logger;
import com.skeds.android.phone.business.Utilities.REST.RestConnector;
import com.skeds.android.phone.business.core.async.BaseAsyncTaskLoader;
import com.skeds.android.phone.business.data.xml.CustomerEquipmentHandler;
import com.skeds.android.phone.business.data.xml.api.ParserCallback;
import com.skeds.android.phone.business.model.CustomerEquipment;

import java.io.IOException;

public class CustomerEquipmentLoader extends BaseAsyncTaskLoader<CustomerEquipment> {
    private final String mUrl;
    private CustomerEquipment mEquipment;

    public CustomerEquipmentLoader(final Context context, final String url) {
        super(context);
        mUrl = url;
    }

    @Override
    public CustomerEquipment loadInBackground() {
        try {
            RestConnector.getInstance().httpGet(mUrl, new CustomerEquipmentHandler(new ParserCallback<CustomerEquipment>() {
                @Override
                public void onStart() throws IOException {
                    // NO-OP
                }

                @Override
                public void onChildObject(CustomerEquipment object) throws IOException {
                    mEquipment = object;
                }

                @Override
                public void onFinish() throws IOException {
                    // NO-OP
                }
            }));
        } catch (Exception e) {
            Logger.err(e.getMessage());
            mLoadException = e;
        }
        return mEquipment;
    }
}