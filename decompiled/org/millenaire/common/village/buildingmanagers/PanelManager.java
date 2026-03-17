/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockWallSign
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.MathHelper
 */
package org.millenaire.common.village.buildingmanagers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.BlockWallSign;
import net.minecraft.block.properties.IProperty;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import org.millenaire.common.block.MillBlocks;
import org.millenaire.common.buildingplan.BuildingPlan;
import org.millenaire.common.buildingplan.BuildingPlanSet;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.entity.TileEntityPanel;
import org.millenaire.common.goal.Goal;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.item.MillItems;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.utilities.WorldUtilities;
import org.millenaire.common.village.Building;
import org.millenaire.common.village.BuildingProject;
import org.millenaire.common.village.ConstructionIP;
import org.millenaire.common.village.VillagerRecord;

public class PanelManager {
    public static final int MAX_LINE_NB = 8;
    private static ItemStack FLOWER_PINK = new ItemStack((Block)Blocks.field_150328_O, 1, 7);
    private static ItemStack FLOWER_BLUE = new ItemStack((Block)Blocks.field_150328_O, 1, 1);
    private static Map<Item, ItemStack> itemStacks = new HashMap<Item, ItemStack>();
    public long lastSignUpdate = 0L;
    private final Building building;
    private final Building townHall;

    private static ItemStack stackFromBlock(Block block) {
        Item item = Item.func_150898_a((Block)block);
        return PanelManager.stackFromItem(item);
    }

    private static ItemStack stackFromItem(Item item) {
        if (itemStacks.containsKey(item)) {
            return itemStacks.get(item);
        }
        ItemStack stack = new ItemStack(item);
        itemStacks.put(item, stack);
        return stack;
    }

    public PanelManager(Building building) {
        this.building = building;
        this.townHall = building.getTownHall();
    }

    public WallStatusInfos computeWallInfos(List<BuildingProject> projects, int wallLevel) {
        HashMap<InvItem, Integer> resCost = new HashMap<InvItem, Integer>();
        HashMap<InvItem, Integer> resHas = new HashMap<InvItem, Integer>();
        int segmentsDone = 0;
        int segmentsToDo = 0;
        String wallTag = "wall_level_" + wallLevel;
        for (BuildingProject project : projects) {
            BuildingPlan startingPlan = project.getPlan(0, 0);
            if (startingPlan == null || !startingPlan.isWallSegment) continue;
            if (project.getExistingPlan() != null && project.getExistingPlan().containsTags(wallTag)) {
                for (InvItem key : project.getExistingPlan().resCost.keySet()) {
                    if (resCost.containsKey(key)) {
                        resCost.put(key, (Integer)resCost.get(key) + project.getExistingPlan().resCost.get(key));
                        resHas.put(key, (Integer)resHas.get(key) + project.getExistingPlan().resCost.get(key));
                        continue;
                    }
                    resCost.put(key, project.getExistingPlan().resCost.get(key));
                    resHas.put(key, project.getExistingPlan().resCost.get(key));
                }
                ++segmentsDone;
                continue;
            }
            if (project.getNextBuildingPlan(false) == null || !project.getNextBuildingPlan(false).containsTags(wallTag)) continue;
            for (InvItem key : project.getNextBuildingPlan((boolean)false).resCost.keySet()) {
                if (resCost.containsKey(key)) {
                    resCost.put(key, (Integer)resCost.get(key) + project.getNextBuildingPlan((boolean)false).resCost.get(key));
                    continue;
                }
                resCost.put(key, project.getNextBuildingPlan((boolean)false).resCost.get(key));
                resHas.put(key, 0);
            }
            ++segmentsToDo;
        }
        for (ConstructionIP cip : this.building.getConstructionsInProgress()) {
            if (cip.getBuildingLocation() == null || !cip.isWallConstruction()) continue;
            BuildingPlan plan = cip.getBuildingLocation().getPlan();
            for (InvItem key : plan.resCost.keySet()) {
                if (!resCost.containsKey(key)) continue;
                resHas.put(key, (Integer)resHas.get(key) + plan.resCost.get(key));
            }
        }
        for (InvItem key : resCost.keySet()) {
            int availableInTh = this.building.countGoods(key.getItem(), key.meta);
            resHas.put(key, (Integer)resHas.get(key) + availableInTh);
        }
        for (InvItem key : resCost.keySet()) {
            if ((Integer)resHas.get(key) <= (Integer)resCost.get(key)) continue;
            resHas.put(key, (Integer)resCost.get(key));
        }
        ArrayList<ResourceLine> resources = new ArrayList<ResourceLine>();
        for (InvItem key : resCost.keySet()) {
            resources.add(new ResourceLine(key, (Integer)resCost.get(key), (Integer)resHas.get(key)));
        }
        Collections.sort(resources);
        return new WallStatusInfos(resources, segmentsDone, segmentsToDo);
    }

    private TileEntityPanel.PanelUntranslatedLine createEmptyLine() {
        TileEntityPanel.PanelUntranslatedLine line = new TileEntityPanel.PanelUntranslatedLine();
        return line;
    }

    private TileEntityPanel.PanelUntranslatedLine createFullLine(String fullLine, ItemStack leftIcon, ItemStack rightIcon) {
        return this.createFullLine(new String[]{fullLine}, leftIcon, rightIcon);
    }

    private TileEntityPanel.PanelUntranslatedLine createFullLine(String[] fullLine, ItemStack leftIcon, ItemStack rightIcon) {
        TileEntityPanel.PanelUntranslatedLine line = new TileEntityPanel.PanelUntranslatedLine();
        line.setFullLine(fullLine);
        line.leftIcon = leftIcon != null ? leftIcon : ItemStack.field_190927_a;
        line.rightIcon = rightIcon != null ? rightIcon : ItemStack.field_190927_a;
        return line;
    }

    private void generateResourceLines(List<ResourceLine> resources, List<TileEntityPanel.PanelUntranslatedLine> lines) {
        int resPos = 0;
        while (resPos < Math.min(resources.size(), (8 - lines.size()) * 2)) {
            TileEntityPanel.PanelUntranslatedLine line;
            ResourceLine resource = resources.get(resPos);
            if (resource.cost < 100) {
                line = new TileEntityPanel.PanelUntranslatedLine();
                line.setLeftColumn(new String[]{"" + resource.has + "/" + resource.cost});
                line.leftIcon = resource.res.staticStack;
                if (resPos + 1 < resources.size()) {
                    resource = resources.get(resPos + 1);
                    line.rightColumn = new String[]{"" + resource.has + "/" + resource.cost};
                    line.middleIcon = resource.res.staticStack;
                }
                lines.add(line);
                resPos += 2;
                continue;
            }
            line = new TileEntityPanel.PanelUntranslatedLine();
            line.setFullLine(new String[]{"" + resource.has + "/" + resource.cost});
            line.leftIcon = resource.res.staticStack;
            line.centerLine = false;
            lines.add(line);
            ++resPos;
        }
    }

