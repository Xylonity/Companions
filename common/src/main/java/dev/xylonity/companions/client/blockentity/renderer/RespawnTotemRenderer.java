package dev.xylonity.companions.client.blockentity.renderer;

import dev.xylonity.companions.client.blockentity.model.RespawnTotemModel;
import dev.xylonity.companions.common.blockentity.RespawnTotemBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class RespawnTotemRenderer extends AbstractGeoBlockRenderer<RespawnTotemBlockEntity> {

    public RespawnTotemRenderer(BlockEntityRendererProvider.Context rendererDispatcher) {
        super(new RespawnTotemModel());
    }

}