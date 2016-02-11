package com.skeds.android.phone.business.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.skeds.android.phone.business.AsyncTasks.BaseUiReportTask;
import com.skeds.android.phone.business.Custom.AccountMenu;
import com.skeds.android.phone.business.Custom.QuickAction;
import com.skeds.android.phone.business.Dialogs.DialogLocationCallOrEdit;
import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Location;
import com.skeds.android.phone.business.Utilities.General.CommonUtilities;
import com.skeds.android.phone.business.Utilities.General.UserUtilitiesSingleton;
import com.skeds.android.phone.business.Utilities.REST.Objects.RESTLargeLocationsList;
import com.skeds.android.phone.business.Utilities.REST.Objects.RESTLocation;

import java.util.List;

public class ActivityCustomerLocationListView extends BaseSkedsActivity {

    public static final String ACTION_PICK_LOCATION = "pick_location";
    public static final String ACTION_VIEW_LOCATION = "view_location";

    private ListView locationList;
    private ArrayAdapter<String> listAdapter;

    private LinearLayout headerLayout;
    private ImageView headerButtonUser;
    private ImageView headerButtonBack;

    private TextView buttonAddLocation;

    private EditText searchEditText;
    private ImageView searchButton;

    private Activity mActivity;
    private Context mContext;

    private List<Location> locations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_customer_location_list_view);

        headerLayout = (LinearLayout) findViewById(R.id.activity_header);

        mActivity = ActivityCustomerLocationListView.this;
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

        buttonAddLocation = (TextView) headerLayout
                .findViewById(R.id.header_standard_button_right);

        searchEditText = (EditText) findViewById(R.id.location_search_edittext);
        searchButton = (ImageView) findViewById(R.id.location_search_button);

        searchButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!CommonUtilities.isNetworkAvailable(mContext)) {
                    Toast.makeText(mContext, "Network connection unavailable.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                String searchKey = searchEditText.getText().toString();
                if (searchKey.isEmpty())
                    Toast.makeText(mContext, "Search Field Can't Be Empty", Toast.LENGTH_SHORT).show();
                else
                    new GetLocationsForKeyTask(searchKey).execute();
            }
        });

        locationList = (ListView) findViewById(R.id.activity_customer_location_list_listview_location);
        locations = AppDataSingleton.getInstance().getCustomer().locationList;
        setupUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (listAdapter != null)
            listAdapter.notifyDataSetChanged();
    }


    private final static int ARCODE_NEWLOCATION = 1;
    private final static int ARCODE_EDITLOCATION = 2;

    private OnClickListener buttonListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.header_standard_button_right: {
                    // new location
                    Intent it = new Intent(ActivityCustomerLocationListView.this,
                            ActivityLocationAddEdit.class);
                    it.putExtra(ActivityLocationAddEdit.LOCATION_POS, -1); // new
                    startActivityForResult(it, ARCODE_NEWLOCATION);
                    break;
                }
                default:
                    // Nothing
                    break;
            }
        }
    };

    private OnClickListener mapPinListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            int pos = Integer.parseInt(v.getTag().toString());

            boolean noLatLongInfo = false;
            String address = "";
            double latitude, longitude;

            if (!TextUtils.isEmpty(locations.get(pos).getAddress1())) {
                address = locations.get(pos)
                        .getAddress1()
                        + " "
                        + locations.get(pos)
                        .getAddress2()
                        + " "
                        + locations.get(pos)
                        .getCity()
                        + ", "
                        + locations.get(pos)
                        .getState()
                        + " "
                        + locations.get(pos)
                        .getZip();
            }

            if ("null".equals(locations.get(pos).getLatitude())
                    || TextUtils.isEmpty(locations.get(pos).getLatitude())) {
                noLatLongInfo = true;
                latitude = 0.0;
                longitude = 0.0;
            } else {
                latitude = Double
                        .parseDouble(locations.get(
                                pos).getLatitude());
                longitude = Double
                        .parseDouble(locations.get(
                                pos).getLongitude());
            }

            if (noLatLongInfo && TextUtils.isEmpty(address)) {
                Toast.makeText(
                        mContext,
                        "There does not appear to be any address information here.",
                        Toast.LENGTH_LONG).show();
            } else {
                String url = getDirectionsToAddressUrl(latitude, longitude,
                        address);
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse(url));

                startActivity(intent);
            }
        }
    };

    private OnItemClickListener listListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> adapter, View v, int position,
                                long id) {

            if (getIntent().getAction().equals(ACTION_PICK_LOCATION)) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("location_number", (int) id);
                setResult(RESULT_OK, returnIntent);
                finish();
            } else {
                new GetLocationTask((int) id).execute();
            }

        }
    };

    private void showCallOrEditDialog(final int id) {
        final Location loc = locations.get(id);
        final DialogLocationCallOrEdit locationDialog = new DialogLocationCallOrEdit(mContext, loc, new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                switch (v.getId()) {
                    case R.id.phone1:
                        callIntent.setData(Uri.parse(getString(R.string.dial_number_action, loc.getPhone1())));
                        startActivity(callIntent);
                        break;
                    case R.id.phone2:
                        callIntent.setData(Uri.parse(getString(R.string.dial_number_action, loc.getPhone2())));
                        startActivity(callIntent);
                        break;
                    case R.id.edit: // Edit
                        showEditDialog(id);
                        break;
                    default:
                        // Nothing
                        break;
                }
            }
        });
        locationDialog.show();
    }

    private void showEditDialog(int editItem) {
        // existen location
        Intent it = new Intent(ActivityCustomerLocationListView.this,
                ActivityLocationAddEdit.class);

        int position = 0;

        for (Location loc : AppDataSingleton.getInstance().getCustomer().locationList) {
            if (loc.getId() == locations.get(editItem).getId()) {
                position = AppDataSingleton.getInstance().getCustomer().locationList.indexOf(loc);
            }

        }

        it.putExtra(ActivityLocationAddEdit.LOCATION_POS, position); // new
        startActivityForResult(it, ARCODE_EDITLOCATION);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;
        switch (requestCode) {
            case ARCODE_NEWLOCATION:
            case ARCODE_EDITLOCATION:
                setupUI();
                return;
            default:
                break;
        }
    }

    private void setupUI() {
        locationList.setAdapter(null);

        if (getIntent().getAction().equals(ACTION_PICK_LOCATION)) {
            buttonAddLocation.setVisibility(View.GONE);
        } else {
            buttonAddLocation.setOnClickListener(buttonListener);
        }

        if (locations != null) {

            int locationsCount = AppDataSingleton.getInstance().getCustomer().locationsCount;
            if ((locationsCount >= 0) && (locationsCount < 20)) {
                listAdapter = new MyCustomAdapter(mActivity,
                        R.layout.row_location_list_item, new String[locations.size()]);
                locationList.setAdapter(listAdapter);

                searchButton.setVisibility(View.GONE);
                searchEditText.setVisibility(View.GONE);
            }


            locationList.setOnItemClickListener(listListener);
            locationList.setTextFilterEnabled(true);
        }
    }

    public static String getDirectionsToAddressUrl(double lat, double log,
                                                   String address) {// connect to map web service

        StringBuffer urlString = new StringBuffer();
        // urlString.append("http://maps.google.com/maps?f=d&hl=en");
        // urlString.append("&saddr=");// from
        urlString.append("http://maps.google.com/maps?q=");
        if (TextUtils.isEmpty(address)) {
            urlString.append(Double.toString(lat));
            urlString.append(",");
            urlString.append(Double.toString(log));
        } else {
            urlString.append(address);
        }
        urlString.append("&ie=UTF8&0&om=0&output=kml");
        return urlString.toString();
    }

    private class MyCustomAdapter extends ArrayAdapter<String> {

        public MyCustomAdapter(Context context, int textViewResourceId,
                               String[] objects) {
            super(context, textViewResourceId, objects);
            notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View row;

            row = inflater.inflate(R.layout.item_location, parent, false);

            if (locations == null)
                return row;
            else if (locations.size()-1 < position)
                return row;

            TextView name = (TextView) row.findViewById(R.id.locationName);
            TextView content = (TextView) row.findViewById(R.id.locationContent);

            final Location location = locations.get(position);
            String locName = location.getName();
            String adress1 = location.getAddress1();
            String adress2 = location.getAddress2();
            String city = location.getCity();
            String state = location.getState();
            String zip = location.getZip();

            if (!locName.isEmpty()) {
                name.setVisibility(View.VISIBLE);
                name.setText(locName + ":");
            } else {
                name.setVisibility(View.GONE);
            }

            if (!adress1.isEmpty() && !adress2.isEmpty()) {
                content.setText(getString(R.string.adress_format_address1_adress2, adress1, adress2, city, state, zip));
            } else if (!city.isEmpty()) {
                content.setText(getString(R.string.adress_format_address1, adress1, city, state, zip));
            } else {
                content.setText(adress1);
            }

            return row;
        }
    }

    private final class GetLocationTask extends BaseUiReportTask<String> {

        private int locationPos;

        public GetLocationTask(int position) {
            super(mActivity, "Loading Location...");
            locationPos = position;
        }

        @Override
        protected boolean taskBody(String... params) throws Exception {
            RESTLocation.query(locationPos);
            return true;
        }

        @Override
        protected void onSuccess() {
            super.onSuccess();
            showCallOrEditDialog(locationPos);
        }

    }

    private class GetLocationsForKeyTask extends BaseUiReportTask<String> {

        private String key;

        GetLocationsForKeyTask(String key) {
            super(mActivity, "Search For Locations...");
            this.key = key;
        }

        @Override
        protected boolean taskBody(String... params) throws Exception {
            if (UserUtilitiesSingleton.getInstance().user.isLoggedIn()) {
                RESTLargeLocationsList.query(key);
                return true;
            }

            return false;
        }

        @Override
        protected void onSuccess() {
            super.onSuccess();
            listAdapter = new MyCustomAdapter(mActivity,
                    R.layout.row_location_list_item, new String[locations.size()]);
            locationList.setAdapter(listAdapter);
        }
    }

}