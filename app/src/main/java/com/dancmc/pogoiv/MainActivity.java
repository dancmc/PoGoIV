package com.dancmc.pogoiv;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements IVCalculatorFragment.Contract, PokeboxFragment.Contract, ViewPokeballFragment.Contract {

    private static final String TAG = "MainActivity";
    private IVCalculatorFragment mCalcFragment;
    private PokeboxFragment mPokeboxFragment;
    private ViewPokeballFragment mViewPokeballFragment;
    private CompareSummaryFragment mCompareSummaryFragment;

    private Toast mToast;
    private long mLastPressed;
    private PokeballsDataSource mDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDataSource = new PokeballsDataSource(this);

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                Pokeballs.getPokeballsInstance().addAll(mDataSource.getAllPokeballs());
                return null;
            }
        }.execute();

        //normal flow without adding : calculateIV -> pokebox -> viewpokemon
        if (getSupportFragmentManager().findFragmentById(R.id.main_fragment_container) == null) {
            mCalcFragment = new IVCalculatorFragment();

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.main_fragment_container, mCalcFragment, "calcFragment")
                    .commit();
        }


    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void addButtonPressed(Pokemon pokemon) {
        Intent i = new Intent(this, AddPokemonActivity.class);
        i.putExtra(AddPokemonActivity.EXTRA, pokemon);
        startActivity(i);
    }

    //null & no combinations handled in calculator fragment
    @Override
    public void pokeboxButtonPressed() {
        if (mPokeboxFragment == null) {
            mPokeboxFragment = new PokeboxFragment();
            Log.d(TAG, "pokeboxButtonPressed: making new pokeBoxFragment");
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_container, mPokeboxFragment)
                .commit();
    }

    @Override
    public void selectedPokeball(int position) {
        mViewPokeballFragment = new ViewPokeballFragment();
        mViewPokeballFragment.setPosition(position);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_container, mViewPokeballFragment)
                .commit();
    }

    @Override
    public void onViewSummaryClick(String s) {
        mCompareSummaryFragment = new CompareSummaryFragment();
        mCompareSummaryFragment.setText(s);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_container, mCompareSummaryFragment)
                .commit();
    }

    @Override
    public void onAddFabClick(int position) {
        //TODO : go to editpokemonfragment
    }

    /**
     * TODO : add methods for saving edited pokemon, and for adding new pokemon, based on fragment information
     * when editing, need to move from ViewPokemonFragment to EditPokemonFragment, sending the pokeball and position as an argument
     * EditPokemonFragment should be like calculator, but fixed pokemon field, and a save button - save button checks that number of results >1, and that not the same (customEquals) as any pokemon in the pokeball.
     * Also needs to clone the uniqueID and nickname
     * When saving, delete data at that pokeball/position, and resave with new pokemon object. And then move back to ViewPokemonFragment by sending the pokeball number
     * <p/>
     * When adding pokemon, also add advice to remove known Level if no combis
     */

    //TODO probably need an async loading bar for the initial boot where loading from database
    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().findFragmentById(R.id.main_fragment_container) instanceof PokeboxFragment) {
            if (mCalcFragment == null) {
                mCalcFragment = new IVCalculatorFragment();
            }
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_fragment_container, mCalcFragment)
                    .commit();
        } else if (getSupportFragmentManager().findFragmentById(R.id.main_fragment_container) instanceof ViewPokeballFragment) {
            if (mPokeboxFragment == null) {
                mPokeboxFragment = new PokeboxFragment();
            }
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_fragment_container, mPokeboxFragment)
                    .commit();
        } else if (getSupportFragmentManager().findFragmentById(R.id.main_fragment_container) instanceof CompareSummaryFragment) {
            if (mViewPokeballFragment == null) {
                mViewPokeballFragment = new ViewPokeballFragment();
            }
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_fragment_container, mViewPokeballFragment)
                    .commit();
        } else if (getSupportFragmentManager().findFragmentById(R.id.main_fragment_container) instanceof IVCalculatorFragment) {
            long currentTime = System.currentTimeMillis();
            if (mLastPressed + 4000 > currentTime) {
                mToast.cancel();
                super.onBackPressed();
            } else {
                mToast = Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_LONG);
                mToast.show();
                mLastPressed = currentTime;
            }


        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //TODO : save IVcalculator's typed state here as key pairs
    }

    //TODO :setting nicknames - need to update database
}
