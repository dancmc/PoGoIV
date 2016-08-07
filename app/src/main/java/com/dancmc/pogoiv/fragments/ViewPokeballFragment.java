package com.dancmc.pogoiv.fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dancmc.pogoiv.utilities.Pokeball;
import com.dancmc.pogoiv.utilities.Pokeballs;
import com.dancmc.pogoiv.database.PokeballsDataSource;
import com.dancmc.pogoiv.R;
import com.dancmc.pogoiv.adapters.ViewPokeballRecyclerViewAdapter;
import com.dancmc.pogoiv.activities.AddPokemonActivity;
import com.dancmc.pogoiv.activities.MainActivity;
import com.dancmc.pogoiv.utilities.Pokemon;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;


/**
 * A simple {@link Fragment} subclass.
 */
public class ViewPokeballFragment extends ContractFragment<ViewPokeballFragment.Contract> {


    private ArrayList<double[]> mResult;

    public ViewPokeballFragment() {
        // Required empty public constructor
    }

    public static ViewPokeballFragment newInstance(int pokeballNumber) {
        ViewPokeballFragment myFragment = new ViewPokeballFragment();

        Bundle args = new Bundle();
        args.putInt("pokeballNumber", pokeballNumber);

        myFragment.setArguments(args);

        return myFragment;
    }

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
    private static final String TAG = "ViewPokeballFragment";

