package com.skeds.android.phone.business.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.activities.ActivityTimeClockView;

public class DialogClockInConfirm extends Dialog {

    Context context;

    public boolean submitOnComplete;

    TextView buttonSave;
    TextView buttonCancel;

    public DialogClockInConfirm(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create the dialog
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_layout_yes_no_response);

        TextView title = (TextView) findViewById(R.id.dialog_yes_no_response_textview_title);
        TextView body = (TextView) findViewById(R.id.dialog_yes_no_response_textview_body);

        title.setText("Time Clock");

        String currentStatus = AppDataSingleton.getInstance().getClockTechnician()
                .get(ActivityTimeClockView.technicianToManage).getTimeClockMethod();
        String technicianName = AppDataSingleton.getInstance().getClockTechnician()
                .get(ActivityTimeClockView.technicianToManage).getName();
        if ("IN".equals(currentStatus)) {
            body.setText("Are you sure you wish to clock out " + technicianName
                    + "?");
        } else {
            body.setText("Are you sure you wish to clock in " + technicianName
                    + "?");
        }

        buttonSave = (TextView) findViewById(R.id.dialog_yes_no_response_button_yes);
        buttonCancel = (TextView) findViewById(R.id.dialog_yes_no_response_button_no);

        buttonSave.setOnClickListener(new saveButtonListener());
        buttonCancel.setOnClickListener(new cancelButtonListener());
    }

    private class saveButtonListener implements
            android.view.View.OnClickListener {

        @Override
        public void onClick(View v) {

            submitOnComplete = true;
            DialogClockInConfirm.this.dismiss();
        }
    }

    ;

    private class cancelButtonListener implements
            android.view.View.OnClickListener {

        @Override
        public void onClick(View v) {
            submitOnComplete = false;
            DialogClockInConfirm.this.dismiss();
        }
    }

    ;
}