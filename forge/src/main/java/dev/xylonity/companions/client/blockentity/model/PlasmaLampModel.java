package dev.xylonity.companions.client.blockentity.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.common.block.AbstractTeslaBlock;
import dev.xylonity.companions.common.blockentity.PlasmaLampBlockEntity;
import dev.xylonity.companions.common.tesla.TeslaConnectionManager;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class PlasmaLampModel extends GeoModel<PlasmaLampBlockEntity> {

    @Override
    public ResourceLocation getModelResource(PlasmaLampBlockEntity animatable) {
        String dirName = animatable.getBlockState().getValue(AbstractTeslaBlock.FACING).getName();
        return new ResourceLocation(Companions.MOD_ID, "geo/plasma_lamp_block_" + dirName + ".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(PlasmaLampBlockEntity animatable) {
        if (animatable.isActive()) {
            return new ResourceLocation(CompanionsCommon.MOD_ID, "textures/block/plasma_lamp_block_on.png");
        }

        return new ResourceLocation(Companions.MOD_ID, "textures/block/plasma_lamp_block.png");
    }

    @Override
    public ResourceLocation getAnimationResource(PlasmaLampBlockEntity animatable) {
        return new ResourceLocation(Companions.MOD_ID, "animations/generic.animation.json");
    }

}