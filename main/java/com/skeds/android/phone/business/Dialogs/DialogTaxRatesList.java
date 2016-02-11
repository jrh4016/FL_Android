package com.skeds.android.phone.business.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.TaxRate;
import com.skeds.android.phone.business.Utilities.General.UserUtilitiesSingleton;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class DialogTaxRatesList extends Dialog {

    public static final int CUSTOM_MODE = 1;
    public static final int MODIFY_MODE = 2;

    private Context context;
    private ListView list;

    private int dialogLaunchMode = 0;

    private String taxRateType;
    private TaxRate selectedRate;

    public static long selectedRateId = 0;

    public DialogTaxRatesList(Context context, TaxRate rate, int mode) {
        super(context);
        this.context = context;
        selectedRate = rate;
        dialogLaunchMode = mode;
        this.taxRateType = rate.getType();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_tax_rates_list);

        ArrayList<TaxRate> taxes = new ArrayList<TaxRate>();

        list = (ListView) findViewById(R.id.taxRatesList);
        for (TaxRate tr : UserUtilitiesSingleton.getInstance().user.getCountryInfo().getTaxRates()) {
            if (tr.getType().equals(taxRateType))
                taxes.add(tr);
        }

        list.setAdapter(new Adapter(context, taxes));
        list.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ViewHolder holder = (ViewHolder) view.getTag();
                long rateId = holder.rateId;

                switch (dialogLaunchMode) {
                    case CUSTOM_MODE:
                        selectRateIds(DialogLineItemCustom.customLineItem.rateIds, rateId);
                        break;
                    case MODIFY_MODE:
                        selectRateIds(DialogLineItemModify.lineItem.rateIds, rateId);
                        break;
                    default:
                        break;
                }

                selectedRateId = rateId;

                dismiss();
            }
        });
    }

    /**
     * Select necessary rate from UI to the model
     *
     * @param rateIds
     * @param rateId
     */
    private void selectRateIds(List<Long> rateIds, Long rateId) {
        ListIterator<Long> ratesIterator = rateIds.listIterator();
        while (ratesIterator.hasNext()) {
            Long currentRateId = ratesIterator.next();
            if (currentRateId == selectedRate.getId()) {
                ratesIterator.set(rateId);
                break;
            }
        }
    }

    private class Adapter extends ArrayAdapter<TaxRate> {

        private ArrayList<TaxRate> list;

        public Adapter(Context context, ArrayList<TaxRate> list) {
            super(context, R.layout.row_tax_rate, R.id.taxRateText, list);
            this.list = list;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            ViewHolder holder;

            if (v == null) {
                LayoutInflater vi = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.row_tax_rate, null);

                // cache view fields into the holder
                holder = new ViewHolder();
                holder.name = (TextView) v.findViewById(R.id.taxRateText);
                holder.rateId = list.get(position).getId();
                // associate the holder with the view for later lookup
                v.setTag(holder);
            } else {
                // view already exists, get the holder instance from the view
                holder = (ViewHolder) v.getTag();
            }

            BigDecimal value = list.get(position).getValue().multiply(new BigDecimal("100.00").setScale(2, RoundingMode.HALF_UP));
            DecimalFormat df = new DecimalFormat();

            df.setMaximumFractionDigits(4);

            df.setMinimumFractionDigits(0);

            df.setGroupingUsed(false);

            String result = df.format(value);

            holder.name.setText(list.get(position).getName() + " "
                    + result + "%");
            return v;
        }

    }

    private static class ViewHolder {
        long rateId;
        TextView name;
    }

}