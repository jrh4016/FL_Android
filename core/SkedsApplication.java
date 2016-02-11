package com.skeds.android.phone.business.core;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Build;


import com.newrelic.agent.android.NewRelic;
import com.skeds.android.phone.business.Pricebook.PricebookDatabase;
import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.Constants;
import com.skeds.android.phone.business.Utilities.General.UserUtilitiesSingleton;
import com.skeds.android.phone.business.Utilities.Logger;
import com.skeds.android.phone.business.Utilities.REST.RestConnector;
import com.skeds.android.phone.business.activities.ActivitySettingsView;
import com.skeds.android.phone.business.core.util.IOUtils;
import com.skeds.android.phone.business.data.UserAccount;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

//import com.newrelic.agent.android.NewRelic;

/**
 * Application object. Used for global preferences/options initializing, log
 * subsystem initialize.
 */
public class SkedsApplication extends Application implements Thread.UncaughtExceptionHandler {
    public static final String prefsFileName = "skedsData";

    /**
     * Name of shared preferences implementation that encrypts all stored data. This preferences
     * should be used to store any sensitive data like user credentials, access tokens, etc.
     */
    public static final String SECURED_PREFERENCES = SecuredSharedPreferences.PREFERENCES_NAME;

    /*
     * Application options / prefs
     */
    private SharedPreferences appPrefs;
    private boolean beta;
    private boolean notifySound = true;
    private boolean notifyVibrate = true;
    private boolean savePhotosLocally;
    private int applicationMode;
    private boolean useGPS = true;

    private UserAccount mUserAccount;

    public static Context getContext() {
        return sInstance != null ? sInstance.getApplicationContext() : null;
    }

    private static SkedsApplication sInstance;

    /**
     * Get application instance
     *
     * @return
     */
    public static SkedsApplication getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sInstance = this;

        Logger.init(this);


        appPrefs = getSharedPreferences(prefsFileName, Context.MODE_PRIVATE);
        beta = appPrefs.getBoolean("beta", false);

        AppDataSingleton.getInstance();
        UserUtilitiesSingleton.getInstance();

        UserUtilitiesSingleton.getInstance().setUsername(appPrefs.getString("login", ""));
        UserUtilitiesSingleton.getInstance().setPassword(appPrefs.getString("password", ""));
        notifySound = appPrefs.getBoolean("notifySound", true);
        notifyVibrate = appPrefs.getBoolean("notifyVibrate", true);
        savePhotosLocally = appPrefs.getBoolean("savePhotosLocally", false);


        useGPS = !"Kindle Fire".equals(Build.MODEL);
        if (useGPS) {
            useGPS = ((LocationManager) getSystemService(Context.LOCATION_SERVICE))
                    .getProvider(LocationManager.GPS_PROVIDER) != null;
        }

