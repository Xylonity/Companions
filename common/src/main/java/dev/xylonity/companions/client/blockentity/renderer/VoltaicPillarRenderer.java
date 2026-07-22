package dev.xylonity.companions.client.blockentity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xylonity.companions.Companions;
import dev.xylonity.companions.client.blockentity.model.VoltaicPillarModel;
import dev.xylonity.companions.client.layer.ElectricConnectionLayer;
import dev.xylonity.companions.common.blockentity.VoltaicPillarBlockEntity;
import dev.xylonity.companions.common.util.interfaces.ITeslaUtil;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;

public class VoltaicPillarRenderer extends AbstractGeoBlockRenderer<VoltaicPillarBlockEntity> {

    public VoltaicPillarRenderer(BlockEntityRendererProvider.Context rendererDispatcher, int totalFrames, int ticksPerFrame) {
        super(new VoltaicPillarModel());
        addRenderLayer(new ElectricConnectionLayer<>(this,
                Companions.of("textures/misc/electric_arch_wall.png"),
                totalFrames, ticksPerFrame,
                ElectricConnectionLayer.FrameMode.LOOP));
    }

    public VoltaicPillarRenderer(BlockEntityRendererProvider.Context renderManager) {
        this(renderManager, 4, ITeslaUtil.ELECTRICAL_CHARGE_DURATION / 4);
    }

    @Override
    protected void rotateBlock(Direction facing, PoseStack poseStack) { }

    @Override
    public boolean shouldRenderOffScreen(VoltaicPillarBlockEntity blockEntity) {
        return Companions.PLATFORM.requiresGlobalTeslaRenderer();
    }

    public AABB getRenderBoundingBox(VoltaicPillarBlockEntity blockEntity) {
        return blockEntity.getConnectionRenderBoundingBox();
    }

    @Override
    public int getViewDistance() {
        return 256;
    }

}
