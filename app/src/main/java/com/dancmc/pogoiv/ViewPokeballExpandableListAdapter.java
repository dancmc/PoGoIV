package com.dancmc.pogoiv;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Daniel on 27/07/2016.
 */
public class ViewPokeballExpandableListAdapter extends BaseExpandableListAdapter {

    private Context mContext;
    private ArrayList<String> mListHeaders;
    private ArrayList<String> mListBodies;

    public ViewPokeballExpandableListAdapter(Context context, ArrayList<String> listHeaders, ArrayList<String> listBodies) {
        mContext = context;
        mListHeaders = listHeaders;
        mListBodies = listBodies;
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
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String header = (String)getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.pokeball_view_elv_header, null);
        }
        TextView elvHeader = (TextView) convertView.findViewById(R.id.pokeball_view_elv_header);
        elvHeader.setTypeface(null, Typeface.BOLD);
        elvHeader.setText(header);
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
}
