package com.dancmc.pogoiv;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements IVCalculatorFragment.Contract, PokeboxFragment.Contract, ViewPokeballFragment.Contract, EditPokemonFragment.Contract {

    private static final String TAG = "MainActivity";
    private IVCalculatorFragment mCalcFragment;
    private PokeboxFragment mPokeboxFragment;
    private ViewPokeballFragment mViewPokeballFragment;
    private CompareSummaryFragment mCompareSummaryFragment;
    private EditPokemonFragment mEditPokemonFragment;

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
                if (Pokeballs.getPokeballsInstance().size() == 0) {
                    Pokeballs.getPokeballsInstance().addAll(mDataSource.getAllPokeballs());
                }
                return null;
            }
        }.execute();

        Log.d(TAG, "onCreate: " + Pokemon.calculateCPPercentAtLevel(7, 5, 14, 9, 79) + " " + Pokemon.calculateCPPercentAtLevel(7, 5, 1, 9, 79));

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

        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_container, mPokeboxFragment).addToBackStack(null)
                .commit();
    }

    @Override
    public void selectedPokeball(int position) {
        mViewPokeballFragment = new ViewPokeballFragment();
        mViewPokeballFragment.setPosition(position);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_container, mViewPokeballFragment).addToBackStack(null)
                .commit();
    }

    @Override
    public void onViewSummaryClick(Pokemon pokemon, ArrayList<double[]> ivCombos) {
        mCompareSummaryFragment = new CompareSummaryFragment();
        mCompareSummaryFragment.setPokemon(pokemon);
        mCompareSummaryFragment.setIVCombos(ivCombos);
        mCompareSummaryFragment.isSinglePokemon(false);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_container, mCompareSummaryFragment).addToBackStack(null)
                .commit();
    }

    @Override
    public void moreInfoButtonPressed(Pokemon pokemon) {

        mCompareSummaryFragment = new CompareSummaryFragment();
        mCompareSummaryFragment.setPokemon(pokemon);
        mCompareSummaryFragment.setIVCombos(pokemon.getIVCombinationsArray());
        mCompareSummaryFragment.isSinglePokemon(true);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_container, mCompareSummaryFragment).addToBackStack(null)
                .commit();
        getSupportFragmentManager().executePendingTransactions();
    }

    //Moving to EditFragment to ADD pokemon
    @Override
    public void onAddFabClick(int position) {

        mEditPokemonFragment = EditPokemonFragment.newInstance(position,-1, null);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_container, mEditPokemonFragment)
                .commit();
        getSupportFragmentManager().executePendingTransactions();
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
        if (getSupportFragmentManager().findFragmentById(R.id.main_fragment_container) instanceof IVCalculatorFragment) {
            long currentTime = System.currentTimeMillis();
            if (mLastPressed + 4000 > currentTime) {
                mToast.cancel();
                super.onBackPressed();
            } else {
                mToast = Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_LONG);
                mToast.show();
                mLastPressed = currentTime;
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //TODO : save IVcalculator's typed state here as key pairs
    }

    //make EditTexts lose focus on click out
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public void onDeleteLastPokemon() {
        if (getSupportFragmentManager().findFragmentById(R.id.main_fragment_container) instanceof ViewPokeballFragment) {
            if (mPokeboxFragment == null) {
                mPokeboxFragment = new PokeboxFragment();
            }
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_fragment_container, mPokeboxFragment)
                    .commit();
        }
    }

    //TODO :setting nicknames - need to update database
}
