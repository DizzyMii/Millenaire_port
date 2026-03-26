package org.dizzymii.millenaire2.entity.brain.behaviour;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SwordItem;
import org.dizzymii.millenaire2.entity.HumanoidNPC;
import org.dizzymii.millenaire2.entity.brain.ModMemoryTypes;
import org.dizzymii.millenaire2.entity.brain.smartbrain.ExtendedBehaviour;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * Behaviour that selects the best tool from the NPC's carried inventory for the
 * current macro-objective and swaps it into the main hand.
 *
 * <p>This behaviour belongs to the WORK (acquisition) activity and fires before
 * any block-breaking or attacking action so the NPC is always wielding the
 * correct tool without manual intervention.
 *
 * <p>In the SBL tick loop:
 * <ol>
 *   <li>{@link #checkExtraStartConditions} — evaluates whether the main hand
 *       is already optimal for the active objective; returns {@code true} only
 *       when a swap is actually needed.
 *   <li>{@link #start} — performs the one-shot swap and transitions back to
 *       {@code STOPPED} on the same tick (no keep-running logic required).
 * </ol>
 *
 * <p>Objective → tool-category mapping:
 * <table>
 *   <tr><th>Objective keyword</th><th>Tool class</th></tr>
 *   <tr><td>wood / lumber</td><td>{@link AxeItem}</td></tr>
 *   <tr><td>stone / mine</td><td>{@link PickaxeItem}</td></tr>
 *   <tr><td>defend / attack / fight</td><td>{@link SwordItem}</td></tr>
 *   <tr><td>farm / harvest</td><td>{@link HoeItem}</td></tr>
 *   <tr><td>dig / excavat</td><td>{@link ShovelItem}</td></tr>
 * </table>
 *
 * <p>Actual pathfinding and block-breaking logic are intentionally out of scope
 * for this pass; only the memory, sensor, and tool-swapping architecture is
 * implemented here.
 */
public class ContextualToolSwapBehavior extends ExtendedBehaviour<HumanoidNPC> {

    // ========== Entry conditions ==========

    /**
     * Declares the memory preconditions that must be satisfied before this
     * behaviour is even considered for start.
     *
     * <p>Requires {@link ModMemoryTypes#MACRO_OBJECTIVE} to be present so the
     * behaviour knows which tool category to seek.
     *
     * @return map of required memory states
     */
    @Override
    protected Map<MemoryModuleType<?>, MemoryStatus> entryConditions() {
        return Map.of(ModMemoryTypes.MACRO_OBJECTIVE.get(), MemoryStatus.VALUE_PRESENT);
    }

    // ========== Start conditions ==========

    /**
     * Returns {@code true} when the NPC's main hand does not already hold an
     * optimal tool for the active {@link ModMemoryTypes#MACRO_OBJECTIVE}.
     *
     * <p>This prevents no-op swaps and keeps the behaviour inactive during ticks
     * where the NPC is already correctly equipped.
     *
     * @param level  the server-side level
     * @param entity the {@link HumanoidNPC} being evaluated
     * @return {@code true} if the main hand should be changed this tick
     */
    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, HumanoidNPC entity) {
        if (entity.getBrain().getMemory(ModMemoryTypes.NEEDS_HEALING.get()).orElse(false)) {
            return false;
        }
        if (entity.getBrain().getMemory(ModMemoryTypes.LAST_KNOWN_DANGER.get()).isPresent()) {
            return false;
        }

        String objective = entity.getBrain()
                .getMemory(ModMemoryTypes.MACRO_OBJECTIVE.get())
                .orElse("");
        if (objective.isEmpty()) return false;

        ItemStack currentMainHand = entity.getItemInHand(InteractionHand.MAIN_HAND);
        return !isOptimalTool(currentMainHand, objective);
    }

    // ========== Behaviour execution ==========

    /**
     * Performs the tool swap: iterates the NPC's carried inventory, finds the
     * first {@link ItemStack} appropriate for the active objective, and places a
     * copy of it into the main hand.
     *
     * <p>If no matching tool is found in the inventory the main hand is left
     * unchanged. This is a one-shot behaviour — it does not keep running.
     *
     * @param level  the server-side level
     * @param entity the {@link HumanoidNPC} performing the swap
     */
    @Override
    protected void start(ServerLevel level, HumanoidNPC entity) {
        String objective = entity.getBrain()
                .getMemory(ModMemoryTypes.MACRO_OBJECTIVE.get())
                .orElse("");
        if (objective.isEmpty()) return;

        List<ItemStack> inventory = entity.getCarriedInventory();
        ItemStack bestTool = findBestToolForObjective(inventory, objective);
        if (bestTool != null && !bestTool.isEmpty()) {
            entity.setItemInHand(InteractionHand.MAIN_HAND, bestTool.copy());
        }
    }

    // ========== Private helpers ==========

    /**
     * Returns {@code true} when {@code stack} is already an appropriate tool for
     * {@code objective}, avoiding a redundant swap.
     *
     * @param stack     the item currently in the main hand
     * @param objective the active macro-objective string from memory
     * @return {@code true} if no swap is needed
     */
    private boolean isOptimalTool(ItemStack stack, String objective) {
        if (stack.isEmpty()) return false;
        return switch (resolveObjectiveCategory(objective)) {
            case "wood"   -> stack.getItem() instanceof AxeItem;
            case "stone"  -> stack.getItem() instanceof PickaxeItem;
            case "combat" -> stack.getItem() instanceof SwordItem;
            case "farm"   -> stack.getItem() instanceof HoeItem;
            case "dig"    -> stack.getItem() instanceof ShovelItem;
            default       -> false;
        };
    }

    /**
     * Scans the given inventory list and returns the first {@link ItemStack} that
     * matches the tool category derived from {@code objective}.
     *
     * @param inventory the NPC's carried items (from
     *                  {@link HumanoidNPC#getCarriedInventory()})
     * @param objective the active macro-objective string from memory
     * @return the best matching tool, or {@code null} if none found
     */
    @Nullable
    private ItemStack findBestToolForObjective(List<ItemStack> inventory, String objective) {
        String category = resolveObjectiveCategory(objective);
        for (ItemStack stack : inventory) {
            if (stack.isEmpty()) continue;
            boolean match = switch (category) {
                case "wood"   -> stack.getItem() instanceof AxeItem;
                case "stone"  -> stack.getItem() instanceof PickaxeItem;
                case "combat" -> stack.getItem() instanceof SwordItem;
                case "farm"   -> stack.getItem() instanceof HoeItem;
                case "dig"    -> stack.getItem() instanceof ShovelItem;
                default       -> false;
            };
            if (match) return stack;
        }
        return null;
    }

    /**
     * Maps a raw objective string to a simplified tool-category token used by the
     * switch expressions in {@link #isOptimalTool} and
     * {@link #findBestToolForObjective}.
     *
     * @param objective the {@link ModMemoryTypes#MACRO_OBJECTIVE} value from memory
     * @return one of {@code "wood"}, {@code "stone"}, {@code "combat"},
     *         {@code "farm"}, {@code "dig"}, or {@code "none"}
     */
    private String resolveObjectiveCategory(String objective) {
        if (objective.contains("wood")    || objective.contains("lumber"))   return "wood";
        if (objective.contains("stone")   || objective.contains("mine"))     return "stone";
        if (objective.contains("defend")  || objective.contains("attack")
                                          || objective.contains("fight"))    return "combat";
        if (objective.contains("farm")    || objective.contains("harvest"))  return "farm";
        if (objective.contains("dig")     || objective.contains("excavat"))  return "dig";
        return "none";
    }
}
