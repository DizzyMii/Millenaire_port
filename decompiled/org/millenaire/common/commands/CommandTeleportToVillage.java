/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.command.CommandBase
 *  net.minecraft.command.CommandException
 *  net.minecraft.command.ICommand
 *  net.minecraft.command.ICommandSender
 *  net.minecraft.command.WrongUsageException
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 */
package org.millenaire.common.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.millenaire.common.commands.CommandUtilities;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.utilities.LanguageUtilities;
import org.millenaire.common.village.Building;
import org.millenaire.common.world.MillWorldData;

public class CommandTeleportToVillage
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
            if (args.length != 1 && args.length != 2) {
                throw new WrongUsageException(this.func_71518_a(sender), new Object[0]);
            }
            String villageParam = args[0];
            MillWorldData worldData = Mill.getMillWorld(world);
            List<Building> townHalls = CommandUtilities.getMatchingVillages(worldData, villageParam);
            if (townHalls.size() == 0) {
                throw new CommandException(LanguageUtilities.string("command.tp_nomatchingvillage"), new Object[0]);
            }
            if (townHalls.size() > 1) {
                throw new CommandException(LanguageUtilities.string("command.tp_multiplematchingvillages", "" + townHalls.size()), new Object[0]);
            }
            Building village = townHalls.get(0);
            Entity entity = null;
            if (args.length == 1) {
                if (sender instanceof EntityPlayer) {
                    entity = (Entity)sender;
                }
            } else {
                entity = CommandBase.func_184885_b((MinecraftServer)server, (ICommandSender)sender, (String)args[1]);
            }
            if (entity != null && entity instanceof EntityPlayerMP) {
                entity.func_184210_p();
                ((EntityPlayerMP)entity).field_71135_a.func_175089_a(village.location.getSellingPos().x, village.location.getSellingPos().y, village.location.getSellingPos().z, 0.0f, 0.0f, Collections.emptySet());
            }
        }
    }

    public List<String> func_71514_a() {
        return Collections.emptyList();
    }

    public String func_71517_b() {
        return "millTp";
    }

    public int getRequiredPermissionLevel() {
        return 3;
    }

    public List<String> func_184883_a(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        if (args.length == 2) {
            return CommandBase.func_71530_a((String[])args, (String[])server.func_71213_z());
        }
        if (args.length == 1) {
            World world = sender.func_130014_f_();
            MillWorldData worldData = Mill.getMillWorld(world);
            List<Building> townHalls = CommandUtilities.getMatchingVillages(worldData, args[0]);
            ArrayList<String> possibleMatches = new ArrayList<String>();
            for (Building th : townHalls) {
                possibleMatches.add(CommandUtilities.normalizeString(th.getVillageQualifiedName()));
            }
            return possibleMatches;
        }
        return Collections.emptyList();
    }

    public String func_71518_a(ICommandSender sender) {
        return "commands." + this.func_71517_b().toLowerCase() + ".usage";
    }

    public boolean func_82358_a(String[] args, int index) {
        return index == 0;
    }
}

