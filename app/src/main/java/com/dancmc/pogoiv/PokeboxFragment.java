package com.dancmc.pogoiv;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;


/**
 * A simple {@link Fragment} subclass.
 */
public class PokeboxFragment extends ContractFragment<PokeboxFragment.Contract> {

    private PokeboxViewAdapter mAdapter;
    private GridLayoutManager mGridLayoutManager;

    public PokeboxFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_pokebox, container, false);

        RecyclerView recyclerView = (RecyclerView)v.findViewById(R.id.pokeball_grid);

        mAdapter = new PokeboxViewAdapter(getActivity());
        mAdapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getContract().selectedPokeball(position);
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
