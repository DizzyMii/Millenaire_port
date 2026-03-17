package org.dizzymii.millenaire2.client.book;

import org.dizzymii.millenaire2.util.Point;
import org.dizzymii.millenaire2.village.Building;
import org.dizzymii.millenaire2.world.MillWorldData;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * Specialized book manager for the travel book (village discovery journal).
 * Generates pages from discovered villages and player reputation data.
 * Ported from org.millenaire.client.book.BookManagerTravelBook (Forge 1.12.2).
 */
public class BookManagerTravelBook {

    private static final int VILLAGES_PER_PAGE = 5;

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
