package com.skeds.android.phone.business.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.core.SkedsApplication;
import com.skeds.android.phone.business.ui.fragment.AppointmentFragment;

public class DialogSwitchVersion extends Dialog {

    private boolean yesButtonPressed = false;

    private Context context;

    private EditText betaUrl;

    private TextView buttonYes;
    private TextView buttonNo;
    private TextView buttonDefault;

    private TextView textFrom;
    private TextView textTo;
    private TextView textCurrently;

    private String stringFrom;
    private String stringTo;

    private SharedPreferences appPrefs;

    public DialogSwitchVersion(Context context, String from, String to) {
        super(context);
        this.context = context;
        stringFrom = from;
        stringTo = to;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_layout_switch_version);

        buttonYes = (TextView) findViewById(R.id.dialog_switch_yes);
        buttonNo = (TextView) findViewById(R.id.dialog_switch_no);
        buttonDefault = (TextView) findViewById(R.id.dialog_switch_default);
        buttonDefault.setOnClickListener(new android.view.View.OnClickListener() {

            @Override
            public void onClick(View v) {
                betaUrl.setText(getContext().getString(R.string.beta_base_url));
            }
        });

        betaUrl = (EditText) findViewById(R.id.dialog_switch_url);

        textFrom = (TextView) findViewById(R.id.dialog_switch_from);
        textTo = (TextView) findViewById(R.id.dialog_switch_to);
        textCurrently = (TextView) findViewById(R.id.dialog_switch_currently);

        textFrom.setText(stringFrom);
        textTo.setText(stringTo);
        textCurrently.setText(stringFrom);

        appPrefs = context.getSharedPreferences(SkedsApplication.prefsFileName, Context.MODE_PRIVATE);

        buttonYes.setOnClickListener(new saveButtonListener());
        buttonNo.setOnClickListener(new cancelButtonListener());

        if ("(Beta)".equals(stringTo)) {
            betaUrl.setText(appPrefs.getString("betaUrl", getContext().getString(R.string.beta_base_url)));
        } else {
            betaUrl.setVisibility(View.GONE);
            buttonDefault.setVisibility(View.GONE);
        }
        AppointmentFragment.submitOnDialogClose = false;
    }

    public boolean isYesAnswered() {
        return yesButtonPressed;
    }

    private class saveButtonListener implements
            android.view.View.OnClickListener {

        @Override
        public void onClick(View v) {
            yesButtonPressed = true;
            if ("(Beta)".equals(stringTo))
                appPrefs.edit().putString("betaUrl", betaUrl.getText().toString()).apply();
            dismiss();
        }
    }

    ;

    private class cancelButtonListener implements
            android.view.View.OnClickListener {

        @Override
        public void onClick(View v) {
            yesButtonPressed = false;
            dismiss();
        }
    }

    ;
}
