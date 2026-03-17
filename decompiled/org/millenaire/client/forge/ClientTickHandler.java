/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  net.minecraftforge.fml.common.gameevent.TickEvent$ClientTickEvent
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package org.millenaire.client.forge;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.millenaire.client.gui.DisplayActions;
import org.millenaire.common.forge.Mill;

@SideOnly(value=Side.CLIENT)
public class ClientTickHandler {
    private boolean startupMessageShow;

    @SubscribeEvent
    public void tickStart(TickEvent.ClientTickEvent event) {
        if (Mill.clientWorld == null || !Mill.clientWorld.millenaireEnabled || Minecraft.func_71410_x().field_71439_g == null) {
            return;
        }
        boolean inOverworld = Minecraft.func_71410_x().field_71439_g.field_71093_bK == 0;
        Mill.clientWorld.updateWorldClient(inOverworld);
        if (!this.startupMessageShow) {
            DisplayActions.displayStartupOrError((EntityPlayer)Minecraft.func_71410_x().field_71439_g, Mill.startupError);
            this.startupMessageShow = true;
        }
        Mill.proxy.handleClientGameUpdate();
    }
}

