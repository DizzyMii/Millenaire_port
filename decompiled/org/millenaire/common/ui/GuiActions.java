/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntitySign
 *  net.minecraft.util.EnumActionResult
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.text.TextComponentString
 *  net.minecraft.world.World
 */
package org.millenaire.common.ui;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import org.millenaire.common.advancements.MillAdvancements;
import org.millenaire.common.block.MillBlocks;
import org.millenaire.common.buildingplan.BuildingCustomPlan;
import org.millenaire.common.buildingplan.BuildingImportExport;
import org.millenaire.common.buildingplan.BuildingPlan;
import org.millenaire.common.buildingplan.BuildingPlanSet;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.culture.Culture;
import org.millenaire.common.culture.VillageType;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.item.ItemParchment;
import org.millenaire.common.item.MillItems;
import org.millenaire.common.network.ServerSender;
import org.millenaire.common.quest.QuestInstance;
import org.millenaire.common.quest.SpecialQuestActions;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.utilities.WorldUtilities;
import org.millenaire.common.village.Building;
import org.millenaire.common.village.BuildingProject;
import org.millenaire.common.village.VillagerRecord;
import org.millenaire.common.world.MillWorldData;
import org.millenaire.common.world.UserProfile;
import org.millenaire.common.world.WorldGenVillage;

public class GuiActions {
    public static final int VILLAGE_SCROLL_PRICE = 128;
    public static final int VILLAGE_SCROLL_REPUTATION = 8192;
    public static final int CROP_REPUTATION = 8192;
    public static final int CROP_PRICE = 512;
    public static final int CULTURE_CONTROL_REPUTATION = 131072;

    public static void activateMillChest(EntityPlayer player, Point p) {
        MillWorldData mw;
        World world = player.field_70170_p;
        if (MillConfigValues.DEV && (mw = Mill.getMillWorld(world)).buildingExists(p)) {
            Building ent = mw.getBuilding(p);
            if (player.field_71071_by.func_70448_g() != ItemStack.field_190927_a && player.field_71071_by.func_70448_g().func_77973_b() == Item.func_150898_a((Block)Blocks.field_150354_m)) {
                ent.testModeGoods();
                return;
            }
            if (player.field_71071_by.func_70448_g() != ItemStack.field_190927_a && player.field_71071_by.func_70448_g().func_77973_b() == Item.func_150898_a((Block)MillBlocks.PATHDIRT)) {
                ent.recalculatePaths(true);
                ent.clearOldPaths();
                ent.constructCalculatedPaths();
                return;
            }
            if (player.field_71071_by.func_70448_g() != ItemStack.field_190927_a && player.field_71071_by.func_70448_g().func_77973_b() == Item.func_150898_a((Block)MillBlocks.PATHGRAVEL)) {
                ent.clearOldPaths();
                return;
            }
            if (player.field_71071_by.func_70448_g() != ItemStack.field_190927_a && player.field_71071_by.func_70448_g().func_77973_b() == Item.func_150898_a((Block)MillBlocks.PATHDIRT_SLAB)) {
                ent.recalculatePaths(true);
                return;
            }
            if (player.field_71071_by.func_70448_g() != ItemStack.field_190927_a && player.field_71071_by.func_70448_g().func_77973_b() == MillItems.DENIER_OR) {
                ent.displayInfos(player);
                return;
            }
            if (player.field_71071_by.func_70448_g() != ItemStack.field_190927_a && player.field_71071_by.func_70448_g().func_77973_b() == Items.field_151069_bo) {
                mw.setGlobalTag("alchemy");
                MillLog.major(mw, "Set alchemy tag.");
                return;
            }
            if (player.field_71071_by.func_70448_g() != ItemStack.field_190927_a && player.field_71071_by.func_70448_g().func_77973_b() == MillItems.SUMMONING_WAND) {
                ent.displayInfos(player);
                try {
                    if (ent.isTownhall) {
                        ent.rushCurrentConstructions(false);
                    }
                    if (ent.isInn) {
                        ent.attemptMerchantMove(true);
                    }
                    if (ent.hasVisitors) {
                        ent.getVisitorManager().update(true);
                    }
                }
                catch (Exception e) {
                    MillLog.printException(e);
                }
                return;
            }
            if (player.field_71071_by.func_70448_g() != ItemStack.field_190927_a && player.field_71071_by.func_70448_g().func_77973_b() == Item.func_150898_a((Block)MillBlocks.PAINTED_BRICK_WHITE)) {
                ent.choseAndApplyBrickTheme();
                MillLog.major(mw, "Changed theme of village " + ent.getVillageQualifiedName() + " to: " + ent.brickColourTheme.key);
                return;
            }
        }
        ServerSender.displayMillChest(player, p);
    }

