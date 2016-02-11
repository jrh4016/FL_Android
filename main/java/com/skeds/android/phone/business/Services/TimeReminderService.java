package com.skeds.android.phone.business.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;

import java.util.Timer;
import java.util.TimerTask;

public class TimeReminderService extends Service {

    private NotificationManager nm;

    private Timer mTimer;

    private RemindTimerTask mTask;

    private int count = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("Time service", "STARTED!");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {

        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mTimer = new Timer();
        mTask = new RemindTimerTask();

        mTimer.schedule(mTask, 28800000, 3600000);
        return super.onStartCommand(intent, flags, startId);
    }

    void sendNotif() {
        Notification notif = new Notification(R.drawable.icon,
                "Time Clock Reminder", System.currentTimeMillis());

        Intent intent = new Intent();
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

        if ((AppDataSingleton.getInstance().getHoursWorked() != null) &&
                (AppDataSingleton.getInstance().getHoursWorked() != null) &&
                (AppDataSingleton.getInstance().getHoursWorked().getClockInTime() != null))
            notif.setLatestEventInfo(this, "Time Clock Reminder",
                    "You Are Still Clocked In Since " + AppDataSingleton.getInstance().getHoursWorked().getClockInTime(), pIntent);
        else return;


        // set flag for notification to be desappeared after tapping
        notif.flags |= Notification.FLAG_AUTO_CANCEL;

        nm.notify(1, notif);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopTask();
        Log.e("Time service", "STOPPED!");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void stopTask() {
        if (mTask != null) {
            mTask.cancel();
            mTimer.cancel();
        }
    }

    class RemindTimerTask extends TimerTask {
        public void run() {
            sendNotif();

            count++;
            if (count == 18)
                stopTask();
        }
    }

}
