package dev.xylonity.companions.client.blockentity.renderer;

import dev.xylonity.companions.client.blockentity.model.ShadeBatAltarModel;
import dev.xylonity.companions.client.blockentity.model.ShadeMawAltarModel;
import dev.xylonity.companions.common.blockentity.ShadeBatAltarBlockEntity;
import dev.xylonity.companions.common.blockentity.ShadeMawAltarBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class ShadeBatAltarRenderer extends GeoBlockRenderer<ShadeBatAltarBlockEntity> {

    public ShadeBatAltarRenderer(BlockEntityRendererProvider.Context rendererDispatcher) {
        super(new ShadeBatAltarModel());
    }

}