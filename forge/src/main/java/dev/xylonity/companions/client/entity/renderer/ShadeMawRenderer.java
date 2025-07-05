package dev.xylonity.companions.client.entity.renderer;

import dev.xylonity.companions.client.entity.model.ShadeMawModel;
import dev.xylonity.companions.common.entity.companion.ShadeMawEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ShadeMawRenderer extends GeoEntityRenderer<ShadeMawEntity> {

    public ShadeMawRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ShadeMawModel());
    }

    @Override
    protected float getDeathMaxRotation(ShadeMawEntity animatable) {
        return 0f;
    }

}