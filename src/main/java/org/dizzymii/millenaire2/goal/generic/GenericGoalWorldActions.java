package org.dizzymii.millenaire2.goal.generic;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.entity.VillagerActionRuntime;
import org.dizzymii.millenaire2.entity.action.VillagerActions;
import org.dizzymii.millenaire2.goal.GoalInformation;
import org.dizzymii.millenaire2.item.InvItem;
import org.dizzymii.millenaire2.util.Point;
import org.dizzymii.millenaire2.village.Building;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

final class GenericGoalWorldActions {

    private GenericGoalWorldActions() {}

    @Nullable
    static GoalInformation getGatherDestination(MillVillager villager, GenericGoalDefinition definition, @Nullable Building targetBuilding) {
        if (targetBuilding == null || GenericGoalSupport.limitsReached(villager, definition, targetBuilding)
                || GenericGoalSupport.exceedsSimultaneousLimits(villager, definition, targetBuilding)) {
            return null;
        }
        BlockPos target = findGatherBlock(villager, definition, targetBuilding, false);
        return target == null ? null : new GoalInformation(new Point(target), targetBuilding, definition.range);
    }

    static boolean performGather(MillVillager villager, GenericGoalDefinition definition, @Nullable Building targetBuilding) {
        BlockPos target = findGatherBlock(villager, definition, targetBuilding, true);
        if (target == null) {
            return true;
        }
        BlockState resultingState = definition.parseResultingBlockState();
        return switch (advanceAction(villager, "generic_gather_" + target.asLong(), VillagerActions.breakBlock(target, false))) {
            case RUNNING -> false;
            case SUCCESS -> {
                if (resultingState != null) {
                    villager.level().setBlockAndUpdate(target, resultingState);
                }
                awardChanceItems(villager, targetBuilding, definition.harvestItems, definition.collectInBuilding, targetBuilding);
                yield true;
            }
            case FAILED -> true;
        };
    }

    @Nullable
    static GoalInformation getHarvestDestination(MillVillager villager, GenericGoalDefinition definition, @Nullable Building targetBuilding) {
        if (targetBuilding == null || GenericGoalSupport.limitsReached(villager, definition, targetBuilding)
                || GenericGoalSupport.exceedsSimultaneousLimits(villager, definition, targetBuilding)) {
            return null;
        }
        BlockPos target = findHarvestBlock(villager, definition, targetBuilding, false);
        return target == null ? null : new GoalInformation(new Point(target), targetBuilding, definition.range);
    }

    static boolean performHarvest(MillVillager villager, GenericGoalDefinition definition, @Nullable Building targetBuilding) {
        BlockPos target = findHarvestBlock(villager, definition, targetBuilding, true);
        if (target == null) {
            return true;
        }
        BlockState state = villager.level().getBlockState(target);
        return switch (advanceAction(villager, "generic_harvest_" + target.asLong(), VillagerActions.breakBlock(target, false))) {
            case RUNNING -> false;
            case SUCCESS -> {
                if (state.getBlock() instanceof CropBlock cropBlock) {
                    villager.level().setBlockAndUpdate(target, cropBlock.getStateForAge(0));
                }
                awardChanceItems(villager, targetBuilding, definition.harvestItems, false, targetBuilding);
                yield true;
            }
            case FAILED -> true;
        };
    }

    @Nullable
    static GoalInformation getPlantDestination(MillVillager villager, GenericGoalDefinition definition, @Nullable Building targetBuilding) {
        if (targetBuilding == null || GenericGoalSupport.exceedsSimultaneousLimits(villager, definition, targetBuilding)) {
            return null;
        }
        if (!hasPlantingInputs(villager, definition, targetBuilding)) {
            return null;
        }
        BlockPos target = findPlantingSpot(villager, definition, targetBuilding, false);
        return target == null ? null : new GoalInformation(new Point(target), targetBuilding, definition.range);
    }

    static boolean performPlant(MillVillager villager, GenericGoalDefinition definition, @Nullable Building targetBuilding) {
        if (targetBuilding == null || !hasPlantingInputs(villager, definition, targetBuilding)) {
            return true;
        }
        BlockPos target = findPlantingSpot(villager, definition, targetBuilding, true);
        if (target == null) {
            return true;
        }
        BlockState plantState = resolvePlantState(definition);
        if (plantState == null) {
            return true;
        }
        return switch (advanceAction(villager, "generic_plant_" + target.asLong(), VillagerActions.placeBlock(target, plantState, false))) {
            case RUNNING -> false;
            case SUCCESS -> {
                consumePlantingInputs(villager, definition, targetBuilding);
                yield true;
            }
            case FAILED -> true;
        };
    }

