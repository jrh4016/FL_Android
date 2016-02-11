/**
 *
 */
package com.skeds.android.phone.business.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.skeds.android.phone.business.R;

/**
 * Progress popup
 */
public class DialogProgressPopup extends Dialog implements View.OnClickListener {
    private AsyncTask<?, ?, ?> task;
    private CharSequence msg;
    private View btnAbort;
    private TextView messageView = null;

    private Context context;

    /**
     * Create progress popup
     *
     * @param context context
     * @param msg     message to diplay or null for default message
     *                ("Please wait...")
     * @param task    if non-null specified task can be cancelled by user long press
     *                on "Abort" button
     */
    public DialogProgressPopup(Context context, AsyncTask<?, ?, ?> task) {
        super(context, R.style.Theme_ProgressPopup);
        this.task = task;
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.popup_progress);
        btnAbort = findViewById(R.id.btn_abort);
        if (btnAbort != null)
            btnAbort.setVisibility(View.GONE);
        messageView = (TextView) findViewById(R.id.text_message);
        if (msg != null)
            messageView.setText(msg);
        setCancelable(false);
    }

    @Override
    public void onBackPressed() {
        if (task != null) {
            btnAbort.setOnClickListener(this);
            btnAbort.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_abort && v.isEnabled()) {
            Toast.makeText(context, "Canceled", Toast.LENGTH_SHORT).show();
            if (task == null)
                dismiss();
            else {
                task.cancel(true);
                v.setEnabled(false);
                dismiss();
            }
        }
    }

    public void setMessage(String m) {
        msg = m;
        if (messageView != null)
            messageView.setText(m);
    }
}
