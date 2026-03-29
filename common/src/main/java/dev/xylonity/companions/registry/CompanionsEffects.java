package dev.xylonity.companions.registry;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.effect.ElectroshockEffect;
import dev.xylonity.companions.common.effect.FireMarkEffect;
import dev.xylonity.companions.common.effect.PhantomEffect;
import dev.xylonity.companions.common.effect.VoodooEffect;
import dev.xylonity.knightlib.api.registrar.ResourceDispatcher;
import dev.xylonity.knightlib.api.registrar.ResourceEntry;
import dev.xylonity.knightlib.api.registrar.ResourceRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;

import java.util.function.Supplier;

public class CompanionsEffects {

    public static final ResourceRegistry<MobEffect> EFFECTS = ResourceDispatcher.create(BuiltInRegistries.MOB_EFFECT, Companions.MOD_ID);

    public static final ResourceEntry<MobEffect> FIRE_MARK = EFFECTS.register("fire_mark", FireMarkEffect::new);
    public static final ResourceEntry<MobEffect> ELECTROSHOCK = EFFECTS.register("electroshock", ElectroshockEffect::new);
    public static final ResourceEntry<MobEffect> VOODOO = EFFECTS.register("voodoo", VoodooEffect::new);
    public static final ResourceEntry<MobEffect> PHANTOM = EFFECTS.register("phantom", PhantomEffect::new);

}
