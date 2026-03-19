package org.dizzymii.millenaire2.entity.action;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.entity.VillagerActionRuntime;
import org.dizzymii.millenaire2.item.InvItem;
import org.dizzymii.millenaire2.util.Point;
import org.dizzymii.millenaire2.village.Building;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public final class VillagerActions {

    private VillagerActions() {}

    public static VillagerActionRuntime.Action waitTicks(int ticks) {
        return new WaitAction(ticks);
    }

    public static VillagerActionRuntime.Action moveTo(Point target, double speed, int completionRange, int timeoutTicks) {
        return new MoveToAction(target, speed, completionRange, timeoutTicks);
    }

    public static VillagerActionRuntime.Action face(Point target) {
        return new FacePointAction(target);
    }

    public static VillagerActionRuntime.Action equip(String itemKey) {
        return new EquipItemAction(itemKey);
    }

    public static VillagerActionRuntime.Action breakBlock(BlockPos pos, boolean dropItems) {
        return new BreakBlockAction(pos, dropItems);
    }

    public static VillagerActionRuntime.Action breakBlockAsPlayer(BlockPos pos) {
        return new ProxyBreakBlockAction(pos);
    }

    public static VillagerActionRuntime.Action placeBlock(BlockPos pos, BlockState blockState, boolean consumeSelectedItem) {
        return new PlaceBlockAction(pos, blockState, consumeSelectedItem);
    }

    public static VillagerActionRuntime.Action useBlock(BlockPos pos, Direction face, InteractionHand hand) {
        return new UseBlockAction(pos, face, hand);
    }

    public static VillagerActionRuntime.Action interactEntity(int entityId, InteractionHand hand) {
        return new InteractEntityAction(entityId, hand);
    }

    public static VillagerActionRuntime.Action attackEntity(int entityId, float damage) {
        return new AttackEntityAction(entityId, damage);
    }

    public static VillagerActionRuntime.Action takeStoredGoods(Building building, InvItem item, int amount) {
        return new TakeStoredGoodsAction(building, item, amount);
    }

    public static VillagerActionRuntime.Action storeGoods(Building building, InvItem item, int amount) {
        return new StoreGoodsAction(building, item, amount);
    }

    public static VillagerActionRuntime.Action storeAllInventory(Building building) {
        return new StoreAllInventoryAction(building);
    }

    public static VillagerActionRuntime.Action transformStoredGoods(Building building, Map<InvItem, Integer> inputs, Map<InvItem, Integer> outputs) {
        return new TransformStoredGoodsAction(building, inputs, outputs);
    }

    private static final class WaitAction implements VillagerActionRuntime.Action {
        private final int ticks;
        private long startedAt = -1L;

        private WaitAction(int ticks) {
            this.ticks = Math.max(0, ticks);
        }

        @Override
        public String key() {
            return "wait";
        }

        @Override
        public VillagerActionRuntime.ExecutorType executorType() {
            return VillagerActionRuntime.ExecutorType.VILLAGER_NATIVE;
        }

        @Override
        public void start(MillVillager villager) {
            startedAt = villager.level().getGameTime();
        }

        @Override
        public VillagerActionRuntime.Result tick(MillVillager villager) {
            if (startedAt < 0L) {
                startedAt = villager.level().getGameTime();
            }
            return villager.level().getGameTime() - startedAt >= ticks
                    ? VillagerActionRuntime.Result.success("wait")
                    : VillagerActionRuntime.Result.running("wait");
        }
    }

    private static final class MoveToAction implements VillagerActionRuntime.Action {
        private final Point target;
        private final double speed;
        private final int completionRange;
        private final int timeoutTicks;
        private long startedAt = -1L;

        private MoveToAction(Point target, double speed, int completionRange, int timeoutTicks) {
            this.target = target;
            this.speed = speed;
            this.completionRange = Math.max(1, completionRange);
            this.timeoutTicks = Math.max(20, timeoutTicks);
        }

        @Override
        public String key() {
            return "move_to";
        }

        @Override
        public VillagerActionRuntime.ExecutorType executorType() {
            return VillagerActionRuntime.ExecutorType.VILLAGER_NATIVE;
        }

        @Override
        public void start(MillVillager villager) {
            startedAt = villager.level().getGameTime();
            villager.getNavigation().moveTo(target.x + 0.5, target.y, target.z + 0.5, speed);
            villager.setPathDestPoint(target);
        }

        @Override
        public VillagerActionRuntime.Result tick(MillVillager villager) {
            if (isInRange(villager, target, completionRange)) {
                return VillagerActionRuntime.Result.success("move_to");
            }
            if (startedAt >= 0L && villager.level().getGameTime() - startedAt > timeoutTicks) {
                return VillagerActionRuntime.Result.failure("move_to_timeout", true);
            }
            if (villager.getNavigation().isDone()) {
                villager.getNavigation().moveTo(target.x + 0.5, target.y, target.z + 0.5, speed);
            }
            return VillagerActionRuntime.Result.running("move_to");
        }

        @Override
        public void stop(MillVillager villager) {
            villager.getNavigation().stop();
        }
    }

    private static final class FacePointAction implements VillagerActionRuntime.Action {
        private final Point target;

        private FacePointAction(Point target) {
            this.target = target;
        }

        @Override
        public String key() {
            return "face_point";
        }

        @Override
        public VillagerActionRuntime.ExecutorType executorType() {
            return VillagerActionRuntime.ExecutorType.VILLAGER_NATIVE;
        }

        @Override
        public VillagerActionRuntime.Result tick(MillVillager villager) {
            villager.getLookControl().setLookAt(target.x + 0.5, target.y + 0.5, target.z + 0.5);
            return VillagerActionRuntime.Result.success("face_point");
        }
    }

    private static final class EquipItemAction implements VillagerActionRuntime.Action {
        private final String itemKey;

        private EquipItemAction(String itemKey) {
            this.itemKey = itemKey;
        }

        @Override
        public String key() {
            return "equip_item";
        }

        @Override
        public VillagerActionRuntime.ExecutorType executorType() {
            return VillagerActionRuntime.ExecutorType.VILLAGER_NATIVE;
        }

        @Override
        public VillagerActionRuntime.Result tick(MillVillager villager) {
            InvItem item = InvItem.get(itemKey);
            if (item == null) {
                villager.setSelectedInventorySlot(-1);
                villager.syncSelectedItemToHands();
                return VillagerActionRuntime.Result.failure("equip_missing_item_key", false);
            }
            for (int slot = 0; slot < villager.getInventoryContainer().getContainerSize(); slot++) {
                ItemStack stack = villager.getInventoryContainer().getItem(slot);
                if (!stack.isEmpty() && stack.getItem() == item.getItem()) {
                    int targetSlot = selectHotbarSlot(villager, slot);
                    if (slot != targetSlot) {
                        ItemStack targetStack = villager.getInventoryContainer().getItem(targetSlot).copy();
                        villager.getInventoryContainer().setItem(targetSlot, stack.copy());
                        villager.getInventoryContainer().setItem(slot, targetStack);
                    }
                    villager.setSelectedInventorySlot(targetSlot);
                    villager.syncSelectedItemToHands();
                    return VillagerActionRuntime.Result.success("equip_item");
                }
            }
            villager.setSelectedInventorySlot(-1);
            villager.syncSelectedItemToHands();
            return VillagerActionRuntime.Result.failure("equip_item_not_found", false);
        }

        private int selectHotbarSlot(MillVillager villager, int foundSlot) {
            if (foundSlot >= 0 && foundSlot <= 8) {
                return foundSlot;
            }
            int selectedSlot = villager.getSelectedInventorySlot();
            if (selectedSlot >= 0 && selectedSlot <= 8) {
                return selectedSlot;
            }
            for (int slot = 0; slot <= 8; slot++) {
                if (villager.getInventoryContainer().getItem(slot).isEmpty()) {
                    return slot;
                }
            }
            return 0;
        }
    }

    private static final class BreakBlockAction implements VillagerActionRuntime.Action {
        private final BlockPos pos;
        private final boolean dropItems;

        private BreakBlockAction(BlockPos pos, boolean dropItems) {
            this.pos = pos.immutable();
            this.dropItems = dropItems;
        }

        @Override
        public String key() {
            return "break_block";
        }

        @Override
        public VillagerActionRuntime.ExecutorType executorType() {
            return VillagerActionRuntime.ExecutorType.VILLAGER_NATIVE;
        }

        @Override
        public VillagerActionRuntime.Result tick(MillVillager villager) {
            if (villager.level().getBlockState(pos).isAir()) {
                return VillagerActionRuntime.Result.success("break_block");
            }
            boolean destroyed = villager.level().destroyBlock(pos, dropItems, villager);
            if (destroyed) {
                villager.swing(InteractionHand.MAIN_HAND);
                villager.syncSelectedItemToHands();
                return VillagerActionRuntime.Result.success("break_block");
            }
            return VillagerActionRuntime.Result.failure("break_block_failed", true);
        }
    }

    private static final class ProxyBreakBlockAction implements VillagerActionRuntime.Action {
        private final BlockPos pos;

        private ProxyBreakBlockAction(BlockPos pos) {
            this.pos = pos.immutable();
        }

        @Override
        public String key() {
            return "break_block";
        }

        @Override
        public VillagerActionRuntime.ExecutorType executorType() {
            return VillagerActionRuntime.ExecutorType.PLAYER_PROXY;
        }

        @Override
        public VillagerActionRuntime.Result tick(MillVillager villager) {
            return VillagerPlayerProxy.breakBlock(villager, pos);
        }
    }

    private static final class PlaceBlockAction implements VillagerActionRuntime.Action {
        private final BlockPos pos;
        private final BlockState blockState;
        private final boolean consumeSelectedItem;

        private PlaceBlockAction(BlockPos pos, BlockState blockState, boolean consumeSelectedItem) {
            this.pos = pos.immutable();
            this.blockState = blockState;
            this.consumeSelectedItem = consumeSelectedItem;
        }

        @Override
        public String key() {
            return "place_block";
        }

        @Override
        public VillagerActionRuntime.ExecutorType executorType() {
            return VillagerActionRuntime.ExecutorType.VILLAGER_NATIVE;
        }

        @Override
        public VillagerActionRuntime.Result tick(MillVillager villager) {
            BlockState existing = villager.level().getBlockState(pos);
            if (!existing.isAir() && !existing.canBeReplaced()) {
                return VillagerActionRuntime.Result.failure("place_block_occupied", false);
            }
            if (!villager.level().setBlockAndUpdate(pos, blockState)) {
                return VillagerActionRuntime.Result.failure("place_block_failed", true);
            }
            if (consumeSelectedItem) {
                consumeSelectedItem(villager, blockState);
            }
            villager.swing(InteractionHand.MAIN_HAND);
            villager.syncSelectedItemToHands();
            return VillagerActionRuntime.Result.success("place_block");
        }

        private void consumeSelectedItem(MillVillager villager, BlockState placedState) {
            int slot = villager.getSelectedInventorySlot();
            if (slot < 0) {
                return;
            }
            ItemStack selected = villager.getInventoryContainer().getItem(slot);
            if (selected.getItem() instanceof BlockItem blockItem && blockItem.getBlock() == placedState.getBlock()) {
                selected.shrink(1);
                villager.getInventoryContainer().setItem(slot, selected.isEmpty() ? ItemStack.EMPTY : selected);
            }
        }
    }

    private static final class UseBlockAction implements VillagerActionRuntime.Action {
        private final BlockPos pos;
        private final Direction face;
        private final InteractionHand hand;

        private UseBlockAction(BlockPos pos, Direction face, InteractionHand hand) {
            this.pos = pos.immutable();
            this.face = face;
            this.hand = hand;
        }

        @Override
        public String key() {
            return "use_block";
        }

        @Override
        public VillagerActionRuntime.ExecutorType executorType() {
            return VillagerActionRuntime.ExecutorType.PLAYER_PROXY;
        }

        @Override
        public VillagerActionRuntime.Result tick(MillVillager villager) {
            return VillagerPlayerProxy.useBlock(villager, pos, face, hand);
        }
    }

    private static final class InteractEntityAction implements VillagerActionRuntime.Action {
        private final int entityId;
        private final InteractionHand hand;

        private InteractEntityAction(int entityId, InteractionHand hand) {
            this.entityId = entityId;
            this.hand = hand;
        }

        @Override
        public String key() {
            return "interact_entity";
        }

        @Override
        public VillagerActionRuntime.ExecutorType executorType() {
            return VillagerActionRuntime.ExecutorType.PLAYER_PROXY;
        }

        @Override
        public VillagerActionRuntime.Result tick(MillVillager villager) {
            Entity entity = villager.level().getEntity(entityId);
            return entity == null
                    ? VillagerActionRuntime.Result.failure("interact_entity_missing", true)
                    : VillagerPlayerProxy.interactEntity(villager, entity, hand);
        }
    }

    private static final class AttackEntityAction implements VillagerActionRuntime.Action {
        private final int entityId;
        private final float damage;

        private AttackEntityAction(int entityId, float damage) {
            this.entityId = entityId;
            this.damage = damage;
        }

        @Override
        public String key() {
            return "attack_entity";
        }

        @Override
        public VillagerActionRuntime.ExecutorType executorType() {
            return VillagerActionRuntime.ExecutorType.VILLAGER_NATIVE;
        }

        @Override
        public VillagerActionRuntime.Result tick(MillVillager villager) {
            Entity entity = villager.level().getEntity(entityId);
            if (entity == null || !entity.isAlive()) {
                return VillagerActionRuntime.Result.success("attack_entity");
            }
            villager.swing(InteractionHand.MAIN_HAND);
            return entity.hurt(villager.damageSources().mobAttack(villager), damage)
                    ? VillagerActionRuntime.Result.success("attack_entity")
                    : VillagerActionRuntime.Result.failure("attack_entity_failed", true);
        }
    }

    private static final class TakeStoredGoodsAction implements VillagerActionRuntime.Action {
        private final Building building;
        private final InvItem item;
        private final int amount;

        private TakeStoredGoodsAction(Building building, InvItem item, int amount) {
            this.building = building;
            this.item = item;
            this.amount = Math.max(0, amount);
        }

        @Override
        public String key() {
            return "take_stored_goods";
        }

        @Override
        public VillagerActionRuntime.ExecutorType executorType() {
            return VillagerActionRuntime.ExecutorType.VILLAGER_NATIVE;
        }

        @Override
        public VillagerActionRuntime.Result tick(MillVillager villager) {
            if (building == null || item == null || amount <= 0) {
                return VillagerActionRuntime.Result.failure("take_stored_goods_invalid", false);
            }
            if (!building.resManager.takeGoods(item, amount)) {
                return VillagerActionRuntime.Result.failure("take_stored_goods_unavailable", false);
            }
            villager.addToInv(item, amount);
            return VillagerActionRuntime.Result.success("take_stored_goods");
        }
    }

    private static final class StoreGoodsAction implements VillagerActionRuntime.Action {
        private final Building building;
        private final InvItem item;
        private final int amount;

        private StoreGoodsAction(Building building, InvItem item, int amount) {
            this.building = building;
            this.item = item;
            this.amount = Math.max(0, amount);
        }

        @Override
        public String key() {
            return "store_goods";
        }

        @Override
        public VillagerActionRuntime.ExecutorType executorType() {
            return VillagerActionRuntime.ExecutorType.VILLAGER_NATIVE;
        }

        @Override
        public VillagerActionRuntime.Result tick(MillVillager villager) {
            if (building == null || item == null || amount <= 0) {
                return VillagerActionRuntime.Result.failure("store_goods_invalid", false);
            }
            int transfer = Math.min(amount, villager.countInv(item));
            if (transfer <= 0) {
                return VillagerActionRuntime.Result.failure("store_goods_missing", false);
            }
            villager.removeFromInv(item, transfer);
            building.resManager.storeGoods(item, transfer);
            villager.syncSelectedItemToHands();
            return VillagerActionRuntime.Result.success("store_goods");
        }
    }

    private static final class StoreAllInventoryAction implements VillagerActionRuntime.Action {
        private final Building building;

        private StoreAllInventoryAction(Building building) {
            this.building = building;
        }

        @Override
        public String key() {
            return "store_all_inventory";
        }

        @Override
        public VillagerActionRuntime.ExecutorType executorType() {
            return VillagerActionRuntime.ExecutorType.VILLAGER_NATIVE;
        }

        @Override
        public VillagerActionRuntime.Result tick(MillVillager villager) {
            if (building == null) {
                return VillagerActionRuntime.Result.failure("store_all_inventory_invalid", false);
            }
            for (Map.Entry<InvItem, Integer> entry : new ArrayList<>(villager.inventory.entrySet())) {
                if (entry.getValue() > 0) {
                    building.resManager.storeGoods(entry.getKey(), entry.getValue());
                }
            }
            villager.inventory.clear();
            villager.setSelectedInventorySlot(-1);
            villager.syncSelectedItemToHands();
            return VillagerActionRuntime.Result.success("store_all_inventory");
        }
    }

    private static final class TransformStoredGoodsAction implements VillagerActionRuntime.Action {
        private final Building building;
        private final Map<InvItem, Integer> inputs;
        private final Map<InvItem, Integer> outputs;

        private TransformStoredGoodsAction(Building building, Map<InvItem, Integer> inputs, Map<InvItem, Integer> outputs) {
            this.building = building;
            this.inputs = copyGoodsMap(inputs);
            this.outputs = copyGoodsMap(outputs);
        }

        @Override
        public String key() {
            return "transform_stored_goods";
        }

        @Override
        public VillagerActionRuntime.ExecutorType executorType() {
            return VillagerActionRuntime.ExecutorType.VILLAGER_NATIVE;
        }

        @Override
        public VillagerActionRuntime.Result tick(MillVillager villager) {
            if (building == null || inputs.isEmpty() || outputs.isEmpty()) {
                return VillagerActionRuntime.Result.failure("transform_stored_goods_invalid", false);
            }
            for (Map.Entry<InvItem, Integer> input : inputs.entrySet()) {
                if (input.getKey() == null || input.getValue() <= 0 || building.resManager.countGoods(input.getKey()) < input.getValue()) {
                    return VillagerActionRuntime.Result.failure("transform_stored_goods_missing", false);
                }
            }
            for (Map.Entry<InvItem, Integer> input : inputs.entrySet()) {
                if (!building.resManager.takeGoods(input.getKey(), input.getValue())) {
                    return VillagerActionRuntime.Result.failure("transform_stored_goods_consume_failed", false);
                }
            }
            for (Map.Entry<InvItem, Integer> output : outputs.entrySet()) {
                building.resManager.storeGoods(output.getKey(), output.getValue());
            }
            return VillagerActionRuntime.Result.success("transform_stored_goods");
        }

        private static Map<InvItem, Integer> copyGoodsMap(@Nullable Map<InvItem, Integer> goods) {
            Map<InvItem, Integer> copy = new LinkedHashMap<>();
            if (goods == null) {
                return copy;
            }
            for (Map.Entry<InvItem, Integer> entry : goods.entrySet()) {
                if (entry.getKey() != null && entry.getValue() != null && entry.getValue() > 0) {
                    copy.put(entry.getKey(), entry.getValue());
                }
            }
            return copy;
        }
    }

    private static boolean isInRange(MillVillager villager, Point target, int completionRange) {
        double distance = villager.distanceToSqr(target.x + 0.5, target.y, target.z + 0.5);
        return distance <= completionRange * completionRange + 1;
    }
}
