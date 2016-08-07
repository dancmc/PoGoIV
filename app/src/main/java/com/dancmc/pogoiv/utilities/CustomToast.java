package com.dancmc.pogoiv.utilities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.PixelFormat;
import android.support.v4.view.ViewCompat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.dancmc.pogoiv.R;

/**
 * Created by Daniel on 7/08/2016.
 */
public class CustomToast {
    private View v;
    WindowManager windowManager;

    private CustomToast(Context context){
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        v = View.inflate(context, R.layout.toast_hack, null);


    }

    public static CustomToast makeToast(Context context){
        return new CustomToast(context);
    }

    public CustomToast setMessage(String s){
        ((TextView)v.findViewById(R.id.toast_message)).setText(s);
        return this;
    }

    public void show(){
        windowManager.addView(v, new WindowManager.LayoutParams( WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSLUCENT));

        v.setAlpha(0.0f);

        v.animate().alpha(1f).setDuration(500).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                v.animate().alpha(0.97f).setDuration(1400).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        v.animate().alpha(0.0f).setDuration(500).setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                windowManager.removeView(v);
                            }
                        });
                    }
                });
            }
        });


    }

    private View getView(){
        return v;
    }







}
