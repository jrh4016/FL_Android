package com.skeds.android.phone.business.Custom;

import android.app.Activity;
import android.content.Context;

public class ActionMenu {

    public static QuickAction setupAppointmentTrackableMenu(
            final Context context, final Activity activity) {
        final QuickAction quickAction = new QuickAction(context,
                QuickAction.VERTICAL);

        // ActionItem nextItem = new ActionItem(ACTION_DASHBOARD, "Dashboard",
        // null);
        // ActionItem prevItem = new ActionItem(ACTION_SETTINGS, "Settings",
        // null);
        // ActionItem searchItem = new ActionItem(ACTION_HELP, "Help", null);
        // ActionItem eraseItem = new ActionItem(ACTION_LOGOUT, "Logout", null);
        // ActionItem okItem = new ActionItem(ACTION_EXIT_SKEDS, "Exit Skeds",
        // null);
        //
        // prevItem.setSticky(true);
        // nextItem.setSticky(true);
        //
        // // add action items into QuickAction
        // quickAction.addActionItem(nextItem);
        // quickAction.addActionItem(prevItem);
        // quickAction.addActionItem(searchItem);
        // quickAction.addActionItem(eraseItem);
        // quickAction.addActionItem(okItem);
        //
        // quickAction
        // .setOnActionItemClickListener(new
        // QuickAction.OnActionItemClickListener() {
        // @Override
        // public void onItemClick(QuickAction source, int pos,
        // int actionId) {
        // ActionItem actionItem = quickAction.getActionItem(pos);
        //
        // Intent i = null;
        // // here we can filter which action item was clicked with
        // // pos or actionId parameter
        // switch (actionId) {
        // case ACTION_DASHBOARD:
        // i = new Intent(context, ViewDashboard.class);
        // context.startActivity(i);
        // activity.finish();
        // break;
        // case ACTION_SETTINGS:
        // i = new Intent(context, ViewSettings.class);
        // context.startActivity(i);
        // activity.finish();
        // break;
        // case ACTION_HELP:
        // i = new Intent(context, ViewHelp.class);
        // context.startActivity(i);
        // activity.finish();
        // break;
        // case ACTION_LOGOUT:
        //
        // UserUtilities.userLogoutPrompt(activity, context,
        // R.layout.dialog_layout_yes_no_response,
        // R.id.dialog_yes_no_response_textview_title,
        // R.id.dialog_yes_no_response_textview_body,
        // R.id.dialog_yes_no_response_button_yes,
        // R.id.dialog_yes_no_response_button_no,
        // ViewLogin.class);
        // break;
        // case ACTION_EXIT_SKEDS:
        // UserUtilities.userQuitPrompt(activity, context,
        // R.layout.dialog_layout_yes_no_response,
        // R.id.dialog_yes_no_response_textview_title,
        // R.id.dialog_yes_no_response_textview_body,
        // R.id.dialog_yes_no_response_button_yes,
        // R.id.dialog_yes_no_response_button_no);
        // break;
        // }
        // }
        // });

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

    public static QuickAction setupInvoiceMenu(final Context context,
                                               final Activity activity) {
        final QuickAction quickAction = new QuickAction(context,
                QuickAction.VERTICAL);

        // ActionItem nextItem = new ActionItem(ACTION_DASHBOARD, "Dashboard",
        // null);
        // ActionItem prevItem = new ActionItem(ACTION_SETTINGS, "Settings",
        // null);
        // ActionItem searchItem = new ActionItem(ACTION_HELP, "Help", null);
        // ActionItem eraseItem = new ActionItem(ACTION_LOGOUT, "Logout", null);
        // ActionItem okItem = new ActionItem(ACTION_EXIT_SKEDS, "Exit Skeds",
        // null);
        //
        // prevItem.setSticky(true);
        // nextItem.setSticky(true);
        //
        // // add action items into QuickAction
        // quickAction.addActionItem(nextItem);
        // quickAction.addActionItem(prevItem);
        // quickAction.addActionItem(searchItem);
        // quickAction.addActionItem(eraseItem);
        // quickAction.addActionItem(okItem);
        //
        // quickAction
        // .setOnActionItemClickListener(new
        // QuickAction.OnActionItemClickListener() {
        // @Override
        // public void onItemClick(QuickAction source, int pos,
        // int actionId) {
        // ActionItem actionItem = quickAction.getActionItem(pos);
        //
        // Intent i = null;
        // // here we can filter which action item was clicked with
        // // pos or actionId parameter
        // switch (actionId) {
        // case ACTION_DASHBOARD:
        // i = new Intent(context, ViewDashboard.class);
        // context.startActivity(i);
        // activity.finish();
        // break;
        // case ACTION_SETTINGS:
        // i = new Intent(context, ViewSettings.class);
        // context.startActivity(i);
        // activity.finish();
        // break;
        // case ACTION_HELP:
        // i = new Intent(context, ViewHelp.class);
        // context.startActivity(i);
        // activity.finish();
        // break;
        // case ACTION_LOGOUT:
        //
        // UserUtilities.userLogoutPrompt(activity, context,
        // R.layout.dialog_layout_yes_no_response,
        // R.id.dialog_yes_no_response_textview_title,
        // R.id.dialog_yes_no_response_textview_body,
        // R.id.dialog_yes_no_response_button_yes,
        // R.id.dialog_yes_no_response_button_no,
        // ViewLogin.class);
        // break;
        // case ACTION_EXIT_SKEDS:
        // UserUtilities.userQuitPrompt(activity, context,
        // R.layout.dialog_layout_yes_no_response,
        // R.id.dialog_yes_no_response_textview_title,
        // R.id.dialog_yes_no_response_textview_body,
        // R.id.dialog_yes_no_response_button_yes,
        // R.id.dialog_yes_no_response_button_no);
        // break;
        // }
        // }
        // });
        //
        // // set listnener for on dismiss event, this listener will be called
        // only
        // // if QuickAction dialog was dismissed
        // // by clicking the area outside the dialog.
        // quickAction.setOnDismissListener(new QuickAction.OnDismissListener()
        // {
        // @Override
        // public void onDismiss() {
        //
        // }
        // });
        return quickAction;
    }

    public ActionMenu() {
    }
}