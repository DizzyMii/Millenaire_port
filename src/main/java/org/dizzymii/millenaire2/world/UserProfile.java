package org.dizzymii.millenaire2.world;

import org.dizzymii.millenaire2.util.Point;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Player reputation, diplomacy, unlocked content, and quest data.
 * Ported from org.millenaire.common.world.UserProfile (Forge 1.12.2).
 */
public class UserProfile {

    // ========== Update type constants ==========
    public static final int UPDATE_ALL = 1;
    public static final int UPDATE_REPUTATION = 2;
    public static final int UPDATE_DIPLOMACY = 3;
    public static final int UPDATE_ACTIONDATA = 4;
    public static final int UPDATE_TAGS = 5;
    public static final int UPDATE_LANGUAGE = 6;
    public static final int UPDATE_GLOBAL_TAGS = 7;
    public static final int UPDATE_UNLOCKED_CONTENT = 8;

    public static final int UNLOCKED_BUILDING = 1;
    public static final int UNLOCKED_VILLAGE = 2;
    public static final int UNLOCKED_VILLAGER = 3;
    public static final int UNLOCKED_TRADE_GOOD = 4;

    public static final int FRIEND_OF_THE_VILLAGE = 8192;
    public static final int ONE_OF_US = 32768;

    // ========== Fields ==========
    private final Set<String> unlockedVillagers = new HashSet<>();
    private final Set<String> unlockedVillages = new HashSet<>();
    private final Set<String> unlockedBuildings = new HashSet<>();
    private final Set<String> unlockedTradeGoods = new HashSet<>();

    private final HashMap<Point, Integer> villageReputations = new HashMap<>();
    private final HashMap<Point, Byte> villageDiplomacy = new HashMap<>();
    private final HashMap<String, Integer> cultureReputations = new HashMap<>();
    private final HashMap<String, Integer> cultureLanguages = new HashMap<>();
    private final List<String> profileTags = new ArrayList<>();

    @Nullable public UUID uuid;
    @Nullable public String playerName;
    public boolean donationActivated = false;

    // TODO: List<QuestInstance> questInstances — depends on quest system (Phase 7)

    // ========== Reputation ==========

    public int getVillageReputation(Point villagePos) {
        return villageReputations.getOrDefault(villagePos, 0);
    }

    public void setVillageReputation(Point villagePos, int rep) {
        villageReputations.put(villagePos, rep);
    }

    public void adjustVillageReputation(Point villagePos, int delta) {
        villageReputations.merge(villagePos, delta, Integer::sum);
    }

    public int getCultureReputation(String cultureKey) {
        return cultureReputations.getOrDefault(cultureKey, 0);
    }

    public void setCultureReputation(String cultureKey, int rep) {
        cultureReputations.put(cultureKey, rep);
    }

    public int getCultureLanguage(String cultureKey) {
        return cultureLanguages.getOrDefault(cultureKey, 0);
    }

    public void setCultureLanguage(String cultureKey, int level) {
        cultureLanguages.put(cultureKey, level);
    }

    // ========== Unlocked content ==========

    public boolean hasUnlockedVillager(String key) { return unlockedVillagers.contains(key); }
    public void unlockVillager(String key) { unlockedVillagers.add(key); }

    public boolean hasUnlockedVillage(String key) { return unlockedVillages.contains(key); }
    public void unlockVillage(String key) { unlockedVillages.add(key); }

    public boolean hasUnlockedBuilding(String key) { return unlockedBuildings.contains(key); }
    public void unlockBuilding(String key) { unlockedBuildings.add(key); }

    public boolean hasUnlockedTradeGood(String key) { return unlockedTradeGoods.contains(key); }
    public void unlockTradeGood(String key) { unlockedTradeGoods.add(key); }

    // ========== Tags ==========

    public List<String> getProfileTags() { return profileTags; }
    public boolean hasTag(String tag) { return profileTags.contains(tag); }
    public void addTag(String tag) { if (!profileTags.contains(tag)) profileTags.add(tag); }
    public void removeTag(String tag) { profileTags.remove(tag); }

    // TODO: NBT save/load, packet serialisation
}