    public static void controlledBuildingsForgetBuilding(EntityPlayer player, Building townHall, BuildingProject project) {
        townHall.cancelBuilding(project.location);
    }

    public static void controlledBuildingsToggleUpgrades(EntityPlayer player, Building townHall, BuildingProject project, boolean allow) {
        project.location.upgradesAllowed = allow;
        if (allow) {
            townHall.noProjectsLeft = false;
        }
    }

    public static void controlledMilitaryCancelRaid(EntityPlayer player, Building townHall) {
        if (townHall.raidStart == 0L) {
            townHall.cancelRaid();
            if (!townHall.world.field_72995_K) {
                townHall.sendBuildingPacket(player, false);
            }
        }
    }

    public static void controlledMilitaryDiplomacy(EntityPlayer player, Building townHall, Point target, int level) {
        townHall.adjustRelation(target, level, true);
        if (!townHall.world.field_72995_K) {
            townHall.sendBuildingPacket(player, false);
        }
    }

    public static void controlledMilitaryPlanRaid(EntityPlayer player, Building townHall, Building target) {
        if (townHall.raidStart == 0L) {
            townHall.adjustRelation(target.getPos(), -100, true);
            townHall.planRaid(target);
            if (!townHall.world.field_72995_K) {
                townHall.sendBuildingPacket(player, false);
            }
        }
    }

    public static void hireExtend(EntityPlayer player, MillVillager villager) {
        villager.hiredBy = player.func_70005_c_();
        villager.hiredUntil += 24000L;
        MillCommonUtilities.changeMoney((IInventory)player.field_71071_by, -villager.getHireCost(player), player);
    }

    public static void hireHire(EntityPlayer player, MillVillager villager) {
        villager.hiredBy = player.func_70005_c_();
        villager.hiredUntil = villager.field_70170_p.func_72820_D() + 24000L;
        VillagerRecord vr = villager.getRecord();
        if (vr != null) {
            vr.awayhired = true;
        }
        MillAdvancements.HIRED.grant(player);
        MillCommonUtilities.changeMoney((IInventory)player.field_71071_by, -villager.getHireCost(player), player);
    }

    public static void hireRelease(EntityPlayer player, MillVillager villager) {
        villager.hiredBy = null;
        villager.hiredUntil = 0L;
        VillagerRecord vr = villager.getRecord();
        if (vr != null) {
            vr.awayhired = false;
        }
    }

    public static void hireToggleStance(EntityPlayer player, boolean stance) {
        AxisAlignedBB surroundings = new AxisAlignedBB(player.field_70165_t, player.field_70163_u, player.field_70161_v, player.field_70165_t + 1.0, player.field_70163_u + 1.0, player.field_70161_v + 1.0).func_72321_a(16.0, 8.0, 16.0).func_72321_a(-16.0, -8.0, -16.0);
        List list = player.field_70170_p.func_72872_a(MillVillager.class, surroundings);
        for (Object o : list) {
            MillVillager villager = (MillVillager)o;
            if (!player.func_70005_c_().equals(villager.hiredBy)) continue;
            villager.aggressiveStance = stance;
        }
    }

