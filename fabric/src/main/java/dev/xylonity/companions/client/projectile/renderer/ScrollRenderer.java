package dev.xylonity.companions.client.projectile.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xylonity.companions.client.projectile.model.ScrollModel;
import dev.xylonity.companions.common.entity.projectile.ScrollProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ScrollRenderer extends GeoEntityRenderer<ScrollProjectile> {

    public ScrollRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ScrollModel());
    }

    @Override
    public void render(@NotNull ScrollProjectile animatable, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.scale(1.3F, 1.3F, 1.3F);
        super.render(animatable, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

}