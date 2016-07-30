package com.dancmc.pogoiv;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Daniel on 30/07/2016.
 */
public class ViewPokeballRecyclerViewAdapter extends RecyclerView.Adapter<ViewPokeballRecyclerViewAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View v){
            super(v);
        }
    }

    private Context mContext;
    private Pokeball mPokeball;
    private int mPokeballPosition;

    public ViewPokeballRecyclerViewAdapter(Context context, int pokeballPosition){

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
