package com.skeds.android.phone.business.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.skeds.android.phone.business.AsyncTasks.BaseUiReportTask;
import com.skeds.android.phone.business.Custom.AccountMenu;
import com.skeds.android.phone.business.Custom.QuickAction;
import com.skeds.android.phone.business.Dialogs.DialogProgressPopup;
import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.PdfDocument;
import com.skeds.android.phone.business.Utilities.General.CommonUtilities;
import com.skeds.android.phone.business.Utilities.REST.Objects.RESTPdfFiles;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import pdftron.Common.PDFNetException;
import pdftron.PDF.PDFDoc;
import pdftron.PDF.PDFNet;
import pdftron.PDF.PDFViewCtrl;
import pdftron.PDF.Tools.ToolManager;
import pdftron.SDF.SDFDoc;

public class ActivityPdfViewCtrl extends Activity implements
        PDFViewCtrl.RenderingListener {

    private static final String file_path = "/mnt/sdcard/Download/field_locate.pdf";

    private PDFViewCtrl mPDFView;
    private RenderSpinner mSpinner = null;

    private LinearLayout headerLayout;
    private ImageView headerButtonUser;
    private ImageView headerButtonBack;

    private DialogProgressPopup progressDialog;

    private EditText description;
    private TextView saveBtn;

    private int apptId;
    private int equipId;

    private PdfDocument selectedDoc;

    private String docType;

    private String encodedPdfFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        setResult(Activity.RESULT_CANCELED);
        if (!CommonUtilities.isNetworkAvailable(this)) {
            Toast.makeText(this, "Network connection unavailable.",
                    Toast.LENGTH_SHORT).show();
            finish();
        }

		/*
         * initialize PDFNet   FieldAware US, Inc.(fieldlocate.com):OEM:Skeds::IA:AMS(20140225):F00AA7DAE7267E23C8DEB62F4E6F2FD9707A9ADD8FD4D61C64CBBE52AAB6F5C7
		 */
        try {
            PDFNet.initialize(
                    this, R.raw.pdfnet);

        } catch (Exception e) {
            return;
        }

        setContentView(R.layout.layout_pdf_view_ctrl);
        init();
        new DownloadPdfTask().execute();

    }

    private void init() {

        initHeader();

        description = (EditText) findViewById(R.id.pdf_description_field);
        saveBtn = (TextView) findViewById(R.id.pdf_save_btn);
        saveBtn.requestFocus();
        saveBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                PDFDoc doc = mPDFView.getDoc();
                if (doc != null) {
                    try {
                        doc.lock(); // note: document needs to be locked first
                        // before it can be saved.
                        if (doc.isModified()) {
                            File file = new File(file_path);
                            boolean exist = file.exists();
                            if (!exist || file.canWrite()) {
                                // use file name to output file
                                String s = doc.getFileName();
                                doc.save(doc.getFileName(),
                                        SDFDoc.e_compatibility, null);

                                // use custom filter to output file
                                // File f = new
                                // File("/mnt/sdcard/Download/custom_filter_test.pdf");
                                // RandomAccessFile raf = new
                                // RandomAccessFile(f, "rw");
                                // UserCustomFilter custom_filter = new
                                // UserCustomFilter(CustomFilter.WRITE_MODE,
                                // raf);
                                // doc.save(custom_filter, 0);

                            } else {
                            }
                        }
                    } catch (Exception e) {
                        Log.v("PDFNet", e.toString());
                    } finally {
                        try {
                            // doc.unlock(); //note: unlock the document is
                            // necessary.
                        } catch (Exception e) {
                        }
                    }
                }

                new UploadPdfTask().execute();
            }
        });

        retrieveExtras();
//		showProgressDialog();
    }

    private void initPdfViewer() {
        mPDFView = (PDFViewCtrl) findViewById(R.id.pdfviewctrl);
        ToolManager tm = new ToolManager(mPDFView);
        mPDFView.setToolManager(tm);

//		mPDFView.setDocumentDownloadListener(new DocumentDownloadListener() {
//
//			@Override
//			public void onDownloadEvent(int type, int page_num,
//					int page_downloaded, int page_count,
//					java.lang.String message) {
//				switch (type) {
//				case PDFViewCtrl.DOWNLOAD_PAGE:
//				case PDFViewCtrl.DOWNLOAD_FINISH:
//					if (progressDialog != null)
//						if (progressDialog.isShowing())
//							progressDialog.dismiss();
//					break;
//				case PDFViewCtrl.DOWNLOAD_FAIL:
//					Toast.makeText(ActivityPdfViewCtrl.this, message,
//							Toast.LENGTH_SHORT).show();
//					break;
//				default:
//					break;
//				}
//			}
//		});
        /*
         * misc PDFViewCtrl settings
		 */
        mPDFView.setPagePresentationMode(PDFViewCtrl.PAGE_PRESENTATION_SINGLE); // set
        // to
        // single
        // page
        // mode;
        // default
        // is
        // single
        // continuous
        // .
        mPDFView.setHighlightFields(true); // turn on form fields highlighting.
        Drawable draw = getResources().getDrawable(
                R.drawable.background_repeating_stripes);
        mPDFView.setBackgroundDrawable(draw);

        mPDFView.setCaching(true);

        mPDFView.setZoomLimits(PDFViewCtrl.ZOOM_LIMIT_RELATIVE, 1.0, 4);

        mPDFView.setRenderedContentCacheSize((long) (Runtime.getRuntime()
                .maxMemory() / (1024 * 1024) * 0.5));


        try {
            mPDFView.setDoc(new PDFDoc(file_path));

            if ("pdf_doc".equals(docType))
                mPDFView.getDoc().flattenAnnotations();
        } catch (PDFNetException e) {
            e.printStackTrace();
            finish();
        } catch (RuntimeException e) {
            finish();
        }

    }

