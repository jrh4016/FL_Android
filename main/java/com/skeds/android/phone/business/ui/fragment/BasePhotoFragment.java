package com.skeds.android.phone.business.ui.fragment;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.UserUtilitiesSingleton;
import com.skeds.android.phone.business.Utilities.Logger;
import com.skeds.android.phone.business.Utilities.NonfatalException;
import com.skeds.android.phone.business.Utilities.REST.RestConnector;
import com.skeds.android.phone.business.core.ContentLengthInputStream;
import com.skeds.android.phone.business.core.SkedsApplication;
import com.skeds.android.phone.business.core.async.BaseAsyncTaskLoader;
import com.skeds.android.phone.business.core.util.IOUtils;
import com.skeds.android.phone.business.ui.dialog.AlertDialogFragment;
import com.skeds.android.phone.business.util.SystemUtils;

import org.jdom2.Document;
import org.jdom2.Element;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

public class BasePhotoFragment extends BaseSkedsFragment implements View.OnClickListener,
        AlertDialogFragment.OnBodyInflatedListener, AlertDialogFragment.OnClickListener,
        LoaderManager.LoaderCallbacks<Boolean> {

    private static final String CAPTURE_PHOTO_FRAGMENT_TAG = "capture_photo_tag";
    private static final String DESCRIPTION_PHOTO_FRAGMENT_TAG = "description_tag";

    private static final int UPLOAD_PHOTO_LOADER_ID = 10;

    private static final String TEMP_PHOTO_FILE_DIR = "temp";
    private static final String MIME_TYPE_IMAGE = "image/*";

    private static final String KEY_PHOTO_PATH = "photo_path";
    private static final String KEY_PHOTO_DESC = "photo_desc";

    private static final int RQ_CAPTURE_PHOTO = 10;
    private static final int RQ_GALLERY_PHOTO = 20;

    private AlertDialogFragment mPhotoDialog;
    private EditText mDescription;

    private String mPhotoPath;
    private String mPhotoDesc = "";
    private CheckBox addPhotoToPDFCheckBox;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        fragmentView.findViewById(R.id.capture_photo).setOnClickListener(this);

        mPhotoDialog = (AlertDialogFragment) getFragmentManager().findFragmentByTag(CAPTURE_PHOTO_FRAGMENT_TAG);
        if (mPhotoDialog != null) {
            mPhotoDialog.setOnBodyInflatedListener(this);
        }

        Logger.err("Created: " + String.valueOf(savedInstanceState != null));
        if (savedInstanceState != null) {
            mPhotoPath = savedInstanceState.getString(KEY_PHOTO_PATH);
            mPhotoDesc = savedInstanceState.getString(KEY_PHOTO_DESC);
        } else {
            //delete left files after upload that was not finished right or cancelled

        }


        Logger.err("Created mPhotoPath: " + mPhotoPath);

        final AlertDialogFragment photoDescriptionDialog = (AlertDialogFragment) getFragmentManager().findFragmentByTag(
                DESCRIPTION_PHOTO_FRAGMENT_TAG);
        if (photoDescriptionDialog != null) {
            photoDescriptionDialog.setOnBodyInflatedListener(this);
            photoDescriptionDialog.setOnNegativeButtonListener(this);
            photoDescriptionDialog.setOnPositiveButtonListener(this);
        } else if (!TextUtils.isEmpty(mPhotoPath)) {
            getLoaderManager().initLoader(UPLOAD_PHOTO_LOADER_ID, null, this);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Logger.err("Save state mPhotoPath: " + mPhotoPath);
        outState.putString(KEY_PHOTO_PATH, mPhotoPath);
        outState.putString(KEY_PHOTO_DESC, mPhotoDesc);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Logger.err("result data: " + requestCode);
        if (resultCode == Activity.RESULT_OK) {
            final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
            switch (requestCode) {
                case RQ_GALLERY_PHOTO: {
                    final Uri uri = data.getData();
                    if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
                        // ExternalStorageProvider
                        if (isExternalStorageDocument(uri)) {
                            final String docId = DocumentsContract.getDocumentId(uri);
                            final String[] split = docId.split(":");
                            final String type = split[0];

                            if ("primary".equalsIgnoreCase(type)) {
                                mPhotoPath = Environment.getExternalStorageDirectory() + "/" + split[1];
                            }

                            // TODO handle non-primary volumes
                        }
                        // DownloadsProvider
                        else if (isDownloadsDocument(uri)) {

                            final String id = DocumentsContract.getDocumentId(uri);
                            final Uri contentUri = ContentUris.withAppendedId(
                                    Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                            mPhotoPath = getDataColumn(contentUri, null, null);
                        }
                        // MediaProvider
                        else if (isMediaDocument(uri)) {
                            final String docId = DocumentsContract.getDocumentId(uri);
                            final String[] split = docId.split(":");
                            final String type = split[0];

                            Uri contentUri = null;
                            if ("image".equals(type)) {
                                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                            } else if ("video".equals(type)) {
                                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                            } else if ("audio".equals(type)) {
                                contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                            }

                            final String selection = "_id=?";
                            final String[] selectionArgs = new String[]{
                                    split[1]
                            };

                            mPhotoPath = getDataColumn(contentUri, selection, selectionArgs);
                        }
                    } else if ("content".equalsIgnoreCase(uri.getScheme())) {

                        // Return the remote address
                        if (isGooglePhotosUri(uri)) {
                            mPhotoPath = uri.getLastPathSegment();
                        }

                        mPhotoPath = getDataColumn(uri, null, null);
                    } else if ("file".equalsIgnoreCase(uri.getScheme())) {
                        mPhotoPath = uri.getPath();
                    }
                }
                case RQ_CAPTURE_PHOTO: {
                    Logger.err("mPhotoPath: " + mPhotoPath);
                    showDescriptionDialog();
                    break;
                }
                default:
                    break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        final View view = v;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (view.getId()) {
                    case R.id.capture_photo: {
                        showTakePhotoDialog();
                        break;
                    }
                    case R.id.camera: {
//                        mPhotoDialog.dismiss();
                        takePhoto();
                        break;
                    }
                    case R.id.gallery: {
//                        mPhotoDialog.dismiss();
                        pickPhoto();
                        break;
                    }
                }
            }
        });

    }

    @Override
    public void onBodyInflated(String fragmentTag, View bodyView) {
        if (CAPTURE_PHOTO_FRAGMENT_TAG.equals(fragmentTag)) {
            bodyView.findViewById(R.id.camera).setOnClickListener(BasePhotoFragment.this);
            bodyView.findViewById(R.id.gallery).setOnClickListener(BasePhotoFragment.this);
        } else if (DESCRIPTION_PHOTO_FRAGMENT_TAG.equals(fragmentTag)) {
            mDescription = (EditText) bodyView.findViewById(R.id.description);

            addPhotoToPDFCheckBox = (CheckBox) bodyView.findViewById(R.id.addPhotoToPdf);
            addPhotoToPDFCheckBox.setEnabled(UserUtilitiesSingleton.getInstance().user.isAllowAddToInvoicePDF());
        }
    }

    @Override
    public void onClick(View v, String fragmentTag) {
        if (DESCRIPTION_PHOTO_FRAGMENT_TAG.equals(fragmentTag)) {
            switch (v.getId()) {
                case R.id.button_positive: {
                    mPhotoDesc = mDescription != null ? mDescription.getText().toString() : "";
                    getLoaderManager().initLoader(UPLOAD_PHOTO_LOADER_ID, null, this);
                }
            }
        }
    }

    private String[] getArgumentsForUploading() {
        final String[] args = getAdditionalArgs();
        if (args.length > 2) {
            throw new IllegalArgumentException(
                    "Params can't be more then 2, because photo can be added from appointment or equipment screen!");
        }
        return args;
    }

    @Override
    public void onDestroyView() {
        Logger.err("View was destroyed!");
        mDescription = null;
        mPhotoDialog = null;
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        Logger.err("Fragment was detached!");

        mPhotoPath = null;
        mPhotoDesc = null;
        super.onDetach();
    }


    /**
     * Get the value of the data column for this Uri. This is useful for MediaStore Uris, and other file-based
     * ContentProviders.
     *
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */

    private String getDataColumn(final Uri uri, final String selection, final String[] selectionArgs) {
        Cursor cursor = null;
        final String[] projection = {MediaStore.Images.Media.DATA};
        try {
            cursor = SkedsApplication.getContext().getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                DatabaseUtils.dumpCursor(cursor);
                final int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                return cursor.getString(columnIndex);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    private boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    private void takePhoto() {
        final Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (SystemUtils.hasActivityToHandleIntent(takePictureIntent)) {

            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Logger.err("Could not create file.");
                Toast.makeText(SkedsApplication.getContext(), getString(R.string.cant_save_file),
                        Toast.LENGTH_SHORT).show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, RQ_CAPTURE_PHOTO);
            }
        }
    }

    public void pickPhoto() {
        final Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
        photoPickerIntent.setType(MIME_TYPE_IMAGE);
        if (SystemUtils.hasActivityToHandleIntent(photoPickerIntent)) {
            startActivityForResult(photoPickerIntent, RQ_GALLERY_PHOTO);
        }
    }

    private void showTakePhotoDialog() {
        final AlertDialogFragment.DialogBuilder builder = new AlertDialogFragment.DialogBuilder(context);
        mPhotoDialog = builder.setTitle(getString(R.string.dialog_header_capture_photo)).setBodyLayoutId(
                R.layout.d_get_photo).setOnBodyInflatedListener(this).build();
        mPhotoDialog.show(getFragmentManager(), CAPTURE_PHOTO_FRAGMENT_TAG);
    }

    private void showDescriptionDialog() {
        final AlertDialogFragment.DialogBuilder builder = new AlertDialogFragment.DialogBuilder(context);
        final AlertDialogFragment photoDescriptionDialog = builder.setTitle(
                getString(R.string.dialog_header_capture_photo_description)).setBodyLayoutId(
                R.layout.d_photo_description).setOnBodyInflatedListener(this).setNegativeButton(
                getString(R.string.button_string_cancel), this)
                .setPositiveButton(getString(R.string.button_string_save), this).build();

        photoDescriptionDialog.show(getFragmentManager(), DESCRIPTION_PHOTO_FRAGMENT_TAG);
    }

    private File createImageFile() throws IOException {
        final File storageDir = new File(SystemUtils.getFilesDirToStoreImage(context), TEMP_PHOTO_FILE_DIR);
        // Create an image file name
        final String imageFileName = "JPEG_" + DateFormat.format("yyyyMMdd_HHmmss", new Date()) + "_";
        // Make sure the Pictures directory exists.
        final boolean isCreated = storageDir.mkdirs();

        final File image = File.createTempFile(
                imageFileName,  /* prefix */
                null,         /* suffix */
                storageDir      /* directory */
        );
        mPhotoPath = image.getAbsolutePath();
        return image;
    }

    /**
     * Can be overridden by children in order to set array of appointment id and equipment id. Right now user can add
     * photos only from appt or equipment screen.
     *
     * @return array of ids with length no more then 2 otherwise will be thrown an exception inside {@link
     * com.skeds.android.phone.business.AsyncTasks.SubmitPhotoTask}
     */
    protected String[] getAdditionalArgs() {
        return new String[2];
    }

    @Override
    public Loader<Boolean> onCreateLoader(int id, Bundle bundle) {
        final String[] args = getArgumentsForUploading();
        return new UploadPhotoLoader(context, addPhotoToPDFCheckBox.isChecked(), args[0], args[1], mPhotoPath, mPhotoDesc);
    }

    @Override
    public void onLoadFinished(Loader<Boolean> loader, final Boolean data) {

        final BaseAsyncTaskLoader<Boolean> l = (BaseAsyncTaskLoader<Boolean>) loader;

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (l.getLoadException() != null || !data) {
                    Toast.makeText(context, getString(R.string.cant_save_file), Toast.LENGTH_SHORT).show();

                } else
                    Toast.makeText(context, "Photo uploaded", Toast.LENGTH_SHORT).show();

//                if (mPhotoDialog != null)
//                    mPhotoDialog.dismiss();
            }
        });


        Logger.err("Upload finished successfully!");

        mPhotoPath = null;
        mDescription = null;
        mPhotoDesc = null;

        getLoaderManager().destroyLoader(loader.getId());


    }

    @Override
    public void onLoaderReset(Loader<Boolean> loader) {
        //NO-OP
    }

    private static class UploadPhotoLoader extends BaseAsyncTaskLoader<Boolean> {

        private static final String TAG = UploadPhotoLoader.class.getName();

        private static final String KEY_NODE_EQUIPMENT_ID = "equipmentId";
        private static final String KEY_NODE_APPT_ID = "appointmentId";
        private static final String KEY_NODE_DESCRIPTION = "tagText";
        private static final String KEY_ROOT_PHOTO = "addPhoto";

        private static final int WIDTH_HEIGHT_BITMAP = 700;
        private static final int BUFFER_SIZE = 32 * 1024; // 32 Kb
        private boolean addPhotoToPDF;
        private final String[] mArgs;

        public UploadPhotoLoader(Context context, boolean addPhotoToPDF, final String... args) {
            super(context);
            this.addPhotoToPDF = addPhotoToPDF;
            mArgs = args;
        }

        private InputStream resizeBitmap(final String bitmapPath) throws IOException {
            Logger.err(TAG, "Bitmap path: " + bitmapPath);
            InputStream imageStream = getImageStream(bitmapPath);
            if (imageStream == null) return null;
            try {
                // Get the dimensions of the bitmap
                final Bitmap subSampledBitmap;
                try {
                    subSampledBitmap = BitmapFactory.decodeStream(imageStream);
                } catch (OutOfMemoryError ex) {
                    Logger.err("Out of memory");
                    return null;
                }
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
            try {
                return new ContentLengthInputStream(new BufferedInputStream(new FileInputStream(filePath), BUFFER_SIZE),
                        new File(filePath).length());
            } catch (Exception ex) {
                return null;
            }
        }

        @Override
        public Boolean loadInBackground() {
            for (final String arg : mArgs) {
                Logger.err(TAG, "Args: " + arg);
            }
            try {
                RestConnector.getInstance().httpPostCheckSuccess(prepareDocForServer(),
                        SkedsApplication.getContext().getString(
                                R.string.upload_photo_url,
                                AppDataSingleton.getInstance().getCustomer().getId()),
                        resizeBitmap(mArgs[2])
                );
            } catch (NonfatalException e) {
                Logger.err(e.getMessage());
                mLoadException = e;
                return false;
            } catch (IOException e) {
                Logger.err(e.getMessage());
                mLoadException = e;
                return false;
            }
            return true;
        }

        private Document prepareDocForServer() {
            final Element root = new Element(KEY_ROOT_PHOTO);

            if (!TextUtils.isEmpty(mArgs[0])) {
                final Element appointmentIdNode = new Element(KEY_NODE_APPT_ID);
                appointmentIdNode.setText(mArgs[0]);
                root.addContent(appointmentIdNode);
            }

            if (!TextUtils.isEmpty(mArgs[1])) {
                final Element equipmentIdNode = new Element(KEY_NODE_EQUIPMENT_ID);
                equipmentIdNode.setText(String.valueOf(mArgs[1]));
                root.addContent(equipmentIdNode);
            }

            if (!TextUtils.isEmpty(mArgs[3])) {
                final Element tagText = new Element(KEY_NODE_DESCRIPTION);
                tagText.setText(mArgs[3]);
                root.addContent(tagText);
            }

            Element addToInvoicePDF = new Element("addToInvoicePDF");
            addToInvoicePDF.addContent(String.valueOf(addPhotoToPDF));
            root.addContent(addToInvoicePDF);

            return new Document(root);
        }
    }
}