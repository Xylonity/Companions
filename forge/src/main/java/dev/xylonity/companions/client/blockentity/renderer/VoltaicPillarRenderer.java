package dev.xylonity.companions.client.blockentity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xylonity.companions.Companions;
import dev.xylonity.companions.client.blockentity.model.VoltaicPillarModel;
import dev.xylonity.companions.client.layer.StaticElectricConnectionLayer;
import dev.xylonity.companions.common.blockentity.VoltaicPillarBlockEntity;
import dev.xylonity.companions.common.util.interfaces.ITeslaUtil;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class VoltaicPillarRenderer extends GeoBlockRenderer<VoltaicPillarBlockEntity> implements ITeslaUtil {

    public VoltaicPillarRenderer(BlockEntityRendererProvider.Context rendererDispatcher, int totalFrames, int ticksPerFrame) {
        super(new VoltaicPillarModel());
        addRenderLayer(new StaticElectricConnectionLayer<>(this, new ResourceLocation(Companions.MOD_ID, "textures/misc/electric_arch_wall.png"), totalFrames, ticksPerFrame));
    }

    public VoltaicPillarRenderer(BlockEntityRendererProvider.Context renderManager) {
        this(renderManager, 4, ELECTRICAL_CHARGE_DURATION / 4);
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