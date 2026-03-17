/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.Unpooled
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.network.PacketBuffer
 *  net.minecraftforge.fml.common.network.internal.FMLProxyPacket
 */
package org.millenaire.client.network;

import io.netty.buffer.Unpooled;
import java.io.IOException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.culture.Culture;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.network.StreamReadWrite;
import org.millenaire.common.ui.GuiActions;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.village.Building;
import org.millenaire.common.village.BuildingProject;

public class ClientSender {
    public static void activateMillChest(EntityPlayer player, Point pos) {
        PacketBuffer data = ClientSender.getPacketBuffer();
        data.writeInt(200);
        data.writeInt(81);
        StreamReadWrite.writeNullablePoint(pos, data);
        ClientSender.createAndSendServerPacket(data);
    }

    public static void controlledBuildingsForgetBuilding(EntityPlayer player, Building townHall, BuildingProject project) {
        PacketBuffer data = ClientSender.getPacketBuffer();
        data.writeInt(200);
        data.writeInt(71);
        StreamReadWrite.writeNullablePoint(townHall.getPos(), data);
        data.func_180714_a(project.key);
        StreamReadWrite.writeNullablePoint(project.location.pos, data);
        ClientSender.createAndSendServerPacket(data);
        GuiActions.controlledBuildingsForgetBuilding(player, townHall, project);
    }

    public static void controlledBuildingsToggleUpgrades(EntityPlayer player, Building townHall, BuildingProject project, boolean allow) {
        PacketBuffer data = ClientSender.getPacketBuffer();
        data.writeInt(200);
        data.writeInt(70);
        StreamReadWrite.writeNullablePoint(townHall.getPos(), data);
        data.func_180714_a(project.key);
        StreamReadWrite.writeNullablePoint(project.location.pos, data);
        data.writeBoolean(allow);
        ClientSender.createAndSendServerPacket(data);
        GuiActions.controlledBuildingsToggleUpgrades(player, townHall, project, allow);
    }

    public static void controlledMilitaryCancelRaid(EntityPlayer player, Building th) {
        PacketBuffer data = ClientSender.getPacketBuffer();
        data.writeInt(200);
        data.writeInt(92);
        StreamReadWrite.writeNullablePoint(th.getPos(), data);
        ClientSender.createAndSendServerPacket(data);
        GuiActions.controlledMilitaryCancelRaid(player, th);
    }

    public static void controlledMilitaryDiplomacy(EntityPlayer player, Building th, Point target, int amount) {
        PacketBuffer data = ClientSender.getPacketBuffer();
        data.writeInt(200);
        data.writeInt(90);
        StreamReadWrite.writeNullablePoint(th.getPos(), data);
        StreamReadWrite.writeNullablePoint(target, data);
        data.writeInt(amount);
        ClientSender.createAndSendServerPacket(data);
        GuiActions.controlledMilitaryDiplomacy(player, th, target, amount);
    }

    public static void controlledMilitaryPlanRaid(EntityPlayer player, Building th, Point target) {
        PacketBuffer data = ClientSender.getPacketBuffer();
        data.writeInt(200);
        data.writeInt(91);
        StreamReadWrite.writeNullablePoint(th.getPos(), data);
        StreamReadWrite.writeNullablePoint(target, data);
        ClientSender.createAndSendServerPacket(data);
        GuiActions.controlledMilitaryPlanRaid(player, th, th.mw.getBuilding(target));
    }

    private static void createAndSendServerPacket(PacketBuffer bytes) {
        ClientSender.sendPacketToServer(bytes);
    }

    public static void devCommand(int devcommand) {
        PacketBuffer data = ClientSender.getPacketBuffer();
        data.writeInt(206);
        data.writeInt(devcommand);
        ClientSender.createAndSendServerPacket(data);
    }

    public static void displayVillageList(boolean loneBuildings) {
        PacketBuffer data = ClientSender.getPacketBuffer();
        data.writeInt(201);
        data.writeBoolean(loneBuildings);
        ClientSender.createAndSendServerPacket(data);
    }

    public static PacketBuffer getPacketBuffer() {
        return new PacketBuffer(Unpooled.buffer());
    }

    public static void hireExtend(EntityPlayer player, MillVillager villager) {
        PacketBuffer data = ClientSender.getPacketBuffer();
        data.writeInt(200);
        data.writeInt(31);
        data.writeLong(villager.getVillagerId());
        ClientSender.createAndSendServerPacket(data);
        GuiActions.hireExtend(player, villager);
    }

    public static void hireHire(EntityPlayer player, MillVillager villager) {
        PacketBuffer data = ClientSender.getPacketBuffer();
        data.writeInt(200);
        data.writeInt(30);
        data.writeLong(villager.getVillagerId());
        ClientSender.createAndSendServerPacket(data);
        GuiActions.hireHire(player, villager);
    }

