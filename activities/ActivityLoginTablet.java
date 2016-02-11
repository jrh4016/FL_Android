package com.skeds.android.phone.business.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask.Status;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.text.method.SingleLineTransformationMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.Log;
import com.google.android.gcm.GCMRegistrar;
import com.skeds.android.phone.business.AsyncTasks.BaseUiReportTask;
import com.skeds.android.phone.business.Dialogs.DialogErrorPopup;
import com.skeds.android.phone.business.Dialogs.DialogSwitchVersion;
import com.skeds.android.phone.business.GCMIntentService;
import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.AccessPreferences;
import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.AppSettingsUtilities;
import com.skeds.android.phone.business.Utilities.General.CommonUtilities;
import com.skeds.android.phone.business.Utilities.General.Constants;
import com.skeds.android.phone.business.Utilities.General.UserUtilitiesSingleton;
import com.skeds.android.phone.business.Utilities.Logger;
import com.skeds.android.phone.business.Utilities.REST.Objects.RESTAccount;
import com.skeds.android.phone.business.Utilities.REST.Objects.RESTLeadSource;
import com.skeds.android.phone.business.core.SkedsApplication;

public class ActivityLoginTablet extends LoginActivity {

    private final static String DEBUG_TAG = "[Login]";

    private EditText edittextUserName;
    private EditText edittextPassword;
    private CheckBox checkboxDisplayPassword;

    private TextView loginButton;

    private LinearLayout headerLayout;

    private static int mDebugIterator = 0;

    private ImageView buttonSkedsLogo;


