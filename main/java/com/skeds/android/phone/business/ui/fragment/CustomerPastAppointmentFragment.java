package com.skeds.android.phone.business.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.skeds.android.phone.business.AsyncTasks.BaseUiReportTask;
import com.skeds.android.phone.business.AsyncTasks.ViewGalleryTask;
import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Appointment;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Participant;
import com.skeds.android.phone.business.Utilities.General.CommonUtilities;
import com.skeds.android.phone.business.Utilities.General.Constants;
import com.skeds.android.phone.business.Utilities.General.UserUtilitiesSingleton;
import com.skeds.android.phone.business.Utilities.IntentExtras;
import com.skeds.android.phone.business.Utilities.REST.Objects.RESTInvoice;
import com.skeds.android.phone.business.activities.ActivityAppointmentCommentsView;
import com.skeds.android.phone.business.activities.ActivityAppointmentOnTruckListView;
import com.skeds.android.phone.business.activities.ActivityEstimateListFragment;
import com.skeds.android.phone.business.activities.ActivityEstimateView;
import com.skeds.android.phone.business.activities.ActivityInvoiceSingleFragment;
import com.skeds.android.phone.business.util.DateUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class CustomerPastAppointmentFragment extends BasePhotoFragment {

    private static final String TAG = CustomerPastAppointmentFragment.class.getName();

    private static final String ARG_APPOINTMENT = "appointment";

    private static final String DAY_MONTH_PATTERN = "EE MMM, dd yyyy";

    private Appointment mLastAppt;

    public static Fragment newInstance(final Appointment appointment) {
        if (appointment == null) {
            throw new IllegalArgumentException("Appointment can't be null!");
        }
        final Fragment fragment = new CustomerPastAppointmentFragment();
        final Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_APPOINTMENT, appointment);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.f_past_appointment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final Bundle args = getArguments();
        if (args == null || !args.containsKey(ARG_APPOINTMENT)) {
            throw new IllegalArgumentException("Args can't be null and should contain appointment in the bundle.");
        }
        mLastAppt = (Appointment) args.getSerializable(ARG_APPOINTMENT);

        setupUI();
    }

    private void setupUI() {

        // These aren't always set and it causes crashes
        final String firstName = AppDataSingleton.getInstance().getCustomer().getFirstName();
        final String lastName = AppDataSingleton.getInstance().getCustomer().getLastName();
        final String orgName = AppDataSingleton.getInstance().getCustomer().getOrganizationName();

        final String customerName;
        if ((TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName)) && !TextUtils.isEmpty(orgName)) {
            customerName = orgName;
        } else if (TextUtils.isEmpty(orgName)) {
            customerName = getString(R.string.lines_with_space, firstName, lastName);
        } else {
            customerName = orgName + '\n' + getString(R.string.lines_with_space, firstName, lastName);
        }

        final String apptTypeName = mLastAppt.getApptTypeName();
        final String appointmentTypeString;
        if (TextUtils.isEmpty(apptTypeName)) {
            appointmentTypeString = getString(R.string.placeholder_string_appointment_type);
        } else {
            appointmentTypeString = apptTypeName.trim();
        }

        final String location = mLastAppt.getLocationValue();
        String eventLocationString;
        if (TextUtils.isEmpty(location)) {
            eventLocationString = getString(R.string.appt_type_unknown);
        } else {
            eventLocationString = location;

            final String eventA, eventB;
            final int index = eventLocationString.indexOf(',');
            eventA = eventLocationString.substring(0, index + 1);
            eventB = eventLocationString.substring(index + 1, eventLocationString.length());

            final SpannableString contentA = new SpannableString(eventA.trim());
            contentA.setSpan(new UnderlineSpan(), 0, contentA.length(), 0);

            final SpannableString contentB = new SpannableString(eventB.trim());
            contentB.setSpan(new UnderlineSpan(), 0, contentB.length(), 0);

            eventLocationString = contentA.toString() + '\n' + contentB.toString();
        }

        ((TextView) fragmentView.findViewById(R.id.address)).setText(
                eventLocationString);

        String eventServiceProviderString = null;
        if (!mLastAppt.getParticipantList().isEmpty()) {
            final StringBuilder builderServiceProvider = new StringBuilder();
            final List<Participant> participants = mLastAppt.getParticipantList();
            for (final Participant participant : participants) {
                builderServiceProvider.append(participant.getFirstName());
                builderServiceProvider.append(' ');
                builderServiceProvider.append(participant.getLastName());
                builderServiceProvider.append(' ');
                builderServiceProvider.append('-');
                builderServiceProvider.append(' ');
                builderServiceProvider.append(participant.getTypeName());
                builderServiceProvider.append('\n');
                builderServiceProvider.append('\n');
            }
            eventServiceProviderString = builderServiceProvider.toString();
        }

        ((TextView) fragmentView.findViewById(R.id.customer_name)).setText(customerName);
        ((TextView) fragmentView.findViewById(R.id.activity_appointment_textview_appointment_type)).setText(
                appointmentTypeString);
        ((TextView) fragmentView.findViewById(R.id.participants)).setText(
                eventServiceProviderString);

        final TextView addedEquipment = (TextView) fragmentView.findViewById(
                R.id.added_equipment);

        if (mLastAppt.getEquipmentList().isEmpty()) {
            addedEquipment.setVisibility(View.GONE);
        } else {
            addedEquipment.setVisibility(View.VISIBLE);
            StringBuilder builderAddedEquipment = new StringBuilder();
            for (final Appointment.pieceOfEquipment equipment : mLastAppt.getEquipmentList()) {
                builderAddedEquipment.append(
                        getString(R.string.name_model_serial, equipment.getName(), equipment.getModelNumber(),
                                equipment.getSerialNumber()));
            }
            addedEquipment.setText(builderAddedEquipment.toString());
        }

        ((TextView) fragmentView.findViewById(R.id.service_aggrement)).setText(mLastAppt.getSelectedAgreementName());

        String startTime = DateUtils.convertFromPatternToPattern(

                mLastAppt.getDate() + " " + mLastAppt.getStartTime(),
                "MM/dd/yyyy hh:mm aaa",
                "MM/dd/yyyy h:mm aaa",
                UserUtilitiesSingleton.getInstance().user.getTimeZone(),
                TimeZone.getDefault()
        );

        String endTime = DateUtils.convertFromPatternToPattern(

                mLastAppt.getDate() + " " + mLastAppt.getEndTime(),
                "MM/dd/yyyy hh:mm aaa",
                "MM/dd/yyyy h:mm aaa",
                UserUtilitiesSingleton.getInstance().user.getTimeZone(),
                TimeZone.getDefault()
        );

        ((TextView) fragmentView.findViewById(R.id.date)).setText(startTime + '-' + endTime);

        ((TextView) fragmentView.findViewById(R.id.notes)).setText(
                mLastAppt.getNotes());


        final Button viewInvoice = (Button) fragmentView.findViewById(R.id.view_invoice);
        if (mLastAppt.getInvoiceId() <= 0) {
            viewInvoice.setEnabled(false);
        }

        viewInvoice.setOnClickListener(this);

        // Scroll to top of view
        final ScrollView scrollView = (ScrollView) fragmentView.findViewById(R.id.scroll);
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_UP);
            }
        });

        fragmentView.findViewById(R.id.view_comments).setOnClickListener(this);
        fragmentView.findViewById(R.id.view_estimate).setOnClickListener(this);
        fragmentView.findViewById(R.id.on_truck).setOnClickListener(this);
        fragmentView.findViewById(R.id.view_gallery).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.view_invoice:
                if (!CommonUtilities.isNetworkAvailable(context)) {
                    Toast.makeText(context, getString(R.string.network_unavailable), Toast.LENGTH_SHORT).show();
                    return;
                }
                new GetInvoiceTask(getActivity()).execute(mLastAppt.getInvoiceId());
                break;
            case R.id.view_comments:
                //TODO: what the fuck here, public static variable is used from another class, oh my god!!!!
                ActivityAppointmentCommentsView.previousActivity = ActivityAppointmentCommentsView.PREVIOUS_ACTIVITY_APPOINTMENT_PREVIOUS;
                startActivity(new Intent(context, ActivityAppointmentCommentsView.class));
                break;

            case R.id.view_estimate:
                //TODO: what the fuck here again, public static variable is used from another class, oh my god!!!!
                ActivityEstimateView.closeAppointmentOnComplete = false;
                AppDataSingleton.getInstance().setEstimateListViewMode(
                        Constants.ESTIMATE_LIST_VIEW_FROM_PAST_APPOINTMENT);
                final Intent i = new Intent(context, ActivityEstimateListFragment.class);
                i.putExtra(IntentExtras.LOCATION_ID, AppDataSingleton.getInstance().getAppointment().getLocationId());
                startActivity(i);
                break;
            case R.id.on_truck:
                startActivity(new Intent(context, ActivityAppointmentOnTruckListView.class));
                break;
            case R.id.view_gallery:
                new ViewGalleryTask(getActivity(), mLastAppt.getId(), 0).execute();
                break;
            default:
                super.onClick(v);
        }
    }

    @Override
    protected String[] getAdditionalArgs() {
        return new String[]{String.valueOf(mLastAppt.getId()), null};
    }

    private String dateGenerator(String theDate) {
        String formattedDate = "";

        theDate = theDate.trim();
        DateFormat df = new SimpleDateFormat("EEEE, MM/dd/yyyy");
        DateFormat df2 = new SimpleDateFormat("MM/dd/yyyy");

        try {
            Date today = df.parse(theDate);

            DateFormat formatter = new SimpleDateFormat("EEEE, MMMM d, yyyy");
            formattedDate = formatter.format(today);
        } catch (ParseException e) {
            try {
                Date today = df2.parse(theDate);

                DateFormat formatter = new SimpleDateFormat("EEEE, MMMM d, yyyy");
                formattedDate = formatter.format(today);
            } catch (ParseException e2) {
                Log.e("FieldLocate", "Could not parse date: " + theDate);
            }
        }
        return formattedDate;
    }

    private static final class GetInvoiceTask extends BaseUiReportTask<Integer> {
        GetInvoiceTask(final Activity activity) {
            super(activity, R.string.async_task_string_loading_invoice);

        }

        @Override
        protected void onSuccess() {
            // TODO - Pass as intent bundle
            //TODO: what the fuck here again, public static variable is used from another class, oh my god!!!!
            InvoiceFragment.isReadOnly = true;
            AppDataSingleton.getInstance().setInvoiceViewMode(Constants.INVOICE_VIEW_FROM_PAST_APPOINTMENT);

            final Bundle bundle = new Bundle();
            bundle.putInt(InvoiceFragment.INVOICE_ID, AppDataSingleton.getInstance().getInvoice().getId());
            launchActivity(bundle, ActivityInvoiceSingleFragment.class);
        }

        @Override
        protected boolean taskBody(final Integer... args) throws Exception {
            RESTInvoice.query(args[0]);
            return true;
        }
    }
}
