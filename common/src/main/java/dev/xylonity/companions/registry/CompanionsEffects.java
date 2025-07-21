package dev.xylonity.companions.registry;

import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.common.effect.ElectroshockEffect;
import dev.xylonity.companions.common.effect.FireMarkEffect;
import dev.xylonity.companions.common.effect.PhantomEffect;
import dev.xylonity.companions.common.effect.VoodooEffect;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;

import java.util.function.Supplier;

public class CompanionsEffects {

    public static void init() { ;; }

    public static final Holder<MobEffect> FIRE_MARK = registerEffect("fire_mark", FireMarkEffect::new);
    public static final Holder<MobEffect> ELECTROSHOCK = registerEffect("electroshock", ElectroshockEffect::new);
    public static final Holder<MobEffect> VOODOO = registerEffect("voodoo", VoodooEffect::new);
    public static final Holder<MobEffect> PHANTOM = registerEffect("phantom", PhantomEffect::new);

    private static <T extends MobEffect> Holder<T> registerEffect(String id, Supplier<T> effect) {
        return CompanionsCommon.COMMON_PLATFORM.registerEffect(id, effect);
    }

}
