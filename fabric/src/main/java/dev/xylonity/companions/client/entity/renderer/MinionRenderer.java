package dev.xylonity.companions.client.entity.renderer;

import dev.xylonity.companions.client.entity.model.MinionModel;
import dev.xylonity.companions.common.entity.companion.MinionEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class MinionRenderer extends GeoEntityRenderer<MinionEntity> {

    public MinionRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new MinionModel());
        addRenderLayer(new AutoGlowingGeoLayer<>(this));
        this.shadowRadius = 0.7f;
    }

}