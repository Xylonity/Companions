package dev.xylonity.companions.registry;

import dev.xylonity.companions.Companions;
import dev.xylonity.knightlib.KnightLib;
import dev.xylonity.knightlib.api.registrar.ResourceDispatcher;
import dev.xylonity.knightlib.api.registrar.ResourceEntry;
import dev.xylonity.knightlib.api.registrar.ResourceRegistry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;

public class CompanionsParticles {

    public static final ResourceRegistry<ParticleType<?>> PARTICLES = ResourceDispatcher.create(BuiltInRegistries.PARTICLE_TYPE, Companions.MOD_ID);

    public static final ResourceEntry<SimpleParticleType> TEDDY_TRANSFORMATION = PARTICLES.register("teddy_transformation", KnightLib.PLATFORM.createParticle(true));
    public static final ResourceEntry<SimpleParticleType> TEDDY_TRANSFORMATION_CLOUD = PARTICLES.register("teddy_transformation_cloud", KnightLib.PLATFORM.createParticle(true));
    public static final ResourceEntry<SimpleParticleType> ILLAGER_GOLEM_SPARK = PARTICLES.register("illager_golem_spark", KnightLib.PLATFORM.createParticle(true));
    public static final ResourceEntry<SimpleParticleType> DINAMO_SPARK = PARTICLES.register("dinamo_spark", KnightLib.PLATFORM.createParticle(true));
    public static final ResourceEntry<SimpleParticleType> EMBER = PARTICLES.register("ember", KnightLib.PLATFORM.createParticle(true));
    public static final ResourceEntry<SimpleParticleType> BLACK_HOLE_STAR = PARTICLES.register("black_hole_star", KnightLib.PLATFORM.createParticle(true));
    public static final ResourceEntry<SimpleParticleType> BLIZZARD_SNOW = PARTICLES.register("blizzard_snow", KnightLib.PLATFORM.createParticle(true));
    public static final ResourceEntry<SimpleParticleType> BLIZZARD_ICE = PARTICLES.register("blizzard_ice", KnightLib.PLATFORM.createParticle(true));
    public static final ResourceEntry<SimpleParticleType> GOLDEN_ALLAY_TRAIL = PARTICLES.register("golden_allay_trail", KnightLib.PLATFORM.createParticle(true));
    public static final ResourceEntry<SimpleParticleType> CAKE_CREAM = PARTICLES.register("cake_cream", KnightLib.PLATFORM.createParticle(true));
    public static final ResourceEntry<SimpleParticleType> CAKE_CREAM_STRAWBERRY = PARTICLES.register("cake_cream_strawberry", KnightLib.PLATFORM.createParticle(true));
    public static final ResourceEntry<SimpleParticleType> CAKE_CREAM_CHOCOLATE = PARTICLES.register("cake_cream_chocolate", KnightLib.PLATFORM.createParticle(true));
    public static final ResourceEntry<SimpleParticleType> SOUL_FLAME = PARTICLES.register("soul_flame", KnightLib.PLATFORM.createParticle(true));
    public static final ResourceEntry<SimpleParticleType> FIREWORK_TOAD = PARTICLES.register("firework_toad", KnightLib.PLATFORM.createParticle(true));
    public static final ResourceEntry<SimpleParticleType> SHADE_TRAIL = PARTICLES.register("shade_trail", KnightLib.PLATFORM.createParticle(true));
    public static final ResourceEntry<SimpleParticleType> SHADE_SUMMON = PARTICLES.register("shade_summon", KnightLib.PLATFORM.createParticle(true));
    public static final ResourceEntry<SimpleParticleType> HOLINESS_RED_STAR_TRAIL = PARTICLES.register("holiness_red_star_trail", KnightLib.PLATFORM.createParticle(true));
    public static final ResourceEntry<SimpleParticleType> HOLINESS_BLUE_STAR_TRAIL = PARTICLES.register("holiness_blue_star_trail", KnightLib.PLATFORM.createParticle(true));
    public static final ResourceEntry<SimpleParticleType> BLINK = PARTICLES.register("blink", KnightLib.PLATFORM.createParticle(true));
    public static final ResourceEntry<SimpleParticleType> LASER_SPARK = PARTICLES.register("laser_spark", KnightLib.PLATFORM.createParticle(true));
    public static final ResourceEntry<SimpleParticleType> EMBER_POLE_EXPLOSION = PARTICLES.register("ember_pole_explosion", KnightLib.PLATFORM.createParticle(true));
    public static final ResourceEntry<SimpleParticleType> RESPAWN_TOTEM = PARTICLES.register("respawn_totem", KnightLib.PLATFORM.createParticle(true));

}
