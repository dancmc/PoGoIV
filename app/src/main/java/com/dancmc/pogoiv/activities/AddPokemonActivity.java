package com.dancmc.pogoiv.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dancmc.pogoiv.fragments.CompareSummaryFragment;
import com.dancmc.pogoiv.utilities.Pokeball;
import com.dancmc.pogoiv.utilities.Pokeballs;
import com.dancmc.pogoiv.database.PokeballsDataSource;
import com.dancmc.pogoiv.fragments.PokeboxFragment;
import com.dancmc.pogoiv.utilities.Pokemon;
import com.dancmc.pogoiv.R;
import com.dancmc.pogoiv.fragments.ViewPokeballFragment;
import com.dancmc.pogoiv.fragments.EditPokemonFragment;

import java.util.ArrayList;

public class AddPokemonActivity extends AppCompatActivity implements PokeboxFragment.Contract, ViewPokeballFragment.Contract, EditPokemonFragment.Contract {

    public static final String EXTRA = "APA_Pokemon";
    private static final String TAG = "AddPokemonActivity";
    private ImageView mAddCardImage;
    private TextView mAddCardCP;
    private TextView mAddCardHP;
    private TextView mAddCardDust;
    private TextView mAddCardIV;
    private ImageButton mAddCardCancelButton;
    private TextView mTextPrompt;

    private Pokemon mPokemonToAdd;
    private PokeballsDataSource mDataSource;

