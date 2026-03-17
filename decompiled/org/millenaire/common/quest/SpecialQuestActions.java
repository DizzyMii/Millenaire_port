/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.item.EntityTNTPrimed
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.biome.Biome
 *  net.minecraftforge.fml.relauncher.ReflectionHelper
 */
package org.millenaire.common.quest;

import java.lang.reflect.Field;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.culture.Culture;
import org.millenaire.common.culture.VillageType;
import org.millenaire.common.entity.EntityTargetedBlaze;
import org.millenaire.common.entity.EntityTargetedGhast;
import org.millenaire.common.entity.EntityTargetedWitherSkeleton;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.network.ServerSender;
import org.millenaire.common.utilities.BlockItemUtilities;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.utilities.WorldUtilities;
import org.millenaire.common.village.Building;
import org.millenaire.common.world.MillWorldData;
import org.millenaire.common.world.WorldGenVillage;

public class SpecialQuestActions {
    private static final int MARVEL_MIN_DISTANCE = 200;
    public static final String COMPLETE = "_complete";
    public static final String EXPLORE_TAG = "action_explore_";
    public static final String ENCHANTMENTTABLE = "action_build_enchantment_table";
    public static final String UNDERWATER_GLASS = "action_underwater_glass";
    public static final String UNDERWATER_DIVE = "action_underwater_dive";
    public static final String TOPOFTHEWORLD = "action_topoftheworld";
    public static final String BOTTOMOFTHEWORLD = "action_bottomoftheworld";
    public static final String BOREHOLE = "action_borehole";
    public static final String BOREHOLETNT = "action_boreholetnt";
    public static final String BOREHOLETNTLIT = "action_boreholetntlit";
    public static final String THEVOID = "action_thevoid";
    public static final String MAYANSIEGE = "action_mayansiege";
    public static final String NORMANMARVEL_PICKLOCATION = "normanmarvel_picklocation";
    public static final String NORMANMARVEL_GENERATE = "normanmarvel_generate";
    public static final String NORMANMARVEL_LOCATION = "normanmarvel_location";
    private static final String NORMANMARVEL_VILLAGEPOS = "normanmarvel_villagepos";
    private static Field FIELD_BIOME_NAME = ReflectionHelper.findField(Biome.class, (String)"biomeName", (String)"field_76791_y");

    private static void indianCQHandleBottomOfTheWorld(MillWorldData mw, EntityPlayer player) {
        if (!mw.getProfile(player).isTagSet(BOTTOMOFTHEWORLD) || mw.getProfile(player).isTagSet("action_bottomoftheworld_complete")) {
            return;
        }
        if (player.field_70163_u < 4.0) {
            ServerSender.sendTranslatedSentence(player, '7', "actions.bottomoftheworld_success", new String[0]);
            mw.getProfile(player).clearTag(BOTTOMOFTHEWORLD);
            mw.getProfile(player).setTag("action_bottomoftheworld_complete");
            return;
        }
    }

