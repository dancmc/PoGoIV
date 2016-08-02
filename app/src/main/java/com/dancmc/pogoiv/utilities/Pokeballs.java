package com.dancmc.pogoiv.utilities;

/**
 * Created by Daniel on 27/07/2016.
 */

import java.util.ArrayList;

//This is a singleton class to hold Pokeballs which are displayed in the Pokebox fragment

public class Pokeballs extends ArrayList<Pokeball> {
        private static Pokeballs sPokeballsInstance = null;

    public static Pokeballs getPokeballsInstance(){
        if (sPokeballsInstance==null){
            sPokeballsInstance = new Pokeballs();
        }
        return sPokeballsInstance;
    }


}
