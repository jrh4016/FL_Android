package com.skeds.android.phone.business.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.skeds.android.phone.business.Dialogs.DialogErrorPopup;
import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Customer;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Estimate;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.LineItem;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.PriceBook.TaxValue;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.TaxRate;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.TaxRateType;
import com.skeds.android.phone.business.Utilities.General.Constants;
import com.skeds.android.phone.business.Utilities.General.NumberFormatTool;
import com.skeds.android.phone.business.Utilities.General.TaxAmountCalculator;
import com.skeds.android.phone.business.Utilities.General.UserUtilitiesSingleton;
import com.skeds.android.phone.business.core.SkedsApplication;

import org.w3c.dom.Text;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pdftron.PDF.Annots.Line;

public class ActivityEstimateCustomerView extends BaseSkedsActivity {


    private final String cloneInJson;
    private NumberFormatTool currencyFormatTool = NumberFormatTool.getCurrencyFormat();

    //private HashMap<Integer, Boolean> lineItemsStates;

    private ArrayList<RadioButton> rGroupCheckedList;

    private LinearLayout estimationItemContainer;

    private TextView youSavedText;
    private TextView fromServicePlan;

    private BigDecimal estimateTotalValue;
    private BigDecimal estimateNetTotalValue;

    private TextView textCustomerName;
    private TextView textPhoneNumber;
    private TextView textEmailAddress;
    private TextView textAddress;
    private TextView textSavedAmount;
    private TextView textNotes;
    private TextView textCreated;
    private TextView textAgreement;

    private TextView textSubtotal;

    private Customer customer;

    private DialogErrorPopup noticePopup;

    private String agreementName = "<none>";
    private String notes = "";
    private String tax = "";

    private final static String mPercentageString = "Using percentage for discount";


    private Button completeButton;
    private ImageView backButton;

