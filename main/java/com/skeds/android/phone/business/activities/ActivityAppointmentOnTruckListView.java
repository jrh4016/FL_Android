package com.skeds.android.phone.business.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.skeds.android.phone.business.AsyncTasks.BaseUiReportTask;
import com.skeds.android.phone.business.Custom.AccountMenu;
import com.skeds.android.phone.business.Custom.QuickAction;
import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.LineItem;
import com.skeds.android.phone.business.Utilities.General.CommonUtilities;
import com.skeds.android.phone.business.Utilities.General.UserUtilitiesSingleton;
import com.skeds.android.phone.business.Utilities.REST.Objects.RESTOnTruckList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ActivityAppointmentOnTruckListView extends BaseSkedsActivity {

    private LinearLayout headerLayout;
    private ImageView headerButtonUser;
    private ImageView headerButtonBack;

    private Activity mActivity;
    private Context mContext;

    private PullToRefreshListView mPullRefreshListView;
    private ListView mProductList;
    private List<LineItem> mListItems;

    private TextView mNoneAvailable;

    public static int mSelectedItem;

    private Dialog addPartDialog;
    private EditText addPartDialogQuantity;
    // private CheckBox addPartDialogToInvoice;
    private TextView addPartDialogButtonAdd;
    private TextView addPartDialogButtonCancel;

    private double quantityToAdd;

    // private boolean addToInvoice;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_on_truck);

        headerLayout = (LinearLayout) findViewById(R.id.activity_header);

        mActivity = ActivityAppointmentOnTruckListView.this;
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

        mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.activity_on_truck_listview_truck_items);
        mNoneAvailable = (TextView) findViewById(R.id.activity_on_truck_textview_no_items);

        if (!CommonUtilities.isNetworkAvailable(mContext)) {
            Toast.makeText(mContext, "Network connection unavailable.",
                    Toast.LENGTH_SHORT).show();
            finish();
        } else {
            new GetOnTruckItemsTask().execute();
        }
    }


    @Override
    public void onBackPressed() {
        if (addPartDialog != null)
            if (addPartDialog.isShowing())
                addPartDialog.dismiss();

        super.onBackPressed();


    }


    private OnItemClickListener mListClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View v, int position,
                                long id) {

            mSelectedItem = (int) id;
            showAddPartDialog();

        }
    };

    private OnClickListener mDialogButtonListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.dialog_layout_add_truck_item_button_add:
                    if (!CommonUtilities.isNetworkAvailable(mContext)) {
                        Toast.makeText(mContext, "Network connection unavailable.",
                                Toast.LENGTH_SHORT).show();
                        break;
                    }
                    quantityToAdd = Double.parseDouble(addPartDialogQuantity
                            .getText().toString());

                    if (quantityToAdd <= 0) {
                        Toast.makeText(mContext, "Quantity Can't Be Null!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (!addPartDialog.isShowing())
                        addPartDialog.dismiss();

                    new SubmitTruckItemTask().execute();
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

    private void setupUI() {

        // Set a listener to be invoked when the list should be refreshed.
        mPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshBase refreshView) {
                mPullRefreshListView.setLastUpdatedLabel(DateUtils
                        .formatDateTime(mContext, System.currentTimeMillis(),
                                DateUtils.FORMAT_SHOW_TIME
                                        | DateUtils.FORMAT_SHOW_DATE
                                        | DateUtils.FORMAT_ABBREV_ALL));

                if (!CommonUtilities.isNetworkAvailable(mContext)) {
                    Toast.makeText(mContext, "Network connection unavailable.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    // Do work to refresh the list here.
                    new PullToRefreshTask().execute();
                }
            }
        });

        mProductList = mPullRefreshListView.getRefreshableView();

        if (!AppDataSingleton.getInstance().getOnTruckPartsList().isEmpty()) {
            // if (CommonUtilities.mTruckItem != null) {
            mNoneAvailable.setVisibility(View.GONE);

            mProductList.setOnItemClickListener(mListClickListener);

            mListItems = new ArrayList<LineItem>();
            mListItems.addAll(AppDataSingleton.getInstance().getOnTruckPartsList());

            ArrayAdapter<LineItem> adapter = new MyCustomAdapter(mContext,
                    R.layout.row_product_name_price_description, mListItems);
            mProductList.setAdapter(adapter);

            // ((PullToRefresh) mProductList)
            // .setOnRefreshListener(new OnRefreshListener() {
            // @Override
            // public void onRefresh() {
            // // Do work to refresh the list here.
            // new PullToRefreshTask().execute();
            // }
            // });

        } else {
            mNoneAvailable.setVisibility(View.VISIBLE);
        }
    }

    private void showAddPartDialog() {

        addPartDialog = new Dialog(mContext);
        addPartDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        addPartDialog.setContentView(R.layout.dialog_layout_add_truck_item);

        addPartDialogQuantity = (EditText) addPartDialog
                .findViewById(R.id.dialog_layout_add_truck_item_edittext_quantity);
        // addPartDialogToInvoice = (CheckBox) addPartDialog
        // .findViewById(R.id.dialog_layout_add_truck_item_checkbox_invoice);

        addPartDialogQuantity.setHint("quantity");
        addPartDialogButtonAdd = (TextView) addPartDialog
                .findViewById(R.id.dialog_layout_add_truck_item_button_add);
        addPartDialogButtonCancel = (TextView) addPartDialog
                .findViewById(R.id.dialog_layout_add_truck_item_button_cancel);

        addPartDialogButtonAdd.setOnClickListener(mDialogButtonListener);
        addPartDialogButtonCancel.setOnClickListener(mDialogButtonListener);

        addPartDialog.show();
    }

    private class PullToRefreshTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            // Simulates a background job.
            try {
                if (UserUtilitiesSingleton.getInstance().user.isLoggedIn()) {
                    RESTOnTruckList
                            .query(AppDataSingleton.getInstance().getAppointment().getOwnerId());
                    return true;
                }
            } catch (Exception e) {
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {

            // Call onRefreshComplete when the list has been refreshed.
            mPullRefreshListView.onRefreshComplete();

            if (!UserUtilitiesSingleton.getInstance().user.isLoggedIn()) {
                Intent i = new Intent(mContext, ActivityDashboardView.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            } else {
                if (success)
                    setupUI();
            }
        }
    }

    private class GetOnTruckItemsTask extends BaseUiReportTask<String> {
        GetOnTruckItemsTask() {
            super(ActivityAppointmentOnTruckListView.this,
                    R.string.async_task_string_loading_on_truck_equipment);
        }

        @Override
        protected void onSuccess() {
            setupUI();
        }

        @Override
        protected boolean taskBody(final String... args) throws Exception {
            RESTOnTruckList.query(AppDataSingleton.getInstance().getAppointment().getOwnerId());
            return true;
        }
    }

    private class SubmitTruckItemTask extends BaseUiReportTask<String> {
        SubmitTruckItemTask() {
            super(ActivityAppointmentOnTruckListView.this,
                    R.string.async_task_string_adding_item);
        }

        @Override
        protected boolean taskBody(final String... args) throws Exception {
            RESTOnTruckList.add(AppDataSingleton.getInstance().getOnTruckPartsList()
                            .get(mSelectedItem).getId(), AppDataSingleton.getInstance().getAppointment()
                            .getId(), UserUtilitiesSingleton.getInstance().user.getServiceProviderId(),
                    quantityToAdd, false); // 'false' is
            // addToInvoice
            return true;
        }

        @Override
        protected void onSuccess() {
            super.onSuccess();
            setResult(Activity.RESULT_OK);
            onBackPressed();
        }
    }

    public class MyCustomAdapter extends ArrayAdapter<LineItem> {

        public MyCustomAdapter(Context context, int textViewResourceId,
                               List<LineItem> objects) {
            super(context, textViewResourceId, objects);
            notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(
                    R.layout.row_product_name_price_description, parent, false);

            // Set Sked Name Label
            TextView productName = (TextView) row
                    .findViewById(R.id.layout_row_product_name_price_description_text_name);
            TextView productPrice = (TextView) row
                    .findViewById(R.id.layout_row_product_name_price_description_text_price);
            TextView productDescription = (TextView) row
                    .findViewById(R.id.layout_row_product_name_price_description_text_description);

            if (getItem(position) != null) {
                productName.setText(getItem(position)
                        .getName());
                productPrice.setText("$"
                        + getItem(position).cost);
                productDescription.setText(getItem(position).getDescription());
            } else {
                productName.setText("Error on item");
                productPrice.setText("");
                productDescription.setText("");
            }
            return row;
        }

    }
}