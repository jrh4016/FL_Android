package com.skeds.android.phone.business.Custom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.PdfDocument;

import java.util.List;

public class PdfTemplatesAdapter extends ArrayAdapter<PdfDocument> {

    private Context context;

    private List<PdfDocument> templateList;

    public PdfTemplatesAdapter(Context context) {
        super(context, R.layout.row_pdf_item, AppDataSingleton.getInstance()
                .getPdfTemplatesList());
        this.context = context;
        templateList = AppDataSingleton.getInstance().getPdfTemplatesList();

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        ViewHolder holder;

        if (v == null) {
            LayoutInflater vi = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.row_pdf_template, null);

            // cache view fields into the holder
            holder = new ViewHolder();
            holder.name = (TextView) v.findViewById(R.id.pdf_item_name);
            holder.description = (TextView) v
                    .findViewById(R.id.pdf_item_description);
            // associate the holder with the view for later lookup
            v.setTag(holder);
        } else {
            // view already exists, get the holder instance from the view
            holder = (ViewHolder) v.getTag();
        }

        holder.id = templateList.get(position).getId();
        holder.name.setText(templateList.get(position).getName());
        holder.description.setText(templateList.get(position).getDescription());

        return v;
    }

    public static class ViewHolder {
        public int id;
        TextView name;
        TextView description;
    }

}