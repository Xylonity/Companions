package dev.xylonity.companions.client.blockentity.renderer;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.client.blockentity.model.CroissantEggModel;
import dev.xylonity.companions.common.blockentity.CroissantEggBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class CroissantEggRenderer extends GeoBlockRenderer<CroissantEggBlockEntity> {

    public CroissantEggRenderer(BlockEntityRendererProvider.Context rendererDispatcher) {
        super(new CroissantEggModel());
    }

}