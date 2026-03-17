package org.dizzymii.millenaire2.buildingplan;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * All known special point type constants used in building plan PNG colour maps.
 * Ported from org.millenaire.common.buildingplan.SpecialPointTypeList (Forge 1.12.2).
 */
public abstract class SpecialPointTypeList {

    // --- Terrain / empty ---
    public static final String bempty = "empty";
    public static final String bpreserveground = "preserveground";
    public static final String ballbuttrees = "allbuttrees";
    public static final String bgrass = "grass";

    // --- Agriculture soil types ---
    public static final String bsoil = "soil";
    public static final String bricesoil = "ricesoil";
    public static final String bturmericsoil = "turmericsoil";
    public static final String bmaizesoil = "maizesoil";
    public static final String bcarrotsoil = "carrotsoil";
    public static final String bpotatosoil = "potatosoil";
    public static final String bflowersoil = "flowersoil";
    public static final String bsugarcanesoil = "sugarcanesoil";
    public static final String bnetherwartsoil = "netherwartsoil";
    public static final String bvinesoil = "vinesoil";
    public static final String bcottonsoil = "cottonsoil";
    public static final String bsilkwormblock = "silkwormblock";
    public static final String bsnailsoilblock = "snailsoilblock";
    public static final String bcacaospot = "cacaospot";

    // --- Chest positions ---
    public static final String blockedchestGuess = "lockedchestGuess";
    public static final String blockedchestTop = "lockedchestTop";
    public static final String blockedchestBottom = "lockedchestBottom";
    public static final String blockedchestLeft = "lockedchestLeft";
    public static final String blockedchestRight = "lockedchestRight";
    public static final String bmainchestGuess = "mainchestGuess";
    public static final String bmainchestTop = "mainchestTop";
    public static final String bmainchestBottom = "mainchestBottom";
    public static final String bmainchestLeft = "mainchestLeft";
    public static final String bmainchestRight = "mainchestRight";

    // --- Functional positions ---
    public static final String bsleepingPos = "sleepingPos";
    public static final String bsellingPos = "sellingPos";
    public static final String bcraftingPos = "craftingPos";
    public static final String bdefendingPos = "defendingPos";
    public static final String bshelterPos = "shelterPos";
    public static final String bpathStartPos = "pathStartPos";
    public static final String bleasurePos = "leisurePos";

    // --- Guessed block types ---
    public static final String btorchGuess = "torchGuess";
    public static final String bladderGuess = "ladderGuess";
    public static final String bsignwallGuess = "signwallGuess";
    public static final String bfurnaceGuess = "furnaceGuess";

    // --- Free blocks ---
    public static final String bfreestone = "freestone";
    public static final String bfreesand = "freesand";
    public static final String bfreesandstone = "freesandstone";
    public static final String bfreegravel = "freegravel";
    public static final String bfreewool = "freewool";
    public static final String bfreecobblestone = "freecobblestone";
    public static final String bfreestonebrick = "freestonebrick";
    public static final String bfreepaintedbrick = "freepaintedbrick";
    public static final String bfreegrass_block = "freegrass_block";

    // --- Misc ---
    public static final String bstall = "stall";

    public static boolean isSpecialPointTypeKnow(String type) {
        for (Field f : SpecialPointTypeList.class.getDeclaredFields()) {
            try {
                if (f.getType() == String.class && type.equals(f.get(null))) {
                    return true;
                }
            } catch (IllegalAccessException ignored) {}
        }
        return false;
    }

    public static List<String> getAllSpecialPointTypes() {
        List<String> types = new ArrayList<>();
        for (Field f : SpecialPointTypeList.class.getDeclaredFields()) {
            try {
                if (f.getType() == String.class) {
                    types.add((String) f.get(null));
                }
            } catch (IllegalAccessException ignored) {}
        }
        return types;
    }
}
