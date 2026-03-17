/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.command.CommandException
 *  net.minecraft.command.ICommand
 *  net.minecraft.command.ICommandSender
 *  net.minecraft.command.WrongUsageException
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 */
package org.millenaire.common.commands;

import java.util.Collections;
import java.util.List;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.network.ServerSender;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.world.MillWorldData;
import org.millenaire.common.world.UserProfile;

public class CommandDebugResendProfiles
implements ICommand {
    public boolean func_184882_a(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    public int compareTo(ICommand o) {
        return this.func_71517_b().compareTo(o.func_71517_b());
    }

    public void func_184881_a(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        World world = sender.func_130014_f_();
        if (!world.field_72995_K) {
            if (args.length != 1) {
                throw new WrongUsageException(this.func_71518_a(sender), new Object[0]);
            }
            if (args[0].equals("all")) {
                MillWorldData worldData = Mill.getMillWorld(world);
                for (UserProfile profile : worldData.profiles.values()) {
                    if (!profile.connected) continue;
                    profile.sendInitialPackets();
                    if (sender instanceof EntityPlayer) {
                        ServerSender.sendTranslatedSentence((EntityPlayer)sender, '2', "Resent profile data for " + profile.playerName, new String[0]);
                        continue;
                    }
                    MillLog.major(profile, "Resent profile data.");
                }
            } else {
                EntityPlayer player = world.func_72924_a(args[0]);
                if (player == null) {
                    throw new CommandException("This command requires a player name or 'all' as first parameter.", new Object[0]);
                }
                MillWorldData worldData = Mill.getMillWorld(world);
                UserProfile profile = worldData.getProfile(player);
                profile.sendInitialPackets();
                if (sender instanceof EntityPlayer) {
                    ServerSender.sendTranslatedSentence((EntityPlayer)sender, '2', "Resent profile data for " + profile.playerName, new String[0]);
                } else {
                    MillLog.major(profile, "Resent profile data.");
                }
            }
        }
    }

    public List<String> func_71514_a() {
        return Collections.emptyList();
    }

    public String func_71517_b() {
        return "millDebugSendProfiles";
    }

    public List<String> func_184883_a(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        return Collections.emptyList();
    }

    public String func_71518_a(ICommandSender sender) {
        return "commands." + this.func_71517_b().toLowerCase() + ".usage";
    }

    public boolean func_82358_a(String[] args, int index) {
        return index == 1;
    }
}

