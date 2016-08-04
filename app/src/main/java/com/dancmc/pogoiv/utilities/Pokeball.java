package com.dancmc.pogoiv.utilities;

import com.dancmc.pogoiv.utilities.Pokemon;

import java.util.ArrayList;

//This represents a Pokeball, which holds multiple levels of the same pokemon

public class Pokeball extends ArrayList<Pokemon> {
    private int mHighestEvolvedPokemonNumber;
    private String mNickname;

    public int getHighestEvolvedPokemonNumber(){
        return mHighestEvolvedPokemonNumber;
    }

    public void setHighestEvolvedPokemonNumber(int number){
        mHighestEvolvedPokemonNumber = number;
    }

    public String getNickname() {
        return mNickname;
    }

    public void setNickname(String name){
        mNickname = name;
    }



}
