package com.skeds.android.phone.business.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.skeds.android.phone.business.AsyncTasks.BaseUiReportTask;
import com.skeds.android.phone.business.AsyncTasks.SendErrorReportTask;
import com.skeds.android.phone.business.Custom.AccountMenu;
import com.skeds.android.phone.business.Custom.QuickAction;
import com.skeds.android.phone.business.Dialogs.DialogSettingsAddIssue;
import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.General.CommonUtilities;
import com.skeds.android.phone.business.Utilities.General.Constants;
import com.skeds.android.phone.business.Utilities.General.UserUtilitiesSingleton;
import com.skeds.android.phone.business.Utilities.REST.GMailSender;
import com.skeds.android.phone.business.Utilities.REST.Objects.RESTAccount;
import com.skeds.android.phone.business.core.SkedsApplication;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ActivitySettingsView extends BaseSkedsActivity implements
        View.OnClickListener {

    private Activity mActivity;
    private Context mContext;

    private LinearLayout headerLayout;
    private ImageView headerButtonLogo;
    private ImageView headerButtonUser;
    private ImageView headerButtonBack;
    private TextView buttonSave;

    private TextView buttonLogout, buttonChangePassword, textCurrentLoginUser,
            textApplicationVersionNumber;

    private CheckBox checkboxVibrate, checkboxSound, checkboxSavePhotos;

    private RadioGroup itemsGroup;

    private EditText edittextCurrentPassword, edittextNewPassword,
            edittextConfirmPassword;
    private ErrorReportTask errorReportTask = null;

    private static int mDebugIterator = 0;

    private int itemMode = Constants.SHOW_ITEM_NAME;

    private android.widget.RadioGroup.OnCheckedChangeListener radioListener = new android.widget.RadioGroup.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.item_name_radio_button:
                    itemMode = Constants.SHOW_ITEM_NAME;
                    break;
                case R.id.item_description_radio_button:
                    itemMode = Constants.SHOW_ITEM_DESCRIPTION;
                    break;
                case R.id.item_name_description_radio_button:
                    itemMode = Constants.SHOW_ITEM_NAME_AND_DESCRIPTION;
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_settings_view);

        headerLayout = (LinearLayout) findViewById(R.id.activity_header);
        headerButtonLogo = (ImageView) headerLayout
                .findViewById(R.id.header_imageview_logo);

        mActivity = ActivitySettingsView.this;
        mContext = this;

        final QuickAction accountMenu;
        accountMenu = AccountMenu.setupMenu(mContext, mActivity);

        headerButtonUser = (ImageView) headerLayout
                .findViewById(R.id.header_button_user);

        headerButtonUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accountMenu.dismiss();
                accountMenu.show(v);
                accountMenu.setAnimStyle(QuickAction.ANIM_GROW_FROM_CENTER);
            }
        });

        headerButtonBack = (ImageView) headerLayout
                .findViewById(R.id.header_button_back);
        buttonSave = (TextView) headerLayout
                .findViewById(R.id.header_standard_button_right);

        itemsGroup = (RadioGroup) findViewById(R.id.line_items_radio_group);
        itemsGroup.setOnCheckedChangeListener(radioListener);

        textCurrentLoginUser = (TextView) findViewById(R.id.activity_settings_textview_current_user);

        buttonLogout = (TextView) findViewById(R.id.activity_settings_button_logout);
        buttonChangePassword = (TextView) findViewById(R.id.activity_settings_button_change_password);

        checkboxVibrate = (CheckBox) findViewById(R.id.activity_settings_checkbox_notification_vibrate);
        checkboxVibrate.setOnCheckedChangeListener(checkBoxListener);
        checkboxSound = (CheckBox) findViewById(R.id.activity_settings_checkbox_notification_sound);
        checkboxSound.setOnCheckedChangeListener(checkBoxListener);
        checkboxSavePhotos = (CheckBox) findViewById(R.id.activity_settings_checkbox_save_photos_to_gallery);
        checkboxSavePhotos.setOnCheckedChangeListener(checkBoxListener);

        edittextCurrentPassword = (EditText) findViewById(R.id.activity_settings_edittext_current_password);
        edittextNewPassword = (EditText) findViewById(R.id.activity_settings_edittext_new_password);
        edittextConfirmPassword = (EditText) findViewById(R.id.activity_settings_edittext_confirm_password);

        textApplicationVersionNumber = (TextView) findViewById(R.id.activity_settings_textview_version_number);

        setupUI();

        findViewById(R.id.btn_send_error_report).setOnClickListener(this);
    }

    private OnCheckedChangeListener checkBoxListener = new OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            switch (buttonView.getId()) {
                case R.id.activity_settings_checkbox_notification_sound:
                    ((SkedsApplication) getApplication()).setNotifySound(isChecked);
                    break;
                case R.id.activity_settings_checkbox_notification_vibrate:
                    ((SkedsApplication) getApplication()).setNotifyVibrate(isChecked);
                    break;
                case R.id.activity_settings_checkbox_save_photos_to_gallery:
                    ((SkedsApplication) getApplication()).setSavePhotosLocally(isChecked);
                    break;

                default:
                    break;
            }

        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send_error_report:

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //sendXmlResponses();
                    }
                }).start();

                if (!CommonUtilities.isNetworkAvailable(mContext)) {
                    Toast.makeText(mContext, "Network connection unavailable.", Toast.LENGTH_SHORT).show();
                    return;
                }

                final DialogSettingsAddIssue issueDialog = new DialogSettingsAddIssue(mContext);
                issueDialog.setOnDismissListener(new OnDismissListener() {

                    @Override
                    public void onDismiss(DialogInterface dialog) {

                        if (issueDialog.isAcceptedToReport()) {
                            errorReportTask = new ErrorReportTask();
                            errorReportTask.execute();
                        }

                    }
                });
                issueDialog.show();
                break;
            default:
        }
    }

    private void sendXmlResponses() {
        final String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File[] files = Environment.getExternalStorageDirectory().listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File file, String s) {
                    return s.toLowerCase().endsWith(".xml");
                }
            });
            List<File> files1 = Arrays.asList(files);

            GMailSender.sendWithAttachment(new Date().toString(), "responses", files1);

            for (File file : files1) {
                file.delete();
            }

        }
    }


    private View.OnClickListener mGoBackListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onBackPressed();
        }
    };

    private View.OnClickListener mSaveListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            saveSettings();

            Intent i = new Intent(mContext, ActivityDashboardView.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            // finish();
        }
    };

    private View.OnClickListener mLogoutListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            logout();
        }
    };

    private View.OnClickListener mHeaderListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mDebugIterator++;

            if (mDebugIterator == 4) {
                mDebugIterator = 0;
                String msg = "User Id: "
                        + UserUtilitiesSingleton.getInstance().user.getId()
                        + "\nBusiness Name: "
                        + UserUtilitiesSingleton.getInstance().user
                        .getOwnerName()
                        + "\nBusiness Id: "
                        + UserUtilitiesSingleton.getInstance().user
                        .getOwnerId()
                        + "\nService Prov. Id: "
                        + UserUtilitiesSingleton.getInstance().user
                        .getServiceProviderId()
                        + "\nView Customers: "
                        + UserUtilitiesSingleton.getInstance().user
                        .isAllowViewAllCustomers()
                        + "\nEdit Appts: "
                        + UserUtilitiesSingleton.getInstance().user
                        .isAllowAddEditAppointments();

                if (SkedsApplication.getInstance().isBeta()) {
                    new AlertDialog.Builder(ActivitySettingsView.this)
                            .setTitle("User settings")
                            .setMessage(msg)
                            .setCancelable(true)
                            .setNegativeButton("Close", null)
                            .setPositiveButton("Toggle edit appts.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {
                                            UserUtilitiesSingleton
                                                    .getInstance().user
                                                    .setAllowAddEditAppointments(!UserUtilitiesSingleton
                                                            .getInstance().user
                                                            .isAllowAddEditAppointments());
                                        }
                                    }).create().show();
                } else {
                    Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
                }
            }
        }
    };

    private View.OnClickListener mChangePasswordListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (!CommonUtilities.isNetworkAvailable(mContext)) {
                Toast.makeText(mContext, "Network connection unavailable.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (edittextNewPassword.getText().toString().length() > 5) {
                if (edittextCurrentPassword
                        .getText()
                        .toString()
                        .equals(UserUtilitiesSingleton.getInstance()
                                .getPassword())) {
                    if (edittextNewPassword
                            .getText()
                            .toString()
                            .equals(edittextConfirmPassword.getText()
                                    .toString()))
                        new ChangePasswordTask(mActivity).execute();
                    else
                        Toast.makeText(mContext, "Passwords do not match.",
                                Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(mContext, "Current password is incorrect.",
                            Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(mContext,
                        "Password must be at least 6 characters in length.",
                        Toast.LENGTH_LONG).show();
            }
        }
    };

    private void setupUI() {

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        checkboxSound.setChecked(SkedsApplication.getInstance().isNotifySound());
        checkboxVibrate.setChecked(SkedsApplication.getInstance().isNotifyVibrate());
        checkboxSavePhotos.setChecked(SkedsApplication.getInstance().isSavePhotosLocally());

        checkboxSavePhotos
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        if (isChecked) {
                            if (!CommonUtilities.isExternalStorageAvailable()) {
                                Toast.makeText(
                                        mContext,
                                        "External storage unavailable for photos.",
                                        Toast.LENGTH_LONG).show();
                                checkboxSavePhotos.setChecked(false);
                            }
                        }

                    }
                });

        textCurrentLoginUser
                .setText("Currently logged in as: "
                        + UserUtilitiesSingleton.getInstance().user
                        .getFirstName()
                        + " "
                        + UserUtilitiesSingleton.getInstance().user
                        .getLastName());

        // Tell them the version number of the app
        try {
            textApplicationVersionNumber.setText(mContext.getPackageManager()
                    .getPackageInfo(mContext.getPackageName(), 0).versionName);
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        headerButtonBack.setOnClickListener(mGoBackListener);
        buttonSave.setOnClickListener(mSaveListener);

        buttonLogout.setOnClickListener(mLogoutListener);
        buttonChangePassword.setOnClickListener(mChangePasswordListener);

        headerButtonLogo.setOnClickListener(mHeaderListener);

        switch (((SkedsApplication) getApplication()).getLineItemsMode()) {
            case Constants.SHOW_ITEM_NAME:
                ((RadioButton) itemsGroup.findViewById(R.id.item_name_radio_button))
                        .setChecked(true);
                break;
            case Constants.SHOW_ITEM_DESCRIPTION:
                ((RadioButton) itemsGroup
                        .findViewById(R.id.item_description_radio_button))
                        .setChecked(true);
                break;
            case Constants.SHOW_ITEM_NAME_AND_DESCRIPTION:
                ((RadioButton) itemsGroup
                        .findViewById(R.id.item_name_description_radio_button))
                        .setChecked(true);
                break;
            default:
                break;
        }
    }

    /*
     * Interacts with ViewDashboard function "checkSettings()"
     */
    private void saveSettings() {
        SkedsApplication app = (SkedsApplication) getApplication();
        app.setNotifySound(checkboxSound.isChecked());
        app.setNotifyVibrate(checkboxVibrate.isChecked());
        app.setSavePhotosLocally(checkboxSavePhotos.isChecked());
        app.setLineItemsMode(itemMode);
    }

    /*
     * If Updated, also update ViewSkeds class function "logout()"
     */
    private void logout() {

        UserUtilitiesSingleton.getInstance().userLogoutPrompt(mActivity,
                mContext, R.layout.dialog_layout_yes_no_response,
                R.id.dialog_yes_no_response_textview_title,
                R.id.dialog_yes_no_response_textview_body,
                R.id.dialog_yes_no_response_button_yes,
                R.id.dialog_yes_no_response_button_no, loginClass);
    }

    private class ChangePasswordTask extends BaseUiReportTask<String> {
        ChangePasswordTask(Activity activity) {
            super(ActivitySettingsView.this,
                    R.string.async_task_string_changing_password);
        }

        @Override
        protected void onSuccess() {
            SkedsApplication app = (SkedsApplication) getApplication();
            String oldPassExists = UserUtilitiesSingleton.getInstance()
                    .getPassword();
            if (!oldPassExists.equals("")) {
                app.saveLogin();
            }

            Toast.makeText(ActivitySettingsView.this,
                    "Password updated successfully.", Toast.LENGTH_LONG).show();
            UserUtilitiesSingleton.getInstance().userLogout(mActivity,
                    mContext, loginClass);
        }

        @Override
        protected boolean taskBody(final String... args) throws Exception {
            RESTAccount
                    .changePassword(edittextNewPassword.getText().toString());
            return true;
        }
    }

    /**
     * Send error report
     */
    private class ErrorReportTask extends SendErrorReportTask {

        ErrorReportTask() {
            super(ActivitySettingsView.this);
        }

        @Override
        protected void dtor() {
            errorReportTask = null;
        }
    }
}