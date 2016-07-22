package com.dancmc.pogoiv;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Daniel on 21/07/2016.
 */
public class PokeballsDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "pokeballtracker.db";
    private static final int DATABASE_VERSION = 1;
    public PokeballsDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE pokeballs (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "pokeball INTEGER, " +
                "pokemon_number INTEGER," +
                "pokemon_name TEXT,");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