    private static void indianCQHandleContinuousExplore(MillWorldData mw, EntityPlayer player, long worldTime, String biome, ResourceLocation mob, int nbMob, int minTravel) throws IllegalArgumentException, IllegalAccessException {
        int i;
        if (!mw.getProfile(player).isTagSet(EXPLORE_TAG + biome) || mw.getProfile(player).isTagSet(EXPLORE_TAG + biome + COMPLETE)) {
            return;
        }
        if (mw.world.func_72935_r()) {
            return;
        }
        String biomeName = ((String)FIELD_BIOME_NAME.get(mw.world.func_180494_b(player.func_180425_c()))).toLowerCase();
        if (biomeName.equals("extreme hills")) {
            biomeName = "mountain";
        }
        if (!biomeName.equals(biome)) {
            return;
        }
        int surface = WorldUtilities.findTopSoilBlock(mw.world, (int)player.field_70165_t, (int)player.field_70161_v);
        if (player.field_70163_u <= (double)(surface - 2)) {
            return;
        }
        String testnbstr = mw.getProfile(player).getActionData(biome + "_explore_nbcomplete");
        int nbtest = 0;
        if (testnbstr != null) {
            nbtest = Integer.parseInt(testnbstr);
            for (i = 1; i <= nbtest; ++i) {
                Point p;
                String pointstr = mw.getProfile(player).getActionData(biome + "_explore_point" + i);
                if (pointstr == null || !((p = new Point(pointstr)).horizontalDistanceTo((Entity)player) < (double)minTravel)) continue;
                return;
            }
        }
        if (++nbtest >= 20) {
            ServerSender.sendTranslatedSentence(player, '7', "actions." + biome + "_success", new String[0]);
            mw.getProfile(player).clearActionData(biome + "_explore_nbcomplete");
            for (i = 1; i <= 10; ++i) {
                mw.getProfile(player).clearActionData(biome + "_explore_point" + i);
            }
            mw.getProfile(player).clearTag(EXPLORE_TAG + biome);
            mw.getProfile(player).setTag(EXPLORE_TAG + biome + COMPLETE);
            return;
        }
        mw.getProfile(player).setActionData(biome + "_explore_point" + nbtest, new Point((Entity)player).getIntString());
        mw.getProfile(player).setActionData(biome + "_explore_nbcomplete", "" + nbtest);
        ServerSender.sendTranslatedSentence(player, '7', "actions." + biome + "_continue", "" + nbtest * 5);
        WorldUtilities.spawnMobsAround(mw.world, new Point((Entity)player), 20, mob, 2, 4);
    }

    private static void indianCQHandleEnchantmentTable(MillWorldData mw, EntityPlayer player) {
        if (!mw.getProfile(player).isTagSet(ENCHANTMENTTABLE) || mw.getProfile(player).isTagSet("action_build_enchantment_table_complete")) {
            return;
        }
        boolean closeEnough = false;
        for (int i = 0; i < mw.loneBuildingsList.types.size(); ++i) {
            if (!mw.loneBuildingsList.types.get(i).equals("sadhutree") || !(mw.loneBuildingsList.pos.get(i).distanceToSquared((Entity)player) < 100.0)) continue;
            closeEnough = true;
        }
        if (!closeEnough) {
            return;
        }
        for (int x = (int)player.field_70165_t - 5; x < (int)player.field_70165_t + 5; ++x) {
            for (int z = (int)player.field_70161_v - 5; z < (int)player.field_70161_v + 5; ++z) {
                for (int y = (int)player.field_70163_u - 3; y < (int)player.field_70163_u + 3; ++y) {
                    Block block = WorldUtilities.getBlock(mw.world, x, y, z);
                    if (block != Blocks.field_150381_bn) continue;
                    int nbBookShelves = 0;
                    for (int dx = -1; dx <= 1; ++dx) {
                        for (int dz = -1; dz <= 1; ++dz) {
                            if (dx == 0 && dz == 0 || !mw.world.func_175623_d(new BlockPos(x + dx, y, z + dz)) || !mw.world.func_175623_d(new BlockPos(x + dx, y + 1, z + dz))) continue;
                            if (WorldUtilities.getBlock(mw.world, x + dx * 2, y, z + dz * 2) == Blocks.field_150342_X) {
                                ++nbBookShelves;
                            }
                            if (WorldUtilities.getBlock(mw.world, x + dx * 2, y + 1, z + dz * 2) == Blocks.field_150342_X) {
                                ++nbBookShelves;
                            }
                            if (dz == 0 || dx == 0) continue;
                            if (WorldUtilities.getBlock(mw.world, x + dx * 2, y, z + dz) == Blocks.field_150342_X) {
                                ++nbBookShelves;
                            }
                            if (WorldUtilities.getBlock(mw.world, x + dx * 2, y + 1, z + dz) == Blocks.field_150342_X) {
                                ++nbBookShelves;
                            }
                            if (WorldUtilities.getBlock(mw.world, x + dx, y, z + dz * 2) == Blocks.field_150342_X) {
                                ++nbBookShelves;
                            }
                            if (WorldUtilities.getBlock(mw.world, x + dx, y + 1, z + dz * 2) != Blocks.field_150342_X) continue;
                            ++nbBookShelves;
                        }
                    }
                    if (nbBookShelves <= 0) continue;
                    ServerSender.sendTranslatedSentence(player, '7', "actions.enchantmenttable_success", new String[0]);
                    mw.getProfile(player).clearTag(ENCHANTMENTTABLE);
                    mw.getProfile(player).setTag("action_build_enchantment_table_complete");
                    return;
                }
            }
        }
    }

