package com.dancmc.pogoiv.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

import com.dancmc.pogoiv.R;
import com.dancmc.pogoiv.services.FloatingHead;

public class OverlayActivity extends AppCompatActivity {
    SharedPreferences sp;
    SharedPreferences.Editor ed;
    public static boolean mRunning;
    public static boolean mIsIntentionalCall;
    private static final String TAG = "OverlayActivity";

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: ");
            if (mMessageReceiver != null) {
                unregisterReceiver(mMessageReceiver);
            }

            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setUpWindow();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overlay);



        startService(new Intent(this, FloatingHead.class));
        //indicate that running
        sp = getSharedPreferences("OVERLAY_ACTIVITY", MODE_PRIVATE);
        //ed = sp.edit();
        //ed.putBoolean("RUNNING", true).commit();

        mRunning = true;

        this.registerReceiver(mMessageReceiver, new IntentFilter("stop overlay"));

        Log.d(TAG, "onCreate: "+mIsIntentionalCall);
    }


    public void setUpWindow() {

        // Creates the layout for the window and the look of it
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND,
                WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        // Params for the window.
        // You can easily set the alpha and the dim behind the window from here
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.alpha = 0.6f;    // lower than one makes it more transparent
        params.dimAmount = 0f;  // set it higher if you want to dim behind the window
        getWindow().setAttributes(params);

        // Gets the display size so that you can set the window to a percent of that
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        // You could also easily used an integer value from the shared preferences to set the percent

        getWindow().setLayout((int) (width * 1), (int) (height * 1));


    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mMessageReceiver != null) {
            try {
                unregisterReceiver(mMessageReceiver);
            } catch (Exception e) {
            }

        }
        mRunning = false;

    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mMessageReceiver != null) {
            try {
                unregisterReceiver(mMessageReceiver);
            } catch (Exception e) {
            }
        }
        mRunning = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mMessageReceiver != null) {
            try {
                unregisterReceiver(mMessageReceiver);
            } catch (Exception e) {
            }
        }
        mRunning = false;


    }


    //onResume is called every single time, whether from the restart route, or the oncreate
    //so here do a check whether the overlay call was explicitly started by a button press, and then invalidate the boolean
    //all explicit calls to this activity (ie not from recent tasks) have to set the static variable, or it will shift to MainActivity
    @Override
    protected void onResume() {
        super.onResume();

        if (!mIsIntentionalCall) {
            startActivity(new Intent(this, MainActivity.class));
        }

        startService(new Intent(this, FloatingHead.class));
        this.registerReceiver(mMessageReceiver, new IntentFilter("stop overlay"));

        mRunning = true;
        mIsIntentionalCall = false;
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        startService(new Intent(this, FloatingHead.class));
        this.registerReceiver(mMessageReceiver, new IntentFilter("stop overlay"));

        mRunning = true;


    }

    public static boolean overlayIsRunning() {
        return mRunning;
    }




}
