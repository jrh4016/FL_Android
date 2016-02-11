package com.skeds.android.phone.business.Custom;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.LineItem;
import com.skeds.android.phone.business.activities.ActivityAppointmentOnTruckAddedFragment;
import com.skeds.android.phone.business.ui.fragment.OnTruckAddedFragment;

import java.util.List;

public class OnTruckAddedAdapter extends ArrayAdapter<LineItem> {

    private Context context;

    private List<LineItem> onTruckList;

    private DeleteOnItemListener deleteItemListener;

    private OnClickListener listener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (deleteItemListener == null)
                return;
            View parent = (View) v.getParent();
            ViewHolder holder = (ViewHolder) parent.getTag();
            if (holder.id != 0) {
                for (LineItem item : onTruckList) {
                    if (item.getId() == holder.id) {
                        deleteItemListener.onDelete(onTruckList.indexOf(item));
                    }
                }
            }
        }
    };

    public OnTruckAddedAdapter(Context context) {
        super(context, R.layout.row_on_truck_added, AppDataSingleton.getInstance().getAppointment().getOnTruckList());
        this.context = context;
        onTruckList = AppDataSingleton.getInstance().getAppointment().getOnTruckList();

        if (context instanceof ActivityAppointmentOnTruckAddedFragment) {
            FragmentManager fm = ((FragmentActivity) context).getSupportFragmentManager();
            Fragment f = fm.findFragmentById(R.id.on_truck_fragment);
            deleteItemListener = (OnTruckAddedFragment) f;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        ViewHolder holder;

        if (v == null) {
            LayoutInflater vi = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.row_on_truck_added, null);

            // cache view fields into the holder
            holder = new ViewHolder();
            holder.name = (TextView) v
                    .findViewById(R.id.on_truck_added_name);
            holder.quantity = (TextView) v
                    .findViewById(R.id.on_truck_added_quantity);
            holder.buttonAddDelete = (ImageView) v
                    .findViewById(R.id.on_truck_added_button_add);
            // associate the holder with the view for later lookup
            v.setTag(holder);
        } else {
            // view already exists, get the holder instance from the view
            holder = (ViewHolder) v.getTag();
        }

        if (position == 0) {
            holder.id = 0;
            holder.name.setText("Add Product");
            holder.name.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
            holder.quantity.setText("");
            holder.buttonAddDelete.setClickable(false);
            holder.buttonAddDelete.setImageResource(R.drawable.icon_add_on_truck);
        } else {
            holder.id = onTruckList.get(position).getId();
            holder.name.setText(onTruckList.get(position).getName());
            holder.name.setTextColor(context.getResources().getColor(android.R.color.black));
            holder.quantity.setText(onTruckList.get(position).getQuantity() + "");
            holder.buttonAddDelete.setOnClickListener(listener);
            holder.buttonAddDelete.setImageResource(R.drawable.icon_delete_on_truck);
        }

        return v;
    }

    public static class ViewHolder {
        public int id;
        TextView name;
        TextView quantity;
        ImageView buttonAddDelete;
    }

}
