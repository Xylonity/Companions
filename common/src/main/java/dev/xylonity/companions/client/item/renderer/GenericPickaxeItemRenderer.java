package dev.xylonity.companions.client.item.renderer;

import dev.xylonity.companions.client.item.model.GenericPickaxeItemModel;
import dev.xylonity.companions.common.item.gecko.GeckoPickaxeItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class GenericPickaxeItemRenderer extends GeoItemRenderer<GeckoPickaxeItem> {
    public GenericPickaxeItemRenderer(String resourceKey) {
        super(new GenericPickaxeItemModel(resourceKey));
    }
}