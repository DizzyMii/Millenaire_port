/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.pathfinding.PathPoint
 *  net.minecraft.util.text.TextFormatting
 *  net.minecraft.world.World
 */
package org.millenaire.common.utilities;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import org.millenaire.common.block.MillBlocks;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.item.MillItems;
import org.millenaire.common.network.ServerSender;
import org.millenaire.common.pathing.atomicstryker.AStarConfig;
import org.millenaire.common.pathing.atomicstryker.AStarNode;
import org.millenaire.common.pathing.atomicstryker.AStarPathPlannerJPS;
import org.millenaire.common.pathing.atomicstryker.IAStarPathedEntity;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.utilities.ThreadSafeUtilities;
import org.millenaire.common.utilities.WorldUtilities;

public class DevModUtilities {
    private static HashMap<EntityPlayer, Integer> autoMoveDirection = new HashMap();
    private static HashMap<EntityPlayer, Integer> autoMoveTarget = new HashMap();

    public static void fillInFreeGoods(EntityPlayer player) {
        MillCommonUtilities.putItemsInChest((IInventory)player.field_71071_by, (Item)MillItems.JAPANESE_BLUE_LEGGINGS, 1);
        MillCommonUtilities.putItemsInChest((IInventory)player.field_71071_by, (Item)MillItems.JAPANESE_BLUE_BOOTS, 1);
        MillCommonUtilities.putItemsInChest((IInventory)player.field_71071_by, (Item)MillItems.JAPANESE_BLUE_HELMET, 1);
        MillCommonUtilities.putItemsInChest((IInventory)player.field_71071_by, (Item)MillItems.JAPANESE_BLUE_CHESTPLATE, 1);
        MillCommonUtilities.putItemsInChest((IInventory)player.field_71071_by, (Item)MillItems.JAPANESE_RED_LEGGINGS, 1);
        MillCommonUtilities.putItemsInChest((IInventory)player.field_71071_by, (Item)MillItems.JAPANESE_RED_BOOTS, 1);
        MillCommonUtilities.putItemsInChest((IInventory)player.field_71071_by, (Item)MillItems.JAPANESE_RED_HELMET, 1);
        MillCommonUtilities.putItemsInChest((IInventory)player.field_71071_by, (Item)MillItems.JAPANESE_RED_CHESTPLATE, 1);
        MillCommonUtilities.putItemsInChest((IInventory)player.field_71071_by, (Item)MillItems.JAPANESE_GUARD_LEGGINGS, 1);
        MillCommonUtilities.putItemsInChest((IInventory)player.field_71071_by, (Item)MillItems.JAPANESE_GUARD_BOOTS, 1);
        MillCommonUtilities.putItemsInChest((IInventory)player.field_71071_by, (Item)MillItems.JAPANESE_GUARD_HELMET, 1);
        MillCommonUtilities.putItemsInChest((IInventory)player.field_71071_by, (Item)MillItems.JAPANESE_GUARD_CHESTPLATE, 1);
        MillCommonUtilities.putItemsInChest((IInventory)player.field_71071_by, MillItems.SUMMONING_WAND, 1);
        MillCommonUtilities.putItemsInChest((IInventory)player.field_71071_by, MillItems.AMULET_SKOLL_HATI, 1);
        MillCommonUtilities.putItemsInChest((IInventory)player.field_71071_by, Items.field_151113_aN, 1);
        MillCommonUtilities.putItemsInChest((IInventory)player.field_71071_by, (Item)MillItems.NORMAN_AXE, 1);
        MillCommonUtilities.putItemsInChest((IInventory)player.field_71071_by, (Item)MillItems.NORMAN_PICKAXE, 1);
        MillCommonUtilities.putItemsInChest((IInventory)player.field_71071_by, (Item)MillItems.NORMAN_SHOVEL, 1);
        MillCommonUtilities.putItemsInChest((IInventory)player.field_71071_by, Blocks.field_150340_R, 0, 64);
        MillCommonUtilities.putItemsInChest((IInventory)player.field_71071_by, Blocks.field_150364_r, 64);
        MillCommonUtilities.putItemsInChest((IInventory)player.field_71071_by, Items.field_151044_h, 64);
        MillCommonUtilities.putItemsInChest((IInventory)player.field_71071_by, Blocks.field_150347_e, 128);
        MillCommonUtilities.putItemsInChest((IInventory)player.field_71071_by, Blocks.field_150348_b, 512);
        MillCommonUtilities.putItemsInChest((IInventory)player.field_71071_by, (Block)Blocks.field_150354_m, 128);
        MillCommonUtilities.putItemsInChest((IInventory)player.field_71071_by, Blocks.field_150325_L, 64);
        MillCommonUtilities.putItemsInChest((IInventory)player.field_71071_by, (Item)MillItems.CALVA, 0, 2);
        MillCommonUtilities.putItemsInChest((IInventory)player.field_71071_by, (Item)MillItems.CHICKEN_CURRY, 2);
        MillCommonUtilities.putItemsInChest((IInventory)player.field_71071_by, MillItems.RICE, 0, 64);
        MillCommonUtilities.putItemsInChest((IInventory)player.field_71071_by, MillItems.MAIZE, 0, 64);
        MillCommonUtilities.putItemsInChest((IInventory)player.field_71071_by, MillItems.TURMERIC, 0, 64);
    }

