package com.skeds.android.phone.business.AsyncTasks;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.Logger;
import com.skeds.android.phone.business.Utilities.REST.Objects.RESTPhoto;
import com.skeds.android.phone.business.core.ContentLengthInputStream;
import com.skeds.android.phone.business.core.util.IOUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class SubmitPhotoTask extends BaseUiReportTask<String> {

    private static final String TAG = SubmitPhotoTask.class.getName();

    private static final int WIDTH_HEIGHT_BITMAP = 700;

    private static final int BUFFER_SIZE = 32 * 1024; // 32 Kb

    public SubmitPhotoTask(Activity parent) {
        super(parent, R.string.async_task_string_submitting_new_photo);
    }

    private InputStream resizeBitmap(final String bitmapPath) throws IOException {
        Logger.err(TAG, "Bitmap path: " + bitmapPath);
        InputStream imageStream = getImageStream(bitmapPath);
        try {
            // Get the dimensions of the bitmap
            final Bitmap subSampledBitmap = BitmapFactory.decodeStream(imageStream);
            if (subSampledBitmap != null) {
                final int newWidth;
                final int newHeight;
                if (subSampledBitmap.getWidth() > subSampledBitmap.getHeight()) {
                    newWidth = WIDTH_HEIGHT_BITMAP;
                    newHeight = subSampledBitmap.getHeight() * WIDTH_HEIGHT_BITMAP / subSampledBitmap.getWidth();
                } else {
                    newWidth = subSampledBitmap.getWidth() * WIDTH_HEIGHT_BITMAP / subSampledBitmap.getHeight();
                    newHeight = WIDTH_HEIGHT_BITMAP;
                }

                final float scaleWidth = newWidth / (float) subSampledBitmap.getWidth();
                final float scaleHeight = newHeight / (float) subSampledBitmap.getHeight();

                Matrix matrix = new Matrix();
                matrix.postScale(scaleWidth, scaleHeight);
                final Bitmap finalBitmap = Bitmap.createBitmap(subSampledBitmap, 0, 0, subSampledBitmap.getWidth(),
                        subSampledBitmap.getHeight(),
                        matrix, true);

                if (finalBitmap != subSampledBitmap) {
                    subSampledBitmap.recycle();
                }

                final ByteArrayOutputStream bos = new ByteArrayOutputStream(BUFFER_SIZE);
                ByteArrayInputStream stream;
                try {
                    finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                    stream = new ByteArrayInputStream(bos.toByteArray());
                } finally {
                    IOUtils.closeQuietly(bos);
                    finalBitmap.recycle();
                }
                return stream;
            }
        } finally {
            IOUtils.closeQuietly(imageStream);
        }
        return null;
    }

    private InputStream getImageStream(final String filePath) throws IOException {
        return new ContentLengthInputStream(new BufferedInputStream(new FileInputStream(filePath), BUFFER_SIZE),
                new File(filePath).length());
    }

    @Override
    protected boolean taskBody(final String... args) throws Exception {
        for (final String arg : args) {
            Logger.err(TAG, "Args: " + arg);
        }
        RESTPhoto.add(args[0], args[1], resizeBitmap(args[2]),
                args[3]);
        return true;
    }
}
