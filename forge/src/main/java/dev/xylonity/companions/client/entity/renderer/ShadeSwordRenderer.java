package dev.xylonity.companions.client.entity.renderer;

import dev.xylonity.companions.client.entity.model.ShadeSwordModel;
import dev.xylonity.companions.common.entity.companion.ShadeSwordEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ShadeSwordRenderer extends GeoEntityRenderer<ShadeSwordEntity> {

    public ShadeSwordRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ShadeSwordModel());
    }

    @Override
    protected float getDeathMaxRotation(ShadeSwordEntity animatable) {
        return 0f;
    }

}