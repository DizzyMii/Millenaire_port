package org.dizzymii.millenaire2.menu;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.dizzymii.millenaire2.Millenaire2;

/**
 * Central registry for all Millénaire container menu types.
 */
public class MillMenuTypes {

    public static final DeferredRegister<MenuType<?>> MENU_TYPES =
            DeferredRegister.create(Registries.MENU, Millenaire2.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<FirePitMenu>> FIRE_PIT =
            MENU_TYPES.register("fire_pit", () -> IMenuTypeExtension.create(FirePitMenu::new));

    /**
     * Called from Millenaire2 constructor to force class loading.
     */
    public static void init() {
        // Class loading triggers all static final fields above
    }
}
