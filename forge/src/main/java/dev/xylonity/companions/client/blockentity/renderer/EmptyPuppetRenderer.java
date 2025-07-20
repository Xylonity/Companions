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

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull EmptyPuppetBlockEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(Companions.MOD_ID, "textures/block/empty_puppet_block.png");
    }

}