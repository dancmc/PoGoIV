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

        SQLiteDatabase db =mDbHelper.getWritableDatabase();

        //delete without decrementing, then set
        db.delete(mDbHelper.POKEBALLS_TABLE, mDbHelper.POKEBALL_NUMBER + "=" + pokeballNumber + " AND " + mDbHelper.POKEBALL_LIST_NUMBER + "=" + pokeballListNumber, null);

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

            db.insert(PokeballsDbHelper.POKEBALLS_TABLE, null, values);
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
        for (int i = 0; i < pokeballs.size(); i++) {
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

    public ArrayList<double[]> compareAllPokemon(int position) {

        ArrayList<double[]> result = new ArrayList<>();
        Cursor cursor = mDbHelper.getReadableDatabase().query(PokeballsDbHelper.POKEBALLS_TABLE, new String[]{mDbHelper.STA_IV, mDbHelper.ATK_IV, mDbHelper.DEF_IV, mDbHelper.POGOIV_ID, mDbHelper.PERCENT_PERFECT, mDbHelper.POKEMON_NUMBER}, mDbHelper.POKEBALL_NUMBER + "=" + position, null, mDbHelper.STA_IV + "," + mDbHelper.ATK_IV + "," + mDbHelper.DEF_IV, "count(distinct " + mDbHelper.POGOIV_ID + ")=" + Pokeballs.getPokeballsInstance().get(position).size(), mDbHelper.PERCENT_PERFECT);

        if (cursor.getCount() == 0) {
            return result;
        }

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            double[] resultInterim = {0, cursor.getDouble(0), cursor.getDouble(1), cursor.getDouble(2), cursor.getDouble(4)};
            result.add(resultInterim);
            cursor.moveToNext();
        }

        return result;
    }

    public void deletePokeball(int pokeballNumber) {
        //decrements all pokeballs above
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.delete(mDbHelper.POKEBALLS_TABLE, mDbHelper.POKEBALL_NUMBER + "=" + pokeballNumber, null);
        db.execSQL("update " + mDbHelper.POKEBALLS_TABLE + " set " + mDbHelper.POKEBALL_NUMBER + "=" + mDbHelper.POKEBALL_NUMBER + " -1 " + " where " + mDbHelper.POKEBALL_NUMBER + ">" + pokeballNumber);
    }

    public void deletePokemon(int pokeballNumber, int pokeballListNumber) {
        //decrements all pokemon above, also deletes pokeball if last pokemon left
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.delete(mDbHelper.POKEBALLS_TABLE, mDbHelper.POKEBALL_NUMBER + "=" + pokeballNumber + " AND " + mDbHelper.POKEBALL_LIST_NUMBER + "=" + pokeballListNumber, null);

        Cursor cursor = mDbHelper.getReadableDatabase().query(mDbHelper.POKEBALLS_TABLE, new String[]{mDbHelper.POKEBALL_NUMBER}, mDbHelper.POKEBALL_NUMBER + "=" + pokeballNumber, null, null, null, null);
        if (cursor.getCount() == 0) {
            deletePokeball(pokeballNumber);
        } else {
            db.execSQL("update " + mDbHelper.POKEBALLS_TABLE + " set " + mDbHelper.POKEBALL_LIST_NUMBER + "=" + mDbHelper.POKEBALL_LIST_NUMBER + " -1 " + " where " + mDbHelper.POKEBALL_NUMBER + "=" + pokeballNumber + " AND " + mDbHelper.POKEBALL_LIST_NUMBER + ">" + pokeballListNumber);
        }

    }


    public void setNickname(String nickname, int pokeball) {
        mDbHelper.getWritableDatabase().execSQL("update " + mDbHelper.POKEBALLS_TABLE + " set " + mDbHelper.NICKNAME + "='" + nickname + "' where " + mDbHelper.POKEBALL_NUMBER + "=" + pokeball);
    }
}
