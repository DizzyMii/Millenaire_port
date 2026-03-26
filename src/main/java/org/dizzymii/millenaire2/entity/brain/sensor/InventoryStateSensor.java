package org.dizzymii.millenaire2.entity.brain.sensor;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.dizzymii.millenaire2.entity.HumanoidNPC;
import org.dizzymii.millenaire2.entity.brain.ModMemoryTypes;
import org.dizzymii.millenaire2.entity.brain.smartbrain.ExtendedSensor;

import java.util.ArrayList;
import java.util.List;

/**
 * Sensor that evaluates a {@link HumanoidNPC}'s equipment and carried inventory
 * to build a picture of what the NPC is missing or needs to replace.
 *
 * <p>During each scan cycle this sensor:
 * <ol>
 *   <li>Inspects the main-hand item for durability damage.
 *   <li>Inspects the offhand item for durability damage.
 *   <li>Scans all items in {@link HumanoidNPC#getCarriedInventory()} for damaged tools.
 *   <li>Writes the complete need-list to {@link ModMemoryTypes#NEEDED_MATERIALS}.
 * </ol>
 *
 * <p>In the SBL tick loop this sensor runs before behaviours (called from
 * {@link HumanoidNPC#customServerAiStep}), so that
 * {@link org.dizzymii.millenaire2.entity.brain.behaviour.ContextualToolSwapBehavior}
 * always reads an up-to-date snapshot on the same tick.
 *
 * <p>Need identifiers use the format:
 * <ul>
 *   <li>{@code "main_hand_tool:empty"} — main hand slot is empty.
 *   <li>{@code "main_hand_tool:damaged:<item_description_id>"} — main-hand tool is critically worn.
 *   <li>{@code "off_hand_tool:empty"} — offhand slot is empty.
 *   <li>{@code "inventory:damaged:<item_description_id>"} — a carried tool needs replacing.
 * </ul>
 */
public class InventoryStateSensor extends ExtendedSensor<HumanoidNPC> {

    /**
     * Durability fraction below which a tool is considered critically damaged and
     * added to the {@link ModMemoryTypes#NEEDED_MATERIALS} list.
     * A value of {@code 0.25f} means the tool has 25 % durability remaining
     * (75 % lost), and should be replaced.
     */
    private static final float DAMAGE_THRESHOLD = 0.25f;

    // ========== Scan rate ==========

    /**
     * Polls twice per second (every 10 ticks) so that memory stays fresh enough
     * for {@link org.dizzymii.millenaire2.entity.brain.behaviour.ContextualToolSwapBehavior}
     * to react before the next action fires.
     *
     * @param entity the entity being sensed
     * @return scan interval in game ticks
     */
    @Override
    public int getScanRate(HumanoidNPC entity) {
        return 10;
    }

    // ========== Core sense logic ==========

    /**
     * Evaluates the entity's current equipment and writes the resulting need-list
     * into the {@link ModMemoryTypes#NEEDED_MATERIALS} brain memory.
     *
     * <p>An empty list means the NPC is fully equipped with undamaged tools.
     * A non-empty list signals to behaviours that the NPC should seek replacements.
     *
     * @param level  the server-side level (used for potential future world queries)
     * @param entity the {@link HumanoidNPC} being evaluated
     */
    @Override
    protected void doTick(ServerLevel level, HumanoidNPC entity) {
        List<String> needed = new ArrayList<>();

        evaluateHandItem(
                entity.getItemInHand(InteractionHand.MAIN_HAND),
                "main_hand_tool",
                needed);

        evaluateHandItem(
                entity.getItemInHand(InteractionHand.OFF_HAND),
                "off_hand_tool",
                needed);

        scanInventoryForNeeds(entity, needed);

        entity.getBrain().setMemory(ModMemoryTypes.NEEDED_MATERIALS.get(), needed);
    }

    // ========== Private helpers ==========

    /**
     * Checks whether a hand slot is empty or whether its item has critically low
     * durability, appending a need identifier to {@code needed} if so.
     *
     * @param stack   the {@link ItemStack} currently in the hand slot
     * @param slotKey a descriptive prefix used as the need identifier
     *                (e.g. {@code "main_hand_tool"})
     * @param needed  mutable list to append need strings to
     */
    private void evaluateHandItem(ItemStack stack, String slotKey, List<String> needed) {
        if (stack.isEmpty()) {
            needed.add(slotKey + ":empty");
            return;
        }
        if (stack.isDamageableItem()) {
            float durabilityFraction =
                    1.0f - ((float) stack.getDamageValue() / (float) stack.getMaxDamage());
            if (durabilityFraction < DAMAGE_THRESHOLD) {
                needed.add(slotKey + ":damaged:" + stack.getItem().getDescriptionId());
            }
        }
    }

    /**
     * Iterates the NPC's logical inventory and flags any tool whose durability has
     * fallen below {@link #DAMAGE_THRESHOLD}.
     *
     * @param entity the entity whose carried inventory is scanned
     * @param needed mutable list to append need strings to
     */
    private void scanInventoryForNeeds(HumanoidNPC entity, List<String> needed) {
        for (ItemStack stack : entity.getCarriedInventory()) {
            if (stack.isEmpty()) continue;
            if (stack.isDamageableItem()) {
                float durabilityFraction =
                        1.0f - ((float) stack.getDamageValue() / (float) stack.getMaxDamage());
                if (durabilityFraction < DAMAGE_THRESHOLD) {
                    needed.add("inventory:damaged:" + stack.getItem().getDescriptionId());
                }
            }
        }
    }
}
