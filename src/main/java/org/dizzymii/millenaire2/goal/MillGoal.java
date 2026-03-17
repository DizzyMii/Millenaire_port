package org.dizzymii.millenaire2.goal;

import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.item.InvItem;
import org.dizzymii.millenaire2.util.LanguageUtilities;
import org.dizzymii.millenaire2.util.Point;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Abstract base for Millénaire's custom goal/AI system.
 * Named MillGoal to avoid clash with net.minecraft.world.entity.ai.goal.Goal.
 *
 * Ported from org.millenaire.common.goal.Goal (Forge 1.12.2).
 * Goals are loaded from config files and drive villager behavior.
 */
public abstract class MillGoal {

    public static final int STANDARD_DELAY = 2000;

    public static HashMap<String, MillGoal> goals;
    public static MillGoal beSeller;
    public static MillGoal construction;
    public static MillGoal deliverGoodsHousehold;
    public static MillGoal getResourcesForBuild;
    public static MillGoal raidVillage;
    public static MillGoal defendVillage;
    public static MillGoal hide;
    public static MillGoal sleep;
    public static MillGoal gettool;
    public static MillGoal gosocialise;

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
    public InvItem[] balanceOutput = null;
    public int minimumHour = -1;
    public int maximumHour = -1;
    public boolean travelBookShow = true;
    protected InvItem icon = null;
    protected InvItem floatingIcon = null;

    /**
     * Initialise the hardcoded goal registry.
     * TODO: Populate with actual goal subclass instances in a later phase.
     */
    public static void initGoals() {
        goals = new HashMap<>();
        // Goal subclasses will be registered here when ported
    }

    // --- Abstract / overridable methods ---

    public int actionDuration(MillVillager villager) throws Exception {
        return 10;
    }

    public boolean allowRandomMoves() throws Exception {
        return false;
    }

    public boolean autoInterruptIfNoTarget() {
        return true;
    }

    public boolean canBeDoneAtNight() {
        return false;
    }

    public boolean canBeDoneInDayTime() {
        return true;
    }

    public String gameName() {
        return LanguageUtilities.string("goal." + this.labelKey(null));
    }

    public String gameName(MillVillager villager) {
        return LanguageUtilities.string("goal." + this.labelKey(villager));
    }

    public Point getCurrentGoalTarget(MillVillager villager) {
        return null; // TODO: implement
    }

    public abstract String labelKey(MillVillager villager);

    public String labelKeyWhileTravelling(MillVillager villager) {
        return labelKey(villager);
    }

    public int range(MillVillager villager) {
        return ACTIVATION_RANGE;
    }

    public boolean isStillValid(MillVillager villager) throws Exception {
        return true;
    }

    public boolean lookAtGoal() {
        return true;
    }

    public abstract boolean performAction(MillVillager villager) throws Exception;

    public abstract boolean triggerAction(MillVillager villager) throws Exception;

    /**
     * Information about a goal currently being performed by a villager.
     */
    public static class GoalInformation {
        public Point destPoint;
        public int destEntityId = -1;
        public String extraInfo;

        public GoalInformation() {}

        public GoalInformation(Point dest) {
            this.destPoint = dest;
        }
    }
}
