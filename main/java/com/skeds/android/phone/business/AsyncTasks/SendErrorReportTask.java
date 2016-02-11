/**
 *
 */
package com.skeds.android.phone.business.AsyncTasks;

import android.app.Activity;

import com.skeds.android.phone.business.Utilities.Logger;
import com.skeds.android.phone.business.Utilities.REST.Objects.RESTErrorReport;

/**
 * Send error report to server
 */
public class SendErrorReportTask extends BaseUiReportTask<String> {

    public SendErrorReportTask(Activity parent) {
        super(parent, "Sending error report...");
    }

    @Override
    protected boolean taskBody(String... params) throws Exception {
        RESTErrorReport.add(Logger.generateReport());
        return true;
    }
}