    private EnumSignType getSignType() {
        if (this.building.isTownhall) {
            if (this.building.villageType.isMarvel()) {
                return EnumSignType.MARVEL;
            }
            if (this.building.location.showTownHallSigns) {
                return EnumSignType.TOWNHALL;
            }
            if (this.building.location.getMaleResidents().size() > 0 || this.building.location.getFemaleResidents().size() > 0) {
                return EnumSignType.HOUSE;
            }
            return EnumSignType.DEFAULT;
        }
        if (this.building.hasVisitors) {
            return EnumSignType.VISITORS;
        }
        if (this.building.isInn) {
            return EnumSignType.INN;
        }
        if (this.building.containsTags("archives")) {
            return EnumSignType.ARCHIVES;
        }
        if (this.building.containsTags("borderpostsign")) {
            return EnumSignType.WALL;
        }
        if (this.building.location.getMaleResidents().size() > 0 || this.building.location.getFemaleResidents().size() > 0) {
            return EnumSignType.HOUSE;
        }
        return EnumSignType.DEFAULT;
    }

    private void updateArchiveSigns() {
        if (this.building.world.field_72995_K) {
            return;
        }
        EntityPlayer player = this.building.world.func_184137_a((double)this.building.getPos().getiX(), (double)this.building.getPos().getiY(), (double)this.building.getPos().getiZ(), 16.0, false);
        if (player == null) {
            return;
        }
        if (this.building.world.func_72820_D() - this.lastSignUpdate < 100L) {
            return;
        }
        if (this.building.getResManager().signs.size() == 0) {
            return;
        }
        for (int i = 0; i < this.building.getResManager().signs.size(); ++i) {
            EnumFacing facing;
            Point p = this.building.getResManager().signs.get(i);
            if (p == null || WorldUtilities.getBlock(this.building.world, p) == MillBlocks.PANEL || (facing = WorldUtilities.guessPanelFacing(this.building.world, p)) == null) continue;
            WorldUtilities.setBlockstate(this.building.world, p, MillBlocks.PANEL.func_176223_P().func_177226_a((IProperty)BlockWallSign.field_176412_a, (Comparable)facing), true, false);
        }
        int signId = 0;
        for (VillagerRecord vr : this.building.getTownHall().getVillagerRecords().values()) {
            TileEntityPanel sign;
            if (!vr.raidingVillage && !vr.getType().visitor && this.building.getResManager().signs.get(signId) != null && (sign = this.building.getResManager().signs.get(signId).getPanel(this.building.world)) != null) {
                ArrayList<TileEntityPanel.PanelUntranslatedLine> lines = new ArrayList<TileEntityPanel.PanelUntranslatedLine>();
                lines.add(this.createFullLine(vr.firstName, vr.getType().getIcon(), vr.getType().getIcon()));
                lines.add(this.createFullLine(vr.familyName, null, null));
                lines.add(this.createEmptyLine());
                if (vr.awayraiding) {
                    lines.add(this.createFullLine("panels.awayraiding", PanelManager.stackFromItem(Items.field_151036_c), PanelManager.stackFromItem(Items.field_151036_c)));
                } else if (vr.awayhired) {
                    lines.add(this.createFullLine("panels.awayhired", PanelManager.stackFromItem(MillItems.PURSE), PanelManager.stackFromItem(MillItems.PURSE)));
                } else if (vr.killed) {
                    lines.add(this.createFullLine("panels.dead", PanelManager.stackFromItem(Items.field_151144_bL), PanelManager.stackFromItem(Items.field_151144_bL)));
                } else {
                    MillVillager villager = this.building.mw.getVillagerById(vr.getVillagerId());
                    if (villager == null) {
                        lines.add(this.createFullLine("panels.missing", PanelManager.stackFromItem(Items.field_151144_bL), PanelManager.stackFromItem(Items.field_151144_bL)));
                    } else if (!villager.isVisitor()) {
                        String distance = "" + Math.floor(this.building.getPos().distanceTo((Entity)villager));
                        String direction = this.building.getPos().directionTo(villager.getPos());
                        String occupation = "";
                        if (villager.goalKey != null && Goal.goals.containsKey(villager.goalKey)) {
                            occupation = "goal." + Goal.goals.get(villager.goalKey).labelKey(villager);
                        }
                        lines.add(this.createFullLine(new String[]{"other.shortdistancedirection", distance, direction}, null, null));
                        lines.add(this.createFullLine(occupation, null, null));
                    }
                }
                sign.villager_id = vr.getVillagerId();
                sign.untranslatedLines = lines;
                sign.buildingPos = this.building.getTownHallPos();
                sign.panelType = 7;
                sign.texture = this.building.culture.panelTexture;
                sign.triggerUpdate();
                ++signId;
            }
            if (signId < this.building.getResManager().signs.size()) continue;
            break;
        }
        for (int i = signId; i < this.building.getResManager().signs.size(); ++i) {
            TileEntityPanel sign;
            if (this.building.getResManager().signs.get(i) == null || (sign = this.building.getResManager().signs.get(i).getPanel(this.building.world)) == null) continue;
            ArrayList<TileEntityPanel.PanelUntranslatedLine> lines = new ArrayList<TileEntityPanel.PanelUntranslatedLine>();
            lines.add(this.createFullLine("ui.reservedforvillager1", null, null));
            lines.add(this.createFullLine("ui.reservedforvillager2", null, null));
            lines.add(this.createEmptyLine());
            lines.add(this.createFullLine("#" + (i + 1), null, null));
            sign.untranslatedLines = lines;
            sign.buildingPos = this.building.getTownHallPos();
            sign.panelType = 0;
            sign.texture = this.building.culture.panelTexture;
            sign.triggerUpdate();
        }
        this.lastSignUpdate = this.building.world.func_72820_D();
    }