    public static void runAutoMove(World world) {
        for (Object o : world.field_73010_i) {
            EntityPlayer p;
            if (!(o instanceof EntityPlayer) || !autoMoveDirection.containsKey(p = (EntityPlayer)o)) continue;
            if (autoMoveDirection.get(p) == 1) {
                if ((double)autoMoveTarget.get(p).intValue() < p.field_70165_t) {
                    autoMoveDirection.put(p, -1);
                    autoMoveTarget.put(p, (int)(p.field_70165_t - 100000.0));
                    ServerSender.sendChat(p, TextFormatting.GREEN, "Auto-move: turning back.");
                }
            } else if (autoMoveDirection.get(p) == -1 && (double)autoMoveTarget.get(p).intValue() > p.field_70165_t) {
                autoMoveDirection.put(p, 1);
                autoMoveTarget.put(p, (int)(p.field_70165_t + 100000.0));
                ServerSender.sendChat(p, TextFormatting.GREEN, "Auto-move: turning back again.");
            }
            p.func_70634_a(p.field_70165_t + (double)autoMoveDirection.get(p).intValue() * 0.5, p.field_70163_u, p.field_70161_v);
            p.func_70080_a(p.field_70165_t + (double)autoMoveDirection.get(p).intValue() * 0.5, p.field_70163_u, p.field_70161_v, p.field_70177_z, p.field_70125_A);
        }
    }

    public static void testGetItemFromBlock() {
        long starttime = System.nanoTime();
        Iterator iterator = Block.field_149771_c.iterator();
        int count = 0;
        while (iterator.hasNext()) {
            Block block = (Block)iterator.next();
            Item.func_150898_a((Block)block);
            ++count;
        }
        MillLog.temp(null, "Took " + 1.0 * (double)(System.nanoTime() - starttime) / 1000000.0 + " ms to load " + count + " items from blocks.");
    }

    public static void testPaths(EntityPlayer player) {
        Point centre = new Point((Entity)player);
        MillLog.temp(null, "Attempting test path around: " + player);
        Point start = null;
        Point end = null;
        int toleranceMode = 0;
        for (int i = 0; i < 100 && (start == null || end == null); ++i) {
            for (int j = 0; j < 100 && (start == null || end == null); ++j) {
                for (int k = 0; k < 100 && (start == null || end == null); ++k) {
                    for (int l = 0; l < 8 && (start == null || end == null); ++l) {
                        Point p = centre.getRelative(i * (1 - (l & 1) * 2), j * (1 - (l & 2)), k * (1 - (l & 4) / 2));
                        Block block = WorldUtilities.getBlock(player.field_70170_p, p);
                        if (start == null && block == Blocks.field_150340_R) {
                            start = p;
                        }
                        if (end == null && block == Blocks.field_150339_S) {
                            end = p.getAbove();
                            toleranceMode = 0;
                            continue;
                        }
                        if (end == null && block == Blocks.field_150484_ah) {
                            end = p.getAbove();
                            toleranceMode = 1;
                            continue;
                        }
                        if (end != null || block != Blocks.field_150368_y) continue;
                        end = p.getAbove();
                        toleranceMode = 2;
                    }
                }
            }
        }
        if (start != null && end != null) {
            DevPathedEntity pathedEntity = new DevPathedEntity(player.field_70170_p, player);
            AStarConfig jpsConfig = toleranceMode == 1 ? new AStarConfig(true, false, false, true, true, 2, 2) : (toleranceMode == 2 ? new AStarConfig(true, false, false, true, true, 2, 20) : new AStarConfig(true, false, false, true, true));
            ServerSender.sendChat(player, TextFormatting.DARK_GREEN, "Calculating path. Tolerance H: " + jpsConfig.toleranceHorizontal);
            AStarPathPlannerJPS jpsPathPlanner = new AStarPathPlannerJPS(player.field_70170_p, pathedEntity, true);
            try {
                jpsPathPlanner.getPath(start.getiX(), start.getiY(), start.getiZ(), end.getiX(), end.getiY(), end.getiZ(), jpsConfig);
            }
            catch (ThreadSafeUtilities.ChunkAccessException e) {
                MillLog.printException(e);
            }
        } else {
            ServerSender.sendChat(player, TextFormatting.DARK_RED, "Could not find start or end: " + start + " - " + end);
        }
    }

