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
    private static final double[] CP_MULTIPLIERS = {0,0.094,0.135137431783352,0.166397869391408,0.192650918521039,0.215732477564228,0.236572668212116,0.255720067288432,0.273530397365631,0.290249898797571,0.306057395305194,0.321087612171507,0.335445047897565,0.349212693967444,0.362457764569888,0.375235601410634,0.387592417411641,0.399567275316686,0.411193546852331,0.422500009990532,0.432926418825417,0.443107560070916,0.453059968952235,0.462798402268202,0.472336095187526,0.481684972250536,0.490855822178978,0.499858443803843,0.508701768737833,0.517393965167743,0.525942526201295,0.534354345488085,0.542635782285872,0.550792717718744,0.558830603642106,0.566754505268375,0.57456913850032,0.582278902753655,0.589887909917639,0.597400009994978,0.604823675003716,0.612157319443295,0.619404140808729,0.626567151671711,0.633649194315751,0.640652953914988,0.647580970429953,0.654435649369745,0.661219271549915,0.667934001958278,0.67458189782635,0.681164915991715,0.687684919625987,0.694143684393945,0.700542904101526,0.706884195883598,0.713169104976513,0.719399109115378,0.725575622591471,0.731700000001367,0.734741009330499,0.737769484040917,0.740785577864472,0.74378944141605,0.746781222281332,0.74976106510141,0.752729111654386,0.755685500934086,0.758630369226015,0.761563850180666,0.764486074884298,0.767397171927289,0.770297267470163,0.773186485307394,0.776064946929057,0.778932771580449,0.781790076319725,0.784636976073649,0.787473583691543,0.79030000999747};
    private static final String[] PNG_FILENAME = {"0","xxx1","xxx2","xxx3","xxx4","xxx5","xxx6","xxx7","xxx8","xxx9","xx10","xx11","xx12","xx13","xx14","xx15","xx16","xx17","xx18","xx19","xx20","xx21","xx22","xx23","xx24","xx25","xx26","xx27","xx28","xx29","xx30","xx31","xx32","xx33","xx34","xx35","xx36","xx37","xx38","xx39","xx40","xx41","xx42","xx43","xx44","xx45","xx46","xx47","xx48","xx49","xx50","xx51","xx52","xx53","xx54","xx55","xx56","xx57","xx58","xx59","xx60","xx61","xx62","xx63","xx64","xx65","xx66","xx67","xx68","xx69","xx70","xx71","xx72","xx73","xx74","xx75","xx76","xx77","xx78","xx79","xx80","xx81","xx82","xx83","xx84","xx85","xx86","xx87","xx88","xx89","xx90","xx91","xx92","xx93","xx94","xx95","xx96","xx97","xx98","xx99","x100","x101","x102","x103","x104","x105","x106","x107","x108","x109","x110","x111","x112","x113","x114","x115","x116","x117","x118","x119","x120","x121","x122","x123","x124","x125","x126","x127","x128","x129","x130","x131","x132","x133","x134","x135","x136","x137","x138","x139","x140","x141","x142","x143","x144","x145","x146","x147","x148","x149","x150","x151"};

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
        //in case of the rare even leveled pokemon caught
        if(mNumberOfResults==0)
        {
            getLevelsFromStardust(mStardust, false);
            generateIV();
        }
        Log.d(TAG, "Pokemon: "+mPokemonName + " "+ calculateCP(0,0,0,CP_MULTIPLIERS[40]));
        Log.d(TAG, "Pokemon: "+mPokemonName + " "+ calculateCP(15,15,15,CP_MULTIPLIERS[40]));
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
                            Log.d(TAG, "generateIV: "+cpHolding + " sta "+ staIV + " atk " + atkIV+ " def " + defIV);

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
        boolean temp = (pokemon.mPokemonName==mPokemonName&&pokemon.mHP==this.mHP&&pokemon.mCP==this.mCP&&pokemon.mStardust==this.mStardust);

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

    public String getPngFileName(){
        return PNG_FILENAME[mPokemonNumber];
    }
}
