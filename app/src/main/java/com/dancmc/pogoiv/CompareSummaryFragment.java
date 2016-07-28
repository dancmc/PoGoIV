package com.dancmc.pogoiv;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class CompareSummaryFragment extends Fragment {

    TextView mTextView;
    String mIVList;

    public CompareSummaryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_compare_summary, container, false);

        mTextView = (TextView) v.findViewById(R.id.compare_summary_textview);
        mTextView.setText(mIVList);


        return v;
    }

    public void setText(String s) {
        mIVList = s;
    }


}
