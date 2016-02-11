package com.skeds.android.phone.business.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.skeds.android.phone.business.AsyncTasks.BaseUiReportTask;
import com.skeds.android.phone.business.Dialogs.DialogErrorPopup;
import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Estimate;
import com.skeds.android.phone.business.Utilities.General.Constants;
import com.skeds.android.phone.business.Utilities.General.UserUtilitiesSingleton;
import com.skeds.android.phone.business.Utilities.REST.Objects.RESTEstimate;
import com.skeds.android.phone.business.Utilities.REST.Objects.RESTEstimateList;
import com.skeds.android.phone.business.activities.ActivityAppointmentDualFragment;
import com.skeds.android.phone.business.activities.ActivityEstimateView;
import com.skeds.android.phone.business.core.SkedsApplication;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

public class EstimateListFragment extends BaseSkedsFragment {

    private PullToRefreshListView mPullRefreshListView;
    private ListView workEstimatesList;

    private Activity mActivity;

    private TextView buttonAddEstimate;

    private OnClickListener workOrderClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {

            ViewHolder holder = (ViewHolder) v.getTag();

            ActivityEstimateView.estimateId = holder.id;

            AppDataSingleton.getInstance().setEstimateViewType(
                    Constants.ESTIMATE_VIEW_TYPE_VIEW_EDIT);
            Intent i = new Intent(mActivity, ActivityEstimateView.class);
            i.putExtras(mActivity.getIntent());
            mActivity.startActivityForResult(i, 1);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mActivity = getActivity();

        return inflater.inflate(R.layout.layout_estimate_list_view, container,
                false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mPullRefreshListView = (PullToRefreshListView) mActivity
                .findViewById(R.id.activity_estimate__list_listview_work_estimate);
        buttonAddEstimate = (TextView) mActivity
                .findViewById(R.id.estimate_list_button_add);
        buttonAddEstimate.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startNewEstimate();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadEstimates();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SkedsApplication.getInstance().saveAppAndUserDataIntoFile();
    }

    // @Override
    // public void onActivityResult(int requestCode, int resultCode, Intent
    // data) {
    // super.onActivityResult(requestCode, resultCode, data);
    //
    // if (resultCode == Activity.RESULT_OK) {
    // mActivity.finish();
    // }
    // }

    private void startNewEstimate() {
        AppDataSingleton.getInstance().getEstimate().setSignature("");
        AppDataSingleton.getInstance().setEstimateViewType(
                Constants.ESTIMATE_VIEW_TYPE_ADD);
        Intent i = new Intent(mActivity, ActivityEstimateView.class);
        i.putExtras(mActivity.getIntent());
        AppDataSingleton.getInstance().setEstimate(new Estimate());
        mActivity.startActivity(i);
    }

    private void loadEstimates() {
        AppDataSingleton.getInstance().setEstimate(new Estimate()); // Needs to
        // be
        // cleared
        // out

        switch (AppDataSingleton.getInstance().getEstimateListViewMode()) {
            case Constants.ESTIMATE_LIST_VIEW_FROM_APPOINTMENT:
            case Constants.ESTIMATE_LIST_VIEW_FROM_PAST_APPOINTMENT:
                new GetAppointmentEstimatesTask(mActivity).execute();
                break;
            case Constants.ESTIMATE_LIST_VIEW_FROM_CUSTOMER:
                new GetCustomerEstimatesTask().execute();
                break;
            default:
                // Nothing
                break;
        }
    }

    private void setupUI() {

        // Set a listener to be invoked when the list should be refreshed.
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

        workEstimatesList = mPullRefreshListView.getRefreshableView();

        if (AppDataSingleton.getInstance().customerEstimateId != null) {
            workEstimatesList.setAdapter(null);

            // // Set a listener to be invoked when the list should be
            // refreshed.
            // ((PullToRefresh) workEstimatesList)
            // .setOnRefreshListener(new OnRefreshListener() {
            // @Override
            // public void onRefresh() {
            // // Do work to refresh the list here.
            //
            // }
            // });

            ArrayAdapter<String> adapter = null;
            adapter = new MyCustomAdapter(mActivity, R.layout.row_invoice_item,
                    AppDataSingleton.getInstance().customerEstimateDate);

            workEstimatesList.setAdapter(adapter);
            workEstimatesList.setTextFilterEnabled(true);
        }
    }

    public class MyCustomAdapter extends ArrayAdapter<String> {

        public MyCustomAdapter(Context context, int textViewResourceId,
                               String[] mCustomerEstimateDate) {
            super(context, textViewResourceId, mCustomerEstimateDate);
            this.notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            ViewHolder holder; // to reference the child views for later actions

            if (v == null) {
                LayoutInflater vi = (LayoutInflater) mActivity
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                v = vi.inflate(R.layout.row_estimate_with_delete_button, null);

                // cache view fields into the holder
                holder = new ViewHolder();
                holder.dayAndMonth = (TextView) v
                        .findViewById(R.id.row_invoice_item_textview_day_and_month);
                holder.dayOfMonth = (TextView) v
                        .findViewById(R.id.row_invoice_item_textview_month_date);
                holder.customerName = (TextView) v
                        .findViewById(R.id.row_invoice_item_textview_customer_name);
                holder.invoiceStatus = (TextView) v
                        .findViewById(R.id.row_invoice_item_textview_invoice_status);
                holder.invoiceDescription = (TextView) v
                        .findViewById(R.id.row_invoice_item_textview_invoice_description);
                holder.invoiceNumber = (TextView) v
                        .findViewById(R.id.row_invoice_item_textview_invoice_number);
                holder.deleteEstimate = (ImageView) v
                        .findViewById(R.id.delete_estimate);

                // associate the holder with the view for later lookup
                v.setTag(holder);
            } else {
                // view already exists, get the holder instance from the view
                holder = (ViewHolder) v.getTag();
            }

            if (AppDataSingleton.getInstance().customerEstimateDate[position] != null) {

                if (AppDataSingleton.getInstance().getEstimateListViewMode() == Constants.ESTIMATE_LIST_VIEW_FROM_APPOINTMENT) {
                    holder.deleteEstimate.setVisibility(View.VISIBLE);
                    holder.deleteEstimate.setOnClickListener(deleteListener);
                }

                DecimalFormat format = new DecimalFormat("#0.00");
                holder.customerName
                        .setText("Total Cost: "
                                + format.format(AppDataSingleton.getInstance().customerEstimateCost[position]));

                holder.invoiceDescription.setVisibility(View.GONE);

				/* this is the date portion */
                DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
                SimpleDateFormat formatDayMonth = new SimpleDateFormat(
                        "EEE MMM");
                SimpleDateFormat formatDate = new SimpleDateFormat("d");

                try {

                    holder.dayAndMonth
                            .setText(formatDayMonth.format(df
                                    .parse(AppDataSingleton.getInstance().customerEstimateDate[position])));
                    holder.dayOfMonth
                            .setText(formatDate.format(df
                                    .parse(AppDataSingleton.getInstance().customerEstimateDate[position])));
                } catch (Exception e) {

                }

                holder.id = AppDataSingleton.getInstance().customerEstimateId[position];
                v.setOnClickListener(workOrderClickListener);
            }

            return v;
        }

        private OnClickListener deleteListener = new OnClickListener() {

            @Override
            public void onClick(View v) {
                View parent = (View) v.getParent();
                ViewHolder holder = (ViewHolder) parent.getTag();

                new deleteEstimateTask(holder.id).execute();
            }
        };

    }

    static class ViewHolder {
        Integer id;

        TextView dayAndMonth;
        TextView dayOfMonth;
        TextView customerName;
        TextView invoiceStatus;
        TextView invoiceDescription;
        TextView invoiceNumber;

        ImageView deleteEstimate;
    }

    private class GetCustomerEstimatesTask extends BaseUiReportTask<String> {

        GetCustomerEstimatesTask() {
            super(mActivity, R.string.async_task_string_loading_estimates);
        }

        @Override
        protected void onSuccess() {
            setupUI();
        }

        @Override
        protected boolean taskBody(final String... args) throws Exception {
            RESTEstimateList.query(AppDataSingleton.getInstance().getCustomer()
                    .getId(), -1);
            return true;
        }
    }

    private class deleteEstimateTask extends BaseUiReportTask<String> {

        private int estimateId;

        public deleteEstimateTask(int estimateId) {
            super(mActivity, "Deleting Estimate...");
            this.estimateId = estimateId;
        }

        @Override
        protected void onSuccess() {
            super.onSuccess();
            Toast.makeText(
                    mActivity,
                    "This estimate has been successfully removed from this appointment. If needed, you can find this estimate in the estimate search screen for this customer.",
                    Toast.LENGTH_LONG).show();
            loadEstimates();
        }

        @Override
        protected boolean taskBody(final String... args) throws Exception {
            RESTEstimate.deleteFromAppointment(estimateId);
            return true;
        }

    }

    private class GetAppointmentEstimatesTask extends BaseUiReportTask<String> {

        GetAppointmentEstimatesTask(Activity activity) {
            super(mActivity, R.string.async_task_string_loading_estimates);
        }

        @Override
        protected void onSuccess() {

            if (!(mActivity instanceof ActivityAppointmentDualFragment)) {
                mActivity.finish();
            } else
                mActivity.onBackPressed();

            if (AppDataSingleton.getInstance().customerEstimateId == null) {
                startNewEstimate();
            } else if (AppDataSingleton.getInstance().customerEstimateId.length == 0) {
                startNewEstimate();
            } else {
                ActivityEstimateView.estimateId = AppDataSingleton
                        .getInstance().customerEstimateId[0];

                AppDataSingleton.getInstance().setEstimateViewType(
                        Constants.ESTIMATE_VIEW_TYPE_VIEW_EDIT);
                Intent i = new Intent(mActivity, ActivityEstimateView.class);
                i.putExtras(mActivity.getIntent());
                mActivity.startActivityForResult(i, 1);
            }
        }

        @Override
        protected boolean taskBody(final String... args) throws Exception {
            RESTEstimateList.query(AppDataSingleton.getInstance().getCustomer()
                    .getId(), AppDataSingleton.getInstance().getAppointment()
                    .getId());
            return true;
        }
    }

    private class PullToRefreshTask extends AsyncTask<Void, Void, Boolean> {
        Exception error = null;

        @Override
        protected Boolean doInBackground(Void... params) {
            // Simulates a background job.
            try {
                if (UserUtilitiesSingleton.getInstance().user.isLoggedIn()) {
                    if (UserUtilitiesSingleton.getInstance().user
                            .getTimeTrackable()) {

                        switch (AppDataSingleton.getInstance()
                                .getEstimateListViewMode()) {
                            case Constants.ESTIMATE_LIST_VIEW_FROM_APPOINTMENT:
                                RESTEstimateList.query(AppDataSingleton
                                                .getInstance().getCustomer().getId(),
                                        AppDataSingleton.getInstance()
                                                .getAppointment().getId());
                                return true;
                            case Constants.ESTIMATE_LIST_VIEW_FROM_CUSTOMER:
                                RESTEstimateList.query(AppDataSingleton
                                        .getInstance().getCustomer().getId(), -1);
                                return true;

                            case Constants.ESTIMATE_LIST_VIEW_FROM_PAST_APPOINTMENT:
                                // TODO - No idea
                                break;
                            default:
                                // Nothing
                                break;
                        }
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
                setupUI();
            } else {
                if (error != null)
                    new DialogErrorPopup(mActivity, "Could not load estimates",
                            null, error);
            }
        }
    }

}