package com.skeds.android.phone.business.ui.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.skeds.android.phone.business.AsyncTasks.BaseUiReportTask;
import com.skeds.android.phone.business.Custom.DeleteOnItemListener;
import com.skeds.android.phone.business.Custom.OnTruckAddedAdapter;
import com.skeds.android.phone.business.Custom.OnTruckAddedAdapter.ViewHolder;
import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.LineItem;
import com.skeds.android.phone.business.Utilities.General.UserUtilitiesSingleton;
import com.skeds.android.phone.business.Utilities.REST.Objects.RESTOnTruckList;
import com.skeds.android.phone.business.activities.ActivityAppointmentOnTruckListView;

import java.math.BigDecimal;

public class OnTruckAddedFragment extends BaseSkedsFragment implements DeleteOnItemListener {

    public static final int ON_TRUCK_ITEMS = 11;

    private ListView itemsList;

    private LineItem selectedItem;

    private Activity activity;

    private ArrayAdapter<LineItem> adapter;

    private Dialog addPartDialog;
    private EditText addPartDialogQuantity;
    private TextView addPartDialogButtonEdit;
    private TextView addPartDialogButtonCancel;

    private double quantityToAdd;

    private OnItemClickListener listener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ViewHolder holder = (ViewHolder) view.getTag();
            if (position == 0) {
                startActivityForResult(new Intent(activity, ActivityAppointmentOnTruckListView.class), 1);
            } else {
                selectedItem = AppDataSingleton.getInstance().getAppointment().getOnTruckList().get(position);
                showAddPartDialog();
            }
        }
    };

    private OnClickListener mDialogButtonListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.dialog_layout_add_truck_item_button_add:
                    quantityToAdd = Double.parseDouble(addPartDialogQuantity
                            .getText().toString());

                    if (quantityToAdd <= 0) {
                        Toast.makeText(activity, "Quantity Can't Be Null!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (!addPartDialog.isShowing())
                        addPartDialog.dismiss();

                    if (addPartDialog != null)
                        if (addPartDialog.isShowing())
                            addPartDialog.dismiss();
                    new SubmitTruckItemTask("Updating Item...").execute();
                    break;
                case R.id.dialog_layout_add_truck_item_button_cancel:
                    if (addPartDialog.isShowing())
                        addPartDialog.dismiss();
                    break;
                default:
                    // Nothing
                    break;
            }
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_on_truck_added, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        activity = getActivity();
        itemsList = (ListView) activity.findViewById(R.id.on_truck_added_list);
        itemsList.setOnItemClickListener(listener);

        new GetAddedOnTruckItems().execute();
    }


    @Override
    public void onPause() {
        super.onPause();
        if (addPartDialog != null)
            if (addPartDialog.isShowing())
                addPartDialog.dismiss();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK)
            new GetAddedOnTruckItems().execute();

    }

    private void showAddPartDialog() {

        addPartDialog = new Dialog(activity);
        addPartDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        addPartDialog.setContentView(R.layout.dialog_layout_add_truck_item);

        addPartDialogQuantity = (EditText) addPartDialog
                .findViewById(R.id.dialog_layout_add_truck_item_edittext_quantity);
        // addPartDialogToInvoice = (CheckBox) addPartDialog
        // .findViewById(R.id.dialog_layout_add_truck_item_checkbox_invoice);

        addPartDialogQuantity.setHint("quantity");
        addPartDialogQuantity.setText(selectedItem.getQuantity() + "");
        addPartDialogButtonEdit = (TextView) addPartDialog
                .findViewById(R.id.dialog_layout_add_truck_item_button_add);
        addPartDialogButtonEdit.setText("Update");
        addPartDialogButtonCancel = (TextView) addPartDialog
                .findViewById(R.id.dialog_layout_add_truck_item_button_cancel);

        addPartDialogButtonEdit.setOnClickListener(mDialogButtonListener);
        addPartDialogButtonCancel.setOnClickListener(mDialogButtonListener);

        addPartDialog.show();
    }


    private class SubmitTruckItemTask extends BaseUiReportTask<String> {

        private String title;

        SubmitTruckItemTask(String title) {
            super(activity, title);
            this.title = title;
        }

        @Override
        protected boolean taskBody(final String... args) throws Exception {
            RESTOnTruckList.add(selectedItem.getServiceTypeId(), AppDataSingleton.getInstance().getAppointment()
                            .getId(), UserUtilitiesSingleton.getInstance().user.getServiceProviderId(),
                    quantityToAdd, false); // 'false' is
            // addToInvoice
            return true;
        }

        @Override
        protected void onSuccess() {
            super.onSuccess();
            if ("Updating Item...".equals(title))
                selectedItem.setQuantity(new BigDecimal(quantityToAdd));
            else if ("Deleting Item...".equals(title)) {
                AppDataSingleton.getInstance().getAppointment().getOnTruckList().remove(selectedItem);
            }

            itemsList.setAdapter(new OnTruckAddedAdapter(activity));
        }
    }

    private class GetAddedOnTruckItems extends BaseUiReportTask<String> {

        public GetAddedOnTruckItems() {
            super(activity, "Loading On Truck...");
        }

        @Override
        protected boolean taskBody(String... params) throws Exception {
            RESTOnTruckList.queryAdded(AppDataSingleton.getInstance().getAppointment().getId());
            return true;
        }

        @Override
        protected void onSuccess() {
            super.onSuccess();

            itemsList.setAdapter(new OnTruckAddedAdapter(activity));
        }

    }

    @Override
    public void onDelete(int position) {
        selectedItem = AppDataSingleton.getInstance().getAppointment().getOnTruckList().get(position);
        quantityToAdd = 0;
        new SubmitTruckItemTask("Deleting Item...").execute();
    }

}
