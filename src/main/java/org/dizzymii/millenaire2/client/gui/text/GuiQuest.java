package org.dizzymii.millenaire2.client.gui.text;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import org.dizzymii.millenaire2.network.ClientPacketSender;
import org.dizzymii.millenaire2.network.MillPacketIds;
import org.dizzymii.millenaire2.network.PacketDataHelper;
import org.dizzymii.millenaire2.quest.QuestInstance;

import javax.annotation.Nullable;

/**
 * Quest display screen — shows quest description, current step, and accept/decline buttons.
 */
public class GuiQuest extends GuiText {

    @Nullable private final QuestInstance questInstance;

    public GuiQuest(@Nullable QuestInstance qi) {
        super(Component.literal("Quest"));
        this.questInstance = qi;
    }

    public GuiQuest() { this(null); }

    @Override
    protected void init() {
        super.init();
        lines.clear();

        if (questInstance != null && questInstance.quest != null) {
            addLine("Quest: " + questInstance.quest.key);
            addLine("Step: " + (questInstance.currentStep + 1) + " / " + questInstance.quest.steps.size());
            addLine("");
            if (questInstance.currentStep < questInstance.quest.steps.size()) {
                String desc = questInstance.quest.steps.get(questInstance.currentStep)
                        .getDescription("en");
                addLine(desc != null ? desc : "Continue your quest...");
            }
        } else {
            addLine("No active quest.");
            addLine("");
            addLine("Speak to a village leader to receive quests.");
        }

        // Accept/Decline buttons only if quest is offered but not yet started
        if (questInstance == null) {
            int btnY = guiTop + BG_HEIGHT - 55;
            addRenderableWidget(Button.builder(Component.literal("Accept"), btn -> acceptQuest())
                    .bounds(guiLeft + MARGIN, btnY, 65, 20).build());
            addRenderableWidget(Button.builder(Component.literal("Decline"), btn -> onClose())
                    .bounds(guiLeft + MARGIN + 70, btnY, 65, 20).build());
        }
    }

    private void acceptQuest() {
        PacketDataHelper.Writer w = new PacketDataHelper.Writer();
        w.writeLong(questInstance != null ? questInstance.uniqueid : -1);
        ClientPacketSender.sendGuiAction(MillPacketIds.GUIACTION_QUEST_COMPLETESTEP, w.toByteArray());
        onClose();
    }
}
