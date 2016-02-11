package com.skeds.android.phone.business.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.skeds.android.phone.business.AsyncTasks.DownloadImageTask;
import com.skeds.android.phone.business.Custom.AccountMenu;
import com.skeds.android.phone.business.Custom.QuickAction;
import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.UserPhoto;

public class ActivityPhotoView extends BaseSkedsActivity implements
        OnClickListener {

    private Activity mActivity;
    private Context mContext;

    private LinearLayout headerLayout;
    private ImageView headerButtonUser;
    private ImageView headerButtonBack;

    private LinearLayout photoInfoLayout;

    private TextView tapToSeeFullInfo;

    public static UserPhoto photo;
    private Bitmap bitmap;

    private ImageView photoImage;
    private TextView textPhotoTag, textPhotoDate, textPhotographer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_photo_view);

        headerLayout = (LinearLayout) findViewById(R.id.activity_header);

        mActivity = ActivityPhotoView.this;
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

        photoImage = (ImageView) findViewById(R.id.activity_photo_imageview_photo);
        textPhotoTag = (TextView) findViewById(R.id.activity_photo_textview_image_tag);
        textPhotoDate = (TextView) findViewById(R.id.activity_photo_textview_date);
        textPhotographer = (TextView) findViewById(R.id.activity_photo_textview_photographer);
        photoInfoLayout = (LinearLayout) findViewById(R.id.activity_photo_info_layout);
        tapToSeeFullInfo = (TextView) findViewById(R.id.activity_photo_textview_see_full);

        setupUI();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppDataSingleton.getInstance().recycleBitmap();
    }

    private void setupUI() {

        photoImage.setOnClickListener(this);
        photoImage.setPadding(100, 0, 100, 0);

        new getBitmap(this).execute(photo.getURL());

        textPhotoTag.setText(photo.getTagText());
        textPhotoDate.setText(photo.getDate());
        textPhotographer.setText(photo.getPhotographer());
    }

    @Override
    public void onClick(View v) {
        startActivity(new Intent(this, ActivityZoomPhoto.class));
    }

    private class getBitmap extends DownloadImageTask {

        public getBitmap(Context context) {
            super(context);
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            bitmap = result;
            AppDataSingleton.getInstance().setBitmap(bitmap);
            photoImage.setImageBitmap(bitmap);
        }

    }

}