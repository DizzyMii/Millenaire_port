package org.dizzymii.millenaire2.client;

import net.minecraft.client.Minecraft;
import org.dizzymii.millenaire2.client.book.BookManager;
import org.dizzymii.millenaire2.util.MillLog;

/**
 * Client-side proxy for registering renderers, key bindings, and client events.
 * Called from ClientSetup during FMLClientSetupEvent.
 * Ported from org.millenaire.client.ClientProxy (Forge 1.12.2).
 */
public class ClientProxy {

    /**
     * Perform client-side initialization: load books, set up overlay state.
     */
    public static void init() {
        MillLog.minor("ClientProxy", "Initializing client-side systems...");
        BookManager.loadBooks();
        MillLog.minor("ClientProxy", "Client initialization complete.");
    }

    /**
     * Resets all client-side state (on world disconnect).
     */
    public static void reset() {
        ClientTickHandler.reset();
        BookManager.clearBooks();
    }

    /**
     * Gets the Minecraft client instance (convenience).
     */
    public static Minecraft mc() {
        return Minecraft.getInstance();
    }
}
