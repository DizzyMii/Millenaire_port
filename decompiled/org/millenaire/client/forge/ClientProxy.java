/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockLeaves
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.color.IBlockColor
 *  net.minecraft.client.renderer.color.IItemColor
 *  net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
 *  net.minecraft.client.settings.KeyBinding
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.text.ITextComponent
 *  net.minecraft.util.text.TextComponentString
 *  net.minecraft.util.text.TextFormatting
 *  net.minecraft.world.ColorizerFoliage
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.biome.BiomeColorHelper
 *  net.minecraftforge.client.event.ModelRegistryEvent
 *  net.minecraftforge.fml.client.FMLClientHandler
 *  net.minecraftforge.fml.client.registry.ClientRegistry
 *  net.minecraftforge.fml.client.registry.IRenderFactory
 *  net.minecraftforge.fml.client.registry.RenderingRegistry
 *  net.minecraftforge.fml.common.FMLCommonHandler
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  net.minecraftforge.fml.common.network.IGuiHandler
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 *  org.lwjgl.input.Keyboard
 */
package org.millenaire.client.forge;

import java.io.File;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import org.millenaire.client.MillClientUtilities;
import org.millenaire.client.forge.ClientGuiHandler;
import org.millenaire.client.forge.ClientTickHandler;
import org.millenaire.client.network.ClientReceiver;
import org.millenaire.client.network.ClientSender;
import org.millenaire.client.render.RenderMillVillager;
import org.millenaire.client.render.RenderWallDecoration;
import org.millenaire.client.render.TESRFirePit;
import org.millenaire.client.render.TESRMockBanner;
import org.millenaire.client.render.TESRPanel;
import org.millenaire.client.render.TileEntityLockedChestRenderer;
import org.millenaire.client.render.TileEntityMillBedRenderer;
import org.millenaire.common.block.MillBlocks;
import org.millenaire.common.entity.EntityWallDecoration;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.entity.TileEntityFirePit;
import org.millenaire.common.entity.TileEntityLockedChest;
import org.millenaire.common.entity.TileEntityMillBed;
import org.millenaire.common.entity.TileEntityMockBanner;
import org.millenaire.common.entity.TileEntityPanel;
import org.millenaire.common.forge.CommonProxy;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.item.MillItems;
import org.millenaire.common.utilities.LanguageUtilities;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.world.UserProfile;

