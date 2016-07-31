package com.dancmc.pogoiv;


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
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;


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

    Toolbar mToolbar;
    private PokeballsDataSource mDataSource;
    private ViewPokeballRecyclerViewAdapter mAdapter;
    private DecimalFormat mDF;
    private boolean asyncIsRunning;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_view_pokeball, container, false);

        //everything that is affected by an edit async goes in an update method, everything based on Pokeballs.getInstance stays here
        mPokeballNumber = getArguments().getInt("pokeballNumber");

        //updated immediately by edit pokemon, not involved in database, only comparison stuff is. Basically anything that comes after compareAllPokemon is called
        mPokeball = Pokeballs.getPokeballsInstance().get(mPokeballNumber);
        mDF = new DecimalFormat("0.0");
        asyncIsRunning = false;

        mPokemonImage = (ImageView) v.findViewById(R.id.pokeball_view_image);
        mNickname = (TextView) v.findViewById(R.id.pokeball_view_nickname);
        mEditNickname = (EditText) v.findViewById(R.id.pokeball_view_nickname_edittext);

        mIVPercent = (TextView) v.findViewById(R.id.pokeball_view_IV_percent);
        mIVPercentDesc = (TextView) v.findViewById(R.id.pokeball_view_IV_percent_desc);
        mCPPercent = (TextView) v.findViewById(R.id.pokeball_view_CP_percent);
        mCPPercentDesc = (TextView) v.findViewById(R.id.pokeball_view_CP_percent_desc);
        mSummary = (TextView) v.findViewById(R.id.pokeball_view_summary);
        mComparison = (Button) v.findViewById(R.id.pokeball_view_comparison);
        mFAB = (FloatingActionButton) v.findViewById(R.id.add_activity_fab);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.view_pokeball_recyclerview);
        mDataSource = new PokeballsDataSource(getActivity());
        mToolbar = (Toolbar) v.findViewById(R.id.fragment_view_pokeball_toolbar);
        mToolbar.setTitle("View Storage");

        if (getActivity().getClass().getSimpleName().equals("AddPokemonActivity")) {
            mToolbar.setVisibility(View.GONE);
        }

        mPokemonImage.setImageResource(getResources().getIdentifier(Pokemon.getPngFileName(mPokeball.getHighestEvolvedPokemonNumber()), "drawable", getActivity().getPackageName()));
        mPokemonImage.setBackgroundResource(R.drawable.circle_background);
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
                    mEditNickname.clearFocus();
                    InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow((null == getActivity().getCurrentFocus()) ? null : getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
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

        if (getActivity() instanceof MainActivity) {
            if (((MainActivity) getActivity()).getSaveAsyncStatus() == AsyncTask.Status.RUNNING || ((MainActivity) getActivity()).getSaveAsyncStatus() == AsyncTask.Status.PENDING) {
                mSummary.setText("RE-CALCULATING.....");
                mIVPercent.setText("--%");
                mIVPercentDesc.setText("IV%\n" + ("--"));
                mCPPercent.setText("--%");
                mCPPercentDesc.setText("IV%\n" + ("--"));
                mComparison.setEnabled(false);
            }else {
                updateBasedOnCompare();}
        } else if (getActivity() instanceof AddPokemonActivity) {
            if (((AddPokemonActivity) getActivity()).getSaveAsyncStatus() == AsyncTask.Status.RUNNING || ((MainActivity) getActivity()).getSaveAsyncStatus() == AsyncTask.Status.PENDING) {
                mSummary.setText("RE-CALCULATING.....");
                mIVPercent.setText("--%");
                mIVPercentDesc.setText("IV%\n" + ("--"));
                mCPPercent.setText("--%");
                mCPPercentDesc.setText("IV%\n" + ("--"));
                mComparison.setEnabled(false);
            }else {
                updateBasedOnCompare();}
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

                                //delete from DATABASE
                                new AsyncTask<Void, Void, Void>() {
                                    @Override
                                    protected Void doInBackground(Void... params) {
                                        mDataSource.deletePokemon(mPokeballNumber, position);
                                        return null;
                                    }
                                }.execute();

                                //delete from SINGLETON
                                Pokeballs.getPokeballsInstance().get(mPokeballNumber).remove(position);
                                if (Pokeballs.getPokeballsInstance().get(mPokeballNumber).size() == 0) {
                                    Pokeballs.getPokeballsInstance().remove(mPokeballNumber);
                                    getContract().onDeleteLastPokemon();
                                    return;
                                }

                                //can either make a new adapter or use a method to reset internal data
                                mAdapter.updateAdapter();
                                mAdapter.notifyDataSetChanged();

                                Toast.makeText(getActivity(), "Pokemon deleted", Toast.LENGTH_LONG)
                                        .show();

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
                getContract().editPokemon(mPokeballNumber, position);
            }
        });

        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContract().onAddFabClick(mPokeballNumber);
            }
        });

        return v;
    }


    public interface Contract {
        public void onViewSummaryClick(Pokemon pokemon, ArrayList<double[]> ivCombos);

        public void onAddFabClick(int pokeballPosition);

        public void onDeleteLastPokemon();

        public void editPokemon(int pokeballPosition, int pokeballListPosition);

    }

    private void updateBasedOnCompare() {
        mResult = mDataSource.compareAllPokemon(mPokeballNumber);
        ArrayList<Double> percentRange = new ArrayList<>();

        Log.d(TAG, "onCreateView: POkeball Number " + mPokeballNumber);
        Log.d(TAG, "onCreateView: result " + mResult.size());


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
                mSummary.setText("No comparison done as only one dataset for this Pokemon. " + mResult.size() + " IV combinations found.\n");
            } else {
                mSummary.setText(mResult.size() + " overlapping combinations found.\n");
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
        Log.d(TAG, "editAsyncFinished: called");
        mComparison.setEnabled(true);
    }
}
