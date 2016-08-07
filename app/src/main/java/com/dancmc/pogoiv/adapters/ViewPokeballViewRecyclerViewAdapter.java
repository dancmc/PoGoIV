package com.dancmc.pogoiv.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.dancmc.pogoiv.R;
import com.dancmc.pogoiv.utilities.Pokeball;
import com.dancmc.pogoiv.utilities.Pokeballs;
import com.dancmc.pogoiv.utilities.Pokemon;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Daniel on 30/07/2016.
 */
public class ViewPokeballViewRecyclerViewAdapter extends RecyclerView.Adapter<ViewPokeballViewRecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "ViewHolder";
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        private ImageView mImageView;
        private TextView mTextView;


        public ViewHolder(View v){
            super(v);
            mImageView = (ImageView) v.findViewById(R.id.view_pokemon_recycler_image);
            mTextView = (TextView) v.findViewById(R.id.view_pokemon_recycler_text);
            mImageView.setOnLongClickListener(this);
            mTextView.setOnLongClickListener(this);

        }



        @Override
        public boolean onLongClick(View v) {
            mLongClickListener.onItemLongClick(null, v, getLayoutPosition(),0);
            return true;
        }

    }

    private Context mContext;
    private Pokeball mPokeball;
    private int mPokeballPosition;
    private ArrayList<Integer> mListImages;
    private ArrayList<String> mListText;
    private AdapterView.OnItemLongClickListener mLongClickListener;

    public ViewPokeballViewRecyclerViewAdapter(Context context, int pokeballPosition){
        mContext = context;
        mPokeballPosition = pokeballPosition;

        updateAdapter();

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_pokeball_view_recyclerview_noeditbutton, parent, false);

        return new ViewPokeballViewRecyclerViewAdapter.ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mTextView.setText(mListText.get(position));
        holder.mImageView.setImageResource(mListImages.get(position));
    }

    @Override
    public int getItemCount() {
        return mListText.size();
    }

    public void updateAdapter(){
        try {
            mPokeball = Pokeballs.getPokeballsInstance().get(mPokeballPosition);

            mListImages = new ArrayList<>();
            for (int i = 0; i < mPokeball.size(); i++) {
                mListImages.add(mContext.getResources().getIdentifier(Pokemon.getPngFileName(mPokeball.get(i).getPokemonNumber()), "drawable", mContext.getPackageName()));
            }

            mListText = new ArrayList<>();
            for (int i = 0; i < mPokeball.size(); i++) {
                StringBuilder sb = new StringBuilder();
                if (mPokeball.get(i).getCP() > 0) {
                    sb.append("CP : " + mPokeball.get(i).getCP() + "  ");
                } else {
                    sb.append("CP : nil  ");
                }
                if (mPokeball.get(i).getHP() > 0) {
                    sb.append("HP : " + mPokeball.get(i).getHP() + "  ");
                } else {
                    sb.append("HP : nil  ");
                }
                if (mPokeball.get(i).getStardust() > 0) {
                    sb.append("Dust : " + mPokeball.get(i).getStardust() + " \n");
                } else {
                    sb.append("Dust : nil  \n");
                }
                sb.append("Lvl : "+ Collections.min(mPokeball.get(i).getResultLevelRange())+"-"+Collections.max(mPokeball.get(i).getResultLevelRange())+"  ");
                if (mPokeball.get(i).getFreshMeat()) {
                    sb.append("Not powered up");
                } else {
                    sb.append("Powered up");
                }
                mListText.add(sb.toString());
            }
        }catch (Exception e){

        }
    }

    public void setOnItemLongClickListener(AdapterView.OnItemLongClickListener listener){
        mLongClickListener = listener;
    }



}