    public static void toggleAutoMove(EntityPlayer player) {
        if (autoMoveDirection.containsKey(player)) {
            autoMoveDirection.remove(player);
            autoMoveTarget.remove(player);
            ServerSender.sendChat(player, TextFormatting.GREEN, "Auto-move disabled");
        } else {
            autoMoveDirection.put(player, 1);
            autoMoveTarget.put(player, (int)(player.field_70165_t + 100000.0));
            ServerSender.sendChat(player, TextFormatting.GREEN, "Auto-move enabled");
        }
    }

    public static void validateResourceMap(Map<InvItem, Integer> map) {
        int errors = 0;
        for (InvItem item : map.keySet()) {
            if (item == null) {
                MillLog.printException(new MillLog.MillenaireException("Found a null InvItem in map!"));
                ++errors;
                continue;
            }
            if (!map.containsKey(item)) {
                MillLog.printException(new MillLog.MillenaireException("Key: " + item + " not present in map???"));
                ++errors;
                continue;
            }
            if (map.get(item) != null) continue;
            MillLog.printException(new MillLog.MillenaireException("Key: " + item + " has null value in map."));
            ++errors;
        }
        if (map.size() > 0) {
            MillLog.error(null, "Validated map. Found " + errors + " amoung " + map.size() + " keys.");
        }
    }

