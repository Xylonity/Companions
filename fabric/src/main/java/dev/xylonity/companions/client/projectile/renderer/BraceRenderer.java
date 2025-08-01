package dev.xylonity.companions.client.projectile.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.client.projectile.model.BraceModel;
import dev.xylonity.companions.common.entity.projectile.BraceProjectile;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BraceRenderer extends GeoEntityRenderer<BraceProjectile> {

    public BraceRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new BraceModel());
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull BraceProjectile animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/brace.png");
    }

    @Override
    public RenderType getRenderType(BraceProjectile animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }

    @Override
    protected void applyRotations(BraceProjectile animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick) {
        double pivotY = animatable.getBbHeight() / 2.0;

        poseStack.translate(0, pivotY, 0);

        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        float cameraYaw = camera.getYRot();
        float cameraPitch = camera.getXRot();
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - cameraYaw));
        poseStack.mulPose(Axis.XP.rotationDegrees(-cameraPitch));

        poseStack.translate(0, -pivotY, 0);

        super.applyRotations(animatable, poseStack, ageInTicks, rotationYaw, partialTick);
    }

    @Override
    public void render(BraceProjectile entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.scale(0.75f, 0.75f, 0.75f);
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, 15728880);
    }

}