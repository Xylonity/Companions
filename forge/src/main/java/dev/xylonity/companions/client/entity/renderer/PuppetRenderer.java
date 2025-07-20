package dev.xylonity.companions.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.client.entity.model.PuppetModel;
import dev.xylonity.companions.common.entity.companion.PuppetEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PuppetRenderer extends GeoEntityRenderer<PuppetEntity> {

    public PuppetRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new PuppetModel());
        this.shadowRadius = 0.7f;
    }

    @Override
    public void renderRecursively(PoseStack poseStack, PuppetEntity animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        String[] armNames = animatable.getArmNames().split(",");
        String leftArm = armNames[0];
        String rightArm = armNames[1];

        if (bone.getName().endsWith("arm_left")) {
            if (!bone.getName().trim().startsWith("arm_left") && leftArm.trim().equals("none")) {
                return;
            } else if (!bone.getName().trim().contains(leftArm) && leftArm.trim().equals(leftArm) && !leftArm.trim().equals("none")) {
                return;
            }
        }

        if (bone.getName().endsWith("arm_right")) {
            if (!bone.getName().trim().startsWith("arm_right") && rightArm.trim().equals("none")) {
                return;
            } else if (!bone.getName().trim().contains(rightArm) && rightArm.trim().equals(rightArm) && !rightArm.trim().equals("none")) {
                return;
            }
        }

        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

}