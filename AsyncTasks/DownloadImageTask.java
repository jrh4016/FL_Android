package com.skeds.android.phone.business.AsyncTasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.skeds.android.phone.business.Dialogs.DialogProgressPopup;
import com.skeds.android.phone.business.Utilities.General.CommonUtilities;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

    private Context mContext;

    private DialogProgressPopup popup = null;

    public DownloadImageTask(Context context) {
        super();
        mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if (mContext != null) {
            popup = new DialogProgressPopup(mContext, this);
            popup.setMessage("Loading the Photo...");
            popup.show();
        }
    }

    /**
     * The system calls this to perform work in a worker thread and delivers it
     * the parameters given to AsyncTask.execute()
     */
    protected Bitmap doInBackground(String... urls) {
        if (!CommonUtilities.isNetworkAvailable(mContext)) {
            Toast.makeText(mContext, "Network connection unavailable.",
                    Toast.LENGTH_SHORT).show();
            return null;
        }

        URL url;
        try {
            url = new URL(urls[0]);
            Object content = url.getContent();
            InputStream is = (InputStream) content;
            Bitmap bm = BitmapFactory.decodeStream(is);
            is.close();
            return bm;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * The system calls this to perform work in the UI thread and delivers the
     * result from doInBackground()
     */
    protected void onPostExecute(Bitmap result) {
        terminate();
        if (result == null)
            return;
        Log.e("IMAGE_SIZE", result.getWidth() + "  " + result.getHeight());
    }

    @Override
    protected void onCancelled() {
        terminate();
        if (mContext != null)
            Toast.makeText(mContext, "Canceled", Toast.LENGTH_SHORT).show();
    }

    private void terminate() {
        if (popup != null) {
            popup.dismiss();
            popup = null;
        }
    }

}
