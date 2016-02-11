package com.skeds.android.phone.business.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.skeds.android.phone.business.AsyncTasks.BaseUiReportTask;
import com.skeds.android.phone.business.Custom.PdfTemplatesAdapter;
import com.skeds.android.phone.business.Custom.PdfTemplatesAdapter.ViewHolder;
import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.PdfDocument;
import com.skeds.android.phone.business.Utilities.General.Constants;
import com.skeds.android.phone.business.Utilities.REST.Objects.RESTPdfFiles;
import com.skeds.android.phone.business.activities.ActivityPdfViewCtrl;

public class PdfTemplatesFragment extends BaseSkedsFragment {

    private ListView itemsList;

    private Activity activity;

    private String typeMode;

    private OnItemClickListener listener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            ViewHolder holder = (ViewHolder) view.getTag();
            int docId = holder.id;
            PdfDocument selectedDoc = null;

            for (PdfDocument doc : AppDataSingleton.getInstance()
                    .getPdfTemplatesList()) {
                if (doc.getId() == docId) {
                    selectedDoc = doc;
                    break;
                }
            }
            if (selectedDoc != null)
                if (!selectedDoc.getUrl().endsWith(".pdf")) {
                    Toast.makeText(activity, "Not a valid format", Toast.LENGTH_SHORT).show();
                    return;
                }

            Intent i = new Intent(activity, ActivityPdfViewCtrl.class);
            i.putExtras(activity.getIntent().getExtras());
            i.putExtra("docId", docId);
            i.putExtra("docType", "pdf_template");
            startActivityForResult(i, 0);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater
                .inflate(R.layout.layout_pdf_documents, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = getActivity();

        itemsList = (ListView) activity.findViewById(R.id.pdf_docs_list);
        itemsList.setOnItemClickListener(listener);

        typeMode = activity.getIntent().getStringExtra("templateTypeMode");
        if (typeMode == null)
            typeMode = Constants.PDF_VIEW_ALL_MODE;

    }

    @Override
    public void onResume() {
        super.onResume();
        new GetPdfTemplatesTask().execute();
    }

    private class GetPdfTemplatesTask extends BaseUiReportTask<String> {

        public GetPdfTemplatesTask() {
            super(activity, "Loading PDF Templates...");
        }

        @Override
        protected boolean taskBody(String... params) throws Exception {
            RESTPdfFiles.queryTemplates(typeMode);
            return true;
        }

        @Override
        protected void onSuccess() {
            super.onSuccess();
            itemsList.setAdapter(new PdfTemplatesAdapter(activity));
        }
    }

}
