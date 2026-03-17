package org.dizzymii.millenaire2.client.book;

/**
 * Represents a single line of formatted text in a book page.
 * Ported from org.millenaire.client.book.TextLine (Forge 1.12.2).
 */
public class TextLine {

    public String text = "";
    public int colour = 0x000000;
    public boolean bold = false;
    public boolean italic = false;
    public boolean underline = false;

    public TextLine() {}

    public TextLine(String text) {
        this.text = text;
    }

    public TextLine(String text, int colour) {
        this.text = text;
        this.colour = colour;
    }

    // TODO: Implement formatting helpers, click handlers
}
