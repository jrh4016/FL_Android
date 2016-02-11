package com.skeds.android.phone.business.ui.fragment;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.skeds.android.phone.business.AsyncTasks.BaseUiReportTask;
import com.skeds.android.phone.business.C2DMUtilities.C2DMConstants;
import com.skeds.android.phone.business.Dialogs.DialogErrorPopup;
import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.AppointmentListItem;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Status;
import com.skeds.android.phone.business.Utilities.General.CommonUtilities;
import com.skeds.android.phone.business.Utilities.General.Constants;
import com.skeds.android.phone.business.Utilities.General.UserUtilitiesSingleton;
import com.skeds.android.phone.business.Utilities.REST.Objects.RESTAppointmentList;
import com.skeds.android.phone.business.activities.ActivityDashboardView;
import com.skeds.android.phone.business.activities.ActivityLoginMobile;
import com.skeds.android.phone.business.activities.ActivityLoginTablet;
import com.skeds.android.phone.business.core.SkedsApplication;
import com.skeds.android.phone.business.util.DateUtils;

import java.util.ArrayList;
import java.util.TimeZone;


public class AppointmentListFragment extends BaseSkedsFragment {

    OnHeadlineSelectedListener mCallback;

    public static int mSelectedItem;
    public static int mSelectedItemId;

    private ImageView navButtonActive;
    private ImageView navButtonPrevious;

    private PullToRefreshListView mPullRefreshListView;
    private ListView listviewAppointmentList;


    private ArrayList<Integer> activeAppointmentList;
    private ArrayList<Integer> previousAppointmentList;

    private int listType;
    private static final int LIST_TYPE_ACTIVE = 0;
    private static final int LIST_TYPE_PREVIOUS = 1;

    private boolean hasRetreivedSkeds = false;

    private Activity mActivity;
    public static boolean needsRefresh;

