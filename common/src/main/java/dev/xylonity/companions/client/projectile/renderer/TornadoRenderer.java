package dev.xylonity.companions.client.projectile.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xylonity.companions.client.projectile.model.TornadoModel;
import dev.xylonity.companions.common.entity.projectile.TornadoProjectile;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class TornadoRenderer extends GeoEntityRenderer<TornadoProjectile> {

    public TornadoRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new TornadoModel());
    }

    @Override
    public RenderType getRenderType(TornadoProjectile animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }

    @Override
    public void render(TornadoProjectile entity, float entityYaw, float partialTick, PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
        poseStack.scale(2,2,2);
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, entity.isInWall() ? LightTexture.FULL_SKY : packedLight);
    }

}