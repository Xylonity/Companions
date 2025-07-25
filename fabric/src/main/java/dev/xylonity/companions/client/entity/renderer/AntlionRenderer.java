package dev.xylonity.companions.client.entity.renderer;

import dev.xylonity.companions.client.entity.model.AntlionModel;
import dev.xylonity.companions.common.entity.companion.AntlionEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class AntlionRenderer extends GeoEntityRenderer<AntlionEntity> {

    public AntlionRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new AntlionModel());
        addRenderLayer(new AutoGlowingGeoLayer<>(this));
        this.shadowRadius = 0.8f;
    }

}