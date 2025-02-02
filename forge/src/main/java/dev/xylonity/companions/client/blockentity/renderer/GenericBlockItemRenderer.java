package dev.xylonity.companions.client.blockentity.renderer;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.client.blockentity.model.GenericBlockItemModel;
import dev.xylonity.companions.common.item.GenericBlockItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class GenericBlockItemRenderer extends GeoItemRenderer<GenericBlockItem> {
    public GenericBlockItemRenderer() {
        super(new GenericBlockItemModel());
    }

}
