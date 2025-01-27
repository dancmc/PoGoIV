package com.dancmc.pogoiv.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.dancmc.pogoiv.utilities.Pokeball;
import com.dancmc.pogoiv.utilities.Pokeballs;
import com.dancmc.pogoiv.database.PokeballsDataSource;
import com.dancmc.pogoiv.utilities.Pokemon;
import com.dancmc.pogoiv.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditPokemonFragment extends ContractFragment<EditPokemonFragment.Contract> {

    //Views and utility stuff
    private AutoCompleteTextView mPokemonNameInput;
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
    private ScrollView mScrollview;

    private static final String TAG = "EditFragment";
    //Buttons
    private Button mCalculateButton;
    private Button mMoreInfoButton;
    private DecimalFormat mDF;
    private int mCalculateButtonPressed;

    private PokeballsDataSource mDataSource;

    //extra stuff for edit pokemon
    private int mPokeballNumber;
    private int mPokeballListNumber;
    private Toolbar mToolbar;
    private LinearLayout mToolbarContainer;

    public EditPokemonFragment() {
        // Required empty public constructor
    }

    public static EditPokemonFragment newInstance(int pokeballNumber, int pokeballListNumber) {
        EditPokemonFragment myFragment = new EditPokemonFragment();

        Bundle args = new Bundle();
        args.putInt("pokeballNumber", pokeballNumber);
        args.putInt("pokeballListNumber", pokeballListNumber);
        if (pokeballListNumber == -1) {
            args.putSerializable("pokemon", null);
        } else {
            args.putSerializable("pokemon", Pokeballs.getPokeballsInstance().get(pokeballNumber).get(pokeballListNumber));
        }

        myFragment.setArguments(args);

        return myFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_edit_pokemon, container, false);

        mDataSource = new PokeballsDataSource(getActivity());
        //get arguments
        mPokeballNumber = getArguments().getInt("pokeballNumber");
        mPokeballListNumber = getArguments().getInt("pokeballListNumber");
        mPokemon = (Pokemon) getArguments().getSerializable("pokemon");
        mCalculateButtonPressed = 0;
        mToolbarContainer = (LinearLayout) v.findViewById(R.id.toolbar_container);
        mScrollview = (ScrollView) v.findViewById(R.id.edit_calculator_main_layout);

        mToolbar = (Toolbar) v.findViewById(R.id.toolbar);
        mToolbar.inflateMenu(R.menu.menu_edit_fragment);
        if (mPokeballListNumber == -1) {
            mToolbar.setTitle(getResources().getString(R.string.edit_pokemon_fragment_add));
        } else {
            mToolbar.setTitle(getResources().getString(R.string.edit_pokemon_fragment_edit));
        }

        if (getActivity().getClass().getSimpleName().equals("AddPokemonActivity")) {
            mToolbarContainer.setVisibility(View.GONE);

            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mScrollview.getLayoutParams();
            params.topMargin = 0;
            mScrollview.setLayoutParams(params);
        } else {


            mToolbar.setNavigationIcon(R.drawable.ic_close_white_48px);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().onBackPressed();
                }
            });

            mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    //if mPokeballListNumber is -1, we are adding, if ListNumber >=0, we are editing
                    //if adding, mPokemon starts off null, do usual screen and add
                    //if editing, mPokemon would be loaded, if they save without calculating, then have to create the pokemon
                    if (item.getItemId() == R.id.save_edit_pokemon) {

                        if (mPokemon == null) {
                            Toast.makeText(getActivity(), "You have not calculated a snapshot yet", Toast.LENGTH_SHORT)
                                    .show();
                            return true;
                        }


                        if (mPokemon.getNumberOfResults() == 0) {
                            Toast.makeText(getActivity(), "Sorry, you can't add snapshots with no combinations.", Toast.LENGTH_SHORT)
                                    .show();
                            return true;
                        }

                        Pokeball pokeball = new Pokeball();
                        if (mPokeballNumber > -1) {
                            pokeball = Pokeballs.getPokeballsInstance().get(mPokeballNumber);

                            //handle scenario when different family
                            for (int i = 0; i < pokeball.size(); i++) {
                                if (!(pokeball.get(i).getPokemonFamily().equals(mPokemon.getPokemonFamily()))) {
                                    Toast.makeText(getActivity(), "These Pokemon are from different families!", Toast.LENGTH_SHORT)
                                            .show();
                                    return true;
                                }
                            }

                            //handle scenario when different evolution paths (need to change code later on to handle Wurmple)
                            for (int i = 0; i < pokeball.size(); i++) {
                                if ((pokeball.get(i).getEvolutionTier() == mPokemon.getEvolutionTier() && (pokeball.get(i).getPokemonNumber() != mPokemon.getPokemonNumber()))) {
                                    Toast.makeText(getActivity(), "These Pokemon are from different evolution paths!", Toast.LENGTH_SHORT)
                                            .show();
                                    return true;
                                }
                            }
                        }

                        //if ADDING NEW POKEMON
                        if (mPokeballNumber > -1 && mPokeballListNumber == -1) {
                            //handle scenario when already added pokemon
                            for (int i = 0; i < pokeball.size(); i++) {
                                if (pokeball.get(i).customEquals(mPokemon)) {
                                    Toast.makeText(getActivity(), "You have added the same snapshot before!", Toast.LENGTH_SHORT)
                                            .show();
                                    return true;
                                }
                            }

                            mPokemon.setNickname(pokeball.getNickname());
                            Pokeballs.getPokeballsInstance().get(mPokeballNumber).add(mPokemon);

                            int highestTier = 0;
                            int highestEvolved = 0;
                            for (int i = 0; i < Pokeballs.getPokeballsInstance().get(mPokeballNumber).size(); i++) {
                                int tier = Pokeballs.getPokeballsInstance().get(mPokeballNumber).get(i).getEvolutionTier();
                                if (tier > highestTier) {
                                    highestTier = tier;
                                    highestEvolved = i;
                                }
                            }
                            Pokeballs.getPokeballsInstance().get(mPokeballNumber).setHighestEvolvedPokemonNumber(Pokeballs.getPokeballsInstance().get(mPokeballNumber).get(highestEvolved).getPokemonNumber());

                            Toast.makeText(getActivity(), "Added Pokemon!", Toast.LENGTH_SHORT)
                                    .show();

                            //async was causing the adapter to not update when going back to view pookeball fragment, so have to do series of callbacks
                            //new pokemon already added to Pokeballs instance, so will be the last one
                            getContract().saveButtonPressed(mPokeballNumber, (Pokeballs.getPokeballsInstance().get(mPokeballNumber).size() - 1), mPokemon);
                            return true;

                        } else if (mPokeballNumber == -1)
                        //ADDING NEW POKEBALL
                        {
                            pokeball.setNickname(mPokemon.getPokemonName());
                            pokeball.setHighestEvolvedPokemonNumber(mPokemon.getPokemonNumber());
                            pokeball.add(mPokemon);

                            Pokeballs.getPokeballsInstance().add(pokeball);

                            Toast.makeText(getActivity(), "Added Pokemon!", Toast.LENGTH_SHORT)
                                    .show();

                            getContract().saveButtonPressed(Pokeballs.getPokeballsInstance().size()-1, 0, mPokemon);
                            return true;

                        } else
                        //if EDITING
                        {

                            //handle scenario when already added pokemon
                            for (int i = 0; i < pokeball.size(); i++) {

                                if (i == mPokeballListNumber) {
                                } else if (pokeball.get(i).customEquals(mPokemon)) {
                                    Toast.makeText(getActivity(), "You have added the same Pokemon before!", Toast.LENGTH_SHORT)
                                            .show();
                                    return true;
                                }
                            }

                            //handling scenario where editing but haven't made a change
                            if (mCalculateButtonPressed == 0) {
                                Toast.makeText(getActivity(), "You need to recalculate before saving changes.", Toast.LENGTH_SHORT)
                                        .show();
                                return true;
                            }

                            //set nickname for pokeball
                            mPokemon.setNickname(pokeball.getNickname());
                            Pokeballs.getPokeballsInstance().get(mPokeballNumber).set(mPokeballListNumber, mPokemon);

                            //set pokeball with highest evolved pokemon number
                            int highestTier = 0;
                            int highestEvolved = 0;
                            for (int i = 0; i < Pokeballs.getPokeballsInstance().get(mPokeballNumber).size(); i++) {
                                int tier = Pokeballs.getPokeballsInstance().get(mPokeballNumber).get(i).getEvolutionTier();
                                if (tier > highestTier) {
                                    highestTier = tier;
                                    highestEvolved = i;
                                }
                            }
                            Pokeballs.getPokeballsInstance().get(mPokeballNumber).setHighestEvolvedPokemonNumber(Pokeballs.getPokeballsInstance().get(mPokeballNumber).get(highestEvolved).getPokemonNumber());

                            Toast.makeText(getActivity(), "Edited Pokemon!", Toast.LENGTH_SHORT)
                                    .show();


                            //just replace pokemon at the list number
                            getContract().saveButtonPressed(mPokeballNumber, mPokeballListNumber, mPokemon);

                            return true;
                        }
                    }
                    return false;
                }
            });
        }


        //finding views
        mCPInput = (EditText) v.findViewById(R.id.edit_enter_cp);
        mHPInput = (EditText) v.findViewById(R.id.edit_enter_hp);
        mStarDustInput = (EditText) v.findViewById(R.id.edit_enter_stardust);
        mLevelInput = (EditText) v.findViewById(R.id.edit_enter_known_level);
        mNotFreshMeatInput = (CheckBox) v.findViewById(R.id.edit_checkbox_powerup);

        mPokemonImage = (ImageView) v.findViewById(R.id.edit_iv_calc_pokemon_image);
        mAverageIVPercent = (TextView) v.findViewById(R.id.edit_iv_calc_ivpercent_text);
        mAverageIVPercentDesc = (TextView) v.findViewById(R.id.edit_iv_calc_ivpercent_text_desc);
        mAverageCPPercent = (TextView) v.findViewById(R.id.edit_iv_calc_cppercent_text);
        mAverageCPPercentDesc = (TextView) v.findViewById(R.id.edit_iv_calc_cppercent_text_desc);
        mDF = new DecimalFormat("0.0");

        //Autocomplete textview setup
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, Pokemon.getPokedex());
        mPokemonNameInput = (AutoCompleteTextView) v.findViewById(R.id.edit_enter_pokemon_name);
        mPokemonNameInput.setAdapter(adapter);


        if (mPokemon != null) {
            mPokemonNameInput.setText(mPokemon.getPokemonName());
            mCPInput.setText(mPokemon.getCP() + "");
            if (mPokemon.getHP() > 0) {
                mHPInput.setText(mPokemon.getHP() + "");
            }
            if (mPokemon.getStardust() > 0) {
                mStarDustInput.setText(mPokemon.getStardust() + "");
            }
            if (mPokemon.getKnownLevel() > 0) {
                mLevelInput.setText(mPokemon.getKnownLevel() + "");
            }
            mNotFreshMeatInput.setChecked(!mPokemon.getFreshMeat());

            ArrayList<Integer> tempLevelRange = mPokemon.getResultLevelRange();
            //ArrayList<Double> tempCpRange = Pokemon.getCpPercentRangeFromIVS(mPokemon.getIVCombinationsArray(), mPokemon.getPokemonNumber());
            int lowestLevel = Collections.min(tempLevelRange);
            int highestLevel = Collections.max(tempLevelRange);
            mPokemonImage.setImageResource(getResources().getIdentifier(Pokemon.getPngFileName(mPokemon.getPokemonNumber()), "drawable", getActivity().getPackageName()));
            mAverageIVPercent.setText((int) mPokemon.getAverageIVPercent() + "%");
            mAverageCPPercent.setText((int) mPokemon.getAverageCPPercent() + "%");
            mAverageIVPercentDesc.setText("(" + mDF.format(Collections.min(mPokemon.getIVPercentRange())) + " - " + mDF.format(Collections.max(mPokemon.getIVPercentRange())) + "%)\n" + "Level " + mDF.format((lowestLevel+1)/2.0) + "-" + mDF.format((highestLevel+1)/2.0) + "\n");
            mAverageCPPercentDesc.setText("(" + mDF.format(Collections.min(mPokemon.getCPPercentRange())) + " - " + mDF.format(Collections.max(mPokemon.getCPPercentRange())) + "%)\n" + "Worst CP " + (int) (Pokemon.calculateMinCPAtLevel(mPokemon.getPokemonNumber(), lowestLevel)) + "-" + (int) (Pokemon.calculateMinCPAtLevel(mPokemon.getPokemonNumber(), highestLevel)) + "\nPerfect CP " + (int) (Pokemon.calculateMaxCPAtLevel(mPokemon.getPokemonNumber(), lowestLevel)) + "-" + (int) (Pokemon.calculateMaxCPAtLevel(mPokemon.getPokemonNumber(), highestLevel)));

        }

        //calculate button setup
        mCalculateButton = (Button) v.findViewById(R.id.edit_calculate_button);
        mCalculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStringBuilder = new StringBuilder();
                mStringBuilder.append("");

                //hides keyboard
                InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow((null == getActivity().getCurrentFocus()) ? null : getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);


                try {
                    createPokemonFromInput();
                } catch (Exception e) {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT)
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
                    mAverageIVPercentDesc.setText("(" + mDF.format(Collections.min(mPokemon.getIVPercentRange())) + " - " + mDF.format(Collections.max(mPokemon.getIVPercentRange())) + "%)\n" + "Level " + lowestLevel + "-" + highestLevel + "\n");
                    mAverageCPPercentDesc.setText("(" + mDF.format(Collections.min(mPokemon.getCPPercentRange())) + " - " + mDF.format(Collections.max(mPokemon.getCPPercentRange())) + "%)\n" + "Worst CP " + (int) (Pokemon.calculateMinCPAtLevel(mPokemon.getPokemonNumber(), lowestLevel)) + "-" + (int) (Pokemon.calculateMinCPAtLevel(mPokemon.getPokemonNumber(), highestLevel)) + "\nPerfect CP " + (int) (Pokemon.calculateMaxCPAtLevel(mPokemon.getPokemonNumber(), lowestLevel)) + "-" + (int) (Pokemon.calculateMaxCPAtLevel(mPokemon.getPokemonNumber(), highestLevel)));
                    Toast.makeText(getActivity(), "Calculated!", Toast.LENGTH_SHORT)
                            .show();
                }
                if (mPokemon != null && mPokemon.getNumberOfResults() == 0) {
                    Toast.makeText(getActivity(), "No combinations found!", Toast.LENGTH_SHORT)
                            .show();
                }
                mCalculateButtonPressed += 1;
            }
        });


        //set up more info button
        mMoreInfoButton = (Button) v.findViewById(R.id.edit_iv_calc_more_info_button);
        mMoreInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mPokemon == null) {
                    Toast.makeText(getActivity(), "You have not calculated a Pokemon yet", Toast.LENGTH_SHORT)
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

            } else if (textInput == mHPInput) {

            }
            return -1;
        } else {
            try {
                number = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                throw new NumberFormatException("You have to enter whole numbers for CP/HP/Dust.");
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

        void moreInfoButtonPressed(Pokemon pokemon);

        void saveButtonPressed(int pokeballNumber, int pokeballListNumber, Pokemon pokemon);
    }

    public void setPokemon(Pokemon pokemon) {
        mPokemon = pokemon;
    }


}