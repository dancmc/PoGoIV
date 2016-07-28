package com.dancmc.pogoiv;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import android.widget.TextView;
import android.widget.Toast;


public class IVCalculatorFragment extends ContractFragment<IVCalculatorFragment.Contract>{

    //Views and utility stuff
    private AutoCompleteTextView mPokemonNameInput;
    private TextView mOutputView;
    private EditText mCPInput;
    private EditText mHPInput;
    private EditText mStarDustInput;
    private EditText mLevelInput;
    private CheckBox mFreshMeatInput;
    private StringBuilder mStringBuilder;
    private Pokemon mPokemon;

    //Buttons
    private Button mCalculateButton;
    private Button mPokeboxButton;
    private ImageButton mAddButton;

    private PokeballsDataSource mDataSource;

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
        mFreshMeatInput = (CheckBox) v.findViewById(R.id.checkbox_powerup);
        mOutputView = (TextView) v.findViewById(R.id.output);

        //Autocomplete textview setup
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, Pokemon.getPokedex());
        mPokemonNameInput = (AutoCompleteTextView) v.findViewById(R.id.enter_pokemon_name);
        mPokemonNameInput.setAdapter(adapter);

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
                    return;
                }


                if (mPokemon != null) {
                    mStringBuilder.append(mPokemon.getStringOutput());
                }
                mOutputView.setText(mStringBuilder.toString());
                mFreshMeatInput.setChecked(false);
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
        mAddButton = (ImageButton)v.findViewById(R.id.add_button);
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPokemon.getNumberOfResults() == 0) {
                    Toast.makeText(getActivity(), "Sorry, you can't add Pokemon with no combinations.", Toast.LENGTH_LONG)
                            .show();
                    return;
                }
                if (mPokemon == null) {
                    Toast.makeText(getActivity(), "You have not calculated a Pokemon yet", Toast.LENGTH_LONG)
                            .show();
                    return;
                }
                getContract().addButtonPressed(mPokemon);
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
        boolean freshMeat = mFreshMeatInput.isChecked();

        //throws exception if invalid/blank pokemon name, or invalid stardust
        //if HP/CP/Stardust blank, is ok, passes in a -1
        try {
            mPokemon = new Pokemon(pokemonName, hp, cp, stardust, freshMeat, level);
        } catch (IllegalArgumentException e) {
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
    }

}
