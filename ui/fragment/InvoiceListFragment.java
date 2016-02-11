package com.skeds.android.phone.business.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.skeds.android.phone.business.AsyncTasks.BaseUiReportTask;
import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.UserUtilitiesSingleton;
import com.skeds.android.phone.business.Utilities.REST.Objects.RESTInvoice;
import com.skeds.android.phone.business.Utilities.REST.Objects.RESTInvoiceList;
import com.skeds.android.phone.business.activities.ActivityDashboardView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.LinkedList;

public class InvoiceListFragment extends BaseSkedsFragment {

    OnHeadlineSelectedListener mCallback;


    private int mInvoiceId;
    public static int mSelectedItem;

    private PullToRefreshListView mPullRefreshListView;
    private ListView listInvoices;
    private LinkedList<String> mListItems;
    private TextView textNoInvoices;

    private Activity mActivity;
    public static boolean needsRefresh;

    public interface OnHeadlineSelectedListener {
        public void onArticleSelected();
    }

    private OnItemClickListener mInvoiceListClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View v, int position,
                                long id) {

            // TODO - All of these need to be passed via bundle intent
            // TODO - Move these to AsyncTask, and then assign to bundle
            mSelectedItem = (int) id;
            mInvoiceId = AppDataSingleton.getInstance().getInvoiceList().get((int) id).getId();

            if (AppDataSingleton.getInstance().getInvoiceList().get((int) id).isClosed())
                InvoiceFragment.isReadOnly = true;
            else
                InvoiceFragment.isReadOnly = false;

            // AppDataSingleton.getInstance().setLineItemMode(Constants.ADD_LINE_ITEMS_MODE_FOR_INVOICE);
            // AppDataSingleton.getInstance().setInvoiceMode(Constants.INVOICE_MODE_FROM_INVOICE_LIST);

            new GetSingleInvoiceTask().execute();
        }
    };

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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.layout_invoices_list_view, container,
                false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mPullRefreshListView = (PullToRefreshListView) mActivity
                .findViewById(R.id.activity_invoice_list_listview_invoices);
        textNoInvoices = (TextView) mActivity
                .findViewById(R.id.activity_invoice_list_textview_no_invoices);

        new GetInvoiceListTask().execute();
    }


    private void setupUI() {

        mPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener() {

            @Override
            public void onRefresh(PullToRefreshBase refreshView) {
                mPullRefreshListView.setLastUpdatedLabel(DateUtils
                        .formatDateTime(mActivity, System.currentTimeMillis(),
                                DateUtils.FORMAT_SHOW_TIME
                                        | DateUtils.FORMAT_SHOW_DATE
                                        | DateUtils.FORMAT_ABBREV_ALL));

                // Do work to refresh the list here.
                new PullToRefreshTask().execute();
            }
        });

        listInvoices = mPullRefreshListView.getRefreshableView();

        if (!AppDataSingleton.getInstance().getInvoiceList().isEmpty()) {
            textNoInvoices.setVisibility(View.GONE);
            String[] sizeArray = new String[AppDataSingleton.getInstance().getInvoiceList().size()];

            listInvoices.setOnItemClickListener(mInvoiceListClickListener);

            mListItems = new LinkedList<String>();
            mListItems.addAll(Arrays.asList(sizeArray));

            ArrayAdapter<String> adapter = new MyCustomAdapter(mActivity,
                    R.layout.row_invoice_item, mListItems);
            listInvoices.setAdapter(adapter);
        } else {
            textNoInvoices.setVisibility(View.VISIBLE);
        }
    }

    private class PullToRefreshTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            // Simulates a background job.
            try {
                if (UserUtilitiesSingleton.getInstance().user.isLoggedIn()) {
                    RESTInvoiceList.query();
                    return true;
                }
            } catch (Exception e) {

            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {

            // Call onRefreshComplete when the list has been refreshed.
            mPullRefreshListView.onRefreshComplete();

            if (!UserUtilitiesSingleton.getInstance().user.isLoggedIn()) {
                Intent i = new Intent(mActivity, ActivityDashboardView.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                // finish();
            } else {
                if (success)
                    setupUI();
            }
        }
    }

    private class GetSingleInvoiceTask extends BaseUiReportTask<String> {
        GetSingleInvoiceTask() {
            super(mActivity, R.string.async_task_string_loading_invoice);
        }

        @Override
        protected void onSuccess() {
            mCallback.onArticleSelected();
        }

        @Override
        protected boolean taskBody(final String... args) throws Exception {
            RESTInvoice.query(mInvoiceId);
            return true;
        }
    }

    private class GetInvoiceListTask extends BaseUiReportTask<String> {
        GetInvoiceListTask() {
            super(mActivity, R.string.async_task_string_loading_invoices);
        }

        @Override
        protected void onSuccess() {
            if (UserUtilitiesSingleton.getInstance().user.isLoggedIn()) {
                setupUI();
            }
        }

        @Override
        protected boolean taskBody(final String... args) throws Exception {
            RESTInvoiceList.query();
            return true;
        }
    }

    public class MyCustomAdapter extends ArrayAdapter<String> {

        public MyCustomAdapter(Context context, int textViewResourceId,
                               LinkedList<String> objects) {
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
                v = vi.inflate(R.layout.row_invoice_item, null);

                // cache view fields into the holder
                holder = new ViewHolder();
                holder.customerName = (TextView) v
                        .findViewById(R.id.row_invoice_item_textview_customer_name);
                holder.dayAndMonth = (TextView) v
                        .findViewById(R.id.row_invoice_item_textview_day_and_month);
                holder.dayOfMonth = (TextView) v
                        .findViewById(R.id.row_invoice_item_textview_month_date);
                holder.invoiceDescription = (TextView) v
                        .findViewById(R.id.row_invoice_item_textview_invoice_description);
                holder.invoiceNumber = (TextView) v
                        .findViewById(R.id.row_invoice_item_textview_invoice_number);
                holder.invoiceStatus = (TextView) v
                        .findViewById(R.id.row_invoice_item_textview_invoice_status);

                // associate the holder with the view for later lookup
                v.setTag(holder);
            } else {
                // view already exists, get the holder instance from the view
                holder = (ViewHolder) v.getTag();
            }

            holder.customerName.setText(AppDataSingleton.getInstance().getInvoiceList().get(position)
                    .getCustomerName());
            DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
            SimpleDateFormat formatDayMonth = new SimpleDateFormat("EEE MMM");
            SimpleDateFormat formatDate = new SimpleDateFormat("dd");

            try {

                holder.dayAndMonth
                        .setText(formatDayMonth.format(df.parse(AppDataSingleton.getInstance()
                                .getInvoiceList().get(position).getDate())));
                holder.dayOfMonth.setText(formatDate.format(df.parse(AppDataSingleton.getInstance()
                        .getInvoiceList().get(position).getDate())));
            } catch (Exception e) {
            }

            if (!TextUtils.isEmpty(AppDataSingleton.getInstance().getInvoiceList().get(position).getDescription()))
                holder.invoiceDescription.setText(AppDataSingleton.getInstance().getInvoiceList()
                        .get(position).getDescription());
            else {
                holder.invoiceDescription
                        .setText("No description for this invoice");
                holder.invoiceDescription.setTypeface(null, Typeface.ITALIC);
            }

            holder.invoiceNumber.setText("Invoice: "
                    + AppDataSingleton.getInstance().getInvoiceList().get(position).getNumber());

            if (AppDataSingleton.getInstance().getInvoiceList().get(position).isClosed()) {
                holder.invoiceStatus.setTextColor(Color.rgb(168, 72, 55));// Red
                holder.invoiceStatus.setText("Closed");
            } else {
                holder.invoiceStatus.setText("Open");
                holder.invoiceStatus.setTextColor(Color.rgb(140, 198, 63)); // Green
            }
            holder.invoiceStatus.setContentDescription("row_invoice_item_textview_invoice_" + AppDataSingleton.getInstance().getInvoiceList().get(position).getNumber() + "_status");

            return v;
        }

    }

    static class ViewHolder {
        TextView dayAndMonth;
        TextView dayOfMonth;
        TextView customerName;
        TextView invoiceStatus;
        TextView invoiceDescription;
        TextView invoiceNumber;
    }

}
