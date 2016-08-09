package com.dancmc.pogoiv.utilities;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.dancmc.pogoiv.R;

/**
 * Created by Daniel on 9/08/2016.
 */
public class ViewPagerDots {

    private LinearLayout layout;
    private Context mContext;
    private int mSize;
    private int mPosition;

    public ViewPagerDots(Context context){
        layout = (LinearLayout)View.inflate(context, R.layout.viewpager_dots, null);
        mContext = context;
        mSize = 1;
        mPosition = 1;
    }

    public ViewPagerDots setSize(int number){
        mSize = number;
        return this;
    }

    public LinearLayout setPosition(int position){
        mPosition = position;
        layout.removeAllViews();
        layout.setGravity(Gravity.CENTER);

        for (int i = 0; i <mPosition; i++) {
           addEmptyDot();
        }

        addFilledDot();

        for (int i = mPosition+1; i<mSize;i++){
            addEmptyDot();
        }

        return layout;
    }

    public LinearLayout getView(){

        layout.removeAllViews();

        return layout;
    }

    private void addEmptyDot(){
        ImageView image = new ImageView(mContext);
        image.setImageResource(R.drawable.viewpager_dot_empty);
        image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        layout.addView(image, new LinearLayout.LayoutParams((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10.0f,mContext.getResources().getDisplayMetrics()), (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10.0f,mContext.getResources().getDisplayMetrics())));
    }

    private void addFilledDot() {
        ImageView image = new ImageView(mContext);
        image.setImageResource(R.drawable.viewpager_dot_filled);
        image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        layout.addView(image, new LinearLayout.LayoutParams((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10.0f, mContext.getResources().getDisplayMetrics()), (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10.0f, mContext.getResources().getDisplayMetrics())));
    }
}
