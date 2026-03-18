package org.dizzymii.millenaire2.sound;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.dizzymii.millenaire2.Millenaire2;

/**
 * Central registry for all Millénaire sound events.
 * Sound files (.ogg) go in assets/millenaire2/sounds/.
 * Sound definitions go in assets/millenaire2/sounds.json.
 */
public class MillSounds {

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, Millenaire2.MODID);

    // --- Village ambience ---
    public static final DeferredHolder<SoundEvent, SoundEvent> NORMAN_BELLS =
            register("norman_bells");

    // --- Villager action sounds ---
    public static final DeferredHolder<SoundEvent, SoundEvent> VILLAGER_WORKING =
            register("villager_working");
    public static final DeferredHolder<SoundEvent, SoundEvent> VILLAGER_EATING =
            register("villager_eating");
    public static final DeferredHolder<SoundEvent, SoundEvent> VILLAGER_SLEEPING =
            register("villager_sleeping");
    public static final DeferredHolder<SoundEvent, SoundEvent> VILLAGER_TRADING =
            register("villager_trading");
    public static final DeferredHolder<SoundEvent, SoundEvent> VILLAGER_GREETING =
            register("villager_greeting");
    public static final DeferredHolder<SoundEvent, SoundEvent> VILLAGER_HURT =
            register("villager_hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> VILLAGER_DEATH =
            register("villager_death");

    // --- Construction sounds ---
    public static final DeferredHolder<SoundEvent, SoundEvent> CONSTRUCTION_HAMMER =
            register("construction_hammer");
    public static final DeferredHolder<SoundEvent, SoundEvent> CONSTRUCTION_COMPLETE =
            register("construction_complete");

    // --- Combat sounds ---
    public static final DeferredHolder<SoundEvent, SoundEvent> VILLAGER_ATTACK =
            register("villager_attack");
    public static final DeferredHolder<SoundEvent, SoundEvent> VILLAGER_BOW_SHOOT =
            register("villager_bow_shoot");

    // --- Quest / interaction sounds ---
    public static final DeferredHolder<SoundEvent, SoundEvent> QUEST_ACCEPTED =
            register("quest_accepted");
    public static final DeferredHolder<SoundEvent, SoundEvent> QUEST_COMPLETED =
            register("quest_completed");

    private static DeferredHolder<SoundEvent, SoundEvent> register(String name) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(Millenaire2.MODID, name);
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(id));
    }
}
