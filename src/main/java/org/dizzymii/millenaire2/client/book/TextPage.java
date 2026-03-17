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

    public void addLine(String text, int colour) {
        lines.add(new TextLine(text, colour));
    }

    public void addBlankLine() {
        lines.add(new TextLine(""));
    }

    public int getLineCount() { return lines.size(); }

    /**
     * Word-wraps a long string into multiple TextLines that fit within maxWidth characters.
     */
    public void addWrappedText(String text, int maxCharsPerLine) {
        if (text == null || text.isEmpty()) {
            addBlankLine();
            return;
        }
        String[] words = text.split(" ");
        StringBuilder current = new StringBuilder();
        for (String word : words) {
            if (current.length() + word.length() + 1 > maxCharsPerLine) {
                lines.add(new TextLine(current.toString().trim()));
                current = new StringBuilder();
            }
            current.append(word).append(" ");
        }
        if (current.length() > 0) {
            lines.add(new TextLine(current.toString().trim()));
        }
    }
}
