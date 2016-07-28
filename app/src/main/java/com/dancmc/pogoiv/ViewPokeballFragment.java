package com.dancmc.pogoiv;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ViewPokeballFragment extends ContractFragment<ViewPokeballFragment.Contract> {


    public ViewPokeballFragment() {
        // Required empty public constructor
    }

    private int mPosition;
    private Pokeball mPokeball;

    private ImageView mPokemonImage;
    private TextView mNickname;
    private TextView mIVPercent;
    private TextView mCPPercent;
    private TextView mSummary;
    private ExpandableListView mELV;
    private FloatingActionButton mFAB;


    private PokeballsDataSource mDataSource;
    private ViewPokeballExpandableListAdapter mAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_view_pokeball, container, false);

        mPokeball = Pokeballs.getPokeballsInstance().get(mPosition);

        mPokemonImage = (ImageView) v.findViewById(R.id.pokeball_view_image);
        mNickname = (TextView) v.findViewById(R.id.pokeball_view_nickname);
        mIVPercent = (TextView) v.findViewById(R.id.pokeball_view_IV_percent);
        mCPPercent = (TextView) v.findViewById(R.id.pokeball_view_CP_percent);
        mSummary = (TextView) v.findViewById(R.id.pokeball_view_summary);
        mFAB = (FloatingActionButton) v.findViewById(R.id.add_activity_fab);

        mELV = (ExpandableListView) v.findViewById(R.id.pokeball_view_expandableLV);
        mDataSource = new PokeballsDataSource(getActivity());

        /*
        int highestTier = 0;
        int highestEvolved = 0;
        for (int i = 0; i < mPokeball.size(); i++) {
            int tier = mPokeball.get(i).getEvolutionTier();
            if (tier > highestTier) {
                highestTier = tier;
                highestEvolved = i;
            }
        }*/


        mPokemonImage.setImageResource(getResources().getIdentifier(Pokemon.getPngFileName(mPokeball.getHighestEvolvedPokemonNumber()), "drawable", getActivity().getPackageName()));
        mPokemonImage.setBackgroundResource(R.drawable.circle_background);
        mNickname.setText(mPokeball.get(0).getNickname());


        final String[] result = mDataSource.compareAllPokemon(mPosition);
        if (Integer.parseInt(result[0]) == 0) {
            mSummary.setText(result[1]);
        } else {
            mIVPercent.setText(result[2] + "%");
            mCPPercent.setText(result[3] + "%");
            mSummary.setText(result[4]);
        }

        mSummary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContract().onViewSummaryClick(result[5]);
            }
        });

        ArrayList<String> listHeaders = new ArrayList<>();
        for (int i = 0; i < mPokeball.size(); i++) {
            StringBuilder sb = new StringBuilder();
            if (mPokeball.get(i).getCP() > 0) {
                sb.append("CP : " + mPokeball.get(i).getCP() + "  ");
            } else {
                sb.append("CP : nil  ");
            }
            if (mPokeball.get(i).getHP() > 0) {
                sb.append("HP : " + mPokeball.get(i).getHP() + "  ");
            } else {
                sb.append("HP : nil  ");
            }
            if (mPokeball.get(i).getStardust() > 0) {
                sb.append("Dust : " + mPokeball.get(i).getStardust() + " \n");
            } else {
                sb.append("Dust : nil  \n");
            }
            if (mPokeball.get(i).getFreshMeat()) {
                sb.append("Powered up");
            } else {
                sb.append("Not powered up");
            }

            listHeaders.add(sb.toString());
        }

        ArrayList<Integer> listHeaderImages = new ArrayList<>();
        for (int i = 0; i < mPokeball.size(); i++) {
            listHeaderImages.add(getResources().getIdentifier(Pokemon.getPngFileName(mPokeball.get(i).getPokemonNumber()), "drawable", getActivity().getPackageName()));
        }

        ArrayList<String> listBodies = new ArrayList<>();
        for (int i = 0; i < mPokeball.size(); i++) {
            //TODO : edit this output
            listBodies.add(mPokeball.get(i).getStringOutput());
        }

        mAdapter = new ViewPokeballExpandableListAdapter(getActivity(), listHeaders, listBodies, listHeaderImages);
        mELV.setAdapter(mAdapter);

        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContract().onAddFabClick(mPosition);
            }
        });

        return v;
    }

    //the position is the number of the pokeball in the singleton
    public void setPosition(int position) {
        mPosition = position;
    }

    public interface Contract {
        public void onViewSummaryClick(String s);

        public void onAddFabClick(int position);

    }
}