    @Nullable
    static GoalInformation getMiningDestination(MillVillager villager, GenericGoalDefinition definition, @Nullable Building targetBuilding) {
        if (GenericGoalSupport.limitsReached(villager, definition, targetBuilding)
                || GenericGoalSupport.exceedsSimultaneousLimits(villager, definition, targetBuilding)) {
            return null;
        }
        BlockPos target = findMiningBlock(villager, definition, targetBuilding, false);
        return target == null ? null : new GoalInformation(new Point(target), targetBuilding, definition.range);
    }

    static boolean performMining(MillVillager villager, GenericGoalDefinition definition, @Nullable Building targetBuilding) {
        BlockPos target = findMiningBlock(villager, definition, targetBuilding, true);
        if (target == null) {
            return true;
        }
        return switch (advanceAction(villager, "generic_mine_" + target.asLong(), VillagerActions.breakBlock(target, false))) {
            case RUNNING -> false;
            case SUCCESS -> {
                for (GenericGoalDefinition.ItemAmount loot : definition.lootItems) {
                    InvItem item = loot.resolve();
                    if (item != null) {
                        villager.addToInv(item, loot.count);
                    }
                }
                yield true;
            }
            case FAILED -> true;
        };
    }

    @Nullable
    static GoalInformation getSlaughterDestination(MillVillager villager, GenericGoalDefinition definition, @Nullable Building targetBuilding) {
        if (GenericGoalSupport.exceedsSimultaneousLimits(villager, definition, targetBuilding)) {
            return null;
        }
        LivingEntity target = findAnimalTarget(villager, definition, targetBuilding, false);
        return target == null ? null : new GoalInformation(new Point(target.blockPosition()), targetBuilding, definition.range);
    }

    static boolean performSlaughter(MillVillager villager, GenericGoalDefinition definition, @Nullable Building targetBuilding) {
        LivingEntity target = findAnimalTarget(villager, definition, targetBuilding, true);
        if (target == null) {
            return true;
        }
        return switch (advanceAction(villager, "generic_slaughter_" + target.getId(), VillagerActions.attackEntity(target.getId(), Float.MAX_VALUE))) {
            case RUNNING -> false;
            case SUCCESS -> {
                if (target.isAlive()) {
                    target.discard();
                }
                awardChanceItems(villager, targetBuilding, definition.bonusItems, false, targetBuilding);
                yield true;
            }
            case FAILED -> true;
        };
    }

    @Nullable
    private static BlockPos findGatherBlock(MillVillager villager, GenericGoalDefinition definition, @Nullable Building targetBuilding, boolean preferGoalTarget) {
        BlockState expected = definition.parseGatherBlockState();
        if (expected == null) {
            return null;
        }
        return findMatchingBlock(villager, targetBuilding, preferGoalTarget, actionSearchRadius(definition), 4,
                state -> state.getBlock() == expected.getBlock());
    }

    @Nullable
    private static BlockPos findHarvestBlock(MillVillager villager, GenericGoalDefinition definition, @Nullable Building targetBuilding, boolean preferGoalTarget) {
        List<BlockState> explicitStates = definition.parseHarvestBlockStates();
        if (!explicitStates.isEmpty()) {
            return findMatchingBlock(villager, targetBuilding, preferGoalTarget, actionSearchRadius(definition), 4,
                    state -> matchesAnyBlock(state, explicitStates));
        }
        CropBlock cropBlock = resolveCropBlock(definition.cropType);
        if (cropBlock == null) {
            return null;
        }
        return findMatchingBlock(villager, targetBuilding, preferGoalTarget, actionSearchRadius(definition), 4,
                state -> state.getBlock() == cropBlock && cropBlock.isMaxAge(state));
    }

    @Nullable
    private static BlockPos findPlantingSpot(MillVillager villager, GenericGoalDefinition definition, @Nullable Building targetBuilding, boolean preferGoalTarget) {
        BlockState plantState = resolvePlantState(definition);
        if (plantState == null) {
            return null;
        }
        CropBlock cropBlock = resolveCropBlock(definition.cropType);
        return findMatchingPosition(villager, targetBuilding, preferGoalTarget, actionSearchRadius(definition), 4,
                pos -> isValidPlantSpot(villager.level(), pos, definition, plantState, cropBlock));
    }

    @Nullable
    private static BlockPos findMiningBlock(MillVillager villager, GenericGoalDefinition definition, @Nullable Building targetBuilding, boolean preferGoalTarget) {
        BlockState expected = definition.parseSourceBlockState();
        if (expected == null) {
            return null;
        }
        return findMatchingBlock(villager, targetBuilding, preferGoalTarget, Math.max(actionSearchRadius(definition), 6), 4,
                state -> state.getBlock() == expected.getBlock());
    }

