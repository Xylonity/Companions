package dev.xylonity.companions.client.blockentity.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.common.block.AbstractTeslaBlock;
import dev.xylonity.companions.common.blockentity.PlasmaLampBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class PlasmaLampModel extends GeoModel<PlasmaLampBlockEntity> {

    @Override
    public ResourceLocation getModelResource(PlasmaLampBlockEntity animatable) {
        String dirName = animatable.getBlockState().getValue(AbstractTeslaBlock.FACING).getName();
        return ResourceLocation.fromNamespaceAndPath(Companions.MOD_ID, "geo/plasma_lamp_block_" + dirName + ".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(PlasmaLampBlockEntity animatable) {
        if (animatable.isActive()) {
            return ResourceLocation.fromNamespaceAndPath(CompanionsCommon.MOD_ID, "textures/block/plasma_lamp_block_on.png");
        }

        return ResourceLocation.fromNamespaceAndPath(Companions.MOD_ID, "textures/block/plasma_lamp_block.png");
    }

    @Override
    public ResourceLocation getAnimationResource(PlasmaLampBlockEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(Companions.MOD_ID, "animations/generic.animation.json");
    }

}