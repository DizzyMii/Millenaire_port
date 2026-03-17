/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.client.gui.FontRenderer
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.network.NetworkManager
 *  net.minecraft.network.play.server.SPacketUpdateTileEntity
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package org.millenaire.common.entity;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.millenaire.client.book.BookManager;
import org.millenaire.client.book.TextBook;
import org.millenaire.client.gui.text.GuiText;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.utilities.LanguageUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.village.Building;
import org.millenaire.common.village.buildingmanagers.PanelContentGenerator;

public class TileEntityPanel
extends TileEntity {
    public static final int MAP_VILLAGE_MAP = 1;
    public static final int ETAT_CIVIL = 1;
    public static final int CONSTRUCTIONS = 2;
    public static final int PROJECTS = 3;
    public static final int CONTROLLED_PROJECTS = 4;
    public static final int HOUSE = 5;
    public static final int RESOURCES = 6;
    public static final int ARCHIVES = 7;
    public static final int VILLAGE_MAP = 8;
    public static final int MILITARY = 9;
    public static final int INN_TRADE_GOODS = 10;
    public static final int INN_VISITORS = 11;
    public static final int MARKET_MERCHANTS = 12;
    public static final int CONTROLLED_MILITARY = 13;
    public static final int VISITORS = 14;
    public static final int WALLS = 15;
    public static final int MARVEL_DONATIONS = 20;
    public static final int MARVEL_RESOURCES = 21;
    public List<PanelUntranslatedLine> untranslatedLines = new ArrayList<PanelUntranslatedLine>();
    public List<PanelDisplayLine> displayLines = new ArrayList<PanelDisplayLine>();
    public Point buildingPos = null;
    public long villager_id = 0L;
    public int panelType = 0;
    public ResourceLocation texture = null;

    public TextBook getFullText(EntityPlayer player) {
        if (this.panelType == 0 || this.buildingPos == null) {
            return null;
        }
        Building building = Mill.clientWorld.getBuilding(this.buildingPos);
        if (this.panelType == 1) {
            return PanelContentGenerator.generateEtatCivil(building);
        }
        if (this.panelType == 2) {
            return PanelContentGenerator.generateConstructions(building);
        }
        if (this.panelType == 3) {
            return PanelContentGenerator.generateProjects(player, building);
        }
        if (this.panelType == 5) {
            return PanelContentGenerator.generateHouse(building);
        }
        if (this.panelType == 7) {
            return PanelContentGenerator.generateArchives(building, this.villager_id);
        }
        if (this.panelType == 6) {
            return PanelContentGenerator.generateResources(building);
        }
        if (this.panelType == 8) {
            return PanelContentGenerator.generateVillageMap(building);
        }
        if (this.panelType == 9) {
            return PanelContentGenerator.generateMilitary(building);
        }
        if (this.panelType == 10) {
            return PanelContentGenerator.generateInnGoods(building);
        }
        if (this.panelType == 11) {
            return PanelContentGenerator.generateInnVisitors(building);
        }
        if (this.panelType == 12) {
            return PanelContentGenerator.generateVisitors(building, true);
        }
        if (this.panelType == 14) {
            return PanelContentGenerator.generateVisitors(building, false);
        }
        if (this.panelType == 15) {
            return PanelContentGenerator.generateWalls(player, building);
        }
        if (this.panelType == 20) {
            return building.getMarvelManager().generateDonationPanelText();
        }
        if (this.panelType == 21) {
            return building.getMarvelManager().generateResourcesPanelText();
        }
        return null;
    }

    public int getMapType() {
        if (this.panelType == 8) {
            return 1;
        }
        return 0;
    }

    private IBlockState getState() {
        return this.field_145850_b.func_180495_p(this.field_174879_c);
    }

    @Nullable
    public SPacketUpdateTileEntity func_189518_D_() {
        return new SPacketUpdateTileEntity(this.field_174879_c, -1, this.func_189517_E_());
    }

    public NBTTagCompound func_189517_E_() {
        return this.func_189515_b(new NBTTagCompound());
    }

    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        super.onDataPacket(net, pkt);
        this.handleUpdateTag(pkt.func_148857_g());
    }

    public void func_145839_a(NBTTagCompound compound) {
        super.func_145839_a(compound);
        this.buildingPos = Point.read(compound, "buildingPos");
        this.panelType = compound.func_74762_e("panelType");
        this.villager_id = compound.func_74763_f("villager_id");
        if (compound.func_74764_b("texture")) {
            String textureString = compound.func_74779_i("texture");
            this.texture = new ResourceLocation(textureString);
        }
        int pos = 0;
        this.untranslatedLines.clear();
        while (compound.func_74764_b("Lines_" + pos)) {
            this.untranslatedLines.add(PanelUntranslatedLine.readFromNBT(compound.func_74775_l("Lines_" + pos)));
            ++pos;
        }
    }

    protected void func_190201_b(World worldIn) {
        this.func_145834_a(worldIn);
    }

    private String translatedLines_cutLines(FontRenderer fontrenderer, String text, int maxLength) {
        if (fontrenderer.func_78256_a(text) > maxLength) {
            while (fontrenderer.func_78256_a(text + "...") > maxLength) {
                text = text.substring(0, text.length() - 1);
            }
            text = text + "...";
        }
        return text;
    }

    @SideOnly(value=Side.CLIENT)
    public void translateLines(FontRenderer fontrenderer) {
        this.displayLines.clear();
        int nbExtraLines = 0;
        for (PanelUntranslatedLine line : this.untranslatedLines) {
            PanelDisplayLine displayLine = new PanelDisplayLine();
            displayLine.leftIcon = line.leftIcon;
            displayLine.middleIcon = line.middleIcon;
            displayLine.rightIcon = line.rightIcon;
            displayLine.fullLine = LanguageUtilities.string(line.fullLine);
            displayLine.leftColumn = LanguageUtilities.string(line.leftColumn);
            displayLine.rightColumn = LanguageUtilities.string(line.rightColumn);
            displayLine.centerLine = line.centerLine;
            int maxLength = 80;
            if (displayLine.leftIcon.func_77973_b() != Items.field_190931_a) {
                maxLength = 62;
            }
            displayLine.leftColumn = this.translatedLines_cutLines(fontrenderer, displayLine.leftColumn, 32);
            displayLine.rightColumn = this.translatedLines_cutLines(fontrenderer, displayLine.rightColumn, 32);
            List<String> splitStrings = BookManager.splitStringByLength(new GuiText.FontRendererWrapped(fontrenderer), displayLine.fullLine, maxLength);
            displayLine.fullLine = splitStrings.get(0);
            this.displayLines.add(displayLine);
            if (splitStrings.size() <= 1 || this.untranslatedLines.size() + nbExtraLines + 1 >= 8) continue;
            PanelDisplayLine extraDisplayLine = new PanelDisplayLine();
            extraDisplayLine.fullLine = splitStrings.get(1);
            this.displayLines.add(extraDisplayLine);
            ++nbExtraLines;
        }
    }

    public void triggerUpdate() {
        this.field_145850_b.func_175704_b(this.field_174879_c, this.field_174879_c);
        this.field_145850_b.func_184138_a(this.field_174879_c, this.getState(), this.getState(), 3);
        this.field_145850_b.func_180497_b(this.field_174879_c, this.func_145838_q(), 0, 0);
        this.func_70296_d();
    }

    public NBTTagCompound func_189515_b(NBTTagCompound compound) {
        super.func_189515_b(compound);
        try {
            if (this.buildingPos != null) {
                this.buildingPos.write(compound, "buildingPos");
            }
            compound.func_74768_a("panelType", this.panelType);
            compound.func_74772_a("villager_id", this.villager_id);
            if (this.texture != null) {
                compound.func_74778_a("texture", this.texture.toString());
            }
            for (int i = 0; i < this.untranslatedLines.size(); ++i) {
                compound.func_74782_a("Lines_" + i, (NBTBase)this.untranslatedLines.get(i).writeToNBT(new NBTTagCompound()));
            }
        }
        catch (Exception e) {
            MillLog.printException("Error writing panel", e);
        }
        return compound;
    }

    public static class PanelUntranslatedLine {
        private String[] fullLine = new String[]{""};
        private String[] leftColumn = new String[]{""};
        public String[] rightColumn = new String[]{""};
        public ItemStack leftIcon = ItemStack.field_190927_a;
        public ItemStack middleIcon = ItemStack.field_190927_a;
        public ItemStack rightIcon = ItemStack.field_190927_a;
        public boolean centerLine = true;

        public static PanelUntranslatedLine readFromNBT(NBTTagCompound compound) {
            PanelUntranslatedLine line = new PanelUntranslatedLine();
            line.fullLine = PanelUntranslatedLine.readText(compound, "fullLine");
            line.leftColumn = PanelUntranslatedLine.readText(compound, "leftColumn");
            line.rightColumn = PanelUntranslatedLine.readText(compound, "rightColumn");
            line.leftIcon = new ItemStack(compound.func_74775_l("leftIcon"));
            line.middleIcon = new ItemStack(compound.func_74775_l("middleIcon"));
            line.rightIcon = new ItemStack(compound.func_74775_l("rightIcon"));
            line.centerLine = compound.func_74767_n("centerLine");
            return line;
        }

        private static String[] readText(NBTTagCompound compound, String key) {
            ArrayList<String> lineFragment = new ArrayList<String>();
            int i = 0;
            while (compound.func_74764_b(key + "_" + i)) {
                lineFragment.add(compound.func_74779_i(key + "_" + i));
                ++i;
            }
            return lineFragment.toArray(new String[0]);
        }

        private static void writeText(NBTTagCompound compound, String[] text, String key) {
            for (int i = 0; i < text.length; ++i) {
                compound.func_74778_a(key + "_" + i, text[i]);
            }
        }

        public void setFullLine(String[] fullLine) {
            this.fullLine = fullLine;
            for (int i = 0; i < this.fullLine.length; ++i) {
                if (this.fullLine[i] != null) continue;
                this.fullLine[i] = "";
            }
        }

        public void setLeftColumn(String[] leftColumn) {
            this.leftColumn = leftColumn;
            for (int i = 0; i < this.leftColumn.length; ++i) {
                if (this.leftColumn[i] != null) continue;
                this.leftColumn[i] = "";
            }
        }

        public void setRightColumn(String[] rightColumn) {
            this.rightColumn = rightColumn;
            for (int i = 0; i < this.rightColumn.length; ++i) {
                if (this.rightColumn[i] != null) continue;
                this.rightColumn[i] = "";
            }
        }

        public NBTTagCompound writeToNBT(NBTTagCompound compound) {
            PanelUntranslatedLine.writeText(compound, this.fullLine, "fullLine");
            PanelUntranslatedLine.writeText(compound, this.leftColumn, "leftColumn");
            PanelUntranslatedLine.writeText(compound, this.rightColumn, "rightColumn");
            compound.func_74782_a("leftIcon", (NBTBase)this.leftIcon.func_77955_b(new NBTTagCompound()));
            compound.func_74782_a("middleIcon", (NBTBase)this.middleIcon.func_77955_b(new NBTTagCompound()));
            compound.func_74782_a("rightIcon", (NBTBase)this.rightIcon.func_77955_b(new NBTTagCompound()));
            compound.func_74757_a("centerLine", this.centerLine);
            return compound;
        }
    }

    public static class PanelPacketInfo {
        public Point pos;
        public Point buildingPos;
        public String[][] lines;
        public long villager_id;
        public int panelType;

        public PanelPacketInfo(Point pos, String[][] lines, Point buildingPos, int panelType, long village_id) {
            this.pos = pos;
            this.lines = lines;
            this.buildingPos = buildingPos;
            this.panelType = panelType;
            this.villager_id = village_id;
        }
    }

    public static class PanelDisplayLine {
        public String fullLine = "";
        public String leftColumn = "";
        public String rightColumn = "";
        public ItemStack leftIcon = ItemStack.field_190927_a;
        public ItemStack middleIcon = ItemStack.field_190927_a;
        public ItemStack rightIcon = ItemStack.field_190927_a;
        public boolean centerLine = true;
    }
}

