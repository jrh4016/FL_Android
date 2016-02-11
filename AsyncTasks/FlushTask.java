package com.skeds.android.phone.business.AsyncTasks;

import android.app.Activity;

import com.skeds.android.phone.business.Utilities.General.ClassObjects.StatusBuffer;

public class FlushTask extends BaseUiReportTask<String> {

    public FlushTask(Activity parent) {
        super(parent, "Uploading Data Saved in Offline...");
    }

    @Override
    protected boolean taskBody(String... params) throws Exception {

        StatusBuffer.instance().flush();
        return true;
    }

}