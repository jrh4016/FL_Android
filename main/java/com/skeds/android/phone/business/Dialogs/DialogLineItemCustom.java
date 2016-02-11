package com.skeds.android.phone.business.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.LineItem;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.TaxRate;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.TaxRateType;
import com.skeds.android.phone.business.Utilities.General.Constants;
import com.skeds.android.phone.business.Utilities.General.NumberFormatTool;
import com.skeds.android.phone.business.Utilities.General.UserUtilitiesSingleton;
import com.skeds.android.phone.business.activities.ActivityEstimateView;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class DialogLineItemCustom extends Dialog {

    private EditText itemName;
    private EditText itemPrice;
    private EditText itemQuantity;
    private EditText itemDescription;
    private TextView costText;

    private Context context;

    private LinearLayout extendedTaxesLayout;

    private TextView buttonAdd;
    private TextView buttonCancel;

    private LinearLayout saveAsNewItemLayout;
    private CheckBox saveAsNewItem;

    private CheckBox itemIsTaxable;

    private int lineItemViewMode;

    private NumberFormatTool percentFormatTool;

    public static LineItem customLineItem;

    public DialogLineItemCustom(Context context, int lineItemViewMode) {
        super(context);
        this.context = context;
        this.lineItemViewMode = lineItemViewMode;
        customLineItem = new LineItem();
        // reset rate ids
        customLineItem.resetRates();
        percentFormatTool = NumberFormatTool.getPercentFormat();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_layout_custom_line_item);

        initResources();

        setupUIforExtendedTaxes();
    }

    private void setupUIforExtendedTaxes() {
        extendedTaxesLayout = (LinearLayout) findViewById(R.id.extendedTaxesLayoutWithButtons);
        extendedTaxesLayout.setVisibility(View.GONE);
        if (UserUtilitiesSingleton.getInstance().user.getCountryInfo().isUseExtendedTax()) {
            long index = 0;
            for (TaxRateType trt : UserUtilitiesSingleton.getInstance().user.getCountryInfo().getTaxRateTypes()) {
                View itemView = LayoutInflater.from(context).inflate(
                        R.layout.field_extended_taxes_with_button, null);
                extendedTaxesLayout.addView(itemView);

                TextView label = (TextView) itemView.findViewById(R.id.rateTypeLabel);
                label.setText(trt.getName() + " (" + trt.getType() + ")");

                TextView button = (TextView) itemView.findViewById(R.id.rateTypeButton);

                TaxRate template = new TaxRate();
                template.setId(-1L * ++index);
                template.setType(trt.getType());
                button.setTag(template);
                button.setOnClickListener(taxRateTypesListener);
            }
        }
    }

    private void initResources() {
        itemName = (EditText) findViewById(R.id.dialog_add_custom_line_item_edittext_name);
        itemPrice = (EditText) findViewById(R.id.dialog_add_custom_line_item_edittext_price);
        itemPrice.addTextChangedListener(costWatcher);
        itemQuantity = (EditText) findViewById(R.id.dialog_add_custom_line_item_edittext_quantity);
        itemQuantity.addTextChangedListener(qtyWatcher);
        itemDescription = (EditText) findViewById(R.id.dialog_add_custom_line_item_edittext_description);
        costText = (TextView) findViewById(R.id.dialog_add_custom_line_item_edittext_cost);

        saveAsNewItemLayout = (LinearLayout) findViewById(R.id.dialog_add_custom_line_item_linearlayout_save_item);
        saveAsNewItem = (CheckBox) findViewById(R.id.dialog_add_custom_line_item_checkbox_save_item);

        itemIsTaxable = (CheckBox) findViewById(R.id.dialog_add_custom_line_item_checkbox_taxable);
        itemIsTaxable.setOnCheckedChangeListener(taxableListener);

        // Hide this until feature is implemented
        saveAsNewItemLayout.setVisibility(View.GONE);

        buttonAdd = (TextView) findViewById(R.id.dialog_add_custom_line_item_button_add);
        buttonCancel = (TextView) findViewById(R.id.dialog_add_custom_line_item_button_cancel);

        buttonAdd.setOnClickListener(new addButtonListener());
        buttonCancel.setOnClickListener(new cancelButtonListener());
    }

    private class addButtonListener implements
            android.view.View.OnClickListener {

        @Override
        public void onClick(View v) {

            if (TextUtils.isEmpty(itemName.getText())) {
                Toast.makeText(getContext(), "Please enter an item name.",
                        Toast.LENGTH_LONG).show();
            } else if (TextUtils.isEmpty(itemPrice.getText())) {
                Toast.makeText(getContext(), "Please enter an item price.",
                        Toast.LENGTH_LONG).show();
            } else if (TextUtils.isEmpty(itemQuantity.getText())) {
                Toast.makeText(getContext(), "Please enter an item quantity.",
                        Toast.LENGTH_LONG).show();
            } else {

                try {
                    customLineItem.setName(itemName.getText().toString());
                    customLineItem.cost = new BigDecimal(itemPrice.getText()
                            .toString()).setScale(2, BigDecimal.ROUND_HALF_UP);
                    // is custom price
                    customLineItem.setCustomPrice(true);
                    customLineItem.setQuantity(new BigDecimal(itemQuantity
                            .getText().toString()));
                    customLineItem.setDescription(itemDescription.getText()
                            .toString());
                    customLineItem.setRemovable(true);
                    customLineItem.setCustomLineItem(true);
                    customLineItem.setUserAdded(true);
                    customLineItem.setTaxable(itemIsTaxable.isChecked());
                    if (!itemIsTaxable.isChecked()) {
                        customLineItem.resetRates();
                    }

                    BigDecimal finalCost = new BigDecimal("0.00").setScale(2,
                            BigDecimal.ROUND_HALF_UP);
                    // double finalCost = 0.0;
                    finalCost = customLineItem.cost.multiply(
                            customLineItem.getQuantity()).setScale(
                            2, BigDecimal.ROUND_HALF_UP);
                    // finalCost = customLineItem.getCost()
                    // * customLineItem.getQuantity();

                    customLineItem.finalCost = finalCost;

                    switch (lineItemViewMode) {
                        case Constants.LINE_ITEM_ADD_VIEW_FROM_ESTIMATE:
                            AppDataSingleton.getInstance().getEstimate().getLineItems().add(customLineItem);
                            break;
                        case Constants.LINE_ITEM_ADD_VIEW_FROM_INVOICE:
                            AppDataSingleton.getInstance().getInvoice().getLineItems().add(customLineItem);
                            break;
                        case Constants.LINE_ITEM_ADD_VIEW_FROM_PRICEBOOK_LIST:
                            // Nothing
                            break;
                        default:
                            // Nothing
                            break;
                    }
                    ActivityEstimateView.hasAnyChanges = true;
                    DialogLineItemCustom.this.dismiss();
                } catch (Throwable ex) {
                    Toast.makeText(context, "Check inputs", Toast.LENGTH_SHORT).show();
                    ex.printStackTrace();
                }

            }
        }
    }

    ;

    private class cancelButtonListener implements
            android.view.View.OnClickListener {

        @Override
        public void onClick(View v) {
            DialogLineItemCustom.this.dismiss();
        }
    }

    ;

    private View.OnClickListener taxRateTypesListener = new View.OnClickListener() {

        @Override
        public void onClick(final View v) {
            TaxRate rate = (TaxRate) v.getTag();
            DialogTaxRatesList dialog = new DialogTaxRatesList(context, rate, DialogTaxRatesList.CUSTOM_MODE);
            dialog.setOnDismissListener(new OnDismissListener() {

                @Override
                public void onDismiss(DialogInterface dialog) {
                    TextView btn = (TextView) v;
                    DialogTaxRatesList d = (DialogTaxRatesList) dialog;

                    for (TaxRate tr : UserUtilitiesSingleton.getInstance().user.getCountryInfo().getTaxRates())
                        if (tr.getId() == d.selectedRateId) {

                            DecimalFormat df = new DecimalFormat();

                            df.setMaximumFractionDigits(4);

                            df.setMinimumFractionDigits(0);

                            df.setGroupingUsed(false);

                            btn.setTag(tr);
                            btn.setText(tr.getName() + " " + df.format(tr.getValue().multiply(new BigDecimal("100.00"))) + "%");
                            d.selectedRateId = 0;
                            break;
                        }
                }
            });
            dialog.show();
        }
    };

    private TextWatcher qtyWatcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            try {
                String qty = s.toString();
                if (TextUtils.isEmpty(qty))
                    return;
                BigDecimal cost = new BigDecimal(itemPrice.getText().toString())
                        .setScale(2, BigDecimal.ROUND_HALF_UP);
                costText.setText("" + cost.multiply(
                        new BigDecimal(qty)));
            } catch (Exception ex) {
                //Do nothing
            }
        }
    };

    private TextWatcher costWatcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            try {
                if (TextUtils.isEmpty(s.toString()))
                    return;

                BigDecimal cost = new BigDecimal(itemPrice.getText().toString())
                        .setScale(2, BigDecimal.ROUND_HALF_UP);

                costText.setText("" + cost.multiply(
                        new BigDecimal(itemQuantity.getText().toString())));
            } catch (Exception ex) {
                //Do nothing
            }
        }
    };

    private OnCheckedChangeListener taxableListener = new OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (extendedTaxesLayout != null)
                if (isChecked) {
                    extendedTaxesLayout.setVisibility(View.VISIBLE);
                } else {
                    extendedTaxesLayout.setVisibility(View.GONE);
                }
        }
    };

}