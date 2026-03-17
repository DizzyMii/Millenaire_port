/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockBanner
 *  net.minecraft.block.BlockBed
 *  net.minecraft.block.BlockChest
 *  net.minecraft.block.BlockDoor
 *  net.minecraft.block.BlockDoublePlant
 *  net.minecraft.block.BlockFenceGate
 *  net.minecraft.block.BlockFlowerPot
 *  net.minecraft.block.BlockFlowerPot$EnumFlowerType
 *  net.minecraft.block.BlockLeaves
 *  net.minecraft.block.BlockLiquid
 *  net.minecraft.block.BlockNewLeaf
 *  net.minecraft.block.BlockNewLog
 *  net.minecraft.block.BlockOldLeaf
 *  net.minecraft.block.BlockOldLog
 *  net.minecraft.block.BlockPlanks$EnumType
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.properties.PropertyEnum
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.tileentity.TileEntitySign
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.text.ITextComponent
 *  net.minecraft.util.text.TextComponentString
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 */
package org.millenaire.common.buildingplan;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import javax.imageio.ImageIO;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBanner;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockFlowerPot;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockNewLeaf;
import net.minecraft.block.BlockNewLog;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.millenaire.client.network.ClientSender;
import org.millenaire.common.block.IBlockPath;
import org.millenaire.common.block.MillBlocks;
import org.millenaire.common.block.mock.MockBlockAnimalSpawn;
import org.millenaire.common.block.mock.MockBlockDecor;
import org.millenaire.common.block.mock.MockBlockFree;
import org.millenaire.common.block.mock.MockBlockMarker;
import org.millenaire.common.block.mock.MockBlockSoil;
import org.millenaire.common.block.mock.MockBlockSource;
import org.millenaire.common.block.mock.MockBlockTreeSpawn;
import org.millenaire.common.buildingplan.BuildingDevUtilities;
import org.millenaire.common.buildingplan.BuildingPlan;
import org.millenaire.common.buildingplan.BuildingPlanSet;
import org.millenaire.common.buildingplan.PointType;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.culture.Culture;
import org.millenaire.common.entity.TileEntityImportTable;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.network.ServerSender;
import org.millenaire.common.utilities.BlockItemUtilities;
import org.millenaire.common.utilities.BlockStateUtilities;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.utilities.WorldUtilities;
import org.millenaire.common.utilities.virtualdir.VirtualDir;
import org.millenaire.common.village.BuildingLocation;

public class BuildingImportExport {
    public static String EXPORT_DIR = "exportdir";
    private static HashMap<Integer, PointType> reverseColourPoints = new HashMap();

    public static Point adjustForOrientation(int x, int y, int z, int xoffset, int zoffset, int orientation) {
        Point pos = null;
        if (orientation == 0) {
            pos = new Point(x + xoffset, y, z + zoffset);
        } else if (orientation == 1) {
            pos = new Point(x + zoffset, y, z - xoffset);
        } else if (orientation == 2) {
            pos = new Point(x - xoffset, y, z - zoffset - 1);
        } else if (orientation == 3) {
            pos = new Point(x - zoffset - 1, y, z + xoffset);
        }
        return pos;
    }

