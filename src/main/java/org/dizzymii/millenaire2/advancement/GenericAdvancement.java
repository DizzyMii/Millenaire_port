package org.dizzymii.millenaire2.advancement;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.dizzymii.millenaire2.Millenaire2;

/**
 * A simple advancement trigger that can be granted programmatically.
 * Ported from org.millenaire.common.advancements.GenericAdvancement (Forge 1.12.2).
 *
 * In NeoForge 1.21.1, custom criteria use CriterionTrigger<T>.
 * This stub stores the key and provides a grant() method; full trigger
 * wiring will be implemented when advancement JSON data is created.
 */
public class GenericAdvancement {

    private final String key;
    private final ResourceLocation triggerRL;

    public GenericAdvancement(String key) {
        this.key = key;
        this.triggerRL = ResourceLocation.fromNamespaceAndPath(Millenaire2.MODID, key);
    }

    public String getKey() {
        return key;
    }

    public ResourceLocation getTriggerRL() {
        return triggerRL;
    }

    public void grant(ServerPlayer player) {
        // TODO: Implement custom CriterionTrigger and grant advancement via PlayerAdvancements
        //       Also send advancement-earned packet to client
    }
}
