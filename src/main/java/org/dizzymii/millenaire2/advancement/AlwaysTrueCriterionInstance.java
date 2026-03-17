package org.dizzymii.millenaire2.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

/**
 * A criterion trigger that is always satisfied — used for advancements granted
 * purely programmatically (no in-game condition to check).
 * Ported from org.millenaire.common.advancements.AlwaysTrueCriterionInstance (Forge 1.12.2).
 *
 * In 1.21.1, this extends SimpleCriterionTrigger with a TriggerInstance that
 * always returns true.
 */
public class AlwaysTrueCriterionInstance extends SimpleCriterionTrigger<AlwaysTrueCriterionInstance.Instance> {

    @Override
    public Codec<Instance> codec() {
        return Instance.CODEC;
    }

    /**
     * Triggers this criterion for the given player, granting the associated advancement criterion.
     */
    public void trigger(ServerPlayer player) {
        super.trigger(player, instance -> true);
    }

    /**
     * The trigger instance — has no conditions, always matches.
     */
    public record Instance(Optional<net.minecraft.advancements.critereon.ContextAwarePredicate> player)
            implements SimpleCriterionTrigger.SimpleInstance {

        public static final Codec<Instance> CODEC = RecordCodecBuilder.create(inst ->
                inst.group(
                        net.minecraft.advancements.critereon.ContextAwarePredicate.CODEC
                                .optionalFieldOf("player").forGetter(Instance::player)
                ).apply(inst, Instance::new)
        );

        public static Instance create() {
            return new Instance(Optional.empty());
        }
    }
}
