package dev.xylonity.companions.client.item.renderer;

import dev.xylonity.companions.client.item.model.GenericPickaxeItemModel;
import dev.xylonity.companions.common.item.generic.GeckoPickAxeItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class GenericPickaxeItemRenderer extends GeoItemRenderer<GeckoPickAxeItem> {
    public GenericPickaxeItemRenderer(String resourceKey) {
        super(new GenericPickaxeItemModel(resourceKey));
    }
}