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
    private String mPokemonName;
    private int mHP;
    private int mCP;
    private int mStardust;
    private boolean mFreshMeat;

    private AutoCompleteTextView mPokemonNameInput;
    private Button mCalculateButton;
    private Button mRecalculateButton;
    private Button mFirstPokemon;
    private Button mSecondPokemon;
    private TextView mOutputView;
    private Pokemon mPokemon;
    private ArrayList<String> mFirstOutput;
    private ArrayList<String> mSecondOutput;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirstPokemon = (Button)findViewById(R.id.first_pokemon);
        mSecondPokemon = (Button)findViewById(R.id.second_pokemon);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, Pokemon.POKEDEX);
        mPokemonNameInput = (AutoCompleteTextView) findViewById(R.id.enter_pokemon_name);
        mPokemonNameInput.setAdapter(adapter);
        mOutputView = (TextView) findViewById(R.id.output);

        //First pokemon
        mCalculateButton = (Button) findViewById(R.id.calculate_button);
        mCalculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                buttonPressedSetup();
                mPokemon = new Pokemon(mPokemonName, mHP, mCP, mStardust, mFreshMeat);

                StringBuilder stringBuilder = new StringBuilder();
                mFirstOutput = mPokemon.ivCombos();
                for (int i = 0; i < mFirstOutput.size(); i++) {
                    stringBuilder.append(mFirstOutput.get(i) + "\n");
                }

                mOutputView.setText(stringBuilder.toString());
                mFirstPokemon.setText("1st : " + mPokemonName);
                mSecondPokemon.setText("2nd : None");

                //hides keyboard
                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow((null == getCurrentFocus()) ? null : getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });

        //Second comparison pokemon
        mRecalculateButton = (Button) findViewById(R.id.second_calculate_button);
        mRecalculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                buttonPressedSetup();
                mPokemon = new Pokemon(mPokemonName, mHP, mCP, mStardust, false);

                StringBuilder stringBuilder = new StringBuilder();
                mSecondOutput = mPokemon.ivCombos();

                if (mFirstOutput.get(0).equals(mSecondOutput.get(0))) {
                    stringBuilder.append(mFirstOutput.get(0) + "\n\n");
                    stringBuilder.append("First Pokemon " + mFirstOutput.get(1) + "\n");
                    stringBuilder.append("Second Pokemon " + mSecondOutput.get(1) + "\n\nComparison Matches :\n");
                    for (int i = 4; i < mFirstOutput.size(); i++) {
                        Log.d(TAG, "onClick: " + i);
                        for (int j = 4; j < mSecondOutput.size(); j++) {
                            if (mFirstOutput.get(i).substring(9).equals(mSecondOutput.get(j).substring(9))) {
                                stringBuilder.append(mFirstOutput.get(i).substring(0, 8).trim() + " | " + mSecondOutput.get(j)+"\n");
                            }
                        }
                    }
                } else mOutputView.setText("You are comparing different Pokemon!");

                mOutputView.setText(stringBuilder.toString());
                mSecondPokemon.setText("2nd : " +mPokemonName);

                //hides keyboard
                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow((null == getCurrentFocus()) ? null : getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });

    }

    private void buttonPressedSetup() {
        mHP = parseInput((EditText) findViewById(R.id.enter_hp));
        mCP = parseInput((EditText) findViewById(R.id.enter_cp));
        mStardust = parseInput((EditText) findViewById(R.id.enter_stardust));
        mFreshMeat = ((CheckBox) findViewById(R.id.checkbox_powerup)).isChecked();

        if (mPokemonNameInput.getText() == null || mHP == 0 || mCP == 0 || mStardust == 0) {
            mOutputView.setText("You did not fill in a field.");
            return;
        }

        mPokemonName = mPokemonNameInput.getText().toString();
    }

    private int parseInput(EditText editText) {
        int number = 0;
        if (editText.getText().toString().equals("")) {
            mOutputView.setText("You did not enter a field.");
            return number;
        } else {
            number = Integer.parseInt(editText.getText().toString());
        }
        return number;
    }
}
