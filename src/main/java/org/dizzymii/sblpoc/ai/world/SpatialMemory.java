package org.dizzymii.sblpoc.ai.world;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Persistent spatial memory for the NPC. Remembers block locations,
 * points of interest, danger zones, and explored area.
 *
 * Capped at MAX_ENTRIES with LRU eviction by distance from home.
 */
public class SpatialMemory {

    private static final int MAX_BLOCK_ENTRIES = 50_000;
    private static final int SCAN_RADIUS = 8;

    // ========== Block Memory ==========

    /** Known block positions by category for fast lookup. */
    private final Map<BlockCategory, Set<BlockPos>> blocksByCategory = new EnumMap<>(BlockCategory.class);

    /** All remembered positions (for eviction). Oldest entries first. */
    private final LinkedHashMap<BlockPos, BlockCategory> allBlocks = new LinkedHashMap<>(256, 0.75f, true);

    // ========== Points of Interest ==========

    private final Map<POIType, BlockPos> pointsOfInterest = new EnumMap<>(POIType.class);

    // ========== Exploration ==========

    /** Set of chunk coordinates (packed as long) that have been scanned. */
    private final Set<Long> exploredChunks = new HashSet<>();

    // ========== Danger Zones ==========

    private final Set<BlockPos> dangerZones = new HashSet<>();

    public SpatialMemory() {
        for (BlockCategory cat : BlockCategory.values()) {
            blocksByCategory.put(cat, new LinkedHashSet<>());
        }
    }

    // ========== Scanning ==========

    /**
     * Scan blocks around the NPC and update the memory map.
     * Called by BlockScanSensor every ~40 ticks.
     */
    public void scanAround(Level level, BlockPos center) {
        long chunkKey = chunkKey(center.getX() >> 4, center.getZ() >> 4);
        exploredChunks.add(chunkKey);

        for (int dx = -SCAN_RADIUS; dx <= SCAN_RADIUS; dx++) {
            for (int dy = -SCAN_RADIUS; dy <= SCAN_RADIUS; dy++) {
                for (int dz = -SCAN_RADIUS; dz <= SCAN_RADIUS; dz++) {
                    BlockPos pos = center.offset(dx, dy, dz);
                    BlockState state = level.getBlockState(pos);
                    BlockCategory category = categorize(state);
                    if (category != null) {
                        remember(pos, category);
                    }
                }
            }
        }
    }

    /**
     * Remember a block at a position. Replaces any previous entry at that pos.
     */
    public void remember(BlockPos pos, BlockCategory category) {
        BlockPos immutable = pos.immutable();

        // Remove old entry if exists
        BlockCategory old = allBlocks.remove(immutable);
        if (old != null) {
            blocksByCategory.get(old).remove(immutable);
        }

        // Add new
        allBlocks.put(immutable, category);
        blocksByCategory.get(category).add(immutable);

        // Evict if over capacity
        if (allBlocks.size() > MAX_BLOCK_ENTRIES) {
            Iterator<Map.Entry<BlockPos, BlockCategory>> it = allBlocks.entrySet().iterator();
            if (it.hasNext()) {
                Map.Entry<BlockPos, BlockCategory> oldest = it.next();
                it.remove();
                blocksByCategory.get(oldest.getValue()).remove(oldest.getKey());
            }
        }
    }

    /**
     * Forget a block position (e.g., after mining it).
     */
    public void forget(BlockPos pos) {
        BlockCategory old = allBlocks.remove(pos);
        if (old != null) {
            blocksByCategory.get(old).remove(pos);
        }
    }

    // ========== Queries ==========

    /**
     * Find the nearest remembered block of a given category.
     */
    @Nullable
    public BlockPos findNearest(BlockCategory category, BlockPos from) {
        Set<BlockPos> positions = blocksByCategory.get(category);
        if (positions == null || positions.isEmpty()) return null;

        BlockPos nearest = null;
        double nearestDistSq = Double.MAX_VALUE;
        for (BlockPos pos : positions) {
            double distSq = from.distSqr(pos);
            if (distSq < nearestDistSq) {
                nearestDistSq = distSq;
                nearest = pos;
            }
        }
        return nearest;
    }

