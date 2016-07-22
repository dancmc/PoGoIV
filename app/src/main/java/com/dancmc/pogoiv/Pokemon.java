package com.dancmc.pogoiv;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by Daniel on 19/07/2016.
 */
public class Pokemon implements Serializable {
    private static final String TAG = "PokemonClass";

    public static final String[] POKEDEX = {"nil", "Bulbasaur", "Ivysaur", "Venusaur", "Charmander", "Charmeleon", "Charizard", "Squirtle", "Wartortle", "Blastoise", "Caterpie", "Metapod", "Butterfree", "Weedle", "Kakuna", "Beedrill", "Pidgey", "Pidgeotto", "Pidgeot", "Rattata", "Raticate", "Spearow", "Fearow", "Ekans", "Arbok", "Pikachu", "Raichu", "Sandshrew", "Sandslash", "NidoranF", "Nidorina", "Nidoqueen", "NidoranM", "Nidorino", "Nidoking", "Clefairy", "Clefable", "Vulpix", "Ninetales", "Jigglypuff", "Wigglytuff", "Zubat", "Golbat", "Oddish", "Gloom", "Vileplume", "Paras", "Parasect", "Venonat", "Venomoth", "Diglett", "Dugtrio", "Meowth", "Persian", "Psyduck", "Golduck", "Mankey", "Primeape", "Growlithe", "Arcanine", "Poliwag", "Poliwhirl", "Poliwrath", "Abra", "Kadabra", "Alakazam", "Machop", "Machoke", "Machamp", "Bellsprout", "Weepinbell", "Victreebel", "Tentacool", "Tentacruel", "Geodude", "Graveler", "Golem", "Ponyta", "Rapidash", "Slowpoke", "Slowbro", "Magnemite", "Magneton", "Farfetch'd", "Doduo", "Dodrio", "Seel", "Dewgong", "Grimer", "Muk", "Shellder", "Cloyster", "Gastly", "Haunter", "Gengar", "Onix", "Drowzee", "Hypno", "Krabby", "Kingler", "Voltorb", "Electrode", "Exeggcute", "Exeggutor", "Cubone", "Marowak", "Hitmonlee", "Hitmonchan", "Lickitung", "Koffing", "Weezing", "Rhyhorn", "Rhydon", "Chansey", "Tangela", "Kangaskhan", "Horsea", "Seadra", "Goldeen", "Seaking", "Staryu", "Starmie", "Mr. Mime", "Scyther", "Jynx", "Electabuzz", "Magmar", "Pinsir", "Tauros", "Magikarp", "Gyarados", "Lapras", "Ditto", "Eevee", "Vaporeon", "Jolteon", "Flareon", "Porygon", "Omanyte", "Omastar", "Kabuto", "Kabutops", "Aerodactyl", "Snorlax", "Articuno", "Zapdos", "Moltres", "Dratini", "Dragonair", "Dragonite", "Mewtwo", "Mew"};
    public static final String[] POKEMON_FAMILIES = {"nil", "FAMILY_BULBASAUR", "FAMILY_BULBASAUR", "FAMILY_BULBASAUR", "FAMILY_CHARMANDER", "FAMILY_CHARMANDER", "FAMILY_CHARMANDER", "FAMILY_SQUIRTLE", "FAMILY_SQUIRTLE", "FAMILY_SQUIRTLE", "FAMILY_CATERPIE", "FAMILY_CATERPIE", "FAMILY_CATERPIE", "FAMILY_WEEDLE", "FAMILY_WEEDLE", "FAMILY_WEEDLE", "FAMILY_PIDGEY", "FAMILY_PIDGEY", "FAMILY_PIDGEY", "FAMILY_RATTATA", "FAMILY_RATTATA", "FAMILY_SPEAROW", "FAMILY_SPEAROW", "FAMILY_EKANS", "FAMILY_EKANS", "FAMILY_PIKACHU", "FAMILY_PIKACHU", "FAMILY_SANDSHREW", "FAMILY_SANDSHREW", "FAMILY_NIDORAN", "FAMILY_NIDORAN", "FAMILY_NIDORAN", "FAMILY_NIDORAN", "FAMILY_NIDORAN", "FAMILY_NIDORAN", "FAMILY_CLEFAIRY", "FAMILY_CLEFAIRY", "FAMILY_VULPIX", "FAMILY_VULPIX", "FAMILY_JIGGLYPUFF", "FAMILY_JIGGLYPUFF", "FAMILY_ZUBAT", "FAMILY_ZUBAT", "FAMILY_ODDISH", "FAMILY_ODDISH", "FAMILY_ODDISH", "FAMILY_PARAS", "FAMILY_PARAS", "FAMILY_VENONAT", "FAMILY_VENONAT", "FAMILY_DIGLETT", "FAMILY_DIGLETT", "FAMILY_MEOWTH", "FAMILY_MEOWTH", "FAMILY_PSYDUCK", "FAMILY_PSYDUCK", "FAMILY_MANKEY", "FAMILY_MANKEY", "FAMILY_GROWLITHE", "FAMILY_GROWLITHE", "FAMILY_POLIWAG", "FAMILY_POLIWAG", "FAMILY_POLIWAG", "FAMILY_ABRA", "FAMILY_ABRA", "FAMILY_ABRA", "FAMILY_MACHOP", "FAMILY_MACHOP", "FAMILY_MACHOP", "FAMILY_BELLSPROUT", "FAMILY_BELLSPROUT", "FAMILY_BELLSPROUT", "FAMILY_TENTACOOL", "FAMILY_TENTACOOL", "FAMILY_GEODUDE", "FAMILY_GEODUDE", "FAMILY_GEODUDE", "FAMILY_PONYTA", "FAMILY_PONYTA", "FAMILY_SLOWPOKE", "FAMILY_SLOWPOKE", "FAMILY_MAGNEMITE", "FAMILY_MAGNEMITE", "FAMILY_FARFETCHD", "FAMILY_DODUO", "FAMILY_DODUO", "FAMILY_SEEL", "FAMILY_SEEL", "FAMILY_GRIMER", "FAMILY_GRIMER", "FAMILY_SHELLDER", "FAMILY_SHELLDER", "FAMILY_GASTLY", "FAMILY_GASTLY", "FAMILY_GASTLY", "FAMILY_ONIX", "FAMILY_DROWZEE", "FAMILY_DROWZEE", "FAMILY_KRABBY", "FAMILY_KRABBY", "FAMILY_VOLTORB", "FAMILY_VOLTORB", "FAMILY_EXEGGCUTE", "FAMILY_EXEGGCUTE", "FAMILY_CUBONE", "FAMILY_CUBONE", "FAMILY_HITMONLEE", "FAMILY_HITMONCHAN", "FAMILY_LICKITUNG", "FAMILY_KOFFING", "FAMILY_KOFFING", "FAMILY_RHYHORN", "FAMILY_RHYHORN", "FAMILY_CHANSEY", "FAMILY_TANGELA", "FAMILY_KANGASKHAN", "FAMILY_HORSEA", "FAMILY_HORSEA", "FAMILY_GOLDEEN", "FAMILY_GOLDEEN", "FAMILY_STARYU", "FAMILY_STARYU", "FAMILY_MR_MIME", "FAMILY_SCYTHER", "FAMILY_JYNX", "FAMILY_ELECTABUZZ", "FAMILY_MAGMAR", "FAMILY_PINSIR", "FAMILY_TAUROS", "FAMILY_MAGIKARP", "FAMILY_MAGIKARP", "FAMILY_LAPRAS", "FAMILY_DITTO", "FAMILY_EEVEE", "FAMILY_EEVEE", "FAMILY_EEVEE", "FAMILY_EEVEE", "FAMILY_PORYGON", "FAMILY_OMANYTE", "FAMILY_OMANYTE", "FAMILY_KABUTO", "FAMILY_KABUTO", "FAMILY_AERODACTYL", "FAMILY_SNORLAX", "FAMILY_ARTICUNO", "FAMILY_ZAPDOS", "FAMILY_MOLTRES", "FAMILY_DRATINI", "FAMILY_DRATINI", "FAMILY_DRATINI", "FAMILY_MEWTWO", "FAMILY_MEW"};
    private static final int[] EVOLUTIONS = {0, 2, 3, 0, 5, 6, 0, 8, 9, 0, 11, 12, 0, 14, 15, 0, 17, 18, 0, 20, 0, 22, 0, 24, 0, 26, 0, 28, 0, 30, 31, 32, 33, 34, 0, 36, 0, 38, 0, 40, 0, 42, 0, 44, 45, 0, 47, 0, 49, 0, 51, 0, 53, 0, 55, 0, 57, 0, 59, 0, 61, 62, 0, 64, 65, 0, 67, 68, 0, 70, 71, 0, 73, 0, 75, 76, 0, 78, 0, 80, 0, 82, 0, 0, 85, 0, 87, 0, 89, 0, 91, 0, 93, 94, 0, 0, 97, 0, 99, 0, 101, 0, 103, 0, 105, 0, 0, 0, 0, 110, 0, 112, 0, 0, 0, 0, 117, 0, 119, 0, 121, 0, 0, 0, 0, 0, 0, 0, 0, 130, 0, 0, 0, 134, 135, 136, 0, 0, 139, 0, 141, 0, 0, 0, 0, 0, 0, 148, 149, 0, 0, 0};

