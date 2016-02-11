package com.skeds.android.phone.business.Services;

import android.app.IntentService;
import android.content.Intent;
import android.widget.Toast;

import com.google.analytics.tracking.android.Log;
import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.General.Constants;
import com.skeds.android.phone.business.Utilities.Logger;
import com.skeds.android.phone.business.Utilities.NonfatalException;
import com.skeds.android.phone.business.Utilities.REST.CallBackManager;
import com.skeds.android.phone.business.Utilities.REST.Objects.RESTUploadFile;

import org.jdom2.Document;

import java.io.IOException;

/**
 * Run REST service
 *
 * @author Den Oleshkevich
 */

public class RestIntentService extends IntentService {

    private static final String SERVICE_NAME = "Skeds_Service";

    public static final String EXTRA_FILE_PATH = "file_path";
    public static final String EXTRA_CUSTOMER_ID = "customer_id";
    public static final String EXTRA_INVOICE_ID = "invoice_id";
    public static final String EXTRA_APPOINTMENT_ID = "appt_id";
    public static final String EXTRA_FILE_NAME = "file_name";
    public static final String EXTRA_DISPLAY_NAME = "display_name";
    public static final String EXTRA_FILE_EXTENSION = "file_extension";

    private CallBackManager callbackManager;

    public RestIntentService() {
        super(SERVICE_NAME);
        callbackManager = CallBackManager.get();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final String action = intent.getAction();
        if (action == null) {
            Log.e(getString(R.string.no_actions));
            return;
        }

        try {
            handleRequest(intent, action);
        } catch (NonfatalException e) {
            Logger.info("APP CRASHES", e.getStackTrace().toString() + "\n" + e.getMessage() + "\n" + e.getLocalizedMessage());
            callbackManager.failed(action);
        } catch (IOException e) {
            Logger.info("APP CRASHES", e.getStackTrace().toString() + "\n" + e.getMessage() + "\n" + e.getLocalizedMessage());
            callbackManager.failed(action);
        }
    }

    private void handleRequest(Intent intent, final String action) throws NonfatalException, IOException {
        if (action.equals(Constants.ACTION_SEND_FILE)) {

            Document doc = RESTUploadFile.send(intent.getStringExtra(EXTRA_FILE_PATH), intent.getIntExtra(EXTRA_CUSTOMER_ID, 0),
                    intent.getIntExtra(EXTRA_INVOICE_ID, 0), intent.getIntExtra(EXTRA_APPOINTMENT_ID, 0), intent.getStringExtra(EXTRA_FILE_NAME),
                    intent.getStringExtra(EXTRA_DISPLAY_NAME), intent.getStringExtra(EXTRA_FILE_EXTENSION));
            Toast.makeText(getBaseContext(), "File has been sent", Toast.LENGTH_SHORT).show();
        }
        // Successfull feedback
        callbackManager.succesfully(action);
    }
}