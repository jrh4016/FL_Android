package com.skeds.android.phone.business.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.skeds.android.phone.business.AsyncTasks.BaseUiReportTask;
import com.skeds.android.phone.business.Custom.AccountMenu;
import com.skeds.android.phone.business.Custom.QuickAction;
import com.skeds.android.phone.business.Dialogs.DialogAgreementPaymentTypes;
import com.skeds.android.phone.business.Dialogs.DialogAgreementStatus;
import com.skeds.android.phone.business.Dialogs.DialogAgreementTypes;
import com.skeds.android.phone.business.Dialogs.DialogServiceAgreementSelectSystemsNumber;
import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Agreement;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Generic;
import com.skeds.android.phone.business.Utilities.General.CommonUtilities;
import com.skeds.android.phone.business.Utilities.General.Constants;
import com.skeds.android.phone.business.Utilities.General.UserUtilitiesSingleton;
import com.skeds.android.phone.business.Utilities.REST.Objects.RESTAgreement;
import com.skeds.android.phone.business.Utilities.REST.Objects.RESTLocationAndEquipmentList;
import com.skeds.android.phone.business.Utilities.REST.Objects.RESTServicePlanList;

import java.util.ArrayList;
import java.util.List;

public class ActivityServiceAgreementAddEdit extends BaseSkedsActivity {

    public static boolean editMode;

    private List<Integer> locationIds = new ArrayList<Integer>();
    private List<Integer> equipmentIds = new ArrayList<Integer>();

    private LinearLayout headerLayout;
    private ImageView headerButtonUser;
    private ImageView headerButtonBack;

    private TextView buttonSave;
    private LinearLayout linearlayoutServiceAgreement;
    private TextView textServiceAgreement;
    private LinearLayout linearlayoutStatus;
    private TextView textStatus;
    private LinearLayout linearlayoutPaymentType;
    private TextView textPaymentType;
    private LinearLayout linearlayoutStartDate;
    private TextView textStartDate;
    private LinearLayout linearlayoutEndDate;
    private TextView textEndDate;
    private EditText edittextDescription;
    private EditText edittextSalesPerson;
    private EditText edittextAgreementNumber;
    // private ImageView buttonAddLocation;
    private LinearLayout linearlayoutSystemsNumber;
    private TextView textSystemsNumber;
    private LinearLayout linearlayoutLocationList;

    private Dialog dialogDate;
    private DatePicker dialogDateDatePicker;
    private TextView dialogDateButtonSave, dialogDateButtonCancel;

    private static int dialogType;
    private final int DIALOG_START_DATE = 1;
    private final int DIALOG_END_DATE = 2;

    private Activity mActivity;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        setContentView(R.layout.layout_service_agreement_add);

        mActivity = this;
        mContext = this;

