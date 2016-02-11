package com.skeds.android.phone.business.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.skeds.android.phone.business.AsyncTasks.BaseUiReportTask;
import com.skeds.android.phone.business.Dialogs.DialogErrorPopup;
import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.UserUtilitiesSingleton;
import com.skeds.android.phone.business.Utilities.REST.Objects.RESTCustomerList;
import com.skeds.android.phone.business.Utilities.REST.Objects.RESTLargeCustomerList;
import com.skeds.android.phone.business.activities.ActivityCustomerAddEdit;
import com.skeds.android.phone.business.core.SkedsApplication;

import java.util.ArrayList;

public class CustomerListFragment extends BaseSkedsFragment {

    OnHeadlineSelectedListener mCallback;

    private static final String DEBUG_TAG = "[Customer List]";

    private PullToRefreshListView mPullRefreshListView;
    private ListView listviewCustomerList;

    private EditText edittextSearch;
    private ImageView buttonSearch;
    private ImageView buttonAddCustomer;

    private String searchString;

    private ArrayAdapter<String> adapter;
    private ArrayList<String> customerNames;

    private Activity mActivity;

    public interface OnHeadlineSelectedListener {
        public void onArticleSelected(int id);
    }

    private OnItemClickListener mCustomerListClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View v, int position,
                                long id) {

            String nameToUse = customerNames.get(position - 1).toString();
            int custId = Integer.parseInt(nameToUse.substring(
                    nameToUse.indexOf("|") + 1, nameToUse.length()));
            mCallback.onArticleSelected(custId);
        }
    };

    private OnClickListener mButtonListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.header_customer_search_button_search:
                    if (UserUtilitiesSingleton.getInstance().user
                            .isUseLargeScaleSearch()) {
                        searchString = edittextSearch.getText().toString();
                        new LargeSearchTask().execute();
                    }
                    break;
                case R.id.header_customer_search_button_add:
                    ActivityCustomerAddEdit.isEditMode = false;
                    Intent i = new Intent(mActivity, ActivityCustomerAddEdit.class);
                    startActivityForResult(i, 1);
                    // finish();
                    break;
                default:
                    // Nothing
                    break;
            }
        }
    };

    private TextWatcher textWatcherNormalSearch = new TextWatcher() {

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
            if (UserUtilitiesSingleton.getInstance().user
                    .isUseLargeScaleSearch()) {
                if (s.length() > 0)
                    adapter.getFilter().filter(s);
                else {
                    setupUI();
                }
            } else {
                adapter.getFilter().filter(s);
            }
        }
    };

    private void setupUI() {

        mPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener() {

            @Override
            public void onRefresh(PullToRefreshBase refreshView) {
                // Do work to refresh the list here.
                new PullToRefreshTask().execute();
            }
        });

        listviewCustomerList = mPullRefreshListView.getRefreshableView();

        // This will stop the keyboard from automatically popping up (I hope)
        mActivity.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        buttonSearch.setOnClickListener(mButtonListener);
        buttonAddCustomer.setOnClickListener(mButtonListener);

        if (AppDataSingleton.getInstance().getCustomerList() != null) {

            customerNames = new ArrayList<String>();
            for (int i = 0; i < AppDataSingleton.getInstance()
                    .getCustomerList().size(); i++) {
                customerNames.add(AppDataSingleton.getInstance()
                        .getCustomerList().get(i).getFirstName()
                        + ' '
                        + AppDataSingleton.getInstance().getCustomerList()
                        .get(i).getLastName()
                        + '|'
                        + AppDataSingleton.getInstance().getCustomerList()
                        .get(i).getId());
            }

            listviewCustomerList
                    .setOnItemClickListener(mCustomerListClickListener);

            adapter = new MyCustomAdapter(mActivity,
                    R.layout.row_equipment_item, customerNames);
            listviewCustomerList.setAdapter(adapter);
            listviewCustomerList.setFastScrollEnabled(true);
            listviewCustomerList.setTextFilterEnabled(true);

            edittextSearch.addTextChangedListener(textWatcherNormalSearch);
        } else {
            listviewCustomerList.setAdapter(null);
            edittextSearch.removeTextChangedListener(textWatcherNormalSearch);
        }

        edittextSearch.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN)
                        && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    if (buttonSearch.getVisibility() == View.VISIBLE) {
                        searchString = edittextSearch.getText().toString();
                        new LargeSearchTask().execute();
                    }
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (OnHeadlineSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivity = getActivity();
        adapter = null;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_layout_customer_list_view,
                container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        edittextSearch = (EditText) mActivity
                .findViewById(R.id.header_customer_search_edittext_search);
        buttonSearch = (ImageView) mActivity
                .findViewById(R.id.header_customer_search_button_search);
        buttonAddCustomer = (ImageView) mActivity
                .findViewById(R.id.header_customer_search_button_add);

        mPullRefreshListView = (PullToRefreshListView) mActivity
                .findViewById(R.id.activity_customer_list_listview_customers);

        // This will stop the keyboard from automatically popping up (I hope)
        mActivity.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }

    @Override
    public void onResume() {
        super.onResume();

        if (!UserUtilitiesSingleton.getInstance().user.isUseLargeScaleSearch()) {
            buttonSearch.setVisibility(View.GONE);
            new GetCustomerListTask().execute();
        } else {
            AppDataSingleton.getInstance().getCustomerList().clear();
            buttonSearch.setVisibility(View.VISIBLE);
            setupUI();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SkedsApplication.getInstance().saveAppAndUserDataIntoFile();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            new GetCustomerListTask().execute();
        }
    }

    private final class LargeSearchTask extends BaseUiReportTask<String> {
        LargeSearchTask() {
            super(mActivity, R.string.async_task_string_loading_customers);
        }

        @Override
        protected void onSuccess() {
            setupUI();
        }

        @Override
        protected boolean taskBody(final String... args) throws Exception {
            RESTLargeCustomerList.query(searchString);
            return true;
        }
    }

    private class GetCustomerListTask extends BaseUiReportTask<String> {

        GetCustomerListTask() {
            super(mActivity, R.string.async_task_string_loading_customers);
        }

        @Override
        protected void onSuccess() {
            if (UserUtilitiesSingleton.getInstance().user.isLoggedIn()) {
                setupUI();
            }
        }

        @Override
        protected boolean taskBody(final String... args) throws Exception {
            if (UserUtilitiesSingleton.getInstance().user.isLoggedIn()
                    && !UserUtilitiesSingleton.getInstance().user
                    .isUseLargeScaleSearch()) {
                RESTCustomerList.query();
                return true;
            }
            return false;
        }
    }

    private class PullToRefreshTask extends AsyncTask<Void, Void, Boolean> {
        Exception error = null;

        @Override
        protected Boolean doInBackground(Void... params) {
            // Simulates a background job.

            if (UserUtilitiesSingleton.getInstance().user.isUseLargeScaleSearch())
                return true;

            try {
                if (UserUtilitiesSingleton.getInstance().user.isLoggedIn()) {
                    if (UserUtilitiesSingleton.getInstance().user
                            .getTimeTrackable()) {
                        RESTCustomerList.query();
                        return true;
                    }
                }
            } catch (Exception e) {
                error = e;
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            // Call onRefreshComplete when the list has been refreshed.
            mPullRefreshListView.onRefreshComplete();

            if (success)
                setupUI();
            else {
                if (error != null) {
                    DialogErrorPopup errorPopup = new DialogErrorPopup(
                            mActivity, "Could not load customers list", null,
                            error);
                    errorPopup.show();
                }
            }
        }
    }

    public class MyCustomAdapter extends ArrayAdapter<String> {

        private ArrayList<String> original;
        private ArrayList<String> fitems;
        private Filter filter;

        public MyCustomAdapter(Context context, int textViewResourceId,
                               ArrayList<String> items) {
            super(context, textViewResourceId, items);

            original = new ArrayList<String>(items);
            fitems = new ArrayList<String>(items);
            notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            ViewHolder holder;

            if (v == null) {
                LayoutInflater vi = (LayoutInflater) mActivity
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.row_equipment_item, null);

                // cache view fields into the holder
                holder = new ViewHolder();
                holder.customerLabel = (TextView) v
                        .findViewById(R.id.equipmentListItem);
                // associate the holder with the view for later lookup
                v.setTag(holder);
            } else {
                // view already exists, get the holder instance from the view
                holder = (ViewHolder) v.getTag();
            }

            holder.customerLabel.setText(fitems.get(position).substring(0,
                    fitems.get(position).indexOf("|")));

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

                try {
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
                } catch (Exception e) {
                    Log.d(DEBUG_TAG, "Search exception: " + e.toString());
                    return null;
                }
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

    static class ViewHolder {
        TextView customerLabel;
    }

}
