package com.skeds.android.phone.business.util;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;

import com.skeds.android.phone.business.core.SkedsApplication;

import java.io.File;

public final class SystemUtils {
    private SystemUtils() {
    }

    /**
     * Check if any apps can handle intent.
     *
     * @param intent intent to handle
     * @return true if there is at least one application can handle intent otherwise false
     */
    public static boolean hasActivityToHandleIntent(final Intent intent) {
        return intent != null && intent.resolveActivity(SkedsApplication.getContext().getPackageManager()) != null;
    }

    public static File getFilesDirToStoreImage(final Context context) {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                ? Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) :
                context.getFilesDir();
    }
}
