package org.dizzymii.millenaire2.quest;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Defines a villager role within a quest definition.
 * Ported from org.millenaire.common.quest.QuestVillager (Forge 1.12.2).
 */
public class QuestVillager {

    public List<String> forbiddenTags = new ArrayList<>();
    @Nullable public String key;
    @Nullable public String relatedto;
    @Nullable public String relation;
    public List<String> requiredTags = new ArrayList<>();
    public List<String> types = new ArrayList<>();

    public QuestVillager() {}

    // TODO: testVillager(UserProfile, VillagerRecord) — depends on full VillagerRecord integration
}
