package com.dancmc.pogoiv;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private AutoCompleteTextView mPokemonNameInput;
    private TextView mOutputView;
    private Pokemon mPokemon;
    private StringBuilder mStringBuilder;


    private ArrayList<Pokemon> mPokeballs;
    private final static String POKEBALL_ARRAY = "pokeball_array";

    private Button mCalculateButton;
    private Button mCompareButton;
    private ImageButton mAddButton;

    private PokeballsDataSource mDataSource;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Autocomplete textview setup
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, Pokemon.POKEDEX);
        mPokemonNameInput = (AutoCompleteTextView) findViewById(R.id.enter_pokemon_name);
        mPokemonNameInput.setAdapter(adapter);
        mOutputView = (TextView) findViewById(R.id.output);

        /*restore saved instance & pokeballs setup
        if (savedInstanceState != null) {
            mPokeballs = (ArrayList<Pokemon>) savedInstanceState.getSerializable(POKEBALL_ARRAY);
            for (int i = 1; i < mPokeballs.size() - 1; i++) {
                if (mPokeballs.get(i) != null)
                    buildPokeball(i);
            }
        } else {
            mPokeballs = new ArrayList<Pokemon>(Arrays.asList(new Pokemon[]{null, null, null, null, null, null, null, null}));
        }
        */

        //Retrieves data from database, populates Array & builds pokeballs
        mDataSource = new PokeballsDataSource(this);
        mPokeballs = mDataSource.getAllPokeballs();
        for (int i = 1; i < mPokeballs.size() - 1; i++) {

            if (mPokeballs.get(i) != null){
                Log.d(TAG, "onCreate: "+mPokeballs.get(i).toString());
                buildPokeball(i);}
        }

            //calculate button setup
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
                        mOutputView.setText(e.getMessage());
                        return;
                    }


                    if (mPokemon != null) {
                        mStringBuilder.append(mPokemon.getStringOutput());
                    }
                    mOutputView.setText(mStringBuilder.toString());

                }
            });

            //Add button setup
            mAddButton = (ImageButton) findViewById(R.id.fab);
            mAddButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Add Pokemon")
                            .setMessage("Do you want to add the current Pokemon to your list?")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(final DialogInterface dialog, int which) {
                                    //check whether pokemon has been added to any of the 6 pokeballs already
                                    for (int i = 1; i <= mPokeballs.size() - 1; i++) {
                                        if (mPokeballs.get(i) != null && mPokeballs.get(i).customEquals(mPokemon)) {
                                            Toast.makeText(MainActivity.this, "You have already added this Pokemon!", Toast.LENGTH_LONG)
                                                    .show();
                                            return;
                                        }
                                    }

                                    //reject adding pokemon with no combinations
                                    if (mPokemon.getNumberOfResults()==0){
                                        Toast.makeText(MainActivity.this, "Sorry, you can't add Pokemon with no combinations.", Toast.LENGTH_LONG)
                                                .show();
                                        return;
                                    }

                                    for (int i = 1; i <= mPokeballs.size() - 1; i++) {
                                        if (i == 7) {
                                            Toast.makeText(MainActivity.this, "You have no more space", Toast.LENGTH_LONG)
                                                    .show();
                                            return;
                                        } else if (mPokemon == null) {
                                            Toast.makeText(MainActivity.this, "You have not calculated a Pokemon yet", Toast.LENGTH_LONG)
                                                    .show();
                                            return;

                                        } else if (mPokeballs.get(i) == null) {
                                            mPokeballs.set(i, mPokemon);
                                            buildPokeball(i);
                                            return;
                                        }
                                    }
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });

            //compare button setup
            mCompareButton = (Button) findViewById(R.id.compare_button);
            mCompareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String s = mDataSource.compareAllPokeballs(mPokeballs);
                    mOutputView.setText(s);

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
        int level = parseIntInput(R.id.enter_known_level);
        boolean freshMeat = ((CheckBox) findViewById(R.id.checkbox_powerup)).isChecked();

        //throws exception if invalid/blank pokemon name, or invalid stardust
        //if HP/CP/Stardust blank, is ok, passes in a -1
        try {
            mPokemon = new Pokemon(pokemonName, hp, cp, stardust, freshMeat, level);
        } catch (IllegalArgumentException e) {
            throw e;
        }

    }

    //returns the integer in an number EditText, or if blank, returns 0
    private int parseIntInput(int editTextID) {
        int number=0;
        String input = ((EditText) findViewById(editTextID)).getText().toString();
        if (input.equals("")) {
            switch (editTextID) {
                case (R.id.enter_cp):
                    throw new IllegalArgumentException("You must enter a CP value.");
                case (R.id.enter_stardust):
                    mStringBuilder.append("Note : You did not enter a stardust value. All levels calculated.\n\n");
                    return -1;
                case (R.id.enter_hp):
                    mStringBuilder.append("Note : You did not enter a HP value. All values calculated\n\n");
                    return -1;
            }
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

    //build a pokeball based on a position in the global ArrayList<Pokemon>
    private void buildPokeball(final int i) {
        //find the correct ImageButton
        int id = getResources().getIdentifier("pokeball_" + i, "id", getPackageName());
        final ImageButton pokeball = (ImageButton) findViewById(id);
        //set Imagebutton picture
        int id2 = getResources().getIdentifier(mPokeballs.get(i).getPngFileName(), "drawable", getPackageName());
        pokeball.setBackgroundResource(R.drawable.circle_background);
        pokeball.setImageResource(id2);
        pokeball.setScaleType(ImageView.ScaleType.CENTER_CROP);

        //update database
        mDataSource.setPokeballData(mPokeballs.get(i), i);

        pokeball.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStringBuilder = new StringBuilder(mPokeballs.get(i).getStringOutput());
                mOutputView.setText(mStringBuilder);
            }
        });

        pokeball.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder2 = new AlertDialog.Builder(MainActivity.this);
                builder2.setTitle("Delete")
                        .setMessage("Do you want to delete this Pokemon?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mPokeballs.set(i, null);
                                pokeball.setOnClickListener(null);
                                pokeball.setOnLongClickListener(null);
                                pokeball.setBackgroundResource(R.drawable.pokeball);
                                pokeball.setImageResource(0);
                                Toast.makeText(MainActivity.this, "Pokemon deleted", Toast.LENGTH_LONG)
                                        .show();
                                mDataSource.deletePokeball(i);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                AlertDialog dialog2 = builder2.create();
                dialog2.show();
                return true;
            }
        });

        Toast.makeText(MainActivity.this, "Added to Pokeball " + i, Toast.LENGTH_LONG)
                .show();
        Log.d(TAG, "onClick: Added.");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(POKEBALL_ARRAY, mPokeballs);
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mPokeballs = (ArrayList<Pokemon>) savedInstanceState.getSerializable(POKEBALL_ARRAY);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
