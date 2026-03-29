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

    /**
     * Creates a villager-tracking entry for an active quest instance.
     *
     * @param mw       the world data context used to look up the villager record on demand
     * @param p        the town-hall position of the associated village, or {@code null}
     * @param vid      the unique villager ID to track
     */
    public QuestInstanceVillager(MillWorldData mw, @Nullable Point p, long vid) {
        this.townHall = p;
        this.id = vid;
        this.mw = mw;
    }

    /**
     * Creates a villager-tracking entry for an active quest instance with a
     * pre-resolved villager record.
     *
     * @param mw       the world data context
     * @param p        the town-hall position, or {@code null}
     * @param vid      the unique villager ID to track
     * @param v        the already-resolved villager record
     */
    public QuestInstanceVillager(MillWorldData mw, @Nullable Point p, long vid, VillagerRecord v) {
        this(mw, p, vid);
        this.vr = v;
    }

    /**
     * Lazily resolves and returns the {@link VillagerRecord} for this quest participant.
     * On the first call the record is looked up from {@link MillWorldData} and cached.
     * Returns {@code null} if the record cannot be found.
     *
     * @return the villager record, or {@code null}
     */
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
