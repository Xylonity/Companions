package dev.xylonity.companions.client.blockentity.renderer;

import dev.xylonity.companions.client.blockentity.model.ShadeSwordAltarModel;
import dev.xylonity.companions.common.blockentity.ShadeSwordAltarBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class ShadeSwordAltarRenderer extends GeoBlockRenderer<ShadeSwordAltarBlockEntity> {

    public ShadeSwordAltarRenderer(BlockEntityRendererProvider.Context rendererDispatcher) {
        super(new ShadeSwordAltarModel());
    }

}