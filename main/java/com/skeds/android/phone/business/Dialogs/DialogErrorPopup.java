/**
 *
 */
package com.skeds.android.phone.business.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.General.Constants;
import com.skeds.android.phone.business.Utilities.NonfatalException;
import com.skeds.android.phone.business.activities.ActivityLoginMobile;
import com.skeds.android.phone.business.activities.ActivityLoginTablet;
import com.skeds.android.phone.business.core.SkedsApplication;

/**
 * Error popup
 */
public class DialogErrorPopup extends Dialog implements View.OnClickListener {
    private CharSequence title;
    private final StringBuilder msg = new StringBuilder();

    private Context context;

    /**
     * Prepare error pop-up
     *
     * @param context parent activity
     * @param title   pop-up title or null for default title
     * @param amsg    pop-up message or null
     * @param e       error cause or null
     */
    public DialogErrorPopup(Context context, CharSequence title,
                            CharSequence amsg, Exception e) {
        super(context, R.style.Theme_ProgressPopup);
        this.title = title;
        this.context = context;

        if (amsg != null)
            msg.append(amsg);
        if (e != null) {
            msg.append("\n");
            if (e instanceof NonfatalException) {
                NonfatalException nfe = (NonfatalException) e;
                if (title == null)
                    title = nfe.tag + " error";
                msg.append(nfe.getMessage());
                if (nfe.getCause() != null) {
                    msg.append('\n').append(nfe.getCause().getMessage());
                }
            } else {
                msg.append(e.getMessage());
            }

            Class loginClass;

            if (SkedsApplication.getInstance().getApplicationMode() == Constants.APPLICATION_MODE_PHONE_SERVICE)
                loginClass = ActivityLoginMobile.class;
            else loginClass = ActivityLoginTablet.class;

            if (msg.toString().contains("Unauthorized"))
                if (context.getClass() == loginClass) {
                    msg.delete(0, msg.length());
                    msg.append("Invalid username or password.");
                }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.popup_error);
        findViewById(R.id.btn_close).setOnClickListener(this);
        if (title != null)
            ((TextView) findViewById(R.id.text_title)).setText(title);
        if (msg.length() > 0)
            ((TextView) findViewById(R.id.text_message)).setText(msg);
        setCancelable(true);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_close)
            dismiss();
    }
}
