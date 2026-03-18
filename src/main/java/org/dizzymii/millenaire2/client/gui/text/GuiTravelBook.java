package org.dizzymii.millenaire2.client.gui.text;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import org.dizzymii.millenaire2.network.ClientPacketHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Travel book screen — paginated display of discovered villages and cultures.
 * Auto-populates from ClientPacketHandler.villageListCache.
 */
public class GuiTravelBook extends GuiText {

    private static final int ENTRIES_PER_PAGE = 6;
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
        buildPagesFromCache();
        rebuildPage();

        // Navigation buttons
        addRenderableWidget(Button.builder(Component.literal("<"), btn -> prevPage())
                .bounds(guiLeft + MARGIN, guiTop + BG_HEIGHT - 30, 30, 20).build());
        addRenderableWidget(Button.builder(Component.literal(">"), btn -> nextPage())
                .bounds(guiLeft + MARGIN + 35, guiTop + BG_HEIGHT - 30, 30, 20).build());
    }

    private void buildPagesFromCache() {
        pages.clear();
        currentPage = 0;
        List<ClientPacketHandler.VillageListClientEntry> cache = ClientPacketHandler.villageListCache;
        if (cache.isEmpty()) return;

        List<String> currentPageLines = new ArrayList<>();
        int count = 0;
        for (ClientPacketHandler.VillageListClientEntry v : cache) {
            String name = v.name != null ? v.name : "Unknown";
            String culture = v.cultureKey != null ? v.cultureKey : "unknown";
            String type = v.isLoneBuilding ? "Lone building" : "Village";
            currentPageLines.add("\u00a76" + name + "\u00a7r (" + culture + ")");
            currentPageLines.add("  " + type + " — " + v.distance + "m away");
            currentPageLines.add("");
            count++;
            if (count % ENTRIES_PER_PAGE == 0) {
                pages.add(currentPageLines);
                currentPageLines = new ArrayList<>();
            }
        }
        if (!currentPageLines.isEmpty()) {
            pages.add(currentPageLines);
        }
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
