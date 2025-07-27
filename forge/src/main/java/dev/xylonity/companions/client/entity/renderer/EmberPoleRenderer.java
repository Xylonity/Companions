package dev.xylonity.companions.client.entity.renderer;

import dev.xylonity.companions.client.entity.model.EmberPoleModel;
import dev.xylonity.companions.client.entity.model.FireworkToadModel;
import dev.xylonity.companions.common.entity.summon.EmberPoleEntity;
import dev.xylonity.companions.common.entity.summon.FireworkToadEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class EmberPoleRenderer extends GeoEntityRenderer<EmberPoleEntity> {

    public EmberPoleRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new EmberPoleModel());
        addRenderLayer(new AutoGlowingGeoLayer<>(this));
        this.shadowRadius = 0.2f;
    }

}