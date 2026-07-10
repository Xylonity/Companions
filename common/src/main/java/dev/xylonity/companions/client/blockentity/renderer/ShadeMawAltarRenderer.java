package dev.xylonity.companions.client.blockentity.renderer;

import dev.xylonity.companions.client.blockentity.model.ShadeMawAltarModel;
import dev.xylonity.companions.common.blockentity.ShadeMawAltarBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class ShadeMawAltarRenderer extends AbstractGeoBlockRenderer<ShadeMawAltarBlockEntity> {

    public ShadeMawAltarRenderer(BlockEntityRendererProvider.Context rendererDispatcher) {
        super(new ShadeMawAltarModel());
    }

}