    private OnClickListener clickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.nav_appointment_imageview_nav_active:
                    listType = LIST_TYPE_ACTIVE;
                    setupBusinessSkedList();
                    break;
                case R.id.nav_appointment_imageview_nav_previous:
                    listType = LIST_TYPE_PREVIOUS;
                    setupBusinessSkedList();
                    break;
                default:
                    // Nothing
                    break;
            }

        }
    };

    private OnItemClickListener mListSelectedListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View v, int position,
                                long id) {

            // We have to subtract 1 from position because ListView has header
            // for pulling to refresh updates
            position -= 1;

            switch (listType) {
                case LIST_TYPE_ACTIVE:
                    mSelectedItem = position;
                    mSelectedItemId = AppDataSingleton.getInstance().getAppointmentList()
                            .get(activeAppointmentList.get(position)).getId();

                    break;
                case LIST_TYPE_PREVIOUS:
                    mSelectedItem = position;
                    mSelectedItemId = AppDataSingleton.getInstance().getAppointmentList()
                            .get(previousAppointmentList.get(position)).getId();
                    break;
                default:
                    // Nothing
                    break;
            }
            mCallback.onArticleSelected(mSelectedItemId);
            listviewAppointmentList.setItemChecked(position, true);
        }
    };


    public interface OnHeadlineSelectedListener {
        public void onArticleSelected(int id);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (OnHeadlineSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivity = getActivity();

        AppDataSingleton.getInstance().getCustomQuestionList().clear();
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager notificationManager = (NotificationManager) mActivity
                .getSystemService(ns);
        notificationManager
                .cancel(C2DMConstants.NOTIFICATION_TYPE_CANCEL_APPOINTMENT);
        notificationManager
                .cancel(C2DMConstants.NOTIFICATION_TYPE_NEW_APPOINTMENT);
        notificationManager.cancel(C2DMConstants.NOTIFICATION_TYPE_REMINDER);
        notificationManager
                .cancel(C2DMConstants.NOTIFICATION_TYPE_REMOVE_SERVICE_PROVIDER);
        notificationManager
                .cancel(C2DMConstants.NOTIFICATION_TYPE_UPDATE_APPOINTMENT);
        /* End of Notification List to be cleared */

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.layout_appointment_list_view, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mPullRefreshListView = (PullToRefreshListView) mActivity.findViewById(R.id.activity_appointment_list_listview_appointments);
        navButtonActive = (ImageView) mActivity.findViewById(R.id.nav_appointment_imageview_nav_active);
        navButtonPrevious = (ImageView) mActivity.findViewById(R.id.nav_appointment_imageview_nav_previous);

        if (CommonUtilities.isNetworkAvailable(mActivity)) { // Data
            // Connection
            if (UserUtilitiesSingleton.getInstance().user.isLoggedIn()) { // Proper
                // credentials
                if (!hasRetreivedSkeds) {
                    new GetAppointmentListTask().execute();
                }
            } else {
                Class loginClass;

                if (SkedsApplication.getInstance().getApplicationMode() == Constants.APPLICATION_MODE_PHONE_SERVICE)
                    loginClass = ActivityLoginMobile.class;
                else loginClass = ActivityLoginTablet.class;

                Intent i = new Intent(mActivity, loginClass);
                startActivity(i);
                // finish();
            }
        } else {
            listviewAppointmentList = null;
            // mListView.setAdapter(new ArrayAdapter<String>(mContext,
            // android.R.layout.simple_list_item_1, noDataConnection));

            // TODO
            // mListView.setOnItemClickListener(mListSelectedListener);
            // mListView.setBackgroundColor(Color.rgb(62, 81, 101));

        }

    }


    private void setupBusinessSkedList() {

        if (!AppDataSingleton.getInstance().getAppointmentList().isEmpty()) {

            // Set a listener to be invoked when the list should be refreshed.
            mPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener() {

                @Override
                public void onRefresh(PullToRefreshBase refreshView) {
                    // Do work to refresh the list here.
                    new PullToRefreshTask().execute();
                }
            });

            listviewAppointmentList = mPullRefreshListView.getRefreshableView();
            listviewAppointmentList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            listviewAppointmentList.setAdapter(null);
            listviewAppointmentList
                    .setOnItemClickListener(mListSelectedListener);

            navButtonActive.setOnClickListener(clickListener);
            navButtonPrevious.setOnClickListener(clickListener);

            activeAppointmentList = new ArrayList<Integer>();
            previousAppointmentList = new ArrayList<Integer>();
            for (int i = 0; i < AppDataSingleton.getInstance().getAppointmentList().size(); i++) {
                // for (int i = 0; i < AppDataSingleton.getInstance().mAppointmentId.length; i++) {
                switch (AppDataSingleton.getInstance().getAppointmentList().get(i).getStatus()) {
                    case ON_ROUTE:
                    case START_APPOINTMENT:
                    case SUSPEND_APPOINTMENT:
                    case RESTART_APPOINTMENT:
                    case FINISH_APPOINTMENT:
                    case PARTS_RUN_APPOINTMENT:
                    case NOT_STARTED:
                        activeAppointmentList.add(i);
                        break;

                    case CLOSE_APPOINTMENT:
                        previousAppointmentList.add(i);
                        break;
                    default:
                        // Nothing
                        break;
                }
            }

            ArrayAdapter<String> adapter = null;

            navButtonActive
                    .setImageResource(R.drawable.custom_nav_button_appointment_active);
            navButtonPrevious
                    .setImageResource(R.drawable.custom_nav_button_appointment_previous);
            switch (listType) {
                case LIST_TYPE_ACTIVE:
                    navButtonActive
                            .setImageResource(R.drawable.nav_appointment_list_active_pressed);

                    adapter = new MyCustomAdapter(mActivity,
                            R.layout.row_standard, activeAppointmentList);
                    break;

                case LIST_TYPE_PREVIOUS:
                    navButtonPrevious
                            .setImageResource(R.drawable.nav_appointment_list_previous_pressed);

                    adapter = new MyCustomAdapter(mActivity,
                            R.layout.row_standard, previousAppointmentList);
                    break;
                default:
                    // Nothing
                    break;
            }

            listviewAppointmentList.setAdapter(adapter);
            // mListView.setTextFilterEnabled(true);
        }
    }


    private class PullToRefreshTask extends AsyncTask<Void, Void, Boolean> {
        private Exception error = null;

        @Override
        protected Boolean doInBackground(Void... params) {
            // Simulates a background job.
            try {
                if (UserUtilitiesSingleton.getInstance().user.isLoggedIn()) {
                    if (UserUtilitiesSingleton.getInstance().user.getTimeTrackable()) {
                        RESTAppointmentList.query();
                        return true;
                    }
                }
            } catch (Exception e) {
                error = e;
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {

            // Call onRefreshComplete when the list has been refreshed.
            mPullRefreshListView.onRefreshComplete();

            if (success) {
                if (!UserUtilitiesSingleton.getInstance().user.isLoggedIn()) {
                    Intent i = new Intent(mActivity, ActivityDashboardView.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    // finish();
                } else {
                    setupBusinessSkedList();
                }
            } else {
                if (error != null)
                    new DialogErrorPopup(mActivity,
                            "Could not load appointments", null, error).show();
            }
        }
    }

    private class GetAppointmentListTask extends BaseUiReportTask<String> {

        GetAppointmentListTask() {
            super(mActivity,
                    R.string.async_task_string_loading_appointments);
        }

        @Override
        protected void onSuccess() {
            if (UserUtilitiesSingleton.getInstance().user.isLoggedIn()) {
                setupBusinessSkedList();
                // mListView.requestFocus();

            } else {
                Intent i = new Intent(mActivity, ActivityDashboardView.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                // finish();
            }
        }

        @Override
        protected boolean taskBody(final String... args) throws Exception {
            RESTAppointmentList.query();
            return true;
        }
    }

    public class MyCustomAdapter extends ArrayAdapter<String> {

        public MyCustomAdapter(Context context, int textViewResourceId,
                               ArrayList objects) {
            super(context, textViewResourceId, objects);
            notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            ViewHolder holder;

            if (v == null) {
                LayoutInflater vi = (LayoutInflater) mActivity
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.row_appointment, null);

                // cache view fields into the holder
                holder = new ViewHolder();
                holder.dayMonth = (TextView) v.findViewById(R.id.skedDayMonth);
                holder.dayDate = (TextView) v.findViewById(R.id.skedDayDate);
                holder.label = (TextView) v.findViewById(R.id.skedLabel);
                holder.apptStatus = (TextView) v.findViewById(R.id.skedStatus);
                holder.time = (TextView) v.findViewById(R.id.skedTime);
                holder.workOrderNumber = (TextView) v.findViewById(R.id.workOrderNumber);
                holder.location = (TextView) v.findViewById(R.id.apptLocation);
                // associate the holder with the view for later lookup
                v.setTag(holder);
            } else {
                // view already exists, get the holder instance from the view
                holder = (ViewHolder) v.getTag();
            }


            int itemToUse = 0;
            int length = 0;
            switch (listType) {
                case LIST_TYPE_ACTIVE:
                    itemToUse = activeAppointmentList.get(position);
                    length = activeAppointmentList.size();
                    break;
                case LIST_TYPE_PREVIOUS:
                    itemToUse = previousAppointmentList.get(position);
                    length = previousAppointmentList.size();
                    break;
                default:
                    // Nothing
                    break;
            }

            if (position < length) {

                // Setup Date Labels
                if (AppDataSingleton.getInstance().getAppointmentList().get(itemToUse) != null) {

                    AppointmentListItem appointmentListItem = AppDataSingleton.getInstance().getAppointmentList().get(itemToUse);

                    String startTime = DateUtils.convertFromPatternToPattern(

                            appointmentListItem.getStartDate() + " " + appointmentListItem.getStartTime(),
                            "MM/dd/yyyy hh:mm aaa",
                            "h:mm aaa",
                            UserUtilitiesSingleton.getInstance().user.getTimeZone(),
                            TimeZone.getDefault()
                    );

                    String dayMonth = DateUtils.convertFromPatternToPattern(

                            appointmentListItem.getStartDate() + " " + appointmentListItem.getStartTime(),
                            "MM/dd/yyyy hh:mm aaa",
                            "EEE MMM",
                            UserUtilitiesSingleton.getInstance().user.getTimeZone(),
                            TimeZone.getDefault()
                    );


                    String date = DateUtils.convertFromPatternToPattern(

                            appointmentListItem.getStartDate() + " " + appointmentListItem.getStartTime(),
                            "MM/dd/yyyy hh:mm aaa",
                            "d",
                            UserUtilitiesSingleton.getInstance().user.getTimeZone(),
                            TimeZone.getDefault()
                    );

                    if (appointmentListItem.getTimeZone()!=null&&(!appointmentListItem.getTimeZone().equals(TimeZone.getDefault()))) {

                        String startTime1 = DateUtils.convertFromPatternToPattern(

                                appointmentListItem.getStartDate()+ " " + appointmentListItem.getStartTime(),
                                "MM/dd/yyyy hh:mm aaa",
                                "h:mm aaa",
                                UserUtilitiesSingleton.getInstance().user.getTimeZone(),
                                appointmentListItem.getTimeZone()

                        );

                        startTime = startTime + " (" + startTime1+ " "+ appointmentListItem.getTimeZone().getDisplayName(false, TimeZone.SHORT) + ")";
                    }

                    holder.dayMonth.setText(dayMonth);

                    holder.dayDate.setText(date);

                    holder.time.setText(startTime);

                    holder.location.setText(appointmentListItem.getLocation());
                }

                if (!AppDataSingleton.getInstance().getAppointmentList().get(itemToUse).getWorkOrderNumber().isEmpty()) {
                    holder.workOrderNumber.setText(AppDataSingleton.getInstance().getAppointmentList().get(itemToUse).getWorkOrderNumber());
                    holder.workOrderNumber.setVisibility(View.VISIBLE);
                } else holder.workOrderNumber.setVisibility(View.GONE);

                // Set Sked Name Label

                String skedTitle = "";
                if (AppDataSingleton.getInstance().getAppointmentList().get(itemToUse).getLabel() != null)
                    skedTitle = AppDataSingleton.getInstance().getAppointmentList().get(itemToUse)
                            .getLabel();
                else
                    skedTitle = "Customer name not found";

                holder.label.setText(skedTitle);

                // Appointment Status Label
                holder.apptStatus.setVisibility(View.VISIBLE);
                switch (AppDataSingleton.getInstance().getAppointmentList().get(itemToUse).getStatus()) {
                    case ON_ROUTE:
                        holder.apptStatus
                                .setText(mActivity.getResources().getString(
                                        R.string.job_status_string_on_route));
                        holder.apptStatus.setTextColor(Color
                                .parseColor(Status.COLOR_ON_ROUTE)); // Blue
                        break;
                    case SUSPEND_APPOINTMENT:
                        holder.apptStatus.setText(mActivity.getResources()
                                .getString(R.string.job_status_string_paused));
                        holder.apptStatus.setTextColor(Color
                                .parseColor(Status.COLOR_SUSPEND_APPOINTMENT)); // Red
                        break;
                    case PARTS_RUN_APPOINTMENT:
                        holder.apptStatus
                                .setText(mActivity.getResources().getString(
                                        R.string.job_status_string_parts_run));
                        holder.apptStatus.setTextColor(Color
                                .parseColor(Status.COLOR_PARTS_RUN)); // Orange
                        break;
                    case START_APPOINTMENT:
                    case RESTART_APPOINTMENT:
                        holder.apptStatus.setText(mActivity.getResources()
                                .getString(R.string.job_status_string_working));
                        holder.apptStatus.setTextColor(Color
                                .parseColor(Status.COLOR_START_APPOINTMENT)); // Green
                        break;
                    case FINISH_APPOINTMENT:
                        holder.apptStatus
                                .setText(mActivity.getResources().getString(
                                        R.string.job_status_string_finished));
                        holder.apptStatus.setTextColor(Color
                                .parseColor(Status.COLOR_FINISH_APPOINTMENT)); // Dark
                        // Gray
                        break;
                    case NOT_STARTED:
                        holder.apptStatus
                                .setText(mActivity.getResources().getString(
                                        R.string.job_status_string_not_started));
                        holder.apptStatus.setTextColor(Color
                                .parseColor(Status.COLOR_NOT_STARTED)); // Gray
                        holder.apptStatus
                                .setTypeface(null, Typeface.ITALIC);
                        break;
                    case CLOSE_APPOINTMENT:
                        holder.apptStatus.setText(mActivity.getResources()
                                .getString(R.string.job_status_string_closed)
                                + " ");
                        holder.apptStatus.setTextColor(Color
                                .parseColor(Status.COLOR_CLOSE_APPOINTMENT)); // Black
                        holder.apptStatus.setTypeface(null,
                                Typeface.BOLD_ITALIC);
                    default:
                        // Nothing
                        break;
                }


//                String startTime = DateUtils.formatAmToAM(AppDataSingleton.getInstance().getAppointmentList()
//                        .get(itemToUse).getStartTime(), "h:mm a");


            } else {
                v.setVisibility(View.GONE);
            }

            return v;
        }
    }

    private static class ViewHolder {
        TextView dayMonth;
        TextView dayDate;
        TextView label;
        TextView apptStatus;
        TextView time;
        TextView workOrderNumber;
        TextView location;
    }

}
