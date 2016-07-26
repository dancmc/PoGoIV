package com.dancmc.pogoiv;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Created by Daniel on 22/07/2016.
 */
public class PokeballsDataSource {

    private PokeballsDbHelper mDbHelper;
    private SQLiteDatabase mDatabase;
    private static final String TAG = "PokeballsDataSource";
    private String[] allColumns = {PokeballsDbHelper.COLUMN_ID,
            PokeballsDbHelper.POGOIV_ID,
            PokeballsDbHelper.POKEBALL_NUMBER,
            PokeballsDbHelper.POKEMON_NAME,
            PokeballsDbHelper.NICKNAME,
            PokeballsDbHelper.POKEMON_NUMBER,
            PokeballsDbHelper.POKEMON_FAMILY,
            PokeballsDbHelper.HP,
            PokeballsDbHelper.CP,
            PokeballsDbHelper.STARDUST,
            PokeballsDbHelper.LEVEL,
            PokeballsDbHelper.STA_IV,
            PokeballsDbHelper.ATK_IV,
            PokeballsDbHelper.DEF_IV,
            PokeballsDbHelper.FRESH_MEAT,
            PokeballsDbHelper.PERCENT_PERFECT,
            PokeballsDbHelper.BASE_STA,
            PokeballsDbHelper.BASE_ATK,
            PokeballsDbHelper.BASE_DEF};

    public PokeballsDataSource(Context context) {
        mDbHelper = PokeballsDbHelper.getInstance(context);
    }

    public void setPokeballData(Pokemon pokemon, int pokeballNumber) {
        for (int i = 0; i < pokemon.getNumberOfResults(); i++) {

            ContentValues values = new ContentValues();
            values.put(PokeballsDbHelper.POGOIV_ID, pokemon.getUniqueID());
            values.put(PokeballsDbHelper.POKEBALL_NUMBER, pokeballNumber);
            values.put(PokeballsDbHelper.POKEMON_NAME, pokemon.getPokemonName());
            values.put(PokeballsDbHelper.POKEMON_NUMBER, pokemon.getPokemonNumber());
            values.put(PokeballsDbHelper.POKEMON_FAMILY, pokemon.getPokemonFamily());
            values.put(PokeballsDbHelper.HP, pokemon.getHP());
            values.put(PokeballsDbHelper.CP, pokemon.getCP());
            values.put(PokeballsDbHelper.STARDUST, pokemon.getStardust());
            values.put(PokeballsDbHelper.LEVEL, (int) pokemon.getIVCombinationsArray().get(i)[0]);
            values.put(PokeballsDbHelper.STA_IV, pokemon.getIVCombinationsArray().get(i)[1]);
            values.put(PokeballsDbHelper.ATK_IV, pokemon.getIVCombinationsArray().get(i)[2]);
            values.put(PokeballsDbHelper.DEF_IV, pokemon.getIVCombinationsArray().get(i)[3]);
            values.put(PokeballsDbHelper.FRESH_MEAT, pokemon.getFreshMeat());
            values.put(PokeballsDbHelper.PERCENT_PERFECT, pokemon.getIVCombinationsArray().get(i)[4]);
            values.put(PokeballsDbHelper.BASE_STA, pokemon.getBaseSta());
            values.put(PokeballsDbHelper.BASE_ATK, pokemon.getBaseAtk());
            values.put(PokeballsDbHelper.BASE_DEF, pokemon.getBaseDef());
            mDbHelper.getWritableDatabase().insert(PokeballsDbHelper.POKEBALLS_TABLE, null, values);
        }
    }

    public ArrayList<Pokemon> getAllPokeballs() {
        ArrayList<Pokemon> pokeballs = new ArrayList<Pokemon>(Arrays.asList(new Pokemon[]{null, null, null, null, null, null, null, null}));
        for (int i = 1; i < 7; i++) {
            Cursor cursor = mDbHelper.getReadableDatabase().query(mDbHelper.POKEBALLS_TABLE, new String[]{mDbHelper.POKEBALL_NUMBER, mDbHelper.POKEMON_NAME, mDbHelper.HP, mDbHelper.CP, mDbHelper.STARDUST, mDbHelper.FRESH_MEAT, mDbHelper.LEVEL, mDbHelper.POGOIV_ID}, mDbHelper.POKEBALL_NUMBER + "=" + i, null, mDbHelper.LEVEL, null, null);
            cursor.moveToFirst();
            Pokemon tempPokemon;

            //if the pokeball exists,
            if (cursor.getCount() == 1) {
                tempPokemon = new Pokemon(cursor.getString(1), cursor.getInt(2), cursor.getInt(3), cursor.getInt(4), (cursor.getInt(5) != 0), cursor.getInt(6));
                tempPokemon.setUniqueID(cursor.getInt(7));
                pokeballs.set(i, tempPokemon);
            } else if (cursor.getCount() > 1) {
                tempPokemon = new Pokemon(cursor.getString(1), cursor.getInt(2), cursor.getInt(3), cursor.getInt(4), (cursor.getInt(5) != 0), -1);
                tempPokemon.setUniqueID(cursor.getInt(7));
                pokeballs.set(i, tempPokemon);
            }
            cursor.close();
        }
        return pokeballs;
    }