    private OnClickListener buttonListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.activity_estimate_button_back:
                    noticePopup = new DialogErrorPopup(
                            ActivityEstimateCustomerView.this, "Notice",
                            "Please give the device back to the technician.", null);
                    noticePopup.show();
                    noticePopup.setOnDismissListener(new OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            Intent returnIntent = new Intent();
                            setResult(RESULT_CANCELED, returnIntent);

                            Gson gson = new Gson();
                            AppDataSingleton.getInstance().setEstimate(gson.fromJson(cloneInJson, Estimate.class));
                            finish();

                        }
                    });
                    break;
                case R.id.activity_estimate_button_complete:
                    if (!isEverythingChecked()) {
                        Toast.makeText(ActivityEstimateCustomerView.this,
                                "Accept Or Decline Each Item please", Toast.LENGTH_LONG).show();
                    } else {
                        Estimate estimate = AppDataSingleton.getInstance()
                                .getEstimate();

                        estimate.setNetTotal(estimateNetTotalValue);
                        estimate.setTotal(estimateTotalValue);

                        if (signatureIsEmpty())
                            startActivityForResult(new Intent(
                                    ActivityEstimateCustomerView.this,
                                    ActivityEstimateCustomerSignature.class), 2);
                        else {
                            setResult(RESULT_OK);
                            finish();
                        }
                    }
                    break;
                default:
                    break;
            }

        }
    };

    private boolean dialogDiscountUsingPercentage;
    private LinearLayout taxTitleContainer;
    private LinearLayout taxValueContainer;
    private TextView textDiscount;
    private TextView textTotal;

    public ActivityEstimateCustomerView() {
        Gson gson = new Gson();
        cloneInJson = gson.toJson(AppDataSingleton.getInstance().getEstimate());

    }


    private boolean signatureIsEmpty() {
        return AppDataSingleton.getInstance().getEstimate().getSignature() == null
                || AppDataSingleton.getInstance().getEstimate().getSignature().isEmpty();
    }

    private OnCheckedChangeListener radioListener = new OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            int lineItemId = (Integer) group.getTag();
            RadioButton checkedRadioButton = (RadioButton) group
                    .findViewById(checkedId);

            updateRecommendation(checkedRadioButton.getId(), lineItemId);

            calculateTotal();
        }
    };

    @Override
    public View onCreateView(String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }

    private void calculateTotal() {

        estimateNetTotalValue = BigDecimal.ZERO;
        estimateTotalValue = BigDecimal.ZERO;




        BigDecimal sumTotal = BigDecimal.ZERO;
        for (int i = 0; i < AppDataSingleton.getInstance().getEstimate().getLineItems().size(); i++) {
            LineItem lineItem = AppDataSingleton.getInstance().getEstimate().getLineItems().get(i);

            sumTotal = sumTotal.add(lineItem.getFinalCost());

            if (lineItem.getRecommendation() == LineItem.Recommendation.DECLINED) continue;

            estimateNetTotalValue = estimateNetTotalValue.add(lineItem.getFinalCost());
        }




        BigDecimal discount = BigDecimal.ZERO;
        if (AppDataSingleton.getInstance().getEstimate().getDescription().contains(mPercentageString)) {
            try {
                String description  = AppDataSingleton.getInstance().getEstimate().getDescription();

                String parse =  description.substring(mPercentageString.length(),description.indexOf("%"));
                BigDecimal discountPercent = new BigDecimal(parse.trim());

                discount =
                        estimateNetTotalValue.multiply(discountPercent)
                                .divide(new BigDecimal("100.0").setScale(2, RoundingMode.HALF_UP), 2, RoundingMode.HALF_UP);
            }catch (Exception ex){
                ex.printStackTrace();
            }
        } else {
            discount = AppDataSingleton.getInstance().getEstimate().getDiscount();
        }


        AppDataSingleton.getInstance().getEstimate().setDiscount(discount);

        AppDataSingleton.getInstance().getEstimate().getTaxes().clear();

        BigDecimal runningTax = new TaxAmountCalculator().calculateTaxAmount(AppDataSingleton.getInstance().getEstimate()).setScale(2, RoundingMode.HALF_UP);

        taxTitleContainer.removeAllViews();

        taxValueContainer.removeAllViews();
        if (UserUtilitiesSingleton.getInstance().user.getCountryInfo().isUseExtendedTax()) {

            //AppDataSingleton.getInstance().getEstimate().getTaxes().clear();
            List<TaxRateType> taxRateTypesForBusiness = UserUtilitiesSingleton.getInstance().user.getCountryInfo().getTaxRateTypes();


            Map<TaxRate, BigDecimal> extendedInfoTypes = AppDataSingleton.getInstance().getEstimate().getExtendedInfoTypes();
            for (TaxRate taxRate : extendedInfoTypes.keySet()) {

                TextView title = new TextView(this);
                title.setContentDescription(taxRate.getName() + "_label");

                title.setTextColor(getResources().getColor(android.R.color.black));
                title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                title.setText(taxRate.getName());
                title.setTypeface(Typeface.DEFAULT_BOLD);
                taxTitleContainer.addView(title);

                TextView value = new TextView(this);
                value.setContentDescription(taxRate.getName() + "_value");

                value.setTextColor(getResources().getColor(android.R.color.black));
                value.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                value.setTypeface(Typeface.DEFAULT_BOLD);
                value.setText(currencyFormatTool.format(extendedInfoTypes.get(taxRate).multiply(taxRate.getValue())));
                taxValueContainer.addView(value);
            }

        } else {
            TextView title = new TextView(this);
            title.setContentDescription("tax_label");

            title.setTextColor(getResources().getColor(android.R.color.black));
            title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            title.setText("Tax");
            title.setTypeface(Typeface.DEFAULT_BOLD);
            taxTitleContainer.addView(title);

            TextView value = new TextView(this);
            value.setContentDescription("tax_value");

            value.setTextColor(getResources().getColor(android.R.color.black));
            value.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            value.setTypeface(Typeface.DEFAULT_BOLD);
            value.setText(currencyFormatTool.format(runningTax.setScale(2, RoundingMode.HALF_UP)));
            taxValueContainer.addView(value);
        }

        BigDecimal tax = calculateTaxAmount();
        estimateTotalValue =
                estimateNetTotalValue.add(tax)
                        .subtract(discount);

        BigDecimal finalTotal = (UserUtilitiesSingleton.getInstance().user.getCountryInfo().isUseExtendedTax() ?
                sum(estimateNetTotalValue, AppDataSingleton.getInstance().getEstimate().getTaxes()) :
                estimateNetTotalValue.add(runningTax))
                .subtract(discount);

        textDiscount.setText(currencyFormatTool.format(discount));
        //textTax.setText(stringBuilder);
        //textTotal.setText(currencyFormatTool.format(finalTotal));
        textTotal.setText(currencyFormatTool.format(estimateTotalValue));
        textSubtotal.setText(currencyFormatTool.format(estimateNetTotalValue));
    }

    private String inflateTotalTaxItem(TaxRate taxRateType, BigDecimal calculatedValue) {


        return taxRateType.getName() + "   " + currencyFormatTool.format(calculatedValue) + "\n";
    }

    private BigDecimal sum(BigDecimal subtotal, List<TaxValue> taxes) {
        BigDecimal sum = subtotal;
        for (TaxValue taxValue : taxes) {
            sum = sum.add(taxValue.getValue());
        }
        return sum;
    }

    private BigDecimal calculateTaxAmount() {

        return new TaxAmountCalculator().calculateTaxAmount(AppDataSingleton.getInstance().getEstimate());
    }

    private TaxRate getTaxRate(LineItem lineItem, TaxRateType trt) {
        TaxRate selectedRate = new TaxRate();
        for (Long rateId : lineItem.rateIds) {

            for (TaxRate rate : UserUtilitiesSingleton.getInstance().user.getCountryInfo().getTaxRates()) {
                if (rate.getId() == rateId && rate.getType().equals(trt.getType())) {
                    selectedRate = rate;
                    break;
                }

            }
        }
        return selectedRate;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_estimate_customer_view);

        estimationItemContainer = (LinearLayout) findViewById(R.id.activity_estimate_items_container);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.getString("service_agreement") != null) {
                agreementName = bundle.getString("service_agreement");
            }
            if (bundle.getString("notes") != null) {
                notes = bundle.getString("notes");
            }


            if (bundle.getSerializable("discountAmount")!=null)
                dialogDiscountUsingPercentage = (Boolean) bundle.getSerializable("percentage");
        }

        rGroupCheckedList = new ArrayList<RadioButton>();
        youSavedText = (TextView) findViewById(R.id.you_saved);
        fromServicePlan = (TextView) findViewById(R.id.from_service_plan);

        textCustomerName = (TextView) findViewById(R.id.activity_estimate_textview_customer_name);
        textPhoneNumber = (TextView) findViewById(R.id.activity_estimate_textview_customer_phone_number);
        textEmailAddress = (TextView) findViewById(R.id.activity_estimate_textview_customer_email_address);
        textAddress = (TextView) findViewById(R.id.activity_estimate_textview_customer_physical_address);

        textSubtotal = (TextView)findViewById(R.id.activity_estimate_textview_net_total);
        taxTitleContainer = (LinearLayout)findViewById(R.id.tax_title_container);
        taxValueContainer = (LinearLayout)findViewById(R.id.tax_value_container);

        textSavedAmount = (TextView) findViewById(R.id.activity_estimate_saved_amount_text);
        textNotes = (TextView) findViewById(R.id.activity_estimate_text_notes);
        textCreated = (TextView) findViewById(R.id.activity_estimate_textview_created_on);
        textAgreement = (TextView) findViewById(R.id.activity_estimate_textview_service_agreement);
        completeButton = (Button) findViewById(R.id.activity_estimate_button_complete);
        backButton = (ImageView) findViewById(R.id.activity_estimate_button_back);

        textDiscount = (TextView)findViewById(R.id.activity_estimate_textview_discount);
        textTotal = (TextView)findViewById(R.id.activity_estimate_textview_total);
        init();
        calculateTotal();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (noticePopup != null) {
            if (noticePopup.isShowing())
                noticePopup.dismiss();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                if (noticePopup != null) {
                    if (noticePopup.isShowing())
                        noticePopup.dismiss();
                }
                noticePopup = new DialogErrorPopup(
                        ActivityEstimateCustomerView.this, "Notice",
                        "Please give the device back to the technician.", null);
                noticePopup.show();
                noticePopup.setOnDismissListener(new OnDismissListener() {

                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        Intent returnIntent = new Intent();
                        setResult(RESULT_OK, returnIntent);
                        finish();
                    }
                });
            }
            if (resultCode == RESULT_CANCELED) {

            }
        }
    }

    private void init() {

        initItemsHeader();
        initSaveFromServicePlanLine();

        completeButton.setOnClickListener(buttonListener);
        backButton.setOnClickListener(buttonListener);
        customer = AppDataSingleton.getInstance().getCustomer();

        if (customer != null) {
            if ("org".equals(customer.getType()))
                textCustomerName.setText(customer.getOrganizationName());
            else
                textCustomerName.setText(customer.getFirstName() + " "
                        + customer.getLastName());

            if (!customer.phone.isEmpty()) {
                textPhoneNumber.setText(customer.phone.get(0));
            }

            if (!customer.email.isEmpty())
                textEmailAddress.setText(customer.email.get(0));
            else
                textEmailAddress.setText("");

            textAddress.setText(customer.getAddress1());
        }

        if (!TextUtils.isEmpty(AppDataSingleton.getInstance().getEstimate()
                .getDate())) {
            textCreated.setText(AppDataSingleton.getInstance().getEstimate()
                    .getDate());
        } else {
            textCreated.setText("");
        }

        textAgreement.setText(agreementName);

        for (int lineItemId = 0; lineItemId < AppDataSingleton.getInstance().getEstimate().getLineItems().size(); lineItemId++) {
            createItemField(AppDataSingleton.getInstance().getEstimate().getLineItems().get(lineItemId), lineItemId);
        }

    }

    private void initItemsHeader() {
        View itemView = LayoutInflater.from(this).inflate(
                R.layout.field_estimate_customer_item, null);
        estimationItemContainer.addView(itemView);

        RadioGroup rGroup = (RadioGroup) itemView
                .findViewById(R.id.estimate_customer_radio_group);
        TextView qtyText = (TextView) itemView
                .findViewById(R.id.estimate_customer_text_qty);
        TextView descriptionText = (TextView) itemView
                .findViewById(R.id.estimate_customer_text_description);
        TextView costText = (TextView) itemView
                .findViewById(R.id.estimate_customer_text_cost);
        TextView totalText = (TextView) itemView
                .findViewById(R.id.estimate_customer_text_total);
        TextView textTitleApproval = (TextView) findViewById(R.id.estimate_customer_title_approval);

        rGroup.setVisibility(View.GONE);
        textTitleApproval.setVisibility(View.VISIBLE);
        qtyText.setText("Qty");
        descriptionText.setText("Description");

        if (AppDataSingleton.getInstance().getEstimate()
                .isAgreementComparison()) {
            if (AppDataSingleton.getInstance().getEstimate()
                    .isServicePlanUsedForPricing()) {
                costText.setText("With Out Agreement");
                totalText.setText("With Agreement");
            } else {
                costText.setText("With Agreement");
                totalText.setText("With Out Agreement");
            }
        } else {
            costText.setText("Cost");
            totalText.setText("Total");
        }
    }

    private void initSaveFromServicePlanLine() {
        textSavedAmount.setText(currencyFormatTool.format(BigDecimal.ZERO));

        if (AppDataSingleton.getInstance().getEstimate()
                .isAgreementComparison()) {

            BigDecimal serviceAgreementSavedAmount = AppDataSingleton.getInstance().getEstimate().serviceAgreementSavedAmount;
            if (serviceAgreementSavedAmount != null) {
                textSavedAmount
                        .setText(currencyFormatTool.format(serviceAgreementSavedAmount));

                if (!AppDataSingleton.getInstance().getEstimate()
                        .getServicePlanName().isEmpty()) {
                    fromServicePlan.setText(" from "
                            + AppDataSingleton.getInstance().getEstimate()
                            .getServicePlanName());
                }

                if (AppDataSingleton.getInstance().getEstimate()
                        .isServicePlanUsedForPricing()) {
                    youSavedText.setText("You Saved ");
                } else {
                    youSavedText.setText("You Could Have Saved ");
                }

            } else {
                textSavedAmount.setVisibility(View.GONE);
                youSavedText.setVisibility(View.GONE);
                fromServicePlan.setVisibility(View.GONE);
            }
        }
    }

    private void createItemField(LineItem item, int lineItemId) {
        View itemView = LayoutInflater.from(this).inflate(
                R.layout.field_estimate_customer_item, null);
        estimationItemContainer.addView(itemView);

        RadioGroup rGroup = (RadioGroup) itemView
                .findViewById(R.id.estimate_customer_radio_group);
        rGroup.setTag(lineItemId);
        rGroup.setOnCheckedChangeListener(radioListener);
        rGroup.check(item.getRecommendation() == LineItem.Recommendation.ACCEPTED ? R.id.accept_radio_button :
                item.getRecommendation() == LineItem.Recommendation.DECLINED ? R.id.decline_radio_button :
                        R.id.neutral_radio_button);

        rGroupCheckedList.add((RadioButton) rGroup
                .findViewById(R.id.neutral_radio_button));

        TextView qtyText = (TextView) itemView
                .findViewById(R.id.estimate_customer_text_qty);
        TextView descriptionText = (TextView) itemView
                .findViewById(R.id.estimate_customer_text_description);
        TextView costText = (TextView) itemView
                .findViewById(R.id.estimate_customer_text_cost);
        TextView totalText = (TextView) itemView
                .findViewById(R.id.estimate_customer_text_total);

        SpannableString spanString;
        spanString = new SpannableString(item.getQuantity() + "x");
        spanString.setSpan(new StyleSpan(Typeface.NORMAL), 0,
                spanString.length(), 0);
        qtyText.setText(spanString);

        spanString = new SpannableString(item.getName());
        switch (((SkedsApplication) getApplication()).getLineItemsMode()) {
            case Constants.SHOW_ITEM_NAME:
                spanString = new SpannableString(item.getName());
                break;
            case Constants.SHOW_ITEM_DESCRIPTION:
                spanString = new SpannableString(item.getDescription().isEmpty() ? item.getName() : item.getDescription());
                break;
            case Constants.SHOW_ITEM_NAME_AND_DESCRIPTION:
                spanString = new SpannableString(item.getName() + "\n" + item.getDescription());
                break;
            default:
                break;
        }
        spanString.setSpan(new StyleSpan(Typeface.NORMAL), 0,
                spanString.length(), 0);
        descriptionText.setText(spanString);

        if (AppDataSingleton.getInstance().getEstimate()
                .isAgreementComparison()
                && item.comparisonCost.compareTo(BigDecimal.ZERO) != 0) {
            spanString = new SpannableString(item.comparisonCost + "");
        } else {
            spanString = new SpannableString(item.cost + "");
        }
        spanString.setSpan(new StyleSpan(Typeface.NORMAL), 0,
                spanString.length(), 0);
        costText.setText(currencyFormatTool.format(new BigDecimal(spanString.toString())));

        spanString = new SpannableString(item.finalCost + "");
        spanString.setSpan(new StyleSpan(Typeface.BOLD), 0,
                spanString.length(), 0);
        totalText.setText(currencyFormatTool.format(new BigDecimal(spanString.toString())));

    }

    private void updateRecommendation(int checkedButtonId, int lineItemId) {


        //itemCost = AppDataSingleton.getInstance().getEstimate().getLineItems().get(lineItemId).finalCost;
        switch (checkedButtonId) {
            case R.id.accept_radio_button:
                AppDataSingleton.getInstance().getEstimate().getLineItems().get(lineItemId).setRecommendation(LineItem.Recommendation.ACCEPTED);
                break;
            case R.id.neutral_radio_button:
                AppDataSingleton.getInstance().getEstimate().getLineItems().get(lineItemId).setRecommendation(LineItem.Recommendation.BLANK);

                break;
            case R.id.decline_radio_button:
                AppDataSingleton.getInstance().getEstimate().getLineItems().get(lineItemId).setRecommendation(LineItem.Recommendation.DECLINED);


                break;
            default:
                break;
        }


    }

    private boolean isEverythingChecked() {
        for (RadioButton btn : rGroupCheckedList) {
            if (btn.isChecked())
                return false;
        }
        return true;
    }


}
