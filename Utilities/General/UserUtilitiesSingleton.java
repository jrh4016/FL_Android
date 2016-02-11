package com.skeds.android.phone.business.Utilities.General;

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.User;
import com.skeds.android.phone.business.Utilities.REST.RestConnector;
import com.skeds.android.phone.business.core.SkedsApplication;

import java.io.IOException;
import java.io.Serializable;

public class UserUtilitiesSingleton implements Serializable {

    private static UserUtilitiesSingleton instance;

    private UserUtilitiesSingleton() {

    }

    public static UserUtilitiesSingleton getInstance() {
        if (instance == null) {

            UserUtilitiesSingleton data = SkedsApplication.getInstance().getUserDataFromFile();
            if (data != null) {
                Log.d("file_transaction", "User Data Have Been Retrieved From File");
                instance = data;
                return data;
            }

            Log.e("USER_DATA", "!User Data Instance Created!");
            instance = new UserUtilitiesSingleton();
        }
        return instance;
    }

    public static UserUtilitiesSingleton getReference() {
        return instance;
    }

    public static void clear() {
        instance = null;
    }

    public User user = new User();

    private String password = "";

    private String name = "";

    private String username = "";

    public void userLogout(Activity activity, final Context context, Class<?> redirectClassOnYes) {
        /** This unregisters their current c2dm setup */
        user.setC2dmRegistered(false);
        Intent unregIntent = new Intent("com.google.android.c2dm.intent.UNREGISTER");
        unregIntent.putExtra("app", PendingIntent.getBroadcast(context, 0, new Intent(), 0));
        context.startService(unregIntent);


        new Thread(new Runnable() {
            @Override
            public void run() {
                GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
                try {
                    gcm.unregister();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();


        //GCMRegistrar.unregister(context);


        SkedsApplication.getInstance().clearApplicationData();
        AppDataSingleton.clear();
        UserUtilitiesSingleton.clear();
        RestConnector.resetInstance();

        cleanLogin(context);

        if (redirectClassOnYes != null) {
            Intent i = new Intent(context, redirectClassOnYes);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(i);
        }

        Log.d("[CommonUtilities]", "User Was Logged Out.");
        UserUtilitiesSingleton.getInstance().user.setLoggedIn(false);
    }

    public void userLogoutPrompt(Activity activity, Context context, int layoutResId,
                                 int textTitleResId, int textBodyResId, int yesButtonResId, int noButtonResId,
                                 Class<?> redirectClassOnYes) {

        final Activity activityHandoff = activity;
        final Class<?> redirectClassHandoff = redirectClassOnYes;
        final Context contextHandoff = context;

        final Dialog logoutDialog = new Dialog(contextHandoff);
        logoutDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        logoutDialog.setContentView(layoutResId);

        TextView titleText = (TextView) logoutDialog.findViewById(textTitleResId);
        TextView bodyText = (TextView) logoutDialog.findViewById(textBodyResId);

        titleText.setText("User Logout");
        bodyText.setText("Are you sure you want to logout as " + user.getFirstName() + " "
                + user.getLastName() + "?");

        TextView yesButton = (TextView) logoutDialog.findViewById(yesButtonResId);
        TextView noButton = (TextView) logoutDialog.findViewById(noButtonResId);

        yesButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                userLogout(activityHandoff, contextHandoff, redirectClassHandoff);
                logoutDialog.dismiss();
            }
        });

        noButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                logoutDialog.dismiss();
            }
        });

        logoutDialog.show();
    }

    public void userYesNoPrompt(final Activity activity, Context context, int layoutResId,
                                int textTitleResId, int textBodyResId, int yesButtonResId, int noButtonResId,
                                String title, String message) {
        final Activity activityHandoff = activity;
        final Context contextHandoff = context;

        final Dialog logoutDialog = new Dialog(contextHandoff);
        logoutDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        logoutDialog.setContentView(layoutResId);

        TextView titleText = (TextView) logoutDialog.findViewById(textTitleResId);
        TextView bodyText = (TextView) logoutDialog.findViewById(textBodyResId);

        titleText.setText(title);
        bodyText.setText(message);

        TextView yesButton = (TextView) logoutDialog.findViewById(yesButtonResId);
        TextView noButton = (TextView) logoutDialog.findViewById(noButtonResId);

        yesButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                logoutDialog.dismiss();

//                userLogout(activityHandoff, contextHandoff, null);

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                activity.startActivity(intent);
            }
        });

        noButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                logoutDialog.dismiss();
            }
        });

        logoutDialog.show();
    }

    public void userQuitPrompt(Activity activity, Context context, int layoutResId,
                               int textTitleResId, int textBodyResId, int yesButtonResId, int noButtonResId) {

        userYesNoPrompt(activity, context, layoutResId, textTitleResId, textBodyResId,
                yesButtonResId, noButtonResId, "FieldLocate", "Are you sure you want to exit?");

    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        RestConnector.resetInstance();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
        RestConnector.resetInstance();
    }

    public void cleanLogin(Context context) {
        SharedPreferences appPrefs = context.getSharedPreferences(SkedsApplication.prefsFileName, Context.MODE_PRIVATE);
        appPrefs.edit().putString("login", "").apply();
        appPrefs.edit().putString("password", "").apply();
    }

}