    public static void villagerInteractDev(EntityPlayer entityplayer, MillVillager villager) {
        MillVillager child;
        if (villager.func_70631_g_()) {
            villager.growSize();
            ServerSender.sendChat(entityplayer, TextFormatting.GREEN, villager.func_70005_c_() + ": Size: " + villager.getSize() + " gender: " + villager.gender);
            if (entityplayer.field_71071_by.func_70448_g() != null && entityplayer.field_71071_by.func_70448_g().func_77973_b() == MillItems.SUMMONING_WAND) {
                villager.getRecord().size = 20;
                villager.growSize();
            }
        }
        if (entityplayer.field_71071_by.func_70448_g() == ItemStack.field_190927_a || entityplayer.field_71071_by.func_70448_g().func_77973_b() == Items.field_190931_a) {
            ServerSender.sendChat(entityplayer, TextFormatting.GREEN, villager.func_70005_c_() + ": Current goal: " + villager.getGoalLabel(villager.goalKey) + " Current pos: " + villager.getPos());
            ServerSender.sendChat(entityplayer, TextFormatting.GREEN, villager.func_70005_c_() + ": House: " + villager.housePoint + " Town Hall: " + villager.townHallPoint);
            ServerSender.sendChat(entityplayer, TextFormatting.GREEN, villager.func_70005_c_() + ": ID: " + villager.getVillagerId());
            if (villager.getRecord() != null) {
                ServerSender.sendChat(entityplayer, TextFormatting.GREEN, villager.func_70005_c_() + ": Spouse: " + villager.getRecord().spousesName);
            }
            if (villager.getPathDestPoint() != null && villager.pathEntity != null && villager.pathEntity.func_75874_d() > 1) {
                ServerSender.sendChat(entityplayer, TextFormatting.GREEN, villager.func_70005_c_() + ": Dest: " + villager.getPathDestPoint() + " distance: " + villager.getPathDestPoint().distanceTo((Entity)villager) + " stuck: " + villager.longDistanceStuck + " jump:" + villager.pathEntity.getNextTargetPathPoint());
            } else {
                ServerSender.sendChat(entityplayer, TextFormatting.GREEN, villager.func_70005_c_() + ": No dest point.");
            }
            String s = "";
            if (villager.getRecord() != null) {
                for (String tag : villager.getRecord().questTags) {
                    s = s + tag + " ";
                }
            }
            if (villager.mw.getProfile((EntityPlayer)entityplayer).villagersInQuests.containsKey(villager.getVillagerId())) {
                s = s + " quest: " + villager.mw.getProfile((EntityPlayer)entityplayer).villagersInQuests.get((Object)Long.valueOf((long)villager.getVillagerId())).quest.key + "/" + villager.mw.getProfile((EntityPlayer)entityplayer).villagersInQuests.get((Object)Long.valueOf((long)villager.getVillagerId())).getCurrentVillager().id;
            }
            if (s != null && s.length() > 0) {
                ServerSender.sendChat(entityplayer, TextFormatting.GREEN, "Tags: " + s);
            }
            s = "";
            for (InvItem key : villager.inventory.keySet()) {
                if (villager.inventory.get(key) <= 0) continue;
                s = s + key + ":" + villager.inventory.get(key) + " ";
            }
            if (villager.func_70638_az() != null) {
                s = s + "attacking: " + villager.func_70638_az() + " ";
            }
            if (s != null && s.length() > 0) {
                ServerSender.sendChat(entityplayer, TextFormatting.GREEN, "Inv: " + s);
            }
        } else if (entityplayer.field_71071_by.func_70448_g() != ItemStack.field_190927_a && entityplayer.field_71071_by.func_70448_g().func_77973_b() == Item.func_150898_a((Block)Blocks.field_150354_m)) {
            if (villager.hiredBy == null) {
                villager.hiredBy = entityplayer.func_70005_c_();
                ServerSender.sendChat(entityplayer, TextFormatting.GREEN, "Hired: " + entityplayer.func_70005_c_());
            } else {
                villager.hiredBy = null;
                ServerSender.sendChat(entityplayer, TextFormatting.GREEN, "No longer hired");
            }
        } else if (entityplayer.field_71071_by.func_70448_g() != ItemStack.field_190927_a && entityplayer.field_71071_by.func_70448_g().func_77973_b() == Item.func_150898_a((Block)Blocks.field_150346_d) && villager.pathEntity != null) {
            int meta = MillCommonUtilities.randomInt(16);
            for (PathPoint p : villager.pathEntity.pointsCopy) {
                if (WorldUtilities.getBlock(villager.field_70170_p, p.field_75839_a, p.field_75837_b - 1, p.field_75838_c) == MillBlocks.LOCKED_CHEST) continue;
                WorldUtilities.setBlockAndMetadata(villager.field_70170_p, new Point(p).getBelow(), Blocks.field_150325_L, meta);
            }
            PathPoint p = villager.pathEntity.getCurrentTargetPathPoint();
            if (p != null && WorldUtilities.getBlock(villager.field_70170_p, p.field_75839_a, p.field_75837_b - 1, p.field_75838_c) != MillBlocks.LOCKED_CHEST) {
                WorldUtilities.setBlockAndMetadata(villager.field_70170_p, new Point(p).getBelow(), Blocks.field_150340_R, 0);
            }
            if ((p = villager.pathEntity.getNextTargetPathPoint()) != null && WorldUtilities.getBlock(villager.field_70170_p, p.field_75839_a, p.field_75837_b - 1, p.field_75838_c) != MillBlocks.LOCKED_CHEST) {
                WorldUtilities.setBlockAndMetadata(villager.field_70170_p, new Point(p).getBelow(), Blocks.field_150484_ah, 0);
            }
            if ((p = villager.pathEntity.getPreviousTargetPathPoint()) != null && WorldUtilities.getBlock(villager.field_70170_p, p.field_75839_a, p.field_75837_b - 1, p.field_75838_c) != MillBlocks.LOCKED_CHEST) {
                WorldUtilities.setBlockAndMetadata(villager.field_70170_p, new Point(p).getBelow(), Blocks.field_150339_S, 0);
            }
        }
        if (villager.hasChildren() && entityplayer.field_71071_by.func_70448_g() != ItemStack.field_190927_a && entityplayer.field_71071_by.func_70448_g().func_77973_b() == MillItems.SUMMONING_WAND && (child = villager.getHouse().createChild(villager, villager.getTownHall(), villager.getRecord().spousesName)) != null) {
            child.getRecord().size = 20;
            child.growSize();
        }
    }

    private static class DevPathedEntity
    implements IAStarPathedEntity {
        World world;
        EntityPlayer caller;

        DevPathedEntity(World w, EntityPlayer p) {
            this.world = w;
            this.caller = p;
        }

        @Override
        public void onFoundPath(List<AStarNode> result) {
            int meta = MillCommonUtilities.randomInt(16);
            for (AStarNode node : result) {
                if (node == result.get(0) || node == result.get(result.size() - 1)) continue;
                WorldUtilities.setBlockAndMetadata(this.world, new Point(node).getBelow(), Blocks.field_150325_L, meta);
            }
        }

        @Override
        public void onNoPathAvailable() {
            ServerSender.sendChat(this.caller, TextFormatting.DARK_RED, "No path available.");
        }
    }
}

