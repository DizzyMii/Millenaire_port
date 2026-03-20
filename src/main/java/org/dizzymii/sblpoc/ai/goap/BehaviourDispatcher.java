package org.dizzymii.sblpoc.ai.goap;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import org.dizzymii.sblpoc.PocNpc;
import org.dizzymii.sblpoc.ai.world.BlockCategory;
import org.dizzymii.sblpoc.behaviour.survival.*;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * Maps GOAP action names to real behaviour instances that execute them.
 * Each action name from GoapActionRegistry is mapped to a factory that
 * creates and starts the appropriate ExtendedBehaviour.
 *
 * The PlanExecutor calls {@link #startAction} when advancing to a new
 * plan step, and {@link #tickAction} each tick to drive the behaviour.
 */
public class BehaviourDispatcher {

    /**
     * Encapsulates a running behaviour step.
     */
    public interface ActiveStep {
        /** Tick the step. Returns true when complete. */
        boolean tick(ServerLevel level, PocNpc npc, long gameTime);
        /** Called when the step is interrupted or completed. */
        void stop(ServerLevel level, PocNpc npc, long gameTime);
    }

    private static final Map<String, BiFunction<ServerLevel, PocNpc, ActiveStep>> DISPATCH = new HashMap<>();

    static {
        // ===== WOOD =====
        DISPATCH.put("chop_tree", (level, npc) -> timedBehaviour(new ChopTreeBehaviour(), level, npc));

        // ===== BASIC CRAFTING =====
        DISPATCH.put("craft_planks", (level, npc) -> craftAction(npc, Items.OAK_LOG, 1, Items.OAK_PLANKS, 4));
        DISPATCH.put("craft_sticks", (level, npc) -> craftAction(npc, Items.OAK_PLANKS, 2, Items.STICK, 4));
        DISPATCH.put("craft_crafting_table", (level, npc) -> craftAction(npc, Items.OAK_PLANKS, 4, Items.CRAFTING_TABLE, 1));

        // ===== PLACE STATIONS =====
        DISPATCH.put("place_crafting_table", (level, npc) -> placeBlockAction(level, npc, Items.CRAFTING_TABLE));
        DISPATCH.put("place_furnace", (level, npc) -> placeBlockAction(level, npc, Items.FURNACE));

        // ===== TOOL CRAFTING =====
        DISPATCH.put("craft_wooden_pickaxe", (level, npc) -> timedBehaviour(new CraftItemBehaviour(), level, npc));
        DISPATCH.put("craft_wooden_axe", (level, npc) -> timedBehaviour(new CraftItemBehaviour(), level, npc));
        DISPATCH.put("craft_wooden_sword", (level, npc) -> timedBehaviour(new CraftItemBehaviour(), level, npc));
        DISPATCH.put("craft_stone_pickaxe", (level, npc) -> timedBehaviour(new CraftItemBehaviour(), level, npc));
        DISPATCH.put("craft_stone_sword", (level, npc) -> timedBehaviour(new CraftItemBehaviour(), level, npc));
        DISPATCH.put("craft_stone_axe", (level, npc) -> timedBehaviour(new CraftItemBehaviour(), level, npc));
        DISPATCH.put("craft_furnace", (level, npc) -> timedBehaviour(new CraftItemBehaviour(), level, npc));
        DISPATCH.put("craft_iron_pickaxe", (level, npc) -> timedBehaviour(new CraftItemBehaviour(), level, npc));
        DISPATCH.put("craft_iron_sword", (level, npc) -> timedBehaviour(new CraftItemBehaviour(), level, npc));
        DISPATCH.put("craft_shield", (level, npc) -> timedBehaviour(new CraftItemBehaviour(), level, npc));
        DISPATCH.put("craft_diamond_pickaxe", (level, npc) -> timedBehaviour(new CraftItemBehaviour(), level, npc));
        DISPATCH.put("craft_diamond_sword", (level, npc) -> timedBehaviour(new CraftItemBehaviour(), level, npc));
        DISPATCH.put("craft_iron_helmet", (level, npc) -> timedBehaviour(new CraftItemBehaviour(), level, npc));
        DISPATCH.put("craft_iron_chestplate", (level, npc) -> timedBehaviour(new CraftItemBehaviour(), level, npc));
        DISPATCH.put("craft_iron_leggings", (level, npc) -> timedBehaviour(new CraftItemBehaviour(), level, npc));
        DISPATCH.put("craft_iron_boots", (level, npc) -> timedBehaviour(new CraftItemBehaviour(), level, npc));

        // ===== MINING =====
        DISPATCH.put("mine_stone", (level, npc) -> timedBehaviour(new MineBlockBehaviour(), level, npc));
        DISPATCH.put("mine_iron_ore", (level, npc) -> {
            MineOreBehaviour b = new MineOreBehaviour();
            b.oreType(BlockCategory.IRON_ORE);
            return timedBehaviour(b, level, npc);
        });
        DISPATCH.put("mine_coal", (level, npc) -> {
            MineOreBehaviour b = new MineOreBehaviour();
            b.oreType(BlockCategory.COAL_ORE);
            return timedBehaviour(b, level, npc);
        });
        DISPATCH.put("mine_diamond_ore", (level, npc) -> {
            MineOreBehaviour b = new MineOreBehaviour();
            b.oreType(BlockCategory.DIAMOND_ORE);
            return timedBehaviour(b, level, npc);
        });

        // ===== SMELTING =====
        DISPATCH.put("smelt_iron", (level, npc) -> timedBehaviour(new SmeltItemBehaviour(), level, npc));
        DISPATCH.put("cook_food", (level, npc) -> timedBehaviour(new SmeltItemBehaviour(), level, npc));

        // ===== FOOD =====
        DISPATCH.put("hunt_animal", (level, npc) -> timedBehaviour(new HuntAnimalBehaviour(), level, npc));

        // ===== SHELTER =====
        DISPATCH.put("build_shelter", (level, npc) -> timedBehaviour(new BuildShelterBehaviour(), level, npc));

        // ===== EXPLORATION =====
        DISPATCH.put("explore_area", (level, npc) -> timedBehaviour(new ExploreBehaviour(), level, npc));

        // ===== ARMOR =====
        DISPATCH.put("equip_iron_armor", (level, npc) -> instantAction(() -> {
            // Equip all iron armor pieces from inventory
            equipArmorFromInventory(npc, Items.IRON_HELMET, net.minecraft.world.entity.EquipmentSlot.HEAD);
            equipArmorFromInventory(npc, Items.IRON_CHESTPLATE, net.minecraft.world.entity.EquipmentSlot.CHEST);
            equipArmorFromInventory(npc, Items.IRON_LEGGINGS, net.minecraft.world.entity.EquipmentSlot.LEGS);
            equipArmorFromInventory(npc, Items.IRON_BOOTS, net.minecraft.world.entity.EquipmentSlot.FEET);
        }));
    }

    /**
     * Start executing a GOAP action. Returns null if no dispatch exists.
     */
    @Nullable
    public static ActiveStep startAction(String actionName, ServerLevel level, PocNpc npc) {
        BiFunction<ServerLevel, PocNpc, ActiveStep> factory = DISPATCH.get(actionName);
        if (factory == null) return null;
        return factory.apply(level, npc);
    }

    // ========== Step Factories ==========

    /**
     * Wraps an ExtendedBehaviour into an ActiveStep, driving it via Behavior's
     * public API (tryStart / tickOrStop / doStop).
     */
    private static ActiveStep timedBehaviour(
            net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour<PocNpc> behaviour,
            ServerLevel level, PocNpc npc) {
        return new ActiveStep() {
            private boolean started = false;
            private boolean finished = false;

            @Override
            public boolean tick(ServerLevel lvl, PocNpc entity, long gameTime) {
                if (finished) return true;

                if (!started) {
                    // tryStart checks memory requirements + checkExtraStartConditions
                    started = behaviour.tryStart(lvl, entity, gameTime);
                    if (!started) {
                        // Behaviour can't start (preconditions not met) — skip step
                        finished = true;
                        return true;
                    }
                    return false;
                }

                // Drive the behaviour's tick + shouldKeepRunning logic
                behaviour.tickOrStop(lvl, entity, gameTime);

                // If behaviour stopped itself (shouldKeepRunning returned false),
                // mark as complete
                if (behaviour.getStatus() == net.minecraft.world.entity.ai.behavior.Behavior.Status.STOPPED) {
                    finished = true;
                    return true;
                }
                return false;
            }

            @Override
            public void stop(ServerLevel lvl, PocNpc entity, long gameTime) {
                if (started && !finished) {
                    behaviour.doStop(lvl, entity, gameTime);
                }
            }
        };
    }

    /**
     * Instant craft: consume inputs, produce outputs in the NPC inventory.
     */
    private static ActiveStep craftAction(PocNpc npc,
                                           net.minecraft.world.item.Item input, int inputCount,
                                           net.minecraft.world.item.Item output, int outputCount) {
        return instantAction(() -> {
            // Consume input
            int remaining = inputCount;
            for (int i = 0; i < npc.getInventory().getContainerSize() && remaining > 0; i++) {
                var slot = npc.getInventory().getItem(i);
                if (slot.getItem() == input) {
                    int take = Math.min(remaining, slot.getCount());
                    npc.getInventory().removeItem(i, take);
                    remaining -= take;
                }
            }
            // Produce output
            var result = new net.minecraft.world.item.ItemStack(output, outputCount);
            for (int i = 0; i < npc.getInventory().getContainerSize(); i++) {
                var slot = npc.getInventory().getItem(i);
                if (slot.isEmpty()) {
                    npc.getInventory().setItem(i, result);
                    result = net.minecraft.world.item.ItemStack.EMPTY;
                    break;
                }
            }
            if (!result.isEmpty()) {
                npc.spawnAtLocation(result);
            }
            npc.getInventoryModel().markDirty();
        });
    }

    /**
     * Place a block item adjacent to the NPC.
     */
    private static ActiveStep placeBlockAction(ServerLevel level, PocNpc npc,
                                                net.minecraft.world.item.Item blockItem) {
        return instantAction(() -> {
            int slot = npc.getInventoryModel().findSlot(s -> s.getItem() == blockItem);
            if (slot >= 0) {
                npc.getInventory().removeItem(slot, 1);
                BlockPos target = npc.blockPosition().offset(1, 0, 0);
                if (level.getBlockState(target).isAir()) {
                    var block = net.minecraft.world.level.block.Block.byItem(blockItem);
                    if (block != Blocks.AIR) {
                        level.setBlock(target, block.defaultBlockState(), 3);
                        npc.getSpatialMemory().scanAround(level, target);
                    }
                }
                npc.getInventoryModel().markDirty();
            }
        });
    }

    /**
     * An action that completes immediately after running a callback.
     */
    private static ActiveStep instantAction(Runnable action) {
        return new ActiveStep() {
            private boolean done = false;

            @Override
            public boolean tick(ServerLevel level, PocNpc npc, long gameTime) {
                if (!done) {
                    action.run();
                    done = true;
                }
                return true; // Completes immediately
            }

            @Override
            public void stop(ServerLevel level, PocNpc npc, long gameTime) {
                // Nothing to clean up
            }
        };
    }

    private static void equipArmorFromInventory(PocNpc npc, net.minecraft.world.item.Item armorItem,
                                                 net.minecraft.world.entity.EquipmentSlot slot) {
        int invSlot = npc.getInventoryModel().findSlot(s -> s.getItem() == armorItem);
        if (invSlot >= 0) {
            var current = npc.getItemBySlot(slot);
            npc.setItemSlot(slot, npc.getInventory().getItem(invSlot));
            npc.getInventory().setItem(invSlot, current);
        }
    }
}
