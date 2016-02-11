package com.skeds.android.phone.business.AsyncTasks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.REST.Objects.RESTPhotoList;
import com.skeds.android.phone.business.activities.ActivityPhotoListViewFragment;
import com.skeds.android.phone.business.ui.fragment.PhotoListViewFragment;

public class ViewGalleryTask extends BaseUiReportTask<String> {

    private Context context;

    private long apptId;

    private long equipId;

    public ViewGalleryTask(Activity parent, long apptId, long equipId) {
        super(parent, R.string.async_task_string_loading_photos);

        context = parent;
        this.apptId = apptId;
        this.equipId = equipId;

        //Reset previously saved photos (old data)
        AppDataSingleton.getInstance().getPhotoList().clear();
    }

    @Override
    protected void onSuccess() {
        PhotoListViewFragment.returnToView = PhotoListViewFragment.VIEW_FROM_APPOINTMENT;
        context.startActivity(new Intent(context, ActivityPhotoListViewFragment.class));
    }

    @Override
    protected boolean taskBody(final String... args) throws Exception {
        RESTPhotoList.query(apptId, equipId);
        return true;
    }
}
