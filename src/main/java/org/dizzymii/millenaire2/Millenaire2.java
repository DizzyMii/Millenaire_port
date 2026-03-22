package org.dizzymii.millenaire2;

import com.mojang.logging.LogUtils;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import org.dizzymii.millenaire2.init.ModBlocks;
import org.dizzymii.millenaire2.client.screen.FirePitScreen;
import org.dizzymii.millenaire2.data.ContentDeployer;
import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.init.ModBlockEntityTypes;
import org.dizzymii.millenaire2.init.ModCreativeTabs;
import org.dizzymii.millenaire2.init.ModEntityTypes;
import org.dizzymii.millenaire2.init.ModItems;
import org.dizzymii.millenaire2.init.ModMenuTypes;
import org.dizzymii.millenaire2.network.MillNetworking;
import org.dizzymii.millenaire2.world.MillWorldData;
import org.slf4j.Logger;

@Mod(Millenaire2.MODID)
public class Millenaire2 {
    public static final String MODID = "millenaire2";
    public static final String MODNAME = "Millénaire";
    public static final String VERSION = "2.0.0";
    private static final Logger LOGGER = LogUtils.getLogger();

    public Millenaire2(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::registerEntityAttributes);
        modEventBus.addListener(MillNetworking::register);

        // Register all deferred registers
        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModCreativeTabs.register(modEventBus);
        ModEntityTypes.register(modEventBus);
        ModBlockEntityTypes.register(modEventBus);
        ModMenuTypes.register(modEventBus);

        // Force class loading of registration holders
        ModBlocks.init();
        ModItems.init();
        ModEntityTypes.init();
        ModBlockEntityTypes.init();
        ModCreativeTabs.init();
        ModMenuTypes.init();

        NeoForge.EVENT_BUS.register(this);

        modContainer.registerConfig(ModConfig.Type.COMMON, MillConfig.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("{} {} common setup", MODNAME, VERSION);
        event.enqueueWork(() -> {
            ContentDeployer.deployContent();
            org.dizzymii.millenaire2.goal.Goal.initGoals();
        });
    }

    private void registerEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntityTypes.MILL_VILLAGER.get(), MillVillager.createAttributes().build());
        event.put(ModEntityTypes.TARGETED_BLAZE.get(), net.minecraft.world.entity.monster.Blaze.createAttributes().build());
        event.put(ModEntityTypes.TARGETED_WITHER_SKELETON.get(), net.minecraft.world.entity.monster.WitherSkeleton.createAttributes().build());
        event.put(ModEntityTypes.TARGETED_GHAST.get(), net.minecraft.world.entity.monster.Ghast.createAttributes().build());
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("{} server starting", MODNAME);
        org.dizzymii.millenaire2.buildingplan.PointType.loadFromServer(event.getServer());
        org.dizzymii.millenaire2.culture.Culture.loadCultures();
        org.dizzymii.millenaire2.world.BiomeCultureMapper.loadFromServer(event.getServer());
        org.dizzymii.millenaire2.item.TradeGoodLoader.loadFromServer(event.getServer());
        org.dizzymii.millenaire2.village.VillageEconomyLoader.loadFromServer(event.getServer());
        org.dizzymii.millenaire2.village.DiplomacyManager.loadFromServer(event.getServer());
        java.io.File questDir = new java.io.File(org.dizzymii.millenaire2.util.MillCommonUtilities.getMillenaireContentDir(), "quests");
        org.dizzymii.millenaire2.quest.Quest.loadAllQuests(questDir);
        // Initialize world data from the overworld
        net.minecraft.server.level.ServerLevel overworld = event.getServer().overworld();
        MillWorldData loadedWorldData = MillWorldData.get(overworld);
        loadedWorldData.clearTriedChunks();
        if (MillConfig.generateVillages() && !loadedWorldData.generateVillages) {
            loadedWorldData.generateVillages = true;
            loadedWorldData.setDirty();
        }
        LOGGER.info("{} world data loaded: {} buildings", MODNAME, loadedWorldData.allBuildings().size());

        // Evaluate and log parity contracts
        var parityReport = org.dizzymii.millenaire2.parity.ParityContracts.evaluateStartupReport(loadedWorldData);
        org.dizzymii.millenaire2.parity.ParityContracts.logStartupReport(parityReport);

        // Register empty GameTest templates
        org.dizzymii.millenaire2.gametest.GameTestSetup.ensureTemplates(overworld);
    }

    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            LOGGER.info("{} client setup", MODNAME);
        }

        @SubscribeEvent
        public static void registerMenuScreens(RegisterMenuScreensEvent event) {
            event.register(ModMenuTypes.FIRE_PIT.get(), FirePitScreen::new);
        }
    }
}

