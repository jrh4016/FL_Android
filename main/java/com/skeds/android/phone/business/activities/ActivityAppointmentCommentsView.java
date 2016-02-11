package com.skeds.android.phone.business.activities;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.skeds.android.phone.business.AsyncTasks.BaseUiReportTask;
import com.skeds.android.phone.business.C2DMUtilities.C2DMConstants;
import com.skeds.android.phone.business.Custom.AccountMenu;
import com.skeds.android.phone.business.Custom.QuickAction;
import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Comment;
import com.skeds.android.phone.business.Utilities.General.CommonUtilities;
import com.skeds.android.phone.business.Utilities.General.UserUtilitiesSingleton;
import com.skeds.android.phone.business.Utilities.REST.Objects.RESTComment;
import com.skeds.android.phone.business.Utilities.REST.Objects.RESTCommentList;

import java.util.ArrayList;
import java.util.List;

public class ActivityAppointmentCommentsView extends BaseSkedsActivity {

    private ListView listviewComments;
    private PullToRefreshListView mPullRefreshListView;
    private TextView buttonNewComment;

    private LinearLayout headerLayout;
    private ImageView headerButtonUser;
    private ImageView headerButtonBack;

    private SlidingDrawer slidingdrawer;
    private EditText edittextComment;
    private TextView buttonSubmitComment;

    private Context mContext;
    private Activity mActivity;

    private QuickAction accountMenu;

