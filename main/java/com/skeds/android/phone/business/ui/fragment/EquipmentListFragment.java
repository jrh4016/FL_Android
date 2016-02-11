package com.skeds.android.phone.business.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Customer;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Location;
import com.skeds.android.phone.business.Utilities.General.CommonUtilities;
import com.skeds.android.phone.business.Utilities.Logger;
import com.skeds.android.phone.business.Utilities.NonfatalException;
import com.skeds.android.phone.business.Utilities.REST.Objects.RESTEquipment;
import com.skeds.android.phone.business.activities.ActivityEquipmentAddEdit;
import com.skeds.android.phone.business.activities.CustomerEquipmentActivity;
import com.skeds.android.phone.business.activities.CustomerEquipmentViewActivity;
import com.skeds.android.phone.business.core.SkedsApplication;
import com.skeds.android.phone.business.core.async.BaseAsyncTaskLoader;
import com.skeds.android.phone.business.core.async.BaseWorkerHandler;
import com.skeds.android.phone.business.data.loader.CustomerEquipmentLoader;
import com.skeds.android.phone.business.model.CustomerEquipment;
import com.skeds.android.phone.business.model.Equipment;

import java.util.ArrayList;
import java.util.List;

import de.timroes.android.listview.EnhancedListView;

public class EquipmentListFragment extends BaseListFragment implements LoaderManager.LoaderCallbacks<CustomerEquipment> {

    private static final String KEY_CUSTOMER_ID = "customer_id";
    private static final String KEY_EQUIPMENTS = "equipments";

    private static final String WORKER_THREAD_NAME = EquipmentListFragment.class.getSimpleName() + "Worker";

    private static final int EQUIPMENT_LOADER_ID = 10;

    private static final int MSG_DELETE_ITEMS = 1;

    private EqAdapter mEquipmentAdaptor;
    private ArrayList<Equipment> mEquipments;

    private LocalBroadcastManager mLocalBroadcastManager;
    private BroadcastReceiver mSyncEquipmentsReceiver;

    private WorkerHandler mWorkerHandler;
    private Handler mUiHandler;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retain this fragment across configuration changes.
        setRetainInstance(true);

        // create local broadcast receiver to have ability to see if sync is in progress
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(SkedsApplication.getContext());
        mSyncEquipmentsReceiver = new SyncEquipmentList();

        mUiHandler = new Handler(Looper.getMainLooper());

        final HandlerThread workerThread = new HandlerThread(WORKER_THREAD_NAME) {
            @Override
            protected void onLooperPrepared() {
                mWorkerHandler = new WorkerHandler(getLooper());
            }
        };
        workerThread.start();

