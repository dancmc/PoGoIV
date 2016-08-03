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
import android.os.Vibrator;
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
    private int mFinalX;
    private int mFinalY;
    private RelativeLayout.LayoutParams mCloneRLParams;
    private ImageView mCloneImage;
    private RelativeLayout mCloneRL;
    private WindowManager.LayoutParams mWMParams;
    private RelativeLayout.LayoutParams mRLParams;
    private Vibrator mVibrator;


    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
        mScreenMetrics = new DisplayMetrics();
        display.getMetrics(mScreenMetrics);
        mShortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);
        mContext = this;

        chatHead = new ImageView(this);
        chatHead.setImageResource(R.drawable.floating_head);
        mBottomZoneGradient = new FrameLayout(mContext);

        mWMParams = new WindowManager.LayoutParams(
                (int) (70 * mScreenMetrics.density), (int) (70 * mScreenMetrics.density),
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        mWMParams.gravity = Gravity.TOP | Gravity.LEFT;
        mWMParams.x = 0;
        mWMParams.y = 0;

        mRelativeLayout = new RelativeLayout(this);
        windowManager.addView(mRelativeLayout, mWMParams);

        mRLParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        mRelativeLayout.addView(chatHead, mRLParams);

        mRelativeLayout.setClipChildren(false);
        mRelativeLayout.setClipToPadding(false);

        ImageView closeButton = new ImageView(mContext);
        closeButton.setImageResource(R.drawable.ic_cancel_black_48px);
        final ImageView closeButtonHalo = new ImageView(mContext);
        closeButtonHalo.setImageResource(R.drawable.close_button_halo);
        final FrameLayout.LayoutParams llp = new FrameLayout.LayoutParams((int) (55 * mScreenMetrics.density), (int) (55 * mScreenMetrics.density), Gravity.CENTER_HORIZONTAL);
        mBottomZoneGradient.addView(closeButton, llp);

        try {
            chatHead.setOnTouchListener(new View.OnTouchListener() {
                private int initialX;
                private int initialY;
                private float initialTouchX;
                private float initialTouchY;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {

                        case MotionEvent.ACTION_DOWN:

                            initialX = mWMParams.x;
                            initialY = mWMParams.y;
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
                            mFinalX = mWMParams.x;
                            mFinalY = mWMParams.y;

                            //create a new cloned icon with a match parent relativelayout so icon can bounce outside bounds
                            //Adding new Relative Layout to WindowManager
                            mCloneRL = new RelativeLayout(mContext);
                            windowManager.addView(mCloneRL, new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.TYPE_SYSTEM_ALERT, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT));
                            //Adding new ImageView to Relative Layout
                            mCloneImage = new ImageView(mContext);
                            mCloneImage.setImageResource(R.drawable.floating_head);
                            mCloneRLParams = new RelativeLayout.LayoutParams((int) (70 * mScreenMetrics.density), (int) (70 * mScreenMetrics.density));
                            //mCloneRLParams.leftMargin = mFinalX;
                            //mCloneRLParams.topMargin = mFinalY;
                            mCloneRL.addView(mCloneImage, mCloneRLParams);
                            mCloneImage.setTranslationX(mFinalX);
                            mCloneImage.setTranslationY(mFinalY);

                            //hide dragged main icon, only cloned one visible
                            mRelativeLayout.setVisibility(View.INVISIBLE);

                            //Set the X value for where cloned icon should end up
                            if ((mWMParams.x < mScreenMetrics.widthPixels / 2 - chatHead.getWidth() / 2) && !inViewInBounds(mBottomZoneGradient, (int) rawX, (int) rawY)) {
                                mWMParams.x = -(int) (chatHead.getWidth() * 0.3);
                                //animate the new icon, then replace it with the old one at the end
                                animateSnapToEdge(true);

                            } else if ((mWMParams.x > mScreenMetrics.widthPixels / 2 - chatHead.getWidth() / 2) && !inViewInBounds(mBottomZoneGradient, (int) rawX, (int) rawY)) {
                                mWMParams.x = mScreenMetrics.widthPixels - (int) (chatHead.getWidth() * 0.7);
                                animateSnapToEdge(false);

                            } else if (inViewInBounds(mBottomZoneGradient, (int) rawX, (int) rawY)) {
                                //cleaning up
                                if (chatHead != null) {
                                    windowManager.removeView(mRelativeLayout);
                                }
                                if (mCloneRL != null && mCloneRL.getParent() != null) {
                                    windowManager.removeView(mCloneRL);
                                }
                                mVibrator.vibrate(100);
                                stopSelf();
                            }

                            //clean up
                            mBottomZoneGradient.removeView(closeButtonHalo);
                            windowManager.removeView(mBottomZoneGradient);
                            break;

                        case MotionEvent.ACTION_MOVE:

                            mRLParams.setMargins(0, 0, 0, 0);
                            mRelativeLayout.updateViewLayout(chatHead, mRLParams);

                            mWMParams.x = initialX + (int) (event.getRawX() - initialTouchX);
                            mWMParams.y = initialY + (int) (event.getRawY() - initialTouchY);
                            windowManager.updateViewLayout(mRelativeLayout, mWMParams);

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

    private void animateSnapToEdge(final boolean onTheLeft) {
        //animate the new icon, then replace it with the old one at the end


        mCloneImage.animate().x((float)mWMParams.x).setDuration(1500).setInterpolator(new OvershootInterpolator(2.0f)).setListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animator) {

                windowManager.updateViewLayout(mRelativeLayout, mWMParams);
                if (onTheLeft) {
                    mRLParams.leftMargin = -(int) (chatHead.getWidth() * 0.3);
                    mRLParams.rightMargin = (int) (chatHead.getWidth() * 0.3);
                } else {
                    mRLParams.leftMargin = (int) (chatHead.getWidth() * 0.3);
                    mRLParams.rightMargin = -(int) (chatHead.getWidth() * 0.3);
                }
                mRelativeLayout.updateViewLayout(chatHead, mRLParams);
                mRelativeLayout.setVisibility(View.VISIBLE);
                mCloneRL.animate().alpha(0.0f).setDuration(mShortAnimationDuration).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        windowManager.removeView(mCloneRL);
                    }
                });
            }
        });
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

    }


}
