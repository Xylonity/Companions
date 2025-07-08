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
    public static final Supplier<SoundEvent> BONANZA = registerSound("bonanza", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "bonanza")));
    public static final Supplier<SoundEvent> COIN_CLATTER = registerSound("coin_clatter", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "coin_clatter")));

    public static final Supplier<SoundEvent> DINAMO_STEP = registerSound("dinamo_step", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "dinamo_step")));
    public static final Supplier<SoundEvent> DINAMO_IDLE = registerSound("dinamo_idle", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "dinamo_idle")));
    public static final Supplier<SoundEvent> DINAMO_HURT = registerSound("dinamo_hurt", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "dinamo_hurt")));
    public static final Supplier<SoundEvent> DINAMO_DEATH = registerSound("dinamo_death", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "dinamo_death")));
    public static final Supplier<SoundEvent> DINAMO_ATTACK = registerSound("dinamo_attack", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "dinamo_attack")));

    private static <T extends SoundEvent> Supplier<T> registerSound(String id, Supplier<T> sound) {
        return CompanionsCommon.COMMON_PLATFORM.registerSound(id, sound);
    }

}
