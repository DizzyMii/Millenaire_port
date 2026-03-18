package org.dizzymii.millenaire2;

import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.entity.BlockEntityType;
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
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import org.dizzymii.millenaire2.block.MillBlocks;
import org.dizzymii.millenaire2.client.screen.FirePitScreen;
import org.dizzymii.millenaire2.data.ContentDeployer;
import org.dizzymii.millenaire2.entity.MillEntities;
import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.item.MillItems;
import org.dizzymii.millenaire2.menu.MillMenuTypes;
import org.dizzymii.millenaire2.network.MillNetworking;
import org.dizzymii.millenaire2.world.MillWorldData;
import org.slf4j.Logger;

import javax.annotation.Nullable;

@Mod(Millenaire2.MODID)
public class Millenaire2 {
    public static final String MODID = "millenaire2";
    public static final String MODNAME = "Millénaire";
    public static final String VERSION = "2.0.0";
    private static final Logger LOGGER = LogUtils.getLogger();
    @Nullable private static MillWorldData worldData;

    // --- Deferred Registers ---
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MODID);

    // --- Creative Tab ---
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MILL_TAB = CREATIVE_MODE_TABS.register("millenaire_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.millenaire2"))
                    .withTabsBefore(CreativeModeTabs.COMBAT)
                    .icon(() -> MillItems.DENIER.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        MillItems.ALL_ITEMS.forEach(item -> output.accept(item.get()));
                    })
                    .build());

    public Millenaire2(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::registerEntityAttributes);
        modEventBus.addListener(MillNetworking::register);

        // Register all deferred registers
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        ENTITY_TYPES.register(modEventBus);
        BLOCK_ENTITY_TYPES.register(modEventBus);
        MillMenuTypes.MENU_TYPES.register(modEventBus);

        // Force class loading of registration holders
        MillBlocks.init();
        MillItems.init();
        MillEntities.init();
        MillMenuTypes.init();

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
        event.put(MillEntities.GENERIC_MALE.get(), MillVillager.createAttributes().build());
        event.put(MillEntities.GENERIC_SYMM_FEMALE.get(), MillVillager.createAttributes().build());
        event.put(MillEntities.GENERIC_ASYMM_FEMALE.get(), MillVillager.createAttributes().build());
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("{} server starting", MODNAME);
        org.dizzymii.millenaire2.buildingplan.PointType.registerDefaults();
        org.dizzymii.millenaire2.culture.Culture.loadCultures();
        // Initialize world data from the overworld
        net.minecraft.server.level.ServerLevel overworld = event.getServer().overworld();
        worldData = MillWorldData.get(overworld);
        LOGGER.info("{} world data loaded: {} buildings", MODNAME, worldData.allBuildings().size());

        // Register empty GameTest templates
        org.dizzymii.millenaire2.gametest.GameTestSetup.ensureTemplates(overworld);
    }

    @Nullable
    public static MillWorldData getWorldData() {
        return worldData;
    }

    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            LOGGER.info("{} client setup", MODNAME);
        }

        @SubscribeEvent
        public static void registerMenuScreens(RegisterMenuScreensEvent event) {
            event.register(MillMenuTypes.FIRE_PIT.get(), FirePitScreen::new);
        }
    }
}