    private static void indianCQHandleTopOfTheWorld(MillWorldData mw, EntityPlayer player) {
        if (!mw.getProfile(player).isTagSet(TOPOFTHEWORLD) || mw.getProfile(player).isTagSet("action_topoftheworld_complete")) {
            return;
        }
        if (player.field_70163_u > 250.0) {
            ServerSender.sendTranslatedSentence(player, '7', "actions.topoftheworld_success", new String[0]);
            mw.getProfile(player).clearTag(TOPOFTHEWORLD);
            mw.getProfile(player).setTag("action_topoftheworld_complete");
            return;
        }
    }

    private static void indianCQHandleUnderwaterDive(MillWorldData mw, EntityPlayer player) {
        if (!mw.getProfile(player).isTagSet(UNDERWATER_DIVE) || mw.getProfile(player).isTagSet("action_underwater_dive_complete")) {
            return;
        }
        Point p = new Point((Entity)player);
        int nbWater = 0;
        while (WorldUtilities.getBlock(mw.world, p) == Blocks.field_150355_j) {
            ++nbWater;
            p = p.getAbove();
        }
        if (nbWater > 12) {
            ServerSender.sendTranslatedSentence(player, '7', "actions.underwaterdive_success", new String[0]);
            mw.getProfile(player).clearTag(UNDERWATER_DIVE);
            mw.getProfile(player).setTag("action_underwater_dive_complete");
            return;
        }
    }

    private static void indianCQHandleUnderwaterGlass(MillWorldData mw, EntityPlayer player) {
        if (!mw.getProfile(player).isTagSet(UNDERWATER_GLASS) || mw.getProfile(player).isTagSet("action_underwater_glass_complete")) {
            return;
        }
        Point p = new Point((Entity)player);
        Block block = WorldUtilities.getBlock(mw.world, p);
        while (block != null && !BlockItemUtilities.isBlockOpaqueCube(block) && block != Blocks.field_150359_w && block != Blocks.field_150410_aZ) {
            p = p.getAbove();
            block = WorldUtilities.getBlock(mw.world, p);
        }
        block = WorldUtilities.getBlock(mw.world, p);
        if (block != Blocks.field_150359_w && block != Blocks.field_150410_aZ) {
            return;
        }
        p = p.getAbove();
        int nbWater = 0;
        while (WorldUtilities.getBlock(mw.world, p) == Blocks.field_150355_j) {
            ++nbWater;
            p = p.getAbove();
        }
        if (nbWater > 15) {
            ServerSender.sendTranslatedSentence(player, '7', "actions.underwaterglass_success", new String[0]);
            mw.getProfile(player).clearTag(UNDERWATER_GLASS);
            mw.getProfile(player).setTag("action_underwater_glass_complete");
            return;
        }
        if (nbWater > 1) {
            ServerSender.sendTranslatedSentence(player, '7', "actions.underwaterglass_notdeepenough", new String[0]);
        }
    }

