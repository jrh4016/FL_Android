package com.skeds.android.phone.business.activities;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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
import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.CommonUtilities;
import com.skeds.android.phone.business.Utilities.General.UserUtilitiesSingleton;
import com.skeds.android.phone.business.Utilities.REST.Objects.RESTHoursWorkedList;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ActivityHoursWorkedListView extends BaseSkedsActivity {

    private static final int NAV_TODAY = 0;
    private static final int NAV_THISWEEK = 1;
    private static final int NAV_LASTWEEK = 2;

    private LinearLayout headerLayout;
    private ImageView headerButtonUser;

    private ImageView navButtonHoursToday, navButtonHoursThisWeek,
            navButtonHoursLastWeek;

    private TextView textviewTotalHours;
    private TextView textviewTotalHoursWording;

    private ListView listviewHoursWorked;
    private String[] sizeArray;
    private int totalSize;

    private int mCurrentHoursSetup;

    private Activity mActivity;
    private Context mContext;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_my_hours_view);

        headerLayout = (LinearLayout) findViewById(R.id.activity_header);

        mActivity = ActivityHoursWorkedListView.this;
        mContext = this;

        final QuickAction accountMenu;
        accountMenu = AccountMenu.setupMenu(mContext, mActivity);

        headerButtonUser = (ImageView) headerLayout
                .findViewById(R.id.header_button_user);

        headerButtonUser.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                accountMenu.show(v);
                accountMenu.setAnimStyle(QuickAction.ANIM_GROW_FROM_CENTER);
            }
        });

        listviewHoursWorked = (ListView) findViewById(R.id.activity_my_hours_listview_hours);

        navButtonHoursToday = (ImageView) headerLayout
                .findViewById(R.id.header_nav_hours_worked_imageview_nav_today);
        navButtonHoursThisWeek = (ImageView) headerLayout
                .findViewById(R.id.header_nav_hours_worked_imageview_nav_this_week);
        navButtonHoursLastWeek = (ImageView) headerLayout
                .findViewById(R.id.header_nav_hours_worked_imageview_nav_last_week);

        textviewTotalHours = (TextView) headerLayout
                .findViewById(R.id.header_nav_hours_worked_textview_total);
        textviewTotalHoursWording = (TextView) headerLayout
                .findViewById(R.id.header_nav_hours_worked_textview_information);

        navButtonHoursToday.setOnClickListener(buttonListener);
        navButtonHoursThisWeek.setOnClickListener(buttonListener);
        navButtonHoursLastWeek.setOnClickListener(buttonListener);

        if (!CommonUtilities.isNetworkAvailable(mContext)) {
            Toast.makeText(mContext, "Network connection unavailable.",
                    Toast.LENGTH_SHORT).show();
            finish();
        } else {
            new GetHoursWorkedTask().execute();
        }
    }


    private OnClickListener buttonListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.header_nav_hours_worked_imageview_nav_today:
                    if (mCurrentHoursSetup != NAV_TODAY) {
                        mCurrentHoursSetup = NAV_TODAY;
                        setActiveNavigation(NAV_TODAY); // Today
                    }
                    break;
                case R.id.header_nav_hours_worked_imageview_nav_this_week:
                    if (mCurrentHoursSetup != NAV_THISWEEK) {
                        mCurrentHoursSetup = NAV_THISWEEK;
                        setActiveNavigation(NAV_THISWEEK); // This Week
                    }
                    break;
                case R.id.header_nav_hours_worked_imageview_nav_last_week:
                    if (mCurrentHoursSetup != NAV_LASTWEEK) {
                        mCurrentHoursSetup = NAV_LASTWEEK;
                        setActiveNavigation(NAV_LASTWEEK); // Last Week
                    }
                    break;
                default:
                    // Nothing
                    break;
            }

        }
    };

    private void setActiveNavigation(int navigation) {

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
                    textviewTotalHours
                            .setText(AppDataSingleton.getInstance().getHoursWorked().todayTimesWorked
                                    .getTotalHours());

                    totalSize = AppDataSingleton.getInstance().getHoursWorked().todayTimesWorked.timeSpan
                            .size();
                    setupHoursList();
                } else {
                    textviewTotalHours.setText("N/A");
                    listviewHoursWorked.setAdapter(null);
                }
                break;
            case NAV_THISWEEK: // This Week
                navButtonHoursThisWeek
                        .setImageResource(R.drawable.hours_nav_thisweek_pressed);
                textviewTotalHoursWording
                        .setText("Total Hours for This Week: ");

                if (AppDataSingleton.getInstance().getHoursWorked().thisweekTimesWorked.timeSpan != null) {
                    textviewTotalHours
                            .setText(AppDataSingleton.getInstance().getHoursWorked().thisweekTimesWorked
                                    .getTotalHours());

                    totalSize = AppDataSingleton.getInstance().getHoursWorked().thisweekTimesWorked.timeSpan
                            .size();
                    setupHoursList();
                } else {
                    textviewTotalHours.setText("N/A");
                    listviewHoursWorked.setAdapter(null);
                }
                break;
            case NAV_LASTWEEK: // Last Week
                navButtonHoursLastWeek
                        .setImageResource(R.drawable.hours_nav_lastweek_pressed);
                textviewTotalHoursWording
                        .setText("Total Hours for Last Week: ");
                if (AppDataSingleton.getInstance().getHoursWorked().lastweekTimesWorked.timeSpan != null) {
                    textviewTotalHours
                            .setText(AppDataSingleton.getInstance().getHoursWorked().lastweekTimesWorked
                                    .getTotalHours());

                    totalSize = AppDataSingleton.getInstance().getHoursWorked().lastweekTimesWorked.timeSpan
                            .size();
                    setupHoursList();
                } else {
                    textviewTotalHours.setText("N/A");
                    listviewHoursWorked.setAdapter(null);
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
            notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.row_hours_worked, parent,
                    false);

            switch (mCurrentHoursSetup) {
                case NAV_TODAY:
                    if (AppDataSingleton.getInstance().getHoursWorked().todayTimesWorked.timeSpan != null) {

                        String date = AppDataSingleton.getInstance().getHoursWorked().todayTimesWorked.timeSpan
                                .get(position).getFromTo();
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

                            DateFormat formatter = new SimpleDateFormat(
                                    "EE MMM");
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
                        nameLabel
                                .setText(AppDataSingleton.getInstance().getHoursWorked().todayTimesWorked.timeSpan
                                        .get(position).getCustomerName());

                        TextView statusDescription = (TextView) row
                                .findViewById(R.id.hoursworkedStatusDescription);
                        statusDescription
                                .setText(AppDataSingleton.getInstance().getHoursWorked().todayTimesWorked.timeSpan
                                        .get(position).getStatus());

                        TextView timeLabel = (TextView) row
                                .findViewById(R.id.hoursworkedTime);
                        String time = AppDataSingleton.getInstance().getHoursWorked().todayTimesWorked.timeSpan
                                .get(position).getFromTo();
                        timeLabel.setText(time.substring(0,
                                time.lastIndexOf(" ")));

                        TextView statusLabel = (TextView) row
                                .findViewById(R.id.hoursworkedLabel);
                        statusLabel.setText(AppDataSingleton.getInstance().getHoursWorked().getStatus());

                    }
                    break;
                case NAV_THISWEEK:
                    if (AppDataSingleton.getInstance().getHoursWorked().thisweekTimesWorked.timeSpan != null) {
                        String date = AppDataSingleton.getInstance().getHoursWorked().thisweekTimesWorked.timeSpan
                                .get(position).getFromTo();
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

                            DateFormat formatter = new SimpleDateFormat(
                                    "EE MMM");
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
                        nameLabel
                                .setText(AppDataSingleton.getInstance().getHoursWorked().thisweekTimesWorked.timeSpan
                                        .get(position).getCustomerName());

                        TextView statusDescription = (TextView) row
                                .findViewById(R.id.hoursworkedStatusDescription);
                        statusDescription
                                .setText(AppDataSingleton.getInstance().getHoursWorked().thisweekTimesWorked.timeSpan
                                        .get(position).getStatus());


                        TextView timeLabel = (TextView) row
                                .findViewById(R.id.hoursworkedTime);
                        String time = AppDataSingleton.getInstance().getHoursWorked().thisweekTimesWorked.timeSpan
                                .get(position).getFromTo();
                        timeLabel.setText(time.substring(0,
                                time.lastIndexOf(" ")));
                    }
                    break;
                case NAV_LASTWEEK:
                    if (AppDataSingleton.getInstance().getHoursWorked().lastweekTimesWorked.timeSpan != null) {
                        String date = AppDataSingleton.getInstance().getHoursWorked().lastweekTimesWorked.timeSpan
                                .get(position).getFromTo();
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

                            DateFormat formatter = new SimpleDateFormat(
                                    "EE MMM");
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
                        nameLabel
                                .setText(AppDataSingleton.getInstance().getHoursWorked().lastweekTimesWorked.timeSpan
                                        .get(position).getCustomerName());

                        TextView statusDescription = (TextView) row
                                .findViewById(R.id.hoursworkedStatusDescription);
                        statusDescription
                                .setText(AppDataSingleton.getInstance().getHoursWorked().lastweekTimesWorked.timeSpan
                                        .get(position).getStatus());

                        TextView timeLabel = (TextView) row
                                .findViewById(R.id.hoursworkedTime);
                        String time = AppDataSingleton.getInstance().getHoursWorked().lastweekTimesWorked.timeSpan
                                .get(position).getFromTo();
                        timeLabel.setText(time.substring(0,
                                time.lastIndexOf(" ")));
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

        listviewHoursWorked.setAdapter(null);

        Drawable drawableDivider = new ColorDrawable(
                android.R.color.transparent);
        listviewHoursWorked.setCacheColorHint(Color.rgb(62, 81, 101));

        listviewHoursWorked.setDivider(drawableDivider);
        listviewHoursWorked.setDividerHeight(6); // Pixel spacing in-between
        // items

        listviewHoursWorked.setPadding(12, 0, 12, 0);

        listviewHoursWorked.setAdapter(new MyCustomAdapter(mContext,
                R.layout.row_hours_worked, sizeArray));
        listviewHoursWorked.setTextFilterEnabled(true);
    }

    private class GetHoursWorkedTask extends BaseUiReportTask<String> {
        GetHoursWorkedTask() {
            super(ActivityHoursWorkedListView.this,
                    R.string.async_task_string_loading_hours_worked);
        }

        @Override
        protected void onSuccess() {
            setupHoursList();
            setActiveNavigation(NAV_TODAY);
        }

        @Override
        protected boolean taskBody(final String... args) throws Exception {
            RESTHoursWorkedList.query(UserUtilitiesSingleton.getInstance().user.getId());
            return true;
        }
    }
}