    @Nullable
    private static LivingEntity findAnimalTarget(MillVillager villager, GenericGoalDefinition definition, @Nullable Building targetBuilding, boolean preferGoalTarget) {
        BlockPos center = getSearchCenter(villager, targetBuilding, preferGoalTarget);
        int radius = Math.max(actionSearchRadius(definition), 10);
        AABB searchBox = AABB.ofSize(Vec3.atCenterOf(center), radius * 2.0 + 1.0, 8.0, radius * 2.0 + 1.0);
        LivingEntity nearest = null;
        double bestDistance = Double.MAX_VALUE;
        for (LivingEntity entity : villager.level().getEntitiesOfClass(LivingEntity.class, searchBox, living -> living.isAlive() && matchesAnimal(definition, living))) {
            double distance = entity.distanceToSqr(Vec3.atCenterOf(center));
            if (nearest == null || distance < bestDistance) {
                nearest = entity;
                bestDistance = distance;
            }
        }
        return nearest;
    }

    private static boolean matchesAnimal(GenericGoalDefinition definition, LivingEntity entity) {
        ResourceLocation key = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
        if (key == null || !key.getPath().equalsIgnoreCase(definition.animalKey)) {
            return false;
        }
        return !(entity instanceof AgeableMob ageableMob) || !ageableMob.isBaby();
    }

    private static boolean hasPlantingInputs(MillVillager villager, GenericGoalDefinition definition, @Nullable Building targetBuilding) {
        if (definition.seedItemKey.isEmpty()) {
            return true;
        }
        InvItem seed = InvItem.get(definition.seedItemKey);
        return seed != null && GenericGoalSupport.countAvailable(villager, seed, targetBuilding, villager.getTownHallBuilding()) > 0;
    }

    private static boolean consumePlantingInputs(MillVillager villager, GenericGoalDefinition definition, @Nullable Building targetBuilding) {
        if (definition.seedItemKey.isEmpty()) {
            return true;
        }
        InvItem seed = InvItem.get(definition.seedItemKey);
        return seed != null && GenericGoalSupport.takeAvailable(villager, seed, 1, targetBuilding, villager.getTownHallBuilding());
    }

    @Nullable
    private static BlockState resolvePlantState(GenericGoalDefinition definition) {
        List<BlockState> explicitStates = definition.parsePlantBlockStates();
        if (!explicitStates.isEmpty()) {
            return explicitStates.get(0);
        }
        CropBlock cropBlock = resolveCropBlock(definition.cropType);
        return cropBlock == null ? null : cropBlock.defaultBlockState();
    }

    @Nullable
    private static CropBlock resolveCropBlock(String cropType) {
        return switch (cropType) {
            case "wheat" -> (CropBlock) Blocks.WHEAT;
            case "potatoes" -> (CropBlock) Blocks.POTATOES;
            case "carrots" -> (CropBlock) Blocks.CARROTS;
            case "beetroots" -> (CropBlock) Blocks.BEETROOTS;
            default -> {
                Block block = resolveBlock(cropType);
                yield block instanceof CropBlock cropBlock ? cropBlock : null;
            }
        };
    }

    @Nullable
    private static Block resolveBlock(String key) {
        if (key == null || key.isBlank() || "flower".equals(key)) {
            return null;
        }
        String normalized = key.contains(":") ? key : "minecraft:" + key;
        try {
            return BuiltInRegistries.BLOCK.get(ResourceLocation.parse(normalized));
        } catch (Exception ignored) {
            return null;
        }
    }

    private static boolean isValidPlantSpot(Level level, BlockPos pos, GenericGoalDefinition definition, BlockState plantState, @Nullable CropBlock cropBlock) {
        if (!level.getBlockState(pos).isAir()) {
            return false;
        }
        if (plantState.getBlock() instanceof DoublePlantBlock && !level.getBlockState(pos.above()).isAir()) {
            return false;
        }
        BlockState belowState = level.getBlockState(pos.below());
        if (cropBlock != null) {
            return belowState.is(Blocks.FARMLAND);
        }
        if ("grass".equals(definition.soilType)) {
            return belowState.is(Blocks.GRASS_BLOCK) || belowState.is(BlockTags.DIRT);
        }
        return belowState.isFaceSturdy(level, pos.below(), Direction.UP);
    }

