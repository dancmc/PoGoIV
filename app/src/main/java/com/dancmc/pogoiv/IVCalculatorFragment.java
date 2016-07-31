package com.dancmc.pogoiv;


import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;


public class IVCalculatorFragment extends ContractFragment<IVCalculatorFragment.Contract> {

    //Views and utility stuff
    private AutoCompleteTextView mPokemonNameInput;
    private TextView mOutputView;
    private EditText mCPInput;
    private EditText mHPInput;
    private EditText mStarDustInput;
    private EditText mLevelInput;
    private CheckBox mNotFreshMeatInput;
    private StringBuilder mStringBuilder;
    private Pokemon mPokemon;

    private ImageView mPokemonImage;
    private TextView mAverageIVPercent;
    private TextView mAverageCPPercent;
    private TextView mAverageIVPercentDesc;
    private TextView mAverageCPPercentDesc;

    private static final String TAG = "IVCalculatorFragment";
    //Buttons
    private Button mCalculateButton;
    private Button mPokeboxButton;
    private ImageButton mAddButton;
    private Button mMoreInfoButton;
    private DecimalFormat mDF;

    private PokeballsDataSource mDataSource;
    private Toolbar mToolbar;

    public IVCalculatorFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_ivcalculator, container, false);

        //finding views
        mCPInput = (EditText) v.findViewById(R.id.enter_cp);
        mHPInput = (EditText) v.findViewById(R.id.enter_hp);
        mStarDustInput = (EditText) v.findViewById(R.id.enter_stardust);
        mLevelInput = (EditText) v.findViewById(R.id.enter_known_level);
        mNotFreshMeatInput = (CheckBox) v.findViewById(R.id.checkbox_powerup);
        mOutputView = (TextView) v.findViewById(R.id.output);

        mPokemonImage = (ImageView) v.findViewById(R.id.iv_calc_pokemon_image);
        mAverageIVPercent = (TextView) v.findViewById(R.id.iv_calc_ivpercent_text);
        mAverageIVPercentDesc = (TextView) v.findViewById(R.id.iv_calc_ivpercent_text_desc);
        mAverageCPPercent = (TextView) v.findViewById(R.id.iv_calc_cppercent_text);
        mAverageCPPercentDesc = (TextView) v.findViewById(R.id.iv_calc_cppercent_text_desc);
        mDF = new DecimalFormat("0.0");

        mToolbar = (Toolbar) v.findViewById(R.id.fragment_iv_calc_toolbar);
        mToolbar.setTitle("IV Calculator");

        //Autocomplete textview setup
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, Pokemon.getPokedex());
        mPokemonNameInput = (AutoCompleteTextView) v.findViewById(R.id.enter_pokemon_name);
        mPokemonNameInput.setAdapter(adapter);

        if(getActivity().getClass().getSimpleName().equals("AddPokemonActivity")) {
            mToolbar.setVisibility(View.GONE);
        }

        if (mPokemon!=null) {
            mPokemonNameInput.setText(mPokemon.getPokemonName());
            mCPInput.setText(mPokemon.getCP()+"");
            if(mPokemon.getHP()>0){
                mHPInput.setText(mPokemon.getHP()+"");
            }
            if(mPokemon.getStardust()>0){
                mHPInput.setText(mPokemon.getStardust()+"");
            }
            if(mPokemon.getKnownLevel()>0){
                mLevelInput.setText(mPokemon.getKnownLevel()+"");
            }
            mNotFreshMeatInput.setChecked(!mPokemon.getFreshMeat());

            ArrayList<Integer> tempLevelRange = mPokemon.getResultLevelRange();
            //ArrayList<Double> tempCpRange = Pokemon.getCpPercentRangeFromIVS(mPokemon.getIVCombinationsArray(), mPokemon.getPokemonNumber());
            int lowestLevel = Collections.min(tempLevelRange);
            int highestLevel = Collections.max(tempLevelRange);
            mPokemonImage.setImageResource(getResources().getIdentifier(Pokemon.getPngFileName(mPokemon.getPokemonNumber()), "drawable", getActivity().getPackageName()));
            mAverageIVPercent.setText((int) mPokemon.getAverageIVPercent() + "%");
            mAverageCPPercent.setText((int) mPokemon.getAverageCPPercent() + "%");
            mAverageIVPercentDesc.setText("IV%\n" + "(" + mDF.format(Collections.min(mPokemon.getIVPercentRange())) + " - " + mDF.format(Collections.max(mPokemon.getIVPercentRange())) + "%)\n"+"Level " + lowestLevel + "-" + highestLevel + "\n");
            mAverageCPPercentDesc.setText("CP%\n" +  "(" + mDF.format(Collections.min(mPokemon.getCPPercentRange())) + " - " + mDF.format(Collections.max(mPokemon.getCPPercentRange())) + "%)\n"+"Worst CP " + (int) (Pokemon.calculateMinCPAtLevel(mPokemon.getPokemonNumber(), lowestLevel)) + "-" + (int) (Pokemon.calculateMinCPAtLevel(mPokemon.getPokemonNumber(), highestLevel)) + "\nPerfect CP " + (int) (Pokemon.calculateMaxCPAtLevel(mPokemon.getPokemonNumber(), lowestLevel)) + "-" + (int) (Pokemon.calculateMaxCPAtLevel(mPokemon.getPokemonNumber(), highestLevel)));

            mOutputView.setText(mStringBuilder.toString());
        }

        //calculate button setup
        mCalculateButton = (Button) v.findViewById(R.id.calculate_button);
        mCalculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStringBuilder = new StringBuilder();

                //hides keyboard
                InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow((null == getActivity().getCurrentFocus()) ? null : getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                try {
                    createPokemonFromInput();
                } catch (Exception e) {
                    mOutputView.setText(e.getMessage());
                    Toast.makeText(getActivity(), "Error calculating...check input", Toast.LENGTH_SHORT)
                            .show();
                    return;
                }

                mOutputView.setText(mStringBuilder.toString());

                if (mPokemon != null && mPokemon.mNumberOfResults != 0) {
                    ArrayList<Integer> tempLevelRange = mPokemon.getResultLevelRange();
                    //ArrayList<Double> tempCpRange = Pokemon.getCpPercentRangeFromIVS(mPokemon.getIVCombinationsArray(), mPokemon.getPokemonNumber());
                    int lowestLevel = Collections.min(tempLevelRange);
                    int highestLevel = Collections.max(tempLevelRange);
                    mPokemonImage.setImageResource(getResources().getIdentifier(Pokemon.getPngFileName(mPokemon.getPokemonNumber()), "drawable", getActivity().getPackageName()));
                    mAverageIVPercent.setText((int) mPokemon.getAverageIVPercent() + "%");
                    mAverageCPPercent.setText((int) mPokemon.getAverageCPPercent() + "%");
                    mAverageIVPercentDesc.setText("IV%\n" + "(" + mDF.format(Collections.min(mPokemon.getIVPercentRange())) + " - " + mDF.format(Collections.max(mPokemon.getIVPercentRange())) + "%)\n"+"Level " + lowestLevel + "-" + highestLevel + "\n");
                    mAverageCPPercentDesc.setText("CP%\n" +  "(" + mDF.format(Collections.min(mPokemon.getCPPercentRange())) + " - " + mDF.format(Collections.max(mPokemon.getCPPercentRange())) + "%)\n"+"Worst CP " + (int) (Pokemon.calculateMinCPAtLevel(mPokemon.getPokemonNumber(), lowestLevel)) + "-" + (int) (Pokemon.calculateMinCPAtLevel(mPokemon.getPokemonNumber(), highestLevel)) + "\nPerfect CP " + (int) (Pokemon.calculateMaxCPAtLevel(mPokemon.getPokemonNumber(), lowestLevel)) + "-" + (int) (Pokemon.calculateMaxCPAtLevel(mPokemon.getPokemonNumber(), highestLevel)));
                }
                Toast.makeText(getActivity(), "Calculated!", Toast.LENGTH_SHORT)
                        .show();
            }
        });

        //pokebox button setup
        mPokeboxButton = (Button) v.findViewById(R.id.calc_to_pokebox_button);
        mPokeboxButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContract().pokeboxButtonPressed();
            }
        });

        //Add Pokemon button setup
        mAddButton = (ImageButton) v.findViewById(R.id.add_button);
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mPokemon == null) {
                    Toast.makeText(getActivity(), "You have not calculated a Pokemon yet", Toast.LENGTH_SHORT)
                            .show();
                    return;
                }

                if (mPokemon.getNumberOfResults() == 0) {
                    Toast.makeText(getActivity(), "Sorry, you can't add Pokemon with no combinations.", Toast.LENGTH_SHORT)
                            .show();
                    return;
                }

                getContract().addButtonPressed(mPokemon);
            }
        });

        //set up more info button
        mMoreInfoButton = (Button) v.findViewById(R.id.iv_calc_more_info_button);
        mMoreInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mPokemon == null) {
                    Toast.makeText(getActivity(), "You have not calculated a Pokemon yet", Toast.LENGTH_SHORT)
                            .show();
                    return;
                } else if(mPokemon.mNumberOfResults==0) {
                    Toast.makeText(getActivity(), "There are no combinations!", Toast.LENGTH_SHORT)
                            .show();
                    return;
                } else {
                    getContract().moreInfoButtonPressed(mPokemon);
                }

            }
        });

        return v;
    }

    //reads the input from the EditText views and calls Pokemon constructor

    private void createPokemonFromInput() {
        String pokemonName = "" + mPokemonNameInput.getText().toString();
        int cp = parseIntInput(mCPInput);
        int hp = parseIntInput(mHPInput);
        int stardust = parseIntInput(mStarDustInput);
        int level = parseIntInput(mLevelInput);
        boolean freshMeat = !(mNotFreshMeatInput.isChecked());

        //throws exception if invalid/blank pokemon name, or invalid stardust
        //if HP/CP/Stardust blank, is ok, passes in a -1
        try {
            mPokemon = new Pokemon(pokemonName, hp, cp, stardust, freshMeat, level);
        } catch (Exception e) {
            throw e;
        }

    }

    //returns the integer in an number EditText, or if blank, returns 0
    private int parseIntInput(EditText textInput) {
        int number;
        String input = textInput.getText().toString();
        if (input.equals("")) {
            if (textInput == mCPInput)
                throw new IllegalArgumentException("You must enter a CP value.");
            else if (textInput == mStarDustInput)
                mStringBuilder.append("Note : You did not enter a stardust value. All levels calculated.\n\n");
            else if (textInput == mHPInput)
                mStringBuilder.append("Note : You did not enter a HP value. All values calculated\n\n");
            return -1;
        } else {
            try {
                number = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                throw new NumberFormatException("You have to enter whole numbers.");
            }
            if (number < 1)
                throw new NumberFormatException("You have to enter positive numbers.");

        }
        return number;
    }

    public interface Contract {
        public void addButtonPressed(Pokemon pokemon);

        public void pokeboxButtonPressed();

        public void moreInfoButtonPressed(Pokemon pokemon);
    }

}
