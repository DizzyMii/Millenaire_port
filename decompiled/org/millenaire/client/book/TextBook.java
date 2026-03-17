/*
 * Decompiled with CFR 0.152.
 */
package org.millenaire.client.book;

import java.util.ArrayList;
import java.util.List;
import org.millenaire.client.book.TextLine;
import org.millenaire.client.book.TextPage;

public class TextBook {
    private final List<TextPage> pages;

    public static TextBook convertLinesToBook(List<List<TextLine>> linesList) {
        ArrayList<TextPage> pages = new ArrayList<TextPage>();
        for (List<TextLine> lines : linesList) {
            pages.add(new TextPage(lines));
        }
        return new TextBook(pages);
    }

    public static TextBook convertStringsToBook(List<List<String>> stringsList) {
        ArrayList<TextPage> pages = new ArrayList<TextPage>();
        for (List<String> strings : stringsList) {
            pages.add(TextPage.convertStringsToPage(strings));
        }
        return new TextBook(pages);
    }

    public TextBook() {
        this.pages = new ArrayList<TextPage>();
    }

    public TextBook(List<TextPage> pages) {
        this.pages = pages;
    }

    public void addBook(TextBook book) {
        for (TextPage page : book.getPages()) {
            this.pages.add(page);
        }
    }

    public void addPage(TextPage page) {
        this.pages.add(page);
    }

    public TextPage getPage(int pos) {
        return this.pages.get(pos);
    }

    public List<TextPage> getPages() {
        return this.pages;
    }

    public int nbPages() {
        return this.pages.size();
    }
}

