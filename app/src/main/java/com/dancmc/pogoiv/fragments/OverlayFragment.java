package com.dancmc.pogoiv.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dancmc.pogoiv.R;
import com.dancmc.pogoiv.utilities.LevelAngle;
import com.dancmc.pogoiv.utilities.Pokeballs;
import com.dancmc.pogoiv.utilities.Pokemon;

import org.w3c.dom.Text;

/**
 * A simple {@link Fragment} subclass.
 */
public class OverlayFragment extends Fragment {

    private static final String TAG = "OverlayFragment";
    private LevelAngle mLevelAngleView;
    private AutoCompleteTextView mPokemonNameInput;
    private TextView mPokemonNameDisplay;
    private TextView mTrainerLevelText;
    private TextView mPokemonLevelText;
    private TextView mCPText;
    private TextView mHPText;

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

    private SharedPreferences sp;
    private SharedPreferences.Editor ed;

    private int mPokemonNumber;

    public OverlayFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_overlay, container, false);

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

        sp = getActivity().getPreferences(Context.MODE_PRIVATE);
        ed = sp.edit();



        mTrainerLevel = sp.getInt("TrainerLevel", 11);
        mPokemonLevel = sp.getInt("PokemonLevel", 10);
        mPokemonNumber = sp.getInt("PokemonNumber", 0);
        mCPInput = sp.getInt("CPInput",1);
        mHPInput = sp.getInt("HPInput", 1);
        mPokemonNameDisplay.setText(sp.getString("PokemonName", "Enter Pokemon Name"));

        //Autocomplete textview setup
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, Pokemon.getPokedex());
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
        Log.d(TAG, "onCreateView: Overlay Frag");
        alterCPHPBars();

        return v;
    }




    private void inputSetup() {
        mPokemonNameInput.setVisibility(View.INVISIBLE);
        mPokemonNameDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPokemonNameInput.setVisibility(View.VISIBLE);
                mPokemonNameInput.setText(mPokemonNameDisplay.getText().toString());
                mPokemonNameDisplay.setVisibility(View.INVISIBLE);
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(mPokemonNameInput, InputMethodManager.SHOW_IMPLICIT);
                mPokemonNameInput.requestFocus();
                mPokemonNameInput.selectAll();
            }
        });

        mPokemonNameInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow((null == getActivity().getCurrentFocus()) ? null : getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
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
                        Toast.makeText(getActivity(), "Invalid Pokemon Name", Toast.LENGTH_SHORT)
                                .show();
                        mPokemonNameDisplay.setText("Enter Pokemon Name");
                        mPokemonNameDisplay.setTextColor(Color.parseColor("#EF5350"));
                    }else {
                        mPokemonNameDisplay.setText(nickname);
                        mPokemonNameDisplay.setTextColor(Color.parseColor("#0277BD"));
                        mPokemonNumber=Pokemon.getPokemonNumberFromName(nickname);
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
        Log.d(TAG, "alterCPHPBars: "+mCPMin+ " "+mCPMax);
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
    }

    @Override
    public void onPause() {
        super.onPause();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mPokemonNameInput.getWindowToken(), 0);

        ed.putInt("TrainerLevel", mTrainerLevel);
        ed.putInt("PokemonLevel", (mPokemonLevel+1)/2);
        ed.putInt("PokemonNumber", mPokemonNumber);
        ed.putInt("CPInput", mCPInput);
        ed.putInt("HPInput", mHPInput);
        ed.putString("PokemonName", mPokemonNameDisplay.getText().toString());
        ed.commit();
        Log.d(TAG, "onPause: ");
    }
}
