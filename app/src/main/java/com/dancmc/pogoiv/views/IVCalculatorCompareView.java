package com.dancmc.pogoiv.views;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dancmc.pogoiv.R;
import com.dancmc.pogoiv.adapters.IVAdapter;
import com.dancmc.pogoiv.services.FloatingHead;
import com.dancmc.pogoiv.utilities.CustomToast;
import com.dancmc.pogoiv.utilities.Pokemon;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Daniel on 6/08/2016.
 */
public class IVCalculatorCompareView extends GenericServiceView {

    private ImageView mPokemonImage;
    private TextView mAverageIVPercent;
    private TextView mAverageCPPercent;
    private TextView mAverageIVPercentDesc;
    private TextView mAverageCPPercentDesc;

    private ListView mListView;
    private IVAdapter mAdapter;
    private ArrayList<double[]> mIVCombos;

    private FloatingActionButton mAddButton;


    public IVCalculatorCompareView(Context context) {
        //standard setup. Must call a super method GenericServiceView has no default constructor
        super(context);

        boolean adFree = sp.getBoolean("Adfree", false);

        if (adFree) {
            v = View.inflate(mContext, R.layout.view_service_ivcalculatorcompare_adfree, null);

        } else {
            v = View.inflate(mContext, R.layout.view_service_ivcalculatorcompare, null);
            AdView mAdView = (AdView) v.findViewById(R.id.adview_overlay_ivcalc);
            AdRequest request = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                    .addTestDevice("ABDC8E5277217A63DADF93CFCE6B47DB") // An example device ID
                    .build();
            mAdView.loadAd(request);
        }


        mListView = (ListView) v.findViewById(R.id.compare_summary_listview);
        //Most of this service layout is appended as a header to the ListView
        LayoutInflater vi = (LayoutInflater) mContext.getApplicationContext().getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
        View header = vi.inflate(R.layout.adapter_compare_summary_iv_listview_header, null, false);
        ViewGroup insertPoint1 = (ViewGroup) header.findViewById(R.id.summary_linear_layout1);


        //This part is the top part
        View top = vi.inflate(R.layout.header_ivcalc_display, null, false);
        mPokemonImage = (ImageView) top.findViewById(R.id.iv_calc_pokemon_image);
        mAverageIVPercent = (TextView) top.findViewById(R.id.iv_calc_ivpercent_text);
        mAverageIVPercentDesc = (TextView) top.findViewById(R.id.iv_calc_ivpercent_text_desc);
        mAverageCPPercent = (TextView) top.findViewById(R.id.iv_calc_cppercent_text);
        mAverageCPPercentDesc = (TextView) top.findViewById(R.id.iv_calc_cppercent_text_desc);

        if (mPokemon != null && mPokemon.getNumberOfResults() != 0) {
            ArrayList<Integer> tempLevelRange = mPokemon.getResultLevelRange();
            int lowestLevel = Collections.min(tempLevelRange);
            int highestLevel = Collections.max(tempLevelRange);
            mPokemonImage.setImageResource(mContext.getResources().getIdentifier(Pokemon.getPngFileName(mPokemon.getPokemonNumber()), "drawable", mContext.getPackageName()));
            mAverageIVPercent.setText((int) mPokemon.getAverageIVPercent() + "%");
            mAverageCPPercent.setText((int) mPokemon.getAverageCPPercent() + "%");
            mAverageIVPercentDesc.setText("(" + mDF.format(Collections.min(mPokemon.getIVPercentRange())) + " - " + mDF.format(Collections.max(mPokemon.getIVPercentRange())) + "%)\n" + "Level " + mDF.format((lowestLevel + 1) / 2.0) + "-" + mDF.format((highestLevel + 1) / 2.0) + "\n");
            mAverageCPPercentDesc.setText("Worst CP " + (int) (Pokemon.calculateMinCPAtLevel(mPokemon.getPokemonNumber(), lowestLevel)) + "-" + (int) (Pokemon.calculateMinCPAtLevel(mPokemon.getPokemonNumber(), highestLevel)) + "\nPerfect CP " + (int) (Pokemon.calculateMaxCPAtLevel(mPokemon.getPokemonNumber(), lowestLevel)) + "-" + (int) (Pokemon.calculateMaxCPAtLevel(mPokemon.getPokemonNumber(), highestLevel)));


        }
        insertPoint1.addView(top, -1, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));


