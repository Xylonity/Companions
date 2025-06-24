package dev.xylonity.companions.client.blockentity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.xylonity.companions.client.blockentity.model.FrogBonanzaModel;
import dev.xylonity.companions.common.blockentity.FrogBonanzaBlockEntity;
import dev.xylonity.companions.common.util.Util;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class FrogBonanzaRenderer extends GeoBlockRenderer<FrogBonanzaBlockEntity> {

    private static final String[] WHEELS = {"wheel", "wheel2", "wheel3"};

    public FrogBonanzaRenderer(BlockEntityRendererProvider.Context ctx) {
        super(new FrogBonanzaModel());
    }

    @Override
    public void renderRecursively(PoseStack poseStack, FrogBonanzaBlockEntity animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        for (int i = 0; i < 3; i++) {
            final int idx = i; // my guy i doesn't want to be referenced directly
            this.model.getBone(WHEELS[i]).ifPresent(b -> b.setRotX(b.getRotX() + Util.degToRad(animatable.getWheelRotation(idx))));
        }

        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);

        for (int i = 0; i < 3; i++) {
            final int idx = i;
            this.model.getBone(WHEELS[i]).ifPresent(b -> b.setRotX(b.getRotX() - Util.degToRad(animatable.getWheelRotation(idx))));
        }
    }

}
