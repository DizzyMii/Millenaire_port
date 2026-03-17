package org.dizzymii.millenaire2.quest;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A single step within a quest definition.
 * Ported from org.millenaire.common.quest.QuestStep (Forge 1.12.2).
 */
public class QuestStep {

    private final Quest quest;
    public int pos;

    public List<String> clearGlobalTagsFailure = new ArrayList<>();
    public List<String> clearGlobalTagsSuccess = new ArrayList<>();
    public List<String> clearPlayerTagsFailure = new ArrayList<>();
    public List<String> clearPlayerTagsSuccess = new ArrayList<>();
    public List<String[]> clearTagsFailure = new ArrayList<>();
    public List<String[]> clearTagsSuccess = new ArrayList<>();

    public final HashMap<String, String> descriptions = new HashMap<>();
    public final HashMap<String, String> descriptionsRefuse = new HashMap<>();
    public final HashMap<String, String> descriptionsSuccess = new HashMap<>();
    public final HashMap<String, String> descriptionsTimeUp = new HashMap<>();
    public final HashMap<String, String> labels = new HashMap<>();
    public final HashMap<String, String> listings = new HashMap<>();

    public int duration = 1;
    public List<String> forbiddenGlobalTag = new ArrayList<>();
    public List<String> forbiddenPlayerTag = new ArrayList<>();
    public int penaltyReputation = 0;
    public HashMap<String, Integer> requiredGood = new HashMap<>();
    public List<String> stepRequiredGlobalTag = new ArrayList<>();
    public List<String> stepRequiredPlayerTag = new ArrayList<>();
    public HashMap<String, Integer> rewardGoods = new HashMap<>();
    public int rewardMoney = 0;
    public int rewardReputation = 0;
    public List<String> bedrockbuildings = new ArrayList<>();

    public List<String> setGlobalTagsFailure = new ArrayList<>();
    public List<String> setGlobalTagsSuccess = new ArrayList<>();
    public List<String> setPlayerTagsFailure = new ArrayList<>();
    public List<String> setPlayerTagsSuccess = new ArrayList<>();
    public List<String[]> setVillagerTagsFailure = new ArrayList<>();
    public List<String[]> setVillagerTagsSuccess = new ArrayList<>();
    public List<String[]> setActionDataSuccess = new ArrayList<>();

    public boolean showRequiredGoods = true;
    @Nullable public String villager;

    public QuestStep(Quest quest, int pos) {
        this.quest = quest;
        this.pos = pos;
    }

    public Quest getQuest() { return quest; }

    public String getStringKey() {
        return quest.key + "_" + pos + "_";
    }

    // ========== Text accessors ==========

    public String getDescription(String lang) {
        return descriptions.getOrDefault(lang, descriptions.getOrDefault("en", ""));
    }

    public String getDescriptionRefuse(String lang) {
        return descriptionsRefuse.getOrDefault(lang, descriptionsRefuse.getOrDefault("en", ""));
    }

    public String getDescriptionSuccess(String lang) {
        return descriptionsSuccess.getOrDefault(lang, descriptionsSuccess.getOrDefault("en", ""));
    }

    public String getDescriptionTimeUp(String lang) {
        return descriptionsTimeUp.getOrDefault(lang, descriptionsTimeUp.getOrDefault("en", ""));
    }

    public String getLabel(String lang) {
        return labels.getOrDefault(lang, labels.getOrDefault("en", "Step " + pos));
    }

    public String getListing(String lang) {
        return listings.getOrDefault(lang, listings.getOrDefault("en", ""));
    }

    // ========== Condition checks ==========

    @Nullable
    public String lackingConditions(org.dizzymii.millenaire2.world.UserProfile profile,
                                     org.dizzymii.millenaire2.world.MillWorldData mw) {
        for (String tag : forbiddenGlobalTag) {
            if (mw.hasGlobalTag(tag)) return "Forbidden global tag present: " + tag;
        }
        for (String tag : forbiddenPlayerTag) {
            if (profile.hasTag(tag)) return "Forbidden player tag present: " + tag;
        }
        for (String tag : stepRequiredGlobalTag) {
            if (!mw.hasGlobalTag(tag)) return "Required global tag missing: " + tag;
        }
        for (String tag : stepRequiredPlayerTag) {
            if (!profile.hasTag(tag)) return "Required player tag missing: " + tag;
        }
        return null; // all conditions met
    }
}
