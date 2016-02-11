package com.skeds.android.phone.business.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.skeds.android.phone.business.AsyncTasks.BaseUiReportTask;
import com.skeds.android.phone.business.Custom.AccountMenu;
import com.skeds.android.phone.business.Custom.QuickAction;
import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Country;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.LeadSource;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Region;
import com.skeds.android.phone.business.Utilities.General.CommonUtilities;
import com.skeds.android.phone.business.Utilities.General.UserUtilitiesSingleton;
import com.skeds.android.phone.business.Utilities.REST.Objects.RESTCustomer;

import java.util.ArrayList;

public class ActivityCustomerAddEdit extends BaseSkedsActivity {

    private static final int GET_LEAD_SOURCE_REQUEST = 10;
    private Activity mActivity;
    private Context mContext;

    private Country countryInfo;

    public static boolean isEditMode = false;

    /* Layout Tidly bits */
    /* Header */
    private LinearLayout headerLayout;
    private ImageView headerButtonUser;
    private TextView headerButtonSave;
    private ImageView headerButtonBack;

    /* Main layout */
    private EditText editTextCustomerFirstName;
    private EditText editTextCustomerLastName;
    private EditText editTextCustomerCompanyName;
    private EditText editTextCustomerAddress1;
    private EditText editTextCustomerAddress2;
    private EditText editTextCustomerLocationName;
    private EditText editTextCustomerAddressCity;
    private Spinner spinnerCustomerAddressProvince;
    private EditText editTextCustomerAddressZip;

