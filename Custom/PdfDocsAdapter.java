package com.skeds.android.phone.business.Custom;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.PdfDocument;
import com.skeds.android.phone.business.activities.ActivityPdfDocumentsFragment;

import java.util.List;

public class PdfDocsAdapter extends ArrayAdapter<PdfDocument> {

    private Context context;

    private List<PdfDocument> docList;

    private DeleteOnItemListener deleteItemListener;

    private OnClickListener listener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (deleteItemListener == null)
                return;
            View parent = (View) v.getParent().getParent();
            ViewHolder holder = (ViewHolder) parent.getTag();
            if ((holder!=null)&&(holder.id != 0)) {
                for (PdfDocument item : docList) {
                    if (item.getId() == holder.id) {
                        deleteItemListener.onDelete(docList.indexOf(item));
                    }
                }
            }
        }
    };

    public PdfDocsAdapter(Context context) {
        super(context, R.layout.row_pdf_item, AppDataSingleton.getInstance()
                .getPdfDocsList());
        this.context = context;
        docList = AppDataSingleton.getInstance().getPdfDocsList();

        if (context instanceof ActivityPdfDocumentsFragment) {
            FragmentManager fm = ((FragmentActivity) context)
                    .getSupportFragmentManager();
            Fragment f = fm.findFragmentById(R.id.pdf_documents_fragment);
            deleteItemListener = (DeleteOnItemListener) f;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        ViewHolder holder;

        if (v == null) {
            LayoutInflater vi = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.row_pdf_item, null);

            // cache view fields into the holder
            holder = new ViewHolder();
            holder.name = (TextView) v.findViewById(R.id.pdf_item_name);
            holder.reporter = (TextView) v.findViewById(R.id.pdf_reporter_name);
            holder.buttonAddDelete = (ImageView) v
                    .findViewById(R.id.pdf_button_add);
            // associate the holder with the view for later lookup
            v.setTag(holder);
        } else {
            // view already exists, get the holder instance from the view
            holder = (ViewHolder) v.getTag();
        }

        if (position == 0) {
            holder.id = 0;
            holder.name.setText("Add New Form");
            holder.name.setTextColor(context.getResources().getColor(
                    android.R.color.darker_gray));
            holder.buttonAddDelete.setClickable(false);
            holder.buttonAddDelete
                    .setImageResource(R.drawable.icon_add_on_truck);
            holder.reporter.setVisibility(View.GONE);
        } else {
            holder.id = docList.get(position).getId();
            holder.name.setText(docList.get(position).getName());
            holder.name.setTextColor(context.getResources().getColor(
                    android.R.color.black));
            holder.buttonAddDelete.setOnClickListener(listener);
            holder.buttonAddDelete
                    .setImageResource(R.drawable.icon_delete_on_truck);
            holder.reporter.setVisibility(View.VISIBLE);
        }

        holder.reporter.setText("Reporter: "
                + docList.get(position).getReporterName());

        return v;
    }

    public static class ViewHolder {
        public int id;
        TextView name;
        TextView reporter;
        ImageView buttonAddDelete;
    }



}