    public String compareAllPokeballs(ArrayList<Pokemon> a) {
        int count = 0;
        for (int i = 0; i < a.size(); i++) {
            if (a.get(i) != null)
                count += 1;
        }

        //DO a check
        Cursor cursor = mDbHelper.getReadableDatabase().query(PokeballsDbHelper.POKEBALLS_TABLE, new String[]{mDbHelper.POKEMON_FAMILY}, null, null, mDbHelper.POKEMON_FAMILY, null, null);
        Log.d(TAG, "compareAllPokeballs: number of Pokemon families " + cursor.getCount());
        if (cursor.getCount() > 1) {
            cursor.close();
            return "You are trying to compare Pokemon from different families, please delete some.";
        }
        cursor.close();


        StringBuilder sb = new StringBuilder();
        cursor = mDbHelper.getReadableDatabase().query(PokeballsDbHelper.POKEBALLS_TABLE, new String[]{mDbHelper.STA_IV, mDbHelper.ATK_IV, mDbHelper.DEF_IV, mDbHelper.POGOIV_ID, mDbHelper.PERCENT_PERFECT, mDbHelper.POKEMON_NUMBER}, null, null, mDbHelper.STA_IV + "," + mDbHelper.ATK_IV + "," + mDbHelper.DEF_IV, "count(distinct " + mDbHelper.POGOIV_ID + ")=" + count, mDbHelper.PERCENT_PERFECT);
        if (cursor.getCount() == 0)
            return "There are no overlapping combinations found, are these the same pokemon?";

        cursor.moveToFirst();
        ArrayList<double[]> ivCombos = new ArrayList<>();
        int pokemonNumber = cursor.getInt(5);
        double averagePercent = 0;
        double lowest = cursor.getDouble(4);


        while (!cursor.isAfterLast()) {
            sb.append(cursor.getPosition() + " : " + cursor.getInt(0) + "/" + cursor.getInt(1) + "/" + cursor.getInt(2) + "   " + String.format(Locale.US, "%.1f", cursor.getDouble(4)) + "%\n");
            averagePercent += cursor.getDouble(4);
            double[] ivCombo = {0, cursor.getDouble(0), cursor.getDouble(1), cursor.getDouble(2), 0};
            ivCombos.add(ivCombo);
            cursor.moveToNext();
        }

        //calculating average percent
        averagePercent = averagePercent / cursor.getCount();

        cursor.moveToLast();
        double highest = cursor.getDouble(4);
        cursor.close();

        StringBuilder sb2 = new StringBuilder();


        sb2.append("There are " + cursor.getCount() + " overlapping combinations found, with an average power of " + String.format(Locale.US, "%.1f", averagePercent) + "%, and a range of " + String.format(Locale.US, "%.1f", lowest) + "% to " + String.format(Locale.US, "%.1f", highest) + "%.\n\nSta/Atk/Def\n");

        //get the max evolution of the pokemons to be compared, calculate the maxmimum CP with 15 IVs at lvl 79, get the average CP of current pokemon at lvl 79
        String[] maxEvolved = Pokemon.getMaxEvolved(pokemonNumber);
        for(int i=0; i<maxEvolved.length; i++) {
            int pokeNumber = Pokemon.getPokemonNumberFromName(maxEvolved[i]);
            double maxedCPPercent = Pokemon.calculateMaxLevelAverageCPPercent(ivCombos,pokeNumber);
            Log.d(TAG, "compareAllPokeballs: " + maxedCPPercent);
            sb2.append("A max leveled "+ maxEvolved[i] + " with this average IV% would have a CP of ~" + String.format(Locale.US, "%.0f", (maxedCPPercent*(Pokemon.getCPDifference(pokeNumber))/100.0+Pokemon.getMinCP(pokeNumber))) + " (" +String.format(Locale.US, "%.1f", maxedCPPercent)+"%), versus a max of " + Pokemon.getMaxCP(pokeNumber) +" and a min of " + Pokemon.getMinCP(pokeNumber)+".\n");
        }
        sb2.append(" \n");
        return sb2.toString()+sb.toString();
    }

    public void deletePokeball(int i) {
        mDbHelper.getWritableDatabase().delete(mDbHelper.POKEBALLS_TABLE, mDbHelper.POKEBALL_NUMBER + "=" + i, null);
    }
}
