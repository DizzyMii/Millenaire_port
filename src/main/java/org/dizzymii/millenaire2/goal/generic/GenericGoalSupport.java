package org.dizzymii.millenaire2.goal.generic;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.goal.GoalInformation;
import org.dizzymii.millenaire2.item.InvItem;
import org.dizzymii.millenaire2.util.Point;
import org.dizzymii.millenaire2.village.Building;
import org.dizzymii.millenaire2.world.MillWorldData;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

final class GenericGoalSupport {

    private GenericGoalSupport() {}

    @Nullable
    static MillWorldData getWorldData(MillVillager villager) {
        return villager.level() instanceof ServerLevel serverLevel ? MillWorldData.get(serverLevel) : null;
    }

    @Nullable
    static Point getVillageAnchor(MillVillager villager) {
        return villager.townHallPoint != null ? villager.townHallPoint : villager.housePoint;
    }

    @Nullable
    static Building resolveTargetBuilding(MillVillager villager, GenericGoalDefinition definition) {
        if (definition.townHallGoal) {
            Building townHall = villager.getTownHallBuilding();
            return hasRequiredBuildingTags(townHall, definition) ? townHall : null;
        }
        MillWorldData worldData = getWorldData(villager);
        Point villageAnchor = getVillageAnchor(villager);
        if (worldData != null && villageAnchor != null && (!definition.buildingTag.isEmpty() || !definition.requiredTags.isEmpty())) {
            Building nearest = null;
            double bestDistance = Double.MAX_VALUE;
            Point origin = new Point(villager.blockPosition());
            for (Building building : worldData.getVillageBuildings(villageAnchor)) {
                if (!hasRequiredBuildingTags(building, definition)) {
                    continue;
                }
                Point point = building.getPos() != null ? building.getPos() : building.getTownHallPos();
                if (point == null) {
                    continue;
                }
                double distance = origin.distanceTo(point);
                if (nearest == null || distance < bestDistance) {
                    nearest = building;
                    bestDistance = distance;
                }
            }
            return nearest;
        }
        Building home = villager.getHomeBuilding();
        if (hasRequiredBuildingTags(home, definition)) {
            return home;
        }
        Building townHall = villager.getTownHallBuilding();
        return hasRequiredBuildingTags(townHall, definition) ? townHall : null;
    }

    @Nullable
    static GoalInformation buildGoalInformation(MillVillager villager, GenericGoalDefinition definition, @Nullable Building building) {
        if (building == null) {
            return null;
        }
        Point targetPoint = resolveTargetPoint(building, definition);
        return targetPoint == null ? null : new GoalInformation(targetPoint, building, definition.range);
    }

    @Nullable
    static Point resolveTargetPoint(Building building, GenericGoalDefinition definition) {
        String targetPosition = definition.targetPosition;
        if (targetPosition.isEmpty()) {
            targetPosition = switch (definition.family) {
                case VISIT -> definition.leisure ? "leasure" : "";
                case TAKE_FROM_BUILDING -> "chest";
                case CRAFTING -> "crafting";
                case COOKING, TEND_FURNACE -> "crafting";
                default -> "";
            };
        }
        Point point = targetPosition.isEmpty() ? building.getPos() : building.getActivityPosition(targetPosition);
        return point != null ? point : building.getPos();
    }

    static boolean hasRequiredBuildingTags(@Nullable Building building, GenericGoalDefinition definition) {
        if (building == null) {
            return definition.buildingTag.isEmpty() && definition.requiredTags.isEmpty();
        }
        if (!definition.buildingTag.isEmpty() && !building.hasTag(definition.buildingTag)) {
            return false;
        }
        for (String requiredTag : definition.requiredTags) {
            if (!building.hasTag(requiredTag)) {
                return false;
            }
        }
        return true;
    }

    static boolean hasRequiredVillagerTags(MillVillager villager, GenericGoalDefinition definition) {
        if (definition.requiredTags.isEmpty()) {
            return true;
        }
        if (villager.vtype == null) {
            return false;
        }
        var villagerType = villager.vtype;
        for (String requiredTag : definition.requiredTags) {
            if (!villagerType.tags.contains(requiredTag)) {
                return false;
            }
        }
        return true;
    }

