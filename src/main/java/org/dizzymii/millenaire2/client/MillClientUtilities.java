package org.dizzymii.millenaire2.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.resources.ResourceLocation;
import org.dizzymii.millenaire2.Millenaire2;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Client-side utility methods (text rendering helpers, resource location lookups, etc.).
 * Ported from org.millenaire.client.MillClientUtilities (Forge 1.12.2).
 */
public class MillClientUtilities {

    /**
     * Creates a mod ResourceLocation for a texture path.
     */
    public static ResourceLocation texture(String path) {
        return ResourceLocation.fromNamespaceAndPath(Millenaire2.MODID, "textures/" + path);
    }

    /**
     * Creates a mod ResourceLocation for a GUI texture.
     */
    public static ResourceLocation guiTexture(String name) {
        return texture("gui/" + name + ".png");
    }

    /**
     * Word-wraps a string to fit within a given pixel width using the current font.
     */
    public static List<String> wrapText(String text, int maxWidth) {
        Font font = Minecraft.getInstance().font;
        List<String> lines = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            lines.add("");
            return lines;
        }

        String[] words = text.split(" ");
        StringBuilder current = new StringBuilder();
        for (String word : words) {
            String test = current.length() > 0 ? current + " " + word : word;
            if (font.width(test) > maxWidth && current.length() > 0) {
                lines.add(current.toString());
                current = new StringBuilder(word);
            } else {
                current = new StringBuilder(test);
            }
        }
        if (current.length() > 0) lines.add(current.toString());
        return lines;
    }

    /**
     * Truncates text to fit within a pixel width, adding "..." if truncated.
     */
    public static String truncateText(String text, int maxWidth) {
        Font font = Minecraft.getInstance().font;
        if (font.width(text) <= maxWidth) return text;
        String ellipsis = "...";
        int ellipsisWidth = font.width(ellipsis);
        StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (font.width(sb.toString() + c) + ellipsisWidth > maxWidth) break;
            sb.append(c);
        }
        return sb + ellipsis;
    }

    /**
     * Converts a hex color (0xRRGGBB) to ARGB with full alpha.
     */
    public static int withAlpha(int rgb) {
        return 0xFF000000 | (rgb & 0x00FFFFFF);
    }

    /**
     * Converts a hex color with a custom alpha (0-255).
     */
    public static int withAlpha(int rgb, int alpha) {
        return (alpha << 24) | (rgb & 0x00FFFFFF);
    }

    /**
     * Gets the current game tick time or 0 if level is null.
     */
    public static long getGameTime() {
        Minecraft mc = Minecraft.getInstance();
        return mc.level != null ? mc.level.getGameTime() : 0;
    }

    /**
     * Checks if the player is currently in a GUI screen.
     */
    public static boolean isInGui() {
        return Minecraft.getInstance().screen != null;
    }

    /**
     * Gets the player's culture-formatted display name, or a fallback.
     */
    @Nullable
    public static String getPlayerName() {
        Minecraft mc = Minecraft.getInstance();
        return mc.player != null ? mc.player.getGameProfile().getName() : null;
    }
}
