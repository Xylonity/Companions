package dev.xylonity.companions.registry;

import dev.xylonity.companions.CompanionsCommon;
import net.minecraft.core.particles.SimpleParticleType;

import java.util.function.Supplier;

public class CompanionsParticles {

    public static void init() { ;; }

    public static final Supplier<SimpleParticleType> TEDDY_TRANSFORMATION = registerParticle("teddy_transformation", true);
    public static final Supplier<SimpleParticleType> TEDDY_TRANSFORMATION_CLOUD = registerParticle("teddy_transformation_cloud", true);

    private static <T extends SimpleParticleType> Supplier<T> registerParticle(String id, boolean overrideLimiter) {
        return CompanionsCommon.COMMON_PLATFORM.registerParticle(id, overrideLimiter);
    }

}
