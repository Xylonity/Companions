package dev.xylonity.companions.client.projectile.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.xylonity.companions.Companions;
import dev.xylonity.companions.client.projectile.model.BlackHoleModel;
import dev.xylonity.companions.common.entity.projectile.BlackHoleProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BlackHoleRenderer extends GeoEntityRenderer<BlackHoleProjectile> {

    public BlackHoleRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new BlackHoleModel());
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull BlackHoleProjectile animatable) {
        if (animatable.getTickCount() == 18 || animatable.getTickCount() == 19) return ResourceLocation.fromNamespaceAndPath(Companions.MOD_ID, "textures/entity/black_hole_white.png");
        if (animatable.getTickCount() == 20 || animatable.getTickCount() == 21) return ResourceLocation.fromNamespaceAndPath(Companions.MOD_ID, "textures/entity/black_hole_black.png");

        return ResourceLocation.fromNamespaceAndPath(Companions.MOD_ID, "textures/entity/black_hole.png");
    }

    @Override
    public RenderType getRenderType(BlackHoleProjectile animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }

    @Override
    public void actuallyRender(PoseStack poseStack, BlackHoleProjectile animatable, BakedGeoModel model, @Nullable RenderType renderType, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        poseStack.scale(1.75F, 1.75F, 1.75F);
        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }

}