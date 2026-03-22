package org.dizzymii.millenaire2.quest;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;
import org.dizzymii.millenaire2.util.Point;
import org.dizzymii.millenaire2.world.MillWorldData;
import org.dizzymii.millenaire2.world.UserProfile;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Quest definition — loaded from data files.
 * Ported from org.millenaire.common.quest.Quest (Forge 1.12.2).
 */
public class Quest {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static HashMap<String, Quest> quests = new HashMap<>();

    // ========== World quest keys ==========
    public static final String INDIAN_WQ = "sadhu";
    public static final String NORMAN_WQ = "alchemist";
    public static final String MAYAN_WQ = "fallenking";
    public static final Map<String, Integer> WORLD_MISSION_NB = new HashMap<>();
    public static final String[] WORLD_MISSION_KEYS = { INDIAN_WQ, NORMAN_WQ, MAYAN_WQ };

    static {
        WORLD_MISSION_NB.put(INDIAN_WQ, 15);
        WORLD_MISSION_NB.put(NORMAN_WQ, 13);
        WORLD_MISSION_NB.put(MAYAN_WQ, 10);
    }

    // ========== Instance fields ==========
    @Nullable public String key;
    public double chanceperhour = 0.0;
    public int maxsimultaneous = 5;
    public int minreputation = 0;

    public List<QuestStep> steps = new ArrayList<>();
    public List<String> globalTagsForbidden = new ArrayList<>();
    public List<String> globalTagsRequired = new ArrayList<>();
    public List<String> profileTagsForbidden = new ArrayList<>();
    public List<String> profileTagsRequired = new ArrayList<>();
    public HashMap<String, QuestVillager> villagers = new HashMap<>();
    public List<QuestVillager> villagersOrdered = new ArrayList<>();

    public Quest() {}

    // ========== Loading ==========

    @Nullable
    public static Quest loadQuest(File file) {
        if (!file.exists() || !file.getName().endsWith(".txt")) return null;

        Quest quest = new Quest();
        quest.key = file.getName().replace(".txt", "");

        QuestStep currentStep = null;
        QuestVillager currentVillager = null;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("//")) continue;

                String[] parts = line.split(":", 2);
                if (parts.length < 2) continue;
                String tag = parts[0].trim().toLowerCase();
                String value = parts[1].trim();

                // Step markers
                if (tag.equals("step")) {
                    int stepPos = quest.steps.size();
                    currentStep = new QuestStep(quest, stepPos);
                    quest.steps.add(currentStep);
                    currentVillager = null;
                    continue;
                }

                // Villager markers
                if (tag.equals("villager")) {
                    currentVillager = new QuestVillager();
                    currentVillager.key = value;
                    quest.villagers.put(value, currentVillager);
                    quest.villagersOrdered.add(currentVillager);
                    currentStep = null;
                    continue;
                }

