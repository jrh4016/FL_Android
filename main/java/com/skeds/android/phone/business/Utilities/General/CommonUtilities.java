package com.skeds.android.phone.business.Utilities.General;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;

import java.math.BigDecimal;

public class CommonUtilities {
    /*
     * @return boolean return true if the application can access the internet
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivity.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    /*
     * Grabs Application Version
     */
    public static String getVersionName(Context context, Class<?> cls) {
        try {
            ComponentName comp = new ComponentName(context, cls);

            PackageInfo pinfo = context.getPackageManager().getPackageInfo(
                    comp.getPackageName(), 0);
            return pinfo.versionName;
        } catch (android.content.pm.PackageManager.NameNotFoundException e) {
            return "";
        }
    }

    /* For SD/External Storage */
    public static boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // We can read and write the media
            return true;
        } else {
            return false;
        }
    }

    /* For rounding double values */
    public static double roundDouble(double unrounded, int precision,
                                     int roundingMode) {
        BigDecimal bd = new BigDecimal(unrounded);
        BigDecimal rounded = bd.setScale(precision, roundingMode);
        return rounded.doubleValue();
    }
}