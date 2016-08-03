package com.dancmc.pogoiv.services;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AbsoluteLayout;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.dancmc.pogoiv.R;

public class FloatingHead extends Service {

    private static final String TAG = "FlyBitch";
    private WindowManager windowManager;
    private ImageView chatHead;
    private Display display;
    private DisplayMetrics mScreenMetrics;
    private RelativeLayout mRelativeLayout;
    private Context mContext;
    private int mShortAnimationDuration;
    private FrameLayout mBottomZoneGradient;


    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
        mScreenMetrics = new DisplayMetrics();
        display.getMetrics(mScreenMetrics);
        mShortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);
        mContext = this;

        chatHead = new ImageView(this);
        chatHead.setImageResource(R.drawable.floating_head);
        mBottomZoneGradient = new FrameLayout(mContext);

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                (int) (70 * mScreenMetrics.density), (int) (70 * mScreenMetrics.density),
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = 0;

        mRelativeLayout = new RelativeLayout(this);
        windowManager.addView(mRelativeLayout, params);

        final RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        mRelativeLayout.addView(chatHead, params2);

        mRelativeLayout.setClipChildren(false);
        mRelativeLayout.setClipToPadding(false);

        ImageView closeButton = new ImageView(mContext);
        closeButton.setImageResource(R.drawable.ic_cancel_black_48px);
        final ImageView closeButtonHalo = new ImageView(mContext);
        closeButtonHalo.setImageResource(R.drawable.close_button_halo);
        final FrameLayout.LayoutParams llp = new FrameLayout.LayoutParams((int) (55 * mScreenMetrics.density), (int) (55 * mScreenMetrics.density), Gravity.CENTER_HORIZONTAL);
        //mBottomZoneGradient.setVerticalGravity(Gravity.TOP);
        //mBottomZoneGradient.setHorizontalGravity(Gravity.CENTER_HORIZONTAL);
        mBottomZoneGradient.addView(closeButton, llp);

        try {
            chatHead.setOnTouchListener(new View.OnTouchListener() {
                //private WindowManager.LayoutParams paramsF = params;
                private int initialX;
                private int initialY;
                private float initialTouchX;
                private float initialTouchY;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {

                        case MotionEvent.ACTION_DOWN:

                            initialX = params.x;
                            initialY = params.y;
                            initialTouchX = event.getRawX();
                            initialTouchY = event.getRawY();

                            //add bottom zone
                            WindowManager.LayoutParams rlBottomParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, (int) (150 * mScreenMetrics.density), WindowManager.LayoutParams.TYPE_SYSTEM_ALERT, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
                            rlBottomParams.gravity = Gravity.BOTTOM;
                            mBottomZoneGradient.setBackgroundResource(R.drawable.bottom_shadow);
                            windowManager.addView(mBottomZoneGradient, rlBottomParams);

                            break;

                        case MotionEvent.ACTION_UP:

                            final float rawX = event.getRawX();
                            final float rawY = event.getRawY();
                            final int currentX = params.x;
                            int currentY = params.y;

                            //create a new cloned icon with a match parent relativelayout so icon can bounce outside bounds
                            //Adding new Relative Layout to WindowManager
                            final RelativeLayout newLayout = new RelativeLayout(mContext);
                            windowManager.addView(newLayout, new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.TYPE_SYSTEM_ALERT, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT));
                            //Adding new ImageView to Relative Layout
                            final ImageView newImage = new ImageView(mContext);
                            newImage.setImageResource(R.drawable.floating_head);
                            final RelativeLayout.LayoutParams tempParams = new RelativeLayout.LayoutParams((int) (70 * mScreenMetrics.density), (int) (70 * mScreenMetrics.density));
                            tempParams.leftMargin = currentX;
                            tempParams.topMargin = currentY;
                            newLayout.addView(newImage, tempParams);

                            //hide dragged main icon, only cloned one visible
                            mRelativeLayout.setVisibility(View.INVISIBLE);

                            //Set the X value for where cloned icon should end up
                            if ((params.x < mScreenMetrics.widthPixels / 2 - chatHead.getWidth() / 2)&&!inViewInBounds(mBottomZoneGradient, (int)rawX, (int)rawY)) {

                                params.x = -(int) (chatHead.getWidth() * 0.2);

                                //animate the new icon, then replace it with the old one at the end
                                ValueAnimator translateLeft = ValueAnimator.ofInt(currentX, params.x);
                                translateLeft.setInterpolator(new OvershootInterpolator(2f));
                                translateLeft.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                    @Override
                                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                        int val = (Integer) valueAnimator.getAnimatedValue();
                                        tempParams.leftMargin = val;
                                        newLayout.updateViewLayout(newImage, tempParams);
                                    }
                                });
                                translateLeft.setDuration(1000);
                                translateLeft.start();
                                //
                                translateLeft.addListener(new AnimatorListenerAdapter() {

                                    @Override
                                    public void onAnimationEnd(Animator animator) {

                                            windowManager.updateViewLayout(mRelativeLayout, params);
                                            params2.leftMargin = -(int) (chatHead.getWidth() * 0.2);
                                            mRelativeLayout.updateViewLayout(chatHead, params2);
                                            mRelativeLayout.setVisibility(View.VISIBLE);
                                            Log.d(TAG, "onTouch: " + params2.leftMargin);
                                            newLayout.animate().alpha(0.0f).setDuration(mShortAnimationDuration).setListener(new AnimatorListenerAdapter() {
                                                @Override
                                                public void onAnimationEnd(Animator animation) {
                                                    windowManager.removeView(newLayout);
                                                }
                                            });



                                    }
                                });
                            } else if ((params.x > mScreenMetrics.widthPixels / 2 - chatHead.getWidth() / 2)&&!inViewInBounds(mBottomZoneGradient, (int)rawX, (int)rawY)) {

                                params.x = mScreenMetrics.widthPixels - (int) (chatHead.getWidth() * 0.8);
                                //animate the new icon, then replace it with the old one at the end
                                ValueAnimator translateRight = ValueAnimator.ofInt(currentX, params.x);
                                translateRight.setInterpolator(new OvershootInterpolator(2f));
                                translateRight.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                    @Override
                                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                        int val = (Integer) valueAnimator.getAnimatedValue();
                                        //paramsF.x = val;
                                        //windowManager.updateViewLayout(mRelativeLayout, paramsF);
                                        tempParams.leftMargin = val;
                                        tempParams.rightMargin = mScreenMetrics.widthPixels - val - chatHead.getWidth();
                                        newLayout.updateViewLayout(newImage, tempParams);
                                    }
                                });
                                translateRight.setDuration(1000);
                                translateRight.start();
                                translateRight.addListener(new AnimatorListenerAdapter() {

                                    @Override
                                    public void onAnimationEnd(Animator animator) {

                                            windowManager.updateViewLayout(mRelativeLayout, params);
                                            params2.leftMargin = (int) (chatHead.getWidth() * 0.2);
                                            params2.rightMargin = -(int) (chatHead.getWidth() * 0.2);

                                            mRelativeLayout.updateViewLayout(chatHead, params2);
                                            Log.d(TAG, "onTouch: " + params2.rightMargin);
                                            mRelativeLayout.setVisibility(View.VISIBLE);
                                            newLayout.animate().alpha(0.0f).setDuration(mShortAnimationDuration).setListener(new AnimatorListenerAdapter() {
                                                @Override
                                                public void onAnimationEnd(Animator animation) {
                                                    windowManager.removeView(newLayout);
                                                }
                                            });


                                    }
                                });
                            }else if (inViewInBounds(mBottomZoneGradient, (int) rawX, (int) rawY)) {
                                if (chatHead != null) windowManager.removeView(mRelativeLayout);
                                if(newLayout!=null&&newLayout.getParent()!=null) windowManager.removeView(newLayout);
                                stopSelf();
                            }
                            mBottomZoneGradient.removeView(closeButtonHalo);
                            windowManager.removeView(mBottomZoneGradient);

                            break;
                        case MotionEvent.ACTION_MOVE:
                            Log.d(TAG, "onTouch: moving");

                            params2.setMargins(0, 0, 0, 0);
                            mRelativeLayout.updateViewLayout(chatHead, params2);

                            params.x = initialX + (int) (event.getRawX() - initialTouchX);
                            params.y = initialY + (int) (event.getRawY() - initialTouchY);
                            windowManager.updateViewLayout(mRelativeLayout, params);

                            if (inViewInBounds(mBottomZoneGradient, (int) event.getRawX(), (int) event.getRawY()) && closeButtonHalo.getParent() == null) {
                                mBottomZoneGradient.addView(closeButtonHalo, llp);
                            } else if (!inViewInBounds(mBottomZoneGradient, (int) event.getRawX(), (int) event.getRawY()) && closeButtonHalo.getParent() != null) {
                                mBottomZoneGradient.removeView(closeButtonHalo);
                            }

                            break;
                    }
                    return true;
                }
            });
        } catch (Exception e) {
            Log.d(TAG, "onCreate: " + e.getMessage());
            // TODO: handle exception
        }

    }


    private boolean inViewInBounds(View view, int x, int y) {
        Rect outRect = new Rect();
        int[] location = new int[2];

        view.getDrawingRect(outRect);
        view.getLocationOnScreen(location);
        outRect.offset(location[0], location[1]);
        return outRect.contains(x, y);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();


        Log.d(TAG, "onDestroy: ");
    }


}
