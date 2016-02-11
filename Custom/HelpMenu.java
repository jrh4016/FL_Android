package com.skeds.android.phone.business.Custom;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.skeds.android.phone.business.activities.ActivityHelpView;

public class HelpMenu {

    public static QuickAction setupMenu(final Context context,
                                        final Activity activity, final String activityHelpName) {
        final QuickAction quickAction = new QuickAction(context,
                QuickAction.VERTICAL);

        final int ACTION_USER_GUIDE = 0;
        final int ACTION_FAQ = 1;
        final int ACTION_SUBMIT_BUG = 2;

        ActionItem helpItem = new ActionItem(ACTION_USER_GUIDE, "Help Guide",
                null);
        ActionItem faqItem = new ActionItem(ACTION_FAQ, "FAQ", null);
        // ActionItem eraseItem = new ActionItem(ACTION_SUBMIT_BUG,
        // "Submit Bug", null);

        // prevItem.setSticky(true);
        // nextItem.setSticky(true);

        // add action items into QuickAction
        quickAction.addActionItem(helpItem);
        quickAction.addActionItem(faqItem);

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
                            case ACTION_USER_GUIDE:
                                i = new Intent(context, ActivityHelpView.class);
                                i.putExtra("activity", activityHelpName);
                                context.startActivity(i);
                                // activity.finish();
                                break;
                            case ACTION_FAQ:
                                i = new Intent(context, ActivityHelpView.class);
                                i.putExtra("activity", activityHelpName);
                                context.startActivity(i);
                                // activity.finish();
                                break;
                            case ACTION_SUBMIT_BUG:
                                i = new Intent(context, ActivityHelpView.class);
                                i.putExtra("activity", activityHelpName);
                                context.startActivity(i);
                                // activity.finish();
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

    public HelpMenu() {
    }
}