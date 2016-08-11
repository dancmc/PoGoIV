package com.dancmc.pogoiv.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dancmc.pogoiv.R;
import com.dancmc.pogoiv.fragments.SettingsFragment;
import com.dancmc.pogoiv.services.FloatingHead;
import com.dancmc.pogoiv.utilities.CustomToast;
import com.dancmc.pogoiv.utilities.LevelAngle;
import com.dancmc.pogoiv.utilities.Pokemon;
import com.example.daniel.material.RatioLayout;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

/**
 * Created by Daniel on 6/08/2016.
 */
public class OverlayView extends GenericServiceView {
    private static final String TAG = "OverlayView";
    private FrameLayout mMainLayout;
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
    private Button mPokeboxButton;


    public OverlayView(Context context) {
        //standard setup. Must call a super method GenericServiceView has no default constructor
        super(context);
        boolean overlayAdjusted = sp.getBoolean(SettingsFragment.OVERLAY_ADJUSTED, false);
        boolean adFree = sp.getBoolean("Adfree", false);

        if (overlayAdjusted && adFree) {
            v = View.inflate(context, R.layout.view_service_overlay_adjusted_adfree, null);
        } else if (overlayAdjusted && !adFree) {
            v = View.inflate(context, R.layout.view_service_overlay_adjusted, null);
            MobileAds.initialize(mContext.getApplicationContext(), "ca-app-pub-7691928644284038~9762759909");
            AdView mAdView = (AdView) v.findViewById(R.id.adview_overlay);
            AdRequest request = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                    .addTestDevice("ABDC8E5277217A63DADF93CFCE6B47DB") // An example device ID
                    .build();
            mAdView.loadAd(request);
        } else if (!overlayAdjusted && adFree) {
            v = View.inflate(context, R.layout.view_service_overlay_adfree, null);
        } else if (!overlayAdjusted && !adFree) {
            v = View.inflate(context, R.layout.view_service_overlay, null);
            AdView mAdView = (AdView) v.findViewById(R.id.adview_overlay);
            AdRequest request = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                    .addTestDevice("ABDC8E5277217A63DADF93CFCE6B47DB") // An example device ID
                    .build();
            mAdView.loadAd(request);
        }

        mMainLayout = (FrameLayout) v.findViewById(R.id.overlay_service_main_layout);

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
        mPokeboxButton = (Button) v.findViewById(R.id.overlay_to_pokebox);


        mTrainerLevel = sp.getInt("OverlayView_TrainerLevel", 11);
        mPokemonLevel = sp.getInt("OverlayView_PokemonLevel", 10);
        mPokemonNumber = sp.getInt("OverlayView_PokemonNumber", 0);
        mCPInput = sp.getInt("OverlayView_CPInput", 1);
        mHPInput = sp.getInt("OverlayView_HPInput", 1);
        mPokemonNameDisplay.setText(sp.getString("OverlayView_PokemonName", "Enter Pokemon Name"));


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
                ed.putInt("OverlayView_TrainerLevel", mTrainerLevel).apply();
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
                ed.putInt("OverlayView_PokemonLevel", (mPokemonLevel + 1) / 2).apply();
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


                mCPInput = mCPMin + progress;

                ed.putInt("OverlayView_CPInput", mCPInput).apply();
                mCPText.setText("CP : " + mCPInput);

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

                mHPInput = mHPMin + progress;
                ed.putInt("OverlayView_HPInput", mHPInput).apply();
                mHPText.setText("HP : " + mHPInput);

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

        mCalculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pokemon newPokemon = null;

                try {
                    newPokemon = new Pokemon(mPokemonNameDisplay.getText().toString(), mHPInput, mCPInput, -1, false, mPokemonLevel);
                } catch (Exception e) {
                    CustomToast.makeToast(mContext).setMessage(e.getMessage().toString()).show();
                    return;
                }

                if (newPokemon != null && newPokemon.getNumberOfResults() == 0) {
                    CustomToast.makeToast(mContext).setMessage("No combinations found!").show();
                    return;

                } else if (newPokemon != null) {
                    FloatingHead.floatingPokemonToAdd = newPokemon;
                    FloatingHead.viewMode = false;
                    FloatingHead.switchService(IV_CALCULATOR_SERVICE);
                }
            }
        });

        mPokeboxButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FloatingHead.switchService(ADD_POKEBOX_SERVICE);
            }
        });

        v.setClickable(true);
        v.setFocusable(true);
        v.setFocusableInTouchMode(true);


    }


    private void inputSetup() {
        mPokemonNameInput.setVisibility(View.INVISIBLE);
        mPokemonNameDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPokemonNameInput.setVisibility(View.VISIBLE);
                mPokemonNameInput.setText(mPokemonNameDisplay.getText().toString());
                mPokemonNameDisplay.setVisibility(View.INVISIBLE);
                mPokemonNameInput.requestFocus();
                mPokemonNameInput.selectAll();
                InputMethodManager imm = (InputMethodManager) ((Activity) mContext).getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(mPokemonNameInput, InputMethodManager.SHOW_IMPLICIT);


            }
        });

        mPokemonNameInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager inputManager = (InputMethodManager) ((Activity) mContext).getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
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
                        mPokemonNameDisplay.setBackgroundColor(Color.parseColor("#FFEB3B"));
                    } else if (Pokemon.getPokemonNumberFromName(nickname) == 0) {
                        CustomToast.makeToast(mContext).setMessage("Invalid Pokemon Name").show();
                        mPokemonNameDisplay.setText("Enter Pokemon Name");
                        mPokemonNameDisplay.setTextColor(Color.parseColor("#EF5350"));
                        mPokemonNameDisplay.setBackgroundColor(Color.parseColor("#FFEB3B"));
                    } else {
                        mPokemonNameDisplay.setText(nickname);
                        mPokemonNameDisplay.setTextColor(Color.parseColor("#0277BD"));
                        mPokemonNameDisplay.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        mPokemonNumber = Pokemon.getPokemonNumberFromName(nickname);
                        ed.putInt("OverlayView_PokemonNumber", mPokemonNumber).apply();
                        ed.putString("OverlayView_PokemonName", mPokemonNameDisplay.getText().toString()).apply();
                        alterCPHPBars();
                    }
                    mPokemonNameInput.setVisibility(View.INVISIBLE);
                    InputMethodManager inputManager = (InputMethodManager) ((Activity) mContext).getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }

            }
        });
        if (Pokemon.getPokemonNumberFromName(mPokemonNameDisplay.getText().toString()) == 0) {
            mPokemonNameDisplay.setBackgroundColor(Color.parseColor("#FFEB3B"));
        }


    }

    private void alterCPHPBars() {
        mCPMax = Pokemon.calculateMaxCPAtLevel(mPokemonNumber, mPokemonLevel);
        Log.d(TAG, "alterCPHPBars: calculate");
        mCPMin = Pokemon.calculateMinCPAtLevel(mPokemonNumber, mPokemonLevel);


        if (mCPInput >= mCPMin && mCPInput <= mCPMax) {
            mCPBar.setProgress(mCPInput - mCPMin);
        } else if (mCPInput > mCPMax) {
            mCPBar.setProgress(mCPBar.getMax());
            mCPInput = mCPMax;
        } else if (mCPInput < mCPMin) {
            mCPBar.setProgress(0);
            mCPInput = mCPMin;
        }
        mCPBar.setMax(mCPMax - mCPMin);
        mCPText.setText("CP : " + mCPInput);
        ed.putInt("OverlayView_CPInput", mCPInput).apply();

        mHPMax = Pokemon.calculateMaxHPAtLevel(mPokemonNumber, mPokemonLevel);
        mHPMin = Pokemon.calculateMinHPAtLevel(mPokemonNumber, mPokemonLevel);


        if (mHPInput >= mHPMin && mHPInput <= mHPMax) {
            mHPBar.setProgress(mHPInput - mHPMin);
        } else if (mHPInput > mHPMax) {
            mHPBar.setProgress(mHPBar.getMax());
            mHPInput = mHPMax;
        } else if (mHPInput < mHPMin) {
            mHPBar.setProgress(0);
            mHPInput = mHPMin;
        }
        mHPBar.setMax(mHPMax - mHPMin);
        mHPText.setText("HP : " + mHPInput);
        ed.putInt("OverlayView_HPInput", mHPInput).apply();
    }


}
