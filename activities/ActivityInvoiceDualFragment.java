package com.skeds.android.phone.business.activities;

import android.app.Dialog;
import android.content.Context;
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
import android.widget.Toast;

import com.skeds.android.phone.business.AsyncTasks.BaseUiReportTask;
import com.skeds.android.phone.business.Custom.AccountMenu;
import com.skeds.android.phone.business.Custom.QuickAction;
import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.General.CommonUtilities;
import com.skeds.android.phone.business.Utilities.REST.Objects.RESTInvoice;
import com.skeds.android.phone.business.ui.fragment.InvoiceFragment;
import com.skeds.android.phone.business.ui.fragment.InvoiceListFragment;
import com.skeds.android.phone.business.ui.fragment.LogoPlaceholderFragment;

public class ActivityInvoiceDualFragment extends BaseSkedsActivity implements
        InvoiceListFragment.OnHeadlineSelectedListener {

    private LinearLayout headerLayout;
    private ImageView headerButtonUser;
    private ImageView headerButtonBack;

    private boolean isPlaceholderView;

    private boolean isNewInvoice = false;

    private int invoiceId;
    private Context mContext;
    private Dialog dialogWarning;

    private int containerId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = ActivityInvoiceDualFragment.this;

        setContentView(R.layout.layout_invoice_view_container);
        invoiceId = 0;

        initHeader();

        if (findViewById(R.id.fragment_invoice_list_container) != null) {

            if (savedInstanceState != null) {
                return;
            }

            InvoiceListFragment firstFragment = new InvoiceListFragment();

            firstFragment.setArguments(getIntent().getExtras());

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_invoice_list_container, firstFragment)
                    .commit();
        } else {
            LogoPlaceholderFragment fragmentLogo = new LogoPlaceholderFragment();
            FragmentTransaction transaction = getSupportFragmentManager()
                    .beginTransaction();

            transaction.replace(R.id.fragment_invoice_content_container,
                    fragmentLogo);

            // Commit the transaction
            transaction.commit();
            isPlaceholderView = true;
        }

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            isNewInvoice = bundle.getBoolean("new_invoice");
            invoiceId = bundle.getInt(InvoiceFragment.INVOICE_ID);
            if (invoiceId != 0) {
                if (!CommonUtilities.isNetworkAvailable(mContext)) {
                    Toast.makeText(mContext, "Network connection unavailable.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    new GetSingleInvoiceTask().execute();
                }
            } else if (isNewInvoice)
                onArticleSelected();
        }

    }

    public void onArticleSelected() {
        FrameLayout contentFrag = (FrameLayout) findViewById(R.id.fragment_invoice_content_container);

        if (contentFrag != null) {
            containerId = R.id.fragment_invoice_content_container;
        } else {
            containerId = R.id.fragment_invoice_list_container;
        }

        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();
        Bundle args = new Bundle();
        args.putBoolean("new_invoice", isNewInvoice);
        InvoiceFragment fragment = new InvoiceFragment();
        fragment.setArguments(args);

        transaction.replace(containerId, fragment);

        if (containerId == R.id.fragment_invoice_list_container)
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

        FragmentManager fm = getSupportFragmentManager();
        Fragment f = fm.findFragmentById(containerId);
        if (f instanceof InvoiceFragment) {
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

    private class GetSingleInvoiceTask extends BaseUiReportTask<String> {
        GetSingleInvoiceTask() {
            super(ActivityInvoiceDualFragment.this,
                    R.string.async_task_string_loading_invoice);
        }

        @Override
        protected void onSuccess() {
            onArticleSelected();
        }

        @Override
        protected boolean taskBody(final String... args) throws Exception {
            RESTInvoice.query(invoiceId);
            return true;
        }
    }
}
