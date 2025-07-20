package dev.xylonity.companions.client.projectile.renderer;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.client.projectile.model.GenericTriggerProjectileModel;
import dev.xylonity.companions.common.entity.projectile.trigger.GenericTriggerProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class GenericTriggerProjectileRenderer extends GeoEntityRenderer<GenericTriggerProjectile> {

    public GenericTriggerProjectileRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new GenericTriggerProjectileModel());
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull GenericTriggerProjectile animatable) {
        return new ResourceLocation(Companions.MOD_ID, "textures/entity/generic.png");
    }

    @Override
    public RenderType getRenderType(GenericTriggerProjectile animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }

}