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
            PokeballsDbHelper.POKEBALL_LIST_NUMBER,
            PokeballsDbHelper.POKEMON_NAME,
            PokeballsDbHelper.NICKNAME,
            PokeballsDbHelper.POKEMON_NUMBER,
            PokeballsDbHelper.POKEMON_FAMILY,
            PokeballsDbHelper.HP,
            PokeballsDbHelper.CP,
            PokeballsDbHelper.STARDUST,
            PokeballsDbHelper.LEVEL,
            PokeballsDbHelper.KNOWN_LEVEL,
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

    public void setPokemonData(Pokemon pokemon, int pokeballNumber, int pokeballListNumber) {

        //delete any current pokemon with those coordinates and set new one
        deletePokemon(pokeballNumber, pokeballListNumber);

        for (int i = 0; i < pokemon.getNumberOfResults(); i++) {

            ContentValues values = new ContentValues();
            values.put(PokeballsDbHelper.POGOIV_ID, pokemon.getUniqueID());
            values.put(PokeballsDbHelper.POKEBALL_NUMBER, pokeballNumber);
            values.put(PokeballsDbHelper.POKEBALL_LIST_NUMBER, pokeballListNumber);
            values.put(PokeballsDbHelper.POKEMON_NAME, pokemon.getPokemonName());
            values.put(PokeballsDbHelper.NICKNAME, pokemon.getNickname());
            values.put(PokeballsDbHelper.POKEMON_NUMBER, pokemon.getPokemonNumber());
            values.put(PokeballsDbHelper.POKEMON_FAMILY, pokemon.getPokemonFamily());
            values.put(PokeballsDbHelper.HP, pokemon.getHP());
            values.put(PokeballsDbHelper.CP, pokemon.getCP());
            values.put(PokeballsDbHelper.STARDUST, pokemon.getStardust());
            values.put(PokeballsDbHelper.LEVEL, (int) pokemon.getIVCombinationsArray().get(i)[0]);
            values.put(PokeballsDbHelper.KNOWN_LEVEL, pokemon.getKnownLevel());
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


    public Pokeballs getAllPokeballs() {
        Pokeballs pokeballs = new Pokeballs();

        Cursor cursor = mDbHelper.getReadableDatabase().query(mDbHelper.POKEBALLS_TABLE, new String[]{mDbHelper.POGOIV_ID, mDbHelper.POKEMON_NAME, mDbHelper.HP, mDbHelper.CP, mDbHelper.STARDUST, mDbHelper.FRESH_MEAT, mDbHelper.KNOWN_LEVEL, mDbHelper.POKEBALL_NUMBER, mDbHelper.POKEBALL_LIST_NUMBER, mDbHelper.NICKNAME}, null, null, mDbHelper.POGOIV_ID, null, mDbHelper.POKEBALL_NUMBER + " ASC," + mDbHelper.POKEBALL_LIST_NUMBER + " ASC");

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            Pokemon tempPokemon;

            int pokeballHolding = -1;
            int pokeballListNumberHolding = -1;

            while (!cursor.isAfterLast()) {

                if (cursor.getInt(7) > pokeballHolding) {
                    pokeballs.add(new Pokeball());
                    pokeballHolding = cursor.getInt(7);
                    pokeballListNumberHolding = -1;
                }
                if (cursor.getInt(8) > pokeballListNumberHolding) {
                    pokeballs.get(pokeballHolding).add(null);
                    pokeballListNumberHolding = cursor.getInt(8);
                }

                tempPokemon = new Pokemon(cursor.getString(1), cursor.getInt(2), cursor.getInt(3), cursor.getInt(4), (cursor.getInt(5) != 0), cursor.getInt(6));
                tempPokemon.setUniqueID(cursor.getInt(0));
                tempPokemon.setNickname(cursor.getString(9));
                pokeballs.get(cursor.getInt(7)).set(cursor.getInt(8), tempPokemon);

                cursor.moveToNext();
            }
            cursor.close();
        }
        //need to set the pokeball fields for the previous pokeball
        for (int i = 0; i <pokeballs.size(); i++) {
            Pokeball pokeball = pokeballs.get(i);
            int highestTier = 0;
            int highestEvolved = 0;
            for (int j = 0; j < pokeball.size(); j++) {
                int tier = pokeball.get(j).getEvolutionTier();
                if (tier > highestTier) {
                    highestTier = tier;
                    highestEvolved = j;
                }
            }
            pokeballs.get(i).setHighestEvolvedPokemonNumber(pokeball.get(highestEvolved).getPokemonNumber());
            pokeballs.get(i).setNickname(pokeball.get(0).getNickname());
        }

        return pokeballs;
    }

    public String[] compareAllPokemon(int position) {


        /*Shouldn't be able to add different families
        Cursor cursor = mDbHelper.getReadableDatabase().query(PokeballsDbHelper.POKEBALLS_TABLE, new String[]{mDbHelper.POKEMON_FAMILY}, null, null, mDbHelper.POKEMON_FAMILY, null, null);
        Log.d(TAG, "compareAllPokeballs: number of Pokemon families " + cursor.getCount());
        if (cursor.getCount() > 1) {
            cursor.close();
            return "You are trying to compare Pokemon from different families, please delete some.";
        }
        cursor.close();
        */

        String[] result = new String[6];
        Cursor cursor = mDbHelper.getReadableDatabase().query(PokeballsDbHelper.POKEBALLS_TABLE, new String[]{mDbHelper.STA_IV, mDbHelper.ATK_IV, mDbHelper.DEF_IV, mDbHelper.POGOIV_ID, mDbHelper.PERCENT_PERFECT, mDbHelper.POKEMON_NUMBER}, mDbHelper.POKEBALL_NUMBER+"="+position, null, mDbHelper.STA_IV + "," + mDbHelper.ATK_IV + "," + mDbHelper.DEF_IV, "count(distinct " + mDbHelper.POGOIV_ID + ")=" + Pokeballs.getPokeballsInstance().get(position).size(), mDbHelper.PERCENT_PERFECT);
        int count = cursor.getCount();
        result[0] = ""+count;
        if (cursor.getCount() == 0) {
            result[1] = "There are no overlapping combinations found, are these the same pokemon?";
            return result;
        }

        cursor.moveToFirst();
        ArrayList<double[]> ivCombos = new ArrayList<>();
        int pokemonNumber = cursor.getInt(5);
        double averagePercent = 0;
        double lowest = cursor.getDouble(4);

        StringBuilder ivCompareList = new StringBuilder();

        ivCompareList.append("\nSta/Atk/Def\n");
        while (!cursor.isAfterLast()) {
            ivCompareList.append(cursor.getPosition() + " : " + cursor.getInt(0) + "/" + cursor.getInt(1) + "/" + cursor.getInt(2) + "   " + String.format(Locale.US, "%.1f", cursor.getDouble(4)) + "%\n");
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

        StringBuilder summary = new StringBuilder();
        String stringResult3 = "";
        //get the max evolution of the pokemons to be compared, calculate the maxmimum CP with 15 IVs at lvl 79, get the average CP of current pokemon at lvl 79
        String[] maxEvolved = Pokemon.getMaxEvolved(pokemonNumber);
        for (int i = 0; i < maxEvolved.length; i++) {
            int pokeNumber = Pokemon.getPokemonNumberFromName(maxEvolved[i]);
            double maxedCPPercent = Pokemon.calculateMaxLevelAverageCPPercent(ivCombos, pokeNumber);
            stringResult3+=(int)maxedCPPercent+ " ";
            summary.append("A max leveled " + maxEvolved[i] + " with this average IV% would have a CP of ~" + String.format(Locale.US, "%.0f", (maxedCPPercent * (Pokemon.getCPDifference(pokeNumber)) / 100.0 + Pokemon.getMinCP(pokeNumber))) + " (" + String.format(Locale.US, "%.1f", maxedCPPercent) + "%), versus a perfect max of " + Pokemon.getMaxCP(pokeNumber) + " and a min of " + Pokemon.getMinCP(pokeNumber) + ".\n");
        }
        summary.append(" \n");
        if(Pokeballs.getPokeballsInstance().get(position).size()==1){
            summary.append("No comparison done as only one dataset for this Pokemon. " + cursor.getCount() + " IV combinations found, average IV% " + String.format(Locale.US, "%.1f", averagePercent) + "% (range " + String.format(Locale.US, "%.1f", lowest) + " - " + String.format(Locale.US, "%.1f", highest) + "%).\n\n");
        }else {
            summary.append("There are " + cursor.getCount() + " overlapping combinations found, average IV% " + String.format(Locale.US, "%.1f", averagePercent) + "% (range " + String.format(Locale.US, "%.1f", lowest) + " - " + String.format(Locale.US, "%.1f", highest) + "%).\n\n");
        }
        summary.append("(CLICK FOR COMPARISON LIST)");
        result[2] = ""+(int)averagePercent;
        result[3] = stringResult3.trim();
        result[4] = summary.toString();
        if(Pokeballs.getPokeballsInstance().get(position).size() == 1) {
            result[5] = "No comparison done, only 1 dataset.";

        } else{
            result[5] = ivCompareList.toString();
        }

        return result;
    }

    public void deletePokeball(int pokeballNumber) {
        //decrements all pokeballs above
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.delete(mDbHelper.POKEBALLS_TABLE, mDbHelper.POKEBALL_NUMBER + "=" + pokeballNumber, null);
        db.execSQL("update "+ mDbHelper.POKEBALLS_TABLE +" set "+mDbHelper.POKEBALL_NUMBER+ "=" +mDbHelper.POKEBALL_NUMBER+ " -1 "+" where " + mDbHelper.POKEBALL_NUMBER +">" +pokeballNumber);
    }

    public void deletePokemon(int pokeballNumber, int pokeballListNumber) {
        //decrements all pokemon above
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.delete(mDbHelper.POKEBALLS_TABLE, mDbHelper.POKEBALL_NUMBER + "=" + pokeballNumber + " AND " + mDbHelper.POKEBALL_LIST_NUMBER + "=" + pokeballListNumber, null);
        Cursor cursor = mDbHelper.getReadableDatabase().query(mDbHelper.POKEBALLS_TABLE, new String[]{mDbHelper.POKEBALL_NUMBER}, mDbHelper.POKEBALL_NUMBER+"="+pokeballNumber, null, null, null, null);

        if(cursor.getCount()==0){
            deletePokeball(pokeballNumber);
        } else {
            db.execSQL("update " + mDbHelper.POKEBALLS_TABLE + " set " + mDbHelper.POKEBALL_LIST_NUMBER + "=" + mDbHelper.POKEBALL_LIST_NUMBER + " -1 " + " where " +mDbHelper.POKEBALL_NUMBER+"="+pokeballNumber+" AND "+mDbHelper.POKEBALL_LIST_NUMBER + ">" + pokeballListNumber);
        }

    }


}
