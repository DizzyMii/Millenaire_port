/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockBush
 *  net.minecraft.block.BlockCrops
 *  net.minecraft.block.BlockDoor
 *  net.minecraft.block.BlockFarmland
 *  net.minecraft.block.BlockFlowerPot$EnumFlowerType
 *  net.minecraft.block.BlockLiquid
 *  net.minecraft.block.BlockSlab
 *  net.minecraft.block.BlockStairs
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.BlockPos
 *  net.minecraftforge.fluids.IFluidBlock
 */
package org.millenaire.common.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.BlockFlowerPot;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.IFluidBlock;
import org.millenaire.common.block.BlockMillCrops;
import org.millenaire.common.block.BlockPathSlab;
import org.millenaire.common.block.IBlockPath;
import org.millenaire.common.block.MillBlocks;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.network.ServerSender;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.utilities.VillageUtilities;
import org.millenaire.common.village.Building;
import org.millenaire.common.village.BuildingLocation;
import org.millenaire.common.world.MillWorldData;
import org.millenaire.common.world.UserProfile;

public class BlockItemUtilities {
    private static final Map<Material, String> MATERIAL_NAME_MAP = BlockItemUtilities.createMaterialNameMap();
    private static final Set<String> FORBIDDEN_MATERIALS = new HashSet<String>(20);
    private static final Set<String> FORBIDDEN_BLOCKS = new HashSet<String>(20);
    private static final Set<String> FORBIDDEN_EXCEPTIONS = new HashSet<String>(20);
    private static final Set<String> GROUND_MATERIALS = new HashSet<String>(20);
    private static final Set<String> GROUND_BLOCKS = new HashSet<String>(20);
    private static final Set<String> GROUND_EXCEPTIONS = new HashSet<String>(20);
    private static final Set<String> DANGER_MATERIALS = new HashSet<String>(20);
    private static final Set<String> DANGER_BLOCKS = new HashSet<String>(20);
    private static final Set<String> DANGER_EXCEPTIONS = new HashSet<String>(20);
    private static final Set<String> WATER_MATERIALS = new HashSet<String>(20);
    private static final Set<String> WATER_BLOCKS = new HashSet<String>(20);
    private static final Set<String> WATER_EXCEPTIONS = new HashSet<String>(20);
    private static final Set<String> PATH_REPLACEABLE_MATERIALS = new HashSet<String>(20);
    private static final Set<String> PATH_REPLACEABLE_BLOCKS = new HashSet<String>(20);
    private static final Set<String> PATH_REPLACEABLE_EXCEPTIONS = new HashSet<String>(20);

    public static void checkForHarvestTheft(EntityPlayer player, BlockPos pos) {
        Building building;
        BuildingLocation location;
        Point actionPos;
        MillWorldData mwd = Mill.getMillWorld(player.field_70170_p);
        Building closestVillageTH = mwd.getClosestVillage(actionPos = new Point(pos));
        if (closestVillageTH != null && !closestVillageTH.controlledBy(player) && (location = closestVillageTH.getLocationAtCoord(actionPos)) != null && (building = location.getBuilding(player.field_70170_p)) != null) {
            UserProfile serverProfile;
            boolean isBuildingPlayerOwned;
            boolean bl = isBuildingPlayerOwned = building.location.getPlan() != null && (building.location.getPlan().price > 0 || building.location.getPlan().isgift);
            if (!isBuildingPlayerOwned && (serverProfile = VillageUtilities.getServerProfile(player.field_70170_p, player)) != null) {
                int reputationLost = 100;
                serverProfile.adjustReputation(closestVillageTH, -100);
                ServerSender.sendTranslatedSentence(player, '6', "ui.stealingcrops", "100");
            }
        }
    }

    private static Map<Material, String> createMaterialNameMap() {
        HashMap<Material, String> result = new HashMap<Material, String>(50);
        result.put(Material.field_151579_a, "air");
        result.put(Material.field_151577_b, "grass");
        result.put(Material.field_151578_c, "ground");
        result.put(Material.field_151575_d, "wood");
        result.put(Material.field_151576_e, "rock");
        result.put(Material.field_151573_f, "iron");
        result.put(Material.field_151574_g, "anvil");
        result.put(Material.field_151586_h, "water");
        result.put(Material.field_151587_i, "lava");
        result.put(Material.field_151584_j, "leaves");
        result.put(Material.field_151585_k, "plants");
        result.put(Material.field_151582_l, "vine");
        result.put(Material.field_151583_m, "sponge");
        result.put(Material.field_151580_n, "cloth");
        result.put(Material.field_151581_o, "fire");
        result.put(Material.field_151595_p, "sand");
        result.put(Material.field_151594_q, "circuits");
        result.put(Material.field_151593_r, "carpet");
        result.put(Material.field_151592_s, "glass");
        result.put(Material.field_151591_t, "redstone_light");
        result.put(Material.field_151590_u, "tnt");
        result.put(Material.field_151589_v, "coral");
        result.put(Material.field_151588_w, "ice");
        result.put(Material.field_151598_x, "packed_ice");
        result.put(Material.field_151597_y, "snow");
        result.put(Material.field_151596_z, "crafted_snow");
        result.put(Material.field_151570_A, "cactus");
        result.put(Material.field_151571_B, "clay");
        result.put(Material.field_151572_C, "gourd");
        result.put(Material.field_151566_D, "dragon_egg");
        result.put(Material.field_151567_E, "portal");
        result.put(Material.field_151568_F, "cake");
        result.put(Material.field_151569_G, "web");
        return Collections.unmodifiableMap(result);
    }

