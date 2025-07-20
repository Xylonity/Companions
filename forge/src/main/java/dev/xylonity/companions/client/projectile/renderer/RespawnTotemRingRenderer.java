package dev.xylonity.companions.client.projectile.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xylonity.companions.client.projectile.model.RespawnTotemRingModel;
import dev.xylonity.companions.common.entity.projectile.RespawnTotemRingProjectile;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class RespawnTotemRingRenderer extends GeoEntityRenderer<RespawnTotemRingProjectile> {

    public RespawnTotemRingRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new RespawnTotemRingModel());
    }

    @Override
    public void render(RespawnTotemRingProjectile entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.scale(5, 5, 5);
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, LightTexture.pack(15, 15));
    }

}