    /* The data that will be submitted */
    private String customerFirstName = "";
    private String customerLastName = "";
    private String customerCompanyName = "";
    private String customerAddress1 = "";
    private String customerAddress2 = "";
    private String customerLocationName = "";
    private String customerAddressCity = "";
    private String customerAddressZip = "";
    private LeadSource leadSource;
    private TextView leadSourceText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_add_customer_view);
        initLayout();
        setupUI();
        if (isEditMode)
            setupEditMode();
    }


    private OnClickListener mButtonListener = new OnClickListener() {

        @Override
        public void onClick(View v) {

            if (!CommonUtilities.isNetworkAvailable(mContext)) {
                Toast.makeText(mContext, "Network connection unavailable.",
                        Toast.LENGTH_SHORT).show();
                return;
            }

			/* Setup hand-off variables */
            customerFirstName = editTextCustomerFirstName.getText().toString();
            customerLastName = editTextCustomerLastName.getText().toString();
            customerCompanyName = editTextCustomerCompanyName.getText()
                    .toString();
            customerAddress1 = editTextCustomerAddress1.getText().toString();
            customerAddress2 = editTextCustomerAddress2.getText().toString();
            customerLocationName = editTextCustomerLocationName.getText()
                    .toString();
            customerAddressCity = editTextCustomerAddressCity.getText()
                    .toString();


            customerAddressZip = editTextCustomerAddressZip.getText()
                    .toString();

            if (isEnteredAppropriateData()) {
                if (isEditMode) {
                    new SubmitUpdatedCustomerTask().execute();
                } else {
                    new SubmitNewCustomerTask().execute();
                }
            }

        }
    };

    private boolean isEnteredAppropriateData() {

        if (TextUtils.isEmpty(customerCompanyName))
            if (TextUtils.isEmpty(customerFirstName)
                    || TextUtils.isEmpty(customerLastName)) {
                Toast.makeText(mContext, "Enter Company Name Please",
                        Toast.LENGTH_SHORT).show();
                return false;
            }

        if (TextUtils.isEmpty(customerAddress1)) {
            Toast.makeText(mContext, "Enter Adress 1 Please",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(customerAddressCity)) {
            Toast.makeText(mContext, "Enter City Please", Toast.LENGTH_SHORT)
                    .show();
            return false;
        }


        if (customerAddressZip.isEmpty()) {
            Toast.makeText(mContext, "Enter Zip Code Please",
                    Toast.LENGTH_SHORT).show();
            return false;
        } else if (customerAddressZip.length() != countryInfo.getZipCodeLength()) {
            Toast.makeText(mContext, "Zip Code Must Be " + countryInfo.getZipCodeLength()
                            + " digits",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;

    }

    private void setupUI() {
        headerButtonSave.setOnClickListener(mButtonListener);

        TextView zipCodeLabel = (TextView) findViewById(R.id.activity_add_customer_zip_label);
        TextView provinceLabel = (TextView) findViewById(R.id.activity_add_customer_province_label);

        zipCodeLabel.setText(countryInfo.getZipCodeLabel() + ":");
        provinceLabel.setText(countryInfo.getProvinceLabel() + ":");

			/* Set the max length of the postal code to 6 */
        InputFilter[] fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(countryInfo.getZipCodeLength());

        editTextCustomerAddressZip.setFilters(fArray);

        if (!countryInfo.getZipCodePattern().isEmpty())
            editTextCustomerAddressZip.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    String newText = s.toString().replaceAll("\\D+", "");
                    StringBuilder formatted = new StringBuilder();
                    String codePattern = countryInfo.getZipCodePattern();
                    for (int i = 0; i < newText.length(); i++) {
                        if (codePattern.charAt(i) == '#')
                            formatted.append(newText.charAt(i));
                        else {
                            formatted.append(codePattern.charAt(i));
                            formatted.append(newText.charAt(i));
                        }
                    }
                    editTextCustomerAddressZip.removeTextChangedListener(this);
                    editTextCustomerAddressZip.setText(formatted);
                    editTextCustomerAddressZip.setSelection(formatted.length());
                    editTextCustomerAddressZip.addTextChangedListener(this);
                }
            });
    }

    private void initLayout() {
        mActivity = ActivityCustomerAddEdit.this;
        mContext = this;

        countryInfo = UserUtilitiesSingleton.getInstance().user.getCountryInfo();

        final QuickAction accountMenu;
        accountMenu = AccountMenu.setupMenu(mContext, mActivity);

        headerLayout = (LinearLayout) findViewById(R.id.activity_header);
        headerButtonUser = (ImageView) headerLayout.findViewById(R.id.header_button_user);
        headerButtonBack = (ImageView) headerLayout.findViewById(R.id.header_button_back);

        leadSourceText = (TextView) findViewById(R.id.lead_source_text);

        headerButtonUser.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                accountMenu.show(v);
                accountMenu.setAnimStyle(QuickAction.ANIM_GROW_FROM_CENTER);
            }
        });

        headerButtonBack.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        headerButtonSave = (TextView) headerLayout
                .findViewById(R.id.header_standard_button_right);

		/* Define remaining layout bits */
        editTextCustomerFirstName = (EditText) findViewById(R.id.activity_add_customer_edittext_first_name);
        editTextCustomerLastName = (EditText) findViewById(R.id.activity_add_customer_edittext_last_name);
        editTextCustomerCompanyName = (EditText) findViewById(R.id.activity_add_customer_edittext_company_name);

        editTextCustomerAddress1 = (EditText) findViewById(R.id.activity_add_customer_edittext_address1);
        editTextCustomerAddress2 = (EditText) findViewById(R.id.activity_add_customer_edittext_address2);
        editTextCustomerLocationName = (EditText) findViewById(R.id.activity_add_customer_edittext_location_name);
        editTextCustomerAddressCity = (EditText) findViewById(R.id.activity_add_customer_edittext_address_city);
        spinnerCustomerAddressProvince = (Spinner) findViewById(R.id.activity_add_customer_spinner_provinces);

        TextView addLeadsButton = (TextView) findViewById(R.id.source_lead_add);
        addLeadsButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, LeadSourceListActivity.class);
                intent.putExtra(LeadSourceListActivity.LEAD_SOURCE_TYPE_FILTER, "CUSTOMER");
                startActivityForResult(intent, GET_LEAD_SOURCE_REQUEST);
            }
        });

        ArrayList<String> provincesList = new ArrayList<String>();
        for (Region reg : countryInfo.getRegions()) {
            provincesList.add(reg.getLabel());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, provincesList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCustomerAddressProvince.setPrompt("Please Select " + countryInfo.getProvinceLabel());
        spinnerCustomerAddressProvince.setAdapter(adapter);

        editTextCustomerAddressZip = (EditText) findViewById(R.id.activity_add_customer_edittext_address_zip);
    }

    private void setupEditMode() {
        editTextCustomerFirstName.setText(AppDataSingleton.getInstance()
                .getCustomer().getFirstName());
        editTextCustomerLastName.setText(AppDataSingleton.getInstance()
                .getCustomer().getLastName());
        editTextCustomerCompanyName.setText(AppDataSingleton.getInstance()
                .getCustomer().getOrganizationName());

        if (AppDataSingleton.getInstance().getCustomer().locationList != null
                && !AppDataSingleton.getInstance().getCustomer().locationList
                .isEmpty()) {
            editTextCustomerAddress1.setText(AppDataSingleton.getInstance()
                    .getCustomer().locationList.get(0).getAddress1());
            editTextCustomerAddress2.setText(AppDataSingleton.getInstance()
                    .getCustomer().locationList.get(0).getAddress2());
            editTextCustomerLocationName.setText(AppDataSingleton.getInstance()
                    .getCustomer().locationList.get(0).getName());
            editTextCustomerAddressCity.setText(AppDataSingleton.getInstance()
                    .getCustomer().locationList.get(0).getCity());


            if (!TextUtils.isEmpty(AppDataSingleton.getInstance().getCustomer().locationList.get(0).getState())) {
                for (Region reg : countryInfo.getRegions()) {
                    if (AppDataSingleton.getInstance().getCustomer().locationList
                            .get(0).getState().equals(reg.getLabel())) {
                        spinnerCustomerAddressProvince.setSelection(countryInfo.getRegions().indexOf(reg));
                        break;
                    }
                }
            }

            editTextCustomerAddressZip.setText(AppDataSingleton.getInstance()
                    .getCustomer().locationList.get(0).getZip());

            for (LeadSource source : AppDataSingleton.getInstance().getLeadSourceListItem()) {
                if (source.getId().equals(AppDataSingleton.getInstance()
                        .getCustomer().getLeadSourceId())){
                    leadSource = source;
                    leadSourceText.setText(leadSource.getName());
                }



            }


        }
    }

    private class SubmitNewCustomerTask extends BaseUiReportTask<String> {

        SubmitNewCustomerTask() {
            super(ActivityCustomerAddEdit.this,
                    R.string.async_task_string_submitting_new_customer);
        }

        @Override
        protected void onSuccess() {
            // Intent i = new Intent(mContext,
            // ActivityCustomerViewFragment.class);
            // startActivity(i);
            setResult(Activity.RESULT_OK);
            finish();
        }

        @Override
        protected boolean taskBody(final String... args) throws Exception {
            String stateName = "";

            for (Region r : countryInfo.getRegions()) {
                if (r.getLabel().equals(spinnerCustomerAddressProvince.getSelectedItem())) {
                    stateName = r.getName();
                    break;
                }
            }


            RESTCustomer.add(
                    UserUtilitiesSingleton.getInstance().user.getOwnerId(),
                    customerCompanyName, customerFirstName, customerLastName,
                    customerAddress1, customerAddress2, customerAddressCity,
                    stateName, customerAddressZip, customerLocationName,leadSource);
            return true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((resultCode == LeadSourceListActivity.LEAD_SOURCE)&&(data!=null)) {
            leadSource = (LeadSource) data.getSerializableExtra(LeadSourceListActivity.key);
            if (leadSource != null) {
                AppDataSingleton.getInstance().getCustomer().setLeadSourceId(leadSource.getId());
                leadSourceText.setText(leadSource.getName());
            }
        }
    }

    private class SubmitUpdatedCustomerTask extends BaseUiReportTask<String> {

        SubmitUpdatedCustomerTask() {
            super(ActivityCustomerAddEdit.this,
                    R.string.async_task_string_updating_customer);
        }

        @Override
        protected void onSuccess() {
            // Intent i = new Intent(mContext,
            // ActivityCustomerViewFragment.class);
            // startActivity(i);
            setResult(Activity.RESULT_OK);
            Toast.makeText(mContext, "Succesfully Updated", Toast.LENGTH_SHORT)
                    .show();
            finish();
        }

        @Override
        protected boolean taskBody(final String... args) throws Exception {
            String stateName = "";

            for (Region r : countryInfo.getRegions()) {
                if (r.getLabel().equals(spinnerCustomerAddressProvince.getSelectedItem())) {
                    stateName = r.getName();
                    break;
                }
            }

            RESTCustomer.update(AppDataSingleton.getInstance().getCustomer()
                            .getId(), customerCompanyName, customerFirstName,
                    customerLastName, customerAddress1, customerAddress2,
                    customerAddressCity, stateName, customerAddressZip,
                    customerLocationName,leadSource);
            return true;
        }
    }
}