    private void updateBorderPostSign() {
        if (this.building.world.field_72995_K) {
            return;
        }
        EntityPlayer player = this.building.world.func_184137_a((double)this.building.getPos().getiX(), (double)this.building.getPos().getiY(), (double)this.building.getPos().getiZ(), 20.0, false);
        if (player == null) {
            return;
        }
        if (this.building.getResManager().signs.size() == 0) {
            return;
        }
        for (int i = 0; i < this.building.getResManager().signs.size(); ++i) {
            EnumFacing facing;
            Point p = this.building.getResManager().signs.get(i);
            if (p == null || WorldUtilities.getBlock(this.building.world, p) == MillBlocks.PANEL || (facing = WorldUtilities.guessPanelFacing(this.building.world, p)) == null) continue;
            WorldUtilities.setBlockstate(this.building.world, p, MillBlocks.PANEL.func_176223_P().func_177226_a((IProperty)BlockWallSign.field_176412_a, (Comparable)facing), true, false);
        }
        TileEntityPanel sign = this.building.getResManager().signs.get(0).getPanel(this.building.world);
        if (sign != null && this.building.getTownHall() != null) {
            ArrayList<TileEntityPanel.PanelUntranslatedLine> lines = new ArrayList<TileEntityPanel.PanelUntranslatedLine>();
            int nbvill = 0;
            for (VillagerRecord vr : this.building.getTownHall().getVillagerRecords().values()) {
                boolean belongsToVillage;
                if (vr == null || !(belongsToVillage = !vr.raidingVillage && vr.getType() != null && !vr.getType().visitor)) continue;
                ++nbvill;
            }
            lines.add(this.createFullLine(this.building.getTownHall().getVillageNameWithoutQualifier(), this.building.getTownHall().getBannerStack(), this.building.getTownHall().getBannerStack()));
            lines.add(this.createFullLine(this.building.getTownHall().getQualifier(), null, null));
            lines.add(this.createEmptyLine());
            lines.add(this.createFullLine(new String[]{"ui.populationnumber", "" + nbvill}, null, null));
            if (this.building.getTownHall().controlledBy != null) {
                lines.add(this.createEmptyLine());
                lines.add(this.createFullLine(this.building.getTownHall().controlledByName, PanelManager.stackFromItem((Item)Items.field_151169_ag), PanelManager.stackFromItem((Item)Items.field_151169_ag)));
            } else {
                lines.add(this.createEmptyLine());
                lines.add(this.createFullLine("Visits welcome", null, null));
                lines.add(this.createFullLine("ui.borderpost_constructionforbidden", null, null));
            }
            sign.untranslatedLines = lines;
            sign.buildingPos = this.building.getTownHallPos();
            sign.panelType = 8;
            sign.texture = this.building.culture.panelTexture;
            sign.triggerUpdate();
        }
    }

    private void updateDefaultSign() {
        TileEntityPanel sign;
        EnumFacing facing;
        if (this.building.world.field_72995_K) {
            return;
        }
        if (this.building.getResManager().signs.size() == 0) {
            return;
        }
        if (this.building.getPos() == null || this.building.location == null) {
            return;
        }
        EntityPlayer player = this.building.world.func_184137_a((double)this.building.getPos().getiX(), (double)this.building.getPos().getiY(), (double)this.building.getPos().getiZ(), 16.0, false);
        if (player == null) {
            return;
        }
        if (this.building.world.func_72820_D() - this.lastSignUpdate < 100L) {
            return;
        }
        Point p = this.building.getResManager().signs.get(0);
        if (p == null) {
            return;
        }
        if (WorldUtilities.getBlock(this.building.world, p.getiX(), p.getiY(), p.getiZ()) != MillBlocks.PANEL && (facing = WorldUtilities.guessPanelFacing(this.building.world, p)) != null) {
            WorldUtilities.setBlockstate(this.building.world, p, MillBlocks.PANEL.func_176223_P().func_177226_a((IProperty)BlockWallSign.field_176412_a, (Comparable)facing), true, false);
        }
        if ((sign = p.getPanel(this.building.world)) == null) {
            MillLog.error(this, "No TileEntitySign at: " + p);
        } else {
            ArrayList<TileEntityPanel.PanelUntranslatedLine> lines = new ArrayList<TileEntityPanel.PanelUntranslatedLine>();
            lines.add(this.createFullLine(this.building.getNativeBuildingName(), this.building.getIcon(), this.building.getIcon()));
            lines.add(this.createEmptyLine());
            lines.add(this.createFullLine(this.building.getGameBuildingName(), null, null));
            sign.untranslatedLines = lines;
            sign.buildingPos = this.building.getPos();
            sign.texture = this.building.culture.panelTexture;
            sign.triggerUpdate();
        }
        this.lastSignUpdate = this.building.world.func_72820_D();
    }

    private void updateHouseSign() {
        TileEntityPanel sign;
        EnumFacing facing;
        if (this.building.world.field_72995_K) {
            return;
        }
        if (this.building.getResManager().signs.size() == 0) {
            return;
        }
        if (this.building.getPos() == null || this.building.location == null) {
            return;
        }
        if (this.building.isTownhall && this.building.location.showTownHallSigns) {
            return;
        }
        EntityPlayer player = this.building.world.func_184137_a((double)this.building.getPos().getiX(), (double)this.building.getPos().getiY(), (double)this.building.getPos().getiZ(), 16.0, false);
        if (player == null) {
            return;
        }
        if (this.building.world.func_72820_D() - this.lastSignUpdate < 100L) {
            return;
        }
        VillagerRecord wife = null;
        VillagerRecord husband = null;
        int nbMaleAdults = 0;
        int nbFemaleAdults = 0;
        int nbResidents = 0;
        for (VillagerRecord vr : this.building.getTownHall().getVillagerRecords().values()) {
            if (!this.building.getPos().equals(vr.getHousePos())) continue;
            if (!(vr.gender != 2 || vr.getType() != null && vr.getType().isChild)) {
                wife = vr;
                ++nbFemaleAdults;
            }
            if (!(vr.gender != 1 || vr.getType() != null && vr.getType().isChild)) {
                husband = vr;
                ++nbMaleAdults;
            }
            ++nbResidents;
        }
        Point p = this.building.getResManager().signs.get(0);
        if (p == null) {
            return;
        }
        if (WorldUtilities.getBlock(this.building.world, p.getiX(), p.getiY(), p.getiZ()) != MillBlocks.PANEL && (facing = WorldUtilities.guessPanelFacing(this.building.world, p)) != null) {
            WorldUtilities.setBlockstate(this.building.world, p, MillBlocks.PANEL.func_176223_P().func_177226_a((IProperty)BlockWallSign.field_176412_a, (Comparable)facing), true, false);
        }
        if ((sign = p.getPanel(this.building.world)) == null) {
            MillLog.error(this, "No TileEntitySign at: " + p);
        } else {
            ArrayList<TileEntityPanel.PanelUntranslatedLine> lines = new ArrayList<TileEntityPanel.PanelUntranslatedLine>();
            lines.add(this.createFullLine(this.building.getNativeBuildingName(), this.building.getIcon(), this.building.getIcon()));
            lines.add(this.createEmptyLine());
            if ((wife != null || husband != null) && nbMaleAdults < 2 && nbFemaleAdults < 2) {
                if (husband != null && wife != null) {
                    lines.add(this.createFullLine(new String[]{"panels.nameand", wife.firstName}, wife.getType().getIcon(), wife.getType().getIcon()));
                    lines.add(this.createFullLine(husband.firstName, husband.getType().getIcon(), husband.getType().getIcon()));
                    lines.add(this.createFullLine(husband.familyName, null, null));
                } else if (husband != null) {
                    lines.add(this.createFullLine(husband.firstName, husband.getType().getIcon(), husband.getType().getIcon()));
                    lines.add(this.createFullLine(husband.familyName, null, null));
                } else if (wife != null) {
                    lines.add(this.createFullLine(wife.firstName, wife.getType().getIcon(), wife.getType().getIcon()));
                    lines.add(this.createFullLine(wife.familyName, null, null));
                }
            } else if (nbResidents > 0) {
                for (VillagerRecord vr : this.building.getTownHall().getVillagerRecords().values()) {
                    if (!this.building.getPos().equals(vr.getHousePos())) continue;
                    lines.add(this.createFullLine(vr.firstName, vr.getType().getIcon(), vr.getType().getIcon()));
                }
            } else {
                lines.add(this.createFullLine("ui.currentlyempty1", null, null));
                lines.add(this.createFullLine("ui.currentlyempty2", null, null));
            }
            sign.untranslatedLines = lines;
            sign.buildingPos = this.building.getPos();
            sign.panelType = 5;
            sign.texture = this.building.culture.panelTexture;
            sign.triggerUpdate();
        }
        this.lastSignUpdate = this.building.world.func_72820_D();
    }

