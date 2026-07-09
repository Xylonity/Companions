package dev.xylonity.companions.client.blockentity.renderer;

import dev.xylonity.companions.client.blockentity.model.ShadeMawAltarModel;
import dev.xylonity.companions.common.blockentity.ShadeMawAltarBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class ShadeMawAltarRenderer extends GeoBlockRenderer<ShadeMawAltarBlockEntity> {

    public ShadeMawAltarRenderer(BlockEntityRendererProvider.Context rendererDispatcher) {
        super(new ShadeMawAltarModel());
    }

}