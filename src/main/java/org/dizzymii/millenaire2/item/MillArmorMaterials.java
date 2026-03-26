package org.dizzymii.millenaire2.item;

import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.dizzymii.millenaire2.Millenaire2;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Custom {@link ArmorMaterial} entries for each Millénaire culture.
 * Registered via {@link DeferredRegister} in the mod constructor.
 */
public class MillArmorMaterials {

    public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS =
            DeferredRegister.create(Registries.ARMOR_MATERIAL, Millenaire2.MODID);

    // Norman — iron-level, good toughness
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> NORMAN =
            ARMOR_MATERIALS.register("norman", () -> create("norman",
                    defenseMap(2, 5, 6, 2), 10, 1.0F, 0.0F));

    // Japanese Red — iron-level
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> JAPANESE_RED =
            ARMOR_MATERIALS.register("japanese_red", () -> create("japanese_red",
                    defenseMap(2, 5, 6, 2), 12, 0.5F, 0.0F));

    // Japanese Blue — iron-level
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> JAPANESE_BLUE =
            ARMOR_MATERIALS.register("japanese_blue", () -> create("japanese_blue",
                    defenseMap(2, 5, 6, 2), 12, 0.5F, 0.0F));

    // Japanese Guard — slightly better
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> JAPANESE_GUARD =
            ARMOR_MATERIALS.register("japanese_guard", () -> create("japanese_guard",
                    defenseMap(2, 6, 7, 2), 12, 1.0F, 0.0F));

    // Byzantine — higher enchantability
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> BYZANTINE =
            ARMOR_MATERIALS.register("byzantine", () -> create("byzantine",
                    defenseMap(2, 5, 6, 2), 15, 1.0F, 0.0F));

    // Fur (Inuit) — leather-level
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> FUR =
            ARMOR_MATERIALS.register("fur", () -> create("fur",
                    defenseMap(1, 3, 4, 1), 10, 0.0F, 0.0F));

    // Seljuk — iron-level
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> SELJUK =
            ARMOR_MATERIALS.register("seljuk", () -> create("seljuk",
                    defenseMap(2, 5, 6, 2), 10, 1.0F, 0.0F));

    // Mayan Crown — gold-level helmet only (reuse for the single crown piece)
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> MAYAN =
            ARMOR_MATERIALS.register("mayan", () -> create("mayan",
                    defenseMap(2, 4, 5, 2), 25, 0.0F, 0.0F));

    // ========== Helpers ==========

    private static Map<ArmorItem.Type, Integer> defenseMap(int boots, int leggings, int chestplate, int helmet) {
        return Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
            map.put(ArmorItem.Type.BOOTS, boots);
            map.put(ArmorItem.Type.LEGGINGS, leggings);
            map.put(ArmorItem.Type.CHESTPLATE, chestplate);
            map.put(ArmorItem.Type.HELMET, helmet);
            // BODY covers horse/wolf armor in 1.21.1; mapped to chestplate value by convention
            map.put(ArmorItem.Type.BODY, chestplate);
        });
    }

    private static ArmorMaterial create(String name, Map<ArmorItem.Type, Integer> defense,
                                         int enchantmentValue, float toughness, float knockbackResistance) {
        return new ArmorMaterial(
                defense,
                enchantmentValue,
                SoundEvents.ARMOR_EQUIP_IRON,
                () -> Ingredient.EMPTY,
                List.of(new ArmorMaterial.Layer(
                        ResourceLocation.fromNamespaceAndPath(Millenaire2.MODID, name)
                )),
                toughness,
                knockbackResistance
        );
    }

    /** Called from Millenaire2 constructor to force class loading. */
    public static void init() {
        // Class loading triggers all static final fields above
    }
}
