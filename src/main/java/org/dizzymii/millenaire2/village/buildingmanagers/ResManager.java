package org.dizzymii.millenaire2.village.buildingmanagers;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.dizzymii.millenaire2.util.Point;
import org.dizzymii.millenaire2.village.Building;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Extended resource manager for building-specific resource operations (farming, trees, etc.).
 * Tracks farming plots, saplings, silk worm blocks, snail soil, and cocoa positions.
 * Ported from org.millenaire.common.village.buildingmanagers.ResManager (Forge 1.12.2).
 */
public class ResManager {

    private final Building building;

    public final List<Point> farmingPlots = new ArrayList<>();
    public final List<Point> saplingPositions = new ArrayList<>();
    public final List<Point> silkwormBlocks = new ArrayList<>();
    public final List<Point> snailSoilBlocks = new ArrayList<>();
    public final List<Point> cacaoSpots = new ArrayList<>();
    public final List<Point> sugarCanePlots = new ArrayList<>();

    private int harvestCooldown = 0;
    private static final int HARVEST_INTERVAL = 100;

    public ResManager(Building building) {
        this.building = building;
    }

    /**
     * Called periodically by the building's slow tick to manage farming resources.
     */
    public void update() {
        if (building.world == null || building.world.isClientSide) return;
        if (!(building.world instanceof ServerLevel level)) return;

        harvestCooldown--;
        if (harvestCooldown > 0) return;
        harvestCooldown = HARVEST_INTERVAL;

        harvestMatureCrops(level);
        checkSaplings(level);
    }

    /**
     * Harvest any mature crops on tracked farming plots.
     */
    private void harvestMatureCrops(ServerLevel level) {
        for (Point plot : farmingPlots) {
            BlockPos pos = plot.toBlockPos().above();
            BlockState state = level.getBlockState(pos);
            Block block = state.getBlock();

            if (block instanceof CropBlock crop) {
                if (crop.isMaxAge(state)) {
                    // Harvest: break and replant
                    level.destroyBlock(pos, true);
                    level.setBlock(pos, crop.defaultBlockState(), 3);
                }
            }
        }
    }

    /**
     * Check sapling positions — if a sapling has grown into a tree, mark the spot as available.
     */
    private void checkSaplings(ServerLevel level) {
        saplingPositions.removeIf(sap -> {
            BlockPos pos = sap.toBlockPos();
            BlockState state = level.getBlockState(pos);
            String desc = state.getBlock().getDescriptionId();
            // If it's no longer a sapling (grew into a log), remove from tracking
            return desc.contains("log") || state.isAir();
        });
    }

    /**
     * Registers a farming plot position for crop management.
     */
    public void addFarmingPlot(Point plot) {
        if (!farmingPlots.contains(plot)) farmingPlots.add(plot);
    }

    /**
     * Registers a sapling position for tree tracking.
     */
    public void addSapling(Point pos) {
        if (!saplingPositions.contains(pos)) saplingPositions.add(pos);
    }

    /**
     * Gets the next farming plot that needs planting (no crop above the soil).
     */
    @Nullable
    public Point getNextUnplantedPlot(ServerLevel level) {
        for (Point plot : farmingPlots) {
            BlockPos above = plot.toBlockPos().above();
            if (level.isEmptyBlock(above)) return plot;
        }
        return null;
    }

    // ========== NBT persistence ==========

    public void save(CompoundTag parent, String prefix) {
        savePointList(parent, prefix + "farms", farmingPlots);
        savePointList(parent, prefix + "saplings", saplingPositions);
        savePointList(parent, prefix + "silkworms", silkwormBlocks);
        savePointList(parent, prefix + "snails", snailSoilBlocks);
        savePointList(parent, prefix + "cacao", cacaoSpots);
        savePointList(parent, prefix + "sugarcane", sugarCanePlots);
    }

    public void load(CompoundTag parent, String prefix) {
        farmingPlots.clear();
        saplingPositions.clear();
        silkwormBlocks.clear();
        snailSoilBlocks.clear();
        cacaoSpots.clear();
        sugarCanePlots.clear();

        loadPointList(parent, prefix + "farms", farmingPlots);
        loadPointList(parent, prefix + "saplings", saplingPositions);
        loadPointList(parent, prefix + "silkworms", silkwormBlocks);
        loadPointList(parent, prefix + "snails", snailSoilBlocks);
        loadPointList(parent, prefix + "cacao", cacaoSpots);
        loadPointList(parent, prefix + "sugarcane", sugarCanePlots);
    }

    private void savePointList(CompoundTag parent, String key, List<Point> points) {
        ListTag list = new ListTag();
        for (Point p : points) {
            CompoundTag t = new CompoundTag();
            p.writeToNBT(t, "p");
            list.add(t);
        }
        parent.put(key, list);
    }

    private void loadPointList(CompoundTag parent, String key, List<Point> points) {
        if (parent.contains(key, Tag.TAG_LIST)) {
            ListTag list = parent.getList(key, Tag.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++) {
                Point p = Point.readFromNBT(list.getCompound(i), "p");
                if (p != null) points.add(p);
            }
        }
    }
}
