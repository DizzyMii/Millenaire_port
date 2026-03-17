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

    // TODO: Implement book content loading from lang/resource files, culture-specific books
}
