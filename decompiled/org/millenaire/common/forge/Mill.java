/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.command.ICommand
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.BannerPattern
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.SoundEvent
 *  net.minecraft.world.World
 *  net.minecraft.world.biome.Biome
 *  net.minecraft.world.gen.structure.MapGenVillage
 *  net.minecraftforge.common.ForgeChunkManager
 *  net.minecraftforge.common.ForgeChunkManager$LoadingCallback
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.common.util.EnumHelper
 *  net.minecraftforge.fml.common.FMLCommonHandler
 *  net.minecraftforge.fml.common.IWorldGenerator
 *  net.minecraftforge.fml.common.Mod
 *  net.minecraftforge.fml.common.Mod$EventHandler
 *  net.minecraftforge.fml.common.Mod$Instance
 *  net.minecraftforge.fml.common.ProgressManager
 *  net.minecraftforge.fml.common.ProgressManager$ProgressBar
 *  net.minecraftforge.fml.common.SidedProxy
 *  net.minecraftforge.fml.common.event.FMLInitializationEvent
 *  net.minecraftforge.fml.common.event.FMLPostInitializationEvent
 *  net.minecraftforge.fml.common.event.FMLPreInitializationEvent
 *  net.minecraftforge.fml.common.event.FMLServerStartingEvent
 *  net.minecraftforge.fml.common.network.FMLEventChannel
 *  net.minecraftforge.fml.common.network.NetworkRegistry
 *  net.minecraftforge.fml.common.registry.EntityRegistry
 *  net.minecraftforge.fml.common.registry.GameRegistry
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.millenaire.common.forge;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.command.ICommand;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.MapGenVillage;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ProgressManager;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.FMLEventChannel;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.millenaire.common.advancements.MillAdvancements;
import org.millenaire.common.annotedparameters.ParametersManager;
import org.millenaire.common.block.MillBlocks;
import org.millenaire.common.buildingplan.BuildingDevUtilities;
import org.millenaire.common.buildingplan.BuildingPlan;
import org.millenaire.common.commands.CommandDebugResendProfiles;
import org.millenaire.common.commands.CommandDebugResetVillagers;
import org.millenaire.common.commands.CommandGiveReputation;
import org.millenaire.common.commands.CommandImportCulture;
import org.millenaire.common.commands.CommandListActiveVillages;
import org.millenaire.common.commands.CommandRenameVillage;
import org.millenaire.common.commands.CommandSpawnVillage;
import org.millenaire.common.commands.CommandSwitchVillageControl;
import org.millenaire.common.commands.CommandTeleportToVillage;
import org.millenaire.common.config.DocumentedElement;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.culture.Culture;
import org.millenaire.common.deployer.ContentDeployer;
import org.millenaire.common.entity.EntityTargetedBlaze;
import org.millenaire.common.entity.EntityTargetedGhast;
import org.millenaire.common.entity.EntityTargetedWitherSkeleton;
import org.millenaire.common.entity.EntityWallDecoration;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.entity.VillagerConfig;
import org.millenaire.common.forge.BuildingChunkLoader;
import org.millenaire.common.forge.CommonProxy;
import org.millenaire.common.forge.MillEventController;
import org.millenaire.common.forge.ServerTickHandler;
import org.millenaire.common.goal.Goal;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.item.MillItems;
import org.millenaire.common.network.ServerReceiver;
import org.millenaire.common.quest.Quest;
import org.millenaire.common.utilities.BlockItemUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.virtualdir.VirtualDir;
import org.millenaire.common.world.MillWorldData;
import org.millenaire.common.world.WorldGenVillage;

