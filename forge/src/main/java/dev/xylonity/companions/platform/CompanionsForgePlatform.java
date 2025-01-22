package dev.xylonity.companions.platform;

import dev.xylonity.companions.Companions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

@SuppressWarnings("unchecked")
public class CompanionsForgePlatform implements CompanionsPlatform {

    @Override
    public <T extends Item> Supplier<T> registerItem(String id, Supplier<T> item) {
        return Companions.ITEMS.register(id, item);
    }

    @Override
    public <T extends MobEffect> Supplier<T> registerEffect(String id, Supplier<T> effect) {
        return Companions.MOB_EFFECTS.register(id, effect);
    }

    @Override
    public <T extends ParticleType<?>> Supplier<T> registerParticle(String id, boolean overrideLimiter) {
        return Companions.PARTICLES.register(id, () -> (T) new SimpleParticleType(overrideLimiter));
    }

    @Override
    public <T extends CreativeModeTab> Supplier<T> registerCreativeModeTab(String id, Supplier<T> tab) {
        return Companions.CREATIVE_TABS.register(id, tab);
    }

    @Override
    public CreativeModeTab.Builder creativeTabBuilder() {
        return CreativeModeTab.builder();
    }

}