    static boolean limitsReached(MillVillager villager, GenericGoalDefinition definition, @Nullable Building targetBuilding) {
        if (targetBuilding != null) {
            for (GenericGoalDefinition.ItemAmount limit : definition.buildingLimits) {
                if (countStoredGoods(targetBuilding, limit) >= limit.count) {
                    return true;
                }
            }
        }
        Building townHall = villager.getTownHallBuilding();
        if (townHall != null) {
            for (GenericGoalDefinition.ItemAmount limit : definition.townHallLimits) {
                if (countStoredGoods(townHall, limit) >= limit.count) {
                    return true;
                }
            }
        }
        for (GenericGoalDefinition.ItemAmount limit : definition.villageLimits) {
            if (countVillageGoods(villager, limit) >= limit.count) {
                return true;
            }
        }
        return false;
    }

    static int countVillageGoods(MillVillager villager, GenericGoalDefinition.ItemAmount amount) {
        InvItem item = amount.resolve();
        if (item == null) {
            return 0;
        }
        Point villageAnchor = getVillageAnchor(villager);
        MillWorldData worldData = getWorldData(villager);
        if (villageAnchor == null || worldData == null) {
            return 0;
        }
        int total = 0;
        for (Building building : worldData.getVillageBuildings(villageAnchor)) {
            total += building.resManager.countGoods(item);
        }
        return total;
    }

    static boolean exceedsSimultaneousLimits(MillVillager villager, GenericGoalDefinition definition, @Nullable Building targetBuilding) {
        if (definition.maxSimultaneousInBuilding <= 0 && definition.maxSimultaneousTotal <= 0) {
            return false;
        }
        if (!(villager.level() instanceof ServerLevel serverLevel)) {
            return false;
        }
        Point villageAnchor = getVillageAnchor(villager);
        AABB searchBox = villageAnchor != null
                ? AABB.ofSize(new Vec3(villageAnchor.x + 0.5, villageAnchor.y + 0.5, villageAnchor.z + 0.5), 256.0, 128.0, 256.0)
                : villager.getBoundingBox().inflate(128.0);
        int total = 0;
        int inBuilding = 0;
        for (MillVillager other : serverLevel.getEntitiesOfClass(MillVillager.class, searchBox, entity -> entity != villager && entity.isAlive())) {
            if (!Objects.equals(other.goalKey, definition.goalKey)) {
                continue;
            }
            if (villageAnchor != null && !Objects.equals(other.townHallPoint, villageAnchor)) {
                continue;
            }
            total++;
            GoalInformation otherGoal = other.getGoalInformation();
            if (targetBuilding != null && sameBuilding(targetBuilding, otherGoal != null ? otherGoal.targetBuilding : null)) {
                inBuilding++;
            }
        }
        return definition.maxSimultaneousTotal > 0 && total >= definition.maxSimultaneousTotal
                || definition.maxSimultaneousInBuilding > 0 && inBuilding >= definition.maxSimultaneousInBuilding;
    }

    static boolean canCollectFromBuilding(MillVillager villager, GenericGoalDefinition definition, Building sourceBuilding) {
        GenericGoalDefinition.ItemAmount collectGood = definition.collectGood;
        if (collectGood == null) {
            return false;
        }
        InvItem item = collectGood.resolve();
        if (item == null) {
            return false;
        }
        int available = sourceBuilding.resManager.countGoods(item);
        if (available < Math.max(1, definition.minimumPickup)) {
            return false;
        }
        return villager.countInv(item) < collectGood.count;
    }

    static boolean collectFromBuilding(MillVillager villager, GenericGoalDefinition definition, Building sourceBuilding) {
        GenericGoalDefinition.ItemAmount collectGood = definition.collectGood;
        if (collectGood == null) {
            return true;
        }
        InvItem item = collectGood.resolve();
        if (item == null) {
            return true;
        }
        int current = villager.countInv(item);
        int available = sourceBuilding.resManager.countGoods(item);
        if (current >= collectGood.count || available < Math.max(1, definition.minimumPickup)) {
            return true;
        }
        int amountToTake = Math.min(collectGood.count - current, available);
        if (amountToTake <= 0) {
            return true;
        }
        if (sourceBuilding.resManager.takeGoods(item, amountToTake)) {
            villager.addToInv(item, amountToTake);
        }
        return true;
    }

