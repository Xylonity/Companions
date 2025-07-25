package dev.xylonity.companions.client.entity.renderer;

import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.client.entity.model.LivingCandleModel;
import dev.xylonity.companions.common.entity.summon.LivingCandleEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class LivingCandleRenderer extends GeoEntityRenderer<LivingCandleEntity> {

    public LivingCandleRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new LivingCandleModel());
        addRenderLayer(new AutoGlowingGeoLayer<>(this));
        this.shadowRadius = 0.3f;
    }

}