package com.skeds.android.phone.business.Custom;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Status;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.UpcomingAppointment;
import com.skeds.android.phone.business.Utilities.General.UserUtilitiesSingleton;
import com.skeds.android.phone.business.util.DateUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

public class DashboardApptsAdapter extends ArrayAdapter<UpcomingAppointment> {

    private Context context;

    private List<UpcomingAppointment> apptList = new ArrayList<UpcomingAppointment>();

    public DashboardApptsAdapter(Context context) {
        super(context, R.layout.row_standard, AppDataSingleton.getInstance()
                .getUpcomingAppointmentsList());
        this.context = context;

        apptList.clear();
        apptList.addAll(AppDataSingleton.getInstance().getUpcomingAppointmentsList());

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        ViewHolder holder;

        if (v == null) {
            LayoutInflater vi = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.row_standard, null);

            // cache view fields into the holder
            holder = new ViewHolder();

            holder.textDayOfWeekAndMonth = (TextView) v
                    .findViewById(R.id.skedDayMonth);
            holder.textDayDate = (TextView) v.findViewById(R.id.skedDayDate);
            holder.textAppointmentLabel = (TextView) v
                    .findViewById(R.id.skedLabel);
            holder.textAppointmentStatus = (TextView) v
                    .findViewById(R.id.skedStatus);
            holder.textAppointmentTime = (TextView) v
                    .findViewById(R.id.skedTime);

            v.setTag(holder);
        } else {
            // view already exists, get the holder instance from the view
            holder = (ViewHolder) v.getTag();
        }

        UpcomingAppointment upcomingAppointment;
        if (apptList == null) return new View(context);
        try {
            upcomingAppointment = apptList.get(position);
        } catch (Exception ex) {
            return new View(context);
        }

        holder.textDayOfWeekAndMonth.setText(
                DateUtils.convertFromPatternToPattern(
                        upcomingAppointment.getStartDate() + " " + upcomingAppointment.getStartTime(),
                        "MM/dd/yyyy hh:mm aaa",
                        "EEE MMM",
                        UserUtilitiesSingleton.getInstance().user.getTimeZone(),
                        TimeZone.getDefault()
                )
        );

        holder.textDayDate.setText(DateUtils.convertFromPatternToPattern(

                        upcomingAppointment.getStartDate() + " " + upcomingAppointment.getStartTime(),
                        "MM/dd/yyyy hh:mm aaa",
                        "d",
                        UserUtilitiesSingleton.getInstance().user.getTimeZone(),
                        TimeZone.getDefault()
                )
        );

        if (upcomingAppointment.isOrganization()) {
            holder.textAppointmentLabel.setText(upcomingAppointment
                    .getCustomerOrgName());
        } else {
            holder.textAppointmentLabel.setText(upcomingAppointment
                    .getCustomerFirstName()
                    + " "
                    + upcomingAppointment
                    .getCustomerLastName());
        }

        holder.textAppointmentStatus.setVisibility(View.VISIBLE);

        switch (upcomingAppointment.getStatus()) {

            case ON_ROUTE:
                holder.textAppointmentStatus.setText("On Route");
                holder.textAppointmentStatus.setTextColor(Color
                        .parseColor(Status.COLOR_ON_ROUTE)); // Blue
                break;
            case SUSPEND_APPOINTMENT:
                holder.textAppointmentStatus.setText("Paused");
                holder.textAppointmentStatus.setTextColor(Color
                        .parseColor(Status.COLOR_SUSPEND_APPOINTMENT)); // Red
                break;
            case PARTS_RUN_APPOINTMENT:
                holder.textAppointmentStatus.setText("Parts Run");
                holder.textAppointmentStatus.setTextColor(Color
                        .parseColor(Status.COLOR_PARTS_RUN)); // Orange
                break;
            case START_APPOINTMENT:
            case RESTART_APPOINTMENT:
                holder.textAppointmentStatus.setText("Working");
                holder.textAppointmentStatus.setTextColor(Color
                        .parseColor(Status.COLOR_START_APPOINTMENT)); // Green
                break;
            case FINISH_APPOINTMENT:
                holder.textAppointmentStatus.setText("Finished");
                holder.textAppointmentStatus.setTextColor(Color
                        .parseColor(Status.COLOR_FINISH_APPOINTMENT)); // Dark
                // Gray
                break;
            case NOT_STARTED:
                holder.textAppointmentStatus.setText("Not Started");
                holder.textAppointmentStatus.setTextColor(Color
                        .parseColor(Status.COLOR_NOT_STARTED)); // Gray
                holder.textAppointmentStatus.setTypeface(null, Typeface.ITALIC);
                break;
            case CLOSE_APPOINTMENT:
                holder.textAppointmentStatus.setTextColor(Color
                        .parseColor(Status.COLOR_CLOSE_APPOINTMENT)); // Black
                holder.textAppointmentStatus.setText("Closed ");
                holder.textAppointmentStatus.setTypeface(null, Typeface.BOLD_ITALIC);
                break;
            default:
                // Nothing
                break;
        }

        if (upcomingAppointment != null) {

            String startTime = DateUtils.convertFromPatternToPattern(

                    upcomingAppointment.getStartDate() + " " + upcomingAppointment.getStartTime(),
                    "MM/dd/yyyy hh:mm aaa",
                    "h:mm aaa",
                    UserUtilitiesSingleton.getInstance().user.getTimeZone(),
                    TimeZone.getDefault()
            );

            if (upcomingAppointment.getTimeZone()!=null&&!TimeZone.getDefault().equals(upcomingAppointment.getTimeZone())) {

                startTime += " (" +
                        DateUtils.convertFromPatternToPattern(

                                upcomingAppointment.getStartDate() + " " + upcomingAppointment.getStartTime(),
                                "MM/dd/yyyy hh:mm aaa",
                                "h:mm aaa",
                                UserUtilitiesSingleton.getInstance().user.getTimeZone(),
                                upcomingAppointment.getTimeZone()
                        ) + " "
                        + upcomingAppointment.getTimeZone().getDisplayName(false, TimeZone.SHORT) + " )";
            }

            holder.textAppointmentTime.setText(startTime + " "
                    + upcomingAppointment
                    .getLocationAddress());

        }

        return v;
    }

    private static class ViewHolder {
        TextView textDayOfWeekAndMonth;
        TextView textDayDate;
        TextView textAppointmentLabel;
        TextView textAppointmentStatus;
        TextView textAppointmentTime;
    }

}
