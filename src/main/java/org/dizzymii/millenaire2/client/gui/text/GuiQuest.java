package org.dizzymii.millenaire2.client.gui.text;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import org.dizzymii.millenaire2.network.ClientPacketHandler;
import org.dizzymii.millenaire2.network.ClientPacketSender;
import org.dizzymii.millenaire2.network.MillPacketIds;
import org.dizzymii.millenaire2.network.PacketDataHelper;

/**
 * Quest display screen — shows quest description, current step, and accept/decline buttons.
 * Reads from ClientPacketHandler.cachedQuest populated by PACKET_QUESTINSTANCE.
 */
public class GuiQuest extends GuiText {

    public GuiQuest() {
        super(Component.literal("Quest"));
    }

    @Override
    protected void init() {
        super.init();
        lines.clear();

        ClientPacketHandler.QuestClientEntry q = ClientPacketHandler.cachedQuest;

        if (q != null) {
            addLine("Quest: " + q.questKey);
            addLine("Step: " + (q.stepIndex + 1) + " / " + q.totalSteps);
            addLine("");
            if (!q.stepLabel.isEmpty()) addLine(q.stepLabel);
            if (!q.stepDescription.isEmpty()) addLine(q.stepDescription);
            addLine("");
            if (q.rewardMoney > 0) addLine("Reward: " + q.rewardMoney + " deniers");
            if (q.rewardReputation > 0) addLine("Reputation: +" + q.rewardReputation);

            int btnY = guiTop + BG_HEIGHT - 55;
            if (q.isOffer) {
                addRenderableWidget(Button.builder(Component.literal("Accept"), btn -> acceptQuest())
                        .bounds(guiLeft + MARGIN, btnY, 65, 20).build());
                addRenderableWidget(Button.builder(Component.literal("Decline"), btn -> declineQuest())
                        .bounds(guiLeft + MARGIN + 70, btnY, 65, 20).build());
            } else {
                addRenderableWidget(Button.builder(Component.literal("Complete"), btn -> completeStep())
                        .bounds(guiLeft + MARGIN, btnY, 75, 20).build());
                addRenderableWidget(Button.builder(Component.literal("Close"), btn -> onClose())
                        .bounds(guiLeft + MARGIN + 80, btnY, 65, 20).build());
            }
        } else {
            addLine("No active quest.");
            addLine("");
            addLine("Speak to a village leader to receive quests.");
        }
    }

    private void acceptQuest() {
        ClientPacketHandler.QuestClientEntry q = ClientPacketHandler.cachedQuest;
        if (q == null) return;
        PacketDataHelper.Writer w = new PacketDataHelper.Writer();
        w.writeString(q.questKey);
        w.writeInt(ClientPacketHandler.cachedQuestVillagerEntityId);
        ClientPacketSender.sendGuiAction(MillPacketIds.GUIACTION_QUEST_COMPLETESTEP, w);
        onClose();
    }

    private void completeStep() {
        ClientPacketHandler.QuestClientEntry q = ClientPacketHandler.cachedQuest;
        if (q == null) return;
        PacketDataHelper.Writer w = new PacketDataHelper.Writer();
        w.writeString(q.questKey);
        w.writeInt(ClientPacketHandler.cachedQuestVillagerEntityId);
        ClientPacketSender.sendGuiAction(MillPacketIds.GUIACTION_QUEST_COMPLETESTEP, w);
        onClose();
    }

    private void declineQuest() {
        ClientPacketHandler.QuestClientEntry q = ClientPacketHandler.cachedQuest;
        if (q == null) return;
        PacketDataHelper.Writer w = new PacketDataHelper.Writer();
        w.writeString(q.questKey);
        w.writeInt(ClientPacketHandler.cachedQuestVillagerEntityId);
        ClientPacketSender.sendGuiAction(MillPacketIds.GUIACTION_QUEST_REFUSE, w);
        onClose();
    }
}
