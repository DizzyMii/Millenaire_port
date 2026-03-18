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

    static {
        // Register all individual advancements
        MILL_ADVANCEMENTS.add(FIRST_CONTACT);
        MILL_ADVANCEMENTS.add(CRESUS);
        MILL_ADVANCEMENTS.add(SUMMONING_WAND);
        MILL_ADVANCEMENTS.add(AMATEUR_ARCHITECT);
        MILL_ADVANCEMENTS.add(MEDIEVAL_METROPOLIS);
        MILL_ADVANCEMENTS.add(THE_QUEST);
        MILL_ADVANCEMENTS.add(MAITRE_A_PENSER);
        MILL_ADVANCEMENTS.add(EXPLORER);
        MILL_ADVANCEMENTS.add(MARCO_POLO);
        MILL_ADVANCEMENTS.add(MAGELLAN);
        MILL_ADVANCEMENTS.add(SELF_DEFENSE);
        MILL_ADVANCEMENTS.add(PANTHEON);
        MILL_ADVANCEMENTS.add(DARK_SIDE);
        MILL_ADVANCEMENTS.add(SCIPIO);
        MILL_ADVANCEMENTS.add(ATTILA);
        MILL_ADVANCEMENTS.add(VIKING);
        MILL_ADVANCEMENTS.add(CHEERS);
        MILL_ADVANCEMENTS.add(HIRED);
        MILL_ADVANCEMENTS.add(MASTER_FARMER);
        MILL_ADVANCEMENTS.add(GREAT_HUNTER);
        MILL_ADVANCEMENTS.add(A_FRIEND_INDEED);
        MILL_ADVANCEMENTS.add(RAINBOW);
        MILL_ADVANCEMENTS.add(ISTANBUL);
        MILL_ADVANCEMENTS.add(NOTTODAY);
        MILL_ADVANCEMENTS.add(MP_WEAPON);
        MILL_ADVANCEMENTS.add(MP_HIREDGOON);
        MILL_ADVANCEMENTS.add(MP_FRIENDLYVILLAGE);
        MILL_ADVANCEMENTS.add(MP_NEIGHBOURTRADE);
        MILL_ADVANCEMENTS.add(MP_RAIDONPLAYER);
        MILL_ADVANCEMENTS.add(WQ_INDIAN);
        MILL_ADVANCEMENTS.add(WQ_NORMAN);
        MILL_ADVANCEMENTS.add(WQ_MAYAN);
        MILL_ADVANCEMENTS.add(PUJA);
        MILL_ADVANCEMENTS.add(SACRIFICE);
        MILL_ADVANCEMENTS.add(MARVEL_NORMAN);

        // Per-culture advancement maps
        for (String culture : ADVANCEMENT_CULTURES) {
            GenericAdvancement rep = new GenericAdvancement(culture + "_reputation");
            GenericAdvancement complete = new GenericAdvancement(culture + "_complete");
            GenericAdvancement leader = new GenericAdvancement(culture + "_villageleader");

            REP_ADVANCEMENTS.put(culture, rep);
            COMPLETE_ADVANCEMENTS.put(culture, complete);
            VILLAGE_LEADER_ADVANCEMENTS.put(culture, leader);

            MILL_ADVANCEMENTS.add(rep);
            MILL_ADVANCEMENTS.add(complete);
            MILL_ADVANCEMENTS.add(leader);
        }
    }

    /**
     * Grants a culture-specific reputation advancement to the player.
     */
    public static void grantRepAdvancement(net.minecraft.server.level.ServerPlayer player, String cultureKey) {
        GenericAdvancement adv = REP_ADVANCEMENTS.get(cultureKey);
        if (adv != null) adv.grant(player);
    }

    /**
     * Grants a culture-specific completion advancement to the player.
     */
    public static void grantCompleteAdvancement(net.minecraft.server.level.ServerPlayer player, String cultureKey) {
        GenericAdvancement adv = COMPLETE_ADVANCEMENTS.get(cultureKey);
        if (adv != null) adv.grant(player);
    }

    /**
     * Grants a culture-specific village leader advancement to the player.
     */
    public static void grantVillageLeaderAdvancement(net.minecraft.server.level.ServerPlayer player, String cultureKey) {
        GenericAdvancement adv = VILLAGE_LEADER_ADVANCEMENTS.get(cultureKey);
        if (adv != null) adv.grant(player);
    }

    /**
     * Returns a map of advancement key -> earned status for the given player.
     */
    public static Map<String, Boolean> getPlayerAdvancementStatus(net.minecraft.server.level.ServerPlayer player) {
        Map<String, Boolean> status = new HashMap<>();
        for (GenericAdvancement adv : MILL_ADVANCEMENTS) {
            status.put(adv.getKey(), adv.isEarned(player));
        }
        return status;
    }
}
