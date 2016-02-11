package com.skeds.android.phone.business.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.skeds.android.phone.business.Custom.AccountMenu;
import com.skeds.android.phone.business.Custom.QuickAction;
import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.LeadSource;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user_sca on 14.10.2014.
 */
public class LeadSourceListActivity extends BaseSkedsActivity {

    public static final String LEAD_SOURCE_TYPE_FILTER = "lead_source_type_filter";
    private PullToRefreshListView mPullRefreshListView;
    private ListView leadSourceList;

    public static int LEAD_SOURCE = 10;
    public static String key = "leadSource";
    private LeadSourceListActivity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_lead_list_view);


        mPullRefreshListView = (PullToRefreshListView)
                findViewById(R.id.activity_lead_listview);

        mActivity = LeadSourceListActivity.this;
        LeadSourceListActivity mContext = this;

        final QuickAction accountMenu;
        accountMenu = AccountMenu.setupMenu(mContext, mActivity);

        LinearLayout headerLayout = (LinearLayout) findViewById(R.id.activity_header);

        ImageView headerButtonUser = (ImageView) headerLayout
                .findViewById(R.id.header_button_user);
        ImageView headerButtonBack = (ImageView) headerLayout
                .findViewById(R.id.header_button_back);

        headerButtonUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accountMenu.show(v);
                accountMenu.setAnimStyle(QuickAction.ANIM_GROW_FROM_CENTER);
            }
        });

        headerButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    public String getTypeForFilter(){
        return getIntent().getStringExtra(LEAD_SOURCE_TYPE_FILTER);
    }
    @Override
    public void onResume() {
        super.onResume();
        setupUI();
    }

    private void setupUI() {



        leadSourceList = mPullRefreshListView.getRefreshableView();

        List<LeadSource> leadSourceListItem = AppDataSingleton.getInstance().getLeadSourceListItem();

        List<LeadSource> filteredLeadSource = new ArrayList<LeadSource>();

        for (LeadSource leadSource : leadSourceListItem) {
            if (leadSource.getType().contains(getTypeForFilter()))
                filteredLeadSource.add(leadSource);
        }

        final ArrayAdapter<LeadSource> adapter = new LeadSourcesAdapter(this, R.layout.lead_sources_view_item,
                filteredLeadSource);

        leadSourceList.setAdapter(adapter);

        final LeadSourceListActivity activiy = this;
        leadSourceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent();
                intent.putExtra(key, adapter.getItem(i-1));
                activiy.setResult(LEAD_SOURCE,intent);

                activiy.finish();
            }
        });
    }




}
