package com.skeds.android.phone.business;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.skeds.android.phone.business.C2DMUtilities.C2DMConstants;
import com.skeds.android.phone.business.C2DMUtilities.C2DMRedirect;
import com.skeds.android.phone.business.Services.PingLocation;
import com.skeds.android.phone.business.Utilities.General.AppSettingsUtilities;
import com.skeds.android.phone.business.Utilities.General.UserUtilitiesSingleton;
import com.skeds.android.phone.business.activities.ActivityAppointmentDualFragment;

import org.json.JSONObject;

public class GCMIntentService extends IntentService {

    private static final String GCM_DATA_ACTION = "action";

    public static String googleAppId = "666058075679";
    // protected static String apiKey =
    // "AIzaSyDo2QUaUpJsK-wFsLBUN4RgxcF-qfLNdCg";

    private final String DEBUG_TAG = "[GCM]";

    private Context context;

    @SuppressWarnings("hiding")
    private static final String TAG = "GCMIntentService";

    public GCMIntentService() {
        super("GcmIntentService");
        Log.i(TAG, "GcmIntentService");
    }

    private void setupNotification(String notificationString) {

        // Change the message text, and launch class activity based upon the
        // notificationString data
        // Payload is:
        //
        /*
         * {"alert":"Removed from Service Call: Wednesday, July 6 - 7:00 PM",
		 * "apptId":"1044","acc":4,"app":true}
		 */

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

            displayNotification(context, alert, apptId, actionCode, app);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * This will have to be moved to its own private file because much of it is
     * specific to this app and its c2dm functions
     */
    public static void displayNotification(Context context, String message,
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
                    break;
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

    @Override
    protected void onHandleIntent(Intent intent) {

        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                //sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
                //sendNotification("Deleted messages on server: " +
                //        extras.toString());
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // This loop represents the service doing some work.

                Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
                // Post notification of received message.
                //sendNotification("Received: " + extras.toString());
                Log.i(TAG, "Received: " + extras.toString());

                publishIntent(intent);
            }
        }


    }

    private void publishIntent(Intent intent) {
        Log.d(DEBUG_TAG, "HandleMessage Function Reached.");
        String action = intent.getStringExtra("action");
        String title = intent.getStringExtra("title");
        String message = intent.getStringExtra("msg");
        String payload = intent.getStringExtra("payload");

        Log.i(DEBUG_TAG, "======= Action is: " + action);
        Log.i(DEBUG_TAG, "======= Title is: " + title);
        Log.i(DEBUG_TAG, "======= Message is: " + message);
        Log.i(DEBUG_TAG, "======= Payload is: " + payload);
        Log.i(DEBUG_TAG, "======= Intent is: " + intent);

        this.context = getApplicationContext();

        if (UserUtilitiesSingleton.getInstance().user.isLoggedIn())
            setupNotification(payload);
    }

//    private void sendNotification(String msg) {
//        mNotificationManager = (NotificationManager)
//                this.getSystemService(Context.NOTIFICATION_SERVICE);
//
//        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
//                new Intent(this, DemoActivity.class), 0);
//
//        NotificationCompat.Builder mBuilder =
//                new NotificationCompat.Builder(this)
//                        .setSmallIcon(R.drawable.ic_stat_gcm)
//                        .setContentTitle("GCM Notification")
//                        .setStyle(new NotificationCompat.BigTextStyle()
//                                .bigText(msg))
//                        .setContentText(msg);
//
//        mBuilder.setContentIntent(contentIntent);
//        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
//    }
}
