package dev.xylonity.companions.client.blockentity.renderer;

import dev.xylonity.companions.client.blockentity.model.CroissantEggModel;
import dev.xylonity.companions.client.blockentity.model.RespawnTotemModel;
import dev.xylonity.companions.common.blockentity.CroissantEggBlockEntity;
import dev.xylonity.companions.common.blockentity.RespawnTotemBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class RespawnTotemRenderer extends GeoBlockRenderer<RespawnTotemBlockEntity> {

    public RespawnTotemRenderer(BlockEntityRendererProvider.Context rendererDispatcher) {
        super(new RespawnTotemModel());
    }

}