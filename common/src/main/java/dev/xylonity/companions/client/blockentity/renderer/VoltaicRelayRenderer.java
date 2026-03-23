package dev.xylonity.companions.client.blockentity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.xylonity.companions.Companions;
import dev.xylonity.companions.client.blockentity.model.VoltaicRelayModel;
import dev.xylonity.companions.client.layer.ElectricConnectionLayer;
import dev.xylonity.companions.common.blockentity.VoltaicPillarBlockEntity;
import dev.xylonity.companions.common.blockentity.VoltaicRelayBlockEntity;
import dev.xylonity.companions.common.util.interfaces.ITeslaUtil;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class VoltaicRelayRenderer extends GeoBlockRenderer<VoltaicRelayBlockEntity> implements ITeslaUtil {

    public VoltaicRelayRenderer(BlockEntityRendererProvider.Context rendererDispatcher, int totalFrames, int ticksPerFrame) {
        super(new VoltaicRelayModel());
        addRenderLayer(new ElectricConnectionLayer<>(this, Companions.of("textures/misc/electric_arch.png"), totalFrames, ticksPerFrame));
    }

    public VoltaicRelayRenderer(BlockEntityRendererProvider.Context renderManager) {
        this(renderManager, 8, ELECTRICAL_CHARGE_DURATION / 8);
    }

    @Override
    protected void rotateBlock(Direction facing, PoseStack poseStack) {
        ;;
    }

    @Override
    public void render(VoltaicRelayBlockEntity voltaicRelayBlockEntity, float v, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int i1) {
        this.animatable = voltaicRelayBlockEntity;
        defaultRender(poseStack, this.animatable, multiBufferSource, null, null, 0, i, i1);
    }

    @Override
    public void actuallyRender(PoseStack poseStack, VoltaicRelayBlockEntity animatable, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public int getViewDistance() {
        return 256;
    }

}