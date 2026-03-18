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

    // --- Quarry sources ---
    public static final String bstonesource = "stonesource";
    public static final String bsandsource = "sandsource";
    public static final String bsandstonesource = "sandstonesource";
    public static final String bclaysource = "claysource";
    public static final String bgravelsource = "gravelsource";
    public static final String bgranitesource = "granitesource";
    public static final String bdioritesource = "dioritesource";
    public static final String bandesitesource = "andesitesource";
    public static final String bsnowsource = "snowsource";
    public static final String bicesource = "icesource";
    public static final String bredsandstonesource = "redsandstonesource";
    public static final String bquartzsource = "quartzsource";

    // --- Animal spawn spots ---
    public static final String bcowspawn = "cowspawn";
    public static final String bpigspawn = "pigspawn";
    public static final String bsheepspawn = "sheepspawn";
    public static final String bchickenspawn = "chickenspawn";
    public static final String bsquidspawn = "squidspawn";
    public static final String bwolfspawn = "wolfspawn";
    public static final String bpolarbearspawn = "polarbearspawn";

    // --- Tree spawn spots ---
    public static final String boakspawn = "oakspawn";
    public static final String bpinespawn = "pinespawn";
    public static final String bbirchspawn = "birchspawn";
    public static final String bjunglespawn = "junglespawn";
    public static final String bacaciaspawn = "acaciaspawn";
    public static final String bdarkoakspawn = "darkoakspawn";
    public static final String bappletreespawn = "appletreespawn";
    public static final String bolivetreespawn = "olivetreespawn";
    public static final String bpistachiotreespawn = "pistachiotreespawn";
    public static final String bcherrytreespawn = "cherrytreespawn";
    public static final String bsakuratreespawn = "sakuratreespawn";

    // --- Mob spawners ---
    public static final String bspawnerskeleton = "spawnerskeleton";
    public static final String bspawnerzombie = "spawnerzombie";
    public static final String bspawnerspider = "spawnerspider";
    public static final String bspawnercavespider = "spawnercavespider";
    public static final String bspawnercreeper = "spawnercreeper";
    public static final String bspawnerblaze = "spawnerblaze";

    // --- Decorative specials ---
    public static final String btapestry = "tapestry";
    public static final String bindianstatue = "indianstatue";
    public static final String bbyzantineiconsmall = "byzantineiconsmall";
    public static final String bbyzantineiconmedium = "byzantineiconmedium";
    public static final String bbyzantineiconlarge = "byzantineiconlarge";
    public static final String bmayanstatue = "mayanstatue";
    public static final String bwallcarpetsmall = "wallcarpetsmall";
    public static final String bwallcarpetmedium = "wallcarpetmedium";
    public static final String bwallcarpetlarge = "wallcarpetlarge";
    public static final String bhidehanging = "hidehanging";

    // --- Misc ---
    public static final String bstall = "stall";
    public static final String bbrickspot = "brickspot";
    public static final String bplainSignGuess = "plainSignGuess";
    public static final String bdispenserunknownpowder = "dispenserunknownpowder";
    public static final String bbrewingstand = "brewingstand";
    public static final String bhealingspot = "healingspot";
    public static final String bfishingspot = "fishingspot";

    // --- Banners ---
    public static final String bvillageBannerWallGuess = "villageBannerWallGuess";
    public static final String bvillageBannerWallTop = "villageBannerWallTop";
    public static final String bvillageBannerWallBottom = "villageBannerWallBottom";
    public static final String bvillageBannerWallLeft = "villageBannerWallLeft";
    public static final String bvillageBannerWallRight = "villageBannerWallRight";
    public static final String bvillageBannerStandingGuess = "villageBannerStandingGuess";
    public static final String bvillageBannerStandingTop = "villageBannerStandingTop";
    public static final String bvillageBannerStandingBottom = "villageBannerStandingBottom";
    public static final String bvillageBannerStandingLeft = "villageBannerStandingLeft";
    public static final String bvillageBannerStandingRight = "villageBannerStandingRight";
    public static final String bcultureBannerWallGuess = "cultureBannerWallGuess";
    public static final String bcultureBannerWallTop = "cultureBannerWallTop";
    public static final String bcultureBannerWallBottom = "cultureBannerWallBottom";
    public static final String bcultureBannerWallLeft = "cultureBannerWallLeft";
    public static final String bcultureBannerWallRight = "cultureBannerWallRight";
    public static final String bcultureBannerStandingGuess = "cultureBannerStandingGuess";
    public static final String bcultureBannerStandingTop = "cultureBannerStandingTop";
    public static final String bcultureBannerStandingBottom = "cultureBannerStandingBottom";
    public static final String bcultureBannerStandingLeft = "cultureBannerStandingLeft";
    public static final String bcultureBannerStandingRight = "cultureBannerStandingRight";

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
