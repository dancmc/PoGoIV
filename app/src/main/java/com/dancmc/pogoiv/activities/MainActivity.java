package com.dancmc.pogoiv.activities;

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

import com.dancmc.pogoiv.fragments.CompareSummaryFragment;
import com.dancmc.pogoiv.fragments.IVCalculatorFragment;
import com.dancmc.pogoiv.utilities.Pokeballs;
import com.dancmc.pogoiv.database.PokeballsDataSource;
import com.dancmc.pogoiv.fragments.PokeboxFragment;
import com.dancmc.pogoiv.utilities.Pokemon;
import com.dancmc.pogoiv.R;
import com.dancmc.pogoiv.fragments.ViewPokeballFragment;
import com.dancmc.pogoiv.fragments.EditPokemonFragment;

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

    private boolean mEditAsyncIsRunning;
    private SaveAsyncTask mSaveAsyncTask;

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
        mViewPokeballFragment = ViewPokeballFragment.newInstance(position);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_container, mViewPokeballFragment).addToBackStack(null)
                .commit();
    }

    @Override
    public void onViewSummaryClick(Pokemon pokemon, ArrayList<double[]> ivCombos) {
        mCompareSummaryFragment = CompareSummaryFragment.newInstance(pokemon, ivCombos, false);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_container, mCompareSummaryFragment).addToBackStack(null)
                .commit();
    }

    //merge with viewsummaryclicked
    @Override
    public void moreInfoButtonPressed(Pokemon pokemon) {

        mCompareSummaryFragment = CompareSummaryFragment.newInstance(pokemon, pokemon.getIVCombinationsArray(), true);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_container, mCompareSummaryFragment).addToBackStack(null)
                .commit();

    }

    //Moving to EditFragment to ADD pokemon
    @Override
    public void onAddFabClick(int pokeballNumber) {

        mEditPokemonFragment = EditPokemonFragment.newInstance(pokeballNumber, -1);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_container, mEditPokemonFragment).addToBackStack(null)
                .commit();
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
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void editPokemon(int pokeballNumber, int pokeballListNumber) {
        mEditPokemonFragment = EditPokemonFragment.newInstance(pokeballNumber, pokeballListNumber);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_container, mEditPokemonFragment).addToBackStack(null)
                .commit();
    }

    @Override
    public void saveButtonPressed(int pokeballNumber, int pokeballListNumber, Pokemon pokemon) {
        mSaveAsyncTask = new SaveAsyncTask(pokemon, pokeballNumber, pokeballListNumber, this);
        mSaveAsyncTask.execute();
        onBackPressed();
    }


    public SaveAsyncTask.Status getSaveAsyncStatus() {
        if (mSaveAsyncTask != null) {
            return mSaveAsyncTask.getStatus();
        }
        return null;
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

}
