/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.math.ChunkPos
 *  net.minecraft.world.World
 *  net.minecraftforge.common.ForgeChunkManager
 *  net.minecraftforge.common.ForgeChunkManager$LoadingCallback
 *  net.minecraftforge.common.ForgeChunkManager$Ticket
 *  net.minecraftforge.common.ForgeChunkManager$Type
 */
package org.millenaire.common.forge;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.village.Building;

public class BuildingChunkLoader {
    Building townHall;
    List<ForgeChunkManager.Ticket> tickets = new ArrayList<ForgeChunkManager.Ticket>();
    public boolean chunksLoaded = false;

    public BuildingChunkLoader(Building th) {
        this.townHall = th;
    }

    private ForgeChunkManager.Ticket getTicket() {
        for (ForgeChunkManager.Ticket ticket : this.tickets) {
            if (ticket.getChunkList().size() >= ticket.getChunkListDepth() - 1) continue;
            return ticket;
        }
        ForgeChunkManager.Ticket ticket = ForgeChunkManager.requestTicket((Object)Mill.instance, (World)this.townHall.world, (ForgeChunkManager.Type)ForgeChunkManager.Type.NORMAL);
        if (ticket == null) {
            MillLog.warning(this.townHall, "Couldn't get ticket in BuildingChunkLoader. Your Forge chunk loading settings must be interfearing.");
            return null;
        }
        this.tickets.add(ticket);
        return ticket;
    }

    public void loadChunks() {
        if (this.townHall.winfo != null) {
            int nbLoaded = 0;
            for (int cx = this.townHall.winfo.chunkStartX - 1; cx < this.townHall.winfo.chunkStartX + this.townHall.winfo.length / 16 + 1; ++cx) {
                for (int cz = this.townHall.winfo.chunkStartZ - 1; cz < this.townHall.winfo.chunkStartZ + this.townHall.winfo.width / 16 + 1; ++cz) {
                    ForgeChunkManager.Ticket ticket = this.getTicket();
                    if (ticket == null) continue;
                    ChunkPos chunkCoords = new ChunkPos(cx, cz);
                    ForgeChunkManager.forceChunk((ForgeChunkManager.Ticket)ticket, (ChunkPos)chunkCoords);
                    ++nbLoaded;
                }
            }
            this.chunksLoaded = true;
            if (MillConfigValues.LogChunkLoader >= 1) {
                MillLog.major(this.townHall, "Force-Loaded " + nbLoaded + " chunks.");
            }
        }
    }

    public void unloadChunks() {
        for (ForgeChunkManager.Ticket ticket : this.tickets) {
            ForgeChunkManager.releaseTicket((ForgeChunkManager.Ticket)ticket);
        }
        this.tickets.clear();
        this.chunksLoaded = false;
        if (MillConfigValues.LogChunkLoader >= 1) {
            MillLog.major(this.townHall, "Unloaded the chunks.");
        }
    }

    public static class ChunkLoaderCallback
    implements ForgeChunkManager.LoadingCallback {
        public void ticketsLoaded(List<ForgeChunkManager.Ticket> tickets, World world) {
            for (ForgeChunkManager.Ticket ticket : tickets) {
                ForgeChunkManager.releaseTicket((ForgeChunkManager.Ticket)ticket);
            }
        }
    }
}

