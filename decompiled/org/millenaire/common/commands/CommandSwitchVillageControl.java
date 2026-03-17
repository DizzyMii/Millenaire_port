/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
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

import com.mojang.authlib.GameProfile;
import java.util.ArrayList;
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
import org.millenaire.common.commands.CommandUtilities;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.network.ServerSender;
import org.millenaire.common.utilities.LanguageUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.village.Building;
import org.millenaire.common.world.MillWorldData;

public class CommandSwitchVillageControl
implements ICommand {
    public boolean func_184882_a(MinecraftServer server, ICommandSender sender) {
        return sender.func_70003_b(this.getRequiredPermissionLevel(), this.func_71517_b());
    }

    public int compareTo(ICommand o) {
        return this.func_71517_b().compareTo(o.func_71517_b());
    }

    public void func_184881_a(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        World world = sender.func_130014_f_();
        if (!world.field_72995_K) {
            if (args.length != 2) {
                throw new WrongUsageException(this.func_71518_a(sender), new Object[0]);
            }
            MillWorldData worldData = Mill.getMillWorld(world);
            List<Building> townHalls = CommandUtilities.getMatchingVillages(worldData, args[0]);
            if (townHalls.size() == 0) {
                throw new CommandException(LanguageUtilities.string("command.tp_nomatchingvillage"), new Object[0]);
            }
            if (townHalls.size() > 1) {
                throw new CommandException(LanguageUtilities.string("command.tp_multiplematchingvillages", "" + townHalls.size()), new Object[0]);
            }
            Building village = townHalls.get(0);
            if (!village.villageType.playerControlled) {
                throw new CommandException(LanguageUtilities.string("command.switchcontrol_notcontrolled", village.getVillageQualifiedName()), new Object[0]);
            }
            String playerName = args[1];
            GameProfile profile = world.func_73046_m().func_152358_ax().func_152655_a(playerName);
            if (profile == null) {
                throw new CommandException(LanguageUtilities.string("command.switchcontrol_playernotfound", playerName), new Object[0]);
            }
            String oldControllerName = village.controlledByName;
            village.controlledBy = profile.getId();
            village.controlledByName = profile.getName();
            MillLog.major(this, "Switched controller from " + oldControllerName + " to " + village.controlledByName + " via command by " + sender.func_70005_c_() + ".");
            for (EntityPlayer player : world.field_73010_i) {
                ServerSender.sendTranslatedSentence(player, '9', "command.switchcontrol_notification", sender.func_70005_c_(), oldControllerName, profile.getName(), village.getVillageQualifiedName());
            }
        }
    }

    public List<String> func_71514_a() {
        return Collections.emptyList();
    }

    public String func_71517_b() {
        return "millSwitchVillageControl";
    }

    public int getRequiredPermissionLevel() {
        return 3;
    }

    public List<String> func_184883_a(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        if (args.length == 1) {
            World world = sender.func_130014_f_();
            MillWorldData worldData = Mill.getMillWorld(world);
            List<Building> townHalls = CommandUtilities.getMatchingVillages(worldData, args[0]);
            ArrayList<String> possibleMatches = new ArrayList<String>();
            for (Building th : townHalls) {
                if (!th.villageType.playerControlled) continue;
                possibleMatches.add(CommandUtilities.normalizeString(th.getVillageQualifiedName()));
            }
            return possibleMatches;
        }
        if (args.length == 2) {
            World world = sender.func_130014_f_();
            ArrayList<String> possibleMatches = new ArrayList<String>();
            String normalizedQuery = CommandUtilities.normalizeString(args[1]);
            for (String userName : world.func_73046_m().func_152358_ax().func_152654_a()) {
                String normalizedName = CommandUtilities.normalizeString(userName);
                if (!normalizedName.startsWith(normalizedQuery)) continue;
                possibleMatches.add(normalizedName);
            }
            return possibleMatches;
        }
        return Collections.emptyList();
    }

    public String func_71518_a(ICommandSender sender) {
        return "commands." + this.func_71517_b().toLowerCase() + ".usage";
    }

    public boolean func_82358_a(String[] args, int index) {
        return false;
    }
}

