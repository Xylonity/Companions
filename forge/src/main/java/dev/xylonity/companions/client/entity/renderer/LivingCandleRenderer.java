package dev.xylonity.companions.client.entity.renderer;

import dev.xylonity.companions.client.entity.model.LivingCandleModel;
import dev.xylonity.companions.common.entity.summon.LivingCandleEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class LivingCandleRenderer extends GeoEntityRenderer<LivingCandleEntity> {

    public LivingCandleRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new LivingCandleModel());
        this.shadowRadius = 0.3f;
    }

}