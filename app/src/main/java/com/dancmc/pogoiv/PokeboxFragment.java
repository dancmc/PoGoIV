package com.dancmc.pogoiv;


import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class PokeboxFragment extends ContractFragment<PokeboxFragment.Contract> {

    private PokeboxViewAdapter mAdapter;
    private GridLayoutManager mGridLayoutManager;
    private PokeballsDataSource mDataSource;

    public PokeboxFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_pokebox, container, false);

        if(getActivity().getClass().getSimpleName()!="AddPokemonActivity") {
            Toolbar toolbar = (Toolbar) v.findViewById(R.id.fragment_pokebox_toolbar);
            toolbar.setTitle("View Storage");
        }

        mDataSource = new PokeballsDataSource(getActivity());

        RecyclerView recyclerView = (RecyclerView)v.findViewById(R.id.pokeball_grid);
        mAdapter = new PokeboxViewAdapter(getActivity());

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
                                new AsyncTask<Void, Void, Void>(){
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

        mGridLayoutManager = new GridLayoutManager(getActivity(), 5);
        recyclerView.setLayoutManager(mGridLayoutManager);
        recyclerView.setAdapter(mAdapter);

        return v;
    }


    public interface Contract {
        public void selectedPokeball(int position);

    }


}
