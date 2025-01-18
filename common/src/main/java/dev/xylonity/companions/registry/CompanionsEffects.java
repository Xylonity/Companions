package dev.xylonity.companions.registry;

import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.common.effect.DarkAbsorption;
import dev.xylonity.companions.common.item.EarthQuakeItem;
import dev.xylonity.companions.common.item.TestItem;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

public class CompanionsEffects {

    public static void init() { ;; }

    public static final Supplier<MobEffect> BLACK_ABSORPTION = registerEffect("black_absorption", DarkAbsorption::new);

    private static <T extends MobEffect> Supplier<T> registerEffect(String id, Supplier<T> effect) {
        return CompanionsCommon.COMMON_PLATFORM.registerEffect(id, effect);
    }

}