    public static void hireRelease(EntityPlayer player, MillVillager villager) {
        PacketBuffer data = ClientSender.getPacketBuffer();
        data.writeInt(200);
        data.writeInt(32);
        data.writeLong(villager.getVillagerId());
        ClientSender.createAndSendServerPacket(data);
        GuiActions.hireRelease(player, villager);
    }

    public static void hireToggleStance(EntityPlayer player, boolean stance) {
        PacketBuffer data = ClientSender.getPacketBuffer();
        data.writeInt(200);
        data.writeInt(33);
        data.writeBoolean(stance);
        ClientSender.createAndSendServerPacket(data);
    }

    public static void importTableCreateNewBuilding(Point tablePos, int length, int width, int startLevel, boolean clearGround) {
        PacketBuffer data = ClientSender.getPacketBuffer();
        data.writeInt(200);
        data.writeInt(102);
        StreamReadWrite.writeNullablePoint(tablePos, data);
        data.writeInt(length);
        data.writeInt(width);
        data.writeInt(startLevel);
        data.writeBoolean(clearGround);
        ClientSender.createAndSendServerPacket(data);
    }

    public static void importTableImportBuildingPlan(EntityPlayer player, Point tablePos, String source, String buildingKey, boolean importAll, int variation, int level, int orientation, boolean importMockBlocks) {
        PacketBuffer data = ClientSender.getPacketBuffer();
        data.writeInt(200);
        data.writeInt(100);
        StreamReadWrite.writeNullablePoint(tablePos, data);
        StreamReadWrite.writeNullableString(source, data);
        StreamReadWrite.writeNullableString(buildingKey, data);
        data.writeBoolean(importAll);
        data.writeInt(variation);
        data.writeInt(level);
        data.writeInt(orientation);
        data.writeBoolean(importMockBlocks);
        ClientSender.createAndSendServerPacket(data);
    }

    public static void importTableUpdateSettings(Point tablePos, int upgradeLevel, int orientation, int startingLevel, boolean exportSnow, boolean importMockBlocks, boolean autoconvertToPreserveGround, boolean exportRegularChests) {
        PacketBuffer data = ClientSender.getPacketBuffer();
        data.writeInt(200);
        data.writeInt(101);
        StreamReadWrite.writeNullablePoint(tablePos, data);
        data.writeInt(upgradeLevel);
        data.writeInt(orientation);
        data.writeInt(startingLevel);
        data.writeBoolean(exportSnow);
        data.writeBoolean(importMockBlocks);
        data.writeBoolean(autoconvertToPreserveGround);
        data.writeBoolean(exportRegularChests);
        ClientSender.createAndSendServerPacket(data);
    }

    public static void negationWand(EntityPlayer player, Building townHall) {
        PacketBuffer data = ClientSender.getPacketBuffer();
        data.writeInt(200);
        data.writeInt(40);
        StreamReadWrite.writeNullablePoint(townHall.getPos(), data);
        ClientSender.createAndSendServerPacket(data);
    }

    public static void newBuilding(EntityPlayer player, Building townHall, Point pos, String planKey) {
        PacketBuffer data = ClientSender.getPacketBuffer();
        data.writeInt(200);
        data.writeInt(50);
        StreamReadWrite.writeNullablePoint(townHall.getPos(), data);
        StreamReadWrite.writeNullablePoint(pos, data);
        data.func_180714_a(planKey);
        ClientSender.createAndSendServerPacket(data);
    }

    public static void newCustomBuilding(EntityPlayer player, Building townHall, Point pos, String planKey) {
        PacketBuffer data = ClientSender.getPacketBuffer();
        data.writeInt(200);
        data.writeInt(51);
        StreamReadWrite.writeNullablePoint(townHall.getPos(), data);
        StreamReadWrite.writeNullablePoint(pos, data);
        data.func_180714_a(planKey);
        ClientSender.createAndSendServerPacket(data);
    }

    public static void newVillageCreation(EntityPlayer player, Point pos, String cultureKey, String villageTypeKey) {
        PacketBuffer data = ClientSender.getPacketBuffer();
        data.writeInt(200);
        data.writeInt(20);
        data.func_180714_a(cultureKey);
        data.func_180714_a(villageTypeKey);
        StreamReadWrite.writeNullablePoint(pos, data);
        ClientSender.createAndSendServerPacket(data);
    }

    public static void playerToggleDonation(EntityPlayer player, boolean donation) {
        PacketBuffer data = ClientSender.getPacketBuffer();
        data.writeInt(200);
        data.writeInt(61);
        data.writeBoolean(donation);
        ClientSender.createAndSendServerPacket(data);
    }

    public static void pujasChangeEnchantment(EntityPlayer player, Building temple, int enchantmentId) {
        PacketBuffer data = ClientSender.getPacketBuffer();
        data.writeInt(200);
        data.writeInt(60);
        StreamReadWrite.writeNullablePoint(temple.getPos(), data);
        data.writeShort(enchantmentId);
        ClientSender.createAndSendServerPacket(data);
    }

