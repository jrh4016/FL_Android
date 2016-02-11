package com.skeds.android.phone.business.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.skeds.android.phone.business.AsyncTasks.BaseUiReportTask;
import com.skeds.android.phone.business.Custom.AccountMenu;
import com.skeds.android.phone.business.Custom.QuickAction;
import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Customer;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Location;
import com.skeds.android.phone.business.Utilities.General.CommonUtilities;
import com.skeds.android.phone.business.Utilities.General.UserUtilitiesSingleton;
import com.skeds.android.phone.business.Utilities.Logger;
import com.skeds.android.phone.business.Utilities.NonfatalException;
import com.skeds.android.phone.business.Utilities.REST.Objects.RESTAppointmentTypeList;
import com.skeds.android.phone.business.Utilities.REST.Objects.RESTEquipment;
import com.skeds.android.phone.business.Utilities.REST.Objects.RESTEquipmentManufacturerList;
import com.skeds.android.phone.business.model.Equipment;

public class ActivityEquipmentAddEdit extends BaseSkedsActivity {

    public static final String EXTRA_NEW_EQUIPMENT = "new_equipment";

    public static final String EXTRA_LAUNCHED = "launched";

    public static final int LAUNCHED_FROM_APPT = 1;

    public static final String ACTION_EDIT_EQUIPMENT_FINISHED = "com.skeds.android.EDIT_EQUIPMENT_FINISHED";

    /*
     * Layout Bits
     */
    private LinearLayout headerLayout;
    private ImageView headerButtonBack;
    private ImageView headerButtonUser;
    private TextView headerButtonSave;

    private EditText editTextEquipmentName, editTextSerialNumber,
            editTextModelNumber, customCodeField;
    private TextView textInstallationDate, textWarrantyDate,
            textNextServiceCallDate, textLaborWarrantyDate, textManufacturer;
    private TextView buttonSetInstallationDate, buttonSetWarrantyDate,
            buttonSetNextServiceCallDate, buttonSetLaborWarrantyDate;
    private EditText editTextFilter;

    // Custom info panel
    private TextView customInfoLabel1, customInfoLabel2, customInfoLabel3;
    private EditText customInfoField1, customInfoField2, customInfoField3;

    private ImageView buttonAddLocation;

    private ImageView buttonAddManufacturer;

    private LinearLayout manufacturerLayout;

    private CheckBox checkboxCreateUnscheduledAppointment;

    private LinearLayout layoutAppointmentType;
    private TextView textAppointmentType;
    private TextView buttonAppointmentTypeSelect;
    private int selectedAppointmentId;

    /*
     * "Set Date" Dialog Reused for each of the 3 types Installation, Warranty,
     * Service Call
     */
    private Dialog dialogDate;
    private DatePicker dialogDateDatePicker;
    private TextView dialogDateButtonSave, dialogDateButtonCancel;

    /*
     * Lets us know which date specifically that we're editing
     */
    private int mCurrentDateSelector;
    private static final int DATE_SELECTOR_INSTALLATION = 0;
    private static final int DATE_SELECTOR_WARRANTY = 1;
    private static final int DATE_SELECTOR_NEXT_SERVICE_CALL = 2;
    private static final int DATE_SELECTOR_LABOR_WARRANTY = 3;

	/*
     * Select Location Dialog
	 */

    /*
     * Select Appointment Type Dialog
     */
    private Dialog appointmentTypesDialog;
    private TextView appointmentTypesDialogSave, appointmentTypesDialogCancel;

    private Activity mActivity;
    private Context mContext;

    private Customer customer;
    private Location location = null;
    private Location selectedLocation = null;
    private Equipment equipment;

    private int selectedManufacturer = -1;

    private LocalBroadcastManager mLocalBroadcastManager;

    private TextView mWarrantyContractHolder;
    private TextView mWarrantyContractNumber;

