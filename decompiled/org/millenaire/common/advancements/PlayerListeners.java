/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  net.minecraft.advancements.ICriterionTrigger$Listener
 *  net.minecraft.advancements.PlayerAdvancements
 */
package org.millenaire.common.advancements;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import org.millenaire.common.advancements.AlwaysTrueCriterionInstance;

public class PlayerListeners {
    private final PlayerAdvancements playerAdvancements;
    private final Set<ICriterionTrigger.Listener<AlwaysTrueCriterionInstance>> listeners = Sets.newHashSet();

    public PlayerListeners(PlayerAdvancements playerAdvancementsIn) {
        this.playerAdvancements = playerAdvancementsIn;
    }

    public void add(ICriterionTrigger.Listener<AlwaysTrueCriterionInstance> listener) {
        this.listeners.add(listener);
    }

    public void grantAndNotify() {
        List list = null;
        for (ICriterionTrigger.Listener<AlwaysTrueCriterionInstance> listener : this.listeners) {
            if (!((AlwaysTrueCriterionInstance)listener.func_192158_a()).test()) continue;
            if (list == null) {
                list = Lists.newArrayList();
            }
            list.add(listener);
        }
        if (list != null) {
            for (ICriterionTrigger.Listener listener : list) {
                listener.func_192159_a(this.playerAdvancements);
            }
        }
    }

    public boolean isEmpty() {
        return this.listeners.isEmpty();
    }

    public void remove(ICriterionTrigger.Listener<AlwaysTrueCriterionInstance> listener) {
        this.listeners.remove(listener);
    }
}

