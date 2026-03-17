package org.dizzymii.millenaire2.quest;

import org.dizzymii.millenaire2.util.MillLog;
import org.dizzymii.millenaire2.util.Point;
import org.dizzymii.millenaire2.world.MillWorldData;
import org.dizzymii.millenaire2.world.UserProfile;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * An active quest instance for a specific player.
 * Ported from org.millenaire.common.quest.QuestInstance (Forge 1.12.2).
 */
public class QuestInstance {

    private static final int QUEST_LANGUAGE_BONUS = 50;
    private static final String SEP = ";";

    public int currentStep = 0;
    public long currentStepStart;
    @Nullable public Quest quest;
    public long startTime;
    public HashMap<String, QuestInstanceVillager> villagers;
    @Nullable public UserProfile profile;
    @Nullable public MillWorldData mw;
    public long uniqueid;

    public QuestInstance(MillWorldData mw, Quest quest, UserProfile profile,
                         HashMap<String, QuestInstanceVillager> villagers,
                         long startTime) {
        this(mw, quest, profile, villagers, startTime, 0, startTime);
    }

    public QuestInstance(MillWorldData mw, Quest quest, UserProfile profile,
                         HashMap<String, QuestInstanceVillager> villagers,
                         long startTime, int step, long stepStartTime) {
        this.mw = mw;
        this.villagers = villagers;
        this.quest = quest;
        this.currentStep = step;
        this.startTime = startTime;
        this.profile = profile;
        this.currentStepStart = stepStartTime;
        this.uniqueid = (long) (Math.random() * Long.MAX_VALUE);
    }

    @Nullable
    public QuestStep getCurrentStep() {
        if (quest == null || currentStep < 0 || currentStep >= quest.steps.size()) return null;
        return quest.steps.get(currentStep);
    }

    // ========== Step lifecycle ==========

    public boolean completeStep() {
        QuestStep step = getCurrentStep();
        if (step == null || quest == null || profile == null || mw == null) return false;

        // Apply success tags
        for (String tag : step.setGlobalTagsSuccess) mw.addGlobalTag(tag);
        for (String tag : step.clearGlobalTagsSuccess) mw.removeGlobalTag(tag);
        for (String tag : step.setPlayerTagsSuccess) profile.addTag(tag);
        for (String tag : step.clearPlayerTagsSuccess) profile.removeTag(tag);

        // Apply reputation reward
        if (step.rewardReputation != 0) {
            // Find the village position from the first villager's townhall
            Point villagePos = findVillagePos();
            if (villagePos != null) {
                profile.adjustVillageReputation(villagePos, step.rewardReputation);
            }
        }

        // Advance to next step or complete the quest
        currentStep++;
        currentStepStart = System.currentTimeMillis();

        if (currentStep >= quest.steps.size()) {
            MillLog.minor("QuestInstance", "Quest '" + quest.key + "' completed!");
            return true; // quest finished
        }

        MillLog.minor("QuestInstance", "Advanced to step " + currentStep + " of quest '" + quest.key + "'");
        return false; // more steps remain
    }

    public void failStep() {
        QuestStep step = getCurrentStep();
        if (step == null || quest == null || profile == null || mw == null) return;

        // Apply failure tags
        for (String tag : step.setGlobalTagsFailure) mw.addGlobalTag(tag);
        for (String tag : step.clearGlobalTagsFailure) mw.removeGlobalTag(tag);
        for (String tag : step.setPlayerTagsFailure) profile.addTag(tag);
        for (String tag : step.clearPlayerTagsFailure) profile.removeTag(tag);

        // Apply reputation penalty
        if (step.penaltyReputation != 0) {
            Point villagePos = findVillagePos();
            if (villagePos != null) {
                profile.adjustVillageReputation(villagePos, -step.penaltyReputation);
            }
        }

        MillLog.minor("QuestInstance", "Quest '" + quest.key + "' failed at step " + currentStep);
    }

    public boolean isExpired(long currentTime) {
        QuestStep step = getCurrentStep();
        if (step == null) return false;
        long elapsed = (currentTime - currentStepStart) / 1000; // seconds
        return step.duration > 0 && elapsed > step.duration * 3600L; // duration is in hours
    }

    @Nullable
    private Point findVillagePos() {
        if (villagers == null || villagers.isEmpty()) return null;
        for (QuestInstanceVillager qiv : villagers.values()) {
            if (qiv.townHall != null) return qiv.townHall;
        }
        return null;
    }

    // ========== String serialization ==========

    public String saveToString() {
        StringBuilder sb = new StringBuilder();
        sb.append(quest != null ? quest.key : "null").append(SEP);
        sb.append(currentStep).append(SEP);
        sb.append(startTime).append(SEP);
        sb.append(currentStepStart).append(SEP);
        sb.append(uniqueid).append(SEP);

        // Serialize villagers: key|id|thX|thY|thZ
        sb.append(villagers.size());
        for (Map.Entry<String, QuestInstanceVillager> entry : villagers.entrySet()) {
            sb.append(SEP).append(entry.getKey());
            sb.append(SEP).append(entry.getValue().id);
            Point th = entry.getValue().townHall;
            if (th != null) {
                sb.append(SEP).append(th.x).append(SEP).append(th.y).append(SEP).append(th.z);
            } else {
                sb.append(SEP).append("0").append(SEP).append("0").append(SEP).append("0");
            }
        }
        return sb.toString();
    }

    @Nullable
    public static QuestInstance loadFromString(String data, MillWorldData mw, UserProfile profile) {
        try {
            String[] parts = data.split(SEP);
            int idx = 0;
            String questKey = parts[idx++];
            Quest quest = Quest.quests.get(questKey);
            if (quest == null) return null;

            int step = Integer.parseInt(parts[idx++]);
            long start = Long.parseLong(parts[idx++]);
            long stepStart = Long.parseLong(parts[idx++]);
            long uid = Long.parseLong(parts[idx++]);

            int villagerCount = Integer.parseInt(parts[idx++]);
            HashMap<String, QuestInstanceVillager> vils = new HashMap<>();
            for (int i = 0; i < villagerCount; i++) {
                String vKey = parts[idx++];
                long vid = Long.parseLong(parts[idx++]);
                int tx = Integer.parseInt(parts[idx++]);
                int ty = Integer.parseInt(parts[idx++]);
                int tz = Integer.parseInt(parts[idx++]);
                Point th = new Point(tx, ty, tz);
                vils.put(vKey, new QuestInstanceVillager(mw, th, vid));
            }

            QuestInstance qi = new QuestInstance(mw, quest, profile, vils, start, step, stepStart);
            qi.uniqueid = uid;
            return qi;
        } catch (Exception e) {
            MillLog.major("QuestInstance", "Failed to load quest instance from string: " + e.getMessage());
            return null;
        }
    }
}
