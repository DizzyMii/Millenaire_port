package org.dizzymii.millenaire2.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import org.dizzymii.millenaire2.Millenaire2;
import org.dizzymii.millenaire2.culture.Culture;
import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.entity.VillagerDebugger;
import org.dizzymii.millenaire2.util.Point;
import org.dizzymii.millenaire2.village.Building;
import org.dizzymii.millenaire2.village.VillagerRecord;
import org.dizzymii.millenaire2.world.MillWorldData;
import org.dizzymii.millenaire2.world.UserProfile;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Registers all Millenaire commands using NeoForge's Brigadier event.
 * Ported from the 10 command classes in org.millenaire.common.commands (Forge 1.12.2).
 */
@EventBusSubscriber(modid = Millenaire2.MODID)
public class MillCommands {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("millenaire")
                .then(Commands.literal("listvillages")
                        .requires(src -> src.hasPermission(0))
                        .executes(MillCommands::cmdListVillages)
                )
                .then(Commands.literal("tp")
                        .requires(src -> src.hasPermission(2))
                        .then(Commands.argument("village", StringArgumentType.greedyString())
                                .executes(MillCommands::cmdTeleport)
                        )
                )
                .then(Commands.literal("reputation")
                        .requires(src -> src.hasPermission(2))
                        .then(Commands.argument("amount", IntegerArgumentType.integer())
                                .then(Commands.argument("village", StringArgumentType.greedyString())
                                        .executes(MillCommands::cmdReputation)
                                )
                        )
                )
                .then(Commands.literal("spawn")
                        .requires(src -> src.hasPermission(2))
                        .then(Commands.argument("culture", StringArgumentType.string())
                                .executes(MillCommands::cmdSpawn)
                        )
                )
                .then(Commands.literal("rename")
                        .requires(src -> src.hasPermission(2))
                        .then(Commands.argument("village", StringArgumentType.greedyString())
                                .executes(MillCommands::cmdRename)
                        )
                )
                .then(Commands.literal("control")
                        .requires(src -> src.hasPermission(2))
                        .then(Commands.argument("village", StringArgumentType.greedyString())
                                .executes(MillCommands::cmdControl)
                        )
                )
                .then(Commands.literal("importculture")
                        .requires(src -> src.hasPermission(2))
                        .then(Commands.argument("culture", StringArgumentType.string())
                                .executes(MillCommands::cmdImportCulture)
                        )
                )
                .then(Commands.literal("debug")
                        .requires(src -> src.hasPermission(2))
                        .then(Commands.literal("resetvillagers")
                                .executes(MillCommands::cmdDebugResetVillagers)
                        )
                        .then(Commands.literal("resendprofiles")
                                .executes(MillCommands::cmdDebugResendProfiles)
                        )
                        .then(Commands.literal("villager")
                                .executes(MillCommands::cmdDebugVillagerNearest)
                                .then(Commands.literal("nearby")
                                        .then(Commands.argument("radius", IntegerArgumentType.integer(1, 128))
                                                .executes(MillCommands::cmdDebugVillagerNearby)
                                        )
                                )
                                .then(Commands.literal("extralog")
                                        .then(Commands.argument("radius", IntegerArgumentType.integer(1, 64))
                                                .executes(MillCommands::cmdDebugExtraLog)
                                        )
                                )
                        )
                )
        );
    }

    // ========== Command implementations ==========

    private static int cmdListVillages(CommandContext<CommandSourceStack> ctx) {
        MillWorldData mw = Millenaire2.getWorldData();
        if (mw == null) {
            ctx.getSource().sendFailure(Component.literal("Millenaire world data not loaded."));
            return 0;
        }

        List<Building> townhalls = new ArrayList<>();
        for (Building b : mw.allBuildings()) {
            if (b.isTownhall && b.isActive) townhalls.add(b);
        }

        if (townhalls.isEmpty()) {
            ctx.getSource().sendSuccess(() -> Component.literal("§6[Millénaire]§r No active villages found."), false);
            return 1;
        }

        ctx.getSource().sendSuccess(() -> Component.literal("§6[Millénaire]§r Active villages (" + townhalls.size() + "):"), false);
        for (Building th : townhalls) {
            String name = th.getName() != null ? th.getName() : "Unknown";
            String culture = th.cultureKey != null ? th.cultureKey : "?";
            Point pos = th.getPos();
            String posStr = pos != null ? ("[" + pos.x + ", " + pos.y + ", " + pos.z + "]") : "[?]";
            int villagerCount = th.getVillagerRecords().size();
            ctx.getSource().sendSuccess(() -> Component.literal(
                    "  §e" + name + "§r (" + culture + ") at " + posStr + " — " + villagerCount + " villagers"
            ), false);
        }
        return townhalls.size();
    }

    private static int cmdTeleport(CommandContext<CommandSourceStack> ctx) {
        if (!(ctx.getSource().getEntity() instanceof ServerPlayer player)) {
            ctx.getSource().sendFailure(Component.literal("This command can only be run by a player."));
            return 0;
        }
        String villageName = StringArgumentType.getString(ctx, "village");
        Building target = findVillageByName(villageName);
        if (target == null || target.getPos() == null) {
            ctx.getSource().sendFailure(Component.literal("Village '" + villageName + "' not found."));
            return 0;
        }
        Point pos = target.getPos();
        player.teleportTo(pos.x + 0.5, pos.y + 1.0, pos.z + 0.5);
        ctx.getSource().sendSuccess(() -> Component.literal("§6[Millénaire]§r Teleported to " + target.getName()), false);
        return 1;
    }

    private static int cmdReputation(CommandContext<CommandSourceStack> ctx) {
        if (!(ctx.getSource().getEntity() instanceof ServerPlayer player)) {
            ctx.getSource().sendFailure(Component.literal("This command can only be run by a player."));
            return 0;
        }
        int amount = IntegerArgumentType.getInteger(ctx, "amount");
        String villageName = StringArgumentType.getString(ctx, "village");
        Building target = findVillageByName(villageName);
        if (target == null || target.getTownHallPos() == null) {
            ctx.getSource().sendFailure(Component.literal("Village '" + villageName + "' not found."));
            return 0;
        }
        MillWorldData mw = Millenaire2.getWorldData();
        if (mw == null) return 0;
        UserProfile profile = mw.getOrCreateProfile(player.getUUID(), player.getGameProfile().getName());
        profile.adjustVillageReputation(target.getTownHallPos(), amount);
        int newRep = profile.getVillageReputation(target.getTownHallPos());
        mw.setDirty();
        ctx.getSource().sendSuccess(() -> Component.literal(
                "§6[Millénaire]§r Reputation with " + target.getName() + " set to " + newRep
        ), true);
        return 1;
    }

    private static int cmdSpawn(CommandContext<CommandSourceStack> ctx) {
        String cultureName = StringArgumentType.getString(ctx, "culture");
        Culture culture = Culture.getCultureByName(cultureName);
        if (culture == null) {
            ctx.getSource().sendFailure(Component.literal("Culture '" + cultureName + "' not found."));
            return 0;
        }
        MillWorldData mw = Millenaire2.getWorldData();
        if (mw == null) return 0;

        // Create a new townhall building at the command source's position
        net.minecraft.core.BlockPos blockPos = net.minecraft.core.BlockPos.containing(
                ctx.getSource().getPosition());
        Point spawnPoint = new Point(blockPos);

        Building th = new Building();
        th.cultureKey = cultureName;
        th.isTownhall = true;
        th.isActive = true;
        th.setPos(spawnPoint);
        th.setTownHallPos(spawnPoint);
        th.setName(culture.key + " Village");
        th.world = ctx.getSource().getLevel();
        th.mw = mw;

        mw.addBuilding(th, spawnPoint);
        ctx.getSource().sendSuccess(() -> Component.literal(
                "§6[Millénaire]§r Spawned " + cultureName + " village at " + spawnPoint
        ), true);
        return 1;
    }

    private static int cmdRename(CommandContext<CommandSourceStack> ctx) {
        String input = StringArgumentType.getString(ctx, "village");
        // Format: "OldName NewName" — split on last space
        int lastSpace = input.lastIndexOf(' ');
        if (lastSpace <= 0) {
            ctx.getSource().sendFailure(Component.literal("Usage: /millenaire rename <old name> <new name>"));
            return 0;
        }
        String oldName = input.substring(0, lastSpace).trim();
        String newName = input.substring(lastSpace + 1).trim();
        Building target = findVillageByName(oldName);
        if (target == null) {
            ctx.getSource().sendFailure(Component.literal("Village '" + oldName + "' not found."));
            return 0;
        }
        target.setName(newName);
        MillWorldData mw = Millenaire2.getWorldData();
        if (mw != null) mw.setDirty();
        ctx.getSource().sendSuccess(() -> Component.literal(
                "§6[Millénaire]§r Renamed '" + oldName + "' to '" + newName + "'"
        ), true);
        return 1;
    }

    private static int cmdControl(CommandContext<CommandSourceStack> ctx) {
        if (!(ctx.getSource().getEntity() instanceof ServerPlayer player)) {
            ctx.getSource().sendFailure(Component.literal("This command can only be run by a player."));
            return 0;
        }
        String villageName = StringArgumentType.getString(ctx, "village");
        Building target = findVillageByName(villageName);
        if (target == null) {
            ctx.getSource().sendFailure(Component.literal("Village '" + villageName + "' not found."));
            return 0;
        }
        // Toggle control
        if (target.controlledBy != null && target.controlledBy.equals(player.getUUID())) {
            target.controlledBy = null;
            target.controlledByName = null;
            ctx.getSource().sendSuccess(() -> Component.literal(
                    "§6[Millénaire]§r Released control of " + target.getName()
            ), true);
        } else {
            target.controlledBy = player.getUUID();
            target.controlledByName = player.getGameProfile().getName();
            ctx.getSource().sendSuccess(() -> Component.literal(
                    "§6[Millénaire]§r Now controlling " + target.getName()
            ), true);
        }
        MillWorldData mw = Millenaire2.getWorldData();
        if (mw != null) mw.setDirty();
        return 1;
    }

    private static int cmdImportCulture(CommandContext<CommandSourceStack> ctx) {
        String cultureName = StringArgumentType.getString(ctx, "culture");
        // Reload cultures from disk
        Culture.loadCultures();
        Culture culture = Culture.getCultureByName(cultureName);
        if (culture == null) {
            ctx.getSource().sendFailure(Component.literal("Culture '" + cultureName + "' not found after reload."));
            return 0;
        }
        ctx.getSource().sendSuccess(() -> Component.literal(
                "§6[Millénaire]§r Reloaded culture '" + cultureName + "' successfully."
        ), true);
        return 1;
    }

    private static int cmdDebugResetVillagers(CommandContext<CommandSourceStack> ctx) {
        MillWorldData mw = Millenaire2.getWorldData();
        if (mw == null) return 0;
        int count = 0;
        for (Building b : mw.allBuildings()) {
            for (VillagerRecord vr : b.getVillagerRecords()) {
                if (vr.killed) {
                    vr.killed = false;
                    count++;
                }
            }
        }
        mw.setDirty();
        final int resetCount = count;
        ctx.getSource().sendSuccess(() -> Component.literal(
                "§6[Millénaire]§r Reset " + resetCount + " killed villager records. They will respawn."
        ), true);
        return 1;
    }

    private static int cmdDebugResendProfiles(CommandContext<CommandSourceStack> ctx) {
        MillWorldData mw = Millenaire2.getWorldData();
        if (mw == null) return 0;
        int count = mw.profiles.size();
        // Mark dirty so profiles are re-saved
        mw.setDirty();
        ctx.getSource().sendSuccess(() -> Component.literal(
                "§6[Millénaire]§r Marked " + count + " profiles for resync."
        ), true);
        return 1;
    }

    /** Shows debug info for the nearest MillVillager within 16 blocks. */
    private static int cmdDebugVillagerNearest(CommandContext<CommandSourceStack> ctx) {
        if (!(ctx.getSource().getEntity() instanceof ServerPlayer player)) {
            ctx.getSource().sendFailure(Component.literal("Must be run by a player."));
            return 0;
        }
        MillVillager nearest = findNearestVillager(player, 16);
        if (nearest == null) {
            ctx.getSource().sendFailure(Component.literal("No Millénaire villager found within 16 blocks."));
            return 0;
        }
        VillagerDebugger.sendDebugToPlayer(nearest, player);
        return 1;
    }

    /** Lists all MillVillagers within a given radius with their current goal and activity. */
    private static int cmdDebugVillagerNearby(CommandContext<CommandSourceStack> ctx) {
        if (!(ctx.getSource().getEntity() instanceof ServerPlayer player)) {
            ctx.getSource().sendFailure(Component.literal("Must be run by a player."));
            return 0;
        }
        int radius = IntegerArgumentType.getInteger(ctx, "radius");
        AABB box = AABB.ofSize(player.position(), radius * 2, radius * 2, radius * 2);
        List<MillVillager> villagers = player.serverLevel().getEntitiesOfClass(MillVillager.class, box);
        if (villagers.isEmpty()) {
            ctx.getSource().sendSuccess(
                    () -> Component.literal("§6[Millénaire]§r No villagers within " + radius + " blocks."), false);
            return 0;
        }
        player.sendSystemMessage(Component.literal(
                "§6[Millénaire]§r " + villagers.size() + " villager(s) within " + radius + " blocks:"));
        for (MillVillager v : villagers) {
            String name = v.getFirstName() + " " + v.getFamilyName();
            String goal = v.goalKey != null ? v.goalKey : "idle";
            String activity;
            try {
                activity = v.getBrain().getActiveActivities().stream()
                        .map(a -> a.getName()).reduce((a, b) -> a + "+" + b).orElse("none");
            } catch (Exception e) {
                activity = "?";
            }
            player.sendSystemMessage(Component.literal(
                    "  §e" + name + "§r [" + (v.vtypeKey != null ? v.vtypeKey : "?") + "] "
                    + "activity=§f" + activity + "§r goal=§f" + goal));
        }
        return villagers.size();
    }

    /** Toggles verbose extraLog flag on all MillVillagers within radius. */
    private static int cmdDebugExtraLog(CommandContext<CommandSourceStack> ctx) {
        if (!(ctx.getSource().getEntity() instanceof ServerPlayer player)) {
            ctx.getSource().sendFailure(Component.literal("Must be run by a player."));
            return 0;
        }
        int radius = IntegerArgumentType.getInteger(ctx, "radius");
        AABB box = AABB.ofSize(player.position(), radius * 2, radius * 2, radius * 2);
        List<MillVillager> villagers = player.serverLevel().getEntitiesOfClass(MillVillager.class, box);
        int toggled = 0;
        for (MillVillager v : villagers) {
            v.extraLog = !v.extraLog;
            toggled++;
        }
        final int count = toggled;
        ctx.getSource().sendSuccess(
                () -> Component.literal("§6[Millénaire]§r Toggled extraLog on " + count + " villager(s)."), false);
        return count;
    }

    // ========== Helpers ==========

    @Nullable
    private static Building findVillageByName(String name) {
        MillWorldData mw = Millenaire2.getWorldData();
        if (mw == null) return null;
        String lower = name.toLowerCase();
        for (Building b : mw.allBuildings()) {
            if (b.isTownhall && b.getName() != null && b.getName().toLowerCase().contains(lower)) {
                return b;
            }
        }
        return null;
    }

    @Nullable
    private static MillVillager findNearestVillager(ServerPlayer player, int radius) {
        AABB box = AABB.ofSize(player.position(), radius * 2, radius * 2, radius * 2);
        List<MillVillager> villagers = player.serverLevel().getEntitiesOfClass(MillVillager.class, box);
        MillVillager nearest = null;
        double bestDist = Double.MAX_VALUE;
        for (MillVillager v : villagers) {
            double d = v.distanceToSqr(player);
            if (d < bestDist) {
                bestDist = d;
                nearest = v;
            }
        }
        return nearest;
    }
}
