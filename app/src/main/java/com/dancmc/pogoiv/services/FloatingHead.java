package com.dancmc.pogoiv.services;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Build;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.dancmc.pogoiv.R;
import com.dancmc.pogoiv.activities.MainActivity;
import com.dancmc.pogoiv.activities.OverlayActivity;

public class FloatingHead extends Service {

    public static boolean mRunning;
    private static final String TAG = "FloatingHead";
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
                (int) (85 * mScreenMetrics.density), (int) (85 * mScreenMetrics.density),
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        mWMParams.gravity = Gravity.TOP | Gravity.LEFT;
        mWMParams.x = 0;
        mWMParams.y = 200;

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

                private int numberOfMoves;
                private float moveX1;
                private float moveX2;
                private float moveY1;
                private float moveY2;
                private float totalDeltaMove;
                private long lastMoveTime;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {

                        case MotionEvent.ACTION_DOWN:

                            numberOfMoves = 0;
                            initialX = mWMParams.x;
                            initialY = mWMParams.y;
                            initialTouchX = event.getRawX();
                            initialTouchY = event.getRawY();
                            totalDeltaMove = 0;

                            //button pressed animation
                            chatHead.setImageResource(R.drawable.inset_floating_head);
                            chatHead.setAlpha(0.75f);

                            break;

                        case MotionEvent.ACTION_UP:

                            final float rawX = event.getRawX();
                            final float rawY = event.getRawY();
                            mFinalX = mWMParams.x;
                            mFinalY = mWMParams.y;

                            chatHead.setImageResource(R.drawable.floating_head);
                            chatHead.setAlpha(1.0f);
                            //create a new cloned icon with a match parent relativelayout so icon can bounce outside bounds
                            //Adding new Relative Layout to WindowManager
                            mCloneRL = new RelativeLayout(mContext);
                            windowManager.addView(mCloneRL, new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.TYPE_SYSTEM_ALERT, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT));
                            //Adding new ImageView to Relative Layout
                            mCloneImage = new ImageView(mContext);
                            mCloneImage.setImageResource(R.drawable.floating_head);
                            mCloneRLParams = new RelativeLayout.LayoutParams((int) (85 * mScreenMetrics.density), (int) (85 * mScreenMetrics.density));

                            mCloneRL.addView(mCloneImage, mCloneRLParams);
                            mCloneImage.setTranslationX(mFinalX);
                            mCloneImage.setTranslationY(mFinalY);

                            //hide dragged main icon, only cloned one visible
                            mRelativeLayout.setVisibility(View.INVISIBLE);

                            //handle click with negligible move
                            Log.d(TAG, "onTouch: " + totalDeltaMove);
                            if (Math.abs(totalDeltaMove) < 30f) {
                                if (OverlayActivity.overlayIsRunning()) {
                                    Log.d(TAG, "onTouch: " + OverlayActivity.overlayIsRunning());
                                    sendBroadcast(new Intent().setAction("stop overlay"));
                                } else {
                                    Intent window = new Intent(mContext, OverlayActivity.class);
                                    window.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    OverlayActivity.mIsIntentionalCall = true;
                                    startActivity(window);


                                }

                            }

