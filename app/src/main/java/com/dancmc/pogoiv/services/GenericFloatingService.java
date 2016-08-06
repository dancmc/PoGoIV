package com.dancmc.pogoiv.services;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

public class GenericFloatingService extends Service {


    public static WindowManager windowManager;
    public WindowManager.LayoutParams mWMParams;

    public Display display;
    public DisplayMetrics mScreenMetrics;
    public SharedPreferences sp;
    public SharedPreferences.Editor ed;
    public static String currentlyRunningServiceFragment;
    public static final String OVERLAY_SERVICE = "OVERLAY_SERVICE";
    public static final String IV_CALCULATOR_SERVICE = "IV_CALCULATOR_SERVICE";
    public static final String ADD_POKEBOX_SERVICE = "ADD_POKEBOX_SERVICE";
    public static final String ADD_VIEW_POKEBALL_SERVICE = "ADD_VIEW_POKEBALL_SERVICE";



    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
