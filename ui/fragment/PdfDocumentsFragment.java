package com.skeds.android.phone.business.ui.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.skeds.android.phone.business.AsyncTasks.BaseUiReportTask;
import com.skeds.android.phone.business.Custom.DeleteOnItemListener;
import com.skeds.android.phone.business.Custom.PdfDocsAdapter;
import com.skeds.android.phone.business.Custom.PdfDocsAdapter.ViewHolder;
import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.PdfDocument;
import com.skeds.android.phone.business.Utilities.General.Constants;
import com.skeds.android.phone.business.Utilities.REST.Objects.RESTPdfFiles;
import com.skeds.android.phone.business.activities.ActivityPdfTemplatesFragment;
import com.skeds.android.phone.business.activities.ActivityPdfViewCtrl;

public class PdfDocumentsFragment extends BaseSkedsFragment implements DeleteOnItemListener {

    private ListView itemsList;

    private Activity activity;

    private int apptId;
    private int equipmentId;

    private String templatesTypeMode;

    private OnItemClickListener listener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ViewHolder holder = (ViewHolder) view.getTag();
            int docId = holder.id;

            if (position == 0) {
                Intent i = new Intent(activity, ActivityPdfTemplatesFragment.class);
                i.putExtras(activity.getIntent().getExtras());
                i.putExtra("templateTypeMode", templatesTypeMode);
                startActivity(i);
            } else {
                PdfDocument selectedDoc = null;

                for (PdfDocument doc : AppDataSingleton.getInstance()
                        .getPdfDocsList()) {
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
                i.putExtra("docType", "pdf_doc");
                startActivityForResult(i, 0);
            }
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_pdf_documents, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = getActivity();

        templatesTypeMode = activity.getIntent().getStringExtra("templateTypeMode");
        if (templatesTypeMode == null)
            templatesTypeMode = Constants.PDF_VIEW_ALL_MODE;
        apptId = getActivity().getIntent().getIntExtra("apptId", 0);
        equipmentId = getActivity().getIntent().getIntExtra("equipmentId", 0);


        itemsList = (ListView) activity.findViewById(R.id.pdf_docs_list);
        itemsList.setOnItemClickListener(listener);


        itemsList.setAdapter(new PdfDocsAdapter(activity));
    }

    @Override
    public void onResume() {
        super.onResume();

        new GetPdfDocsTask().execute();
    }

    private void showDeleteDialog(final int position) {
        final Dialog deleteDialog = new Dialog(activity);
        deleteDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);


        deleteDialog.setContentView(R.layout.dialog_layout_yes_no_response);

        final TextView title = (TextView) deleteDialog.findViewById(R.id.dialog_yes_no_response_textview_title);
        final TextView textBody = (TextView) deleteDialog.findViewById(R.id.dialog_yes_no_response_textview_body);
        final TextView yesBtn = (TextView) deleteDialog.findViewById(R.id.dialog_yes_no_response_button_yes);
        final TextView noBtn = (TextView) deleteDialog.findViewById(R.id.dialog_yes_no_response_button_no);

        title.setText("PDF Document");
        textBody.setText("Delete  " + AppDataSingleton.getInstance().getPdfDocsList().get(position).getName() + "  ?");

        yesBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                new DeleteDocTask(position).execute();
                deleteDialog.dismiss();
//				itemsList.invalidateViews();
            }
        });

        noBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                deleteDialog.dismiss();
            }
        });
        deleteDialog.show();
    }


    private class GetPdfDocsTask extends BaseUiReportTask<String> {

        public GetPdfDocsTask() {
            super(activity, "Loading PDF docs...");
        }

        @Override
        protected boolean taskBody(String... params) throws Exception {
            RESTPdfFiles.queryDocuments(apptId, equipmentId);
            return true;
        }

        @Override
        protected void onSuccess() {
            super.onSuccess();
            itemsList.setAdapter(new PdfDocsAdapter(activity));
        }
    }

    private class DeleteDocTask extends BaseUiReportTask<String> {

        private int position;

        public DeleteDocTask(int position) {
            super(activity, "Deleting PDF doc...");
            this.position = position;
        }

        @Override
        protected boolean taskBody(String... params) throws Exception {
            RESTPdfFiles.deleteDocument(AppDataSingleton.getInstance().getPdfDocsList().get(position).getId());
            return true;
        }

        @Override
        protected void onSuccess() {
            super.onSuccess();
            AppDataSingleton.getInstance().getPdfDocsList().remove(position);
            itemsList.invalidateViews();
        }
    }

    @Override
    public void onDelete(int position) {
        showDeleteDialog(position);
    }
}
