package com.dancmc.pogoiv.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dancmc.pogoiv.services.FloatingHead;

/**
 * Created by Daniel on 7/08/2016.
 */
public class FrameLayoutIntercept extends FrameLayout {

    private static final String TAG = "FrameLayoutIntercept";
    private Context mContext;
    public FrameLayoutIntercept(Context context){
        super(context);
        mContext = context;
    }


    public FrameLayoutIntercept(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, 0);
        mContext = context;
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK&&event.getAction() == MotionEvent.ACTION_UP){
            FloatingHead.onBackPressed();
            Log.d(TAG, "dispatchKeyEvent: ");
            return true;
        }

            return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN&&FloatingHead.currentlyRunningServiceFragment==FloatingHead.OVERLAY_SERVICE) {
            //Log.d(TAG, "onInterceptTouchEvent: "+this.getFocusedChild());
            if (this.getFocusedChild()!=null) {
                Log.d(TAG, "onInterceptTouchEvent: intercepted");
                this.getFocusedChild().clearFocus();
                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(this.getWindowToken(), 0);
                return true;
            }
        }

        return false;
    }
}
