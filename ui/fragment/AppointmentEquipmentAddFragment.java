package com.skeds.android.phone.business.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.skeds.android.phone.business.AsyncTasks.BaseUiReportTask;
import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.Logger;
import com.skeds.android.phone.business.Utilities.REST.Objects.RESTEquipment;
import com.skeds.android.phone.business.activities.ActivityEquipmentAddEdit;
import com.skeds.android.phone.business.activities.CustomerEquipmentActivity;
import com.skeds.android.phone.business.core.SkedsApplication;
import com.skeds.android.phone.business.core.async.BaseAsyncTaskLoader;
import com.skeds.android.phone.business.data.loader.CustomerEquipmentLoader;
import com.skeds.android.phone.business.model.CustomerEquipment;
import com.skeds.android.phone.business.model.Equipment;

import java.util.ArrayList;
import java.util.List;

public class AppointmentEquipmentAddFragment extends BaseSkedsFragment implements LoaderManager.LoaderCallbacks<CustomerEquipment>, OnClickListener {

    private static final String KEY_CUSTOMER_EQUIPMENTS = "customer_equipments";
    private static final int EQUIPMENT_LOADER_ID = 10;

    private List<Integer> mAddedEquipment = new ArrayList<Integer>();

    private MyCustomAdapter mEquipmentAdaptor;
    private List<Equipment> mEquipments;

    private View mProgressBar;
    private ListView mEquipmentsListView;

    private LocalBroadcastManager mLocalBroadcastManager;
    private BroadcastReceiver mSyncEquipmentsReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // create local broadcast receiver to have ability to see if sync is in progress
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(SkedsApplication.getContext());
        mSyncEquipmentsReceiver = new SyncEquipmentList();

