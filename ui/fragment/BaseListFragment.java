package com.skeds.android.phone.business.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.skeds.android.phone.business.R;

import de.timroes.android.listview.EnhancedListView;

public abstract class BaseListFragment extends Fragment {

    View progress;
    View listContainer;
    TextView emptyText;
    EnhancedListView listView;

    Context context;

    boolean isDetached;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.f_base_list, container, false);
    }

    @Override
    public void onDestroyView() {
        progress = null;
        listContainer = null;
        emptyText = null;
        isDetached = true;
        super.onDestroyView();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final View fragmentView = getView();

        progress = fragmentView.findViewById(R.id.progress);
        listContainer = fragmentView.findViewById(R.id.list_container);
        emptyText = (TextView) fragmentView.findViewById(R.id.empty_text);
        listView = (EnhancedListView) fragmentView.findViewById(R.id.list);
        listView.setEmptyView(emptyText);
        context = fragmentView.getContext().getApplicationContext();

        isDetached = false;
    }

    void setListShown(boolean shown) {
        // check if fragment is still attached to the manager otherwise we are on the step of
        // detaching
        if (!isDetached) {
            if (shown) {
                progress.startAnimation(AnimationUtils.loadAnimation(getActivity(),
                        android.R.anim.fade_out));
                listContainer.startAnimation(AnimationUtils.loadAnimation(getActivity(),
                        android.R.anim.fade_in));

                progress.setVisibility(View.GONE);
                listContainer.setVisibility(View.VISIBLE);
            } else {
                progress.startAnimation(AnimationUtils.loadAnimation(getActivity(),
                        android.R.anim.fade_in));
                listContainer.startAnimation(AnimationUtils.loadAnimation(getActivity(),
                        android.R.anim.fade_out));

                progress.setVisibility(View.VISIBLE);
                listContainer.setVisibility(View.GONE);
            }
        }
    }
}