    //arrays of all pokemon stamina/atk/def, stardust, CP multipliers at each level
    private static final int[] STAMINAS = {0, 90, 120, 160, 78, 116, 156, 88, 118, 158, 90, 100, 120, 80, 90, 130, 80, 126, 166, 60, 110, 80, 130, 70, 120, 70, 120, 100, 150, 110, 140, 180, 92, 122, 162, 140, 190, 76, 146, 230, 280, 80, 150, 90, 120, 150, 70, 120, 120, 140, 20, 70, 80, 130, 100, 160, 80, 130, 110, 180, 80, 130, 180, 50, 80, 110, 140, 160, 180, 100, 130, 160, 80, 160, 80, 110, 160, 100, 130, 180, 190, 50, 100, 104, 70, 120, 130, 180, 160, 210, 60, 100, 60, 90, 120, 70, 120, 170, 60, 110, 80, 120, 120, 190, 100, 120, 100, 100, 180, 80, 130, 160, 210, 500, 130, 210, 60, 110, 90, 160, 60, 120, 80, 140, 130, 130, 130, 130, 150, 40, 190, 260, 96, 110, 260, 130, 130, 130, 70, 140, 60, 120, 160, 320, 180, 180, 180, 82, 122, 182, 212, 200};
    private static final int[] ATKS = {0, 126, 156, 198, 128, 160, 212, 112, 144, 186, 62, 56, 144, 68, 62, 144, 94, 126, 170, 92, 146, 102, 168, 112, 166, 124, 200, 90, 150, 100, 132, 184, 110, 142, 204, 116, 178, 106, 176, 98, 168, 88, 164, 134, 162, 202, 122, 162, 108, 172, 108, 148, 104, 156, 132, 194, 122, 178, 156, 230, 108, 132, 180, 110, 150, 186, 118, 154, 198, 158, 190, 222, 106, 170, 106, 142, 176, 168, 200, 110, 184, 128, 186, 138, 126, 182, 104, 156, 124, 180, 120, 196, 136, 172, 204, 90, 104, 162, 116, 178, 102, 150, 110, 232, 102, 140, 148, 138, 126, 136, 190, 110, 166, 40, 164, 142, 122, 176, 112, 172, 130, 194, 154, 176, 172, 198, 214, 184, 148, 42, 192, 186, 110, 114, 186, 192, 238, 156, 132, 180, 148, 190, 182, 180, 198, 232, 242, 128, 170, 250, 284, 220};
    private static final int[] DEFS = {0, 126, 158, 200, 108, 140, 182, 142, 176, 222, 66, 86, 144, 64, 82, 130, 90, 122, 166, 86, 150, 78, 146, 112, 166, 108, 154, 114, 172, 104, 136, 190, 94, 128, 170, 124, 178, 118, 194, 54, 108, 90, 164, 130, 158, 190, 120, 170, 118, 154, 86, 140, 94, 146, 112, 176, 96, 150, 110, 180, 98, 132, 202, 76, 112, 152, 96, 144, 180, 78, 110, 152, 136, 196, 118, 156, 198, 138, 170, 110, 198, 138, 180, 132, 96, 150, 138, 192, 110, 188, 112, 196, 82, 118, 156, 186, 140, 196, 110, 168, 124, 174, 132, 164, 150, 202, 172, 204, 160, 142, 198, 116, 160, 60, 152, 178, 100, 150, 126, 160, 128, 192, 196, 180, 134, 160, 158, 186, 184, 84, 196, 190, 110, 128, 168, 174, 178, 158, 160, 202, 142, 190, 162, 180, 242, 194, 194, 110, 152, 212, 202, 220};
    private static final int[] STARDUST_AMOUNTS = {0, 200, 200, 200, 200, 400, 400, 400, 400, 600, 600, 600, 600, 800, 800, 800, 800, 1000, 1000, 1000, 1000, 1300, 1300, 1300, 1300, 1600, 1600, 1600, 1600, 1900, 1900, 1900, 1900, 2200, 2200, 2200, 2200, 2500, 2500, 2500, 2500, 3000, 3000, 3000, 3000, 3500, 3500, 3500, 3500, 4000, 4000, 4000, 4000, 4500, 4500, 4500, 4500, 5000, 5000, 5000, 5000, 6000, 6000, 6000, 6000, 7000, 7000, 7000, 7000, 8000, 8000, 8000, 8000, 9000, 9000, 9000, 9000, 10000, 10000, 10000};
    private static final double[] CP_MULTIPLIERS = {0, 0.094, 0.1351374, 0.1663979, 0.1926509, 0.2157325, 0.2365727, 0.2557201, 0.2735304, 0.2902499, 0.3060574, 0.3210876, 0.335445, 0.3492127, 0.3624578, 0.3752356, 0.3875924, 0.3995673, 0.4111936, 0.4225, 0.4329264, 0.4431076, 0.45306, 0.4627984, 0.4723361, 0.481685, 0.4908558, 0.4998584, 0.5087018, 0.517394, 0.5259425, 0.5343543, 0.5426358, 0.5507927, 0.5588306, 0.5667545, 0.5745692, 0.5822789, 0.5898879, 0.5974, 0.6048188, 0.6121573, 0.6194041, 0.6265671, 0.6336492, 0.640653, 0.647581, 0.6544356, 0.6612193, 0.667934, 0.6745819, 0.6811649, 0.6876849, 0.6941437, 0.7005429, 0.7068842, 0.7131691, 0.7193991, 0.7255756, 0.7317, 0.734741, 0.7377695, 0.7407856, 0.7437894, 0.7467812, 0.749761, 0.7527291, 0.7556855, 0.7586304, 0.7615638, 0.7644861, 0.7673972, 0.7702973, 0.7731865, 0.776065, 0.7789328, 0.7817901, 0.784637, 0.7874736, 0.7903};

