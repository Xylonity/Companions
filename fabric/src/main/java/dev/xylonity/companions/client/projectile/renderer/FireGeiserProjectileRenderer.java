package dev.xylonity.companions.client.projectile.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xylonity.companions.client.projectile.model.FireGeiserProjectileModel;
import dev.xylonity.companions.client.projectile.model.PontiffFireRingModel;
import dev.xylonity.companions.common.entity.projectile.FireGeiserProjectile;
import dev.xylonity.companions.common.entity.projectile.PontiffFireRingProjectile;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class FireGeiserProjectileRenderer extends GeoEntityRenderer<FireGeiserProjectile> {

    public FireGeiserProjectileRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new FireGeiserProjectileModel());
    }

    @Override
    public void render(@NotNull FireGeiserProjectile entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.scale(1.25f, 1.25f, 1.25f);
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, LightTexture.FULL_SKY);
    }

}