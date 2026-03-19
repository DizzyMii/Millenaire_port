package org.dizzymii.millenaire2.buildingplan;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.dizzymii.millenaire2.block.MillBlocks;
import org.dizzymii.millenaire2.util.MillCommonUtilities;
import org.dizzymii.millenaire2.util.MillLog;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Parses the legacy blocklist.txt colour-to-block mapping file.
 * This file defines the actual RGB colours used in the PNG building plans.
 * Translates 1.12.2 block IDs to 1.21.1 equivalents.
 */
public class BlocklistLoader {

    private static final Map<String, Block> LEGACY = new HashMap<>();

    static {
        buildLegacyMap();
    }

    /**
     * Load blocklist.txt and register all colour mappings into PointType.colourPoints.
     * @return true if at least one entry was loaded
     */
    public static boolean load() {
        File contentDir = MillCommonUtilities.getMillenaireContentDir();
        File file = new File(contentDir, "blocklist.txt");
        if (!file.exists()) {
            MillLog.warn(null, "BlocklistLoader: blocklist.txt not found at " + file.getAbsolutePath());
            return false;
        }

        PointType.colourPoints.clear();
        int loaded = 0;
        int skipped = 0;

        try (BufferedReader reader = MillCommonUtilities.getReader(file)) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("//")) continue;

                String[] parts = line.split(";", -1);
                if (parts.length < 5) continue;

                String name = parts[0].trim();
                String blockRef = parts[1].trim();
                String propsStr = parts.length > 2 ? parts[2].trim() : "";
                String secondStr = parts.length > 3 ? parts[3].trim() : "";
                String colorStr = parts.length > 4 ? parts[4].trim() : "";

                int colour = parseRGB(colorStr);
                if (colour < 0) continue;

                boolean secondStep = secondStr.startsWith("true");

                // Special entry (no block reference)
                if (blockRef.isEmpty() || "0".equals(blockRef)) {
                    PointType.registerSpecial(colour, name);
                    loaded++;
                    continue;
                }

                // Block entry - resolve legacy ID to modern block
                Block block = resolve(blockRef, propsStr);
                if (block == null) {
                    if (skipped < 50) {
                        MillLog.warn(null, "BlocklistLoader: unresolved block '" + blockRef
                                + "' props='" + propsStr + "' (name=" + name + ")");
                    }
                    skipped++;
                    continue;
                }

