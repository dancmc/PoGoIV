package com.dancmc.pogoiv.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
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

/**
 * Created by Daniel on 27/07/2016.
 */
public class PokeboxRecyclerViewAdapter extends RecyclerView.Adapter<PokeboxRecyclerViewAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private final ImageView pokeballPicture;
        private final TextView pokeballNickname;

        ViewHolder(View v) {
            super(v);
            pokeballPicture = (ImageView) v.findViewById(R.id.pokeball_image);
            pokeballNickname = (TextView) v.findViewById(R.id.pokeball_nickname);
            v.setOnClickListener(this);
            v.setOnLongClickListener(this);
        }

        @Override
        //basically ends up calling the onItemClick method that you define in MainActivity
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(null, v, getLayoutPosition(), 0);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (mOnItemLongClickListener != null) {
                mOnItemLongClickListener.onItemLongClick(null, v, getLayoutPosition(), 0);
            }
            return true;
        }
    }

    private Context context;
    private AdapterView.OnItemClickListener mOnItemClickListener;
    private AdapterView.OnItemLongClickListener mOnItemLongClickListener;

    public PokeboxRecyclerViewAdapter(Context context) {
        this.context = context;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Pokeball pokeball = Pokeballs.getPokeballsInstance().get(position);
        //sets the cardview layout of this adapter to the pokeball nickname and picture of highest evolved pokemon inside pokeball

        holder.pokeballNickname.setText(pokeball.getNickname());
        holder.pokeballPicture.setImageResource(context.getResources().getIdentifier(Pokemon.getPngFileName(pokeball.getHighestEvolvedPokemonNumber()), "drawable", context.getPackageName()));
        holder.pokeballPicture.setScaleType(ImageView.ScaleType.CENTER_CROP);
        holder.pokeballPicture.setBackgroundResource(R.drawable.inset_circle_background);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.adapter_pokebox_cardview, viewGroup, false);
        return new ViewHolder(v);
    }

    //OnItemClickListener object takes the concrete object that you defined in MainActivity
    public void setOnItemClickListener(AdapterView.OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public void setOnItemLongClickListener(AdapterView.OnItemLongClickListener mOnItemLongClickListener) {
        this.mOnItemLongClickListener = mOnItemLongClickListener;
    }

    @Override
    public int getItemCount() {
        return Pokeballs.getPokeballsInstance().size();
    }
}
