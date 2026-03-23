package dev.xylonity.companions.client.blockentity.renderer;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.client.blockentity.model.EmptyPuppetModel;
import dev.xylonity.companions.common.blockentity.EmptyPuppetBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class EmptyPuppetRenderer extends GeoBlockRenderer<EmptyPuppetBlockEntity> {

    public EmptyPuppetRenderer(BlockEntityRendererProvider.Context rendererDispatcher) {
        super(new EmptyPuppetModel());
    }

}