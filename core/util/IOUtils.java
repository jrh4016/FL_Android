package com.skeds.android.phone.business.core.util;

import java.io.Closeable;
import java.io.IOException;


public class IOUtils {

    public static void closeQuietly(Closeable is) {
        if (is != null) {
            try {
                is.close();
            } catch (IOException ignored) {
            }
        }
    }
}
