package dev.xylonity.companions.client.blockentity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xylonity.companions.client.blockentity.model.RecallPlatformModel;
import dev.xylonity.companions.common.blockentity.RecallPlatformBlockEntity;
import dev.xylonity.companions.common.util.interfaces.ITeslaUtil;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class RecallPlatformRenderer extends GeoBlockRenderer<RecallPlatformBlockEntity> implements ITeslaUtil {

    public RecallPlatformRenderer(BlockEntityRendererProvider.Context rendererDispatcher) {
        super(new RecallPlatformModel());
    }

    @Override
    protected void rotateBlock(Direction facing, PoseStack poseStack) {

    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull RecallPlatformBlockEntity pBlockEntity) {
        return true;
    }

}