    private void updateInnSign() {
        if (this.building.world.field_72995_K) {
            return;
        }
        EntityPlayer player = this.building.world.func_184137_a((double)this.building.getPos().getiX(), (double)this.building.getPos().getiY(), (double)this.building.getPos().getiZ(), 20.0, false);
        if (player == null) {
            return;
        }
        if (this.building.getResManager().signs.size() == 0) {
            return;
        }
        for (int i = 0; i < this.building.getResManager().signs.size(); ++i) {
            EnumFacing facing;
            Point p = this.building.getResManager().signs.get(i);
            if (p == null || WorldUtilities.getBlock(this.building.world, p) == MillBlocks.PANEL || (facing = WorldUtilities.guessPanelFacing(this.building.world, p)) == null) continue;
            WorldUtilities.setBlockstate(this.building.world, p, MillBlocks.PANEL.func_176223_P().func_177226_a((IProperty)BlockWallSign.field_176412_a, (Comparable)facing), true, false);
        }
        TileEntityPanel sign = this.building.getResManager().signs.get(0).getPanel(this.building.world);
        if (sign != null) {
            ArrayList<TileEntityPanel.PanelUntranslatedLine> lines = new ArrayList<TileEntityPanel.PanelUntranslatedLine>();
            lines.add(this.createFullLine(this.building.getNativeBuildingName(), this.building.getIcon(), this.building.getIcon()));
            lines.add(this.createEmptyLine());
            lines.add(this.createFullLine("ui.visitorslist1", null, null));
            lines.add(this.createFullLine("ui.visitorslist2", null, null));
            sign.untranslatedLines = lines;
            sign.buildingPos = this.building.getPos();
            sign.panelType = 11;
            sign.texture = this.building.culture.panelTexture;
            sign.triggerUpdate();
        }
        if (this.building.getResManager().signs.size() < 2) {
            return;
        }
        sign = this.building.getResManager().signs.get(1).getPanel(this.building.world);
        if (sign != null) {
            ArrayList<String[]> linesFull = new ArrayList<String[]>();
            ArrayList<ItemStack> icons = new ArrayList<ItemStack>();
            linesFull.add(new String[]{"ui.goodstraded"});
            linesFull.add(new String[]{""});
            linesFull.add(new String[]{"ui.import_total", "" + MillCommonUtilities.getInvItemHashTotal(this.building.imported)});
            linesFull.add(new String[]{"ui.export_total", "" + MillCommonUtilities.getInvItemHashTotal(this.building.exported)});
            icons.add(PanelManager.stackFromBlock((Block)Blocks.field_150486_ae));
            sign.buildingPos = this.building.getPos();
            sign.panelType = 10;
            sign.texture = this.building.culture.panelTexture;
            sign.triggerUpdate();
        }
    }

    private void updateMarvelDonationsSign(TileEntityPanel sign) {
        if (sign != null) {
            HashSet<String> villages = new HashSet<String>();
            for (String s : this.townHall.getMarvelManager().getDonationList()) {
                String village = s.split(";")[1];
                villages.add(village);
            }
            ArrayList<TileEntityPanel.PanelUntranslatedLine> lines = new ArrayList<TileEntityPanel.PanelUntranslatedLine>();
            lines.add(this.createFullLine("ui.paneldonations1", PanelManager.stackFromItem(MillItems.DENIER_OR), PanelManager.stackFromItem(MillItems.DENIER_OR)));
            lines.add(this.createEmptyLine());
            lines.add(this.createFullLine(new String[]{"ui.paneldonations2", "" + this.townHall.getMarvelManager().getDonationList().size()}, null, null));
            lines.add(this.createFullLine(new String[]{"ui.paneldonations3", "" + villages.size()}, null, null));
            sign.untranslatedLines = lines;
            sign.buildingPos = this.building.getPos();
            sign.panelType = 20;
            sign.texture = this.building.culture.panelTexture;
            sign.triggerUpdate();
        }
    }

    private void updateMarvelProjectsSign(TileEntityPanel sign) {
        if (sign != null) {
            List<BuildingProject> projects = this.townHall.getFlatProjectList();
            int totalProjects = 0;
            int doneProjects = 0;
            for (BuildingProject project : projects) {
                boolean obsolete;
                BuildingPlan plan = project.planSet.getFirstStartingPlan();
                BuildingPlan parentPlan = project.parentPlan;
                if (!plan.containsTags("marvel") && (parentPlan == null || !parentPlan.containsTags("marvel"))) continue;
                totalProjects += project.planSet.plans.get(0).length;
                if (project.location == null || project.location.level < 0) continue;
                boolean bl = obsolete = project.planSet != null && project.location.version != project.planSet.plans.get((int)project.location.getVariation())[0].version;
                if (project.location.level + 1 >= project.getLevelsNumber(project.location.getVariation())) {
                    doneProjects += project.planSet.plans.get(0).length;
                    continue;
                }
                if (obsolete) {
                    doneProjects += project.location.level + 1;
                    continue;
                }
                doneProjects += project.location.level + 1;
            }
            ArrayList<TileEntityPanel.PanelUntranslatedLine> lines = new ArrayList<TileEntityPanel.PanelUntranslatedLine>();
            lines.add(this.createFullLine("ui.panelmarvelprojects", PanelManager.stackFromItem(Items.field_151037_a), PanelManager.stackFromItem(Items.field_151037_a)));
            lines.add(this.createEmptyLine());
            lines.add(this.createFullLine(new String[]{"ui.panelmarvelprojectsdone", "" + doneProjects}, null, null));
            lines.add(this.createFullLine(new String[]{"ui.panelmarvelprojectstotal", "" + totalProjects}, null, null));
            sign.untranslatedLines = lines;
            sign.buildingPos = this.building.getPos();
            sign.panelType = 3;
            sign.texture = this.building.culture.panelTexture;
            sign.triggerUpdate();
        }
    }

