package com.skeds.android.phone.business.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;
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

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.skeds.android.phone.business.AsyncTasks.BaseUiReportTask;
import com.skeds.android.phone.business.Custom.AccountMenu;
import com.skeds.android.phone.business.Custom.QuickAction;
import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Agreement;
import com.skeds.android.phone.business.Utilities.General.CommonUtilities;
import com.skeds.android.phone.business.Utilities.General.Constants;
import com.skeds.android.phone.business.Utilities.General.UserUtilitiesSingleton;
import com.skeds.android.phone.business.Utilities.REST.Objects.RESTAgreementList;
import com.skeds.android.phone.business.Utilities.REST.Objects.RESTPastAppointmentList;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ActivityCustomerServiceAgreementsListView extends BaseSkedsActivity {

    private PullToRefreshListView mPullRefreshListView;
    private ListView serviceAgreementsList;

    private LinearLayout headerLayout;
    private ImageView headerButtonUser;
    private ImageView headerButtonBack;
    private TextView headerButtonAddServiceAgreement;

    private Activity mActivity;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_customer_service_agreements_list_view);
        AppDataSingleton.getInstance().getServiceAgreementList().clear();

        headerLayout = (LinearLayout) findViewById(R.id.activity_header);

        mActivity = ActivityCustomerServiceAgreementsListView.this;
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

        headerButtonAddServiceAgreement = (TextView) headerLayout
                .findViewById(R.id.header_standard_button_right);
        mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.activity_customer_service_agreements_listview_location);

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!CommonUtilities.isNetworkAvailable(mContext)) {
            Toast.makeText(mContext, "Network connection unavailable.",
                    Toast.LENGTH_SHORT).show();
            finish();
        } else {
            /* Gets information, sets up UI on success */
            new GetServiceAgreementsListTask().execute();
        }
    }


    private OnClickListener buttonListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.header_standard_button_right:
                    ActivityServiceAgreementAddEdit.editMode = false;

                    AppDataSingleton.getInstance().setServiceAgreement(new Agreement());
                    AppDataSingleton.getInstance().setServiceAgreementAddViewMode(Constants.SERVICE_AGREEMENT_ADD_VIEW_FROM_AGREEMENT_LIST);
                    Intent i = new Intent(mActivity,
                            ActivityServiceAgreementAddEdit.class);
                    startActivity(i);
                    break;
                default:
                    // Nothing
                    break;
            }
        }
    };

    private void setupUI() {

        // Set a listener to be invoked when the list should be refreshed.
        mPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener() {

            @Override
            public void onRefresh(PullToRefreshBase refreshView) {
                mPullRefreshListView.setLastUpdatedLabel(DateUtils
                        .formatDateTime(mContext, System.currentTimeMillis(),
                                DateUtils.FORMAT_SHOW_TIME
                                        | DateUtils.FORMAT_SHOW_DATE
                                        | DateUtils.FORMAT_ABBREV_ALL));

                if (!CommonUtilities.isNetworkAvailable(mContext)) {
                    Toast.makeText(mContext, "Network connection unavailable.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    // Do work to refresh the list here.
                    new PullToRefreshTask().execute();
                }
            }
        });

        serviceAgreementsList = mPullRefreshListView.getRefreshableView();

        headerButtonAddServiceAgreement.setOnClickListener(buttonListener);
        serviceAgreementsList.setAdapter(null);

        List<Agreement> agreementList = new ArrayList<Agreement>();
        agreementList.addAll(AppDataSingleton.getInstance().getServiceAgreementList());

            serviceAgreementsList.setAdapter(new MyCustomAdapter(mActivity,
                    R.layout.row_standard, agreementList));
            serviceAgreementsList.setTextFilterEnabled(true);

    }

    private class PullToRefreshTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            // Simulates a background job.
            try {
                if (UserUtilitiesSingleton.getInstance().user.isLoggedIn()) {
                    if (UserUtilitiesSingleton.getInstance().user.getTimeTrackable()) {

                        RESTPastAppointmentList.query(AppDataSingleton.getInstance().getCustomer()
                                .getId());
                        return true;
                    }
                }
            } catch (Exception e) {

            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {

            // Call onRefreshComplete when the list has been refreshed.
            mPullRefreshListView.onRefreshComplete();

            if (success)
                setupUI();
        }
    }

    private class MyCustomAdapter extends ArrayAdapter<Agreement> {
        public MyCustomAdapter(Context context, int textViewResourceId,
                               List<Agreement> sizeArray) {
            super(context, textViewResourceId, sizeArray);
            notifyDataSetChanged();
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.row_standard, parent, false);

            DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
            SimpleDateFormat formatDayMonth = new SimpleDateFormat("EEE MMM");
            SimpleDateFormat formatDate = new SimpleDateFormat("dd");

            try {
                TextView skedDayMonth = (TextView) row
                        .findViewById(R.id.skedDayMonth);
                skedDayMonth.setText(formatDayMonth.format(df
                        .parse(getItem(position)
                                .getStartDate())));

                TextView skedDayDate = (TextView) row
                        .findViewById(R.id.skedDayDate);
                skedDayDate.setText(formatDate.format(df
                        .parse(getItem(position)
                                .getStartDate())));
            } catch (Exception e) {

            }

            // Set Sked Name Label
            TextView skedLabel = (TextView) row.findViewById(R.id.skedLabel);

            if (!TextUtils.isEmpty(getItem(position)
                    .getServicePlanName()))
                skedLabel.setText(getItem(position)
                                    .getServicePlanName());
            else
                skedLabel.setText("");

            // Appointment Status Label
            TextView skedAppointmentStatus = (TextView) row
                    .findViewById(R.id.skedStatus);
            skedAppointmentStatus.setVisibility(View.GONE);

            // Put description to Sked Time Label
            TextView skedTime = (TextView) row.findViewById(R.id.skedTime);
            skedTime.setText(getItem(position).getDescription());

            row.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {

                    // TODO - Update, pass this through the bundle
                    ActivityServiceAgreementView.agreementId = getItem(position).getId();

                    AppDataSingleton.getInstance().setServiceAgreementViewMode(Constants.SERVICE_AGREEMENT_VIEW_FROM_CUSTOMER);
                    Intent i = new Intent(mActivity,
                            ActivityServiceAgreementView.class);
                    startActivity(i);
                }
            });

            return row;
        }
    }

    private class GetServiceAgreementsListTask extends BaseUiReportTask<String> {

        public GetServiceAgreementsListTask() {
            super(ActivityCustomerServiceAgreementsListView.this,
                    R.string.async_task_string_loading_service_agreements);
        }

        @Override
        protected void onSuccess() {
            setupUI();
        }

        @Override
        protected boolean taskBody(final String... args) throws Exception {
            RESTAgreementList.query(AppDataSingleton.getInstance().getCustomer().getId());
            return true;
        }
    }
}