    private Context mContext;
    private Activity mActivity;
    private SkedsApplication app;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.a_login);

        app = (SkedsApplication) getApplication();
        mActivity = this;
        mContext = this.getApplicationContext();


        headerLayout = (LinearLayout) findViewById(R.id.activity_header);

        switch (AppSettingsUtilities.getApplicationMode()) {
            case Constants.APPLICATION_MODE_PHONE_SERVICE:
                buttonSkedsLogo = (ImageView) headerLayout
                        .findViewById(R.id.header_imageview_logo);
                if (SkedsApplication.getInstance().isBeta())
                    setupBetaHeader();

                break;
            case Constants.APPLICATION_MODE_TABLET_101_SERVICE:
                buttonSkedsLogo = (ImageView) findViewById(R.id.tablet_login_logo);
                setupBetaHeader();
                break;

            default:
                // Nothing
                break;
        }


        if (buttonSkedsLogo != null)
            buttonSkedsLogo.setOnClickListener(mLogoListener);
        edittextUserName = (EditText) findViewById(R.id.user_name);
        edittextPassword = (EditText) findViewById(R.id.password);

        loginButton = (TextView) findViewById(R.id.login);
        checkboxDisplayPassword = (CheckBox) findViewById(R.id.display_password);

        String text = getSharedPreferences(SkedsApplication.prefsFileName, Context.MODE_PRIVATE).getString("password", "");
        if (text == null || text.length() < 1)
            edittextPassword.requestFocus();
        else
            edittextPassword.setText(text);
        text = getSharedPreferences(SkedsApplication.prefsFileName, Context.MODE_PRIVATE).getString("login", "");
        if (text == null || text.length() < 1)
            edittextUserName.requestFocus();
        else
            edittextUserName.setText(text);

        loginButton.setEnabled(true);
        loginButton.setOnClickListener(buttonListener);

        checkboxDisplayPassword
                .setOnCheckedChangeListener(displayPasswordListener);
    }


    @Override
    public void onBackPressed() {

        finish();

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }


    private OnCheckedChangeListener displayPasswordListener = new OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            if (isChecked) {
                // passwordText.setInputType(InputType.TYPE_TEXT_VARIATION_NORMAL);
                edittextPassword
                        .setTransformationMethod(SingleLineTransformationMethod
                                .getInstance());
            } else {
                // passwordText
                // .setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                edittextPassword
                        .setTransformationMethod(PasswordTransformationMethod
                                .getInstance());
            }
        }
    };

    private OnClickListener buttonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!TextUtils.isEmpty(edittextUserName.getText())
                    && !TextUtils.isEmpty(edittextPassword.getText())) {

                UserUtilitiesSingleton.getInstance()
                        .setUsername(edittextUserName.getText().toString()); // Sets
                // these
                // in
                // memory
                UserUtilitiesSingleton.getInstance()
                        .setPassword(edittextPassword.getText().toString());

                app.saveLogin();

                // CommonUtilities.appointment = null;
                AppDataSingleton.getInstance().getPastAppointmentList().clear();

                if (CommonUtilities.isNetworkAvailable(mContext)) {
                    LoginRequestTask loginTask = new LoginRequestTask();
                    if ((loginTask.getStatus() != Status.FINISHED)
                            && (loginTask.getStatus() != Status.RUNNING)) {
                        loginTask.execute();
                    }
                } else
                    Toast.makeText(mContext, "Network connection unavailable.",
                            Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext,
                        "Input valid username and password to continue",
                        Toast.LENGTH_SHORT).show();
            }
        }
    };

    private OnClickListener mLogoListener = new OnClickListener() {
        @Override
        public void onClick(View v) {

            if (mDebugIterator < 5)
                mDebugIterator++;
            else {
                mDebugIterator = 0;
                String debugString = "";
                String switchString = "";
                if (AppSettingsUtilities.isBetaServerMode()) {
                    debugString = "(Beta)";
                    switchString = "(Production)";
                } else {
                    debugString = "(Production)";
                    switchString = "(Beta)";
                }


                final DialogSwitchVersion switchDialog = new DialogSwitchVersion(ActivityLoginTablet.this, debugString, switchString);
                switchDialog.setOnDismissListener(new DialogSwitchVersion.OnDismissListener() {

                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if (switchDialog.isYesAnswered()) {
                            app.toggleBeta();
                            setupBetaHeader();
                        }
                    }
                });
                switchDialog.show();
            }
        }
    };

    private void setupBetaHeader() {

        switch (AppSettingsUtilities.getApplicationMode()) {
            case Constants.APPLICATION_MODE_PHONE_SERVICE:
                if (buttonSkedsLogo != null) {
                    if (AppSettingsUtilities.isBetaServerMode()) {
                        buttonSkedsLogo
                                .setImageResource(R.drawable.fieldlocate_logo_beta);
                    } else {
                        buttonSkedsLogo.setImageResource(R.drawable.fieldlocate_logo);
                    }
                }

                break;
            case Constants.APPLICATION_MODE_TABLET_7_SERVICE:
                // TODO - Blank
                break;

            case Constants.APPLICATION_MODE_TABLET_101_SERVICE:
                if (buttonSkedsLogo != null) {
                    if (AppSettingsUtilities.isBetaServerMode()) {
                        buttonSkedsLogo
                                .setImageResource(R.drawable.fieldlocate_logo_login_beta);
                    } else {
                        buttonSkedsLogo
                                .setImageResource(R.drawable.fieldlocate_logo_login);
                    }
                }

                break;
            default:
                // Nothing
                break;
        }
    }

    private class LoginRequestTask extends BaseUiReportTask<String> {


        LoginRequestTask() {
            super(ActivityLoginTablet.this, "Logging in as "
                    + UserUtilitiesSingleton.getInstance().getUsername() + " ...");
        }


        @Override
        protected void onSuccess() {
            if (UserUtilitiesSingleton.getInstance().user.isLoggedIn()) {
                if (!UserUtilitiesSingleton.getInstance().user.isPermissionServiceProvider()) {
                    new DialogErrorPopup(
                            ActivityLoginTablet.this,
                            "Login problem",
                            "You are not a technician. Please contact FieldLocate support.",
                            null).show();
                    UserUtilitiesSingleton.getInstance().userLogout(getParent(),
                            ActivityLoginTablet.this, null);
                } else {

					/*
                     * This will instantiate the C2DM Object
					 */
                    // if (!UserUtilitiesSingleton.getInstance().user.isC2dmRegistered()) {
                    // Intent registrationIntent = new Intent(
                    // "com.google.android.c2dm.intent.REGISTER");
                    // registrationIntent
                    // .putExtra("app", PendingIntent
                    // .getBroadcast(mContext, 0,
                    // new Intent(), 0));
                    // registrationIntent.putExtra("sender",
                    // "skeds.developer@gmail.com");
                    // mContext.startService(registrationIntent);
                    // }

                    Intent i = new Intent(mContext, ActivityDashboardView.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    setAutocloseOnSuccess(true);
                    finish();
                }
            }
        }

        @Override
        protected boolean taskBody(final String... args) throws Exception {
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            String operatorName = telephonyManager.getNetworkOperatorName();

            String phoneManufacturer = Build.MANUFACTURER;
            String phoneModel = Build.MODEL;
            String phoneDeviceName = Build.DEVICE;
            String phoneDisplay = Build.DISPLAY;
            String phoneProvider = operatorName;

            String operatingSystemVersion = Build.VERSION.RELEASE;
            String applicationVersion = CommonUtilities.getVersionName(
                    ActivityLoginTablet.this, ActivityAppointmentDualFragment.class);

            // String registrationKey = "";
            // registrationKey = context.getSharedPreferences("c2dmPref",
            // Context.MODE_PRIVATE).getString(C2DM_REGISTRATION_KEY,
            // "");

			/* GCM Version */


            RESTAccount.login(applicationVersion, operatingSystemVersion,
                    operatorName, phoneManufacturer, phoneModel,
                    phoneDeviceName, phoneDisplay, phoneProvider,
                    getRegistrationId(mContext));

            RESTAccount.getCountriesInfo();

            RESTLeadSource.getByOwnerId(UserUtilitiesSingleton.getInstance().user.getOwnerId());
            return true;
        }
    }
}