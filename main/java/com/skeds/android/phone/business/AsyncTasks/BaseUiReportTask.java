/**
 *
 */
package com.skeds.android.phone.business.AsyncTasks;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.skeds.android.phone.business.Dialogs.DialogProgressPopup;
import com.skeds.android.phone.business.Utilities.Logger;

/**
 * Base async task that will report user about progress start/end, operation
 * error. User can abort task background operations and {@link #cancel(boolean)}
 * with true arg will be called. Override {@link #taskBody(Object...)} method to
 * implement actual task body.
 */
public abstract class BaseUiReportTask<Params>
        extends
        AsyncTask<Params, String, Boolean>
        implements
        DialogInterface.OnDismissListener {
    private Activity parent;
    private Fragment fragment;
    private Exception error = null;
    private DialogProgressPopup popup = null;
    private String progressMessage = null;
    // if true parent activity will be closed if taskBody() doest not return
    // success
    private boolean autoCloseOnFalse = false;
    // if true parent activity will be closed if taskBody() does return success
    private boolean autoCloseOnSuccess = false;
    private boolean taskSuccess = false;

    /**
     * Create new task
     *
     * @param parent          parent activity which will be blocked while task is not
     *                        complted or canceled. If null no UI pop-ups will be shown.
     * @param progressMessage progress message or null for default message
     * @param allowTaskCancel set to true if task may be cancelled by user
     */
    public BaseUiReportTask(Activity parent, String progressMessage) {
        this.parent = parent;
        this.progressMessage = progressMessage;
    }

    public final AsyncTask<Params, String, Boolean> attachFragment(Fragment f) {
        this.fragment = f;
        return this;
    }

    /**
     * Create new task
     *
     * @param parent   parent activity
     * @param msgresid message resource id (R.string.<i>id</i>)
     */
    public BaseUiReportTask(Activity parent, int msgresid) {
        this(parent, parent.getResources().getString(msgresid));
    }

    public BaseUiReportTask(Activity parent) {
        this(parent, null);
    }

    public void setAutocloseOnNotSuccess(boolean doAutoclose) {
        autoCloseOnFalse = doAutoclose;
    }

    public void setAutocloseOnSuccess(boolean doAutoclose) {
        autoCloseOnSuccess = doAutoclose;
    }

    public void setProgressMessage(String message) {
        progressMessage = message;
        if (popup != null) {
            popup.setMessage(progressMessage);
        }
    }

    public final boolean isSuccess() {
        return taskSuccess;
    }

    public void launchActivity(Class<?> cl) {
        if (parent != null) {
            parent.startActivity(new Intent(parent, cl));
        }
    }

    public void launchActivity(Bundle bundle, Class<?> cl) {
        if (parent != null) {
            final Intent i = new Intent(parent, cl);
            i.putExtras(bundle);
            parent.startActivity(i);
        }
    }

    /**
     * Task desctructor is called in UI thread when task finished. Override it
     * in child to do task clean-up operations.
     */
    protected void dtor() {
    }

    /**
     * Override in child to handle successfully task completition
     */
    protected void onSuccess() {
    }

    protected void onFailed() {
    }

    /**
     * Called instead of {@link #onSuccess()} if taskBody() failed or returned
     * false but before user closed error pop-up
     */
    // protected void onFiasco() {}
    public void terminate() {
        // isShowing to prevent "java.lang.IllegalArgumentException: View not attached to window manager" bug
        if ((popup != null) && (popup.isShowing())) {
            try {

                popup.dismiss();
                popup = null;

            } catch (Exception ex){
                //if activity is not attached
            }
        }
        dtor();
    }

    private void doAutoCloseOnFalse() {
        if (autoCloseOnFalse && parent != null) {
            parent.finish();
            parent = null;
        }
    }

    public void onDismiss(DialogInterface dialog) {
        doAutoCloseOnFalse();
    }

    /**
     * If overriden in child, parent <u>should</u> be called
     */
    @Override
    protected void onPreExecute() {
        if (parent != null) {
            popup = new DialogProgressPopup(parent, this);
            if (progressMessage != null) {
                popup.setMessage(progressMessage);
                popup.show();
            }
        }
    }

    /**
     * If overriden in child, parent <u>should</u> be called
     */
    @Override
    protected final void onPostExecute(Boolean success) {

        if (fragment != null) {
            if (fragment.getView() == null) {
                return;
            }
        }

        taskSuccess = success;
        if (popup != null)
            if (!popup.isShowing())
                return;

        terminate();
        if (success) {
            onSuccess();
            if (autoCloseOnSuccess && parent != null) {
                parent.finish();
                parent = null;
            }
            return;
        } else
            onFailed();

        if (error != null && parent != null) {
//			DialogErrorPopup errorPopup = new DialogErrorPopup(parent, null,
//					null, error);
//			errorPopup.show();
//			errorPopup.setOnDismissListener(this);

            if (error.getMessage() != null) {
                if (error.getMessage().contains("401")) {
                    Toast.makeText(parent, "Invalid username or password.", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(parent, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else
            doAutoCloseOnFalse();
    }

    @Override
    protected void onProgressUpdate(String... values) {
        if (popup != null && values != null && values.length > 0) {
            popup.setMessage(values[0]);
        }
    }

    /**
     * If overriden in child, parent <u>should</u> be called
     */
    @Override
    protected void onCancelled() {
        terminate();
        error = null;
        doAutoCloseOnFalse();
    }

    /**
     * Actual task body, called from {@link #doInBackground(Object...)}
     */
    protected abstract boolean taskBody(Params... params) throws Exception;

    @Override
    protected final Boolean doInBackground(Params... params) {
        try {
            return taskBody(params);
        } catch (Exception e) {
            error = e;
            Logger.err("ASYNC_TASK", e);
        }
        return false;
    }
}
