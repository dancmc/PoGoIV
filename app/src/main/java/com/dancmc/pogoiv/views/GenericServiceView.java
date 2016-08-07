package com.dancmc.pogoiv.views;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;

import com.dancmc.pogoiv.services.FloatingHead;
import com.dancmc.pogoiv.utilities.Pokemon;

import java.text.DecimalFormat;

/**
 * Created by Daniel on 6/08/2016.
 * All views extending this must call super(context) in constructor as no default
 */


public class GenericServiceView {
    public View v;

    public Display display;
    public DisplayMetrics mScreenMetrics;
    public SharedPreferences sp;
    public SharedPreferences.Editor ed;
    public DecimalFormat mDF;
    public static final String OVERLAY_SERVICE = "OVERLAY_SERVICE";
    public static final String IV_CALCULATOR_SERVICE = "IV_CALCULATOR_SERVICE";
    public static final String ADD_POKEBOX_SERVICE = "ADD_POKEBOX_SERVICE";
    public static final String ADD_VIEW_POKEBALL_SERVICE = "ADD_VIEW_POKEBALL_SERVICE";

    public Context mContext;
    public Pokemon mPokemon;

    public GenericServiceView(Context context){
        mContext = context;

        sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        ed = sp.edit();

        mDF = new DecimalFormat("0.0");

        mPokemon = FloatingHead.floatingPokemonToAdd;
    }

    public View getView(){
        return v;
    }

}
