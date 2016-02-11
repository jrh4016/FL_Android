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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.skeds.android.phone.business.AsyncTasks.BaseUiReportTask;
import com.skeds.android.phone.business.Custom.AccountMenu;
import com.skeds.android.phone.business.Custom.QuickAction;
import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.LeadSource;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.NewAppointment;
import com.skeds.android.phone.business.Utilities.REST.Objects.RESTAppointment;
import com.skeds.android.phone.business.Utilities.REST.Objects.RESTAppointmentTypeList;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ActivityUnscheduledAppointmentAddEdit extends BaseSkedsActivity {

    private int selectedLocation;
    private int selectedAppointmentType;

    private LinearLayout headerLayout;
    private ImageView headerButtonUser;
    private ImageView headerButtonBack;
    private TextView headerButtonSave;

    private ScrollView scrollView;

    private int selectedPriority = -1;

    private TextView textCompanyName;
    private TextView textAddressLine1;
    private TextView textAddressLine2;
    private TextView textAppointmentType;
    private TextView textSuggestedDate;
    private TextView textPriority;

    private EditText edittextNotes;

    private TextView buttonSelectLocation;
    private TextView buttonSelectAppointmentType;
    private TextView buttonSetSuggestedDate;
    private TextView buttonSelectAppointmentPriority;

    private Dialog appointmentTypesDialog;
    private TextView appointmentTypesDialogSave, appointmentTypesDialogCancel;

    private Dialog locationsDialog;
    private TextView locationsDialogSave, locationsDialogCancel;

    private Dialog suggestedDateDialog;
    private TableRow suggestedDateTab;

    private String dateToUse = "";

    private Dialog appointmentPriorityDialog;
    private TextView appointmentPriorityDialogSave,
            appointmentPriorityDialogCancel;

    private final String[] priorityArray = new String[]{"LOW", "MEDIUM", "HIGH"};

    private Activity mActivity;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_unscheduled_appointment_add);

        mActivity = this;
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

        scrollView = (ScrollView) findViewById(R.id.activity_unscheduled_appointment_add_scrollview);

        textCompanyName = (TextView) findViewById(R.id.activity_unscheduled_appointment_add_textview_company_name);
        textAddressLine1 = (TextView) findViewById(R.id.activity_unscheduled_appointment_add_textview_location_1);
        textAddressLine2 = (TextView) findViewById(R.id.activity_unscheduled_appointment_add_textview_location_2);
        textAppointmentType = (TextView) findViewById(R.id.activity_unscheduled_appointment_add_textview_appointment_type);
        textSuggestedDate = (TextView) findViewById(R.id.activity_unscheduled_appointment_add_textview_start_date);
        edittextNotes = (EditText) findViewById(R.id.activity_unscheduled_appointment_add_edittext_notes);

        buttonSelectLocation = (TextView) findViewById(R.id.activity_unscheduled_appointment_add_button_select_location);
        buttonSelectAppointmentType = (TextView) findViewById(R.id.activity_unscheduled_appointment_add_button_select_appointment_type);
        buttonSetSuggestedDate = (TextView) findViewById(R.id.activity_unscheduled_appointment_add_button_start_date);

        textPriority = (TextView) findViewById(R.id.activity_add_appointment_textview_appointment_priority);
        buttonSelectAppointmentPriority = (TextView) findViewById(R.id.activity_add_appointment_button_select_appointment_priority);
        buttonSelectAppointmentPriority.setOnClickListener(priorityListener);

        suggestedDateTab = (TableRow) findViewById(R.id.unscheduled_appointment_suggested_date_field);

        new GetAppointmentsTask(mActivity).execute();
    }


    private void setupUI() {

        // Keeps the keyboard from popping-up on the notes textbox
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        headerButtonBack.setOnClickListener(goBackListener);
        headerButtonSave.setOnClickListener(saveListener);

        buttonSelectLocation.setOnClickListener(selectLocationListener);
        buttonSelectAppointmentType
                .setOnClickListener(selectAppointmentTypeListener);
        buttonSetSuggestedDate.setOnClickListener(setSuggestedDateListener);

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((resultCode == LeadSourceListActivity.LEAD_SOURCE)&&(data!=null)) {
            LeadSource leadSource = (LeadSource) data.getSerializableExtra(LeadSourceListActivity.key);
            if (leadSource != null) {

                AppDataSingleton.getInstance().getCustomer().setLeadSourceId(leadSource.getId());
                ((TextView)findViewById(R.id.lead_source_text)).setText(leadSource.getName());
            }
        }
    }

    private OnClickListener goBackListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            onBackPressed();
        }
    };

    private OnClickListener priorityListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            setupPriorityList();
        }
    };

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

    private OnClickListener priorityTypesSaveListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            TextView selectedText = (TextView) appointmentPriorityDialog
                    .findViewById(R.id.dialog_list_textview_selected);

            if ("None".equals(selectedText.getText())) {
                textPriority.setText(priorityArray[0]);
                selectedPriority = 0;
            } else {

                textPriority
                        .setText(priorityArray[selectedPriority]);
            }

            if (selectedPriority == 0 || selectedPriority == 1) {
                suggestedDateTab.setVisibility(View.VISIBLE);
            } else {
                suggestedDateTab.setVisibility(View.GONE);
                textSuggestedDate.setText("");
            }


            if (appointmentPriorityDialog.isShowing())
                appointmentPriorityDialog.dismiss();
        }
    };

    private OnClickListener priorityTypesCancelListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (appointmentPriorityDialog.isShowing())
                appointmentPriorityDialog.dismiss();
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

    private void setupLocationsList() {

        locationsDialog = new Dialog(mContext);
        locationsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        locationsDialog.setContentView(R.layout.dialog_layout_list);

        ListView mListView = (ListView) locationsDialog
                .findViewById(R.id.dialog_list_listview);

        mListView.setAdapter(null);

        Drawable drawableDivider = new ColorDrawable(
                android.R.color.transparent);
        mListView.setBackgroundColor(mContext.getResources().getColor(
                android.R.color.transparent));
        mListView.setCacheColorHint(Color.rgb(62, 81, 101));

        mListView.setVerticalScrollBarEnabled(false);
        mListView.setDivider(drawableDivider);
        mListView.setDividerHeight(6); // Pixel spacing in-between items

        mListView.setPadding(12, 0, 12, 0);

        String[] sizeArray = new String[AppDataSingleton.getInstance().getCustomer().locationList
                .size()];

        ArrayAdapter<String> adapter = new CustomLocationAdapter(mContext,
                R.layout.row_equipment_item, sizeArray);
        mListView.setAdapter(adapter);

        mListView.setTextFilterEnabled(true);
        mListView.setOnItemClickListener(locationListListener);

        locationsDialogSave = (TextView) locationsDialog
                .findViewById(R.id.dialog_list_button_save);
        locationsDialogCancel = (TextView) locationsDialog
                .findViewById(R.id.dialog_list_button_cancel);

        locationsDialogSave.setOnClickListener(locationSaveListener);
        locationsDialogCancel.setOnClickListener(locationCancelListener);

        locationsDialog.show();
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

    private OnItemClickListener locationListListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View v, int position,
                                long id) {

            TextView selectedText = (TextView) locationsDialog
                    .findViewById(R.id.dialog_list_textview_selected);
            selectedText.setText(AppDataSingleton.getInstance().getCustomer().locationList.get(
                    (int) id).getAddress1());
            selectedText.setTag(AppDataSingleton.getInstance().getCustomer().locationList
                    .get((int) id).getId());

            selectedLocation = (int) id;
        }
    };

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

            textSuggestedDate.setText("");

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

    private OnClickListener locationSaveListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            TextView selectedText = (TextView) locationsDialog
                    .findViewById(R.id.dialog_list_textview_selected);

            if ("None".equals(selectedText.getText())) {
                // set 0's
                selectedLocation = 0;
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
            } else {
                // regular
                textAddressLine1.setText(AppDataSingleton.getInstance().getCustomer().locationList
                        .get(selectedLocation).getAddress1()
                        + " "
                        + AppDataSingleton.getInstance().getCustomer().locationList.get(
                        selectedLocation).getAddress2());
                textAddressLine1.setTag(selectedText.getTag());

                textAddressLine2.setText(AppDataSingleton.getInstance().getCustomer().locationList
                        .get(selectedLocation).getCity()
                        + ", "
                        + AppDataSingleton.getInstance().getCustomer().locationList.get(
                        selectedLocation).getState()
                        + " "
                        + AppDataSingleton.getInstance().getCustomer().locationList.get(
                        selectedLocation).getZip());
            }

            if (locationsDialog.isShowing())
                locationsDialog.dismiss();

        }
    };

    private OnClickListener locationCancelListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (locationsDialog.isShowing())
                locationsDialog.dismiss();
        }
    };

    private OnClickListener saveListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (textAppointmentType.getTag() == null || textAddressLine1.getTag() == null) {
                Toast.makeText(mContext,
                        "Make sure to fill in all required fields",
                        Toast.LENGTH_LONG).show();
                return;
            }

            if (textSuggestedDate.getText().toString().isEmpty() && suggestedDateTab.getVisibility() == View.VISIBLE) {
                Toast.makeText(mContext,
                        "Make sure to fill in all required fields",
                        Toast.LENGTH_LONG).show();
                return;
            }
            new SubmitAppointmentTask().execute();
        }
    };

    private OnClickListener selectLocationListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            setupLocationsList();
        }
    };

    private OnClickListener selectAppointmentTypeListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            setupAppointmentsList();
        }
    };

    private OnClickListener setSuggestedDateListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (textAppointmentType.getTag() != null) {
                suggestedDateDialog = new Dialog(mContext);
                suggestedDateDialog
                        .requestWindowFeature(Window.FEATURE_NO_TITLE);
                suggestedDateDialog
                        .setContentView(R.layout.dialog_layout_date_picker);

                TextView saveButton = (TextView) suggestedDateDialog
                        .findViewById(R.id.dialog_date_picker_button_save);
                TextView cancelButton = (TextView) suggestedDateDialog
                        .findViewById(R.id.dialog_date_picker_button_cancel);

                ArrayList<String> timesForSpinner = new ArrayList<String>();

                long thisTime = 0;
                while (thisTime < NewAppointment.getLatestTimeMinutes()) {

                    // Figure out to the hour/minute setup
                    int hourToUse = (int) (thisTime / 60);
                    int minuteToUse = (int) (thisTime % 60);

                    // Convert to string used by array list
                    String setTime = hourToUse + ":" + minuteToUse;
                    DateFormat df = new SimpleDateFormat("HH:mm");

                    String formattedTime = "";
                    try {
                        Date today = df.parse(setTime);

                        DateFormat formatter = new SimpleDateFormat("h:mm aaa");
                        formattedTime = formatter.format(today).replace("am", "AM").replace("pm", "PM");
                        ;
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    // Push to array list
                    timesForSpinner.add(formattedTime);

                    // Add the increment and start over
                    thisTime += AppDataSingleton.getInstance().getNewAppointment().appointmentTypeList
                            .get(selectedAppointmentType).getIncrementMinutes();

                }


                saveButton
                        .setOnClickListener(selectSuggestedDateTimeSaveListener);
                cancelButton.setOnClickListener(selectDateTimeCancelListener);
                suggestedDateDialog.show();
            } else {
                Toast.makeText(mContext,
                        "You must select an appointment type first.",
                        Toast.LENGTH_LONG).show();
            }
        }
    };

    private OnClickListener selectSuggestedDateTimeSaveListener = new OnClickListener() {

        @Override
        public void onClick(View v) {

            DatePicker dp = (DatePicker) suggestedDateDialog
                    .findViewById(R.id.dialog_date_picker_datepicker);


            dateToUse = dp.getMonth() + 1 + "/" + dp.getDayOfMonth() + "/"
                    + dp.getYear();

            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            Calendar c = Calendar.getInstance();
            try {
                c.setTime(sdf.parse(dateToUse));
            } catch (ParseException e) {

                e.printStackTrace();
            }

            textSuggestedDate.setText(dateToUse);

            if (suggestedDateDialog.isShowing())
                suggestedDateDialog.dismiss();
        }
    };

    private OnClickListener selectDateTimeCancelListener = new OnClickListener() {

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
            super(ActivityUnscheduledAppointmentAddEdit.this,
                    R.string.async_task_string_submitting_new_appointment);
            setAutocloseOnSuccess(true);
        }

        @Override
        protected boolean taskBody(final String... args) throws Exception {
            RESTAppointment.addFromEstimate(ActivityEstimateView.estimateId,
                    Integer.parseInt(textAppointmentType.getTag().toString()),
                    textSuggestedDate.getText().toString(),
                    edittextNotes.getText().toString(),
                    Integer.parseInt(textAddressLine1.getTag().toString()),
                    priorityArray[selectedPriority]
            );
            return true;
        }

        @Override
        protected void onSuccess() {
            super.onSuccess();
            Toast.makeText(mContext, "Succesfully Created", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private class GetAppointmentsTask extends BaseUiReportTask<String> {

        GetAppointmentsTask(Activity activity) {
            super(ActivityUnscheduledAppointmentAddEdit.this,
                    R.string.async_task_string_loading_appointment_types);
        }

        @Override
        protected void onSuccess() {
            setupUI();
            // setupAppointmentsList();
        }

        @Override
        protected boolean taskBody(final String... args) throws Exception {
            RESTAppointmentTypeList.query();
            return true;
        }
    }

    public class CustomLocationAdapter extends ArrayAdapter<String> {

        public CustomLocationAdapter(Context context, int textViewResourceId,
                                     String[] objects) {
            super(context, textViewResourceId, objects);
            this.notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.row_equipment_item, parent,
                    false);

            TextView label = (TextView) row
                    .findViewById(R.id.equipmentListItem);
            label.setText(AppDataSingleton.getInstance().getCustomer().locationList.get(position)
                    .getAddress1());
            label.setTag(AppDataSingleton.getInstance().getCustomer().locationList.get(position)
                    .getId());

            return row;
        }

    }

    public class CustomAppointmentAdapter extends ArrayAdapter<String> {

        public CustomAppointmentAdapter(Context context,
                                        int textViewResourceId, String[] objects) {
            super(context, textViewResourceId, objects);
            this.notifyDataSetChanged();
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