    public static void questCompleteStep(EntityPlayer player, MillVillager villager) {
        PacketBuffer data = ClientSender.getPacketBuffer();
        data.writeInt(200);
        data.writeInt(10);
        data.writeLong(villager.getVillagerId());
        ClientSender.createAndSendServerPacket(data);
    }

    public static void questRefuse(EntityPlayer player, MillVillager villager) {
        PacketBuffer data = ClientSender.getPacketBuffer();
        data.writeInt(200);
        data.writeInt(11);
        data.writeLong(villager.getVillagerId());
        ClientSender.createAndSendServerPacket(data);
    }

    public static void requestMapInfo(Building townHall) {
        PacketBuffer data = ClientSender.getPacketBuffer();
        data.writeInt(203);
        StreamReadWrite.writeNullablePoint(townHall.getPos(), data);
        ClientSender.createAndSendServerPacket(data);
    }

    public static void sendAvailableContent() {
        PacketBuffer data = ClientSender.getPacketBuffer();
        try {
            data.writeInt(205);
            data.func_180714_a(MillConfigValues.effective_language);
            data.func_180714_a(MillConfigValues.fallback_language);
            data.writeShort(Culture.ListCultures.size());
            for (Culture culture : Culture.ListCultures) {
                culture.writeCultureAvailableContentPacket(data);
            }
        }
        catch (IOException e) {
            MillLog.printException("Error in displayVillageList", e);
        }
        ClientSender.createAndSendServerPacket(data);
    }

    public static void sendPacketToServer(PacketBuffer packetBuffer) {
        FMLProxyPacket proxyPacket = new FMLProxyPacket(packetBuffer, "millenaire");
        Mill.millChannel.sendToServer(proxyPacket);
    }

    public static void sendVersionInfo() {
        PacketBuffer data = ClientSender.getPacketBuffer();
        data.writeInt(202);
        data.func_180714_a("8.1.2");
        ClientSender.createAndSendServerPacket(data);
    }

    public static void updateCustomBuilding(EntityPlayer player, Building building) {
        PacketBuffer data = ClientSender.getPacketBuffer();
        data.writeInt(200);
        data.writeInt(52);
        StreamReadWrite.writeNullablePoint(building.getPos(), data);
        ClientSender.createAndSendServerPacket(data);
    }

    public static void villageChiefPerformBuilding(EntityPlayer player, MillVillager chief, String planKey) {
        PacketBuffer data = ClientSender.getPacketBuffer();
        data.writeInt(200);
        data.writeInt(1);
        data.writeLong(chief.getVillagerId());
        data.func_180714_a(planKey);
        ClientSender.createAndSendServerPacket(data);
    }

    public static void villageChiefPerformCrop(EntityPlayer player, MillVillager chief, String value) {
        PacketBuffer data = ClientSender.getPacketBuffer();
        data.writeInt(200);
        data.writeInt(2);
        data.writeLong(chief.getVillagerId());
        data.func_180714_a(value);
        ClientSender.createAndSendServerPacket(data);
    }

    public static void villageChiefPerformCultureControl(EntityPlayer player, MillVillager chief) {
        PacketBuffer data = ClientSender.getPacketBuffer();
        data.writeInt(200);
        data.writeInt(3);
        data.writeLong(chief.getVillagerId());
        ClientSender.createAndSendServerPacket(data);
    }

    public static void villageChiefPerformDiplomacy(EntityPlayer player, MillVillager chief, Point village, boolean praise) {
        PacketBuffer data = ClientSender.getPacketBuffer();
        data.writeInt(200);
        data.writeInt(4);
        data.writeLong(chief.getVillagerId());
        StreamReadWrite.writeNullablePoint(village, data);
        data.writeBoolean(praise);
        ClientSender.createAndSendServerPacket(data);
        GuiActions.villageChiefPerformDiplomacy(player, chief, village, praise);
    }

    public static void villageChiefPerformHuntingDrop(EntityPlayer player, MillVillager chief, String value) {
        PacketBuffer data = ClientSender.getPacketBuffer();
        data.writeInt(200);
        data.writeInt(6);
        data.writeLong(chief.getVillagerId());
        data.func_180714_a(value);
        ClientSender.createAndSendServerPacket(data);
    }

    public static void villageChiefPerformVillageScroll(EntityPlayer player, MillVillager chief) {
        PacketBuffer data = ClientSender.getPacketBuffer();
        data.writeInt(200);
        data.writeInt(5);
        data.writeLong(chief.getVillagerId());
        ClientSender.createAndSendServerPacket(data);
    }

    public static void villagerInteractSpecial(EntityPlayer player, MillVillager villager) {
        PacketBuffer data = ClientSender.getPacketBuffer();
        data.writeInt(204);
        data.writeLong(villager.getVillagerId());
        ClientSender.createAndSendServerPacket(data);
    }
}