    /**
     * Find the N nearest blocks of a given category.
     */
    public List<BlockPos> findNNearest(BlockCategory category, BlockPos from, int n) {
        Set<BlockPos> positions = blocksByCategory.get(category);
        if (positions == null || positions.isEmpty()) return Collections.emptyList();

        List<BlockPos> sorted = new ArrayList<>(positions);
        sorted.sort(Comparator.comparingDouble(from::distSqr));
        return sorted.subList(0, Math.min(n, sorted.size()));
    }

    /**
     * Check if we know about any block of the given category.
     */
    public boolean knows(BlockCategory category) {
        Set<BlockPos> positions = blocksByCategory.get(category);
        return positions != null && !positions.isEmpty();
    }

    /**
     * Count known blocks of a category.
     */
    public int count(BlockCategory category) {
        Set<BlockPos> positions = blocksByCategory.get(category);
        return positions != null ? positions.size() : 0;
    }

    // ========== POI Management ==========

    public void setPOI(POIType type, BlockPos pos) {
        pointsOfInterest.put(type, pos.immutable());
    }

    @Nullable
    public BlockPos getPOI(POIType type) {
        return pointsOfInterest.get(type);
    }

    public boolean hasPOI(POIType type) {
        return pointsOfInterest.containsKey(type);
    }

    public void clearPOI(POIType type) {
        pointsOfInterest.remove(type);
    }

    // ========== Danger Zones ==========

    public void markDanger(BlockPos pos) {
        dangerZones.add(pos.immutable());
    }

    public boolean isDangerous(BlockPos pos) {
        // Check within 2 blocks of any danger zone
        for (BlockPos danger : dangerZones) {
            if (danger.distSqr(pos) < 9.0) return true;
        }
        return false;
    }

    // ========== Exploration ==========

    public boolean isChunkExplored(int chunkX, int chunkZ) {
        return exploredChunks.contains(chunkKey(chunkX, chunkZ));
    }

    public int getExploredChunkCount() {
        return exploredChunks.size();
    }

    /**
     * Find an unexplored chunk direction relative to center.
     * Returns the BlockPos at the center of the nearest unexplored chunk.
     */
    @Nullable
    public BlockPos findUnexploredDirection(BlockPos center) {
        int cx = center.getX() >> 4;
        int cz = center.getZ() >> 4;

        // Search in expanding rings
        for (int ring = 1; ring <= 8; ring++) {
            for (int dx = -ring; dx <= ring; dx++) {
                for (int dz = -ring; dz <= ring; dz++) {
                    if (Math.abs(dx) != ring && Math.abs(dz) != ring) continue; // Only ring edges
                    if (!isChunkExplored(cx + dx, cz + dz)) {
                        return new BlockPos((cx + dx) * 16 + 8, center.getY(), (cz + dz) * 16 + 8);
                    }
                }
            }
        }
        return null;
    }

    // ========== Block Categorization ==========

    @Nullable
    private static BlockCategory categorize(BlockState state) {
        Block block = state.getBlock();

        // Ores
        if (block == Blocks.COAL_ORE || block == Blocks.DEEPSLATE_COAL_ORE) return BlockCategory.COAL_ORE;
        if (block == Blocks.IRON_ORE || block == Blocks.DEEPSLATE_IRON_ORE) return BlockCategory.IRON_ORE;
        if (block == Blocks.GOLD_ORE || block == Blocks.DEEPSLATE_GOLD_ORE) return BlockCategory.GOLD_ORE;
        if (block == Blocks.DIAMOND_ORE || block == Blocks.DEEPSLATE_DIAMOND_ORE) return BlockCategory.DIAMOND_ORE;
        if (block == Blocks.EMERALD_ORE || block == Blocks.DEEPSLATE_EMERALD_ORE) return BlockCategory.EMERALD_ORE;
        if (block == Blocks.LAPIS_ORE || block == Blocks.DEEPSLATE_LAPIS_ORE) return BlockCategory.LAPIS_ORE;
        if (block == Blocks.REDSTONE_ORE || block == Blocks.DEEPSLATE_REDSTONE_ORE) return BlockCategory.REDSTONE_ORE;
        if (block == Blocks.ANCIENT_DEBRIS) return BlockCategory.ANCIENT_DEBRIS;

        // Trees (only remember logs, not leaves)
        if (state.is(net.minecraft.tags.BlockTags.LOGS)) return BlockCategory.LOG;

        // Crafting stations
        if (block == Blocks.CRAFTING_TABLE) return BlockCategory.CRAFTING_TABLE;
        if (block == Blocks.FURNACE || block == Blocks.BLAST_FURNACE || block == Blocks.SMOKER) return BlockCategory.FURNACE;
        if (block == Blocks.ANVIL || block == Blocks.CHIPPED_ANVIL || block == Blocks.DAMAGED_ANVIL) return BlockCategory.ANVIL;
        if (block == Blocks.ENCHANTING_TABLE) return BlockCategory.ENCHANTING_TABLE;
        if (block == Blocks.BREWING_STAND) return BlockCategory.BREWING_STAND;
        if (block == Blocks.SMITHING_TABLE) return BlockCategory.SMITHING_TABLE;

        // Storage
        if (block == Blocks.CHEST || block == Blocks.TRAPPED_CHEST || block == Blocks.BARREL) return BlockCategory.CHEST;

        // Water/Lava
        if (block == Blocks.WATER) return BlockCategory.WATER;
        if (block == Blocks.LAVA) return BlockCategory.LAVA;

        // Bed
        if (state.is(net.minecraft.tags.BlockTags.BEDS)) return BlockCategory.BED;

        // Crops
        if (block instanceof CropBlock) return BlockCategory.CROP;

        // Animals are handled by entity sensors, not block scan

        return null; // Not interesting enough to remember
    }

