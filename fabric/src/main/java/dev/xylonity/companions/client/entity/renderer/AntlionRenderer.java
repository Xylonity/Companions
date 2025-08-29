package dev.xylonity.companions.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.xylonity.companions.client.entity.model.AntlionModel;
import dev.xylonity.companions.common.entity.companion.AntlionEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class AntlionRenderer extends GeoEntityRenderer<AntlionEntity> {

    public AntlionRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new AntlionModel());
        addRenderLayer(new AutoGlowingGeoLayer<>(this));
        this.shadowRadius = 0.8f;
    }

    @Override
    public void renderRecursively(PoseStack poseStack, AntlionEntity animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        if (bone.getName().contains("chain") && !animatable.isPhaseLocked()) {
            return;
        }

        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }

}