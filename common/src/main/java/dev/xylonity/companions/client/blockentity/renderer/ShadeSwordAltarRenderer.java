package dev.xylonity.companions.client.blockentity.renderer;

import dev.xylonity.companions.client.blockentity.model.ShadeSwordAltarModel;
import dev.xylonity.companions.common.blockentity.ShadeSwordAltarBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class ShadeSwordAltarRenderer extends AbstractGeoBlockRenderer<ShadeSwordAltarBlockEntity> {

    public ShadeSwordAltarRenderer(BlockEntityRendererProvider.Context rendererDispatcher) {
        super(new ShadeSwordAltarModel());
    }

}