package com.dancmc.pogoiv.services;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dancmc.pogoiv.R;
import com.dancmc.pogoiv.utilities.LevelAngle;
import com.dancmc.pogoiv.utilities.Pokemon;

import java.util.zip.Inflater;

public class OverlayService extends GenericFloatingService {

    private static final String TAG = "OverlayService";
    private LevelAngle mLevelAngleView;
    private AutoCompleteTextView mPokemonNameInput;
    private TextView mPokemonNameDisplay;
    private TextView mTrainerLevelText;
    private TextView mPokemonLevelText;
    private TextView mCPText;
    private TextView mHPText;
    private int mPokemonNumber;

    private ImageButton mTrainerPlus;
    private ImageButton mTrainerMinus;
    private ImageButton mPokemonPlus;
    private ImageButton mPokemonMinus;
    private ImageButton mCPPlus;
    private ImageButton mCPMinus;
    private ImageButton mHPPlus;
    private ImageButton mHPMinus;

    private SeekBar mTrainerLevelBar;
    private int mTrainerLevel;
    private SeekBar mPokemonLevelBar;
    private int mPokemonLevel;
    private SeekBar mCPBar;
    private int mCPInput;
    private int mCPMin;
    private int mCPMax;
    private SeekBar mHPBar;
    private int mHPInput;
    private int mHPMin;
    private int mHPMax;
    private Button mCalculateButton;

