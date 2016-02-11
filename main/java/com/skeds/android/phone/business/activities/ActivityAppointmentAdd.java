package com.skeds.android.phone.business.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.skeds.android.phone.business.AsyncTasks.BaseUiReportTask;
import com.skeds.android.phone.business.Custom.AccountMenu;
import com.skeds.android.phone.business.Custom.QuickAction;
import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.BusinessHourException;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.LeadSource;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Location;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.NewAppointment;
import com.skeds.android.phone.business.Utilities.General.CommonUtilities;
import com.skeds.android.phone.business.Utilities.General.Constants;
import com.skeds.android.phone.business.Utilities.General.UserUtilitiesSingleton;
import com.skeds.android.phone.business.Utilities.REST.Objects.RESTAppointment;
import com.skeds.android.phone.business.Utilities.REST.Objects.RESTAppointmentTypeList;
import com.skeds.android.phone.business.util.DateUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class ActivityAppointmentAdd extends BaseSkedsActivity {

    private boolean editMode;

    private int selectedStartHour, selectedStartMinute;
    private int selectedLocation;
    private int selectedAppointmentType;
    private int selectedPriority = -1;

    private LinearLayout headerLayout;
    private ImageView headerButtonUser;
    private ImageView headerButtonBack;
    private TextView headerButtonSave;

    private ScrollView scrollView;

    private TextView textCompanyName;
    private TextView textAddressLine1;
    private TextView textAddressLine2;
    private TextView textAppointmentType;
    private TextView textStartDate;
    private TextView textEndDate;
    private EditText edittextNotes;
    private TextView textPriority;
    private TextView textSuggestedDate;

    private TextView buttonSelectLocation;
    private TextView buttonSelectAppointmentType;
    private TextView buttonSelectAppointmentPriority;
    private TextView buttonSetStartDate;
    private TextView buttonSetEndDate;
    private TextView buttonSetSuggestedDate;

    private TableRow startDateTab;
    private TableRow endDateTab;
    private TableRow suggestedDateTab;
    private TableRow priorityTab;

    private CheckBox mAfterHours;
    private CheckBox mUnscheduled;

    private Dialog appointmentTypesDialog;
    private TextView appointmentTypesDialogSave, appointmentTypesDialogCancel;

    private Dialog appointmentPriorityDialog;
    private TextView appointmentPriorityDialogSave,
            appointmentPriorityDialogCancel;


    private Dialog endDateDialog, startDateDialog, suggestedDateDialog;

    private String dateToUse = "";
    private String tomorrowsDate = ""; // If they select a "Midnight" time slot

    private final String DEBUG_TAG = "[New Appointment]";

    private Activity mActivity;
    private Context mContext;

    private static final int GET_LEAD_SOURCE_REQUEST = 10;

    private final String[] priorityArray = new String[]{"LOW", "MEDIUM", "HIGH"};
    private TimeZone timeZone;
    private TextView textStartDateTitle;
    private TextView textEndDateTitle;
    private LeadSource leadSource;
    private TextView leadSourceText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_add_appointment_view);

        mActivity = ActivityAppointmentAdd.this;
        mContext = this;

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
        headerButtonSave = (TextView) headerLayout
                .findViewById(R.id.header_standard_button_right);

        scrollView = (ScrollView) findViewById(R.id.activity_add_appointment_scrollview);

        textCompanyName = (TextView) findViewById(R.id.activity_add_appointment_textview_company_name);
        textAddressLine1 = (TextView) findViewById(R.id.activity_add_appointment_textview_location_1);
        textAddressLine2 = (TextView) findViewById(R.id.activity_add_appointment_textview_location_2);
        textAppointmentType = (TextView) findViewById(R.id.activity_add_appointment_textview_appointment_type);
        textStartDate = (TextView) findViewById(R.id.activity_add_appointment_textview_start_date);
        textEndDate = (TextView) findViewById(R.id.activity_add_appointment_textview_end_date);

        textStartDateTitle = (TextView) findViewById(R.id.tv_set_start_date_title);
        textEndDateTitle = (TextView) findViewById(R.id.tv_set_end_date_title);
        textEndDate = (TextView) findViewById(R.id.activity_add_appointment_textview_end_date);
        edittextNotes = (EditText) findViewById(R.id.activity_add_appointment_edittext_notes);

        textPriority = (TextView) findViewById(R.id.activity_add_appointment_textview_appointment_priority);
        textSuggestedDate = (TextView) findViewById(R.id.activity_add_appointment_textview_suggested_date);

        buttonSelectLocation = (TextView) findViewById(R.id.activity_add_appointment_button_select_location);
        buttonSelectAppointmentType = (TextView) findViewById(R.id.activity_add_appointment_button_select_appointment_type);
        buttonSelectAppointmentPriority = (TextView) findViewById(R.id.activity_add_appointment_button_select_appointment_priority);
        buttonSetStartDate = (TextView) findViewById(R.id.activity_add_appointment_button_start_date);
        buttonSetEndDate = (TextView) findViewById(R.id.activity_add_appointment_button_end_date);
        buttonSetSuggestedDate = (TextView) findViewById(R.id.activity_add_appointment_button_suggested_date);

        startDateTab = (TableRow) findViewById(R.id.start_date_tab);
        endDateTab = (TableRow) findViewById(R.id.end_date_tab);
        suggestedDateTab = (TableRow) findViewById(R.id.suggested_date_tab);
        priorityTab = (TableRow) findViewById(R.id.priority_tab);
        leadSourceText = (TextView) findViewById(R.id.lead_source_text);

        mAfterHours = (CheckBox) findViewById(R.id.activity_add_appointment_checkbox_after_hours);

        mUnscheduled = (CheckBox) findViewById(R.id.activity_add_appointment_checkbox_unscheduled);

        TextView addLeadsButton = (TextView) findViewById(R.id.source_lead_add);
        addLeadsButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, LeadSourceListActivity.class);
                intent.putExtra(LeadSourceListActivity.LEAD_SOURCE_TYPE_FILTER,"APPOINTMENT");
                startActivityForResult(intent, GET_LEAD_SOURCE_REQUEST);
            }
        });

        switch (AppDataSingleton.getInstance().getAppointmentAddViewMode()) {
            case Constants.APPOINTMENT_ADD_VIEW_FROM_APPOINTMENT:
                editMode = true;
                break;
            case Constants.APPOINTMENT_ADD_VIEW_FROM_CUSTOMER:
                editMode = false;
                break;
            default:
                // Nothing
                break;
        }

        if (!CommonUtilities.isNetworkAvailable(mContext)) {
            Toast.makeText(mContext, "Network connection unavailable.",
                    Toast.LENGTH_SHORT).show();
            finish();
        } else {
            new GetAppointmentsTask().execute();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            selectedLocation = data.getIntExtra("location_number", 0);

            if (AppDataSingleton.getInstance().getCustomer().locationList.size() <= selectedLocation)
                return;


            timeZone = AppDataSingleton.getInstance().getCustomer().locationList.get(selectedLocation).getTimeZone();


            setupUI();
            if (AppDataSingleton.getInstance().getCustomer().locationList
                            .get(selectedLocation)==null) return;
            textAddressLine1.setText(AppDataSingleton.getInstance().getCustomer().locationList
                    .get(selectedLocation).getAddress1()
                    + " "
                    + AppDataSingleton.getInstance().getCustomer().locationList.get(
                    selectedLocation).getAddress2());
            textAddressLine1.setTag(AppDataSingleton.getInstance().getCustomer().locationList.get(
                    selectedLocation).getId());

            textAddressLine2.setText(AppDataSingleton.getInstance().getCustomer().locationList
                    .get(selectedLocation).getCity()
                    + ", "
                    + AppDataSingleton.getInstance().getCustomer().locationList.get(
                    selectedLocation).getState()
                    + " "
                    + AppDataSingleton.getInstance().getCustomer().locationList.get(
                    selectedLocation).getZip());
        }

        if ((resultCode == LeadSourceListActivity.LEAD_SOURCE)&&(data!=null)) {
            leadSource = (LeadSource) data.getSerializableExtra(LeadSourceListActivity.key);
            if (leadSource != null) {
                AppDataSingleton.getInstance().getAppointment().setLeadSourceId(leadSource.getId());
                leadSourceText.setText(leadSource.getName());
            }
        }

    }

    private void setupUI() {

        // Keeps the keyboard from popping-up on the notes textbox
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        String startTime;

        if (timeZone == null)
            timeZone = AppDataSingleton.getInstance().getAppointment().getTimeZone() != null ?
                    AppDataSingleton.getInstance().getAppointment().getTimeZone() : TimeZone.getDefault();


        String endTime;
        if (editMode) {

            startTime = DateUtils.convertFromPatternToPattern(

                    AppDataSingleton.getInstance().getAppointment().getDate() + " " + AppDataSingleton.getInstance().getAppointment().getStartTime(),
                    "MM/dd/yyyy hh:mm aaa",
                    "MM/dd/yyyy h:mm aaa",
                    UserUtilitiesSingleton.getInstance().user.getTimeZone(),
                    timeZone

            );

            endTime = DateUtils.convertFromPatternToPattern(

                    AppDataSingleton.getInstance().getAppointment().getDate() + " " + AppDataSingleton.getInstance().getAppointment().getEndTime(),
                    "MM/dd/yyyy hh:mm aaa",
                    "MM/dd/yyyy h:mm aaa",
                    UserUtilitiesSingleton.getInstance().user.getTimeZone(),
                    timeZone
            );

            textStartDate.setText(startTime
                    + " " + timeZone.getDisplayName(false, TimeZone.SHORT));
            textEndDate.setText(endTime
                    + " " + timeZone.getDisplayName(false, TimeZone.SHORT));
        } 

        textStartDateTitle.setText("Set Start Date in " + timeZone.getDisplayName(false, TimeZone.SHORT) + " time zone");

        textEndDateTitle.setText("Set End Date in " + timeZone.getDisplayName(false, TimeZone.SHORT) + " time zone");


        headerButtonBack.setOnClickListener(clickListener);
        headerButtonSave.setOnClickListener(clickListener);

        buttonSelectLocation.setOnClickListener(clickListener);
        buttonSelectAppointmentType.setOnClickListener(clickListener);
        buttonSelectAppointmentPriority.setOnClickListener(clickListener);
        buttonSetStartDate.setOnClickListener(clickListener);
        buttonSetEndDate.setOnClickListener(clickListener);
        buttonSetSuggestedDate.setOnClickListener(clickListener);

        mUnscheduled.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {

                    selectedPriority = -1;
                    startDateTab.setVisibility(View.GONE);
                    endDateTab.setVisibility(View.GONE);
                    priorityTab.setVisibility(View.VISIBLE);
                    textPriority.setText("");
                    textSuggestedDate.setText("");
                    if ("low".equals(textPriority.getText())
                            || "medium".equals(textPriority.getText())) {
                        suggestedDateTab.setVisibility(View.VISIBLE);
                    }
                } else {
                    startDateTab.setVisibility(View.VISIBLE);
                    endDateTab.setVisibility(View.VISIBLE);
                    priorityTab.setVisibility(View.GONE);
                    suggestedDateTab.setVisibility(View.GONE);
                }
            }
        });

        StringBuilder builderCompanyName = new StringBuilder();
        if (!TextUtils.isEmpty(AppDataSingleton.getInstance().getCustomer().getOrganizationName()))
            builderCompanyName.append(AppDataSingleton.getInstance().getCustomer()
                    .getOrganizationName());
        else {

            builderCompanyName.append(AppDataSingleton.getInstance().getCustomer().getFirstName());
            builderCompanyName.append(" ");
            builderCompanyName.append(AppDataSingleton.getInstance().getCustomer().getLastName());
        }
        textCompanyName.setText(builderCompanyName.toString());

        if (editMode) {

			/* Address/Location Info */
            textAddressLine1.setTag(AppDataSingleton.getInstance().getAppointment().getLocationId());
            Location location = AppDataSingleton.getInstance().getCustomer().locationList.get(selectedLocation);

            if (location != null) {
                textAddressLine1.setText(location.getAddress1()
                        + " "
                        + location.getAddress2());

                textAddressLine2.setText(location.getCity()
                        + ", "
                        + location.getState()
                        + " "
                        + location.getZip());

            }
            selectedLocation = AppDataSingleton.getInstance().getAppointment().getLocationId();

            // TODO - is name or value available? If so, divide up, populate
            // accordingly, if not, then show lat/long

			/* Appointment Type */
            textAppointmentType.setText(AppDataSingleton.getInstance().getAppointment()
                    .getApptTypeName());
            textAppointmentType
                    .setTag(AppDataSingleton.getInstance().getAppointment().getApptTypeId());
            // selectedAppointmentType = CommonUtilities.appointment
            // .getApptTypeId();

			/* Notes */
            edittextNotes.setText(AppDataSingleton.getInstance().getAppointment().getNotes());

            for (LeadSource source : AppDataSingleton.getInstance().getLeadSourceListItem()) {
                if (source.getId().equals(AppDataSingleton.getInstance()
                        .getAppointment().getLeadSourceId())){
                    leadSource = source;
                    leadSourceText.setText(leadSource.getName());
                }



            }
        }

        edittextNotes.clearFocus();
        textCompanyName.requestFocus();

        // Scroll to top of view
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_UP);
            }
        });
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

    private void setupPriorityList() {

        appointmentPriorityDialog = new Dialog(mContext);
        appointmentPriorityDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        appointmentPriorityDialog.setContentView(R.layout.dialog_layout_list);

        ListView mListView = (ListView) appointmentPriorityDialog
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

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,
                R.layout.row_equipment_item, R.id.equipmentListItem,
                priorityArray);

        mListView.setAdapter(adapter);

        mListView.setTextFilterEnabled(true);
        mListView.setOnItemClickListener(priorityListListener);

        appointmentPriorityDialogSave = (TextView) appointmentPriorityDialog
                .findViewById(R.id.dialog_list_button_save);
        appointmentPriorityDialogCancel = (TextView) appointmentPriorityDialog
                .findViewById(R.id.dialog_list_button_cancel);

        appointmentPriorityDialogSave
                .setOnClickListener(priorityTypesSaveListener);
        appointmentPriorityDialogCancel
                .setOnClickListener(priorityTypesCancelListener);

        appointmentPriorityDialog.show();
    }


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

            selectedAppointmentType = (int) id;
        }
    };

    private OnItemClickListener priorityListListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View v, int position,
                                long id) {

            TextView selectedText = (TextView) appointmentPriorityDialog
                    .findViewById(R.id.dialog_list_textview_selected);
            selectedText.setText(priorityArray[position]);

            selectedPriority = position;
        }
    };


    private OnClickListener clickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.activity_add_appointment_button_start_date:
                    if (textAppointmentType.getTag() != null) {
                        startDateDialog = new Dialog(mContext);
                        startDateDialog
                                .requestWindowFeature(Window.FEATURE_NO_TITLE);
                        startDateDialog
                                .setContentView(R.layout.dialog_layout_date_picker_and_selector);

                        TextView saveButton = (TextView) startDateDialog
                                .findViewById(R.id.dialog_date_picker_and_selector_button_save);
                        TextView cancelButton = (TextView) startDateDialog
                                .findViewById(R.id.dialog_date_picker_and_selector_button_cancel);
                        Spinner startTimeSpinner = (Spinner) startDateDialog
                                .findViewById(R.id.dialog_date_picker_and_selector_spinner);

                        startTimePickerDialog(saveButton, cancelButton, startTimeSpinner);
                    } else {
                        Toast.makeText(mContext,
                                "You must select an appointment type first.",
                                Toast.LENGTH_LONG).show();
                    }
                    break;

                case R.id.header_button_back:
                    onBackPressed();
                    break;
                case R.id.header_standard_button_right:

                    if (!CommonUtilities.isNetworkAvailable(mContext)) {
                        Toast.makeText(mContext, "Network connection unavailable.",
                                Toast.LENGTH_SHORT).show();
                        break;
                    }

                    if (isDateInExceptionsDate()) {
                        Toast.makeText(mContext,
                                "You could not set this dates, because it in exceptions hours ",
                                Toast.LENGTH_LONG).show();

                        return;
                    }

                    if (textAppointmentType.getTag() != null
                            && textAddressLine1.getTag() != null) {
                        if (mUnscheduled.isChecked() && selectedPriority == 2) {
                            new SubmitAppointmentTask().execute();
                        } else if (mUnscheduled.isChecked()
                                && selectedPriority != -1
                                && !TextUtils.isEmpty(textSuggestedDate.getText())) {
                            new SubmitAppointmentTask().execute();
                        } else if (!mUnscheduled.isChecked()
                                && !TextUtils.isEmpty(textStartDate.getText())
                                && !TextUtils.isEmpty(textEndDate.getText())) {
                            new SubmitAppointmentTask().execute();
                        } else
                            Toast.makeText(mContext,
                                    "Make sure to fill in all required fields",
                                    Toast.LENGTH_LONG).show();
                    } else
                        Toast.makeText(mContext,
                                "Make sure to fill in all required fields",
                                Toast.LENGTH_LONG).show();
                    break;

                case R.id.activity_add_appointment_button_select_location:
                    Intent i = new Intent(mContext, ActivityCustomerLocationListView.class);
                    i.setAction(ActivityCustomerLocationListView.ACTION_PICK_LOCATION);
                    startActivityForResult(i, 0);
                    break;

                case R.id.activity_add_appointment_button_select_appointment_type:

                    setupAppointmentsList();
                    break;
                case R.id.activity_add_appointment_button_select_appointment_priority:
                    setupPriorityList();
                    break;
                case R.id.activity_add_appointment_button_suggested_date:
                    if (textAppointmentType.getTag() != null) {
                        suggestedDateDialog = new Dialog(mContext);
                        suggestedDateDialog
                                .requestWindowFeature(Window.FEATURE_NO_TITLE);
                        suggestedDateDialog
                                .setContentView(R.layout.dialog_layout_date_picker_and_selector);

                        TextView saveButton = (TextView) suggestedDateDialog
                                .findViewById(R.id.dialog_date_picker_and_selector_button_save);
                        TextView cancelButton = (TextView) suggestedDateDialog
                                .findViewById(R.id.dialog_date_picker_and_selector_button_cancel);
                        Spinner startTimeSpinner = (Spinner) suggestedDateDialog
                                .findViewById(R.id.dialog_date_picker_and_selector_spinner);

                        ArrayList<String> timesForSpinner = new ArrayList<String>();

                        long thisTime = 0;
                        while (thisTime < NewAppointment.getLatestTimeMinutes()) {

                            // Figure out to the hour/minute setup
                            int hourToUse = (int) (thisTime / 60);
                            int minuteToUse = (int) (thisTime % 60);

                            // Convert to string used by array list
                            String setTime = hourToUse + ":" + minuteToUse;
                            DateFormat df = new SimpleDateFormat("H:mm");

                            String formattedTime = "";
                            try {
                                Date today = df.parse(setTime);

                                DateFormat formatter = new SimpleDateFormat(
                                        "h:mm aaa");
                                formattedTime = formatter.format(today);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            // Push to array list
                            timesForSpinner.add(formattedTime);

                            // Add the increment and start over
                            thisTime += AppDataSingleton.getInstance().getNewAppointment().appointmentTypeList
                                    .get(selectedAppointmentType)
                                    .getIncrementMinutes();

                        }

                        startTimeSpinner.setVisibility(View.GONE);
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                                mContext,
                                android.R.layout.simple_spinner_dropdown_item,
                                timesForSpinner);

                        saveButton
                                .setOnClickListener(selectSuggestedDateTimeSaveListener);
                        cancelButton
                                .setOnClickListener(selectSuggestedDateTimeCancelListener);
                        suggestedDateDialog.show();
                    } else {
                        Toast.makeText(mContext,
                                "You must select an appointment type first.",
                                Toast.LENGTH_LONG).show();
                    }
                    break;
                case R.id.activity_add_appointment_button_end_date:
                    if (textAppointmentType.getTag() != null) {
                        if (!TextUtils.isEmpty(textStartDate.getText())) {
                            endDateDialog = new Dialog(mContext);
                            endDateDialog
                                    .requestWindowFeature(Window.FEATURE_NO_TITLE);
                            endDateDialog
                                    .setContentView(R.layout.dialog_layout_appointment_end_time);

                            TextView saveButton = (TextView) endDateDialog
                                    .findViewById(R.id.dialog_appointment_end_time_button_save);
                            TextView cancelButton = (TextView) endDateDialog
                                    .findViewById(R.id.dialog_appointment_end_time_button_cancel);

                            ArrayList<String> timesForSpinner = new ArrayList<String>();

                            String inputStartTime = textStartDate.getText()
                                    .toString();
                            DateFormat startDf = new SimpleDateFormat(
                                    "dd/MM/yyyy h:mm aaa");
                            try {
                                Date startDate = startDf.parse(inputStartTime);

                                DateFormat startDateFormat = new SimpleDateFormat(
                                        "H:mm");
                                String startDateOutput = startDateFormat
                                        .format(startDate);

                                selectedStartHour = Integer
                                        .parseInt(startDateOutput.substring(0,
                                                startDateOutput.indexOf(":")));
                                selectedStartMinute = Integer
                                        .parseInt(startDateOutput.substring(
                                                startDateOutput.indexOf(":") + 1,
                                                startDateOutput.length()));

                            } catch (ParseException e1) {

                                e1.printStackTrace();
                            }

                            Log.e(DEBUG_TAG, "Start Hour: " + selectedStartHour
                                    + " Start Minute: " + selectedStartMinute);

                            long thisTime = (((selectedStartHour * 60) + selectedStartMinute))
                                    + AppDataSingleton.getInstance().getNewAppointment().appointmentTypeList
                                    .get(selectedAppointmentType)
                                    .getMinimumLengthMinutes();

                            long maxTime = thisTime
                                    + AppDataSingleton.getInstance().getNewAppointment().appointmentTypeList
                                    .get(selectedAppointmentType)
                                    .getMaximumLengthMinutes();

                            while (thisTime <= maxTime) {

                                // Figure out to the hour/minute setup
                                int hourToUse = (int) (thisTime / 60);
                                int minuteToUse = (int) (thisTime % 60);

                                // Convert to string used by array list
                                String setTime = hourToUse + ":" + minuteToUse;
                                DateFormat df = new SimpleDateFormat("H:mm");

                                String formattedTime = "";
                                try {
                                    Date today = df.parse(setTime);

                                    DateFormat formatter = new SimpleDateFormat(
                                            "h:mm aaa");
                                    formattedTime = formatter.format(today);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                // Push to array list
                                timesForSpinner.add(formattedTime);

                                // Add the increment and start over
                                thisTime += AppDataSingleton.getInstance().getNewAppointment().appointmentTypeList
                                        .get(selectedAppointmentType)
                                        .getIncrementMinutes();
                            }

                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                                    mContext,
                                    android.R.layout.simple_spinner_dropdown_item,
                                    timesForSpinner);
                            Spinner timeSpinner = (Spinner) endDateDialog
                                    .findViewById(R.id.dialog_appointment_end_time_spinner);
                            timeSpinner.setAdapter(null);
                            timeSpinner.setAdapter(arrayAdapter);

                            saveButton
                                    .setOnClickListener(selectEndDateTimeSaveListener);
                            cancelButton
                                    .setOnClickListener(selectDateTimeCancelListener);
                            endDateDialog.show();
                        } else {
                            Toast.makeText(
                                    mContext,
                                    mContext.getResources()
                                            .getString(
                                                    R.string.toast_string_you_must_select_a_start_time_first),
                                    Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(
                                mContext,
                                mContext.getResources()
                                        .getString(
                                                R.string.toast_string_you_must_select_an_appointment_type_first),
                                Toast.LENGTH_LONG).show();
                    }
                    break;

                default:
                    // Nothing
                    break;
            }
        }
    };

    private void startTimePickerDialog(TextView saveButton, TextView cancelButton, Spinner startTimeSpinner) {
        ArrayList<String> timesForSpinner = new ArrayList<String>();

        long thisTime = NewAppointment.getEarliestTimeMinutes();
        while (thisTime <= NewAppointment.getLatestTimeMinutes()) {

            // Figure out to the hour/minute setup
            int hourToUse = (int) (thisTime / 60);
            int minuteToUse = (int) (thisTime % 60);

            // Convert to string used by array list
            String setTime = hourToUse + ":" + minuteToUse;
            DateFormat df = new SimpleDateFormat("H:mm");

            String formattedTime = "";
            try {
                Date today = df.parse(setTime);

                DateFormat formatter = new SimpleDateFormat(
                        "h:mm aaa");
                formattedTime = formatter.format(today);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            // Push to array list
            timesForSpinner.add(formattedTime);

            // Add the increment and start over
            thisTime += AppDataSingleton.getInstance().getNewAppointment().appointmentTypeList
                    .get(selectedAppointmentType)
                    .getIncrementMinutes();

        }

        startTimeSpinner.setAdapter(null);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                mContext,
                android.R.layout.simple_spinner_dropdown_item,
                timesForSpinner);
        startTimeSpinner.setAdapter(arrayAdapter);

        saveButton
                .setOnClickListener(selectStartDateTimeSaveListener);
        cancelButton
                .setOnClickListener(selectDateTimeCancelListener);
        startDateDialog.show();
    }

    private boolean isDateInExceptionsDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy h:mm aaa");


        if (TextUtils.isEmpty(textStartDate.getText())
                && TextUtils.isEmpty(textEndDate.getText()))
            return true;


        for (BusinessHourException businessHourException : AppDataSingleton.getInstance().getNewAppointment().businessHoursExceptionList) {
            try {
                Date startDate = sdf.parse(textStartDate.getText().toString());
                Date endDate = sdf.parse(textEndDate.getText().toString());

                Date exceptionFromDate = sdf.parse(businessHourException.fromDate + " " + businessHourException.fromTime);

                Date exceptionToDate = sdf.parse(businessHourException.toDate + " " + businessHourException.toTime);

                //if startDate and endDate range intersects with exceptionFromDate
                if (startDate.before(exceptionToDate) && endDate.after(exceptionFromDate))
                    return true;

            } catch (ParseException e) {
                e.printStackTrace();

                Log.e(DEBUG_TAG, "Parse error :" + e.getMessage());

                return true;
            }

        }


        return false;
    }

    private OnClickListener appointmentTypesSaveListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            TextView selectedText = (TextView) appointmentTypesDialog
                    .findViewById(R.id.dialog_list_textview_selected);

            if ("None".equals(selectedText.getText())) {
                textAppointmentType
                        .setText(AppDataSingleton.getInstance().getNewAppointment().appointmentTypeList
                                .get(0).getName());
                textAppointmentType
                        .setTag(AppDataSingleton.getInstance().getNewAppointment().appointmentTypeList
                                .get(0).getId());
                selectedAppointmentType = 0;
            } else {

                textAppointmentType.setText(selectedText.getText());
                textAppointmentType.setTag(selectedText.getTag());
            }

            if (appointmentTypesDialog.isShowing())
                appointmentTypesDialog.dismiss();
        }
    };

    private OnClickListener priorityTypesSaveListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            TextView selectedText = (TextView) appointmentPriorityDialog
                    .findViewById(R.id.dialog_list_textview_selected);

            if ("None".equals(selectedText.getText())) {
                textPriority.setText(priorityArray[0]);
                selectedPriority = 0;
            } else {
                textPriority.setText(priorityArray[selectedPriority]);
            }

            if (selectedPriority == 0 || selectedPriority == 1) {
                suggestedDateTab.setVisibility(View.VISIBLE);
            } else {
                suggestedDateTab.setVisibility(View.GONE);
            }

            if (appointmentPriorityDialog.isShowing())
                appointmentPriorityDialog.dismiss();
        }
    };

    private OnClickListener appointmentTypesCancelListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (appointmentTypesDialog.isShowing())
                appointmentTypesDialog.dismiss();
        }
    };

    private OnClickListener priorityTypesCancelListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (appointmentPriorityDialog.isShowing())
                appointmentPriorityDialog.dismiss();
        }
    };


    private OnClickListener selectSuggestedDateTimeSaveListener = new OnClickListener() {

        @Override
        public void onClick(View v) {

            DatePicker dp = (DatePicker) suggestedDateDialog
                    .findViewById(R.id.dialog_date_picker_and_selector_datepicker);

            dateToUse = dp.getMonth() + 1 + "/" + dp.getDayOfMonth() + "/"
                    + dp.getYear();

            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            Calendar c = Calendar.getInstance();
            try {
                c.setTime(sdf.parse(dateToUse));
            } catch (ParseException e) {

                e.printStackTrace();
            }
            c.add(Calendar.DATE, 1); // number of days to add
            tomorrowsDate = sdf.format(c.getTime());

            textSuggestedDate.setText(dateToUse);

            if (suggestedDateDialog.isShowing())
                suggestedDateDialog.dismiss();
        }
    };

    private OnClickListener selectStartDateTimeSaveListener = new OnClickListener() {

        @Override
        public void onClick(View v) {

            DatePicker dp = (DatePicker) startDateDialog
                    .findViewById(R.id.dialog_date_picker_and_selector_datepicker);

            Spinner timeSpinner = (Spinner) startDateDialog
                    .findViewById(R.id.dialog_date_picker_and_selector_spinner);

            dateToUse = dp.getMonth() + 1 + "/" + dp.getDayOfMonth() + "/"
                    + dp.getYear();

            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            Calendar c = Calendar.getInstance();
            try {
                c.setTime(sdf.parse(dateToUse));
            } catch (ParseException e) {

                e.printStackTrace();
            }
            c.add(Calendar.DATE, 1); // number of days to add
            tomorrowsDate = sdf.format(c.getTime());

            textStartDate.setText(dateToUse + " "
                    + timeSpinner.getSelectedItem().toString());

            String inputStartTime = textStartDate.getText()
                    .toString();
            DateFormat startDf = new SimpleDateFormat(
                    "dd/MM/yyyy h:mm aaa");
            try {
                Date startDate = startDf.parse(inputStartTime);

                DateFormat startDateFormat = new SimpleDateFormat(
                        "H:mm");
                String startDateOutput = startDateFormat
                        .format(startDate);

                selectedStartHour = Integer
                        .parseInt(startDateOutput.substring(0,
                                startDateOutput.indexOf(":")));
                selectedStartMinute = Integer
                        .parseInt(startDateOutput.substring(
                                startDateOutput.indexOf(":") + 1,
                                startDateOutput.length()));

            } catch (ParseException e1) {

                e1.printStackTrace();
            }

            long endTime = (((selectedStartHour * 60) + selectedStartMinute))
                    + AppDataSingleton.getInstance().getNewAppointment().appointmentTypeList
                    .get(selectedAppointmentType)
                    .getMinimumLengthMinutes();
            int hourToUse = (int) (endTime / 60);
            int minuteToUse = (int) (endTime % 60);

            // Convert to string used by array list
            String setTime = hourToUse + ":" + minuteToUse;
            DateFormat df = new SimpleDateFormat("H:mm");

            String formattedTime = "";
            try {
                Date today = df.parse(setTime);

                DateFormat formatter = new SimpleDateFormat(
                        "h:mm aaa");
                formattedTime = formatter.format(today);
            } catch (ParseException e) {
                e.printStackTrace();
            }


            textEndDate.setText(dateToUse + " " + formattedTime); // Clear this

            if (startDateDialog.isShowing())
                startDateDialog.dismiss();
        }
    };

    private OnClickListener selectEndDateTimeSaveListener = new OnClickListener() {

        @Override
        public void onClick(View v) {

            String date = "";

            Spinner timeSpinner = (Spinner) endDateDialog
                    .findViewById(R.id.dialog_appointment_end_time_spinner);

            if (timeSpinner.getSelectedItem().toString().contains("am")
                    && textStartDate.getText().toString().contains("pm")) {
                date = tomorrowsDate;
            } else {
                date = dateToUse;
            }

            textEndDate.setText(date + " "
                    + timeSpinner.getSelectedItem().toString());

            if (endDateDialog.isShowing())
                endDateDialog.dismiss();
        }
    };

    private OnClickListener selectDateTimeCancelListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (endDateDialog != null)
                if (endDateDialog.isShowing())
                    endDateDialog.dismiss();

            if (startDateDialog != null)
                if (startDateDialog.isShowing())
                    startDateDialog.dismiss();
        }
    };

    private OnClickListener selectSuggestedDateTimeCancelListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (suggestedDateDialog != null)
                if (suggestedDateDialog.isShowing())
                    suggestedDateDialog.dismiss();
        }
    };

    // TODO - This needs to lock/unlock the buttons to keep from multiple
    // attempts to submit something
    private class SubmitAppointmentTask extends BaseUiReportTask<String> {
        SubmitAppointmentTask() {
            super(ActivityAppointmentAdd.this, editMode
                    ? R.string.async_task_string_updating_appointment
                    : R.string.async_task_string_submitting_new_appointment);
        }

        @Override
        protected boolean taskBody(final String... args) throws Exception {
            if (editMode) {
                if (!mUnscheduled.isChecked()) {
                    RESTAppointment.update(textAppointmentType.getTag()
                                    .toString(), textStartDate.getText().toString(),
                            textEndDate.getText().toString(), edittextNotes
                                    .getText().toString(), textAddressLine1
                                    .getTag().toString(), mAfterHours
                                    .isChecked(),
//                            AppDataSingleton.getInstance().getCustomer().getTimeZone() != null ?
//                                    AppDataSingleton.getInstance().getCustomer().getTimeZone() :
                            timeZone,leadSource);
                } else {
                    RESTAppointment.update(textAppointmentType.getTag()
                                    .toString(),
                            textSuggestedDate.getText().toString(),
                            mUnscheduled.isChecked(),
                            priorityArray[selectedPriority], edittextNotes
                                    .getText().toString(), textAddressLine1
                                    .getTag().toString(), mAfterHours
                                    .isChecked(),leadSource);
                }
            } else if (!mUnscheduled.isChecked()) {
                RESTAppointment.add(textAppointmentType.getTag().toString(),
                        textStartDate.getText().toString(), textEndDate
                                .getText().toString(), edittextNotes.getText()
                                .toString(), textAddressLine1.getTag()
                                .toString(), mAfterHours.isChecked(),
//                         AppDataSingleton.getInstance().getCustomer().getTimeZone() != null ?
//                                AppDataSingleton.getInstance().getCustomer().getTimeZone() :
                        timeZone,leadSource);
            } else {
                RESTAppointment.add(textAppointmentType.getTag().toString(),
                        textSuggestedDate.getText().toString(), mUnscheduled
                                .isChecked(), priorityArray[selectedPriority],
                        edittextNotes.getText().toString(), textAddressLine1
                                .getTag().toString(), mAfterHours.isChecked(),leadSource);
            }
            return true;
        }

        @Override
        protected void onSuccess() {
            super.onSuccess();
            AppDataSingleton.getInstance().setNewApptStatus(true);
            Toast.makeText(ActivityAppointmentAdd.this, "Successfully Completed", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private class GetAppointmentsTask extends BaseUiReportTask<String> {

        public GetAppointmentsTask() {
            super(ActivityAppointmentAdd.this,
                    R.string.async_task_string_loading_appointment_types);
        }

        @Override
        protected void onSuccess() {
            if (editMode) {
                selectedAppointmentType = AppDataSingleton.getInstance().getAppointment()
                        .getApptTypeId();

                for (int i = 0; i < AppDataSingleton.getInstance().getNewAppointment().appointmentTypeList
                        .size(); i++) {
                    if (selectedAppointmentType == AppDataSingleton.getInstance().getNewAppointment().appointmentTypeList
                            .get(i).getId()) {
                        selectedAppointmentType = i;
                        break;
                    }
                }
            }

            setupUI();
            // setupAppointmentsList();
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
}