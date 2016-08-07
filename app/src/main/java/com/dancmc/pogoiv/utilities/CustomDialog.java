package com.dancmc.pogoiv.utilities;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dancmc.pogoiv.R;

/**
 * Created by Daniel on 7/08/2016.
 */
public class CustomDialog {
    private View v;

    public CustomDialog(Context context){
        v = View.inflate(context, R.layout.dialog_box_hack, null);
        v.findViewById(R.id.dialog_blankspace).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ViewGroup)v.getParent()).removeView(v);
            }
        });
    }

    public View getView(){
        return v;
    }

    public CustomDialog setTitle(String s){
        ((TextView)v.findViewById(R.id.dialog_title)).setText(s);
        return this;
    }

    public CustomDialog setMessage(String s){
        ((TextView)v.findViewById(R.id.dialog_desc)).setText(s);
        return this;
    }


    public CustomDialog setPositiveButton(String s, View.OnClickListener listener){
        ((TextView)v.findViewById(R.id.dialog_ok_button)).setText(s.toUpperCase());
        ((TextView)v.findViewById(R.id.dialog_ok_button)).setOnClickListener(listener);
        return this;
    }

    public CustomDialog setNegativeButton(String s, View.OnClickListener listener){
        ((TextView)v.findViewById(R.id.dialog_cancel_button)).setText(s.toUpperCase());
        ((TextView)v.findViewById(R.id.dialog_cancel_button)).setOnClickListener(listener);
        return this;
    }

}
