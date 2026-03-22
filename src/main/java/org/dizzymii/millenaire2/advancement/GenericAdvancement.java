package org.dizzymii.millenaire2.advancement;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.dizzymii.millenaire2.Millenaire2;

import javax.annotation.Nullable;

/**
 * A simple advancement trigger that can be granted programmatically.
 * Ported from org.millenaire.common.advancements.GenericAdvancement (Forge 1.12.2).
 *
 * In NeoForge 1.21.1, advancements are granted via PlayerAdvancements by
 * looking up the AdvancementHolder from the server's advancement tree and
 * awarding all remaining criteria.
 */
public class GenericAdvancement {
    private static final Logger LOGGER = LogUtils.getLogger();

    private final String key;
    private final ResourceLocation advancementRL;

    public GenericAdvancement(String key) {
        this.key = key;
        this.advancementRL = ResourceLocation.fromNamespaceAndPath(Millenaire2.MODID, key);
    }

    public String getKey() {
        return key;
    }

    public ResourceLocation getAdvancementRL() {
        return advancementRL;
    }

    /**
     * Grants this advancement to the player if not already earned.
     */
    public void grant(ServerPlayer player) {
        AdvancementHolder holder = getHolder(player);
        if (holder == null) {
            LOGGER.debug("Advancement not found in tree: " + advancementRL);
            return;
        }

        AdvancementProgress progress = player.getAdvancements().getOrStartProgress(holder);
        if (progress.isDone()) return;

        // Award all remaining criteria
        for (String criterion : progress.getRemainingCriteria()) {
            player.getAdvancements().award(holder, criterion);
        }

        LOGGER.debug("Granted advancement '" + key + "' to " + player.getName().getString());
    }

    /**
     * Checks whether the player already has this advancement.
     */
    public boolean isEarned(ServerPlayer player) {
        AdvancementHolder holder = getHolder(player);
        if (holder == null) return false;
        return player.getAdvancements().getOrStartProgress(holder).isDone();
    }

    /**
     * Revokes this advancement from the player.
     */
    public void revoke(ServerPlayer player) {
        AdvancementHolder holder = getHolder(player);
        if (holder == null) return;

        AdvancementProgress progress = player.getAdvancements().getOrStartProgress(holder);
        for (String criterion : progress.getCompletedCriteria()) {
            player.getAdvancements().revoke(holder, criterion);
        }
    }

    @Nullable
    private AdvancementHolder getHolder(ServerPlayer player) {
        return player.server.getAdvancements().get(advancementRL);
    }

    @Override
    public String toString() {
        return "GenericAdvancement[" + key + "]";
    }
}
