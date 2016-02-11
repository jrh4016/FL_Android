package com.skeds.android.phone.business.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Location;

public class DialogLocationCallOrEdit extends Dialog implements View.OnClickListener {
    private Location mLocation;
    private View.OnClickListener mOnClickListener;

    public DialogLocationCallOrEdit(final Context context, final Location loc, final View.OnClickListener onClickListener) {
        super(context);
        mLocation = loc;
        mOnClickListener = onClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Context context = getContext();

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.d_location);

        final TextView phone1 = (TextView) findViewById(R.id.phone1);
        phone1.setOnClickListener(this);
        final TextView phone2 = (TextView) findViewById(R.id.phone2);
        phone2.setOnClickListener(this);

        final StringBuilder builder = new StringBuilder();

        if (!TextUtils.isEmpty(mLocation.getPhone1())) {
            phone1.setVisibility(View.VISIBLE);
            builder.append(mLocation.getPhone1());
            if (mLocation.getPhone1Description() != null) {
                builder.append(' ');
                builder.append(mLocation.getPhone1Description());
            }
            phone1.setText(context.getString(R.string.call_phone_number, builder.toString()));
        }

        builder.setLength(0);
        if (!TextUtils.isEmpty(mLocation.getPhone2())) {
            phone2.setVisibility(View.VISIBLE);
            builder.append(mLocation.getPhone2());

            if (mLocation.getPhone2Description() != null) {
                builder.append(' ');
                builder.append(mLocation.getPhone2Description());
            }
            phone2.setText(context.getString(R.string.call_phone_number, builder.toString()));
        }

        final TextView buttonEdit = (TextView) findViewById(R.id.edit);
        buttonEdit.setText(context.getString(R.string.button_string_edit));
        buttonEdit.setOnClickListener(this);

        ((TextView) findViewById(R.id.header)).setText(context.getString(R.string.dialog_header_string_location));
        ((TextView) findViewById(R.id.body)).setText(context.getString(R.string.dialog_body_string_please_select_and_option_or_press_back_to_cancel));
    }

    @Override
    public void onClick(View v) {
        mOnClickListener.onClick(v);
        dismiss();
    }
}