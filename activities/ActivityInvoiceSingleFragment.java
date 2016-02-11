package com.skeds.android.phone.business.activities;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.skeds.android.phone.business.Custom.AccountMenu;
import com.skeds.android.phone.business.Custom.QuickAction;
import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.ui.fragment.InvoiceFragment;

public class ActivityInvoiceSingleFragment extends BaseSkedsActivity {
    private LinearLayout headerLayout;
    private ImageView headerButtonUser;
    private ImageView headerButtonBack;

    private Dialog dialogWarning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_invoice_single_view_container);

        setResult(Activity.RESULT_OK);

        Fragment f = new InvoiceFragment();
        f.setArguments(getIntent().getExtras());

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.add(R.id.invoice_single_container, f);

        transaction.commit();

        initHeader();

    }

    @Override
    public void onBackPressed() {
        if (InvoiceFragment.hasAnyChanges)
            showWarningDialog();
        else
            super.onBackPressed();
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
                dialogWarning.dismiss();
                finish();
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

}
