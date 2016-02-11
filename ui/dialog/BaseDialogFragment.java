package com.skeds.android.phone.business.ui.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;

public abstract class BaseDialogFragment extends DialogFragment {

    private static final String KEY_CANCELABLE = "dialog_cancelable";

    private DialogInterface.OnCancelListener mOnCancelListener;
    private OnCreateDialogListener mOnCreateDialogListener;

    boolean mCancelable = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // disable native cancelable support because when dialog is cancelable it can be dismissed
        // by "Search" pressed or by tap outside the dialog. We'd like to cancel dialogs only by
        // pressing "Back" button.
        super.setCancelable(false);

        if (savedInstanceState != null) {
            mCancelable = savedInstanceState.getBoolean(KEY_CANCELABLE, true);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(KEY_CANCELABLE, mCancelable);
        super.onSaveInstanceState(outState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_BACK:
                        // dialog can be canceled only by back pressed.
                        if (mCancelable) dialog.cancel();
                        return true;
                    case KeyEvent.KEYCODE_SEARCH:
                        return true;
                    default:
                        return false;
                }
            }
        });

        if (mOnCreateDialogListener != null) {
            mOnCreateDialogListener.onCreateDialog(this, dialog);
        }

        return dialog;
    }

    @Override
    public void setCancelable(boolean cancelable) {
        mCancelable = cancelable;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        if (mOnCancelListener != null) {
            mOnCancelListener.onCancel(dialog);
        }
    }

    public void setOnCancelListener(DialogInterface.OnCancelListener listener) {
        mOnCancelListener = listener;
    }

    public void setOnCreateDialogListener(OnCreateDialogListener listener) {
        mOnCreateDialogListener = listener;
    }

    public static interface OnCreateDialogListener {
        void onCreateDialog(final DialogFragment fragment, final Dialog createdDialog);
    }
}