        // register local broadcast receiver
        final IntentFilter filter = new IntentFilter();
        filter.addAction(ActivityEquipmentAddEdit.ACTION_EDIT_EQUIPMENT_FINISHED);
        mLocalBroadcastManager.registerReceiver(mSyncEquipmentsReceiver, filter);
    }

    public static EquipmentListFragment newInstance(final Bundle bundle) {
        EquipmentListFragment fragment = new EquipmentListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onDestroy() {
        mLocalBroadcastManager.unregisterReceiver(mSyncEquipmentsReceiver);
        mWorkerHandler.tryQuit(true);

        mEquipmentAdaptor = null;
        mEquipments = null;
        mWorkerHandler = null;
        mUiHandler = null;

        super.onDestroy();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final Bundle args = getArguments();
        if (args == null) {
            throw new IllegalArgumentException("EquipmentListFragment must contain args to process data!");
        }

        final View fragmentView = getView();
        final Context context = fragmentView.getContext();

        final Customer customer = AppDataSingleton.getInstance().getCustomerById(args.getInt(CustomerEquipmentActivity.EXTRA_CUSTOMER_ID, -1));
        if (!args.getBoolean(CustomerEquipmentActivity.EXTRA_CAN_ADD_NEW_EQUIPMENT, true)) {
            fragmentView.findViewById(R.id.btn_add).setVisibility(View.GONE);
        }

        final Location location = customer.getLocationById(args.getInt(CustomerEquipmentActivity.EXTRA_LOCATION_ID, -1));
        emptyText.setText(getString(R.string.equipment_empty_text));

        initListView(args, context);

        final DeleteUndoCallback callback = new DeleteUndoCallback() {
            @Override
            public void undo(int position, View swipingLayout) {
                listView.undoSwipeToDelete(position, swipingLayout);
            }

            @Override
            public void delete(int position, View swipingLayout, final View listItemView) {
                listView.dismissItem(position, swipingLayout, listItemView);
            }

            @Override
            public void reset(int position, View swipingLayout, final View choiceLayout) {
                listView.resetSwipeAction(position, swipingLayout, choiceLayout);
            }
        };

        if (savedInstanceState != null) {
            mEquipments = savedInstanceState.getParcelableArrayList(KEY_EQUIPMENTS);
        }

        if (mEquipments == null) {
            mEquipments = new ArrayList<Equipment>();
        }

        if (mEquipmentAdaptor == null) {
            initAdapter(context, customer, location);
        }

        mEquipmentAdaptor.setCallback(callback);
        listView.setAdapter(mEquipmentAdaptor);

        if (!CommonUtilities.isNetworkAvailable(context)) {
            emptyText.setText(getString(R.string.network_unavailable));
        }

        if (mEquipments.isEmpty()) {
            final Bundle bundle = new Bundle();
            bundle.putInt(KEY_CUSTOMER_ID, customer.getId());
            setListShown(false);
            getLoaderManager().initLoader(EQUIPMENT_LOADER_ID, bundle, this);
        } else {
            setListShown(true);
        }
    }

    private void initAdapter(final Context context, final Customer customer, final Location location) {
        mEquipmentAdaptor = new EqAdapter(context, mEquipments, location, customer);
    }

    private void initListView(final Bundle args, final Context context) {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Intent i = new Intent(context, CustomerEquipmentViewActivity.class);
                i.putExtra(CustomerEquipmentViewActivity.EXTRA_EQUIPMENT, (Equipment) parent.getItemAtPosition(position));
                i.putExtras(args);
                startActivity(i);
            }
        });


        // Set the callback that handles dismisses.
        listView.setDismissCallback(new EnhancedListView.OnDismissCallback() {
            /**
             * This method will be called when the user swiped a way or deleted it via
             * {@link de.timroes.android.listview.EnhancedListView#delete(int)}.
             *
             * @param listView The {@link EnhancedListView} the item has been deleted from.
             * @param position The position of the item to delete from your adapter.
             * @return An {@link de.timroes.android.listview.EnhancedListView.Undoable}, if you want
             *      to give the user the possibility to undo the deletion.
             */
            @Override
            public EnhancedListView.Undoable onDismiss(final EnhancedListView listView, final int position) {
                final Equipment item = (Equipment) listView.getAdapter().getItem(position);
                mEquipmentAdaptor.remove(position);
                //should pass handler that is connected with main ui queue in order to invalidate adapter, because adapter can't be validated outside the main ui thread
                BaseWorkerHandler.WorkerHandlerRequest<Long> request = new BaseWorkerHandler.WorkerHandlerRequest<Long>(item.getId(), mUiHandler) {

                    @Override
                    public void onError(Exception e) {
                        mEquipments.add(position, item);
                        mEquipmentAdaptor.notifyDataSetChanged();
                    }
                };
                final Message msg = mWorkerHandler.obtainMessage(MSG_DELETE_ITEMS, request);
                msg.sendToTarget();
                return null;
            }
        });

        listView.enableSwipeToDismiss();

        listView.setSwipingLayout(R.id.swiping_layout);
        listView.setChoiceLayout(R.id.choice_layout);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(KEY_EQUIPMENTS, mEquipments);
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<CustomerEquipment> onCreateLoader(int i, Bundle bundle) {
        return new CustomerEquipmentLoader(getView().getContext(), getString(R.string.customer_equipment_url, bundle.getInt(KEY_CUSTOMER_ID)));
    }

    @Override
    public void onLoadFinished(Loader<CustomerEquipment> loader, CustomerEquipment customerEquipment) {
        BaseAsyncTaskLoader<CustomerEquipment> l = (BaseAsyncTaskLoader<CustomerEquipment>) loader;
        if (l.getLoadException() == null && customerEquipment != null) {
            if (customerEquipment.getEquipmentList() != null) {
                mEquipments.clear();
                mEquipments.addAll(customerEquipment.getEquipmentList());
            }
            mEquipmentAdaptor.notifyDataSetChanged();
        }

        if (!listView.isShown()) {
            setListShown(true);
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
    }

    private static interface DeleteUndoCallback {
        void undo(final int position, final View swipingLayout);

        void delete(final int position, final View swipingLayout, final View listItemView);

        void reset(final int position, final View swipingLayout, final View choiceLayout);
    }

    private static class EqAdapter extends BaseAdapter {
        private final LayoutInflater mInflater;
        private final List<Equipment> mEquipmentList;
        private final Location mLocation;
        private final int mNameColor;
        private final int mSerialNumberColor;
        private final Customer mCustomer;
        private DeleteUndoCallback mCallback;

        public EqAdapter(final Context context, final List<Equipment> equipmentList, final Location location, final Customer customer) {
            mInflater = LayoutInflater.from(context);
            mEquipmentList = equipmentList;
            mLocation = location;
            mCustomer = customer;
            mNameColor = context.getResources().getColor(R.color.color_equipment_name);
            mSerialNumberColor = context.getResources().getColor(R.color.color_equipment_serial_number);
        }

        @Override
        public int getCount() {
            return mEquipmentList.size();
        }

        @Override
        public Equipment getItem(int pos) {
            return mEquipmentList.get(pos);
        }

        @Override
        public long getItemId(int pos) {
            return pos;
        }

        public void setCallback(final DeleteUndoCallback callback) {
            mCallback = callback;
        }

        @Override
        public View getView(final int pos, View convertView, ViewGroup parent) {
            final EquipmentHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.li_customer_equipment, parent, false);
                holder = initConvertView(convertView);
                convertView.setTag(holder);
            } else {
                holder = (EquipmentHolder) convertView.getTag();
            }

            //make copy of the list item view in order to pass it to the callback
            final View childView = convertView;

            final Equipment equipment = getItem(pos);
            holder.mName.setText(equipment.getName());
            holder.mName.setTextColor(mNameColor);

            holder.mSerialNumber.setTextColor(mSerialNumberColor);

            final String serialNumber = equipment.getSerialNumber();
            final StringBuffer serialNumberBuf;
            if (serialNumber != null) {
                serialNumberBuf = new StringBuffer(equipment.getSerialNumber());
            } else {
                serialNumberBuf = new StringBuffer();
            }

            if (mLocation == null) {
                final Location location = mCustomer.getLocationById(equipment.getLocationId());
                if (location != null) {
                    final String address = location.getAddress1();
                    if (!TextUtils.isEmpty(address)) {
                        serialNumberBuf.append('\n');
                        serialNumberBuf.append(address);
                    }
                }
            }
            holder.mSerialNumber.setText(serialNumberBuf);

            holder.mDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.delete(pos, holder.mSwipingLayout, childView);
                }
            });

            mCallback.reset(pos, holder.mSwipingLayout, holder.mChoiceLayout);

            return convertView;
        }

        private EquipmentHolder initConvertView(View convertView) {
            EquipmentHolder holder = new EquipmentHolder();
            holder.mName = (TextView) convertView.findViewById(R.id.name);
            holder.mSerialNumber = (TextView) convertView.findViewById(R.id.serial_number);
            holder.mDelete = convertView.findViewById(R.id.delete);
            holder.mSwipingLayout = convertView.findViewById(R.id.swiping_layout);
            holder.mChoiceLayout = convertView.findViewById(R.id.choice_layout);
            return holder;
        }

        public void remove(int position) {
            mEquipmentList.remove(position);
            notifyDataSetChanged();
        }

        private static class EquipmentHolder {
            TextView mName;
            TextView mSerialNumber;
            View mDelete;
            View mSwipingLayout;
            View mChoiceLayout;
        }
    }

    private class SyncEquipmentList extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (ActivityEquipmentAddEdit.ACTION_EDIT_EQUIPMENT_FINISHED.equals(action)) {
                final Equipment equipment = intent.getParcelableExtra(ActivityEquipmentAddEdit.EXTRA_NEW_EQUIPMENT);
                if (equipment != null) {
                    final boolean isEmpty = mEquipments.isEmpty();
                    updateEquipmentsList(equipment);
                    //if the list was empty while we add new equipment then try to reload list with equipments because
                    // it might be that network was unavailable
                    if (isEmpty) {
                        setListShown(false);
                        final Bundle bundle = new Bundle();
                        bundle.putInt(KEY_CUSTOMER_ID, getArguments().getInt(CustomerEquipmentActivity.EXTRA_CUSTOMER_ID, -1));
                        getLoaderManager().restartLoader(EQUIPMENT_LOADER_ID, bundle, EquipmentListFragment.this);
                    } else {
                        mEquipmentAdaptor.notifyDataSetChanged();
                    }
                }
            } else {
                Logger.err("Receiver action is not supported: " + action);
            }
        }
    }

    //equipment can be new or can be updated via edit functionality
    private void updateEquipmentsList(final Equipment equipment) {
        for (int i = 0; i < mEquipments.size(); i++) {
            final Equipment eq = mEquipments.get(i);
            if (eq.getId() == equipment.getId()) {
                mEquipments.remove(eq);
                mEquipments.add(i, equipment);
                return;
            }
        }
        mEquipments.add(equipment);
    }

    private static class WorkerHandler extends BaseWorkerHandler {

        public WorkerHandler(Looper looper) {
            super(looper, MSG_DELETE_ITEMS);
        }

        // this is internal handler and we're sure on what payload posted in the message to the
        // current handler
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_DELETE_ITEMS:
                    final WorkerHandlerRequest<Long> request = (WorkerHandlerRequest<Long>) msg.obj;
                    try {
                        RESTEquipment.delete(request.getData());
                    } catch (NonfatalException e) {
                        request.postError(e);
                    }
                    tryQuit(false);
                    break;
            }
        }
    }
}
