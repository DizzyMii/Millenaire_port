package org.dizzymii.millenaire2.quest;

import org.dizzymii.millenaire2.world.MillWorldData;
import org.dizzymii.millenaire2.world.UserProfile;

import javax.annotation.Nullable;
import java.util.HashMap;

/**
 * An active quest instance for a specific player.
 * Ported from org.millenaire.common.quest.QuestInstance (Forge 1.12.2).
 */
public class QuestInstance {

    private static final int QUEST_LANGUAGE_BONUS = 50;

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

    // TODO: completeStep, failStep, loadFromString, saveToString, packet serialisation
}
