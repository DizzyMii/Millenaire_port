package org.dizzymii.millenaire2.client.gui.text;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import org.dizzymii.millenaire2.client.book.BookManagerTravelBook;
import org.dizzymii.millenaire2.client.book.TextBook;
import org.dizzymii.millenaire2.client.book.TextPage;
import org.dizzymii.millenaire2.client.book.TextLine;
import org.dizzymii.millenaire2.client.book.TravelBookExporter;
import org.dizzymii.millenaire2.client.network.ClientNetworkCache;
import org.dizzymii.millenaire2.network.ClientPacketSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Travel book screen — paginated display of discovered villages and cultures.
 * Auto-populates from ClientNetworkCache.villageListCache.
 */
public class GuiTravelBook extends GuiText {

    private enum Mode {
        HOME,
        CULTURE_LIST,
        CULTURE_DETAIL,
        PLACE_DETAIL
    }

    private final List<TextPage> pages = new ArrayList<>();
    private int currentPage = 0;
    private Mode mode = Mode.HOME;
    private String selectedCulture = "";
    private ClientNetworkCache.VillageListClientEntry selectedEntry = null;

    public GuiTravelBook() {
        super(Component.literal("Travel Book"));
    }

    @Override
    protected void init() {
        super.init();
        ClientPacketSender.sendVillageListRequest();
        rebuildBookForMode();
        rebuildPage();
        rebuildButtons();
    }

    private void rebuildBookForMode() {
        pages.clear();
        TextBook book;
        if (mode == Mode.HOME) {
            book = BookManagerTravelBook.generateHomeBook(ClientNetworkCache.villageListCache);
        } else if (mode == Mode.CULTURE_LIST) {
            TextBook listBook = new TextBook("travel_book_cultures", "Travel Book - Cultures");
            TextPage page = new TextPage("Cultures");
            Map<String, List<ClientNetworkCache.VillageListClientEntry>> byCulture =
                    BookManagerTravelBook.indexByCulture(ClientNetworkCache.villageListCache);
            List<String> cultures = new ArrayList<>(byCulture.keySet());
            Collections.sort(cultures);
            if (cultures.isEmpty()) {
                page.addLine("No cultures discovered yet.");
            } else {
                page.addLine("Choose a culture with ◀ / ▶, then open it.");
                page.addBlankLine();
                for (String culture : cultures) {
                    int size = byCulture.getOrDefault(culture, List.of()).size();
                    page.addLine("- " + culture + " (" + size + ")");
                }
            }
            listBook.addPage(page);
            book = listBook;
        } else if (mode == Mode.CULTURE_DETAIL) {
            book = BookManagerTravelBook.generateCultureBook(selectedCulture, ClientNetworkCache.villageListCache);
        } else {
            if (selectedEntry != null) {
                book = BookManagerTravelBook.generateVillageDetailBook(selectedEntry);
            } else {
                book = BookManagerTravelBook.generateHomeBook(ClientNetworkCache.villageListCache);
                mode = Mode.HOME;
            }
        }

        pages.addAll(book.pages);
        if (currentPage >= pages.size()) {
            currentPage = Math.max(0, pages.size() - 1);
        }
    }

