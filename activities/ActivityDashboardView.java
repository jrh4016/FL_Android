package com.skeds.android.phone.business.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.format.DateUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.skeds.android.phone.business.Custom.AccountMenu;
import com.skeds.android.phone.business.Custom.DashboardApptsAdapter;
import com.skeds.android.phone.business.Custom.QuickAction;
import com.skeds.android.phone.business.Dialogs.DialogRateUs;
import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.CommonUtilities;
import com.skeds.android.phone.business.Utilities.General.Constants;
import com.skeds.android.phone.business.Utilities.General.UserUtilitiesSingleton;
import com.skeds.android.phone.business.Utilities.NonfatalException;
import com.skeds.android.phone.business.Utilities.REST.Objects.RESTDashboardAppointmentList;
import com.skeds.android.phone.business.Utilities.REST.Objects.RESTTermsOfService;
import com.skeds.android.phone.business.core.SkedsApplication;
import com.skeds.android.phone.business.ui.fragment.AppointmentFragment;

public class ActivityDashboardView extends BaseSkedsActivity {

    private Activity mActivity;
    private Context mContext;
    private QuickAction accountMenu;

    private LinearLayout headerLayout;
    private ImageView headerButtonUser;

    private View buttonMySchedule;
    private View buttonMyHours;
    private View buttonCustomers;
    private View buttonInvoices;
    private View buttonPartOrders;
    private View buttonPricebook;

    private LinearLayout linearlayoutTechnicianStatus;

    private TextView textTechnicianName;
    private ImageView imageTechnicianStatus;
    private TextView textTechnicianStatus;
    private TextView textUpdateApplication;

    private boolean mLoadingUpcomingAppointments = false;
    private int mSingleAppointmentId;

    /*
     * Terms of Service Dialog
     */
    private Dialog dialogTermsOfService;
    private TextView dialogTermsOfServiceText;
    private TextView dialogTermsOfServiceAgree, dialogTermsOfServiceDisagree;

    private PullToRefreshListView mPullRefreshListView;

    private ListView dashboardList;
    private DashboardApptsAdapter dashboardApptsAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Crashlytics.start(this);