        initHeader();
        initResources();
        resetFields();
        new GetLocationAndEquipmentTask().execute();
    }

    private void initResources() {
        buttonSave = (TextView) findViewById(R.id.activity_add_service_agreement_button_save);
        linearlayoutServiceAgreement = (LinearLayout) findViewById(R.id.activity_add_service_agreement_linearlayout_service_agreement);
        textServiceAgreement = (TextView) findViewById(R.id.activity_add_service_agreement_textview_service_agreement);
        linearlayoutStatus = (LinearLayout) findViewById(R.id.activity_add_service_agreement_linearlayout_status);
        textStatus = (TextView) findViewById(R.id.activity_add_service_agreement_textview_status);
        linearlayoutPaymentType = (LinearLayout) findViewById(R.id.activity_add_service_agreement_linearlayout_payment_type);
        textPaymentType = (TextView) findViewById(R.id.activity_add_service_agreement_textview_payment_type);
        linearlayoutStartDate = (LinearLayout) findViewById(R.id.activity_add_service_agreement_linearlayout_start_date);
        textStartDate = (TextView) findViewById(R.id.activity_add_service_agreement_textview_start_date);
        linearlayoutEndDate = (LinearLayout) findViewById(R.id.activity_add_service_agreement_linearlayout_end_date);
        textEndDate = (TextView) findViewById(R.id.activity_add_service_agreement_textview_end_date);
        edittextDescription = (EditText) findViewById(R.id.activity_add_service_agreement_edittext_description);
        edittextSalesPerson = (EditText) findViewById(R.id.activity_add_service_agreement_edittext_sales_person);
        edittextAgreementNumber = (EditText) findViewById(R.id.activity_add_service_agreement_edittext_agreement_number);
        // buttonAddLocation = (ImageView)
        // findViewById(R.id.activity_add_service_agreement_button_add_location);
        linearlayoutSystemsNumber = (LinearLayout) findViewById(R.id.activity_add_service_agreement_linearlayout_systems_number);
        textSystemsNumber = (TextView) findViewById(R.id.activity_add_service_agreement_textview_systems_number);
        linearlayoutLocationList = (LinearLayout) findViewById(R.id.activity_add_service_agreement_linearlayout_locations_list);
    }

    private void initHeader() {
        final QuickAction accountMenu;
        accountMenu = AccountMenu.setupMenu(mContext, mActivity);

        headerLayout = (LinearLayout) findViewById(R.id.activity_header);
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

        headerButtonBack.setOnClickListener(buttonListener);

        switch (AppDataSingleton.getInstance().getServiceAgreementAddViewMode()) {
            case Constants.SERVICE_AGREEMENT_ADD_VIEW_FROM_AGREEMENT:
                editMode = true;
                break;
            case Constants.SERVICE_AGREEMENT_ADD_VIEW_FROM_AGREEMENT_LIST:
                AppDataSingleton.getInstance().setServiceAgreement(new Agreement());
                editMode = false;
                break;
            case Constants.SERVICE_AGREEMENT_ADD_VIEW_FROM_VIEW_APPOINTMENT:
                AppDataSingleton.getInstance().setServiceAgreement(new Agreement());
                editMode = false;
                break;
            default:
                // Nothing
                break;
        }
    }


    private OnClickListener dialogButtonListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.dialog_date_picker_button_save:
                    if (dialogType == DIALOG_START_DATE) {
                        textStartDate
                                .setText((dialogDateDatePicker.getMonth() + 1)
                                        + "/"
                                        + dialogDateDatePicker.getDayOfMonth()
                                        + "/" + dialogDateDatePicker.getYear());
                        AppDataSingleton.getInstance().getServiceAgreement().setStartDate(
                                textStartDate.getText().toString());
                    } else {
                        textEndDate
                                .setText((dialogDateDatePicker.getMonth() + 1)
                                        + "/"
                                        + dialogDateDatePicker.getDayOfMonth()
                                        + "/" + dialogDateDatePicker.getYear());
                        AppDataSingleton.getInstance().getServiceAgreement().setEndDate(
                                textEndDate.getText().toString());
                    }
                    if (dialogDate.isShowing())
                        dialogDate.dismiss();
                    break;

                case R.id.dialog_date_picker_button_cancel:

                    if (dialogDate.isShowing())
                        dialogDate.dismiss();
                    break;
                default:
                    // Nothing
                    break;
            }
        }
    };

    private OnClickListener buttonListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {

			/* Back button */
                case R.id.header_button_back:
                    onBackPressed();
                    break;

				/* Save Button */
                case R.id.activity_add_service_agreement_button_save:

                    if (allDataEntered()) {
                        submitAgreement();
                        new SubmitAgreementTask().execute();
                    } else {
                        Toast.makeText(mContext, "Make sure to fill in all required fields", Toast.LENGTH_SHORT).show();
                    }
                    break;

				/* Linear Layout "Service Agreement" */
                case R.id.activity_add_service_agreement_linearlayout_service_agreement:
                    DialogAgreementTypes selectAgreementDialog = new DialogAgreementTypes(
                            mContext);
                    selectAgreementDialog.show();
                    selectAgreementDialog.setOnDismissListener(new OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            setupUI();
                        }
                    });
                    break;

				/* Linear Layout "Status" */
                case R.id.activity_add_service_agreement_linearlayout_status:
                    DialogAgreementStatus selectStatusDialog = new DialogAgreementStatus(mContext);
                    selectStatusDialog.show();
                    selectStatusDialog.setOnDismissListener(new OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            setupUI();
                        }
                    });
                    break;

				/* Linear Layout "Payment Type" */
                case R.id.activity_add_service_agreement_linearlayout_payment_type:
                    DialogAgreementPaymentTypes selectPaymentTypeDialog = new DialogAgreementPaymentTypes(
                            mContext);
                    selectPaymentTypeDialog.show();
                    selectPaymentTypeDialog
                            .setOnDismissListener(new OnDismissListener() {

                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    setupUI();
                                }
                            });
                    break;

				/* Linear Layout "Start Date */
                case R.id.activity_add_service_agreement_linearlayout_start_date:
                    dialogType = DIALOG_START_DATE;
                    showDateTimeDialog();
                    break;

				/* Linear Layout "End Date" */
                case R.id.activity_add_service_agreement_linearlayout_end_date:
                    dialogType = DIALOG_END_DATE;
                    showDateTimeDialog();
                    break;


				/* Linear layout "Systems Number" */
                case R.id.activity_add_service_agreement_linearlayout_systems_number:
                    DialogServiceAgreementSelectSystemsNumber selectSystemsNumberDialog = new DialogServiceAgreementSelectSystemsNumber(
                            mContext);
                    selectSystemsNumberDialog.show();
                    selectSystemsNumberDialog
                            .setOnDismissListener(new OnDismissListener() {

                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    setupUI();
                                }
                            });
                    break;
                default:
                    // Nothing
                    break;
            }
        }

        private void submitAgreement() {
            AppDataSingleton.getInstance().getServiceAgreement().setStartDate(
                    textStartDate.getText().toString());
            if (!"Never Expire".equals(textEndDate.getText()))
                AppDataSingleton.getInstance().getServiceAgreement().setEndDate(
                        textEndDate.getText().toString());
            AppDataSingleton.getInstance().getServiceAgreement().setDescription(
                    edittextDescription.getText().toString());
            AppDataSingleton.getInstance().getServiceAgreement().setSalesPerson(
                    edittextSalesPerson.getText().toString());
            AppDataSingleton.getInstance().getServiceAgreement().setContractNumber(
                    edittextAgreementNumber.getText().toString());
            AppDataSingleton.getInstance().getServiceAgreement().setNumberOfSystems(
                    Integer.parseInt(textSystemsNumber.getText()
                            .toString()));
        }
    };

    private void resetFields() {
        textServiceAgreement.setText(null);
        textStatus.setText(null);
        textPaymentType.setText(null);
        textStartDate.setText(null);
        textEndDate.setText(null);
    }

    private boolean allDataEntered() {
        if (TextUtils.isEmpty(textServiceAgreement.getText()) || TextUtils.isEmpty(textStatus.getText())
                || TextUtils.isEmpty(textPaymentType.getText()) || TextUtils.isEmpty(textStartDate.getText()))
            return false;
        return true;

    }

    private void setupUI() {

        buttonSave.setOnClickListener(buttonListener);
        linearlayoutServiceAgreement.setOnClickListener(buttonListener);
        linearlayoutStatus.setOnClickListener(buttonListener);
        linearlayoutPaymentType.setOnClickListener(buttonListener);
        linearlayoutStartDate.setOnClickListener(buttonListener);
        linearlayoutEndDate.setOnClickListener(buttonListener);
        linearlayoutSystemsNumber.setOnClickListener(buttonListener);

        populateFields();
        setupLocationsList();
    }

    private String getStatusLabel() {
        String selectedStatus = AppDataSingleton.getInstance().getServiceAgreement().getStatus();
        String[] statusesLabels = getResources().getStringArray(R.array.agreement_statuses);
        String[] statusesLinedUp = getResources().getStringArray(R.array.agreement_statuses_lined_up);

        for (int i = 0; i < statusesLinedUp.length; i++)
            if (statusesLinedUp[i].equals(selectedStatus)) {
                return statusesLabels[i];
            }
        return selectedStatus;
    }

    private String getPaymentLabel() {
        String selectedPayment = AppDataSingleton.getInstance().getServiceAgreement().getPaymentType();
        String[] paymentsLabels = getResources().getStringArray(R.array.agreement_payment_types);
        String[] paymentsLinedUp = getResources().getStringArray(R.array.agreement_payment_types_lined_up);

        for (int i = 0; i < paymentsLinedUp.length; i++)
            if (paymentsLinedUp[i].equals(selectedPayment)) {
                return paymentsLabels[i];
            }
        return selectedPayment;
    }

    private void populateFields() {

        if (editMode) {

            textServiceAgreement.setText(AppDataSingleton.getInstance().getServiceAgreement().getServicePlanName());
            textStatus.setText(getStatusLabel());
            textPaymentType.setText(getPaymentLabel());
            textStartDate.setText(AppDataSingleton.getInstance().getServiceAgreement().getStartDate());
            if (TextUtils.isEmpty(AppDataSingleton.getInstance().getServiceAgreement().getEndDate()))
                textEndDate.setText(mContext.getResources().getString(R.string.empty_string_never_expire));
            else
                textEndDate.setText(AppDataSingleton.getInstance().getServiceAgreement().getEndDate());
            edittextSalesPerson.setText(AppDataSingleton.getInstance().getServiceAgreement().getSalesPerson());
            edittextAgreementNumber.setText(AppDataSingleton.getInstance().getServiceAgreement().getContractNumber());
            textSystemsNumber.setText(String.valueOf(AppDataSingleton.getInstance().getServiceAgreement().getNumberOfSystems()));
            edittextDescription.setText(AppDataSingleton.getInstance().getServiceAgreement().getDescription());

        } else {
            textServiceAgreement.setText(AppDataSingleton.getInstance().getServiceAgreement().getServicePlanName());
            textStatus.setText(getStatusLabel());
            textPaymentType.setText(getPaymentLabel());
            textStartDate.setText(AppDataSingleton.getInstance().getServiceAgreement().getStartDate());
            if (TextUtils.isEmpty(AppDataSingleton.getInstance().getServiceAgreement().getEndDate()))
                textEndDate.setText(mContext.getResources().getString(R.string.empty_string_never_expire));
            else
                textEndDate.setText(AppDataSingleton.getInstance().getServiceAgreement().getEndDate());

            textSystemsNumber.setText(String.valueOf(AppDataSingleton.getInstance().getServiceAgreement().getNumberOfSystems()));

        }
    }

    private void setupLocationsList() {
        /* Locations AND equipment */
        linearlayoutLocationList.removeAllViews();
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (!AppDataSingleton.getInstance().getSingleAgreementLocationAndEquipmentList().isEmpty()) {
            for (int i = 0; i < AppDataSingleton.getInstance()
                    .getSingleAgreementLocationAndEquipmentList().size(); i++) {

                if (AppDataSingleton.getInstance().getSingleAgreementLocationAndEquipmentList()
                        .get(i).getType().equals("location")) {
                    View row = inflater
                            .inflate(
                                    R.layout.row_service_agreement_location_equipment,
                                    null);

                    TextView mainText = (TextView) row
                            .findViewById(R.id.row_service_agreement_location_equipment_textview_location);

                    // Checkboxes don't matter
                    CheckBox mainCheckBox = (CheckBox) row
                            .findViewById(R.id.row_service_agreement_location_equipment_checkbox_location);

                    LinearLayout subRowLayout = (LinearLayout) row
                            .findViewById(R.id.row_service_agreement_location_equipment_linearlayout_sub_row);

                    if (AppDataSingleton.getInstance()
                            .getSingleAgreementLocationAndEquipmentList()
                            .get(i).getType().equals("location")) {
                        mainText.setText(AppDataSingleton.getInstance()
                                .getSingleAgreementLocationAndEquipmentList()
                                .get(i).getName());

                        mainCheckBox
                                .setTag(AppDataSingleton.getInstance()
                                        .getSingleAgreementLocationAndEquipmentList()
                                        .get(i).getId());

                        mainCheckBox.setChecked(false);
                        if (editMode)
                            setupLocationsCheckState(i, mainCheckBox);

                        mainCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                int idToUse = Integer.parseInt(buttonView.getTag().toString());

                                Log.d("Add/Remove", "Location Modified");
                                if (isChecked) {
                                    // Add to list
                                    locationIds.add(idToUse);
                                } else {
                                    for (int i = 0; i < locationIds.size(); i++) {
                                        if (locationIds.get(i) == idToUse) {
                                            Log.d("Add/Remove", "Removed");
                                            locationIds.remove(i);
                                            break;
                                        }
                                    }
                                    // Go through the list, until we
                                    // find this id, and remove it
                                }

                            }
                        });

                        if ((i + 1) != AppDataSingleton.getInstance()
                                .getSingleAgreementLocationAndEquipmentList()
                                .size()) {
                            if (!AppDataSingleton.getInstance()
                                    .getSingleAgreementLocationAndEquipmentList()
                                    .get(i + 1).getType()
                                    .equals("location")) {
                                while (AppDataSingleton.getInstance()
                                        .getSingleAgreementLocationAndEquipmentList()
                                        .get(i + 1).getType()
                                        .equals("equipment")) {

                                    // Use row inflater to make "subRow"
                                    View subRow = inflater
                                            .inflate(
                                                    R.layout.row_service_agreement_location_equipment,
                                                    null);

                                    LinearLayout subRowLayoutSub = (LinearLayout) subRow
                                            .findViewById(R.id.row_service_agreement_location_equipment_linearlayout_sub_row);
                                    subRowLayoutSub
                                            .setVisibility(View.GONE);

                                    // Do sub row stuff
                                    TextView subText = (TextView) subRow
                                            .findViewById(R.id.row_service_agreement_location_equipment_textview_location);

                                    CheckBox subCheckBox = (CheckBox) subRow
                                            .findViewById(R.id.row_service_agreement_location_equipment_checkbox_location);

                                    subText.setText(AppDataSingleton.getInstance()
                                            .getSingleAgreementLocationAndEquipmentList()
                                            .get(i + 1).getName());

                                    subCheckBox
                                            .setTag(AppDataSingleton.getInstance()
                                                    .getSingleAgreementLocationAndEquipmentList()
                                                    .get(i + 1).getId());

                                    subCheckBox.setChecked(false);
                                    if (editMode)
                                        setupLocationEquipmentsCheckState(i, subCheckBox);

                                    subCheckBox
                                            .setOnCheckedChangeListener(new OnCheckedChangeListener() {

                                                @Override
                                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                    int idToUse = Integer.parseInt(buttonView.getTag().toString());

                                                    Log.d("Add/Remove", "1st Set of EQ Modified");
                                                    if (isChecked) {
                                                        // Add to list
                                                        equipmentIds.add(idToUse);
                                                    } else {
                                                        for (int i = 0; i < equipmentIds
                                                                .size(); i++) {
                                                            if (equipmentIds.get(i) == idToUse) {
                                                                Log.d("Add/Remove", "Removed");
                                                                equipmentIds.remove(i);
                                                                break;
                                                            }
                                                        }
                                                        // Go through the list, until we find this id, and remove it
                                                    }

                                                }
                                            });

                                    subRowLayout.addView(subRow);

                                    // Now, check to make sure i + 1 exists,
                                    // if so, ++, else break loop
                                    if ((i + 1) != AppDataSingleton.getInstance()
                                            .getSingleAgreementLocationAndEquipmentList()
                                            .size() - 1)
                                        i++;
                                    else
                                        break;
                                }
                            }
                        }
                    }

                    linearlayoutLocationList.addView(row);
                }
            }
        }

		/* Just Equipment */
        if (!AppDataSingleton.getInstance().getSingleAgreementEquipmentList().isEmpty()) {
            for (int i = 0; i < AppDataSingleton.getInstance()
                    .getSingleAgreementLocationAndEquipmentList().size(); i++) {

                View row = inflater.inflate(
                        R.layout.row_service_agreement_location_equipment,
                        null);

                TextView mainText = (TextView) row
                        .findViewById(R.id.row_service_agreement_location_equipment_textview_location);

                CheckBox mainCheckBox = (CheckBox) row
                        .findViewById(R.id.row_service_agreement_location_equipment_checkbox_location);

                mainCheckBox.setTag(AppDataSingleton.getInstance()
                        .getSingleAgreementEquipmentList().get(i).getId());

                LinearLayout subRowLayout = (LinearLayout) row
                        .findViewById(R.id.row_service_agreement_location_equipment_linearlayout_sub_row);

                mainText.setText(AppDataSingleton.getInstance().getSingleAgreementEquipmentList()
                        .get(i).getName());
                subRowLayout.setVisibility(View.GONE);

                mainCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        int idToUse = Integer.parseInt(buttonView.getTag().toString());

                        Log.d("Add/Remove", "2nd Set of modified");
                        if (isChecked) {
                            // Add to list
                            equipmentIds.add(idToUse);
                        } else {
                            for (int i = 0; i < equipmentIds.size(); i++) {
                                if (equipmentIds.get(i) == idToUse) {
                                    Log.d("Add/Remove", "Removed");
                                    equipmentIds.remove(i);
                                    break;
                                }
                            }
                            // Go through the list, until we
                            // find this id, and remove it
                        }

                    }
                });

                // Iterate through the list from mSingleAgreement, to
                // see if it should be checked or not (compare Ids)
                for (int x = 0; x < AppDataSingleton.getInstance().getServiceAgreement().equipment
                        .size(); x++) {
                    if (AppDataSingleton.getInstance().getServiceAgreement().equipment.get(x)
                            .getId() == AppDataSingleton.getInstance()
                            .getSingleAgreementEquipmentList().get(i)
                            .getId()) {
                        mainCheckBox.setChecked(true);
                        equipmentIds
                                .add(AppDataSingleton.getInstance().getServiceAgreement().equipment
                                        .get(x).getId());
                        break;
                    } else {
                        mainCheckBox.setChecked(false);
                    }
                }

                linearlayoutLocationList.addView(row);
            }
        }
    }

    private void setupLocationEquipmentsCheckState(int i, CheckBox subCheckBox) {
        List<Generic> locationAndEquipment = AppDataSingleton.getInstance().getServiceAgreement().locationAndEquipment;
        for (int x = 0; x < locationAndEquipment.size(); x++) {
            List<Generic> singleAgreementLocationAndEquipmentList = AppDataSingleton.getInstance()
                    .getSingleAgreementLocationAndEquipmentList();
            if (locationAndEquipment.get(x).getId() == singleAgreementLocationAndEquipmentList.get(i + 1).getId()
                    && "equipment".equals(singleAgreementLocationAndEquipmentList.get(i + 1).getType())) {
                subCheckBox.setChecked(true);
                equipmentIds.add(singleAgreementLocationAndEquipmentList.get(i + 1).getId());
                break;
            } else {
                subCheckBox.setChecked(false);
            }
        }
    }

    private void setupLocationsCheckState(int i, CheckBox mainCheckBox) {
        for (int y = 0; y < AppDataSingleton.getInstance().getServiceAgreement().locationAndEquipment.size(); y++) {

            List<Generic> singleAgLocEquipmList = AppDataSingleton.getInstance().getSingleAgreementLocationAndEquipmentList();
            if (singleAgLocEquipmList.get(i).getId() == singleAgLocEquipmList.get(y).getId()
                    && "location".equals(singleAgLocEquipmList.get(i).getType())) {
                mainCheckBox.setChecked(true);
                locationIds.add(singleAgLocEquipmList.get(i).getId());
                break;
            } else {
                mainCheckBox.setChecked(false);
            }
        }
    }

    private void showDateTimeDialog() {
        // Create the dialog
        dialogDate = new Dialog(mContext);
        dialogDate.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogDate.setContentView(R.layout.dialog_layout_date_picker);
        dialogDate.setTitle(mContext.getResources().getString(
                R.string.dialog_header_string_select_date));

        dialogDateButtonSave = (TextView) dialogDate
                .findViewById(R.id.dialog_date_picker_button_save);
        dialogDateButtonCancel = (TextView) dialogDate
                .findViewById(R.id.dialog_date_picker_button_cancel);

        dialogDateDatePicker = (DatePicker) dialogDate
                .findViewById(R.id.dialog_date_picker_datepicker);

        dialogDateButtonSave.setOnClickListener(dialogButtonListener);
        dialogDateButtonCancel.setOnClickListener(dialogButtonListener);

        dialogDate.show();

    }

    private class GetLocationAndEquipmentTask extends BaseUiReportTask<String> {
        GetLocationAndEquipmentTask() {
            super(ActivityServiceAgreementAddEdit.this,
                    R.string.async_task_string_loading_locations_and_equipment);
        }

        @Override
        protected void onSuccess() {
            setupUI();
        }

        @Override
        protected boolean taskBody(final String... args) throws Exception {
            if (!CommonUtilities.isNetworkAvailable(mActivity)) {
                Toast.makeText(mActivity, "Network connection unavailable.",
                        Toast.LENGTH_SHORT).show();
                return true;
            }
            RESTLocationAndEquipmentList.query(AppDataSingleton.getInstance().getCustomer().getId());
            RESTServicePlanList.query(UserUtilitiesSingleton.getInstance().user.getOwnerId());
            return true;
        }
    }

    private class SubmitAgreementTask extends BaseUiReportTask<String> {

        SubmitAgreementTask() {
            super(
                    ActivityServiceAgreementAddEdit.this,
                    editMode
                            ? R.string.async_task_string_updating_service_agreement
                            : R.string.async_task_string_submitting_new_service_agreement);
        }

        @Override
        protected void onSuccess() {
            finish();
        }

        @Override
        protected boolean taskBody(final String... args) throws Exception {
            if (!CommonUtilities.isNetworkAvailable(mActivity)) {
                Toast.makeText(mActivity, "Network connection unavailable.",
                        Toast.LENGTH_SHORT).show();
                return true;
            }

            if (editMode)
                RESTAgreement.update(AppDataSingleton.getInstance().getServiceAgreement().getId(),
                        AppDataSingleton.getInstance().getServiceAgreement().getServicePlanId(),
                        AppDataSingleton.getInstance().getServiceAgreement().getStatus(), AppDataSingleton.getInstance()
                                .getServiceAgreement().getDescription(),
                        AppDataSingleton.getInstance().getServiceAgreement().getPaymentType(), AppDataSingleton.getInstance()
                                .getServiceAgreement().getContractNumber(),
                        AppDataSingleton.getInstance().getServiceAgreement().getSalesPerson(), AppDataSingleton.getInstance()
                                .getServiceAgreement().getNumberOfSystems(),
                        AppDataSingleton.getInstance().getServiceAgreement().getStartDate(), AppDataSingleton.getInstance()
                                .getServiceAgreement().getEndDate(),
                        locationIds, equipmentIds);
            else
                RESTAgreement.add(AppDataSingleton.getInstance().getCustomer().getId(), AppDataSingleton.getInstance()
                                .getServiceAgreement().getServicePlanId(), AppDataSingleton.getInstance()
                                .getServiceAgreement().getStatus(), AppDataSingleton.getInstance()
                                .getServiceAgreement().getDescription(), AppDataSingleton.getInstance()
                                .getServiceAgreement().getPaymentType(), AppDataSingleton.getInstance()
                                .getServiceAgreement().getContractNumber(), AppDataSingleton.getInstance()
                                .getServiceAgreement().getSalesPerson(), AppDataSingleton.getInstance()
                                .getServiceAgreement().getNumberOfSystems(), AppDataSingleton.getInstance()
                                .getServiceAgreement().getStartDate(), AppDataSingleton.getInstance()
                                .getServiceAgreement().getEndDate(), locationIds,
                        equipmentIds);
            return true;
        }
    }
}