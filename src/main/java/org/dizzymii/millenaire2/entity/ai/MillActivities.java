package org.dizzymii.millenaire2.entity.ai;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.schedule.Activity;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.dizzymii.millenaire2.Millenaire2;

/**
 * Custom Activities for Millénaire villager brain scheduling.
 */
public class MillActivities {

    public static final DeferredRegister<Activity> ACTIVITIES =
            DeferredRegister.create(Registries.ACTIVITY, Millenaire2.MODID);

    /** Working activity — construction, farming, gathering, crafting */
    public static final DeferredHolder<Activity, Activity> WORK =
            ACTIVITIES.register("work", () -> new Activity("millenaire2:work"));

    /** Socializing activity — chatting, resting, wandering near village */
    public static final DeferredHolder<Activity, Activity> SOCIALISE =
            ACTIVITIES.register("socialise", () -> new Activity("millenaire2:socialise"));

    /** Combat activity — defending, raiding, fleeing */
    public static final DeferredHolder<Activity, Activity> COMBAT =
            ACTIVITIES.register("combat", () -> new Activity("millenaire2:combat"));

    public static void init() {
        // Class loading triggers registration
    }
}
