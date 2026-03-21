package org.dizzymii.millenaire2.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.dizzymii.millenaire2.Millenaire2;
import org.dizzymii.millenaire2.item.MillItems;

/**
 * Creative mode tab registration for Millénaire.
 */
public class ModCreativeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Millenaire2.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MILL_TAB =
            CREATIVE_MODE_TABS.register("millenaire_tab",
                    () -> CreativeModeTab.builder()
                            .title(Component.translatable("itemGroup.millenaire2"))
                            .withTabsBefore(CreativeModeTabs.COMBAT)
                            .icon(() -> MillItems.DENIER.get().getDefaultInstance())
                            .displayItems((parameters, output) -> {
                                MillItems.ALL_ITEMS.forEach(item -> output.accept(item.get()));
                            })
                            .build());

    public static void register(IEventBus modEventBus) {
        CREATIVE_MODE_TABS.register(modEventBus);
    }

    public static void init() {
        // Class loading triggers all static final fields above
    }
}
