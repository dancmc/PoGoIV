package com.dancmc.pogoiv.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.dancmc.pogoiv.R;
import com.dancmc.pogoiv.fragments.OverlayFragment;
import com.dancmc.pogoiv.services.FloatingHead;

public class OverlayActivity extends AppCompatActivity {
    SharedPreferences sp;
    SharedPreferences.Editor ed;
    public static boolean mRunning;
    public static boolean mIsIntentionalCall;
    public static boolean mIsIntentionalShutdown;
    private static final String TAG = "OverlayActivity";
    private OverlayFragment mOverlayFragment;

    private FrameLayout mMainLayout;
    private boolean mVisible;

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: ");


            /*if (intent.getAction().equals("toggle overlay")&&mVisible) {
                mMainLayout.setVisibility(View.GONE);
                mVisible = false;
                getWindow().addFlags(
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                                | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            } else if (intent.getAction().equals("toggle overlay")&&!mVisible) {
                mMainLayout.setVisibility(View.VISIBLE);
                getWindow().clearFlags(
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                                | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                mVisible = true;
            }else if (intent.getAction().equals("stop overlay")){
                finish();
            }*/
            finish();
        }
    };
    private IntentFilter mIntentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setUpWindow();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overlay);

        startService(new Intent(this, FloatingHead.class));

        mMainLayout = (FrameLayout) findViewById(R.id.overlay_main_layout);
        mVisible = true;

        //indicate that running
        sp = getSharedPreferences("OVERLAY_ACTIVITY", MODE_PRIVATE);

        mRunning = true;

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("toggle overlay");
        mIntentFilter.addAction("stop overlay");
        this.registerReceiver(mMessageReceiver, mIntentFilter);

        if (mOverlayFragment == null) {
            mOverlayFragment = new OverlayFragment();
        }
        getSupportFragmentManager().beginTransaction()
                .add(R.id.overlay_main_layout, mOverlayFragment)
                .commit();
        Log.d(TAG, "onCreate: ");


    }


    public void setUpWindow() {

        // Creates the layout for the window and the look of it
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND,
                WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        // Params for the window.
        // You can easily set the alpha and the dim behind the window from here
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.alpha = 1f;    // lower than one makes it more transparent
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

        mRunning = false;
        Log.d(TAG, "onStop: ");
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
        //get rid of slide up and down transitions
        overridePendingTransition(0, 0);
        mRunning = false;
        Log.d(TAG, "onPause: ");
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

        Log.d(TAG, "onDestroy: ");

    }


    //onResume is called every single time, whether from the restart route, or the oncreate
    //so here do a check whether the overlay call was explicitly started by a button press, and then invalidate the boolean
    //all explicit calls to this activity (ie not from recent tasks) have to set the static variable, or it will shift to MainActivity
    @Override
    protected void onResume() {
        super.onResume();

        FloatingHead.removeWaitingText();

        mMainLayout.setVisibility(View.VISIBLE);
        mVisible = true;

        if (!mIsIntentionalCall) {
            startActivity(new Intent(this, MainActivity.class));
        }

        startService(new Intent(this, FloatingHead.class));
        this.registerReceiver(mMessageReceiver, mIntentFilter);

        //get rid of slide up and down transitions
        overridePendingTransition(0, 0);
        mRunning = true;
        mIsIntentionalCall = false;
        mIsIntentionalShutdown = false;
        Log.d(TAG, "onResume: ");
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        startService(new Intent(this, FloatingHead.class));
        this.registerReceiver(mMessageReceiver, new IntentFilter("stop overlay"));

        mRunning = true;

        Log.d(TAG, "onRestart: ");

    }

    public static boolean overlayIsRunning() {
        return mRunning;
    }

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
        Log.d(TAG, "dispatchTouchEvent: ");
        return super.dispatchTouchEvent(event);
    }


}