        NewRelic.withApplicationToken(
                "AAe3034418028a8562cea30240df50f625f8a16451"
        ).start(getApplicationContext());
    }



    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Logger.err("memory low!!!");
    }

    @Override
    public SharedPreferences getSharedPreferences(String name, int mode) {

        SharedPreferences prefs = super.getSharedPreferences(name, mode);
        if (SECURED_PREFERENCES.equals(name)) {
            // wrap secured shared preferences to easy encrypt/decrypt its properties
            prefs = new SecuredSharedPreferences(this, prefs);
        }
        return prefs;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        AlarmManager mgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000,
                PendingIntent.getActivity(this.getBaseContext(),
                        0, new Intent(this.getApplicationContext(),
                                ActivitySettingsView.class), 0
                )
        );
        // Required, for otherwise the restarting wont work
        Logger.info("APP CRASHES", ex.getStackTrace().toString() + "\n" + ex.getMessage() + "\n" + ex.getLocalizedMessage());

        System.exit(2);
    }

    public UserAccount getUserAccount() {
        return mUserAccount;
    }

    public void saveAppAndUserDataIntoFile() {
        FileOutputStream fos = null;
        ObjectOutputStream os = null;
        final Context context = getContext();

        try {
            fos = context.openFileOutput(Constants.APPLICATION_DATA_FILE, Context.MODE_PRIVATE);
            os = new ObjectOutputStream(fos);
            os.writeObject(AppDataSingleton.getReference());
        } catch (IOException e) {
            Logger.err(e);
        } finally {
            IOUtils.closeQuietly(fos);
            IOUtils.closeQuietly(os);
        }
        try {
            fos = context.openFileOutput(Constants.USER_DATA_FILE, Context.MODE_PRIVATE);
            os = new ObjectOutputStream(fos);
            os.writeObject(UserUtilitiesSingleton.getReference());
        } catch (IOException e) {
            Logger.err(e);
        } finally {
            IOUtils.closeQuietly(fos);
            IOUtils.closeQuietly(os);
        }
        Logger.debug("Data Have Been Saved Into File");
    }

    public void savePriceBookDbToFile() {
        FileOutputStream fos = null;
        ObjectOutputStream os = null;
        try {
            fos = getContext().openFileOutput(Constants.PRICEBOOK_DATABASE_FILE, Context.MODE_PRIVATE);
            os = new ObjectOutputStream(fos);
            os.writeObject(PricebookDatabase.getReference());
            Logger.debug("Pricebook Have Been Saved Into File");
        } catch (IOException e) {
            Logger.err(e);
        } finally {
            IOUtils.closeQuietly(fos);
            IOUtils.closeQuietly(os);
        }
    }

    public AppDataSingleton getAppDataFromFile() {
        FileInputStream fis = null;
        ObjectInputStream is = null;
        AppDataSingleton data = null;
        try {
            fis = getContext().openFileInput(Constants.APPLICATION_DATA_FILE);
            is = new ObjectInputStream(fis);
            data = (AppDataSingleton) is.readObject();
        } catch (IOException e) {
            Logger.err(e);
        } catch (ClassNotFoundException e) {
            Logger.err(e);
        } catch (Exception e) {
            Logger.err(e);
        } finally {
            IOUtils.closeQuietly(fis);
            IOUtils.closeQuietly(is);
        }
        return data;
    }

    public UserUtilitiesSingleton getUserDataFromFile() {
        FileInputStream fis = null;
        ObjectInputStream is = null;
        UserUtilitiesSingleton data = null;
        try {
            fis = getContext().openFileInput(Constants.USER_DATA_FILE);
            is = new ObjectInputStream(fis);
            data = (UserUtilitiesSingleton) is.readObject();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException e) {
            Logger.err(e);
        } catch (ClassNotFoundException e) {
            Logger.err(e);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            IOUtils.closeQuietly(fis);
            IOUtils.closeQuietly(is);
        }
        return data;
    }

    public PricebookDatabase getPriceBookFromFile() {
        FileInputStream fis = null;
        ObjectInputStream is = null;
        PricebookDatabase data = null;
        try {
            fis = getContext().openFileInput(Constants.PRICEBOOK_DATABASE_FILE);
            is = new ObjectInputStream(fis);
            data = (PricebookDatabase) is.readObject();
            is.close();
            return data;
        } catch (IOException e) {
            Logger.err(e);
        } catch (ClassNotFoundException e) {
            Logger.err(e);
        } finally {
            IOUtils.closeQuietly(fis);
            IOUtils.closeQuietly(is);
        }
        return data;
    }

    public void clearApplicationData() {
        final Context context = getContext();
        if (context.deleteFile(Constants.APPLICATION_DATA_FILE) && context.deleteFile(Constants.USER_DATA_FILE))
            Logger.debug("file_transaction", "Data Have Been Removed From File");
    }

    public void clearPriceBook() {
        if (getContext().deleteFile(Constants.PRICEBOOK_DATABASE_FILE))
            Logger.debug("Pricebook Have Been Removed From File");
    }

    /**
     * @return base part of server URL
     */
    public static String getBaseUrl() {
        SharedPreferences appPrefs = getContext().getSharedPreferences(SkedsApplication.prefsFileName, Context.MODE_PRIVATE);
        return sInstance.beta ? appPrefs.getString("betaUrl", sInstance.getString(R.string.beta_base_url)) : sInstance.getString(R.string.prod_base_url);
    }


    /**
     * @return true if "beta" server is using
     */
    public boolean isBeta() {
        return beta;
    }

    public boolean isUseGps() {
        return useGPS;
    }

    /**
     * Toggle "beta" mode
     *
     * @return new beta mode
     */
    public boolean toggleBeta() {
        beta = !beta;
        appPrefs.edit().putBoolean("beta", beta).apply();
        RestConnector.resetInstance();
        return beta;
    }

    public void setLineItemsMode(int mode) {
        appPrefs.edit().putInt("lineItemMode", mode).apply();
    }

    public int getLineItemsMode() {
        return appPrefs.getInt("lineItemMode", Constants.SHOW_ITEM_NAME);
    }

    /**
     * @return application mode (PHONE or TABLET)
     */
    public int getApplicationMode() {
        return applicationMode;
    }

    public void setApplicationMode(int mode) {
        applicationMode = mode;
    }

    public boolean isNotifySound() {
        return notifySound;
    }

    public void setNotifySound(boolean notifySound) {
        if (this.notifySound != notifySound) {
            this.notifySound = notifySound;
            appPrefs.edit().putBoolean("notifySound", notifySound).apply();
        }
    }

    public boolean isNotifyVibrate() {
        return notifyVibrate;
    }

    public void setNotifyVibrate(boolean notifyVibrate) {
        if (this.notifyVibrate != notifyVibrate) {
            this.notifyVibrate = notifyVibrate;
            appPrefs.edit().putBoolean("notifyVibrate", notifyVibrate).apply();
        }
    }

    public boolean isSavePhotosLocally() {
        return savePhotosLocally;
    }

    public void setSavePhotosLocally(boolean savePhotosLocally) {
        this.savePhotosLocally = savePhotosLocally;
        appPrefs.edit().putBoolean("savePhotosLocally", savePhotosLocally)
                .apply();
    }

    public void saveLogin() {
        appPrefs.edit().putString("login", UserUtilitiesSingleton.getInstance().getUsername()).apply();
        appPrefs.edit().putString("password", UserUtilitiesSingleton.getInstance().getPassword())
                .apply();
    }

    public void cleanLogin() {
        appPrefs.edit().putString("login", "").apply();
        appPrefs.edit().putString("password", "").apply();
    }
}
