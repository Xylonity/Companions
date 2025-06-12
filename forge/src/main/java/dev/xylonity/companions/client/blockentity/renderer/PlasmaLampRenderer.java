package dev.xylonity.companions.client.blockentity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xylonity.companions.Companions;
import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.client.blockentity.model.PlasmaLampModel;
import dev.xylonity.companions.common.blockentity.PlasmaLampBlockEntity;
import dev.xylonity.companions.common.util.interfaces.ITeslaUtil;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class PlasmaLampRenderer extends GeoBlockRenderer<PlasmaLampBlockEntity> implements ITeslaUtil {

    public PlasmaLampRenderer(BlockEntityRendererProvider.Context rendererDispatcher) {
        super(new PlasmaLampModel());
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull PlasmaLampBlockEntity pBlockEntity) {
        return true;
    }

    @Override
    protected void rotateBlock(Direction facing, PoseStack poseStack) {

    }

}