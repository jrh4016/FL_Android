package com.skeds.android.phone.business.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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
import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.GroupCode;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.LineItem;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Manufacturer;
import com.skeds.android.phone.business.Utilities.General.CommonUtilities;
import com.skeds.android.phone.business.Utilities.General.Constants;
import com.skeds.android.phone.business.Utilities.General.NumberFormatTool;
import com.skeds.android.phone.business.Utilities.General.UserUtilitiesSingleton;
import com.skeds.android.phone.business.Utilities.REST.Objects.RESTLineItemList;
import com.skeds.android.phone.business.Utilities.REST.Objects.RESTPricebook;
import com.skeds.android.phone.business.Utilities.REST.Objects.RESTPricebookCodeList;
import com.skeds.android.phone.business.Utilities.REST.Objects.RESTPricebookGroupCodeList;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;

import static com.skeds.android.phone.business.Utilities.General.AppDataSingleton.getInstance;

public class ActivityPricebookListView extends BaseSkedsActivity {

    public static Activity mActivity;

    private static LinearLayout headerLayout;
    private static ImageView headerButtonUser;
    private static ImageView headerButtonBack;

    private static TextView textNoItems;
    private static ListView listProductList;
    private static LinkedList<String> mListItems;
    private static EditText edittextSearch;
    private static ArrayAdapter<LineItem> adapter = null;

    public static int mSelectedGroupCodeId;
    public static int mSelectedManufacturerCodeId;

    public static int mSelectedApptId = 0;

    public static int mSearchMode = -1;
    private int mPricebookViewMode = -1;
    private int mLineItemAddViewMode = -1;