                // Quest-level properties
                if (currentStep == null && currentVillager == null) {
                    parseQuestProperty(quest, tag, value);
                } else if (currentStep != null) {
                    parseStepProperty(currentStep, tag, value);
                } else {
                    parseVillagerProperty(currentVillager, tag, value);
                }
            }
        } catch (Exception e) {
            LOGGER.info("Failed to load quest file: " + file.getName() + " — " + e.getMessage());
            return null;
        }

        quests.put(quest.key, quest);
        LOGGER.debug("Loaded quest '" + quest.key + "' with " + quest.steps.size() + " steps.");
        return quest;
    }

    private static void parseQuestProperty(Quest quest, String tag, String value) {
        switch (tag) {
            case "chanceperhour" -> quest.chanceperhour = Double.parseDouble(value);
            case "maxsimultaneous" -> quest.maxsimultaneous = Integer.parseInt(value);
            case "minreputation" -> quest.minreputation = Integer.parseInt(value);
            case "globaltagrequired" -> quest.globalTagsRequired.add(value);
            case "globaltagforbidden" -> quest.globalTagsForbidden.add(value);
            case "profiletagrequired" -> quest.profileTagsRequired.add(value);
            case "profiletagforbidden" -> quest.profileTagsForbidden.add(value);
        }
    }

    private static void parseStepProperty(QuestStep step, String tag, String value) {
        switch (tag) {
            case "duration" -> step.duration = Integer.parseInt(value);
            case "rewardmoney" -> step.rewardMoney = Integer.parseInt(value);
            case "rewardreputation" -> step.rewardReputation = Integer.parseInt(value);
            case "penaltyreputation" -> step.penaltyReputation = Integer.parseInt(value);
            case "villager" -> step.villager = value;
            case "showrequiredgoods" -> step.showRequiredGoods = Boolean.parseBoolean(value);
            case "requiredgood" -> {
                String[] rParts = value.split(",", 2);
                if (rParts.length == 2) step.requiredGood.put(rParts[0].trim(), Integer.parseInt(rParts[1].trim()));
            }
            case "rewardgood" -> {
                String[] rwParts = value.split(",", 2);
                if (rwParts.length == 2) step.rewardGoods.put(rwParts[0].trim(), Integer.parseInt(rwParts[1].trim()));
            }
            case "setglobaltagsuccess" -> step.setGlobalTagsSuccess.add(value);
            case "setglobaltagfailure" -> step.setGlobalTagsFailure.add(value);
            case "setplayertagsuccess" -> step.setPlayerTagsSuccess.add(value);
            case "setplayertagfailure" -> step.setPlayerTagsFailure.add(value);
            case "clearglobaltagsuccess" -> step.clearGlobalTagsSuccess.add(value);
            case "clearglobaltagfailure" -> step.clearGlobalTagsFailure.add(value);
            case "clearplayertagsuccess" -> step.clearPlayerTagsSuccess.add(value);
            case "clearplayertagfailure" -> step.clearPlayerTagsFailure.add(value);
            case "forbiddenglobaltag" -> step.forbiddenGlobalTag.add(value);
            case "forbiddenplayertag" -> step.forbiddenPlayerTag.add(value);
            case "requiredglobaltag" -> step.stepRequiredGlobalTag.add(value);
            case "requiredplayertag" -> step.stepRequiredPlayerTag.add(value);
            case "bedrockbuilding" -> step.bedrockbuildings.add(value);
            default -> {
                // Language-specific descriptions: description_en, label_en, etc.
                if (tag.startsWith("description_")) step.descriptions.put(tag.substring(12), value);
                else if (tag.startsWith("descriptionrefuse_")) step.descriptionsRefuse.put(tag.substring(18), value);
                else if (tag.startsWith("descriptionsuccess_")) step.descriptionsSuccess.put(tag.substring(19), value);
                else if (tag.startsWith("descriptiontimeup_")) step.descriptionsTimeUp.put(tag.substring(18), value);
                else if (tag.startsWith("label_")) step.labels.put(tag.substring(6), value);
                else if (tag.startsWith("listing_")) step.listings.put(tag.substring(8), value);
            }
        }
    }

    private static void parseVillagerProperty(QuestVillager villager, String tag, String value) {
        switch (tag) {
            case "type" -> villager.types.add(value);
            case "requiredtag" -> villager.requiredTags.add(value);
            case "forbiddentag" -> villager.forbiddenTags.add(value);
            case "relatedto" -> villager.relatedto = value;
            case "relation" -> villager.relation = value;
        }
    }

    // ========== Runtime checks ==========

    public boolean canStart(UserProfile profile, MillWorldData mw, @Nullable Point villagePos) {
        // Check minimum reputation
        if (villagePos != null && profile.getVillageReputation(villagePos) < minreputation) {
            return false;
        }

        // Check required global tags
        for (String tag : globalTagsRequired) {
            if (!mw.hasGlobalTag(tag)) return false;
        }

        // Check forbidden global tags
        for (String tag : globalTagsForbidden) {
            if (mw.hasGlobalTag(tag)) return false;
        }

        // Check required profile tags
        for (String tag : profileTagsRequired) {
            if (!profile.hasTag(tag)) return false;
        }

        // Check forbidden profile tags
        for (String tag : profileTagsForbidden) {
            if (profile.hasTag(tag)) return false;
        }

        return true;
    }

    public static void loadAllQuests(File questDir) {
        if (!questDir.isDirectory()) return;
        File[] files = questDir.listFiles((dir, name) -> name.endsWith(".txt"));
        if (files == null) return;
        quests.clear();
        for (File f : files) {
            loadQuest(f);
        }
        LOGGER.debug("Loaded " + quests.size() + " quests from " + questDir.getAbsolutePath());
    }
}
