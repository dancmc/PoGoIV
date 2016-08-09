package com.dancmc.pogoiv.views;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dancmc.pogoiv.R;
import com.dancmc.pogoiv.activities.AddPokemonActivity;
import com.dancmc.pogoiv.activities.MainActivity;
import com.dancmc.pogoiv.adapters.ViewPokeballRecyclerViewAdapter;
import com.dancmc.pogoiv.adapters.ViewPokeballViewRecyclerViewAdapter;
import com.dancmc.pogoiv.database.PokeballsDataSource;
import com.dancmc.pogoiv.services.FloatingHead;
import com.dancmc.pogoiv.utilities.CustomDialog;
import com.dancmc.pogoiv.utilities.CustomToast;
import com.dancmc.pogoiv.utilities.Pokeball;
import com.dancmc.pogoiv.utilities.Pokeballs;
import com.dancmc.pogoiv.utilities.Pokemon;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Daniel on 6/08/2016.
 */
public class ViewPokeballView extends GenericServiceView {

    private ArrayList<double[]> mResult;
    private int mPokeballNumber;
    private Pokeball mPokeball;
    private ImageView mPokemonImage;
    private TextView mNickname;
    private EditText mEditNickname;

    private TextView mIVPercent;
    private TextView mIVPercentDesc;
    private TextView mCPPercent;
    private TextView mCPPercentDesc;
    private TextView mSummary;
    private Button mComparison;
    private RecyclerView mRecyclerView;
    private FloatingActionButton mFAB;
    private static final String TAG = "ViewPokeballView";

    private PokeballsDataSource mDataSource;
    private ViewPokeballViewRecyclerViewAdapter mAdapter;
    private RelativeLayout mMainLayout;
    private FrameLayout mFrameLayout;


    public ViewPokeballView(Context context) {
        //standard setup. Must call a super method GenericServiceView has no default constructor
        super(context);
        v = View.inflate(mContext, R.layout.view_service_view_pokeball, null);

        if(!FloatingHead.viewMode) {
            //setting up persistent Add Card at the top of screen
            View cardView = View.inflate(mContext, R.layout.adhoc_overlay_addpokemon_cardview, null);
            ImageView addCardImage = (ImageView) cardView.findViewById(R.id.overlay_add_pokemon_card_image);
            TextView addCardCP = (TextView) cardView.findViewById(R.id.overlay_add_pokemon_card_cp);
            TextView addCardHP = (TextView) cardView.findViewById(R.id.overlay_add_pokemon_card_hp);
            TextView addCardLevel = (TextView) cardView.findViewById(R.id.overlay_add_pokemon_card_level);
            TextView addCardIV = (TextView) cardView.findViewById(R.id.overlay_add_pokemon_card_IV);
            TextView addCardPrompt = (TextView) cardView.findViewById(R.id.overlay_add_pokemon_card_text_prompt);
            LinearLayout insertPoint = (LinearLayout) v.findViewById(R.id.overlay_insertpoint_viewpokeball);


            addCardImage.setImageResource(mContext.getResources().getIdentifier(Pokemon.getPngFileName(mPokemon.getPokemonNumber()), "drawable", mContext.getPackageName()));
            if (mPokemon.getCP() > 0) {
                addCardCP.setText("" + mPokemon.getCP());
            } else {
                addCardCP.setText("nil");
            }
            if (mPokemon.getHP() > 0) {
                addCardHP.setText("" + mPokemon.getHP());
            } else {
                addCardHP.setText("nil");
            }
            addCardLevel.setText("" + mPokemon.getKnownLevelConverted());
            if (mPokemon.getAverageIVPercent() > 0) {
                addCardIV.setText((int) mPokemon.getAverageIVPercent() + "%");
            } else {
                addCardIV.setText("nil");
            }
            addCardPrompt.setText("Press the + button to add to this Pokeball.");
            insertPoint.addView(cardView, 0);
        }

        mPokeballNumber = FloatingHead.mPokeboxClickedPosition;
        try {
            mPokeball = Pokeballs.getPokeballsInstance().get(mPokeballNumber);
        } catch (Exception e) {
            return;
        }
        mPokemonImage = (ImageView) v.findViewById(R.id.overlay_pokeball_view_image);
        mNickname = (TextView) v.findViewById(R.id.overlay_pokeball_view_nickname);
        mEditNickname = (EditText) v.findViewById(R.id.overlay_pokeball_view_nickname_edittext);

        mIVPercent = (TextView) v.findViewById(R.id.overlay_pokeball_view_IV_percent);
        mIVPercentDesc = (TextView) v.findViewById(R.id.overlay_pokeball_view_IV_percent_desc);
        mCPPercent = (TextView) v.findViewById(R.id.overlay_pokeball_view_CP_percent);
        mCPPercentDesc = (TextView) v.findViewById(R.id.overlay_pokeball_view_CP_percent_desc);
        mSummary = (TextView) v.findViewById(R.id.overlay_pokeball_view_summary);
        mComparison = (Button) v.findViewById(R.id.overlay_pokeball_view_comparison);
        mFAB = (FloatingActionButton) v.findViewById(R.id.overlay_add_to_existing_pokeball_fab);

        mFrameLayout = (FrameLayout) v.findViewById(R.id.overlay_frame_layout_viewpokeball);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.overlay_view_pokeball_recyclerview);
        mDataSource = new PokeballsDataSource(mContext);

