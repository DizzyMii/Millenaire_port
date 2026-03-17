/*
 * Decompiled with CFR 0.152.
 */
package org.millenaire.client.book;

import java.util.ArrayList;
import java.util.List;
import org.millenaire.client.book.TextBook;
import org.millenaire.client.book.TextLine;
import org.millenaire.client.book.TextPage;
import org.millenaire.common.utilities.MillLog;

public class BookManager {
    protected IFontRendererWrapper fontRendererWrapper;
    protected int lineSizeInPx;
    private final int textHeight;
    private final int xSize;
    private final int ySize;
    private final int textXStart;

    private static String getStringUpToSize(IFontRendererWrapper fontRendererWrapper, String input, int lineWidthInPx) {
        String output = "";
        for (int charPos = 0; fontRendererWrapper.getStringWidth(output) < lineWidthInPx && charPos < input.length(); ++charPos) {
            output = output + input.substring(charPos, charPos + 1);
        }
        return output;
    }

    public static List<TextLine> mergeColumns(List<TextLine> leftColumn, List<TextLine> rightColumn) {
        ArrayList<TextLine> lines = new ArrayList<TextLine>();
        for (int i = 0; i < Math.max(leftColumn.size(), rightColumn.size()); ++i) {
            TextLine col1 = i < leftColumn.size() ? leftColumn.get(i) : new TextLine();
            TextLine col2 = i < rightColumn.size() ? rightColumn.get(i) : new TextLine();
            lines.add(new TextLine(col1, col2));
        }
        for (TextLine line : lines) {
            line.exportTwoColumns = true;
        }
        return lines;
    }

    public static List<TextLine> splitInColumns(List<TextLine> lines, int nbColumns) {
        ArrayList<TextLine> splitLines = new ArrayList<TextLine>();
        for (int i = 0; i < lines.size(); i += nbColumns) {
            TextLine[] columns = new TextLine[nbColumns];
            for (int col = 0; col < nbColumns; ++col) {
                columns[col] = i + col < lines.size() ? lines.get(i + col) : new TextLine();
            }
            splitLines.add(new TextLine(columns));
        }
        return splitLines;
    }

    public static List<String> splitStringByLength(IFontRendererWrapper fontRendererWrapper, String string, int lineSize) {
        if (lineSize < 5) {
            MillLog.printException("Request to split string to size: " + lineSize, new Exception());
            ArrayList<String> splitStrings = new ArrayList<String>();
            splitStrings.add(string);
            return splitStrings;
        }
        ArrayList<String> splitStrings = new ArrayList<String>();
        if (string == null) {
            return splitStrings;
        }
        if (string.trim().length() == 0) {
            splitStrings.add("");
            return splitStrings;
        }
        if (!fontRendererWrapper.isAvailable()) {
            splitStrings.add(string);
            return splitStrings;
        }
        while (fontRendererWrapper.getStringWidth(string) > lineSize) {
            String fittedString = BookManager.getStringUpToSize(fontRendererWrapper, string, lineSize);
            int end = fittedString.lastIndexOf(32);
            if (end < 1) {
                end = fittedString.length();
            }
            String subLine = string.substring(0, end);
            string = string.substring(subLine.length()).trim();
            int colPos = subLine.lastIndexOf(167);
            if (colPos > -1) {
                string = subLine.substring(colPos, colPos + 2) + string;
            }
            splitStrings.add(subLine);
        }
        if (string.trim().length() > 0) {
            splitStrings.add(string.trim());
        }
        return splitStrings;
    }

    public BookManager(int xSize, int ySize, int textHeight, int lineSizeInPx, IFontRendererWrapper fontRenderer) {
        this.xSize = xSize;
        this.ySize = ySize;
        this.textHeight = textHeight;
        this.lineSizeInPx = lineSizeInPx;
        this.fontRendererWrapper = fontRenderer;
        this.textXStart = 8;
    }

    public BookManager(int xSize, int ySize, int textHeight, int lineSizeInPx, int textXStart, IFontRendererWrapper fontRenderer) {
        this.xSize = xSize;
        this.ySize = ySize;
        this.textHeight = textHeight;
        this.lineSizeInPx = lineSizeInPx;
        this.fontRendererWrapper = fontRenderer;
        this.textXStart = textXStart;
    }