                BlockState state = resolveState(block, propsStr);
                PointType pt = new PointType(colour, name, block, state, secondStep);
                PointType.colourPoints.put(colour, pt);
                loaded++;
            }
        } catch (Exception e) {
            MillLog.error(null, "BlocklistLoader: error reading blocklist.txt", e);
            return false;
        }

        MillLog.minor(null, "BlocklistLoader: loaded " + loaded + " entries (" + skipped + " skipped)");
        return loaded > 0;
    }

    /** Parse "R/G/B" colour string to packed int, or -1 on failure. */
    private static int parseRGB(String s) {
        try {
            String[] rgb = s.split("/");
            if (rgb.length != 3) return -1;
            int r = Integer.parseInt(rgb[0].trim()) & 0xFF;
            int g = Integer.parseInt(rgb[1].trim()) & 0xFF;
            int b = Integer.parseInt(rgb[2].trim()) & 0xFF;
            return (r << 16) | (g << 8) | b;
        } catch (Exception e) {
            return -1;
        }
    }

    /** Resolve a legacy block reference to a modern Block. */
    @Nullable
    private static Block resolve(String blockRef, String propsStr) {
        // Try exact match: blockRef|propsStr
        String key = blockRef + "|" + propsStr;
        Block b = LEGACY.get(key);
        if (b != null) return b;

        // Try just blockRef
        b = LEGACY.get(blockRef);
        if (b != null) return b;

        // Try millenaire: → millenaire2: namespace translation
        if (blockRef.startsWith("millenaire:")) {
            String modernId = "millenaire2:" + blockRef.substring("millenaire:".length());
            b = tryRegistry(modernId);
            if (b != null) return b;
        }

        // Try direct registry lookup (handles blocks whose IDs haven't changed)
        b = tryRegistry(blockRef);
        if (b != null) return b;

        return null;
    }

    @Nullable
    private static Block tryRegistry(String id) {
        try {
            ResourceLocation rl = ResourceLocation.parse(id);
            Block block = BuiltInRegistries.BLOCK.get(rl);
            if (block != Blocks.AIR || "minecraft:air".equals(id)) {
                return block;
            }
        } catch (Exception ignored) {}
        return null;
    }

    /** Apply block state properties from the properties string. */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static BlockState resolveState(Block block, String propsStr) {
        BlockState state = block.defaultBlockState();
        if (propsStr.isEmpty() || isNumericMeta(propsStr)) return state;

        // Parse comma-separated key=value pairs
        for (String pair : propsStr.split(",")) {
            pair = pair.trim();
            if (!pair.contains("=")) continue;
            String[] kv = pair.split("=", 2);
            String propName = kv[0].trim();
            String propVal = kv[1].trim();

            // Skip legacy-only properties
            if ("variant".equals(propName) || "decayable".equals(propName) ||
                "color".equals(propName) || "type".equals(propName) ||
                "topbottom".equals(propName) || "alignment".equals(propName)) {
                continue;
            }

            // Try to find and apply the property
            for (Property<?> prop : state.getProperties()) {
                if (prop.getName().equals(propName)) {
                    Optional<?> val = prop.getValue(propVal);
                    if (val.isPresent()) {
                        state = state.setValue((Property) prop, (Comparable) val.get());
                    }
                    break;
                }
            }
        }
        return state;
    }

    private static boolean isNumericMeta(String s) {
        try {
            Integer.parseInt(s.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /** Build the legacy 1.12.2 → 1.21.1 block translation map. */
    private static void buildLegacyMap() {
        // === Stone variants ===
        LEGACY.put("minecraft:stone", Blocks.STONE);
        LEGACY.put("minecraft:stone|variant=stone", Blocks.STONE);
        LEGACY.put("minecraft:stone|variant=granite", Blocks.GRANITE);
        LEGACY.put("minecraft:stone|variant=smooth_granite", Blocks.POLISHED_GRANITE);
        LEGACY.put("minecraft:stone|variant=diorite", Blocks.DIORITE);
        LEGACY.put("minecraft:stone|variant=smooth_diorite", Blocks.POLISHED_DIORITE);
        LEGACY.put("minecraft:stone|variant=andesite", Blocks.ANDESITE);
        LEGACY.put("minecraft:stone|variant=smooth_andesite", Blocks.POLISHED_ANDESITE);

        // === Dirt ===
        LEGACY.put("minecraft:dirt", Blocks.DIRT);
        LEGACY.put("minecraft:dirt|variant=dirt", Blocks.DIRT);
        LEGACY.put("minecraft:dirt|variant=coarse_dirt", Blocks.COARSE_DIRT);
        LEGACY.put("minecraft:dirt|variant=podzol", Blocks.PODZOL);
        LEGACY.put("minecraft:dirt|0", Blocks.DIRT);
        LEGACY.put("minecraft:dirt|1", Blocks.COARSE_DIRT);

        // === Stone bricks ===
        LEGACY.put("minecraft:stonebrick", Blocks.STONE_BRICKS);
        LEGACY.put("minecraft:stonebrick|0", Blocks.STONE_BRICKS);
        LEGACY.put("minecraft:stonebrick|1", Blocks.MOSSY_STONE_BRICKS);
        LEGACY.put("minecraft:stonebrick|2", Blocks.CRACKED_STONE_BRICKS);
        LEGACY.put("minecraft:stonebrick|3", Blocks.CHISELED_STONE_BRICKS);

        // === Planks ===
        LEGACY.put("minecraft:planks", Blocks.OAK_PLANKS);
        LEGACY.put("minecraft:planks|variant=oak", Blocks.OAK_PLANKS);
        LEGACY.put("minecraft:planks|variant=spruce", Blocks.SPRUCE_PLANKS);
        LEGACY.put("minecraft:planks|variant=birch", Blocks.BIRCH_PLANKS);
        LEGACY.put("minecraft:planks|variant=jungle", Blocks.JUNGLE_PLANKS);
        LEGACY.put("minecraft:planks|variant=acacia", Blocks.ACACIA_PLANKS);
        LEGACY.put("minecraft:planks|variant=dark_oak", Blocks.DARK_OAK_PLANKS);

        // === Logs ===
        putLogVariants("minecraft:log", "oak", Blocks.OAK_LOG);
        putLogVariants("minecraft:log", "spruce", Blocks.SPRUCE_LOG);
        putLogVariants("minecraft:log", "birch", Blocks.BIRCH_LOG);
        putLogVariants("minecraft:log", "jungle", Blocks.JUNGLE_LOG);
        putLogVariants("minecraft:log2", "acacia", Blocks.ACACIA_LOG);
        putLogVariants("minecraft:log2", "dark_oak", Blocks.DARK_OAK_LOG);

        // === Leaves ===
        LEGACY.put("minecraft:leaves|decayable=false,variant=oak", Blocks.OAK_LEAVES);
        LEGACY.put("minecraft:leaves|decayable=false,variant=spruce", Blocks.SPRUCE_LEAVES);
        LEGACY.put("minecraft:leaves|decayable=false,variant=birch", Blocks.BIRCH_LEAVES);
        LEGACY.put("minecraft:leaves|decayable=false,variant=jungle", Blocks.JUNGLE_LEAVES);
        LEGACY.put("minecraft:leaves2|decayable=false,variant=acacia", Blocks.ACACIA_LEAVES);
        LEGACY.put("minecraft:leaves2|decayable=false,variant=dark_oak", Blocks.DARK_OAK_LEAVES);

        // === Saplings ===
        LEGACY.put("minecraft:sapling", Blocks.OAK_SAPLING);
        LEGACY.put("minecraft:sapling|type=oak", Blocks.OAK_SAPLING);
        LEGACY.put("minecraft:sapling|type=spruce", Blocks.SPRUCE_SAPLING);
        LEGACY.put("minecraft:sapling|type=birch", Blocks.BIRCH_SAPLING);
        LEGACY.put("minecraft:sapling|type=jungle", Blocks.JUNGLE_SAPLING);
        LEGACY.put("minecraft:sapling|type=acacia", Blocks.ACACIA_SAPLING);
        LEGACY.put("minecraft:sapling|type=dark_oak", Blocks.DARK_OAK_SAPLING);
        LEGACY.put("minecraft:sapling|0", Blocks.OAK_SAPLING);
        LEGACY.put("minecraft:sapling|1", Blocks.SPRUCE_SAPLING);

        // === Renamed blocks ===
        LEGACY.put("minecraft:brick_block", Blocks.BRICKS);
        LEGACY.put("minecraft:brick_block|0", Blocks.BRICKS);
        LEGACY.put("minecraft:mossy_cobblestone", Blocks.MOSSY_COBBLESTONE);
        LEGACY.put("minecraft:mossy_cobblestone|0", Blocks.MOSSY_COBBLESTONE);
        LEGACY.put("minecraft:lit_pumpkin", Blocks.JACK_O_LANTERN);
        LEGACY.put("minecraft:lit_pumpkin|0", Blocks.JACK_O_LANTERN);
        LEGACY.put("minecraft:melon_block", Blocks.MELON);
        LEGACY.put("minecraft:melon_block|0", Blocks.MELON);
        LEGACY.put("minecraft:waterlily", Blocks.LILY_PAD);
        LEGACY.put("minecraft:waterlily|0", Blocks.LILY_PAD);
        LEGACY.put("minecraft:nether_brick", Blocks.NETHER_BRICKS);
        LEGACY.put("minecraft:nether_brick|0", Blocks.NETHER_BRICKS);
        LEGACY.put("minecraft:lit_redstone_lamp", Blocks.REDSTONE_LAMP);
        LEGACY.put("minecraft:lit_redstone_lamp|0", Blocks.REDSTONE_LAMP);
        LEGACY.put("minecraft:noteblock", Blocks.NOTE_BLOCK);
        LEGACY.put("minecraft:noteblock|0", Blocks.NOTE_BLOCK);
        LEGACY.put("minecraft:portal", Blocks.NETHER_PORTAL);
        LEGACY.put("minecraft:portal|0", Blocks.NETHER_PORTAL);

        // === Doors ===
        LEGACY.put("minecraft:wooden_door", Blocks.OAK_DOOR);
        putAllFacing("minecraft:wooden_door", Blocks.OAK_DOOR);
        putAllFacing("minecraft:spruce_door", Blocks.SPRUCE_DOOR);
        putAllFacing("minecraft:birch_door", Blocks.BIRCH_DOOR);
        putAllFacing("minecraft:jungle_door", Blocks.JUNGLE_DOOR);
        putAllFacing("minecraft:acacia_door", Blocks.ACACIA_DOOR);
        putAllFacing("minecraft:dark_oak_door", Blocks.DARK_OAK_DOOR);
        LEGACY.put("minecraft:trapdoor", Blocks.OAK_TRAPDOOR);

        // === Fences ===
        LEGACY.put("minecraft:fence", Blocks.OAK_FENCE);
        LEGACY.put("minecraft:fence|0", Blocks.OAK_FENCE);
        LEGACY.put("minecraft:fence_gate", Blocks.OAK_FENCE_GATE);
        LEGACY.put("minecraft:fence_gate|0", Blocks.OAK_FENCE_GATE);

        // === Pressure plates ===
        LEGACY.put("minecraft:wooden_pressure_plate", Blocks.OAK_PRESSURE_PLATE);
        LEGACY.put("minecraft:wooden_pressure_plate|0", Blocks.OAK_PRESSURE_PLATE);

        // === Signs ===
        LEGACY.put("minecraft:standing_sign", Blocks.OAK_SIGN);

        // === Snow ===
        LEGACY.put("minecraft:snow", Blocks.SNOW_BLOCK);
        LEGACY.put("minecraft:snow|0", Blocks.SNOW_BLOCK);
        LEGACY.put("minecraft:snow_layer", Blocks.SNOW);

        // === Flowing liquids ===
        LEGACY.put("minecraft:flowing_lava", Blocks.LAVA);
        LEGACY.put("minecraft:flowing_lava|0", Blocks.LAVA);

        // === Wool (16 colours) ===
        Block[] wools = {Blocks.WHITE_WOOL, Blocks.ORANGE_WOOL, Blocks.MAGENTA_WOOL, Blocks.LIGHT_BLUE_WOOL,
                Blocks.YELLOW_WOOL, Blocks.LIME_WOOL, Blocks.PINK_WOOL, Blocks.GRAY_WOOL,
                Blocks.LIGHT_GRAY_WOOL, Blocks.CYAN_WOOL, Blocks.PURPLE_WOOL, Blocks.BLUE_WOOL,
                Blocks.BROWN_WOOL, Blocks.GREEN_WOOL, Blocks.RED_WOOL, Blocks.BLACK_WOOL};
        for (int i = 0; i < 16; i++) {
            LEGACY.put("minecraft:wool|" + i, wools[i]);
        }
        LEGACY.put("minecraft:wool", Blocks.WHITE_WOOL);

        // === Carpet (16 colours) ===
        Block[] carpets = {Blocks.WHITE_CARPET, Blocks.ORANGE_CARPET, Blocks.MAGENTA_CARPET, Blocks.LIGHT_BLUE_CARPET,
                Blocks.YELLOW_CARPET, Blocks.LIME_CARPET, Blocks.PINK_CARPET, Blocks.GRAY_CARPET,
                Blocks.LIGHT_GRAY_CARPET, Blocks.CYAN_CARPET, Blocks.PURPLE_CARPET, Blocks.BLUE_CARPET,
                Blocks.BROWN_CARPET, Blocks.GREEN_CARPET, Blocks.RED_CARPET, Blocks.BLACK_CARPET};
        for (int i = 0; i < 16; i++) {
            LEGACY.put("minecraft:carpet|" + i, carpets[i]);
        }

        // === Stained glass panes (16 colours) ===
        Block[] panes = {Blocks.WHITE_STAINED_GLASS_PANE, Blocks.ORANGE_STAINED_GLASS_PANE, Blocks.MAGENTA_STAINED_GLASS_PANE,
                Blocks.LIGHT_BLUE_STAINED_GLASS_PANE, Blocks.YELLOW_STAINED_GLASS_PANE, Blocks.LIME_STAINED_GLASS_PANE,
                Blocks.PINK_STAINED_GLASS_PANE, Blocks.GRAY_STAINED_GLASS_PANE, Blocks.LIGHT_GRAY_STAINED_GLASS_PANE,
                Blocks.CYAN_STAINED_GLASS_PANE, Blocks.PURPLE_STAINED_GLASS_PANE, Blocks.BLUE_STAINED_GLASS_PANE,
                Blocks.BROWN_STAINED_GLASS_PANE, Blocks.GREEN_STAINED_GLASS_PANE, Blocks.RED_STAINED_GLASS_PANE,
                Blocks.BLACK_STAINED_GLASS_PANE};
        for (int i = 0; i < 16; i++) {
            LEGACY.put("minecraft:stained_glass_pane|" + i, panes[i]);
        }

        // === Terracotta ===
        LEGACY.put("minecraft:hardened_clay", Blocks.TERRACOTTA);
        LEGACY.put("minecraft:hardened_clay|0", Blocks.TERRACOTTA);
        putTerracotta("white", Blocks.WHITE_TERRACOTTA);
        putTerracotta("orange", Blocks.ORANGE_TERRACOTTA);
        putTerracotta("magenta", Blocks.MAGENTA_TERRACOTTA);
        putTerracotta("light_blue", Blocks.LIGHT_BLUE_TERRACOTTA);
        putTerracotta("yellow", Blocks.YELLOW_TERRACOTTA);
        putTerracotta("lime", Blocks.LIME_TERRACOTTA);
        putTerracotta("pink", Blocks.PINK_TERRACOTTA);
        putTerracotta("gray", Blocks.GRAY_TERRACOTTA);
        putTerracotta("silver", Blocks.LIGHT_GRAY_TERRACOTTA);
        putTerracotta("cyan", Blocks.CYAN_TERRACOTTA);
        putTerracotta("purple", Blocks.PURPLE_TERRACOTTA);
        putTerracotta("blue", Blocks.BLUE_TERRACOTTA);
        putTerracotta("brown", Blocks.BROWN_TERRACOTTA);
        putTerracotta("green", Blocks.GREEN_TERRACOTTA);
        putTerracotta("red", Blocks.RED_TERRACOTTA);
        putTerracotta("black", Blocks.BLACK_TERRACOTTA);

        // === Concrete (16 colours) ===
        Block[] concretes = {Blocks.WHITE_CONCRETE, Blocks.ORANGE_CONCRETE, Blocks.MAGENTA_CONCRETE, Blocks.LIGHT_BLUE_CONCRETE,
                Blocks.YELLOW_CONCRETE, Blocks.LIME_CONCRETE, Blocks.PINK_CONCRETE, Blocks.GRAY_CONCRETE,
                Blocks.LIGHT_GRAY_CONCRETE, Blocks.CYAN_CONCRETE, Blocks.PURPLE_CONCRETE, Blocks.BLUE_CONCRETE,
                Blocks.BROWN_CONCRETE, Blocks.GREEN_CONCRETE, Blocks.RED_CONCRETE, Blocks.BLACK_CONCRETE};
        for (int i = 0; i < 16; i++) {
            LEGACY.put("minecraft:concrete|" + i, concretes[i]);
        }

        // === Slabs ===
        LEGACY.put("minecraft:stone_slab", Blocks.SMOOTH_STONE_SLAB);
        LEGACY.put("minecraft:stone_slab|0", Blocks.SMOOTH_STONE_SLAB);
        LEGACY.put("minecraft:stone_slab|1", Blocks.SANDSTONE_SLAB);
        LEGACY.put("minecraft:stone_slab|3", Blocks.COBBLESTONE_SLAB);
        LEGACY.put("minecraft:stone_slab|4", Blocks.BRICK_SLAB);
        LEGACY.put("minecraft:stone_slab|5", Blocks.STONE_BRICK_SLAB);
        LEGACY.put("minecraft:stone_slab|8", Blocks.SMOOTH_STONE_SLAB);
        LEGACY.put("minecraft:stone_slab|9", Blocks.SANDSTONE_SLAB);
        LEGACY.put("minecraft:stone_slab|11", Blocks.COBBLESTONE_SLAB);
        LEGACY.put("minecraft:stone_slab|12", Blocks.BRICK_SLAB);
        LEGACY.put("minecraft:stone_slab|13", Blocks.STONE_BRICK_SLAB);
        LEGACY.put("minecraft:stone_slab|variant=quartz,half=bottom", Blocks.QUARTZ_SLAB);
        LEGACY.put("minecraft:stone_slab|variant=quartz,half=top", Blocks.QUARTZ_SLAB);
        LEGACY.put("minecraft:stone_slab2", Blocks.RED_SANDSTONE_SLAB);
        LEGACY.put("minecraft:stone_slab2|0", Blocks.RED_SANDSTONE_SLAB);
        LEGACY.put("minecraft:stone_slab2|8", Blocks.RED_SANDSTONE_SLAB);
        LEGACY.put("minecraft:wooden_slab", Blocks.OAK_SLAB);
        LEGACY.put("minecraft:wooden_slab|0", Blocks.OAK_SLAB);
        LEGACY.put("minecraft:wooden_slab|1", Blocks.SPRUCE_SLAB);
        LEGACY.put("minecraft:wooden_slab|2", Blocks.BIRCH_SLAB);
        LEGACY.put("minecraft:wooden_slab|3", Blocks.JUNGLE_SLAB);
        LEGACY.put("minecraft:wooden_slab|4", Blocks.ACACIA_SLAB);
        LEGACY.put("minecraft:wooden_slab|5", Blocks.DARK_OAK_SLAB);
        LEGACY.put("minecraft:wooden_slab|8", Blocks.OAK_SLAB);
        LEGACY.put("minecraft:wooden_slab|9", Blocks.SPRUCE_SLAB);
        LEGACY.put("minecraft:wooden_slab|10", Blocks.BIRCH_SLAB);
        LEGACY.put("minecraft:wooden_slab|11", Blocks.JUNGLE_SLAB);
        LEGACY.put("minecraft:wooden_slab|12", Blocks.ACACIA_SLAB);
        LEGACY.put("minecraft:wooden_slab|13", Blocks.DARK_OAK_SLAB);
        LEGACY.put("minecraft:double_stone_slab", Blocks.SMOOTH_STONE);
        LEGACY.put("minecraft:double_stone_slab|0", Blocks.SMOOTH_STONE);

        // === Stairs (renamed) ===
        LEGACY.put("minecraft:stone_stairs", Blocks.COBBLESTONE_STAIRS);

        // === Tallgrass / flowers ===
        LEGACY.put("minecraft:tallgrass", Blocks.SHORT_GRASS);
        LEGACY.put("minecraft:tallgrass|0", Blocks.DEAD_BUSH);
        LEGACY.put("minecraft:tallgrass|1", Blocks.SHORT_GRASS);
        LEGACY.put("minecraft:tallgrass|2", Blocks.FERN);
        LEGACY.put("minecraft:yellow_flower", Blocks.DANDELION);
        LEGACY.put("minecraft:yellow_flower|0", Blocks.DANDELION);
        LEGACY.put("minecraft:red_flower", Blocks.POPPY);
        LEGACY.put("minecraft:red_flower|0", Blocks.POPPY);
        LEGACY.put("minecraft:red_flower|1", Blocks.BLUE_ORCHID);
        LEGACY.put("minecraft:red_flower|2", Blocks.ALLIUM);
        LEGACY.put("minecraft:red_flower|3", Blocks.AZURE_BLUET);
        LEGACY.put("minecraft:red_flower|4", Blocks.RED_TULIP);
        LEGACY.put("minecraft:red_flower|5", Blocks.ORANGE_TULIP);
        LEGACY.put("minecraft:red_flower|6", Blocks.WHITE_TULIP);
        LEGACY.put("minecraft:red_flower|7", Blocks.PINK_TULIP);
        LEGACY.put("minecraft:red_flower|8", Blocks.OXEYE_DAISY);

        // === Double plants ===
        LEGACY.put("minecraft:double_plant", Blocks.SUNFLOWER);
        LEGACY.put("minecraft:double_plant|facing=north,half=lower,variant=sunflower", Blocks.SUNFLOWER);
        LEGACY.put("minecraft:double_plant|facing=north,half=lower,variant=syringa", Blocks.LILAC);
        LEGACY.put("minecraft:double_plant|facing=north,half=lower,variant=double_grass", Blocks.TALL_GRASS);
        LEGACY.put("minecraft:double_plant|facing=north,half=lower,variant=double_fern", Blocks.LARGE_FERN);
        LEGACY.put("minecraft:double_plant|facing=north,half=lower,variant=double_rose", Blocks.ROSE_BUSH);
        LEGACY.put("minecraft:double_plant|facing=north,half=lower,variant=paeonia", Blocks.PEONY);

        // === Sandstone variants ===
        LEGACY.put("minecraft:sandstone|0", Blocks.SANDSTONE);
        LEGACY.put("minecraft:sandstone|1", Blocks.CHISELED_SANDSTONE);
        LEGACY.put("minecraft:sandstone|2", Blocks.SMOOTH_SANDSTONE);
        LEGACY.put("minecraft:red_sandstone|0", Blocks.RED_SANDSTONE);
        LEGACY.put("minecraft:red_sandstone|1", Blocks.CHISELED_RED_SANDSTONE);
        LEGACY.put("minecraft:red_sandstone|2", Blocks.SMOOTH_RED_SANDSTONE);

        // === Quartz ===
        LEGACY.put("minecraft:quartz_block", Blocks.QUARTZ_BLOCK);
        LEGACY.put("minecraft:quartz_block|variant=default", Blocks.QUARTZ_BLOCK);
        LEGACY.put("minecraft:quartz_block|variant=chiseled", Blocks.CHISELED_QUARTZ_BLOCK);
        LEGACY.put("minecraft:quartz_block|variant=lines_x", Blocks.QUARTZ_PILLAR);
        LEGACY.put("minecraft:quartz_block|variant=lines_y", Blocks.QUARTZ_PILLAR);
        LEGACY.put("minecraft:quartz_block|variant=lines_z", Blocks.QUARTZ_PILLAR);

        // === Flower pots (simplified - map to empty pot) ===
        LEGACY.put("minecraft:flower_pot", Blocks.FLOWER_POT);

        // === Glazed terracotta (1.12 already had modern-ish names) ===
        LEGACY.put("minecraft:silver_glazed_terracotta", Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA);

        // === Cauldron ===
        LEGACY.put("minecraft:cauldron|0", Blocks.CAULDRON);
        LEGACY.put("minecraft:cauldron|3", Blocks.WATER_CAULDRON);

        // === Mushroom blocks ===
        LEGACY.put("minecraft:brown_mushroom_block", Blocks.BROWN_MUSHROOM_BLOCK);
        LEGACY.put("minecraft:brown_mushroom_block|0", Blocks.BROWN_MUSHROOM_BLOCK);
        LEGACY.put("minecraft:red_mushroom_block", Blocks.RED_MUSHROOM_BLOCK);
        LEGACY.put("minecraft:red_mushroom_block|0", Blocks.RED_MUSHROOM_BLOCK);

        // === Misc renamed ===
        LEGACY.put("minecraft:cobblestone_wall|0", Blocks.COBBLESTONE_WALL);
        LEGACY.put("minecraft:nether_brick_fence|0", Blocks.NETHER_BRICK_FENCE);
        LEGACY.put("minecraft:purpur_slab|0", Blocks.PURPUR_SLAB);
        LEGACY.put("minecraft:purpur_slab|8", Blocks.PURPUR_SLAB);

        // === Millenaire blocks → Millenaire2 ===
        LEGACY.put("millenaire:wood_deco", MillBlocks.TIMBER_FRAME_PLAIN.get());
        LEGACY.put("millenaire:wood_deco|0", MillBlocks.TIMBER_FRAME_PLAIN.get());
        LEGACY.put("millenaire:wood_deco|1", MillBlocks.TIMBER_FRAME_CROSS.get());
        LEGACY.put("millenaire:wood_deco|2", MillBlocks.THATCH.get());
        LEGACY.put("millenaire:wood_deco|3", MillBlocks.THATCH.get());
        LEGACY.put("millenaire:stone_deco", MillBlocks.MUD_BRICK.get());
        LEGACY.put("millenaire:stone_deco|0", MillBlocks.MUD_BRICK.get());
        LEGACY.put("millenaire:stone_deco|2", MillBlocks.STONE_DECORATION.get());
        LEGACY.put("millenaire:stone_deco|5", MillBlocks.STONE_DECORATION.get());
        LEGACY.put("millenaire:stone_deco|6", MillBlocks.STONE_DECORATION.get());
        LEGACY.put("millenaire:stone_deco|variant=byzantine_mosaic_red", MillBlocks.STONE_DECORATION.get());
        LEGACY.put("millenaire:stone_deco|variant=byzantine_mosaic_blue", MillBlocks.STONE_DECORATION.get());
        LEGACY.put("millenaire:earth_deco", MillBlocks.MUD_BRICK.get());
        LEGACY.put("millenaire:earth_deco|0", MillBlocks.MUD_BRICK.get());
        LEGACY.put("millenaire:extended_mud_brick", MillBlocks.MUD_BRICK_EXTENDED.get());
        LEGACY.put("millenaire:extended_mud_brick|variant=mudbrick_smooth", MillBlocks.MUD_BRICK_EXTENDED.get());
        LEGACY.put("millenaire:extended_mud_brick|variant=mudbrick_seljuk_ornamented", MillBlocks.MUD_BRICK_EXTENDED.get());
        LEGACY.put("millenaire:extended_mud_brick|variant=mudbrick_seljuk_decorated", MillBlocks.MUD_BRICK_EXTENDED.get());
        LEGACY.put("millenaire:crop_maize", MillBlocks.CROP_MAIZE.get());
        LEGACY.put("millenaire:crop_maize|0", MillBlocks.CROP_MAIZE.get());
        LEGACY.put("millenaire:crop_maize|7", MillBlocks.CROP_MAIZE.get());

        // Millenaire decorative items → closest available block
        LEGACY.put("millenaire:tapestry", MillBlocks.STAINED_GLASS.get());
        LEGACY.put("millenaire:indianstatue", MillBlocks.MILL_STATUE.get());
        LEGACY.put("millenaire:mayanstatue", MillBlocks.MILL_STATUE.get());
        LEGACY.put("millenaire:byzantineiconsmall", MillBlocks.ROSETTE.get());
        LEGACY.put("millenaire:byzantineiconmedium", MillBlocks.ROSETTE.get());
        LEGACY.put("millenaire:byzantineiconlarge", MillBlocks.ROSETTE.get());
        LEGACY.put("millenaire:wallcarpetsmall", MillBlocks.STAINED_GLASS.get());
        LEGACY.put("millenaire:wallcarpetmedium", MillBlocks.STAINED_GLASS.get());
        LEGACY.put("millenaire:wallcarpetlarge", MillBlocks.STAINED_GLASS.get());

        // Paths
        LEGACY.put("millenaire:pathdirt", MillBlocks.PATH_DIRT.get());
        LEGACY.put("millenaire:pathdirt|1", MillBlocks.PATH_DIRT.get());
        LEGACY.put("millenaire:pathgravel", MillBlocks.PATH_GRAVEL.get());
        LEGACY.put("millenaire:pathgravel|1", MillBlocks.PATH_GRAVEL.get());
        LEGACY.put("millenaire:pathslabs", MillBlocks.PATH_SLABS.get());
        LEGACY.put("millenaire:pathslabs|1", MillBlocks.PATH_SLABS.get());
        LEGACY.put("millenaire:pathsandstone", MillBlocks.PATH_SANDSTONE.get());
        LEGACY.put("millenaire:pathsandstone|1", MillBlocks.PATH_SANDSTONE.get());
        LEGACY.put("millenaire:pathochretiles", MillBlocks.PATH_OCHRE_TILES.get());
        LEGACY.put("millenaire:pathochretiles|1", MillBlocks.PATH_OCHRE_TILES.get());
        LEGACY.put("millenaire:pathgravelslabs", MillBlocks.PATH_GRAVEL_SLABS.get());
        LEGACY.put("millenaire:pathgravelslabs|1", MillBlocks.PATH_GRAVEL_SLABS.get());
        LEGACY.put("millenaire:pathsnow", MillBlocks.PATH_SNOW.get());
        LEGACY.put("millenaire:pathsnow|1", MillBlocks.PATH_SNOW.get());

        // Path slabs
        LEGACY.put("millenaire:pathdirt_slab", MillBlocks.SLAB_PATH_DIRT.get());
        LEGACY.put("millenaire:pathdirt_slab|1", MillBlocks.SLAB_PATH_DIRT.get());
        LEGACY.put("millenaire:pathgravel_slab", MillBlocks.SLAB_PATH_GRAVEL.get());
        LEGACY.put("millenaire:pathgravel_slab|1", MillBlocks.SLAB_PATH_GRAVEL.get());
        LEGACY.put("millenaire:pathslabs_slab", MillBlocks.SLAB_PATH_SLABS.get());
        LEGACY.put("millenaire:pathslabs_slab|1", MillBlocks.SLAB_PATH_SLABS.get());
        LEGACY.put("millenaire:pathsandstone_slab", MillBlocks.SLAB_PATH_SANDSTONE.get());
        LEGACY.put("millenaire:pathsandstone_slab|1", MillBlocks.SLAB_PATH_SANDSTONE.get());
        LEGACY.put("millenaire:pathochretiles_slab", MillBlocks.SLAB_PATH_OCHRE_TILES.get());
        LEGACY.put("millenaire:pathochretiles_slab|1", MillBlocks.SLAB_PATH_OCHRE_TILES.get());
        LEGACY.put("millenaire:pathgravelslabs_slab", MillBlocks.SLAB_PATH_GRAVEL_SLABS.get());
        LEGACY.put("millenaire:pathgravelslabs_slab|1", MillBlocks.SLAB_PATH_GRAVEL_SLABS.get());
        LEGACY.put("millenaire:pathsnow_slab", MillBlocks.SLAB_PATH_SNOW.get());
        LEGACY.put("millenaire:pathsnow_slab|1", MillBlocks.SLAB_PATH_SNOW.get());

        // Ice & Snow
        LEGACY.put("millenaire:snowbrick", MillBlocks.SNOW_BRICK.get());
        LEGACY.put("millenaire:snowbrick|0", MillBlocks.SNOW_BRICK.get());
        LEGACY.put("millenaire:icebrick", MillBlocks.ICE_BRICK.get());
        LEGACY.put("millenaire:icebrick|0", MillBlocks.ICE_BRICK.get());
        LEGACY.put("millenaire:snowwall", MillBlocks.WALL_SNOW.get());
        LEGACY.put("millenaire:snowwall|0", MillBlocks.WALL_SNOW.get());

        // Painted bricks: silver → light_gray
        LEGACY.put("millenaire:painted_brick_silver", MillBlocks.PAINTED_BRICK_LIGHT_GRAY.get());

        // Decorated painted bricks: legacy "decorated_" → modern "deco_"
        LEGACY.put("millenaire:painted_brick_decorated_white", MillBlocks.PAINTED_BRICK_DECO_WHITE.get());
        LEGACY.put("millenaire:painted_brick_decorated_orange", MillBlocks.PAINTED_BRICK_DECO_ORANGE.get());
        LEGACY.put("millenaire:painted_brick_decorated_magenta", MillBlocks.PAINTED_BRICK_DECO_MAGENTA.get());
        LEGACY.put("millenaire:painted_brick_decorated_light_blue", MillBlocks.PAINTED_BRICK_DECO_LIGHT_BLUE.get());
        LEGACY.put("millenaire:painted_brick_decorated_yellow", MillBlocks.PAINTED_BRICK_DECO_YELLOW.get());
        LEGACY.put("millenaire:painted_brick_decorated_lime", MillBlocks.PAINTED_BRICK_DECO_LIME.get());
        LEGACY.put("millenaire:painted_brick_decorated_pink", MillBlocks.PAINTED_BRICK_DECO_PINK.get());
        LEGACY.put("millenaire:painted_brick_decorated_gray", MillBlocks.PAINTED_BRICK_DECO_GRAY.get());
        LEGACY.put("millenaire:painted_brick_decorated_silver", MillBlocks.PAINTED_BRICK_DECO_LIGHT_GRAY.get());
        LEGACY.put("millenaire:painted_brick_decorated_cyan", MillBlocks.PAINTED_BRICK_DECO_CYAN.get());
        LEGACY.put("millenaire:painted_brick_decorated_purple", MillBlocks.PAINTED_BRICK_DECO_PURPLE.get());
        LEGACY.put("millenaire:painted_brick_decorated_blue", MillBlocks.PAINTED_BRICK_DECO_BLUE.get());
        LEGACY.put("millenaire:painted_brick_decorated_brown", MillBlocks.PAINTED_BRICK_DECO_BROWN.get());
        LEGACY.put("millenaire:painted_brick_decorated_green", MillBlocks.PAINTED_BRICK_DECO_GREEN.get());
        LEGACY.put("millenaire:painted_brick_decorated_red", MillBlocks.PAINTED_BRICK_DECO_RED.get());
        LEGACY.put("millenaire:painted_brick_decorated_black", MillBlocks.PAINTED_BRICK_DECO_BLACK.get());

        // Painted brick stairs: no stair blocks exist yet, map to base painted bricks as fallback
        LEGACY.put("millenaire:stairs_painted_brick_white", MillBlocks.PAINTED_BRICK_WHITE.get());
        LEGACY.put("millenaire:stairs_painted_brick_orange", MillBlocks.PAINTED_BRICK_ORANGE.get());
        LEGACY.put("millenaire:stairs_painted_brick_magenta", MillBlocks.PAINTED_BRICK_MAGENTA.get());
        LEGACY.put("millenaire:stairs_painted_brick_light_blue", MillBlocks.PAINTED_BRICK_LIGHT_BLUE.get());
        LEGACY.put("millenaire:stairs_painted_brick_yellow", MillBlocks.PAINTED_BRICK_YELLOW.get());
        LEGACY.put("millenaire:stairs_painted_brick_lime", MillBlocks.PAINTED_BRICK_LIME.get());
        LEGACY.put("millenaire:stairs_painted_brick_pink", MillBlocks.PAINTED_BRICK_PINK.get());
        LEGACY.put("millenaire:stairs_painted_brick_gray", MillBlocks.PAINTED_BRICK_GRAY.get());
        LEGACY.put("millenaire:stairs_painted_brick_silver", MillBlocks.PAINTED_BRICK_LIGHT_GRAY.get());
        LEGACY.put("millenaire:stairs_painted_brick_cyan", MillBlocks.PAINTED_BRICK_CYAN.get());
        LEGACY.put("millenaire:stairs_painted_brick_purple", MillBlocks.PAINTED_BRICK_PURPLE.get());
        LEGACY.put("millenaire:stairs_painted_brick_blue", MillBlocks.PAINTED_BRICK_BLUE.get());
        LEGACY.put("millenaire:stairs_painted_brick_brown", MillBlocks.PAINTED_BRICK_BROWN.get());
        LEGACY.put("millenaire:stairs_painted_brick_green", MillBlocks.PAINTED_BRICK_GREEN.get());
        LEGACY.put("millenaire:stairs_painted_brick_red", MillBlocks.PAINTED_BRICK_RED.get());
        LEGACY.put("millenaire:stairs_painted_brick_black", MillBlocks.PAINTED_BRICK_BLACK.get());

        // Painted brick slabs: no slab blocks exist yet, map to base painted bricks as fallback
        LEGACY.put("millenaire:slab_painted_brick_white", MillBlocks.PAINTED_BRICK_WHITE.get());
        LEGACY.put("millenaire:slab_painted_brick_orange", MillBlocks.PAINTED_BRICK_ORANGE.get());
        LEGACY.put("millenaire:slab_painted_brick_magenta", MillBlocks.PAINTED_BRICK_MAGENTA.get());
        LEGACY.put("millenaire:slab_painted_brick_light_blue", MillBlocks.PAINTED_BRICK_LIGHT_BLUE.get());
        LEGACY.put("millenaire:slab_painted_brick_yellow", MillBlocks.PAINTED_BRICK_YELLOW.get());
        LEGACY.put("millenaire:slab_painted_brick_lime", MillBlocks.PAINTED_BRICK_LIME.get());
        LEGACY.put("millenaire:slab_painted_brick_pink", MillBlocks.PAINTED_BRICK_PINK.get());
        LEGACY.put("millenaire:slab_painted_brick_gray", MillBlocks.PAINTED_BRICK_GRAY.get());
        LEGACY.put("millenaire:slab_painted_brick_silver", MillBlocks.PAINTED_BRICK_LIGHT_GRAY.get());
        LEGACY.put("millenaire:slab_painted_brick_cyan", MillBlocks.PAINTED_BRICK_CYAN.get());
        LEGACY.put("millenaire:slab_painted_brick_purple", MillBlocks.PAINTED_BRICK_PURPLE.get());
        LEGACY.put("millenaire:slab_painted_brick_blue", MillBlocks.PAINTED_BRICK_BLUE.get());
        LEGACY.put("millenaire:slab_painted_brick_brown", MillBlocks.PAINTED_BRICK_BROWN.get());
        LEGACY.put("millenaire:slab_painted_brick_green", MillBlocks.PAINTED_BRICK_GREEN.get());
        LEGACY.put("millenaire:slab_painted_brick_red", MillBlocks.PAINTED_BRICK_RED.get());
        LEGACY.put("millenaire:slab_painted_brick_black", MillBlocks.PAINTED_BRICK_BLACK.get());

        // Painted brick walls: no wall blocks exist yet, map to base painted bricks as fallback
        LEGACY.put("millenaire:wall_painted_brick_white", MillBlocks.PAINTED_BRICK_WHITE.get());
        LEGACY.put("millenaire:wall_painted_brick_orange", MillBlocks.PAINTED_BRICK_ORANGE.get());
        LEGACY.put("millenaire:wall_painted_brick_magenta", MillBlocks.PAINTED_BRICK_MAGENTA.get());
        LEGACY.put("millenaire:wall_painted_brick_light_blue", MillBlocks.PAINTED_BRICK_LIGHT_BLUE.get());
        LEGACY.put("millenaire:wall_painted_brick_yellow", MillBlocks.PAINTED_BRICK_YELLOW.get());
        LEGACY.put("millenaire:wall_painted_brick_lime", MillBlocks.PAINTED_BRICK_LIME.get());
        LEGACY.put("millenaire:wall_painted_brick_pink", MillBlocks.PAINTED_BRICK_PINK.get());
        LEGACY.put("millenaire:wall_painted_brick_gray", MillBlocks.PAINTED_BRICK_GRAY.get());
        LEGACY.put("millenaire:wall_painted_brick_silver", MillBlocks.PAINTED_BRICK_LIGHT_GRAY.get());
        LEGACY.put("millenaire:wall_painted_brick_cyan", MillBlocks.PAINTED_BRICK_CYAN.get());
        LEGACY.put("millenaire:wall_painted_brick_purple", MillBlocks.PAINTED_BRICK_PURPLE.get());
        LEGACY.put("millenaire:wall_painted_brick_blue", MillBlocks.PAINTED_BRICK_BLUE.get());
        LEGACY.put("millenaire:wall_painted_brick_brown", MillBlocks.PAINTED_BRICK_BROWN.get());
        LEGACY.put("millenaire:wall_painted_brick_green", MillBlocks.PAINTED_BRICK_GREEN.get());
        LEGACY.put("millenaire:wall_painted_brick_red", MillBlocks.PAINTED_BRICK_RED.get());
        LEGACY.put("millenaire:wall_painted_brick_black", MillBlocks.PAINTED_BRICK_BLACK.get());

        // Tile slabs (legacy name format differs from registered name)
        LEGACY.put("millenaire:byzantine_tiles_slab", MillBlocks.SLAB_BYZANTINE_TILES.get());
        LEGACY.put("millenaire:gray_tiles_slab", MillBlocks.SLAB_GRAY_TILES.get());
        LEGACY.put("millenaire:green_tiles_slab", MillBlocks.SLAB_GREEN_TILES.get());
        LEGACY.put("millenaire:red_tiles_slab", MillBlocks.SLAB_RED_TILES.get());

        // Inuit carving → closest available decorative block
        LEGACY.put("millenaire:inuitcarving", MillBlocks.MILL_STATUE.get());

        // Sod
        LEGACY.put("millenaire:sod", MillBlocks.SOD.get());

        // Beds
        LEGACY.put("minecraft:bed", Blocks.RED_BED);
        LEGACY.put("millenaire:bed_straw", MillBlocks.BED_STRAW.get());
        LEGACY.put("millenaire:bed_charpoy", MillBlocks.BED_CHARPOY.get());

        // Nether wart
        LEGACY.put("minecraft:nether_wart", Blocks.NETHER_WART);
        LEGACY.put("minecraft:nether_wart|3", Blocks.NETHER_WART);

        // Grass path (1.12 used dirt meta)
        // Line 1384: grassPath;minecraft:dirt;0;false;148/122/65
        // This is actually grass_path in modern MC but that was removed, use dirt_path
        // Actually dirt_path doesn't exist as placeble block, just use dirt
    }

    private static void putLogVariants(String legacyId, String variant, Block modern) {
        LEGACY.put(legacyId + "|variant=" + variant + ",axis=y", modern);
        LEGACY.put(legacyId + "|variant=" + variant + ",axis=x", modern);
        LEGACY.put(legacyId + "|variant=" + variant + ",axis=z", modern);
    }

    private static void putAllFacing(String legacyId, Block modern) {
        for (String f : new String[]{"facing=east", "facing=west", "facing=north", "facing=south",
                "half=upper,hinge=left", "half=upper,hinge=right"}) {
            LEGACY.put(legacyId + "|" + f, modern);
        }
    }

    private static void putTerracotta(String color, Block modern) {
        LEGACY.put("minecraft:stained_hardened_clay|color=" + color, modern);
    }
}
