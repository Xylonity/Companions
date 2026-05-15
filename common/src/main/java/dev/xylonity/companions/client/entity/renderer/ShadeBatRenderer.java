package dev.xylonity.companions.client.entity.renderer;

import dev.xylonity.companions.client.entity.model.ShadeBatModel;
import dev.xylonity.companions.common.entity.companion.ShadeBatEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ShadeBatRenderer extends GeoEntityRenderer<ShadeBatEntity> {

    public ShadeBatRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ShadeBatModel());
        this.shadowRadius = 0.5f;
    }

    @Override
    protected float getDeathMaxRotation(ShadeBatEntity animatable) {
        return 0f;
    }

}