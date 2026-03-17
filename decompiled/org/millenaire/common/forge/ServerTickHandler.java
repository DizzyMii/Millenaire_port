/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  net.minecraftforge.fml.common.gameevent.TickEvent$ServerTickEvent
 */
package org.millenaire.common.forge;

import java.util.ArrayList;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.world.MillWorldData;

public class ServerTickHandler {
    @SubscribeEvent
    public void tickStart(TickEvent.ServerTickEvent event) {
        if (Mill.startupError) {
            return;
        }
        ArrayList<MillWorldData> serversCopy = new ArrayList<MillWorldData>(Mill.serverWorlds);
        for (MillWorldData mw : serversCopy) {
            mw.updateWorldServer();
        }
    }
}

