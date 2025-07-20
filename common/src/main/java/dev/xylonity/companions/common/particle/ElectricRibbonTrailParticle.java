package dev.xylonity.companions.common.particle;

import dev.xylonity.companions.CompanionsCommon;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public class ElectricRibbonTrailParticle extends BaseRibbonTrailParticle {

    private static final ResourceLocation TEXTURE = new ResourceLocation(CompanionsCommon.MOD_ID, "textures/particle/trail.png");

    private final double amp;
    private final long seed;

    public ElectricRibbonTrailParticle(ClientLevel level, double x, double y, double z, float r, float g, float b, float radius, float height, int targetId, float trailHeight, double amplitude) {
        super(level, x, y, z, r, g, b, radius, height, targetId, trailHeight);
        this.amp = amplitude;
        this.seed = level.random.nextLong();
        this.gravity = 0f;
        this.lifetime = 20 + level.random.nextInt(10);
    }

    @Override
    protected ResourceLocation getRibbonSprite() {
        return TEXTURE;
    }

    @Override
    protected int totalSegments() {
        return 8;
    }

    @Override
    protected int segmentInterval() {
        return 2;
    }

    @Override
    protected Vec3 sampleTrailPoint(int idx, float tick) {
        Vec3 base = super.sampleTrailPoint(idx, tick);

        // Deterministic hash using an exact seed to prevent visual flickering to the power of delta (the hex code)
        // so the randomized values are distributed correctly along the pattern
        long h = seed ^ ((long) age >> 1) ^ (idx * 0x9E3779B9L);
        // fmix64 (splitmix64) impl adaptation
        // https://github.com/zendesk/maxwell/blob/master/src/main/java/com/zendesk/maxwell/util/MurmurHash3.java
        h ^= (h >>> 33);
        h *= 0xff51afd7ed558ccdL;
        h ^= (h >>> 33);
        h *= 0xc4ceb9fe1a85ec53L;
        h ^= (h >>> 33);

        // normalized offset
        double ox = (((h >> 42) & 0x3FF) / 1023.0 - 0.5) * amp;
        double oy = (((h >> 21) & 0x3FF) / 1023.0 - 0.5) * amp;
        double oz = ((h & 0x3FF) / 1023.0 - 0.5) * amp;

        return base.add(ox, oy, oz);
    }

}