    public static String getBlockCanonicalName(Block block) {
        if (block != null) {
            return ((ResourceLocation)Block.field_149771_c.func_177774_c((Object)block)).toString();
        }
        return null;
    }

    public static String getBlockMaterialName(Block block) {
        if (block != null && block.func_176223_P() != null) {
            return MATERIAL_NAME_MAP.get(block.func_176223_P().func_185904_a());
        }
        return null;
    }

    public static ItemStack getFlowerpotItemStackFromEnum(BlockFlowerPot.EnumFlowerType type) {
        switch (type) {
            case POPPY: {
                return new ItemStack((Block)Blocks.field_150328_O, 1, 0);
            }
            case BLUE_ORCHID: {
                return new ItemStack((Block)Blocks.field_150328_O, 1, 1);
            }
            case ALLIUM: {
                return new ItemStack((Block)Blocks.field_150328_O, 1, 2);
            }
            case HOUSTONIA: {
                return new ItemStack((Block)Blocks.field_150328_O, 1, 3);
            }
            case RED_TULIP: {
                return new ItemStack((Block)Blocks.field_150328_O, 1, 4);
            }
            case ORANGE_TULIP: {
                return new ItemStack((Block)Blocks.field_150328_O, 1, 5);
            }
            case WHITE_TULIP: {
                return new ItemStack((Block)Blocks.field_150328_O, 1, 6);
            }
            case PINK_TULIP: {
                return new ItemStack((Block)Blocks.field_150328_O, 1, 7);
            }
            case OXEYE_DAISY: {
                return new ItemStack((Block)Blocks.field_150328_O, 1, 8);
            }
            case DANDELION: {
                return new ItemStack((Block)Blocks.field_150327_N, 1, 0);
            }
            case OAK_SAPLING: {
                return new ItemStack(Blocks.field_150345_g, 1, 0);
            }
            case SPRUCE_SAPLING: {
                return new ItemStack(Blocks.field_150345_g, 1, 1);
            }
            case BIRCH_SAPLING: {
                return new ItemStack(Blocks.field_150345_g, 1, 2);
            }
            case JUNGLE_SAPLING: {
                return new ItemStack(Blocks.field_150345_g, 1, 3);
            }
            case ACACIA_SAPLING: {
                return new ItemStack(Blocks.field_150345_g, 1, 4);
            }
            case DARK_OAK_SAPLING: {
                return new ItemStack(Blocks.field_150345_g, 1, 5);
            }
            case MUSHROOM_RED: {
                return new ItemStack((Block)Blocks.field_150337_Q, 1, 0);
            }
            case MUSHROOM_BROWN: {
                return new ItemStack((Block)Blocks.field_150338_P, 1, 0);
            }
            case DEAD_BUSH: {
                return new ItemStack((Block)Blocks.field_150330_I, 1, 0);
            }
            case FERN: {
                return new ItemStack((Block)Blocks.field_150329_H, 1, 2);
            }
            case CACTUS: {
                return new ItemStack((Block)Blocks.field_150434_aF, 1, 0);
            }
        }
        return ItemStack.field_190927_a;
    }

    public static ItemStack getItemStackFromBlockState(IBlockState state, int quantity) {
        return new ItemStack(state.func_177230_c(), quantity, state.func_177230_c().func_176201_c(state));
    }

    public static IBlockState getLogBlockstateFromPlankMeta(int plankMeta) {
        if (plankMeta < 4) {
            return Blocks.field_150364_r.func_176203_a(plankMeta);
        }
        return Blocks.field_150363_s.func_176203_a(plankMeta - 4);
    }

