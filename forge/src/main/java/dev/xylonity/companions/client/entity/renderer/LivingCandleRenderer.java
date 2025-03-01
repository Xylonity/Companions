package dev.xylonity.companions.client.entity.renderer;

import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.client.entity.model.GoldenAllayModel;
import dev.xylonity.companions.client.entity.model.LivingCandleModel;
import dev.xylonity.companions.common.entity.custom.GoldenAllayEntity;
import dev.xylonity.companions.common.entity.custom.LivingCandleEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class LivingCandleRenderer extends GeoEntityRenderer<LivingCandleEntity> {

    public LivingCandleRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new LivingCandleModel());
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull LivingCandleEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/living_candle.png");
    }

}