    View v;
    private static Context mContext;
    public static boolean isRunning;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: ");
            windowManager.removeView(v);
            stopSelf();
        }
    };



    @Override
    public void onCreate() {
        super.onCreate();

        v = View.inflate(this, R.layout.fragment_overlay,  null);

        //standard setup
        FloatingHead.currentlyRunningServiceFragment = OVERLAY_SERVICE;
        startService(new Intent(this, FloatingHead.class));
        this.registerReceiver(mReceiver, new IntentFilter("stop overlay"));

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
        mScreenMetrics = new DisplayMetrics();
        display.getMetrics(mScreenMetrics);

        mWMParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        mLevelAngleView = (LevelAngle) v.findViewById(R.id.level_angle_display);
        mPokemonNameInput = (AutoCompleteTextView) v.findViewById(R.id.enter_pokemon_name);
        mTrainerLevelText = (TextView) v.findViewById(R.id.textview_trainer_level);
        mPokemonLevelText = (TextView) v.findViewById(R.id.textview_pokemon_level);
        mPokemonNameDisplay = (TextView) v.findViewById(R.id.pokemon_name_display);
        mCPText = (TextView) v.findViewById(R.id.textview_cp);
        mHPText = (TextView) v.findViewById(R.id.textview_hp);

        mTrainerPlus = (ImageButton) v.findViewById(R.id.button_plus_trainer_level);
        mTrainerMinus = (ImageButton) v.findViewById(R.id.button_minus_trainer_level);
        mPokemonPlus = (ImageButton) v.findViewById(R.id.button_plus_pokemon_level);
        mPokemonMinus = (ImageButton) v.findViewById(R.id.button_minus_pokemon_level);
        mCPPlus = (ImageButton) v.findViewById(R.id.button_plus_cp);
        mCPMinus = (ImageButton) v.findViewById(R.id.button_minus_cp);
        mHPPlus = (ImageButton) v.findViewById(R.id.button_plus_hp);
        mHPMinus = (ImageButton) v.findViewById(R.id.button_minus_hp);

        mTrainerLevelBar = (SeekBar) v.findViewById(R.id.seekbar_trainer_level);
        mPokemonLevelBar = (SeekBar) v.findViewById(R.id.seekbar_pokemon_level);
        mCPBar = (SeekBar) v.findViewById(R.id.seekbar_cp);
        mHPBar = (SeekBar) v.findViewById(R.id.seekbar_hp);
        mCalculateButton = (Button) v.findViewById(R.id.overlay_calculate_button);

        sp = PreferenceManager.getDefaultSharedPreferences(this);
        ed = sp.edit();



        mTrainerLevel = sp.getInt("OverlayService_TrainerLevel", 11);
        mPokemonLevel = sp.getInt("OverlayService_PokemonLevel", 10);
        mPokemonNumber = sp.getInt("OverlayService_PokemonNumber", 0);
        mCPInput = sp.getInt("OverlayService_CPInput",1);
        mHPInput = sp.getInt("OverlayService_HPInput", 1);
        mPokemonNameDisplay.setText(sp.getString("OverlayService_PokemonName", "Enter Pokemon Name"));

        //Autocomplete textview setup
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, Pokemon.getPokedex());
        mPokemonNameInput = (AutoCompleteTextView) v.findViewById(R.id.enter_pokemon_name);
        mPokemonNameInput.setAdapter(adapter);

        inputSetup();


        //SETUP trainer bar
        mTrainerLevelBar.setMax(39);
        mTrainerPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTrainerLevelBar.getProgress() < 39) {
                    mTrainerLevelBar.setProgress(mTrainerLevelBar.getProgress() + 1);
                }
            }
        });
        mTrainerMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTrainerLevelBar.getProgress() > 0) {
                    mTrainerLevelBar.setProgress(mTrainerLevelBar.getProgress() - 1);
                }
            }
        });
        mTrainerLevelBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mTrainerLevel = progress + 1;
                ed.putInt("OverlayService_TrainerLevel", mTrainerLevel).apply();
                if (mTrainerLevel < 39) {
                    mPokemonLevelBar.setMax(mTrainerLevel * 2 + 1);
                } else {
                    mPokemonLevelBar.setMax(78);
                }
                mTrainerLevelText.setText("Trainer Level : " + mTrainerLevel);
                mLevelAngleView.setTrainerLevel(mTrainerLevel);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        mTrainerLevelBar.setProgress(mTrainerLevel-1);

        //SETUP pokemon bar
        mPokemonLevelBar.setMax(mTrainerLevel*2+1);
        mPokemonPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPokemonLevelBar.getProgress() < mPokemonLevelBar.getMax()) {
                    mPokemonLevelBar.setProgress(mPokemonLevelBar.getProgress() + 1);
                }
            }
        });
        mPokemonMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPokemonLevelBar.getProgress() > 0) {
                    mPokemonLevelBar.setProgress(mPokemonLevelBar.getProgress() - 1);
                }
            }
        });
        mPokemonLevelBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mPokemonLevelText.setText("Pokemon Level : " + (progress * 0.5 + 1));
                mPokemonLevel = progress+1;
                ed.putInt("OverlayService_PokemonLevel", (mPokemonLevel+1)/2).apply();
                alterCPHPBars();
                mLevelAngleView.setPokemonLevel(progress*0.5f+1);
                Log.d(TAG, "pokemon level " + mPokemonLevel);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mPokemonLevelBar.setProgress((int)((mPokemonLevel-1)/0.5));

        //SETUP CP bar
        mCPBar.setMax(1);
        mCPPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCPBar.getProgress() < mCPBar.getMax()) {
                    mCPBar.setProgress(mCPBar.getProgress() + 1);
                }
            }
        });
        mCPMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCPBar.getProgress() > 0) {
                    mCPBar.setProgress(mCPBar.getProgress() - 1);
                }
            }
        });
        mCPBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser) {
                    mCPInput = mCPMin + progress;
                    ed.putInt("OverlayService_CPInput", mCPInput).apply();
                    mCPText.setText("CP : " + mCPInput);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        //SETUP HP bar
        mHPBar.setMax(1);
        mHPPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mHPBar.getProgress() < mHPBar.getMax()) {
                    mHPBar.setProgress(mHPBar.getProgress() + 1);
                }
            }
        });
        mHPMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mHPBar.getProgress() > 0) {
                    mHPBar.setProgress(mHPBar.getProgress() - 1);
                }
            }
        });
        mHPBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser) {
                    mHPInput = mHPMin + progress;
                    ed.putInt("OverlayService_HPInput", mHPInput).apply();
                    mHPText.setText("HP : " + mHPInput);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        Log.d(TAG, "onCreateView: Overlay Service");
        alterCPHPBars();

        windowManager.addView(v,mWMParams);
        FloatingHead.bringToFront();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void inputSetup() {
        mPokemonNameInput.setVisibility(View.INVISIBLE);
        mPokemonNameDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPokemonNameInput.setVisibility(View.VISIBLE);
                mPokemonNameInput.setText(mPokemonNameDisplay.getText().toString());
                mPokemonNameDisplay.setVisibility(View.INVISIBLE);
                InputMethodManager imm = (InputMethodManager) ((Activity)mContext).getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(mPokemonNameInput, InputMethodManager.SHOW_IMPLICIT);
                mPokemonNameInput.requestFocus();
                mPokemonNameInput.selectAll();
            }
        });

        mPokemonNameInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager inputManager = (InputMethodManager) ((Activity)mContext).getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow((null == ((Activity)mContext).getCurrentFocus()) ? null : ((Activity)mContext).getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    mPokemonNameInput.clearFocus();
                    return true;
                }
                return false;
            }
        });

        mPokemonNameInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    mPokemonNameDisplay.setVisibility(View.VISIBLE);
                    final String nickname = mPokemonNameInput.getText().toString();
                    if (nickname.trim().equals("")) {
                        mPokemonNameDisplay.setText("Enter Pokemon Name");
                        mPokemonNameDisplay.setTextColor(Color.parseColor("#EF5350"));
                    } else if (Pokemon.getPokemonNumberFromName(nickname) == 0) {
                        Toast.makeText(OverlayService.this, "Invalid Pokemon Name", Toast.LENGTH_SHORT)
                                .show();
                        mPokemonNameDisplay.setText("Enter Pokemon Name");
                        mPokemonNameDisplay.setTextColor(Color.parseColor("#EF5350"));
                    }else {
                        mPokemonNameDisplay.setText(nickname);
                        mPokemonNameDisplay.setTextColor(Color.parseColor("#0277BD"));
                        mPokemonNumber=Pokemon.getPokemonNumberFromName(nickname);
                        ed.putInt("OverlayService_PokemonNumber", mPokemonNumber).apply();
                        ed.putString("OverlayService_PokemonName", mPokemonNameDisplay.getText().toString()).apply();
                        alterCPHPBars();
                    }
                    mPokemonNameInput.setVisibility(View.INVISIBLE);

                }

            }
        });
    }

    private void alterCPHPBars(){
        mCPMax = Pokemon.calculateMaxCPAtLevel(mPokemonNumber,mPokemonLevel);
        mCPMin = Pokemon.calculateMinCPAtLevel(mPokemonNumber,mPokemonLevel);
        mCPBar.setMax(mCPMax-mCPMin);

        if(mCPInput>=mCPMin&&mCPInput<=mCPMax){
            mCPBar.setProgress(mCPInput-mCPMin);
        } else if (mCPInput>mCPMax){
            mCPBar.setProgress(mCPBar.getMax());
            mCPInput = mCPMax;
        } else if (mCPInput<mCPMin){
            mCPBar.setProgress(0);
            mCPInput = mCPMin;
        }
        mCPText.setText("CP : "+ mCPInput);
        ed.putInt("OverlayService_CPInput", mCPInput).apply();

        mHPMax = Pokemon.calculateMaxHPAtLevel(mPokemonNumber,mPokemonLevel);
        mHPMin = Pokemon.calculateMinHPAtLevel(mPokemonNumber,mPokemonLevel);
        mHPBar.setMax(mHPMax-mHPMin);

        if(mHPInput>=mHPMin&&mHPInput<=mHPMax){
            mHPBar.setProgress(mHPInput-mHPMin);
        } else if (mHPInput>mHPMax){
            mHPBar.setProgress(mHPBar.getMax());
            mHPInput = mHPMax;
        } else if (mHPInput<mHPMin){
            mHPBar.setProgress(0);
            mHPInput = mHPMin;
        }
        mHPText.setText("HP : "+ mHPInput);
        ed.putInt("OverlayService_HPInput", mHPInput).apply();
    }

    public static void setContext(Context context){
        mContext = context;
    }

}
