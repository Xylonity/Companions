package dev.xylonity.companions.client.entity.renderer;

import dev.xylonity.companions.client.entity.model.CloakModel;
import dev.xylonity.companions.common.entity.companion.CloakEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class CloakRenderer extends GeoEntityRenderer<CloakEntity> {

    public CloakRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new CloakModel());
        addRenderLayer(new AutoGlowingGeoLayer<>(this));
        this.shadowRadius = 1f;
    }

}