    public TextBook adjustTextBookLineLength(TextBook baseText) {
        TextBook adjustedBook = new TextBook();
        for (TextPage page : baseText.getPages()) {
            TextPage newPage = new TextPage();
            for (TextLine line : page.getLines()) {
                if (line.buttons != null || line.textField != null) {
                    newPage.addLine(line);
                    continue;
                }
                if (line.columns != null) {
                    int lineSize = this.getLineSizeInPx() - line.getTextMarginLeft() - line.getLineMarginLeft() - line.getLineMarginRight();
                    int colSize = (lineSize - (line.columns.length - 1) * 10) / line.columns.length;
                    ArrayList<List<String>> splitColumnText = new ArrayList<List<String>>();
                    int maxNbLines = 0;
                    for (TextLine column : line.columns) {
                        int adjustedColSize = colSize - column.getTextMarginLeft() - column.getLineMarginLeft() - column.getLineMarginRight();
                        List<String> splitStrings = BookManager.splitStringByLength(this.fontRendererWrapper, column.text, adjustedColSize);
                        splitColumnText.add(splitStrings);
                        if (splitStrings.size() <= maxNbLines) continue;
                        maxNbLines = splitStrings.size();
                    }
                    for (int splitLinePos = 0; splitLinePos < maxNbLines; ++splitLinePos) {
                        TextLine newLine = new TextLine("", line, splitLinePos);
                        TextLine[] newColumns = new TextLine[line.columns.length];
                        for (int colPos = 0; colPos < line.columns.length; ++colPos) {
                            newColumns[colPos] = splitLinePos < ((List)splitColumnText.get(colPos)).size() ? new TextLine((String)((List)splitColumnText.get(colPos)).get(splitLinePos), line.columns[colPos], splitLinePos) : new TextLine("", line.columns[colPos], splitLinePos);
                            if (line.columns[colPos].referenceButton == null || splitLinePos != 0) continue;
                            newColumns[colPos].referenceButton = line.columns[colPos].referenceButton;
                        }
                        newLine.columns = newColumns;
                        newPage.addLine(newLine);
                    }
                    continue;
                }
                for (String l : line.text.split("<ret>")) {
                    int i;
                    int lineSize = this.getLineSizeInPx() - line.getTextMarginLeft() - line.getLineMarginLeft() - line.getLineMarginRight();
                    List<String> splitStrings = BookManager.splitStringByLength(this.fontRendererWrapper, l, lineSize);
                    for (i = 0; i < splitStrings.size(); ++i) {
                        newPage.addLine(new TextLine(splitStrings.get(i), line, i));
                        if (line.referenceButton == null || i != 0) continue;
                        newPage.getLastLine().referenceButton = line.referenceButton;
                    }
                    if (line.icons == null) continue;
                    for (i = splitStrings.size(); i < line.icons.size() / 2; ++i) {
                        newPage.addLine(new TextLine("", line, i));
                    }
                }
            }
            while (newPage.getPageHeight() > this.getTextHeight()) {
                TextPage newPage2 = new TextPage();
                int nblinetaken = 0;
                for (int linePos = 0; linePos < newPage.getNbLines(); ++linePos) {
                    int blockSize = 0;
                    for (int nextLinePos = linePos; nextLinePos < newPage.getNbLines(); ++nextLinePos) {
                        blockSize += newPage.getLine(nextLinePos).getLineHeight();
                        if (newPage.getLine((int)nextLinePos).canCutAfter) break;
                    }
                    if (newPage2.getPageHeight() + blockSize > this.getTextHeight() && blockSize < this.getTextHeight()) break;
                    newPage2.addLine(newPage.getLine(linePos));
                    ++nblinetaken;
                }
                for (int i = 0; i < nblinetaken; ++i) {
                    newPage.removeLine(0);
                }
                if ((newPage2 = this.clearEmptyLines(newPage2)) == null) continue;
                adjustedBook.addPage(newPage2);
            }
            TextPage adjustedPage = this.clearEmptyLines(newPage);
            if (adjustedPage == null) continue;
            adjustedBook.addPage(adjustedPage);
        }
        return adjustedBook;
    }

    private TextPage clearEmptyLines(TextPage page) {
        TextPage clearedPage = new TextPage();
        boolean nonEmptyLine = false;
        for (TextLine line : page.getLines()) {
            if (!line.empty()) {
                clearedPage.addLine(line);
                nonEmptyLine = true;
                continue;
            }
            if (!nonEmptyLine) continue;
            clearedPage.addLine(line);
        }
        if (clearedPage.getNbLines() > 0) {
            return clearedPage;
        }
        return null;
    }

    public TextBook convertAndAdjustLines(List<List<TextLine>> baseText) {
        TextBook book = TextBook.convertLinesToBook(baseText);
        return this.adjustTextBookLineLength(book);
    }

    public int getLineSizeInPx() {
        return this.lineSizeInPx;
    }

    public int getTextHeight() {
        return this.textHeight;
    }

    public int getTextXStart() {
        return this.textXStart;
    }

    public int getXSize() {
        return this.xSize;
    }

    public int getYSize() {
        return this.ySize;
    }

    public static interface IFontRendererWrapper {
        public int getStringWidth(String var1);

        public boolean isAvailable();
    }

    public static class FontRendererMock
    implements IFontRendererWrapper {
        @Override
        public int getStringWidth(String text) {
            return 1;
        }

        @Override
        public boolean isAvailable() {
            return true;
        }
    }
}

