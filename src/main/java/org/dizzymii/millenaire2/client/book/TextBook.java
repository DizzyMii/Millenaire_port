package org.dizzymii.millenaire2.client.book;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an in-game book containing multiple pages of formatted text.
 * Ported from org.millenaire.client.book.TextBook (Forge 1.12.2).
 */
public class TextBook {

    public String bookId = "";
    public String title = "";
    public List<TextPage> pages = new ArrayList<>();
    public int currentPage = 0;

    public TextBook() {}

    public TextBook(String bookId, String title) {
        this.bookId = bookId;
        this.title = title;
    }

    public void addPage(TextPage page) {
        pages.add(page);
    }

    public TextPage getCurrentPage() {
        if (currentPage >= 0 && currentPage < pages.size()) {
            return pages.get(currentPage);
        }
        return null;
    }

    public boolean hasNextPage() { return currentPage < pages.size() - 1; }
    public boolean hasPrevPage() { return currentPage > 0; }

    public void nextPage() {
        if (hasNextPage()) currentPage++;
    }

    public void prevPage() {
        if (hasPrevPage()) currentPage--;
    }

    public void goToPage(int page) {
        if (page >= 0 && page < pages.size()) currentPage = page;
    }

    public int getPageCount() { return pages.size(); }

    /**
     * Generates a table of contents page from page titles.
     */
    public TextPage generateTableOfContents() {
        TextPage toc = new TextPage("Table of Contents");
        for (int i = 0; i < pages.size(); i++) {
            TextPage p = pages.get(i);
            String entry = (i + 1) + ". " + (p.title.isEmpty() ? "Page " + (i + 1) : p.title);
            toc.addLine(entry);
        }
        return toc;
    }

    /**
     * Searches all pages for a text string and returns the first matching page index, or -1.
     */
    public int search(String query) {
        String lower = query.toLowerCase();
        for (int i = 0; i < pages.size(); i++) {
            for (TextLine line : pages.get(i).lines) {
                if (line.text.toLowerCase().contains(lower)) return i;
            }
        }
        return -1;
    }
}
