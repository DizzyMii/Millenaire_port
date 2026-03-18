package org.dizzymii.millenaire2.item;

import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;

public class ItemMillenaireSword extends SwordItem {
    public ItemMillenaireSword(Tier tier, Properties props) {
        super(tier, props);
    }

    @Override
    public boolean hurtEnemy(net.minecraft.world.item.ItemStack stack, net.minecraft.world.entity.LivingEntity target,
                             net.minecraft.world.entity.LivingEntity attacker) {
        // Obsidian sword sets target on fire
        target.setRemainingFireTicks(100);
        return super.hurtEnemy(stack, target, attacker);
    }
}
