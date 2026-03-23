package dev.xylonity.companions.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.xylonity.companions.client.entity.model.TeddyModel;
import dev.xylonity.companions.common.entity.companion.TeddyEntity;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class TeddyRenderer extends GeoEntityRenderer<TeddyEntity> {

    public TeddyRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new TeddyModel());
        this.shadowRadius = 0.6f;
    }

    @Override
    protected float getDeathMaxRotation(TeddyEntity animatable) {
        return animatable.getPhase() == 2 ? 0F : super.getDeathMaxRotation(animatable);
    }

    @Override
    public void renderRecursively(PoseStack poseStack, TeddyEntity animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {

        if (bone.getName().equals("sword") && !animatable.isTame()) return;

        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public void render(@NotNull TeddyEntity entity, float entityYaw, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, (entity.isInWall() && entity.getPhase() == 2) ? LightTexture.FULL_SKY : packedLight);
    }

}