@Mod(modid="millenaire", name="Mill\u00e9naire", version="Mill\u00e9naire 8.1.2", useMetadata=true)
public class Mill {
    public static final String MODID = "millenaire";
    public static final String MODNAME = "Mill\u00e9naire";
    public static final String VERSION_NUMBER = "8.1.2";
    public static final String MINECRAFT_VERSION_NUMBER = "1.12.2";
    public static final String VERSION = "Mill\u00e9naire 8.1.2";
    public static final Logger LOGGER = LogManager.getLogger((String)"millenaire");
    @SidedProxy(clientSide="org.millenaire.client.forge.ClientProxy", serverSide="org.millenaire.common.forge.CommonProxy")
    public static CommonProxy proxy;
    @Mod.Instance
    public static Mill instance;
    public static List<MillWorldData> serverWorlds;
    public static MillWorldData clientWorld;
    public static List<File> loadingDirs;
    public static VirtualDir virtualLoadingDir;
    public static FMLEventChannel millChannel;
    public static boolean loadingComplete;
    public static final ResourceLocation ENTITY_PIG;
    public static final ResourceLocation ENTITY_COW;
    public static final ResourceLocation ENTITY_CHICKEN;
    public static final ResourceLocation ENTITY_SHEEP;
    public static final ResourceLocation ENTITY_SQUID;
    public static final ResourceLocation ENTITY_WOLF;
    public static final ResourceLocation ENTITY_POLAR_BEAR;
    public static final ResourceLocation ENTITY_SKELETON;
    public static final ResourceLocation ENTITY_CREEPER;
    public static final ResourceLocation ENTITY_SPIDER;
    public static final ResourceLocation ENTITY_CAVESPIDER;
    public static final ResourceLocation ENTITY_ZOMBIE;
    public static final ResourceLocation ENTITY_TARGETED_GHAST;
    public static final ResourceLocation ENTITY_TARGETED_BLAZE;
    public static final ResourceLocation ENTITY_TARGETED_WITHERSKELETON;
    public static final ResourceLocation CROP_WHEAT;
    public static final ResourceLocation CROP_CARROT;
    public static final ResourceLocation CROP_POTATO;
    public static final ResourceLocation CROP_RICE;
    public static final ResourceLocation CROP_TURMERIC;
    public static final ResourceLocation CROP_MAIZE;
    public static final ResourceLocation CROP_VINE;
    public static final ResourceLocation CROP_CACAO;
    public static final ResourceLocation CROP_FLOWER;
    public static final ResourceLocation CROP_COTTON;
    public static boolean startupError;
    public static boolean checkedMillenaireDir;
    public static boolean displayMillenaireLocationError;
    public static SoundEvent SOUND_NORMAN_BELLS;
    static final Class[] BANNER_CLASSES;
    public static final String[] BANNER_SHORTNAMES;

    public static MillWorldData getMillWorld(World world) {
        if (clientWorld != null && Mill.clientWorld.world == world) {
            return clientWorld;
        }
        for (MillWorldData mw : serverWorlds) {
            if (mw.world != world) continue;
            return mw;
        }
        if (serverWorlds != null && serverWorlds.size() > 0) {
            return serverWorlds.get(0);
        }
        return null;
    }

    public static boolean isDistantClient() {
        return clientWorld != null && serverWorlds.isEmpty();
    }

