package com.dancmc.pogoiv.services;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.dancmc.pogoiv.R;

import com.dancmc.pogoiv.utilities.Pokemon;
import com.dancmc.pogoiv.views.IVCalculatorCompareView;
import com.dancmc.pogoiv.views.OverlayView;
import com.dancmc.pogoiv.views.PokeboxView;
import com.dancmc.pogoiv.views.ViewPokeballView;

public class FloatingHead extends Service {

    public static Pokemon floatingPokemonToAdd;
    public static Context serviceContext;

    private boolean pressedDown;
    public static String currentlyRunningServiceFragment;
    public static boolean viewIsRunning;
    public static boolean viewMode;
    public static final String OVERLAY_SERVICE = "OVERLAY_SERVICE";
    public static final String IV_CALCULATOR_SERVICE = "IV_CALCULATOR_SERVICE";
    public static final String ADD_POKEBOX_SERVICE = "ADD_POKEBOX_SERVICE";
    public static final String ADD_VIEW_POKEBALL_SERVICE = "ADD_VIEW_POKEBALL_SERVICE";

    public static int mPokeboxClickedPosition;

    private static final String TAG = "FloatingHead";
    private static WindowManager windowManager;
    private ImageView chatHead;
    private Display display;
    private DisplayMetrics mScreenMetrics;
    private static RelativeLayout mRelativeLayout;
    private static Context mContext;
    private int mShortAnimationDuration;
    private FrameLayout mBottomZoneGradient;
    private int mFinalX;
    private int mFinalY;

    private static WindowManager.LayoutParams mWholeWindowParams;
    private static WindowManager.LayoutParams mWMParams;
    private RelativeLayout.LayoutParams mRLParams;
    private Vibrator mVibrator;

    private long mSystemTimeOnDown;

    private static View mOverlayView;
    private static View mCalcCompareView;
    private static View mPokeboxView;
    private static View mViewPokeballView;


    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        serviceContext = this;

        mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
        mScreenMetrics = new DisplayMetrics();
        display.getMetrics(mScreenMetrics);
        mShortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);

        floatingPokemonToAdd = new Pokemon("bulbasaur", -1, 300, -1, false, -1);
        viewMode = true;

        mOverlayView = (new OverlayView(mContext)).getView();
        mCalcCompareView = (new IVCalculatorCompareView(mContext)).getView();
        mPokeboxView = (new PokeboxView(mContext).getView());
        mViewPokeballView = (new ViewPokeballView(mContext).getView());


        chatHead = new ImageView(this);
        chatHead.setImageResource(R.drawable.floating_head);
        mBottomZoneGradient = new FrameLayout(mContext);


        mWMParams = new WindowManager.LayoutParams(
                (int) (85 * mScreenMetrics.density), (int) (85 * mScreenMetrics.density),
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        mWholeWindowParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                0,
                PixelFormat.TRANSLUCENT);



        mWMParams.gravity = Gravity.TOP | Gravity.LEFT;
        mWMParams.x = 0;
        mWMParams.y = (mScreenMetrics.heightPixels / 4);

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

                    final RelativeLayout.LayoutParams mCloneRLParams;
                    final ImageView mCloneImage;
                    final RelativeLayout mCloneRL;

                    switch (event.getAction()) {

                        case MotionEvent.ACTION_DOWN:

                            numberOfMoves = 0;
                            initialX = mWMParams.x;
                            initialY = mWMParams.y;
                            initialTouchX = event.getRawX();
                            initialTouchY = event.getRawY();
                            totalDeltaMove = 0;
                            pressedDown = true;

                            //button pressed animation
                            chatHead.setImageResource(R.drawable.inset_floating_head);
                            chatHead.setAlpha(0.75f);

                            mSystemTimeOnDown = System.currentTimeMillis();

                            if(viewIsRunning&&currentlyRunningServiceFragment==OVERLAY_SERVICE){
                                mOverlayView.findViewById(R.id.overlayview_bottom).animate().alpha(0.4f).setStartDelay(200).setDuration(400).setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        if(!pressedDown){
                                            mOverlayView.findViewById(R.id.overlayview_bottom).setAlpha(1.0f);
                                        }
                                    }
                                });
                                Log.d(TAG, "onTouch: alpha");
                            }

                            break;

                        case MotionEvent.ACTION_UP:

                            final float rawX = event.getRawX();
                            final float rawY = event.getRawY();
                            mFinalX = mWMParams.x;
                            mFinalY = mWMParams.y;
                            pressedDown = false;

                            if(viewIsRunning&&currentlyRunningServiceFragment==OVERLAY_SERVICE){
                                mOverlayView.findViewById(R.id.overlayview_bottom).setAlpha(1.0f);
                            }

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

                                if ((System.currentTimeMillis() - mSystemTimeOnDown )< 500) {

                                    toggleServiceView();
                                }

                            }

                            //Set the X value for where cloned icon should end up
                            if (numberOfMoves > 2) {
                                if ((mWMParams.x < mScreenMetrics.widthPixels / 2 - chatHead.getWidth() / 2) && !inViewInBounds(mBottomZoneGradient, (int) rawX, (int) rawY)) {
                                    mWMParams.x = -(int) (chatHead.getWidth() * 0.2);
                                    //animate the new icon, then replace it with the old one at the end
                                    //animateSnapToEdge(true);

                                    mCloneImage.animate().x((float) mWMParams.x).setDuration(750).setInterpolator(new OvershootInterpolator(2.0f)).setListener(new AnimatorListenerAdapter() {

                                        @Override
                                        public void onAnimationEnd(Animator animator) {

                                            refreshFloatingHead();

                                            mRLParams.leftMargin = -(int) (chatHead.getWidth() * 0.2);
                                            mRLParams.rightMargin = (int) (chatHead.getWidth() * 0.2);

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

                                } else if ((mWMParams.x > mScreenMetrics.widthPixels / 2 - chatHead.getWidth() / 2) && !inViewInBounds(mBottomZoneGradient, (int) rawX, (int) rawY)) {
                                    mWMParams.x = mScreenMetrics.widthPixels - (int) (chatHead.getWidth() * 0.8);
                                    //animateSnapToEdge(false);

                                    mCloneImage.animate().x((float) mWMParams.x).setDuration(750).setInterpolator(new OvershootInterpolator(2.0f)).setListener(new AnimatorListenerAdapter() {

                                        @Override
                                        public void onAnimationEnd(Animator animator) {

                                            refreshFloatingHead();

                                            mRLParams.leftMargin = (int) (chatHead.getWidth() * 0.2);
                                            mRLParams.rightMargin = -(int) (chatHead.getWidth() * 0.2);

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

                                } else if (inViewInBounds(mBottomZoneGradient, (int) rawX, (int) rawY)) {
                                    //CLOSE ENTIRE SERVICE AND CLEAN UP VIEWS
                                    if (chatHead != null) {
                                        removeAllViews();
                                    }
                                    if (mCloneRL != null && mCloneRL.getParent() != null) {
                                        windowManager.removeView(mCloneRL);
                                    }

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
                                        if (mCloneRL.getParent() != null) {
                                            windowManager.removeView(mCloneRL);
                                        }
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
                                    WindowManager.LayoutParams rlBottomParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, (int) (100 * mScreenMetrics.density), WindowManager.LayoutParams.TYPE_SYSTEM_ALERT, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
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


        mOverlayView.requestFocus();

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
        //TODO : remove all views if parent not null

    }


    public static void setContext(Context context) {
        mContext = context;
    }

    public static void toggleServiceView() {

        View v = new View(mContext);
        switch (currentlyRunningServiceFragment) {
            case OVERLAY_SERVICE:
                v = mOverlayView;
                break;
            case IV_CALCULATOR_SERVICE:
                v = mCalcCompareView;
                break;
            case ADD_POKEBOX_SERVICE:
                v = mPokeboxView;
                break;
            case ADD_VIEW_POKEBALL_SERVICE:
                v = mViewPokeballView;
                break;
        }

        Log.d(TAG, "toggleServiceView: " + mOverlayView);
        Log.d(TAG, "toggleServiceView: " + v);
        if (viewIsRunning) {
            InputMethodManager inputManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
            windowManager.removeView(v);
            viewIsRunning = false;
        } else {
            windowManager.addView(v, mWholeWindowParams);
            viewIsRunning = true;
            refreshFloatingHead();
        }

    }

    private static void refreshFloatingHead() {
        windowManager.removeView(mRelativeLayout);
        windowManager.addView(mRelativeLayout, mWMParams);
    }

    public static void switchService(String serviceConstant) {
        if (mOverlayView.getParent() != null)
            windowManager.removeView(mOverlayView);
        if (mCalcCompareView.getParent() != null)
            windowManager.removeView(mCalcCompareView);
        if (mPokeboxView.getParent() != null)
            windowManager.removeView(mPokeboxView);
        if (mViewPokeballView.getParent() != null)
            windowManager.removeView(mViewPokeballView);

        currentlyRunningServiceFragment = serviceConstant;

        switch (serviceConstant) {
            case (OVERLAY_SERVICE):
                windowManager.addView(mOverlayView, mWholeWindowParams);
                break;
            case (IV_CALCULATOR_SERVICE):
                Log.d(TAG, "switchService: "+mCalcCompareView.getClass().toString());
                windowManager.addView(newCalcCompareView(), mWholeWindowParams);
                break;
            case (ADD_POKEBOX_SERVICE):
                windowManager.addView(newPokeboxView(), mWholeWindowParams);
                break;
            case (ADD_VIEW_POKEBALL_SERVICE):
                windowManager.addView(newViewPokeballView(), mWholeWindowParams);
                break;
        }

        refreshFloatingHead();
    }

    private static View newCalcCompareView() {
        View a = (new IVCalculatorCompareView(mContext)).getView();
        mCalcCompareView = a;
        return mCalcCompareView;
    }

    private static View newPokeboxView() {
        View a = (new PokeboxView(mContext)).getView();
        mPokeboxView = a;
        return mPokeboxView;
    }

    private static View newViewPokeballView() {
        View a = (new ViewPokeballView(mContext)).getView();
        mViewPokeballView = a;
        return mViewPokeballView;
    }

    public static void removeAllViews() {
        windowManager.removeView(mRelativeLayout);
        switch (currentlyRunningServiceFragment) {
            case OVERLAY_SERVICE:
                if (viewIsRunning) {
                    windowManager.removeView(mOverlayView);
                }
                break;
            case IV_CALCULATOR_SERVICE:
                if (viewIsRunning) {
                    windowManager.removeView(mCalcCompareView);
                }
                break;
            case ADD_POKEBOX_SERVICE:
                if (viewIsRunning) {
                    windowManager.removeView(mPokeboxView);
                }
                break;
            case ADD_VIEW_POKEBALL_SERVICE:
                if (viewIsRunning) {
                    windowManager.removeView(mViewPokeballView);
                }
                break;
        }
    }

    public static void onBackPressed() {
        if (viewIsRunning) {
            switch (currentlyRunningServiceFragment) {
                case (OVERLAY_SERVICE):
                    toggleServiceView();
                    return;
                case (IV_CALCULATOR_SERVICE):
                    FloatingHead.viewMode=true;
                    switchService(OVERLAY_SERVICE);
                    return;
                case(ADD_POKEBOX_SERVICE):
                    if(viewMode) {
                        Log.d(TAG, "onBackPressed: "+currentlyRunningServiceFragment);
                        Log.d(TAG, "onBackPressed: "+ viewMode);
                        switchService(OVERLAY_SERVICE);

                    }else{
                        Log.d(TAG, "onBackPressed: "+currentlyRunningServiceFragment);
                        Log.d(TAG, "onBackPressed: "+ viewMode);
                        switchService(IV_CALCULATOR_SERVICE);
                    }
                    return;
                case(ADD_VIEW_POKEBALL_SERVICE):
                    switchService(ADD_POKEBOX_SERVICE);
                    return;
            } ;
        }

    }


}



