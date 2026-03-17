/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiButton
 *  net.minecraft.client.renderer.BufferBuilder
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.GlStateManager$DestFactor
 *  net.minecraft.client.renderer.GlStateManager$SourceFactor
 *  net.minecraft.client.renderer.Tessellator
 *  net.minecraft.client.renderer.vertex.DefaultVertexFormats
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.ChunkPos
 *  net.minecraft.world.World
 *  net.minecraftforge.common.ForgeChunkManager
 *  org.lwjgl.opengl.GL11
 */
package org.millenaire.client.gui.text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import org.lwjgl.opengl.GL11;
import org.millenaire.client.book.BookManager;
import org.millenaire.client.book.TextBook;
import org.millenaire.client.gui.DisplayActions;
import org.millenaire.client.gui.text.GuiText;
import org.millenaire.client.network.ClientSender;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.ui.MillMapInfo;
import org.millenaire.common.utilities.LanguageUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.ThreadSafeUtilities;
import org.millenaire.common.village.Building;
import org.millenaire.common.village.BuildingLocation;
import org.millenaire.common.village.ConstructionIP;
import org.millenaire.common.world.MillWorldData;

public class GuiPanelParchment
extends GuiText {
    public static final int VILLAGE_MAP = 1;
    public static final int CHUNK_MAP = 2;
    public static final int chunkMapSizeInBlocks = 1280;
    private boolean isParchment = false;
    private int mapType = 0;
    private Building townHall = null;
    private final EntityPlayer player;
    ResourceLocation backgroundParchment = new ResourceLocation("millenaire", "textures/gui/parchment.png");
    ResourceLocation backgroundPanel = new ResourceLocation("millenaire", "textures/gui/panel.png");
    private final float targetHeight = 180.0f;
    private float scaledStartX;
    private float scaledStartY;
    private float scaleFactor;
    Tessellator tessellator = Tessellator.func_178181_a();
    BufferBuilder bufferbuilder = this.tessellator.func_178180_c();

    public GuiPanelParchment(EntityPlayer player, Building townHall, TextBook textBook, int mapType, boolean isParchment) {
        this.mapType = mapType;
        this.townHall = townHall;
        this.isParchment = isParchment;
        this.player = player;
        this.textBook = textBook;
        this.bookManager = new BookManager(204, 220, 190, 195, new GuiText.FontRendererGUIWrapper(this));
    }

    public GuiPanelParchment(EntityPlayer player, TextBook textBook, Building townHall, int mapType, boolean isParchment) {
        this.mapType = mapType;
        this.townHall = townHall;
        this.isParchment = isParchment;
        this.player = player;
        this.textBook = textBook;
        this.bookManager = new BookManager(204, 220, 190, 195, new GuiText.FontRendererGUIWrapper(this));
    }

    @Override
    protected void func_146284_a(GuiButton guibutton) throws IOException {
        if (guibutton instanceof GuiText.MillGuiButton) {
            GuiText.MillGuiButton gb = (GuiText.MillGuiButton)guibutton;
            if (gb.field_146127_k == 2000) {
                DisplayActions.displayHelpGUI();
            } else if (gb.field_146127_k == 3000) {
                DisplayActions.displayChunkGUI(this.player, this.player.field_70170_p);
            } else if (gb.field_146127_k == 4000) {
                DisplayActions.displayConfigGUI();
            } else if (gb.field_146127_k == 5000) {
                DisplayActions.displayTravelBookGUI(this.player);
            }
        }
        super.func_146284_a(guibutton);
    }

    @Override
    protected void customDrawBackground(int i, int j, float f) {
    }

    @Override
    public void customDrawScreen(int i, int j, float f) {
        try {
            if (this.mapType == 1 && this.pageNum == 0 && this.townHall != null && this.townHall.mapInfo != null) {
                this.drawVillageMap(i, j);
            } else if (this.mapType == 2 && this.pageNum == 0) {
                this.drawChunkMap(i, j);
            }
        }
        catch (Exception e) {
            MillLog.printException("Exception while rendering map: ", e);
        }
    }

    private void drawChunkMap(int i, int j) {
        if (Mill.serverWorlds.isEmpty()) {
            return;
        }
        int windowXstart = (this.field_146294_l - this.getXSize()) / 2;
        int windowYstart = (this.field_146295_m - this.getYSize()) / 2;
        World world = Mill.serverWorlds.get((int)0).world;
        MillWorldData mw = Mill.serverWorlds.get(0);
        GL11.glDisable((int)2896);
        GL11.glDisable((int)2929);
        int startX = (this.getXSize() - 160) / 2;
        int startY = (this.getYSize() - 160) / 2;
        int posXstart = this.player.field_70176_ah * 16 - 640;
        int posZstart = this.player.field_70164_aj * 16 - 640;
        int mouseX = (i - startX - windowXstart) / 2 * 16 + posXstart;
        int mouseZ = (j - startY - windowYstart) / 2 * 16 + posZstart;
        this.func_73733_a(startX - 2, startY - 2, startX + 160 + 2, startY + 160 + 2, 0x20000000, 0x20000000);
        ArrayList<String> labels = new ArrayList<String>();
        for (int x = posXstart; x < posXstart + 1280; x += 16) {
            for (int z = posZstart; z < posZstart + 1280; z += 16) {
                int colour = 0;
                if (!ThreadSafeUtilities.isChunkAtGenerated(world, x, z)) {
                    colour = 0x40111111;
                    continue;
                }
                colour = ThreadSafeUtilities.isChunkAtLoaded(world, x, z) ? -1073676544 : -1057030144;
                this.drawPixel(startX + (x - posXstart) / 8, startY + (z - posZstart) / 8, colour);
                if (mouseX != x || mouseZ != z) continue;
                labels.add(LanguageUtilities.string("chunk.chunkcoords", "" + x / 16 + "/" + z / 16));
            }
        }
        ArrayList<Building> buildings = new ArrayList<Building>(mw.allBuildings());
        for (Building b : buildings) {
            if (!b.isTownhall || b.winfo == null || b.villageType == null) continue;
            for (int x = b.winfo.mapStartX; x < b.winfo.mapStartX + b.winfo.length; x += 16) {
                for (int z = b.winfo.mapStartZ; z < b.winfo.mapStartZ + b.winfo.width; z += 16) {
                    if (x < posXstart || x > posXstart + 1280 || z < posZstart || z > posZstart + 1280) continue;
                    int colour = b.villageType.lonebuilding ? -258408295 : -268435201;
                    this.drawPixel(startX + (x - posXstart) / 8 + 1, startY + (z - posZstart) / 8 + 1, colour);
                    if (mouseX != x || mouseZ != z) continue;
                    labels.add(LanguageUtilities.string("chunk.village", b.getVillageQualifiedName()));
                }
            }
        }
        boolean labelForced = false;
        for (ChunkPos cc : ForgeChunkManager.getPersistentChunksFor((World)world).keys()) {
            if (cc.field_77276_a * 16 < posXstart || cc.field_77276_a * 16 > posXstart + 1280 || cc.field_77275_b * 16 < posZstart || cc.field_77275_b * 16 > posZstart + 1280) continue;
            this.drawPixel(startX + (cc.field_77276_a * 16 - posXstart) / 8, startY + (cc.field_77275_b * 16 - posZstart) / 8 + 1, -251658241);
            if (mouseX != cc.field_77276_a * 16 || mouseZ != cc.field_77275_b * 16 || labelForced) continue;
            labels.add(LanguageUtilities.string("chunk.chunkforced"));
            labelForced = true;
        }
        if (!labels.isEmpty()) {
            int stringlength = 0;
            for (String s : labels) {
                int w = this.field_146289_q.func_78256_a(s);
                if (w <= stringlength) continue;
                stringlength = w;
            }
            this.func_73733_a(i - 3 - windowXstart + 10, j - 3 - windowYstart, i + stringlength + 3 - windowXstart + 10, j + 11 * labels.size() - windowYstart, -1073741824, -1073741824);
            for (int si = 0; si < labels.size(); ++si) {
                this.field_146289_q.func_78276_b((String)labels.get(si), i - windowXstart + 10, j - windowYstart + 11 * si, 0x909090);
            }
        }
        GL11.glEnable((int)2896);
        GL11.glEnable((int)2929);
    }

    private void drawPixel(int x, int y, int colour) {
        this.func_73733_a(x, y, x + 1, y + 1, colour, colour);
    }

    private void drawScaledRect(int left, int top, int right, int bottom, int color) {
        int alpha = color >> 24 & 0xFF;
        int red = color >> 16 & 0xFF;
        int green = color >> 8 & 0xFF;
        int blue = color & 0xFF;
        this.bufferbuilder.func_181668_a(7, DefaultVertexFormats.field_181706_f);
        this.bufferbuilder.func_181662_b((double)(this.scaledStartX + (float)right * this.scaleFactor), (double)(this.scaledStartY + (float)top * this.scaleFactor), (double)this.field_73735_i).func_181669_b(red, green, blue, alpha).func_181675_d();
        this.bufferbuilder.func_181662_b((double)(this.scaledStartX + (float)left * this.scaleFactor), (double)(this.scaledStartY + (float)top * this.scaleFactor), (double)this.field_73735_i).func_181669_b(red, green, blue, alpha).func_181675_d();
        this.bufferbuilder.func_181662_b((double)(this.scaledStartX + (float)left * this.scaleFactor), (double)(this.scaledStartY + (float)bottom * this.scaleFactor), (double)this.field_73735_i).func_181669_b(red, green, blue, alpha).func_181675_d();
        this.bufferbuilder.func_181662_b((double)(this.scaledStartX + (float)right * this.scaleFactor), (double)(this.scaledStartY + (float)bottom * this.scaleFactor), (double)this.field_73735_i).func_181669_b(red, green, blue, alpha).func_181675_d();
        this.tessellator.func_78381_a();
    }

    private void drawVillageMap(int i, int j) {
        int xStart = (this.field_146294_l - this.getXSize()) / 2;
        int yStart = (this.field_146295_m - this.getYSize()) / 2;
        MillMapInfo minfo = this.townHall.mapInfo;
        GL11.glDisable((int)2896);
        GL11.glDisable((int)2929);
        this.scaleFactor = 180.0f / (float)minfo.width;
        this.scaledStartX = ((float)this.getXSize() - (float)minfo.length * this.scaleFactor) / 2.0f;
        this.scaledStartY = ((float)this.getYSize() - (float)minfo.width * this.scaleFactor) / 2.0f;
        GlStateManager.func_179090_x();
        GlStateManager.func_179147_l();
        GlStateManager.func_179118_c();
        GlStateManager.func_187428_a((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, (GlStateManager.SourceFactor)GlStateManager.SourceFactor.ONE, (GlStateManager.DestFactor)GlStateManager.DestFactor.ZERO);
        GlStateManager.func_179103_j((int)7425);
        this.drawScaledRect(-2, -2, minfo.length + 2, minfo.width + 2, 0x20000000);
        BuildingLocation locHover = null;
        MillVillager villagerHover = null;
        EntityPlayer playerHover = null;
        List<BuildingLocation> locations = this.townHall.getLocations();
        List<ConstructionIP> constructionsIP = this.townHall.getConstructionsInProgress();
        for (ConstructionIP cip : constructionsIP) {
            if (cip.getBuildingLocation() == null) continue;
            BuildingLocation bl = cip.getBuildingLocation();
            int left = Math.max(0, bl.minx - minfo.mapStartX);
            int top = Math.max(0, bl.minz - minfo.mapStartZ);
            int right = Math.min(minfo.length - 1, bl.maxx + 1 - minfo.mapStartX);
            int bottom = Math.min(minfo.width - 1, bl.maxz + 1 - minfo.mapStartZ);
            if (left >= right || top >= bottom) continue;
            float screenLeft = (float)xStart + this.scaledStartX + (float)left * this.scaleFactor;
            float screenRight = (float)xStart + this.scaledStartX + (float)right * this.scaleFactor;
            float screenTop = (float)yStart + this.scaledStartY + (float)top * this.scaleFactor;
            float screenBottom = (float)yStart + this.scaledStartY + (float)bottom * this.scaleFactor;
            if ((float)i >= screenLeft && (float)i <= screenRight && (float)j >= screenTop && (float)j <= screenBottom) {
                locHover = bl;
            }
            this.drawScaledRect(left, top, right, bottom, 0x40FF00FF);
            for (int x = left; x < right; ++x) {
                Arrays.fill(minfo.data[x], top, bottom, (byte)11);
            }
        }
        for (BuildingLocation bl : locations) {
            if (bl.isSubBuildingLocation) continue;
            int left = Math.max(0, bl.minx - minfo.mapStartX);
            int top = Math.max(0, bl.minz - minfo.mapStartZ);
            int right = Math.min(minfo.length - 1, bl.maxx + 1 - minfo.mapStartX);
            int bottom = Math.min(minfo.width - 1, bl.maxz + 1 - minfo.mapStartZ);
            if (left >= right || top >= bottom) continue;
            float screenLeft = (float)xStart + this.scaledStartX + (float)left * this.scaleFactor;
            float screenRight = (float)xStart + this.scaledStartX + (float)right * this.scaleFactor;
            float screenTop = (float)yStart + this.scaledStartY + (float)top * this.scaleFactor;
            float screenBottom = (float)yStart + this.scaledStartY + (float)bottom * this.scaleFactor;
            if ((float)i >= screenLeft && (float)i <= screenRight && (float)j >= screenTop && (float)j <= screenBottom) {
                locHover = bl;
            }
            if (bl.level < 0) {
                this.drawScaledRect(left, top, right, bottom, 0x40000060);
            } else {
                this.drawScaledRect(left, top, right, bottom, 0x400000FF);
            }
            for (int x = left; x < right; ++x) {
                Arrays.fill(minfo.data[x], top, bottom, (byte)11);
            }
        }
        for (int x = 0; x < minfo.length; ++x) {
            int lastColour = 0;
            int lastZ = 0;
            for (int z = 0; z < minfo.width; ++z) {
                int colour = 0;
                byte groundType = minfo.data[x][z];
                colour = groundType == 11 ? 0 : (groundType == 1 ? -1439682305 : (groundType == 2 ? 0x40FF0000 : (groundType == 3 ? 0x40FFFF00 : (groundType == 4 ? 1090486336 : (groundType == 5 ? 0x1000FF00 : (groundType == 10 ? 0x40808080 : (groundType == 6 ? 1090474064 : (groundType == 7 ? Integer.MIN_VALUE : (groundType == 8 ? 1083834265 : 0x4000FF00)))))))));
                if (z == 0) {
                    lastColour = colour;
                    continue;
                }
                if (colour == lastColour) continue;
                if (lastColour != 0) {
                    this.drawScaledRect(x, lastZ, x + 1, z, lastColour);
                }
                lastColour = colour;
                lastZ = z;
            }
            if (lastColour == 0) continue;
            this.drawScaledRect(x, lastZ, x + 1, minfo.width, lastColour);
        }
        for (MillVillager villager : this.townHall.getKnownVillagers()) {
            int mapPosX = (int)(villager.field_70165_t - (double)minfo.mapStartX);
            int mapPosZ = (int)(villager.field_70161_v - (double)minfo.mapStartZ);
            if (mapPosX <= 0 || mapPosZ <= 0 || mapPosX >= minfo.length || mapPosZ >= minfo.width) continue;
            if (villager.func_70631_g_()) {
                this.drawScaledRect(mapPosX - 1, mapPosZ - 1, mapPosX + 1, mapPosZ + 1, -1593835776);
            } else if (villager.getRecord() != null && villager.getRecord().raidingVillage) {
                this.drawScaledRect(mapPosX - 1, mapPosZ - 1, mapPosX + 1, mapPosZ + 1, -1610612736);
            } else if (villager.gender == 1) {
                this.drawScaledRect(mapPosX - 1, mapPosZ - 1, mapPosX + 1, mapPosZ + 1, -1610547201);
            } else {
                this.drawScaledRect(mapPosX - 1, mapPosZ - 1, mapPosX + 1, mapPosZ + 1, -1593901056);
            }
            int screenPosX = (int)((float)xStart + this.scaledStartX + (float)mapPosX * this.scaleFactor);
            int screenPosY = (int)((float)yStart + this.scaledStartY + (float)mapPosZ * this.scaleFactor);
            if (screenPosX <= i - 2 || screenPosX >= i + 2 || screenPosY <= j - 2 || screenPosY >= j + 2) continue;
            villagerHover = villager;
        }
        int mapPosX = (int)(this.player.field_70165_t - (double)minfo.mapStartX);
        int mapPosZ = (int)(this.player.field_70161_v - (double)minfo.mapStartZ);
        if (mapPosX > 0 && mapPosZ > 0 && mapPosX < minfo.length && mapPosZ < minfo.width) {
            this.drawScaledRect(mapPosX - 1, mapPosZ - 1, mapPosX + 2, mapPosZ + 2, -1593835521);
            int screenPosX = (int)((float)xStart + this.scaledStartX + (float)mapPosX * this.scaleFactor);
            int screenPosY = (int)((float)yStart + this.scaledStartY + (float)mapPosZ * this.scaleFactor);
            if (screenPosX > i - 2 && screenPosX < i + 3 && screenPosY > j - 2 && screenPosY < j + 3) {
                playerHover = this.player;
            }
        }
        if (villagerHover != null) {
            boolean gameString;
            int stringlength = this.field_146289_q.func_78256_a(villagerHover.func_70005_c_());
            stringlength = Math.max(stringlength, this.field_146289_q.func_78256_a(villagerHover.getNativeOccupationName()));
            boolean bl = gameString = villagerHover.getGameOccupationName(this.player.func_70005_c_()) != null && villagerHover.getGameOccupationName(this.player.func_70005_c_()).length() > 0;
            if (gameString) {
                stringlength = Math.max(stringlength, this.field_146289_q.func_78256_a(villagerHover.getGameOccupationName(this.player.func_70005_c_())));
                this.func_73733_a(i + 10 - 3 - xStart, j + 10 - 3 - yStart, i + 10 + stringlength + 3 - xStart, j + 10 + 33 - yStart, -1073741824, -1073741824);
                this.field_146289_q.func_78276_b(villagerHover.func_70005_c_(), i + 10 - xStart, j + 10 - yStart, 0x909090);
                this.field_146289_q.func_78276_b(villagerHover.getNativeOccupationName(), i + 10 - xStart, j + 10 - yStart + 11, 0x909090);
                this.field_146289_q.func_78276_b(villagerHover.getGameOccupationName(this.player.func_70005_c_()), i + 10 - xStart, j + 10 - yStart + 22, 0x909090);
            } else {
                this.func_73733_a(i + 10 - 3 - xStart, j + 10 - 3 - yStart, i + 10 + stringlength + 3 - xStart, j + 10 + 22 - yStart, -1073741824, -1073741824);
                this.field_146289_q.func_78276_b(villagerHover.func_70005_c_(), i + 10 - xStart, j + 10 - yStart, 0x909090);
                this.field_146289_q.func_78276_b(villagerHover.getNativeOccupationName(), i + 10 - xStart, j + 10 - yStart + 11, 0x909090);
            }
        } else if (playerHover != null) {
            int stringlength = this.field_146289_q.func_78256_a(playerHover.func_70005_c_());
            this.func_73733_a(i + 10 - 3 - xStart, j + 10 - 3 - yStart, i + 10 + stringlength + 3 - xStart, j + 10 + 11 - yStart, -1073741824, -1073741824);
            this.field_146289_q.func_78276_b(playerHover.func_70005_c_(), i + 10 - xStart, j + 10 - yStart, 0x909090);
        } else if (locHover != null) {
            boolean gameString;
            String nativeString;
            int stringlength;
            boolean unreachable;
            Building b = locHover.getBuilding(this.townHall.world);
            boolean bl = unreachable = b != null && this.townHall.regionMapper != null && !b.isReachableFromRegion(this.townHall.regionMapper.thRegion);
            if (unreachable) {
                stringlength = this.field_146289_q.func_78256_a(locHover.getNativeName() + " - " + LanguageUtilities.string("panels.unreachablebuilding"));
                nativeString = locHover.getNativeName() + " - " + LanguageUtilities.string("panels.unreachablebuilding");
            } else {
                stringlength = this.field_146289_q.func_78256_a(locHover.getNativeName());
                nativeString = locHover.getNativeName();
            }
            int nblines = 1;
            boolean bl2 = gameString = locHover.getGameName() != null && locHover.getGameName().length() > 0;
            if (gameString) {
                stringlength = Math.max(stringlength, this.field_146289_q.func_78256_a(locHover.getGameName()));
                ++nblines;
            }
            List<String> effects = locHover.getBuildingEffects(this.townHall.world);
            nblines += effects.size();
            for (String s : effects) {
                stringlength = Math.max(stringlength, this.field_146289_q.func_78256_a(s));
            }
            this.func_73733_a(i - 3 - xStart, j - 3 - yStart, i + stringlength + 3 - xStart, j + 11 * nblines - yStart, -1073741824, -1073741824);
            this.field_146289_q.func_78276_b(nativeString, i - xStart, j - yStart, 0x909090);
            int pos = 1;
            if (gameString) {
                this.field_146289_q.func_78276_b(locHover.getGameName(), i - xStart, j - yStart + 11, 0x909090);
                ++pos;
            }
            for (String s : effects) {
                this.field_146289_q.func_78276_b(s, i - xStart, j - yStart + 11 * pos, 0x909090);
                ++pos;
            }
        }
        GlStateManager.func_179103_j((int)7424);
        GlStateManager.func_179084_k();
        GlStateManager.func_179141_d();
        GlStateManager.func_179098_w();
        GL11.glEnable((int)2896);
        GL11.glEnable((int)2929);
    }

    @Override
    public ResourceLocation getPNGPath() {
        if (this.isParchment) {
            return this.backgroundParchment;
        }
        return this.backgroundPanel;
    }

    @Override
    public void initData() {
        this.textBook = this.bookManager.adjustTextBookLineLength(this.textBook);
        if (this.mapType == 1 && this.townHall != null) {
            ClientSender.requestMapInfo(this.townHall);
        }
    }

    public void func_73876_c() {
    }
}

