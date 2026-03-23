package dev.xylonity.companions.client.blockentity.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.block.AbstractTeslaBlock;
import dev.xylonity.companions.common.blockentity.TeslaCoilBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class TeslaCoilModel extends GeoModel<TeslaCoilBlockEntity> {

    @Override
    public ResourceLocation getModelResource(TeslaCoilBlockEntity animatable) {
        String dirName = animatable.getBlockState().getValue(AbstractTeslaBlock.FACING).getName();
        return Companions.of("geo/tesla_coil_block_" + dirName + ".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(TeslaCoilBlockEntity animatable) {
        if (animatable.isActive()) {
            return Companions.of("textures/block/tesla_coil_block_on.png");
        }

        return Companions.of("textures/block/tesla_coil_block.png");
    }

    @Override
    public ResourceLocation getAnimationResource(TeslaCoilBlockEntity animatable) {
        return Companions.of("animations/generic.animation.json");
    }

}