    private void addBannerPatterns() {
        EnumHelper.addEnum(BannerPattern.class, (String)"BYZANTINE", (Class[])BANNER_CLASSES, (Object[])new Object[]{"byzantine", BANNER_SHORTNAMES[0], new ItemStack((Item)MillItems.BANNERPATTERN, 1, 0)});
        EnumHelper.addEnum(BannerPattern.class, (String)"BYZANTINE_1", (Class[])BANNER_CLASSES, (Object[])new Object[]{"byzantine_1", BANNER_SHORTNAMES[1], new ItemStack((Item)MillItems.BANNERPATTERN, 1, 1)});
        EnumHelper.addEnum(BannerPattern.class, (String)"BYZANTINE_2", (Class[])BANNER_CLASSES, (Object[])new Object[]{"byzantine_2", BANNER_SHORTNAMES[2], new ItemStack((Item)MillItems.BANNERPATTERN, 1, 2)});
        EnumHelper.addEnum(BannerPattern.class, (String)"SELJUK", (Class[])BANNER_CLASSES, (Object[])new Object[]{"seljuk", BANNER_SHORTNAMES[3], new ItemStack((Item)MillItems.BANNERPATTERN, 1, 3)});
        EnumHelper.addEnum(BannerPattern.class, (String)"MAYAN", (Class[])BANNER_CLASSES, (Object[])new Object[]{"mayan", BANNER_SHORTNAMES[4], new ItemStack((Item)MillItems.BANNERPATTERN, 1, 4)});
        EnumHelper.addEnum(BannerPattern.class, (String)"INUIT", (Class[])BANNER_CLASSES, (Object[])new Object[]{"inuit", BANNER_SHORTNAMES[5], new ItemStack((Item)MillItems.BANNERPATTERN, 1, 5)});
        EnumHelper.addEnum(BannerPattern.class, (String)"INDIAN", (Class[])BANNER_CLASSES, (Object[])new Object[]{"indian", BANNER_SHORTNAMES[6], new ItemStack((Item)MillItems.BANNERPATTERN, 1, 6)});
        EnumHelper.addEnum(BannerPattern.class, (String)"INDIAN_1", (Class[])BANNER_CLASSES, (Object[])new Object[]{"indian_1", BANNER_SHORTNAMES[7], new ItemStack((Item)MillItems.BANNERPATTERN, 1, 7)});
        EnumHelper.addEnum(BannerPattern.class, (String)"INDIAN_2", (Class[])BANNER_CLASSES, (Object[])new Object[]{"indian_2", BANNER_SHORTNAMES[8], new ItemStack((Item)MillItems.BANNERPATTERN, 1, 8)});
        EnumHelper.addEnum(BannerPattern.class, (String)"INDIAN_3", (Class[])BANNER_CLASSES, (Object[])new Object[]{"indian_3", BANNER_SHORTNAMES[9], new ItemStack((Item)MillItems.BANNERPATTERN, 1, 9)});
        EnumHelper.addEnum(BannerPattern.class, (String)"INDIAN_4", (Class[])BANNER_CLASSES, (Object[])new Object[]{"indian_4", BANNER_SHORTNAMES[10], new ItemStack((Item)MillItems.BANNERPATTERN, 1, 10)});
        EnumHelper.addEnum(BannerPattern.class, (String)"INDIAN_5", (Class[])BANNER_CLASSES, (Object[])new Object[]{"indian_5", BANNER_SHORTNAMES[11], new ItemStack((Item)MillItems.BANNERPATTERN, 1, 11)});
        EnumHelper.addEnum(BannerPattern.class, (String)"NORMAN", (Class[])BANNER_CLASSES, (Object[])new Object[]{"norman", BANNER_SHORTNAMES[12], new ItemStack((Item)MillItems.BANNERPATTERN, 1, 12)});
        EnumHelper.addEnum(BannerPattern.class, (String)"MAYAN_1", (Class[])BANNER_CLASSES, (Object[])new Object[]{"mayan_1", BANNER_SHORTNAMES[13], new ItemStack((Item)MillItems.BANNERPATTERN, 1, 13)});
        EnumHelper.addEnum(BannerPattern.class, (String)"MAYAN_2", (Class[])BANNER_CLASSES, (Object[])new Object[]{"mayan_2", BANNER_SHORTNAMES[14], new ItemStack((Item)MillItems.BANNERPATTERN, 1, 14)});
        EnumHelper.addEnum(BannerPattern.class, (String)"MAYAN_3", (Class[])BANNER_CLASSES, (Object[])new Object[]{"mayan_3", BANNER_SHORTNAMES[15], new ItemStack((Item)MillItems.BANNERPATTERN, 1, 15)});
        EnumHelper.addEnum(BannerPattern.class, (String)"MAYAN_4", (Class[])BANNER_CLASSES, (Object[])new Object[]{"mayan_4", BANNER_SHORTNAMES[16], new ItemStack((Item)MillItems.BANNERPATTERN, 1, 16)});
        EnumHelper.addEnum(BannerPattern.class, (String)"INUIT_1", (Class[])BANNER_CLASSES, (Object[])new Object[]{"inuit_1", BANNER_SHORTNAMES[17], new ItemStack((Item)MillItems.BANNERPATTERN, 1, 17)});
        EnumHelper.addEnum(BannerPattern.class, (String)"INUIT_1", (Class[])BANNER_CLASSES, (Object[])new Object[]{"inuit_2", BANNER_SHORTNAMES[18], new ItemStack((Item)MillItems.BANNERPATTERN, 1, 18)});
        EnumHelper.addEnum(BannerPattern.class, (String)"INUIT_1", (Class[])BANNER_CLASSES, (Object[])new Object[]{"inuit_3", BANNER_SHORTNAMES[19], new ItemStack((Item)MillItems.BANNERPATTERN, 1, 19)});
        EnumHelper.addEnum(BannerPattern.class, (String)"INUIT_1", (Class[])BANNER_CLASSES, (Object[])new Object[]{"inuit_4", BANNER_SHORTNAMES[20], new ItemStack((Item)MillItems.BANNERPATTERN, 1, 20)});
        EnumHelper.addEnum(BannerPattern.class, (String)"JAPANESE", (Class[])BANNER_CLASSES, (Object[])new Object[]{"japanese", BANNER_SHORTNAMES[21], new ItemStack((Item)MillItems.BANNERPATTERN, 1, 21)});
        EnumHelper.addEnum(BannerPattern.class, (String)"JAPANESE_AGR", (Class[])BANNER_CLASSES, (Object[])new Object[]{"japanese_agr", BANNER_SHORTNAMES[22], new ItemStack((Item)MillItems.BANNERPATTERN, 1, 22)});
        EnumHelper.addEnum(BannerPattern.class, (String)"JAPANESE_MIL", (Class[])BANNER_CLASSES, (Object[])new Object[]{"japanese_mil", BANNER_SHORTNAMES[23], new ItemStack((Item)MillItems.BANNERPATTERN, 1, 23)});
        EnumHelper.addEnum(BannerPattern.class, (String)"JAPANESE_REL", (Class[])BANNER_CLASSES, (Object[])new Object[]{"japanese_rel", BANNER_SHORTNAMES[24], new ItemStack((Item)MillItems.BANNERPATTERN, 1, 24)});
        EnumHelper.addEnum(BannerPattern.class, (String)"JAPANESE_TRA", (Class[])BANNER_CLASSES, (Object[])new Object[]{"japanese_tra", BANNER_SHORTNAMES[25], new ItemStack((Item)MillItems.BANNERPATTERN, 1, 25)});
        EnumHelper.addEnum(BannerPattern.class, (String)"SELJUK_rel", (Class[])BANNER_CLASSES, (Object[])new Object[]{"seljuk_rel", BANNER_SHORTNAMES[26], new ItemStack((Item)MillItems.BANNERPATTERN, 1, 26)});
        EnumHelper.addEnum(BannerPattern.class, (String)"SELJUK_mil", (Class[])BANNER_CLASSES, (Object[])new Object[]{"seljuk_mil", BANNER_SHORTNAMES[27], new ItemStack((Item)MillItems.BANNERPATTERN, 1, 27)});
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        if (startupError) {
            return;
        }
        if (MillConfigValues.stopDefaultVillages) {
            MapGenVillage.field_75055_e = Arrays.asList(new Biome[0]);
        }
        MillBlocks.initBlockStates();
        boolean error = false;
        ProgressManager.ProgressBar bar = ProgressManager.push((String)"Loading Data", (int)(6 + virtualLoadingDir.getChildDirectory("cultures").listSubDirs().size()));
        bar.step("Items...");
        if (!error) {
            InvItem.loadItemList();
        }
        bar.step("Building points...");
        if (!error) {
            error = BuildingPlan.loadBuildingPoints();
        }
        bar.step("Villager configs...");
        if (!error) {
            VillagerConfig.loadConfigs();
        }
        bar.step("Villager goals...");
        if (!error) {
            Goal.initGoals();
        }
        if (!error) {
            error = Culture.loadCultures(bar);
        }
        bar.step("Quests...");
        if (!error) {
            Quest.loadQuests();
        }
        if (MillConfigValues.generateHelpData) {
            ParametersManager.generateHelpFiles();
            DocumentedElement.generateHelpFiles();
        }
        bar.step("Registering entities & items...");
        startupError = error;
        int id = 1;
        EntityRegistry.registerModEntity((ResourceLocation)MillVillager.GENERIC_VILLAGER, MillVillager.EntityGenericMale.class, (String)MillVillager.GENERIC_VILLAGER.func_110623_a(), (int)id++, (Object)MODID, (int)128, (int)3, (boolean)true);
        EntityRegistry.registerModEntity((ResourceLocation)MillVillager.GENERIC_SYMM_FEMALE, MillVillager.EntityGenericSymmFemale.class, (String)MillVillager.GENERIC_SYMM_FEMALE.func_110623_a(), (int)id++, (Object)MODID, (int)128, (int)3, (boolean)true);
        EntityRegistry.registerModEntity((ResourceLocation)MillVillager.GENERIC_ASYMM_FEMALE, MillVillager.EntityGenericAsymmFemale.class, (String)MillVillager.GENERIC_ASYMM_FEMALE.func_110623_a(), (int)id++, (Object)MODID, (int)128, (int)3, (boolean)true);
        EntityRegistry.registerModEntity((ResourceLocation)EntityWallDecoration.WALL_DECORATION, EntityWallDecoration.class, (String)EntityWallDecoration.WALL_DECORATION.func_110623_a(), (int)id++, (Object)MODID, (int)64, (int)3, (boolean)false);
        EntityRegistry.registerModEntity((ResourceLocation)ENTITY_TARGETED_BLAZE, EntityTargetedBlaze.class, (String)ENTITY_TARGETED_BLAZE.func_110623_a(), (int)id++, (Object)MODID, (int)64, (int)3, (boolean)true);
        EntityRegistry.registerModEntity((ResourceLocation)ENTITY_TARGETED_WITHERSKELETON, EntityTargetedWitherSkeleton.class, (String)ENTITY_TARGETED_WITHERSKELETON.func_110623_a(), (int)id++, (Object)MODID, (int)64, (int)3, (boolean)true);
        EntityRegistry.registerModEntity((ResourceLocation)ENTITY_TARGETED_GHAST, EntityTargetedGhast.class, (String)ENTITY_TARGETED_GHAST.func_110623_a(), (int)id++, (Object)MODID, (int)64, (int)3, (boolean)true);
        GameRegistry.addSmelting((ItemStack)new ItemStack((Block)MillBlocks.STONE_DECORATION, 1, 0), (ItemStack)new ItemStack((Block)MillBlocks.PAINTED_BRICK_WHITE, 1, 0), (float)1.0f);
        GameRegistry.addSmelting((ItemStack)new ItemStack((Item)MillItems.BEARMEAT_RAW, 1, 0), (ItemStack)new ItemStack((Item)MillItems.BEARMEAT_COOKED, 1, 0), (float)1.0f);
        GameRegistry.addSmelting((ItemStack)new ItemStack((Item)MillItems.WOLFMEAT_RAW, 1, 0), (ItemStack)new ItemStack((Item)MillItems.WOLFMEAT_COOKED, 1, 0), (float)1.0f);
        GameRegistry.addSmelting((ItemStack)new ItemStack((Item)MillItems.SEAFOOD_RAW, 1, 0), (ItemStack)new ItemStack((Item)MillItems.SEAFOOD_COOKED, 1, 0), (float)1.0f);
        ResourceLocation location = new ResourceLocation(MODID, "norman_bells");
        SOUND_NORMAN_BELLS = new SoundEvent(location);
        loadingComplete = true;
        if (MillConfigValues.LogOther >= 1) {
            if (startupError) {
                MillLog.major(this, "Mill\u00e9naire Mill\u00e9naire 8.1.2 loaded unsuccessfully.");
            } else {
                MillLog.major(this, "Mill\u00e9naire Mill\u00e9naire 8.1.2 loaded successfully.");
            }
        }
        FMLCommonHandler.instance().bus().register((Object)new ServerTickHandler());
        FMLCommonHandler.instance().bus().register((Object)this);
        millChannel.register((Object)new ServerReceiver());
        proxy.registerForgeClientClasses();
        proxy.registerKeyBindings();
        NetworkRegistry.INSTANCE.registerGuiHandler((Object)instance, proxy.createGuiHandler());
        GameRegistry.registerWorldGenerator((IWorldGenerator)new WorldGenVillage(), (int)1000);
        MinecraftForge.EVENT_BUS.register((Object)new MillEventController());
        ForgeChunkManager.setForcedChunkLoadingCallback((Object)this, (ForgeChunkManager.LoadingCallback)new BuildingChunkLoader.ChunkLoaderCallback());
        MillAdvancements.registerTriggers();
        proxy.loadLanguagesIfNeeded();
        if (MillConfigValues.DEV && !proxy.isTrueServer()) {
            BuildingDevUtilities.exportMissingTravelBookDesc();
            BuildingDevUtilities.exportTravelBookDescCSV();
        }
        this.addBannerPatterns();
        ProgressManager.pop((ProgressManager.ProgressBar)bar);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ContentDeployer.deployContent(event.getSourceFile());
        MillConfigValues.initConfig();
        proxy.refreshClientResources();
        BlockItemUtilities.initBlockTypes();
    }