        setContentView(R.layout.a_dashboard_view);
        initLayout();
    }

    @Override
    protected void onResume() {
        super.onResume();

        SkedsApplication.getInstance().clearPriceBook();

        switch (SkedsApplication.getInstance().getApplicationMode()) {
            case Constants.APPLICATION_MODE_PHONE_SERVICE:

                // headerLayout = (LinearLayout)
                // findViewById(R.id.activity_header);
                headerButtonUser = (ImageView) headerLayout
                        .findViewById(R.id.header_button_user);
                headerButtonUser.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        accountMenu.show(v);
                        accountMenu.setAnimStyle(QuickAction.ANIM_GROW_FROM_CENTER);
                    }
                });

                setupUI();
                new GetUpcomingAppointmentsTask(mActivity).execute();

                break;
            case Constants.APPLICATION_MODE_TABLET_101_SERVICE:
                setupUI720();
                break;
            case Constants.APPLICATION_MODE_TABLET_7_SERVICE:
                setupUI600();
                break;
            default:
                // Nothing
                break;
        }

        SkedsApplication.getInstance().saveAppAndUserDataIntoFile();
    }

    @Override
    public void onBackPressed() {
        UserUtilitiesSingleton.getInstance().userQuitPrompt(mActivity,
                mContext, R.layout.dialog_layout_yes_no_response,
                R.id.dialog_yes_no_response_textview_title,
                R.id.dialog_yes_no_response_textview_body,
                R.id.dialog_yes_no_response_button_yes,
                R.id.dialog_yes_no_response_button_no);
    }

    private void initLayout() {
        mActivity = ActivityDashboardView.this;
        mContext = this;
        accountMenu = AccountMenu.setupMenu(mContext, mActivity);

        headerLayout = (LinearLayout) findViewById(R.id.activity_header);
        SkedsApplication.getInstance().setApplicationMode(
                headerLayout != null ? Constants.APPLICATION_MODE_PHONE_SERVICE
                        : Constants.APPLICATION_MODE_TABLET_101_SERVICE);

        buttonMySchedule = findViewById(R.id.activity_dashboard_button_my_schedule);
        buttonMyHours = findViewById(R.id.activity_dashboard_button_my_hours);
        buttonCustomers = findViewById(R.id.activity_dashboard_button_customers);
        buttonInvoices = findViewById(R.id.activity_dashboard_button_invoices);
        buttonPricebook = findViewById(R.id.activity_dashboard_button_pricebook);
        buttonPartOrders = findViewById(R.id.activity_dashboard_button_part_orders);

		/* Bottom Portion, Phone App only */
        textTechnicianName = (TextView) findViewById(R.id.activity_dashboard_textview_technician_name);
        textTechnicianStatus = (TextView) findViewById(R.id.activity_dashboard_textview_status);

        imageTechnicianStatus = (ImageView) findViewById(R.id.activity_dashboard_imageview_status_icon);
        linearlayoutTechnicianStatus = (LinearLayout) findViewById(R.id.activity_dashboard_linearlayout_status_bar);
        textUpdateApplication = (TextView) findViewById(R.id.activity_dashboard_textview_update_application);

        mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.dashboard_list);
    }

    private OnClickListener buttonListener = new OnClickListener() {

        @Override
        public void onClick(View v) {

            Intent i = null;

            switch (v.getId()) {

                case R.id.activity_dashboard_button_customers:
                    if (!CommonUtilities.isNetworkAvailable(mContext)) {
                        Toast.makeText(mContext, "Network connection unavailable.",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    i = new Intent(mContext, ActivityCustomerDualFragment.class);
                    startActivity(i);
                    break;

                case R.id.activity_dashboard_button_help:
                    if (!CommonUtilities.isNetworkAvailable(mContext)) {
                        Toast.makeText(mContext, "Network connection unavailable.",
                                Toast.LENGTH_SHORT).show();
                    }

                    i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.fieldlocate.com/support/"));
                    startActivity(i);

                    break;

                case R.id.activity_dashboard_button_invoices:
                    if (!CommonUtilities.isNetworkAvailable(mContext)) {
                        Toast.makeText(mContext, "Network connection unavailable.",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    i = new Intent(mContext, ActivityInvoiceDualFragment.class);
                    startActivity(i);
                    break;

                case R.id.activity_dashboard_button_my_hours:
                    if (!CommonUtilities.isNetworkAvailable(mContext)) {
                        Toast.makeText(mContext, "Network connection unavailable.",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (UserUtilitiesSingleton.getInstance().user
                            .isUsingTimeClock())
                        if (UserUtilitiesSingleton.getInstance().user
                                .isPermissionSupervisor())
                            i = new Intent(mContext,
                                    ActivityTimeClockSupervisorList.class);
                        else
                            i = new Intent(mContext, ActivityTimeClockView.class);
                    else
                        i = new Intent(mContext, ActivityHoursWorkedListView.class);

                    startActivity(i);
                    break;

                case R.id.activity_dashboard_button_my_schedule:
                    if (!CommonUtilities.isNetworkAvailable(mContext)) {
                        Toast.makeText(mContext, "Network connection unavailable.",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    i = new Intent(mContext, ActivityAppointmentDualFragment.class);
                    startActivity(i);
                    break;

                case R.id.activity_dashboard_button_pricebook:
                    if (!CommonUtilities.isNetworkAvailable(mContext)) {
                        Toast.makeText(mContext, "Network connection unavailable.",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    i = new Intent(mContext, ActivityPricebookListView.class);
                    i.putExtra(Constants.EXTRA_PRICEBOOK_LIST_VIEW_MODE,
                            Constants.PRICEBOOK_LIST_VIEW_FROM_DASHBOARD);
                    startActivity(i);
                    break;

                case R.id.activity_dashboard_button_settings:
                    i = new Intent(mContext, ActivitySettingsView.class);
                    startActivity(i);
                    break;

                case R.id.activity_dashboard_button_logout:
                    UserUtilitiesSingleton.getInstance().userLogoutPrompt(
                            mActivity, mContext,
                            R.layout.dialog_layout_yes_no_response,
                            R.id.dialog_yes_no_response_textview_title,
                            R.id.dialog_yes_no_response_textview_body,
                            R.id.dialog_yes_no_response_button_yes,
                            R.id.dialog_yes_no_response_button_no,
                            ActivityLoginMobile.class);
                    break;

                case R.id.activity_dashboard_button_part_orders:
                    if (!CommonUtilities.isNetworkAvailable(mContext)) {
                        Toast.makeText(mContext, "Network connection unavailable.",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    i = new Intent(mContext, ActivityPartOrdersListView.class);
                    startActivity(i);
                    break;
                default:
                    // Nothing
                    break;
            }
        }
    };

    private OnClickListener tabletButtonListener = new OnClickListener() {

        @Override
        public void onClick(View v) {

            if (!CommonUtilities.isNetworkAvailable(mContext)) {
                Toast.makeText(mContext, "Network connection unavailable.",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            Intent i = null;
            switch (v.getId()) {

                case R.id.activity_dashboard_button_customers:
                    i = new Intent(mContext, ActivityCustomerDualFragment.class);
                    startActivity(i);
                    break;

                case R.id.activity_dashboard_button_help:
                    i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.fieldlocate.com/support/"));
                    startActivity(i);
                    break;

                case R.id.activity_dashboard_button_invoices:

                    i = new Intent(mContext, ActivityInvoiceDualFragment.class);
                    startActivity(i);
                    break;

                case R.id.activity_dashboard_button_my_hours:
                    if (!CommonUtilities.isNetworkAvailable(mContext)) {
                        Toast.makeText(mContext, "Network connection unavailable.",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (UserUtilitiesSingleton.getInstance().user
                            .isUsingTimeClock())
                        if (UserUtilitiesSingleton.getInstance().user
                                .isPermissionSupervisor())
                            i = new Intent(mContext,
                                    ActivityTimeClockSupervisorList.class);
                        else
                            i = new Intent(mContext, ActivityTimeClockView.class);
                    else
                        i = new Intent(mContext, ActivityHoursWorkedListView.class);

                    startActivity(i);
                    break;

                case R.id.activity_dashboard_button_my_schedule:
                    i = new Intent(mContext, ActivityAppointmentDualFragment.class);
                    startActivity(i);
                    break;

                case R.id.activity_dashboard_button_pricebook:
                    // TODO - Remove this
                    // AppDataSingleton.getInstance().setLineItemMode(Constants.ADD_LINE_ITEMS_MODE_VIEW_ONLY);

                    i = new Intent(mContext, ActivityPricebookListView.class);
                    i.putExtra(Constants.EXTRA_PRICEBOOK_LIST_VIEW_MODE,
                            Constants.PRICEBOOK_LIST_VIEW_FROM_DASHBOARD);
                    startActivity(i);
                    if (!CommonUtilities.isNetworkAvailable(mContext)) {
                        Toast.makeText(mContext, "Network connection unavailable.",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    break;

                case R.id.activity_dashboard_button_settings:
                    i = new Intent(mContext, ActivitySettingsView.class);
                    startActivity(i);
                    break;

                case R.id.activity_dashboard_button_logout:
                    UserUtilitiesSingleton.getInstance().userLogoutPrompt(
                            mActivity, mContext,
                            R.layout.dialog_layout_yes_no_response,
                            R.id.dialog_yes_no_response_textview_title,
                            R.id.dialog_yes_no_response_textview_body,
                            R.id.dialog_yes_no_response_button_yes,
                            R.id.dialog_yes_no_response_button_no,
                            ActivityLoginMobile.class);
                    break;

                case R.id.activity_dashboard_button_part_orders:
                    i = new Intent(mContext, ActivityPartOrdersListView.class);
                    startActivity(i);
                    break;
                default:
                    // Nothing
                    break;
            }
        }
    };

    private OnClickListener mUpdateApplicationListener = new OnClickListener() {

        @Override
        public void onClick(View v) {

            if (!CommonUtilities.isNetworkAvailable(mContext)) {
                Toast.makeText(mContext, "Network connection unavailable.",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            UserUtilitiesSingleton.getInstance().user
                    .setNeedToUpdateApplication(false);

            Intent i = new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=com.skeds.android.phone.business"));
            try {
                startActivity(i);
            }catch (Exception ex){
                Toast.makeText(v.getContext(),"Cannot call activity", Toast.LENGTH_LONG).show();
            }
        }
    };

    private OnItemClickListener listener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (!CommonUtilities.isNetworkAvailable(mContext)) {
                Toast.makeText(mContext, "Network connection unavailable.",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            mSingleAppointmentId = AppDataSingleton.getInstance()
                    .getUpcomingAppointmentsList().get((int) id).getId();

            Intent i = new Intent(mContext,
                    ActivityAppointmentSingleFragment.class);
            i.putExtra(AppointmentFragment.APPOINTMENT_ID, mSingleAppointmentId);
            // Indicator for how we arrived at the given activity
            AppDataSingleton.getInstance().setAppointmentViewMode(
                    Constants.APPOINTMENT_VIEW_FROM_DASHBOARD);
            startActivity(i);
        }
    };


    private void setupUI() {

        if (UserUtilitiesSingleton.getInstance().user.isUsingTimeClock())
            buttonMyHours.setBackgroundResource(R.drawable.btn_time_clock);
        else
            buttonMyHours.setBackgroundResource(R.drawable.phone_custom_dashboard_button_my_hours);

        headerLayout = (LinearLayout) findViewById(R.id.activity_header);
        headerButtonUser = (ImageView) headerLayout
                .findViewById(R.id.header_button_user);
        headerButtonUser.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                accountMenu.show(v);
                accountMenu.setAnimStyle(QuickAction.ANIM_GROW_FROM_CENTER);
            }
        });

        buttonMySchedule.setOnClickListener(buttonListener);
        buttonMyHours.setOnClickListener(buttonListener);

        if (UserUtilitiesSingleton.getInstance().user.isAllowViewAllCustomers()) {
            buttonCustomers.setEnabled(true);
            buttonCustomers.setOnClickListener(buttonListener);
        } else
            buttonCustomers.setEnabled(false);

        buttonInvoices.setOnClickListener(buttonListener);
        buttonPricebook.setOnClickListener(buttonListener);

        android.util.Log.i(
                "Skeds",
                "isAllowPartOrdering: "
                        + UserUtilitiesSingleton.getInstance().user
                        .isAllowPartOrdering());
        buttonPartOrders.setEnabled(UserUtilitiesSingleton.getInstance().user
                .isAllowPartOrdering());
        buttonPartOrders.setOnClickListener(buttonListener);

        textUpdateApplication.setOnClickListener(mUpdateApplicationListener);

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

        dashboardList = mPullRefreshListView.getRefreshableView();
        dashboardApptsAdapter = new DashboardApptsAdapter(mContext);
        dashboardList.setAdapter(dashboardApptsAdapter);
        dashboardList.setOnItemClickListener(listener);
    }

    /* 7" Tablet Layout setup */
    private void setupUI600() {

    }

    /* 10.1" Tablet Layout Setup */
    private void setupUI720() {

        ImageView imageLogo = (ImageView) findViewById(R.id.logo);

        if (SkedsApplication.getInstance().isBeta())
            imageLogo.setImageResource(R.drawable.fieldlocate_logo_dash_beta);

        View buttonMySchedule = findViewById(R.id.activity_dashboard_button_my_schedule);
        View buttonMyHours = findViewById(R.id.activity_dashboard_button_my_hours);
        View buttonCustomers = findViewById(R.id.activity_dashboard_button_customers);
        View buttonInvoices = findViewById(R.id.activity_dashboard_button_invoices);
        View buttonPriceBook = findViewById(R.id.activity_dashboard_button_pricebook);
        View buttonSettings = findViewById(R.id.activity_dashboard_button_settings);
        View buttonHelp = findViewById(R.id.activity_dashboard_button_help);
        View buttonLogout = findViewById(R.id.activity_dashboard_button_logout);
        View buttonPartOrders = findViewById(R.id.activity_dashboard_button_part_orders);

        buttonPartOrders.setEnabled(UserUtilitiesSingleton.getInstance().user
                .isAllowPartOrdering());
        buttonPartOrders.setOnClickListener(tabletButtonListener);

        buttonMySchedule.setOnClickListener(tabletButtonListener);

        buttonMyHours.setOnClickListener(tabletButtonListener);

        if (UserUtilitiesSingleton.getInstance().user.isAllowViewAllCustomers()) {
            buttonCustomers.setEnabled(true);
            buttonCustomers.setOnClickListener(tabletButtonListener);
        } else
            buttonCustomers.setEnabled(false);

        buttonInvoices.setOnClickListener(tabletButtonListener);

        buttonPriceBook.setOnClickListener(tabletButtonListener);

        buttonSettings.setOnClickListener(tabletButtonListener);

        buttonHelp.setOnClickListener(tabletButtonListener);

        buttonLogout.setOnClickListener(tabletButtonListener);
    }

    private void rateUs() {
        SharedPreferences appPrefs = getSharedPreferences(SkedsApplication.prefsFileName, Context.MODE_PRIVATE);
        boolean rate = appPrefs.getBoolean("rate_us", false);

        if (!rate) {
            DialogRateUs dialog = new DialogRateUs(mContext);
            dialog.show();
        }
    }


    private class AcceptTermsOfServiceTask extends
            AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                RESTTermsOfService.post();
                return true;
            } catch (NonfatalException e) {
            }
            return false;
        }
    }

    /*
     * mContext retrieves the upcoming user appointments, as well as the user
     * status
     */
    private class GetUpcomingAppointmentsTask extends
            AsyncTask<String, Void, Boolean> {
        Exception error = null;
        /**
         * application context.
         */
        private Context context;

        public GetUpcomingAppointmentsTask(Activity activity) {
            context = activity;
        }

        @Override
        protected void onPreExecute() {
            mLoadingUpcomingAppointments = true;
            AppDataSingleton.getInstance().getUpcomingAppointmentsList().clear();
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            if (success) {

				/*
                 * Terms of Service if they haven't agreed
				 */
                if (!UserUtilitiesSingleton.getInstance().user.isAgreedToTOS()) {
                    dialogTermsOfService = new Dialog(context);
                    dialogTermsOfService
                            .requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialogTermsOfService
                            .setContentView(R.layout.dialog_layout_terms_of_service);

                    dialogTermsOfServiceText = (TextView) dialogTermsOfService
                            .findViewById(R.id.dialog_terms_of_service_textview_terms);
                    String termsText = "I have read and agree to the <a href='http://www.skeds.com/content/user-agreement'>Terms of Service</a>";
                    dialogTermsOfServiceText.setText(Html.fromHtml(termsText));
                    dialogTermsOfServiceText
                            .setOnClickListener(new OnClickListener() {

                                @Override
                                public void onClick(View v) {

                                    if (!CommonUtilities
                                            .isNetworkAvailable(mContext)) {
                                        Toast.makeText(
                                                mContext,
                                                "Network connection unavailable.",
                                                Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                    Intent i = new Intent(Intent.ACTION_VIEW);
                                    i.setData(Uri
                                            .parse("http://www.skeds.com/content/user-agreement"));
                                    startActivity(i);

                                }
                            });

                    dialogTermsOfServiceAgree = (TextView) dialogTermsOfService
                            .findViewById(R.id.dialog_terms_of_service_button_agree);
                    dialogTermsOfServiceDisagree = (TextView) dialogTermsOfService
                            .findViewById(R.id.dialog_terms_of_service_button_disagree);

                    dialogTermsOfServiceAgree
                            .setOnClickListener(new OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    // boolean tosSuccess =
                                    // PostToServerUtilities
                                    // .submitAcceptTOS();

                                    // if (tosSuccess)

                                    if (!CommonUtilities
                                            .isNetworkAvailable(mContext)) {
                                        Toast.makeText(
                                                mContext,
                                                "Network connection unavailable.",
                                                Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                    UserUtilitiesSingleton.getInstance().user
                                            .setAgreedToTOS(true);
                                    dialogTermsOfService.dismiss();

                                    // if (dialogTermsOfService.isShowing())
                                    // dialogTermsOfService.dismiss();
                                }
                            });

                    dialogTermsOfServiceDisagree
                            .setOnClickListener(new OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    if (!CommonUtilities
                                            .isNetworkAvailable(mContext)) {
                                        Toast.makeText(
                                                mContext,
                                                "Network connection unavailable.",
                                                Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    UserUtilitiesSingleton.getInstance().user
                                            .setAgreedToTOS(false);
                                    dialogTermsOfService.dismiss();
                                    // finish(); // Quits the application
                                }
                            });

                    dialogTermsOfService
                            .setOnDismissListener(new OnDismissListener() {

                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    if (UserUtilitiesSingleton.getInstance().user
                                            .isAgreedToTOS()) {
                                        new AcceptTermsOfServiceTask()
                                                .execute();
                                    } else {
                                        mActivity.finish();
                                    }

                                }
                            });

                    dialogTermsOfService.show();
                }

                if (mLoadingUpcomingAppointments) {
                    // Set them up for the "need to update" if it's relevant
                    if (UserUtilitiesSingleton.getInstance().user
                            .isNeedToUpdateApplication()) {
                        linearlayoutTechnicianStatus.setVisibility(View.GONE);
                        textUpdateApplication.setVisibility(View.VISIBLE);

                        Animation animation = AnimationUtils.loadAnimation(
                                mContext, android.R.anim.fade_out);
                        animation.setDuration(1200);
                        animation.setRepeatCount(-1);

                        textUpdateApplication.setAnimation(animation);
                        textUpdateApplication.startAnimation(animation);
                        animation.start();
                    } else {

                        textUpdateApplication.setAnimation(null); // Clear this
                        linearlayoutTechnicianStatus
                                .setVisibility(View.VISIBLE);
                        textUpdateApplication.setVisibility(View.GONE);
                        textTechnicianName.setText(AppDataSingleton
                                .getInstance().getTechnicianStatus()
                                .getTechnicianName());

                        String techStatus = AppDataSingleton.getInstance()
                                .getTechnicianStatus()
                                .getTechnicianStatusString();

                        textTechnicianStatus.setText(techStatus);

                        // Adjust the "Status" image
                        if ("Working".equals(techStatus)) {
                            imageTechnicianStatus
                                    .setImageResource(R.drawable.dash_status_working);
                        } else if ("On Route".equals(techStatus)) {
                            imageTechnicianStatus
                                    .setImageResource(R.drawable.dash_status_onroute);
                        } else if ("Paused".equals(techStatus)) {
                            imageTechnicianStatus
                                    .setImageResource(R.drawable.dash_status_paused);
                        } else {
                            imageTechnicianStatus
                                    .setImageResource(R.drawable.dash_status_none);
                        }
                    }

                    if (AppDataSingleton.getInstance()
                            .getUpcomingAppointmentsList() != null) {
                        setupUI();
                    }
                    mLoadingUpcomingAppointments = false;
                }
            } else {
                if (error != null)
                    Toast.makeText(mContext, "Could not load appointments.",
                            Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected Boolean doInBackground(final String... args) {
            try {
                if (mLoadingUpcomingAppointments) {
                    if (UserUtilitiesSingleton.getInstance().user.isLoggedIn()) {
                        RESTDashboardAppointmentList.query();
                        return true;
                    }
                }
            } catch (Exception e) {
                error = e;
            }
            return false;
        }
    }


    private class PullToRefreshTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                if (UserUtilitiesSingleton.getInstance().user.isLoggedIn()) {
                    RESTDashboardAppointmentList.query();
                }
            } catch (Exception e) {
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            // Call onRefreshComplete when the list has been refreshed.

            if (dashboardApptsAdapter!=null)
            dashboardApptsAdapter.notifyDataSetChanged();
            mPullRefreshListView.onRefreshComplete();

            if (success) {
                setupUI();
            }
        }
    }
}