    //Owned pokemon visible stats (Entered)
    private int mPokemonNumber;
    private String mPokemonName;
    private String mPokemonFamily;
    private int mHP;
    private int mCP;
    private int mStardust;
    private boolean mFreshMeat;

    private boolean hasHP;
    private boolean hasStardust;
    private int mUniqueID;
    private String mNickname;

    //possible levels of pokemon based on stardust & whether powered up
    private ArrayList<Integer> mLevelRange;

    //calculating values
    private ArrayList<double[]> mIVCombinationsArray;
    private int mSumAllStats;
    int mNumberOfResults;
    private ArrayList<Double> mIVPercentRange;
    private String mStringOutput;

    //PoGo recalculated base stats (direct from table)
    private int mGoBaseStamina;
    private int mGoBaseAtk;
    private int mGoBaseDef;


    public Pokemon(String pokemonName, int hp, int cp, int stardust, boolean freshMeat, int knownLevel) {

        if ((hp >= 0 && hp < 10) || (cp >= 0 && cp < 10))
            throw new IllegalArgumentException("HP and CP cannot be less than 10.");

        //hp/stardust will be -1 if no input
        hasHP = (hp >= 0);
        hasStardust = (stardust >= 0);
        Log.d(TAG, "HasHP=" + hasHP + ", HasStardust=" + hasStardust);

        mUniqueID = UUID.randomUUID().hashCode();
        mHP = hp;
        mCP = cp;
        mStardust = stardust;
        mPokemonName = pokemonName;
        mPokemonNumber = getPokemonNumberFromName(pokemonName);
        mFreshMeat = freshMeat;
        if (mPokemonNumber == 0) {
            throw new IllegalArgumentException("Your Pokemon name is not valid.");
        }
        mPokemonFamily = getPokemonFamilyFromNumber(mPokemonNumber);

        mGoBaseStamina = STAMINAS[mPokemonNumber];
        mGoBaseAtk = ATKS[mPokemonNumber];
        mGoBaseDef = DEFS[mPokemonNumber];

        mLevelRange = new ArrayList<Integer>();
        mIVCombinationsArray = new ArrayList<double[]>();
        mIVPercentRange = new ArrayList<Double>();
        if (knownLevel > 0)
            mLevelRange.add(knownLevel);
        else
            getLevelsFromStardust(mStardust, freshMeat);

        generateIV();
    }


