package com.skeds.android.phone.business.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.skeds.android.phone.business.AsyncTasks.BaseUiReportTask;
import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Manufacturer;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.PartOrder;
import com.skeds.android.phone.business.Utilities.General.CommonUtilities;
import com.skeds.android.phone.business.Utilities.General.UserUtilitiesSingleton;
import com.skeds.android.phone.business.Utilities.REST.Objects.RESTEquipmentManufacturerList;
import com.skeds.android.phone.business.Utilities.REST.Objects.RESTPartOrder;

import java.util.List;

public class ActivityPartOrderAddEdit extends BaseSkedsActivity implements
        View.OnClickListener {

    private int appointmentId = -1;
    private Spinner manufacturers;
    private Spinner orderStatus;
    private GetManufacturersTask getManufacturersTask;
    private List<Manufacturer> manufsList = null;
    private PartOrder partOrder;
    private TextView partNameView, partNumberView, quantityView, priceView;

    private static PartOrder partOrderPatchHack = null;

    public static void launch(Activity from, PartOrder po, Fragment fragment) {
        assert po != null;
        Intent it = new Intent(from, ActivityPartOrderAddEdit.class);
        partOrderPatchHack = po;
        if (fragment != null)
            fragment.startActivityForResult(it, 1);
        else
            from.startActivityForResult(it, 1);
    }

    public static void launch(Activity from, Fragment fragment, int apptId) {
        Intent it = new Intent(from, ActivityPartOrderAddEdit.class);
        it.putExtra("appointmentId", apptId);
        partOrderPatchHack = null;
        if (fragment != null)
            fragment.startActivityForResult(it, 1);
        else
            from.startActivityForResult(it, 1);
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // This will stop the keyboard from automatically popping up
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        partOrder = partOrderPatchHack;
        partOrderPatchHack = null;
        if (partOrder == null) {
            Intent intent = getIntent();
            appointmentId = intent.getIntExtra("appointmentId", -1);
            assert appointmentId > 0;
        }

        setContentView(R.layout.layout_partorders_edit);
        manufacturers = (Spinner) findViewById(R.id.manufacturer);
        manufacturers.setEnabled(true);
        orderStatus = (Spinner) findViewById(R.id.order_status);

        partNameView = (TextView) findViewById(R.id.part_name_inputline);
        partNumberView = (TextView) findViewById(R.id.part_number_inputline);
        quantityView = (TextView) findViewById(R.id.quantity_inputline);
        priceView = (TextView) findViewById(R.id.price_inputline);

        if (!AppDataSingleton
                .getInstance().getPartOrderCustomFieldName1().isEmpty())
            ((TextView) findViewById(R.id.custinfo1)).setText(AppDataSingleton
                    .getInstance().getPartOrderCustomFieldName1());

        if (!AppDataSingleton
                .getInstance().getPartOrderCustomFieldName2().isEmpty())
            ((TextView) findViewById(R.id.custinfo2)).setText(AppDataSingleton
                    .getInstance().getPartOrderCustomFieldName2());

        if (!AppDataSingleton
                .getInstance().getPartOrderCustomFieldName3().isEmpty())
            ((TextView) findViewById(R.id.custinfo3)).setText(AppDataSingleton
                    .getInstance().getPartOrderCustomFieldName3());

        TextView text = (TextView) findViewById(R.id.title_text);

        if (partOrder == null) {
            text.setText("New part order");
            orderStatus.setSelection(0);
            // orderStatus.setEnabled(false);
        } else {
            text.setText(partOrder.getName());
            partNameView.setText(partOrder.getName());
            partNumberView.setText(partOrder.getPartNumber());
            quantityView.setText(partOrder.getQuantity());
            priceView.setText(partOrder.getPrice());

            String cs = partOrder.getStatus();
            String slist[] = getResources().getStringArray(
                    R.array.partorder_statuses_xml);
            for (int i = 0; i < slist.length; i++) {
                if (cs.equals(slist[i])) {
                    orderStatus.setSelection(i);
                    break;
                }
            }
            orderStatus.setEnabled(true);

            ((EditText) findViewById(R.id.tracknum_inputline))
                    .setText(partOrder.getTrackingNumber());
            ((EditText) findViewById(R.id.custinfo1_inputline))
                    .setText(partOrder.getCustomField1());
            ((EditText) findViewById(R.id.custinfo2_inputline))
                    .setText(partOrder.getCustomField2());
            ((EditText) findViewById(R.id.custinfo3_inputline))
                    .setText(partOrder.getCustomField3());
            ((EditText) findViewById(R.id.eta_inputline)).setText(partOrder
                    .getDeliveryDateOnly());
        }

        findViewById(R.id.btn_back).setOnClickListener(this);
        text = (TextView) findViewById(R.id.btn_submit);
        text.setOnClickListener(this);
        if (partOrder == null)
            text.setText("Create");

        if (!CommonUtilities.isNetworkAvailable(this)) {
            Toast.makeText(this, "Network connection unavailable.",
                    Toast.LENGTH_SHORT).show();
            finish();
        } else {
            getManufacturersTask = new GetManufacturersTask();
            getManufacturersTask.execute();
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                setResult(RESULT_CANCELED);
                finish();
                return;
            // Save
            case R.id.btn_submit: {
                String partName = partNameView.getText().toString();
                if (partName.length() < 1)
                    return;
                String partNumber = partNumberView.getText().toString();
                if (partNumber.length() < 1)
                    return;
                String qty = quantityView.getText().toString();
                if (qty.length() < 1)
                    return;
                String price = priceView.getText().toString();
                if (price.length() < 1)
                    return;
                if (!CommonUtilities.isNetworkAvailable(this)) {
                    Toast.makeText(this, "Network connection unavailable.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                new SaveChangesTask(partName, partNumber, qty, price).execute();
                setResult(RESULT_OK);
                break;
            }
            default:
                // Nothing
                break;
        }
    }

	/*
     * @Override public void onItemSelected(AdapterView<?> parent, View view,
	 * int position, long id) { switch (parent.getId()) { case
	 * R.id.manufacturer: { resManuf = position; return; } case
	 * R.id.order_status: { debug("status spinner: " +
	 * view.getClass().getSimpleName() + ": " + position + "/" + id);
	 * resOrderStatus = position; return; } default: return; } }
	 * 
	 * @Override public void onNothingSelected(AdapterView<?> parent) { switch
	 * (parent.getId()) { case R.id.manufacturer: { resManuf = -1; return; }
	 * case R.id.order_status: { resOrderStatus = -1; return; } default: return;
	 * } }
	 */

    private void setupManufacturers() {
        manufsList = AppDataSingleton.getInstance()
                .getEquipmentManufacturerList();
        if (manufsList != null && !manufsList.isEmpty()) {
            String adapternames[] = new String[manufsList.size()];
            for (int i = 0; i < adapternames.length; i++)
                adapternames[i] = manufsList.get(i).getName();
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, adapternames);
            arrayAdapter
                    .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            manufacturers.setAdapter(arrayAdapter);
            manufacturers.setEnabled(true);

            if (partOrder != null) {
                int iterator = 0;
                for (Manufacturer manuf : AppDataSingleton.getInstance().getEquipmentManufacturerList()) {
                    if (manuf.getId() == partOrder.getManufacturerId()) {
                        manufacturers.setSelection(iterator);
                        return;
                    }
                    iterator++;
                }
            }
        }
    }

    private class GetManufacturersTask extends BaseUiReportTask<String> {

        GetManufacturersTask() {
            super(ActivityPartOrderAddEdit.this, "Loading manufacturers...");
        }

        @Override
        protected void dtor() {
            getManufacturersTask = null;
        }

        @Override
        protected void onSuccess() {
            setupManufacturers();
        }

        @Override
        protected boolean taskBody(final String... args) throws Exception {
            RESTEquipmentManufacturerList.query(UserUtilitiesSingleton
                    .getInstance().user.getOwnerId());
            return true;

        }
    }

    private final class SaveChangesTask extends BaseUiReportTask<String> {

        final String partName, partNumber, quantity, price, status;

        // final String manufId;

        SaveChangesTask(String partName, String partNumber, String qty,
                        String price) {
            super(ActivityPartOrderAddEdit.this,
                    partOrder == null ? "Adding new part order..."
                            : "Applying changes...");
            this.partName = partName;
            this.partNumber = partNumber;
            this.quantity = qty;
            this.price = price;

            int spos = manufacturers.getSelectedItemPosition();
            // manufId = spos < 0 ? null : "" + spos;

            if (partOrder == null) {
                status = getResources().getStringArray(
                        R.array.partorder_statuses_xml)[orderStatus
                        .getSelectedItemPosition()];
            } else {
                spos = orderStatus.getSelectedItemPosition();
                status = getResources().getStringArray(
                        R.array.partorder_statuses_xml)[spos];
            }
            setAutocloseOnNotSuccess(true);
            setAutocloseOnSuccess(true);
        }

        @Override
        protected boolean taskBody(String... params) throws Exception {

            int manufId = 0;

            if (manufacturers != null)
                if (!AppDataSingleton.getInstance()
                        .getEquipmentManufacturerList().isEmpty())
                    manufId = AppDataSingleton.getInstance()
                            .getEquipmentManufacturerList()
                            .get(manufacturers.getSelectedItemPosition())
                            .getId();


            if (partOrder == null)
                RESTPartOrder.add(
                        appointmentId,
                        partName,
                        partNumber,
                        quantity,
                        price,
                        manufId,
                        status);
            else
                RESTPartOrder.edit(
                        partOrder.getId(),
                        partName,
                        partNumber,
                        quantity,
                        price,
                        manufId,
                        status);
            return true;
        }
    }

}