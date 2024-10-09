package dev.xylonity.companions.platform;

import net.minecraft.world.item.Item;

import java.util.function.Supplier;

public interface CompanionsPlatform {

    <T extends Item> Supplier<T> registerItem(String id, Supplier<T> item);

}
