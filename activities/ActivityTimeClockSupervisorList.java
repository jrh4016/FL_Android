package com.skeds.android.phone.business.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.skeds.android.phone.business.AsyncTasks.BaseUiReportTask;
import com.skeds.android.phone.business.Custom.AccountMenu;
import com.skeds.android.phone.business.Custom.QuickAction;
import com.skeds.android.phone.business.Custom.TimeClockTechsAdapter;
import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.General.CommonUtilities;
import com.skeds.android.phone.business.Utilities.General.UserUtilitiesSingleton;
import com.skeds.android.phone.business.Utilities.REST.Objects.RESTHoursWorkedList;
import com.skeds.android.phone.business.Utilities.REST.Objects.RESTTimeclockList;

public class ActivityTimeClockSupervisorList extends BaseSkedsActivity {

    private ListView techsList;
    private TimeClockTechsAdapter technicianAdapter;

    private LinearLayout headerLayout;
    private ImageView headerButtonUser;
    private ImageView headerButtonBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_timeclock_supervisor_list);
        headerLayout = (LinearLayout) findViewById(R.id.activity_header);
        final QuickAction accountMenu;
        accountMenu = AccountMenu.setupMenu(
                ActivityTimeClockSupervisorList.this,
                ActivityTimeClockSupervisorList.this);

        headerButtonUser = (ImageView) headerLayout
                .findViewById(R.id.header_button_user);
        headerButtonBack = (ImageView) headerLayout
                .findViewById(R.id.header_button_back);

        headerButtonUser.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                accountMenu.show(v);
                accountMenu.setAnimStyle(QuickAction.ANIM_GROW_FROM_CENTER);
            }
        });

        headerButtonBack.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        technicianAdapter = new TimeClockTechsAdapter(
                ActivityTimeClockSupervisorList.this);

        techsList = (ListView) findViewById(R.id.timeclock_supervisor_list);
        techsList.setAdapter(technicianAdapter);
        techsList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                Intent i = new Intent(ActivityTimeClockSupervisorList.this, ActivityTimeClockView.class);
                i.putExtra("tech_position", position);
                startActivity(i);

            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!CommonUtilities.isNetworkAvailable(this)) {
            Toast.makeText(this, "Network connection unavailable.",
                    Toast.LENGTH_SHORT).show();
            finish();
        } else
            new GetTimeClockTask().execute();
    }


    private class GetTechniciansTask extends BaseUiReportTask<String> {
        GetTechniciansTask() {
            super(ActivityTimeClockSupervisorList.this,
                    "Loading technicians...");
        }

        @Override
        protected void onSuccess() {
            techsList.setAdapter(technicianAdapter);
        }

        @Override
        protected boolean taskBody(final String... args) throws Exception {

            RESTTimeclockList.query(UserUtilitiesSingleton.getInstance().user.getServiceProviderId());
            return true;
        }
    }


    private class GetTimeClockTask extends BaseUiReportTask<String> {

        GetTimeClockTask() {
            super(ActivityTimeClockSupervisorList.this,
                    R.string.async_task_string_loading_hours_worked);
        }

        @Override
        protected void onSuccess() {
            new GetTechniciansTask().execute();
        }

        @Override
        protected boolean taskBody(final String... args) throws Exception {
            RESTHoursWorkedList.query(UserUtilitiesSingleton.getInstance().user.getId());
            return true;
        }
    }

}
