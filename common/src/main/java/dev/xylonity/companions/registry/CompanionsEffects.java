package dev.xylonity.companions.registry;

import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.common.effect.ElectroshockEffect;
import dev.xylonity.companions.common.effect.FireMarkEffect;
import dev.xylonity.companions.common.effect.VoodooEffect;
import net.minecraft.world.effect.MobEffect;

import java.util.function.Supplier;

public class CompanionsEffects {

    public static void init() { ;; }

    public static final Supplier<MobEffect> FIRE_MARK = registerEffect("fire_mark", FireMarkEffect::new);
    public static final Supplier<MobEffect> ELECTROSHOCK = registerEffect("electroshock", ElectroshockEffect::new);
    public static final Supplier<MobEffect> VOODOO = registerEffect("voodoo", VoodooEffect::new);

    private static <T extends MobEffect> Supplier<T> registerEffect(String id, Supplier<T> effect) {
        return CompanionsCommon.COMMON_PLATFORM.registerEffect(id, effect);
    }

}
