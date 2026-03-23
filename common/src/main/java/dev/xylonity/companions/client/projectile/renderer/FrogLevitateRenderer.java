package dev.xylonity.companions.client.projectile.renderer;

import dev.xylonity.companions.client.projectile.model.FrogLevitateModel;
import dev.xylonity.companions.common.entity.projectile.FrogHealProjectile;
import dev.xylonity.companions.common.entity.projectile.FrogLevitateProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class FrogLevitateRenderer extends GeoEntityRenderer<FrogLevitateProjectile> {

    public FrogLevitateRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new FrogLevitateModel());
    }

    @Override
    public RenderType getRenderType(FrogLevitateProjectile animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucentEmissive(getTextureLocation(animatable));
    }
}