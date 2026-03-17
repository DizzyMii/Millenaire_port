package org.dizzymii.millenaire2.client.gui.text;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Travel book screen — paginated display of discovered villages and cultures.
 */
public class GuiTravelBook extends GuiText {

    private final List<List<String>> pages = new ArrayList<>();
    private int currentPage = 0;

    public GuiTravelBook() {
        super(Component.literal("Travel Book"));
    }

    public void addPage(List<String> pageLines) {
        pages.add(new ArrayList<>(pageLines));
    }

    @Override
    protected void init() {
        super.init();
        rebuildPage();

        // Navigation buttons
        addRenderableWidget(Button.builder(Component.literal("<"), btn -> prevPage())
                .bounds(guiLeft + MARGIN, guiTop + BG_HEIGHT - 30, 30, 20).build());
        addRenderableWidget(Button.builder(Component.literal(">"), btn -> nextPage())
                .bounds(guiLeft + MARGIN + 35, guiTop + BG_HEIGHT - 30, 30, 20).build());
    }

    private void rebuildPage() {
        lines.clear();
        if (pages.isEmpty()) {
            addLine("Your travel book is empty.");
            addLine("Discover villages to fill it!");
        } else {
            lines.addAll(pages.get(currentPage));
            addLine("");
            addLine("Page " + (currentPage + 1) + " / " + pages.size());
        }
    }

    private void prevPage() {
        if (currentPage > 0) {
            currentPage--;
            rebuildPage();
        }
    }

    private void nextPage() {
        if (currentPage < pages.size() - 1) {
            currentPage++;
            rebuildPage();
        }
    }
}