    public static void initBlockTypes() {
        File mainBlockTypesFile = new File(MillCommonUtilities.getMillenaireContentDir(), "blocktypes.txt");
        if (!mainBlockTypesFile.exists()) {
            System.err.println("ERROR: Could not find the blocktypes file at " + mainBlockTypesFile.getAbsolutePath());
            Mill.startupError = true;
            return;
        }
        boolean success = BlockItemUtilities.readBlockTypesFile(mainBlockTypesFile);
        if (!success) {
            System.err.println("ERROR: Could not read the blocktypes file at " + mainBlockTypesFile.getAbsolutePath());
            Mill.startupError = true;
            return;
        }
        File customBlockTypesFile = new File(MillCommonUtilities.getMillenaireCustomContentDir(), "blocktypes.txt");
        if (customBlockTypesFile.exists()) {
            BlockItemUtilities.readBlockTypesFile(customBlockTypesFile);
        }
    }

    public static boolean isBlockDangerous(Block b) {
        if (b == null || b == Blocks.field_150350_a || DANGER_EXCEPTIONS.contains(BlockItemUtilities.getBlockCanonicalName(b))) {
            return false;
        }
        if (DANGER_MATERIALS.contains(BlockItemUtilities.getBlockMaterialName(b))) {
            return true;
        }
        return DANGER_BLOCKS.contains(BlockItemUtilities.getBlockCanonicalName(b));
    }

    public static boolean isBlockDecorativePlant(Block block) {
        if (block == null || block == Blocks.field_150350_a) {
            return false;
        }
        if (block instanceof BlockMillCrops || block instanceof BlockCrops) {
            return false;
        }
        return block instanceof BlockBush;
    }

    public static boolean isBlockForbidden(Block b) {
        if (b == null || b == Blocks.field_150350_a || FORBIDDEN_EXCEPTIONS.contains(BlockItemUtilities.getBlockCanonicalName(b))) {
            return false;
        }
        if (b.hasTileEntity(b.func_176223_P())) {
            return true;
        }
        if (FORBIDDEN_MATERIALS.contains(BlockItemUtilities.getBlockMaterialName(b))) {
            return true;
        }
        return FORBIDDEN_BLOCKS.contains(BlockItemUtilities.getBlockCanonicalName(b));
    }

    public static boolean isBlockGround(Block b) {
        if (b == null || b == Blocks.field_150350_a || GROUND_EXCEPTIONS.contains(BlockItemUtilities.getBlockCanonicalName(b))) {
            return false;
        }
        if (GROUND_MATERIALS.contains(BlockItemUtilities.getBlockMaterialName(b))) {
            return true;
        }
        return GROUND_BLOCKS.contains(BlockItemUtilities.getBlockCanonicalName(b));
    }

    public static boolean isBlockLiquid(Block b) {
        if (b == null || b == Blocks.field_150350_a) {
            return false;
        }
        return b instanceof BlockLiquid || b instanceof IFluidBlock;
    }

    public static boolean isBlockOpaqueCube(Block block) {
        IBlockState bs = block.func_176223_P();
        return bs.func_185913_b();
    }

    public static boolean isBlockPathReplaceable(Block b) {
        if (b == null) {
            return false;
        }
        if (b == Blocks.field_150350_a) {
            return false;
        }
        if (PATH_REPLACEABLE_EXCEPTIONS.contains(BlockItemUtilities.getBlockCanonicalName(b))) {
            return false;
        }
        if (PATH_REPLACEABLE_MATERIALS.contains(BlockItemUtilities.getBlockMaterialName(b))) {
            return true;
        }
        return PATH_REPLACEABLE_BLOCKS.contains(BlockItemUtilities.getBlockCanonicalName(b));
    }

    public static boolean isBlockSolid(Block b) {
        if (b == null) {
            return false;
        }
        if (b.func_176223_P().func_185913_b() || b.func_176223_P().func_185896_q()) {
            return true;
        }
        return b == Blocks.field_150359_w || b == Blocks.field_150410_aZ || b == Blocks.field_150333_U || b instanceof BlockSlab || b instanceof BlockStairs || BlockItemUtilities.isFence(b) || b == MillBlocks.PAPER_WALL || b instanceof IBlockPath || b instanceof BlockFarmland;
    }

    public static boolean isBlockWalkable(Block b) {
        if (b == null) {
            return false;
        }
        if (b.func_176223_P().func_185913_b() || b.func_176223_P().func_185896_q()) {
            return true;
        }
        return b == Blocks.field_150359_w || b == Blocks.field_150333_U || b instanceof BlockSlab || b instanceof BlockStairs || b instanceof IBlockPath || b instanceof BlockFarmland;
    }

    public static boolean isBlockWater(Block b) {
        if (b == null || b == Blocks.field_150350_a || WATER_EXCEPTIONS.contains(BlockItemUtilities.getBlockCanonicalName(b))) {
            return false;
        }
        if (WATER_MATERIALS.contains(BlockItemUtilities.getBlockMaterialName(b))) {
            return true;
        }
        return WATER_BLOCKS.contains(BlockItemUtilities.getBlockCanonicalName(b));
    }

