/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockBed
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.entity.EntityPlayerSP
 *  net.minecraft.client.gui.FontRenderer
 *  net.minecraft.client.model.ModelBiped
 *  net.minecraft.client.renderer.BufferBuilder
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.GlStateManager$DestFactor
 *  net.minecraft.client.renderer.GlStateManager$SourceFactor
 *  net.minecraft.client.renderer.RenderItem
 *  net.minecraft.client.renderer.Tessellator
 *  net.minecraft.client.renderer.entity.Render
 *  net.minecraft.client.renderer.entity.RenderBiped
 *  net.minecraft.client.renderer.entity.RenderLivingBase
 *  net.minecraft.client.renderer.entity.RenderManager
 *  net.minecraft.client.renderer.entity.layers.LayerBipedArmor
 *  net.minecraft.client.renderer.entity.layers.LayerRenderer
 *  net.minecraft.client.renderer.vertex.DefaultVertexFormats
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLiving
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.item.ItemStack
 *  net.minecraft.pathfinding.PathPoint
 *  net.minecraft.util.ResourceLocation
 *  net.minecraftforge.fml.client.FMLClientHandler
 *  net.minecraftforge.fml.client.registry.IRenderFactory
 *  org.lwjgl.opengl.GL11
 */
package org.millenaire.client.render;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import org.lwjgl.opengl.GL11;
import org.millenaire.client.render.LayerVillagerClothes;
import org.millenaire.client.render.ModelFemaleAsymmetrical;
import org.millenaire.client.render.ModelFemaleSymmetrical;
import org.millenaire.client.render.ModelMillVillager;
import org.millenaire.common.block.BlockMillBed;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.item.MillItems;
import org.millenaire.common.pathing.atomicstryker.AS_PathEntity;
import org.millenaire.common.quest.QuestInstance;
import org.millenaire.common.utilities.LanguageUtilities;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.world.UserProfile;

