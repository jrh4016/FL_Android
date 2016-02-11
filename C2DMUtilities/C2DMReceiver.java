package com.skeds.android.phone.business.C2DMUtilities;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Services.PingLocation;
import com.skeds.android.phone.business.Utilities.General.AppSettingsUtilities;
import com.skeds.android.phone.business.Utilities.General.UserUtilitiesSingleton;
import com.skeds.android.phone.business.activities.ActivityAppointmentDualFragment;

import org.json.JSONObject;

public class C2DMReceiver extends BroadcastReceiver {
    private static String KEY = "c2dmPref";
    private static String REGISTRATION_KEY = "DQAAALsAAACD1GwxxtuWdiLv7xp3ePosJPAsYYtSfErbM1XGQ2k9fZy7fMCmdCY6YJ3-Azg8l2ELbDcsuz4GRfZY53JNr7COhysGR6NGH7ya2yjGWviViXyu4tUOTR1EUqioFJiUlIhOY6QOSnzTnKheJqcY5Z3JFMj5uQQ030pI-BpkGpT973e-URrSH01fq4fwTQSvM0RIU88Tmm2bd8ZVfcQD77tqTgfYeCPa_4T3MWRSeISe-gBSgFPb-JZmPDhZV5oTJoM";
    private static final String C2DM_DATA_ACTION = "action";

    protected static String googleAppId = "666058075679";
    protected static String apiKey = "AIzaSyDo2QUaUpJsK-wFsLBUN4RgxcF-qfLNdCg";