    public static boolean isFence(Block block) {
        return block == Blocks.field_180405_aT || block == Blocks.field_180404_aQ || block == Blocks.field_180406_aS || block == Blocks.field_180403_aR || block == Blocks.field_180407_aO || block == Blocks.field_180408_aP;
    }

    public static boolean isFenceGate(Block block) {
        return block == Blocks.field_180387_bt || block == Blocks.field_180392_bq || block == Blocks.field_180385_bs || block == Blocks.field_180386_br || block == Blocks.field_180390_bo || block == Blocks.field_180391_bp;
    }

    public static boolean isPath(Block block) {
        return block instanceof IBlockPath || block instanceof BlockPathSlab;
    }

    public static boolean isPathSlab(Block block) {
        return block instanceof BlockPathSlab;
    }

    public static boolean isWoodenDoor(Block block) {
        return block instanceof BlockDoor && block.func_149688_o(null) == Material.field_151575_d;
    }

    private static boolean readBlockTypesFile(File file) {
        if (!file.exists()) {
            return false;
        }
        try {
            BufferedReader reader = MillCommonUtilities.getReader(file);
            String line = reader.readLine();
            while (line != null) {
                String[] temp;
                if (line.trim().length() > 0 && !line.startsWith("//") && (temp = line.split("=")).length == 2) {
                    String key = temp[0].trim().toLowerCase();
                    String value = temp[1];
                    if (key.equals("forbidden_materials")) {
                        FORBIDDEN_MATERIALS.clear();
                        FORBIDDEN_MATERIALS.addAll(Arrays.asList(value.split(",")));
                    } else if (key.equals("forbidden_blocks")) {
                        FORBIDDEN_BLOCKS.clear();
                        FORBIDDEN_BLOCKS.addAll(Arrays.asList(value.split(",")));
                    } else if (key.equals("forbidden_exceptions")) {
                        FORBIDDEN_EXCEPTIONS.clear();
                        FORBIDDEN_EXCEPTIONS.addAll(Arrays.asList(value.split(",")));
                    } else if (key.equals("ground_materials")) {
                        GROUND_MATERIALS.clear();
                        GROUND_MATERIALS.addAll(Arrays.asList(value.split(",")));
                    } else if (key.equals("ground_blocks")) {
                        GROUND_BLOCKS.clear();
                        GROUND_BLOCKS.addAll(Arrays.asList(value.split(",")));
                    } else if (key.equals("ground_exceptions")) {
                        GROUND_EXCEPTIONS.clear();
                        GROUND_EXCEPTIONS.addAll(Arrays.asList(value.split(",")));
                    } else if (key.equals("danger_materials")) {
                        DANGER_MATERIALS.clear();
                        DANGER_MATERIALS.addAll(Arrays.asList(value.split(",")));
                    } else if (key.equals("danger_blocks")) {
                        DANGER_BLOCKS.clear();
                        DANGER_BLOCKS.addAll(Arrays.asList(value.split(",")));
                    } else if (key.equals("danger_exceptions")) {
                        DANGER_EXCEPTIONS.clear();
                        DANGER_EXCEPTIONS.addAll(Arrays.asList(value.split(",")));
                    } else if (key.equals("water_materials")) {
                        WATER_MATERIALS.clear();
                        WATER_MATERIALS.addAll(Arrays.asList(value.split(",")));
                    } else if (key.equals("water_blocks")) {
                        WATER_BLOCKS.clear();
                        WATER_BLOCKS.addAll(Arrays.asList(value.split(",")));
                    } else if (key.equals("water_exceptions")) {
                        WATER_EXCEPTIONS.clear();
                        WATER_EXCEPTIONS.addAll(Arrays.asList(value.split(",")));
                    } else if (key.equals("path_replaceable_materials")) {
                        PATH_REPLACEABLE_MATERIALS.clear();
                        PATH_REPLACEABLE_MATERIALS.addAll(Arrays.asList(value.split(",")));
                    } else if (key.equals("path_replaceable_blocks")) {
                        PATH_REPLACEABLE_BLOCKS.clear();
                        PATH_REPLACEABLE_BLOCKS.addAll(Arrays.asList(value.split(",")));
                    } else if (key.equals("path_replaceable_exceptions")) {
                        PATH_REPLACEABLE_EXCEPTIONS.clear();
                        PATH_REPLACEABLE_EXCEPTIONS.addAll(Arrays.asList(value.split(",")));
                    } else {
                        MillLog.error(null, "Unknown block type category on line: " + line);
                    }
                }
                line = reader.readLine();
            }
            reader.close();
        }
        catch (Exception e) {
            MillLog.printException(e);
            return false;
        }
        return true;
    }
}

