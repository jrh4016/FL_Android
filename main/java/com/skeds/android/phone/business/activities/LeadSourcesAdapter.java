package com.skeds.android.phone.business.activities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.LeadSource;

import java.util.List;

/**
 * Created by user_sca on 08.10.2014.
 */
public class LeadSourcesAdapter extends ArrayAdapter<LeadSource> {

    private Context context;

    public LeadSourcesAdapter(Context context, int resource, List<LeadSource> objects) {
        super(context, resource, objects);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.lead_sources_view_item, null);
        }


        LeadSource item = getItem(position);
        ((TextView) v.findViewById(R.id.lead_source_name)).setText("Name: "+item.getName());

        ((TextView) v.findViewById(R.id.lead_source_type)).setText("Type: " +item.getType());

        ((TextView) v.findViewById(R.id.lead_source_description)).setText("Description: " +item.getDescription());

        ((TextView) v.findViewById(R.id.lead_source_end_date_str)).setText("End Date: " +item.getEndDateStr());


        return v;


    }
}