    /*
     * This is how I know which way to setup the data, as well as what to go
     * back to.
     */
    public static int previousActivity;
    public static final int PREVIOUS_ACTIVITY_APPOINTMENT_TRACKABLE = 0;
    public static final int PREVIOUS_ACTIVITY_DASHBOARD = 1;
    public static final int PREVIOUS_ACTIVITY_APPOINTMENT_PREVIOUS = 2;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_comments_view);

        mContext = getApplicationContext();

		/* This will clear active "comment" notifications */
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager mNotificationManager = (NotificationManager) mContext
                .getSystemService(ns);
        mNotificationManager.cancel(C2DMConstants.NOTIFICATION_TYPE_COMMENT);

        headerLayout = (LinearLayout) findViewById(R.id.activity_header);
        buttonNewComment = (TextView) findViewById(R.id.header_standard_button_right);

        mActivity = ActivityAppointmentCommentsView.this;
        mContext = this;

        headerButtonUser = (ImageView) headerLayout
                .findViewById(R.id.header_button_user);
        headerButtonBack = (ImageView) headerLayout
                .findViewById(R.id.header_button_back);

        accountMenu = AccountMenu.setupMenu(mContext, mActivity);

        headerButtonUser.setOnClickListener(clickListener);
        headerButtonBack.setOnClickListener(clickListener);

        slidingdrawer = (SlidingDrawer) findViewById(R.id.activity_comments_slidingdrawer);
        buttonSubmitComment = (TextView) slidingdrawer
                .findViewById(R.id.activity_comments_button_save);
        edittextComment = (EditText) slidingdrawer
                .findViewById(R.id.activity_comments_edittext_comment);

        mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.activity_comments_listview);
        // listviewComments = (ListView)
        // findViewById(R.id.activity_comments_listview);

        // listviewComments.setAdapter(null);

        buttonNewComment.setOnClickListener(clickListener);
        buttonSubmitComment.setOnClickListener(clickListener);

        if (!CommonUtilities.isNetworkAvailable(mContext)) {
            Toast.makeText(mContext, "Network connection unavailable.",
                    Toast.LENGTH_SHORT).show();
            finish();
        } else {
            new RetreiveCommentsTask().execute();
        }
    }

    /**
     * Called when a menu item is selected.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {

        super.onBackPressed();
        if (slidingdrawer.isOpened()) {
            slidingdrawer.animateClose();
            edittextComment.clearFocus();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        return true;
    }


    private OnClickListener clickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                // "Back" button
                case R.id.header_button_back:
                    onBackPressed();
                    break;
                // "User" button
                case R.id.header_button_user:
                    accountMenu.show(v);
                    accountMenu.setAnimStyle(QuickAction.ANIM_GROW_FROM_CENTER);
                    break;
                // "Add Comment"
                case R.id.header_standard_button_right:
                    if (slidingdrawer.isOpened()) {
                        slidingdrawer.animateClose();
                    } else {
                        slidingdrawer.animateOpen();
                        edittextComment.clearFocus();
                    }
                    break;
                // Save Comment
                case R.id.activity_comments_button_save:
                    if (!CommonUtilities.isNetworkAvailable(mContext)) {
                        Toast.makeText(mContext, "Network connection unavailable.",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        if (!TextUtils.isEmpty(edittextComment.getText()))
                            new SubmitCommentTask().execute();
                    }
                    break;
                default:
                    // Nothing
                    break;
            }

        }
    };

    private void setupCommentsList() {

        mPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener() {

            @Override
            public void onRefresh(PullToRefreshBase refreshView) {
                mPullRefreshListView.setLastUpdatedLabel(DateUtils
                        .formatDateTime(mContext, System.currentTimeMillis(),
                                DateUtils.FORMAT_SHOW_TIME
                                        | DateUtils.FORMAT_SHOW_DATE
                                        | DateUtils.FORMAT_ABBREV_ALL));

                if (!CommonUtilities.isNetworkAvailable(mContext)) {
                    Toast.makeText(mContext, "Network connection unavailable.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    // Do work to refresh the list here.
                    new PullToRefreshCommentsTask().execute();
                }
            }
        });

        listviewComments = mPullRefreshListView.getRefreshableView();

        List<Comment> commentList = new ArrayList<Comment>();
        commentList.addAll(AppDataSingleton.getInstance().getAppointment().getCommentList());
        listviewComments.setAdapter(new MyCustomAdapter(mContext,
            R.layout.row_comment_entry, commentList));


        // Set the layout type
        Drawable drawableDivider = new ColorDrawable(
                android.R.color.transparent);
        listviewComments.setBackgroundColor(Color.TRANSPARENT);
        listviewComments.setCacheColorHint(Color.rgb(62, 81, 101));

        listviewComments.setDivider(drawableDivider);
        listviewComments.setDividerHeight(6); // Pixel spacing in-between items

        listviewComments.setPadding(12, 0, 12, 0);

    }

    private void updateCommentsList() {

        Comment comment = new Comment();

        comment.setId(1);
        comment.setPosterName(UserUtilitiesSingleton.getInstance().user.getFirstName() + " "
                + UserUtilitiesSingleton.getInstance().user.getLastName());
        comment.setPosterDateTime("Just Now");
        comment.setText(edittextComment.getText().toString());

        AppDataSingleton.getInstance().getAppointment().getCommentList().add(comment);

        List<Comment> commentList = new ArrayList<Comment>();

        commentList.addAll(AppDataSingleton.getInstance().getAppointment().getCommentList());

        Drawable drawableDivider = new ColorDrawable(
                android.R.color.transparent);
        listviewComments.setCacheColorHint(Color.rgb(62, 81, 101));

        listviewComments.setDivider(drawableDivider);
        listviewComments.setDividerHeight(6); // Pixel spacing in-between
        // items

        listviewComments.setPadding(12, 0, 12, 0);

        listviewComments.setAdapter(new MyCustomAdapter(mContext,
                R.layout.row_comment_entry, commentList));
        // }

        // Last but not least, clear the text box and close the sliding drawer
        // widget
        edittextComment.setText("");
        slidingdrawer.animateClose();
    }

    private class PullToRefreshCommentsTask
            extends
            AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            // Simulates a background job.
            try {
                int appointmentId = 0;

                switch (previousActivity) {
                    case PREVIOUS_ACTIVITY_APPOINTMENT_TRACKABLE:
                    case PREVIOUS_ACTIVITY_DASHBOARD:
                        appointmentId = AppDataSingleton.getInstance().getAppointment().getId();
                        break;

                    case PREVIOUS_ACTIVITY_APPOINTMENT_PREVIOUS:
                        appointmentId = AppDataSingleton.getInstance().getPastAppointment().getId();
                        break;
                    default:
                        // Nothing
                        break;
                }

                if (UserUtilitiesSingleton.getInstance().user.isLoggedIn()) {
                    RESTCommentList.query(appointmentId);
                    return true;
                }
            } catch (Exception e) {
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {

            // Call onRefreshComplete when the list has been refreshed.
            mPullRefreshListView.onRefreshComplete();

            if (!UserUtilitiesSingleton.getInstance().user.isLoggedIn()) {
                Intent i = new Intent(mContext, ActivityDashboardView.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                // finish();
            }

            if (UserUtilitiesSingleton.getInstance().user.isLoggedIn()) {
                if (success)
                    setupCommentsList();
            }
        }
    }

    private class RetreiveCommentsTask extends BaseUiReportTask<String> {

        public RetreiveCommentsTask() {
            super(ActivityAppointmentCommentsView.this,
                    R.string.async_task_string_loading_comments);
        }

        @Override
        protected void onSuccess() {
            setupCommentsList();
        }

        @Override
        protected boolean taskBody(final String... args) throws Exception {
            int appointmentId = 0;

            switch (previousActivity) {
                case PREVIOUS_ACTIVITY_APPOINTMENT_TRACKABLE:
                case PREVIOUS_ACTIVITY_DASHBOARD:
                    appointmentId = AppDataSingleton.getInstance().getAppointment().getId();
                    break;

                case PREVIOUS_ACTIVITY_APPOINTMENT_PREVIOUS:
                    appointmentId = AppDataSingleton.getInstance().getPastAppointment().getId();
                    break;
                default:
                    // Nothing
                    break;
            }

            RESTCommentList.query(appointmentId);
            return true;
        }
    }

    private class SubmitCommentTask extends BaseUiReportTask<String> {

        public SubmitCommentTask() {
            super(ActivityAppointmentCommentsView.this,
                    R.string.async_task_string_submitting_new_comment);
        }

        @Override
        protected void onSuccess() {
            updateCommentsList();
        }

        @Override
        protected boolean taskBody(final String... args) throws Exception {
            RESTComment.add(UserUtilitiesSingleton.getInstance().user.getServiceProviderId(),
                    edittextComment.getText().toString());
            return true;
        }
    }

    // To generate custom comments list layout
    public class MyCustomAdapter extends ArrayAdapter<Comment> {

        public MyCustomAdapter(Context context, int textViewResourceId,
                               List<Comment> objects) {
            super(context, textViewResourceId, objects);
            notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.row_comment_entry, parent,
                    false);

            // Commenter Name
            TextView commenterName = (TextView) row
                    .findViewById(R.id.commentRowTextPosterName);

            // Commenter Comment
            TextView commenterText = (TextView) row
                    .findViewById(R.id.commentRowTextComment);

            // Commenter TimeStamp
            TextView commenterTimeStamp = (TextView) row
                    .findViewById(R.id.commentRowTextTimestamp);

            commenterName.setText(AppDataSingleton.getInstance().getAppointment().getCommentList().get(
                    position).getPosterName());

            commenterText.setText(AppDataSingleton.getInstance().getAppointment().getCommentList().get(
                    position).getText());

            commenterTimeStamp.setText(AppDataSingleton.getInstance().getAppointment().getCommentList()
                    .get(position).getPosterDateTime());

            return row;
        }
    }
}