    private final String DEBUG_TAG = "[C2DM]";

    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(DEBUG_TAG, "Received C2DM Message");
        this.context = context;
        if ("com.google.android.c2dm.intent.REGISTRATION".equals(intent.getAction())) {
            handleRegistration(context, intent);
        } else if ("com.google.android.c2dm.intent.RECEIVE".equals(intent.getAction())) {
            handleMessage(context, intent);
        }
    }

    private void handleRegistration(Context context, Intent intent) {
        String registration = intent.getStringExtra("registration_id");
        if (intent.getStringExtra("error") != null) {
            // Registration failed, should try again later.
            Log.d(DEBUG_TAG, "registration failed");
            String error = intent.getStringExtra("error");
            if (error == "SERVICE_NOT_AVAILABLE") {
                Log.d(DEBUG_TAG, "SERVICE_NOT_AVAILABLE");
            } else if (error == "ACCOUNT_MISSING") {
                Log.d(DEBUG_TAG, "ACCOUNT_MISSING");
            } else if (error == "AUTHENTICATION_FAILED") {
                Log.d(DEBUG_TAG, "AUTHENTICATION_FAILED");
            } else if (error == "TOO_MANY_REGISTRATIONS") {
                Log.d(DEBUG_TAG, "TOO_MANY_REGISTRATIONS");
            } else if (error == "INVALID_SENDER") {
                Log.d(DEBUG_TAG, "INVALID_SENDER");
            } else if (error == "PHONE_REGISTRATION_ERROR") {
                Log.d(DEBUG_TAG, "PHONE_REGISTRATION_ERROR");
            }
        } else if (intent.getStringExtra("unregistered") != null) {

            UserUtilitiesSingleton.getInstance().user.setC2dmRegistered(false);

            // unregistration done, new messages from the authorized sender will
            // be rejected
            Log.d(DEBUG_TAG, "unregistered");

        } else if (registration != null) {
            // if (Globals.isDebugVersion)
            // Toast.makeText(context, "Token: " + registration,
            // Toast.LENGTH_SHORT).show();

            Log.d(DEBUG_TAG, registration);
            Editor editor = context.getSharedPreferences(KEY,
                    Context.MODE_PRIVATE).edit();
            editor.putString(REGISTRATION_KEY, registration);
            editor.commit();
            UserUtilitiesSingleton.getInstance().user.setC2dmRegistered(true);
            // Send the registration ID to the 3rd party site that is sending
            // the messages.
            // This should be done in a separate thread.
            // When done, remember that all registration is done.
        }
    }

    private void handleMessage(Context context, Intent intent) {
        Log.d(DEBUG_TAG, "HandleMessage Function Reached.");
        String action = intent.getStringExtra(C2DM_DATA_ACTION);
        String title = intent.getStringExtra("title");
        String message = intent.getStringExtra("msg");
        String payload = intent.getStringExtra("payload");

        Log.d(DEBUG_TAG, "======= Action is: " + action);
        Log.d(DEBUG_TAG, "======= Title is: " + title);
        Log.d(DEBUG_TAG, "======= Message is: " + message);
        Log.d(DEBUG_TAG, "======= Payload is: " + payload);
        Log.e(DEBUG_TAG, "======= Intent is: " + intent);

        createNotification(payload);
    }

    private void createNotification(String notificationString) {

        // Change the message text, and launch class activity based upon the
        // notificationString data
        // Payload is:
        // {"alert":"Removed from Service Call: Wednesday, July 6 - 7:00 PM","apptId":"1044","acc":4,"app":true}

        String alert = "";
        int apptId = 0;
        int actionCode = 0;
        boolean app = false;

        try {
            JSONObject jObject = new JSONObject(notificationString);

            Log.d(DEBUG_TAG, "Object: " + jObject);
            try {
                alert = jObject.getString("alert");
            } catch (Exception e) {

            }

            try {
                apptId = Integer.parseInt(jObject.getString("apptId"));
            } catch (Exception e) {

            }

            try {
                actionCode = jObject.getInt("acc");
            } catch (Exception e) {

            }

            try {
                app = jObject.getBoolean("app");
            } catch (Exception e) {

            }

            generateNotification(context, alert, apptId, actionCode, app);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * This will have to be moved to its own private file because much of it is
     * specific to this app and its c2dm functions
     */
    public static void generateNotification(Context context, String message,
                                            int appointmentId, int actionCode, boolean approved) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(ns);

        int icon = R.drawable.icon;
        CharSequence tickerText = "";
        long when = System.currentTimeMillis();

        CharSequence contentTitle = "";
        CharSequence contentText = "";
        Intent notificationIntent = null;
        PendingIntent contentIntent = null;

        boolean playsSound = false;

        String mainTitle, subTitle;

        if (actionCode != C2DMConstants.NOTIFICATION_TYPE_UPDATE_APPLICATION
                && actionCode != C2DMConstants.NOTIFICATION_TYPE_PING_GPS) {
            switch (actionCode) {
                case C2DMConstants.NOTIFICATION_TYPE_NEW_APPOINTMENT:
                case C2DMConstants.NOTIFICATION_TYPE_UPDATE_APPOINTMENT:
                case C2DMConstants.NOTIFICATION_TYPE_REMOVE_SERVICE_PROVIDER:
                case C2DMConstants.NOTIFICATION_TYPE_REMINDER:

                    mainTitle = message.substring(0, message.indexOf(":"));
                    subTitle = message.substring(message.indexOf(":") + 1,
                            message.length());

                    tickerText = mainTitle;
                    contentTitle = mainTitle;
                    contentText = subTitle;

                    C2DMRedirect.appointmentId = appointmentId;
                    C2DMRedirect.viewType = C2DMRedirect.VIEW_TYPE_APPOINTMENT;
                    notificationIntent = new Intent(
                            context.getApplicationContext(), C2DMRedirect.class);
                    contentIntent = PendingIntent.getActivity(
                            context.getApplicationContext(), 0, notificationIntent,
                            0);

                    playsSound = true;

                    // These will all separate strings, and set the same path to
                    // update
                    break;

                case C2DMConstants.NOTIFICATION_TYPE_CANCEL_APPOINTMENT:

                    mainTitle = message.substring(0, message.indexOf(":"));
                    subTitle = message.substring(message.indexOf(":") + 1,
                            message.length());

                    tickerText = mainTitle;
                    contentTitle = mainTitle;
                    contentText = subTitle;

                    notificationIntent = new Intent(
                            context.getApplicationContext(),
                            ActivityAppointmentDualFragment.class);
                    contentIntent = PendingIntent.getActivity(
                            context.getApplicationContext(), 0, notificationIntent,
                            0);

                    playsSound = true;

                    break;

                case C2DMConstants.NOTIFICATION_TYPE_COMMENT:

                    tickerText = "New Comment from Dispatch";
                    contentTitle = "New Comment from Dispatch";
                    contentText = message;

                    C2DMRedirect.appointmentId = appointmentId;
                    C2DMRedirect.viewType = C2DMRedirect.VIEW_TYPE_COMMENT;
                    notificationIntent = new Intent(
                            context.getApplicationContext(), C2DMRedirect.class);
                    contentIntent = PendingIntent.getActivity(
                            context.getApplicationContext(), 0, notificationIntent,
                            0);

                    playsSound = true;

                    break;

                case 0:
                    tickerText = "IMPORTANT: Application Update Available";
                    contentTitle = "Application Update Available";
                    contentText = message;

                    notificationIntent = new Intent(
                            context.getApplicationContext(),
                            ActivityAppointmentDualFragment.class);
                    contentIntent = PendingIntent.getActivity(
                            context.getApplicationContext(), 0, notificationIntent,
                            0);

                    playsSound = true;
                default:
                    // Nothing
                    break;
            }

            Notification notification = new Notification(icon, tickerText, when);

            // Not all notifications need to make noise and vibrate

            if (AppSettingsUtilities.isExecuteNotificationSound()) {
                if (AppSettingsUtilities.isExecuteNotificationVibrate()) {
                    if (playsSound) {
                        long[] vibrate = {0, 100, 200, 300};
                        notification.vibrate = vibrate;
                        notification.defaults |= Notification.DEFAULT_SOUND;
                    }
                } else {
                    // Sound, Not vibrate
                    if (playsSound) {
                        notification.defaults |= Notification.DEFAULT_SOUND;
                    }
                }
            } else {
                if (AppSettingsUtilities.isExecuteNotificationVibrate()) {
                    if (playsSound) {
                        long[] vibrate = {0, 100, 200, 300};
                        notification.vibrate = vibrate;
                    }
                }
            }

            notification.setLatestEventInfo(context.getApplicationContext(),
                    contentTitle, contentText, contentIntent);
            mNotificationManager.notify(actionCode, notification);
        } else {
            if (actionCode == C2DMConstants.NOTIFICATION_TYPE_UPDATE_APPLICATION) {
                /* Notification of more recent app (need to update) */
                UserUtilitiesSingleton.getInstance().user.setNeedToUpdateApplication(true);
            } else if (actionCode == C2DMConstants.NOTIFICATION_TYPE_PING_GPS) {

                // context.bindService(new Intent(context,
                // ServicePingLocation.class), mConnection,
                // Context.BIND_AUTO_CREATE);
                context.startService(new Intent(context, PingLocation.class));
            }
        }

    }

    public static void clearNotification(Context context, int notificationId) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(ns);

        mNotificationManager.cancel(notificationId);
    }
}