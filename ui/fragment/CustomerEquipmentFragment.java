package com.skeds.android.phone.business.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.skeds.android.phone.business.AsyncTasks.BaseUiReportTask;
import com.skeds.android.phone.business.AsyncTasks.ViewGalleryTask;
import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Customer;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Location;
import com.skeds.android.phone.business.Utilities.General.CommonUtilities;
import com.skeds.android.phone.business.Utilities.General.Constants;
import com.skeds.android.phone.business.Utilities.General.UserUtilitiesSingleton;
import com.skeds.android.phone.business.Utilities.Logger;
import com.skeds.android.phone.business.Utilities.REST.Objects.RESTEquipment;
import com.skeds.android.phone.business.Utilities.REST.Objects.RESTServiceCall;
import com.skeds.android.phone.business.activities.ActivityEquipmentAddEdit;
import com.skeds.android.phone.business.activities.ActivityPdfDocumentsFragment;
import com.skeds.android.phone.business.core.SkedsApplication;
import com.skeds.android.phone.business.model.Equipment;
import com.skeds.android.phone.business.model.ServiceCall;
import com.skeds.android.phone.business.ui.dialog.AlertDialogFragment;
import com.skeds.android.phone.business.util.DateUtils;
import com.skeds.android.phone.business.util.ViewUtils;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CustomerEquipmentFragment extends BasePhotoFragment implements
        AlertDialogFragment.OnValidateValuesBeforeDismissListener {
    private static final String ARG_EQUIPMENT = "equipment";
    private static final String ARG_CUSTOMER_ID = "customer_id";
    private static final String ARG_LOCATION_ID = "location_id";

    private static final String ZXING_ACTION = "com.google.zxing.client.android.SCAN";

    private static final String DAY_MONTH_PATTERN = "EEE MMM";
    private static final String DAY_PATTERN = "dd";
    private static final String TIME_PATTERN = "hh:mm aa";

    private static final int DEFAULT_ID = -1;

    private static final String SERVICE_RECORD_FRAGMENT_TAG = "service_record_tag";

    private static final String DEBUG_TAG = "[View Equipment]";

    private static final int RQ_SCAN = 30;
    private Equipment mEquipment;

    public static CustomerEquipmentFragment newInstance(final Equipment equipment, final int customerId, final int locationId) {
        final CustomerEquipmentFragment fragment = new CustomerEquipmentFragment();
        final Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_EQUIPMENT, equipment);
        bundle.putInt(ARG_CUSTOMER_ID, customerId);
        bundle.putInt(ARG_LOCATION_ID, locationId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.f_equipment_view, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final Bundle args = getArguments();
        if (args == null) {
            throw new IllegalArgumentException("No args were set");
        }

        final Customer customer = AppDataSingleton.getInstance().getCustomerById(
                args.getInt(ARG_CUSTOMER_ID, DEFAULT_ID));
        final Location location = customer.getLocationById(args.getInt(ARG_LOCATION_ID, DEFAULT_ID));

        mEquipment = args.getParcelable(ARG_EQUIPMENT);

        setupUI(customer, location);
    }

    @Override
    public void onClick(View v) {
        final Intent i;
        final int id = v.getId();
        switch (id) {
//            //decided to remove barcode
//            case R.id.barcode:
//                final Intent intent = new Intent(ZXING_ACTION);
//                intent.putExtra("SCAN_MODE", "ONE_D_MODE");
//
//                if (SystemUtils.hasActivityToHandleIntent(intent)) {
//                    startActivityForResult(intent, RQ_SCAN);
//                } else {
//                    // Potentially direct the user to the Market with a Dialog
//                    Toast.makeText(context, getString(R.string.app_string_barcode_scanner_not_found),
//                                   Toast.LENGTH_SHORT).show();
//                }
//                break;
            case R.id.new_service_record: {

                final AlertDialogFragment.DialogBuilder builder = new AlertDialogFragment.DialogBuilder(context);
                final AlertDialogFragment dialog = builder.setTitle(
                        R.string.dialog_header_service_record).setPositiveButton(
                        getString(R.string.button_string_save)).setNegativeButton(
                        getString(R.string.button_string_cancel)).setBodyLayoutId(
                        R.layout.d_service_record).setOnValidateValuesBeforeDismissListener(this).build();
                dialog.show(getFragmentManager(), SERVICE_RECORD_FRAGMENT_TAG);
                break;
            }
            case R.id.photo_gallery: {
                new ViewGalleryTask(getActivity(), 0, mEquipment.getId()).execute();
                break;
            }
            case R.id.pdf:
                i = new Intent(context, ActivityPdfDocumentsFragment.class);

                //TODO: remove these strings
                i.putExtra("equipmentId", mEquipment.getId());
                i.putExtra("templateTypeMode", Constants.PDF_EQUIPMENT_MODE);
                startActivity(i);
                break;
            case R.id.edit:
                final Activity ownerActivity = getActivity();
                i = new Intent(context, ActivityEquipmentAddEdit.class);
                i.putExtras(ownerActivity.getIntent());
                i.putExtra(ActivityEquipmentAddEdit.EXTRA_NEW_EQUIPMENT, mEquipment);
                startActivity(i);
                ownerActivity.finish();
                break;
            default:
                super.onClick(v);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == RQ_SCAN) {
            String contents = data.getStringExtra("SCAN_RESULT");
            // String format = data.getStringExtra("SCAN_RESULT_FORMAT");

            Log.d(DEBUG_TAG, contents);
            if (!CommonUtilities.isNetworkAvailable(context)) {
                Toast.makeText(context, "Network connection unavailable.", Toast.LENGTH_SHORT).show();
            } else {
                new SendCustomerBarcodeTask(getActivity(), mEquipment).execute(contents);
            }
        }
    }

    @Override
    public boolean onValidate(View fragmentView, String fragmentTag) {
        final String text = ((TextView) fragmentView.findViewById(R.id.text)).getText().toString();
        if (!TextUtils.isEmpty(text)) {
            final int position = ((Spinner) fragmentView.findViewById(R.id.spinner)).getSelectedItemPosition();
            final String[] conditions = getResources().getStringArray(R.array.equipment_condition);
            if (!CommonUtilities.isNetworkAvailable(context)) {
                Toast.makeText(context, getString(R.string.network_unavailable),
                        Toast.LENGTH_SHORT).show();
                return false;
            }
            new SubmitEquipmentServiceCallTask(getActivity(), this).execute(text, conditions[position]);
            return true;
        }

        Toast.makeText(context, getString(R.string.dialog_body_service_record_error), Toast.LENGTH_LONG).show();
        return false;
    }

    @Override
    protected String[] getAdditionalArgs() {
        return new String[]{null, String.valueOf(mEquipment.getId())};
    }

    private StringBuilder getLocationAddress(final Location location) {
        final StringBuilder locationBuf = new StringBuilder();
        if (!TextUtils.isEmpty(location.getAddress1())) {
            locationBuf.append(location.getAddress1());
            locationBuf.append(',');
            locationBuf.append(' ');
        }
        if (!TextUtils.isEmpty(location.getAddress2())) {
            locationBuf.append(location.getAddress2());
            locationBuf.append(',');
            locationBuf.append(' ');
        }
        if (!TextUtils.isEmpty(location.getCity())) {
            locationBuf.append(location.getCity());
            locationBuf.append(',');
            locationBuf.append(' ');
        }
        if (!TextUtils.isEmpty(location.getState())) {
            locationBuf.append(location.getState());
        }
        if (!TextUtils.isEmpty(location.getZip())) {
            locationBuf.append(' ');
            locationBuf.append(location.getZip());
        }

        return locationBuf;
    }

    private void setupUI(final Customer customer, final Location location) {

        final TextView name = (TextView) fragmentView.findViewById(R.id.equipment_name);
        final TextView manufacturer = (TextView) fragmentView.findViewById(R.id.manufacturer);
        final TextView textLocation = (TextView) fragmentView.findViewById(R.id.location);
        final TextView serialNumber = (TextView) fragmentView.findViewById(R.id.serial_number);
        final TextView modelNumber = (TextView) fragmentView.findViewById(R.id.model_number);
        final TextView installationDate = (TextView) fragmentView.findViewById(R.id.installation_date);
        final TextView warrantyExpirationDate = (TextView) fragmentView.findViewById(R.id.warranty_expiration_date);
        final TextView laborWarrantyDate = (TextView) fragmentView.findViewById(R.id.labor_warranty_date);
        final TextView nextServiceCallDate = (TextView) fragmentView.findViewById(R.id.next_service_call_date);
        final TextView filter = (TextView) fragmentView.findViewById(R.id.filter);
        final TextView contractHolder = (TextView) fragmentView.findViewById(R.id.warranty_contract_holder);
        final TextView contractNumber = (TextView) fragmentView.findViewById(R.id.warranty_contract_number);
        final TextView customCode = (TextView) fragmentView.findViewById(R.id.custom_code);

        final View buttonAttachBarcode = fragmentView.findViewById(R.id.barcode);
        final ViewGroup layoutServiceCalls = (ViewGroup) fragmentView.findViewById(R.id.service_call_container);

        fragmentView.findViewById(R.id.new_service_record).setOnClickListener(this);
        fragmentView.findViewById(R.id.edit).setOnClickListener(this);
        fragmentView.findViewById(R.id.photo_gallery).setOnClickListener(this);
        fragmentView.findViewById(R.id.pdf).setOnClickListener(this);

        buttonAttachBarcode.setOnClickListener(this);
        buttonAttachBarcode.setEnabled(UserUtilitiesSingleton.getInstance().user.usesBarcodesForEquipment());

        name.setText(mEquipment.getName());
        serialNumber.setText(mEquipment.getSerialNumber());
        modelNumber.setText(mEquipment.getModelNumber());

        ViewUtils.setupText(mEquipment.getManufacturerName(), manufacturer);
        setupText(mEquipment.getFilter(), getString(R.string.filter), filter);

        setupText(mEquipment.getWarrantyContractHolder(), getString(R.string.warranty_contract_holder), contractHolder);
        setupText(mEquipment.getWarrantyContractNumber(), getString(R.string.warranty_contract_number), contractNumber);
        setupText(DateUtils.removeLeadingZeroFromDate(mEquipment.getInstallationDate(),
                        Constants.DATE_FORMAT_WITHOUT_HOURS),
                getString(R.string.equipment_install_date), installationDate
        );
        setupText(DateUtils.removeLeadingZeroFromDate(mEquipment.getWarrantyExpirationDate(),
                        Constants.DATE_FORMAT_WITHOUT_HOURS),
                getString(R.string.warranty_expiration), warrantyExpirationDate
        );
        setupText(DateUtils.removeLeadingZeroFromDate(mEquipment.getLaborWarrantyExpirationDate(),
                        Constants.DATE_FORMAT_WITHOUT_HOURS),
                getString(R.string.labor_warranty),
                laborWarrantyDate
        );

        if (TextUtils.isEmpty(mEquipment.getCustomCode())) {
            customCode.setVisibility(View.GONE);
        } else {
            customCode.setText(getString(R.string.equipment_custom_code, mEquipment.getCustomCode()));
        }

        final String appointmentTypeName = mEquipment.getAppointmentTypeName();
        final String callDate = DateUtils.removeLeadingZeroFromDate(mEquipment.getNextServiceCallDate(),
                Constants.DATE_FORMAT_WITHOUT_HOURS);
        if (TextUtils.isEmpty(appointmentTypeName)) {
            setupText(callDate, getString(R.string.next_service_call), nextServiceCallDate);
        } else {
            nextServiceCallDate.setText(getString(R.string.lines_with_space, callDate, appointmentTypeName));
        }

        StringBuilder locationBuf = new StringBuilder(getString(R.string.not_available));
        if (location == null) {
            final Location aLocation = customer.getLocationById(mEquipment.getLocationId());
            if (aLocation != null) {
                locationBuf = getLocationAddress(aLocation);
            }
        } else {
            locationBuf = getLocationAddress(location);
        }
        textLocation.setText(locationBuf);

        if (locationBuf.length() <= 0) {
            textLocation.setText(mEquipment.getLocationAddress());
        }

        setCustomInfo();

        if (mEquipment.getServiceCallList() != null && !mEquipment.getServiceCallList().isEmpty()) {

            final LayoutInflater inflater = LayoutInflater.from(context);
            final List<ServiceCall> serviceCallList = mEquipment.getServiceCallList();
            for (ServiceCall serviceCall : serviceCallList) {
                addInitServiceCallView(layoutServiceCalls, inflater, serviceCall);
            }
        }
    }

    private void setupText(final String toValidate, final String toSet, TextView view) {
        if (TextUtils.isEmpty(toValidate)) {
            view.setVisibility(View.GONE);
        } else {
            view.setText(toSet + ' ' + toValidate);
        }
    }

    private void addInitServiceCallView(ViewGroup layoutServiceCalls, LayoutInflater inflater, ServiceCall serviceCall) {
        final View row = inflater.inflate(R.layout.v_service_record, layoutServiceCalls, false);
        final TextView serviceCallLabel = (TextView) row.findViewById(R.id.primary_label);
        final TextView serviceCallDayMonth = (TextView) row.findViewById(R.id.month);
        final TextView serviceCallDayDate = (TextView) row.findViewById(R.id.day);
        final TextView serviceCallTime = (TextView) row.findViewById(R.id.time);
        final TextView serviceCallSecondaryLabel = (TextView) row.findViewById(R.id.secondary_label);
        if (serviceCall.getCondition() != null) {
            ((TextView) row.findViewById(R.id.condition_label)).setText(
                    getString(R.string.service_call_condition, serviceCall.getCondition()));
        }
        serviceCallLabel.setText(serviceCall.getDescription());
        serviceCallSecondaryLabel.setText(serviceCall.getTechnicianName());

        final SimpleDateFormat df = new SimpleDateFormat(Constants.DATE_FORMAT_WITH_HOURS);
        try {
            final Date serviceCallDate = df.parse(serviceCall.getDate());
            //apply day month pattern
            df.applyPattern(DAY_MONTH_PATTERN);
            serviceCallDayMonth.setText(df.format(serviceCallDate).replace("AM", "am").replace("PM", "pm"));

            //apply day pattern
            df.applyPattern(DAY_PATTERN);
            serviceCallDayDate.setText(df.format(serviceCallDate).replace("AM", "am").replace("PM", "pm"));

            //apply time format
            df.applyPattern(TIME_PATTERN);
            serviceCallTime.setText(df.format(serviceCallDate).replace("AM", "am").replace("PM", "pm"));
        } catch (Exception e) {
            Logger.err("Can't parse service call date." + e.getMessage());
        }
        layoutServiceCalls.addView(row);
    }

    private void setCustomInfo() {
        final TextView customInfo1 = (TextView) fragmentView.findViewById(R.id.info1);
        final TextView customInfo2 = (TextView) fragmentView.findViewById(R.id.info2);
        final TextView customInfo3 = (TextView) fragmentView.findViewById(R.id.info3);

        setupCustomInfoText(AppDataSingleton.getInstance().getEquipmentCustomFieldName1(), mEquipment.getCustomInfo1(),
                customInfo1);
        setupCustomInfoText(AppDataSingleton.getInstance().getEquipmentCustomFieldName2(), mEquipment.getCustomInfo2(),
                customInfo2);
        setupCustomInfoText(AppDataSingleton.getInstance().getEquipmentCustomFieldName3(), mEquipment.getCustomInfo2(),
                customInfo3);
    }

    private void setupCustomInfoText(final CharSequence toValidate, final CharSequence toSet, TextView view) {
        if (TextUtils.isEmpty(toValidate)) {
            view.setVisibility(View.GONE);
        } else {
            view.setText(getCustomInfoString(toValidate, toSet));
        }
    }

    private String getCustomInfoString(final CharSequence fieldName, final CharSequence customInfo) {
        return getString(R.string.equipment_custom_info, fieldName, TextUtils.isEmpty(customInfo) ? "" : customInfo);
    }

    //    TODO - Lock additional button press
    private class SubmitEquipmentServiceCallTask extends BaseEquipmentTask<String> {
        private WeakReference<CustomerEquipmentFragment> mFragmentRef;

        private String mCondition;
        private String mDesc;

        SubmitEquipmentServiceCallTask(final Activity activity, final CustomerEquipmentFragment fragment) {
            super(activity, R.string.async_task_string_submitting_service_record, fragment.mEquipment);
            mFragmentRef = new WeakReference<CustomerEquipmentFragment>(fragment);
        }

        @Override
        protected void onSuccess() {
            final ServiceCall serviceCall = new ServiceCall();
            serviceCall.setDescription(mDesc);
            serviceCall.setCondition(mCondition);
            serviceCall.setTechnicianName(UserUtilitiesSingleton.getInstance().user.getFirstName()
                    + ' ' + UserUtilitiesSingleton.getInstance().user.getLastName());

            serviceCall.setDate(android.text.format.DateFormat.format(Constants.DATE_FORMAT_WITH_HOURS,
                    Calendar.getInstance().getTime()).toString());
            mEquipment.getServiceCallList().add(serviceCall);

            //send broadcast that service call was added
            final Intent intent = new Intent(ActivityEquipmentAddEdit.ACTION_EDIT_EQUIPMENT_FINISHED);
            intent.putExtra(ActivityEquipmentAddEdit.EXTRA_NEW_EQUIPMENT, mEquipment);
            LocalBroadcastManager.getInstance(SkedsApplication.getContext()).sendBroadcast(intent);

            final CustomerEquipmentFragment fragment = mFragmentRef.get();
            if (fragment != null) {
                fragment.addInitServiceCallView((ViewGroup) fragmentView.findViewById(R.id.service_call_container),
                        LayoutInflater.from(context), serviceCall);
            }

            mFragmentRef = null;
        }

        @Override
        protected boolean taskBody(final String... args) throws Exception {
            int partId = UserUtilitiesSingleton.getInstance().user.getServiceProviderId();
            if (partId == 0) {
                partId = AppDataSingleton.getInstance().getServiceProviderId();
            }

            mDesc = args[0];
            mCondition = args[1];

            RESTServiceCall.add(mEquipment.getId(), 0, partId, mDesc, mCondition.toUpperCase());
            return true;
        }
    }

    private static class SendCustomerBarcodeTask extends BaseEquipmentTask<String> {

        SendCustomerBarcodeTask(final Activity activity, final Equipment equipment) {
            super(activity, R.string.async_task_string_pairing_location, equipment);
        }

        @Override
        protected boolean taskBody(final String... args) throws Exception {
            RESTEquipment.attachBarcode(mEquipment.getId(), args[0]);
            return true;
        }
    }

    private abstract static class BaseEquipmentTask<T> extends BaseUiReportTask<T> {
        final Equipment mEquipment;

        BaseEquipmentTask(final Activity activity, final int taskNameId, final Equipment equipment) {
            super(activity, taskNameId);

            mEquipment = equipment;
        }
    }
}