package org.dizzymii.millenaire2.client.render;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.dizzymii.millenaire2.entity.EntityWallDecoration;

/**
 * Renderer for wall decoration entities (tapestries, paintings, etc.).
 * Ported from org.millenaire.client.render.RenderWallDecoration (Forge 1.12.2).
 */
public class RenderWallDecoration extends EntityRenderer<EntityWallDecoration> {
    public RenderWallDecoration(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(EntityWallDecoration entity) {
        return ResourceLocation.withDefaultNamespace("textures/misc/unknown_pack.png"); // TODO: Return proper texture
    }
    // TODO: Implement wall decoration rendering with texture variants
}
