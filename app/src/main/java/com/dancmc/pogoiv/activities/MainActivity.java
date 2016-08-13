package com.dancmc.pogoiv.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.location.SettingInjectorService;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.dancmc.pogoiv.fragments.CompareSummaryFragment;
import com.dancmc.pogoiv.fragments.IVCalculatorFragment;
import com.dancmc.pogoiv.fragments.SettingsFragment;
import com.dancmc.pogoiv.fragments.TutorialFragment;
import com.dancmc.pogoiv.services.FloatingHead;
import com.dancmc.pogoiv.util.IabHelper;
import com.dancmc.pogoiv.util.IabResult;
import com.dancmc.pogoiv.util.Inventory;
import com.dancmc.pogoiv.util.Purchase;
import com.dancmc.pogoiv.utilities.Pokeballs;
import com.dancmc.pogoiv.database.PokeballsDataSource;
import com.dancmc.pogoiv.fragments.PokeboxFragment;
import com.dancmc.pogoiv.utilities.Pokemon;
import com.dancmc.pogoiv.R;
import com.dancmc.pogoiv.fragments.ViewPokeballFragment;
import com.dancmc.pogoiv.fragments.EditPokemonFragment;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.winsontan520.wversionmanager.library.WVersionManager;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements IVCalculatorFragment.Contract, PokeboxFragment.Contract, ViewPokeballFragment.Contract, EditPokemonFragment.Contract, SettingsFragment.Contract {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_ADFREE = 101;
    private static final String SKU_ADFREE = "sku_adfree";
    private IVCalculatorFragment mCalcFragment;
    private PokeboxFragment mPokeboxFragment;
    private ViewPokeballFragment mViewPokeballFragment;
    private CompareSummaryFragment mCompareSummaryFragment;
    private EditPokemonFragment mEditPokemonFragment;
    private SettingsFragment mSettingsFragment;

    private Toast mToast;
    private long mLastPressed;
    private PokeballsDataSource mDataSource;



    private SaveAsyncTask mSaveAsyncTask;
    private TutorialFragment mTutorialFragment;
    private IabHelper mHelper;
    private SharedPreferences mSharedPrefs;

    public static int OVERLAY_PERMISSION_REQ_CODE = 1234;

    @Override
    public void overlayRequested() {

        if (Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(this)) {
            Log.d(TAG, "onCreate: no permission");
            AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
            builder2.setTitle("Overlay Permissions Request")
                    .setMessage("To use the floating window feature, you need to enable the overlay permission in the next window.")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                            startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
                        }
                    })
                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            AlertDialog dialog2 = builder2.create();
            dialog2.show();
        } else if (Build.VERSION.SDK_INT < 23 || (Build.VERSION.SDK_INT >= 23 && Settings.canDrawOverlays(this))) {
            FloatingHead.setContext(this);
            FloatingHead.viewIsRunning = false;
            FloatingHead.currentlyRunningServiceFragment = FloatingHead.OVERLAY_SERVICE;
            this.startService(new Intent(this, FloatingHead.class));
        }
        Log.d(TAG, "overlayRequested: ");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WVersionManager versionManager  = new WVersionManager(this);
        versionManager.setVersionContentUrl("http://bit.ly/pogoivjson");
        versionManager.checkVersion();
        versionManager.setUpdateUrl("http://yahoo.com");

        String key1 = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAzBKehj4IFluzNYZysVM7netJim+FFIdUuCCbuiPaQzChPPx+sGhVeFpITIByvyXoB8I3Lvek4XkpRp6RCSh18hy2pqgDc260r/KaclA5NF3jynMJGSholsvwLH5l4XrdHlZ7m/cqHCl/AocV5j2uwHh6r";
        String key2 = "BAQADIwq+O1v9rvFbtbbAmvnYzkhfKjXNlWWNG49TyuFcHluO63Czd/sYcfsJYZ0/EE3xUzB/3v41QVbKA8xlB9QNhl5+vkta0/5S4Y6Ay3KPHfT0Y05i9iAG5N4rNW40yleUbrziKlhUOPEIEhw+RklCBF1F25XKQNcspZv5kSb/SAPcppP03T9NLXGtc3G2TO";

        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        if (mSharedPrefs.getBoolean("Adfree", false)) {
            setContentView(R.layout.activity_main_adfree);
        } else {
            setContentView(R.layout.activity_main);
            MobileAds.initialize(getApplicationContext(), "ca-app-pub-7691928644284038~9762759909");
            AdView mAdView = (AdView) findViewById(R.id.adview_main_activity);
            AdRequest request = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                    .addTestDevice("ABDC8E5277217A63DADF93CFCE6B47DB") // An example device ID
                    .build();
            mAdView.loadAd(request);
            Log.d(TAG, "onCreate: ads loaded");
        }

        Log.d(TAG, "onCreate: sharedprefs"+mSharedPrefs.getBoolean("Adfree", false));
        Log.d(TAG, "onCreate: adview" + findViewById(R.id.adview_main_activity));


        mHelper = new IabHelper(this, key1 + encrypt(key2));

        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    Log.d(TAG, "Problem setting up In-app Billing: " + result);
                } else{
                    checkPurchases();
                }
            }
        });




        mDataSource = new PokeballsDataSource(this);

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                if (Pokeballs.getPokeballsInstance().size() == 0) {
                    Pokeballs.getPokeballsInstance().addAll(mDataSource.getAllPokeballs());
                }
                return null;
            }
        }.execute();



        //normal flow without adding : calculateIV -> pokebox -> viewpokemon
        if (getSupportFragmentManager().findFragmentById(R.id.main_fragment_container) == null) {
            mCalcFragment = new IVCalculatorFragment();

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.main_fragment_container, mCalcFragment, "calcFragment")
                    .commit();
        }
    }

    private String encrypt(String s) {
        StringBuilder sb = new StringBuilder();
        sb.append(s);
        sb = sb.reverse();
        return sb.toString();
    }

    @Override
    public void purchaseAdFree() {
        IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener
                = new IabHelper.OnIabPurchaseFinishedListener() {
            public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
                if (result.isFailure()) {
                    Toast.makeText(MainActivity.this, "Error purchasing", Toast.LENGTH_SHORT)
                            .show();
                    return;
                } else if (purchase.getSku().equals(SKU_ADFREE) && purchase.getDeveloperPayload().equals("EeRFdADEsaHCrUP")) {
                    mSharedPrefs.edit().putBoolean("Adfree", true).apply();
                    AdView adview = (AdView) findViewById(R.id.adview_main_activity);
                    if (adview != null && adview.getParent() != null) {
                        ((ViewGroup) adview.getParent()).removeView(adview);
                    }
                    Toast.makeText(MainActivity.this, "Please close and restart the overlay to remove ads.", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        };


        try {
            mHelper.launchPurchaseFlow(this, SKU_ADFREE, IabHelper.ITEM_TYPE_INAPP, null, REQUEST_ADFREE, mPurchaseFinishedListener, "EeRFdADEsaHCrUP");
        } catch (IabHelper.IabAsyncInProgressException e) {
            Toast.makeText(this, "Another async operation in progress..", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, FloatingHead.class));
        if (mHelper != null) {
            mHelper.disposeWhenFinished();
            mHelper = null;
        }
        if(findViewById(R.id.adview_main_activity)!= null){
            ((AdView)findViewById(R.id.adview_main_activity)).destroy();
        }
    }

    @Override
    public void addButtonPressed(Pokemon pokemon) {
        Intent i = new Intent(this, AddPokemonActivity.class);
        i.putExtra(AddPokemonActivity.EXTRA, pokemon);
        startActivity(i);
    }

    //null & no combinations handled in calculator fragment
    @Override
    public void pokeboxButtonPressed() {
        if (mPokeboxFragment == null) {
            mPokeboxFragment = new PokeboxFragment();

        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_container, mPokeboxFragment).addToBackStack(null)
                .commit();
    }

    @Override
    public void selectedPokeball(int position) {
        mViewPokeballFragment = ViewPokeballFragment.newInstance(position);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_container, mViewPokeballFragment).addToBackStack(null)
                .commit();
    }

    @Override
    public void onViewSummaryClick(Pokemon pokemon, ArrayList<double[]> ivCombos) {
        mCompareSummaryFragment = CompareSummaryFragment.newInstance(pokemon, ivCombos, false);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_container, mCompareSummaryFragment).addToBackStack(null)
                .commit();
    }

    //merge with viewsummaryclicked
    @Override
    public void moreInfoButtonPressed(Pokemon pokemon) {

        mCompareSummaryFragment = CompareSummaryFragment.newInstance(pokemon, pokemon.getIVCombinationsArray(), true);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_container, mCompareSummaryFragment).addToBackStack(null)
                .commit();

    }

    //Moving to EditFragment to ADD pokemon
    @Override
    public void addToExistingPokeball(int pokeballNumber) {

        mEditPokemonFragment = EditPokemonFragment.newInstance(pokeballNumber, -1);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_container, mEditPokemonFragment).addToBackStack(null)
                .commit();
    }

    //Moving to EditFragment to ADD pokeball
    @Override
    public void addNewPokeball() {
        mEditPokemonFragment = EditPokemonFragment.newInstance(-1, -1);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_container, mEditPokemonFragment).addToBackStack(null)
                .commit();
    }

    //Moving to editpokemon to edit something
    @Override
    public void editPokemon(int pokeballNumber, int pokeballListNumber) {
        mEditPokemonFragment = EditPokemonFragment.newInstance(pokeballNumber, pokeballListNumber);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_container, mEditPokemonFragment).addToBackStack(null)
                .commit();
    }

    @Override
    public void tutorial() {
        if (mTutorialFragment == null) {
            mTutorialFragment = new TutorialFragment();
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_container, mTutorialFragment).addToBackStack(null)
                .commit();
    }

    @Override
    public void appSettings() {
        if (mSettingsFragment == null) {
            mSettingsFragment = new SettingsFragment();
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_container, mSettingsFragment).addToBackStack(null)
                .commit();
    }


    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().findFragmentById(R.id.main_fragment_container) instanceof IVCalculatorFragment) {
            long currentTime = System.currentTimeMillis();
            if (mLastPressed + 4000 > currentTime) {
                mToast.cancel();
                super.onBackPressed();
            } else {
                mToast = Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_LONG);
                mToast.show();
                mLastPressed = currentTime;
            }
        } else if (getSupportFragmentManager().findFragmentById(R.id.main_fragment_container) instanceof TutorialFragment) {
            mTutorialFragment.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //TODO : save IVcalculator's typed state here as key pairs
    }

    //make EditTexts lose focus on click out
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public void onDeleteLastPokemon() {
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void saveButtonPressed(int pokeballNumber, int pokeballListNumber, Pokemon pokemon) {
        mSaveAsyncTask = new SaveAsyncTask(pokemon, pokeballNumber, pokeballListNumber, this);
        mSaveAsyncTask.execute();
        onBackPressed();
    }


    public SaveAsyncTask.Status getSaveAsyncStatus() {
        if (mSaveAsyncTask != null) {
            return mSaveAsyncTask.getStatus();
        }
        return null;
    }

    public class SaveAsyncTask extends AsyncTask<Void, Void, Void> {
        private PokeballsDataSource mDataSource;
        Pokemon mPokemon;
        int mPokeballNumber;
        int mPokeballListNumber;

        public SaveAsyncTask(Pokemon pokemon, int pokeballNumber, int pokeballListNumber, Context context) {
            mPokemon = pokemon;
            mPokeballNumber = pokeballNumber;
            mPokeballListNumber = pokeballListNumber;
            mDataSource = new PokeballsDataSource(context);
        }

        @Override
        protected Void doInBackground(Void... params) {
            mDataSource.setPokemonData(mPokemon, mPokeballNumber, mPokeballListNumber);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (getSupportFragmentManager().findFragmentById(R.id.main_fragment_container) instanceof ViewPokeballFragment) {
                ((ViewPokeballFragment) getSupportFragmentManager().findFragmentById(R.id.main_fragment_container)).editAsyncFinished();

            }
        }

    }

    public void goBack() {
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == OVERLAY_PERMISSION_REQ_CODE) {
            if (Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(this)) {

            }
        }
    }

    private void checkPurchases() {
        IabHelper.QueryInventoryFinishedListener mGotInventoryListener
                = new IabHelper.QueryInventoryFinishedListener() {
            public void onQueryInventoryFinished(IabResult result,
                                                 Inventory inventory) {
                if (result.isFailure()) {

                } else {

                    if(inventory.hasPurchase(SKU_ADFREE)){
                        mSharedPrefs.edit().putBoolean("Adfree", true).apply();
                    } else{
                       mSharedPrefs.edit().putBoolean("Adfree", false).apply();
                    }
                }
            }
        };
        try {
            mHelper.queryInventoryAsync(mGotInventoryListener);
        } catch (IabHelper.IabAsyncInProgressException e) {
            Log.d(TAG, "checkPurchases: AsyncException");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(findViewById(R.id.adview_main_activity)!= null ){
            ((AdView)findViewById(R.id.adview_main_activity)).pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(findViewById(R.id.adview_main_activity)!= null  ){
            ((AdView)findViewById(R.id.adview_main_activity)).resume();
        }
    }
}
