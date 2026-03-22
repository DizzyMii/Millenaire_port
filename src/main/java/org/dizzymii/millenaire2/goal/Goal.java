package org.dizzymii.millenaire2.goal;

import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.item.InvItem;
import org.dizzymii.millenaire2.pathing.atomicstryker.AStarConfig;
import org.dizzymii.millenaire2.util.Point;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

/**
 * Abstract base class for all villager goals/AI tasks.
 * Ported from org.millenaire.common.goal.Goal (Forge 1.12.2).
 */
public abstract class Goal {

    public static final int STANDARD_DELAY = 2000;
    private static Map<String, Goal> goals;

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

    // --- Registry accessors ---

    @Nullable
    public static Goal get(String key) {
        return goals != null ? goals.get(key) : null;
    }

    public static boolean isInitialized() {
        return goals != null && !goals.isEmpty();
    }

    public static int registeredCount() {
        return goals != null ? goals.size() : 0;
    }

    public static void initGoals() {
        HashMap<String, Goal> mutable = new HashMap<>();

        // Core goals
        register(mutable, "sleep", new GoalSleep());
        register(mutable, "hide", new GoalHide());
        register(mutable, "defendvillage", new GoalDefendVillage());
        register(mutable, "beseller", new GoalBeSeller());
        register(mutable, "construction", new GoalConstructionStepByStep());
        register(mutable, "delivergoodshousehold", new GoalDeliverGoodsHousehold());
        register(mutable, "getresourcesforbuild", new GoalGetResourcesForBuild());
        register(mutable, "gettool", new GoalGetTool());
        register(mutable, "gosocialise", new org.dizzymii.millenaire2.goal.leisure.GoalGoSocialise());

        // Agriculture & gathering
        register(mutable, "gathergoods", new GoalGatherGoods());
        register(mutable, "bringbackresourceshome", new GoalBringBackResourcesHome());
        register(mutable, "getgoodsforhousehold", new GoalGetGoodsForHousehold());
        register(mutable, "deliverresourcesshop", new GoalDeliverResourcesShop());
        register(mutable, "getresourcesforshops", new GoalGetResourcesForShops());
        register(mutable, "lumbermanchoptrees", new GoalLumbermanChopTrees());
        register(mutable, "lumbermanplantsaplings", new GoalLumbermanPlantSaplings());
        register(mutable, "fish", new GoalFish());
        register(mutable, "breedanimals", new GoalBreedAnimals());

        // Culture-specific
        register(mutable, "indiandrybrick", new GoalIndianDryBrick());
        register(mutable, "indiangatherbrick", new GoalIndianGatherBrick());
        register(mutable, "indianharvestsugarcane", new GoalIndianHarvestSugarCane());
        register(mutable, "indianplantsugarcane", new GoalIndianPlantSugarCane());
        register(mutable, "harvestcacao", new GoalHarvestCacao());
        register(mutable, "harvestwarts", new GoalHarvestWarts());
        register(mutable, "byzantinegathersilk", new GoalByzantineGatherSilk());
        register(mutable, "byzantinegathersnails", new GoalByzantineGatherSnails());
        register(mutable, "fishinuit", new GoalFishInuit());

        // Combat & military
        register(mutable, "huntmonster", new GoalHuntMonster());
        register(mutable, "raidvillage", new GoalRaidVillage());

        // Building & path
        register(mutable, "buildpath", new GoalBuildPath());
        register(mutable, "clearoldpath", new GoalClearOldPath());

        // Trade & merchant
        register(mutable, "foreignmerchantkeepstall", new GoalForeignMerchantKeepStall());
        register(mutable, "merchantvisitbuilding", new GoalMerchantVisitBuilding());
        register(mutable, "merchantvisitinn", new GoalMerchantVisitInn());

        // Brewing & crafting
        register(mutable, "brewpotions", new GoalBrewPotions());
        register(mutable, "bepujaperformer", new GoalBePujaPerformer());

        // Child
        register(mutable, "childbecomeadult", new GoalChildBecomeAdult());

        // Leisure
        register(mutable, "gochat", new org.dizzymii.millenaire2.goal.leisure.GoalGoChat());
        register(mutable, "gorest", new org.dizzymii.millenaire2.goal.leisure.GoalGoRest());

        org.dizzymii.millenaire2.goal.generic.GoalGeneric.loadGenericGoals(mutable);

        goals = Collections.unmodifiableMap(mutable);
    }

    private static void register(HashMap<String, Goal> map, String key, Goal goal) {
        goal.key = key;
        map.put(key, goal);
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
        return villager.getPathDestPoint();
    }

    public int range(MillVillager villager) { return ACTIVATION_RANGE; }

    public String labelKey(MillVillager villager) { return key; }
    public String labelKeyWhileTravelling(MillVillager villager) { return key; }

    public boolean isFightingGoal() { return false; }
    public boolean isInterruptedByRaid() { return true; }
    public boolean triggerNextGoalOnFinish(MillVillager villager) { return false; }
    public boolean isStillValid(MillVillager villager) throws Exception { return true; }

    @Override
    public String toString() { return key != null ? key : super.toString(); }

    /** Called from GoalGeneric during init to add additional goals to the mutable build map. */
    protected static void registerGoal(HashMap<String, Goal> map, String key, Goal goal) {
        goal.key = key;
        map.put(key, goal);
    }
}
