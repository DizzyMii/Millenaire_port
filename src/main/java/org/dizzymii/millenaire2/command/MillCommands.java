package org.dizzymii.millenaire2.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import org.dizzymii.millenaire2.Millenaire2;

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
                        .executes(ctx -> {
                            // TODO: list active villages from MillWorldData
                            ctx.getSource().sendSuccess(() -> Component.literal("Millenaire villages: (not yet implemented)"), false);
                            return 1;
                        })
                )
                .then(Commands.literal("tp")
                        .requires(src -> src.hasPermission(2))
                        .then(Commands.argument("village", StringArgumentType.greedyString())
                                .executes(ctx -> {
                                    // TODO: teleport to matching village
                                    ctx.getSource().sendSuccess(() -> Component.literal("Teleport: not yet implemented"), false);
                                    return 1;
                                })
                        )
                )
                .then(Commands.literal("reputation")
                        .requires(src -> src.hasPermission(2))
                        .then(Commands.argument("amount", IntegerArgumentType.integer())
                                .then(Commands.argument("village", StringArgumentType.greedyString())
                                        .executes(ctx -> {
                                            // TODO: give reputation to player for village
                                            ctx.getSource().sendSuccess(() -> Component.literal("Reputation: not yet implemented"), false);
                                            return 1;
                                        })
                                )
                        )
                )
                .then(Commands.literal("spawn")
                        .requires(src -> src.hasPermission(2))
                        .then(Commands.argument("culture", StringArgumentType.string())
                                .executes(ctx -> {
                                    // TODO: spawn a new village
                                    ctx.getSource().sendSuccess(() -> Component.literal("Spawn: not yet implemented"), false);
                                    return 1;
                                })
                        )
                )
                .then(Commands.literal("rename")
                        .requires(src -> src.hasPermission(2))
                        .then(Commands.argument("village", StringArgumentType.greedyString())
                                .executes(ctx -> {
                                    // TODO: rename village
                                    ctx.getSource().sendSuccess(() -> Component.literal("Rename: not yet implemented"), false);
                                    return 1;
                                })
                        )
                )
                .then(Commands.literal("control")
                        .requires(src -> src.hasPermission(2))
                        .then(Commands.argument("village", StringArgumentType.greedyString())
                                .executes(ctx -> {
                                    // TODO: switch village control
                                    ctx.getSource().sendSuccess(() -> Component.literal("Control: not yet implemented"), false);
                                    return 1;
                                })
                        )
                )
                .then(Commands.literal("importculture")
                        .requires(src -> src.hasPermission(2))
                        .then(Commands.argument("culture", StringArgumentType.string())
                                .executes(ctx -> {
                                    // TODO: import culture
                                    ctx.getSource().sendSuccess(() -> Component.literal("Import: not yet implemented"), false);
                                    return 1;
                                })
                        )
                )
                .then(Commands.literal("debug")
                        .requires(src -> src.hasPermission(2))
                        .then(Commands.literal("resetvillagers")
                                .executes(ctx -> {
                                    // TODO: reset villagers
                                    ctx.getSource().sendSuccess(() -> Component.literal("Debug reset: not yet implemented"), false);
                                    return 1;
                                })
                        )
                        .then(Commands.literal("resendprofiles")
                                .executes(ctx -> {
                                    // TODO: resend profiles
                                    ctx.getSource().sendSuccess(() -> Component.literal("Debug resend: not yet implemented"), false);
                                    return 1;
                                })
                        )
                )
        );
    }
}
