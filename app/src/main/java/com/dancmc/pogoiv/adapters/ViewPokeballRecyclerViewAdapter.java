package com.dancmc.pogoiv.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.dancmc.pogoiv.utilities.Pokeball;
import com.dancmc.pogoiv.utilities.Pokeballs;
import com.dancmc.pogoiv.R;
import com.dancmc.pogoiv.utilities.Pokemon;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Daniel on 30/07/2016.
 */
public class ViewPokeballRecyclerViewAdapter extends RecyclerView.Adapter<ViewPokeballRecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "ViewHolder";
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener {

        private ImageView mImageView;
        private TextView mTextView;
        private ImageView mEditButton;


        public ViewHolder(View v){
            super(v);
            mImageView = (ImageView) v.findViewById(R.id.view_pokemon_recycler_image);
            mTextView = (TextView) v.findViewById(R.id.view_pokemon_recycler_text);
            mEditButton = (ImageView) v.findViewById(R.id.view_pokemon_recycler_edit);
            mImageView.setOnLongClickListener(this);
            mTextView.setOnLongClickListener(this);
            mEditButton.setOnLongClickListener(this);
            mImageView.setOnClickListener(this);
            mTextView.setOnClickListener(this);
            mEditButton.setOnClickListener(this);
        }



        @Override
        public boolean onLongClick(View v) {
            mLongClickListener.onItemLongClick(null, v, getLayoutPosition(),0);
            return true;
        }

        @Override
        public void onClick(View v) {
            mClickListener.onItemClick(null,v,getLayoutPosition(),0);
        }
    }

    private Context mContext;
    private Pokeball mPokeball;
    private int mPokeballPosition;
    private ArrayList<Integer> mListImages;
    private ArrayList<String> mListText;
    private AdapterView.OnItemLongClickListener mLongClickListener;
    private AdapterView.OnItemClickListener mClickListener;
    private DecimalFormat mDF;

    public ViewPokeballRecyclerViewAdapter(Context context, int pokeballPosition){
        mContext = context;
        mPokeballPosition = pokeballPosition;
        mDF = new DecimalFormat("0.0");
        updateAdapter();

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_pokeball_view_recyclerview, parent, false);

        return new ViewPokeballRecyclerViewAdapter.ViewHolder(v);
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
                sb.append("Lvl : "+ mDF.format((Collections.min(mPokeball.get(i).getResultLevelRange())+1)/2.0)+"-"+mDF.format((Collections.max(mPokeball.get(i).getResultLevelRange())+1)/2.0)+"  ");
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


    public void setOnItemClickListener(AdapterView.OnItemClickListener listener){
        mClickListener = listener;
    }
}
