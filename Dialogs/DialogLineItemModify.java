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
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Country;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.LineItem;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.TaxRate;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.TaxRateType;
import com.skeds.android.phone.business.Utilities.General.Constants;
import com.skeds.android.phone.business.Utilities.General.NumberFormatTool;
import com.skeds.android.phone.business.Utilities.General.UserUtilitiesSingleton;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class DialogLineItemModify extends Dialog {

    private Context context;

    private LinearLayout extendedTaxesLayout;
    private EditText itemName;
    private EditText itemPrice;
    private EditText itemQuantity;
    private EditText itemDescription;
    private TextView costText;

    private TextView buttonAdd;
    private TextView buttonCancel;

    private LinearLayout saveAsNewItemLayout;
    private CheckBox saveAsNewItem;

    private CheckedTextView itemIsTaxable;

    private final int lineItemAddViewMode;
    private final int lineItemNumber;

    private NumberFormatTool percentFormatTool;

    public static LineItem lineItem;

    // line item's current rate ids
    private List<Long> currentRateIds;

    public DialogLineItemModify(Context context, int lineItemAddViewMode, int lineItemNumber) {
        super(context);
        this.context = context;
        this.lineItemAddViewMode = lineItemAddViewMode;
        this.lineItemNumber = lineItemNumber;
        lineItem = new LineItem();
        // reset rate ids
        lineItem.resetRates();
        percentFormatTool = NumberFormatTool.getPercentFormat();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_layout_modify_line_item);

        initResources();
        setupSelectedItem();

        currentRateIds = new ArrayList<Long>();
        currentRateIds.addAll(lineItem.rateIds);

        itemName.setText(lineItem.getName());
        itemPrice.setText(lineItem.cost.toString());
        itemQuantity.setText(String.valueOf(lineItem.getQuantity()));
        itemDescription.setText(lineItem.getDescription());
        itemIsTaxable.setChecked(lineItem.getTaxable());

        buttonAdd.setText(R.string.button_string_modify);
        TextView headerText = (TextView) findViewById(R.id.dialog_textview_header);
        headerText.setText(R.string.modify_line_item);

        setupUIforExtendedTaxes();
    }

    private void setupUIforExtendedTaxes() {
        extendedTaxesLayout = (LinearLayout) findViewById(R.id.extendedTaxesLayoutWithButtons);
        if (lineItem.getTaxable()) {
            extendedTaxesLayout.setVisibility(View.VISIBLE);
            ;
        } else
            extendedTaxesLayout.setVisibility(View.GONE);
        Country countryInfo = UserUtilitiesSingleton.getInstance().user.getCountryInfo();
        if (countryInfo.isUseExtendedTax()) {
            long index = 0;
            for (TaxRateType trt : countryInfo.getTaxRateTypes()) {
                View itemView = LayoutInflater.from(context).inflate(
                        R.layout.field_extended_taxes_with_button, null);
                extendedTaxesLayout.addView(itemView);

                TextView label = (TextView) itemView.findViewById(R.id.rateTypeLabel);
                label.setText(context.getString(R.string.tax_label_format, trt.getName(), trt.getType()));

                TextView button = (TextView) itemView.findViewById(R.id.rateTypeButton);

                if (!lineItem.isCustomLineItem()) {
                    button.setEnabled(false);
                    button.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
                }

                TaxRate template = new TaxRate();
                template.setId(-1L * ++index);
                template.setType(trt.getType());
                button.setTag(template);
                button.setOnClickListener(taxRateTypesListener);

                List<TaxRate> selectedTaxes = new ArrayList<TaxRate>();
                for (Long rateId : lineItem.rateIds) {
                    for (TaxRate tr : countryInfo.getTaxRates())
                        if (tr.getId() == rateId) {
                            selectedTaxes.add(tr);
                            break;
                        }
                }
                for (TaxRate tr : selectedTaxes) {
                    if (trt.getType().equals(tr.getType())) {
                        button.setText(tr.getName() + " " + percentFormatTool.format(tr.getValue().doubleValue()));
                        button.setTag(tr);
                        break;
                    }
                }

            }
        }
        if (!lineItem.isCustomLineItem()) {
            itemIsTaxable.setClickable(false);
            itemIsTaxable.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
        }
    }

    private void setupSelectedItem() {
        switch (lineItemAddViewMode) {
            case Constants.LINE_ITEM_ADD_VIEW_FROM_ESTIMATE:
                lineItem = AppDataSingleton.getInstance().getEstimate().getLineItems()
                        .get(lineItemNumber);
                break;
            case Constants.LINE_ITEM_ADD_VIEW_FROM_INVOICE:
                lineItem = AppDataSingleton.getInstance().getInvoice().getLineItems()
                        .get(lineItemNumber);
                break;
            case Constants.LINE_ITEM_ADD_VIEW_FROM_PRICEBOOK_LIST:
                // Nothing
                break;
            default:
                // Nothing
                break;
        }
    }

    private void initResources() {
        itemName = (EditText) findViewById(R.id.dialog_add_custom_line_item_edittext_name);
        itemPrice = (EditText) findViewById(R.id.dialog_add_custom_line_item_edittext_price);
        itemPrice.addTextChangedListener(costWatcher);
        costText = (TextView) findViewById(R.id.dialog_add_custom_line_item_edittext_cost);
        itemQuantity = (EditText) findViewById(R.id.dialog_add_custom_line_item_edittext_quantity);
        itemQuantity.addTextChangedListener(qtyWatcher);

        itemDescription = (EditText) findViewById(R.id.dialog_add_custom_line_item_edittext_description);

        saveAsNewItemLayout = (LinearLayout) findViewById(R.id.dialog_add_custom_line_item_linearlayout_save_item);
        saveAsNewItem = (CheckBox) findViewById(R.id.dialog_add_custom_line_item_checkbox_save_item);

        itemIsTaxable = (CheckedTextView) findViewById(R.id.dialog_add_custom_line_item_checkbox_taxable);
        itemIsTaxable.setOnClickListener(listener);

        // Hide this until feature is implemented
        saveAsNewItemLayout.setVisibility(View.GONE);

        buttonAdd = (TextView) findViewById(R.id.dialog_add_custom_line_item_button_add);
        buttonCancel = (TextView) findViewById(R.id.dialog_add_custom_line_item_button_cancel);

        buttonAdd.setOnClickListener(listener);
        buttonCancel.setOnClickListener(listener);
    }

    private android.view.View.OnClickListener listener = new android.view.View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.dialog_add_custom_line_item_checkbox_taxable:
                    itemIsTaxable.toggle();

                    if (extendedTaxesLayout != null)
                        if (itemIsTaxable.isChecked()) {
                            extendedTaxesLayout.setVisibility(View.VISIBLE);
                        } else {
                            extendedTaxesLayout.setVisibility(View.GONE);
                        }
                    break;
                case R.id.dialog_add_custom_line_item_button_add:
                    if (TextUtils.isEmpty(itemName.getText())) {
                        Toast.makeText(getContext(), R.string.enter_name, Toast.LENGTH_LONG).show();
                    } else if (TextUtils.isEmpty(itemPrice.getText())) {
                        Toast.makeText(getContext(), R.string.enter_price, Toast.LENGTH_LONG).show();
                    } else if (TextUtils.isEmpty(itemQuantity.getText())) {
                        Toast.makeText(getContext(), R.string.enter_quantity, Toast.LENGTH_LONG).show();
                    } else {
                        lineItem.setUserAdded(true);
                        if (lineItem.cost.toString().equals(itemPrice.getText().toString())) {
                            lineItem.setCustomPrice(false);
                        } else {
                            lineItem.setCustomPrice(true);
                        }

                        lineItem.setName(itemName.getText().toString());
                        lineItem.cost = new BigDecimal(itemPrice.getText().toString()).setScale(2, BigDecimal.ROUND_HALF_UP);
                        lineItem.setQuantity(new BigDecimal(itemQuantity.getText().toString()));
                        lineItem.setDescription(itemDescription.getText().toString());
                        lineItem.setRemovable(true);
                        lineItem.setCustomLineItem(true);
                        lineItem.setTaxable(itemIsTaxable.isChecked());
                        if (!itemIsTaxable.isChecked()) {
                            lineItem.resetRates();
                        }

                        BigDecimal finalCost = new BigDecimal("0.00").setScale(2, BigDecimal.ROUND_HALF_UP);
                        finalCost = lineItem.cost.multiply(lineItem.getQuantity());
                        lineItem.finalCost = finalCost.setScale(2, BigDecimal.ROUND_HALF_UP);

                        switch (lineItemAddViewMode) {
                            case Constants.LINE_ITEM_ADD_VIEW_FROM_ESTIMATE:
                                AppDataSingleton.getInstance().getEstimate().getLineItems().set(lineItemNumber, lineItem);
                                break;
                            case Constants.LINE_ITEM_ADD_VIEW_FROM_INVOICE:
                                AppDataSingleton.getInstance().getInvoice().getLineItems().set(lineItemNumber, lineItem);
                                break;
                            default:
                                break;
                        }
                        DialogLineItemModify.this.dismiss();
                    }
                    break;
                case R.id.dialog_add_custom_line_item_button_cancel:
                    lineItem.rateIds = currentRateIds;
                    DialogLineItemModify.this.dismiss();
                    break;
                default:
                    break;
            }
        }
    };


    private View.OnClickListener taxRateTypesListener = new View.OnClickListener() {

        @Override
        public void onClick(final View v) {
            TaxRate rate = (TaxRate) v.getTag();
            DialogTaxRatesList dialog = new DialogTaxRatesList(context, rate, DialogTaxRatesList.MODIFY_MODE);
            dialog.setOnDismissListener(new OnDismissListener() {

                @Override
                public void onDismiss(DialogInterface dialog) {
                    TextView btn = (TextView) v;
                    DialogTaxRatesList d = (DialogTaxRatesList) dialog;

                    for (TaxRate tr : UserUtilitiesSingleton.getInstance().user.getCountryInfo().getTaxRates())
                        if (tr.getId() == d.selectedRateId) {
                            btn.setTag(tr);
                            btn.setText(tr.getName() + " " + (int) (tr.getValue().doubleValue() * 100) + "%");
                            //reset value
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
            String qty = s.toString();
            if (TextUtils.isEmpty(qty))
                return;

            String itemPriceString = itemPrice.getText().toString();
            BigDecimal cost = new BigDecimal("0.0");
            BigDecimal quantity = new BigDecimal("0.0");
            try {
                cost = new BigDecimal(itemPriceString).setScale(2, BigDecimal.ROUND_HALF_UP);

                quantity = new BigDecimal(qty).setScale(2, BigDecimal.ROUND_HALF_UP);

                costText.setText(cost.multiply(quantity
                ).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
            } catch (NumberFormatException ex) {
                ex.printStackTrace();
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
            String price = s.toString();
            if (TextUtils.isEmpty(price))
                return;

            String itemPriceString = itemPrice.getText().toString();

            String quantityString = itemQuantity.getText().toString();

            BigDecimal cost = new BigDecimal("0.0");

            BigDecimal quantity = new BigDecimal("0.0");

            try {
                cost = new BigDecimal(itemPriceString).setScale(2, BigDecimal.ROUND_HALF_UP);

                quantity = new BigDecimal(quantityString).setScale(2, BigDecimal.ROUND_HALF_UP);

                costText.setText(cost.multiply(
                        quantity).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
            } catch (NumberFormatException ex) {
                ex.printStackTrace();
            }


        }
    };

}