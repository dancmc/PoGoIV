package com.dancmc.pogoiv;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    private AutoCompleteTextView mPokemonNameInput;
    private Button mCalculateButton;
    private Button mRecalculateButton;
    private Button mFirstPokemon;
    private Button mSecondPokemon;
    private TextView mOutputView;
    private Pokemon mPokemon;
    private StringBuilder mStringBuilder;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Autocomplete textview setup
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, Pokemon.POKEDEX);
        mPokemonNameInput = (AutoCompleteTextView) findViewById(R.id.enter_pokemon_name);
        mPokemonNameInput.setAdapter(adapter);
        mOutputView = (TextView) findViewById(R.id.output);

        mCalculateButton = (Button) findViewById(R.id.calculate_button);
        mCalculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStringBuilder = new StringBuilder();

                //hides keyboard
                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow((null == getCurrentFocus()) ? null : getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                try {
                    createPokemonFromInput();
                } catch (Exception e) {
                    mOutputView.setText(e.toString());
                    return;
                }



                mStringBuilder.append("S");
//mResult.add("Level " + mLevelHolding + " : " + mStaHolding + "/" + mAtkHolding + "/" + mDefHolding + "   " + String.format(Locale.US,"%.1f", percent)+ "%");
                //*delete*mResult.add(3, "Estimated average power " +String.format(Locale.US,"%.1f", average)+ "%, range is "+String.format(Locale.US,"%.1f", mIVComboRanking.get(0)) + "% to " +String.format(Locale.US,"%.1f", mIVComboRanking.get(mIVComboRanking.size()-1)) + "%, "+ mNumberOfResults+ " possible combinations.\n");

                mFirstPokemon.setText("1st : " + mPokemonName);
                mSecondPokemon.setText("2nd : None");


            }
        });

        //Second comparison pokemon
        mRecalculateButton = (Button) findViewById(R.id.second_calculate_button);
        mRecalculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                buttonPressedSetup();
                mPokemon = new Pokemon(mPokemonName, mHP, mCP, mStardust, false);




                //hides keyboard
                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow((null == getCurrentFocus()) ? null : getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });

    }

    //reads the input from the EditText views and calls Pokemon constructor
    private void createPokemonFromInput() {
        String pokemonName = "" + mPokemonNameInput.getText().toString();
        int cp = parseIntInput(R.id.enter_cp);
        int hp = parseIntInput(R.id.enter_hp);
        int stardust = parseIntInput(R.id.enter_stardust);
        boolean freshMeat = ((CheckBox) findViewById(R.id.checkbox_powerup)).isChecked();

        //throws exception if invalid/blank pokemon name, or invalid stardust
        //if HP/CP/Stardust blank, is ok, passes in a -1
        try {
            mPokemon = new Pokemon(pokemonName, hp, cp, stardust, freshMeat);
        } catch (IllegalArgumentException e) {
            throw e;
        }

    }

    //returns the integer in an number EditText, or if blank, returns 0
    private int parseIntInput(int editTextID) {
        int number;
        String input = ((EditText)findViewById(editTextID)).getText().toString();
        if (input.equals("")) {
            switch(editTextID){
                case (R.id.enter_cp) : throw new IllegalArgumentException("You must enter a CP value.");
                case (R.id.enter_hp) : mStringBuilder.append("Note : You did not enter a HP value. All values calculated\n");
                case (R.id.enter_stardust) : mStringBuilder.append("Note : You did not enter a stardust value. All levels calculated.\n");
            }
            return -1;
        } else {
            try {
                number = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                throw new NumberFormatException("You have to enter whole numbers.");
            }
            if(number<0)
                throw new NumberFormatException("You have to enter positive numbers.");

        }
        return number;
    }
}
