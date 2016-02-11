package com.skeds.android.phone.business.AsyncTasks;

import android.app.Activity;

import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.REST.Objects.RESTInvoice;

public class SendInvoiceEmailToCustomerTask extends BaseUiReportTask<String> {

    public SendInvoiceEmailToCustomerTask(Activity activity) {
        super(activity, "Sending Invoice as Email...");
    }

    @Override
    protected boolean taskBody(final String... args) throws Exception {
        RESTInvoice.sendToCustomer(AppDataSingleton.getInstance().getInvoice().getId(), "");
        return true;
    }
}