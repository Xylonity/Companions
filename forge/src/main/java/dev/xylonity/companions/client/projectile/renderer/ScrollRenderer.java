package dev.xylonity.companions.client.projectile.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.client.projectile.model.ScrollModel;
import dev.xylonity.companions.client.projectile.model.StoneSpikeModel;
import dev.xylonity.companions.common.entity.projectile.ScrollProjectile;
import dev.xylonity.companions.common.entity.projectile.StoneSpikeProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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