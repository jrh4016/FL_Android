package com.skeds.android.phone.business.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.skeds.android.phone.business.R;

import java.util.List;


//do not save data right now because appointment fragment should be redesigned and refactored to store data
public class DialogRouteOrCustomerInfo extends Dialog {

    public static final int TRANSFER_TO_ROUTE = 0;
    public static final int TRANSFER_TO_CUSTOMER_INFO = 1;

    private CallbackListener mCallback;

    private final List<String> mPhoneList;

    public static interface CallbackListener {
        void callback(final int type, final String phone);
    }

    public DialogRouteOrCustomerInfo(final Context context, final List<String> phones, final CallbackListener callback) {
        super(context);
        mCallback = callback;
        mPhoneList = phones;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.d_customer_details_info);

        final ListView list = (ListView) findViewById(R.id.customer_info_list);
        list.setAdapter(new ApptPhonesAdapter(getContext(), mPhoneList));
        list.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mCallback.callback(position, (String) parent.getItemAtPosition(position));
                dismiss();
            }
        });
    }

    @Override
    public void dismiss() {
        mCallback = null;
        super.dismiss();
    }

    @Override
    public void onBackPressed() {
        mCallback.callback(-1, null);
        super.onBackPressed();
    }

    private static class ApptPhonesAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        private final List<String> mPhoneList;
        private final String mCall;

        public ApptPhonesAdapter(Context context, List<String> phones) {
            mInflater = LayoutInflater.from(context);
            mPhoneList = phones;
            mCall = context.getString(R.string.call_phone_number);
        }

        @Override
        public int getCount() {
            return mPhoneList.size();
        }

        @Override
        public String getItem(int position) {
            return mPhoneList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            PhoneHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.li_customer_details_info, parent, false);
                holder = new PhoneHolder();
                holder.mName = (TextView) convertView.findViewById(R.id.phone);
                convertView.setTag(holder);
            } else {
                holder = (PhoneHolder) convertView.getTag();
            }

            final String item = getItem(position);
            if (position > 1) {
                holder.mName.setText(String.format(mCall, item));
            } else {
                holder.mName.setText(item);
            }
            return convertView;
        }

        private static class PhoneHolder {
            TextView mName;
        }

    }
}