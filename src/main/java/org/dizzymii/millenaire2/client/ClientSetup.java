package org.dizzymii.millenaire2.client;

import net.minecraft.client.renderer.entity.BlazeRenderer;
import net.minecraft.client.renderer.entity.GhastRenderer;
import net.minecraft.client.renderer.entity.WitherSkeletonRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import org.dizzymii.millenaire2.Millenaire2;
import org.dizzymii.millenaire2.client.render.FemaleAsymmetricalModel;
import org.dizzymii.millenaire2.client.render.FemaleSymmetricalModel;
import org.dizzymii.millenaire2.client.render.MillVillagerModel;
import org.dizzymii.millenaire2.client.render.FemaleAsymmetricalRenderer;
import org.dizzymii.millenaire2.client.render.FemaleSymmetricalRenderer;
import org.dizzymii.millenaire2.client.render.MillVillagerRenderer;
import org.dizzymii.millenaire2.client.render.RenderWallDecoration;
import org.dizzymii.millenaire2.entity.MillEntities;

/**
 * Client-side setup: registers renderers, model layers, and keybindings.
 */
@EventBusSubscriber(modid = Millenaire2.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        // Client-side setup complete — renderers and layer definitions registered via events below.
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(MillVillagerModel.LAYER_LOCATION, MillVillagerModel::createBodyLayer);
        event.registerLayerDefinition(FemaleSymmetricalModel.LAYER_LOCATION, FemaleSymmetricalModel::createBodyLayer);
        event.registerLayerDefinition(FemaleAsymmetricalModel.LAYER_LOCATION, FemaleAsymmetricalModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(MillEntities.GENERIC_MALE.get(), MillVillagerRenderer::new);
        event.registerEntityRenderer(MillEntities.GENERIC_SYMM_FEMALE.get(), FemaleSymmetricalRenderer::new);
        event.registerEntityRenderer(MillEntities.GENERIC_ASYMM_FEMALE.get(), FemaleAsymmetricalRenderer::new);
        event.registerEntityRenderer(MillEntities.WALL_DECORATION.get(), RenderWallDecoration::new);
        event.registerEntityRenderer(MillEntities.TARGETED_BLAZE.get(), BlazeRenderer::new);
        event.registerEntityRenderer(MillEntities.TARGETED_WITHER_SKELETON.get(), WitherSkeletonRenderer::new);
        event.registerEntityRenderer(MillEntities.TARGETED_GHAST.get(), GhastRenderer::new);
    }
}
