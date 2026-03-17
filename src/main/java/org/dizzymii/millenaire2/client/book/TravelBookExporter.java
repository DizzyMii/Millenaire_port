package org.dizzymii.millenaire2.client.book;

import org.dizzymii.millenaire2.util.MillLog;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Exports travel book content to external formats (HTML, text).
 * Ported from org.millenaire.client.book.TravelBookExporter (Forge 1.12.2).
 */
public class TravelBookExporter {

    /**
     * Exports a TextBook to a plain text file.
     */
    public static boolean exportToText(TextBook book, File outputFile) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            writer.write("=== " + book.title + " ===");
            writer.newLine();
            writer.newLine();

            for (int i = 0; i < book.getPageCount(); i++) {
                TextPage page = book.pages.get(i);
                if (!page.title.isEmpty()) {
                    writer.write("--- " + page.title + " ---");
                    writer.newLine();
                }
                for (TextLine line : page.lines) {
                    writer.write(line.text);
                    writer.newLine();
                }
                writer.newLine();
            }

            MillLog.minor("TravelBookExporter", "Exported book to " + outputFile.getAbsolutePath());
            return true;
        } catch (IOException e) {
            MillLog.minor("TravelBookExporter", "Failed to export book: " + e.getMessage());
            return false;
        }
    }

    /**
     * Exports a TextBook to an HTML file.
     */
    public static boolean exportToHtml(TextBook book, File outputFile) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            writer.write("<!DOCTYPE html><html><head><meta charset='UTF-8'>");
            writer.write("<title>" + escapeHtml(book.title) + "</title>");
            writer.write("<style>body{font-family:serif;max-width:700px;margin:40px auto;");
            writer.write("background:#f5e6c8;color:#3f2a14;padding:20px;}");
            writer.write("h1{color:#1a0d00;}h2{color:#1a0d00;border-bottom:1px solid #c6a96c;}</style>");
            writer.write("</head><body>");
            writer.write("<h1>" + escapeHtml(book.title) + "</h1>");

            for (int i = 0; i < book.getPageCount(); i++) {
                TextPage page = book.pages.get(i);
                if (!page.title.isEmpty()) {
                    writer.write("<h2>" + escapeHtml(page.title) + "</h2>");
                }
                for (TextLine line : page.lines) {
                    String text = escapeHtml(line.text);
                    if (line.bold) text = "<b>" + text + "</b>";
                    if (line.italic) text = "<i>" + text + "</i>";
                    writer.write("<p>" + text + "</p>");
                }
            }

            writer.write("</body></html>");
            MillLog.minor("TravelBookExporter", "Exported HTML book to " + outputFile.getAbsolutePath());
            return true;
        } catch (IOException e) {
            MillLog.minor("TravelBookExporter", "Failed to export HTML book: " + e.getMessage());
            return false;
        }
    }

    private static String escapeHtml(String text) {
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
