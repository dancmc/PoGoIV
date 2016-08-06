package com.dancmc.pogoiv.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewGroupCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
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
import com.dancmc.pogoiv.services.FloatingHead;
import com.dancmc.pogoiv.utilities.LevelAngle;
import com.dancmc.pogoiv.utilities.Pokemon;

/**
 * Created by Daniel on 6/08/2016.
 */
public class OverlayView {
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

    public View v;


    public Display display;
    public DisplayMetrics mScreenMetrics;
    public SharedPreferences sp;
    public SharedPreferences.Editor ed;
    public static String currentlyRunningServiceFragment;
    public static final String OVERLAY_SERVICE = "OVERLAY_SERVICE";
    public static final String IV_CALCULATOR_SERVICE = "IV_CALCULATOR_SERVICE";
    public static final String ADD_POKEBOX_SERVICE = "ADD_POKEBOX_SERVICE";
    public static final String ADD_VIEW_POKEBALL_SERVICE = "ADD_VIEW_POKEBALL_SERVICE";

    private static Context mContext;
    public static boolean isRunning;

    public OverlayView(Context context) {

        mContext=context;
        v = View.inflate(context, R.layout.fragment_overlay, null);

        //standard setup
        FloatingHead.currentlyRunningServiceFragment = OVERLAY_SERVICE;


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

        sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        ed = sp.edit();


        mTrainerLevel = sp.getInt("OverlayService_TrainerLevel", 11);
        mPokemonLevel = sp.getInt("OverlayService_PokemonLevel", 10);
        mPokemonNumber = sp.getInt("OverlayService_PokemonNumber", 0);
        mCPInput = sp.getInt("OverlayService_CPInput", 1);
        mHPInput = sp.getInt("OverlayService_HPInput", 1);
        mPokemonNameDisplay.setText(sp.getString("OverlayService_PokemonName", "Enter Pokemon Name"));

        //Autocomplete textview setup
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, Pokemon.getPokedex());
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
        mTrainerLevelBar.setProgress(mTrainerLevel - 1);

        //SETUP pokemon bar
        mPokemonLevelBar.setMax(mTrainerLevel * 2 + 1);
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
                mPokemonLevel = progress + 1;
                ed.putInt("OverlayService_PokemonLevel", (mPokemonLevel + 1) / 2).apply();
                alterCPHPBars();
                mLevelAngleView.setPokemonLevel(progress * 0.5f + 1);
                Log.d(TAG, "pokemon level " + mPokemonLevel);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mPokemonLevelBar.setProgress((int) ((mPokemonLevel - 1) / 0.5));

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
                if (fromUser) {
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
                if (fromUser) {
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
                        Toast.makeText(mContext, "Invalid Pokemon Name", Toast.LENGTH_SHORT)
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

    public View getView(){
        return v;
    }

}