    public void generateIV() {

        Log.d(TAG, "Possible Level Range is " + mLevelRange.get(0) + " to " + mLevelRange.get(mLevelRange.size() - 1));


        for (int i = 0; i < mLevelRange.size(); i++) {
            int level = mLevelRange.get(i);
            double cpMHolding = CP_MULTIPLIERS[level];

            for (int staIV = 0; staIV <= 15; staIV++) {
                int hp = calculateHPFromStamina(mGoBaseStamina, staIV, cpMHolding);

                if (!hasHP || hp == mHP) {
                    for (int atkIV = 15; atkIV >= 0; atkIV--) {

                        for (int defIV = 0; defIV <= 15; defIV++) {
                            int cpHolding = calculateCP(staIV, atkIV, defIV, cpMHolding);

                            if (cpHolding == mCP) {
                                Log.d(TAG, "ivCombo " + i + ": level " + level + " CPM " + cpMHolding + " Sta " + staIV + " Atk " + atkIV + " Def " + defIV);
                                double percent = (staIV + atkIV + defIV) / 45f * 100f;
                                mSumAllStats += (staIV + atkIV + defIV);
                                mNumberOfResults += 1;
                                mIVPercentRange.add(percent);
                                double[] ivArrayTemp = {level, staIV, atkIV, defIV, percent, cpMHolding};
                                mIVCombinationsArray.add(ivArrayTemp);

                            } else if (cpHolding > mCP)
                                break;

                        }
                    }
                } else if (hp > mHP)
                    break;

            }
        }
        Collections.sort(mIVPercentRange);
        StringBuilder sb = new StringBuilder();

        sb.append("Pokemon Name is " + mPokemonName + ", Pokedex Number " + mPokemonNumber + "\n");
        sb.append("Possible Level Range is " + mLevelRange.get(0) + " to " + mLevelRange.get(mLevelRange.size() - 1) + "\n");
        sb.append("Listed as Stamina/Attack/Defence\n\n");
        if (mNumberOfResults != 0) {
            sb.append("Estimated average power " + String.format(Locale.US, "%.1f", getAveragePower()) + "%, range is " + String.format(Locale.US, "%.1f", mIVPercentRange.get(0)) + "% to " + String.format(Locale.US, "%.1f", mIVPercentRange.get(mIVPercentRange.size() - 1)) + "%, " + mNumberOfResults + " possible combinations.\n\n");

            for (int i = 0; i < mIVCombinationsArray.size(); i++) {
                double[] tempArray = mIVCombinationsArray.get(i);
                sb.append("Level " + (int) tempArray[0] + " : " + (int) tempArray[1] + "/" + (int) tempArray[2] + "/" + (int) tempArray[3] + "    " + String.format(Locale.US, "%.1f", tempArray[4]) + "%\n");
            }
        } else {
            sb.append("There were no combinations found.");
        }
        mStringOutput = sb.toString();
    }


