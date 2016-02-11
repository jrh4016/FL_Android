package com.skeds.android.phone.business.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.skeds.android.phone.business.AsyncTasks.BaseUiReportTask;
import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Country;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Location;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Region;
import com.skeds.android.phone.business.Utilities.General.CommonUtilities;
import com.skeds.android.phone.business.Utilities.General.UserUtilitiesSingleton;
import com.skeds.android.phone.business.Utilities.IntentExtras;
import com.skeds.android.phone.business.Utilities.REST.Objects.RESTLocation;

import java.util.ArrayList;
import java.util.List;

public class ActivityLocationAddEdit extends BaseSkedsActivity
        implements
        View.OnClickListener {

    private Location location;
    private int locationPos;
    private Spinner stateSpinner;
    private Context mContext;
    private Activity activity;

    private Spinner phone1typeSpinner;
    private Spinner phone2typeSpinner;

    private ArrayList<String> provincesList;

    private Country countryInfo;
    private EditText zipEditText;

    public static final String LOCATION_POS = "locationPos";

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        activity = this;
        setContentView(R.layout.layout_location_edit);
        countryInfo = UserUtilitiesSingleton.getInstance().user.getCountryInfo();

        phone1typeSpinner = (Spinner) findViewById(R.id.spinner_phone1_type);
        phone2typeSpinner = (Spinner) findViewById(R.id.spinner_phone2_type);

        setupUI();
    }

    private final void setupUI() {
        TextView text = (TextView) findViewById(R.id.title_text);

        Intent intent = getIntent();
        locationPos = intent.getIntExtra(LOCATION_POS, -1);
        if (locationPos < 0) {
            location = null;
            text.setText("New location");
        } else {
            location = AppDataSingleton.getInstance().getCustomer().locationList.get(locationPos);
            text.setText("Edit location");
        }

        setPhoneTypes();

        findViewById(R.id.btn_back).setOnClickListener(this);
        findViewById(R.id.btn_view_equipment).setOnClickListener(this);

        setProvinceZipLabels();

        text = (TextView) findViewById(R.id.btn_submit);
        text.setOnClickListener(this);
        if (location == null)
            text.setText("Create");

        zipEditText = (EditText) findViewById(R.id.input_zipcode);
        InputFilter[] fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(countryInfo.getZipCodeLength());

        if (!countryInfo.getZipCodeLabel().isEmpty())
            ((TextView) findViewById(R.id.location_zip_label)).setText(countryInfo.getZipCodeLabel());
        if (location != null)
            if (location.getCountryId() != 0) {
                List<Country> allCountries = AppDataSingleton.getInstance().getAllCountries();
                for (Country country : allCountries) {
                    if (country.getId() == location.getCountryId()) {
                        fArray[0] = new InputFilter.LengthFilter(country.getZipCodeLength());
                        break;
                    }
                }
            }

        zipEditText.setFilters(fArray);

        if (!countryInfo.getZipCodePattern().isEmpty())
            zipEditText.addTextChangedListener(new TextWatcher() {

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
                    zipEditText.removeTextChangedListener(this);
                    zipEditText.setText(formatted);
                    zipEditText.setSelection(formatted.length());
                    zipEditText.addTextChangedListener(this);
                }
            });
        setProvincesList();
        setSpiner();

        if (location != null) {
            int i;
            String state = location.getState();
            for (i = 0; i < provincesList.size(); i++)
                if (provincesList.get(i).equals(state)) {
                    stateSpinner.setSelection(i);
                    break;
                }
            settext(R.id.input_address1, location.getAddress1());
            settext(R.id.input_address2, location.getAddress2());
            settext(R.id.input_zipcode, location.getZip());
            settext(R.id.input_city, location.getCity());
            settext(R.id.input_location_name, location.getName());
            settext(R.id.input_phone1_contact, location.getPhone1Description());
            settext(R.id.input_phone2_contact, location.getPhone2Description());
            settext(R.id.input_email, location.getEmail());


            if (location.getPhone1() != null)
                settext(R.id.input_phone1, location.getPhone1().replaceAll("\\D+", ""));
            if (location.getPhone2() != null)
                settext(R.id.input_phone2, location.getPhone2().replaceAll("\\D+", ""));
            settext(R.id.input_description, location.getDescription());
            settext(R.id.input_code, location.getCode());
        }

    }

    private void setSpiner() {
        stateSpinner = (Spinner) findViewById(R.id.spinner_province);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, provincesList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateSpinner.setPrompt(getString(R.string.please_select, countryInfo.getProvinceLabel()));

        if (location != null)
            if (location.getCountryId() != 0) {
                List<Country> allCountries = AppDataSingleton.getInstance().getAllCountries();
                for (Country country : allCountries) {
                    if (country.getId() == location.getCountryId()) {
                        stateSpinner.setPrompt(getString(R.string.please_select, country.getProvinceLabel()));
                        break;
                    }
                }
            }

        stateSpinner.setAdapter(adapter);
    }

    private void setProvincesList() {
        provincesList = new ArrayList<String>();
        for (Region reg : countryInfo.getRegions()) {
            provincesList.add(reg.getLabel());
        }

        if (location != null)
            if (location.getCountryId() != 0) {
                List<Country> allCountries = AppDataSingleton.getInstance().getAllCountries();
                for (Country country : allCountries) {
                    if (country.getId() == location.getCountryId()) {
                        provincesList.clear();
                        for (Region reg : country.getRegions()) {
                            provincesList.add(reg.getLabel());
                        }
                        break;
                    }
                }
            }
    }

    private void setProvinceZipLabels() {
        if (!countryInfo.getProvinceLabel().isEmpty())
            ((TextView) findViewById(R.id.location_province_label)).setText(countryInfo.getProvinceLabel());

        if (!countryInfo.getZipCodeLabel().isEmpty())
            ((TextView) findViewById(R.id.location_zip_label)).setText(countryInfo.getZipCodeLabel());
        if (location != null)
            if (location.getCountryId() != 0) {
                List<Country> allCountries = AppDataSingleton.getInstance().getAllCountries();
                for (Country country : allCountries) {
                    if (country.getId() == location.getCountryId()) {
                        ((TextView) findViewById(R.id.location_province_label)).setText(country.getProvinceLabel());
                        ((TextView) findViewById(R.id.location_zip_label)).setText(country.getZipCodeLabel());
                        break;
                    }
                }
            }
    }

    private void setPhoneTypes() {
        if (location != null) {
            String type1 = location.getPhone1Type();
            String type2 = location.getPhone2Type();

            String[] allTypes = getResources().getStringArray(R.array.phone_types);
            for (int i = 0; i < allTypes.length; i++) {

                if (type1.equals(allTypes[i].toUpperCase()))
                    phone1typeSpinner.setSelection(i);

                if (type2.equals(allTypes[i].toUpperCase()))
                    phone2typeSpinner.setSelection(i);
            }
        } else {
            phone1typeSpinner.setSelection(4);
            phone2typeSpinner.setSelection(4);
        }
    }


    private void finishLocationEdit(boolean changesSubmited) {
        setResult(changesSubmited ? RESULT_OK : RESULT_CANCELED);
        finish();
    }

    private void settext(int id, String msg) {
        if (msg != null && msg.length() > 0) {
            TextView tv = (TextView) findViewById(id);
            tv.setText(msg);
        }
    }

    private String gettext(int id, String msg) throws NumberFormatException {
        TextView tv = (TextView) findViewById(id);
        if (tv.length() < 1) {
            if (msg != null)
                throw new NumberFormatException(msg);
            return null;
        }
        return tv.getText().toString();
    }

    // commit modifications
    private void exitCommit() {

        String phone1 = gettext(R.id.input_phone1, null);
        String phone2 = gettext(R.id.input_phone2, null);

        if (phone1 != null)
            if (!phone1.isEmpty())
                if (phone1.length() < 10) {
                    Toast.makeText(mContext, "Phone Number Should Contain 10 Digits", Toast.LENGTH_SHORT).show();
                    return;
                }

        if (phone2 != null)
            if (!phone2.isEmpty())
                if (phone2.length() < 10) {
                    Toast.makeText(mContext, "Phone Number Should Contain 10 Digits", Toast.LENGTH_SHORT).show();
                    return;
                }

        if (zipEditText.getText().toString().isEmpty()) {
            Toast.makeText(mContext, "Enter Zip Code Please",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Location newloc = new Location();
        if (location != null)
            newloc.setLocation(location);
        try {

            newloc.setAddress1(gettext(R.id.input_address1, "Address1"));
            newloc.setName(gettext(R.id.input_location_name, null));
            newloc.setAddress2(gettext(R.id.input_address2, null));
            newloc.setZip(gettext(R.id.input_zipcode, "ZipCode"));
            newloc.setCity(gettext(R.id.input_city, "City"));
            newloc.setPhone1(gettext(R.id.input_phone1, null));
            newloc.setPhone1Type(phone1typeSpinner.getSelectedItem().toString().toUpperCase());
            newloc.setPhone1Description(gettext(R.id.input_phone1_contact, null));
            newloc.setPhone2(gettext(R.id.input_phone2, null));
            newloc.setPhone2Type(phone2typeSpinner.getSelectedItem().toString().toUpperCase());
            newloc.setPhone2Description(gettext(R.id.input_phone2_contact, null));
            newloc.setDescription(gettext(R.id.input_description, null));
            newloc.setEmail(gettext(R.id.input_email, null));
            newloc.setCode(gettext(R.id.input_code, null));
            newloc.setState(provincesList.get(stateSpinner.getSelectedItemPosition()));
            if (!CommonUtilities.isNetworkAvailable(mContext)) {
                Toast.makeText(mContext, "Network connection unavailable.",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            new SaveChangesTask(newloc).execute();
        } catch (NumberFormatException e) {
            Toast.makeText(this,
                    "Field " + e.getMessage() + " has invalid value",
                    Toast.LENGTH_SHORT).show();
            return;
        } catch (IndexOutOfBoundsException e) {
            Toast.makeText(this, "State is not selected or invalid",
                    Toast.LENGTH_LONG).show();
            return;
        }
    }

    @Override
    public void onBackPressed() {
        finishLocationEdit(false);
    }

    @Override
    protected void onDestroy() {
        // desctructor actions
        location = null;
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finishLocationEdit(false);
                return;
            // Save
            case R.id.btn_submit:
                exitCommit();
                return;
            case R.id.btn_view_equipment: {
                String phone1 = gettext(R.id.input_phone1, null);
                String phone2 = gettext(R.id.input_phone2, null);

                if (phone1 != null)
                    if (!phone1.isEmpty())
                        if (phone1.length() < 10) {
                            Toast.makeText(mContext, "Phone Number Should Contain 10 Digits", Toast.LENGTH_SHORT).show();
                            return;
                        }

                if (phone2 != null)
                    if (!phone2.isEmpty())
                        if (phone2.length() < 10) {
                            Toast.makeText(mContext, "Phone Number Should Contain 10 Digits", Toast.LENGTH_SHORT).show();
                            return;
                        }

                Location newloc = new Location();
                if (location != null)
                    newloc.setLocation(location);
                try {
                    newloc.setAddress1(gettext(R.id.input_address1, "Address1"));
                    newloc.setAddress2(gettext(R.id.input_address2, null));
                    newloc.setName(gettext(R.id.input_location_name, null));
                    newloc.setZip(gettext(R.id.input_zipcode, "ZipCode"));
                    newloc.setCity(gettext(R.id.input_city, "City"));
                    newloc.setPhone1(gettext(R.id.input_phone1, null));
                    newloc.setPhone1Type(phone1typeSpinner.getSelectedItem().toString().toUpperCase());
                    newloc.setPhone1Description(gettext(R.id.input_phone1_contact, null));
                    newloc.setPhone2(gettext(R.id.input_phone2, null));
                    newloc.setPhone2Type(phone2typeSpinner.getSelectedItem().toString().toUpperCase());
                    newloc.setPhone2Description(gettext(R.id.input_phone2_contact, null));
                    newloc.setDescription(gettext(R.id.input_description, null));
                    newloc.setEmail(gettext(R.id.input_email, null));
                    newloc.setCode(gettext(R.id.input_code, null));
                    newloc.setState(provincesList.get(stateSpinner.getSelectedItemPosition()));
                    if (!CommonUtilities.isNetworkAvailable(mContext)) {
                        Toast.makeText(mContext, "Network connection unavailable.",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(this,
                            "Field " + e.getMessage() + " has invalid value",
                            Toast.LENGTH_SHORT).show();
                    return;
                } catch (IndexOutOfBoundsException e) {
                    Toast.makeText(this, "State is not selected or invalid",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                new SaveChangesWithoutExitTask(newloc).execute();
                return;
            }
            default:
                // Nothing
                break;
        }
    }


    private class SaveChangesTask extends BaseUiReportTask<String> {

        final Location newloc;

        SaveChangesTask(Location newloc) {
            super(ActivityLocationAddEdit.this, (location == null
                    ? "Adding new location\n"
                    : "Applying changes for\n")
                    + newloc.getState()
                    + " "
                    + newloc.getZip() + "...");
            this.newloc = newloc;
        }

        @Override
        protected void onSuccess() {
            if (location != null) {
                location.setLocation(newloc); // apply changes
            } else {
                AppDataSingleton.getInstance().getCustomer().locationList.add(newloc);
            }
            finishLocationEdit(true);
        }

        @Override
        protected boolean taskBody(String... params) throws Exception {
            if (location == null)
                RESTLocation.add(AppDataSingleton.getInstance().getCustomer().getId(), newloc);
            else
                RESTLocation.update(newloc);
            return true;
        }
    }

    private class SaveChangesWithoutExitTask extends SaveChangesTask {

        SaveChangesWithoutExitTask(Location newloc) {
            super(newloc);
        }

        @Override
        protected void onSuccess() {
            terminate();
            int locationId;
            if (location == null)
                AppDataSingleton.getInstance().getCustomer().locationList.add(newloc);

            location = newloc;
            locationId = location.getId();

            Intent i = new Intent(activity,
                    CustomerEquipmentActivity.class);
            i.putExtra(CustomerEquipmentActivity.EXTRA_CUSTOMER_ID,
                    AppDataSingleton.getInstance().getCustomer().getId())
                    .putExtra(IntentExtras.LOCATION_ID, locationId)
                    .putExtra(CustomerEquipmentActivity.EXTRA_CAN_ADD_NEW_EQUIPMENT, true)
                    .putExtra(IntentExtras.EDIT_ENABLE, true);
            startActivity(i);
        }


    }
}
