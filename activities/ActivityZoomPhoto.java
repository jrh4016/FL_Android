package com.skeds.android.phone.business.activities;

import android.os.Bundle;
import android.view.Window;

import com.skeds.android.phone.business.Custom.ImageZoom.GestureImageView;
import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;

public class ActivityZoomPhoto extends BaseSkedsActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        GestureImageView photoLayout = new GestureImageView(this);

        photoLayout.setImageBitmap(AppDataSingleton.getInstance().getBitmap());
        setContentView(photoLayout);
    }

}
