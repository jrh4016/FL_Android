package com.skeds.android.phone.business.Dialogs;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.core.SkedsApplication;

public class DialogRateUs extends Dialog {

    private Context context;

    private TextView buttonYes;
    private TextView buttonNo;


    private SharedPreferences appPrefs;

    public DialogRateUs(Context context) {
        super(context);
        this.context = context;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_layout_rate_us);

        buttonYes = (TextView) findViewById(R.id.rate_us_button_yes);
        buttonNo = (TextView) findViewById(R.id.rate_us_button_no);
        buttonYes.setOnClickListener(listener);
        buttonNo.setOnClickListener(listener);

        appPrefs = context.getSharedPreferences(SkedsApplication.prefsFileName, Context.MODE_PRIVATE);

        appPrefs.edit().putBoolean("rate_us", true).apply();

    }

    private android.view.View.OnClickListener listener = new android.view.View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.rate_us_button_no:
                    dismiss();
                    break;
                case R.id.rate_us_button_yes:

                    Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
                    Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                    try {
                        context.startActivity(goToMarket);
                    } catch (ActivityNotFoundException e) {

                    }

                    dismiss();
                    break;
                default:
                    break;
            }
        }
    };
}