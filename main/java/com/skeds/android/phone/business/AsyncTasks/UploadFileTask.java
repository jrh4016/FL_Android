package com.skeds.android.phone.business.AsyncTasks;

import android.app.Activity;
import android.widget.Toast;

import com.skeds.android.phone.business.Utilities.REST.Objects.RESTCustomer;

public class UploadFileTask extends BaseUiReportTask<String> {
    public static String fileName;
    public static String fileDisplayName;
    public static String fileExtension;
    public static String encodedFileData;
    public static int customerId;
    public static int appointmentId;
    public static int invoiceId;

    private Activity mActivity;

    public UploadFileTask(Activity activity) {
        super(activity, "Uploading file...");
        mActivity = activity;
    }

    @Override
    protected boolean taskBody(final String... args) throws Exception {
        RESTCustomer.attachDocument(customerId, encodedFileData, invoiceId, appointmentId,
                fileName, fileDisplayName, fileExtension);
        return true;
    }

    @Override
    protected void onSuccess() {
        super.onSuccess();

        Toast.makeText(mActivity, "File uploaded", Toast.LENGTH_SHORT).show();
    }

}