    private static void mayanCQHandleMayanSiege(MillWorldData mw, EntityPlayer player) {
        block9: {
            String siegeStatus;
            block8: {
                if (!mw.getProfile(player).isTagSet(MAYANSIEGE) || mw.getProfile(player).isTagSet("action_mayansiege_complete")) {
                    return;
                }
                siegeStatus = mw.getProfile(player).getActionData("mayan_siege_status");
                if (siegeStatus != null) break block8;
                for (Point p : mw.loneBuildingsList.pos) {
                    Object ent;
                    Point spawn;
                    int i;
                    Building b = mw.getBuilding(p);
                    if (b == null || !b.villageType.key.equals("questpyramid") || !(p.distanceTo((Entity)player) < 50.0)) continue;
                    int nbGhasts = 0;
                    int nbBlazes = 0;
                    int nbSkel = 0;
                    for (i = 0; i < 12; ++i) {
                        spawn = b.location.pos.getRelative(-10 + MillCommonUtilities.randomInt(20), 20.0, -10 + MillCommonUtilities.randomInt(20));
                        ent = (EntityTargetedGhast)WorldUtilities.spawnMobsSpawner(mw.world, spawn, Mill.ENTITY_TARGETED_GHAST);
                        if (ent == null) continue;
                        ent.target = b.location.pos.getRelative(0.0, 20.0, 0.0);
                        ++nbGhasts;
                    }
                    for (i = 0; i < 12; ++i) {
                        spawn = b.location.pos.getRelative(-5 + MillCommonUtilities.randomInt(10), 15.0, -5 + MillCommonUtilities.randomInt(10));
                        ent = (EntityTargetedBlaze)WorldUtilities.spawnMobsSpawner(mw.world, spawn, Mill.ENTITY_TARGETED_BLAZE);
                        if (ent == null) continue;
                        ((EntityTargetedBlaze)((Object)ent)).target = b.location.pos.getRelative(0.0, 10.0, 0.0);
                        ++nbBlazes;
                    }
                    for (i = 0; i < 5; ++i) {
                        spawn = b.location.pos.getRelative(5.0, 12.0, -5 + MillCommonUtilities.randomInt(10));
                        ent = WorldUtilities.spawnMobsSpawner(mw.world, spawn, Mill.ENTITY_TARGETED_WITHERSKELETON);
                        if (ent != null) {
                            ++nbSkel;
                        }
                        if ((ent = WorldUtilities.spawnMobsSpawner(mw.world, spawn = b.location.pos.getRelative(-5.0, 12.0, -5 + MillCommonUtilities.randomInt(10)), Mill.ENTITY_TARGETED_WITHERSKELETON)) == null) continue;
                        ++nbSkel;
                    }
                    mw.getProfile(player).setActionData("mayan_siege_status", "started");
                    mw.getProfile(player).setActionData("mayan_siege_ghasts", "" + nbGhasts);
                    mw.getProfile(player).setActionData("mayan_siege_blazes", "" + nbBlazes);
                    mw.getProfile(player).setActionData("mayan_siege_skeletons", "" + nbSkel);
                    ServerSender.sendTranslatedSentence(player, '7', "actions.mayan_siege_start", "" + nbGhasts, "" + nbBlazes, "" + nbSkel);
                }
                break block9;
            }
            if (!siegeStatus.equals("started")) break block9;
            for (Point p : mw.loneBuildingsList.pos) {
                Building b = mw.getBuilding(p);
                if (b == null || !b.villageType.key.equals("questpyramid") || !(p.distanceTo((Entity)player) < 50.0)) continue;
                List<Entity> mobs = WorldUtilities.getEntitiesWithinAABB(mw.world, EntityTargetedGhast.class, b.location.pos, 128, 128);
                int nbGhasts = mobs.size();
                mobs = WorldUtilities.getEntitiesWithinAABB(mw.world, EntityTargetedBlaze.class, b.location.pos, 128, 128);
                int nbBlazes = mobs.size();
                mobs = WorldUtilities.getEntitiesWithinAABB(mw.world, EntityTargetedWitherSkeleton.class, b.location.pos, 128, 128);
                int nbSkel = mobs.size();
                if (nbGhasts == 0 && nbBlazes == 0 && nbSkel == 0) {
                    mw.getProfile(player).setActionData("mayan_siege_status", "finished");
                    mw.getProfile(player).setTag("action_mayansiege_complete");
                    ServerSender.sendTranslatedSentence(player, '7', "actions.mayan_siege_success", new String[0]);
                    continue;
                }
                int oldGhasts = Integer.parseInt(mw.getProfile(player).getActionData("mayan_siege_ghasts"));
                int oldBlazes = Integer.parseInt(mw.getProfile(player).getActionData("mayan_siege_blazes"));
                int oldSkel = Integer.parseInt(mw.getProfile(player).getActionData("mayan_siege_skeletons"));
                if (oldGhasts == nbGhasts && oldBlazes == nbBlazes && oldSkel == nbSkel) continue;
                ServerSender.sendTranslatedSentence(player, '7', "actions.mayan_siege_update", "" + nbGhasts, "" + nbBlazes, "" + nbSkel);
                mw.getProfile(player).setActionData("mayan_siege_ghasts", "" + nbGhasts);
                mw.getProfile(player).setActionData("mayan_siege_blazes", "" + nbBlazes);
                mw.getProfile(player).setActionData("mayan_siege_skeletons", "" + nbSkel);
            }
        }
    }

