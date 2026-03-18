package org.dizzymii.millenaire2.client.gui.text;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import org.dizzymii.millenaire2.culture.Culture;
import org.dizzymii.millenaire2.network.ClientPacketSender;
import org.dizzymii.millenaire2.network.MillPacketIds;
import org.dizzymii.millenaire2.network.PacketDataHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * New village creation screen — used with the Wand of Summoning to pick
 * a culture and village type to spawn.
 */
public class GuiNewVillage extends GuiText {

    private final List<String> cultureKeys = new ArrayList<>();
    private int selectedCulture = 0;

    public GuiNewVillage() {
        super(Component.literal("Summon Village"));
    }

    @Override
    protected void init() {
        super.init();
        lines.clear();
        cultureKeys.clear();
        cultureKeys.addAll(Culture.getAllCultureKeys());

        addLine("Wand of Summoning");
        addLine("");
        addLine("Select a culture to summon a village:");
        addLine("");
        for (int i = 0; i < cultureKeys.size(); i++) {
            String marker = (i == selectedCulture) ? "> " : "  ";
            addLine(marker + cultureKeys.get(i));
        }

        int btnY = guiTop + BG_HEIGHT - 55;
        addRenderableWidget(Button.builder(Component.literal("\u25B2"), btn -> selectPrev())
                .bounds(guiLeft + MARGIN, btnY, 20, 20).build());
        addRenderableWidget(Button.builder(Component.literal("\u25BC"), btn -> selectNext())
                .bounds(guiLeft + MARGIN + 24, btnY, 20, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Summon"), btn -> summonVillage())
                .bounds(guiLeft + MARGIN + 50, btnY, 75, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Cancel"), btn -> onClose())
                .bounds(guiLeft + MARGIN + 130, btnY, 75, 20).build());
    }

    private void selectPrev() {
        if (!cultureKeys.isEmpty()) {
            selectedCulture = (selectedCulture - 1 + cultureKeys.size()) % cultureKeys.size();
            clearWidgets();
            init();
        }
    }

    private void selectNext() {
        if (!cultureKeys.isEmpty()) {
            selectedCulture = (selectedCulture + 1) % cultureKeys.size();
            clearWidgets();
            init();
        }
    }

    private void summonVillage() {
        if (cultureKeys.isEmpty()) return;
        String culture = cultureKeys.get(selectedCulture);
        PacketDataHelper.Writer w = new PacketDataHelper.Writer();
        w.writeString(culture);
        ClientPacketSender.sendGuiAction(MillPacketIds.GUIACTION_NEWVILLAGE, w);
        onClose();
    }
}
