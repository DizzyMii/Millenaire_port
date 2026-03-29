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

    /**
     * Creates a new, empty {@code QuestVillager} role descriptor.
     * All lists start empty; fields must be populated by the quest loader.
     */
    public QuestVillager() {}

    /**
     * Tests whether a villager record satisfies this quest role's requirements.
     * <p>A record passes when:
     * <ul>
     *   <li>Its {@code type} matches one of the entries in {@link #types} (or the types
     *       list is empty, meaning any type is accepted).</li>
     *   <li>It carries every tag in {@link #requiredTags}.</li>
     *   <li>It carries none of the tags in {@link #forbiddenTags}.</li>
     *   <li>Its {@link org.dizzymii.millenaire2.village.VillagerRecord#killed} flag is
     *       {@code false}.</li>
     * </ul>
     *
     * @param profile the player's user profile (reserved for future relationship checks)
     * @param vr      the villager record to test
     * @return {@code true} if the record satisfies all constraints
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
