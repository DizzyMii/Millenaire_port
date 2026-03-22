package org.dizzymii.millenaire2.client;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;
import net.minecraft.client.Minecraft;
import org.dizzymii.millenaire2.client.book.BookManager;

/**
 * Client-side proxy for registering renderers, key bindings, and client events.
 * Called from ClientSetup during FMLClientSetupEvent.
 * Ported from org.millenaire.client.ClientProxy (Forge 1.12.2).
 */
public class ClientProxy {
    private static final Logger LOGGER = LogUtils.getLogger();

    /**
     * Perform client-side initialization: load books, set up overlay state.
     */
    public static void init() {
        LOGGER.debug("Initializing client-side systems...");
        BookManager.loadBooks();
        LOGGER.debug("Client initialization complete.");
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