    static boolean canCraft(MillVillager villager, GenericGoalDefinition definition, Building targetBuilding) {
        return !limitsReached(villager, definition, targetBuilding)
                && !exceedsSimultaneousLimits(villager, definition, targetBuilding)
                && hasInputs(villager, definition.inputs, targetBuilding, villager.getTownHallBuilding());
    }

    static boolean craft(MillVillager villager, GenericGoalDefinition definition, Building targetBuilding) {
        if (!consumeInputs(villager, definition.inputs, targetBuilding, villager.getTownHallBuilding())) {
            return true;
        }
        storeOutputs(villager, definition, targetBuilding, definition.outputs);
        return true;
    }

    static boolean canCook(MillVillager villager, GenericGoalDefinition definition, Building targetBuilding) {
        if (limitsReached(villager, definition, targetBuilding) || exceedsSimultaneousLimits(villager, definition, targetBuilding)) {
            return false;
        }
        InvItem inputItem = InvItem.get(definition.itemToCookKey);
        if (inputItem == null || smeltingOutput(villager, inputItem) == null) {
            return false;
        }
        GenericGoalDefinition.ItemPair itemsBalance = definition.itemsBalance;
        if (itemsBalance != null) {
            Building balanceBuilding = definition.townHallGoal ? villager.getTownHallBuilding() : targetBuilding;
            if (balanceBuilding != null) {
                InvItem firstItem = itemsBalance.first();
                InvItem secondItem = itemsBalance.second();
                if (firstItem != null && secondItem != null && balanceBuilding.resManager.countGoods(secondItem) > balanceBuilding.resManager.countGoods(firstItem)) {
                    return false;
                }
            }
        }
        return countAvailable(villager, inputItem, targetBuilding, villager.getTownHallBuilding()) >= Math.max(1, definition.minimumToCook);
    }

    static boolean cook(MillVillager villager, GenericGoalDefinition definition, Building targetBuilding) {
        InvItem inputItem = InvItem.get(definition.itemToCookKey);
        if (inputItem == null) {
            return true;
        }
        ItemStack result = smeltingOutput(villager, inputItem);
        if (result == null || result.isEmpty()) {
            return true;
        }
        int available = countAvailable(villager, inputItem, targetBuilding, villager.getTownHallBuilding());
        int batchSize = Math.min(available, Math.max(1, definition.minimumToCook));
        if (batchSize <= 0 || !takeAvailable(villager, inputItem, batchSize, targetBuilding, villager.getTownHallBuilding())) {
            return true;
        }
        InvItem outputItem = InvItem.fromItem(result.getItem());
        if (outputItem == null) {
            villager.addToInv(inputItem, batchSize);
            return true;
        }
        GenericGoalDefinition.ItemAmount cookedOutput = new GenericGoalDefinition.ItemAmount(outputItem.key, result.getCount() * batchSize);
        storeOutputs(villager, definition, targetBuilding, List.of(cookedOutput));
        return true;
    }

    static boolean canMaintainFurnace(MillVillager villager, GenericGoalDefinition definition, Building targetBuilding) {
        InvItem fuelItem = resolveFuelItem(definition);
        if (fuelItem == null) {
            return false;
        }
        int currentFuel = targetBuilding.resManager.countGoods(fuelItem);
        if (currentFuel >= Math.max(1, definition.minimumFuel)) {
            return true;
        }
        Set<Building> sources = new HashSet<>();
        Building townHall = villager.getTownHallBuilding();
        Building home = villager.getHomeBuilding();
        if (townHall != null) {
            sources.add(townHall);
        }
        if (home != null) {
            sources.add(home);
        }
        int availableElsewhere = villager.countInv(fuelItem);
        for (Building source : sources) {
            if (!sameBuilding(source, targetBuilding)) {
                availableElsewhere += source.resManager.countGoods(fuelItem);
            }
        }
        return availableElsewhere > 0;
    }

