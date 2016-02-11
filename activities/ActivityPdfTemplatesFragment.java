package com.skeds.android.phone.business.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.skeds.android.phone.business.Custom.AccountMenu;
import com.skeds.android.phone.business.Custom.QuickAction;
import com.skeds.android.phone.business.R;

public class ActivityPdfTemplatesFragment extends BaseSkedsActivity {

    private LinearLayout headerLayout;
    private ImageView headerButtonUser;
    private ImageView headerButtonBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_pdf_templates_container);

        initHeader();

    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK)
            finish();

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
}
