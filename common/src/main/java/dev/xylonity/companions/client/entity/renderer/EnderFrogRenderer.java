package dev.xylonity.companions.client.entity.renderer;

import dev.xylonity.companions.client.entity.model.EnderFrogModel;
import dev.xylonity.companions.common.entity.summon.EnderFrogEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class EnderFrogRenderer extends GeoEntityRenderer<EnderFrogEntity> {

    public EnderFrogRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new EnderFrogModel());
        addRenderLayer(new AutoGlowingGeoLayer<>(this));
        this.shadowRadius = 0.6f;
    }

}