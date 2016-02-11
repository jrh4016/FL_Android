package com.skeds.android.phone.business.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.activities.ActivityPhotoView;

public class PhotoListViewFragment extends BaseSkedsFragment {

    private Activity mActivity;

    private ListView photoList;

    public static int returnToView = 0;
    public final static int VIEW_FROM_CUSTOMER = 0;
    public final static int VIEW_FROM_APPOINTMENT = 1;
    public final static int VIEW_FROM_EQUIPMENT = 2;

    private OnClickListener mPhotoRowListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            int photoNum = Integer.parseInt(v.getTag().toString());

            ActivityPhotoView.photo = AppDataSingleton.getInstance().getPhotoList().get(photoNum);

            Intent i = new Intent(mActivity, ActivityPhotoView.class);
            startActivity(i);
            // finish();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_photo_gallery_view, container,
                false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();

        photoList = (ListView) mActivity.findViewById(R.id.activity_photo_gallery_listview_photos);

        setupUI();
    }


    private void setupUI() {
        String[] sizeArray = new String[AppDataSingleton.getInstance().getPhotoList().size()];
        ArrayAdapter<String> adapter = new MyCustomAdapter(mActivity,
                R.layout.row_photo_item, sizeArray);
        photoList.setAdapter(adapter);
    }

    public class MyCustomAdapter extends ArrayAdapter<String> {

        public MyCustomAdapter(Context context, int textViewResourceId,
                               String[] objects) {
            super(context, textViewResourceId, objects);
            notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = mActivity.getLayoutInflater();
            View row = inflater.inflate(R.layout.row_photo_item, parent, false);

            // Set Picture Type
            if (AppDataSingleton.getInstance().getPhotoList().get(position) != null) {

                ImageView photoType = (ImageView) row
                        .findViewById(R.id.photoRowItemImage);
                if (AppDataSingleton.getInstance().getPhotoList().get(position).getAppointmentId() != 0) {
                    photoType.setImageResource(R.drawable.row_icon_appointment);
                } else if (AppDataSingleton.getInstance().getPhotoList().get(position)
                        .getEquipmentId() != 0) {
                    photoType.setImageResource(R.drawable.row_icon_equipment);
                } else {
                    photoType.setImageResource(R.drawable.row_icon_customer);
                }

                TextView photoDescription = (TextView) row
                        .findViewById(R.id.photoRowItemDescription);
                if (!TextUtils.isEmpty(AppDataSingleton.getInstance().getPhotoList().get(position).getTagText()))
                    photoDescription.setText(AppDataSingleton.getInstance().getPhotoList()
                            .get(position).getTagText());
                else {
                    photoDescription.setTypeface(null, Typeface.ITALIC);
                    photoDescription.setText("No photo description");
                }

                TextView photoTechName = (TextView) row
                        .findViewById(R.id.photoRowItemTechName);
                if (!TextUtils.isEmpty(AppDataSingleton.getInstance().getPhotoList().get(position).getPhotographer()))
                    photoTechName.setText(AppDataSingleton.getInstance().getPhotoList().get(position)
                            .getPhotographer());
                else
                    photoTechName.setText("");

                TextView photoDate = (TextView) row
                        .findViewById(R.id.photoRowItemDate);
                if (!TextUtils.isEmpty(AppDataSingleton.getInstance().getPhotoList().get(position).getDate()))
                    photoDate.setText(AppDataSingleton.getInstance().getPhotoList().get(position)
                            .getDate());
                else
                    photoDate.setText("");

                row.setTag(position);
                row.setOnClickListener(mPhotoRowListener);
            }

            return row;
        }

    }

}
