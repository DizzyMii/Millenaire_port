package org.dizzymii.millenaire2.quest;

import org.dizzymii.millenaire2.util.Point;
import org.dizzymii.millenaire2.village.VillagerRecord;
import org.dizzymii.millenaire2.world.MillWorldData;

import javax.annotation.Nullable;

/**
 * Tracks a villager participating in an active quest instance.
 * Ported from org.millenaire.common.quest.QuestInstanceVillager (Forge 1.12.2).
 */
public class QuestInstanceVillager {

    public long id;
    @Nullable public Point townHall;
    @Nullable public MillWorldData mw;

    @Nullable private VillagerRecord vr = null;

    public QuestInstanceVillager(MillWorldData mw, @Nullable Point p, long vid) {
        this.townHall = p;
        this.id = vid;
        this.mw = mw;
    }

    public QuestInstanceVillager(MillWorldData mw, @Nullable Point p, long vid, VillagerRecord v) {
        this(mw, p, vid);
        this.vr = v;
    }

    @Nullable
    public VillagerRecord getVillagerRecord() {
        if (vr == null && mw != null) {
            vr = mw.getVillagerRecord(id);
        }
        return vr;
    }

    /**
     * Attempts to find the live MillVillager entity in the given level.
     * Scans loaded entities matching the villager ID stored in this instance.
     */
    @Nullable
    public org.dizzymii.millenaire2.entity.MillVillager getVillager(net.minecraft.world.level.Level level) {
        if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            for (net.minecraft.world.entity.Entity entity : serverLevel.getAllEntities()) {
                if (entity instanceof org.dizzymii.millenaire2.entity.MillVillager mv) {
                    if (mv.getVillagerId() == id) {
                        return mv;
                    }
                }
            }
        }
        return null;
    }
}