    public static void newBuilding(EntityPlayer player, Building townHall, Point pos, String planKey) {
        BuildingPlanSet set = townHall.culture.getBuildingPlanSet(planKey);
        if (set == null) {
            return;
        }
        BuildingPlan plan = set.getRandomStartingPlan();
        BuildingPlan.LocationReturn lr = plan.testSpot(townHall.winfo, townHall.regionMapper, townHall.getPos(), pos.getiX() - townHall.winfo.mapStartX, pos.getiZ() - townHall.winfo.mapStartZ, MillCommonUtilities.getRandom(), -1, true);
        if (lr.location == null) {
            String error = null;
            error = lr.errorCode == 3 ? "ui.constructionforbidden" : (lr.errorCode == 2 ? "ui.locationclash" : (lr.errorCode == 1 ? "ui.outsideradius" : (lr.errorCode == 4 ? "ui.wrongelevation" : (lr.errorCode == 5 ? "ui.danger" : (lr.errorCode == 6 ? "ui.notreachable" : "ui.unknownerror")))));
            if (MillConfigValues.DEV) {
                WorldUtilities.setBlock(townHall.mw.world, lr.errorPos.getRelative(0.0, 30.0, 0.0), Blocks.field_150351_n);
            }
            ServerSender.sendTranslatedSentence(player, '6', "ui.problemat", pos.distanceDirectionShort(lr.errorPos), error);
        } else {
            lr.location.level = -1;
            BuildingProject project = new BuildingProject(set);
            project.location = lr.location;
            GuiActions.setSign(townHall, lr.location.minx, lr.location.minz, project);
            GuiActions.setSign(townHall, lr.location.maxx, lr.location.minz, project);
            GuiActions.setSign(townHall, lr.location.minx, lr.location.maxz, project);
            GuiActions.setSign(townHall, lr.location.maxx, lr.location.maxz, project);
            townHall.buildingProjects.get((Object)BuildingProject.EnumProjects.CUSTOMBUILDINGS).add(project);
            townHall.noProjectsLeft = false;
            ServerSender.sendTranslatedSentence(player, '2', "ui.projectadded", new String[0]);
        }
    }

    public static void newCustomBuilding(EntityPlayer player, Building townHall, Point pos, String planKey) {
        BuildingCustomPlan customBuilding = townHall.culture.getBuildingCustom(planKey);
        if (customBuilding != null) {
            try {
                townHall.addCustomBuilding(customBuilding, pos);
            }
            catch (Exception e) {
                MillLog.printException("Exception when creation custom building: " + planKey, e);
            }
        }
    }

    public static void newVillageCreation(EntityPlayer player, Point pos, String cultureKey, String villageTypeKey) {
        Culture culture = Culture.getCultureByName(cultureKey);
        if (culture == null) {
            return;
        }
        VillageType villageType = culture.getVillageType(villageTypeKey);
        if (villageType == null) {
            return;
        }
        WorldGenVillage genVillage = new WorldGenVillage();
        boolean result = genVillage.generateVillageAtPoint(player.field_70170_p, MillCommonUtilities.random, pos.getiX(), pos.getiY(), pos.getiZ(), player, false, true, false, 0, villageType, null, null, 0.0f);
        if (result) {
            MillAdvancements.SUMMONING_WAND.grant(player);
            if (villageType.playerControlled && MillAdvancements.VILLAGE_LEADER_ADVANCEMENTS.containsKey(cultureKey)) {
                MillAdvancements.VILLAGE_LEADER_ADVANCEMENTS.get(cultureKey).grant(player);
            }
            if (villageType.playerControlled && villageType.customCentre != null) {
                MillAdvancements.AMATEUR_ARCHITECT.grant(player);
            }
        }
    }

    public static void pujasChangeEnchantment(EntityPlayer player, Building temple, int enchantmentId) {
        if (temple != null && temple.pujas != null) {
            temple.pujas.changeEnchantment(enchantmentId);
            temple.sendBuildingPacket(player, false);
            if (temple.pujas.type == 0) {
                MillAdvancements.PUJA.grant(player);
            } else if (temple.pujas.type == 1) {
                MillAdvancements.SACRIFICE.grant(player);
            }
        }
    }

    public static void questCompleteStep(EntityPlayer player, MillVillager villager) {
        UserProfile profile = Mill.getMillWorld(player.field_70170_p).getProfile(player);
        QuestInstance qi = profile.villagersInQuests.get(villager.getVillagerId());
        if (qi == null) {
            MillLog.error(villager, "Could not find quest instance for this villager.");
        } else {
            qi.completeStep(player, villager);
        }
    }

    public static void questRefuse(EntityPlayer player, MillVillager villager) {
        UserProfile profile = Mill.getMillWorld(player.field_70170_p).getProfile(player);
        QuestInstance qi = profile.villagersInQuests.get(villager.getVillagerId());
        if (qi == null) {
            MillLog.error(villager, "Could not find quest instance for this villager.");
        } else {
            qi.refuseQuest(player, villager);
        }
    }