    private int launchedFromFlag = 0;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // This will stop the keyboard from automatically popping up
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        setContentView(R.layout.layout_equipment_add);
        setResult(RESULT_CANCELED);

        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);

        final Intent intent = getIntent();
        customer = AppDataSingleton.getInstance().getCustomerById(intent.getIntExtra(
                CustomerEquipmentActivity.EXTRA_CUSTOMER_ID, -1));

        launchedFromFlag = intent.getIntExtra(EXTRA_LAUNCHED, 0);

        assert customer != null;

        Logger.info("UI", "ActivityEquipmentAddEdit customerid="
                + intent.getIntExtra(CustomerEquipmentActivity.EXTRA_CUSTOMER_ID, -9999)
                + " cust=" + customer);
        location = customer.getLocationById(intent.getIntExtra(
                CustomerEquipmentActivity.EXTRA_LOCATION_ID, -1));

        equipment = intent.getParcelableExtra(EXTRA_NEW_EQUIPMENT);


        // location can't be null, no need for verification
        if (location.getId() != 0) {
            selectedLocation = location;
        } else {
            if (equipment != null) {
                selectedLocation = new Location();
                selectedLocation.setId((int) equipment.getLocationId());
                selectedLocation.setAddress1(equipment.getLocationAddress());
            }
        }

        if (launchedFromFlag == LAUNCHED_FROM_APPT) {
            int locationId = AppDataSingleton.getInstance().getAppointment().getLocationId();
            selectedLocation = customer.getLocationById(locationId);
        }

        headerLayout = (LinearLayout) findViewById(R.id.activity_header);
        headerButtonSave = (TextView) headerLayout
                .findViewById(R.id.header_standard_button_right);

        mActivity = ActivityEquipmentAddEdit.this;
        mContext = this;

        final QuickAction accountMenu;
        accountMenu = AccountMenu.setupMenu(mContext, mActivity);

        headerButtonUser = (ImageView) headerLayout
                .findViewById(R.id.header_button_user);
        headerButtonBack = (ImageView) headerLayout
                .findViewById(R.id.header_button_back);

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

        editTextEquipmentName = (EditText) findViewById(R.id.edtext_equipment_name);
        editTextSerialNumber = (EditText) findViewById(R.id.edtext_serial_number);
        editTextModelNumber = (EditText) findViewById(R.id.edtext_model_number);

        textManufacturer = (TextView) findViewById(R.id.tv_selected_manufacturer);

        buttonAddLocation = (ImageView) findViewById(R.id.btn_add_location);

        buttonAddManufacturer = (ImageView) findViewById(R.id.btn_add_manufacturer);

        customCodeField = (EditText) findViewById(R.id.edtext_custom_code);

        buttonSetInstallationDate = (TextView) findViewById(R.id.btn_set_installation_date);
        buttonSetWarrantyDate = (TextView) findViewById(R.id.btn_set_warranty_date);
        buttonSetNextServiceCallDate = (TextView) findViewById(R.id.btn_next_service_call_date);
        buttonSetLaborWarrantyDate = (TextView) findViewById(R.id.btn_set_labor_warranty);

        textInstallationDate = (TextView) findViewById(R.id.tv_installation_date);
        textWarrantyDate = (TextView) findViewById(R.id.tv_warranty_date);
        textNextServiceCallDate = (TextView) findViewById(R.id.tv_next_service_call_date);
        textLaborWarrantyDate = (TextView) findViewById(R.id.tv_labor_warranty_date);

        manufacturerLayout = (LinearLayout) findViewById(R.id.linearlayout_manufacturer);
        editTextFilter = (EditText) findViewById(R.id.edtext_filter);

        checkboxCreateUnscheduledAppointment = (CheckBox) findViewById(R.id.checkbox_make_unscheduled_appointment);

        layoutAppointmentType = (LinearLayout) findViewById(R.id.linearlayout_appointment_type);

        textAppointmentType = (TextView) findViewById(R.id.tv_appointment_type);
        buttonAppointmentTypeSelect = (TextView) findViewById(R.id.btn_select_appointment_type);

        // Init custom info resources

        customInfoLabel1 = (TextView) findViewById(R.id.custinfo1);
        customInfoLabel2 = (TextView) findViewById(R.id.custinfo2);
        customInfoLabel3 = (TextView) findViewById(R.id.custinfo3);

        customInfoField1 = (EditText) findViewById(R.id.custinfo1_inputline);
        customInfoField2 = (EditText) findViewById(R.id.custinfo2_inputline);
        customInfoField3 = (EditText) findViewById(R.id.custinfo3_inputline);

        mWarrantyContractHolder = (TextView) findViewById(R.id.edtext_warranty_contract_holder);
        mWarrantyContractNumber = (TextView) findViewById(R.id.edtext_warranty_contract_number);

        setupUI();

        if (!CommonUtilities.isNetworkAvailable(mContext)) {
            Toast.makeText(mContext, "Network connection unavailable.",
                    Toast.LENGTH_SHORT).show();
            finish();
        } else {
            new GetManufacturersTask().execute();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {

            switch (requestCode) {
                case 1:
                    int locNumber = data.getIntExtra("location_number", -1);
                    selectedLocation = AppDataSingleton.getInstance().getCustomer().locationList.get(locNumber);

                    TextView locationText = (TextView) findViewById(R.id.tv_location);
                    locationText.setText(selectedLocation.getAddress1() + " "
                            + selectedLocation.getAddress2() + "\n"
                            + selectedLocation.getCity() + " "
                            + selectedLocation.getState() + " "
                            + selectedLocation.getZip());
                    break;
                case 2:
                    selectedManufacturer = data.getIntExtra("selected_manufacturer", -1);
                    if (selectedManufacturer != -1) {
                        textManufacturer.setText(AppDataSingleton.getInstance()
                                .getEquipmentManufacturerList().get(selectedManufacturer).getName());
                    }
                    break;
                default:
                    break;
            }

        }
    }


    private OnClickListener buttonListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {

			/* Save Button */
                case R.id.header_standard_button_right:
                    if (!CommonUtilities.isNetworkAvailable(mContext)) {
                        Toast.makeText(mContext, "Network connection unavailable.",
                                Toast.LENGTH_SHORT).show();
                        break;
                    }

                    if (selectedLocation == null) {
                        Toast.makeText(mContext, "Select the Location",
                                Toast.LENGTH_SHORT).show();
                        break;
                    } else if (selectedLocation.getId() == 0) {
                        Toast.makeText(mContext, "Select the Location Please",
                                Toast.LENGTH_SHORT).show();
                        break;
                    } else if (TextUtils.isEmpty(editTextEquipmentName.getText())) {
                        Toast.makeText(mContext, "Enter the Name",
                                Toast.LENGTH_SHORT).show();
                        break;
                    }
                    new SubmitEquipmentTask(mActivity).execute();

                    break;

				/* "Set Date" for Installation */
                case R.id.btn_set_installation_date:
                    mCurrentDateSelector = DATE_SELECTOR_INSTALLATION;
                    showDateTimeDialog();
                    break;

				/* "Set Date" for Warranty Expiration */
                case R.id.btn_set_warranty_date:
                    mCurrentDateSelector = DATE_SELECTOR_WARRANTY;
                    showDateTimeDialog();
                    break;

				/* "Set Date" for Next Service Call */
                case R.id.btn_next_service_call_date:
                    mCurrentDateSelector = DATE_SELECTOR_NEXT_SERVICE_CALL;
                    showDateTimeDialog();
                    break;

                case R.id.btn_set_labor_warranty:

                    mCurrentDateSelector = DATE_SELECTOR_LABOR_WARRANTY;
                    showDateTimeDialog();
                    break;

				/* "Add Location" */
                case R.id.btn_add_location:
                    setupLocationsList();
                    break;

				/* "Select One" button for appointment types */
                case R.id.btn_select_appointment_type:
                    if (!CommonUtilities.isNetworkAvailable(mContext)) {
                        Toast.makeText(mContext, "Network connection unavailable.",
                                Toast.LENGTH_SHORT).show();
                        break;
                    }
                    new GetAppointmentsTask().execute();
                    break;
                case R.id.btn_add_manufacturer:
                    startActivityForResult(new Intent(mActivity, ActivityManufacturerList.class), 2);
                    break;
                default:
                    // Nothing
                    break;
            }
        }
    };

    private OnCheckedChangeListener checkboxListener = new OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {

            if (isChecked) {
                layoutAppointmentType.setVisibility(View.VISIBLE);
                textAppointmentType.setText(mContext.getResources().getString(
                        R.string.textview_string_select_appointment_type));
                selectedAppointmentId = -1;
            } else {
                layoutAppointmentType.setVisibility(View.GONE);
            }
        }

    };

    private OnClickListener mDialogSaveButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {

            String dateToUse = "";

            dateToUse = (dialogDateDatePicker.getMonth() + 1) + "/"
                    + dialogDateDatePicker.getDayOfMonth() + "/"
                    + dialogDateDatePicker.getYear();

            switch (mCurrentDateSelector) {
                case DATE_SELECTOR_INSTALLATION:
                    textInstallationDate.setText(dateToUse);
                    break;
                case DATE_SELECTOR_WARRANTY:
                    textWarrantyDate.setText(dateToUse);
                    break;
                case DATE_SELECTOR_NEXT_SERVICE_CALL:
                    textNextServiceCallDate.setText(dateToUse);
                    break;
                case DATE_SELECTOR_LABOR_WARRANTY:
                    textLaborWarrantyDate.setText(dateToUse);
                    break;
                default:
                    // Nothing
                    break;
            }

            if (dialogDate.isShowing())
                dialogDate.dismiss();
        }
    };

    private OnClickListener mDialogCancelButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (dialogDate.isShowing())
                dialogDate.dismiss();
        }
    };

    private OnItemClickListener appointmentListListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View v, int position,
                                long id) {

            TextView selectedText = (TextView) appointmentTypesDialog
                    .findViewById(R.id.dialog_list_textview_selected);
            selectedText
                    .setText(AppDataSingleton.getInstance().getNewAppointment().appointmentTypeList
                            .get((int) id).getName());
            selectedText.setTag(AppDataSingleton.getInstance().getNewAppointment().appointmentTypeList
                    .get((int) id).getId());

            selectedAppointmentId = AppDataSingleton.getInstance().getNewAppointment().appointmentTypeList
                    .get((int) id).getId();
        }
    };

    private OnClickListener appointmentTypesSaveListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            TextView selectedText = (TextView) appointmentTypesDialog
                    .findViewById(R.id.dialog_list_textview_selected);

            textAppointmentType.setText(selectedText.getText().toString());

            if (appointmentTypesDialog.isShowing())
                appointmentTypesDialog.dismiss();
        }
    };

    private OnClickListener appointmentTypesCancelListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (appointmentTypesDialog.isShowing())
                appointmentTypesDialog.dismiss();
        }
    };

    private void initializeCustomField(TextView label, EditText field, String text) {
        if (!text.isEmpty()) {
            label.setText(text);
        } else {
            label.setVisibility(View.GONE);
            field.setVisibility(View.GONE);
        }
    }

    private void setupUI() {
        headerButtonSave.setOnClickListener(buttonListener);
        buttonSetInstallationDate.setOnClickListener(buttonListener);
        buttonSetWarrantyDate.setOnClickListener(buttonListener);
        buttonSetLaborWarrantyDate.setOnClickListener(buttonListener);
        buttonSetNextServiceCallDate.setOnClickListener(buttonListener);
        buttonAppointmentTypeSelect.setOnClickListener(buttonListener);
        buttonAddManufacturer.setOnClickListener(buttonListener);
        checkboxCreateUnscheduledAppointment.setOnCheckedChangeListener(checkboxListener);

        // Setup custom fields and headers
        initializeCustomField(customInfoLabel1, customInfoField1, AppDataSingleton.getInstance().getEquipmentCustomFieldName1());
        initializeCustomField(customInfoLabel2, customInfoField2, AppDataSingleton.getInstance().getEquipmentCustomFieldName2());
        initializeCustomField(customInfoLabel3, customInfoField3, AppDataSingleton.getInstance().getEquipmentCustomFieldName3());

        buttonAddLocation.setOnClickListener(buttonListener);

        if (selectedLocation != null) {
            final TextView locationText = (TextView) findViewById(R.id.tv_location);
            if (selectedLocation.getId() != 0) {
                locationText.setText(selectedLocation.getAddress1() + " "
                        + selectedLocation.getAddress2() + "\n"
                        + selectedLocation.getCity() + ", "
                        + selectedLocation.getState() + " "
                        + selectedLocation.getZip());
            } else {
                locationText.setText("None");
            }
        }

        if (equipment != null)
            setupItemToEdit();
    }

    private void setupLocationsList() {
        Intent i = new Intent(this, ActivityCustomerLocationListView.class);
        i.setAction(ActivityCustomerLocationListView.ACTION_PICK_LOCATION);
        startActivityForResult(i, 1);
    }

    private void setupItemToEdit() {
        editTextEquipmentName.setText(equipment.getName());
        editTextSerialNumber.setText(equipment.getSerialNumber());
        editTextModelNumber.setText(equipment.getModelNumber());

        customInfoField1.setText(equipment.getCustomInfo1());
        customInfoField2.setText(equipment.getCustomInfo2());
        customInfoField3.setText(equipment.getCustomInfo3());

        customCodeField.setText(equipment.getCustomCode());

        editTextModelNumber.setText(equipment.getModelNumber());

        textManufacturer.setText(equipment.getManufacturerName());

        editTextFilter.setText(equipment.getFilter());

        textInstallationDate.setText(equipment.getInstallationDate());
        textWarrantyDate.setText(equipment.getWarrantyExpirationDate());
        textLaborWarrantyDate.setText(equipment.getLaborWarrantyExpirationDate());

        mWarrantyContractHolder.setText(equipment.getWarrantyContractHolder());
        mWarrantyContractNumber.setText(equipment.getWarrantyContractNumber());
        textNextServiceCallDate.setText(equipment.getNextServiceCallDate());
    }

    private void showDateTimeDialog() {
        // Create the dialog
        dialogDate = new Dialog(mContext);
        dialogDate.setContentView(R.layout.dialog_layout_date_picker);
        dialogDate.setTitle(mContext.getResources().getString(
                R.string.dialog_header_string_select_date));

        dialogDateButtonSave = (TextView) dialogDate
                .findViewById(R.id.dialog_date_picker_button_save);
        dialogDateButtonCancel = (TextView) dialogDate
                .findViewById(R.id.dialog_date_picker_button_cancel);

        dialogDateDatePicker = (DatePicker) dialogDate
                .findViewById(R.id.dialog_date_picker_datepicker);

        dialogDateButtonSave.setOnClickListener(mDialogSaveButtonListener);
        dialogDateButtonCancel.setOnClickListener(mDialogCancelButtonListener);

        dialogDate.show();

    }

    private void save() throws NonfatalException {

        // Sending a -1 will tell it that we don't actually have one selected
        int appointmentId = -1;

        if (checkboxCreateUnscheduledAppointment.isChecked()) {
            appointmentId = selectedAppointmentId;
        }

        String locationId = null;
        if (selectedLocation != null) {
            locationId = String.valueOf(selectedLocation.getId());
        }

        long manufacturerId = -1;
        String manufacturerName = null;

        if (selectedManufacturer != -1) {
            manufacturerId = AppDataSingleton.getInstance()
                    .getEquipmentManufacturerList().get(selectedManufacturer).getId();
            manufacturerName = AppDataSingleton.getInstance()
                    .getEquipmentManufacturerList().get(selectedManufacturer).getName();
        } else if (equipment != null) {
            manufacturerId = equipment.getManufacturerId();
            manufacturerName = equipment.getManufacturerName();
        }

        equipment = RESTEquipment.update(editTextEquipmentName.getText().toString(), editTextSerialNumber.getText().toString(),
                editTextModelNumber.getText().toString(), textInstallationDate.getText().toString(), textWarrantyDate.getText().toString(),
                textNextServiceCallDate.getText().toString(), textLaborWarrantyDate.getText().toString(), appointmentId,
                locationId, manufacturerId, manufacturerName, editTextFilter.getText().toString(), mWarrantyContractHolder.getText().toString(),
                mWarrantyContractNumber.getText().toString(), customInfoField1.getText().toString(), customInfoField2.getText().toString(),
                customInfoField3.getText().toString(), customCodeField.getText().toString(), selectedLocation.getAddress1(), equipment);
    }

    private void setupAppointmentsList() {

        appointmentTypesDialog = new Dialog(mContext);
        appointmentTypesDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        appointmentTypesDialog.setContentView(R.layout.dialog_layout_list);

        ListView mListView = (ListView) appointmentTypesDialog
                .findViewById(R.id.dialog_list_listview);

        mListView.setAdapter(null);

        Drawable drawableDivider = new ColorDrawable(
                android.R.color.transparent);
        mListView.setBackgroundColor(Color.rgb(230, 230, 230));
        // mListView.setCacheColorHint(Color.rgb(62, 81, 101));

        mListView.setVerticalScrollBarEnabled(false);
        mListView.setDivider(drawableDivider);
        mListView.setDividerHeight(6); // Pixel spacing in-between items

        mListView.setPadding(12, 0, 12, 0);

        String[] sizeArray = new String[AppDataSingleton.getInstance().getNewAppointment().appointmentTypeList
                .size()];

        ArrayAdapter<String> adapter = new CustomAppointmentAdapter(mContext,
                R.layout.row_equipment_item, sizeArray);
        mListView.setAdapter(adapter);

        mListView.setTextFilterEnabled(true);
        mListView.setOnItemClickListener(appointmentListListener);

        appointmentTypesDialogSave = (TextView) appointmentTypesDialog
                .findViewById(R.id.dialog_list_button_save);
        appointmentTypesDialogCancel = (TextView) appointmentTypesDialog
                .findViewById(R.id.dialog_list_button_cancel);

        appointmentTypesDialogSave
                .setOnClickListener(appointmentTypesSaveListener);
        appointmentTypesDialogCancel
                .setOnClickListener(appointmentTypesCancelListener);

        appointmentTypesDialog.show();
    }

    private class CustomLocationAdapter extends ArrayAdapter<String> {

        public CustomLocationAdapter(Context context, int textViewResourceId,
                                     String[] sizeArray) {
            super(context, textViewResourceId, sizeArray);
            notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.row_equipment_item, parent,
                    false);

            if (position < AppDataSingleton.getInstance().getCustomer().locationList.size()) {

                TextView label = (TextView) row
                        .findViewById(R.id.equipmentListItem);
                label.setText(AppDataSingleton.getInstance().getCustomer().locationList.get(position)
                        .getAddress1());
                label.setTag(AppDataSingleton.getInstance().getCustomer().locationList.get(position)
                        .getId());

            } else {

                row.setVisibility(View.GONE);
            }

            return row;
        }

    }

    private class SubmitEquipmentTask extends BaseUiReportTask<String> {

        SubmitEquipmentTask(Activity activity) {
            super(ActivityEquipmentAddEdit.this, equipment != null
                    ? R.string.async_task_string_updating_equipment
                    : R.string.async_task_string_submitting_new_equipment);
        }

        @Override
        protected void onSuccess() {
            final Intent intent = new Intent(ACTION_EDIT_EQUIPMENT_FINISHED);
            intent.putExtra(EXTRA_NEW_EQUIPMENT, equipment);
            mLocalBroadcastManager.sendBroadcast(intent);
            finish();
        }

        @Override
        protected boolean taskBody(final String... args) throws Exception {
            save();
            return true;
        }
    }

    private class GetAppointmentsTask extends BaseUiReportTask<String> {

        public GetAppointmentsTask() {
            super(ActivityEquipmentAddEdit.this,
                    R.string.async_task_string_loading_appointment_types);
        }

        @Override
        protected void onSuccess() {
            setupAppointmentsList();
        }

        @Override
        protected boolean taskBody(final String... args) throws Exception {
            RESTAppointmentTypeList.query();
            return true;
        }
    }

    public class CustomAppointmentAdapter extends ArrayAdapter<String> {

        public CustomAppointmentAdapter(Context context,
                                        int textViewResourceId, String[] objects) {
            super(context, textViewResourceId, objects);
            notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.row_equipment_item, parent,
                    false);

            // Log.d(DEBUG_TAG, "mDates " + mDates.length);

            if (position <= AppDataSingleton.getInstance().getNewAppointment().appointmentTypeList
                    .size()) {

                TextView label = (TextView) row
                        .findViewById(R.id.equipmentListItem);
                label.setText(AppDataSingleton.getInstance().getNewAppointment().appointmentTypeList
                        .get(position).getName());
                label.setTag(AppDataSingleton.getInstance().getNewAppointment().appointmentTypeList
                        .get(position).getId());

            } else {

                row.setVisibility(View.GONE);
                // return null;
            }

            return row;
        }
    }

    private class GetManufacturersTask extends BaseUiReportTask<String> {

        public GetManufacturersTask() {
            super(ActivityEquipmentAddEdit.this,
                    R.string.async_task_string_loading_manufacturer_information);
        }

        @Override
        protected void onSuccess() {
            setupUI();
        }

        @Override
        protected boolean taskBody(final String... args) throws Exception {
            RESTEquipmentManufacturerList
                    .query(UserUtilitiesSingleton.getInstance().user.getOwnerId());
            return true;
        }
    }
}