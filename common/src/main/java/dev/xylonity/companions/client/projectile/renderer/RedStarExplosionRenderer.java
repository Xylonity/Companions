package dev.xylonity.companions.client.projectile.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xylonity.companions.client.projectile.model.RedStarExplosionModel;
import dev.xylonity.companions.common.entity.projectile.RedStarExplosion;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class RedStarExplosionRenderer extends GeoEntityRenderer<RedStarExplosion> {

    public RedStarExplosionRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new RedStarExplosionModel());
    }

    @Override
    public void render(@NotNull RedStarExplosion entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.scale(1.5f, 1.5f, 1.5f);
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, LightTexture.pack(15, 15));
    }

}