    private static void copyPlanSetToExportDir(BuildingPlanSet planSet) {
        File exportDir = MillCommonUtilities.getExportDir();
        Path exportPath = exportDir.toPath();
        Path inputPath = planSet.getFirstStartingPlan().getLoadedFromFile().toPath().getParent();
        try {
            for (int exportVariation = 0; exportVariation < planSet.plans.size(); ++exportVariation) {
                char exportVariationLetter = (char)(65 + exportVariation);
                String txtFileName = planSet.key + "_" + exportVariationLetter + ".txt";
                Files.copy(inputPath.resolve(txtFileName), exportPath.resolve(txtFileName), StandardCopyOption.REPLACE_EXISTING);
                for (int buildingUpgrade = 0; buildingUpgrade < planSet.plans.get(exportVariation).length; ++buildingUpgrade) {
                    String pngFileName = planSet.key + "_" + exportVariationLetter + buildingUpgrade + ".png";
                    Files.copy(inputPath.resolve(pngFileName), exportPath.resolve(pngFileName), StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
        catch (IOException e) {
            MillLog.printException("Error when copying files to export dir:", e);
        }
    }

    private static void doubleHeightPlan(EntityPlayer player, BuildingPlanSet existingSet) {
        for (BuildingPlan[] plans : existingSet.plans) {
            for (BuildingPlan plan : plans) {
                PointType[][][] newPlan = new PointType[plan.plan.length * 2][plan.plan[0].length][plan.plan[0][0].length];
                for (int i = 0; i < plan.plan.length; ++i) {
                    for (int j = 0; j < plan.plan[0].length; ++j) {
                        for (int k = 0; k < plan.plan[0][0].length; ++k) {
                            newPlan[i * 2][j][k] = plan.plan[i][j][k];
                            newPlan[i * 2 + 1][j][k] = plan.plan[i][j][k];
                        }
                    }
                }
                plan.plan = newPlan;
                plan.nbfloors *= 2;
            }
        }
        ServerSender.sendTranslatedSentence(player, 'f', "import.doublevertical", new String[0]);
        MillLog.major(null, "Building height: " + existingSet.plans.get((int)0)[0].plan.length);
    }

    private static void drawWoolBorders(EntityPlayer player, Point tablePos, int orientatedLength, int orientatedWidth) {
        int meta;
        for (int x = 1; x <= orientatedLength; ++x) {
            meta = 0;
            if ((x - 1) % 10 < 5) {
                meta = 14;
            }
            tablePos.getRelative(x, -1.0, 0.0).setBlock(player.field_70170_p, Blocks.field_150325_L, meta, true, false);
            tablePos.getRelative(x, -1.0, orientatedWidth + 1).setBlock(player.field_70170_p, Blocks.field_150325_L, meta, true, false);
        }
        for (int z = 1; z <= orientatedWidth; ++z) {
            meta = 0;
            if ((z - 1) % 10 < 5) {
                meta = 14;
            }
            tablePos.getRelative(0.0, -1.0, z).setBlock(player.field_70170_p, Blocks.field_150325_L, meta, true, false);
            tablePos.getRelative(orientatedLength + 1, -1.0, z).setBlock(player.field_70170_p, Blocks.field_150325_L, meta, true, false);
        }
        tablePos.getRelative(0.0, -1.0, 0.0).setBlock(player.field_70170_p, Blocks.field_150325_L, 15, true, false);
        tablePos.getRelative(orientatedLength + 1, -1.0, 0.0).setBlock(player.field_70170_p, Blocks.field_150325_L, 15, true, false);
        tablePos.getRelative(0.0, -1.0, orientatedWidth + 1).setBlock(player.field_70170_p, Blocks.field_150325_L, 15, true, false);
        tablePos.getRelative(orientatedLength + 1, -1.0, orientatedWidth + 1).setBlock(player.field_70170_p, Blocks.field_150325_L, 15, true, false);
    }

    public static int exportBuilding(World world, Point startPoint, String planName, int variation, int length, int width, int orientation, int upgradeLevel, int startLevel, boolean exportSnow, boolean exportRegularChests, boolean autoconvertToPreserveGround) throws Exception, IOException, UnsupportedEncodingException, FileNotFoundException {
        BuildingImportExport.loadReverseBuildingPoints(autoconvertToPreserveGround, exportRegularChests);
        File exportDir = new File(MillCommonUtilities.getMillenaireCustomContentDir(), "exports");
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }
        char variationLetter = 'A';
        variationLetter = (char)(variationLetter + variation);
        File buildingFile = new File(exportDir, planName + "_" + variationLetter + ".txt");
        PointType[][][] existingPoints = null;
        int existingMinLevel = 0;
        BuildingPlanSet existingSet = null;
        if (buildingFile.exists()) {
            existingSet = BuildingImportExport.loadPlanSetFromExportDir(planName);
            if (existingSet.plans.get((int)variation)[0].length != length) {
                Mill.proxy.localTranslatedSentence(Mill.proxy.getTheSinglePlayer(), '6', "export.errorlength", "" + length, "" + existingSet.plans.get((int)variation)[0].length);
                return upgradeLevel;
            }
            if (existingSet.plans.get((int)variation)[0].width != width) {
                Mill.proxy.localTranslatedSentence(Mill.proxy.getTheSinglePlayer(), '6', "export.errorwidth", "" + width, "" + existingSet.plans.get((int)variation)[0].width);
                return upgradeLevel;
            }
            if (upgradeLevel == -1) {
                upgradeLevel = existingSet.plans.get(variation).length;
            }
            if (existingSet.plans.get((int)variation)[0].parentBuildingPlan != null) {
                String parentBuildingPlanKey = existingSet.plans.get((int)variation)[0].parentBuildingPlan;
                String parentSuffix = parentBuildingPlanKey.split("_")[parentBuildingPlanKey.split("_").length - 1].toUpperCase();
                int parentVariation = parentSuffix.charAt(0) - 65;
                int parentLevel = Integer.parseInt(parentSuffix.substring(1, parentSuffix.length()));
                String parentBuildingKey = parentBuildingPlanKey.substring(0, parentBuildingPlanKey.length() - parentSuffix.length() - 1);
                BuildingPlanSet parentSet = BuildingImportExport.loadPlanSetFromExportDir(parentBuildingKey);
                existingPoints = BuildingImportExport.getConsolidatedPlanWithParent(parentSet, parentVariation, parentLevel, existingSet, variation, upgradeLevel - 1);
                existingMinLevel = Math.min(existingSet.getMinLevel(variation, upgradeLevel - 1), parentSet.getMinLevel(parentVariation, parentLevel));
            } else {
                existingPoints = BuildingImportExport.getConsolidatedPlan(existingSet, variation, upgradeLevel - 1);
                existingMinLevel = existingSet.getMinLevel(variation, upgradeLevel - 1);
            }
        } else {
            upgradeLevel = 0;
        }
        ArrayList<PointType[][]> export = new ArrayList<PointType[][]>();
        boolean stop = false;
        int orientatedLength = length;
        int orientatedWidth = width;
        if (orientation % 2 == 1) {
            orientatedLength = width;
            orientatedWidth = length;
        }
        Point centrePos = startPoint.getRelative(orientatedLength / 2 + 1, 0.0, orientatedWidth / 2 + 1);
        int x = centrePos.getiX();
        int y = centrePos.getiY();
        int z = centrePos.getiZ();
        int lengthOffset = (int)Math.floor((double)length * 0.5);
        int widthOffset = (int)Math.floor((double)width * 0.5);
        int dy = 0;
        while (!stop) {
            PointType[][] level = new PointType[length][width];
            boolean blockFound = false;
            for (int dx = 0; dx < length; ++dx) {
                for (int dz = 0; dz < width; ++dz) {
                    PointType pt;
                    level[dx][dz] = null;
                    Point p = BuildingImportExport.adjustForOrientation(x, y + dy + startLevel, z, dx - lengthOffset, dz - widthOffset, orientation);
                    Block block = WorldUtilities.getBlock(world, p);
                    int meta = WorldUtilities.getBlockMeta(world, p);
                    if (block != Blocks.field_150350_a) {
                        blockFound = true;
                    }
                    if (block instanceof BlockFlowerPot) {
                        meta = ((BlockFlowerPot.EnumFlowerType)WorldUtilities.getBlockState(world, p).func_185899_b((IBlockAccess)world, new BlockPos(p.x, p.y, p.z)).func_177229_b((IProperty)BlockFlowerPot.field_176443_b)).ordinal();
                    }
                    if ((pt = reverseColourPoints.get(BuildingImportExport.getPointHash(block, meta))) != null) {
                        if (!exportSnow && pt.getBlock() == Blocks.field_150431_aC) continue;
                        PointType existing = null;
                        if (existingPoints != null && dy + startLevel >= existingMinLevel && dy + startLevel < existingMinLevel + existingPoints.length && (existing = existingPoints[dy + startLevel - existingMinLevel][dx][dz]) == null) {
                            MillLog.major(null, "Existing pixel is null");
                        }
                        if (existing == null) {
                            if (pt.specialType == null && pt.getBlock() == Blocks.field_150350_a && upgradeLevel == 0) continue;
                            level[dx][dz] = pt;
                            continue;
                        }
                        if (existing == pt || existing.isType("empty") && pt.getBlock() == Blocks.field_150350_a) continue;
                        level[dx][dz] = pt;
                        continue;
                    }
                    if (block instanceof BlockBed || block instanceof BlockDoublePlant || block instanceof BlockLiquid) continue;
                    Mill.proxy.localTranslatedSentence(Mill.proxy.getTheSinglePlayer(), '6', "export.errorunknownblockid", "" + block + "/" + meta + "/" + BuildingImportExport.getPointHash(block, meta));
                }
            }
            if (blockFound || existingPoints != null && export.size() < existingPoints.length) {
                export.add(level);
            } else {
                stop = true;
            }
            if (++dy + startPoint.getiY() + startLevel < 256) continue;
            stop = true;
        }
        BufferedImage pict = new BufferedImage(export.size() * width + export.size() - 1, length, 1);
        Graphics2D graphics = pict.createGraphics();
        graphics.setColor(new Color(11730865));
        graphics.fillRect(0, 0, pict.getWidth(), pict.getHeight());
        for (dy = 0; dy < export.size(); ++dy) {
            PointType[][] level = (PointType[][])export.get(dy);
            for (int i = 0; i < length; ++i) {
                for (int k = 0; k < width; ++k) {
                    int colour = 0xFFFFFF;
                    PointType pt = level[i][k];
                    if (pt != null) {
                        colour = pt.colour;
                    }
                    graphics.setColor(new Color(colour));
                    graphics.fillRect(dy * width + dy + width - k - 1, i, 1, 1);
                }
            }
        }
        String fileName = planName + "_" + variationLetter + upgradeLevel + ".png";
        ImageIO.write((RenderedImage)pict, "png", new File(exportDir, fileName));
        if (upgradeLevel == 0 && existingSet == null) {
            BufferedWriter writer = MillCommonUtilities.getWriter(new File(exportDir, planName + "_" + variationLetter + ".txt"));
            writer.write("building.length=" + length + "\n");
            writer.write("building.width=" + width + "\n");
            writer.write("\n");
            writer.write("initial.startlevel=" + startLevel + "\n");
            writer.write("initial.nativename=" + planName + "\n");
            writer.close();
        } else if (upgradeLevel > existingSet.plans.get(variation).length) {
            BufferedWriter writer = MillCommonUtilities.getAppendWriter(new File(exportDir, planName + "_" + variationLetter + ".txt"));
            writer.write("upgrade" + upgradeLevel + ".startlevel=" + startLevel + "\n");
            writer.close();
        }
        Mill.proxy.localTranslatedSentence(Mill.proxy.getTheSinglePlayer(), 'f', "export.buildingexported", fileName);
        return upgradeLevel;
    }

    private static PointType[][][] getConsolidatedPlan(BuildingPlanSet planSet, int variation, int upgradeLevel) {
        int minLevel = planSet.getMinLevel(variation, upgradeLevel);
        int maxLevel = planSet.getMaxLevel(variation, upgradeLevel);
        int length = planSet.plans.get((int)variation)[0].plan[0].length;
        int width = planSet.plans.get((int)variation)[0].plan[0][0].length;
        PointType[][][] consolidatedPlan = new PointType[maxLevel - minLevel][length][width];
        for (int lid = 0; lid <= upgradeLevel; ++lid) {
            BuildingPlan plan = planSet.plans.get(variation)[lid];
            if (MillConfigValues.LogBuildingPlan >= 1) {
                MillLog.major(planSet, "Consolidating plan: adding level " + lid);
            }
            int ioffset = plan.startLevel - minLevel;
            for (int i = 0; i < plan.plan.length; ++i) {
                for (int j = 0; j < length; ++j) {
                    for (int k = 0; k < width; ++k) {
                        PointType pt = plan.plan[i][j][k];
                        if (pt.isType("empty") && lid != 0) continue;
                        consolidatedPlan[i + ioffset][j][k] = pt;
                    }
                }
            }
        }
        return consolidatedPlan;
    }

    private static PointType[][][] getConsolidatedPlanWithParent(BuildingPlanSet parentSet, int parentVariation, int parentUpgradeLevel, BuildingPlanSet planSet, int variation, int upgradeLevel) {
        int minLevel = Math.min(planSet.getMinLevel(variation, upgradeLevel), parentSet.getMinLevel(parentVariation, parentUpgradeLevel));
        int maxLevel = Math.max(planSet.getMaxLevel(variation, upgradeLevel), parentSet.getMaxLevel(parentVariation, parentUpgradeLevel));
        int length = planSet.plans.get((int)variation)[0].plan[0].length;
        int width = planSet.plans.get((int)variation)[0].plan[0][0].length;
        PointType[][][] consolidatedPlan = new PointType[maxLevel - minLevel][length][width];
        for (int lid = 0; lid <= parentUpgradeLevel; ++lid) {
            BuildingPlan plan = parentSet.plans.get(parentVariation)[lid];
            if (MillConfigValues.LogBuildingPlan >= 1) {
                MillLog.major(parentSet, "Consolidating plan: adding level " + lid);
            }
            int ioffset = plan.startLevel - minLevel;
            for (int i = 0; i < plan.plan.length; ++i) {
                for (int j = 0; j < length; ++j) {
                    for (int k = 0; k < width; ++k) {
                        PointType pt = plan.plan[i][j][k];
                        if (pt.isType("empty") && lid != 0) continue;
                        consolidatedPlan[i + ioffset][j][k] = pt;
                    }
                }
            }
        }
        PointType airPt = reverseColourPoints.get(BuildingImportExport.getPointHash(Blocks.field_150350_a, 0));
        if (parentSet.getMaxLevel(parentVariation, parentUpgradeLevel) < planSet.getMaxLevel(variation, upgradeLevel)) {
            for (int i = parentSet.getMaxLevel(parentVariation, parentUpgradeLevel); i <= planSet.getMaxLevel(variation, upgradeLevel); ++i) {
                for (int j = 0; j < length; ++j) {
                    for (int k = 0; k < width; ++k) {
                        consolidatedPlan[i][j][k] = airPt;
                    }
                }
            }
        }
        for (int lid = 0; lid <= upgradeLevel; ++lid) {
            BuildingPlan plan = planSet.plans.get(variation)[lid];
            if (MillConfigValues.LogBuildingPlan >= 1) {
                MillLog.major(planSet, "Consolidating plan: adding level " + lid);
            }
            int ioffset = plan.startLevel - minLevel;
            for (int i = 0; i < plan.plan.length; ++i) {
                for (int j = 0; j < length; ++j) {
                    for (int k = 0; k < width; ++k) {
                        PointType pt = plan.plan[i][j][k];
                        if (pt.isType("empty")) continue;
                        consolidatedPlan[i + ioffset][j][k] = pt;
                    }
                }
            }
        }
        return consolidatedPlan;
    }

    private static int getPointHash(Block b, int meta) {
        if (b != null) {
            return (b.getRegistryName() + "_" + meta).hashCode();
        }
        return ("unknownBlock_" + meta).hashCode();
    }

    private static int getPointHash(IBlockState bs) {
        if (bs != null) {
            return (bs.func_177230_c().getRegistryName() + "_" + bs.func_177230_c().func_176201_c(bs)).hashCode();
        }
        return "unknownBlock".hashCode();
    }

    private static PointType getPointTypeFromBlockState(IBlockState blockState) {
        for (PointType newPt : PointType.colourPoints.values()) {
            if (newPt.getBlockState() == null || !newPt.getBlockState().equals(blockState)) continue;
            return newPt;
        }
        return null;
    }

    public static void importTableCreateNewBuilding(EntityPlayer player, TileEntityImportTable importTable, int length, int width, int startLevel, boolean clearGround) {
        File exportDir = MillCommonUtilities.getExportDir();
        VirtualDir exportVirtualDir = new VirtualDir(exportDir);
        int exportNumber = 1;
        while (exportVirtualDir.getChildFile("export" + exportNumber + "_A.txt") != null) {
            ++exportNumber;
        }
        if (clearGround) {
            for (int x = importTable.func_174877_v().func_177958_n(); x < importTable.func_174877_v().func_177958_n() + 2 + length; ++x) {
                for (int z = importTable.func_174877_v().func_177952_p(); z < importTable.func_174877_v().func_177952_p() + 2 + width; ++z) {
                    int y;
                    int startingY = Math.min(0, startLevel);
                    for (y = importTable.func_174877_v().func_177956_o() + startingY; y < importTable.func_174877_v().func_177956_o(); ++y) {
                        player.field_70170_p.func_175656_a(new BlockPos(x, y, z), MillBlocks.MARKER_BLOCK.func_176223_P().func_177226_a(MockBlockMarker.VARIANT, (Comparable)((Object)MockBlockMarker.Type.PRESERVE_GROUND)));
                    }
                    for (y = importTable.func_174877_v().func_177956_o(); y < importTable.func_174877_v().func_177956_o() + 50; ++y) {
                        BlockPos pos = new BlockPos(x, y, z);
                        if (pos.equals((Object)importTable.func_174877_v())) continue;
                        player.field_70170_p.func_175656_a(new BlockPos(x, y, z), Blocks.field_150350_a.func_176223_P());
                    }
                }
            }
        }
        BuildingImportExport.drawWoolBorders(player, importTable.getPosPoint(), length, width);
        importTable.updatePlan("export" + exportNumber, length, width, 0, 0, startLevel, player);
    }

    public static void importTableExportBuildingPlan(World world, TileEntityImportTable importTable, int level) {
        if (importTable.getOrientation() != 0) {
            Mill.proxy.localTranslatedSentence(Mill.proxy.getTheSinglePlayer(), '6', "export.northfacingonly", new String[0]);
            return;
        }
        try {
            int upgradeLevelExported = BuildingImportExport.exportBuilding(world, new Point(importTable.func_174877_v()), importTable.getBuildingKey(), importTable.getVariation(), importTable.getLength(), importTable.getWidth(), importTable.getOrientation(), level, importTable.getStartingLevel(), importTable.exportSnow(), importTable.exportRegularChests(), importTable.autoconvertToPreserveGround());
            if (upgradeLevelExported != level) {
                ClientSender.importTableUpdateSettings(new Point(importTable.func_174877_v()), upgradeLevelExported, importTable.getOrientation(), importTable.getStartingLevel(), importTable.exportSnow(), importTable.importMockBlocks(), importTable.autoconvertToPreserveGround(), importTable.exportRegularChests());
            }
        }
        catch (Exception e) {
            MillLog.printException("Error when exporting building:", e);
        }
    }

    public static void importTableExportPlanCost(String buildingKey) {
        BuildingPlanSet set = BuildingImportExport.loadPlanSetFromExportDir(buildingKey);
        File file = new File(MillCommonUtilities.getExportDir(), set.key + " resources used.txt");
        try {
            BufferedWriter writer = MillCommonUtilities.getWriter(file);
            BuildingDevUtilities.writePlanCostTextStyle(set, writer);
            writer.close();
            Mill.proxy.localTranslatedSentence(Mill.proxy.getTheSinglePlayer(), 'f', "importtable.costexported", "export/" + file.getName());
        }
        catch (IOException e) {
            MillLog.printException(e);
        }
    }

    public static int importTableHandleImportRequest(EntityPlayer player, Point tablePos, String source, String buildingKey, boolean importAll, int variation, int level, int orientation, boolean importMockBlocks) {
        BuildingPlanSet importSet = null;
        if (source.equals(EXPORT_DIR)) {
            importSet = BuildingImportExport.loadPlanSetFromExportDir(buildingKey);
        } else {
            importSet = Culture.getCultureByName(source).getBuildingPlanSet(buildingKey);
            BuildingImportExport.copyPlanSetToExportDir(importSet);
            if (importSet.getFirstStartingPlan().parentBuildingPlan != null) {
                String parentBuildingPlanKey = importSet.getFirstStartingPlan().parentBuildingPlan;
                String parentSuffix = parentBuildingPlanKey.split("_")[parentBuildingPlanKey.split("_").length - 1].toUpperCase();
                String parentBuildingKey = parentBuildingPlanKey.substring(0, parentBuildingPlanKey.length() - parentSuffix.length() - 1);
                BuildingImportExport.copyPlanSetToExportDir(Culture.getCultureByName(source).getBuildingPlanSet(parentBuildingKey));
            }
        }
        if (importSet != null) {
            if (!importAll) {
                BuildingImportExport.importTableImportBuilding(player, tablePos, null, importSet, variation, level, orientation, importMockBlocks);
                return importSet.plans.get((int)variation)[0].length + 2 + importSet.plans.get((int)variation)[0].areaToClear;
            }
            int xDelta = 0;
            for (int aVariation = 0; aVariation < importSet.plans.size(); ++aVariation) {
                BuildingPlan basePlan = importSet.plans.get(aVariation)[0];
                int orientatedLength = basePlan.length;
                int orientatedWidth = basePlan.width;
                int orientedGapLength = basePlan.areaToClearLengthAfter + basePlan.areaToClearLengthBefore;
                int orientedGapWidth = basePlan.areaToClearWidthAfter + basePlan.areaToClearWidthBefore;
                if (orientation % 2 == 1) {
                    orientatedLength = basePlan.width;
                    orientatedWidth = basePlan.length;
                    orientedGapLength = basePlan.areaToClearWidthAfter + basePlan.areaToClearWidthBefore;
                    orientedGapWidth = basePlan.areaToClearLengthAfter + basePlan.areaToClearLengthBefore;
                }
                int zDelta = 0;
                for (int aLevel = 0; aLevel < importSet.plans.get(aVariation).length; ++aLevel) {
                    BuildingImportExport.importTableImportBuilding(player, tablePos.getRelative(xDelta, 0.0, zDelta), tablePos, importSet, aVariation, aLevel, orientation, importMockBlocks);
                    zDelta += orientatedWidth + 2 + orientedGapWidth;
                }
                xDelta += orientatedLength + 2 + orientedGapLength;
            }
            return xDelta;
        }
        return 0;
    }

    public static void importTableImportBuilding(EntityPlayer player, Point tablePos, Point parentTablePos, BuildingPlanSet planSet, int variation, int maxLevel, int orientation, boolean importMockBlocks) {
        BuildingPlan basePlan = planSet.getPlan(variation, 0);
        int orientatedLength = basePlan.length;
        int orientatedWidth = basePlan.width;
        if (orientation % 2 == 1) {
            orientatedLength = basePlan.width;
            orientatedWidth = basePlan.length;
        }
        if (basePlan.parentBuildingPlan != null) {
            String parentBuildingPlanKey = basePlan.parentBuildingPlan;
            String parentSuffix = parentBuildingPlanKey.split("_")[parentBuildingPlanKey.split("_").length - 1].toUpperCase();
            int parentVariation = parentSuffix.charAt(0) - 65;
            int parentLevel = Integer.parseInt(parentSuffix.substring(1, parentSuffix.length()));
            String parentBuildingKey = parentBuildingPlanKey.substring(0, parentBuildingPlanKey.length() - parentSuffix.length() - 1);
            BuildingPlanSet parentBuildingSet = BuildingImportExport.loadPlanSetFromExportDir(parentBuildingKey);
            BuildingLocation parentLocation = new BuildingLocation(parentBuildingSet.getPlan(parentVariation, parentLevel), tablePos.getRelative(orientatedLength / 2 + 1, 0.0, orientatedWidth / 2 + 1), orientation);
            for (int level = 0; level <= parentLevel; ++level) {
                parentLocation.level = level;
                parentBuildingSet.buildLocation(Mill.getMillWorld(player.field_70170_p), null, parentLocation, !importMockBlocks, false, null, true, importMockBlocks, null);
            }
        }
        BuildingLocation location = new BuildingLocation(basePlan, tablePos.getRelative(orientatedLength / 2 + 1, 0.0, orientatedWidth / 2 + 1), orientation);
        int level = 0;
        while (level <= maxLevel) {
            location.level = level++;
            planSet.buildLocation(Mill.getMillWorld(player.field_70170_p), null, location, true, false, null, true, importMockBlocks, null);
        }
        BuildingImportExport.drawWoolBorders(player, tablePos, orientatedLength, orientatedWidth);
        TileEntityImportTable table = tablePos.getImportTable(player.field_70170_p);
        if (table == null) {
            tablePos.setBlock(player.field_70170_p, MillBlocks.IMPORT_TABLE, 0, true, false);
            table = tablePos.getImportTable(player.field_70170_p);
        }
        if (table == null) {
            MillLog.error(null, "Can neither find nor create import table at location: " + tablePos);
        } else {
            BuildingPlan plan = planSet.getPlan(variation, maxLevel);
            table.updatePlan(planSet.key, plan.length, plan.width, variation, maxLevel, plan.startLevel, player);
            table.setParentTablePos(parentTablePos);
        }
        ServerSender.sendTranslatedSentence(player, 'f', "importtable.importedbuildingplan", planSet.getPlan((int)variation, (int)maxLevel).planName);
    }

    private static BuildingPlanSet loadPlanSetFromExportDir(String parentBuildingKey) {
        File exportDir = MillCommonUtilities.getExportDir();
        VirtualDir exportVirtualDir = new VirtualDir(exportDir);
        File parentBuildingFile = new File(exportDir, parentBuildingKey + "_A.txt");
        BuildingPlanSet parentBuildingSet = new BuildingPlanSet(null, parentBuildingKey, exportVirtualDir, parentBuildingFile);
        try {
            parentBuildingSet.loadPictPlans(true);
        }
        catch (Exception e) {
            MillLog.printException("Exception when loading plan:", e);
        }
        return parentBuildingSet;
    }

    public static void loadReverseBuildingPoints(Boolean exportPreserveGround, Boolean exportRegularChests) {
        reverseColourPoints.clear();
        for (PointType pt : PointType.colourPoints.values()) {
            if (pt.specialType != null) continue;
            Block block = pt.getBlock();
            if (block == null) {
                MillLog.error(pt, "PointType has neither name nor block.");
                continue;
            }
            reverseColourPoints.put(BuildingImportExport.getPointHash(pt.getBlockState()), pt);
            if (block == Blocks.field_150362_t || block == Blocks.field_150361_u) {
                reverseColourPoints.put(BuildingImportExport.getPointHash(pt.getBlock(), pt.getBlock().func_176201_c(pt.getBlockState().func_177226_a((IProperty)BlockLeaves.field_176237_a, (Comparable)Boolean.valueOf(true)).func_177226_a((IProperty)BlockLeaves.field_176236_b, (Comparable)Boolean.valueOf(true)))), pt);
                reverseColourPoints.put(BuildingImportExport.getPointHash(pt.getBlock(), pt.getBlock().func_176201_c(pt.getBlockState().func_177226_a((IProperty)BlockLeaves.field_176237_a, (Comparable)Boolean.valueOf(true)).func_177226_a((IProperty)BlockLeaves.field_176236_b, (Comparable)Boolean.valueOf(false)))), pt);
                reverseColourPoints.put(BuildingImportExport.getPointHash(pt.getBlock(), pt.getBlock().func_176201_c(pt.getBlockState().func_177226_a((IProperty)BlockLeaves.field_176237_a, (Comparable)Boolean.valueOf(false)).func_177226_a((IProperty)BlockLeaves.field_176236_b, (Comparable)Boolean.valueOf(true)))), pt);
                reverseColourPoints.put(BuildingImportExport.getPointHash(pt.getBlock(), pt.getBlock().func_176201_c(pt.getBlockState().func_177226_a((IProperty)BlockLeaves.field_176237_a, (Comparable)Boolean.valueOf(false)).func_177226_a((IProperty)BlockLeaves.field_176236_b, (Comparable)Boolean.valueOf(false)))), pt);
                continue;
            }
            if (BlockItemUtilities.isPath(block)) {
                reverseColourPoints.put(BuildingImportExport.getPointHash(pt.getBlockState().func_177226_a((IProperty)IBlockPath.STABLE, (Comparable)Boolean.valueOf(false))), pt);
                continue;
            }
            if (pt.getBlock() == MillBlocks.PANEL) {
                reverseColourPoints.put(BuildingImportExport.getPointHash(Blocks.field_150444_as, pt.getMeta()), pt);
                continue;
            }
            if (pt.getBlock() instanceof BlockDoor) {
                reverseColourPoints.put(BuildingImportExport.getPointHash(pt.getBlockState()), pt);
                reverseColourPoints.put(BuildingImportExport.getPointHash(pt.getBlockState().func_177226_a((IProperty)BlockDoor.field_176519_b, (Comparable)Boolean.valueOf(true))), pt);
                continue;
            }
            if (pt.getBlock() instanceof BlockFenceGate) {
                reverseColourPoints.put(BuildingImportExport.getPointHash(pt.getBlockState()), pt);
                reverseColourPoints.put(BuildingImportExport.getPointHash(pt.getBlockState().func_177226_a((IProperty)BlockFenceGate.field_176466_a, (Comparable)Boolean.valueOf(true))), pt);
                continue;
            }
            if (pt.getBlock() == Blocks.field_150460_al) {
                reverseColourPoints.put(BuildingImportExport.getPointHash(pt.getBlockState()), pt);
                int meta = pt.getBlock().func_176201_c(pt.getBlockState());
                IBlockState litBlockState = Blocks.field_150470_am.func_176203_a(meta);
                reverseColourPoints.put(BuildingImportExport.getPointHash(litBlockState), pt);
                continue;
            }
            if (!(pt.getBlock() instanceof BlockFlowerPot)) continue;
            reverseColourPoints.put(BuildingImportExport.getPointHash(pt.getBlock(), pt.getMeta()), pt);
        }
        for (PointType pt : PointType.colourPoints.values()) {
            IBlockState bs;
            if (pt.specialType == null) continue;
            if (pt.specialType.equals("brewingstand")) {
                for (int i = 0; i < 16; ++i) {
                    reverseColourPoints.put(BuildingImportExport.getPointHash(Blocks.field_150382_bo, i), pt);
                }
                continue;
            }
            if (pt.specialType.equals("lockedchestTop")) {
                IBlockState bs2 = MillBlocks.LOCKED_CHEST.func_176223_P().func_177226_a((IProperty)BlockChest.field_176459_a, (Comparable)EnumFacing.WEST);
                reverseColourPoints.put(BuildingImportExport.getPointHash(bs2), pt);
                if (exportRegularChests.booleanValue()) continue;
                bs2 = Blocks.field_150486_ae.func_176223_P().func_177226_a((IProperty)BlockChest.field_176459_a, (Comparable)EnumFacing.WEST);
                reverseColourPoints.put(BuildingImportExport.getPointHash(bs2), pt);
                continue;
            }
            if (pt.specialType.equals("lockedchestBottom")) {
                IBlockState bs3 = MillBlocks.LOCKED_CHEST.func_176223_P().func_177226_a((IProperty)BlockChest.field_176459_a, (Comparable)EnumFacing.EAST);
                reverseColourPoints.put(BuildingImportExport.getPointHash(bs3), pt);
                if (exportRegularChests.booleanValue()) continue;
                bs3 = Blocks.field_150486_ae.func_176223_P().func_177226_a((IProperty)BlockChest.field_176459_a, (Comparable)EnumFacing.EAST);
                reverseColourPoints.put(BuildingImportExport.getPointHash(bs3), pt);
                continue;
            }
            if (pt.specialType.equals("lockedchestLeft")) {
                IBlockState bs4 = MillBlocks.LOCKED_CHEST.func_176223_P().func_177226_a((IProperty)BlockChest.field_176459_a, (Comparable)EnumFacing.SOUTH);
                reverseColourPoints.put(BuildingImportExport.getPointHash(bs4), pt);
                if (exportRegularChests.booleanValue()) continue;
                bs4 = Blocks.field_150486_ae.func_176223_P().func_177226_a((IProperty)BlockChest.field_176459_a, (Comparable)EnumFacing.SOUTH);
                reverseColourPoints.put(BuildingImportExport.getPointHash(bs4), pt);
                continue;
            }
            if (pt.specialType.equals("lockedchestRight")) {
                IBlockState bs5 = MillBlocks.LOCKED_CHEST.func_176223_P().func_177226_a((IProperty)BlockChest.field_176459_a, (Comparable)EnumFacing.NORTH);
                reverseColourPoints.put(BuildingImportExport.getPointHash(bs5), pt);
                if (exportRegularChests.booleanValue()) continue;
                bs5 = Blocks.field_150486_ae.func_176223_P().func_177226_a((IProperty)BlockChest.field_176459_a, (Comparable)EnumFacing.NORTH);
                reverseColourPoints.put(BuildingImportExport.getPointHash(bs5), pt);
                continue;
            }
            if (pt.specialType.equals("mainchestTop")) {
                IBlockState bs6 = MillBlocks.MAIN_CHEST.func_176203_a(EnumFacing.WEST.func_176745_a());
                reverseColourPoints.put(BuildingImportExport.getPointHash(bs6), pt);
                continue;
            }
            if (pt.specialType.equals("mainchestBottom")) {
                IBlockState bs7 = MillBlocks.MAIN_CHEST.func_176203_a(EnumFacing.EAST.func_176745_a());
                reverseColourPoints.put(BuildingImportExport.getPointHash(bs7), pt);
                continue;
            }
            if (pt.specialType.equals("mainchestLeft")) {
                IBlockState bs8 = MillBlocks.MAIN_CHEST.func_176203_a(EnumFacing.SOUTH.func_176745_a());
                reverseColourPoints.put(BuildingImportExport.getPointHash(bs8), pt);
                continue;
            }
            if (pt.specialType.equals("mainchestRight")) {
                IBlockState bs9 = MillBlocks.MAIN_CHEST.func_176203_a(EnumFacing.NORTH.func_176745_a());
                reverseColourPoints.put(BuildingImportExport.getPointHash(bs9), pt);
                continue;
            }
            if (pt.specialType.equals("grass") && !exportPreserveGround.booleanValue()) {
                IBlockState bs10 = Blocks.field_150349_c.func_176223_P();
                reverseColourPoints.put(BuildingImportExport.getPointHash(bs10), pt);
                continue;
            }
            if (pt.isSubType("villageBannerWall")) {
                String facing = pt.specialType.substring(17);
                IBlockState bs11 = MillBlocks.VILLAGE_BANNER_WALL.func_176223_P().func_177226_a((IProperty)BlockBanner.field_176449_a, (Comparable)EnumFacing.func_176739_a((String)facing));
                reverseColourPoints.put(BuildingImportExport.getPointHash(bs11), pt);
                continue;
            }
            if (pt.isSubType("villageBannerStanding")) {
                int rotation = Integer.parseInt(pt.specialType.substring(21));
                IBlockState bs12 = MillBlocks.VILLAGE_BANNER_STANDING.func_176203_a(rotation);
                reverseColourPoints.put(BuildingImportExport.getPointHash(bs12), pt);
                continue;
            }
            if (pt.isSubType("cultureBannerWall")) {
                String facing = pt.specialType.substring(17);
                IBlockState bs13 = MillBlocks.CULTURE_BANNER_WALL.func_176223_P().func_177226_a((IProperty)BlockBanner.field_176449_a, (Comparable)EnumFacing.func_176739_a((String)facing));
                reverseColourPoints.put(BuildingImportExport.getPointHash(bs13), pt);
                continue;
            }
            if (pt.isSubType("cultureBannerStanding")) {
                int rotation = Integer.parseInt(pt.specialType.substring(21));
                IBlockState bs14 = MillBlocks.CULTURE_BANNER_STANDING.func_176203_a(rotation);
                reverseColourPoints.put(BuildingImportExport.getPointHash(bs14), pt);
                continue;
            }
            for (MockBlockMarker.Type type : MockBlockMarker.Type.values()) {
                if (!type.name.equalsIgnoreCase(pt.specialType)) continue;
                bs = MillBlocks.MARKER_BLOCK.func_176203_a(type.meta);
                reverseColourPoints.put(BuildingImportExport.getPointHash(bs), pt);
            }
            if (exportPreserveGround.booleanValue() && "preserveground".equalsIgnoreCase(pt.specialType)) {
                reverseColourPoints.put(BuildingImportExport.getPointHash((Block)Blocks.field_150349_c, 0), pt);
                reverseColourPoints.put(BuildingImportExport.getPointHash((Block)Blocks.field_150354_m, 0), pt);
            }
            for (Enum enum_ : MockBlockAnimalSpawn.Creature.values()) {
                if (!pt.specialType.equalsIgnoreCase(((MockBlockAnimalSpawn.Creature)enum_).name + "spawn")) continue;
                bs = MillBlocks.ANIMAL_SPAWN.func_176203_a(((MockBlockAnimalSpawn.Creature)enum_).meta);
                reverseColourPoints.put(BuildingImportExport.getPointHash(bs), pt);
            }
            for (Enum enum_ : MockBlockSource.Resource.values()) {
                if (!pt.specialType.equalsIgnoreCase(((MockBlockSource.Resource)enum_).name + "source")) continue;
                bs = MillBlocks.SOURCE.func_176203_a(((MockBlockSource.Resource)enum_).meta);
                reverseColourPoints.put(BuildingImportExport.getPointHash(bs), pt);
            }
            for (Enum enum_ : MockBlockFree.Resource.values()) {
                if (!pt.specialType.equalsIgnoreCase("free" + ((MockBlockFree.Resource)enum_).name)) continue;
                bs = MillBlocks.FREE_BLOCK.func_176203_a(((MockBlockFree.Resource)enum_).meta);
                reverseColourPoints.put(BuildingImportExport.getPointHash(bs), pt);
            }
            for (Enum enum_ : MockBlockTreeSpawn.TreeType.values()) {
                if (!pt.specialType.equalsIgnoreCase(((MockBlockTreeSpawn.TreeType)enum_).name + "spawn")) continue;
                bs = MillBlocks.TREE_SPAWN.func_176203_a(((MockBlockTreeSpawn.TreeType)enum_).meta);
                reverseColourPoints.put(BuildingImportExport.getPointHash(bs), pt);
            }
            for (Enum enum_ : MockBlockSoil.CropType.values()) {
                if (!pt.specialType.equalsIgnoreCase(((MockBlockSoil.CropType)enum_).name)) continue;
                bs = MillBlocks.SOIL_BLOCK.func_176203_a(((MockBlockSoil.CropType)enum_).meta);
                reverseColourPoints.put(BuildingImportExport.getPointHash(bs), pt);
            }
            for (Enum enum_ : MockBlockDecor.Type.values()) {
                if (!pt.specialType.equalsIgnoreCase(((MockBlockDecor.Type)enum_).name)) continue;
                bs = MillBlocks.DECOR_BLOCK.func_176203_a(((MockBlockDecor.Type)enum_).meta);
                reverseColourPoints.put(BuildingImportExport.getPointHash(bs), pt);
            }
        }
    }

    private static IBlockState mirrorBlock(PointType pt, boolean horizontal) {
        Comparable rawFacingValue = BlockStateUtilities.getPropertyValueByName(pt.getBlockState(), "facing");
        if (rawFacingValue != null && rawFacingValue instanceof EnumFacing) {
            EnumFacing facing = (EnumFacing)rawFacingValue;
            if (horizontal) {
                if (facing == EnumFacing.EAST) {
                    facing = EnumFacing.WEST;
                } else if (facing == EnumFacing.WEST) {
                    facing = EnumFacing.EAST;
                }
            } else if (facing == EnumFacing.NORTH) {
                facing = EnumFacing.SOUTH;
            } else if (facing == EnumFacing.SOUTH) {
                facing = EnumFacing.NORTH;
            }
            IBlockState adjustedBlockState = BlockStateUtilities.setPropertyValueByName(pt.getBlockState(), "facing", (Comparable)facing);
            return adjustedBlockState;
        }
        return null;
    }

    private static void mirrorPlan(BuildingPlanSet existingSet, boolean horizontalmirror) {
        for (BuildingPlan[] plans : existingSet.plans) {
            for (BuildingPlan plan : plans) {
                int planLength = plan.plan[0].length;
                int planWidth = plan.plan[0][0].length;
                for (int floorPos = 0; floorPos < plan.plan.length; ++floorPos) {
                    for (int lengthPos = 0; lengthPos < plan.plan[0].length; ++lengthPos) {
                        for (int widthPos = 0; widthPos < plan.plan[0][0].length; ++widthPos) {
                            int newWidthPos;
                            int newLengthPos;
                            if (horizontalmirror) {
                                newLengthPos = planLength - lengthPos - 1;
                                newWidthPos = widthPos;
                            } else {
                                newLengthPos = lengthPos;
                                newWidthPos = planWidth - widthPos - 1;
                            }
                            if (plan.plan[floorPos][lengthPos][widthPos].isType("empty")) continue;
                            if (plan.plan[floorPos][lengthPos][widthPos].specialType != null) {
                                plan.plan[floorPos][newLengthPos][newWidthPos] = plan.plan[floorPos][lengthPos][widthPos];
                                continue;
                            }
                            IBlockState blockState = BuildingImportExport.mirrorBlock(plan.plan[floorPos][lengthPos][widthPos], horizontalmirror);
                            if (blockState != null) {
                                PointType newPt = BuildingImportExport.getPointTypeFromBlockState(blockState);
                                if (newPt == null) continue;
                                plan.plan[floorPos][lengthPos][widthPos] = newPt;
                                continue;
                            }
                            plan.plan[floorPos][newLengthPos][newWidthPos] = plan.plan[floorPos][lengthPos][widthPos];
                        }
                    }
                }
            }
        }
    }

    public static void negationWandExportBuilding(EntityPlayer player, World world, Point startPoint) {
        try {
            int zEnd;
            int xEnd;
            TileEntitySign sign = startPoint.getSign(world);
            if (sign == null) {
                return;
            }
            if (sign.field_145915_a[0] == null || sign.field_145915_a[0].func_150260_c().length() == 0) {
                Mill.proxy.localTranslatedSentence(Mill.proxy.getTheSinglePlayer(), '6', "export.errornoname", new String[0]);
                return;
            }
            String planName = sign.field_145915_a[0].func_150260_c().toLowerCase();
            int variation = 0;
            for (int letter = 0; letter < 26; ++letter) {
                if (!planName.endsWith("_" + (char)(97 + letter))) continue;
                planName = planName.substring(0, planName.length() - 2);
                variation = letter;
            }
            int upgradeLevel = -1;
            if (sign.field_145915_a[1] != null && sign.field_145915_a[1].func_150260_c().length() > 0) {
                try {
                    upgradeLevel = Integer.parseInt(sign.field_145915_a[1].func_150260_c());
                }
                catch (Exception e) {
                    ServerSender.sendTranslatedSentence(player, '6', "export.errorinvalidupgradelevel", new String[0]);
                    return;
                }
            }
            boolean found = false;
            for (xEnd = startPoint.getiX() + 1; !found && xEnd < startPoint.getiX() + 257; ++xEnd) {
                Block block = WorldUtilities.getBlock(world, xEnd, startPoint.getiY(), startPoint.getiZ());
                if (block != Blocks.field_150472_an) continue;
                found = true;
                break;
            }
            if (!found) {
                Mill.proxy.localTranslatedSentence(Mill.proxy.getTheSinglePlayer(), '6', "export.errornoendsigneast", new String[0]);
                return;
            }
            found = false;
            for (zEnd = startPoint.getiZ() + 1; !found && zEnd < startPoint.getiZ() + 257; ++zEnd) {
                Block block = WorldUtilities.getBlock(world, startPoint.getiX(), startPoint.getiY(), zEnd);
                if (block != Blocks.field_150472_an) continue;
                found = true;
                break;
            }
            if (!found) {
                Mill.proxy.localTranslatedSentence(Mill.proxy.getTheSinglePlayer(), '6', "export.errornoendsignsouth", new String[0]);
                return;
            }
            int startLevel = -1;
            if (sign.field_145915_a[2] != null && sign.field_145915_a[2].func_150260_c().length() > 0) {
                try {
                    startLevel = Integer.parseInt(sign.field_145915_a[2].func_150260_c());
                }
                catch (Exception e) {
                    Mill.proxy.localTranslatedSentence(Mill.proxy.getTheSinglePlayer(), '6', "export.errorstartinglevel", new String[0]);
                }
            } else {
                Mill.proxy.localTranslatedSentence(Mill.proxy.getTheSinglePlayer(), 'f', "export.defaultstartinglevel", new String[0]);
            }
            boolean exportSnow = false;
            if (sign.field_145915_a[3] != null && sign.field_145915_a[3].func_150260_c().equals("snow")) {
                exportSnow = true;
            }
            int length = xEnd - startPoint.getiX() - 1;
            int width = zEnd - startPoint.getiZ() - 1;
            int orientation = 0;
            if (sign.field_145915_a[3] != null && sign.field_145915_a[3].func_150260_c().startsWith("or:")) {
                String orientationString = sign.field_145915_a[3].func_150260_c().substring(3, sign.field_145915_a[3].func_150260_c().length());
                orientation = Integer.parseInt(orientationString);
            }
            if (orientation != 0) {
                Mill.proxy.localTranslatedSentence(Mill.proxy.getTheSinglePlayer(), '6', "export.northfacingonly", new String[0]);
                return;
            }
            BuildingImportExport.exportBuilding(world, startPoint, planName, variation, length, width, orientation, upgradeLevel, startLevel, exportSnow, false, true);
        }
        catch (Exception e) {
            MillLog.printException("Error when trying to store a building: ", e);
        }
    }

    private static void replaceWoodType(BuildingPlanSet existingSet, BlockPlanks.EnumType newWoodType) {
        PropertyEnum newLeaveBlockVariantProperty;
        PropertyEnum newLogBlockVariantProperty;
        BlockLeaves newLeaveBlock;
        Block newLogBlock;
        if (newWoodType.equals((Object)BlockPlanks.EnumType.ACACIA) || newWoodType.equals((Object)BlockPlanks.EnumType.DARK_OAK)) {
            newLogBlock = Blocks.field_150363_s;
            newLeaveBlock = Blocks.field_150361_u;
            newLogBlockVariantProperty = BlockNewLog.field_176300_b;
            newLeaveBlockVariantProperty = BlockNewLeaf.field_176240_P;
        } else {
            newLogBlock = Blocks.field_150364_r;
            newLeaveBlock = Blocks.field_150362_t;
            newLogBlockVariantProperty = BlockOldLog.field_176301_b;
            newLeaveBlockVariantProperty = BlockOldLeaf.field_176239_P;
        }
        Block[][] blocksToReplace = new Block[][]{{Blocks.field_150476_ad, Blocks.field_150485_bF, Blocks.field_150487_bG, Blocks.field_150481_bH, Blocks.field_150400_ck, Blocks.field_150401_cl}, {Blocks.field_180413_ao, Blocks.field_180414_ap, Blocks.field_180412_aq, Blocks.field_180411_ar, Blocks.field_180410_as, Blocks.field_180409_at}, {Blocks.field_180407_aO, Blocks.field_180408_aP, Blocks.field_180404_aQ, Blocks.field_180403_aR, Blocks.field_180405_aT, Blocks.field_180406_aS}, {Blocks.field_180390_bo, Blocks.field_180391_bp, Blocks.field_180392_bq, Blocks.field_180386_br, Blocks.field_180387_bt, Blocks.field_180385_bs}};
        for (BuildingPlan[] plans : existingSet.plans) {
            for (BuildingPlan plan : plans) {
                for (int floorPos = 0; floorPos < plan.plan.length; ++floorPos) {
                    for (int lengthPos = 0; lengthPos < plan.plan[0].length; ++lengthPos) {
                        for (int widthPos = 0; widthPos < plan.plan[0][0].length; ++widthPos) {
                            IBlockState adjustedBlockState;
                            PointType newPt;
                            PointType newPt2;
                            IBlockState newBlockState;
                            PointType pt = plan.plan[floorPos][lengthPos][widthPos];
                            if (pt.getBlock() == Blocks.field_150364_r || pt.getBlock() == Blocks.field_150363_s) {
                                newBlockState = newLogBlock.func_176203_a(pt.getMeta()).func_177226_a((IProperty)newLogBlockVariantProperty, (Comparable)newWoodType);
                                newPt2 = BuildingImportExport.getPointTypeFromBlockState(newBlockState);
                                if (newPt2 == null) continue;
                                plan.plan[floorPos][lengthPos][widthPos] = newPt2;
                                continue;
                            }
                            if (pt.getBlock() == Blocks.field_150362_t || pt.getBlock() == Blocks.field_150361_u) {
                                newBlockState = newLeaveBlock.func_176203_a(pt.getMeta()).func_177226_a((IProperty)newLeaveBlockVariantProperty, (Comparable)newWoodType);
                                newPt2 = BuildingImportExport.getPointTypeFromBlockState(newBlockState);
                                if (newPt2 == null) continue;
                                plan.plan[floorPos][lengthPos][widthPos] = newPt2;
                                continue;
                            }
                            if (pt.getBlockState() == null) continue;
                            Comparable rawWoodTypeValue = BlockStateUtilities.getPropertyValueByName(pt.getBlockState(), "variant");
                            if (rawWoodTypeValue != null && rawWoodTypeValue instanceof BlockPlanks.EnumType && (newPt = BuildingImportExport.getPointTypeFromBlockState(adjustedBlockState = BlockStateUtilities.setPropertyValueByName(pt.getBlockState(), "variant", (Comparable)newWoodType))) != null) {
                                plan.plan[floorPos][lengthPos][widthPos] = newPt;
                            }
                            Block[][] blockArrayArray = blocksToReplace;
                            int n = blockArrayArray.length;
                            for (int i = 0; i < n; ++i) {
                                Block[] blockList;
                                for (Block block : blockList = blockArrayArray[i]) {
                                    Block newBlock;
                                    IBlockState newBlockState2;
                                    PointType newPt3;
                                    if (pt.getBlock() != block || (newPt3 = BuildingImportExport.getPointTypeFromBlockState(newBlockState2 = (newBlock = blockList[newWoodType.func_176839_a()]).func_176203_a(pt.getMeta()))) == null) continue;
                                    plan.plan[floorPos][lengthPos][widthPos] = newPt3;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static void summoningWandImportBuildingPlan(EntityPlayer player, World world, Point startPoint, int variation, BuildingPlanSet existingSet, int upgradeLevel, boolean includeSpecialPoints, int orientation, boolean createSign) {
        TileEntitySign sign;
        BuildingPlan basePlan = existingSet.plans.get(variation)[0];
        BuildingPlan upgradePlan = existingSet.plans.get(variation)[upgradeLevel];
        if (createSign) {
            Point startingSign = startPoint;
            startingSign.setBlock(world, Blocks.field_150472_an, 0, true, false);
            sign = startingSign.getSign(world);
            char variationLetter = (char)(65 + variation);
            sign.field_145915_a[0] = new TextComponentString(existingSet.key + "_" + variationLetter);
            sign.field_145915_a[1] = new TextComponentString("" + upgradeLevel);
            sign.field_145915_a[2] = new TextComponentString("" + upgradePlan.startLevel);
            sign.field_145915_a[3] = !includeSpecialPoints ? new TextComponentString("nomock") : (orientation > 0 ? new TextComponentString("or:" + orientation) : new TextComponentString(""));
        }
        int orientatedLength = basePlan.length;
        int orientatedWidth = basePlan.width;
        if (orientation % 2 == 1) {
            orientatedLength = basePlan.width;
            orientatedWidth = basePlan.length;
        }
        BuildingLocation location = new BuildingLocation(basePlan, startPoint.getRelative(orientatedLength / 2 + 1, 0.0, orientatedWidth / 2 + 1), orientation);
        for (int i = 0; i <= upgradeLevel; ++i) {
            if (player != null) {
                ServerSender.sendTranslatedSentence(player, 'f', "import.buildinglevel", "" + i);
            }
            existingSet.buildLocation(Mill.getMillWorld(world), null, location, !includeSpecialPoints, false, null, true, includeSpecialPoints, null);
            ++location.level;
        }
        Point eastSign = startPoint.getRelative(orientatedLength + 1, 0.0, 0.0);
        eastSign.setBlock(world, Blocks.field_150472_an, 0, true, false);
        sign = eastSign.getSign(world);
        sign.field_145915_a[0] = new TextComponentString("East End");
        sign.field_145915_a[1] = new TextComponentString("(length)");
        sign.field_145915_a[2] = new TextComponentString("");
        sign.field_145915_a[3] = new TextComponentString("");
        Point southSign = startPoint.getRelative(0.0, 0.0, orientatedWidth + 1);
        southSign.setBlock(world, Blocks.field_150472_an, 0, true, false);
        sign = southSign.getSign(world);
        sign.field_145915_a[0] = new TextComponentString("South End");
        sign.field_145915_a[1] = new TextComponentString("(width)");
        sign.field_145915_a[2] = new TextComponentString("");
        sign.field_145915_a[3] = new TextComponentString("");
    }

    public static void summoningWandImportBuildingRequest(EntityPlayer player, World world, Point startPoint) {
        try {
            TileEntitySign sign = startPoint.getSign(world);
            if (sign == null) {
                return;
            }
            if (sign.field_145915_a[0] == null || sign.field_145915_a[0].func_150260_c().length() == 0) {
                ServerSender.sendTranslatedSentence(player, '6', "import.errornoname", new String[0]);
                return;
            }
            String buildingKey = sign.field_145915_a[0].func_150260_c().toLowerCase();
            int variation = 0;
            boolean explicitVariation = false;
            for (int letter = 0; letter < 26; ++letter) {
                if (!buildingKey.endsWith("_" + (char)(97 + letter))) continue;
                buildingKey = buildingKey.substring(0, buildingKey.length() - 2);
                variation = letter;
                explicitVariation = true;
            }
            char variationLetter = 'A';
            variationLetter = (char)(variationLetter + variation);
            File exportDir = MillCommonUtilities.getExportDir();
            File buildingFile = new File(exportDir, buildingKey + "_" + variationLetter + ".txt");
            if (!buildingFile.exists()) {
                File foundFile = null;
                BuildingPlanSet foundPlanSet = null;
                for (Culture culture : Culture.ListCultures) {
                    for (BuildingPlanSet planSet : culture.ListPlanSets) {
                        if (foundFile != null || !planSet.key.equals(buildingKey) || planSet.plans.size() <= variation) continue;
                        foundFile = planSet.plans.get(variation)[0].getLoadedFromFile();
                        foundPlanSet = planSet;
                    }
                }
                if (foundFile == null) {
                    for (Culture culture : Culture.ListCultures) {
                        for (BuildingPlanSet planSet : culture.ListPlanSets) {
                            if (foundFile != null || planSet.plans.size() <= variation || planSet.plans.get((int)variation)[0].nativeName == null || !planSet.plans.get((int)variation)[0].nativeName.toLowerCase().equals(buildingKey)) continue;
                            foundFile = planSet.plans.get(variation)[0].getLoadedFromFile();
                            foundPlanSet = planSet;
                            buildingKey = planSet.key;
                            ITextComponent[] oldSignData = sign.field_145915_a;
                            startPoint.setBlock(world, Blocks.field_150472_an, 0, true, false);
                            TileEntitySign newSign = startPoint.getSign(world);
                            newSign.field_145915_a[0] = new TextComponentString(buildingKey);
                            newSign.field_145915_a[1] = oldSignData[1];
                            newSign.field_145915_a[2] = oldSignData[2];
                            newSign.field_145915_a[3] = oldSignData[3];
                        }
                    }
                }
                if (foundFile != null) {
                    ServerSender.sendTranslatedSentence(player, '6', "import.copyingfrom", foundFile.getAbsolutePath().replace("\\", "/"));
                    Path exportPath = exportDir.toPath();
                    Path inputPath = foundFile.toPath().getParent();
                    for (int exportVariation = 0; exportVariation < foundPlanSet.plans.size(); ++exportVariation) {
                        char exportVariationLetter = (char)(65 + exportVariation);
                        String txtFileName = foundPlanSet.key + "_" + exportVariationLetter + ".txt";
                        Files.copy(inputPath.resolve(txtFileName), exportPath.resolve(txtFileName), StandardCopyOption.REPLACE_EXISTING);
                        for (int buildingUpgrade = 0; buildingUpgrade < foundPlanSet.plans.get(exportVariation).length; ++buildingUpgrade) {
                            String string = foundPlanSet.key + "_" + exportVariationLetter + buildingUpgrade + ".png";
                            Files.copy(inputPath.resolve(string), exportPath.resolve(string), StandardCopyOption.REPLACE_EXISTING);
                        }
                    }
                } else {
                    ServerSender.sendTranslatedSentence(player, '6', "import.errornotfound", new String[0]);
                    return;
                }
            }
            BuildingPlanSet existingSet = BuildingImportExport.loadPlanSetFromExportDir(buildingKey);
            boolean importAll = sign.field_145915_a[1] != null && sign.field_145915_a[1].func_150260_c().equalsIgnoreCase("all");
            boolean includeSpecialPoints = sign.field_145915_a[3] == null || !sign.field_145915_a[3].func_150260_c().equalsIgnoreCase("nomock");
            int orientation = 0;
            if (sign.field_145915_a[3] != null && sign.field_145915_a[3].func_150260_c().startsWith("or:")) {
                String orientationString = sign.field_145915_a[3].func_150260_c().substring(3, sign.field_145915_a[3].func_150260_c().length());
                orientation = Integer.parseInt(orientationString);
            }
            if (!importAll) {
                int upgradeLevel = 0;
                if (sign.field_145915_a[1] != null && sign.field_145915_a[1].func_150260_c().length() > 0) {
                    try {
                        upgradeLevel = Integer.parseInt(sign.field_145915_a[1].func_150260_c());
                        ServerSender.sendTranslatedSentence(player, 'f', "import.buildingupto", "" + upgradeLevel);
                    }
                    catch (Exception e) {
                        ServerSender.sendTranslatedSentence(player, '6', "import.errorinvalidupgradelevel", new String[0]);
                        return;
                    }
                } else {
                    ServerSender.sendTranslatedSentence(player, 'f', "import.buildinginitialphase", new String[0]);
                }
                if (upgradeLevel >= existingSet.plans.get(variation).length) {
                    ServerSender.sendTranslatedSentence(player, '6', "import.errorupgradeleveltoohigh", new String[0]);
                    return;
                }
                if (sign.field_145915_a[2] != null) {
                    String signLine = sign.field_145915_a[2].func_150260_c();
                    if (signLine.equals("x2")) {
                        BuildingImportExport.doubleHeightPlan(player, existingSet);
                    } else if (signLine.equals("hmirror") || signLine.equals("vmirror")) {
                        boolean horizontalmirror = signLine.equals("hmirror");
                        BuildingImportExport.mirrorPlan(existingSet, horizontalmirror);
                    } else if (signLine.startsWith("wood:")) {
                        String woodTypeName = signLine.substring("wood:".length(), signLine.length());
                        BlockPlanks.EnumType woodType = null;
                        for (BlockPlanks.EnumType type : BlockPlanks.EnumType.values()) {
                            if (!type.func_176610_l().equals(woodTypeName)) continue;
                            woodType = type;
                        }
                        if (woodType == null) {
                            ServerSender.sendTranslatedSentence(player, '6', "import.errorunknownwoodtype", woodTypeName);
                        } else {
                            BuildingImportExport.replaceWoodType(existingSet, woodType);
                        }
                    }
                }
                BuildingImportExport.summoningWandImportBuildingPlan(player, world, startPoint, variation, existingSet, upgradeLevel, includeSpecialPoints, orientation, false);
            } else {
                Point adjustedStartPoint = startPoint.getRelative(1.0, 0.0, 0.0);
                int variationStart = 0;
                int variationEnd = existingSet.plans.size();
                if (explicitVariation) {
                    variationStart = variation;
                    variationEnd = variation + 1;
                }
                for (int variationPos = variationStart; variationPos < variationEnd; ++variationPos) {
                    for (int i = 0; i < existingSet.plans.get(variationPos).length; ++i) {
                        BuildingImportExport.summoningWandImportBuildingPlan(player, world, adjustedStartPoint, variationPos, existingSet, i, includeSpecialPoints, orientation, true);
                        adjustedStartPoint = adjustedStartPoint.getRelative(existingSet.plans.get((int)variationPos)[0].length + 10, 0.0, 0.0);
                    }
                    adjustedStartPoint = new Point(startPoint.x, startPoint.y, adjustedStartPoint.z + (double)existingSet.plans.get((int)variationPos)[0].width + 10.0);
                }
            }
        }
        catch (Exception e) {
            MillLog.printException("Error when importing a building:", e);
        }
    }
}

