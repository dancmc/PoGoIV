package com.dancmc.pogoiv;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity implements IVCalculatorFragment.Contract,PokeboxFragment.Contract {

    private static final String TAG = "MainActivity";
    private IVCalculatorFragment mCalcFragment;
    private PokeboxFragment mPokeboxFragment;
    private ViewPokeballFragment mViewPokeballFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //normal flow without adding : calculateIV -> pokebox -> viewpokemon
        if (getSupportFragmentManager().findFragmentById(R.id.main_fragment_container) == null) {
            mCalcFragment = new IVCalculatorFragment();

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.main_fragment_container, mCalcFragment,"calcFragment")
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
        //TODO when add button in calculator pressed
    }

    @Override
    public void pokeboxButtonPressed() {
        if (getSupportFragmentManager().findFragmentByTag("pokeboxFragment")==null) {
            mPokeboxFragment = new PokeboxFragment();
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_container, mPokeboxFragment,"pokeBoxFragment")
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
/**TODO : add methods for saving edited pokemon, and for adding new pokemon, based on fragment information
     * when editing, need to move from ViewPokemonFragment to EditPokemonFragment, sending the pokeball and position as an argument
     * EditPokemonFragment should be like calculator, but fixed pokemon field, and a save button - save button checks that number of results >1, and that not the same (customEquals) as any pokemon in the pokeball.
     * Also needs to clone the uniqueID and nickname
     * When saving, delete data at that pokeball/position, and resave with new pokemon object. And then move back to ViewPokemonFragment by sending the pokeball number
     *
     * When adding pokemon, also add advice to remove known Level if no combis
     */

    //TODO probably need an async loading bar for the initial boot where loading from database


    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}