    private void updateMarvelResourcesSign(TileEntityPanel sign) {
        if (sign != null) {
            Map<InvItem, Integer> totalCost = this.townHall.villageType.computeVillageTypeCost();
            Map<InvItem, Integer> remainingNeeds = this.townHall.getMarvelManager().computeNeeds();
            int totalCostSum = 0;
            int remainingNeedsSum = 0;
            for (Integer cost : totalCost.values()) {
                totalCostSum += cost.intValue();
            }
            for (Integer needs : remainingNeeds.values()) {
                if (needs <= 0) continue;
                remainingNeedsSum += needs.intValue();
            }
            ArrayList<TileEntityPanel.PanelUntranslatedLine> lines = new ArrayList<TileEntityPanel.PanelUntranslatedLine>();
            lines.add(this.createFullLine("ui.panelmarvelres1", PanelManager.stackFromItem(Item.func_150898_a((Block)Blocks.field_150486_ae)), PanelManager.stackFromItem(Item.func_150898_a((Block)Blocks.field_150486_ae))));
            lines.add(this.createFullLine("ui.panelmarvelres2", null, null));
            lines.add(this.createEmptyLine());
            lines.add(this.createFullLine(new String[]{"ui.panelmarvelrescount", String.format("%,d", totalCostSum - remainingNeedsSum), String.format("%,d", totalCostSum)}, null, null));
            sign.untranslatedLines = lines;
            sign.buildingPos = this.building.getPos();
            sign.panelType = 3;
            sign.texture = this.building.culture.panelTexture;
            sign.triggerUpdate();
        }
    }

    private void updateMarvelSigns(boolean forced) {
        if (this.townHall.world.field_72995_K) {
            return;
        }
        EntityPlayer player = this.townHall.world.func_184137_a((double)this.townHall.getPos().getiX(), (double)this.townHall.getPos().getiY(), (double)this.townHall.getPos().getiZ(), 20.0, false);
        if (player == null) {
            return;
        }
        if (!forced && this.townHall.world.func_72820_D() - this.lastSignUpdate < 40L) {
            return;
        }
        if (this.townHall.getResManager().signs.size() < 7) {
            return;
        }
        for (int i = 0; i < this.townHall.getResManager().signs.size(); ++i) {
            EnumFacing facing;
            Point p = this.townHall.getResManager().signs.get(i);
            if (p == null || WorldUtilities.getBlock(this.townHall.world, p) == MillBlocks.PANEL || (facing = WorldUtilities.guessPanelFacing(this.townHall.world, p)) == null) continue;
            WorldUtilities.setBlockstate(this.townHall.world, p, MillBlocks.PANEL.func_176223_P().func_177226_a((IProperty)BlockWallSign.field_176412_a, (Comparable)facing), true, false);
        }
        int signPos = 0;
        TileEntityPanel sign = (TileEntityPanel)this.townHall.world.func_175625_s(this.townHall.getResManager().signs.get(signPos).getBlockPos());
        this.updateSignTHVillageName(sign);
        sign = this.townHall.getResManager().signs.get(++signPos).getPanel(this.townHall.world);
        this.updateSignTHResources(sign);
        ++signPos;
        ++signPos;
        sign = this.townHall.getResManager().signs.get(++signPos).getPanel(this.townHall.world);
        this.updateSignTHProject(sign);
        sign = this.townHall.getResManager().signs.get(++signPos).getPanel(this.townHall.world);
        this.updateSignTHConstruction(sign);
        sign = this.townHall.getResManager().signs.get(++signPos).getPanel(this.townHall.world);
        this.updateSignTHEtatCivil(sign);
        sign = this.townHall.getResManager().signs.get(++signPos).getPanel(this.townHall.world);
        this.updateSignTHMap(sign);
        sign = this.townHall.getResManager().signs.get(++signPos).getPanel(this.townHall.world);
        this.updateSignTHMilitary(sign);
        sign = this.townHall.getResManager().signs.get(signPos).getPanel(this.townHall.world);
        this.updateMarvelProjectsSign(sign);
        sign = this.townHall.getResManager().signs.get(++signPos).getPanel(this.townHall.world);
        this.updateMarvelResourcesSign(sign);
        sign = this.townHall.getResManager().signs.get(++signPos).getPanel(this.townHall.world);
        this.updateMarvelDonationsSign(sign);
        ++signPos;
        this.lastSignUpdate = this.townHall.world.func_72820_D();
    }

    public void updateSigns() {
        EnumSignType type = this.getSignType();
        if (type == EnumSignType.MARVEL) {
            this.updateMarvelSigns(false);
        } else if (type == EnumSignType.TOWNHALL) {
            this.updateTownHallSigns(false);
        } else if (type == EnumSignType.ARCHIVES) {
            this.updateArchiveSigns();
        } else if (type == EnumSignType.VISITORS) {
            this.updateVisitorsSigns();
        } else if (type == EnumSignType.INN) {
            this.updateInnSign();
        } else if (type == EnumSignType.WALL) {
            this.updateBorderPostSign();
        } else if (type == EnumSignType.HOUSE) {
            this.updateHouseSign();
        } else if (type == EnumSignType.DEFAULT) {
            this.updateDefaultSign();
        }
    }

