package dev.xylonity.companions.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.xylonity.companions.client.entity.model.TeddyModel;
import dev.xylonity.companions.common.entity.companion.TeddyEntity;
import dev.xylonity.knightlib.api.util.KnightLibEasings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class TeddyRenderer extends GeoEntityRenderer<TeddyEntity> {

    private static final float RITUAL_START_END_TICKS = 6f;

    public TeddyRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new TeddyModel());
        this.shadowRadius = 0.6f;
    }

    @Override
    public void render(TeddyEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        final boolean hiddenByAngelTransform = entity.getPhase() == 1 && entity.getAngelPhaseCounter() > 0 && (entity.getAngelPhaseCounter() + partialTick) / 20f >= 2.63f;
        this.shadowRadius = hiddenByAngelTransform ? 0f : 0.6f;

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);

        renderRitualItem(entity, partialTick, poseStack, bufferSource, packedLight);
    }

    private void renderRitualItem(TeddyEntity entity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        final ItemStack stack = entity.getRitualItem();
        if (entity.getRitualTicks() <= 0 || stack.isEmpty()) {
            return;
        }

        final float age = entity.getRitualRenderAge(partialTick);
        final float progress = Mth.clamp(age / TeddyEntity.RITUAL_MAX_TICKS, 0f, 1f);

        // spins fast -> slow -> fast
        final float maxSpeed = 42f;
        final float minSpeed = 5f;
        final float spinEasing = KnightLibEasings.EASE_IN_OUT_SINE.apply(progress);
        final float angle = maxSpeed * age - (maxSpeed - minSpeed) * (2f * TeddyEntity.RITUAL_MAX_TICKS / Mth.PI) * spinEasing;

        final float appear = 1f - KnightLibEasings.EASE_OUT_CUBIC.apply(Mth.clamp(age / RITUAL_START_END_TICKS, 0f, 1f));
        final float vanish = KnightLibEasings.EASE_IN_CUBIC.apply(Mth.clamp((age - (TeddyEntity.RITUAL_MAX_TICKS - RITUAL_START_END_TICKS)) / RITUAL_START_END_TICKS, 0f, 1f));
        final float teleportScale = Math.max(appear, vanish);
        final float scaleXZ = 1f - teleportScale;
        final float scaleY = 1f + teleportScale * 1.9f;
        if (scaleXZ <= 0.01f) {
            return;
        }

        final float yaw = Mth.rotLerp(partialTick, entity.yBodyRotO, entity.yBodyRot) * Mth.DEG_TO_RAD;

        poseStack.pushPose();
        poseStack.translate(-Mth.sin(yaw) * 0.9f, 1.05f + Mth.sin(age * 0.18f) * 0.05f, Mth.cos(yaw) * 0.9f);
        poseStack.mulPose(Axis.YP.rotationDegrees(angle));
        poseStack.scale(0.65f * scaleXZ, 0.65f * scaleY, 0.65f * scaleXZ);
        Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.FIXED, packedLight, OverlayTexture.NO_OVERLAY, poseStack, bufferSource, entity.level(), entity.getId());
        poseStack.popPose();
    }

    @Override
    protected float getDeathMaxRotation(TeddyEntity animatable) {
        return animatable.getPhase() == 2 ? 0F : super.getDeathMaxRotation(animatable);
    }

    @Override
    public void renderRecursively(PoseStack poseStack, TeddyEntity animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        if (bone.getName().equals("sword") && !animatable.isTame()) {
            return;
        }

        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }

    @Override
    public void actuallyRender(PoseStack poseStack, TeddyEntity animatable, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, (animatable.isInWall() && animatable.getPhase() == 2) ? LightTexture.FULL_SKY : packedLight, packedOverlay, colour);
    }

}