package com.skeds.android.phone.business.Custom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.TimeClockTechnician;
import com.skeds.android.phone.business.Utilities.General.UserUtilitiesSingleton;

import java.util.List;

public class TimeClockTechsAdapter extends ArrayAdapter<TimeClockTechnician> {

    private Context context;

    private List<TimeClockTechnician> techsList;


    public TimeClockTechsAdapter(Context context) {
        super(context, R.layout.row_timeclock_technician, AppDataSingleton.getInstance().getClockTechnician());
        this.context = context;

        techsList = AppDataSingleton.getInstance().getClockTechnician();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.row_timeclock_technician_new, parent, false);

        TextView name = (TextView) rowView.findViewById(R.id.row_timeclock_technician_textview_technician_name);
        TextView status = (TextView) rowView.findViewById(R.id.row_timeclock_technician_textview_clock_in_out);

        if (position == 0) {
            name.setText(UserUtilitiesSingleton.getInstance().user.getFirstName() + " " + UserUtilitiesSingleton.getInstance().user.getLastName());
            status.setText(AppDataSingleton.getInstance().getHoursWorked().isClockedIn() ? "Clocked In" : "Clocked Out");
        } else {
            name.setText(techsList.get(position).getName());
            status.setText("IN".equals(techsList.get(position).getTimeClockMethod()) ? "Clocked In" : "Clocked Out");
        }

        if ("Clocked Out".equals(status.getText()))
            status.setTextColor(context.getResources().getColor(R.color.red));
        else
            status.setTextColor(context.getResources().getColor(R.color.green));

        rowView.setTag(Integer.valueOf(techsList.get(position).getId()));

        return rowView;

    }

}