    private void rebuildButtons() {
        int navY = guiTop + BG_HEIGHT - 30;

        addRenderableWidget(Button.builder(Component.literal("<"), btn -> prevPage())
                .bounds(guiLeft + MARGIN, navY, 24, 20).build());
        addRenderableWidget(Button.builder(Component.literal(">"), btn -> nextPage())
                .bounds(guiLeft + MARGIN + 26, navY, 24, 20).build());

        if (mode == Mode.HOME) {
            addRenderableWidget(Button.builder(Component.literal("Cultures"), btn -> {
                        mode = Mode.CULTURE_LIST;
                        currentPage = 0;
                        clearWidgets();
                        init();
                    })
                    .bounds(guiLeft + MARGIN + 54, navY, 58, 20).build());
            addRenderableWidget(Button.builder(Component.literal("Refresh"), btn -> {
                        ClientPacketSender.sendVillageListRequest();
                        rebuildBookForMode();
                        rebuildPage();
                    })
                    .bounds(guiLeft + MARGIN + 114, navY, 56, 20).build());
        }

        if (mode == Mode.CULTURE_LIST) {
            addRenderableWidget(Button.builder(Component.literal("Open"), btn -> openSelectedCulture())
                    .bounds(guiLeft + MARGIN + 54, navY, 48, 20).build());
            addRenderableWidget(Button.builder(Component.literal("Back"), btn -> {
                        mode = Mode.HOME;
                        currentPage = 0;
                        clearWidgets();
                        init();
                    })
                    .bounds(guiLeft + MARGIN + 104, navY, 48, 20).build());
        }

        if (mode == Mode.CULTURE_DETAIL) {
            addRenderableWidget(Button.builder(Component.literal("Places"), btn -> openSelectedPlace())
                    .bounds(guiLeft + MARGIN + 54, navY, 50, 20).build());
            addRenderableWidget(Button.builder(Component.literal("Back"), btn -> {
                        mode = Mode.CULTURE_LIST;
                        currentPage = 0;
                        clearWidgets();
                        init();
                    })
                    .bounds(guiLeft + MARGIN + 106, navY, 48, 20).build());
        }

        if (mode == Mode.PLACE_DETAIL) {
            addRenderableWidget(Button.builder(Component.literal("Back"), btn -> {
                        mode = Mode.CULTURE_DETAIL;
                        currentPage = 0;
                        clearWidgets();
                        init();
                    })
                    .bounds(guiLeft + MARGIN + 54, navY, 48, 20).build());
        }

        addRenderableWidget(Button.builder(Component.literal("Export"), btn -> exportCurrentBook())
                .bounds(guiLeft + BG_WIDTH - 120, navY, 52, 20).build());
    }

    private void rebuildPage() {
        lines.clear();
        if (pages.isEmpty()) {
            addLine("Your travel book is empty.");
            addLine("Discover villages to fill it!");
        } else {
            TextPage page = pages.get(currentPage);
            if (page.title != null && !page.title.isEmpty()) {
                addLine(page.title);
                addLine("");
            }
            for (TextLine line : page.lines) {
                addLine(line.getFormattedText());
            }
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

    private void openSelectedCulture() {
        Map<String, List<ClientNetworkCache.VillageListClientEntry>> byCulture =
                BookManagerTravelBook.indexByCulture(ClientNetworkCache.villageListCache);
        List<String> cultures = new ArrayList<>(byCulture.keySet());
        Collections.sort(cultures);
        if (cultures.isEmpty()) return;
        int idx = Math.max(0, Math.min(currentPage, cultures.size() - 1));
        selectedCulture = cultures.get(idx);
        mode = Mode.CULTURE_DETAIL;
        currentPage = 0;
        clearWidgets();
        init();
    }

    private void openSelectedPlace() {
        if (selectedCulture == null || selectedCulture.isBlank()) return;
        List<ClientNetworkCache.VillageListClientEntry> filtered = new ArrayList<>();
        for (ClientNetworkCache.VillageListClientEntry e : ClientNetworkCache.villageListCache) {
            String culture = e.cultureKey == null ? "unknown" : e.cultureKey;
            if (selectedCulture.equalsIgnoreCase(culture)) filtered.add(e);
        }
        filtered.sort(java.util.Comparator.comparingInt(v -> v.distance));
        if (filtered.isEmpty()) return;
        int idx = Math.max(0, Math.min(currentPage * 6, filtered.size() - 1));
        selectedEntry = filtered.get(idx);
        mode = Mode.PLACE_DETAIL;
        currentPage = 0;
        clearWidgets();
        init();
    }

    private void exportCurrentBook() {
        TextBook exportBook = new TextBook("travel_book_export", "Travel Book Export");
        exportBook.pages.addAll(this.pages);
        exportBook.currentPage = this.currentPage;

        java.io.File outputDir = new java.io.File(
                org.dizzymii.millenaire2.util.MillCommonUtilities.getMillenaireDir(), "exports");
        String baseName = "travel_book_" + mode.name().toLowerCase();
        boolean ok = TravelBookExporter.exportBookSnapshot(exportBook, outputDir, baseName);

        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            mc.player.displayClientMessage(Component.literal(
                    ok ? "[Millénaire] Travel book exported." : "[Millénaire] Travel book export failed."), true);
        }
    }
}
