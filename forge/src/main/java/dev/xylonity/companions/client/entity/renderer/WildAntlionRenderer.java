package dev.xylonity.companions.client.entity.renderer;

import dev.xylonity.companions.client.entity.model.WildAntlionModel;
import dev.xylonity.companions.common.entity.hostile.WildAntlionEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class WildAntlionRenderer extends GeoEntityRenderer<WildAntlionEntity> {

    public WildAntlionRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new WildAntlionModel());
        this.shadowRadius = 0.8f;
    }

}