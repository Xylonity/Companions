package dev.xylonity.companions.client.entity.renderer;

import dev.xylonity.companions.client.entity.model.HostilePuppetGloveModel;
import dev.xylonity.companions.common.entity.hostile.HostilePuppetGloveEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class HostilePuppetGloveRenderer extends GeoEntityRenderer<HostilePuppetGloveEntity> {

    public HostilePuppetGloveRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new HostilePuppetGloveModel());
    }

}