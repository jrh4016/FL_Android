package com.skeds.android.phone.business.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.skeds.android.phone.business.AsyncTasks.BaseUiReportTask;
import com.skeds.android.phone.business.Custom.AccountMenu;
import com.skeds.android.phone.business.Custom.QuickAction;
import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.InvoicePartOrder;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.PartOrder;
import com.skeds.android.phone.business.Utilities.General.CommonUtilities;
import com.skeds.android.phone.business.Utilities.General.UserUtilitiesSingleton;
import com.skeds.android.phone.business.Utilities.REST.Objects.RESTPartOrderList;

import java.util.List;

public class ActivityPartOrdersListView extends BaseSkedsActivity
        implements
        View.OnClickListener {

    private List<InvoicePartOrder> listInvoicePartOrder = null;

    private LinearLayout headerLayout;
    private ImageView headerButtonUser;
    private ImageView headerButtonBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_part_orders_list);
        initHeader();
        findViewById(R.id.btn_add).setVisibility(View.GONE);
        if (!CommonUtilities.isNetworkAvailable(this)) {
            Toast.makeText(this, "Network connection unavailable.",
                    Toast.LENGTH_SHORT).show();
            finish();
        } else {
            new GetPartOrdersTask().execute();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            new GetPartOrdersTask().execute();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                return;
            default:
                break;
        }
    }

    private final class POAdapter extends BaseAdapter
            implements
            ListView.OnItemClickListener {
        final Object list[];
        final LayoutInflater inflater;
        int total = 0;

        POAdapter(List<InvoicePartOrder> inlist) {
            inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final int s = inlist.size();
            int i, listI = 0;
            for (i = 0; i < s; i++)
                listI += 1 + inlist.get(i).partOrder.size();
            list = new Object[listI];
            listI = 0;
            for (i = 0; i < s; i++) {
                InvoicePartOrder ipo = inlist.get(i);
                list[listI++] = ipo;
                int ii, ss = ipo.partOrder.size();
                for (ii = 0; ii < ss; ii++) {
                    PartOrder po = ipo.partOrder.get(ii);
                    list[listI++] = po;
                    total += po.getPriceCents();
                }
            }
        }

        @Override
        public boolean isEnabled(int pos) {
            Object x = getItem(pos);
            return x instanceof PartOrder;
            // return true;
        }

        @Override
        public int getItemViewType(int pos) {
            Object x = getItem(pos);
            return x instanceof InvoicePartOrder ? 0 : 1;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getCount() {
            return list.length;
        }

        @Override
        public Object getItem(int pos) {
            return list[pos];
        }

        @Override
        public long getItemId(int pos) {
            return pos;
        }

        @Override
        public View getView(int pos, View convertView, ViewGroup parent) {
            Object x = getItem(pos);
            if (x instanceof InvoicePartOrder) {
                final InvoicePartOrder ipo = (InvoicePartOrder) x;
                if (convertView == null) {
                    convertView = inflater
                            .inflate(R.layout.row_partorder_invoce_header,
                                    parent, false);
                }
                ((TextView) convertView).setText(ipo.getCustomerName() + "\n"
                        + ipo.getInvoiceDate() + " Invoice "
                        + ipo.getInvoiceNumber());
            } else if (x instanceof PartOrder) {
                final PartOrder po = (PartOrder) x;
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.row_partorder_body,
                            parent, false);
                }
                ((TextView) convertView.findViewById(R.id.cell_qty)).setText(po
                        .getQuantity());
                ((TextView) convertView.findViewById(R.id.cell_details))
                        .setText("" + po.getName());
                ((TextView) convertView.findViewById(R.id.cell_price))
                        .setText(po.getPrice());
                ((TextView) convertView.findViewById(R.id.cell_status))
                        .setText("" + po.getUserStatus());
            }
            return convertView;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int pos,
                                long id) {
            Object x = getItem(pos);
            if (x instanceof PartOrder) {
                final PartOrder po = (PartOrder) x;
                ActivityPartOrderAddEdit.launch(
                        ActivityPartOrdersListView.this, po, null);
            }
        }
    }

    private void initHeader() {
        final QuickAction accountMenu;
        accountMenu = AccountMenu.setupMenu(this, this);

        headerLayout = (LinearLayout) findViewById(R.id.activity_header);
        if (headerLayout != null) {
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
        }

    }

    private void showFullList(List<InvoicePartOrder> inlist) {
        ListView listView = (ListView) findViewById(R.id.partorders_listview);
        POAdapter adapter = new POAdapter(inlist);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(adapter);
        final int total = adapter.total;
//		if (total > 0) {
//			final TextView totalView = (TextView) findViewById(R.id.text_total);
//			totalView.setVisibility(View.VISIBLE);
//			totalView.setText(String.format("Total: $%d.%02d", total / 100,
//					total % 100));
//		}
    }

    /**
     * Getting Part orders list
     */
    private class GetPartOrdersTask extends BaseUiReportTask<String> {

        GetPartOrdersTask() {
            super(ActivityPartOrdersListView.this, "Loading all part orders...");
            setAutocloseOnNotSuccess(true);
        }

        @Override
        protected void onSuccess() {
            showFullList(listInvoicePartOrder);
        }

        @Override
        protected boolean taskBody(final String... args) throws Exception {
            listInvoicePartOrder = RESTPartOrderList.query(UserUtilitiesSingleton.getInstance().user
                    .getOwnerId());
            return true;
        }
    }
}