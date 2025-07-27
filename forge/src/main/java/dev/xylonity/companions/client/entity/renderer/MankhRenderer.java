package dev.xylonity.companions.client.entity.renderer;

import dev.xylonity.companions.client.entity.model.MankhModel;
import dev.xylonity.companions.common.entity.companion.MankhEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class MankhRenderer extends GeoEntityRenderer<MankhEntity> {

    public MankhRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new MankhModel());
        addRenderLayer(new AutoGlowingGeoLayer<>(this));
        this.shadowRadius = 0.7f;
    }

}