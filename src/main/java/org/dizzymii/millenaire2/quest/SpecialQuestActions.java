package org.dizzymii.millenaire2.quest;

/**
 * Special quest action constants and handlers.
 * Ported from org.millenaire.common.quest.SpecialQuestActions (Forge 1.12.2).
 *
 * Full action logic will be implemented once the quest runtime is complete.
 */
public final class SpecialQuestActions {

    private SpecialQuestActions() {}

    public static final String COMPLETE = "_complete";
    public static final String EXPLORE_TAG = "action_explore_";
    public static final String ENCHANTMENTTABLE = "action_build_enchantment_table";
    public static final String UNDERWATER_GLASS = "action_underwater_glass";
    public static final String UNDERWATER_DIVE = "action_underwater_dive";
    public static final String TOPOFTHEWORLD = "action_topoftheworld";
    public static final String BOTTOMOFTHEWORLD = "action_bottomoftheworld";
    public static final String BOREHOLE = "action_borehole";
    public static final String BOREHOLETNT = "action_boreholetnt";
    public static final String BOREHOLETNTLIT = "action_boreholetntlit";
    public static final String THEVOID = "action_thevoid";
    public static final String MAYANSIEGE = "action_mayansiege";
    public static final String NORMANMARVEL_PICKLOCATION = "normanmarvel_picklocation";
    public static final String NORMANMARVEL_GENERATE = "normanmarvel_generate";
    public static final String NORMANMARVEL_LOCATION = "normanmarvel_location";

    /**
     * Checks whether a special action condition is satisfied for the given quest instance.
     * Returns true if the action is fulfilled (or unrecognised — defaults to pass).
     */
    public static boolean checkAction(String actionKey, QuestInstance qi,
                                       net.minecraft.server.level.ServerPlayer player) {
        if (actionKey == null || actionKey.isEmpty()) return true;

        return switch (actionKey) {
            case COMPLETE -> true; // always-pass marker
            case ENCHANTMENTTABLE -> playerNearBlock(player, net.minecraft.world.level.block.Blocks.ENCHANTING_TABLE, 5);
            case TOPOFTHEWORLD -> player.blockPosition().getY() >= 300;
            case BOTTOMOFTHEWORLD -> player.blockPosition().getY() <= 5;
            case THEVOID -> player.blockPosition().getY() <= -60;
            case UNDERWATER_DIVE -> player.isUnderWater();
            case UNDERWATER_GLASS -> {
                net.minecraft.core.BlockPos below = player.blockPosition().below();
                yield player.level().getBlockState(below).is(net.minecraft.world.level.block.Blocks.GLASS);
            }
            default -> {
                if (actionKey.startsWith(EXPLORE_TAG)) {
                    // Exploration quests: tag is set when player reaches a biome/location
                    // Fulfilled if the profile already has the tag
                    if (qi.profile != null) {
                        yield qi.profile.hasTag(actionKey);
                    }
                    yield false;
                }
                // Unknown action — default pass
                yield true;
            }
        };
    }

    private static boolean playerNearBlock(net.minecraft.server.level.ServerPlayer player,
                                            net.minecraft.world.level.block.Block block, int radius) {
        net.minecraft.core.BlockPos center = player.blockPosition();
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (player.level().getBlockState(center.offset(dx, dy, dz)).is(block)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
