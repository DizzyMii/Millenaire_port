package org.dizzymii.millenaire2.client.gui.text;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import org.dizzymii.millenaire2.network.ClientPacketSender;
import org.dizzymii.millenaire2.network.MillPacketIds;
import org.dizzymii.millenaire2.network.PacketDataHelper;

/**
 * Import table screen — allows importing custom building plans from PNG files.
 */
public class GuiImportTable extends GuiText {

    public GuiImportTable() {
        super(Component.literal("Import Table"));
    }

    @Override
    protected void init() {
        super.init();
        lines.clear();
        addLine("Building Import Table");
        addLine("");
        addLine("Place a building plan PNG file in the");
        addLine("millenaire/custom-plans folder, then");
        addLine("click Import to load it.");
        addLine("");
        addLine("The plan will be validated and added");
        addLine("to your available buildings.");

        int btnY = guiTop + BG_HEIGHT - 55;
        addRenderableWidget(Button.builder(Component.literal("Import"), btn -> importPlan())
                .bounds(guiLeft + MARGIN, btnY, 65, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Create"), btn -> createBuilding())
                .bounds(guiLeft + MARGIN + 70, btnY, 65, 20).build());
    }

    private void importPlan() {
        PacketDataHelper.Writer w = new PacketDataHelper.Writer();
        w.writeString("custom_plan");
        ClientPacketSender.sendGuiAction(MillPacketIds.GUIACTION_IMPORTTABLE_IMPORTBUILDINGPLAN, w);
        onClose();
    }

    private void createBuilding() {
        PacketDataHelper.Writer w = new PacketDataHelper.Writer();
        w.writeString("norman");
        ClientPacketSender.sendGuiAction(MillPacketIds.GUIACTION_IMPORTTABLE_CREATEBUILDING, w);
        onClose();
    }
}
