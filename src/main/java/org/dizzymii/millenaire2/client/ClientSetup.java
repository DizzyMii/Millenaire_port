package org.dizzymii.millenaire2.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import org.dizzymii.millenaire2.Millenaire2;
import org.dizzymii.millenaire2.client.render.FemaleAsymmRenderer;
import org.dizzymii.millenaire2.client.render.FemaleAsymmetricalModel;
import org.dizzymii.millenaire2.client.render.FemaleSymmRenderer;
import org.dizzymii.millenaire2.client.render.FemaleSymmetricalModel;
import org.dizzymii.millenaire2.client.render.MillVillagerModel;
import org.dizzymii.millenaire2.client.render.MillVillagerRenderer;
import org.dizzymii.millenaire2.entity.MillEntities;

/**
 * Client-side setup: registers renderers, model layers, and keybindings.
 */
@EventBusSubscriber(modid = Millenaire2.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        // TODO: Register keybindings, overlay renderers, etc.
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
        event.registerEntityRenderer(MillEntities.GENERIC_SYMM_FEMALE.get(), FemaleSymmRenderer::new);
        event.registerEntityRenderer(MillEntities.GENERIC_ASYMM_FEMALE.get(), FemaleAsymmRenderer::new);
        // TODO: Register targeted mob and wall decoration renderers
    }
}
