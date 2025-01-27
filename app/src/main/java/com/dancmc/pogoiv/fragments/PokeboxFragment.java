package com.dancmc.pogoiv.fragments;


import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.dancmc.pogoiv.utilities.Pokeballs;
import com.dancmc.pogoiv.database.PokeballsDataSource;
import com.dancmc.pogoiv.adapters.PokeboxRecyclerViewAdapter;
import com.dancmc.pogoiv.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class PokeboxFragment extends ContractFragment<PokeboxFragment.Contract> {

    private PokeboxRecyclerViewAdapter mAdapter;
    private GridLayoutManager mGridLayoutManager;
    private PokeballsDataSource mDataSource;
    private Toolbar mToolbar;
    private LinearLayout mToolbarContainer;
    private FloatingActionButton mAddButton;
    private RelativeLayout mMainLayout;

    public PokeboxFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_pokebox, container, false);
        mMainLayout = (RelativeLayout) v.findViewById(R.id.pokeball_grid);
        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.pokebox_recyclerview);
        mAddButton = (FloatingActionButton)v.findViewById(R.id.add_to_new_pokeball_fab);

        mToolbar = (Toolbar) v.findViewById(R.id.toolbar);
        mToolbar.setTitle("Viewing Pokebox");
        mToolbarContainer = (LinearLayout) v.findViewById(R.id.toolbar_container);


        FloatingActionButton fab = (FloatingActionButton)v.findViewById(R.id.back_button_pokebox);
        fab.setVisibility(View.GONE);

        //in AddActivity, remove the toolbar + shadow
        if (getActivity().getClass().getSimpleName().equals("AddPokemonActivity")) {
            mToolbarContainer.setVisibility(View.GONE);

            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams)mMainLayout.getLayoutParams();
            params.topMargin = 0;
            mMainLayout.setLayoutParams(params);
        }

        mDataSource = new PokeballsDataSource(getActivity());


        mAdapter = new PokeboxRecyclerViewAdapter(getActivity());

        mAdapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getContract().selectedPokeball(position);
            }
        });
        mAdapter.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder2 = new AlertDialog.Builder(getActivity());
                builder2.setTitle("Delete")
                        .setMessage("Do you want to delete this Pokeball?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new AsyncTask<Void, Void, Void>() {
                                    @Override
                                    protected Void doInBackground(Void... params) {
                                        mDataSource.deletePokeball(position);
                                        return null;
                                    }
                                }.execute();
                                Pokeballs.getPokeballsInstance().remove(position);
                                mAdapter.notifyDataSetChanged();
                                Toast.makeText(getActivity(), "Pokeball deleted", Toast.LENGTH_LONG)
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

        mGridLayoutManager = new GridLayoutManager(getActivity(), 4);
        recyclerView.setLayoutManager(mGridLayoutManager);
        recyclerView.setAdapter(mAdapter);


        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContract().addNewPokeball();
            }
        });

        return v;
    }


    public interface Contract {
        void selectedPokeball(int position);

        void addNewPokeball();

    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();
    }
}