    private Toolbar mToolbar;
    private LinearLayout mToolbarContainer;
    private PokeballsDataSource mDataSource;
    private ViewPokeballRecyclerViewAdapter mAdapter;
    private RelativeLayout mMainLayout;
    private DecimalFormat mDF;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_view_pokeball, container, false);

        //everything that is affected by an edit async goes in an update method to be called on database finish, everything based on Pokeballs.getInstance stays here
        mPokeballNumber = getArguments().getInt("pokeballNumber");

        //updated immediately by edit pokemon, not involved in database, only comparison stuff is. Basically anything that comes after compareAllPokemon is called
        mPokeball = Pokeballs.getPokeballsInstance().get(mPokeballNumber);
        mDF = new DecimalFormat("0.0");

        mPokemonImage = (ImageView) v.findViewById(R.id.pokeball_view_image);
        mNickname = (TextView) v.findViewById(R.id.pokeball_view_nickname);
        mEditNickname = (EditText) v.findViewById(R.id.pokeball_view_nickname_edittext);

        mIVPercent = (TextView) v.findViewById(R.id.pokeball_view_IV_percent);
        mIVPercentDesc = (TextView) v.findViewById(R.id.pokeball_view_IV_percent_desc);
        mCPPercent = (TextView) v.findViewById(R.id.pokeball_view_CP_percent);
        mCPPercentDesc = (TextView) v.findViewById(R.id.pokeball_view_CP_percent_desc);
        mSummary = (TextView) v.findViewById(R.id.pokeball_view_summary);
        mComparison = (Button) v.findViewById(R.id.pokeball_view_comparison);
        mFAB = (FloatingActionButton) v.findViewById(R.id.add_to_existing_pokeball_fab);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.view_pokeball_recyclerview);
        mDataSource = new PokeballsDataSource(getActivity());
        mToolbar = (Toolbar) v.findViewById(R.id.toolbar);
        mToolbarContainer = (LinearLayout) v.findViewById(R.id.toolbar_container);
        mMainLayout = (RelativeLayout) v.findViewById(R.id.view_pokeball_main_layout) ;
        mToolbar.setTitle("Viewing Pokeball");

        FloatingActionButton fab = (FloatingActionButton)v.findViewById(R.id.back_button_viewpokeball);
        fab.setVisibility(View.GONE);

        if (getActivity().getClass().getSimpleName().equals("AddPokemonActivity")) {
            mToolbarContainer.setVisibility(View.GONE);

            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams)mMainLayout.getLayoutParams();
            params.topMargin = 0;
            mMainLayout.setLayoutParams(params);
        }

        mPokemonImage.setImageResource(getResources().getIdentifier(Pokemon.getPngFileName(mPokeball.getHighestEvolvedPokemonNumber()), "drawable", getActivity().getPackageName()));
        mNickname.setText(mPokeball.get(0).getNickname());
        mEditNickname.setVisibility(View.INVISIBLE);

        mNickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditNickname.setVisibility(View.VISIBLE);
                mEditNickname.setText(mNickname.getText().toString());
                mNickname.setVisibility(View.INVISIBLE);
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(mEditNickname, InputMethodManager.SHOW_IMPLICIT);
                mEditNickname.requestFocus();
                mEditNickname.selectAll();
            }
        });

        mEditNickname.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow((null == getActivity().getCurrentFocus()) ? null : getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
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

        Log.d(TAG, "onCreateView: " + getActivity().getClass().getSimpleName());
        if (getActivity() instanceof MainActivity) {
            Log.d(TAG, "onCreateView: got here");
            if (((MainActivity) getActivity()).getSaveAsyncStatus() == AsyncTask.Status.RUNNING || ((MainActivity) getActivity()).getSaveAsyncStatus() == AsyncTask.Status.PENDING) {
                mSummary.setText("RE-CALCULATING.....");
                mIVPercent.setText("--%");
                mIVPercentDesc.setText("IV%\n" + ("--"));
                mCPPercent.setText("--%");
                mCPPercentDesc.setText("IV%\n" + ("--"));
                mComparison.setEnabled(false);
            } else {
                updateBasedOnCompare();
            }
        } else if (getActivity() instanceof AddPokemonActivity) {
            if (((AddPokemonActivity) getActivity()).getSaveAsyncStatus() == AsyncTask.Status.RUNNING || ((AddPokemonActivity) getActivity()).getSaveAsyncStatus() == AsyncTask.Status.PENDING) {
                mSummary.setText("RE-CALCULATING.....");
                mIVPercent.setText("--%");
                mIVPercentDesc.setText("IV%\n" + ("--"));
                mCPPercent.setText("--%");
                mCPPercentDesc.setText("IV%\n" + ("--"));
                mComparison.setEnabled(false);
            } else {
                updateBasedOnCompare();
            }
        }


        mAdapter = new ViewPokeballRecyclerViewAdapter(getActivity(), mPokeballNumber);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                AlertDialog.Builder builder2 = new AlertDialog.Builder(getActivity());
                builder2.setTitle("Delete")
                        .setMessage("Do you want to delete this Pokemon?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

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
                                        if (getActivity() != null&&pokeballSize!=0) {
                                            updateBasedOnCompare();
                                        }
                                    }
                                }.execute();

                                Toast.makeText(getActivity(), "Pokemon deleted", Toast.LENGTH_LONG)
                                        .show();

                                //stop before gets to notifyadapter if there is no pokemon left
                                if (Pokeballs.getPokeballsInstance().get(mPokeballNumber).size() == 0) {
                                    Pokeballs.getPokeballsInstance().remove(mPokeballNumber);
                                    getContract().onDeleteLastPokemon();
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
                                mPokemonImage.setImageResource(getResources().getIdentifier(Pokemon.getPngFileName(mPokeball.getHighestEvolvedPokemonNumber()), "drawable", getActivity().getPackageName()));

                                //can either make a new adapter or use a method to reset internal data
                                mAdapter.updateAdapter();
                                mAdapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                AlertDialog dialog2 = builder2.create();
                dialog2.show();
                return true;


            }
        });

        mAdapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (view.getId() == R.id.view_pokemon_recycler_text || view.getId() == R.id.view_pokemon_recycler_image) {
                    getContract().moreInfoButtonPressed(Pokeballs.getPokeballsInstance().get(mPokeballNumber).get(position));
                } else if (view.getId()==R.id.view_pokemon_recycler_edit){
                    getContract().editPokemon(mPokeballNumber,position);
                }

            }

        });


        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContract().addToExistingPokeball(mPokeballNumber);
            }
        });

        return v;
    }


    public interface Contract {
        public void onViewSummaryClick(Pokemon pokemon, ArrayList<double[]> ivCombos);

        public void addToExistingPokeball(int pokeballPosition);

        public void onDeleteLastPokemon();

        public void editPokemon(int pokeballPosition, int pokeballListPosition);

        void moreInfoButtonPressed(Pokemon pokemon);

    }

    private void updateBasedOnCompare() {
        mResult = mDataSource.compareAllPokemon(mPokeballNumber);
        ArrayList<Double> percentRange = new ArrayList<>();

        mPokemonImage.setImageResource(getResources().getIdentifier(Pokemon.getPngFileName(mPokeball.getHighestEvolvedPokemonNumber()), "drawable", getActivity().getPackageName()));
        mNickname.setText(mPokeball.get(0).getNickname());

        if (mResult.size() == 0) {
            mSummary.setText("There were no overlapping combinations found, are these the same pokemon? (You can try editing all Pokemon to 'powered up'.)");
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

        mComparison.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContract().onViewSummaryClick(Pokeballs.getPokeballsInstance().get(mPokeballNumber).get(0), mResult);
            }
        });
    }


    public void editAsyncFinished() {
        updateBasedOnCompare();
        mComparison.setEnabled(true);
    }
}
