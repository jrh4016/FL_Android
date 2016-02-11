package com.skeds.android.phone.business.AsyncTasks;

import android.app.Activity;
import android.content.Intent;

import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Invoice;
import com.skeds.android.phone.business.Utilities.General.Constants;
import com.skeds.android.phone.business.Utilities.General.UserUtilitiesSingleton;
import com.skeds.android.phone.business.Utilities.NonfatalException;
import com.skeds.android.phone.business.Utilities.REST.Objects.RESTAppointment;
import com.skeds.android.phone.business.Utilities.REST.Objects.RESTInvoice;
import com.skeds.android.phone.business.activities.ActivityDashboardView;
import com.skeds.android.phone.business.ui.fragment.InvoiceListFragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class SendInvoiceTask extends BaseUiReportTask<String> {

    public static double mFromAppointmentLatitude, mFromAppointmentLongitude,
            mFromAppointmentAccuracy;

    private Activity mActivity;

    public SendInvoiceTask(Activity parent) {
        super(parent, R.string.async_task_string_updating_invoice);

        mActivity = parent;
    }

    @Override
    protected void onFailed() {
        super.onFailed();
    }

    @Override
    protected void onSuccess() {
        Intent i = new Intent(mActivity, ActivityDashboardView.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mActivity.startActivity(i);
        AppDataSingleton.getInstance().setInvoice(new Invoice());
    }

    @Override
    protected boolean taskBody(final String... args) throws Exception {
        updateAndClose(false);
        return true;
    }

    public static void updateAndClose(boolean isPartialPayment) throws NonfatalException {
        RESTInvoice.update(AppDataSingleton.getInstance().getInvoice());
        if (isPartialPayment)
            return;
        DateFormat df = null;
        df = new SimpleDateFormat("M/d/yy h:mm a");
        Date todaysDate = new Date();// get current date time with
        // Date()
        String currentDateTime = df.format(todaysDate);

        if (AppDataSingleton.getInstance().getInvoiceViewMode() == Constants.INVOICE_VIEW_FROM_APPOINTMENT)
            RESTAppointment.close(AppDataSingleton.getInstance().getAppointment().getId(),
                    UserUtilitiesSingleton.getInstance().user.getServiceProviderId(),
                    String.valueOf(mFromAppointmentLatitude),
                    String.valueOf(mFromAppointmentLongitude),
                    String.valueOf(mFromAppointmentAccuracy), currentDateTime
            , TimeZone.getDefault());
        else {
            if (AppDataSingleton.getInstance().getInvoiceList().isEmpty())
                return;
            RESTAppointment.close(
                    AppDataSingleton.getInstance().getInvoiceList()
                            .get(InvoiceListFragment.mSelectedItem)
                            .getAppointmentId(),
                    UserUtilitiesSingleton.getInstance().user.getServiceProviderId(), "", "", "",
                    currentDateTime,
                    TimeZone.getDefault());
        }
    }
}
