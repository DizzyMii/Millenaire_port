/*
 * Decompiled with CFR 0.152.
 */
package org.millenaire.common.quest;

import java.util.ArrayList;
import java.util.List;
import org.millenaire.common.village.VillagerRecord;
import org.millenaire.common.world.UserProfile;

public class QuestVillager {
    List<String> forbiddenTags = new ArrayList<String>();
    String key = null;
    String relatedto = null;
    String relation = null;
    List<String> requiredTags = new ArrayList<String>();
    List<String> types = new ArrayList<String>();

    QuestVillager() {
    }

    public boolean testVillager(UserProfile profile, VillagerRecord vr) {
        String tagPlayer;
        if (profile.villagersInQuests.containsKey(vr.getVillagerId())) {
            return false;
        }
        if (!this.types.isEmpty() && !this.types.contains(vr.type)) {
            return false;
        }
        for (String tag : this.requiredTags) {
            tagPlayer = profile.uuid + "_" + tag;
            if (vr.questTags.contains(tagPlayer)) continue;
            return false;
        }
        for (String tag : this.forbiddenTags) {
            tagPlayer = profile.uuid + "_" + tag;
            if (!vr.questTags.contains(tagPlayer)) continue;
            return false;
        }
        for (String tag : vr.questTags) {
            tagPlayer = profile.uuid + "_" + tag;
            if (!this.forbiddenTags.contains(tagPlayer)) continue;
            return false;
        }
        return true;
    }
}

