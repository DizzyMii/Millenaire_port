package org.dizzymii.millenaire2.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.dizzymii.millenaire2.util.Point;
import org.dizzymii.millenaire2.village.Building;
import org.dizzymii.millenaire2.world.MillWorldData;

import java.util.List;

/**
 * Village banner item. When used, displays information about the nearest village.
 * Given to players as a reputation reward or purchased from village chiefs.
 * The banner's NBT stores the village position it belongs to.
 */
public class ItemVillageBanner extends Item {

    public ItemVillageBanner(Properties props) {
        super(props);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide && level instanceof ServerLevel serverLevel) {
            MillWorldData mw = MillWorldData.get(serverLevel);

            // Find nearest village within 200 blocks
            Point playerPos = new Point(player.blockPosition());
            Building nearest = null;
            double nearestDist = Double.MAX_VALUE;
            for (Building b : mw.allBuildings()) {
                if (!b.isTownhall || b.getPos() == null) continue;
                double dist = playerPos.distanceTo(b.getPos());
                if (dist < 200 && dist < nearestDist) {
                    nearest = b;
                    nearestDist = dist;
                }
            }

            if (nearest != null) {
                String villageName = nearest.getName() != null ? nearest.getName() : "Unknown";
                String culture = nearest.cultureKey != null ? nearest.cultureKey : "unknown";
                int pop = nearest.getVillagerRecords().size();
                int dist = (int) nearestDist;

                player.sendSystemMessage(Component.literal("")
                        .append(Component.literal("[Millénaire] ").withStyle(ChatFormatting.GOLD))
                        .append(Component.literal(villageName).withStyle(ChatFormatting.YELLOW))
                        .append(Component.literal(" (" + culture + ")").withStyle(ChatFormatting.GRAY)));
                player.sendSystemMessage(Component.literal("  Population: " + pop
                        + " | Distance: " + dist + " blocks").withStyle(ChatFormatting.WHITE));

                // Show reputation
                var profile = mw.getOrCreateProfile(player.getUUID(), player.getName().getString());
                int rep = profile.getVillageReputation(nearest.getPos());
                String repName = org.dizzymii.millenaire2.util.VillageUtilities.getRelationName(rep);
                player.sendSystemMessage(Component.literal("  Reputation: " + rep + " (" + repName + ")")
                        .withStyle(rep >= 0 ? ChatFormatting.GREEN : ChatFormatting.RED));
            } else {
                player.sendSystemMessage(Component.literal("[Millénaire] No village found nearby.")
                        .withStyle(ChatFormatting.RED));
            }

            player.getCooldowns().addCooldown(this, 40);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("Use to show nearby village info").withStyle(ChatFormatting.GRAY));
    }
}