    private static void normanCQHandleBorehole(MillWorldData mw, EntityPlayer player) {
        if (!mw.getProfile(player).isTagSet(BOREHOLE) || mw.getProfile(player).isTagSet("action_borehole_complete")) {
            return;
        }
        if (player.field_70163_u > 10.0) {
            return;
        }
        int nbok = 0;
        for (int x = (int)(player.field_70165_t - 2.0); x < (int)player.field_70165_t + 3; ++x) {
            for (int z = (int)(player.field_70161_v - 2.0); z < (int)player.field_70161_v + 3; ++z) {
                boolean ok = true;
                boolean stop = false;
                for (int y = 127; y > 0 && !stop; --y) {
                    Block block = WorldUtilities.getBlock(mw.world, x, y, z);
                    if (block == Blocks.field_150357_h) {
                        stop = true;
                        continue;
                    }
                    if (block == Blocks.field_150350_a) continue;
                    stop = true;
                    ok = false;
                }
                if (!ok) continue;
                ++nbok;
            }
        }
        if (nbok >= 25) {
            ServerSender.sendTranslatedSentence(player, '7', "actions.borehole_success", new String[0]);
            mw.getProfile(player).clearTag(BOREHOLE);
            mw.getProfile(player).setTag("action_borehole_complete");
            mw.getProfile(player).setActionData("action_borehole_pos", new Point((Entity)player).getIntString());
            return;
        }
        String maxKnownStr = mw.getProfile(player).getActionData("action_borehole_max");
        int maxKnown = 0;
        if (maxKnownStr != null) {
            maxKnown = Integer.parseInt(maxKnownStr);
        }
        if (nbok > maxKnown) {
            ServerSender.sendTranslatedSentence(player, '7', "actions.borehole_nblineok", "" + nbok);
            mw.getProfile(player).setActionData("action_borehole_max", "" + nbok);
        }
    }

    private static void normanCQHandleBoreholeTNT(MillWorldData mw, EntityPlayer player) {
        if (!mw.getProfile(player).isTagSet(BOREHOLETNT) || mw.getProfile(player).isTagSet("action_boreholetnt_complete")) {
            return;
        }
        String pStr = mw.getProfile(player).getActionData("action_borehole_pos");
        if (pStr == null) {
            return;
        }
        Point p = new Point(pStr);
        if (p.distanceToSquared((Entity)player) > 25.0) {
            return;
        }
        int nbTNT = 0;
        for (int x = p.getiX() - 2; x < p.getiX() + 3; ++x) {
            for (int z = p.getiZ() - 2; z < p.getiZ() + 3; ++z) {
                boolean obsidian = false;
                for (int y = 6; y > 0; --y) {
                    Block block = WorldUtilities.getBlock(mw.world, x, y, z);
                    if (block == Blocks.field_150343_Z) {
                        obsidian = true;
                        continue;
                    }
                    if (!obsidian || block != Blocks.field_150335_W) continue;
                    ++nbTNT;
                }
            }
        }
        if (nbTNT >= 20) {
            ServerSender.sendTranslatedSentence(player, '7', "actions.boreholetnt_success", new String[0]);
            mw.getProfile(player).clearTag(BOREHOLETNT);
            mw.getProfile(player).setTag("action_boreholetnt_complete");
            mw.getProfile(player).setTag(BOREHOLETNTLIT);
            mw.getProfile(player).clearActionData("action_boreholetnt_max");
            return;
        }
        if (nbTNT == 0) {
            return;
        }
        String maxKnownStr = mw.getProfile(player).getActionData("action_boreholetnt_max");
        int maxKnown = 0;
        if (maxKnownStr != null) {
            maxKnown = Integer.parseInt(maxKnownStr);
        }
        if (nbTNT > maxKnown) {
            ServerSender.sendTranslatedSentence(player, '7', "actions.boreholetnt_nbtnt", "" + nbTNT);
            mw.getProfile(player).setActionData("action_boreholetnt_max", "" + nbTNT);
        }
    }

