package com.skeds.android.phone.business.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.skeds.android.phone.business.AsyncTasks.BaseUiReportTask;
import com.skeds.android.phone.business.Custom.AccountMenu;
import com.skeds.android.phone.business.Custom.QuickAction;
import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Appointment;
import com.skeds.android.phone.business.Utilities.General.CommonUtilities;
import com.skeds.android.phone.business.Utilities.General.UserUtilitiesSingleton;
import com.skeds.android.phone.business.Utilities.REST.Objects.RESTPastAppointmentList;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

public class ActivityCustomerPastAppointmentsListView extends BaseSkedsActivity {

    private PullToRefreshListView mPullRefreshListView;
    private ListView pastAppointmentsList;
    private String[] sizeArray;

    private String dayMonthString = "";
    private String dateString = "";

    private LinearLayout headerLayout;
    private ImageView headerButtonUser;
    private ImageView headerButtonBack;

    private Activity mActivity;
    private OnItemClickListener listItemListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> adapter, View v, int position,
                                long id) {

            AppDataSingleton.getInstance().setPastAppointment(AppDataSingleton.getInstance().getPastAppointmentList().get(
                    (int) id));
            Intent i = new Intent(mActivity,
                    CustomerPastAppointmentActivity.class);
            startActivity(i);
        }
    };
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_customer_past_appointments_list_view);

        headerLayout = (LinearLayout) findViewById(R.id.activity_header);

        mActivity = ActivityCustomerPastAppointmentsListView.this;
        mContext = this;

        final QuickAction accountMenu;
        accountMenu = AccountMenu.setupMenu(mContext, mActivity);

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

        mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.activity_customer_past_appointments_listview_past_appointments);
        if (!CommonUtilities.isNetworkAvailable(mContext)) {
            Toast.makeText(mContext, "Network connection unavailable.",
                    Toast.LENGTH_SHORT).show();
            finish();
        } else {
            new GetPastAppointmentsTask().execute();
        }
    }

    private void setupUI() {

        // Set a listener to be invoked when the list should be refreshed.
        mPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshBase refreshView) {
                mPullRefreshListView.setLastUpdatedLabel(DateUtils
                        .formatDateTime(mContext, System.currentTimeMillis(),
                                DateUtils.FORMAT_SHOW_TIME
                                        | DateUtils.FORMAT_SHOW_DATE
                                        | DateUtils.FORMAT_ABBREV_ALL));

                if (!CommonUtilities.isNetworkAvailable(mContext)) {
                    Toast.makeText(mContext, "Network connection unavailable.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    if (!CommonUtilities.isNetworkAvailable(mContext)) {
                        Toast.makeText(mContext, "Network connection unavailable.",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        // Do work to refresh the list here.
                        new PullToRefreshTask().execute();
                    }
                }
            }
        });

        pastAppointmentsList = mPullRefreshListView.getRefreshableView();
        // pastAppointmentsList.setAdapter(null);

        List<Appointment> appointmentList = new ArrayList<Appointment>();
        appointmentList.addAll(AppDataSingleton.getInstance().getPastAppointmentList());


        pastAppointmentsList.setAdapter(new MyCustomAdapter(mActivity,
                R.layout.row_standard, appointmentList));
        pastAppointmentsList.setTextFilterEnabled(true);
        pastAppointmentsList.setOnItemClickListener(listItemListener);

    }

//    private void dateGenerator(String theDate) {
//
//        theDate.trim();
//        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
//
//        try {
//            Date today = df.parse(theDate);
//
//            DateFormat formatter = new SimpleDateFormat("EE MMM");
//            dayMonthString = formatter.format(today);
//
//            formatter = new SimpleDateFormat("d");
//            dateString = formatter.format(today);
//
//            // System.out.println("Today : " + formattedDate);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//    }

    private class PullToRefreshTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            // Simulates a background job.
            try {
                if (UserUtilitiesSingleton.getInstance().user.isLoggedIn()) {
                    if (UserUtilitiesSingleton.getInstance().user.getTimeTrackable()) {

                        RESTPastAppointmentList.query(AppDataSingleton.getInstance().getCustomer()
                                .getId());
                        return true;
                    }
                }
            } catch (Exception e) {
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {

            // Call onRefreshComplete when the list has been refreshed.
            mPullRefreshListView.onRefreshComplete();

            if (success)
                setupUI();
        }
    }

    private final class GetPastAppointmentsTask
            extends
            BaseUiReportTask<String> {
        GetPastAppointmentsTask() {
            super(ActivityCustomerPastAppointmentsListView.this,
                    R.string.async_task_string_loading_appointment_history);
        }

        @Override
        protected void onSuccess() {
            setupUI();
        }

        @Override
        protected boolean taskBody(final String... args) throws Exception {
            RESTPastAppointmentList.query(AppDataSingleton.getInstance().getCustomer().getId());
            return true;
        }
    }

    private class MyCustomAdapter extends ArrayAdapter<Appointment> {

        public MyCustomAdapter(Context context, int textViewResourceId,
                               List<Appointment> objects) {
            super(context, textViewResourceId, objects);
            notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View row;

            row = inflater.inflate(R.layout.row_standard, parent, false);


            TextView dayMonth = (TextView) row
                    .findViewById(R.id.skedDayMonth);
            TextView dayDate = (TextView) row
                    .findViewById(R.id.skedDayDate);

            TextView skedLabel = (TextView) row
                    .findViewById(R.id.skedLabel);
            TextView skedTime = (TextView) row
                    .findViewById(R.id.skedTime);
            TextView skedStatus = (TextView) row
                    .findViewById(R.id.skedStatus);

            //dateGenerator(getItem(position).getDate());

            dayMonth.setText(com.skeds.android.phone.business.util.DateUtils.convertFromPatternToPattern(

                    getItem(position).getDate() + " " + getItem(position).getStartTime(),
                    "MM/dd/yyyy hh:mm aaa",
                    "EEE MMM",
                    TimeZone.getDefault()
            ));
            dayDate.setText(com.skeds.android.phone.business.util.DateUtils.convertFromPatternToPattern(

                    getItem(position).getDate() + " " + getItem(position).getStartTime(),
                    "MM/dd/yyyy hh:mm aaa",
                    "d",
                    TimeZone.getDefault()
            ));

            skedLabel.setText(getItem(position).getApptTypeName());

            String startTime = com.skeds.android.phone.business.util.DateUtils.convertFromPatternToPattern(

                    getItem(position).getDate() + " " + getItem(position).getStartTime(),
                    "MM/dd/yyyy hh:mm aaa",
                    "h:mm aaa",
                    UserUtilitiesSingleton.getInstance().user.getTimeZone(),
                    TimeZone.getDefault()
            );

            String endTime = com.skeds.android.phone.business.util.DateUtils.convertFromPatternToPattern(

                    getItem(position).getDate() + " " + getItem(position).getEndTime(),
                    "MM/dd/yyyy hh:mm aaa",
                    "h:mm aaa",
                    UserUtilitiesSingleton.getInstance().user.getTimeZone(),
                    TimeZone.getDefault()
            );
            skedTime.setText(startTime + " - " + endTime);

            skedStatus.setText("Closed");


            return row;
        }
    }
}