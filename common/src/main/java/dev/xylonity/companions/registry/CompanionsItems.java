package dev.xylonity.companions.registry;

import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.common.item.EarthQuakeItem;
import dev.xylonity.companions.common.item.EternalLighter;
import dev.xylonity.companions.common.item.TestItem;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

public class CompanionsItems {

    public static void init() { ;; }

    public static final Supplier<Item> TEST = registerItem("test", () -> new EarthQuakeItem(new Item.Properties()));
    public static final Supplier<Item> TEST2 = registerItem("test2", () -> new TestItem(new Item.Properties()));
    public static final Supplier<Item> ETERNAL_LIGHTER = registerItem("eternal_lighter", () -> new EternalLighter(new Item.Properties()));

    private static <T extends Item> Supplier<T> registerItem(String id, Supplier<T> item) {
        return CompanionsCommon.COMMON_PLATFORM.registerItem(id, item);
    }

}
