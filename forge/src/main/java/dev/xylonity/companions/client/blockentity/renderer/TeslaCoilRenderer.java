package dev.xylonity.companions.client.blockentity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xylonity.companions.Companions;
import dev.xylonity.companions.client.blockentity.model.TeslaCoilModel;
import dev.xylonity.companions.client.layer.ElectricConnectionLayer;
import dev.xylonity.companions.common.blockentity.TeslaCoilBlockEntity;
import dev.xylonity.companions.common.util.interfaces.ITeslaUtil;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class TeslaCoilRenderer extends GeoBlockRenderer<TeslaCoilBlockEntity> implements ITeslaUtil {

    public TeslaCoilRenderer(BlockEntityRendererProvider.Context rendererDispatcher, int totalFrames, int ticksPerFrame) {
        super(new TeslaCoilModel());
        addRenderLayer(new ElectricConnectionLayer<>(this, new ResourceLocation(Companions.MOD_ID, "textures/misc/electric_arch.png"), totalFrames, ticksPerFrame));
    }

    public TeslaCoilRenderer(BlockEntityRendererProvider.Context renderManager) {
        this(renderManager, 8, ELECTRICAL_CHARGE_DURATION / 8);
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