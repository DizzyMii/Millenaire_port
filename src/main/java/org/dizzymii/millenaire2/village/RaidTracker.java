package org.dizzymii.millenaire2.village;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import org.dizzymii.millenaire2.util.MillLog;
import org.dizzymii.millenaire2.util.Point;
import org.dizzymii.millenaire2.world.MillWorldData;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Tracks all active raids in a world and manages their lifecycle.
 * Owned by MillWorldData — ticked every slow tick from the townhall.
 *
 * A raid has:
 * - An attacker village (townhall pos)
 * - A target village (townhall pos)
 * - A duration countdown
 * - A list of raider villager IDs
 * - An outcome (pending, success, failure)
 */
public class RaidTracker {

    private final List<ActiveRaid> activeRaids = new ArrayList<>();

    /**
     * Tick all active raids. Called from townhall slow tick.
     */
    public void tick(MillWorldData mw) {
        Iterator<ActiveRaid> it = activeRaids.iterator();
        while (it.hasNext()) {
            ActiveRaid raid = it.next();
            raid.ticksRemaining--;

            if (raid.ticksRemaining <= 0) {
                resolveRaid(raid, mw);
                it.remove();
            }
        }
    }

    /**
     * Register a new active raid.
     */
    public void startRaid(Point attackerPos, Point targetPos, List<Long> raiderIds, int durationTicks) {
        ActiveRaid raid = new ActiveRaid();
        raid.attackerVillagePos = attackerPos;
        raid.targetVillagePos = targetPos;
        raid.raiderIds.addAll(raiderIds);
        raid.ticksRemaining = durationTicks;
        activeRaids.add(raid);
        MillLog.minor("RaidTracker", "Raid started: " + attackerPos + " -> " + targetPos
                + " (" + raiderIds.size() + " raiders, " + durationTicks + " ticks)");
    }

    /**
     * Check if a village is currently being raided.
     */
    public boolean isUnderRaid(Point villagePos) {
        for (ActiveRaid raid : activeRaids) {
            if (raid.targetVillagePos.equals(villagePos)) return true;
        }
        return false;
    }

    /**
     * Check if a village is currently raiding another.
     */
    public boolean isRaiding(Point villagePos) {
        for (ActiveRaid raid : activeRaids) {
            if (raid.attackerVillagePos.equals(villagePos)) return true;
        }
        return false;
    }

    /**
     * Get the number of active raids.
     */
    public int getActiveRaidCount() {
        return activeRaids.size();
    }

    /**
     * Resolve a completed raid: determine outcome, clear flags, apply loot.
     */
    private void resolveRaid(ActiveRaid raid, MillWorldData mw) {
        Building attackerTh = findTownhall(mw, raid.attackerVillagePos);
        Building targetTh = findTownhall(mw, raid.targetVillagePos);

        // Count surviving raiders
        int survivingRaiders = 0;
        if (attackerTh != null) {
            for (long id : raid.raiderIds) {
                VillagerRecord vr = attackerTh.getVillagerRecord(id);
                if (vr != null && !vr.killed) {
                    survivingRaiders++;
                    vr.awayraiding = false;
                }
            }
        }

        // Count surviving defenders
        int survivingDefenders = 0;
        if (targetTh != null) {
            for (VillagerRecord vr : targetTh.getVillagerRecords()) {
                if (!vr.killed) survivingDefenders++;
            }
        }

        // Determine outcome: attackers win if they have more survivors
        boolean attackerWins = survivingRaiders > survivingDefenders / 2;

        // Apply results
        if (attackerWins && DiplomacyManager.lootOnSuccess && targetTh != null && attackerTh != null) {
            transferLoot(targetTh, attackerTh);
            MillLog.minor("RaidTracker", "Raid SUCCESS: " + raid.attackerVillagePos
                    + " looted " + raid.targetVillagePos);
        } else {
            MillLog.minor("RaidTracker", "Raid FAILED: " + raid.attackerVillagePos
                    + " repelled by " + raid.targetVillagePos);
        }

        // Clear target's under-attack flag
        if (targetTh != null) {
            targetTh.underAttack = false;
        }

        // Clear attacker's raid target
        if (attackerTh != null) {
            attackerTh.raidTarget = null;
        }

        mw.setDirty();
    }

    /**
     * Transfer some resources from the loser to the winner.
     */
    private void transferLoot(Building loser, Building winner) {
        if (loser.resManager == null || winner.resManager == null) return;

        // Transfer up to 25% of each stored good
        for (var entry : new ArrayList<>(loser.resManager.resources.entrySet())) {
            int amount = entry.getValue();
            int lootAmount = Math.max(1, amount / 4);
            if (lootAmount > 0) {
                loser.resManager.takeGoods(entry.getKey(), lootAmount);
                winner.resManager.storeGoods(entry.getKey(), lootAmount);
            }
        }
    }

    @Nullable
    private Building findTownhall(MillWorldData mw, Point villagePos) {
        for (Building b : mw.getBuildingsMap().values()) {
            if (b.isTownhall && b.getPos() != null && b.getPos().equals(villagePos)) {
                return b;
            }
        }
        return null;
    }

    // ========== NBT Persistence ==========

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        ListTag raidList = new ListTag();
        for (ActiveRaid raid : activeRaids) {
            raidList.add(raid.save());
        }
        tag.put("raids", raidList);
        return tag;
    }

    public void load(CompoundTag tag) {
        activeRaids.clear();
        if (tag.contains("raids", Tag.TAG_LIST)) {
            ListTag raidList = tag.getList("raids", Tag.TAG_COMPOUND);
            for (int i = 0; i < raidList.size(); i++) {
                ActiveRaid raid = new ActiveRaid();
                raid.load(raidList.getCompound(i));
                activeRaids.add(raid);
            }
        }
    }

    // ========== Inner class ==========

    private static class ActiveRaid {
        Point attackerVillagePos = new Point(0, 0, 0);
        Point targetVillagePos = new Point(0, 0, 0);
        final List<Long> raiderIds = new ArrayList<>();
        int ticksRemaining;

        CompoundTag save() {
            CompoundTag tag = new CompoundTag();
            attackerVillagePos.writeToNBT(tag, "attacker");
            targetVillagePos.writeToNBT(tag, "target");
            tag.putInt("ticksRemaining", ticksRemaining);
            long[] ids = raiderIds.stream().mapToLong(Long::longValue).toArray();
            tag.putLongArray("raiderIds", ids);
            return tag;
        }

        void load(CompoundTag tag) {
            Point a = Point.readFromNBT(tag, "attacker");
            if (a != null) attackerVillagePos = a;
            Point t = Point.readFromNBT(tag, "target");
            if (t != null) targetVillagePos = t;
            ticksRemaining = tag.getInt("ticksRemaining");
            raiderIds.clear();
            for (long id : tag.getLongArray("raiderIds")) {
                raiderIds.add(id);
            }
        }
    }
}