    //consider using a switch instead if slow
    private void getLevelsFromStardust(int stardust, boolean freshMeat) {
        //handles case where no stardust input, returns all levels
        if (!hasStardust) {
            if (freshMeat) {
                for (int i = 1; i < STARDUST_AMOUNTS.length; i += 2)
                    mLevelRange.add(i);
            } else {
                for (int i = 1; i < STARDUST_AMOUNTS.length; i++)
                    mLevelRange.add(i);
            }
        } else {
            //if not powered up, omits even levels
            if (freshMeat) {
                for (int i = 1; i < STARDUST_AMOUNTS.length; i += 2) {
                    if (stardust == STARDUST_AMOUNTS[i]) {
                        mLevelRange.add(i);
                        Log.d(TAG, "getLevelsFromStardust: " + mStardust + i);
                    } else if (stardust < STARDUST_AMOUNTS[i])
                        break;
                }
            } else {
                for (int i = 1; i < STARDUST_AMOUNTS.length; i++) {
                    if (stardust == STARDUST_AMOUNTS[i]) {
                        mLevelRange.add(i);
                    } else if (stardust < STARDUST_AMOUNTS[i])
                        break;
                }
            }
        }
        if (mLevelRange.size() == 0) {
            throw new IllegalArgumentException("Please re-enter a valid stardust amount and try again.");
        }
    }

