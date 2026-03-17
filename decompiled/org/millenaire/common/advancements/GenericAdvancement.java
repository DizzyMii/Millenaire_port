/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  net.minecraft.advancements.ICriterionTrigger
 *  net.minecraft.advancements.ICriterionTrigger$Listener
 *  net.minecraft.advancements.PlayerAdvancements
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.util.ResourceLocation
 */
package org.millenaire.common.advancements;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import org.millenaire.common.advancements.AlwaysTrueCriterionInstance;
import org.millenaire.common.advancements.MillAdvancements;
import org.millenaire.common.advancements.PlayerListeners;
import org.millenaire.common.network.ServerSender;

public class GenericAdvancement
implements ICriterionTrigger<AlwaysTrueCriterionInstance> {
    private final String key;
    private final ResourceLocation triggerRL;
    private final Map<PlayerAdvancements, PlayerListeners> playerListeners = new HashMap<PlayerAdvancements, PlayerListeners>();

    public GenericAdvancement(String key) {
        this.key = key;
        this.triggerRL = new ResourceLocation(key);
    }

    public void func_192165_a(PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<AlwaysTrueCriterionInstance> listener) {
        PlayerListeners listeners = this.playerListeners.get(playerAdvancementsIn);
        if (listeners == null) {
            listeners = new PlayerListeners(playerAdvancementsIn);
            this.playerListeners.put(playerAdvancementsIn, listeners);
        }
        listeners.add(listener);
    }

    public AlwaysTrueCriterionInstance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
        return new AlwaysTrueCriterionInstance(this.func_192163_a());
    }

    public ResourceLocation func_192163_a() {
        return this.triggerRL;
    }

    public String getKey() {
        return this.key;
    }

    public void grant(EntityPlayer player) {
        if (player instanceof EntityPlayerMP) {
            PlayerListeners playerListeners = this.playerListeners.get(((EntityPlayerMP)player).func_192039_O());
            if (playerListeners != null) {
                playerListeners.grantAndNotify();
            }
            ServerSender.sendAdvancementEarned((EntityPlayerMP)player, this.key);
        }
        MillAdvancements.addToStats(player, this.key);
    }

    public void func_192167_a(PlayerAdvancements playerAdvancementsIn) {
        this.playerListeners.remove(playerAdvancementsIn);
    }

    public void func_192164_b(PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<AlwaysTrueCriterionInstance> listener) {
        PlayerListeners listeners = this.playerListeners.get(playerAdvancementsIn);
        if (listeners != null) {
            listeners.remove(listener);
            if (listeners.isEmpty()) {
                this.playerListeners.remove(playerAdvancementsIn);
            }
        }
    }
}

