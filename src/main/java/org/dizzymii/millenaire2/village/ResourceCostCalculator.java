package org.dizzymii.millenaire2.village;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.dizzymii.millenaire2.buildingplan.BuildingBlock;
import org.dizzymii.millenaire2.item.InvItem;
import org.dizzymii.millenaire2.util.MillLog;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Computes resource costs for building construction from a list of BuildingBlocks.
 * Maps BlockStates to InvItem resource categories (planks, stone, etc.)
 * Ported from BuildingPlan.computeCost() in the original Millenaire.
 */
public class ResourceCostCalculator {

    private static final String RES_WOOD = "wood";
    private static final String RES_STONE = "stone";
    private static final String RES_SAND = "sand";
    private static final String RES_GLASS = "glass";
    private static final String RES_WOOL = "wool";
    private static final String RES_CLAY = "clay";
    private static final String RES_IRON = "iron";
    private static final String RES_GOLD = "gold";
    private static final String RES_REDSTONE = "redstone";

    /**
     * Compute resource costs from a list of building blocks.
     * Returns a map of resource key -> required count.
     */
    public static Map<String, Integer> computeCost(List<BuildingBlock> blocks) {
        Map<String, Integer> costs = new HashMap<>();

        for (BuildingBlock bb : blocks) {
            if (bb.blockState == null) continue;
            if (bb.blockState.isAir()) continue;

            String resKey = classifyBlock(bb.blockState);
            if (resKey == null) continue;

            // Slabs cost half
            int amount = 1;
            if (bb.blockState.getBlock() instanceof SlabBlock) {
                // Accumulate in half-units; will round up at end
                costs.merge(resKey + "_half", 1, Integer::sum);
                continue;
            }

            costs.merge(resKey, amount, Integer::sum);
        }

        // Convert half-slab counts to full blocks (round up)
        Map<String, Integer> finalCosts = new HashMap<>();
        for (Map.Entry<String, Integer> entry : costs.entrySet()) {
            String key = entry.getKey();
            int count = entry.getValue();
            if (key.endsWith("_half")) {
                String baseKey = key.substring(0, key.length() - 5);
                finalCosts.merge(baseKey, (count + 1) / 2, Integer::sum);
            } else {
                finalCosts.merge(key, count, Integer::sum);
            }
        }

        return finalCosts;
    }

    /**
     * Check if a building's townhall has enough resources for construction.
     * @param townhall the townhall Building whose resManager holds village resources
     * @param costs the required resource costs (from computeCost)
     * @return true if all resources are available
     */
    public static boolean hasResources(Building townhall, Map<String, Integer> costs) {
        for (Map.Entry<String, Integer> entry : costs.entrySet()) {
            InvItem item = resolveResourceItem(entry.getKey());
            if (item == null) continue; // Unknown resource, skip
            int required = entry.getValue();
            int available = townhall.resManager.countGoods(item);
            if (available < required) {
                return false;
            }
        }
        return true;
    }

    /**
     * Deduct resource costs from the townhall's resource manager.
     * Should only be called after hasResources() returns true.
     */
    public static void deductResources(Building townhall, Map<String, Integer> costs) {
        for (Map.Entry<String, Integer> entry : costs.entrySet()) {
            InvItem item = resolveResourceItem(entry.getKey());
            if (item == null) continue;
            townhall.resManager.takeGoods(item, entry.getValue());
        }
    }

