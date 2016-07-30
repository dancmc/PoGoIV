package com.dancmc.pogoiv;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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


    private ArrayList<double[]> mIVCombos;
    private Pokemon mPokemon;
    private boolean isSinglePokemon;
    private RecyclerView mRecyclerView;
    private SummaryFragRecyclerViewAdapter mAdapter;
    private static final String TAG = "CompareSummaryFragment";


    public CompareSummaryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_compare_summary, container, false);
        Log.d(TAG, "onCreateView: start");
        mRecyclerView = (RecyclerView) v.findViewById(R.id.summary_recyclerview);

        if(getActivity().getClass().getSimpleName()!="AddPokemonActivity") {
            Toolbar toolbar = (Toolbar) v.findViewById(R.id.compare_summary_toolbar);
            toolbar.setTitle(getResources().getString(R.string.compare_summary));
        }

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

            ArrayList<Double> CPPercentRange = Pokemon.getCpPercentRangeFromIVS(mIVCombos, pokeNumber);

            imageView.setImageResource(getResources().getIdentifier(Pokemon.getPngFileName(pokeNumber), "drawable", getActivity().getPackageName()));

            textView1.setText("Lvl 79 " + maxEvolved[i] + " with this average IV% would have a CP of ~");

            //display CP range at lvl 79 for different CP%
            textView2.setText((int) Pokemon.getMaxCPFromPercent(Collections.min(CPPercentRange), pokeNumber) + "-" + (int) Pokemon.getMaxCPFromPercent(Collections.max(CPPercentRange), pokeNumber));
            textview3.setText("" + Pokemon.getMinCP(pokeNumber));
            textView4.setText("" + Pokemon.getMaxCP(pokeNumber));

            ViewGroup insertPoint = (ViewGroup) v.findViewById(R.id.summary_linear_layout);
            insertPoint.addView(w, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        TextView textView = (TextView) v.findViewById(R.id.summary_textview);
        if (isSinglePokemon) {
            textView.setText("There were " + mIVCombos.size() + " combinations found.\n");
        } else {
            textView.setText("There were " + mIVCombos.size() + " overlapping combinations found.\n");
        }

        mAdapter = new SummaryFragRecyclerViewAdapter(getActivity(), mIVCombos);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        Log.d(TAG, "onCreateView: end");
        return v;
    }

    public void setPokemon(Pokemon pokemon) {
        mPokemon = pokemon;
    }

    public void setIVCombos(ArrayList<double[]> ivcombos) {
        mIVCombos = ivcombos;
    }

    public void isSinglePokemon(boolean single) {
        isSinglePokemon = single;
    }


}
