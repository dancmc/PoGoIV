package com.dancmc.pogoiv;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class AddPokemonActivity extends AppCompatActivity implements PokeboxFragment.Contract {

    public static final String EXTRA = "APA_Pokemon";
    private static final String TAG = "AddPokemonActivity";
    private ImageView mAddCardImage;
    private TextView mAddCardCP;
    private TextView mAddCardHP;
    private TextView mAddCardDust;
    private TextView mAddCardIV;
    private ImageButton mAddCardCancelButton;
    private TextView mTextPrompt;

    private FloatingActionButton mFAB;

    private Pokemon mPokemonToAdd;
    private PokeballsDataSource mDataSource;

    private PokeboxFragment mPokeboxFragment;
    private ViewPokeballFragment mViewPokeballFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pokemon);

        mPokemonToAdd = (Pokemon) getIntent().getSerializableExtra(EXTRA);
        Pokeball tempPokeball = new Pokeball();
        tempPokeball.setCustomAddBall(true);
        Pokeballs.getPokeballsInstance().add(tempPokeball);

        mDataSource = new PokeballsDataSource(this);

        mAddCardImage = (ImageView) findViewById(R.id.add_pokemon_card_image);
        mAddCardCP = (TextView) findViewById(R.id.add_pokemon_card_cp);
        mAddCardHP = (TextView) findViewById(R.id.add_pokemon_card_hp);
        mAddCardDust = (TextView) findViewById(R.id.add_pokemon_card_stardust);
        mAddCardIV = (TextView) findViewById(R.id.add_pokemon_card_IV);
        mAddCardCancelButton = (ImageButton) findViewById(R.id.add_pokemon_card_cancel);
        mTextPrompt = (TextView) findViewById(R.id.add_activity_text_prompt);
        mFAB = (FloatingActionButton) findViewById(R.id.add_activity_fab);

        mFAB.setVisibility(View.INVISIBLE);
        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        mAddCardImage.setImageResource(getResources().getIdentifier(Pokemon.getPngFileName(mPokemonToAdd.getPokemonNumber()), "drawable", getPackageName()));
        if (mPokemonToAdd.getCP() > 0) {
            mAddCardCP.setText("" + mPokemonToAdd.getCP());
        } else {
            mAddCardCP.setText("nil");
        }
        if (mPokemonToAdd.getHP() > 0) {
            Log.d(TAG, "onCreate: gothere" + mPokemonToAdd.getCP());
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
                Pokeballs.getPokeballsInstance().remove(Pokeballs.getPokeballsInstance().size() - 1);
                finish();
                //TODO : ?add a confirmation dialog?
            }
        });

        if (getSupportFragmentManager().findFragmentById(R.id.add_activity_fragment_container) == null) {
            mPokeboxFragment = new PokeboxFragment();

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.add_activity_fragment_container, mPokeboxFragment)
                    .commit();
        }


    }

    @Override
    public void selectedPokeball(int position) {
        if (Pokeballs.getPokeballsInstance().get(position).isCustomAddBall()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Add Pokemon")
                    .setMessage("Do you want to add the current Pokemon to a new Pokeball?")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, int which) {

                            Pokeballs.getPokeballsInstance().remove(Pokeballs.getPokeballsInstance().size() - 1);
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
                            finish();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            mViewPokeballFragment = new ViewPokeballFragment();
            mViewPokeballFragment.setPosition(position);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.add_activity_fragment_container, mViewPokeballFragment)
                    .commit();
            mTextPrompt.setText("Press the + button to add to this Pokeball.");
            mFAB.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        //TODO : are you sure you want to exit
        Pokeballs.getPokeballsInstance().remove(Pokeballs.getPokeballsInstance().size() - 1);
        super.onBackPressed();
    }
}
