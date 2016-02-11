package com.skeds.android.phone.business.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.skeds.android.phone.business.AsyncTasks.BaseUiReportTask;
import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.PartOrder;
import com.skeds.android.phone.business.Utilities.REST.Objects.RESTPartOrderList;
import com.skeds.android.phone.business.activities.ActivityPartOrderAddEdit;

import java.util.List;

public class PartOrderApptListFragment extends BaseSkedsFragment implements
        View.OnClickListener {

    private int appointmentId;
    private int invoiceId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_part_orders, container,
                false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            appointmentId = args.getInt("appointmentId");
            invoiceId = args.getInt("invoiceId");
        }

        if (appointmentId < 0)
            throw new RuntimeException(
                    "appointmentId SHOULD be initialized with valid value");

        getActivity().findViewById(R.id.btn_add).setOnClickListener(this);
        new GetPartOrdersTask().execute();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == getActivity().RESULT_OK) {
            new GetPartOrdersTask().execute();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add:
                ActivityPartOrderAddEdit.launch(getActivity(), PartOrderApptListFragment.this, appointmentId);
                return;
            default:
                break;
        }
    }

    private final class POAdapter extends BaseAdapter
            implements
            ListView.OnItemClickListener {
        final List<PartOrder> inlist;
        final LayoutInflater inflater;

        POAdapter(List<PartOrder> inlist) {
            inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.inlist = inlist;
            android.util.Log.i("Skeds", "POAdapter got " + inlist.size()
                    + " orders");
        }

        @Override
        public int getCount() {
            android.util.Log.i("Skeds", "POAdapter getCount: " + inlist.size());
            return inlist.size();
        }

        @Override
        public Object getItem(int pos) {
            return inlist.get(pos);
        }

        @Override
        public long getItemId(int pos) {
            return pos;
        }

        @Override
        public View getView(int pos, View convertView, ViewGroup parent) {
            android.util.Log.i("Skeds", "POAdapter getView #" + pos);
            final PartOrder po = inlist.get(pos);
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.row_partorder_body,
                        parent, false);
            }
            ((TextView) convertView.findViewById(R.id.cell_qty)).setText(po
                    .getQuantity());
            ((TextView) convertView.findViewById(R.id.cell_details)).setText(""
                    + po.getName());
            ((TextView) convertView.findViewById(R.id.cell_price)).setText(po
                    .getPrice());
            ((TextView) convertView.findViewById(R.id.cell_status)).setText(""
                    + po.getUserStatus());
            return convertView;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int pos,
                                long id) {
            Object x = getItem(pos);
            if (x instanceof PartOrder) {
                final PartOrder po = (PartOrder) x;
                ActivityPartOrderAddEdit.launch(
                        getActivity(), po, PartOrderApptListFragment.this);
            }
        }
    }

    private void showApptList(List<PartOrder> inlist) {
        final ListView listView = (ListView) getActivity().findViewById(R.id.partorders_listview);
        POAdapter adapter = new POAdapter(inlist);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(adapter);
//		int total = 0;
//		for (PartOrder po : inlist)
//			total += po.getPriceCents()*Integer.parseInt(po.getQuantity());
//		if (total > 0) {
//			final TextView totalView = (TextView) getActivity().findViewById(R.id.text_total);
//			totalView.setVisibility(View.VISIBLE);
//			totalView.setText(String.format("Total: $%d.%02d", total / 100,
//					total % 100));
//		}
    }

    /**
     * Getting Part orders list
     */
    private class GetPartOrdersTask extends BaseUiReportTask<String> {
        List<PartOrder> listPartOrder = null;

        GetPartOrdersTask() {
            super(getActivity(), invoiceId > 0
                    ? "Loading part orders\nInvoice " + invoiceId + "..."
                    : "Loading part orders...");
            setAutocloseOnNotSuccess(true);
        }

        @Override
        protected void onSuccess() {
            showApptList(listPartOrder);
        }

        @Override
        protected boolean taskBody(String... args) throws Exception {
            listPartOrder = RESTPartOrderList
                    .queryForAppointment(appointmentId);
            return true;
        }
    }

}
