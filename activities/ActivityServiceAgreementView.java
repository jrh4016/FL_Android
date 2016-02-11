package com.skeds.android.phone.business.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.skeds.android.phone.business.AsyncTasks.BaseUiReportTask;
import com.skeds.android.phone.business.Custom.AccountMenu;
import com.skeds.android.phone.business.Custom.QuickAction;
import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Agreement;
import com.skeds.android.phone.business.Utilities.General.CommonUtilities;
import com.skeds.android.phone.business.Utilities.General.Constants;
import com.skeds.android.phone.business.Utilities.REST.Objects.RESTAgreement;

public class ActivityServiceAgreementView extends BaseSkedsActivity {

    public static int agreementId;

    private Activity mActivity;
    private Context mContext;

    private LinearLayout headerLayout;
    private ImageView headerButtonUser;
    private ImageView headerButtonBack;

    private TextView textCustomerName;
    private TextView textServicePlanName;
    private TextView textStatus;
    private TextView textDescription;
    private TextView textPaymentType;
    private TextView textAgreementNumber;
    private TextView textEffectiveDate;
    private TextView textSalesperson;
    private TextView textSystemsNumber;
    private LinearLayout layoutLocationsAndEquipment;
    private LinearLayout buttonEditAgreement;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_service_agreement_view);

        headerLayout = (LinearLayout) findViewById(R.id.activity_header);

        mActivity = ActivityServiceAgreementView.this;
        mContext = this;

        final QuickAction accountMenu;
        accountMenu = AccountMenu.setupMenu(mContext, mActivity);

        headerButtonUser = (ImageView) headerLayout
                .findViewById(R.id.header_button_user);

        headerButtonUser.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                accountMenu.show(v);
                accountMenu.setAnimStyle(QuickAction.ANIM_GROW_FROM_CENTER);
            }
        });

        headerButtonBack = (ImageView) headerLayout
                .findViewById(R.id.header_button_back);
        headerButtonBack.setOnClickListener(mGoBackListener);

        textCustomerName = (TextView) findViewById(R.id.activity_service_agreement_textview_customer_name);
        textServicePlanName = (TextView) findViewById(R.id.activity_service_agreement_textview_service_plan_name);
        textStatus = (TextView) findViewById(R.id.activity_service_agreement_textview_status);
        textDescription = (TextView) findViewById(R.id.activity_service_agreement_textview_description);
        textPaymentType = (TextView) findViewById(R.id.activity_service_agreement_textview_payment_type);
        textAgreementNumber = (TextView) findViewById(R.id.activity_service_agreement_textview_agreement_number);
        textEffectiveDate = (TextView) findViewById(R.id.activity_service_agreement_textview_effective_date);
        textSalesperson = (TextView) findViewById(R.id.activity_service_agreement_textview_salesperson);
        textSystemsNumber = (TextView) findViewById(R.id.activity_service_agreement_textview_systems_number);
        layoutLocationsAndEquipment = (LinearLayout) findViewById(R.id.activity_service_agreement_linearlayout_locations_equipment);
        buttonEditAgreement = (LinearLayout) findViewById(R.id.activity_service_agreement_button_edit_agreement);

        AppDataSingleton.getInstance().setServiceAgreement(null);
        if (!CommonUtilities.isNetworkAvailable(mActivity)) {
            Toast.makeText(mActivity, "Network connection unavailable.",
                    Toast.LENGTH_SHORT).show();
            finish();
        } else
            new GetSingleAgreementTask().execute();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AppDataSingleton.getInstance().setServiceAgreement(new Agreement());
    }


    private OnClickListener mGoBackListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            onBackPressed();
        }
    };

    private OnClickListener buttonListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.activity_service_agreement_button_edit_agreement:
                    AppDataSingleton.getInstance().setServiceAgreementAddViewMode(Constants.SERVICE_AGREEMENT_ADD_VIEW_FROM_AGREEMENT);
                    Intent i = new Intent(mActivity,
                            ActivityServiceAgreementAddEdit.class);
                    startActivity(i);
                    finish();
                    break;
                default:
                    // Nothing
                    break;
            }
        }
    };

    public void setupUI() {
        buttonEditAgreement.setOnClickListener(buttonListener);

		/* Populate Text Fields */
        String firstName, lastName, orgName, customerNameString;
        firstName = AppDataSingleton.getInstance().getCustomer().getFirstName();
        lastName = AppDataSingleton.getInstance().getCustomer().getLastName();
        orgName = AppDataSingleton.getInstance().getCustomer().getOrganizationName();

        if (TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName))
            if (TextUtils.isEmpty(orgName)) {
                customerNameString = "";
            } else {
                customerNameString = orgName;
            }
        else {

            if (TextUtils.isEmpty(orgName)) {
                customerNameString = firstName + " " + lastName;
            } else {
                customerNameString = orgName + "\n" + firstName + " "
                        + lastName;
            }
        }
        textCustomerName.setText(customerNameString);

        if ("ACTIVE".equals(AppDataSingleton.getInstance().getServiceAgreement().getStatus()))
            textStatus.setTextColor(Color.rgb(140, 198, 63)); // Green
        else
            textStatus.setTextColor(Color.rgb(168, 72, 55));// Red

        textServicePlanName.setText(AppDataSingleton.getInstance().getServiceAgreement()
                .getServicePlanName());


        String selectedStatus = AppDataSingleton.getInstance().getServiceAgreement().getStatus();
        String[] linedUpStatuses = getResources().getStringArray(R.array.agreement_statuses_lined_up);
        String[] statuses = getResources().getStringArray(R.array.agreement_statuses);
        for (int i = 0; i < linedUpStatuses.length; i++)
            if (linedUpStatuses[i].equals(selectedStatus)) {
                textStatus.setText(statuses[i]);
                break;
            }
        textDescription.setText(AppDataSingleton.getInstance().getServiceAgreement().getDescription());

        String selectedPayment = AppDataSingleton.getInstance().getServiceAgreement().getPaymentType();
        String[] linedUpPaymentTypes = getResources().getStringArray(R.array.agreement_payment_types_lined_up);
        String[] paymentTypes = getResources().getStringArray(R.array.agreement_payment_types);
        for (int i = 0; i < linedUpPaymentTypes.length; i++)
            if (linedUpPaymentTypes[i].equals(selectedPayment)) {
                textPaymentType.setText(paymentTypes[i]);
                break;
            }
        textAgreementNumber.setText(AppDataSingleton.getInstance().getServiceAgreement()
                .getContractNumber());
        if (TextUtils.isEmpty(AppDataSingleton.getInstance().getServiceAgreement().getEndDate())) {
            textEffectiveDate.setText(AppDataSingleton.getInstance().getServiceAgreement()
                    .getStartDate() + " - No Expiration");
        } else {
            textEffectiveDate.setText(AppDataSingleton.getInstance().getServiceAgreement()
                    .getStartDate()
                    + " - "
                    + AppDataSingleton.getInstance().getServiceAgreement().getEndDate());
        }
        textSalesperson.setText(AppDataSingleton.getInstance().getServiceAgreement().getSalesPerson());
        textSystemsNumber.setText(String.valueOf(AppDataSingleton.getInstance().getServiceAgreement()
                .getNumberOfSystems()));

		/* Locations AND equipment */
        layoutLocationsAndEquipment.removeAllViews();
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (!AppDataSingleton.getInstance().getServiceAgreement().locationAndEquipment.isEmpty()) {
            for (int i = 0; i < AppDataSingleton.getInstance().getServiceAgreement().locationAndEquipment
                    .size(); i++) {

                if ("location".equals(AppDataSingleton.getInstance().getServiceAgreement().locationAndEquipment.get(i)
                        .getType())) {
                    View row = inflater.inflate(
                            R.layout.row_service_agreement_location_equipment,
                            null);

                    TextView mainText = (TextView) row
                            .findViewById(R.id.row_service_agreement_location_equipment_textview_location);

                    CheckBox checkBox = (CheckBox) row.findViewById(R.id.row_service_agreement_location_equipment_checkbox_location);
                    checkBox.setClickable(false);

                    // Checkboxes don't matter

                    LinearLayout subRowLayout = (LinearLayout) row
                            .findViewById(R.id.row_service_agreement_location_equipment_linearlayout_sub_row);

                    if ("location".equals(AppDataSingleton.getInstance().getServiceAgreement().locationAndEquipment
                            .get(i).getType())) {
                        mainText.setText(AppDataSingleton.getInstance().getServiceAgreement().locationAndEquipment
                                .get(i).getName());

                        if ((i + 1) != AppDataSingleton.getInstance().getServiceAgreement().locationAndEquipment
                                .size()) {
                            if (!"location".equals(AppDataSingleton.getInstance().getServiceAgreement().locationAndEquipment
                                    .get(i + 1).getType())) {
                                while ("equipment".equals(AppDataSingleton.getInstance().getServiceAgreement().locationAndEquipment
                                        .get(i + 1).getType())) {

                                    // Use row inflater to make "subRow"
                                    View subRow = inflater
                                            .inflate(
                                                    R.layout.row_service_agreement_location_equipment,
                                                    null);
                                    CheckBox subCheckBox = (CheckBox) subRow.findViewById(R.id.row_service_agreement_location_equipment_checkbox_location);
                                    subCheckBox.setClickable(false);

                                    LinearLayout subRowLayoutSub = (LinearLayout) subRow
                                            .findViewById(R.id.row_service_agreement_location_equipment_linearlayout_sub_row);
                                    subRowLayoutSub.setVisibility(View.GONE);

                                    // Do sub row stuff
                                    TextView subText = (TextView) subRow
                                            .findViewById(R.id.row_service_agreement_location_equipment_textview_location);

                                    subText.setText(AppDataSingleton.getInstance()
                                            .getServiceAgreement().locationAndEquipment
                                            .get(i + 1).getName());

                                    subRowLayout.addView(subRow);

                                    // Now, check to make sure i + 1 exists, if
                                    // so,
                                    // ++, else break loop
                                    if ((i + 1) != AppDataSingleton.getInstance()
                                            .getServiceAgreement().locationAndEquipment
                                            .size() - 1)
                                        i++;
                                    else
                                        break;
                                }
                            }
                        }
                    }

                    layoutLocationsAndEquipment.addView(row);
                }
            }
        }

		/* Just Equipment */
        if (!AppDataSingleton.getInstance().getServiceAgreement().equipment.isEmpty()) {
            for (int i = 0; i < AppDataSingleton.getInstance().getServiceAgreement().equipment.size(); i++) {

                View row = inflater
                        .inflate(
                                R.layout.row_service_agreement_location_equipment,
                                null);

                CheckBox checkBox = (CheckBox) row.findViewById(R.id.row_service_agreement_location_equipment_checkbox_location);
                checkBox.setClickable(false);

                TextView mainText = (TextView) row
                        .findViewById(R.id.row_service_agreement_location_equipment_textview_location);

                // Checkboxes don't matter

                LinearLayout subRowLayout = (LinearLayout) row
                        .findViewById(R.id.row_service_agreement_location_equipment_linearlayout_sub_row);

                mainText.setText(AppDataSingleton.getInstance().getServiceAgreement().equipment.get(i)
                        .getName());
                subRowLayout.setVisibility(View.GONE);
                layoutLocationsAndEquipment.addView(row);
            }
        }
    }

    private class GetSingleAgreementTask extends BaseUiReportTask<String> {

        protected void onSuccess() {
            setupUI();
        }

        ;

        GetSingleAgreementTask() {
            super(ActivityServiceAgreementView.this,
                    R.string.async_task_string_loading_service_agreement);
        }

        @Override
        protected boolean taskBody(final String... args) throws Exception {
            RESTAgreement.query(agreementId);
            return true;
        }
    }
}