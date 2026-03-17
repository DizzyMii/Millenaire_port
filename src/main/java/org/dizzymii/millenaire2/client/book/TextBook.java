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

    // TODO: Implement page navigation, table of contents, search
}