        //This part is the comparesummary part
        mIVCombos = mPokemon.getIVCombinationsArray();
        //this part creates a card for every max evolved version of this pokemon
        String[] maxEvolved = Pokemon.getMaxEvolved(mPokemon.getPokemonNumber());
        for (int i = 0; i < maxEvolved.length; i++) {
            View w = vi.inflate(R.layout.adhoc_compare_summary_maxevolve_cardview, null, false);
            ImageView imageView = (ImageView) w.findViewById(R.id.summary_card_image);
            TextView textView1 = (TextView) w.findViewById(R.id.summary_card_pokemon);
            TextView textView2 = (TextView) w.findViewById(R.id.summary_card_CP);
            TextView textView3 = (TextView) w.findViewById(R.id.summary_card_min__CP);
            TextView textView4 = (TextView) w.findViewById(R.id.summary_card_perfect_CP);

            int pokeNumber = Pokemon.getPokemonNumberFromName(maxEvolved[i]);
            ArrayList<Double> CPPercentRange = Pokemon.getCpPercentRangeFromIVS(mPokemon.getIVCombinationsArray(), pokeNumber);

            imageView.setImageResource(mContext.getResources().getIdentifier(Pokemon.getPngFileName(pokeNumber), "drawable", mContext.getPackageName()));

            textView1.setText("Lvl 40.0 " + maxEvolved[i]);

            if (mIVCombos.size() == 0) {
                textView2.setText("--");
                textView3.setText("--");
                textView4.setText("--");
            } else {
                //display CP range at lvl 79 for different CP%
                textView2.setText("CP : " + (int) Pokemon.getMaxCPFromPercent(Collections.min(CPPercentRange), pokeNumber) + "-" + (int) Pokemon.getMaxCPFromPercent(Collections.max(CPPercentRange), pokeNumber));
                textView3.setText("" + Pokemon.getMinCP(pokeNumber));
                textView4.setText("" + Pokemon.getMaxCP(pokeNumber));
            }


            insertPoint1.addView(w, -1, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }


        //this part creates a card for every evolution of this pokemon at current level
        if (!mPokemon.isMaxEvolved()) {
            int[] evolvesTo = mPokemon.getEvolvesTo();
            for (int i = 0; i < evolvesTo.length; i++) {
                View w = vi.inflate(R.layout.adhoc_compare_summary_currentevolve_cardview, null, false);
                ImageView imageView = (ImageView) w.findViewById(R.id.summary_card2_image);
                TextView textView = (TextView) w.findViewById(R.id.summary_card2_text);

                imageView.setImageResource(mContext.getResources().getIdentifier(Pokemon.getPngFileName(evolvesTo[i]), "drawable", mContext.getPackageName()));
                textView.setText("HP : " + Collections.min(Pokemon.calculateHPRangeFromIVRange(mIVCombos, evolvesTo[i])) + " - " + Collections.max(Pokemon.calculateHPRangeFromIVRange(mIVCombos, evolvesTo[i])) + "\n" + "CP : " + Collections.min(Pokemon.calculateCPRangeFromIVRange(mIVCombos, evolvesTo[i])) + " - " + Collections.max(Pokemon.calculateCPRangeFromIVRange(mIVCombos, evolvesTo[i])));

                ViewGroup insertPoint2 = (ViewGroup) header.findViewById(R.id.summary_linear_layout2);
                insertPoint2.addView(w, -1, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
        } else {
            header.findViewById(R.id.summary_textview1).setVisibility(View.GONE);
        }


        //this part lists number of combinations found + the actual combination table
        TextView textView = (TextView) header.findViewById(R.id.summary_textview2);

        textView.setText("There were " + mIVCombos.size() + " combinations found.\n");


        mAdapter = new IVAdapter(mContext, mIVCombos);
        mListView.addHeaderView(header);
        mListView.setAdapter(mAdapter);

        //Add Pokemon button setup
        mAddButton = (FloatingActionButton) v.findViewById(R.id.add_button);
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FloatingHead.switchService(ADD_POKEBOX_SERVICE);
            }
        });


    }


}
