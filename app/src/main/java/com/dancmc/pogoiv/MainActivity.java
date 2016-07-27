package com.dancmc.pogoiv;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.AsyncTask;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


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
    }

    /**TODO : add methods for saving edited pokemon, and for adding new pokemon, based on fragment information
     * when editing, need to move from ViewPokemonFragment to EditPokemonFragment, sending the pokeball and position as an argument
     * EditPokemonFragment should be like calculator, but fixed pokemon field, and a save button - save button checks that number of results >1, and that not the same (customEquals) as any pokemon in the pokeball.
     * Also needs to clone the uniqueID and nickname
     * When saving, delete data at that pokeball/position, and resave with new pokemon object. And then move back to ViewPokemonFragment by sending the pokeball number
     */

    //TODO probably need an async loading bar for the initial boot where loading from database
}
