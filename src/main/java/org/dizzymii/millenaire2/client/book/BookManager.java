package org.dizzymii.millenaire2.client.book;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages loading and caching of in-game books (village parchment, help, etc.).
 * Ported from org.millenaire.client.book.BookManager (Forge 1.12.2).
 */
public class BookManager {

    private static Map<String, TextBook> books = new HashMap<>();

    public static void loadBooks() {
        // TODO: Load book content from resource files
    }

    public static TextBook getBook(String bookId) {
        return books.get(bookId);
    }

    public static void registerBook(String bookId, TextBook book) {
        books.put(bookId, book);
    }

    public static boolean hasBook(String bookId) {
        return books.containsKey(bookId);
    }

    public static void clearBooks() {
        books.clear();
    }

    /**
     * Loads all default books (help, culture info) from embedded content.
     */
    public static void loadBooks() {
        // Help book
        TextBook help = new TextBook("help", "Millenaire Help");
        TextPage p1 = new TextPage("Getting Started");
        p1.addLine("Welcome to Millenaire!");
        p1.addBlankLine();
        p1.addLine("Explore the world to discover villages");
        p1.addLine("from many different cultures.");
        p1.addBlankLine();
        p1.addLine("Trade with villagers to gain reputation");
        p1.addLine("and unlock new buildings and quests.");
        help.addPage(p1);

        TextPage p2 = new TextPage("Trading");
        p2.addLine("Right-click a villager to trade.");
        p2.addBlankLine();
        p2.addLine("Buy items using Deniers (the Millenaire");
        p2.addLine("currency). Sell resources to earn Deniers.");
        p2.addBlankLine();
        p2.addLine("Trading increases your reputation with");
        p2.addLine("the village's culture.");
        help.addPage(p2);

        TextPage p3 = new TextPage("Village Growth");
        p3.addLine("Villages grow as they gather resources.");
        p3.addBlankLine();
        p3.addLine("New buildings are constructed over time.");
        p3.addLine("You can donate resources to speed up");
        p3.addLine("construction and earn extra reputation.");
        help.addPage(p3);

        registerBook("help", help);
    }

    /**
     * Creates a culture-specific information book.
     */
    public static TextBook createCultureBook(String cultureKey, String cultureName) {
        TextBook book = new TextBook(cultureKey + "_info", cultureName);
        TextPage intro = new TextPage(cultureName);
        intro.addLine("Culture: " + cultureName);
        intro.addBlankLine();
        intro.addLine("Learn about the " + cultureName);
        intro.addLine("culture and their buildings.");
        book.addPage(intro);
        return book;
    }
}
