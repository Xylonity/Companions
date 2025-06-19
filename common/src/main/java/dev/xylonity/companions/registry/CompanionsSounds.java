package dev.xylonity.companions.registry;

import dev.xylonity.companions.CompanionsCommon;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

import java.util.function.Supplier;

public class CompanionsSounds {

    public static void init() { ;; }

    public static final Supplier<SoundEvent> ANGEL_OF_GERTRUDE = registerSound("teddy_transformation", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "angel_of_gertrude")));
    public static final Supplier<SoundEvent> FLIP_CARD = registerSound("flip_card", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "flip_card")));

    private static <T extends SoundEvent> Supplier<T> registerSound(String id, Supplier<T> sound) {
        return CompanionsCommon.COMMON_PLATFORM.registerSound(id, sound);
    }

}