    static boolean maintainFurnace(MillVillager villager, GenericGoalDefinition definition, Building targetBuilding) {
        InvItem fuelItem = resolveFuelItem(definition);
        if (fuelItem == null) {
            return true;
        }
        int minimumFuel = Math.max(1, definition.minimumFuel);
        int currentFuel = targetBuilding.resManager.countGoods(fuelItem);
        if (currentFuel >= minimumFuel) {
            return true;
        }
        int needed = minimumFuel - currentFuel;
        Building townHall = villager.getTownHallBuilding();
        Building home = villager.getHomeBuilding();
        if (townHall != null && !sameBuilding(townHall, targetBuilding)) {
            needed -= moveStoredGoods(townHall, targetBuilding, fuelItem, needed);
        }
        if (needed > 0 && home != null && !sameBuilding(home, targetBuilding)) {
            needed -= moveStoredGoods(home, targetBuilding, fuelItem, needed);
        }
        if (needed > 0) {
            int villagerFuel = villager.countInv(fuelItem);
            int move = Math.min(needed, villagerFuel);
            if (move > 0) {
                villager.removeFromInv(fuelItem, move);
                targetBuilding.resManager.storeGoods(fuelItem, move);
            }
        }
        return true;
    }

    static void applyHeldItems(MillVillager villager, GenericGoalDefinition definition) {
        if (!definition.heldItems.isEmpty()) {
            setHeldItem(villager, definition.heldItems.get(0));
        }
    }

    static void clearHeldItems(MillVillager villager) {
        villager.setSelectedInventorySlot(-1);
        villager.syncSelectedItemToHands();
        villager.heldItemCount = 0;
    }

    @Nullable
    static Building resolveOutputBuilding(MillVillager villager, GenericGoalDefinition definition, @Nullable Building targetBuilding) {
        if (definition.townHallGoal) {
            return villager.getTownHallBuilding();
        }
        if (!definition.buildingTag.isEmpty() || !definition.buildingLimits.isEmpty()) {
            return targetBuilding;
        }
        if (!definition.townHallLimits.isEmpty() || !definition.villageLimits.isEmpty()) {
            Building townHall = villager.getTownHallBuilding();
            return townHall != null ? townHall : targetBuilding;
        }
        Building home = villager.getHomeBuilding();
        if (home != null) {
            return home;
        }
        return targetBuilding != null ? targetBuilding : villager.getTownHallBuilding();
    }

    static void storeOutputs(MillVillager villager, GenericGoalDefinition definition, @Nullable Building targetBuilding, List<GenericGoalDefinition.ItemAmount> outputs) {
        Building outputBuilding = resolveOutputBuilding(villager, definition, targetBuilding);
        for (GenericGoalDefinition.ItemAmount output : outputs) {
            InvItem item = output.resolve();
            if (item == null) {
                continue;
            }
            if (outputBuilding != null) {
                outputBuilding.resManager.storeGoods(item, output.count);
            } else {
                villager.addToInv(item, output.count);
            }
        }
    }

    static boolean hasInputs(MillVillager villager, List<GenericGoalDefinition.ItemAmount> inputs, @Nullable Building primaryBuilding, @Nullable Building secondaryBuilding) {
        for (GenericGoalDefinition.ItemAmount input : inputs) {
            InvItem item = input.resolve();
            if (item == null || countAvailable(villager, item, primaryBuilding, secondaryBuilding) < input.count) {
                return false;
            }
        }
        return true;
    }

    static boolean consumeInputs(MillVillager villager, List<GenericGoalDefinition.ItemAmount> inputs, @Nullable Building primaryBuilding, @Nullable Building secondaryBuilding) {
        if (!hasInputs(villager, inputs, primaryBuilding, secondaryBuilding)) {
            return false;
        }
        for (GenericGoalDefinition.ItemAmount input : inputs) {
            InvItem item = input.resolve();
            if (item != null) {
                takeAvailable(villager, item, input.count, primaryBuilding, secondaryBuilding);
            }
        }
        return true;
    }