@Mod.EventBusSubscriber(value={Side.CLIENT})
@SideOnly(value=Side.CLIENT)
public class ClientProxy
extends CommonProxy {
    public static KeyBinding KB_MENU;
    public static KeyBinding KB_VILLAGES;
    public static KeyBinding KB_ESCORTS;

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        MillItems.registerItemModels();
        MillBlocks.registerItemBlockModels();
        RenderingRegistry.registerEntityRenderingHandler(MillVillager.EntityGenericMale.class, (IRenderFactory)RenderMillVillager.FACTORY_MALE);
        RenderingRegistry.registerEntityRenderingHandler(MillVillager.EntityGenericAsymmFemale.class, (IRenderFactory)RenderMillVillager.FACTORY_FEMALE_ASYM);
        RenderingRegistry.registerEntityRenderingHandler(MillVillager.EntityGenericSymmFemale.class, (IRenderFactory)RenderMillVillager.FACTORY_FEMALE_SYM);
        RenderingRegistry.registerEntityRenderingHandler(EntityWallDecoration.class, (IRenderFactory)RenderWallDecoration.FACTORY_WALL_DECORATION);
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMillBed.class, (TileEntitySpecialRenderer)new TileEntityMillBedRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFirePit.class, (TileEntitySpecialRenderer)new TESRFirePit());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMockBanner.class, (TileEntitySpecialRenderer)new TESRMockBanner());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityLockedChest.class, (TileEntitySpecialRenderer)new TileEntityLockedChestRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPanel.class, (TileEntitySpecialRenderer)new TESRPanel());
    }

    @Override
    public IGuiHandler createGuiHandler() {
        return new ClientGuiHandler();
    }

    @Override
    public String getBlockName(Block block, int meta) {
        if (block == null) {
            MillLog.printException(new MillLog.MillenaireException("Trying to get the name of a null block."));
            return null;
        }
        if (meta == -1) {
            meta = 0;
        }
        return new ItemStack(block, 1, meta).func_82833_r();
    }

    @Override
    public UserProfile getClientProfile() {
        if (Mill.proxy.getTheSinglePlayer() == null) {
            return null;
        }
        if (Mill.clientWorld.profiles.containsKey(Mill.proxy.getTheSinglePlayer().func_110124_au())) {
            return Mill.clientWorld.profiles.get(Mill.proxy.getTheSinglePlayer().func_110124_au());
        }
        UserProfile profile = new UserProfile(Mill.clientWorld, Mill.proxy.getTheSinglePlayer());
        Mill.clientWorld.profiles.put(profile.uuid, profile);
        return profile;
    }

    @Override
    public File getConfigFile() {
        return new File(MillCommonUtilities.getMillenaireContentDir(), "config.txt");
    }

    @Override
    public File getCustomConfigFile() {
        return new File(MillCommonUtilities.getMillenaireCustomContentDir(), "config-custom.txt");
    }

    @Override
    public String getItemName(Item item, int meta) {
        if (item == null) {
            MillLog.printException(new MillLog.MillenaireException("Trying to get the name of a null item."));
            return null;
        }
        if (meta == -1) {
            meta = 0;
        }
        return new ItemStack(item, 1, meta).func_82833_r();
    }

    @Override
    public String getKeyString(int value) {
        return Keyboard.getKeyName((int)value);
    }

    @Override
    public File getLogFile() {
        return new File(MillCommonUtilities.getMillenaireCustomContentDir(), "millenaire.log");
    }

    @Override
    public String getQuestKeyName() {
        return Keyboard.getKeyName((int)KB_MENU.func_151463_i());
    }

    @Override
    public String getSinglePlayerName() {
        if (this.getTheSinglePlayer() != null) {
            return this.getTheSinglePlayer().func_70005_c_();
        }
        return "NULL_PLAYER";
    }

    @Override
    public EntityPlayer getTheSinglePlayer() {
        return FMLClientHandler.instance().getClient().field_71439_g;
    }

    @Override
    public void handleClientGameUpdate() {
        MillClientUtilities.handleKeyPress(Mill.clientWorld.world);
        if (Mill.clientWorld.world.func_72820_D() % 20L == 0L) {
            Mill.clientWorld.clearPanelQueue();
        }
        this.loadLanguagesIfNeeded();
    }

    @Override
    public void handleClientLogin() {
        ClientSender.sendVersionInfo();
        ClientSender.sendAvailableContent();
    }

    @Override
    public void initNetwork() {
        Mill.millChannel.register((Object)new ClientReceiver());
    }

    @Override
    public boolean isTrueServer() {
        return false;
    }

    @Override
    public int loadKeySetting(String value) {
        return Keyboard.getKeyIndex((String)value.toUpperCase());
    }

    @Override
    public void loadLanguagesIfNeeded() {
        Minecraft minecraft = Minecraft.func_71410_x();
        LanguageUtilities.loadLanguages(minecraft.field_71474_y.field_74363_ab);
    }

    @Override
    public void localTranslatedSentence(EntityPlayer player, char colour, String code, String ... values) {
        for (int i = 0; i < values.length; ++i) {
            values[i] = LanguageUtilities.unknownString(values[i]);
        }
        this.sendLocalChat(player, colour, LanguageUtilities.string(code, values));
    }

    @Override
    public String logPrefix() {
        return "CLIENT ";
    }

    @Override
    public void refreshClientResources() {
        Minecraft.func_71410_x().func_110436_a();
    }

    @Override
    public void registerForgeClientClasses() {
        FMLCommonHandler.instance().bus().register((Object)new ClientTickHandler());
        Mill.millChannel.register((Object)new ClientReceiver());
        Minecraft.func_71410_x().func_184125_al().func_186722_a(new IBlockColor(){

            public int func_186720_a(IBlockState state, @Nullable IBlockAccess worldIn, @Nullable BlockPos pos, int tintIndex) {
                return worldIn != null && pos != null ? BiomeColorHelper.func_180287_b((IBlockAccess)worldIn, (BlockPos)pos) : ColorizerFoliage.func_77468_c();
            }
        }, new Block[]{MillBlocks.LEAVES_PISTACHIO});
        Minecraft.func_71410_x().getItemColors().func_186731_a(new IItemColor(){

            public int func_186726_a(ItemStack stack, int tintIndex) {
                return ColorizerFoliage.func_77468_c();
            }
        }, new Block[]{MillBlocks.LEAVES_PISTACHIO});
    }

    @Override
    public void registerKeyBindings() {
        KB_MENU = new KeyBinding("key.menu", 50, "key.category.millenaire");
        KB_VILLAGES = new KeyBinding("key.villages", 47, "key.category.millenaire");
        KB_ESCORTS = new KeyBinding("key.escorts", 34, "key.category.millenaire");
        ClientRegistry.registerKeyBinding((KeyBinding)KB_MENU);
        ClientRegistry.registerKeyBinding((KeyBinding)KB_VILLAGES);
        ClientRegistry.registerKeyBinding((KeyBinding)KB_ESCORTS);
    }

    @Override
    public void sendChatAdmin(String s) {
        s = s.trim();
        Minecraft.func_71410_x().field_71456_v.func_146158_b().func_146227_a((ITextComponent)new TextComponentString(s));
    }

    @Override
    public void sendChatAdmin(String s, TextFormatting colour) {
        s = s.trim();
        TextComponentString cc = new TextComponentString(s);
        cc.func_150256_b().func_150238_a(colour);
        Minecraft.func_71410_x().field_71456_v.func_146158_b().func_146227_a((ITextComponent)cc);
    }

    @Override
    public void sendLocalChat(EntityPlayer player, char colour, String s) {
        s = s.trim();
        Minecraft.func_71410_x().field_71456_v.func_146158_b().func_146227_a((ITextComponent)new TextComponentString("\u00a7" + colour + s));
    }

    @Override
    public void setGraphicsLevel(BlockLeaves blockLeaves, boolean value) {
        blockLeaves.func_150122_b(value);
    }
}