//	private void showProgressDialog() {
//		if (progressDialog == null) {
//			progressDialog = new DialogProgressPopup(ActivityPdfViewCtrl.this,
//					null);
//		}
//		if (!progressDialog.isShowing())
//			progressDialog.show();
//	}

    private void initHeader() {
        final QuickAction accountMenu = AccountMenu.setupMenu(this, this);

        headerLayout = (LinearLayout) findViewById(R.id.activity_header);
        if (headerLayout != null) {
            headerButtonUser = (ImageView) headerLayout
                    .findViewById(R.id.header_button_user);
            headerButtonBack = (ImageView) headerLayout
                    .findViewById(R.id.header_button_back);

            headerButtonUser.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    accountMenu.show(v);
                    accountMenu.setAnimStyle(QuickAction.ANIM_GROW_FROM_CENTER);
                }
            });

            headerButtonBack.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

    }

    private void retrieveExtras() {
        final Intent intent = getIntent();
        final int docId = intent.getIntExtra("docId", 0);

        docType = intent.getStringExtra("docType");
        apptId = intent.getIntExtra("apptId", 0);
        equipId = intent.getIntExtra("equipId", 0);

        if ("pdf_doc".equals(docType)) {
            for (final PdfDocument doc : AppDataSingleton.getInstance()
                    .getPdfDocsList()) {
                if (doc.getId() == docId) {
                    selectedDoc = doc;
                    description.setText(selectedDoc.getName());
                    return;
                }
            }
        } else {
            for (final PdfDocument doc : AppDataSingleton.getInstance()
                    .getPdfTemplatesList()) {
                if (doc.getId() == docId) {
                    selectedDoc = doc;
                    description.setText(selectedDoc.getName());
                    return;
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mPDFView != null) {
            mPDFView.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPDFView != null) {
            mPDFView.resume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPDFView != null) {
            mPDFView.destroy();
            mPDFView = null;
        }
    }

    public void onLowMemory() {
        super.onLowMemory();
        if (mPDFView != null) {
            mPDFView.purgeMemory();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            String str = data
                    .getStringExtra("com.pdftron.pdfnet.demo.pdfviewctrl.FileOpenData");
            if (!TextUtils.isEmpty(str)) {
                try {
                    PDFDoc doc = new PDFDoc(str);
                    mPDFView.setDoc(doc);
                } catch (Exception ignored) {
                } finally {
                    mPDFView.closeDoc();
                }
            }
        }
    }

    // rendering spinner
    private class RenderSpinner extends PopupWindow {
        PDFViewCtrl mCtrl;
        ProgressBar mProg;

        public RenderSpinner(PDFViewCtrl ctrl) {
            super(ctrl.getContext());
            mCtrl = ctrl;
            mProg = new ProgressBar(mCtrl.getContext(), null,
                    android.R.attr.progressBarStyle);
            setBackgroundDrawable(new BitmapDrawable()); // this is needed for
            // setTouchInterceptor
            // to work. Strange!
            setWidth(WindowManager.LayoutParams.FILL_PARENT);
            setHeight(WindowManager.LayoutParams.FILL_PARENT);

            setFocusable(false);
            setTouchable(false);
            setOutsideTouchable(false);
            setAnimationStyle(-1);
            setContentView(mProg);
        }

        public void show() {
            int[] sc = new int[2];
            mCtrl.getLocationOnScreen(sc);
            setWidth(100);
            setHeight(100);
            showAtLocation(mCtrl, Gravity.CENTER, sc[0], sc[1]);
        }
    }

    @Override
    public void onRenderingStarted() {
        if (mSpinner != null && !mSpinner.isShowing()) {
            mSpinner.show();
        }
    }

    // callback if the client region of PDFViewCtrl has finished rendering
    @Override
    public void onRenderingFinished() {
        if (mSpinner != null && mSpinner.isShowing()) {
            mSpinner.dismiss();
        }
    }


    private class DownloadPdfTask extends BaseUiReportTask<String> {


        public DownloadPdfTask() {
            super(ActivityPdfViewCtrl.this, "Downloading...");
        }

        @Override
        protected boolean taskBody(String... params) throws Exception {

            URL url;
            try {
                url = new URL(selectedDoc.getUrl());
                Object content = url.getContent();
                InputStream is = (InputStream) content;

                OutputStream os =
                        new FileOutputStream(new File(file_path));
                int read = 0;
                byte[] bytes = new byte[1024];

                while ((read = is.read(bytes)) != -1) {
                    os.write(bytes, 0, read);
                }

                is.close();
                os.close();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onSuccess() {
            super.onSuccess();

            initPdfViewer();
        }
    }


    private class UploadPdfTask extends BaseUiReportTask<String> {

        public UploadPdfTask() {
            super(ActivityPdfViewCtrl.this, "Saving...");
        }

        @Override
        protected boolean taskBody(String... params) throws Exception {

            if ("pdf_doc".equals(docType))
                RESTPdfFiles.updateDocument(description.getText().toString(),
                        selectedDoc.getId(), file_path);
            else
                RESTPdfFiles.addDocument(description.getText().toString(),
                        apptId, equipId, file_path);

            return true;
        }

        @Override
        protected void onSuccess() {
            super.onSuccess();
            setResult(Activity.RESULT_OK);
            finish();
        }
    }

}
