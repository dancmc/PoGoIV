package com.dancmc.pogoiv.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dancmc.pogoiv.database.PokeballsDataSource;
import com.dancmc.pogoiv.R;
import com.dancmc.pogoiv.services.FloatingHead;
import com.dancmc.pogoiv.utilities.Pokemon;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;


public class IVCalculatorFragment extends ContractFragment<IVCalculatorFragment.Contract> {

    //Views and utility stuff
    private AutoCompleteTextView mPokemonNameInput;
    private TextView mOutputView;
    private EditText mCPInput;
    private EditText mHPInput;
    private EditText mStarDustInput;
    private EditText mLevelInput;
    private CheckBox mNotFreshMeatInput;

    private Pokemon mPokemon;

    private ImageView mPokemonImage;
    private TextView mAverageIVPercent;
    private TextView mAverageCPPercent;
    private TextView mAverageIVPercentDesc;
    private TextView mAverageCPPercentDesc;
    private RelativeLayout mMainLayout;

    private static final String TAG = "IVCalculatorFragment";
    //Buttons
    private Button mCalculateButton;
    private Button mPokeboxButton;
    private ImageButton mAddButton;
    private Button mMoreInfoButton;
    private DecimalFormat mDF;

    private PokeballsDataSource mDataSource;
    private Toolbar mToolbar;
    private LinearLayout mToolbarContainer;

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

        mToolbar = (Toolbar) v.findViewById(R.id.toolbar);
        mToolbar.setTitle("IV Calculator");
        mToolbarContainer = (LinearLayout) v.findViewById(R.id.toolbar_container);
        mMainLayout = (RelativeLayout) v.findViewById(R.id.iv_calc_main_layout);