    private void updateSignTHConstruction(TileEntityPanel sign) {
        if (sign != null) {
            ArrayList<TileEntityPanel.PanelUntranslatedLine> lines = new ArrayList<TileEntityPanel.PanelUntranslatedLine>();
            ConstructionIP activeCIP = null;
            int nbActiveCIP = 0;
            for (ConstructionIP cip : this.building.getConstructionsInProgress()) {
                if (cip.getBuildingLocation() == null) continue;
                ++nbActiveCIP;
                if (activeCIP != null) continue;
                activeCIP = cip;
            }
            if (nbActiveCIP == 1) {
                String[] loc;
                BuildingPlanSet planSet = this.building.culture.getBuildingPlanSet(activeCIP.getBuildingLocation().planKey);
                String planName = planSet.getNameNative();
                String[] status = activeCIP.getBuildingLocation().level == 0 ? new String[]{"ui.inconstruction"} : new String[]{"ui.upgrading", "" + activeCIP.getBuildingLocation().level};
                if (activeCIP.getBuildingLocation() != null) {
                    int distance = MathHelper.func_76128_c((double)this.building.getPos().distanceTo(activeCIP.getBuildingLocation().pos));
                    String direction = this.building.getPos().directionTo(activeCIP.getBuildingLocation().pos);
                    loc = new String[]{"other.shortdistancedirection", "" + distance, "" + direction};
                } else {
                    loc = new String[]{""};
                }
                String[] constr = activeCIP.getBblocks() != null && activeCIP.getBblocks().length > 0 ? new String[]{"ui.progress", "" + (int)Math.floor(activeCIP.getBblocksPos() * 100 / activeCIP.getBblocks().length)} : new String[]{"ui.progressnopercent"};
                lines.add(this.createFullLine(planName, planSet.getIcon(), planSet.getIcon()));
                lines.add(this.createEmptyLine());
                lines.add(this.createFullLine(constr, null, null));
                lines.add(this.createFullLine(status, null, null));
                lines.add(this.createFullLine(loc, null, null));
            } else if (nbActiveCIP > 1) {
                lines.add(this.createFullLine(new String[]{"ui.xconstructions", "" + nbActiveCIP}, null, null));
                lines.add(this.createEmptyLine());
                int cipPos = 0;
                for (ConstructionIP cip : this.building.getConstructionsInProgress()) {
                    if (cip.getBuildingLocation() == null || cipPos >= 4) continue;
                    String planName = this.building.culture.getBuildingPlanSet(cip.getBuildingLocation().planKey).getNameNative();
                    ItemStack icon = this.building.culture.getBuildingPlanSet(cip.getBuildingLocation().planKey).getIcon();
                    String level = "l0";
                    if (cip.getBuildingLocation().level > 0) {
                        level = "l" + cip.getBuildingLocation().level;
                    }
                    lines.add(this.createFullLine(planName + " " + level, icon, icon));
                    ++cipPos;
                }
            } else {
                lines.add(this.createEmptyLine());
                lines.add(this.createEmptyLine());
                lines.add(this.createFullLine("ui.noconstruction1", null, null));
                lines.add(this.createFullLine("ui.noconstruction2", null, null));
            }
            sign.untranslatedLines = lines;
            sign.buildingPos = this.building.getPos();
            sign.panelType = 2;
            sign.texture = this.building.culture.panelTexture;
            sign.triggerUpdate();
        }
    }

    private void updateSignTHEtatCivil(TileEntityPanel sign) {
        if (sign != null) {
            int nbMen = 0;
            int nbFemale = 0;
            int nbGrownBoy = 0;
            int nbGrownGirl = 0;
            int nbBoy = 0;
            int nbGirl = 0;
            ArrayList<TileEntityPanel.PanelUntranslatedLine> lines = new ArrayList<TileEntityPanel.PanelUntranslatedLine>();
            for (VillagerRecord vr : this.building.getVillagerRecords().values()) {
                boolean belongsToVillage = vr.getType() != null && !vr.getType().visitor && !vr.raidingVillage;
                if (!belongsToVillage) continue;
                if (!vr.getType().isChild) {
                    if (vr.gender == 1) {
                        ++nbMen;
                        continue;
                    }
                    ++nbFemale;
                    continue;
                }
                if (vr.size == 20) {
                    if (vr.gender == 1) {
                        ++nbGrownBoy;
                        continue;
                    }
                    ++nbGrownGirl;
                    continue;
                }
                if (vr.gender == 1) {
                    ++nbBoy;
                    continue;
                }
                ++nbGirl;
            }
            lines.add(this.createFullLine("ui.population", FLOWER_BLUE, FLOWER_PINK));
            lines.add(this.createEmptyLine());
            lines.add(this.createFullLine(new String[]{"ui.adults", "" + (nbMen + nbFemale), "" + nbMen, "" + nbFemale}, null, null));
            lines.add(this.createFullLine(new String[]{"ui.teens", "" + (nbGrownBoy + nbGrownGirl), "" + nbGrownBoy, "" + nbGrownGirl}, null, null));
            lines.add(this.createFullLine(new String[]{"ui.children", "" + (nbBoy + nbGirl), "" + nbBoy, "" + nbGirl}, null, null));
            sign.untranslatedLines = lines;
            sign.buildingPos = this.building.getPos();
            sign.panelType = 1;
            sign.texture = this.building.culture.panelTexture;
            sign.triggerUpdate();
        }
    }

    private void updateSignTHMap(TileEntityPanel sign) {
        ArrayList<TileEntityPanel.PanelUntranslatedLine> lines = new ArrayList<TileEntityPanel.PanelUntranslatedLine>();
        lines.add(this.createFullLine("ui.villagemap", PanelManager.stackFromItem((Item)Items.field_151098_aY), PanelManager.stackFromItem((Item)Items.field_151098_aY)));
        lines.add(this.createEmptyLine());
        int nbBuildings = 0;
        for (Building building : this.building.getBuildings()) {
            if (!building.location.isCustomBuilding && building.location.getPlan().isWallSegment) continue;
            ++nbBuildings;
        }
        lines.add(this.createFullLine(new String[]{"ui.nbbuildings", "" + nbBuildings}, null, null));
        sign.untranslatedLines = lines;
        sign.buildingPos = this.building.getPos();
        sign.panelType = 8;
        sign.texture = this.building.culture.panelTexture;
        sign.triggerUpdate();
    }

    private void updateSignTHMilitary(TileEntityPanel sign) {
        if (sign != null) {
            String status = "";
            if (this.building.raidTarget != null) {
                status = this.building.raidStart > 0L ? "panels.raidinprogress" : "panels.planningraid";
            } else if (this.building.underAttack) {
                status = "panels.underattack";
            }
            ArrayList<TileEntityPanel.PanelUntranslatedLine> lines = new ArrayList<TileEntityPanel.PanelUntranslatedLine>();
            lines.add(this.createFullLine("panels.military", PanelManager.stackFromItem(Items.field_151040_l), PanelManager.stackFromItem(Items.field_151040_l)));
            lines.add(this.createEmptyLine());
            if (status.length() > 0) {
                lines.add(this.createFullLine(status, null, null));
            }
            lines.add(this.createFullLine(new String[]{"panels.offense", "" + this.building.getVillageRaidingStrength()}, PanelManager.stackFromItem(Items.field_151036_c), PanelManager.stackFromItem(Items.field_151036_c)));
            lines.add(this.createFullLine(new String[]{"panels.defense", "" + this.building.getVillageDefendingStrength()}, PanelManager.stackFromItem((Item)Items.field_151030_Z), PanelManager.stackFromItem((Item)Items.field_151030_Z)));
            int type = this.building.villageType.playerControlled ? 13 : 9;
            sign.untranslatedLines = lines;
            sign.buildingPos = this.building.getPos();
            sign.panelType = type;
            sign.texture = this.building.culture.panelTexture;
            sign.triggerUpdate();
        }
    }

