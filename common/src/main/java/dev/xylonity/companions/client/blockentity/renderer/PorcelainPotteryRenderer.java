package dev.xylonity.companions.client.blockentity.renderer;

import dev.xylonity.companions.client.blockentity.model.EmptyPuppetModel;
import dev.xylonity.companions.client.blockentity.model.PorcelainPotteryModel;
import dev.xylonity.companions.common.blockentity.EmptyPuppetBlockEntity;
import dev.xylonity.companions.common.blockentity.PorcelainPotteryBlockEntity;
import dev.xylonity.companions.common.item.PorcelainPottery;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class PorcelainPotteryRenderer extends AbstractGeoBlockRenderer<PorcelainPotteryBlockEntity> {

    public PorcelainPotteryRenderer(BlockEntityRendererProvider.Context rendererDispatcher) {
        super(new PorcelainPotteryModel());
    }

}