        mMainLayout = (RelativeLayout) v.findViewById(R.id.overlay_view_pokeball_main_layout);


        mComparison.setVisibility(View.GONE);


        mPokemonImage.setImageResource(mContext.getResources().getIdentifier(Pokemon.getPngFileName(mPokeball.getHighestEvolvedPokemonNumber()), "drawable", mContext.getPackageName()));
        mNickname.setText(mPokeball.get(0).getNickname());
        mEditNickname.setVisibility(View.INVISIBLE);

        mNickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditNickname.setVisibility(View.VISIBLE);
                mEditNickname.setText(mNickname.getText().toString());
                mNickname.setVisibility(View.INVISIBLE);
                mEditNickname.requestFocus();
                mEditNickname.selectAll();
                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(mEditNickname, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        mEditNickname.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager inputManager = (InputMethodManager) ((Activity)mContext).getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    mEditNickname.clearFocus();
                    return true;
                }
                return false;
            }
        });

        mEditNickname.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    mNickname.setVisibility(View.VISIBLE);
                    final String nickname = mEditNickname.getText().toString();
                    mNickname.setText(nickname);
                    Pokeballs.getPokeballsInstance().get(mPokeballNumber).setNickname(nickname);
                    for (int i = 0; i < Pokeballs.getPokeballsInstance().get(mPokeballNumber).size(); i++) {
                        Pokeballs.getPokeballsInstance().get(mPokeballNumber).get(i).setNickname(nickname);
                    }

                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... params) {
                            mDataSource.setNickname(nickname, mPokeballNumber);
                            return null;
                        }
                    }.execute();
                    mEditNickname.setVisibility(View.INVISIBLE);
                } else {

                }
            }
        });

        //use database to do comparison, initialise holder for percents

        Log.d(TAG, "onCreateView: " + mContext.getClass().getSimpleName());
        updateBasedOnCompare();


        mAdapter = new ViewPokeballViewRecyclerViewAdapter(mContext, mPokeballNumber);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                final CustomDialog dialog1 = new CustomDialog(mContext);
                dialog1.setTitle("Delete")
                        .setTitle("Delete")
                        .setMessage("Do you want to delete this snapshot?")
                        .setPositiveButton("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                //delete from SINGLETON
                                Pokeballs.getPokeballsInstance().get(mPokeballNumber).remove(position);
                                final int pokeballSize = Pokeballs.getPokeballsInstance().get(mPokeballNumber).size();

                                //delete from DATABASE
                                new AsyncTask<Void, Void, Void>() {
                                    @Override
                                    protected Void doInBackground(Void... params) {
                                        mDataSource.deletePokemon(mPokeballNumber, position);
                                        return null;
                                    }

                                    @Override
                                    protected void onPostExecute(Void aVoid) {
                                        if (mContext != null&&pokeballSize!=0) {

                                            updateBasedOnCompare();
                                        }
                                    }
                                }.execute();

                                CustomToast.makeToast(mContext).setMessage("Snapshot deleted").show();

                                //stop before gets to notifyadapter if there is no pokemon left
                                if (Pokeballs.getPokeballsInstance().get(mPokeballNumber).size() == 0) {
                                    Pokeballs.getPokeballsInstance().remove(mPokeballNumber);
                                    FloatingHead.switchService(ADD_POKEBOX_SERVICE);
                                    return;
                                }

                                //update picture with highest remaining pokemon
                                int highestTier = 0;
                                int highestEvolved = 0;
                                for (int i = 0; i < Pokeballs.getPokeballsInstance().get(mPokeballNumber).size(); i++) {
                                    int tier = Pokeballs.getPokeballsInstance().get(mPokeballNumber).get(i).getEvolutionTier();
                                    if (tier > highestTier) {
                                        highestTier = tier;
                                        highestEvolved = i;
                                    }
                                }
                                Pokeballs.getPokeballsInstance().get(mPokeballNumber).setHighestEvolvedPokemonNumber(Pokeballs.getPokeballsInstance().get(mPokeballNumber).get(highestEvolved).getPokemonNumber());
                                mPokemonImage.setImageResource(mContext.getResources().getIdentifier(Pokemon.getPngFileName(mPokeball.getHighestEvolvedPokemonNumber()), "drawable", mContext.getPackageName()));

                                //can either make a new adapter or use a method to reset internal data
                                mAdapter.updateAdapter();
                                mAdapter.notifyDataSetChanged();
                                mFrameLayout.removeView(dialog1.getView());
                            }
                        })
                        .setNegativeButton("Cancel", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mFrameLayout.removeView(dialog1.getView());
                            }
                        });
                mFrameLayout.addView(dialog1.getView());

                return true;


            }
        });


        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final CustomDialog dialog2 = new CustomDialog(mContext);
                dialog2.setTitle("Add Pokemon")
                        .setMessage("Do you want to add the snapshot to this existing Pokeball?")
                        .setPositiveButton("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Pokeball pokeball = Pokeballs.getPokeballsInstance().get(mPokeballNumber);

                                //null & no combis already handled in calc fragment
                                //handle scenario when already added pokemon
                                for (int i = 0; i < pokeball.size(); i++) {
                                    if (pokeball.get(i).customEquals(mPokemon)) {
                                        CustomToast.makeToast(mContext).setMessage("You have added the same snapshot before!").show();
                                        return;
                                    }
                                }

                                //handle scenario when different family
                                for (int i = 0; i < pokeball.size(); i++) {
                                    if (!(pokeball.get(i).getPokemonFamily().equals(mPokemon.getPokemonFamily()))) {
                                        Log.d(TAG, "onClick: " + pokeball.get(i).getPokemonFamily() + " " + mPokemon.getPokemonFamily());
                                        CustomToast.makeToast(mContext).setMessage("These Pokemon are from different families!").show();
                                        return;
                                    }
                                }

                                //handle scenario when different evolution paths (need to change code later on to handle Wurmple)
                                for (int i = 0; i < pokeball.size(); i++) {
                                    if ((pokeball.get(i).getEvolutionTier() == mPokemon.getEvolutionTier() && (pokeball.get(i).getPokemonNumber() != mPokemon.getPokemonNumber()))) {
                                        CustomToast.makeToast(mContext).setMessage("These Pokemon are from different evolution paths!").show();
                                        return;
                                    }
                                }


                                mPokemon.setNickname(pokeball.getNickname());
                                Pokeballs.getPokeballsInstance().get(mPokeballNumber).add(mPokemon);

                                int highestTier = 0;
                                int highestEvolved = 0;
                                for (int i = 0; i < Pokeballs.getPokeballsInstance().get(mPokeballNumber).size(); i++) {
                                    int tier = Pokeballs.getPokeballsInstance().get(mPokeballNumber).get(i).getEvolutionTier();
                                    if (tier > highestTier) {
                                        highestTier = tier;
                                        highestEvolved = i;
                                    }
                                }
                                Pokeballs.getPokeballsInstance().get(mPokeballNumber).setHighestEvolvedPokemonNumber(Pokeballs.getPokeballsInstance().get(mPokeballNumber).get(highestEvolved).getPokemonNumber());

                                new AsyncTask<Integer, Void, Void>() {
                                    @Override
                                    protected Void doInBackground(Integer... params) {
                                        mDataSource.setPokemonData(mPokemon, mPokeballNumber, Pokeballs.getPokeballsInstance().get(mPokeballNumber).size() - 1);
                                        return null;
                                    }
                                }.execute();
                                mFrameLayout.removeView(dialog2.getView());
                                FloatingHead.viewMode = true;
                                FloatingHead.switchService(OVERLAY_SERVICE);

                                //goes back to IV calc, so no need to refresh view
                            }
                        })
                        .setNegativeButton("Cancel", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mFrameLayout.removeView(dialog2.getView());
                            }
                        });

                mFrameLayout.addView(dialog2.getView());
            }
        });
        if (FloatingHead.viewMode == true) {
            mFAB.setVisibility(View.GONE);
        }


    }

    private void updateBasedOnCompare() {
        mResult = mDataSource.compareAllPokemon(mPokeballNumber);
        ArrayList<Double> percentRange = new ArrayList<>();

        mPokemonImage.setImageResource(mContext.getResources().getIdentifier(Pokemon.getPngFileName(mPokeball.getHighestEvolvedPokemonNumber()), "drawable", mContext.getPackageName()));
        mNickname.setText(mPokeball.get(0).getNickname());

        if (mResult.size() == 0) {
            mSummary.setText("There were no overlapping combinations found, are these the same pokemon? (You can try editing all snapshots to 'powered up'.)");
            mIVPercent.setText("--%");
            mIVPercentDesc.setText("IV%\n" + ("--"));
            mCPPercent.setText("--%");
            mCPPercentDesc.setText("IV%\n" + ("--"));
        } else {
            //calculating average IV%
            double averagePercent = 0;
            for (int i = 0; i < mResult.size(); i++) {
                averagePercent += mResult.get(i)[4];
                percentRange.add(mResult.get(i)[4]);
            }
            averagePercent = averagePercent / mResult.size();

            double lowest = Collections.min(percentRange);
            double highest = Collections.max(percentRange);


            int pokeNumber = Pokeballs.getPokeballsInstance().get(mPokeballNumber).getHighestEvolvedPokemonNumber();
            ArrayList<Double> CPRange = Pokemon.getCpPercentRangeFromIVS(mResult, pokeNumber);
            double maxedAverageCPPercent = Pokemon.calculateMaxLevelAverageCPPercent(mResult, pokeNumber);

            mIVPercent.setText((int) averagePercent + "%");
            mIVPercentDesc.setText("IV%\n" + "(" + mDF.format(lowest) + " - " + mDF.format(highest) + "%)");
            mCPPercent.setText((int) maxedAverageCPPercent + "%");
            mCPPercentDesc.setText("CP%\n" + "(" + mDF.format(Collections.min(CPRange)) + " - " + mDF.format(Collections.max(CPRange)) + "%)");
            if (Pokeballs.getPokeballsInstance().get(mPokeballNumber).size() == 1) {
                mSummary.setText("No comparison done as only one dataset for this Pokemon. " + mResult.size() + " IV combinations found.");
            } else {
                mSummary.setText(mResult.size() + " overlapping combinations found.");
            }
        }


    }
}
