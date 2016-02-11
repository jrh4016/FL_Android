package com.skeds.android.phone.business.util;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

public final class ViewUtils {

    private ViewUtils() {
    }

    public static void setupText(CharSequence text, TextView view) {
        if (TextUtils.isEmpty(text)) {
            view.setVisibility(View.GONE);
        } else {
            view.setText(text);
        }
    }
}