    static int countAvailable(MillVillager villager, InvItem item, @Nullable Building primaryBuilding, @Nullable Building secondaryBuilding) {
        int total = villager.countInv(item);
        if (primaryBuilding != null) {
            total += primaryBuilding.resManager.countGoods(item);
        }
        if (secondaryBuilding != null && !sameBuilding(primaryBuilding, secondaryBuilding)) {
            total += secondaryBuilding.resManager.countGoods(item);
        }
        return total;
    }

    static boolean takeAvailable(MillVillager villager, InvItem item, int count, @Nullable Building primaryBuilding, @Nullable Building secondaryBuilding) {
        int remaining = count;
        if (primaryBuilding != null) {
            remaining -= takeStoredGoods(primaryBuilding, item, remaining);
        }
        if (remaining > 0 && secondaryBuilding != null && !sameBuilding(primaryBuilding, secondaryBuilding)) {
            remaining -= takeStoredGoods(secondaryBuilding, item, remaining);
        }
        if (remaining > 0) {
            int carried = villager.countInv(item);
            int remove = Math.min(remaining, carried);
            if (remove > 0) {
                villager.removeFromInv(item, remove);
                remaining -= remove;
            }
        }
        return remaining <= 0;
    }

    static int countStoredGoods(@Nullable Building building, GenericGoalDefinition.ItemAmount amount) {
        if (building == null) {
            return 0;
        }
        InvItem item = amount.resolve();
        return item == null ? 0 : building.resManager.countGoods(item);
    }

    private static int moveStoredGoods(Building source, Building target, InvItem item, int maxAmount) {
        int moved = takeStoredGoods(source, item, maxAmount);
        if (moved > 0) {
            target.resManager.storeGoods(item, moved);
        }
        return moved;
    }

    private static int takeStoredGoods(Building building, InvItem item, int maxAmount) {
        int available = building.resManager.countGoods(item);
        int taken = Math.min(available, Math.max(0, maxAmount));
        if (taken > 0 && building.resManager.takeGoods(item, taken)) {
            return taken;
        }
        return 0;
    }

    @Nullable
    private static InvItem resolveFuelItem(GenericGoalDefinition definition) {
        if (definition.heldItems.isEmpty()) {
            return null;
        }
        return InvItem.get(definition.heldItems.get(0));
    }

    private static void setHeldItem(MillVillager villager, String itemKey) {
        InvItem item = InvItem.get(itemKey);
        if (item == null) {
            villager.setSelectedInventorySlot(-1);
            villager.syncSelectedItemToHands();
            villager.heldItemCount = 0;
            return;
        }
        for (int slot = 0; slot < villager.getInventoryContainer().getContainerSize(); slot++) {
            ItemStack stack = villager.getInventoryContainer().getItem(slot);
            if (!stack.isEmpty() && stack.getItem() == item.getItem()) {
                villager.setSelectedInventorySlot(slot);
                villager.syncSelectedItemToHands();
                villager.heldItemCount = stack.getCount();
                return;
            }
        }
        villager.setSelectedInventorySlot(-1);
        villager.syncSelectedItemToHands();
        villager.heldItemCount = 0;
    }

    @Nullable
    private static ItemStack smeltingOutput(MillVillager villager, InvItem inputItem) {
        if (!(villager.level() instanceof ServerLevel serverLevel)) {
            return null;
        }
        SingleRecipeInput input = new SingleRecipeInput(inputItem.getItemStack());
        Optional<RecipeHolder<SmeltingRecipe>> recipe = serverLevel.getRecipeManager().getRecipeFor(RecipeType.SMELTING, input, serverLevel);
        return recipe.map(recipeHolder -> recipeHolder.value().assemble(input, serverLevel.registryAccess())).orElse(null);
    }

    private static boolean sameBuilding(@Nullable Building first, @Nullable Building second) {
        if (first == second) {
            return true;
        }
        if (first == null || second == null) {
            return false;
        }
        return Objects.equals(first.getPos(), second.getPos())
                && Objects.equals(first.getTownHallPos(), second.getTownHallPos())
                && Objects.equals(first.planSetKey, second.planSetKey);
    }
}
