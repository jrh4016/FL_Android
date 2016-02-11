package com.skeds.android.phone.business.Utilities.REST;

import android.app.Activity;

import com.skeds.android.phone.business.Custom.CallBackListener;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to send notifications to UI thread
 *
 * @author Den Oleshkevich
 */

public class CallBackManager {

    private static CallBackManager instance;

    private List<CallBackListener> listeners;

    private CallBackManager() {
        listeners = new ArrayList<CallBackListener>();

    }

    public static final CallBackManager get() {

        if (instance == null) {
            instance = new CallBackManager();
        }
        return instance;
    }

    public void registerListener(Activity activity) {
        if (listeners == null)
            listeners = new ArrayList<CallBackListener>();
        listeners.add((CallBackListener) activity);
    }

    public void succesfully(String action) {
        if (listeners == null)
            return;

        for (CallBackListener l : listeners) {
            l.onSuccess(action);
        }
    }

    public void failed(String action) {
        if (listeners == null)
            return;

        for (CallBackListener l : listeners) {
            l.onFailed(action);
        }
    }

    public void unregisterListener(Activity activity) {
        if (listeners == null)
            return;
        listeners.remove((CallBackListener) activity);
    }

    public void removeAllListener(Activity activity) {
        if (listeners == null)
            return;
        listeners.clear();
    }

}
