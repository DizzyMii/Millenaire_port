package org.dizzymii.millenaire2.quest;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Quest definition — loaded from data files.
 * Ported from org.millenaire.common.quest.Quest (Forge 1.12.2).
 */
public class Quest {

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

    // TODO: loadQuest(File), loadQVillager, canStart, full file parsing
}
