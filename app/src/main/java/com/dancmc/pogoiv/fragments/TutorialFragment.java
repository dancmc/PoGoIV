package com.dancmc.pogoiv.fragments;


import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.dancmc.pogoiv.R;
import com.dancmc.pogoiv.activities.MainActivity;
import com.dancmc.pogoiv.adapters.CustomViewPagerAdapter;
import com.dancmc.pogoiv.services.FloatingHead;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 */
public class TutorialFragment extends Fragment {

    private static final String TAG = "TutorialFragment";
    private int mViewLevel;
    private LinearLayout mOverallView;
    private RelativeLayout mMainView;
    private ScrollView mTutorialExplanationScrollview;
    private TextView mTutorialExplanationTextview;
    private View mTableOfContents;
    private ViewPager mViewPager;
    private CustomViewPagerAdapter mAdapter;

    private ArrayList<View> mViewList;

    public TutorialFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mOverallView = (LinearLayout) inflater.inflate(R.layout.fragment_tutorial, container, false);
        mMainView = (RelativeLayout) mOverallView.findViewById(R.id.tutorial_main_layout);
        mTableOfContents = mMainView.findViewById(R.id.tutorial_contents);

        //initialise
        mTutorialExplanationScrollview = (ScrollView) View.inflate(getActivity(), R.layout.tutorial_explanation_text, null);
        mTutorialExplanationTextview = (TextView) mTutorialExplanationScrollview.findViewById(R.id.tutorial_explanation);
        mViewPager = new ViewPager(getActivity());
        mViewLevel = 0;

        //Back key moves up a view hierarchy level, if at root, calls activity onBackPressed
        mMainView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == MotionEvent.ACTION_UP) {

                    goBack();
                    return true;
                }
                return false;
            }
        });

        //Manually setting the contents page onclick behaviour cos too few to bother with adapter
        setOnClickForText(R.id.tutorial_contents_whatareivs, R.string.what_are_ivs);
        setOnClickForText(R.id.tutorial_contents_whatdoesappdo, R.string.whatappdoes);

        //sets a viewpager when the how to use is pressed
        mMainView.findViewById(R.id.tutorial_contents_howtousenormal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Remove the table of contents, set the view hierarchy to 1
                mTableOfContents.setVisibility(View.GONE);
                mViewLevel = 1;

                mViewList = new ArrayList<>();
                mViewList.addAll(Arrays.asList(new View[]{newView(R.layout.tutorial_howtouse_1)}));

                //set params for adding the viewpager, instantiate the adapter, set the adapter, add the viewpager
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                mAdapter = new CustomViewPagerAdapter(getActivity(), mViewList);
                mViewPager.setAdapter(mAdapter);
                mMainView.addView(mViewPager);
            }
        });

        mMainView.findViewById(R.id.tutorial_contents_howtouseoverlay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Remove the table of contents, set the view hierarchy to 1
                mTableOfContents.setVisibility(View.GONE);
                mViewLevel = 1;

                mViewList = new ArrayList<>();
                mViewList.addAll(Arrays.asList(new View[]{newView(R.layout.tutorial_howtouse_1)}));

                //set params for adding the viewpager, instantiate the adapter, set the adapter, add the viewpager
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                mAdapter = new CustomViewPagerAdapter(getActivity(), mViewList);
                mViewPager.setAdapter(mAdapter);
                mMainView.addView(mViewPager);
            }
        });

        return mOverallView;
    }


    //convenience method for creating a textview with the supplied string resource when particular view clicked
    private void setOnClickForText(int viewResource, @StringRes int stringResource) {
        final int sr = stringResource;
        final int vr=viewResource;
        mMainView.findViewById(vr).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //remove the table of contents, set view hierarchy, set Scrollview
                //create new scrollview everytime so the scroll level isn't remembered between pages
                mTableOfContents.setVisibility(View.GONE);
                mViewLevel = 1;
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                mTutorialExplanationScrollview = (ScrollView) View.inflate(getActivity(), R.layout.tutorial_explanation_text, null);

                //Set textview in scrollview
                mTutorialExplanationTextview = (TextView) mTutorialExplanationScrollview.findViewById(R.id.tutorial_explanation);
                mTutorialExplanationTextview.setText(Html.fromHtml(getString(sr)));
                mMainView.addView(mTutorialExplanationScrollview, -1, params);
            }
        });
    }

    //Back key moves up a view hierarchy level, if at root, calls activity onBackPressed
    public void goBack() {
        if (mViewLevel == 1) {
            mTableOfContents.setVisibility(View.VISIBLE);
            mViewLevel = 0;
            if (mTutorialExplanationScrollview.getParent() != null) {
                mMainView.removeView(mTutorialExplanationScrollview);
            }
            if(mViewPager.getParent()!=null){
                mMainView.removeView(mViewPager);
            }
            //Do some stuff to go back to root view
        } else {
            ((MainActivity) getActivity()).goBack();
        }
    }

    //convenience method for inflating a new view from a layout
    private View newView(@LayoutRes int layout){
        return View.inflate(getActivity(), layout, null);
    }

}