    public static int currentDepth = 0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_pricebook_list);

        initHeader();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mPricebookViewMode = bundle.getInt(Constants.EXTRA_PRICEBOOK_LIST_VIEW_MODE, -1);
            mSearchMode = bundle.getInt(Constants.EXTRA_PRICEBOOK_SEARCH_MODE, -1);
        }

        if (mSearchMode == -1) {
            if (getInstance().getAppointment().getSearchModeOverride() == -1)
                mSearchMode = getInstance().getPriceBookSearchMode();
            else
                mSearchMode = getInstance().getAppointment().getSearchModeOverride();
        }

        textNoItems = (TextView) findViewById(R.id.activity_pricebook_list_textview_no_items);
        listProductList = (ListView) findViewById(R.id.activity_pricebook_list_listview_pricebook);
        edittextSearch = (EditText) findViewById(R.id.activity_pricebook_list_edittext_search);

        if (mPricebookViewMode == Constants.PRICEBOOK_LIST_VIEW_FROM_ESTIMATE) {
            mLineItemAddViewMode = Constants.LINE_ITEM_ADD_VIEW_FROM_ESTIMATE;
        } else if (mPricebookViewMode == Constants.PRICEBOOK_LIST_VIEW_FROM_INVOICE) {
            mLineItemAddViewMode = Constants.LINE_ITEM_ADD_VIEW_FROM_INVOICE;
        } else if (mPricebookViewMode == Constants.PRICEBOOK_LIST_VIEW_FROM_DASHBOARD) {
            mLineItemAddViewMode = Constants.LINE_ITEM_ADD_VIEW_FROM_PRICEBOOK_LIST;
        }

        if (mSearchMode == Constants.PRICE_BOOK_SEARCH_MODE_DEFAULT)
            new GetProductsTask().execute(); // for SIMPLE MODE
        else
            new GetCodeListTask().execute(); // for ONE TIER, TWO TIER MODES

    }

    @Override
    public void onBackPressed() {

        if (currentDepth > 0) {
            currentDepth--;

            switch (currentDepth) {
                case 0:
                    setupCodesList();
                    break;
                case 1:
                    setupManufacturerCodeList();
                    break;
                case 2:
                    setupPricebookCatalog();
                    break;
                default:
                    // Nothing
                    break;
            }
        } else {
            super.onBackPressed();
        }
    }


    private OnItemClickListener mPricebookListClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View v, int position,
                                long id) {

            boolean viewAll = false;
            if (v.getTag() != null) {
                String viewAllStr = ((ViewHolder) v.getTag()).viewAll;
                if (viewAllStr != null)
                    if ("viewall".equals(viewAllStr)) {
                        viewAll = true;
                    }
            }

            TextView nameField = (TextView) v.findViewById(R.id.invoiceListItemName);
            if (nameField == null)
                nameField = (TextView) v.findViewById(R.id.layout_row_pricebook_catalog_item_textview_name);
            if (nameField == null)
                nameField = (TextView) v.findViewById(R.id.layout_row_product_name_price_description_text_name);


            String selectedName = nameField.getText().toString();


            switch (mSearchMode) {
                case Constants.PRICE_BOOK_SEARCH_MODE_ONE_TIER:
                    if (!viewAll) {

                        for (GroupCode groupCode : getInstance()
                                .getPricebookGroupCodeList()) {
                            if (groupCode.getName().equals(selectedName)) {
                                mSelectedGroupCodeId = groupCode.getId();
                                break;
                            }
                        }
                        new GetPriceBookTask(mSelectedGroupCodeId, -1).execute();
                    } else
                        new GetPriceBookTask(-1, -1).execute();
                    break;

                case Constants.PRICE_BOOK_SEARCH_MODE_TWO_TIER:
                    if (!viewAll) {

                        if (currentDepth == 0) {
                            for (GroupCode groupCode : getInstance().getPricebookGroupCodeList()) {
                                if (groupCode.getName().equals(selectedName)) {
                                    mSelectedGroupCodeId = groupCode.getId();
                                    break;
                                }
                            }

                            new GetManufacturerListTask().execute();
                        } else {
                            for (Manufacturer manufacturer : getInstance()
                                    .getPricebookManufacturerList()) {
                                if (manufacturer.getName().equals(selectedName)) {
                                    mSelectedManufacturerCodeId = manufacturer.getId();
                                    break;
                                }
                            }
                            new GetPriceBookTask(mSelectedGroupCodeId, mSelectedManufacturerCodeId).execute();
                        }
                    } else if (currentDepth == 0) {
                        new GetPriceBookTask(-1, -1).execute();
                    } else {
                        new GetPriceBookTask(mSelectedGroupCodeId, -1).execute();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private static OnClickListener headerListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.header_button_back:
                    mActivity.onBackPressed();
                    break;
                default:
                    break;
            }
        }
    };

    private void initHeader() {
        headerLayout = (LinearLayout) findViewById(R.id.activity_header);

        mActivity = ActivityPricebookListView.this;

        final QuickAction accountMenu;
        accountMenu = AccountMenu.setupMenu(mActivity, mActivity);

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
        headerButtonBack.setOnClickListener(headerListener);
    }


    private void setupCodesList() {
        edittextSearch.setVisibility(View.GONE);

        if (getInstance().getPricebookGroupCodeList() != null) {
            textNoItems.setVisibility(View.GONE);

            mListItems = new LinkedList<String>();
            mListItems.add("<View All>");
            for (int i = 0; i < getInstance().getPricebookGroupCodeList().size(); i++)
                mListItems.add(getInstance().getPricebookGroupCodeList().get(i).getName());


            listProductList.setOnItemClickListener(mPricebookListClickListener);
            listProductList.setAdapter(new MyCustomAdapter(mActivity,
                    R.layout.row_pricebook_list_item, mListItems));

        } else {
            textNoItems.setVisibility(View.VISIBLE);
        }
    }

    private void setupManufacturerCodeList() {
        edittextSearch.setVisibility(View.GONE);

        if (getInstance().getPricebookManufacturerList() != null) {
            textNoItems.setVisibility(View.GONE);

            mListItems = new LinkedList<String>();

            mListItems.add("<View All>");
            for (int i = 0; i < getInstance().getPricebookManufacturerList().size(); i++)
                mListItems.add(getInstance().getPricebookManufacturerList().get(i)
                        .getName());

            listProductList.setOnItemClickListener(mPricebookListClickListener);
            listProductList.setAdapter(new MyCustomAdapter(mActivity,
                    R.layout.row_pricebook_list_item, mListItems));

        } else {
            textNoItems.setVisibility(View.VISIBLE);
        }
    }

    private static void setupPricebookCatalog() {
        edittextSearch.setVisibility(View.VISIBLE);
        listProductList.setOnItemClickListener(null);

        ArrayList<LineItem> pricebookItemsArray = new ArrayList<LineItem>();

        if (mSearchMode == Constants.PRICE_BOOK_SEARCH_MODE_DEFAULT) {
            for (int i = 0; i < getInstance().getLineItemList().size(); i++) {
                pricebookItemsArray.add(getInstance().getLineItemList().get(i));
            }

            adapter = new StandardItemAdapter(mActivity,
                    R.layout.row_pricebook_catalog_item, pricebookItemsArray);

            listProductList.setAdapter(adapter);

            edittextSearch.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    adapter.getFilter().filter(s);
                }
            });

        } else if (getInstance().getPricebookProductList() != null) {

            for (int i = 0; i < getInstance().getPricebookProductList().size(); i++) {
                pricebookItemsArray.add(getInstance().getPricebookProductList().get(i));
            }

            adapter = new CatalogAdapter(mActivity,
                    R.layout.row_pricebook_catalog_item, pricebookItemsArray);

            listProductList.setAdapter(adapter);

            edittextSearch.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    adapter.getFilter().filter(s);
                }
            });
        }

    }


    static class ViewHolder {
        TextView name;
        TextView price;
        TextView priceName;
        TextView description;

        String viewAll;
    }

    public static class MyCustomAdapter extends ArrayAdapter<String> {

        public MyCustomAdapter(Context context, int textViewResourceId,
                               LinkedList<String> objects) {
            super(context, textViewResourceId, objects);
            notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            ViewHolder holder; // to reference the child views for later actions

            if (v == null) {
                LayoutInflater vi = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.row_pricebook_list_item, null);

                // cache view fields into the holder
                holder = new ViewHolder();
                holder.name = (TextView) v.findViewById(R.id.invoiceListItemName);
                holder.price = (TextView) v.findViewById(R.id.invoiceListItemDate);

                // associate the holder with the view for later lookup
                v.setTag(holder);
            } else {
                // view already exists, get the holder instance from the view
                holder = (ViewHolder) v.getTag();
            }


            if (position == 0) {
                holder.name.setText("<View All>");
                holder.viewAll = "viewall";
            } else {
                holder.viewAll = "not_viewall";
                switch (mSearchMode) {
                    case Constants.PRICE_BOOK_SEARCH_MODE_ONE_TIER:
                        holder.name.setText(getInstance().getPricebookGroupCodeList()
                                .get(position - 1).getName());
                        break;

                    case Constants.PRICE_BOOK_SEARCH_MODE_TWO_TIER:
                        if (currentDepth == 0) {
                            holder.name.setText(getInstance()
                                    .getPricebookGroupCodeList()
                                    .get(position - 1).getName());
                        } else {

                            if (!getInstance()
                                    .getPricebookManufacturerList().isEmpty())
                                holder.name.setText(getInstance()
                                        .getPricebookManufacturerList()
                                        .get(position - 1).getName());
                        }
                        break;
                    default:
                        // Nothing
                        break;
                }
            }

            holder.price.setText("");

            return v;
        }

    }

    public static class CatalogAdapter extends ArrayAdapter<LineItem> {

        private ArrayList<LineItem> original;
        private ArrayList<LineItem> fitems;
        private Filter filter;
        private NumberFormatTool currency;

        public CatalogAdapter(Context context, int textViewResourceId,
                              ArrayList<LineItem> items) {
            super(context, textViewResourceId, items);
            this.original = new ArrayList<LineItem>(items);
            this.fitems = new ArrayList<LineItem>(items);
            currency = new NumberFormatTool();
            currency.setType("currency");
            currency.setCurrencySymbol(
                    UserUtilitiesSingleton.getInstance().user.getCountryInfo().getCurrencySymbol());
            currency.setLocale(UserUtilitiesSingleton.getInstance().user.getCountryInfo().getLocalCode());
            this.notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            ViewHolder holder; // to reference the child views for later actions

            if (v == null) {
                LayoutInflater vi = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.row_pricebook_catalog_item, null);

                // cache view fields into the holder
                holder = new ViewHolder();
                holder.name = (TextView) v.findViewById(R.id.layout_row_pricebook_catalog_item_textview_name);
                holder.price = (TextView) v.findViewById(R.id.layout_row_pricebook_catalog_item_textview_price_values);
                holder.priceName = (TextView) v.findViewById(R.id.layout_row_pricebook_catalog_item_textview_price_names);
                holder.description = (TextView) v.findViewById(R.id.layout_row_pricebook_catalog_item_textview_description);

                // associate the holder with the view for later lookup
                v.setTag(holder);
            } else {
                // view already exists, get the holder instance from the view
                holder = (ViewHolder) v.getTag();
            }


            DecimalFormat format = new DecimalFormat("#0.00");

            holder.name.setText(getItem(position).getName());
            holder.description.setText(getItem(position).getDescription());

            if (!getInstance().getPricebookProductList().isEmpty()) {
                StringBuilder builderNameColumn = new StringBuilder();
                StringBuilder builderPriceColumn = new StringBuilder();
                for (int i = 0; i < getItem(position).productCost.size(); i++) {

                    builderNameColumn
                            .append(getItem(position).productCost.get(i).getName());

                    builderPriceColumn.append(currency
                            .format(getItem(position).productCost.get(i).getCost()));

                    if (i != (getItem(position).productCost.size() - 1)) {
                        builderNameColumn.append("\n");
                        builderPriceColumn.append("\n");
                    }

                }
                holder.priceName.setText(builderNameColumn.toString());
                holder.price.setText(builderPriceColumn.toString());
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

                if (prefix.isEmpty()) {
                    results.values = original;
                    results.count = original.size();
                } else {
                    final ArrayList<LineItem> list = new ArrayList<LineItem>(original);
                    final ArrayList<LineItem> nlist = new ArrayList<LineItem>();

                    for (final LineItem item : list) {
                        final String name = item.getName().toLowerCase();
                        final String description = item.getDescription().toLowerCase();

                        if (name.contains(prefix) || (description.contains(prefix))) {
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
                fitems = (ArrayList<LineItem>) results.values;

                clear();
                for (LineItem item : fitems) {
                    add(item);
                }
                notifyDataSetChanged();
            }

        }

    }

    private static class StandardItemAdapter extends ArrayAdapter<LineItem> {

        private ArrayList<LineItem> fitems;
        private ArrayList<LineItem> fitemsOriginal;
        private Filter filter;
        private NumberFormatTool currency;

        public StandardItemAdapter(Context context, int textViewResourceId,
                                   ArrayList<LineItem> items) {
            super(context, textViewResourceId, items);
            this.fitems = new ArrayList<LineItem>();
            fitemsOriginal = new ArrayList<LineItem>();
            currency = new NumberFormatTool();
            currency.setType("currency");
            currency.setCurrencySymbol(
                    UserUtilitiesSingleton.getInstance().user.getCountryInfo().getCurrencySymbol());
            currency.setLocale(UserUtilitiesSingleton.getInstance().user.getCountryInfo().getLocalCode());
            this.notifyDataSetChanged();

            for (int i = 0; i < getInstance().getLineItemList().size(); i++) {
                fitems.add(getInstance().getLineItemList().get(i));
                fitemsOriginal.add(getInstance().getLineItemList().get(i));
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            ViewHolder holder; // to reference the child views for later actions

            if (v == null) {
                LayoutInflater vi = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.row_product_name_price_description, null);

                // cache view fields into the holder
                holder = new ViewHolder();
                holder.name = (TextView) v.findViewById(R.id.layout_row_product_name_price_description_text_name);
                holder.price = (TextView) v.findViewById(R.id.layout_row_product_name_price_description_text_price);
                holder.description = (TextView) v.findViewById(R.id.layout_row_product_name_price_description_text_description);

                // associate the holder with the view for later lookup
                v.setTag(holder);
            } else {
                // view already exists, get the holder instance from the view
                holder = (ViewHolder) v.getTag();
            }

            holder.name.setText(getInstance().getLineItemList().get(position).getName());


            holder.price.setText(currency.format(
                    getInstance().getLineItemList().get(position).cost));
            holder.description.setText(getInstance().getLineItemList().get(position)
                    .getDescription());

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

                if (prefix.length() == 0) {
                    ArrayList<LineItem> list = new ArrayList<LineItem>(fitemsOriginal);
                    results.values = list;
                    results.count = list.size();
                } else {
                    final ArrayList<LineItem> list = new ArrayList<LineItem>(fitemsOriginal);
                    final ArrayList<LineItem> nlist = new ArrayList<LineItem>();
                    int count = list.size();

                    for (int i = 0; i < count; i++) {
                        final LineItem item = list.get(i);
                        final String value = item.getName().toLowerCase();

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
                fitems = (ArrayList<LineItem>) results.values;

                clear();
                int count = fitems.size();
                for (int i = 0; i < count; i++) {
                    LineItem item = fitems.get(i);
                    add(item);
                }
            }

        }

    }


    private final class GetPriceBookTask extends BaseUiReportTask<String> {

        private Intent i;
        private int groupCodeId;
        private int manufacturerId;


        GetPriceBookTask(int groupCodeId, int manufacturerId) {
            super(ActivityPricebookListView.this,
                    R.string.async_task_string_loading_products);
            i = new Intent(mActivity, ActivityLineItemAdd.class);
            this.groupCodeId = groupCodeId;
            this.manufacturerId = manufacturerId;
        }

        @Override
        protected void onSuccess() {
            if (mPricebookViewMode != Constants.PRICEBOOK_LIST_VIEW_FROM_DASHBOARD) {

                i.putExtras(getIntent().getExtras());
                i.putExtra(Constants.EXTRA_LINE_ITEM_ADD_VIEW_MODE, mLineItemAddViewMode);
                if (getInstance()
                        .getEstimate().isServicePlanUsedForPricing())
                    i.putExtra("ServicePlanId", getInstance().getEstimate().getServicePlanId());
                startActivity(i);
            } else {
                currentDepth++;
                setupPricebookCatalog();
            }
        }

        @Override
        protected boolean taskBody(final String... args) throws Exception {

            if (!CommonUtilities.isNetworkAvailable(mActivity)) {
                Toast.makeText(mActivity, "Network connection unavailable.",
                        Toast.LENGTH_SHORT).show();
                return true;
            }

            int ownerId = UserUtilitiesSingleton.getInstance().user.getOwnerId();

            switch (mPricebookViewMode) {
                case Constants.PRICEBOOK_LIST_VIEW_FROM_INVOICE:

//				RESTPricebook.query(mSelectedApptId, manufacturerId, groupCodeId);
                    i.putExtra("SelectedApptId", mSelectedApptId);
                    i.putExtra("manufacturerId", manufacturerId);
                    i.putExtra("groupCodeId", groupCodeId);
                    break;

                case Constants.PRICEBOOK_LIST_VIEW_FROM_ESTIMATE:
                    if (getInstance()
                            .getEstimate().isServicePlanUsedForPricing())
                        i.putExtra("ServicePlanId", getInstance().getEstimate().getServicePlanId());
                    i.putExtra("SelectedApptId", mSelectedApptId);
                    i.putExtra("manufacturerId", manufacturerId);
                    i.putExtra("groupCodeId", groupCodeId);
                    break;

                case Constants.PRICEBOOK_LIST_VIEW_FROM_DASHBOARD:
                    RESTPricebook.queryForOwner(ownerId, manufacturerId, groupCodeId);
                    if (mSearchMode == Constants.PRICE_BOOK_SEARCH_MODE_TWO_TIER) {
                        if (mSelectedManufacturerCodeId == -1
                                && mSelectedGroupCodeId == -1)
                            currentDepth = 2;
                    }
                    break;
                default:
                    // Nothing
                    break;
            }

            return true;
        }
    }


    // This is tier 2 only
    private final class GetManufacturerListTask
            extends
            BaseUiReportTask<String> {
        GetManufacturerListTask() {
            super(ActivityPricebookListView.this,
                    R.string.async_task_string_loading_products);
        }

        @Override
        protected void onSuccess() {
            currentDepth += 1;
            setupManufacturerCodeList();
        }

        @Override
        protected boolean taskBody(final String... args) throws Exception {
            if (!CommonUtilities.isNetworkAvailable(mActivity)) {
                Toast.makeText(mActivity, "Network connection unavailable.",
                        Toast.LENGTH_SHORT).show();
                return true;
            }
            int ownerId = UserUtilitiesSingleton.getInstance().user.getOwnerId();

            RESTPricebookGroupCodeList.query(ownerId, mSelectedGroupCodeId);
            return true;
        }
    }


    private final class GetCodeListTask extends BaseUiReportTask<String> {
        GetCodeListTask() {
            super(ActivityPricebookListView.this,
                    R.string.async_task_string_loading_products);
        }

        @Override
        protected void onSuccess() {
            setupCodesList();
        }

        @Override
        protected boolean taskBody(final String... args) throws Exception {
            if (!CommonUtilities.isNetworkAvailable(mActivity)) {
                Toast.makeText(mActivity, "Network connection unavailable.",
                        Toast.LENGTH_SHORT).show();
                return true;
            }
            int ownerId = UserUtilitiesSingleton.getInstance().user.getOwnerId();
            try {
                RESTPricebookCodeList.query(ownerId);
            } catch (Exception e) {
            }
            return true;
        }
    }

    private final class GetProductsTask
            extends
            BaseUiReportTask<String> {
        GetProductsTask() {
            super(ActivityPricebookListView.this,
                    R.string.async_task_string_loading_products);
        }

        @Override
        protected void onSuccess() {
            setupPricebookCatalog();
        }

        @Override
        protected boolean taskBody(final String... args) throws Exception {
            if (!CommonUtilities.isNetworkAvailable(mActivity)) {
                Toast.makeText(mActivity, "Network connection unavailable.",
                        Toast.LENGTH_SHORT).show();
                return true;
            }
            try {
                RESTLineItemList.query();
            } catch (Exception e) {

            }
            return true;
        }
    }

}