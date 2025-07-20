package dev.xylonity.companions.client.projectile.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xylonity.companions.client.projectile.model.BloodTornadoModel;
import dev.xylonity.companions.common.entity.projectile.BloodTornadoProjectile;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BloodTornadoRenderer extends GeoEntityRenderer<BloodTornadoProjectile> {

    public BloodTornadoRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new BloodTornadoModel());
    }

    @Override
    public RenderType getRenderType(BloodTornadoProjectile animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }

    @Override
    public void render(BloodTornadoProjectile entity, float entityYaw, float partialTick, PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
        poseStack.scale(1.25f,1.25f,1.25f);
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, entity.isInWall() ? LightTexture.FULL_SKY : packedLight);
    }

}