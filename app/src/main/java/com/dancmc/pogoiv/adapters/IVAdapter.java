package com.dancmc.pogoiv.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.dancmc.pogoiv.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by Daniel on 6/08/2016.
 */
public class IVAdapter extends ArrayAdapter<double[]> {
    DecimalFormat df = new DecimalFormat("0.0");
    ArrayList<double[]> ivCombos;
    Context context;
    private DecimalFormat mDF;

    public IVAdapter(Context context, ArrayList<double[]> ivCombos) {
        super(context, R.layout.adapter_compare_summary_iv_listview, ivCombos);
        this.ivCombos = ivCombos;
        this.context = context;
        mDF = new DecimalFormat("0.0");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v= convertView;

        if(v==null) {
            //cannot pass parent (ListView as second parameter without false as the third, otherwise will throw exception as ListView cannot take addview
            v = inflater.inflate(R.layout.adapter_compare_summary_iv_listview, parent, false);
        }

        TextView column1 = (TextView)v.findViewById(R.id.table_display_iv_column1);
        TextView column2 = (TextView)v.findViewById(R.id.table_display_iv_column2);
        TextView column3 = (TextView)v.findViewById(R.id.table_display_iv_column3);
        TextView column4 = (TextView)v.findViewById(R.id.table_display_iv_column4);
        TextView column5 = (TextView)v.findViewById(R.id.table_display_iv_column5);

        double[] ivCombo = getItem(position);

        if (ivCombos.get(position)[0] < 1) {
            column1.setText("-");
        } else {
            column1.setText(mDF.format((ivCombo[0]+1)/2.0));
        }
        column2.setText(String.valueOf((int)ivCombo[1]));
        column3.setText(String.valueOf((int) ivCombo[2]));
        column4.setText(String.valueOf((int) ivCombo[3]));
        column5.setText(df.format(ivCombo[4]) + "%");


        return v;
    }
}