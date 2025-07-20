package dev.xylonity.companions.client.entity.renderer;

import dev.xylonity.companions.client.entity.model.BubbleFrogModel;
import dev.xylonity.companions.common.entity.summon.BubbleFrogEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BubbleFrogRenderer extends GeoEntityRenderer<BubbleFrogEntity> {

    public BubbleFrogRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new BubbleFrogModel());
        this.shadowRadius = 0.8f;
    }

}