    private void updateSignTHProject(TileEntityPanel sign) {
        if (sign != null) {
            ArrayList<TileEntityPanel.PanelUntranslatedLine> lines = new ArrayList<TileEntityPanel.PanelUntranslatedLine>();
            String[] status = null;
            if (this.building.buildingGoal == null) {
                lines.add(this.createEmptyLine());
                lines.add(this.createEmptyLine());
                lines.add(this.createFullLine("ui.goalscompleted1", null, null));
                lines.add(this.createFullLine("ui.goalscompleted2", null, null));
            } else {
                BuildingPlan goal = this.building.getCurrentGoalBuildingPlan();
                boolean inprogress = false;
                lines.add(this.createFullLine("ui.project", goal.getIcon(), goal.getIcon()));
                lines.add(this.createEmptyLine());
                lines.add(this.createFullLine(goal.nativeName, null, null));
                lines.add(this.createFullLine(goal.getGameNameKey(), null, null));
                for (ConstructionIP cip : this.building.getConstructionsInProgress()) {
                    if (cip.getBuildingLocation() == null || !cip.getBuildingLocation().planKey.equals(this.building.buildingGoal)) continue;
                    status = cip.getBuildingLocation().level == 0 ? new String[]{"ui.inconstruction"} : new String[]{"ui.upgrading", "" + cip.getBuildingLocation().level};
                    inprogress = true;
                }
                if (!inprogress) {
                    status = new String[]{this.building.buildingGoalIssue};
                }
                lines.add(this.createEmptyLine());
                lines.add(this.createFullLine(status, null, null));
            }
            int type = this.building.villageType.playerControlled ? 4 : 3;
            sign.untranslatedLines = lines;
            sign.buildingPos = this.building.getPos();
            sign.panelType = type;
            sign.texture = this.building.culture.panelTexture;
            sign.triggerUpdate();
        }
    }

    private void updateSignTHResources(TileEntityPanel sign) {
        if (sign != null) {
            BuildingPlan goalPlan = this.building.getCurrentGoalBuildingPlan();
            ArrayList<InvItem> res = new ArrayList<InvItem>();
            ArrayList<Integer> resCost = new ArrayList<Integer>();
            ArrayList<Integer> resHas = new ArrayList<Integer>();
            if (goalPlan != null) {
                boolean inprogress = false;
                for (ConstructionIP cip : this.building.getConstructionsInProgress()) {
                    if (cip.getBuildingLocation() == null || !cip.getBuildingLocation().planKey.equals(this.building.buildingGoal)) continue;
                    inprogress = true;
                }
                if (inprogress) {
                    for (InvItem key : goalPlan.resCost.keySet()) {
                        res.add(key);
                        resCost.add(goalPlan.resCost.get(key));
                        resHas.add(goalPlan.resCost.get(key));
                    }
                } else {
                    for (InvItem key : goalPlan.resCost.keySet()) {
                        res.add(key);
                        resCost.add(goalPlan.resCost.get(key));
                        int has = this.building.countGoods(key.getItem(), key.meta);
                        if (has > goalPlan.resCost.get(key)) {
                            has = goalPlan.resCost.get(key);
                        }
                        resHas.add(has);
                    }
                }
            }
            ArrayList<ResourceLine> resources = new ArrayList<ResourceLine>();
            for (int i = 0; i < res.size(); ++i) {
                resources.add(new ResourceLine((InvItem)res.get(i), (Integer)resCost.get(i), (Integer)resHas.get(i)));
            }
            ArrayList<TileEntityPanel.PanelUntranslatedLine> lines = new ArrayList<TileEntityPanel.PanelUntranslatedLine>();
            if (goalPlan != null) {
                lines.add(this.createFullLine("ui.resources", PanelManager.stackFromBlock((Block)Blocks.field_150486_ae), PanelManager.stackFromBlock((Block)Blocks.field_150486_ae)));
                if (res.size() < 12) {
                    lines.add(this.createEmptyLine());
                }
                this.generateResourceLines(resources, lines);
            }
            sign.untranslatedLines = lines;
            sign.buildingPos = this.building.getPos();
            sign.panelType = 6;
            sign.texture = this.building.culture.panelTexture;
            sign.triggerUpdate();
        }
    }

    private void updateSignTHVillageName(TileEntityPanel sign) {
        if (sign != null) {
            int nbvill = 0;
            for (VillagerRecord vr : this.building.getVillagerRecords().values()) {
                boolean belongsToVillage;
                if (vr == null || !(belongsToVillage = !vr.raidingVillage && vr.getType() != null && !vr.getType().visitor)) continue;
                ++nbvill;
            }
            ArrayList<TileEntityPanel.PanelUntranslatedLine> lines = new ArrayList<TileEntityPanel.PanelUntranslatedLine>();
            lines.add(this.createFullLine(this.building.getVillageNameWithoutQualifier(), this.building.getBannerStack(), this.building.getBannerStack()));
            lines.add(this.createFullLine(this.building.getQualifier(), null, null));
            lines.add(this.createEmptyLine());
            lines.add(this.createFullLine(this.building.villageType.name, null, null));
            lines.add(this.createFullLine(new String[]{"ui.populationnumber", "" + nbvill}, null, null));
            if (this.building.controlledBy != null) {
                lines.add(this.createEmptyLine());
                lines.add(this.createFullLine(this.building.controlledByName, PanelManager.stackFromItem((Item)Items.field_151169_ag), PanelManager.stackFromItem((Item)Items.field_151169_ag)));
            }
            sign.untranslatedLines = lines;
            sign.buildingPos = this.building.getPos();
            sign.panelType = 1;
            sign.texture = this.building.culture.panelTexture;
            sign.triggerUpdate();
        }
    }

