package com.dancmc.pogoiv.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.dancmc.pogoiv.R;
import com.dancmc.pogoiv.utilities.ViewPagerDots;

import java.util.List;

/**
 * Created by Daniel on 9/08/2016.
 */
public class CustomViewPagerAdapter extends PagerAdapter {

    private static final String TAG = "CustomViewPagerAdapter";
    private List<Integer> mViewList;
    private Context mContext;
    private ViewPagerDots mDots;

    private CustomViewPagerAdapter() {
        super();
    }

    public CustomViewPagerAdapter(Context context, List<Integer> list){
        mContext = context;
        mViewList = list;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        //Adds the correct view from the list passed in with constructor
        RelativeLayout rl =  new RelativeLayout(mContext);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        //Views from the list get set into dispoable relativelayouts to be passed to the viewgroup container, but that means they need to be detached from their previous parent

        /*if(mViewList.get(position).getParent()!=null){
            ((RelativeLayout)mViewList.get(position).getParent()).removeAllViews();
        }*/
        rl.addView(View.inflate(mContext, mViewList.get(position), null),params);

        //Dynamically sets number of dots, and the position of the filled dot, then adds to the viewpager layout returned. Addview and removeview will work cos instantiateItem will be called on notifyDataSetChanged and size is set here
        RelativeLayout.LayoutParams dotsParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        dotsParams.bottomMargin = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40.0f, mContext.getResources().getDisplayMetrics());
        dotsParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        rl.addView(new ViewPagerDots(mContext).setSize(mViewList.size()).setPosition(position), dotsParams);

        container.addView(rl);

        Log.d(TAG, "instantiateItem: ");
        return rl;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        Log.d(TAG, "destroyItem: ");
        container.removeView((View)object);
        object=null;
    }

    @Override
    public int getCount() {
        return mViewList.size();
    }

    public void addView(Integer layout, int position){
        mViewList.add(position, layout);
        this.notifyDataSetChanged();
    }

    public void removeView(int position){
        mViewList.remove(position);
        this.notifyDataSetChanged();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }
}
