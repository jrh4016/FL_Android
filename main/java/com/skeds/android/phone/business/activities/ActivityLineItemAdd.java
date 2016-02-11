package com.skeds.android.phone.business.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.skeds.android.phone.business.AsyncTasks.BaseUiReportTask;
import com.skeds.android.phone.business.Custom.AccountMenu;
import com.skeds.android.phone.business.Custom.QuickAction;
import com.skeds.android.phone.business.Dialogs.DialogLineItemStandard;
import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.LineItem;
import com.skeds.android.phone.business.Utilities.General.CommonUtilities;
import com.skeds.android.phone.business.Utilities.General.Constants;
import com.skeds.android.phone.business.Utilities.General.NumberFormatTool;
import com.skeds.android.phone.business.Utilities.General.UserUtilitiesSingleton;
import com.skeds.android.phone.business.Utilities.REST.Objects.RESTLineItemList;
import com.skeds.android.phone.business.Utilities.REST.Objects.RESTPricebook;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ActivityLineItemAdd extends BaseSkedsActivity {

    public static List<LineItem> mItemsToAdd = new ArrayList<LineItem>();

    public static boolean usingPriceBook = false;
    public static LineItem mSelectedListLineItem;

    private static LinearLayout headerLayout;
    private ImageView headerButtonUser;
    private static ImageView headerButtonBack;
    private static TextView headerButtonSave;
    private static TextView headerTextLabelValue;

    private static EditText edittextSearch;
    private static ListView listviewProductList;

    private static Activity mActivity;
    private static Context mContext;

    private static int overrideSearchMode = -1;

    private static int ServicePlanId = 0;

    private static ArrayAdapter<String> adapter;

    private static int mSelectedApptId = -1;
    private int manufacturerId = -1;
    private int groupCodeId = -1;

    private static int lineItemAddViewMode;

    public static int estimateAgreementId = 0;

    private static List<LineItem> lineItemsList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_add_line_item);

        // Retrieve extras
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            overrideSearchMode = bundle.getInt(
                    Constants.EXTRA_PRICEBOOK_SEARCH_MODE, -1);
            if (overrideSearchMode == -1)
                overrideSearchMode = AppDataSingleton.getInstance()
                        .getPriceBookSearchMode();
            lineItemAddViewMode = bundle.getInt(
                    Constants.EXTRA_LINE_ITEM_ADD_VIEW_MODE, -1);

            mSelectedApptId = bundle.getInt("SelectedApptId");
            manufacturerId = bundle.getInt("manufacturerId");
            groupCodeId = bundle.getInt("groupCodeId");
        }

        if (mSelectedApptId == 0) {
            switch (lineItemAddViewMode) {
                case Constants.LINE_ITEM_ADD_VIEW_FROM_ESTIMATE:
                    mSelectedApptId = AppDataSingleton.getInstance().getEstimate()
                            .getAppointmentId();
                    break;
                case Constants.LINE_ITEM_ADD_VIEW_FROM_INVOICE:
                    mSelectedApptId = AppDataSingleton.getInstance().getInvoice()
                            .getAppointmentId();
                    if (mSelectedApptId == 0)
                        mSelectedApptId = AppDataSingleton.getInstance()
                                .getAppointment().getId();
                    break;
                default:
                    break;
            }
        }

        ServicePlanId = 0;

        if (AppDataSingleton.getInstance()
                .getEstimate().isServicePlanUsedForPricing())
            if (estimateAgreementId != 0)
                ServicePlanId = estimateAgreementId;
            else ServicePlanId = AppDataSingleton.getInstance()
                    .getEstimate().getServicePlanId();

        if (ServicePlanId == -1)
            ServicePlanId = 0;

        adapter = null;

        initHeader();

        if (!CommonUtilities.isNetworkAvailable(mContext)) {
            Toast.makeText(mContext, "Network connection unavailable.",
                    Toast.LENGTH_SHORT).show();
            finish();
        } else {
            new GetLineItemsTask().execute();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mItemsToAdd.clear();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mItemsToAdd.clear();
    }

    private static OnClickListener headerButtonListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.header_button_back:
                    mActivity.onBackPressed();
                    break;
                case R.id.header_standard_button_right:

                    for (int i = 0; i < mItemsToAdd.size(); i++) {

//					mItemsToAdd.get(i).setUserAdded(true);

                        switch (lineItemAddViewMode) {
                            case Constants.LINE_ITEM_ADD_VIEW_FROM_ESTIMATE:
                                ActivityEstimateView.hasAnyChanges = true;
                                AppDataSingleton.getInstance().getEstimate().getLineItems()
                                        .add(mItemsToAdd.get(i));
                                break;
                            case Constants.LINE_ITEM_ADD_VIEW_FROM_INVOICE:
                                ActivityEstimateView.hasAnyChanges = true;
                                AppDataSingleton.getInstance().getInvoice().getLineItems()
                                        .add(mItemsToAdd.get(i));
                                break;
                            case Constants.LINE_ITEM_ADD_VIEW_FROM_PRICEBOOK_LIST:
                                // TODO - Huh?
                                break;
                            default:
                                // Nothing
                                break;
                        }
                    }

                    ActivityPricebookListView.currentDepth = 0;
                    mItemsToAdd.clear(); // This should be all emptied
                    if (ActivityPricebookListView.mActivity != null)
                        ActivityPricebookListView.mActivity.finish();
                    mActivity.finish();
                    break;
                default:
                    // Nothing
                    break;
            }
        }
    };

    private static OnItemClickListener mLineItemListListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View v, int position,
                                long id) {

            TextView tv = (TextView) v
                    .findViewById(R.id.layout_row_product_name_price_description_text_name);
            String selectedItemName = tv.getText().toString();

            switch (overrideSearchMode) {
                case Constants.PRICE_BOOK_SEARCH_MODE_DEFAULT:
                    mSelectedListLineItem = new LineItem();
                    // mSelectedListLineItem =
                    // CommonUtilities.invoiceLineItem[position];

                    for (LineItem item : lineItemsList) {
                        if (item.getName().equals(selectedItemName)) {
                            mSelectedListLineItem = item.copy();
                            break;
                        }
                    }
                    mSelectedListLineItem.cost = mSelectedListLineItem.cost
                            .setScale(2, BigDecimal.ROUND_HALF_UP);

                    break;

                // These two both respond similarly
                case Constants.PRICE_BOOK_SEARCH_MODE_ONE_TIER:
                case Constants.PRICE_BOOK_SEARCH_MODE_TWO_TIER:
                    mSelectedListLineItem = new LineItem();
                    for (LineItem item : lineItemsList) {
                        if (item.getName().equals(selectedItemName)) {
                            mSelectedListLineItem = item.copy();
                            break;
                        }
                    }

                    if (!(mSelectedListLineItem.additionalCost.doubleValue() == 0.0)) {

                        mSelectedListLineItem.cost = mSelectedListLineItem.cost
                                .setScale(2, BigDecimal.ROUND_HALF_UP);
                    } else {


                        mSelectedListLineItem.setUsingAdditionalCost(false);
                        mSelectedListLineItem.cost = mSelectedListLineItem.cost
                                .setScale(2, BigDecimal.ROUND_HALF_UP);
                    }

                    break;
                default:
                    // Nothing
                    break;
            }

            DialogLineItemStandard standardDialog = new DialogLineItemStandard(
                    mContext, mSelectedListLineItem);

            standardDialog
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            headerTextLabelValue.setText(String
                                    .valueOf(mItemsToAdd.size()));
                        }
                    });

            standardDialog.show();

        }
    };

    private void initHeader() {
        headerLayout = (LinearLayout) findViewById(R.id.activity_header);
        headerButtonSave = (TextView) headerLayout
                .findViewById(R.id.header_standard_button_right);
        headerButtonBack = (ImageView) headerLayout
                .findViewById(R.id.header_button_back);
        headerTextLabelValue = (TextView) headerLayout
                .findViewById(R.id.header_standard_textview_label_value);

        if (mItemsToAdd != null) {
            if (!mItemsToAdd.isEmpty()) {
                int addedItemCount = mItemsToAdd.size();
                headerTextLabelValue.setText(String.valueOf(addedItemCount));
            } else
                headerTextLabelValue.setText("0");
        } else {
            headerTextLabelValue.setText("0"); // No added items yet
        }

        mActivity = ActivityLineItemAdd.this;
        mContext = this;

        final QuickAction accountMenu;
        accountMenu = AccountMenu.setupMenu(mContext, mActivity);

        headerButtonUser = (ImageView) headerLayout
                .findViewById(R.id.header_button_user);

        headerButtonUser.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                accountMenu.show(v);
                accountMenu.setAnimStyle(QuickAction.ANIM_GROW_FROM_CENTER);
            }
        });

        headerButtonBack.setOnClickListener(headerButtonListener);
    }

    /*
     * Custom Functions
     */
    private static void setupUI() {
        headerButtonSave.setOnClickListener(headerButtonListener);

        mActivity.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); // hides
        // keyboard
        // from
        // going
        // to
        // search
        // box

        edittextSearch = (EditText) mActivity
                .findViewById(R.id.activity_add_line_item_standard_edittext_search);
        listviewProductList = (ListView) mActivity
                .findViewById(R.id.activity_add_line_item_standard_listview);

        // String[] sizeArray = null;
        ArrayList<String> lineItemsArray = new ArrayList<String>();

        switch (overrideSearchMode) {
            case Constants.PRICE_BOOK_SEARCH_MODE_DEFAULT:
                if (ServicePlanId != 0) {
                    if (AppDataSingleton.getInstance().getPricebookProductList() != null) {
                        lineItemsList = AppDataSingleton.getInstance()
                                .getPricebookProductList();
                        for (int i = 0; i < AppDataSingleton.getInstance()
                                .getPricebookProductList().size(); i++) {
                            lineItemsArray.add(AppDataSingleton.getInstance()
                                    .getPricebookProductList().get(i).getName()
                                    + "|" + i);
                        }
                    }
                } else {
                    // if (CommonUtilities.invoiceLineItem != null) {
                    if (AppDataSingleton.getInstance().getLineItemList() != null) {
                        lineItemsList = AppDataSingleton.getInstance()
                                .getLineItemList();
                        for (int i = 0; i < AppDataSingleton.getInstance()
                                .getLineItemList().size(); i++) {
                            lineItemsArray.add(AppDataSingleton.getInstance()
                                    .getLineItemList().get(i).getName()
                                    + "|" + i);
                        }
                    }
                }
                break;

            default:

                lineItemsList = AppDataSingleton.getInstance()
                        .getPricebookProductList();
                for (int i = 0; i < AppDataSingleton.getInstance()
                        .getPricebookProductList().size(); i++) {
                    lineItemsArray.add(AppDataSingleton.getInstance()
                            .getPricebookProductList().get(i).getName()
                            + "|" + i);
                }
                break;
        }

        adapter = new StandardLineItemAdapter(mActivity,
                R.layout.row_product_name_price_description, lineItemsArray);

        listviewProductList.setAdapter(adapter);

        listviewProductList.setOnItemClickListener(mLineItemListListener);

		/* Search box */
        edittextSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                adapter.getFilter().filter(s);
            }
        });
    }

    static class ViewHolder {
        TextView name;
        TextView price;
        TextView description;
    }

    public static class StandardLineItemAdapter extends ArrayAdapter<String> {

        private ArrayList<String> original;
        private ArrayList<String> fitems;
        private Filter filter;

        private NumberFormatTool currency;

        public StandardLineItemAdapter(Context context, int textViewResourceId,
                                       ArrayList<String> items) {
            super(context, textViewResourceId, items);
            original = new ArrayList<String>(items);
            fitems = new ArrayList<String>(items);

            currency = new NumberFormatTool();
            currency.setType("currency");
            currency.setCurrencySymbol(
                    UserUtilitiesSingleton.getInstance().user.getCountryInfo().getCurrencySymbol());
            currency.setLocale(UserUtilitiesSingleton.getInstance().user.getCountryInfo().getLocalCode());

            notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            ViewHolder holder; // to reference the child views for later actions

            if (v == null) {
                LayoutInflater inflater = mActivity.getLayoutInflater();
                v = inflater.inflate(R.layout.row_product_name_price_description,
                        parent, false);

                // cache view fields into the holder
                holder = new ViewHolder();
                holder.name = (TextView) v
                        .findViewById(R.id.layout_row_product_name_price_description_text_name);
                holder.price = (TextView) v
                        .findViewById(R.id.layout_row_product_name_price_description_text_price);
                holder.description = (TextView) v
                        .findViewById(R.id.layout_row_product_name_price_description_text_description);

                // associate the holder with the view for later lookup
                v.setTag(holder);
            } else {
                // view already exists, get the holder instance from the view
                holder = (ViewHolder) v.getTag();
            }

            int numOfItem = Integer.parseInt(fitems.get(position).substring(
                    fitems.get(position).indexOf("|") + 1,
                    fitems.get(position).length()));

            // customerLabel.setText(fitems.get(position).substring(0,
            // fitems.get(position).indexOf("|")));

            if (overrideSearchMode != Constants.PRICE_BOOK_SEARCH_MODE_DEFAULT
                    || ServicePlanId != 0) {

                holder.name.setText(fitems.get(position).substring(0,
                        fitems.get(position).indexOf("|")));

                // productName.setText(CommonUtilities.mPricebookProduct[position]
                // .getName());

                holder.price.setText(currency.format(lineItemsList.get(numOfItem).cost));

                // +
                // format.format(AppDataSingleton.getInstance().getPricebookProductList().get(
                // numOfItem).getCost()));
                holder.description.setText(lineItemsList.get(numOfItem)
                        .getDescription());
            } else {
                holder.name.setText(fitems.get(position).substring(0,
                        fitems.get(position).indexOf("|")));
                // productName
                // .setText(CommonUtilities.invoiceLineItem[position]
                // .getName());
                // productPrice
                // .setText("$"
                // + format.format(CommonUtilities.invoiceLineItem[numOfItem]
                // .getCost()));
                // productDescription
                // .setText(CommonUtilities.invoiceLineItem[numOfItem]
                // .getDescription());

                holder.price.setText(currency.format(
                        lineItemsList.get(numOfItem).cost));

                // +
                // format.format(AppDataSingleton.getInstance().getLineItemList().get(numOfItem)
                // .getCost()));
                holder.description.setText(lineItemsList.get(numOfItem)
                        .getDescription());
            }
            return v;
        }

        @Override
        public Filter getFilter() {
            if (filter == null)
                filter = new NameFilter();

            return filter;
        }

        private class NameFilter extends Filter {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                String prefix = constraint.toString().toLowerCase();

                if (prefix == null || prefix.length() == 0) {
                    ArrayList<String> list = new ArrayList<String>(original);
                    results.values = list;
                    results.count = list.size();
                } else {
                    final ArrayList<String> list = new ArrayList<String>(
                            original);
                    final ArrayList<String> nlist = new ArrayList<String>();
                    int count = list.size();

                    for (int i = 0; i < count; i++) {
                        final String item = list.get(i);
                        final String value = item.toLowerCase();

                        if (value.contains(prefix)) {
                            nlist.add(item);
                        }
                    }
                    results.values = nlist;
                    results.count = nlist.size();
                }
                return results;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,
                                          FilterResults results) {
                fitems = (ArrayList<String>) results.values;

                clear();
                int count = fitems.size();
                for (int i = 0; i < count; i++) {
                    String item = fitems.get(i);
                    add(item);
                }
            }

        }

    }

    private class GetLineItemsTask extends BaseUiReportTask<String> {

        GetLineItemsTask() {
            super(ActivityLineItemAdd.this,
                    R.string.async_task_string_loading_products);
        }

        @Override
        protected void onSuccess() {
            setupUI();
        }

        @Override
        protected boolean taskBody(final String... args) throws Exception {

            switch (overrideSearchMode) {
                case Constants.PRICE_BOOK_SEARCH_MODE_DEFAULT:
                    if (ServicePlanId == 0)
                        RESTLineItemList.query();
                    else
                        RESTPricebook.queryForServicePlan(ServicePlanId,
                                UserUtilitiesSingleton.getInstance().user
                                        .getOwnerId(), -1, -1);
                    break;
                default:
                    if (ServicePlanId == 0)
                        if (mSelectedApptId == 0)
                            RESTPricebook.queryForEstimate(AppDataSingleton.getInstance().getCustomer().getId(),
                                    manufacturerId, groupCodeId);
                        else
                            RESTPricebook.query(mSelectedApptId, manufacturerId,
                                    groupCodeId);
                    else
                        RESTPricebook.queryForServicePlan(ServicePlanId,
                                UserUtilitiesSingleton.getInstance().user
                                        .getOwnerId(), manufacturerId, groupCodeId);
                    break;
            }

            return true;
        }
    }
}