    /**
     * Classify a BlockState into a resource category key.
     */
    @Nullable
    private static String classifyBlock(BlockState state) {
        Block block = state.getBlock();

        // Wood family
        if (state.is(BlockTags.PLANKS) || state.is(BlockTags.LOGS)
                || state.is(BlockTags.WOODEN_STAIRS) || state.is(BlockTags.WOODEN_SLABS)
                || state.is(BlockTags.WOODEN_FENCES) || state.is(BlockTags.WOODEN_DOORS)
                || state.is(BlockTags.WOODEN_TRAPDOORS) || state.is(BlockTags.WOODEN_PRESSURE_PLATES)
                || state.is(BlockTags.WOODEN_BUTTONS) || state.is(BlockTags.FENCE_GATES)
                || state.is(BlockTags.SIGNS) || state.is(BlockTags.STANDING_SIGNS)
                || state.is(BlockTags.WALL_SIGNS)) {
            return RES_WOOD;
        }

        // Stone family
        if (block == Blocks.COBBLESTONE || block == Blocks.STONE || block == Blocks.STONE_BRICKS
                || block == Blocks.SMOOTH_STONE || block == Blocks.MOSSY_COBBLESTONE
                || block == Blocks.MOSSY_STONE_BRICKS || block == Blocks.CHISELED_STONE_BRICKS
                || block == Blocks.CRACKED_STONE_BRICKS || block == Blocks.ANDESITE
                || block == Blocks.GRANITE || block == Blocks.DIORITE
                || block == Blocks.POLISHED_ANDESITE || block == Blocks.POLISHED_GRANITE
                || block == Blocks.POLISHED_DIORITE || block == Blocks.COBBLESTONE_WALL
                || block == Blocks.STONE_BRICK_WALL || block == Blocks.STONE_STAIRS
                || block == Blocks.COBBLESTONE_STAIRS || block == Blocks.STONE_BRICK_STAIRS
                || block == Blocks.STONE_SLAB || block == Blocks.COBBLESTONE_SLAB
                || block == Blocks.STONE_BRICK_SLAB || block == Blocks.FURNACE
                || block == Blocks.STONECUTTER || block == Blocks.GRINDSTONE) {
            return RES_STONE;
        }

        // Bricks
        if (block == Blocks.BRICKS || block == Blocks.BRICK_STAIRS
                || block == Blocks.BRICK_SLAB || block == Blocks.BRICK_WALL) {
            return RES_CLAY;
        }

        // Sand / sandstone family
        if (block == Blocks.SANDSTONE || block == Blocks.SMOOTH_SANDSTONE
                || block == Blocks.CHISELED_SANDSTONE || block == Blocks.CUT_SANDSTONE
                || block == Blocks.RED_SANDSTONE || block == Blocks.SMOOTH_RED_SANDSTONE
                || block == Blocks.SANDSTONE_STAIRS || block == Blocks.SANDSTONE_SLAB
                || block == Blocks.SANDSTONE_WALL) {
            return RES_SAND;
        }

        // Glass
        if (block == Blocks.GLASS || block == Blocks.GLASS_PANE
                || block.getDescriptionId().contains("stained_glass")) {
            return RES_GLASS;
        }

        // Wool
        if (state.is(BlockTags.WOOL) || state.is(BlockTags.WOOL_CARPETS)
                || state.is(BlockTags.BEDS) || state.is(BlockTags.BANNERS)) {
            return RES_WOOL;
        }

        // Iron
        if (block == Blocks.IRON_BLOCK || block == Blocks.IRON_BARS
                || block == Blocks.IRON_DOOR || block == Blocks.IRON_TRAPDOOR
                || block == Blocks.ANVIL || block == Blocks.CHAIN
                || block == Blocks.LANTERN || block == Blocks.SOUL_LANTERN) {
            return RES_IRON;
        }

        // Gold
        if (block == Blocks.GOLD_BLOCK) {
            return RES_GOLD;
        }

        // Redstone
        if (block == Blocks.REDSTONE_BLOCK || block == Blocks.REDSTONE_LAMP
                || block == Blocks.REPEATER || block == Blocks.COMPARATOR) {
            return RES_REDSTONE;
        }

        // Millenaire custom blocks — classify by name
        String blockId = block.getDescriptionId();
        if (blockId.contains("painted_brick") || blockId.contains("byzantine")) {
            return RES_CLAY;
        }
        if (blockId.contains("decorative_stone") || blockId.contains("mill_stone")) {
            return RES_STONE;
        }
        if (blockId.contains("decorative_wood") || blockId.contains("timber_frame")
                || blockId.contains("paper_wall") || blockId.contains("thatch")) {
            return RES_WOOD;
        }

        // Stone stairs/slabs not caught above (generic)
        if (block instanceof StairBlock || block instanceof SlabBlock) {
            return RES_STONE;
        }

        // Skip air, water, lava, torches, etc. — they're free
        return null;
    }

    /**
     * Resolve a resource category key to an InvItem.
     * Falls back to vanilla item IDs if no InvItem is registered.
     */
    @Nullable
    private static InvItem resolveResourceItem(String resourceKey) {
        // Try the Millenaire InvItem registry first
        InvItem item = InvItem.get(resourceKey);
        if (item != null) return item;

        // Fall back to well-known vanilla items
        String vanillaId = switch (resourceKey) {
            case RES_WOOD -> "minecraft:oak_planks";
            case RES_STONE -> "minecraft:cobblestone";
            case RES_SAND -> "minecraft:sandstone";
            case RES_GLASS -> "minecraft:glass";
            case RES_WOOL -> "minecraft:white_wool";
            case RES_CLAY -> "minecraft:brick";
            case RES_IRON -> "minecraft:iron_ingot";
            case RES_GOLD -> "minecraft:gold_ingot";
            case RES_REDSTONE -> "minecraft:redstone";
            default -> null;
        };

        if (vanillaId != null) {
            // Register for future lookups
            item = InvItem.registerDirect(resourceKey, vanillaId);
            MillLog.minor("ResourceCost", "Auto-registered resource: " + resourceKey + " -> " + vanillaId);
            return item;
        }

        return null;
    }
}
