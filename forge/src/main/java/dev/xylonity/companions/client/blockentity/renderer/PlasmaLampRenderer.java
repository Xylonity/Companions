package dev.xylonity.companions.client.blockentity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xylonity.companions.client.blockentity.model.PlasmaLampModel;
import dev.xylonity.companions.common.blockentity.PlasmaLampBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class PlasmaLampRenderer extends GeoBlockRenderer<PlasmaLampBlockEntity> {

    public PlasmaLampRenderer(BlockEntityRendererProvider.Context rendererDispatcher) {
        super(new PlasmaLampModel());
    }

    @Override
    protected void rotateBlock(Direction facing, PoseStack poseStack) {
        ;;
    }

    @Override
    public int getViewDistance() {
        return 256;
    }

}