    @Mod.EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
        event.registerServerCommand((ICommand)new CommandDebugResendProfiles());
        event.registerServerCommand((ICommand)new CommandDebugResetVillagers());
        event.registerServerCommand((ICommand)new CommandRenameVillage());
        event.registerServerCommand((ICommand)new CommandListActiveVillages());
        event.registerServerCommand((ICommand)new CommandTeleportToVillage());
        event.registerServerCommand((ICommand)new CommandGiveReputation());
        event.registerServerCommand((ICommand)new CommandSpawnVillage(false));
        event.registerServerCommand((ICommand)new CommandSpawnVillage(true));
        event.registerServerCommand((ICommand)new CommandImportCulture());
        event.registerServerCommand((ICommand)new CommandSwitchVillageControl());
    }

    static {
        serverWorlds = new ArrayList<MillWorldData>();
        clientWorld = null;
        loadingDirs = new ArrayList<File>();
        millChannel = NetworkRegistry.INSTANCE.newEventDrivenChannel(MODID);
        loadingComplete = false;
        ENTITY_PIG = new ResourceLocation("pig");
        ENTITY_COW = new ResourceLocation("cow");
        ENTITY_CHICKEN = new ResourceLocation("chicken");
        ENTITY_SHEEP = new ResourceLocation("sheep");
        ENTITY_SQUID = new ResourceLocation("squid");
        ENTITY_WOLF = new ResourceLocation("wolf");
        ENTITY_POLAR_BEAR = new ResourceLocation("polar_bear");
        ENTITY_SKELETON = new ResourceLocation("skeleton");
        ENTITY_CREEPER = new ResourceLocation("creeper");
        ENTITY_SPIDER = new ResourceLocation("spider");
        ENTITY_CAVESPIDER = new ResourceLocation("cave_spider");
        ENTITY_ZOMBIE = new ResourceLocation("zombie");
        ENTITY_TARGETED_GHAST = new ResourceLocation(MODID, "millghast");
        ENTITY_TARGETED_BLAZE = new ResourceLocation(MODID, "millblaze");
        ENTITY_TARGETED_WITHERSKELETON = new ResourceLocation(MODID, "millwitherskeleton");
        CROP_WHEAT = new ResourceLocation("wheat");
        CROP_CARROT = new ResourceLocation("carrots");
        CROP_POTATO = new ResourceLocation("potatoes");
        CROP_RICE = new ResourceLocation(MODID, "crop_rice");
        CROP_TURMERIC = new ResourceLocation(MODID, "crop_turmeric");
        CROP_MAIZE = new ResourceLocation(MODID, "crop_maize");
        CROP_VINE = new ResourceLocation(MODID, "crop_vine");
        CROP_CACAO = new ResourceLocation("cocoa");
        CROP_FLOWER = new ResourceLocation("flower");
        CROP_COTTON = new ResourceLocation(MODID, "crop_cotton");
        startupError = false;
        checkedMillenaireDir = false;
        displayMillenaireLocationError = false;
        BANNER_CLASSES = new Class[]{String.class, String.class, ItemStack.class};
        BANNER_SHORTNAMES = new String[]{"byz", "by1", "by2", "sjk", "may", "inu", "ind", "in1", "in2", "in3", "in4", "in5", "nor", "ma1", "ma2", "ma3", "ma4", "iu1", "iu2", "iu3", "iu4", "jap", "jaa", "jam", "jar", "jat", "sjkr", "sjkm"};
    }
}

