package com.skeds.android.phone.business.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.Constants;

public class DialogInvoiceDisclaimer extends Dialog {

    TextView disclaimer;
    TextView buttonDismiss;

    public DialogInvoiceDisclaimer(Context context) {
        super(context);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_layout_invoice_disclaimer);

        disclaimer = (TextView) findViewById(R.id.dialog_invoice_disclaimer_textview_disclaimer);
        buttonDismiss = (TextView) findViewById(R.id.dialog_invoice_disclaimer_button_dismiss);

        buttonDismiss.setOnClickListener(new dismissButtonListener());

        String disclamer = "";
        switch (AppDataSingleton.getInstance().getSignatureViewMode()) {
            case Constants.SIGNATURE_VIEW_FROM_INVOICE:
                disclamer = AppDataSingleton.getInstance().getDisclaimerMessage();
                break;

            case Constants.SIGNATURE_VIEW_FROM_ESTIMATE:
                if (AppDataSingleton.getInstance().getEsitmateDisclaimerMessage() != null)
                    disclamer = AppDataSingleton.getInstance().getEsitmateDisclaimerMessage().isEmpty() ?
                            AppDataSingleton.getInstance().getDisclaimerMessage() :
                            AppDataSingleton.getInstance().getEsitmateDisclaimerMessage();
                break;

        }
        disclaimer.setText(disclamer);
    }

    private class dismissButtonListener implements
            android.view.View.OnClickListener {

        @Override
        public void onClick(View v) {
            DialogInvoiceDisclaimer.this.dismiss();
        }
    }

    ;
}