    public boolean customEquals(Pokemon pokemon) {
        boolean temp = (pokemon.mHP==this.mHP&&pokemon.mCP==this.mCP&&pokemon.mStardust==this.mStardust);

        return temp;
    }

    private int getPokemonNumberFromName(String pokemonName) {
        int pokemonNumber = 0;
        for (int i = 0; i < POKEDEX.length; i++) {
            if (pokemonName.equals(POKEDEX[i])) {
                pokemonNumber = i;
                break;
            }
        }
        return pokemonNumber;
    }

    private String getPokemonFamilyFromNumber(int pokemonNumber) {
        return POKEMON_FAMILIES[pokemonNumber];
    }

    private int calculateHPFromStamina(int baseStamina, int staminaIV, double totalCPMultiplier) {
        int hp = Math.max(((int) Math.floor((baseStamina + staminaIV) * totalCPMultiplier)), 10);
        return hp;
    }

    private int calculateCP(int staIV, int atkIV, int defIV, double totalCPM) {
        double workingCP = (mGoBaseAtk + atkIV) * Math.pow((mGoBaseDef + defIV), 0.5) * Math.pow((mGoBaseStamina + staIV), 0.5) * Math.pow(totalCPM, 2) / 10;
        int actualCP = Math.max(10, (int) Math.floor(workingCP));
        return actualCP;
    }

    public double getAveragePower() {
        return mSumAllStats / mNumberOfResults / 45f * 100f;
    }

    public int getUniqueID() {
        return mUniqueID;
    }

    public void setUniqueID(int a) {
        mUniqueID = a;
    }

    public int getCP() {
        return mCP;
    }

    public int getHP() {
        return mHP;
    }

    public boolean getFreshMeat(){
        return mFreshMeat;
    }

    public int getStardust() {
        return mStardust;
    }

    public String getPokemonName() {
        return mPokemonName;
    }

    public int getPokemonNumber() {
        return mPokemonNumber;
    }

    public String getPokemonFamily() { return mPokemonFamily;}

    public ArrayList<Double> getIVPercentRange() {
        return mIVPercentRange;
    }

    public int getSumAllPossibleStats() {
        return mSumAllStats;
    }

    public ArrayList<double[]> getIVCombinationsArray() {
        return mIVCombinationsArray;
    }

    public int getNumberOfResults() {
        return mNumberOfResults;
    }

    public ArrayList<Integer> getLevelRange() {
        return mLevelRange;
    }

    public int getEvolvesTo(){
        return EVOLUTIONS[mPokemonNumber];
    }

    public String getStringOutput() {
        return mStringOutput;
    }

    public int getBaseSta() {
        return mGoBaseStamina;
    }

    public int getBaseAtk() {
        return mGoBaseAtk;
    }

    public int getBaseDef(){
        return mGoBaseDef;
    }

    public String getNickname() {
        return mNickname;
    }
}
