package com.dancmc.pogoiv.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.dancmc.pogoiv.R;
import com.dancmc.pogoiv.activities.MainActivity;
import com.dancmc.pogoiv.services.FloatingHead;

/**
 * A simple {@link Fragment} subclass.
 */
public class TutorialFragment extends Fragment {

    private static final String TAG = "TutorialFragment";
    private int mViewLevel;
    private LinearLayout mMainView;
    private ScrollView mTutorialExplanationScrollview;
    private TextView mTutorialExplanationTextview;
    private View mTableOfContents;


    public TutorialFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView = (LinearLayout) inflater.inflate(R.layout.fragment_tutorial, container, false);
        mTableOfContents = mMainView.findViewById(R.id.tutorial_contents);


        mViewLevel = 0;

        mMainView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == MotionEvent.ACTION_UP) {
                    Log.d(TAG, "onKey: 1");
                    if (mViewLevel == 1) {
                        mTableOfContents.setVisibility(View.VISIBLE);
                        mViewLevel = 0;
                        Log.d(TAG, "onKey: 2");
                        if (mTutorialExplanationScrollview.getParent() != null) {
                            mMainView.removeView(mTutorialExplanationScrollview);
                            Log.d(TAG, "onKey: 3");
                        }
                        //Do some stuff to go back to root view
                    } else {
                        ((MainActivity) getActivity()).goBack();
                    }

                    return true;
                }
                return false;
            }
        });


        setOnClickForText(R.id.tutorial_contents_whatareivs, R.string.what_are_ivs);
        setOnClickForText(R.id.tutorial_contents_whatdoesappdo, R.string.whatappdoes);


        return mMainView;
    }

    private void setOnClickForText(int viewResource, int stringResource) {
        final int sr = stringResource;
        final int vr=viewResource;
        mMainView.findViewById(vr).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTableOfContents.setVisibility(View.GONE);
                mViewLevel = 1;
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                mTutorialExplanationScrollview = (ScrollView) View.inflate(getActivity(), R.layout.tutorial_explanation_text, null);
                mTutorialExplanationTextview = (TextView) mTutorialExplanationScrollview.findViewById(R.id.tutorial_explanation);
                mTutorialExplanationTextview.setText(Html.fromHtml(getString(sr)));
                mMainView.addView(mTutorialExplanationScrollview, -1, params);
            }
        });
    }

    public void goBack() {
        if (mViewLevel == 1) {
            mTableOfContents.setVisibility(View.VISIBLE);
            mViewLevel = 0;
            Log.d(TAG, "onKey: 2");
            if (mTutorialExplanationScrollview.getParent() != null) {
                mMainView.removeView(mTutorialExplanationScrollview);
                Log.d(TAG, "onKey: 3");
            }
            //Do some stuff to go back to root view
        } else {
            ((MainActivity) getActivity()).goBack();
        }
    }

}
