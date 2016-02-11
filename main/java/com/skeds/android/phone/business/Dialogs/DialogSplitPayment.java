package com.skeds.android.phone.business.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Payment;
import com.skeds.android.phone.business.Utilities.General.CommonUtilities;
import com.skeds.android.phone.business.activities.ActivityPaymentOptionTypesView;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class DialogSplitPayment extends Dialog {

    TextView alreadyPaid;
    EditText paymentAmount;

    TextView buttonSave;
    TextView buttonCancel;

    public boolean isCanceled;

    private Context mContext;
    BigDecimal remainingCost;

    public DialogSplitPayment(Context context) {
        super(context);
        mContext = context;
        remainingCost = AppDataSingleton.getInstance().getInvoice().getAmountDue();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_layout_split_payment);

        alreadyPaid = (TextView) findViewById(R.id.dialog_split_payment_textview_currently_paid);
        paymentAmount = (EditText) findViewById(R.id.dialog_split_payment_edittext_amount);

        buttonSave = (TextView) findViewById(R.id.dialog_split_payment_button_save);
        buttonCancel = (TextView) findViewById(R.id.dialog_split_payment_button_cancel);

        BigDecimal paymentValue = BigDecimal.ZERO;

        if (AppDataSingleton.getInstance().getInvoice().getPayments().isEmpty()) {
            alreadyPaid.setText("");
        } else {
            StringBuilder output = new StringBuilder();
            for (final Payment payment : AppDataSingleton.getInstance().getInvoice().getPayments()) {
                output.append("Customer paid $" + payment.paymentAmount);
                output.append(" via " + payment.getPaymentType() + ".\n");
                paymentValue = paymentValue.add(payment.paymentAmount);
            }
            String paidText = output.toString();
            alreadyPaid.setText(paidText);
        }

        remainingCost = remainingCost.subtract(paymentValue);
        // BigDecimal
        paymentAmount.setText(remainingCost.toString());

        buttonSave.setOnClickListener(new saveButtonListener());
        buttonCancel.setOnClickListener(new cancelButtonListener());
    }

    private class saveButtonListener implements
            android.view.View.OnClickListener {


        @Override
        public void onClick(View v) {

            DecimalFormat format = new DecimalFormat("#0.00");

            String paymentAmountText = paymentAmount.getText().toString();

            try {
                BigDecimal paymentValue = new BigDecimal(
                        paymentAmountText.trim());
                paymentValue.setScale(2, BigDecimal.ROUND_HALF_UP);

                if (!paymentValue.equals(0)
                        && (remainingCost.compareTo(paymentValue) == 1 || remainingCost
                        .compareTo(paymentValue) == 0)) {

                    Payment thisPayment = new Payment();
                    if (ActivityPaymentOptionTypesView.mPaymentType==null)
                    {
                        Toast.makeText(getContext(),"Select payment type",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if ("CREDIT_CARD".equals(ActivityPaymentOptionTypesView.mPaymentType.name())) {
                        if (AppDataSingleton.getInstance().getInvoice().isAcceptAmericanExpress() == false
                                && AppDataSingleton.getInstance().getInvoice().isAcceptDiscover() == false
                                && AppDataSingleton.getInstance().getInvoice().isAcceptMasterCard() == false
                                && AppDataSingleton.getInstance().getInvoice().isAcceptVisa() == false) {

                            thisPayment.setPaymentType("CREDIT_CARD");
                            thisPayment.paymentAmount = paymentValue;

                            AppDataSingleton.getInstance().getInvoice().getPayments().add(thisPayment);
                        } else {

                            thisPayment
                                    .setPaymentType(ActivityPaymentOptionTypesView.mPaymentType
                                            .name());
                            thisPayment.paymentAmount = paymentValue;

                            AppDataSingleton.getInstance().getInvoice().getPayments().add(thisPayment);
                        }
                    } else if (ActivityPaymentOptionTypesView.mPaymentType.name().equals(
                            "CHECK")) {

                        thisPayment
                                .setPaymentType(ActivityPaymentOptionTypesView.mPaymentType
                                        .name());
                        thisPayment
                                .setCheckNumber((int) ActivityPaymentOptionTypesView.mCheckNumberValue);
                        thisPayment.paymentAmount = paymentValue;

                        AppDataSingleton.getInstance().getInvoice().getPayments().add(thisPayment);

                    } else {

                        thisPayment
                                .setPaymentType(ActivityPaymentOptionTypesView.mPaymentType
                                        .name());
                        thisPayment.paymentAmount = paymentValue;

                        AppDataSingleton.getInstance().getInvoice().getPayments().add(thisPayment);
                    }

                    ActivityPaymentOptionTypesView.paidAmount = ActivityPaymentOptionTypesView.paidAmount
                            .add(paymentValue);
                    if (!CommonUtilities.isNetworkAvailable(mContext)) {
                        Toast.makeText(mContext, "Network connection unavailable.",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    DialogSplitPayment.this.dismiss();
                } else {
                    Toast.makeText(getContext(), "Please input a valid amount",
                            Toast.LENGTH_LONG).show();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(
                        getContext(),
                        "Input proper amount into payment field. It does not appear to be a dollar amount",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    ;

    private class cancelButtonListener implements
            android.view.View.OnClickListener {

        @Override
        public void onClick(View v) {
            isCanceled = true;
            DialogSplitPayment.this.dismiss();
        }
    }

    ;
}