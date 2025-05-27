package dev.xylonity.companions.client.entity.renderer;

import dev.xylonity.companions.client.entity.model.SacredPontiffModel;
import dev.xylonity.companions.common.entity.hostile.SacredPontiffEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SacredPontiffRenderer extends GeoEntityRenderer<SacredPontiffEntity> {

    public SacredPontiffRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new SacredPontiffModel());
    }

}