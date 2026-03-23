package dev.xylonity.companions.client.projectile.renderer;

import dev.xylonity.companions.client.projectile.model.FrogHealModel;
import dev.xylonity.companions.common.entity.projectile.FireRayPieceProjectile;
import dev.xylonity.companions.common.entity.projectile.FrogHealProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class FrogHealRenderer extends GeoEntityRenderer<FrogHealProjectile> {

    public FrogHealRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new FrogHealModel());
    }

    @Override
    public RenderType getRenderType(FrogHealProjectile animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucentEmissive(getTextureLocation(animatable));
    }
}