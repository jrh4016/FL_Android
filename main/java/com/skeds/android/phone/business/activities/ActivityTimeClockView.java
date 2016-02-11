package com.skeds.android.phone.business.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.skeds.android.phone.business.AsyncTasks.BaseUiReportTask;
import com.skeds.android.phone.business.Custom.AccountMenu;
import com.skeds.android.phone.business.Custom.QuickAction;
import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Services.TimeReminderService;
import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.ServiceProvider;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.TimeClockTechnician;
import com.skeds.android.phone.business.Utilities.General.CommonUtilities;
import com.skeds.android.phone.business.Utilities.General.UserUtilitiesSingleton;
import com.skeds.android.phone.business.Utilities.REST.Objects.RESTHoursWorkedList;
import com.skeds.android.phone.business.Utilities.REST.Objects.RESTTimeclockList;
import com.skeds.android.phone.business.core.SkedsApplication;
import com.skeds.android.phone.business.util.DateUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class ActivityTimeClockView extends BaseSkedsActivity implements
        LocationListener {

    private static final int NAV_TODAY = 0;
    private static final int NAV_THISWEEK = 1;
    private static final int NAV_LASTWEEK = 2;

    private LinearLayout headerLayout;
    private ImageView headerButtonUser;
    private ImageView headerButtonBack;

    private ImageView navButtonHoursToday, navButtonHoursThisWeek,
            navButtonHoursLastWeek;

    private TextView textviewTotalHours;
    private TextView textviewTotalHoursWording;

    private ListView mHoursList;
    private String[] sizeArray;
    private int totalSize;

    // private int mCurrentHoursSetup;
    private int currentJobHourSetup;
    private int currentClockInHourSetup;

    private Activity mActivity;
    private Context mContext;

    private Handler handler = new Handler();
    // private long startTime = 0L;

    /* Updated layout elements */
    /* Elements on both layouts */
    private ImageView navButtonClockIn;
    private ImageView navButtonStatuses;
    private LinearLayout linearlayoutSwapableLayout;

    /* Clock-In Layout Elements */
    private TextView textLargeClockHour;
    private TextView textLargeClockMinute;
    private TextView textLargeClockAMPM;
    private ImageView buttonUpdateStatus;
    private ImageView navButtonClockHoursToday;
    private ImageView navButtonClockHoursThisWeek;
    private ImageView navButtonClockHoursLastWeek;
    private TextView textTotalTimeWorked;
    private LinearLayout linearlayoutClockInList;

    private ClockThread clockThread;

    public static int technicianToManage;

    private int techPosition;

    /* Users GPS data */
    private double longitude, latitude;
    private LocationManager lm;

    private String DEBUG_TAG = "Skeds";

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_timeclock_view);

        techPosition = getIntent().getIntExtra("tech_position", 0);

        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        headerLayout = (LinearLayout) findViewById(R.id.activity_header);

        mActivity = ActivityTimeClockView.this;
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

        linearlayoutSwapableLayout = (LinearLayout) findViewById(R.id.activity_timeclock_linearlayout_swapable);

        navButtonClockIn = (ImageView) findViewById(R.id.activity_timeclock_nav_clock_in);
        navButtonStatuses = (ImageView) findViewById(R.id.activity_timeclock_nav_statuses);

        // Select/inflate "clock-in" layout
        inflateClockInLayout();

        if (!CommonUtilities.isNetworkAvailable(mActivity)) {
            Toast.makeText(mActivity, "Network connection unavailable.",
                    Toast.LENGTH_SHORT).show();
            finish();
        }
        new GetTimeClockTask().execute();
    }

    @Override
    public void onBackPressed() {
        if (clockThread != null)
            clockThread.interrupt();
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        lm.removeUpdates(this);
    }

    private OnClickListener mHoursTodayListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (currentJobHourSetup != NAV_TODAY) {
                currentJobHourSetup = NAV_TODAY;
                setStatusHourNavigation(NAV_TODAY); // Today
            }
        }
    };

    private OnClickListener mHoursThisWeekListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (currentJobHourSetup != NAV_THISWEEK) {
                currentJobHourSetup = NAV_THISWEEK;
                setStatusHourNavigation(NAV_THISWEEK); // This Week
            }
        }
    };

    private OnClickListener mHoursLastWeekListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (currentJobHourSetup != NAV_LASTWEEK) {
                currentJobHourSetup = NAV_LASTWEEK;
                setStatusHourNavigation(NAV_LASTWEEK); // Last Week
            }
        }
    };

    private OnClickListener clockInNavButtonListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.activity_timeclock_button_clocked_in_today:
                    if (currentClockInHourSetup != NAV_TODAY) {
                        currentClockInHourSetup = NAV_TODAY;
                        setClockInHourNavigation(NAV_TODAY);
                    }
                    break;

                case R.id.activity_timeclock_button_clocked_in_this_week:
                    if (currentClockInHourSetup != NAV_THISWEEK) {
                        currentClockInHourSetup = NAV_THISWEEK;
                        setClockInHourNavigation(NAV_THISWEEK);
                    }
                    break;

                case R.id.activity_timeclock_button_clocked_in_last_week:
                    if (currentClockInHourSetup != NAV_LASTWEEK) {
                        currentClockInHourSetup = NAV_LASTWEEK;
                        setClockInHourNavigation(NAV_LASTWEEK);
                    }
                    break;
                default:
                    // Nothing
                    break;
            }
        }
    };

    private OnClickListener buttonListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.activity_timeclock_button_update_status:

                    String updateType;

                    if (techPosition == 0) {
                        if (!AppDataSingleton.getInstance().getHoursWorked()
                                .isClockedIn())
                            startService(new Intent(ActivityTimeClockView.this,
                                    TimeReminderService.class));
                        else
                            stopService(new Intent(ActivityTimeClockView.this,
                                    TimeReminderService.class));

                        updateType = AppDataSingleton.getInstance()
                                .getHoursWorked().isClockedIn() ? "OUT" : "IN";
                    } else {
                        updateType = "IN".equals(AppDataSingleton.getInstance()
                                .getClockTechnician().get(techPosition)
                                .getTimeClockMethod()) ? "OUT" : "IN";
                    }
                    new UpdateTimeClockStatusTask(updateType).execute();
                    break;

                case R.id.activity_timeclock_nav_clock_in:

                    navButtonClockIn
                            .setImageResource(R.drawable.nav_timeclock_clock_in_pressed);
                    navButtonStatuses
                            .setImageResource(R.drawable.nav_timeclock_statuses);

                    inflateClockInLayout();
                    setupClockInUI();
                    // new GetTimeClockTask(mActivity).execute();
                    break;

                case R.id.activity_timeclock_nav_statuses:

                    navButtonClockIn
                            .setImageResource(R.drawable.nav_timeclock_clock_in);
                    navButtonStatuses
                            .setImageResource(R.drawable.nav_timeclock_statuses_pressed);

                    inflateStatusesLayout();
                    setupStatusesUI();
                    // new GetMyHoursTask(mActivity).execute();
                    break;

                case R.id.activity_timeclock_supervisor_imageview_my_hours:
                    LinearLayout layoutSupervisorHours = (LinearLayout) findViewById(R.id.activity_timeclock_supervisor_linearlayout_my_hours);
                    ImageView imageButtonMyHours = (ImageView) findViewById(R.id.activity_timeclock_supervisor_imageview_my_hours);

                    if (layoutSupervisorHours.getVisibility() == LinearLayout.VISIBLE) {
                        layoutSupervisorHours.setVisibility(View.GONE);
                        imageButtonMyHours
                                .setImageResource(R.drawable.time_clock_my_hours_button_collapsed);
                    } else {
                        layoutSupervisorHours.setVisibility(View.VISIBLE);
                        imageButtonMyHours
                                .setImageResource(R.drawable.time_clock_my_hours_button_expanded);
                    }
                    break;
                default:
                    // Nothing
                    break;
            }
        }
    };

    private void inflateClockInLayout() {

        LayoutInflater layoutInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        linearlayoutSwapableLayout.removeAllViews();
        View layoutClockIn;
        if (UserUtilitiesSingleton.getInstance().user.isPermissionSupervisor())
            layoutClockIn = layoutInflater.inflate(
                    R.layout.timeclock_nav_layout_clock_in_supervisor, null);
        else
            layoutClockIn = layoutInflater.inflate(
                    R.layout.timeclock_nav_layout_clock_in, null);
        linearlayoutSwapableLayout.addView(layoutClockIn);

    }

    /* This is called by the progress task after retrieving data */
    private void setupClockInUI() {

        textLargeClockHour = (TextView) findViewById(R.id.activity_timeclock_textview_large_clock_hours);
        textLargeClockMinute = (TextView) findViewById(R.id.activity_timeclock_textview_large_clock_minutes);
        textLargeClockAMPM = (TextView) findViewById(R.id.activity_timeclock_textview_large_clock_ampm);

        TextView name = (TextView) findViewById(R.id.activity_timeclock_textview_name);

        if (techPosition == 0)
            name.setText(UserUtilitiesSingleton.getInstance().user
                    .getFirstName()
                    + " "
                    + UserUtilitiesSingleton.getInstance().user.getLastName());
        else
            name.setText(AppDataSingleton.getInstance().getClockTechnician()
                    .get(techPosition).getName());

        buttonUpdateStatus = (ImageView) findViewById(R.id.activity_timeclock_button_update_status);
        clockThread = new ClockThread();
        clockThread.start();

        buttonUpdateStatus.setOnClickListener(buttonListener);
        navButtonStatuses.setOnClickListener(buttonListener);
        navButtonClockIn.setOnClickListener(buttonListener);

		/* This actually sets up the correct current time on the clock */
        Calendar cal = Calendar.getInstance();
        int minute = cal.get(Calendar.MINUTE);
        // 12 hour format
        int hour = cal.get(Calendar.HOUR);
        if (hour == 0)
            hour = 12;

        int hourofday = cal.get(Calendar.HOUR_OF_DAY);

        textLargeClockHour.setText(String.format("%02d", hour));
        textLargeClockMinute.setText(String.format("%02d", minute));
        // 24 hour format

        if (hourofday >= 12)
            textLargeClockAMPM.setText("PM");
        else
            textLargeClockAMPM.setText("AM");

        LinearLayout linearlayoutClockedInSince = (LinearLayout) findViewById(R.id.activity_timeclock_linearlayout_clocked_in_since);
        TextView textClockedInSince = (TextView) findViewById(R.id.activity_timeclock_textview_clocked_in_since);

        if (techPosition == 0) {
            if (AppDataSingleton.getInstance().getHoursWorked().isClockedIn()) {
                linearlayoutClockedInSince.setVisibility(View.VISIBLE);
                buttonUpdateStatus
                        .setImageResource(R.drawable.phone_custom_timeclock_button_clock_out);
                textClockedInSince.setText(AppDataSingleton.getInstance()
                        .getHoursWorked().getClockInTime().replace("am","AM").replace("pm","PM"));
            } else {
                linearlayoutClockedInSince.setVisibility(View.GONE);
                buttonUpdateStatus
                        .setImageResource(R.drawable.phone_custom_timeclock_button_clock_in);
            }

        } else {
            if ("IN".equals(AppDataSingleton.getInstance().getClockTechnician()
                    .get(techPosition).getTimeClockMethod())) {
                linearlayoutClockedInSince.setVisibility(View.VISIBLE);
                buttonUpdateStatus
                        .setImageResource(R.drawable.phone_custom_timeclock_button_clock_out);
                textClockedInSince.setText(AppDataSingleton.getInstance()
                        .getClockTechnician().get(techPosition)
                        .getTimeClockMethodDate().replace("am","AM").replace("pm","PM"));
            } else {
                linearlayoutClockedInSince.setVisibility(View.GONE);
                buttonUpdateStatus
                        .setImageResource(R.drawable.phone_custom_timeclock_button_clock_in);
            }
        }

		/*
		 * This will understand supervisor vs regular user and setup hooks
		 * correctly
		 */
        if (UserUtilitiesSingleton.getInstance().user.isPermissionSupervisor()) {
            // setup list of technicians + button hooks

            // LinearLayout layoutMyHours = (LinearLayout)
            // findViewById(R.id.activity_timeclock_supervisor_linearlayout_my_hours);
            ImageView imageButtonMyHours = (ImageView) findViewById(R.id.activity_timeclock_supervisor_imageview_my_hours);

            navButtonClockHoursToday = (ImageView) findViewById(R.id.activity_timeclock_button_clocked_in_today);
            navButtonClockHoursThisWeek = (ImageView) findViewById(R.id.activity_timeclock_button_clocked_in_this_week);
            navButtonClockHoursLastWeek = (ImageView) findViewById(R.id.activity_timeclock_button_clocked_in_last_week);
            textTotalTimeWorked = (TextView) findViewById(R.id.activity_timeclock_textview_total_time_worked);
            linearlayoutClockInList = (LinearLayout) findViewById(R.id.activity_timeclock_linearlayout_clocked_in_hours);

            setClockInHourNavigation(NAV_TODAY);
            currentClockInHourSetup = NAV_TODAY;

            navButtonClockHoursToday
                    .setOnClickListener(clockInNavButtonListener);
            navButtonClockHoursThisWeek
                    .setOnClickListener(clockInNavButtonListener);
            navButtonClockHoursLastWeek
                    .setOnClickListener(clockInNavButtonListener);

            imageButtonMyHours.setOnClickListener(buttonListener);
        } else {

            navButtonClockHoursToday = (ImageView) findViewById(R.id.activity_timeclock_button_clocked_in_today);
            navButtonClockHoursThisWeek = (ImageView) findViewById(R.id.activity_timeclock_button_clocked_in_this_week);
            navButtonClockHoursLastWeek = (ImageView) findViewById(R.id.activity_timeclock_button_clocked_in_last_week);
            textTotalTimeWorked = (TextView) findViewById(R.id.activity_timeclock_textview_total_time_worked);
            linearlayoutClockInList = (LinearLayout) findViewById(R.id.activity_timeclock_linearlayout_clocked_in_hours);

            setClockInHourNavigation(NAV_TODAY);
            currentClockInHourSetup = NAV_TODAY;

            navButtonClockHoursToday
                    .setOnClickListener(clockInNavButtonListener);
            navButtonClockHoursThisWeek
                    .setOnClickListener(clockInNavButtonListener);
            navButtonClockHoursLastWeek
                    .setOnClickListener(clockInNavButtonListener);
        }

    }

    private void inflateStatusesLayout() {
        LayoutInflater layoutInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        linearlayoutSwapableLayout.removeAllViews();
        View layoutStatuses = layoutInflater.inflate(
                R.layout.timeclock_nav_layout_statuses, null);
        linearlayoutSwapableLayout.addView(layoutStatuses);
    }

    /* This is called by the progress task after retrieving data */
    private void setupStatusesUI() {
        clockThread.interrupt();

        navButtonStatuses.setOnClickListener(buttonListener);
        navButtonClockIn.setOnClickListener(buttonListener);

        mHoursList = (ListView) findViewById(R.id.activity_my_hours_listview_hours);

        navButtonHoursToday = (ImageView) findViewById(R.id.header_nav_hours_worked_imageview_nav_today);
        navButtonHoursThisWeek = (ImageView) findViewById(R.id.header_nav_hours_worked_imageview_nav_this_week);
        navButtonHoursLastWeek = (ImageView) findViewById(R.id.header_nav_hours_worked_imageview_nav_last_week);

        textviewTotalHours = (TextView) findViewById(R.id.header_nav_hours_worked_textview_total);
        textviewTotalHoursWording = (TextView) findViewById(R.id.header_nav_hours_worked_textview_information);

        currentJobHourSetup = NAV_TODAY;
        setStatusHourNavigation(NAV_TODAY); // Today

        navButtonHoursToday.setOnClickListener(mHoursTodayListener);
        navButtonHoursThisWeek.setOnClickListener(mHoursThisWeekListener);
        navButtonHoursLastWeek.setOnClickListener(mHoursLastWeekListener);
    }

    private void setStatusHourNavigation(int navigation) {

        navButtonHoursToday
                .setImageResource(R.drawable.custom_nav_button_hoursworked_today);
        navButtonHoursThisWeek
                .setImageResource(R.drawable.custom_nav_button_hoursworked_thisweek);
        navButtonHoursLastWeek
                .setImageResource(R.drawable.custom_nav_button_hoursworked_lastweek);

        switch (navigation) {
            case NAV_TODAY: // Today
                navButtonHoursToday
                        .setImageResource(R.drawable.hours_nav_today_pressed);
                textviewTotalHoursWording.setText("Total Hours for Today: ");

                if (AppDataSingleton.getInstance().getHoursWorked().todayTimesWorked.timeSpan != null) {
                    textviewTotalHours.setText(AppDataSingleton.getInstance()
                            .getHoursWorked().todayTimesWorked.getTotalHours());

                    totalSize = AppDataSingleton.getInstance().getHoursWorked().todayTimesWorked.timeSpan
                            .size();
                    setupHoursList();
                } else {
                    textviewTotalHours.setText("N/A");
                    mHoursList.setAdapter(null);
                }
                break;
            case NAV_THISWEEK: // This Week
                navButtonHoursThisWeek
                        .setImageResource(R.drawable.hours_nav_thisweek_pressed);
                textviewTotalHoursWording.setText("Total Hours for This Week: ");

                if (AppDataSingleton.getInstance().getHoursWorked().thisweekTimesWorked.timeSpan != null) {
                    textviewTotalHours.setText(AppDataSingleton.getInstance()
                            .getHoursWorked().thisweekTimesWorked.getTotalHours());

                    totalSize = AppDataSingleton.getInstance().getHoursWorked().thisweekTimesWorked.timeSpan
                            .size();
                    setupHoursList();
                } else {
                    textviewTotalHours.setText("N/A");
                    mHoursList.setAdapter(null);
                }
                break;
            case NAV_LASTWEEK: // Last Week
                navButtonHoursLastWeek
                        .setImageResource(R.drawable.hours_nav_lastweek_pressed);
                textviewTotalHoursWording.setText("Total Hours for Last Week: ");
                if (AppDataSingleton.getInstance().getHoursWorked().lastweekTimesWorked.timeSpan != null) {
                    textviewTotalHours.setText(AppDataSingleton.getInstance()
                            .getHoursWorked().lastweekTimesWorked.getTotalHours());

                    totalSize = AppDataSingleton.getInstance().getHoursWorked().lastweekTimesWorked.timeSpan
                            .size();
                    setupHoursList();
                } else {
                    textviewTotalHours.setText("N/A");
                    mHoursList.setAdapter(null);
                }
                break;
            default:
                // Nothing
                break;
        }
    }

    private void setClockInHourNavigation(int navigation) {

        navButtonClockHoursToday
                .setImageResource(R.drawable.custom_nav_button_hoursworked_today);
        navButtonClockHoursThisWeek
                .setImageResource(R.drawable.custom_nav_button_hoursworked_thisweek);
        navButtonClockHoursLastWeek
                .setImageResource(R.drawable.custom_nav_button_hoursworked_lastweek);

        switch (navigation) {
            case NAV_TODAY: // Today
                navButtonClockHoursToday
                        .setImageResource(R.drawable.hours_nav_today_pressed);

                if (AppDataSingleton.getInstance().getHoursWorked().todaysTimeClockRecords.timeSpan != null) {
                    textTotalTimeWorked.setText(AppDataSingleton.getInstance()
                            .getHoursWorked().todaysTimeClockRecords
                            .getTotalHours());
                    setupClockHoursList();
                } else {
                    textTotalTimeWorked.setText("N/A");
                }
                break;
            case NAV_THISWEEK: // This Week
                navButtonClockHoursThisWeek
                        .setImageResource(R.drawable.hours_nav_thisweek_pressed);

                if (AppDataSingleton.getInstance().getHoursWorked().thisweekTimeClockRecords.timeSpan != null) {
                    textTotalTimeWorked.setText(AppDataSingleton.getInstance()
                            .getHoursWorked().thisweekTimeClockRecords
                            .getTotalHours());
                    setupClockHoursList();
                } else {
                    textTotalTimeWorked.setText("N/A");
                }
                break;
            case NAV_LASTWEEK: // Last Week
                navButtonClockHoursLastWeek
                        .setImageResource(R.drawable.hours_nav_lastweek_pressed);
                if (AppDataSingleton.getInstance().getHoursWorked().lastweekTimeClockRecords.timeSpan != null) {
                    textTotalTimeWorked.setText(AppDataSingleton.getInstance()
                            .getHoursWorked().lastweekTimeClockRecords
                            .getTotalHours());
                    setupClockHoursList();
                } else {
                    textTotalTimeWorked.setText("N/A");
                }
                break;
            default:
                // Nothing
                break;
        }
    }

    private class MyCustomAdapter extends ArrayAdapter<String> {

        public MyCustomAdapter(Context context, int textViewResourceId,
                               String[] objects) {
            super(context, textViewResourceId, objects);
            this.notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.row_hours_worked, parent,
                    false);

            switch (currentJobHourSetup) {
                case NAV_TODAY:
                    if (AppDataSingleton.getInstance().getHoursWorked().todayTimesWorked.timeSpan != null) {

                        String date = AppDataSingleton.getInstance()
                                .getHoursWorked().todayTimesWorked.timeSpan.get(
                                        position).getFromTo();
                        DateFormat df;
                        if (!date.contains("Started at"))
                            df = new SimpleDateFormat(
                                    "hh:mm a - hh:mm a MM/dd/yyyy");
                        else {
                            String temp = "";
                            temp = date.substring(11, date.length());
                            date = temp;
                            df = new SimpleDateFormat("hh:mm a MM/dd/yyyy");
                        }

                        String dayOfWeekMonth = "";
                        String dateOfMonth = "";

                        try {
                            Date today = df.parse(date);

                            DateFormat formatter = new SimpleDateFormat("EE MMM");
                            dayOfWeekMonth = formatter.format(today);

                            formatter = new SimpleDateFormat("dd");
                            dateOfMonth = formatter.format(today);

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        // Set Sked Day Date Label
                        TextView hoursDayMonthDay = (TextView) row
                                .findViewById(R.id.hoursworkedDayMonth);
                        hoursDayMonthDay.setText(dayOfWeekMonth.trim());

                        TextView hoursDayDate = (TextView) row
                                .findViewById(R.id.hoursworkedDayDate);
                        hoursDayDate.setText(dateOfMonth.trim());

                        TextView nameLabel = (TextView) row
                                .findViewById(R.id.hoursworkedLabel);
                        nameLabel.setText(AppDataSingleton.getInstance()
                                .getHoursWorked().todayTimesWorked.timeSpan.get(
                                        position).getCustomerName());

                        TextView statusDescription = (TextView) row
                                .findViewById(R.id.hoursworkedStatusDescription);
                        statusDescription.setText(AppDataSingleton.getInstance()
                                .getHoursWorked().todayTimesWorked.timeSpan.get(
                                        position).getStatus());

                        TextView timeLabel = (TextView) row
                                .findViewById(R.id.hoursworkedTime);
                        String time = DateUtils.formatAmToAM(AppDataSingleton.getInstance()
                                .getHoursWorked().todayTimesWorked.timeSpan.get(
                                        position).getFromTo(), "hh:mm a MM/dd/yyyy");
                        timeLabel.setText(time.substring(0, time.lastIndexOf(" ")));

                        TextView statusLabel = (TextView) row
                                .findViewById(R.id.hoursworkedStatus);
                        statusLabel.setText(AppDataSingleton.getInstance()
                                .getHoursWorked().getStatus());

                    }
                    break;
                case NAV_THISWEEK:
                    if (AppDataSingleton.getInstance().getHoursWorked().thisweekTimesWorked.timeSpan != null) {
                        String date = AppDataSingleton.getInstance()
                                .getHoursWorked().thisweekTimesWorked.timeSpan.get(
                                        position).getFromTo();
                        DateFormat df;
                        if (!date.contains("Started at"))
                            df = new SimpleDateFormat(
                                    "hh:mm a - hh:mm a MM/dd/yyyy");
                        else {
                            String temp = "";
                            temp = date.substring(11, date.length());
                            date = temp;
                            df = new SimpleDateFormat("hh:mm a MM/dd/yyyy");
                        }

                        String dayOfWeekMonth = "";
                        String dateOfMonth = "";

                        try {
                            Date today = df.parse(date);

                            // DateFormat formatter = new
                            // SimpleDateFormat("EE MMM, dd yyyy");
                            // formattedDate = formatter.format(today);

                            DateFormat formatter = new SimpleDateFormat("EE MMM");
                            dayOfWeekMonth = formatter.format(today);

                            formatter = new SimpleDateFormat("dd");
                            dateOfMonth = formatter.format(today);

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        // Set Sked Day Date Label
                        TextView hoursDayMonthDay = (TextView) row
                                .findViewById(R.id.hoursworkedDayMonth);
                        hoursDayMonthDay.setText(dayOfWeekMonth.trim());

                        TextView hoursDayDate = (TextView) row
                                .findViewById(R.id.hoursworkedDayDate);
                        hoursDayDate.setText(dateOfMonth.trim());

                        TextView nameLabel = (TextView) row
                                .findViewById(R.id.hoursworkedLabel);
                        nameLabel.setText(AppDataSingleton.getInstance()
                                .getHoursWorked().thisweekTimesWorked.timeSpan.get(
                                        position).getCustomerName());

                        TextView statusDescription = (TextView) row
                                .findViewById(R.id.hoursworkedStatusDescription);
                        statusDescription.setText(AppDataSingleton.getInstance()
                                .getHoursWorked().thisweekTimesWorked.timeSpan.get(
                                        position).getStatus());
                        statusDescription.setText("");

                        TextView timeLabel = (TextView) row
                                .findViewById(R.id.hoursworkedTime);
                        String time = DateUtils.formatAmToAM(AppDataSingleton.getInstance()
                                .getHoursWorked().thisweekTimesWorked.timeSpan.get(
                                        position).getFromTo(), "h:mm a");
                        timeLabel.setText(time.substring(0, time.lastIndexOf(" ")));
                    }
                    break;
                case NAV_LASTWEEK:
                    if (AppDataSingleton.getInstance().getHoursWorked().lastweekTimesWorked.timeSpan != null) {
                        String date = AppDataSingleton.getInstance()
                                .getHoursWorked().lastweekTimesWorked.timeSpan.get(
                                        position).getFromTo();
                        DateFormat df;
                        if (!date.contains("Started at"))
                            df = new SimpleDateFormat(
                                    "hh:mm a - hh:mm a MM/dd/yyyy");
                        else {
                            String temp = "";
                            temp = date.substring(11, date.length());
                            date = temp;
                            df = new SimpleDateFormat("hh:mm a MM/dd/yyyy");
                        }

                        String dayOfWeekMonth = "";
                        String dateOfMonth = "";

                        try {
                            Date today = df.parse(date);

                            DateFormat formatter = new SimpleDateFormat("EE MMM");
                            dayOfWeekMonth = formatter.format(today);

                            formatter = new SimpleDateFormat("dd");
                            dateOfMonth = formatter.format(today);

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        // Set Sked Day Date Label
                        TextView hoursDayMonthDay = (TextView) row
                                .findViewById(R.id.hoursworkedDayMonth);
                        hoursDayMonthDay.setText(dayOfWeekMonth.trim());

                        TextView hoursDayDate = (TextView) row
                                .findViewById(R.id.hoursworkedDayDate);
                        hoursDayDate.setText(dateOfMonth.trim());

                        TextView statusDescription = (TextView) row
                                .findViewById(R.id.hoursworkedStatusDescription);
                        statusDescription.setText(AppDataSingleton.getInstance()
                                .getHoursWorked().lastweekTimesWorked.timeSpan.get(
                                        position).getStatus());

                        TextView nameLabel = (TextView) row
                                .findViewById(R.id.hoursworkedLabel);
                        nameLabel.setText(AppDataSingleton.getInstance()
                                .getHoursWorked().lastweekTimesWorked.timeSpan.get(
                                        position).getCustomerName());

                        TextView timeLabel = (TextView) row
                                .findViewById(R.id.hoursworkedTime);
                        String time = DateUtils.formatAmToAM(AppDataSingleton.getInstance()
                                .getHoursWorked().lastweekTimesWorked.timeSpan.get(
                                        position).getFromTo(), "h:mm a");
                        timeLabel.setText(time.substring(0, time.lastIndexOf(" ")));
                    }
                    break;
                default:
                    // Nothing
                    break;
            }

            return row;
        }
    }

    private void setupHoursList() {

        sizeArray = new String[totalSize];
        for (String str : sizeArray)
            str = "value";

        mHoursList.setAdapter(null);

        Drawable drawableDivider = new ColorDrawable(
                android.R.color.transparent);
        mHoursList.setCacheColorHint(Color.rgb(62, 81, 101));

        mHoursList.setDivider(drawableDivider);
        mHoursList.setDividerHeight(6); // Pixel spacing in-between items

        mHoursList.setPadding(12, 0, 12, 0);

        mHoursList.setAdapter(new MyCustomAdapter(mContext,
                R.layout.row_hours_worked, sizeArray));
        mHoursList.setTextFilterEnabled(true);
    }

    private void setupClockHoursList() {

        linearlayoutClockInList.removeAllViews();
        LayoutInflater objectInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        switch (currentClockInHourSetup) {
            case NAV_TODAY:
                for (int i = 0; i < AppDataSingleton.getInstance().getHoursWorked().todaysTimeClockRecords.timeSpan
                        .size(); i++) {
                    View row = objectInflater.inflate(R.layout.row_hours_worked,
                            null);

                    row.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                            LayoutParams.WRAP_CONTENT));

                    TextView dayMonth = (TextView) row
                            .findViewById(R.id.hoursworkedDayMonth);
                    TextView dayDate = (TextView) row
                            .findViewById(R.id.hoursworkedDayDate);
                    TextView timeSpan = (TextView) row
                            .findViewById(R.id.hoursworkedLabel);
                    TextView timeLength = (TextView) row
                            .findViewById(R.id.hoursworkedTime);

                    // Get Day/Month/Date using dateparser
                    DateFormat df;
                    String dayOfWeekMonth = "";
                    String dateOfMonth = "";
                    String date = AppDataSingleton.getInstance().getHoursWorked().todaysTimeClockRecords.timeSpan
                            .get(i).getFromTo();
                    if (!date.contains("Started at"))
                        df = new SimpleDateFormat("hh:mm a - hh:mm a MM/dd/yyyy");
                    else {
                        String temp = "";
                        temp = date.substring(11, date.length());
                        date = temp;
                        df = new SimpleDateFormat("hh:mm a MM/dd/yyyy");
                    }

                    String fromTo = AppDataSingleton.getInstance().getHoursWorked().todaysTimeClockRecords.timeSpan
                            .get(i).getFromTo();
                    String timeSpanOnly = fromTo.substring(0,
                            fromTo.lastIndexOf(" ")).toUpperCase();

                    try {

                        int hoursDifference = hoursDifference(UserUtilitiesSingleton.getInstance().user.getTimeZone(), TimeZone.getDefault());
                        Date today = df.parse(date);


                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(today);
                        calendar.add(Calendar.HOUR_OF_DAY, hoursDifference);

                        today = calendar.getTime();
                        //       TimeZone.getTimeZone(UserUtilitiesSingleton.getInstance().)

                        DateFormat formatter = new SimpleDateFormat("EE MMM");
                        dayOfWeekMonth = formatter.format(today);

                        formatter = new SimpleDateFormat("dd");
                        dateOfMonth = formatter.format(today);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    // Set labels appropriately
                    dayMonth.setText(dayOfWeekMonth);
                    dayDate.setText(dateOfMonth);
                    timeSpan.setText(timeSpanOnly);
                    timeLength.setTypeface(Typeface.DEFAULT_BOLD);
                    timeLength.setText(AppDataSingleton.getInstance()
                            .getHoursWorked().todaysTimeClockRecords.timeSpan
                            .get(i).getTimeWorked());

                    linearlayoutClockInList.addView(row);
                }
                break;

            case NAV_THISWEEK:
                for (int i = 0; i < AppDataSingleton.getInstance().getHoursWorked().thisweekTimeClockRecords.timeSpan
                        .size(); i++) {
                    View row = objectInflater.inflate(R.layout.row_hours_worked,
                            null);

                    row.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                            LayoutParams.WRAP_CONTENT));

                    TextView dayMonth = (TextView) row
                            .findViewById(R.id.hoursworkedDayMonth);
                    TextView dayDate = (TextView) row
                            .findViewById(R.id.hoursworkedDayDate);
                    TextView timeSpan = (TextView) row
                            .findViewById(R.id.hoursworkedLabel);
                    TextView timeLength = (TextView) row
                            .findViewById(R.id.hoursworkedTime);

                    // Get Day/Month/Date using dateparser
                    DateFormat df;
                    df = new SimpleDateFormat("hh:mm a - hh:mm a MM/dd/yyyy");
                    String dayOfWeekMonth = "";
                    String dateOfMonth = "";
                    String date = AppDataSingleton.getInstance().getHoursWorked().thisweekTimeClockRecords.timeSpan
                            .get(i).getFromTo();

                    if (!date.contains("Started at"))
                        df = new SimpleDateFormat("hh:mm a - hh:mm a MM/dd/yyyy");
                    else {
                        String temp = "";
                        temp = date.substring(11, date.length());
                        date = temp;
                        df = new SimpleDateFormat("hh:mm a MM/dd/yyyy");
                    }

                    String fromTo = AppDataSingleton.getInstance().getHoursWorked().thisweekTimeClockRecords.timeSpan
                            .get(i).getFromTo();
                    String timeSpanOnly = fromTo.substring(0,
                            fromTo.lastIndexOf(" ")).toUpperCase();

                    try {
                        Date today = df.parse(date);

                        DateFormat formatter = new SimpleDateFormat("EE MMM");
                        dayOfWeekMonth = formatter.format(today);

                        formatter = new SimpleDateFormat("dd");
                        dateOfMonth = formatter.format(today);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    // Set labels appropriately
                    dayMonth.setText(dayOfWeekMonth);
                    dayDate.setText(dateOfMonth);
                    timeSpan.setText(timeSpanOnly);
                    timeLength.setTypeface(Typeface.DEFAULT_BOLD);
                    timeLength.setText(AppDataSingleton.getInstance()
                            .getHoursWorked().thisweekTimeClockRecords.timeSpan
                            .get(i).getTimeWorked());

                    linearlayoutClockInList.addView(row);
                }
                break;

            case NAV_LASTWEEK:
                for (int i = 0; i < AppDataSingleton.getInstance().getHoursWorked().lastweekTimeClockRecords.timeSpan
                        .size(); i++) {
                    View row = objectInflater.inflate(R.layout.row_hours_worked,
                            null);

                    row.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                            LayoutParams.WRAP_CONTENT));

                    TextView dayMonth = (TextView) row
                            .findViewById(R.id.hoursworkedDayMonth);
                    TextView dayDate = (TextView) row
                            .findViewById(R.id.hoursworkedDayDate);
                    TextView timeSpan = (TextView) row
                            .findViewById(R.id.hoursworkedLabel);
                    TextView timeLength = (TextView) row
                            .findViewById(R.id.hoursworkedTime);

                    // Get Day/Month/Date using dateparser
                    DateFormat df;
                    df = new SimpleDateFormat("hh:mm a - hh:mm a MM/dd/yyyy");
                    String dayOfWeekMonth = "";
                    String dateOfMonth = "";
                    String date = AppDataSingleton.getInstance().getHoursWorked().lastweekTimeClockRecords.timeSpan
                            .get(i).getFromTo();

                    if (!date.contains("Started at"))
                        df = new SimpleDateFormat("hh:mm a - hh:mm a MM/dd/yyyy");
                    else {
                        String temp = "";
                        temp = date.substring(11, date.length());
                        date = temp;
                        df = new SimpleDateFormat("hh:mm a MM/dd/yyyy");
                    }

                    String fromTo = AppDataSingleton.getInstance().getHoursWorked().lastweekTimeClockRecords.timeSpan
                            .get(i).getFromTo();
                    String timeSpanOnly = fromTo.substring(0,
                            fromTo.lastIndexOf(" ")).toUpperCase();

                    try {
                        Date today = df.parse(date);

                        DateFormat formatter = new SimpleDateFormat("EE MMM");
                        dayOfWeekMonth = formatter.format(today);

                        formatter = new SimpleDateFormat("dd");
                        dateOfMonth = formatter.format(today);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    // Set labels appropriately
                    dayMonth.setText(dayOfWeekMonth);
                    dayDate.setText(dateOfMonth);
                    timeSpan.setText(timeSpanOnly);
                    timeLength.setTypeface(Typeface.DEFAULT_BOLD);
                    timeLength.setText(AppDataSingleton.getInstance()
                            .getHoursWorked().lastweekTimeClockRecords.timeSpan
                            .get(i).getTimeWorked());

                    linearlayoutClockInList.addView(row);
                }
                break;
            default:
                // Nothing
                break;
        }
    }

    // private class GetMyHoursTask extends AsyncTask<String, Void, Boolean> {
    //
    // /** application context. */
    // private Context context;
    // private NotificationSlider notificationSlider;
    //
    // public GetMyHoursTask(Activity activity) {
    // context = activity;
    // notificationSlider = new NotificationSlider(activity);
    // }
    //
    // @Override
    // protected void onPreExecute() {
    // notificationSlider
    // .startNotification(context.getResources().getString(
    // R.string.async_task_string_loading_hours_worked));
    // }
    //
    // @Override
    // protected void onPostExecute(final Boolean success) {
    //
    // if (success && xmlSuccessful) {
    //
    // notificationSlider
    // .stopNotificationOnSuccess(context
    // .getResources()
    // .getString(
    // R.string.async_task_string_hours_worked_loaded_successfully));
    //
    // setupStatusesUI();
    // } else {
    // notificationSlider.stopNotificationOnFailure();
    // // CommonUtilities.displayErrorMessage(context, true, "");
    // }
    // }
    //
    // @Override
    // protected Boolean doInBackground(final String... args) {
    // try {
    //
    // return xmlSuccessful = QueryServerUtilities.queryMyHours();
    // } catch (Exception e) {
    // if
    // (AppDataSingleton.getInstance().getErrorUtility().isErrorDisplayedToUser())
    // AppDataSingleton.getInstance().getErrorUtility().handleErrorMessage(e.toString());
    // return false;
    // }
    // }
    // }

    private class GetTimeClockTask extends BaseUiReportTask<String> {

        GetTimeClockTask() {
            super(ActivityTimeClockView.this,
                    R.string.async_task_string_loading_hours_worked);
        }

        @Override
        protected void onSuccess() {
            setupClockInUI();
        }

        @Override
        protected boolean taskBody(final String... args) throws Exception {

            int techId;
            if (techPosition == 0)
                techId = UserUtilitiesSingleton.getInstance().user.getId();
            else
                techId = AppDataSingleton.getInstance().getClockTechnician()
                        .get(techPosition).getId();

            RESTHoursWorkedList.query(techId);
            return true;
        }
    }

    private class UpdateTimeClockStatusTask extends BaseUiReportTask<String> {
        int participantId;
        String updateType;

        String currentDateTime;

        UpdateTimeClockStatusTask(String updateType) {
            super(
                    ActivityTimeClockView.this,
                    updateType.equals("IN") ? R.string.async_task_string_clocking_in
                            : R.string.async_task_string_clocking_out);
            this.updateType = updateType;

            if (techPosition == 0) {
                this.participantId = UserUtilitiesSingleton.getInstance().user
                        .getServiceProviderId();
            } else {
                this.participantId = AppDataSingleton.getInstance()
                        .getClockTechnician().get(techPosition).getId();
            }
        }

        @Override
        protected void onSuccess() {
            new GetTimeClockTask().execute();

            if (techPosition != 0) {
                TimeClockTechnician timeClockTech = AppDataSingleton.getInstance().getClockTechnician().get(techPosition);
                timeClockTech.setTimeClockMethod(updateType);
                if ("IN".equals(updateType))
                    timeClockTech.setTimeClockMethodDate(currentDateTime);
                else
                    timeClockTech.setTimeClockMethodDate("");
            } else {
                ServiceProvider hoursWorked = AppDataSingleton.getInstance().getHoursWorked();
                hoursWorked.setClockInTime(currentDateTime);
                if ("IN".equals(updateType))
                    hoursWorked.setClockedIn(true);
                else hoursWorked.setClockedIn(false);
            }
        }

        @Override
        protected boolean taskBody(final String... args) throws Exception {
            DateFormat df = null;
            df = new SimpleDateFormat("M/d/yy h:mm a");
            Date todaysDate = new Date();// get current date time with
            // Date()
            currentDateTime = df.format(todaysDate).replace("AM", "am").replace("PM", "pm");

            RESTTimeclockList.update(participantId, updateType,
                    currentDateTime, String.valueOf(latitude),
                    String.valueOf(longitude));
            return true;
        }
    }

    private class ClockThread extends Thread {
        @Override
        public void run() {

            while (true) {
                try {
                    Thread.sleep(10000);
                    handler.post(new Runnable() {

                        @Override
                        public void run() {
							/*
							 * This actually sets up the correct current time on
							 * the clock
							 */
                            Calendar cal = Calendar.getInstance();
                            int minute = cal.get(Calendar.MINUTE);
                            // 12 hour format
                            int hour = cal.get(Calendar.HOUR);
                            if (hour == 0)
                                hour = 12;

                            int hourofday = cal.get(Calendar.HOUR_OF_DAY);

                            textLargeClockHour.setText(String.format("%02d",
                                    hour));
                            textLargeClockMinute.setText(String.format("%02d",
                                    minute));
                            // 24 hour format

                            if (hourofday >= 12)
                                textLargeClockAMPM.setText("PM");
                            else
                                textLargeClockAMPM.setText("AM");
                        }
                    });
                } catch (Exception e) {
                    Log.e("Skeds", e.toString());
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Non-GPS devices will crash here otherwise=
        if (SkedsApplication.getInstance().isUseGps()) {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 12000, 10f,
                    this);
        } else {
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000,
                    300f, this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.v(DEBUG_TAG, "Location Changed");

        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }

    @Override
    public void onProviderDisabled(String provider) {
		/* this is called if/when the GPS is disabled in settings */
        Log.v(DEBUG_TAG, "GPS/Network Location Disabled");

        Toast.makeText(mContext,
                "Enable GPS and Wireless Network Location to continue",
                Toast.LENGTH_LONG).show();

		/* bring up the GPS settings */
        Intent intent = new Intent(
                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.v(DEBUG_TAG,
                mContext.getResources().getString(
                        R.string.toast_string_gps_enabled));
        Toast.makeText(
                mContext,
                mContext.getResources().getString(
                        R.string.toast_string_gps_enabled), Toast.LENGTH_LONG)
                .show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
		/* This is called when the GPS status alters */
        switch (status) {
            case LocationProvider.OUT_OF_SERVICE:
                Log.v(DEBUG_TAG, "Status Changed: Out of Service");
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                Log.v(DEBUG_TAG, "Status Changed: Temporarily Unavailable");
                break;
            case LocationProvider.AVAILABLE:
                Log.v(DEBUG_TAG, "Status Changed: Available");
                break;
            default:
                // Nothing
                break;
        }
    }

    private int hoursDifference(TimeZone tz1, TimeZone tz2) {
        long currentTime = System.currentTimeMillis();
        int edtOffset = tz1.getOffset(currentTime);
        int gmtOffset = tz2.getOffset(currentTime);
        int hourDifference = (gmtOffset - edtOffset) / (1000 * 60 * 60);
        return Math.abs(hourDifference);
    }
}
