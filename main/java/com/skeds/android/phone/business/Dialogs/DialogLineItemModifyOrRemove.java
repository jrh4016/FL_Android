package com.skeds.android.phone.business.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.skeds.android.phone.business.R;

public class DialogLineItemModifyOrRemove extends Dialog {

    TextView textTitle;
    TextView textBody;

    TextView buttonModify;
    TextView buttonRemove;

    private String title;

    public int dialogResult;

    public DialogLineItemModifyOrRemove(Context context, String title) {
        super(context);
        this.title = title;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dialogResult = -1;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_layout_yes_no_response);

        textTitle = (TextView) findViewById(R.id.dialog_yes_no_response_textview_title);
        textBody = (TextView) findViewById(R.id.dialog_yes_no_response_textview_body);

        buttonModify = (TextView) findViewById(R.id.dialog_yes_no_response_button_yes);
        buttonRemove = (TextView) findViewById(R.id.dialog_yes_no_response_button_no);

        textTitle.setText(title);
        textBody.setText("Please select an option, or press back to cancel");

        buttonModify.setText("Modify");
        buttonRemove.setText("Remove");

        buttonModify.setOnClickListener(new modifyButtonListener());
        buttonRemove.setOnClickListener(new removeButtonListener());
    }

    private class modifyButtonListener implements
            android.view.View.OnClickListener {

        @Override
        public void onClick(View v) {

            dialogResult = 0; // Modify

            // Stop the dialog
            DialogLineItemModifyOrRemove.this.dismiss();
        }
    }

    ;

    private class removeButtonListener implements
            android.view.View.OnClickListener {

        @Override
        public void onClick(View v) {

            dialogResult = 1; // Remove
            DialogLineItemModifyOrRemove.this.dismiss();
        }
    }

    ;
}