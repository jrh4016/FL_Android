package com.skeds.android.phone.business.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.skeds.android.phone.business.R;

public class LogoPlaceholderFragment extends BaseSkedsFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_layout_logo_placeholder,
                container, false);
    }

    public interface ListItemSelectedListener {
        public void onListItemSelected(int index);
    }
}