public class RenderMillVillager
extends RenderBiped<MillVillager> {
    private static final int MAX_VIEW_DISTANCE = 64;
    private static final float LINE_HEIGHT = 0.25f;
    private static final int LINE_SIZE = 60;
    public static final FactoryMale FACTORY_MALE = new FactoryMale();
    public static final FactoryFemaleAsym FACTORY_FEMALE_ASYM = new FactoryFemaleAsym();
    public static final FactoryFemaleSym FACTORY_FEMALE_SYM = new FactoryFemaleSym();

    private static void drawItem2D(FontRenderer fontRendererIn, ItemStack itemStack, float x, float y, float z, float iconPos, float viewerYaw, float viewerPitch, boolean isThirdPersonFrontal, boolean isSneaking) {
        RenderItem renderItem = Minecraft.func_71410_x().func_175599_af();
        if (!itemStack.func_190926_b()) {
            GlStateManager.func_179094_E();
            GlStateManager.func_179109_b((float)x, (float)y, (float)z);
            GlStateManager.func_187432_a((float)0.0f, (float)1.0f, (float)0.0f);
            GlStateManager.func_179114_b((float)(-viewerYaw), (float)0.0f, (float)1.0f, (float)0.0f);
            GlStateManager.func_179114_b((float)((float)(isThirdPersonFrontal ? -1 : 1) * viewerPitch), (float)1.0f, (float)0.0f, (float)0.0f);
            GlStateManager.func_179109_b((float)(0.25f - iconPos * 0.5f), (float)0.0f, (float)-7.5f);
            GlStateManager.func_179152_a((float)-0.025f, (float)-0.025f, (float)0.05f);
            GlStateManager.func_179140_f();
            GlStateManager.func_179132_a((boolean)false);
            if (!isSneaking) {
                GlStateManager.func_179097_i();
            }
            renderItem.func_180450_b(itemStack, 0, 0);
            GlStateManager.func_179118_c();
            GlStateManager.func_179101_C();
            GlStateManager.func_179140_f();
            GlStateManager.func_179145_e();
            GlStateManager.func_179084_k();
            GlStateManager.func_179131_c((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            GlStateManager.func_179121_F();
        }
    }

    private static void drawNameplateColour(FontRenderer fontRendererIn, String str, float x, float y, float z, int verticalShift, float viewerYaw, float viewerPitch, boolean isThirdPersonFrontal, boolean isSneaking, int colour) {
        GlStateManager.func_179094_E();
        GlStateManager.func_179109_b((float)x, (float)y, (float)z);
        GlStateManager.func_187432_a((float)0.0f, (float)1.0f, (float)0.0f);
        GlStateManager.func_179114_b((float)(-viewerYaw), (float)0.0f, (float)1.0f, (float)0.0f);
        GlStateManager.func_179114_b((float)((float)(isThirdPersonFrontal ? -1 : 1) * viewerPitch), (float)1.0f, (float)0.0f, (float)0.0f);
        GlStateManager.func_179152_a((float)-0.025f, (float)-0.025f, (float)0.025f);
        GlStateManager.func_179140_f();
        GlStateManager.func_179132_a((boolean)false);
        if (!isSneaking) {
            GlStateManager.func_179097_i();
        }
        GlStateManager.func_179147_l();
        GlStateManager.func_187428_a((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, (GlStateManager.SourceFactor)GlStateManager.SourceFactor.ONE, (GlStateManager.DestFactor)GlStateManager.DestFactor.ZERO);
        if (str.length() > 0) {
            int xDelta = fontRendererIn.func_78256_a(str) / 2;
            GlStateManager.func_179090_x();
            Tessellator tessellator = Tessellator.func_178181_a();
            BufferBuilder bufferbuilder = tessellator.func_178180_c();
            bufferbuilder.func_181668_a(7, DefaultVertexFormats.field_181706_f);
            bufferbuilder.func_181662_b((double)(-xDelta - 1), (double)(-1 + verticalShift), 0.0).func_181666_a(0.0f, 0.0f, 0.0f, 0.7f).func_181675_d();
            bufferbuilder.func_181662_b((double)(-xDelta - 1), (double)(8 + verticalShift), 0.0).func_181666_a(0.0f, 0.0f, 0.0f, 0.7f).func_181675_d();
            bufferbuilder.func_181662_b((double)(xDelta + 1), (double)(8 + verticalShift), 0.0).func_181666_a(0.0f, 0.0f, 0.0f, 0.7f).func_181675_d();
            bufferbuilder.func_181662_b((double)(xDelta + 1), (double)(-1 + verticalShift), 0.0).func_181666_a(0.0f, 0.0f, 0.0f, 0.7f).func_181675_d();
            tessellator.func_78381_a();
            GlStateManager.func_179098_w();
        }
        if (!isSneaking) {
            fontRendererIn.func_78276_b(str, -fontRendererIn.func_78256_a(str) / 2, verticalShift, colour);
            GlStateManager.func_179126_j();
        }
        GlStateManager.func_179132_a((boolean)true);
        fontRendererIn.func_78276_b(str, -fontRendererIn.func_78256_a(str) / 2, verticalShift, colour);
        GlStateManager.func_179145_e();
        GlStateManager.func_179084_k();
        GlStateManager.func_179131_c((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GlStateManager.func_179121_F();
    }

    public RenderMillVillager(RenderManager renderManager, ModelMillVillager modelbiped) {
        super(renderManager, (ModelBiped)modelbiped, 0.5f);
        this.func_177094_a((LayerRenderer)new LayerBipedArmor((RenderLivingBase)this));
        for (int layer = 0; layer < 2; ++layer) {
            this.func_177094_a(new LayerVillagerClothes((RenderLivingBase<MillVillager>)this, modelbiped, layer));
        }
    }

    protected void applyRotations(MillVillager v, float par2, float rotationYaw, float partialTicks) {
        if (v.func_70089_S() && v.isVillagerSleeping()) {
            float orientation = -v.getBedOrientationInDegrees() + 90.0f;
            if (orientation == 0.0f) {
                GL11.glTranslatef((float)0.5f, (float)0.0f, (float)-0.5f);
            } else if ((double)orientation == 90.0) {
                GL11.glTranslatef((float)-0.5f, (float)0.0f, (float)-0.5f);
            } else if ((double)orientation == -180.0) {
                GL11.glTranslatef((float)-0.5f, (float)0.0f, (float)0.5f);
            } else if ((double)orientation == -90.0) {
                GL11.glTranslatef((float)0.5f, (float)0.0f, (float)0.5f);
            }
            GL11.glRotatef((float)orientation, (float)0.0f, (float)1.0f, (float)0.0f);
            GL11.glRotatef((float)this.func_77037_a((EntityLivingBase)v), (float)0.0f, (float)0.0f, (float)1.0f);
            GL11.glRotatef((float)270.0f, (float)0.0f, (float)1.0f, (float)0.0f);
            Block block = v.getPos().getBlock(v.field_70170_p);
            if (block instanceof BlockMillBed) {
                float adjustement = 0.0f + (float)((BlockMillBed)block).getBedHeight() / 16.0f;
                GL11.glTranslatef((float)0.0f, (float)0.0f, (float)(0.6f - adjustement));
            } else if (block instanceof BlockBed) {
                GL11.glTranslatef((float)0.0f, (float)0.0f, (float)0.1f);
            }
        } else {
            super.func_77043_a((EntityLivingBase)v, par2, rotationYaw, partialTicks);
        }
    }

    private List<ItemStack> defineSpecialIcons(MillVillager villager) {
        ArrayList<ItemStack> icons = new ArrayList<ItemStack>();
        if (villager.vtype != null) {
            if (villager.vtype.isChief) {
                icons.add(Items.field_151169_ag.func_190903_i());
            }
            if (villager.getCurrentGoal() != null && villager.getCurrentGoal().getFloatingIcon() != null) {
                icons.add(villager.getCurrentGoal().getFloatingIcon());
            }
            if (villager.isForeignMerchant()) {
                icons.add(MillItems.PURSE.func_190903_i());
            }
            if (villager.vtype.hireCost > 0) {
                icons.add(MillItems.DENIER.func_190903_i());
            }
            if (villager.isRaider) {
                icons.add(MillItems.NORMAN_AXE.func_190903_i());
            }
        }
        return icons;
    }

    private void displayText(MillVillager v, String text, int colour, double x, double y, double z) {
        this.renderLivingLabelColour(v, text, x, y, z, 64, colour);
    }

    public void doRender(MillVillager entity, double x, double y, double z, float entityYaw, float partialTicks) {
        MillVillager villager = entity;
        super.func_76986_a((EntityLiving)entity, x, y, z, entityYaw, partialTicks);
        this.doRenderVillagerName(villager, x, y, z);
    }

    public void doRenderVillagerName(MillVillager villager, double x, double y, double z) {
        if (villager.shouldLieDown) {
            double height = villager.func_184177_bl().field_72337_e - villager.func_184177_bl().field_72338_b;
            float angle = villager.getBedOrientationInDegrees();
            double dx = 0.0;
            double dz = 0.0;
            if (angle == 0.0f) {
                dx = -height * 0.9;
            } else if (angle == 90.0f) {
                dz = -height * 0.9;
            } else if (angle == 180.0f) {
                dx = height * 0.9;
            } else if (angle == 270.0f) {
                dz = height * 0.9;
            }
            x = villager.field_70142_S + dx;
            z = villager.field_70136_U + dz;
        }
        Minecraft minecraft = FMLClientHandler.instance().getClient();
        EntityPlayerSP player = minecraft.field_71439_g;
        UserProfile profile = Mill.clientWorld.getProfile((EntityPlayer)player);
        float f4 = villager.func_70032_d((Entity)player);
        if (f4 < (float)MillConfigValues.VillagersNamesDistance) {
            List<ItemStack> specialIcons;
            String s;
            String gameSpeech = villager.getGameSpeech(Mill.proxy.getTheSinglePlayer().func_70005_c_());
            String nativeSpeech = villager.getNativeSpeech(Mill.proxy.getTheSinglePlayer().func_70005_c_());
            float height = 0.0f;
            if (MillConfigValues.DEV && Mill.serverWorlds.size() > 0 && Mill.serverWorlds.get(0).getVillagerById(villager.getVillagerId()) != null && !MillConfigValues.DEV) {
                MillVillager dv = Mill.serverWorlds.get(0).getVillagerById(villager.getVillagerId());
                AS_PathEntity pe = dv.pathEntity;
                if (pe != null && pe.pointsCopy != null) {
                    PathPoint[] pp = pe.pointsCopy;
                    if (pp != null && pp.length > 0) {
                        String s2 = "";
                        for (int i = pe.func_75873_e(); i < pp.length && i < pe.func_75873_e() + 5; ++i) {
                            s2 = s2 + "(" + pp[i] + ") ";
                        }
                        this.displayText(villager, s2, -1593835521, (float)x, (float)y + height, (float)z);
                        height += 0.25f;
                    }
                    if (pe != null) {
                        if (pe.func_75874_d() > 0) {
                            this.displayText(villager, "Path: " + pe.func_75874_d() + " end: " + pe.getCurrentTargetPathPoint() + " dist: " + Math.round(villager.getPos().horizontalDistanceTo(pe.func_75870_c()) * 10.0) / 10L + " index: " + pe.func_75873_e() + " " + dv.func_70781_l() + ", stuck: " + dv.longDistanceStuck, -1593835521, (float)x, (float)y + height, (float)z);
                        } else {
                            this.displayText(villager, "Empty path, stuck: " + dv.longDistanceStuck, -1593835521, (float)x, (float)y + height, (float)z);
                        }
                        height += 0.25f;
                    }
                } else {
                    this.displayText(villager, "Null path entity, stuck: " + dv.longDistanceStuck, -1593835521, (float)x, (float)y + height, (float)z);
                    height += 0.25f;
                }
                if (dv.func_70638_az() == null) {
                    this.displayText(villager, "Pos: " + dv.getPos() + " Path dest: " + dv.getPathDestPoint() + " Goal dest: " + dv.getGoalDestPoint() + " dist: " + Math.round(dv.getPos().horizontalDistanceTo(dv.getPathDestPoint()) * 10.0) / 10L + " sm: " + dv.stopMoving + " jps busy: " + dv.pathPlannerJPS.isBusy(), -1593835521, (float)x, (float)y + height, (float)z);
                } else {
                    this.displayText(villager, "Pos: " + dv.getPos() + " Entity: " + dv.func_70638_az() + " dest: " + new Point((Entity)dv.func_70638_az()) + " dist: " + Math.round(dv.getPos().horizontalDistanceTo(new Point((Entity)dv.func_70638_az())) * 10.0) / 10L + " sm: " + dv.stopMoving + " jps busy: " + dv.pathPlannerJPS.isBusy(), -1593835521, (float)x, (float)y + height, (float)z);
                }
                height += 0.25f;
            }
            if (villager.hiredBy == null) {
                String subLine;
                String line;
                ArrayList<String> lines;
                if (gameSpeech != null) {
                    lines = new ArrayList<String>();
                    line = gameSpeech;
                    while (line.length() > 60) {
                        int cutoff = line.lastIndexOf(32, 60);
                        if (cutoff == -1) {
                            cutoff = 60;
                        }
                        subLine = line.substring(0, cutoff);
                        line = line.substring(subLine.length()).trim();
                        lines.add(subLine);
                    }
                    lines.add(line);
                    for (int i = lines.size() - 1; i >= 0; --i) {
                        this.displayText(villager, (String)lines.get(i), -1596166533, (float)x, (float)y + height, (float)z);
                        height += 0.25f;
                    }
                }
                if (nativeSpeech != null) {
                    lines = new ArrayList();
                    line = nativeSpeech;
                    while (line.length() > 60) {
                        int cutoff = line.lastIndexOf(32, 60);
                        if (cutoff == -1) {
                            cutoff = 60;
                        }
                        subLine = line.substring(0, cutoff);
                        line = line.substring(subLine.length()).trim();
                        lines.add(subLine);
                    }
                    lines.add(line);
                    for (int i = lines.size() - 1; i >= 0; --i) {
                        this.displayText(villager, (String)lines.get(i), -1603244324, (float)x, (float)y + height, (float)z);
                        height += 0.25f;
                    }
                }
                if (MillConfigValues.displayNames && villager.getCurrentGoal() != null) {
                    this.displayText(villager, villager.getCurrentGoal().gameName(villager), -1596142994, (float)x, (float)y + height, (float)z);
                    height += 0.25f;
                }
                if (villager.func_70638_az() != null) {
                    this.displayText(villager, LanguageUtilities.string("other.villagerattackinglabel", villager.func_70638_az().func_70005_c_()), -1593901056, (float)x, (float)y + height, (float)z);
                    height += 0.25f;
                }
                if (profile.villagersInQuests.containsKey(villager.getVillagerId())) {
                    QuestInstance qi = profile.villagersInQuests.get(villager.getVillagerId());
                    if (qi.getCurrentVillager().id == villager.getVillagerId()) {
                        this.displayText(villager, "[" + qi.getLabel(profile) + "]", -1596072483, (float)x, (float)y + height, (float)z);
                        height += 0.25f;
                    }
                }
                if (villager.isRaider) {
                    this.displayText(villager, LanguageUtilities.string("ui.raider"), -1593872773, (float)x, (float)y + height, (float)z);
                    height += 0.25f;
                }
                if (villager.vtype.showHealth) {
                    this.displayText(villager, LanguageUtilities.string("hire.health") + ": " + (double)villager.func_110143_aJ() * 0.5 + "/" + (double)villager.func_110138_aP() * 0.5, -1596072483, (float)x, (float)y + height, (float)z);
                    height += 0.25f;
                }
            } else if (villager.hiredBy.equals(profile.playerName)) {
                s = LanguageUtilities.string("hire.health") + ": " + (double)villager.func_110143_aJ() * 0.5 + "/" + (double)villager.func_110138_aP() * 0.5;
                s = villager.aggressiveStance ? s + " - " + LanguageUtilities.string("hire.aggressive") : s + " - " + LanguageUtilities.string("hire.passive");
                this.displayText(villager, s, -1596142994, (float)x, (float)y + height, (float)z);
                s = LanguageUtilities.string("hire.timeleft", "" + Math.round((villager.hiredUntil - villager.field_70170_p.func_72820_D()) / 1000L));
                this.displayText(villager, s, -1596072483, (float)x, (float)y + (height += 0.25f), (float)z);
                height += 0.25f;
            } else {
                s = LanguageUtilities.string("hire.hiredby", villager.hiredBy);
                this.displayText(villager, s, -1596072483, (float)x, (float)y + height, (float)z);
                height += 0.25f;
            }
            if (villager.field_70128_L) {
                this.displayText(villager, "Dead on client!", -1593901056, (float)x, (float)y + height, (float)z);
            }
            if (villager.isDeadOnServer) {
                this.displayText(villager, "Dead on server!", -1593901056, (float)x, (float)y + height, (float)z);
            }
            if (MillConfigValues.displayNames && !villager.vtype.hideName) {
                this.displayText(villager, villager.func_70005_c_() + ", " + villager.getNativeOccupationName(), -1593835521, (float)x, (float)y + height, (float)z);
                height += 0.25f;
            }
            if (!(specialIcons = this.defineSpecialIcons(villager)).isEmpty()) {
                this.renderIcons(villager, specialIcons, (float)x, (float)y + (height += 0.2f), (float)z, 64);
                this.displayText(villager, "", -1593835521, (float)x, (float)y + height, (float)z);
            }
        }
    }

    protected ResourceLocation getEntityTexture(MillVillager villager) {
        return villager.texture;
    }

    protected void preRenderCallback(MillVillager villager, float f) {
        this.preRenderScale(villager, f);
    }

    protected void preRenderScale(MillVillager villager, float f) {
        float scale = 1.0f;
        if (villager.getRecord() != null) {
            scale = villager.getRecord().scale;
        }
        GL11.glScalef((float)scale, (float)scale, (float)scale);
    }

    private void renderIcons(MillVillager entityIn, List<ItemStack> icons, double x, double y, double z, int maxDistance) {
        double d0 = entityIn.func_70068_e(this.field_76990_c.field_78734_h);
        if (d0 <= (double)(maxDistance * maxDistance)) {
            boolean isSneaking = entityIn.func_70093_af();
            float viewerYaw = this.field_76990_c.field_78735_i;
            float viewerPitch = this.field_76990_c.field_78732_j;
            boolean isThirdPersonFrontal = this.field_76990_c.field_78733_k.field_74320_O == 2;
            float f2 = entityIn.field_70131_O + 0.5f - (isSneaking ? 0.25f : 0.0f);
            int pos = 0;
            for (ItemStack icon : icons) {
                RenderMillVillager.drawItem2D(this.func_76983_a(), icon, (float)x, (float)y + f2, (float)z, (float)pos - (float)(icons.size() - 1) / 2.0f, viewerYaw, viewerPitch, isThirdPersonFrontal, isSneaking);
                ++pos;
            }
        }
    }

    private void renderLivingLabelColour(MillVillager entityIn, String str, double x, double y, double z, int maxDistance, int colour) {
        double d0 = entityIn.func_70068_e(this.field_76990_c.field_78734_h);
        if (d0 <= (double)(maxDistance * maxDistance)) {
            boolean isSneaking = entityIn.func_70093_af();
            float viewerYaw = this.field_76990_c.field_78735_i;
            float viewerPitch = this.field_76990_c.field_78732_j;
            boolean isThirdPersonFrontal = this.field_76990_c.field_78733_k.field_74320_O == 2;
            float f2 = entityIn.field_70131_O + 0.5f - (isSneaking ? 0.25f : 0.0f);
            int i = "deadmau5".equals(str) ? -10 : 0;
            RenderMillVillager.drawNameplateColour(this.func_76983_a(), str, (float)x, (float)y + f2, (float)z, i, viewerYaw, viewerPitch, isThirdPersonFrontal, isSneaking, colour);
        }
    }

    public static class FactoryMale
    implements IRenderFactory<MillVillager.EntityGenericMale> {
        public Render<? super MillVillager.EntityGenericMale> createRenderFor(RenderManager manager) {
            return new RenderMillVillager(manager, new ModelMillVillager());
        }
    }

    public static class FactoryFemaleSym
    implements IRenderFactory<MillVillager.EntityGenericSymmFemale> {
        public Render<? super MillVillager.EntityGenericSymmFemale> createRenderFor(RenderManager manager) {
            return new RenderMillVillager(manager, new ModelFemaleSymmetrical());
        }
    }

    public static class FactoryFemaleAsym
    implements IRenderFactory<MillVillager.EntityGenericAsymmFemale> {
        public Render<? super MillVillager.EntityGenericAsymmFemale> createRenderFor(RenderManager manager) {
            return new RenderMillVillager(manager, new ModelFemaleAsymmetrical());
        }
    }
}

