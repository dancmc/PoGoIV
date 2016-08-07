package com.dancmc.pogoiv.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.ListViewCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.dancmc.pogoiv.R;
import com.dancmc.pogoiv.adapters.IVAdapter;
import com.dancmc.pogoiv.utilities.Pokemon;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.zip.Inflater;


/**
 * A simple {@link Fragment} subclass.
 */
public class CompareSummaryFragment extends Fragment {


    private ArrayList<double[]> mIVCombos;
    private Pokemon mPokemon;
    private boolean isSinglePokemon;
    private ListView mListView;
    private IVAdapter mAdapter;
    private static final String TAG = "CompareSummaryFragment";
    private Toolbar mToolbar;
    private LinearLayout mToolbarContainer;



    public CompareSummaryFragment() {
        // Required empty public constructor
    }

    //this fragment is given a reference pokemon, and a set of ivCombos. boolean tells me whether it's a composite(comparison) or a single pokemon view
    //job is to return for the species : max evolved and max leveled stats for ivCombos, instant evolution stats for ivcombos, and ivcombos
    //so eg a lvl 79 blastoise - CP, a current lvl blastoise - CP/HP
    public static CompareSummaryFragment newInstance(Pokemon pokemon, ArrayList<double[]> ivComboArray, boolean isSinglePokemon) {
        CompareSummaryFragment myFragment = new CompareSummaryFragment();

        Bundle args = new Bundle();
        args.putSerializable("pokemon", pokemon);
        args.putSerializable("ivComboArray", ivComboArray);
        args.putBoolean("isSinglePokemon", isSinglePokemon);

        myFragment.setArguments(args);

        return myFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_compare_summary, container, false);

        View header = inflater.inflate(R.layout.adapter_compare_summary_iv_listview_header, null,false);

        //grab the arguments set in constructor
        mPokemon = (Pokemon) getArguments().getSerializable("pokemon");
        mIVCombos = (ArrayList<double[]>) getArguments().getSerializable("ivComboArray");
        isSinglePokemon = getArguments().getBoolean("isSinglePokemon");

        mListView = (ListView) v.findViewById(R.id.compare_summary_listview);
        mToolbar = (Toolbar) v.findViewById(R.id.toolbar);
        mToolbar.setTitle(getResources().getString(R.string.compare_summary));
        mToolbarContainer = (LinearLayout) v.findViewById(R.id.toolbar_container);

        if (getActivity().getClass().getSimpleName().equals("AddPokemonActivity")) {
            mToolbarContainer.setVisibility(View.GONE);

            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams)mListView.getLayoutParams();
            params.topMargin = 0;
            mListView.setLayoutParams(params);
        }

        //this part creates a card for every max evolved version of this pokemon
        LayoutInflater vi = (LayoutInflater) getActivity().getApplicationContext().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);

        String[] maxEvolved = Pokemon.getMaxEvolved(mPokemon.getPokemonNumber());
        for (int i = 0; i <maxEvolved.length; i++) {
            View w = vi.inflate(R.layout.adhoc_compare_summary_maxevolve_cardview, null,false);
            ImageView imageView = (ImageView) w.findViewById(R.id.summary_card_image);
            TextView textView1 = (TextView) w.findViewById(R.id.summary_card_pokemon);
            TextView textView2 = (TextView) w.findViewById(R.id.summary_card_CP);
            TextView textView3 = (TextView) w.findViewById(R.id.summary_card_min__CP);
            TextView textView4 = (TextView) w.findViewById(R.id.summary_card_perfect_CP);

            int pokeNumber = Pokemon.getPokemonNumberFromName(maxEvolved[i]);
            ArrayList<Double> CPPercentRange = Pokemon.getCpPercentRangeFromIVS(mIVCombos, pokeNumber);

            imageView.setImageResource(getResources().getIdentifier(Pokemon.getPngFileName(pokeNumber), "drawable", getActivity().getPackageName()));

            textView1.setText("Lvl 40.0 " + maxEvolved[i]);

            if (mIVCombos.size() == 0) {
                textView2.setText("--");
                textView3.setText("--");
                textView4.setText("--");
            } else {
                //display CP range at lvl 79 for different CP%
                textView2.setText("CP : "+(int) Pokemon.getMaxCPFromPercent(Collections.min(CPPercentRange), pokeNumber) + "-" + (int) Pokemon.getMaxCPFromPercent(Collections.max(CPPercentRange), pokeNumber));
                textView3.setText("" + Pokemon.getMinCP(pokeNumber));
                textView4.setText("" + Pokemon.getMaxCP(pokeNumber));
            }

            ViewGroup insertPoint = (ViewGroup) header.findViewById(R.id.summary_linear_layout1);
            insertPoint.addView(w, -1, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }


        //this part creates a card for every evolution of this pokemon at current level
        if(mPokemon.isMaxEvolved()||!isSinglePokemon){
            TextView textView = (TextView)header.findViewById(R.id.summary_textview1);
            textView.setVisibility(View.GONE);
        }
        else if(!mPokemon.isMaxEvolved()&&isSinglePokemon) {
            int[] evolvesTo = mPokemon.getEvolvesTo();
            for (int i = 0; i<evolvesTo.length; i++) {
                View w = vi.inflate(R.layout.adhoc_compare_summary_currentevolve_cardview, null,false);
                ImageView imageView = (ImageView)w.findViewById(R.id.summary_card2_image);
                TextView textView = (TextView)w.findViewById(R.id.summary_card2_text);

                imageView.setImageResource(getResources().getIdentifier(Pokemon.getPngFileName(evolvesTo[i]), "drawable", getActivity().getPackageName()));
                textView.setText("HP : "+Collections.min(Pokemon.calculateHPRangeFromIVRange(mIVCombos,evolvesTo[i]))+" - "+Collections.max(Pokemon.calculateHPRangeFromIVRange(mIVCombos,evolvesTo[i]))+"\n"+"CP : "+Collections.min(Pokemon.calculateCPRangeFromIVRange(mIVCombos,evolvesTo[i]))+" - "+Collections.max(Pokemon.calculateCPRangeFromIVRange(mIVCombos,evolvesTo[i])));

                ViewGroup insertPoint = (ViewGroup) header.findViewById(R.id.summary_linear_layout2);
                insertPoint.addView(w, -1, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
        }



        //this part lists number of combinations found + the actual combination table
        TextView textView = (TextView) header.findViewById(R.id.summary_textview2);
        if (isSinglePokemon) {
            textView.setText("There were " + mIVCombos.size() + " combinations found.\n");
        } else {
            textView.setText("There were " + mIVCombos.size() + " overlapping combinations found.\n");
        }

        mAdapter = new IVAdapter(getActivity(), mIVCombos);
        mListView.addHeaderView(header);
        mListView.setAdapter(mAdapter);

        return v;
    }



}
