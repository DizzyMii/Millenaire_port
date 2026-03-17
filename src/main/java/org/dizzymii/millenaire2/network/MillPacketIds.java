package org.dizzymii.millenaire2.network;

/**
 * Packet type and GUI action constants, ported from ServerReceiver.
 * These mirror the original mod's protocol IDs exactly for documentation purposes.
 */
public final class MillPacketIds {

    private MillPacketIds() {}

    // ========== Server → Client packet types ==========
    public static final int PACKET_BUILDING = 2;
    public static final int PACKET_VILLAGER = 3;
    public static final int PACKET_MILLCHEST = 5;
    public static final int PACKET_MAPINFO = 7;
    public static final int PACKET_VILLAGELIST = 9;
    public static final int PACKET_SERVER_CONTENT = 10;
    public static final int PACKET_SHOP = 11;
    public static final int PACKET_ALL_VILLAGER_RECORD = 12;
    public static final int PACKET_TRANSLATED_CHAT = 100;
    public static final int PACKET_PROFILE = 101;
    public static final int PACKET_QUESTINSTANCE = 102;
    public static final int PACKET_QUESTINSTANCE_DESTROY = 103;
    public static final int PACKET_OPENGUI = 104;
    public static final int PACKET_ANIMALBREED = 107;
    public static final int PACKET_VILLAGER_SENTENCE = 108;
    public static final int PACKET_ADVANCEMENT_EARNED = 109;
    public static final int PACKET_CONTENT_UNLOCKED = 110;
    public static final int PACKET_CONTENT_UNLOCKED_MULTIPLE = 111;

    // ========== Client → Server packet types ==========
    public static final int PACKET_GUIACTION = 200;
    public static final int PACKET_VILLAGELIST_REQUEST = 201;
    public static final int PACKET_DECLARERELEASENUMBER = 202;
    public static final int PACKET_MAPINFO_REQUEST = 203;
    public static final int PACKET_VILLAGERINTERACT_REQUEST = 204;
    public static final int PACKET_AVAILABLECONTENT = 205;
    public static final int PACKET_DEVCOMMAND = 206;

    // ========== GUI action sub-IDs ==========
    public static final int GUIACTION_CHIEF_BUILDING = 1;
    public static final int GUIACTION_CHIEF_CROP = 2;
    public static final int GUIACTION_CHIEF_CONTROL = 3;
    public static final int GUIACTION_CHIEF_DIPLOMACY = 4;
    public static final int GUIACTION_CHIEF_SCROLL = 5;
    public static final int GUIACTION_CHIEF_HUNTING_DROP = 6;
    public static final int GUIACTION_QUEST_COMPLETESTEP = 10;
    public static final int GUIACTION_QUEST_REFUSE = 11;
    public static final int GUIACTION_NEWVILLAGE = 20;
    public static final int GUIACTION_HIRE_HIRE = 30;
    public static final int GUIACTION_HIRE_EXTEND = 31;
    public static final int GUIACTION_HIRE_RELEASE = 32;
    public static final int GUIACTION_TOGGLE_STANCE = 33;
    public static final int GUIACTION_NEGATION_WAND = 40;
    public static final int GUIACTION_NEW_BUILDING_PROJECT = 50;
    public static final int GUIACTION_NEW_CUSTOM_BUILDING_PROJECT = 51;
    public static final int GUIACTION_UPDATE_CUSTOM_BUILDING_PROJECT = 52;
    public static final int GUIACTION_PUJAS_CHANGE_ENCHANTMENT = 60;
    public static final int GUIACTION_TRADE_TOGGLE_DONATION = 61;
    public static final int GUIACTION_CONTROLLEDBUILDING_TOGGLEALLOWED = 70;
    public static final int GUIACTION_CONTROLLEDBUILDING_FORGET = 71;
    public static final int GUIACTION_MILLCHESTACTIVATE = 81;
    public static final int GUIACTION_MILITARY_RELATIONS = 90;
    public static final int GUIACTION_MILITARY_RAID = 91;
    public static final int GUIACTION_MILITARY_CANCEL_RAID = 92;
    public static final int GUIACTION_IMPORTTABLE_IMPORTBUILDINGPLAN = 100;
    public static final int GUIACTION_IMPORTTABLE_CHANGESETTINGS = 101;
    public static final int GUIACTION_IMPORTTABLE_CREATEBUILDING = 102;

    // ========== Dev command sub-IDs ==========
    public static final int DEV_COMMAND_TOGGLE_AUTO_MOVE = 1;
    public static final int DEV_COMMAND_TEST_PATH = 2;

    // ========== Open GUI sub-IDs (used with PACKET_OPENGUI) ==========
    public static final int GUI_MILLCHEST = 1;
    public static final int GUI_QUEST = 3;
    public static final int GUI_VILLAGE_BOOK = 5;
    public static final int GUI_PANEL = 7;
    public static final int GUI_TRADE = 8;
    public static final int GUI_NEGATION = 9;
    public static final int GUI_NEW_BUILDING = 10;
    public static final int GUI_CONTROLLED_PROJECT = 11;
    public static final int GUI_HIRE = 12;
    public static final int GUI_NEW_VILLAGE = 13;
    public static final int GUI_CONTROLLED_MILITARY = 14;
    public static final int GUI_IMPORT_TABLE = 15;
}
