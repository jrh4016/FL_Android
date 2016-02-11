package com.skeds.android.phone.business.C2DMUtilities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.skeds.android.phone.business.activities.ActivityAppointmentCommentsView;
import com.skeds.android.phone.business.activities.ActivityAppointmentDualFragment;
import com.skeds.android.phone.business.ui.fragment.AppointmentFragment;

/*
 * This class just handles what happens when someone clicks on a C2DM notification. Basically I don't want to pre-query a job
 * if they're already viewing one, because it will destroy pertinent data for the view they're using at the time. This will just grab
 * the job Id, and then redirect them from there
 */

public class C2DMRedirect extends Activity {

    public static int appointmentId;
    public static int viewType;

    public static final int VIEW_TYPE_APPOINTMENT = 0;
    public static final int VIEW_TYPE_COMMENT = 1;

    private static boolean xmlSuccessful = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        // new GetAppointmentTask(C2DMRedirect.this).execute();

        Intent i = null;
        switch (viewType) {
            case VIEW_TYPE_APPOINTMENT:
                i = new Intent(C2DMRedirect.this, ActivityAppointmentDualFragment.class);
                i.putExtra(AppointmentFragment.APPOINTMENT_ID, appointmentId);
                break;

            case VIEW_TYPE_COMMENT:
                i = new Intent(C2DMRedirect.this,
                        ActivityAppointmentCommentsView.class);
                break;
            default:
                // Nothing
                break;
        }

        i.putExtra("fromClass", "ViewDashboard.class");
        startActivity(i);
        finish();
    }
}