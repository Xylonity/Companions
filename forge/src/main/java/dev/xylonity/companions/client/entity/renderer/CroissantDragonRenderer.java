package dev.xylonity.companions.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.client.entity.model.AntlionModel;
import dev.xylonity.companions.client.entity.model.CroissantDragonModel;
import dev.xylonity.companions.common.entity.custom.AntlionEntity;
import dev.xylonity.companions.common.entity.custom.CroissantDragonEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CroissantDragonRenderer extends GeoEntityRenderer<CroissantDragonEntity> {

    private final String TPATH = "textures/entity/croissant_dragon_";

    public CroissantDragonRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new CroissantDragonModel());
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull CroissantDragonEntity animatable) {
        if (animatable.isAttacking()) {
            return new ResourceLocation(CompanionsCommon.MOD_ID, TPATH + animatable.getArmorName() + "_attack.png");
        } else {
            return new ResourceLocation(CompanionsCommon.MOD_ID, TPATH + animatable.getArmorName() + ".png");
        }
    }

    @Override
    public void renderRecursively(PoseStack poseStack, CroissantDragonEntity animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {

        if (animatable.getEatenAmount() >= 1 && bone.getName().equals("eat3")) {
            return;
        }

        if (animatable.getEatenAmount() == 2 && bone.getName().equals("eat4")) {
            return;
        }

        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}