package dev.xylonity.companions.client.projectile.renderer;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.client.projectile.model.GenericTriggerProjectileModel;
import dev.xylonity.companions.common.entity.projectile.trigger.CakeCreamTriggerProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import javax.annotation.Nullable;

public class CakeCreamTriggerProjectileRenderer extends GeoEntityRenderer<CakeCreamTriggerProjectile> {

    public CakeCreamTriggerProjectileRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new GenericTriggerProjectileModel<>());
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull CakeCreamTriggerProjectile animatable) {
        return Companions.of("textures/entity/generic.png");
    }

    @Override
    public RenderType getRenderType(CakeCreamTriggerProjectile animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }

}