    private void updateSignTHWalls(TileEntityPanel sign) {
        if (sign == null) {
            return;
        }
        List<BuildingProject> projects = this.townHall.getFlatProjectList();
        int wallLevel = this.townHall.computeCurrentWallLevel();
        ArrayList<TileEntityPanel.PanelUntranslatedLine> lines = new ArrayList<TileEntityPanel.PanelUntranslatedLine>();
        lines.add(this.createFullLine("ui.panelwalls", PanelManager.stackFromBlock(Blocks.field_150463_bK), PanelManager.stackFromBlock(Blocks.field_150463_bK)));
        if (wallLevel == Integer.MAX_VALUE) {
            lines.add(this.createEmptyLine());
            lines.add(this.createFullLine("ui.panelwallscomplete", null, null));
        } else if (wallLevel == -1) {
            lines.add(this.createEmptyLine());
            lines.add(this.createFullLine("ui.panelwallnowalls", null, null));
        } else {
            WallStatusInfos wallInfos = this.computeWallInfos(projects, wallLevel);
            lines.add(this.createFullLine(new String[]{"ui.panelwallslevel", "" + wallLevel, "" + wallInfos.segmentsDone, "" + (wallInfos.segmentsDone + wallInfos.segmentsToDo)}, null, null));
            if (wallInfos.resources.size() < (8 - lines.size()) * 2) {
                lines.add(this.createEmptyLine());
            }
            this.generateResourceLines(wallInfos.resources, lines);
        }
        sign.untranslatedLines = lines;
        sign.buildingPos = this.building.getPos();
        sign.panelType = 15;
        sign.texture = this.building.culture.panelTexture;
        sign.triggerUpdate();
    }

    private void updateTownHallSigns(boolean forced) {
        if (this.building.world.field_72995_K) {
            return;
        }
        EntityPlayer player = this.building.world.func_184137_a((double)this.building.getPos().getiX(), (double)this.building.getPos().getiY(), (double)this.building.getPos().getiZ(), 20.0, false);
        if (player == null) {
            return;
        }
        if (!forced && this.building.world.func_72820_D() - this.lastSignUpdate < 40L) {
            return;
        }
        block11: for (int i = 0; i < this.building.getResManager().signs.size(); ++i) {
            TileEntityPanel sign;
            EnumFacing facing;
            Point p = this.building.getResManager().signs.get(i);
            if (p == null) continue;
            if (WorldUtilities.getBlock(this.building.world, p) != MillBlocks.PANEL && (facing = WorldUtilities.guessPanelFacing(this.building.world, p)) != null) {
                WorldUtilities.setBlockstate(this.building.world, p, MillBlocks.PANEL.func_176223_P().func_177226_a((IProperty)BlockWallSign.field_176412_a, (Comparable)facing), true, false);
            }
            if ((sign = (TileEntityPanel)this.building.world.func_175625_s(p.getBlockPos())) == null) continue;
            switch (i) {
                case 0: {
                    this.updateSignTHVillageName(sign);
                    continue block11;
                }
                case 1: {
                    this.updateSignTHResources(sign);
                    continue block11;
                }
                case 2: {
                    this.updateSignTHWalls(sign);
                    continue block11;
                }
                case 3: {
                    sign.texture = this.building.culture.panelTexture;
                    sign.triggerUpdate();
                    continue block11;
                }
                case 4: {
                    this.updateSignTHProject(sign);
                    continue block11;
                }
                case 5: {
                    this.updateSignTHConstruction(sign);
                    continue block11;
                }
                case 6: {
                    this.updateSignTHEtatCivil(sign);
                    continue block11;
                }
                case 7: {
                    this.updateSignTHMap(sign);
                    continue block11;
                }
                case 8: {
                    this.updateSignTHMilitary(sign);
                    continue block11;
                }
            }
        }
        this.lastSignUpdate = this.building.world.func_72820_D();
    }

    public void updateVisitorsSigns() {
        EntityPlayer player = this.building.world.func_184137_a((double)this.building.getPos().getiX(), (double)this.building.getPos().getiY(), (double)this.building.getPos().getiZ(), 20.0, false);
        if (player == null) {
            return;
        }
        if (this.building.getResManager().signs.size() == 0 || this.building.getResManager().signs.get(0) == null) {
            return;
        }
        for (int i = 0; i < this.building.getResManager().signs.size(); ++i) {
            EnumFacing facing;
            Point p = this.building.getResManager().signs.get(i);
            if (p == null || WorldUtilities.getBlock(this.building.world, p) == MillBlocks.PANEL || (facing = WorldUtilities.guessPanelFacing(this.building.world, p)) == null) continue;
            WorldUtilities.setBlockstate(this.building.world, p, MillBlocks.PANEL.func_176223_P().func_177226_a((IProperty)BlockWallSign.field_176412_a, (Comparable)facing), true, false);
        }
        TileEntityPanel sign = this.building.getResManager().signs.get(0).getPanel(this.building.world);
        if (sign != null) {
            ArrayList<TileEntityPanel.PanelUntranslatedLine> lines = new ArrayList<TileEntityPanel.PanelUntranslatedLine>();
            lines.add(this.createFullLine(this.building.getNativeBuildingName(), this.building.getIcon(), this.building.getIcon()));
            lines.add(this.createEmptyLine());
            lines.add(this.createFullLine("ui.visitorslist2", null, null));
            int type = 0;
            if (this.building.isMarket) {
                lines.add(this.createFullLine("ui.merchants", null, null));
                type = 12;
            } else {
                lines.add(this.createFullLine("ui.visitors", null, null));
                type = 14;
            }
            lines.add(this.createFullLine(new String[]{"" + this.building.getAllVillagerRecords().size()}, null, null));
            sign.untranslatedLines = lines;
            sign.buildingPos = this.building.getPos();
            sign.panelType = type;
            sign.texture = this.building.culture.panelTexture;
            sign.triggerUpdate();
        }
    }

    public static class WallStatusInfos {
        public final List<ResourceLine> resources;
        public final int segmentsDone;
        public final int segmentsToDo;

        public WallStatusInfos(List<ResourceLine> resources, int segmentsDone, int segmentsToDo) {
            this.resources = resources;
            this.segmentsDone = segmentsDone;
            this.segmentsToDo = segmentsToDo;
        }
    }

    public static class ResourceLine
    implements Comparable<ResourceLine> {
        public InvItem res;
        public int cost;
        public int has;

        ResourceLine(InvItem res, int cost, int has) {
            this.res = res;
            this.cost = cost;
            this.has = has;
        }

        @Override
        public int compareTo(ResourceLine o) {
            return -this.cost + o.cost;
        }
    }

    public static enum EnumSignType {
        DEFAULT,
        HOUSE,
        TOWNHALL,
        INN,
        ARCHIVES,
        MARVEL,
        VISITORS,
        WALL,
        WALLBUILD;

    }
}

