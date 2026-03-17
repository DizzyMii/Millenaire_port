package org.dizzymii.millenaire2.world;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.HashSet;
import java.util.Random;

/**
 * Handles village generation in the world — placing town halls, hamlets, lone buildings.
 * In 1.21.1 NeoForge this will need to integrate with the new worldgen system
 * instead of the old IWorldGenerator interface.
 * Ported from org.millenaire.common.world.WorldGenVillage (Forge 1.12.2).
 */
public class WorldGenVillage {

    private static final int HAMLET_ATTEMPT_ANGLE_STEPS = 36;
    private static final int CHUNK_DISTANCE_LOAD_TEST = 8;
    private static final int HAMLET_MAX_DISTANCE = 350;
    private static final int HAMLET_MIN_DISTANCE = 250;
    private static final double MINIMUM_USABLE_BLOCK_PERC = 0.7;

    private static HashSet<Integer> chunkCoordsTried = new HashSet<>();

    // TODO: Implement village generation using NeoForge 1.21.1 worldgen APIs
    // TODO: Port generateBedrockLoneBuilding, generateNewVillage, generateHamlet
    // TODO: Port spawn radius protection check
}
