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

    /**
     * Tests whether a villager record satisfies this quest role's requirements.
     */
    public boolean testVillager(org.dizzymii.millenaire2.world.UserProfile profile,
                                org.dizzymii.millenaire2.village.VillagerRecord vr) {
        // Check type match
        if (!types.isEmpty()) {
            boolean typeMatch = false;
            String vrType = vr.type;
            if (vrType != null) {
                for (String t : types) {
                    if (vrType.equalsIgnoreCase(t)) {
                        typeMatch = true;
                        break;
                    }
                }
            }
            if (!typeMatch) return false;
        }

        // Check required tags
        for (String tag : requiredTags) {
            if (!vr.hasQuestTag(tag)) return false;
        }

        // Check forbidden tags
        for (String tag : forbiddenTags) {
            if (vr.hasQuestTag(tag)) return false;
        }

        // Check killed
        if (vr.killed) return false;

        return true;
    }
}
