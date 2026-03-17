package org.dizzymii.millenaire2.client.book;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single page of text content in the Millenaire book system.
 * Ported from org.millenaire.client.book.TextPage (Forge 1.12.2).
 */
public class TextPage {

    public List<TextLine> lines = new ArrayList<>();
    public String title = "";

    public TextPage() {}

    public TextPage(String title) {
        this.title = title;
    }

    public void addLine(TextLine line) {
        lines.add(line);
    }

    public void addLine(String text) {
        lines.add(new TextLine(text));
    }

    // TODO: Implement page layout, word wrapping, image support
}