    private static void normanCQHandleBoreholeTNTLit(MillWorldData mw, EntityPlayer player) {
        if (!mw.getProfile(player).isTagSet(BOREHOLETNTLIT) || mw.getProfile(player).isTagSet("action_boreholetntlit_complete")) {
            return;
        }
        Point p = new Point(mw.getProfile(player).getActionData("action_borehole_pos"));
        int nbtnt = mw.world.func_72872_a(EntityTNTPrimed.class, new AxisAlignedBB(p.x, p.y, p.z, p.x + 1.0, p.y + 1.0, p.z + 1.0).func_72321_a(8.0, 4.0, 8.0)).size();
        if (nbtnt > 0) {
            ServerSender.sendTranslatedSentence(player, '7', "actions.boreholetntlit_success", new String[0]);
            mw.getProfile(player).clearTag(BOREHOLETNTLIT);
            mw.getProfile(player).setTag("action_boreholetntlit_complete");
            return;
        }
    }

    private static void normanCQHandleTheVoid(MillWorldData mw, EntityPlayer player) {
        if (!mw.getProfile(player).isTagSet(THEVOID) || mw.getProfile(player).isTagSet("action_thevoid_complete")) {
            return;
        }
        if (player.field_70163_u > 30.0) {
            return;
        }
        for (int i = -5; i < 5; ++i) {
            for (int j = -5; j < 5; ++j) {
                Block block = WorldUtilities.getBlock(mw.world, (int)player.field_70165_t + i, 0, (int)player.field_70161_v + j);
                if (block != Blocks.field_150350_a) continue;
                ServerSender.sendTranslatedSentence(player, '7', "actions.thevoid_success", new String[0]);
                mw.getProfile(player).clearTag(THEVOID);
                mw.getProfile(player).setTag("action_thevoid_complete");
                return;
            }
        }
    }

    private static void normanMarvelGenerateMarvel(MillWorldData mw, EntityPlayer player) {
        if (!mw.getProfile(player).isTagSet(NORMANMARVEL_GENERATE)) {
            return;
        }
        String pStr = mw.getProfile(player).getActionData(NORMANMARVEL_LOCATION);
        if (pStr == null) {
            return;
        }
        Point pos = new Point(pStr);
        VillageType marvelVillageType = Culture.getCultureByName("norman").getVillageType("notredame");
        WorldGenVillage genVillage = new WorldGenVillage();
        boolean result = genVillage.generateVillageAtPoint(player.field_70170_p, MillCommonUtilities.random, pos.getiX(), pos.getiY(), pos.getiZ(), player, false, true, false, 200, marvelVillageType, null, null, 0.0f);
        if (result) {
            ServerSender.sendTranslatedSentence(player, '7', "actions.normanmarvel_generated", new String[0]);
            mw.getProfile(player).clearTag(NORMANMARVEL_PICKLOCATION);
            mw.getProfile(player).clearTag("normanmarvel_picklocation_complete");
            mw.getProfile(player).clearTag(NORMANMARVEL_GENERATE);
            Point villagePos = mw.villagesList.pos.get(mw.villagesList.pos.size() - 1);
            mw.getProfile(player).setActionData(NORMANMARVEL_VILLAGEPOS, villagePos.getIntString());
        } else {
            ServerSender.sendTranslatedSentence(player, '7', "actions.normanmarvel_notgenerated", new String[0]);
            mw.getProfile(player).clearTag("normanmarvel_picklocation_complete");
            mw.getProfile(player).clearTag(NORMANMARVEL_GENERATE);
        }
    }

