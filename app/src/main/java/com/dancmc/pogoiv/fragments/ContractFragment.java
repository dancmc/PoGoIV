package com.dancmc.pogoiv.fragments;


import android.app.Activity;
import android.support.v4.app.Fragment;

/**
 * Created by Daniel on 27/04/2016.
 */

//this class is a reusable contract code for different fragments, which can all use it as it is generic
public class ContractFragment<T> extends Fragment {

    //T can be any type
    private T mContract;

    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try{
            mContract = (T)getActivity();
        } catch (ClassCastException e) {
            throw new IllegalStateException("Activity does not implement Contract");
        }
    }

    public void onDetach() {
        super.onDetach();
        mContract= null;
    }

    public T getContract(){
        return mContract;
    }
}