    private static void setSign(Building townHall, int i, int j, BuildingProject project) {
        WorldUtilities.setBlockAndMetadata(townHall.world, i, WorldUtilities.findTopSoilBlock(townHall.world, i, j), j, Blocks.field_150472_an, 0, true, false);
        TileEntitySign sign = (TileEntitySign)townHall.world.func_175625_s(new BlockPos(i, WorldUtilities.findTopSoilBlock(townHall.world, i, j), j));
        if (sign != null) {
            sign.field_145915_a[0] = new TextComponentString(project.getNativeName());
            sign.field_145915_a[1] = new TextComponentString("");
            sign.field_145915_a[2] = new TextComponentString(project.getGameName());
            sign.field_145915_a[3] = new TextComponentString("");
        }
    }

    public static void updateCustomBuilding(EntityPlayer player, Building building) {
        if (building.location.getCustomPlan() != null) {
            building.location.getCustomPlan().registerResources(building, building.location);
        }
    }

    public static void useNegationWand(EntityPlayer player, Building townHall) {
        ServerSender.sendTranslatedSentence(player, '4', "negationwand.destroyed", townHall.villageType.name);
        if (!townHall.villageType.lonebuilding) {
            MillAdvancements.SCIPIO.grant(player);
        }
        townHall.destroyVillage();
    }

    public static EnumActionResult useSummoningWand(EntityPlayerMP player, Point pos) {
        MillWorldData mw = Mill.getMillWorld(player.field_70170_p);
        Block block = WorldUtilities.getBlock(player.field_70170_p, pos);
        Building closestVillage = mw.getClosestVillage(pos);
        if (closestVillage != null && pos.squareRadiusDistance(closestVillage.getPos()) < closestVillage.villageType.radius + 10) {
            if (block == Blocks.field_150472_an) {
                return EnumActionResult.FAIL;
            }
            if (closestVillage.controlledBy((EntityPlayer)player)) {
                Building b = closestVillage.getBuildingAtCoordPlanar(pos);
                if (b != null) {
                    if (b.location.isCustomBuilding) {
                        ServerSender.displayNewBuildingProjectGUI((EntityPlayer)player, closestVillage, pos);
                    } else {
                        ServerSender.sendTranslatedSentence((EntityPlayer)player, 'e', "ui.wand_locationinuse", new String[0]);
                    }
                } else {
                    ServerSender.displayNewBuildingProjectGUI((EntityPlayer)player, closestVillage, pos);
                }
                return EnumActionResult.SUCCESS;
            }
            ServerSender.sendTranslatedSentence((EntityPlayer)player, 'e', "ui.wand_invillagerange", closestVillage.getVillageQualifiedName());
            return EnumActionResult.FAIL;
        }
        if (block == Blocks.field_150472_an) {
            if (!Mill.proxy.isTrueServer() || player.field_71133_b.func_184103_al().func_152596_g(player.func_146103_bH())) {
                BuildingImportExport.summoningWandImportBuildingRequest((EntityPlayer)player, Mill.serverWorlds.get((int)0).world, pos);
            } else {
                ServerSender.sendTranslatedSentence((EntityPlayer)player, '4', "ui.serverimportforbidden", new String[0]);
            }
            return EnumActionResult.SUCCESS;
        }
        if (block == MillBlocks.LOCKED_CHEST) {
            return EnumActionResult.PASS;
        }
        if (block == Blocks.field_150343_Z) {
            WorldGenVillage genVillage = new WorldGenVillage();
            genVillage.generateVillageAtPoint(player.field_70170_p, MillCommonUtilities.random, pos.getiX(), pos.getiY(), pos.getiZ(), (EntityPlayer)player, false, true, false, 0, null, null, null, 0.0f);
            return EnumActionResult.SUCCESS;
        }
        if (block == Blocks.field_150340_R) {
            ServerSender.displayNewVillageGUI((EntityPlayer)player, pos);
            return EnumActionResult.SUCCESS;
        }
        if (mw.getProfile((EntityPlayer)player).isTagSet("normanmarvel_picklocation")) {
            SpecialQuestActions.normanMarvelPickLocation(mw, (EntityPlayer)player, pos);
            return EnumActionResult.SUCCESS;
        }
        ServerSender.sendTranslatedSentence((EntityPlayer)player, 'f', "ui.wandinstruction", new String[0]);
        return EnumActionResult.FAIL;
    }

