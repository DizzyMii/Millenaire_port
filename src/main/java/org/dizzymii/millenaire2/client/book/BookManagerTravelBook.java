package org.dizzymii.millenaire2.client.book;

import org.dizzymii.millenaire2.client.network.ClientNetworkCache;
import org.dizzymii.millenaire2.util.Point;
import org.dizzymii.millenaire2.village.Building;
import org.dizzymii.millenaire2.world.MillWorldData;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Specialized book manager for the travel book (village discovery journal).
 * Generates pages from discovered villages and player reputation data.
 * Ported from org.millenaire.client.book.BookManagerTravelBook (Forge 1.12.2).
 */
public class BookManagerTravelBook {

    private static final int VILLAGES_PER_PAGE = 6;

    private static String nonNull(@Nullable String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    public static Map<String, List<ClientNetworkCache.VillageListClientEntry>> indexByCulture(
            List<ClientNetworkCache.VillageListClientEntry> entries) {
        Map<String, List<ClientNetworkCache.VillageListClientEntry>> byCulture = new HashMap<>();
        for (ClientNetworkCache.VillageListClientEntry entry : entries) {
            String culture = nonNull(entry.cultureKey, "unknown");
            byCulture.computeIfAbsent(culture, k -> new ArrayList<>()).add(entry);
        }
        for (List<ClientNetworkCache.VillageListClientEntry> cultureEntries : byCulture.values()) {
            cultureEntries.sort(Comparator.comparingInt(v -> v.distance));
        }
        return byCulture;
    }

    public static TextBook generateHomeBook(List<ClientNetworkCache.VillageListClientEntry> cache) {
        TextBook book = new TextBook("travel_book", "Travel Book");

        if (cache.isEmpty()) {
            TextPage empty = new TextPage("Travel Book");
            empty.addLine("Your travel book is empty.");
            empty.addBlankLine();
            empty.addLine("Discover villages to fill it!");
            book.addPage(empty);
            return book;
        }

        Map<String, List<ClientNetworkCache.VillageListClientEntry>> byCulture = indexByCulture(cache);
        List<String> cultures = new ArrayList<>(byCulture.keySet());
        Collections.sort(cultures);

        TextPage summary = new TextPage("Overview");
        summary.addLine("Known places: " + cache.size());
        summary.addLine("Known cultures: " + cultures.size());
        summary.addBlankLine();
        summary.addLine("Closest discovered places:");

        List<ClientNetworkCache.VillageListClientEntry> nearest = new ArrayList<>(cache);
        nearest.sort(Comparator.comparingInt(v -> v.distance));
        int max = Math.min(8, nearest.size());
        for (int i = 0; i < max; i++) {
            ClientNetworkCache.VillageListClientEntry e = nearest.get(i);
            String name = nonNull(e.name, "Unknown");
            String culture = nonNull(e.cultureKey, "unknown");
            summary.addLine("- " + name + " (" + culture + ", " + e.distance + "m)");
        }
        book.addPage(summary);

        TextPage culturesPage = new TextPage("Cultures");
        for (String culture : cultures) {
            List<ClientNetworkCache.VillageListClientEntry> list = byCulture.get(culture);
            if (list == null) continue;
            int villages = 0;
            int lone = 0;
            int nearestDist = Integer.MAX_VALUE;
            for (ClientNetworkCache.VillageListClientEntry e : list) {
                if (e.isLoneBuilding) lone++; else villages++;
                nearestDist = Math.min(nearestDist, e.distance);
            }
            culturesPage.addLine(culture + ": " + list.size() + " discovered");
            culturesPage.addLine("  villages=" + villages + ", lone=" + lone + ", nearest=" + nearestDist + "m");
        }
        book.addPage(culturesPage);

        return book;
    }

    public static TextBook generateCultureBook(String cultureKey, List<ClientNetworkCache.VillageListClientEntry> cache) {
        String culture = nonNull(cultureKey, "unknown");
        TextBook book = new TextBook("travel_book_culture_" + culture, "Travel Book - " + culture);

        List<ClientNetworkCache.VillageListClientEntry> filtered = new ArrayList<>();
        for (ClientNetworkCache.VillageListClientEntry e : cache) {
            if (culture.equalsIgnoreCase(nonNull(e.cultureKey, "unknown"))) {
                filtered.add(e);
            }
        }

        filtered.sort(Comparator.comparingInt(v -> v.distance));

        if (filtered.isEmpty()) {
            TextPage p = new TextPage(culture);
            p.addLine("No discovered places for this culture.");
            book.addPage(p);
            return book;
        }

        TextPage currentPage = new TextPage(culture + " Places");
        int count = 0;
        for (ClientNetworkCache.VillageListClientEntry e : filtered) {
            String name = nonNull(e.name, "Unknown");
            String type = e.isLoneBuilding ? "Lone building" : "Village";
            currentPage.addLine(name, 0x1A0D00);
            currentPage.addLine("  Type: " + type);
            currentPage.addLine("  Distance: " + e.distance + "m");
            if (e.pos != null) {
                var p = e.pos;
                currentPage.addLine("  Coordinates: (" + p.x + ", " + p.y + ", " + p.z + ")");
            }
            currentPage.addBlankLine();

            count++;
            if (count >= VILLAGES_PER_PAGE) {
                book.addPage(currentPage);
                currentPage = new TextPage(culture + " Places (cont.)");
                count = 0;
            }
        }

        if (count > 0 || book.getPageCount() == 0) {
            book.addPage(currentPage);
        }

        return book;
    }

    public static TextBook generateVillageDetailBook(ClientNetworkCache.VillageListClientEntry entry) {
        String name = nonNull(entry.name, "Unknown");
        String culture = nonNull(entry.cultureKey, "unknown");
        String type = entry.isLoneBuilding ? "Lone building" : "Village";

        TextBook book = new TextBook("travel_book_place_" + name.toLowerCase(), name);
        TextPage page = new TextPage(name);
        page.addLine("Culture: " + culture);
        page.addLine("Type: " + type);
        page.addLine("Distance: " + entry.distance + "m");
        if (entry.pos != null) {
            var p = entry.pos;
            page.addLine("Coordinates: (" + p.x + ", " + p.y + ", " + p.z + ")");
        }
        page.addBlankLine();
        page.addLine("Travel Notes:");
        page.addLine("- Build reputation with this culture to unlock more content.");
        page.addLine("- Use the trade interface to improve village relations.");
        book.addPage(page);
        return book;
    }

    /**
     * Generates a travel book from all known buildings in the world data.
     */
    public static TextBook generateTravelBook(@Nullable MillWorldData worldData) {
        TextBook book = new TextBook("travel_book", "Travel Book");

        if (worldData == null) {
            TextPage empty = new TextPage("Travel Book");
            empty.addLine("Your travel book is empty.");
            empty.addBlankLine();
            empty.addLine("Discover villages to fill it!");
            book.addPage(empty);
            return book;
        }

        Collection<Building> buildings = worldData.allBuildings();
        if (buildings.isEmpty()) {
            TextPage empty = new TextPage("Travel Book");
            empty.addLine("No villages discovered yet.");
            book.addPage(empty);
            return book;
        }

        TextPage currentPage = new TextPage("Discovered Villages");
        int count = 0;

        for (Building b : buildings) {
            if (!b.isTownhall) continue;

            String name = b.getName() != null ? b.getName() : "Unknown";
            String culture = b.cultureKey != null ? b.cultureKey : "Unknown";
            Point pos = b.getPos();
            String posStr = pos != null ? "(" + pos.x + ", " + pos.z + ")" : "(?)";

            currentPage.addLine(name, 0x1A0D00);
            currentPage.addLine("  Culture: " + culture);
            currentPage.addLine("  Location: " + posStr);
            currentPage.addBlankLine();
            count++;

            if (count >= VILLAGES_PER_PAGE) {
                book.addPage(currentPage);
                currentPage = new TextPage("Villages (cont.)");
                count = 0;
            }
        }

        if (count > 0 || book.getPageCount() == 0) {
            book.addPage(currentPage);
        }

        return book;
    }
}
