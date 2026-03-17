package org.dizzymii.millenaire2.goal;

import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.item.InvItem;
import org.dizzymii.millenaire2.pathing.atomicstryker.AStarConfig;
import org.dizzymii.millenaire2.util.Point;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Abstract base class for all villager goals/AI tasks.
 * Ported from org.millenaire.common.goal.Goal (Forge 1.12.2).
 */
public abstract class Goal {

    public static final int STANDARD_DELAY = 2000;
    public static HashMap<String, Goal> goals;

    // --- Well-known goal instances ---
    public static Goal beSeller;
    public static Goal construction;
    public static Goal deliverGoodsHousehold;
    public static Goal getResourcesForBuild;
    public static Goal raidVillage;
    public static Goal defendVillage;
    public static Goal hide;
    public static Goal sleep;
    public static Goal gettool;
    public static Goal gosocialise;

    // --- Pathfinding configs ---
    public static final AStarConfig JPS_CONFIG_TIGHT = new AStarConfig(true, false, false, false, true);
    public static final AStarConfig JPS_CONFIG_WIDE = new AStarConfig(true, false, true, false, true);
    public static final AStarConfig JPS_CONFIG_BUILDING = new AStarConfig(true, false, false, false, true);
    public static final AStarConfig JPS_CONFIG_BUILDING_SCAFFOLDINGS = new AStarConfig(true, false, true, false, true);
    public static final AStarConfig JPS_CONFIG_CHOPLUMBER = new AStarConfig(true, false, false, false, true);
    public static final AStarConfig JPS_CONFIG_SLAUGHTERSQUIDS = new AStarConfig(true, false, false, true, true);
    public static final AStarConfig JPS_CONFIG_TIGHT_NO_LEAVES = new AStarConfig(true, false, false, false, false);
    public static final AStarConfig JPS_CONFIG_WIDE_NO_LEAVES = new AStarConfig(true, false, true, false, false);
    public static final AStarConfig JPS_CONFIG_BUILDING_NO_LEAVES = new AStarConfig(true, false, false, false, false);
    public static final AStarConfig JPS_CONFIG_CHOPLUMBER_NO_LEAVES = new AStarConfig(true, false, false, false, false);
    public static final AStarConfig JPS_CONFIG_SLAUGHTERSQUIDS_NO_LEAVES = new AStarConfig(true, false, false, true, false);

    // --- Tags ---
    public static final String TAG_CONSTRUCTION = "tag_construction";
    public static final String TAG_AGRICULTURE = "tag_agriculture";
    protected static final int ACTIVATION_RANGE = 3;

    // --- Instance fields ---
    public String key;
    public boolean leasure = false;
    public boolean sprint = true;
    public List<String> tags = new ArrayList<>();
    public HashMap<InvItem, Integer> buildingLimit = new HashMap<>();
    public HashMap<InvItem, Integer> townhallLimit = new HashMap<>();
    public HashMap<InvItem, Integer> villageLimit = new HashMap<>();
    public int maxSimultaneousInBuilding = 0;
    public int maxSimultaneousTotal = 0;
    public int minimumHour = -1;
    public int maximumHour = -1;
    public boolean travelBookShow = true;

    public static void initGoals() {
        goals = new HashMap<>();
        // TODO: Instantiate all goal subclasses and register them in the map
        //       Also call GoalGeneric.loadGenericGoals() and assign keys/tags
    }

    // --- Abstract methods ---
    public abstract GoalInformation getDestination(MillVillager villager) throws Exception;
    public abstract boolean performAction(MillVillager villager) throws Exception;

    // --- Default methods ---
    public int actionDuration(MillVillager villager) throws Exception { return 10; }
    public boolean allowRandomMoves() throws Exception { return false; }
    public boolean autoInterruptIfNoTarget() { return true; }
    public boolean canBeDoneAtNight() { return false; }
    public boolean canBeDoneInDayTime() { return true; }

    public String gameName() {
        return "goal." + (key != null ? key : "unknown");
    }

    public Point getCurrentGoalTarget(MillVillager villager) {
        // TODO: return villager.getGoalDestPoint() once MillVillager exposes it
        return null;
    }

    public int range(MillVillager villager) { return ACTIVATION_RANGE; }

    public String labelKey(MillVillager villager) { return key; }
    public String labelKeyWhileTravelling(MillVillager villager) { return key; }

    public boolean isFightingGoal() { return false; }
    public boolean isInterruptedByRaid() { return true; }
    public boolean triggerNextGoalOnFinish(MillVillager villager) { return false; }

    @Override
    public String toString() { return key != null ? key : super.toString(); }
}
