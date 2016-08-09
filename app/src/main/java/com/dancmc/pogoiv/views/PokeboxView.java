package com.dancmc.pogoiv.views;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dancmc.pogoiv.R;
import com.dancmc.pogoiv.adapters.PokeboxRecyclerViewAdapter;
import com.dancmc.pogoiv.database.PokeballsDataSource;
import com.dancmc.pogoiv.services.FloatingHead;
import com.dancmc.pogoiv.utilities.CustomDialog;
import com.dancmc.pogoiv.utilities.CustomToast;
import com.dancmc.pogoiv.utilities.Pokeball;
import com.dancmc.pogoiv.utilities.Pokeballs;
import com.dancmc.pogoiv.utilities.Pokemon;

/**
 * Created by Daniel on 6/08/2016.
 */
public class PokeboxView extends GenericServiceView {

    private PokeboxRecyclerViewAdapter mAdapter;
    private GridLayoutManager mGridLayoutManager;
    private PokeballsDataSource mDataSource;
    private FloatingActionButton mAddButton;
    private FrameLayoutIntercept mFrameLayout;


    public PokeboxView(Context context) {
        //standard setup. Must call a super method GenericServiceView has no default constructor
        super(context);
        v = View.inflate(mContext, R.layout.view_service_pokebox, null);

        if(!FloatingHead.viewMode) {
            //setting up persistent Add Card at the top of screen
            View cardView = View.inflate(mContext, R.layout.adhoc_overlay_addpokemon_cardview, null);
            ImageView addCardImage = (ImageView) cardView.findViewById(R.id.overlay_add_pokemon_card_image);
            TextView addCardCP = (TextView) cardView.findViewById(R.id.overlay_add_pokemon_card_cp);
            TextView addCardHP = (TextView) cardView.findViewById(R.id.overlay_add_pokemon_card_hp);
            TextView addCardLevel = (TextView) cardView.findViewById(R.id.overlay_add_pokemon_card_level);
            TextView addCardIV = (TextView) cardView.findViewById(R.id.overlay_add_pokemon_card_IV);
            TextView addCardPrompt = (TextView) cardView.findViewById(R.id.overlay_add_pokemon_card_text_prompt);
            LinearLayout insertPoint = (LinearLayout) v.findViewById(R.id.overlay_insertpoint_pokebox);


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
            insertPoint.addView(cardView, 0);
        }

        // Inflate the layout for this fragment
        LayoutInflater vi = (LayoutInflater) mContext.getApplicationContext().getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.overlay_pokebox_recyclerview);
        mAddButton = (FloatingActionButton) v.findViewById(R.id.overlay_add_to_new_pokeball_fab);
        mFrameLayout = (FrameLayoutIntercept) v.findViewById(R.id.overlay_main_layout_pokebox);


        mDataSource = new PokeballsDataSource(mContext);
        mAdapter = new PokeboxRecyclerViewAdapter(mContext);

        mAdapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                FloatingHead.mPokeboxClickedPosition = position;
                FloatingHead.switchService(ADD_VIEW_POKEBALL_SERVICE);
            }
        });

        mAdapter.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final CustomDialog dialog1 = new CustomDialog(mContext);
                dialog1.setTitle("Delete")
                        .setMessage("Do you want to delete this Pokeball?")
                        .setPositiveButton("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                new AsyncTask<Void, Void, Void>() {
                                    @Override
                                    protected Void doInBackground(Void... params) {
                                        mDataSource.deletePokeball(position);
                                        return null;
                                    }
                                }.execute();
                                Pokeballs.getPokeballsInstance().remove(position);
                                mAdapter.notifyDataSetChanged();
                                mFrameLayout.removeView(dialog1.getView());
                                CustomToast.makeToast(mContext).setMessage("Pokeball deleted").show();

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

        mGridLayoutManager = new GridLayoutManager(mContext, 4);
        recyclerView.setLayoutManager(mGridLayoutManager);
        recyclerView.setAdapter(mAdapter);


        final CustomDialog dialog2 = new CustomDialog(mContext);
        dialog2.setTitle("Add Pokemon")
                .setMessage("Do you want to add the snapshot to a new Pokeball?")
                .setPositiveButton("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Pokeball newPokeball = new Pokeball();
                        newPokeball.add(mPokemon);
                        newPokeball.setNickname(mPokemon.getPokemonName());
                        newPokeball.setHighestEvolvedPokemonNumber(mPokemon.getPokemonNumber());

                        Pokeballs.getPokeballsInstance().add(newPokeball);

                        new AsyncTask<Integer, Void, Void>() {
                            @Override
                            protected Void doInBackground(Integer... params) {
                                mDataSource.setPokemonData(mPokemon, (Pokeballs.getPokeballsInstance().size() - 1), 0);
                                return null;
                            }
                        }.execute();
                        mFrameLayout.removeView(dialog2.getView());
                        FloatingHead.viewMode = true;
                        FloatingHead.switchService(OVERLAY_SERVICE);

                    }
                })
                .setNegativeButton("Cancel", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mFrameLayout.removeView(dialog2.getView());
                    }
                });
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFrameLayout.addView(dialog2.getView());
            }
        });



        if (FloatingHead.viewMode == true) {
            mAddButton.setVisibility(View.GONE);
        }

    }

}
