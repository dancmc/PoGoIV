package com.dancmc.pogoiv;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by Daniel on 30/07/2016.
 */
public class SummaryFragRecyclerViewAdapter extends RecyclerView.Adapter<SummaryFragRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "SummaryFrag";

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView column1;
        private TextView column2;
        private TextView column3;
        private TextView column4;
        private TextView column5;

        public ViewHolder(View v) {
            super(v);
            column1 = (TextView) v.findViewById(R.id.table_display_iv_column1);
            column2 = (TextView) v.findViewById(R.id.table_display_iv_column2);
            column3 = (TextView) v.findViewById(R.id.table_display_iv_column3);
            column4 = (TextView) v.findViewById(R.id.table_display_iv_column4);
            column5 = (TextView) v.findViewById(R.id.table_display_iv_column5);
        }

    }

    private ArrayList<double[]> mIVCombos;
    private Context mContext;

    public SummaryFragRecyclerViewAdapter(Context context, ArrayList<double[]> ivCombos) {
        mIVCombos = ivCombos;
        mContext = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.table_row_display_iv, parent, false);

        return new SummaryFragRecyclerViewAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DecimalFormat df = new DecimalFormat("0.0");
        if (mIVCombos.get(position)[0] < 1) {
            holder.column1.setText("-");
        } else {
            holder.column1.setText(String.valueOf((int) mIVCombos.get(position)[0]));
        }
        holder.column2.setText(String.valueOf((int) mIVCombos.get(position)[1]));
        holder.column3.setText(String.valueOf((int) mIVCombos.get(position)[2]));
        holder.column4.setText(String.valueOf((int) mIVCombos.get(position)[3]));
        holder.column5.setText(df.format(mIVCombos.get(position)[4]) + "%");
    }

    @Override
    public int getItemCount() {
        return mIVCombos.size();
    }
}
