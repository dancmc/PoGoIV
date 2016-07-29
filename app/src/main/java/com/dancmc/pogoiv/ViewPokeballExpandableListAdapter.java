package com.dancmc.pogoiv;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Daniel on 27/07/2016.
 */
public class ViewPokeballExpandableListAdapter extends BaseExpandableListAdapter {

    private Context mContext;
    private ArrayList<String> mListHeaders;
    private ArrayList<String> mListBodies;
    private ArrayList<Integer> mListImages;
    private Pokeball mPokeball;
    private int mPokeballPosition;

    public ViewPokeballExpandableListAdapter(Context context, int pokeballPosition) {
        mContext = context;

        mPokeballPosition = pokeballPosition;
        updateAdapter();



    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public int getGroupCount() {
        return mListHeaders.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mListBodies.get(groupPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mListHeaders.get(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String header = (String)getGroup(groupPosition);
        int imageResource = mListImages.get(groupPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.pokeball_view_elv_header, null);
        }

        ImageView elvHeaderImage = (ImageView) convertView.findViewById(R.id.elv_header_image);
        TextView elvHeaderText = (TextView) convertView.findViewById(R.id.elv_header_text);

        elvHeaderText.setTypeface(null, Typeface.BOLD);
        elvHeaderText.setText(header);
        elvHeaderImage.setImageResource(imageResource);


        return convertView;
    }



    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        String body = (String)getChild(groupPosition,groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.pokeball_view_elv_body, null);
        }
        TextView elvBody = (TextView) convertView.findViewById(R.id.pokeball_view_elv_body);
        elvBody.setTypeface(null, Typeface.BOLD);
        elvBody.setText(body);
        return convertView;
    }

    public void updateAdapter(){
        mPokeball = Pokeballs.getPokeballsInstance().get(mPokeballPosition);

        mListHeaders = new ArrayList<>();
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
            if (mPokeball.get(i).getFreshMeat()) {
                sb.append("Powered up");
            } else {
                sb.append("Not powered up");
            }
            mListHeaders.add(sb.toString());
        }

        mListImages = new ArrayList<>();
        for (int i = 0; i < mPokeball.size(); i++) {
            mListImages.add(mContext.getResources().getIdentifier(Pokemon.getPngFileName(mPokeball.get(i).getPokemonNumber()), "drawable", mContext.getPackageName()));
        }

        mListBodies = new ArrayList<>();
        for (int i = 0; i < mPokeball.size(); i++) {
            //TODO : edit this output
            mListBodies.add(mPokeball.get(i).getStringOutput());
        }

    }
}