                            //Set the X value for where cloned icon should end up
                            if (numberOfMoves > 2) {
                                if ((mWMParams.x < mScreenMetrics.widthPixels / 2 - chatHead.getWidth() / 2) && !inViewInBounds(mBottomZoneGradient, (int) rawX, (int) rawY)) {
                                    mWMParams.x = -(int) (chatHead.getWidth() * 0.2);
                                    //animate the new icon, then replace it with the old one at the end
                                    animateSnapToEdge(true);

                                } else if ((mWMParams.x > mScreenMetrics.widthPixels / 2 - chatHead.getWidth() / 2) && !inViewInBounds(mBottomZoneGradient, (int) rawX, (int) rawY)) {
                                    mWMParams.x = mScreenMetrics.widthPixels - (int) (chatHead.getWidth() * 0.8);
                                    animateSnapToEdge(false);

                                } else if (inViewInBounds(mBottomZoneGradient, (int) rawX, (int) rawY)) {
                                    //closing service, cleaning up
                                    if (chatHead != null) {
                                        windowManager.removeView(mRelativeLayout);
                                    }
                                    if (mCloneRL != null && mCloneRL.getParent() != null) {
                                        windowManager.removeView(mCloneRL);
                                    }
                                    sendBroadcast(new Intent().setAction("stop overlay"));
                                    mVibrator.vibrate(10);
                                    stopSelf();
                                }
                                //clean up bottom zone on finger up
                                mBottomZoneGradient.removeView(closeButtonHalo);
                                windowManager.removeView(mBottomZoneGradient);
                            } else {
                                mRelativeLayout.setVisibility(View.VISIBLE);
                                mCloneRL.animate().alpha(0.0f).setDuration(mShortAnimationDuration).setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        windowManager.removeView(mCloneRL);
                                    }
                                });

                            }


                            break;

                        case MotionEvent.ACTION_MOVE:

                            if (numberOfMoves == 0) {
                                moveX1 = event.getRawX();
                                moveY1 = event.getRawY();
                                totalDeltaMove += (moveX1 - initialTouchX + moveY1 - initialTouchY);
                                numberOfMoves += 1;
                                break;
                            } else if (numberOfMoves == 1) {
                                moveX2 = event.getRawX();
                                moveY2 = event.getRawY();
                                totalDeltaMove += (moveX2 - moveX1 + moveY2 - moveY1);
                                numberOfMoves += 1;
                                break;
                            } else {
                                moveX1 = moveX2;
                                moveY1 = moveY2;
                                moveX2 = event.getRawX();
                                moveY2 = event.getRawY();
                                totalDeltaMove += (moveX2 - moveX1 + moveY2 - moveY1);
                                if (numberOfMoves == 2) {
                                    numberOfMoves += 1;
                                    //add bottom zone
                                    WindowManager.LayoutParams rlBottomParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, (int) (150 * mScreenMetrics.density), WindowManager.LayoutParams.TYPE_SYSTEM_ALERT, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
                                    rlBottomParams.gravity = Gravity.BOTTOM;
                                    mBottomZoneGradient.setBackgroundResource(R.drawable.bottom_shadow);
                                    windowManager.addView(mBottomZoneGradient, rlBottomParams);

                                    break;
                                }
                                numberOfMoves += 1;

                            }

                            mRLParams.setMargins(0, 0, 0, 0);
                            mRelativeLayout.updateViewLayout(chatHead, mRLParams);

                            mWMParams.x = initialX + (int) (event.getRawX() - initialTouchX);
                            mWMParams.y = initialY + (int) (event.getRawY() - initialTouchY);
                            windowManager.updateViewLayout(mRelativeLayout, mWMParams);

                            if (inViewInBounds(mBottomZoneGradient, (int) event.getRawX(), (int) event.getRawY()) && closeButtonHalo.getParent() == null) {
                                mBottomZoneGradient.addView(closeButtonHalo, llp);
                                mVibrator.vibrate(10);

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


        mCloneImage.animate().x((float) mWMParams.x).setDuration(750).setInterpolator(new OvershootInterpolator(2.0f)).setListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animator) {

                windowManager.updateViewLayout(mRelativeLayout, mWMParams);
                if (onTheLeft) {
                    mRLParams.leftMargin = -(int) (chatHead.getWidth() * 0.2);
                    mRLParams.rightMargin = (int) (chatHead.getWidth() * 0.2);
                } else {
                    mRLParams.leftMargin = (int) (chatHead.getWidth() * 0.2);
                    mRLParams.rightMargin = -(int) (chatHead.getWidth() * 0.2);
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



