package dev.xylonity.companions.client.projectile.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xylonity.companions.client.projectile.model.LaserRingModel;
import dev.xylonity.companions.common.entity.projectile.HolinessStartProjectile;
import dev.xylonity.companions.common.entity.projectile.LaserRingProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class LaserRingRenderer extends GeoEntityRenderer<LaserRingProjectile> {

    public LaserRingRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new LaserRingModel());
    }

    @Override
    public void render(@NotNull LaserRingProjectile entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.scale(6f, 6f, 6f);
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    public RenderType getRenderType(LaserRingProjectile animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucentEmissive(getTextureLocation(animatable));
    }
}