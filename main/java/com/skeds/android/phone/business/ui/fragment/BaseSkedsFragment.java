package com.skeds.android.phone.business.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.skeds.android.phone.business.AsyncTasks.FlushTask;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.StatusBuffer;
import com.skeds.android.phone.business.Utilities.General.CommonUtilities;
import com.skeds.android.phone.business.core.SkedsApplication;

public class BaseSkedsFragment extends Fragment {

    Context context;
    View fragmentView;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        fragmentView = getView();
        context = fragmentView.getContext();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (CommonUtilities.isNetworkAvailable(getActivity())) {
            if (StatusBuffer.instance().haveQueue())
                new FlushTask(getActivity()).execute();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (!(this instanceof ApptQuestionsFragment || this instanceof LogoPlaceholderFragment))
            SkedsApplication.getInstance().saveAppAndUserDataIntoFile();
    }

}
