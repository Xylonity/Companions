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

    private static <T extends SimpleParticleType> Supplier<T> registerParticle(String id, boolean overrideLimiter) {
        return CompanionsCommon.COMMON_PLATFORM.registerParticle(id, overrideLimiter);
    }

}
