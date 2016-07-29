package com.dancmc.pogoiv;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class CompareSummaryFragment extends Fragment {


    ArrayList<double[]> mIVCombos;
    Pokemon mPokemon;
    boolean mHasLevels;


    public CompareSummaryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_compare_summary, container, false);

        LayoutInflater vi = (LayoutInflater) getActivity().getApplicationContext().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);

        String[] maxEvolved = Pokemon.getMaxEvolved(mPokemon.getPokemonNumber());
        for (int i = maxEvolved.length - 1; i >= 0; i--) {

            View w = vi.inflate(R.layout.cardview_summary_evolved, null);
            ImageView imageView = (ImageView) w.findViewById(R.id.summary_card_image);
            TextView textView1 = (TextView) w.findViewById(R.id.summary_card_pokemon);
            TextView textView2 = (TextView) w.findViewById(R.id.summary_card_CP);
            TextView textview3 = (TextView) w.findViewById(R.id.summary_card_min__CP);
            TextView textView4 = (TextView) w.findViewById(R.id.summary_card_perfect_CP);

            int pokeNumber = Pokemon.getPokemonNumberFromName(maxEvolved[i]);
            ArrayList<Double> CPRange= Pokemon.getCpPercentRangeFromIVS(mIVCombos, pokeNumber);
            imageView.setImageResource(getResources().getIdentifier(Pokemon.getPngFileName(pokeNumber), "drawable", getActivity().getPackageName()));

            textView1.setText("Lvl 79 " + maxEvolved[i] + " with this average IV% would have a CP of ~");
            textView2.setText((int)Pokemon.getMaxCPFromPercent(Collections.min(CPRange), pokeNumber)+"-"+(int)Pokemon.getMaxCPFromPercent(Collections.max(CPRange), pokeNumber) );
            textview3.setText("" + Pokemon.getMinCP(pokeNumber));
            textView4.setText("" + Pokemon.getMaxCP(pokeNumber));

            ViewGroup insertPoint = (ViewGroup) v.findViewById(R.id.summary_linear_layout);
            insertPoint.addView(w, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        TextView textView = (TextView) v.findViewById(R.id.summary_textview);
        textView.setText("There were " + mIVCombos.size() + " overlapping combinations found.");

        for (int i = -1; i < mIVCombos.size(); i++) {
            ViewGroup insertPoint = (ViewGroup) v.findViewById(R.id.summary_linear_layout2);
            View z = vi.inflate(R.layout.table_row_display_iv, null);
            TextView column1 = (TextView) z.findViewById(R.id.table_display_iv_column1);
            TextView column2 = (TextView) z.findViewById(R.id.table_display_iv_column2);
            TextView column3 = (TextView) z.findViewById(R.id.table_display_iv_column3);
            TextView column4 = (TextView) z.findViewById(R.id.table_display_iv_column4);
            TextView column5 = (TextView) z.findViewById(R.id.table_display_iv_column5);

            if (i == -1) {
                column1.setText("Level");
                column2.setText("Stamina");
                column3.setText("Attack");
                column4.setText("Defence");
                column5.setText("IV%");
            } else {
                if (mHasLevels) {
                    column1.setText(mIVCombos.get(i)[0] + "");
                } else {
                    column1.setText("-");
                }
                column2.setText(mIVCombos.get(i)[1] + "");
                column3.setText(mIVCombos.get(i)[2] + "");
                column4.setText(mIVCombos.get(i)[3] + "");
                column5.setText(String.format(Locale.US, "%.1f", mIVCombos.get(i)[4]) + "%");
            }
            insertPoint.addView(z, -1, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        return v;
    }

    public void setPokemon(Pokemon pokemon) {
        mPokemon = pokemon;
    }

    public void setIVCombos(ArrayList<double[]> ivcombos) {
        mIVCombos = ivcombos;
    }

    public void setHasLevels(boolean hasLevels) {
        mHasLevels = hasLevels;
    }


}
