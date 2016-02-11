package com.skeds.android.phone.business.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.LineItem;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.TaxRate;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.TaxRateType;
import com.skeds.android.phone.business.Utilities.General.NumberFormatTool;
import com.skeds.android.phone.business.Utilities.General.UserUtilitiesSingleton;
import com.skeds.android.phone.business.activities.ActivityLineItemAdd;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class DialogLineItemStandard extends Dialog {

    private LinearLayout pricingLayout;
    private ImageView standardPricing;
    private ImageView additionalPricing;

    private TextView itemName;
    private TextView itemPrice;
    private EditText itemQuantity;

    private TextView buttonAdd;
    private TextView buttonCancel;

    private LineItem selectedLineItem;

    private RelativeLayout extendedTaxesLayout;

    private NumberFormatTool percentFormatTool;

    private NumberFormatTool currencyTool;

    private boolean usingAdditionalPricing = false;

    public DialogLineItemStandard(Context context, LineItem selectedLineItem) {
        super(context);
        this.selectedLineItem = selectedLineItem;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_layout_standard_line_item);
        percentFormatTool = NumberFormatTool.getPercentFormat();
        currencyTool = NumberFormatTool.getCurrencyFormat();
        initResources();
        setupUIforExtendedTaxes();

    }

    private void initResources() {
        pricingLayout = (LinearLayout) findViewById(R.id.dialog_layout_add_line_item_linearlayout_pricing);
        standardPricing = (ImageView) findViewById(R.id.dialog_layout_add_line_item_imageview_standard_price);
        additionalPricing = (ImageView) findViewById(R.id.dialog_layout_add_line_item_imageview_additional_price);

        itemName = (TextView) findViewById(R.id.dialog_layout_add_line_item_textview_name);
        itemPrice = (TextView) findViewById(R.id.dialog_layout_add_line_item_textview_price);
        itemQuantity = (EditText) findViewById(R.id.dialog_layout_add_line_item_edittext_quantity);

        buttonAdd = (TextView) findViewById(R.id.dialog_layout_add_line_item_button_add);
        buttonCancel = (TextView) findViewById(R.id.dialog_layout_add_line_item_button_cancel);

        if (!selectedLineItem.additionalCost.toString()
                .equals("-1") && (!(selectedLineItem.additionalCost.compareTo(BigDecimal.ZERO) == 0))) {
            pricingLayout.setVisibility(View.VISIBLE);
        } else {
            pricingLayout.setVisibility(View.GONE);
            usingAdditionalPricing = false;
        }

        standardPricing.setOnClickListener(new pricingButtonListener());
        additionalPricing.setOnClickListener(new pricingButtonListener());

        buttonAdd.setOnClickListener(new addButtonListener());
        buttonCancel.setOnClickListener(new cancelButtonListener());

        itemName.setText(selectedLineItem.getName());
        itemPrice.setText(currencyTool.format(selectedLineItem.cost));
        itemQuantity.setText("1");
    }

    private void setupUIforExtendedTaxes() {
        extendedTaxesLayout = (RelativeLayout) findViewById(R.id.extendedTaxesLayout);
        if (UserUtilitiesSingleton.getInstance().user.getCountryInfo().isUseExtendedTax()) {
            extendedTaxesLayout.setVisibility(View.VISIBLE);
            TextView taxesLabels = (TextView) extendedTaxesLayout.findViewById(R.id.taxesLabel);
            TextView taxesValues = (TextView) extendedTaxesLayout.findViewById(R.id.taxesValues);

            StringBuilder label = new StringBuilder();
            StringBuilder value = new StringBuilder();

            for (Long rateId : selectedLineItem.rateIds) {
                TaxRate selectedTR = new TaxRate();
                for (TaxRate tr : UserUtilitiesSingleton.getInstance().user.getCountryInfo().getTaxRates())
                    if (tr.getId() == rateId) {
                        selectedTR = tr;
                        break;
                    }

                if (!selectedTR.getType().isEmpty()) {
                    label.append(getTypeName(selectedTR.getType()) + "\n");
                    value.append(selectedTR.getName() + " " + percentFormatTool.format(selectedTR.getValue().doubleValue()) + "\n");
                }
            }

            taxesLabels.setText(label);
            taxesValues.setText(value);
        } else extendedTaxesLayout.setVisibility(View.GONE);
    }

    private String getTypeName(String type) {
        for (TaxRateType trt : UserUtilitiesSingleton.getInstance().user.getCountryInfo().getTaxRateTypes()) {
            if (trt.getType().equals(type)) {
                return trt.getName();
            }
        }
        return type;
    }

    private class addButtonListener implements
            android.view.View.OnClickListener {

        @Override
        public void onClick(View v) {

            BigDecimal cost = (BigDecimal) currencyTool.parse(itemPrice.getText().toString(), true);

            final BigDecimal quantity = new BigDecimal(itemQuantity.getText()
                    .toString());

            selectedLineItem.cost = cost;
            selectedLineItem.setQuantity(quantity);

            selectedLineItem
                    .setUsingAdditionalCost(usingAdditionalPricing);

            selectedLineItem.finalCost = cost
                    .multiply(quantity).setScale(2,
                            BigDecimal.ROUND_HALF_UP);

            ActivityLineItemAdd.mItemsToAdd
                    .add(selectedLineItem);
            selectedLineItem = new LineItem();

            // Stop the dialog
            DialogLineItemStandard.this.dismiss();
        }
    }

    ;

    private class cancelButtonListener implements
            android.view.View.OnClickListener {

        @Override
        public void onClick(View v) {
            DialogLineItemStandard.this.dismiss();
        }
    }

    ;

    private class pricingButtonListener implements
            android.view.View.OnClickListener {

        @Override
        public void onClick(View v) {

            DecimalFormat format = new DecimalFormat("#0.00");

            switch (v.getId()) {
                case R.id.dialog_layout_add_line_item_imageview_standard_price:
                    usingAdditionalPricing = false;
                    standardPricing
                            .setImageResource(R.drawable.line_item_pricing_standard_pressed);
                    additionalPricing
                            .setImageResource(R.drawable.line_item_pricing_additional);

                    itemPrice
                            .setText(currencyTool.format(selectedLineItem.cost));

                    break;
                case R.id.dialog_layout_add_line_item_imageview_additional_price:
                    usingAdditionalPricing = true;
                    standardPricing
                            .setImageResource(R.drawable.line_item_pricing_standard);
                    additionalPricing
                            .setImageResource(R.drawable.line_item_pricing_additional_pressed);

                    itemPrice
                            .setText(currencyTool.format(selectedLineItem.additionalCost));

                    break;
                default:
                    // Nothing
                    break;
            }
        }
    }

    ;
}