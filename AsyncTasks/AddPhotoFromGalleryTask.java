package com.skeds.android.phone.business.AsyncTasks;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;

import net.iharder.utils.Base64;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class AddPhotoFromGalleryTask extends BaseUiReportTask<String> {

    private Uri selectedImage;

    private Activity activity;

    private String photoOutput;

    public AddPhotoFromGalleryTask(Activity parent, Uri uri) {
        super(parent, "Retrieving Photo from Gallery...");
        activity = parent;
        selectedImage = uri;
    }

    @Override
    protected boolean taskBody(String... params) throws Exception {
        try {
            InputStream imageStream;
            imageStream = activity.getContentResolver().openInputStream(selectedImage);
            Bitmap bi = BitmapFactory.decodeStream(imageStream);
            imageStream.close();

            int newWidth;
            int newHeight;

            if (bi.getWidth() > bi.getHeight()) {
                newWidth = 700;
                newHeight = (bi.getHeight() * 700)
                        / bi.getWidth();
            } else {
                newWidth = (bi.getWidth() * 700)
                        / bi.getHeight();
                newHeight = 700;
            }

            float scaleWidth = ((float) newWidth)
                    / bi.getWidth();
            float scaleHeight = ((float) newHeight)
                    / bi.getHeight();

            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);
            Bitmap resizedBitmap = Bitmap.createBitmap(bi, 0,
                    0, bi.getWidth(), bi.getHeight(), matrix,
                    true);
            bi.recycle();

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG,
                    100, bos);
            resizedBitmap.recycle();

            byte[] bitmapdata = bos.toByteArray();
            String output = Base64.encodeBytes(bitmapdata);

            bitmapdata = null;
            photoOutput = output;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
        }
        return true;
    }

    public String getPhotoOutput() {
        return photoOutput;
    }
}