        // register local broadcast receiver
        final IntentFilter filter = new IntentFilter();
        filter.addAction(ActivityEquipmentAddEdit.ACTION_EDIT_EQUIPMENT_FINISHED);
        mLocalBroadcastManager.registerReceiver(mSyncEquipmentsReceiver, filter);
    }

    @Override
    public void onDestroy() {
        mLocalBroadcastManager.unregisterReceiver(mSyncEquipmentsReceiver);
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_add_appointment_equipment_view,
                container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final View fragmentView = getView();

        fragmentView.findViewById(R.id.btn_save_equipment).setOnClickListener(this);
        mEquipmentsListView = (ListView) fragmentView.findViewById(R.id.equipment);
        mProgressBar = fragmentView.findViewById(R.id.progress);

        if (mEquipmentAdaptor == null) {
            if (savedInstanceState != null) {
                mEquipments = savedInstanceState.getParcelableArrayList(KEY_CUSTOMER_EQUIPMENTS);
            }

            if (mEquipments == null) {
                mEquipments = new ArrayList<Equipment>();
            }

            initAdapterAndList(fragmentView);

            if (mEquipments.isEmpty()) {
                getLoaderManager().initLoader(EQUIPMENT_LOADER_ID, null, this);
            } else {
                mEquipmentsListView.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
            }
        } else {
            mEquipmentsListView.setAdapter(mEquipmentAdaptor);
            mEquipmentsListView.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
        }
    }

    private void initAdapterAndList(final View fragmentView) {

        //add footer for listview
        final View footer = LayoutInflater.from(fragmentView.getContext()).inflate(R.layout.row_new_equipment, null,
                false);
        footer.setOnClickListener(this);
        mEquipmentsListView.addFooterView(footer);
        mEquipmentsListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        mEquipmentAdaptor = new MyCustomAdapter(fragmentView.getContext(), mEquipments, mAddedEquipment);
        mEquipmentsListView.setAdapter(mEquipmentAdaptor);
        mEquipmentsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long idToRemove) {
                boolean checked = mEquipmentsListView.isItemChecked(position);
                if (checked) {
                    mAddedEquipment.add((int) idToRemove);
                } else {
                    for (final Integer id : mAddedEquipment) {
                        if (idToRemove == id) {
                            mAddedEquipment.remove(id);
                            break;
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(KEY_CUSTOMER_EQUIPMENTS, new ArrayList<Parcelable>(mEquipments));
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        if (id == R.id.btn_save_equipment) {
            new SubmitEquipmentTask().execute();
        } else if (id == R.id.footer) {
            final Intent i = new Intent(getView().getContext(), ActivityEquipmentAddEdit.class);
            i.putExtra(CustomerEquipmentActivity.EXTRA_CUSTOMER_ID, AppDataSingleton.getInstance().getCustomer().getId());
            i.putExtra(ActivityEquipmentAddEdit.EXTRA_LAUNCHED, ActivityEquipmentAddEdit.LAUNCHED_FROM_APPT);
            startActivity(i);
        }
    }

    //todo: change customer id from singleton to bundle
    @Override
    public Loader<CustomerEquipment> onCreateLoader(int i, Bundle bundle) {
        return new CustomerEquipmentLoader(getView().getContext(), getString(R.string.customer_equipment_url, AppDataSingleton.getInstance()
                .getCustomer().getId()));
    }

    @Override
    public void onLoadFinished(Loader<CustomerEquipment> loader, CustomerEquipment customerEquipment) {
        BaseAsyncTaskLoader<CustomerEquipment> l = (BaseAsyncTaskLoader<CustomerEquipment>) loader;
        if (l.getLoadException() == null && customerEquipment != null) {
            if (customerEquipment.getEquipmentList() != null) {
                mEquipments.clear();
                mEquipments.addAll(filterEquipmentsByAppLocationId(customerEquipment.getEquipmentList()));
            }
            mEquipmentAdaptor.notifyDataSetChanged();
        }

        mProgressBar.setVisibility(View.GONE);
        mEquipmentsListView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoaderReset(Loader<CustomerEquipment> objectLoader) {
    }

    @Override
    public void onDestroyView() {
        mProgressBar = null;
        mEquipmentsListView = null;
        super.onDestroyView();
    }

    private List<Equipment> filterEquipmentsByAppLocationId(final List<Equipment> equipments) {
        final int locationId = AppDataSingleton.getInstance().getAppointment().getLocationId();
        final ArrayList<Equipment> list = new ArrayList<Equipment>();
        for (Equipment eq : equipments) {
            if (locationId == eq.getLocationId()) {
                list.add(eq);
            }
        }
        list.trimToSize();
        return list;
    }

    // TODO - Disable/Enable button pushes to prevent multiple submissions, change to loader in future
    private class SubmitEquipmentTask extends BaseUiReportTask<String> {

        SubmitEquipmentTask() {
            super(getActivity(),
                    R.string.async_task_string_saving_equipment_to_appointment);
        }

        @Override
        protected void onSuccess() {
            try {
                getActivity().onBackPressed();
            }catch (IllegalStateException ex){
                ex.printStackTrace();
            }
        }

        @Override
        protected boolean taskBody(final String... args) throws Exception {
            RESTEquipment.attachToAppointment(AppDataSingleton.getInstance()
                    .getAppointment().getId(), mAddedEquipment);
            return true;
        }
    }

    private static class MyCustomAdapter extends BaseAdapter {
        private final LayoutInflater mInflater;
        private final List<Equipment> mEquipmentList;
        private final List<Integer> mAddedEquipment;

        public MyCustomAdapter(Context context, final List<Equipment> equipmentList, final List<Integer> addedEquipment) {
            mInflater = LayoutInflater.from(context);
            mEquipmentList = equipmentList;
            mAddedEquipment = addedEquipment;
        }

        @Override
        public int getCount() {
            return mEquipmentList.size();
        }

        @Override
        public Equipment getItem(int position) {
            return mEquipmentList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return mEquipmentList.get(position).getId();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final Equipment equipment = getItem(position);

            EquipmentHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.row_add_equipment_to_appointment_equipment, parent, false);
                holder = initConvertView(convertView, equipment);
                convertView.setTag(holder);
            } else {
                holder = (EquipmentHolder) convertView.getTag();
            }

            holder.mName.setText(equipment.getName());
            holder.mSerialNumber.setText(equipment.getSerialNumber());
            return convertView;
        }

        private EquipmentHolder initConvertView(final View convertView, final Equipment equipment) {
            final EquipmentHolder holder = new EquipmentHolder();
            holder.mName = (TextView) convertView.findViewById(R.id.name);
            holder.mSerialNumber = (TextView) convertView.findViewById(R.id.serial_number);
            return holder;
        }

        private static class EquipmentHolder {
            TextView mName;
            TextView mSerialNumber;
        }
    }

    private class SyncEquipmentList extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            final Equipment equipment = intent.getParcelableExtra(ActivityEquipmentAddEdit.EXTRA_NEW_EQUIPMENT);
            if (ActivityEquipmentAddEdit.ACTION_EDIT_EQUIPMENT_FINISHED.equals(action) && equipment != null) {
                mEquipments.add(equipment);
                mEquipmentAdaptor.notifyDataSetChanged();
            } else {
                Logger.err("Receiver action is not supported: " + action);
            }
        }
    }
}
