package dev.xylonity.companions.client.blockentity.renderer;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.client.blockentity.model.CroissantEggModel;
import dev.xylonity.companions.common.blockentity.CroissantEggBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class CroissantEggRenderer extends AbstractGeoBlockRenderer<CroissantEggBlockEntity> {

    public CroissantEggRenderer(BlockEntityRendererProvider.Context rendererDispatcher) {
        super(new CroissantEggModel());
    }

}