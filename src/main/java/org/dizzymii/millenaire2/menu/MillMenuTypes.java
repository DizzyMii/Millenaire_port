package org.dizzymii.millenaire2.menu;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.dizzymii.millenaire2.Millenaire2;
import org.dizzymii.millenaire2.ui.ContainerLockedChest;
import org.dizzymii.millenaire2.ui.ContainerPuja;
import org.dizzymii.millenaire2.ui.ContainerTrade;

/**
 * Central registry for all Millénaire container menu types.
 */
public class MillMenuTypes {

    public static final DeferredRegister<MenuType<?>> MENU_TYPES =
            DeferredRegister.create(Registries.MENU, Millenaire2.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<FirePitMenu>> FIRE_PIT =
            MENU_TYPES.register("fire_pit", () -> IMenuTypeExtension.create(FirePitMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<ContainerTrade>> TRADE =
            MENU_TYPES.register("trade", () -> IMenuTypeExtension.create(ContainerTrade::new));

    public static final DeferredHolder<MenuType<?>, MenuType<ContainerLockedChest>> LOCKED_CHEST =
            MENU_TYPES.register("locked_chest", () -> IMenuTypeExtension.create(ContainerLockedChest::new));

    public static final DeferredHolder<MenuType<?>, MenuType<ContainerPuja>> PUJA =
            MENU_TYPES.register("puja", () -> IMenuTypeExtension.create(ContainerPuja::new));

    /**
     * Called from Millenaire2 constructor to force class loading.
     */
    public static void init() {
        // Class loading triggers all static final fields above
    }
}
