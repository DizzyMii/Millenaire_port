package org.dizzymii.millenaire2.gametest;

import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.dizzymii.millenaire2.Millenaire2;
import org.dizzymii.millenaire2.util.MillLog;

/**
 * Registers empty structure templates for GameTests so they don't need .nbt files.
 * Called during server start, before GameTests attempt to load templates.
 */
public class GameTestSetup {

    private static boolean initialized = false;

    public static void ensureTemplates(ServerLevel level) {
        if (initialized) return;
        initialized = true;

        try {
            StructureTemplateManager manager = level.getStructureManager();
            ResourceLocation emptyId = ResourceLocation.fromNamespaceAndPath(
                    Millenaire2.MODID, "empty");

            // getOrCreate returns an empty template if not found on disk
            StructureTemplate template = manager.getOrCreate(emptyId);

            // Load a minimal 3x3x3 air structure into it
            CompoundTag tag = new CompoundTag();
            tag.putInt("DataVersion", net.minecraft.SharedConstants.getCurrentVersion()
                    .getDataVersion().getVersion());

            ListTag size = new ListTag();
            size.add(IntTag.valueOf(3));
            size.add(IntTag.valueOf(3));
            size.add(IntTag.valueOf(3));
            tag.put("size", size);

            tag.put("entities", new ListTag());
            tag.put("blocks", new ListTag());

            ListTag palette = new ListTag();
            CompoundTag airEntry = new CompoundTag();
            airEntry.putString("Name", "minecraft:air");
            palette.add(airEntry);
            tag.put("palette", palette);

            template.load(
                    level.registryAccess().lookupOrThrow(Registries.BLOCK),
                    tag);

            MillLog.major(null, "GameTest: registered empty structure template");
        } catch (Exception e) {
            MillLog.error(null, "GameTest: failed to register empty template", e);
        }
    }
}
