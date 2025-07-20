package dev.xylonity.companions.client.blockentity.renderer;

import dev.xylonity.companions.client.blockentity.model.GenericBlockItemModel;
import dev.xylonity.companions.common.item.blockitem.GenericBlockItem;
import net.minecraft.world.level.block.Block;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class GenericBlockItemRenderer extends GeoItemRenderer<GenericBlockItem> {
    public GenericBlockItemRenderer(String name) {
        super(new GenericBlockItemModel(name));
    }

}
