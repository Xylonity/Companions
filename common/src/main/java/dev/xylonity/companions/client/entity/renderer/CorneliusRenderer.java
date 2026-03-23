package dev.xylonity.companions.client.entity.renderer;

import dev.xylonity.companions.client.entity.model.CorneliusModel;
import dev.xylonity.companions.common.entity.companion.CorneliusEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CorneliusRenderer extends GeoEntityRenderer<CorneliusEntity> {

    public CorneliusRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new CorneliusModel());
        this.shadowRadius = 0.8f;
    }

}