package org.dizzymii.millenaire2.goal.generic;

import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.entity.VillagerActionRuntime;
import org.dizzymii.millenaire2.goal.Goal;
import org.dizzymii.millenaire2.goal.GoalInformation;
import org.dizzymii.millenaire2.item.InvItem;
import org.dizzymii.millenaire2.util.MillCommonUtilities;
import org.dizzymii.millenaire2.util.MillLog;
import org.dizzymii.millenaire2.village.Building;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GoalGeneric extends Goal {

    public static void loadGenericGoals() {
        File goalsRoot = new File(MillCommonUtilities.getMillenaireContentDir(), "goals");
        if (!goalsRoot.isDirectory()) {
            MillLog.minor(GoalGeneric.class, "Generic goal loader initialised");
            return;
        }

        int registered = 0;
        File[] families = goalsRoot.listFiles();
        if (families != null) {
            for (File familyDir : families) {
                if (!familyDir.isDirectory()) {
                    continue;
                }
                GenericGoalDefinition.Family family = GenericGoalDefinition.Family.fromDirectoryName(familyDir.getName());
                List<File> goalFiles = new ArrayList<>();
                collectGoalFiles(familyDir, goalFiles);
                for (File goalFile : goalFiles) {
                    String goalKey = goalKeyFromFile(goalFile);
                    if (goalKey.isEmpty() || Goal.getGoal(goalKey) != null) {
                        continue;
                    }
                    try {
                        GenericGoalDefinition definition = GenericGoalDefinition.parse(goalFile, family, goalKey);
                        Goal goal = new ConfiguredGoal(definition);
                        applyDefinitionMetadata(goal, definition);
                        registerGoal(goalKey, goal);
                        registered++;
                    } catch (IOException e) {
                        MillLog.error(GoalGeneric.class, "Error loading generic goal: " + goalFile.getPath(), e);
                    }
                }
            }
        }
        MillLog.minor(GoalGeneric.class, "Generic goal loader initialised: " + registered + " data goals");
    }

    private static void applyDefinitionMetadata(Goal goal, GenericGoalDefinition definition) {
        goal.leasure = definition.leisure;
        goal.sprint = definition.sprint;
        goal.minimumHour = definition.minimumHour;
        goal.maximumHour = definition.maximumHour;
        goal.maxSimultaneousInBuilding = definition.maxSimultaneousInBuilding;
        goal.maxSimultaneousTotal = definition.maxSimultaneousTotal;
        goal.tags.addAll(definition.goalTags);
        addLimits(goal.buildingLimit, definition.buildingLimits);
        addLimits(goal.townhallLimit, definition.townHallLimits);
        addLimits(goal.villageLimit, definition.villageLimits);
    }

    private static void addLimits(Map<InvItem, Integer> target, List<GenericGoalDefinition.ItemAmount> limits) {
        for (GenericGoalDefinition.ItemAmount limit : limits) {
            InvItem item = limit.resolve();
            if (item != null) {
                target.put(item, limit.count);
            }
        }
    }

    private static void collectGoalFiles(File directory, List<File> files) {
        File[] children = directory.listFiles();
        if (children == null) {
            return;
        }
        for (File child : children) {
            if (child.isDirectory()) {
                collectGoalFiles(child, files);
            } else if (child.isFile() && child.getName().toLowerCase().endsWith(".txt")) {
                files.add(child);
            }
        }
    }

    private static String goalKeyFromFile(File file) {
        String name = file.getName();
        int dotIndex = name.lastIndexOf('.');
        if (dotIndex >= 0) {
            name = name.substring(0, dotIndex);
        }
        return Goal.normalizeKey(name);
    }

    @Override
    public GoalInformation getDestination(MillVillager v) {
        return null;
    }

    @Override
    public boolean performAction(MillVillager v) {
        return false;
    }

    private static final class ConfiguredGoal extends GoalGeneric {
        private final GenericGoalDefinition definition;

        private ConfiguredGoal(GenericGoalDefinition definition) {
            this.definition = definition;
        }

        @Override
        public GoalInformation getDestination(MillVillager villager) {
            Building targetBuilding = GenericGoalSupport.resolveTargetBuilding(villager, definition);
            return switch (definition.family) {
                case VISIT -> GenericGoalSupport.exceedsSimultaneousLimits(villager, definition, targetBuilding)
                        ? null
                        : GenericGoalSupport.buildGoalInformation(villager, definition, targetBuilding);
                case TAKE_FROM_BUILDING -> targetBuilding != null && GenericGoalSupport.canCollectFromBuilding(villager, definition, targetBuilding)
                        ? GenericGoalSupport.buildGoalInformation(villager, definition, targetBuilding)
                        : null;
                case CRAFTING -> targetBuilding != null && GenericGoalSupport.canCraft(villager, definition, targetBuilding)
                        ? GenericGoalSupport.buildGoalInformation(villager, definition, targetBuilding)
                        : null;
                case COOKING -> targetBuilding != null && GenericGoalSupport.canCook(villager, definition, targetBuilding)
                        ? GenericGoalSupport.buildGoalInformation(villager, definition, targetBuilding)
                        : null;
                case TEND_FURNACE -> targetBuilding != null && GenericGoalSupport.canMaintainFurnace(villager, definition, targetBuilding)
                        ? GenericGoalSupport.buildGoalInformation(villager, definition, targetBuilding)
                        : null;
                case GATHER_BLOCKS -> GenericGoalWorldActions.getGatherDestination(villager, definition, targetBuilding);
                case HARVESTING -> GenericGoalWorldActions.getHarvestDestination(villager, definition, targetBuilding);
                case PLANTING -> GenericGoalWorldActions.getPlantDestination(villager, definition, targetBuilding);
                case MINING -> GenericGoalWorldActions.getMiningDestination(villager, definition, targetBuilding);
                case SLAUGHTER_ANIMAL -> GenericGoalWorldActions.getSlaughterDestination(villager, definition, targetBuilding);
                default -> null;
            };
        }

        @Override
        public boolean performAction(MillVillager villager) {
            Building targetBuilding = GenericGoalSupport.resolveTargetBuilding(villager, definition);
            boolean finished = switch (definition.family) {
                case VISIT -> true;
                case TAKE_FROM_BUILDING -> targetBuilding == null || GenericGoalSupport.collectFromBuilding(villager, definition, targetBuilding);
                case CRAFTING -> targetBuilding == null || GenericGoalSupport.craft(villager, definition, targetBuilding);
                case COOKING -> targetBuilding == null || GenericGoalSupport.cook(villager, definition, targetBuilding);
                case TEND_FURNACE -> targetBuilding == null || GenericGoalSupport.maintainFurnace(villager, definition, targetBuilding);
                case GATHER_BLOCKS -> GenericGoalWorldActions.performGather(villager, definition, targetBuilding);
                case HARVESTING -> GenericGoalWorldActions.performHarvest(villager, definition, targetBuilding);
                case PLANTING -> GenericGoalWorldActions.performPlant(villager, definition, targetBuilding);
                case MINING -> GenericGoalWorldActions.performMining(villager, definition, targetBuilding);
                case SLAUGHTER_ANIMAL -> GenericGoalWorldActions.performSlaughter(villager, definition, targetBuilding);
                default -> true;
            };
            if (finished) {
                GenericGoalSupport.clearHeldItems(villager);
            }
            return finished;
        }

        @Override
        public int actionDuration(MillVillager villager) {
            GenericGoalSupport.applyHeldItems(villager, definition);
            villager.stopMoving = !definition.allowRandomMoves;
            VillagerActionRuntime runtime = villager.getActionRuntime();
            if (usesRuntimeActions() && (runtime.hasAction() || runtime.getLastResult().status() != VillagerActionRuntime.Status.IDLE)) {
                return 1;
            }
            return definition.durationTicks;
        }

        private boolean usesRuntimeActions() {
            return switch (definition.family) {
                case GATHER_BLOCKS, HARVESTING, PLANTING, MINING, SLAUGHTER_ANIMAL -> true;
                default -> false;
            };
        }

        @Override
        public boolean allowRandomMoves() {
            return definition.allowRandomMoves;
        }

        @Override
        public boolean isStillValid(MillVillager villager) {
            return getDestination(villager) != null;
        }

        @Override
        public int range(MillVillager villager) {
            return definition.range;
        }

        @Override
        public String labelKey(MillVillager villager) {
            return definition.labelKey.isEmpty() ? super.labelKey(villager) : definition.labelKey;
        }

        @Override
        public String labelKeyWhileTravelling(MillVillager villager) {
            return labelKey(villager);
        }

        @Override
        public String gameName() {
            return "goal." + (definition.sentenceKey.isEmpty() ? key : definition.sentenceKey);
        }
    }
}
