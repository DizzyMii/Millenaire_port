package org.dizzymii.millenaire2.entity.ai;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.goal.Goal;
import org.dizzymii.millenaire2.goal.GoalInformation;
import org.dizzymii.millenaire2.util.MillLog;
import org.dizzymii.millenaire2.util.Point;
import org.dizzymii.millenaire2.village.VillagerRecord;
import org.dizzymii.millenaire2.world.MillWorldData;

/**
 * Handles combat-related logic for Millénaire villagers.
 * Extracted from MillVillager to separate combat concerns from entity definition.
 */
public final class VillagerCombatHandler {

    private VillagerCombatHandler() {}

    /**
     * Handles damage taken by a villager. Called from {@link MillVillager#hurt}.
     * Triggers a defend-village goal when attacked.
     *
     * @return whether the damage was applied (mirrors super.hurt result)
     */
    public static void onHurt(MillVillager villager, DamageSource source) {
        if (source.getEntity() instanceof Player) {
            villager.setLastAttackByPlayer(true);
        }

        Goal defend = Goal.get("defendvillage");
        if (villager.getVillagerType() != null && villager.getVillagerType().helpInAttacks && defend != null) {
            try {
                GoalInformation info = defend.getDestination(villager);
                if (info != null && info.hasTarget()) {
                    villager.getAIController().setActiveGoal("defendvillage", defend, info);
                }
            } catch (Exception ignored) {}
        }
    }

    /**
     * Handles villager death. Called from {@link MillVillager#die}.
     * Updates the persisted VillagerRecord to mark it as killed.
     */
    public static void onDeath(MillVillager villager, DamageSource source) {
        MillLog.major(villager, "Villager died: " + villager.getFirstName() + " " + villager.getFamilyName()
                + " at " + new Point(villager.blockPosition()));

        if (villager.level() instanceof ServerLevel sl) {
            MillWorldData mw = MillWorldData.get(sl);
            VillagerRecord vr = mw.getVillagerRecord(villager.getVillagerId());
            if (vr != null) {
                vr.killed = true;
            }
        }
    }
}
