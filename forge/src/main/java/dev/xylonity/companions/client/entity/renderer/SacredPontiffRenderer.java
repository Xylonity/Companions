package dev.xylonity.companions.client.entity.renderer;

import dev.xylonity.companions.client.entity.model.SacredPontiffModel;
import dev.xylonity.companions.common.entity.hostile.SacredPontiffEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SacredPontiffRenderer extends GeoEntityRenderer<SacredPontiffEntity> {

    public SacredPontiffRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new SacredPontiffModel());
    }

    @Override
    protected float getDeathMaxRotation(SacredPontiffEntity animatable) {
        return 0f;
    }

    @Override
    public int getPackedOverlay(SacredPontiffEntity animatable, float u) {
        if (animatable.getPhase() == 2 && animatable.isDeadOrDying()) {
            return OverlayTexture.NO_OVERLAY;
        }

        return super.getPackedOverlay(animatable, u);
    }

}