    public static void normanMarvelPickLocation(MillWorldData mw, EntityPlayer player, Point pos) {
        double distance;
        double closestVillageDistance = Double.MAX_VALUE;
        for (Point thp : mw.villagesList.pos) {
            distance = pos.distanceTo(thp);
            if (!(distance < 200.0) || !(distance < closestVillageDistance)) continue;
            closestVillageDistance = distance;
        }
        for (Point thp : mw.loneBuildingsList.pos) {
            distance = pos.distanceTo(thp);
            if (!(distance < 200.0) || !(distance < closestVillageDistance)) continue;
            closestVillageDistance = distance;
        }
        if (closestVillageDistance == Double.MAX_VALUE) {
            mw.getProfile(player).setActionData(NORMANMARVEL_LOCATION, new Point((Entity)player).getIntString());
            mw.getProfile(player).setTag("normanmarvel_picklocation_complete");
            ServerSender.sendTranslatedSentence(player, '7', "actions.normanmarvel_locationset", new String[0]);
        } else {
            ServerSender.sendTranslatedSentence(player, '6', "actions.normanmarvel_villagetooclose", "200", "" + Math.round(closestVillageDistance));
        }
    }

    public static void onTick(MillWorldData mw, EntityPlayer player) {
        long startTime = mw.lastWorldUpdate > 0L ? Math.max(mw.lastWorldUpdate + 1L, mw.world.func_72820_D() - 10L) : mw.world.func_72820_D();
        for (long worldTime = startTime; worldTime <= mw.world.func_72820_D(); ++worldTime) {
            if (worldTime % 250L == 0L) {
                try {
                    SpecialQuestActions.indianCQHandleContinuousExplore(mw, player, worldTime, MillConfigValues.questBiomeForest, Mill.ENTITY_ZOMBIE, 2, 15);
                    SpecialQuestActions.indianCQHandleContinuousExplore(mw, player, worldTime, MillConfigValues.questBiomeDesert, Mill.ENTITY_SKELETON, 2, 15);
                    SpecialQuestActions.indianCQHandleContinuousExplore(mw, player, worldTime, MillConfigValues.questBiomeMountain, Mill.ENTITY_SPIDER, 2, 10);
                }
                catch (IllegalAccessException | IllegalArgumentException e) {
                    MillLog.printException("Error while handling Indian Creation Quest exploration:", e);
                }
            }
            if (worldTime % 500L == 0L) {
                SpecialQuestActions.indianCQHandleUnderwaterGlass(mw, player);
            }
            if (worldTime % 100L == 0L) {
                SpecialQuestActions.indianCQHandleUnderwaterDive(mw, player);
                SpecialQuestActions.indianCQHandleTopOfTheWorld(mw, player);
                SpecialQuestActions.indianCQHandleBottomOfTheWorld(mw, player);
                SpecialQuestActions.normanCQHandleBorehole(mw, player);
                SpecialQuestActions.normanCQHandleBoreholeTNT(mw, player);
                SpecialQuestActions.normanCQHandleTheVoid(mw, player);
                SpecialQuestActions.indianCQHandleEnchantmentTable(mw, player);
            }
            if (worldTime % 10L != 0L) continue;
            SpecialQuestActions.normanCQHandleBoreholeTNTLit(mw, player);
            SpecialQuestActions.mayanCQHandleMayanSiege(mw, player);
            SpecialQuestActions.normanMarvelGenerateMarvel(mw, player);
        }
    }
}

