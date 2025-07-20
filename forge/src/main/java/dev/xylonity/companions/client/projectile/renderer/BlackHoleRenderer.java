package dev.xylonity.companions.client.projectile.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xylonity.companions.Companions;
import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.client.projectile.model.BlackHoleModel;
import dev.xylonity.companions.client.projectile.model.FireMarkRingModel;
import dev.xylonity.companions.common.entity.projectile.BlackHoleProjectile;
import dev.xylonity.companions.common.entity.projectile.FireMarkRingProjectile;
import dev.xylonity.companions.config.CompanionsConfig;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BlackHoleRenderer extends GeoEntityRenderer<BlackHoleProjectile> {

    public BlackHoleRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new BlackHoleModel());
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull BlackHoleProjectile animatable) {
        if (animatable.getTickCount() == 18 || animatable.getTickCount() == 19) return new ResourceLocation(Companions.MOD_ID, "textures/entity/black_hole_white.png");
        if (animatable.getTickCount() == 20 || animatable.getTickCount() == 21) return new ResourceLocation(Companions.MOD_ID, "textures/entity/black_hole_black.png");

        return new ResourceLocation(Companions.MOD_ID, "textures/entity/black_hole.png");
    }

    @Override
    public RenderType getRenderType(BlackHoleProjectile animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }

    @Override
    public void render(BlackHoleProjectile entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.scale(1.75F, 1.75F, 1.75F);
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}