    // ========== NBT Persistence ==========

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();

        // Save block entries (limit to 10k most recent for NBT size)
        ListTag blockList = new ListTag();
        int saved = 0;
        for (Map.Entry<BlockPos, BlockCategory> entry : allBlocks.entrySet()) {
            if (saved >= 10_000) break;
            CompoundTag blockTag = new CompoundTag();
            blockTag.putInt("x", entry.getKey().getX());
            blockTag.putInt("y", entry.getKey().getY());
            blockTag.putInt("z", entry.getKey().getZ());
            blockTag.putByte("c", (byte) entry.getValue().ordinal());
            blockList.add(blockTag);
            saved++;
        }
        tag.put("blocks", blockList);

        // Save POIs
        CompoundTag poiTag = new CompoundTag();
        for (Map.Entry<POIType, BlockPos> entry : pointsOfInterest.entrySet()) {
            CompoundTag posTag = new CompoundTag();
            posTag.putInt("x", entry.getValue().getX());
            posTag.putInt("y", entry.getValue().getY());
            posTag.putInt("z", entry.getValue().getZ());
            poiTag.put(entry.getKey().name(), posTag);
        }
        tag.put("pois", poiTag);

        // Save explored chunks (packed as long array)
        tag.putLongArray("explored", exploredChunks.stream().mapToLong(Long::longValue).toArray());

        return tag;
    }

    public void load(CompoundTag tag) {
        allBlocks.clear();
        for (BlockCategory cat : BlockCategory.values()) {
            blocksByCategory.get(cat).clear();
        }
        pointsOfInterest.clear();
        exploredChunks.clear();
        dangerZones.clear();

        // Load blocks
        ListTag blockList = tag.getList("blocks", Tag.TAG_COMPOUND);
        for (int i = 0; i < blockList.size(); i++) {
            CompoundTag blockTag = blockList.getCompound(i);
            BlockPos pos = new BlockPos(blockTag.getInt("x"), blockTag.getInt("y"), blockTag.getInt("z"));
            int ordinal = blockTag.getByte("c") & 0xFF;
            if (ordinal < BlockCategory.values().length) {
                BlockCategory cat = BlockCategory.values()[ordinal];
                allBlocks.put(pos, cat);
                blocksByCategory.get(cat).add(pos);
            }
        }

        // Load POIs
        if (tag.contains("pois")) {
            CompoundTag poiTag = tag.getCompound("pois");
            for (POIType type : POIType.values()) {
                if (poiTag.contains(type.name())) {
                    CompoundTag posTag = poiTag.getCompound(type.name());
                    pointsOfInterest.put(type, new BlockPos(posTag.getInt("x"), posTag.getInt("y"), posTag.getInt("z")));
                }
            }
        }

        // Load explored chunks
        if (tag.contains("explored")) {
            for (long packed : tag.getLongArray("explored")) {
                exploredChunks.add(packed);
            }
        }
    }

    // ========== Utilities ==========

    private static long chunkKey(int chunkX, int chunkZ) {
        return ((long) chunkX << 32) | (chunkZ & 0xFFFFFFFFL);
    }
}