    public static void villageChiefPerformBuilding(EntityPlayer player, MillVillager chief, String planKey) {
        BuildingPlan plan = chief.getTownHall().culture.getBuildingPlanSet(planKey).getRandomStartingPlan();
        chief.getTownHall().buildingsBought.add(planKey);
        MillCommonUtilities.changeMoney((IInventory)player.field_71071_by, -plan.price, player);
        ServerSender.sendTranslatedSentence(player, 'f', "ui.housebought", chief.func_70005_c_(), plan.nativeName);
    }

    public static void villageChiefPerformCrop(EntityPlayer player, MillVillager chief, String value) {
        UserProfile profile = Mill.getMillWorld(player.field_70170_p).getProfile(player);
        profile.setTag("cropplanting_" + value);
        MillCommonUtilities.changeMoney((IInventory)player.field_71071_by, -512, player);
        Item crop = Item.func_111206_d((String)("millenaire:" + value));
        ServerSender.sendTranslatedSentence(player, 'f', "ui.croplearned", chief.func_70005_c_(), "ui.crop." + crop.getRegistryName().func_110623_a());
    }

    public static void villageChiefPerformCultureControl(EntityPlayer player, MillVillager chief) {
        UserProfile profile = Mill.getMillWorld(player.field_70170_p).getProfile(player);
        profile.setTag("culturecontrol_" + chief.getCulture().key);
        ServerSender.sendTranslatedSentence(player, 'f', "ui.control_gotten", chief.func_70005_c_(), chief.getCulture().getAdjectiveTranslatedKey());
    }

    public static void villageChiefPerformDiplomacy(EntityPlayer player, MillVillager chief, Point village, boolean praise) {
        float effect = 0.0f;
        effect = praise ? 10.0f : -10.0f;
        int reputation = Math.min(chief.getTownHall().getReputation(player), 32768);
        float coeff = (float)((Math.log(reputation) / Math.log(32768.0) * 2.0 + (double)(reputation / 32768)) / 3.0);
        effect *= coeff;
        effect = (float)((double)effect * ((double)(MillCommonUtilities.randomInt(40) + 80) / 100.0));
        chief.getTownHall().adjustRelation(village, (int)effect, false);
        UserProfile profile = Mill.getMillWorld(player.field_70170_p).getProfile(player);
        profile.adjustDiplomacyPoint(chief.getTownHall(), -1);
        if (MillConfigValues.LogVillage >= 1) {
            MillLog.major(chief.getTownHall(), "Adjusted relation by " + effect + " (coef: " + coeff + ")");
        }
    }

    public static void villageChiefPerformHuntingDrop(EntityPlayer player, MillVillager chief, String value) {
        UserProfile profile = Mill.getMillWorld(player.field_70170_p).getProfile(player);
        profile.setTag("huntingdrop_" + value);
        MillCommonUtilities.changeMoney((IInventory)player.field_71071_by, -512, player);
        Item drop = Item.func_111206_d((String)("millenaire:" + value));
        ServerSender.sendTranslatedSentence(player, 'f', "ui.huntingdroplearned", chief.func_70005_c_(), "ui.huntingdrop." + drop.getRegistryName().func_110623_a());
    }

    public static void villageChiefPerformVillageScroll(EntityPlayer player, MillVillager chief) {
        for (int i = 0; i < Mill.getMillWorld((World)player.field_70170_p).villagesList.pos.size(); ++i) {
            Point p = Mill.getMillWorld((World)player.field_70170_p).villagesList.pos.get(i);
            if (!chief.getTownHall().getPos().sameBlock(p)) continue;
            MillCommonUtilities.changeMoney((IInventory)player.field_71071_by, -128, player);
            player.field_71071_by.func_70441_a(ItemParchment.createParchmentForVillage(chief.getTownHall()));
            ServerSender.sendTranslatedSentence(player, 'f', "ui.scrollbought", chief.func_70005_c_());
        }
    }
}

