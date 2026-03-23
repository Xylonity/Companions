package dev.xylonity.companions.client.projectile.renderer;

import dev.xylonity.companions.client.projectile.model.FireRayBeamModel;
import dev.xylonity.companions.common.entity.projectile.BloodTornadoProjectile;
import dev.xylonity.companions.common.entity.projectile.trigger.FireRayBeamEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class FireRayBeamRenderer extends GeoEntityRenderer<FireRayBeamEntity> {

    public FireRayBeamRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new FireRayBeamModel());
    }

    @Override
    public RenderType getRenderType(FireRayBeamEntity animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucentEmissive(getTextureLocation(animatable));
    }
}