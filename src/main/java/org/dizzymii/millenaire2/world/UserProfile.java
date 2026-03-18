package org.dizzymii.millenaire2.world;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import org.dizzymii.millenaire2.util.Point;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
    public int deniers = 0;

    // Quest instances tracked per-player; populated when quest system is implemented (Phase 7)
    public final java.util.List<org.dizzymii.millenaire2.quest.QuestInstance> questInstances = new java.util.ArrayList<>();

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

    // ========== NBT persistence ==========

    public Map<Point, Integer> getVillageReputations() { return villageReputations; }
    public Map<String, Integer> getCultureReputations() { return cultureReputations; }
    public Map<String, Integer> getCultureLanguages() { return cultureLanguages; }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        if (uuid != null) tag.putUUID("uuid", uuid);
        if (playerName != null) tag.putString("playerName", playerName);
        tag.putBoolean("donation", donationActivated);
        tag.putInt("deniers", deniers);

        // Village reputations
        ListTag repList = new ListTag();
        for (Map.Entry<Point, Integer> entry : villageReputations.entrySet()) {
            CompoundTag e = new CompoundTag();
            entry.getKey().writeToNBT(e, "p");
            e.putInt("rep", entry.getValue());
            repList.add(e);
        }
        tag.put("villageRep", repList);

        // Village diplomacy
        ListTag dipList = new ListTag();
        for (Map.Entry<Point, Byte> entry : villageDiplomacy.entrySet()) {
            CompoundTag e = new CompoundTag();
            entry.getKey().writeToNBT(e, "p");
            e.putByte("dip", entry.getValue());
            dipList.add(e);
        }
        tag.put("villageDip", dipList);

        // Culture reputations
        CompoundTag cultureRepTag = new CompoundTag();
        for (Map.Entry<String, Integer> entry : cultureReputations.entrySet()) {
            cultureRepTag.putInt(entry.getKey(), entry.getValue());
        }
        tag.put("cultureRep", cultureRepTag);

        // Culture languages
        CompoundTag cultureLangTag = new CompoundTag();
        for (Map.Entry<String, Integer> entry : cultureLanguages.entrySet()) {
            cultureLangTag.putInt(entry.getKey(), entry.getValue());
        }
        tag.put("cultureLang", cultureLangTag);

        // Tags
        ListTag tagList = new ListTag();
        for (String t : profileTags) {
            tagList.add(StringTag.valueOf(t));
        }
        tag.put("tags", tagList);

        // Unlocked content
        tag.put("unlockedVillagers", saveStringSet(unlockedVillagers));
        tag.put("unlockedVillages", saveStringSet(unlockedVillages));
        tag.put("unlockedBuildings", saveStringSet(unlockedBuildings));
        tag.put("unlockedTradeGoods", saveStringSet(unlockedTradeGoods));

        return tag;
    }

    public static UserProfile load(CompoundTag tag) {
        UserProfile p = new UserProfile();
        if (tag.hasUUID("uuid")) p.uuid = tag.getUUID("uuid");
        if (tag.contains("playerName")) p.playerName = tag.getString("playerName");
        p.donationActivated = tag.getBoolean("donation");
        p.deniers = tag.getInt("deniers");

        // Village reputations
        if (tag.contains("villageRep", Tag.TAG_LIST)) {
            ListTag repList = tag.getList("villageRep", Tag.TAG_COMPOUND);
            for (int i = 0; i < repList.size(); i++) {
                CompoundTag e = repList.getCompound(i);
                Point pt = Point.readFromNBT(e, "p");
                if (pt != null) p.villageReputations.put(pt, e.getInt("rep"));
            }
        }

        // Village diplomacy
        if (tag.contains("villageDip", Tag.TAG_LIST)) {
            ListTag dipList = tag.getList("villageDip", Tag.TAG_COMPOUND);
            for (int i = 0; i < dipList.size(); i++) {
                CompoundTag e = dipList.getCompound(i);
                Point pt = Point.readFromNBT(e, "p");
                if (pt != null) p.villageDiplomacy.put(pt, e.getByte("dip"));
            }
        }

        // Culture reputations
        if (tag.contains("cultureRep", Tag.TAG_COMPOUND)) {
            CompoundTag cr = tag.getCompound("cultureRep");
            for (String key : cr.getAllKeys()) {
                p.cultureReputations.put(key, cr.getInt(key));
            }
        }

        // Culture languages
        if (tag.contains("cultureLang", Tag.TAG_COMPOUND)) {
            CompoundTag cl = tag.getCompound("cultureLang");
            for (String key : cl.getAllKeys()) {
                p.cultureLanguages.put(key, cl.getInt(key));
            }
        }

        // Tags
        if (tag.contains("tags", Tag.TAG_LIST)) {
            ListTag tagList = tag.getList("tags", Tag.TAG_STRING);
            for (int i = 0; i < tagList.size(); i++) {
                p.profileTags.add(tagList.getString(i));
            }
        }

        // Unlocked content
        loadStringSet(tag, "unlockedVillagers", p.unlockedVillagers);
        loadStringSet(tag, "unlockedVillages", p.unlockedVillages);
        loadStringSet(tag, "unlockedBuildings", p.unlockedBuildings);
        loadStringSet(tag, "unlockedTradeGoods", p.unlockedTradeGoods);

        return p;
    }

    // ========== Packet serialization ==========

    public void writeToBuffer(FriendlyByteBuf buf) {
        CompoundTag tag = save();
        buf.writeNbt(tag);
    }

    public static UserProfile readFromBuffer(FriendlyByteBuf buf) {
        CompoundTag tag = buf.readNbt();
        if (tag == null) return new UserProfile();
        return load(tag);
    }

    // ========== Helpers ==========

    private static ListTag saveStringSet(Set<String> set) {
        ListTag list = new ListTag();
        for (String s : set) {
            list.add(StringTag.valueOf(s));
        }
        return list;
    }

    private static void loadStringSet(CompoundTag parent, String key, Set<String> target) {
        if (parent.contains(key, Tag.TAG_LIST)) {
            ListTag list = parent.getList(key, Tag.TAG_STRING);
            for (int i = 0; i < list.size(); i++) {
                target.add(list.getString(i));
            }
        }
    }
}