        //Autocomplete textview setup
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, Pokemon.getPokedex());
        mPokemonNameInput = (AutoCompleteTextView) v.findViewById(R.id.enter_pokemon_name);
        mPokemonNameInput.setAdapter(adapter);

        //Toolbar setup
        if (getActivity().getClass().getSimpleName().equals("AddPokemonActivity")) {
            mToolbarContainer.setVisibility(View.GONE);

            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mMainLayout.getLayoutParams();
            params.topMargin = 0;
            mMainLayout.setLayoutParams(params);
        } else {
            mToolbar.inflateMenu(R.menu.menu_iv_calculator);
            mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case (R.id.start_floating_head):
                            getContract().overlayRequested();
                            return true;
                        case (R.id.tutorial):
                            getContract().tutorial();
                            return true;
                    }
                    return false;
                }
            });
        }

        if (mPokemon != null && mPokemon.getNumberOfResults() != 0) {
            mPokemonNameInput.setText(mPokemon.getPokemonName());
            mCPInput.setText(mPokemon.getCP() + "");
            if (mPokemon.getHP() > 0) {
                mHPInput.setText(mPokemon.getHP() + "");
            }
            if (mPokemon.getStardust() > 0) {
                mHPInput.setText(mPokemon.getStardust() + "");
            }
            if (mPokemon.getKnownLevel() > 0) {
                mLevelInput.setText(mPokemon.getKnownLevel() + "");
            }
            mNotFreshMeatInput.setChecked(!mPokemon.getFreshMeat());

            ArrayList<Integer> tempLevelRange = mPokemon.getResultLevelRange();
            int lowestLevel = Collections.min(tempLevelRange);
            int highestLevel = Collections.max(tempLevelRange);
            mPokemonImage.setImageResource(getResources().getIdentifier(Pokemon.getPngFileName(mPokemon.getPokemonNumber()), "drawable", getActivity().getPackageName()));
            mAverageIVPercent.setText((int) mPokemon.getAverageIVPercent() + "%");
            mAverageCPPercent.setText((int) mPokemon.getAverageCPPercent() + "%");
            mAverageIVPercentDesc.setText("(" + mDF.format(Collections.min(mPokemon.getIVPercentRange())) + " - " + mDF.format(Collections.max(mPokemon.getIVPercentRange())) + "%)\n" + "Level " + mDF.format((lowestLevel + 1) / 2.0) + "-" + mDF.format((highestLevel + 1) / 2.0) + "\n");
            mAverageCPPercentDesc.setText("(" + mDF.format(Collections.min(mPokemon.getCPPercentRange())) + " - " + mDF.format(Collections.max(mPokemon.getCPPercentRange())) + "%)\n" + "Worst CP " + (int) (Pokemon.calculateMinCPAtLevel(mPokemon.getPokemonNumber(), lowestLevel)) + "-" + (int) (Pokemon.calculateMinCPAtLevel(mPokemon.getPokemonNumber(), highestLevel)) + "\nPerfect CP " + (int) (Pokemon.calculateMaxCPAtLevel(mPokemon.getPokemonNumber(), lowestLevel)) + "-" + (int) (Pokemon.calculateMaxCPAtLevel(mPokemon.getPokemonNumber(), highestLevel)));

        }

        //calculate button setup
        mCalculateButton = (Button) v.findViewById(R.id.calculate_button);
        mCalculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //hides keyboard
                InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow((null == getActivity().getCurrentFocus()) ? null : getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);


                try {
                    createPokemonFromInput();
                } catch (Exception e) {
                    Toast.makeText(getActivity(), e.getMessage().toString(), Toast.LENGTH_SHORT)
                            .show();
                    return;
                }


                if (mPokemon != null && mPokemon.getNumberOfResults() != 0) {
                    ArrayList<Integer> tempLevelRange = mPokemon.getResultLevelRange();
                    //ArrayList<Double> tempCpRange = Pokemon.getCpPercentRangeFromIVS(mPokemon.getIVCombinationsArray(), mPokemon.getPokemonNumber());
                    int lowestLevel = Collections.min(tempLevelRange);
                    int highestLevel = Collections.max(tempLevelRange);
                    mPokemonImage.setImageResource(getResources().getIdentifier(Pokemon.getPngFileName(mPokemon.getPokemonNumber()), "drawable", getActivity().getPackageName()));
                    mAverageIVPercent.setText((int) mPokemon.getAverageIVPercent() + "%");
                    mAverageCPPercent.setText((int) mPokemon.getAverageCPPercent() + "%");
                    mAverageIVPercentDesc.setText("(" + mDF.format(Collections.min(mPokemon.getIVPercentRange())) + " - " + mDF.format(Collections.max(mPokemon.getIVPercentRange())) + "%)\n" + "Level " + mDF.format((lowestLevel + 1) / 2.0) + "-" + mDF.format((highestLevel + 1) / 2.0) + "\n");
                    mAverageCPPercentDesc.setText("(" + mDF.format(Collections.min(mPokemon.getCPPercentRange())) + " - " + mDF.format(Collections.max(mPokemon.getCPPercentRange())) + "%)\n" + "Worst CP " + (int) (Pokemon.calculateMinCPAtLevel(mPokemon.getPokemonNumber(), lowestLevel)) + "-" + (int) (Pokemon.calculateMinCPAtLevel(mPokemon.getPokemonNumber(), highestLevel)) + "\nPerfect CP " + (int) (Pokemon.calculateMaxCPAtLevel(mPokemon.getPokemonNumber(), lowestLevel)) + "-" + (int) (Pokemon.calculateMaxCPAtLevel(mPokemon.getPokemonNumber(), highestLevel)));
                    Toast.makeText(getActivity(), "Calculated!", Toast.LENGTH_SHORT)
                            .show();
                }
                if (mPokemon != null && mPokemon.getNumberOfResults() == 0) {
                    Toast.makeText(getActivity(), "No combinations found!", Toast.LENGTH_SHORT)
                            .show();
                }
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
                    Toast.makeText(getActivity(), "You have not calculated a snapshot yet", Toast.LENGTH_SHORT)
                            .show();
                    return;
                }

                if (mPokemon.getNumberOfResults() == 0) {
                    Toast.makeText(getActivity(), "Sorry, you can't add a snapshot with no combinations.", Toast.LENGTH_SHORT)
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
                    Toast.makeText(getActivity(), "You have not calculated a snapshot yet", Toast.LENGTH_SHORT)
                            .show();
                    return;
                } else if (mPokemon.getNumberOfResults() == 0) {
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
        int level = parseLevelInput(mLevelInput);
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
            else if (textInput == mStarDustInput) {
                //mStringBuilder.append("Note : You did not enter a stardust value. All levels calculated.\n\n");
            } else if (textInput == mHPInput) {
                //StringBuilder.append("Note : You did not enter a HP value. All values calculated\n\n");
            }
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

    private int parseLevelInput(EditText levelInput) {
        String input = levelInput.getText().toString();
        if (input.equals("")) {
            return -1;
        }
        double level = Double.parseDouble(input);
        if (level < 1.0 || level > 40.0 || (level % 0.5) != 0) {
            throw new IllegalArgumentException("Known level must be 1-40 at 0.5 intervals");
        }
        return ((int) (level * 2.0 - 1.0));
    }

    public interface Contract {
        public void addButtonPressed(Pokemon pokemon);

        public void pokeboxButtonPressed();

        public void moreInfoButtonPressed(Pokemon pokemon);

        public void overlayRequested();

        public void tutorial();
    }

}