    private PokeboxFragment mPokeboxFragment;
    private ViewPokeballFragment mViewPokeballFragment;
    private EditPokemonFragment mEditPokemonFragment;
    private CompareSummaryFragment mCompareSummaryFragment;
    private SaveAsyncTask mSaveAsyncTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pokemon);

        mPokemonToAdd = (Pokemon) getIntent().getSerializableExtra(EXTRA);

        mDataSource = new PokeballsDataSource(this);

        mAddCardImage = (ImageView) findViewById(R.id.add_pokemon_card_image);
        mAddCardCP = (TextView) findViewById(R.id.add_pokemon_card_cp);
        mAddCardHP = (TextView) findViewById(R.id.add_pokemon_card_hp);
        mAddCardDust = (TextView) findViewById(R.id.add_pokemon_card_stardust);
        mAddCardIV = (TextView) findViewById(R.id.add_pokemon_card_IV);
        mAddCardCancelButton = (ImageButton) findViewById(R.id.add_pokemon_card_cancel);
        mTextPrompt = (TextView) findViewById(R.id.add_activity_text_prompt);


        //setting up persistent Add Card at the top of screen
        mAddCardImage.setImageResource(getResources().getIdentifier(Pokemon.getPngFileName(mPokemonToAdd.getPokemonNumber()), "drawable", getPackageName()));
        if (mPokemonToAdd.getCP() > 0) {
            mAddCardCP.setText("" + mPokemonToAdd.getCP());
        } else {
            mAddCardCP.setText("nil");
        }
        if (mPokemonToAdd.getHP() > 0) {
            mAddCardHP.setText("" + mPokemonToAdd.getHP());
        } else {
            mAddCardHP.setText("nil");
        }
        if (mPokemonToAdd.getStardust() > 0) {
            mAddCardDust.setText("" + mPokemonToAdd.getStardust());
        } else {
            mAddCardDust.setText("nil");
        }
        if (mPokemonToAdd.getAverageIVPercent() > 0) {
            mAddCardIV.setText((int) mPokemonToAdd.getAverageIVPercent() + "%");
        } else {
            mAddCardIV.setText("nil");
        }
        mAddCardCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddPokemonActivity.this, MainActivity.class));
                //TODO : ?add a confirmation dialog?
            }
        });

        //and then start the ball rolling with the PokeboxFragment
        if (getSupportFragmentManager().findFragmentById(R.id.add_activity_fragment_container) == null) {
            mPokeboxFragment = new PokeboxFragment();

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.add_activity_fragment_container, mPokeboxFragment)
                    .commit();
        }


    }

    @Override
    public void selectedPokeball(int position) {

        mViewPokeballFragment = ViewPokeballFragment.newInstance(position);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.add_activity_fragment_container, mViewPokeballFragment).addToBackStack(null)
                .commit();
        mTextPrompt.setText("Press the + button to add to this Pokeball.");

    }

    @Override
    public void addNewPokeball() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Snapshot")
                .setMessage("Do you want to add this snapshot to a new Pokeball?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {

                        Pokeball newPokeball = new Pokeball();
                        newPokeball.add(mPokemonToAdd);
                        newPokeball.setNickname(mPokemonToAdd.getPokemonName());
                        newPokeball.setHighestEvolvedPokemonNumber(mPokemonToAdd.getPokemonNumber());

                        Pokeballs.getPokeballsInstance().add(newPokeball);

                        new AsyncTask<Integer, Void, Void>() {
                            @Override
                            protected Void doInBackground(Integer... params) {
                                mDataSource.setPokemonData(mPokemonToAdd, (Pokeballs.getPokeballsInstance().size() - 1), 0);
                                return null;
                            }
                        }.execute();
                        startActivity(new Intent(AddPokemonActivity.this, MainActivity.class));
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


    //the position is the number of the pokeball in the singleton
    @Override
    public void addToExistingPokeball(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Snapshot")
                .setMessage("Do you want to add this Snapshot to this existing Pokeball?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        Pokeball pokeball = Pokeballs.getPokeballsInstance().get(position);

                        //null & no combis already handled in calc fragment
                        //handle scenario when already added pokemon
                        for (int i = 0; i < pokeball.size(); i++) {
                            if (pokeball.get(i).customEquals(mPokemonToAdd)) {
                                Toast.makeText(AddPokemonActivity.this, "You have added the same snapshot before!", Toast.LENGTH_LONG)
                                        .show();
                                return;
                            }
                        }

                        //handle scenario when different family
                        for (int i = 0; i < pokeball.size(); i++) {
                            if (!(pokeball.get(i).getPokemonFamily().equals(mPokemonToAdd.getPokemonFamily()))) {
                                Log.d(TAG, "onClick: " + pokeball.get(i).getPokemonFamily() + " " + mPokemonToAdd.getPokemonFamily());
                                Toast.makeText(AddPokemonActivity.this, "These Pokemon are from different families!", Toast.LENGTH_LONG)
                                        .show();
                                return;
                            }
                        }

                        //handle scenario when different evolution paths (need to change code later on to handle Wurmple)
                        for (int i = 0; i < pokeball.size(); i++) {
                            if ((pokeball.get(i).getEvolutionTier() == mPokemonToAdd.getEvolutionTier() && (pokeball.get(i).getPokemonNumber() != mPokemonToAdd.getPokemonNumber()))) {
                                Toast.makeText(AddPokemonActivity.this, "These Pokemon are from different evolution paths!", Toast.LENGTH_LONG)
                                        .show();
                                return;
                            }
                        }


                        mPokemonToAdd.setNickname(pokeball.getNickname());
                        Pokeballs.getPokeballsInstance().get(position).add(mPokemonToAdd);

                        int highestTier = 0;
                        int highestEvolved = 0;
                        for (int i = 0; i < Pokeballs.getPokeballsInstance().get(position).size(); i++) {
                            int tier = Pokeballs.getPokeballsInstance().get(position).get(i).getEvolutionTier();
                            if (tier > highestTier) {
                                highestTier = tier;
                                highestEvolved = i;
                            }
                        }
                        Pokeballs.getPokeballsInstance().get(position).setHighestEvolvedPokemonNumber(Pokeballs.getPokeballsInstance().get(position).get(highestEvolved).getPokemonNumber());

                        new AsyncTask<Integer, Void, Void>() {
                            @Override
                            protected Void doInBackground(Integer... params) {
                                mDataSource.setPokemonData(mPokemonToAdd, position, Pokeballs.getPokeballsInstance().get(position).size() - 1);
                                return null;
                            }
                        }.execute();
                        startActivity(new Intent(AddPokemonActivity.this, MainActivity.class));

                        //goes back to IV calc, so no need to refresh view
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


    @Override
    public void onBackPressed() {
        //clean up extra pokeball
        if (getSupportFragmentManager().findFragmentById(R.id.add_activity_fragment_container) instanceof PokeboxFragment) {
            finish();
        } else {
            getSupportFragmentManager().popBackStack();
        }
        //TODO : are you sure you want to exit

    }

    @Override
    public void onViewSummaryClick(Pokemon pokemon, ArrayList<double[]> ivCombos) {
        mCompareSummaryFragment = CompareSummaryFragment.newInstance(pokemon, ivCombos, false);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.add_activity_fragment_container, mCompareSummaryFragment).addToBackStack(null)
                .commit();
    }


    @Override
    public void onDeleteLastPokemon() {
        /*if (getSupportFragmentManager().findFragmentById(R.id.add_activity_fragment_container) instanceof ViewPokeballFragment) {
            if (mPokeboxFragment == null) {
                mPokeboxFragment = new PokeboxFragment();
            }
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.add_activity_fragment_container, mPokeboxFragment)
                    .commit();
        }*/
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void editPokemon(int pokeballNumber, int pokeballListNumber) {
        mEditPokemonFragment = EditPokemonFragment.newInstance(pokeballNumber, pokeballListNumber);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.add_activity_fragment_container, mEditPokemonFragment).addToBackStack(null)
                .commit();

    }

    @Override
    public void moreInfoButtonPressed(Pokemon pokemon) {

        mCompareSummaryFragment = CompareSummaryFragment.newInstance(pokemon, pokemon.getIVCombinationsArray(), true);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.add_activity_fragment_container, mCompareSummaryFragment).addToBackStack(null)
                .commit();

    }

    @Override
    public void saveButtonPressed(int pokeballNumber, int pokeballListNumber, Pokemon pokemon) {
        mSaveAsyncTask = new SaveAsyncTask(pokemon, pokeballNumber, pokeballListNumber, this);
        mSaveAsyncTask.execute();
        onBackPressed();
    }


    public class SaveAsyncTask extends AsyncTask<Void, Void, Void> {
        private PokeballsDataSource mDataSource;
        Pokemon mPokemon;
        int mPokeballNumber;
        int mPokeballListNumber;

        public SaveAsyncTask(Pokemon pokemon, int pokeballNumber, int pokeballListNumber, Context context) {
            mPokemon = pokemon;
            mPokeballNumber = pokeballNumber;
            mPokeballListNumber = pokeballListNumber;
            mDataSource = new PokeballsDataSource(context);
        }

        @Override
        protected Void doInBackground(Void... params) {
            mDataSource.setPokemonData(mPokemon, mPokeballNumber, mPokeballListNumber);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (getSupportFragmentManager().findFragmentById(R.id.main_fragment_container) instanceof ViewPokeballFragment) {
                ((ViewPokeballFragment) getSupportFragmentManager().findFragmentById(R.id.main_fragment_container)).editAsyncFinished();

            }
        }

    }

    public SaveAsyncTask.Status getSaveAsyncStatus() {
        if (mSaveAsyncTask != null) {
            return mSaveAsyncTask.getStatus();
        }
        return null;
    }
}
