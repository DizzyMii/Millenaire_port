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

    public TextLine(String text, int colour, boolean bold, boolean italic) {
        this.text = text;
        this.colour = colour;
        this.bold = bold;
        this.italic = italic;
    }

    @javax.annotation.Nullable
    private Runnable clickAction;

    public TextLine withBold() { this.bold = true; return this; }
    public TextLine withItalic() { this.italic = true; return this; }
    public TextLine withUnderline() { this.underline = true; return this; }
    public TextLine withColor(int color) { this.colour = color; return this; }

    public TextLine withClickAction(Runnable action) {
        this.clickAction = action;
        return this;
    }

    public boolean hasClickAction() { return clickAction != null; }

    public void executeClick() {
        if (clickAction != null) clickAction.run();
    }

    public String getFormattedText() {
        StringBuilder sb = new StringBuilder();
        if (bold) sb.append("\u00A7l");
        if (italic) sb.append("\u00A7o");
        if (underline) sb.append("\u00A7n");
        sb.append(text);
        if (bold || italic || underline) sb.append("\u00A7r");
        return sb.toString();
    }
}