    private static void placePlant(Level level, BlockPos pos, BlockState plantState) {
        if (plantState.getBlock() instanceof DoublePlantBlock && plantState.hasProperty(DoublePlantBlock.HALF)) {
            level.setBlockAndUpdate(pos, plantState.setValue(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER));
            level.setBlockAndUpdate(pos.above(), plantState.setValue(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER));
            return;
        }
        level.setBlockAndUpdate(pos, plantState);
    }

    private static void awardChanceItems(MillVillager villager, @Nullable Building outputBuilding, List<GenericGoalDefinition.ChanceItem> items, boolean collectInBuilding, @Nullable Building tagContextBuilding) {
        for (GenericGoalDefinition.ChanceItem chanceItem : items) {
            if (chanceItem.requiredTag != null && (tagContextBuilding == null || !tagContextBuilding.hasTag(chanceItem.requiredTag))) {
                continue;
            }
            InvItem item = chanceItem.resolve();
            if (item == null) {
                continue;
            }
            if (villager.level().random.nextInt(100) < chanceItem.chance) {
                if (collectInBuilding && outputBuilding != null) {
                    outputBuilding.resManager.storeGoods(item, 1);
                } else {
                    villager.addToInv(item, 1);
                }
            }
        }
    }

    private static boolean matchesAnyBlock(BlockState state, List<BlockState> expectedStates) {
        for (BlockState expected : expectedStates) {
            if (state.getBlock() == expected.getBlock()) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    private static BlockPos findMatchingBlock(MillVillager villager, @Nullable Building targetBuilding, boolean preferGoalTarget, int horizontalRadius, int verticalRadius, Predicate<BlockState> predicate) {
        return findMatchingPosition(villager, targetBuilding, preferGoalTarget, horizontalRadius, verticalRadius,
                pos -> predicate.test(villager.level().getBlockState(pos)));
    }

    @Nullable
    private static BlockPos findMatchingPosition(MillVillager villager, @Nullable Building targetBuilding, boolean preferGoalTarget, int horizontalRadius, int verticalRadius, Predicate<BlockPos> predicate) {
        BlockPos center = getSearchCenter(villager, targetBuilding, preferGoalTarget);
        BlockPos nearest = null;
        double bestDistance = Double.MAX_VALUE;
        for (int dx = -horizontalRadius; dx <= horizontalRadius; dx++) {
            for (int dy = -verticalRadius; dy <= verticalRadius; dy++) {
                for (int dz = -horizontalRadius; dz <= horizontalRadius; dz++) {
                    BlockPos candidate = center.offset(dx, dy, dz);
                    if (!predicate.test(candidate)) {
                        continue;
                    }
                    double distance = candidate.distSqr(center);
                    if (nearest == null || distance < bestDistance) {
                        nearest = candidate.immutable();
                        bestDistance = distance;
                    }
                }
            }
        }
        return nearest;
    }

    private static BlockPos getSearchCenter(MillVillager villager, @Nullable Building targetBuilding, boolean preferGoalTarget) {
        Point preferredPoint = preferGoalTarget ? villager.getPathDestPoint() : null;
        if (preferredPoint != null) {
            return new BlockPos(preferredPoint.x, preferredPoint.y, preferredPoint.z);
        }
        if (targetBuilding != null) {
            Point buildingPoint = targetBuilding.getPos() != null ? targetBuilding.getPos() : targetBuilding.getTownHallPos();
            if (buildingPoint != null) {
                return new BlockPos(buildingPoint.x, buildingPoint.y, buildingPoint.z);
            }
        }
        return villager.blockPosition();
    }

    private static int actionSearchRadius(GenericGoalDefinition definition) {
        return Math.max(8, definition.range * 3);
    }

    private static ActionProgress advanceAction(MillVillager villager, String actionKey, VillagerActionRuntime.Action action) {
        VillagerActionRuntime runtime = villager.getActionRuntime();
        if (runtime.hasAction()) {
            return ActionProgress.RUNNING;
        }
        VillagerActionRuntime.Result lastResult = runtime.getLastResult();
        if (lastResult.status() == VillagerActionRuntime.Status.SUCCESS) {
            if (actionKey.equals(runtime.getLastCompletedActionKey())) {
                runtime.reset(villager);
                return ActionProgress.SUCCESS;
            }
            runtime.reset(villager);
        }
        if (lastResult.status() == VillagerActionRuntime.Status.FAILED) {
            if (actionKey.equals(runtime.getLastCompletedActionKey())) {
                runtime.reset(villager);
                return ActionProgress.FAILED;
            }
            runtime.reset(villager);
        }
        runtime.start(actionKey, action, villager);
        return ActionProgress.RUNNING;
    }

    private enum ActionProgress {
        RUNNING,
        SUCCESS,
        FAILED
    }
}
