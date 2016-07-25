package com.dancmc.pogoiv;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Daniel on 21/07/2016.
 */
public class PokeballsDbHelper extends SQLiteOpenHelper {

    //create a global singleton database manager, don't let ppl create another non static instance by making constructor private
    private static PokeballsDbHelper singleton = null;
    private PokeballsDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public static PokeballsDbHelper getInstance(Context context) {
        if (singleton == null) {
            singleton = new PokeballsDbHelper((context.getApplicationContext()));
        }
        return singleton;
    }

    private static final String DATABASE_NAME = "pokeballtracker.db";
    private static final int DATABASE_VERSION = 1;

    public static final String POKEBALLS_TABLE = "pokeballs";
    public static final String COLUMN_ID = "_id";
    public static final String POGOIV_ID = "pogoiv_id";
    public static final String POKEBALL_NUMBER = "pokeball_number";
    public static final String POKEMON_NAME = "pokemon_name";
    public static final String NICKNAME = "nickname";
    public static final String POKEMON_NUMBER = "pokemon_number";
    public static final String POKEMON_FAMILY = "pokemon_family";
    public static final String HP = "hp";
    public static final String CP = "cp";
    public static final String STARDUST = "stardust";
    public static final String LEVEL = "level";
    public static final String STA_IV = "sta_iv";
    public static final String ATK_IV = "atk_iv";
    public static final String DEF_IV = "def_iv";
    public static final String FRESH_MEAT = "fresh_meat";
    public static final String PERCENT_PERFECT = "percent_perfect";
    public static final String BASE_STA = "base_sta";
    public static final String BASE_ATK = "base_atk";
    public static final String BASE_DEF = "base_def";




    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+POKEBALLS_TABLE+" ("
                +COLUMN_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
                +POGOIV_ID+" INTEGER, "
                +POKEBALL_NUMBER+" INTEGER,"
                +POKEMON_NAME+" TEXT, "
                +NICKNAME+" TEXT, "
                +POKEMON_NUMBER+" INTEGER, "
                +POKEMON_FAMILY+" TEXT, "
                +HP+" INTEGER, "
                +CP+" INTEGER, "
                +STARDUST+" INTEGER, "
                +LEVEL+" INTEGER, "
                +STA_IV+" INTEGER, "
                +ATK_IV+" INTEGER, "
                +DEF_IV+" INTEGER, "
                +FRESH_MEAT+" BOOLEAN, "
                +PERCENT_PERFECT+" DOUBLE, "
                +BASE_STA+" INTEGER, "
                +BASE_ATK+" INTEGER, "
                +BASE_DEF+" INTEGER"
                +")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
