package dev.xylonity.companions.platform;

import dev.xylonity.companions.Companions;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

public class CompanionsForgePlatform implements CompanionsPlatform {

    @Override
    public <T extends Item> Supplier<T> registerItem(String id, Supplier<T> item) {
        return Companions.ITEMS.register(id, item);
    }

}
