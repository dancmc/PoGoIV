package com.dancmc.pogoiv.fragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dancmc.pogoiv.R;
import com.dancmc.pogoiv.services.FloatingHead;
import com.dancmc.pogoiv.util.IabHelper;
import com.dancmc.pogoiv.utilities.Pokeballs;
import com.dancmc.pogoiv.utilities.Pokemon;
import com.google.android.gms.ads.AdView;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends ContractFragment<SettingsFragment.Contract> {

    public static final String OVERLAY_ADJUSTED = "overlayadjusted";

    private static final String TAG = "SettingsFragment";
    private CheckBox mOverlayPositionCheckbox;
    private LinearLayout mRemoveAdsButton;
    public SharedPreferences sp;
    public SharedPreferences.Editor ed;


    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_settings, container, false);

        mOverlayPositionCheckbox = (CheckBox) v.findViewById(R.id.settings_adjust_overlay_checkbox);
        mRemoveAdsButton = (LinearLayout) v.findViewById(R.id.settings_remove_ads);

        //initialise the shared prefs
        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        ed = sp.edit();

        mOverlayPositionCheckbox.setChecked(sp.getBoolean(OVERLAY_ADJUSTED, false));

        //set the logic to start in app purchase
        mRemoveAdsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              //getContract().purchaseAdFree();

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Test")
                        .setMessage("")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ed.putBoolean("Adfree", true).apply();
                                AdView adview = (AdView) getActivity().findViewById(R.id.adview_main_activity);
                                if (adview != null && adview.getParent() != null) {
                                    ((ViewGroup) adview.getParent()).removeView(adview);
                                }
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ed.putBoolean("Adfree", false).apply();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        //set the checkbox to adjust overlay
        mOverlayPositionCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                ed.putBoolean(OVERLAY_ADJUSTED, isChecked).apply();

                getActivity().sendBroadcast(new Intent().setAction("STOP_FLOATING_HEAD"));
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getContract().overlayRequested();
                        Toast.makeText(getActivity(), "Overlay adjustment toggled", Toast.LENGTH_SHORT)
                                .show();
                    }
                }, 600);

            }
        });


        return v;
    }

    public interface Contract {
        void overlayRequested();

        void purchaseAdFree();
    }

}
