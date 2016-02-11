package com.skeds.android.phone.business.Custom;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.General.UserUtilitiesSingleton;
import com.skeds.android.phone.business.activities.ActivityDashboardView;
import com.skeds.android.phone.business.activities.ActivityHelpView;
import com.skeds.android.phone.business.activities.ActivityLoginMobile;
import com.skeds.android.phone.business.activities.ActivitySettingsView;

public class AccountMenu {

    // action id
    private static final int ACTION_DASHBOARD = 1;
    private static final int ACTION_SETTINGS = 2;
    private static final int ACTION_HELP = 3;
    private static final int ACTION_LOGOUT = 4;
    private static final int ACTION_EXIT_SKEDS = 5;

    public static QuickAction setupMenu(final Context context,
                                        final Activity activity) {
        final QuickAction quickAction = new QuickAction(context,
                QuickAction.VERTICAL);

        ActionItem nextItem = new ActionItem(ACTION_DASHBOARD, "Dashboard",
                null);
        ActionItem prevItem = new ActionItem(ACTION_SETTINGS, "Settings", null);
        ActionItem searchItem = new ActionItem(ACTION_HELP, "Help", null);
        ActionItem eraseItem = new ActionItem(ACTION_LOGOUT, "Logout", null);
        ActionItem okItem = new ActionItem(ACTION_EXIT_SKEDS, "Exit FieldLocate",
                null);

        prevItem.setSticky(true);
        nextItem.setSticky(true);

        // add action items into QuickAction
        quickAction.addActionItem(nextItem);
        quickAction.addActionItem(prevItem);
        quickAction.addActionItem(searchItem);
        quickAction.addActionItem(eraseItem);
        quickAction.addActionItem(okItem);

        quickAction
                .setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
                    @Override
                    public void onItemClick(QuickAction source, int pos,
                                            int actionId) {
                        ActionItem actionItem = quickAction.getActionItem(pos);

                        Intent i = null;
                        // here we can filter which action item was clicked with
                        // pos or actionId parameter
                        switch (actionId) {
                            case ACTION_DASHBOARD:
                                i = new Intent(context, ActivityDashboardView.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                context.startActivity(i);
                                // activity.finish();
                                break;
                            case ACTION_SETTINGS:
                                i = new Intent(context, ActivitySettingsView.class);
                                context.startActivity(i);
                                // activity.finish();
                                break;
                            case ACTION_HELP:
                                i = new Intent(context, ActivityHelpView.class);
                                context.startActivity(i);
                                // activity.finish();
                                break;
                            case ACTION_LOGOUT:

                                UserUtilitiesSingleton.getInstance().userLogoutPrompt(activity, context,
                                        R.layout.dialog_layout_yes_no_response,
                                        R.id.dialog_yes_no_response_textview_title,
                                        R.id.dialog_yes_no_response_textview_body,
                                        R.id.dialog_yes_no_response_button_yes,
                                        R.id.dialog_yes_no_response_button_no,
                                        ActivityLoginMobile.class);
                                break;
                            case ACTION_EXIT_SKEDS:
                                UserUtilitiesSingleton.getInstance().userQuitPrompt(activity, context,
                                        R.layout.dialog_layout_yes_no_response,
                                        R.id.dialog_yes_no_response_textview_title,
                                        R.id.dialog_yes_no_response_textview_body,
                                        R.id.dialog_yes_no_response_button_yes,
                                        R.id.dialog_yes_no_response_button_no);
                                break;
                            default:
                                // Nothing
                                break;
                        }
                    }
                });

        // set listnener for on dismiss event, this listener will be called only
        // if QuickAction dialog was dismissed
        // by clicking the area outside the dialog.
        quickAction.setOnDismissListener(new QuickAction.OnDismissListener() {
            @Override
            public void onDismiss() {

            }
        });
        return quickAction;
    }

    public AccountMenu() {

    }
}