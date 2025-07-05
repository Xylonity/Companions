package dev.xylonity.companions.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.client.entity.model.GoldenAllayModel;
import dev.xylonity.companions.common.entity.companion.GoldenAllayEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class GoldenAllayRenderer extends GeoEntityRenderer<GoldenAllayEntity> {

    public GoldenAllayRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new GoldenAllayModel());
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull GoldenAllayEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/golden_allay.png");
    }

    @Override
    public void renderRecursively(PoseStack poseStack, GoldenAllayEntity animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {

        if (animatable.activePieces() != 1 && animatable.activePieces() != 3 && animatable.activePieces() != 4 && bone.getName().equals("hat")) {
            return;
        }

        if (animatable.activePieces() != 2 && animatable.activePieces() != 3 && animatable.activePieces() != 4 && bone.getName().equals("shirt")) {
            return;
        }

        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

}