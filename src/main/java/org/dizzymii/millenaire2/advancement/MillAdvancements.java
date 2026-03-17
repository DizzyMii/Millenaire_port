package org.dizzymii.millenaire2.advancement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * All Millenaire advancement definitions.
 * Ported from org.millenaire.common.advancements.MillAdvancements (Forge 1.12.2).
 */
public class MillAdvancements {

    // --- Culture keys ---
    public static final String NORMAN = "norman";
    public static final String INDIAN = "indian";
    public static final String MAYAN = "mayan";
    public static final String JAPANESE = "japanese";
    public static final String BYZANTINES = "byzantines";
    public static final String INUITS = "inuits";
    public static final String SELJUK = "seljuk";
    public static final String[] ADVANCEMENT_CULTURES = {NORMAN, INDIAN, MAYAN, JAPANESE, BYZANTINES, INUITS, SELJUK};

    public static final int MEDIEVAL_METROPOLIS_VILLAGER_NUMBER = 100;

    // --- Advancement instances ---
    public static final GenericAdvancement FIRST_CONTACT = new GenericAdvancement("firstcontact");
    public static final GenericAdvancement CRESUS = new GenericAdvancement("cresus");
    public static final GenericAdvancement SUMMONING_WAND = new GenericAdvancement("summoningwand");
    public static final GenericAdvancement AMATEUR_ARCHITECT = new GenericAdvancement("amateurarchitect");
    public static final GenericAdvancement MEDIEVAL_METROPOLIS = new GenericAdvancement("medievalmetropolis");
    public static final GenericAdvancement THE_QUEST = new GenericAdvancement("thequest");
    public static final GenericAdvancement MAITRE_A_PENSER = new GenericAdvancement("maitreapenser");
    public static final GenericAdvancement EXPLORER = new GenericAdvancement("explorer");
    public static final GenericAdvancement MARCO_POLO = new GenericAdvancement("marcopolo");
    public static final GenericAdvancement MAGELLAN = new GenericAdvancement("magellan");
    public static final GenericAdvancement SELF_DEFENSE = new GenericAdvancement("selfdefense");
    public static final GenericAdvancement PANTHEON = new GenericAdvancement("pantheon");
    public static final GenericAdvancement DARK_SIDE = new GenericAdvancement("darkside");
    public static final GenericAdvancement SCIPIO = new GenericAdvancement("scipio");
    public static final GenericAdvancement ATTILA = new GenericAdvancement("attila");
    public static final GenericAdvancement VIKING = new GenericAdvancement("viking");
    public static final GenericAdvancement CHEERS = new GenericAdvancement("cheers");
    public static final GenericAdvancement HIRED = new GenericAdvancement("hired");
    public static final GenericAdvancement MASTER_FARMER = new GenericAdvancement("masterfarmer");
    public static final GenericAdvancement GREAT_HUNTER = new GenericAdvancement("greathunter");
    public static final GenericAdvancement A_FRIEND_INDEED = new GenericAdvancement("friendindeed");
    public static final GenericAdvancement RAINBOW = new GenericAdvancement("rainbow");
    public static final GenericAdvancement ISTANBUL = new GenericAdvancement("seljuk_istanbul");
    public static final GenericAdvancement NOTTODAY = new GenericAdvancement("byzantines_nottoday");
    public static final GenericAdvancement MP_WEAPON = new GenericAdvancement("mp_weapon");
    public static final GenericAdvancement MP_HIREDGOON = new GenericAdvancement("mp_hiredgoon");
    public static final GenericAdvancement MP_FRIENDLYVILLAGE = new GenericAdvancement("mp_friendlyvillage");
    public static final GenericAdvancement MP_NEIGHBOURTRADE = new GenericAdvancement("mp_neighbourtrade");
    public static final GenericAdvancement MP_RAIDONPLAYER = new GenericAdvancement("mp_raidonplayer");
    public static final GenericAdvancement WQ_INDIAN = new GenericAdvancement("wq_indian");
    public static final GenericAdvancement WQ_NORMAN = new GenericAdvancement("wq_norman");
    public static final GenericAdvancement WQ_MAYAN = new GenericAdvancement("wq_mayan");
    public static final GenericAdvancement PUJA = new GenericAdvancement("puja");
    public static final GenericAdvancement SACRIFICE = new GenericAdvancement("sacrifice");
    public static final GenericAdvancement MARVEL_NORMAN = new GenericAdvancement("marvel_norman");

    public static final Map<String, GenericAdvancement> REP_ADVANCEMENTS = new HashMap<>();
    public static final Map<String, GenericAdvancement> COMPLETE_ADVANCEMENTS = new HashMap<>();
    public static final Map<String, GenericAdvancement> VILLAGE_LEADER_ADVANCEMENTS = new HashMap<>();
    public static final List<GenericAdvancement> MILL_ADVANCEMENTS = new ArrayList<>();

    // TODO: static initializer to populate REP/COMPLETE/VILLAGE_LEADER maps per culture
    // TODO: registerTriggers() for NeoForge CriteriaTriggers registration
}
