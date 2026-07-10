package dev.xylonity.companions.client.blockentity.renderer;

import dev.xylonity.companions.client.blockentity.model.ShadeBatAltarModel;
import dev.xylonity.companions.client.blockentity.model.ShadeMawAltarModel;
import dev.xylonity.companions.common.blockentity.ShadeBatAltarBlockEntity;
import dev.xylonity.companions.common.blockentity.ShadeMawAltarBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class ShadeBatAltarRenderer extends AbstractGeoBlockRenderer<ShadeBatAltarBlockEntity> {

    public ShadeBatAltarRenderer(BlockEntityRendererProvider.Context rendererDispatcher) {
        super(new ShadeBatAltarModel());
    }

}