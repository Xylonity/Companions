package dev.xylonity.companions.registry;

import dev.xylonity.companions.CompanionsCommon;
import net.minecraft.core.particles.SimpleParticleType;

import java.util.function.Supplier;

public class CompanionsParticles {

    public static void init() { ;; }

    public static final Supplier<SimpleParticleType> TEDDY_TRANSFORMATION = registerParticle("teddy_transformation", true);
    public static final Supplier<SimpleParticleType> TEDDY_TRANSFORMATION_CLOUD = registerParticle("teddy_transformation_cloud", true);
    public static final Supplier<SimpleParticleType> ILLAGER_GOLEM_SPARK = registerParticle("illager_golem_spark", true);
    public static final Supplier<SimpleParticleType> DINAMO_SPARK = registerParticle("dinamo_spark", true);
    public static final Supplier<SimpleParticleType> EMBER = registerParticle("ember", true);
    public static final Supplier<SimpleParticleType> BLACK_HOLE_STAR = registerParticle("black_hole_star", true);
    public static final Supplier<SimpleParticleType> BLIZZARD_SNOW = registerParticle("blizzard_snow", true);
    public static final Supplier<SimpleParticleType> BLIZZARD_ICE = registerParticle("blizzard_ice", true);
    public static final Supplier<SimpleParticleType> GOLDEN_ALLAY_TRAIL = registerParticle("golden_allay_trail", true);
    public static final Supplier<SimpleParticleType> CAKE_CREAM = registerParticle("cake_cream", true);
    public static final Supplier<SimpleParticleType> CAKE_CREAM_STRAWBERRY = registerParticle("cake_cream_strawberry", true);
    public static final Supplier<SimpleParticleType> CAKE_CREAM_CHOCOLATE = registerParticle("cake_cream_chocolate", true);
    public static final Supplier<SimpleParticleType> SOUL_FLAME = registerParticle("soul_flame", true);
    public static final Supplier<SimpleParticleType> FIREWORK_TOAD = registerParticle("firework_toad", true);
    public static final Supplier<SimpleParticleType> SHADE_TRAIL = registerParticle("shade_trail", true);
    public static final Supplier<SimpleParticleType> SHADE_SUMMON = registerParticle("shade_summon", true);
    public static final Supplier<SimpleParticleType> HOLINESS_RED_STAR_TRAIL = registerParticle("holiness_red_star_trail", true);
    public static final Supplier<SimpleParticleType> HOLINESS_BLUE_STAR_TRAIL = registerParticle("holiness_blue_star_trail", true);
    public static final Supplier<SimpleParticleType> BLINK = registerParticle("blink", true);
    public static final Supplier<SimpleParticleType> LASER_SPARK = registerParticle("laser_spark", true);
    public static final Supplier<SimpleParticleType> EMBER_POLE_EXPLOSION = registerParticle("ember_pole_explosion", true);
    public static final Supplier<SimpleParticleType> RESPAWN_TOTEM = registerParticle("respawn_totem", true);

    private static <T extends SimpleParticleType> Supplier<T> registerParticle(String id, boolean overrideLimiter) {
        return CompanionsCommon.COMMON_PLATFORM.registerParticle(id, overrideLimiter);
    }

}
