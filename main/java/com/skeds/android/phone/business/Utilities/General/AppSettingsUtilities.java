package com.skeds.android.phone.business.Utilities.General;

import com.skeds.android.phone.business.core.SkedsApplication;

public class AppSettingsUtilities {

    @Deprecated
    public static boolean isExecuteNotificationSound() {
        return SkedsApplication.getInstance().isNotifySound();
    }

    @Deprecated
    public static boolean isExecuteNotificationVibrate() {
        return SkedsApplication.getInstance().isNotifyVibrate();
    }

    @Deprecated
    public static boolean isShouldSavePhotosLocally() {
        return SkedsApplication.getInstance().isSavePhotosLocally();
    }

    @Deprecated
    public static int getApplicationMode() {
        return SkedsApplication.getInstance().getApplicationMode();
    }

    @Deprecated
    public static boolean isBetaServerMode() {
        return SkedsApplication.getInstance().isBeta();
    }

    @Deprecated
    public static boolean isUseGPS() {
        return SkedsApplication.getInstance().isUseGps();
    }

}