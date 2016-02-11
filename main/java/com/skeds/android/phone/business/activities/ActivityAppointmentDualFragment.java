package com.skeds.android.phone.business.activities;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.skeds.android.phone.business.Custom.AccountMenu;
import com.skeds.android.phone.business.Custom.QuickAction;
import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.ui.fragment.AppointmentEquipmentAddFragment;
import com.skeds.android.phone.business.ui.fragment.AppointmentFragment;
import com.skeds.android.phone.business.ui.fragment.AppointmentListFragment;
import com.skeds.android.phone.business.ui.fragment.EstimateListFragment;
import com.skeds.android.phone.business.ui.fragment.InvoiceFragment;
import com.skeds.android.phone.business.ui.fragment.LogoPlaceholderFragment;
import com.skeds.android.phone.business.ui.fragment.PartOrderApptListFragment;
import com.skeds.android.phone.business.ui.fragment.PhotoListViewFragment;

public class ActivityAppointmentDualFragment extends BaseSkedsActivity implements
        AppointmentListFragment.OnHeadlineSelectedListener,
        AppointmentFragment.OnActionSelectedListener {

    private static final String ESTIMATE_FRAGMENT_TAG = "estimate_tag";
    private static final String NEW_INVOICE_FRAGMENT_TAG = "new_invoice_tag";
    private static final String VIEW_INVOICE_FRAGMENT_TAG = "view_invoice_tag";
    private static final String PART_ORDER_FRAGMENT_TAG = "part_order_appt_tag";
    private static final String ADD_EQUIPMENT_FRAGMENT_TAG = "add_equipment_tag";
    private static final String PHOTO_FRAGMENT_TAG = "photo_tag";

    private LinearLayout headerLayout;
    private ImageView headerButtonUser;
    private ImageView headerButtonBack;

    private boolean isPlaceholderView;

    private Dialog dialogWarning;

    private int selectedAction = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_appointment_view_container);

        initHeader();

        if (findViewById(R.id.fragment_appointment_list_container) != null) {

            if (savedInstanceState != null) {
                return;
            }

            AppointmentListFragment firstFragment = new AppointmentListFragment();

            firstFragment.setArguments(getIntent().getExtras());

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_appointment_list_container,
                            firstFragment).commit();
        } else {
            LogoPlaceholderFragment fragmentLogo = new LogoPlaceholderFragment();
            FragmentTransaction transaction = getSupportFragmentManager()
                    .beginTransaction();

            transaction.replace(R.id.fragment_appointment_content_container,
                    fragmentLogo);

            // Commit the transaction
            transaction.commit();
            isPlaceholderView = true;
        }

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            int apptId = bundle.getInt(AppointmentFragment.APPOINTMENT_ID);
            if (apptId != 0)
                onArticleSelected(apptId);
        }

    }

    public void onArticleSelected(int selectedId) {
        FrameLayout contentFrag = (FrameLayout) findViewById(R.id.fragment_appointment_content_container);

        FragmentManager fm = getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i)
            fm.popBackStack();


        int containerId;
        if (contentFrag != null) {
            containerId = R.id.fragment_appointment_content_container;
        } else {
            containerId = R.id.fragment_appointment_list_container;
        }

        AppointmentFragment newFragment = new AppointmentFragment();
        Bundle args = new Bundle();
        args.putInt(AppointmentFragment.APPOINTMENT_ID, selectedId);
        newFragment.setArguments(args);
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();

        transaction.replace(containerId, newFragment);

        if (containerId == R.id.fragment_appointment_list_container)
            transaction.addToBackStack(null);
        else if (isPlaceholderView) {
            transaction.addToBackStack(null);
            isPlaceholderView = false;
        }

        // Commit the transaction
        transaction.commit();
    }

    @Override
    public void onBackPressed() {

        if (selectedAction == AppointmentFragment.VIEW_INVOICE_SELECTED || selectedAction == AppointmentFragment.NEW_INVOICE_SELECTED) {
            if (!InvoiceFragment.hasAnyChanges) {
                super.onBackPressed();
                isPlaceholderView = !isPlaceholderView;
                return;
            }

            if (dialogWarning != null)
                if (dialogWarning.isShowing()) {
                    dialogWarning.dismiss();
                    super.onBackPressed();
                    return;
                }
            showWarningDialog();
        } else {
            super.onBackPressed();
            isPlaceholderView = !isPlaceholderView;
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

    private void showWarningDialog() {
        dialogWarning = new Dialog(this);
        dialogWarning.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogWarning.setContentView(R.layout.dialog_layout_yes_no_response);
        ((TextView) dialogWarning.findViewById(R.id.dialog_yes_no_response_textview_title)).setText("Warning");

        TextView dialogWarningButtonYes = (TextView) dialogWarning
                .findViewById(R.id.dialog_yes_no_response_button_yes);
        TextView dialogWarningButtonNo = (TextView) dialogWarning
                .findViewById(R.id.dialog_yes_no_response_button_no);

        ((TextView) dialogWarning.findViewById(R.id.dialog_yes_no_response_textview_body)).setText("You Have Unsaved Data. Exit?");


        dialogWarningButtonYes.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        dialogWarningButtonNo.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialogWarning.dismiss();
            }
        });

        dialogWarning.show();
    }

    @Override
    public void onActionSelected(int id) {

        FrameLayout contentFrag = (FrameLayout) findViewById(R.id.fragment_appointment_content_container);

        int containerId;
        if (contentFrag != null) {
            containerId = R.id.fragment_appointment_content_container;
        } else {
            containerId = R.id.fragment_appointment_list_container;
        }

        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();
        Fragment selectedFragment = new Fragment();
        Bundle args;

        selectedAction = id;


        String tag = null;
        switch (id) {
            case AppointmentFragment.ESTIMATE_LIST_SELECTED:
                selectedFragment = new EstimateListFragment();
                tag = ESTIMATE_FRAGMENT_TAG;
                break;
            case AppointmentFragment.NEW_INVOICE_SELECTED:
                selectedFragment = new InvoiceFragment();
                args = new Bundle();
                args.putBoolean("new_invoice", true);
                selectedFragment.setArguments(args);
                tag = NEW_INVOICE_FRAGMENT_TAG;
                break;
            case AppointmentFragment.VIEW_INVOICE_SELECTED:
                selectedFragment = new InvoiceFragment();
                args = new Bundle();
                args.putInt(InvoiceFragment.INVOICE_ID, AppDataSingleton.getInstance().getInvoice().getId());
                args.putBoolean(InvoiceFragment.EXTRA_LOADED_FROM_APPT, true);
                selectedFragment.setArguments(args);
                tag = VIEW_INVOICE_FRAGMENT_TAG;
                break;
            case AppointmentFragment.PART_ORDERS_SELECTED:
                selectedFragment = new PartOrderApptListFragment();
                args = new Bundle();
                args.putInt("appointmentId", AppointmentFragment.appointmentId);
                args.putInt("invoiceId", AppDataSingleton.getInstance().getAppointment().getInvoiceId());
                args.putString(InvoiceFragment.EXTRA_ORDER_NUMBER, AppDataSingleton.getInstance().getAppointment().getWorkOrderNumber());
                selectedFragment.setArguments(args);
                tag = PART_ORDER_FRAGMENT_TAG;
                break;
            case AppointmentFragment.EQUIPMENT_ADD_SELECTED:
                selectedFragment = new AppointmentEquipmentAddFragment();
                tag = ADD_EQUIPMENT_FRAGMENT_TAG;
                break;
            case AppointmentFragment.PHOTO_GALERY_SELECTED:
                selectedFragment = new PhotoListViewFragment();
                tag = PHOTO_FRAGMENT_TAG;
                break;
            default:
                break;
        }
        try {
            transaction.replace(containerId, selectedFragment, tag);
            transaction.addToBackStack(null);
            transaction.commit();
        }catch (IllegalStateException ex){
            ex.printStackTrace();
        }
    }

}
