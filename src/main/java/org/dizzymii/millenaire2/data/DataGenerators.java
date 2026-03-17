package org.dizzymii.millenaire2.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.dizzymii.millenaire2.Millenaire2;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = Millenaire2.MODID, bus = EventBusSubscriber.Bus.MOD)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        // Block states and block models
        generator.addProvider(event.includeClient(), new MillBlockStateProvider(output, existingFileHelper));

        // Item models
        generator.addProvider(event.includeClient(), new MillItemModelProvider(output, existingFileHelper));

        // Language
        generator.addProvider(event.includeClient(), new MillLanguageProvider(output));

        // Block tags
        MillBlockTagProvider blockTagProvider = new MillBlockTagProvider(output, lookupProvider, existingFileHelper);
        generator.addProvider(event.includeServer(), blockTagProvider);

        // Item tags
        generator.addProvider(event.includeServer(), new MillItemTagProvider(output, lookupProvider, blockTagProvider.contentsGetter(), existingFileHelper));

        // Loot tables
        generator.addProvider(event.includeServer(), new MillLootTableProvider(output, lookupProvider));

        // Recipes
        generator.addProvider(event.includeServer(), new MillRecipeProvider(output, lookupProvider));
    }
}
