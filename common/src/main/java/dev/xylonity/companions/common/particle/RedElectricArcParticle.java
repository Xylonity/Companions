package dev.xylonity.companions.common.particle;

import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.knightlib.common.particle.AbstractRibbonTrailParticle;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class RedElectricArcParticle extends AbstractRibbonTrailParticle {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CompanionsCommon.MOD_ID, "textures/particle/trail4.png");

    private static final int TRAIL_SEGMS = 12;

    private final Vec3 start;
    private final Vec3 end;
    private final double arcY;
    private final Vec3[] noiseOffsets;

    private static final float RED_   =  235/255f;
    private static final float GREEN_ =  90/255f;
    private static final float BLUE_  =  55/255f;

    private final boolean whiteStart;

    private final long seed;
    private final double amp;

    public RedElectricArcParticle(ClientLevel level, Vec3 start, Vec3 end, double arcHeight, double amp, boolean whiteStart, int lifetime) {
        super(level, start.x, start.y, start.z, 0, 0, 0, 1f, 1f, 1f);

        this.start = start;
        this.end = end;
        this.arcY = arcHeight;
        this.whiteStart = whiteStart;

        this.seed = level.random.nextLong();
        this.amp = amp;

        this.noiseOffsets = new Vec3[TRAIL_SEGMS];
        for (int i = 0; i < TRAIL_SEGMS; i++) {
            long h = level.random.nextLong() ^ (i * 0x9E3779B9L);
            // fmix64 (scrapped again from ElectricRibbonTrailParticle)
            h ^= (h >>> 33);
            h *= 0xff51afd7ed558ccdL;
            h ^= (h >>> 33);
            h *= 0xc4ceb9fe1a85ec53L;
            h ^= (h >>> 33);

            double ox = (((h >> 42) & 0x3FF) / 1023.0 - 0.5) * amp;
            double oy = (((h >> 21) & 0x3FF) / 1023.0 - 0.5) * amp;
            double oz = ((h & 0x3FF) / 1023.0 - 0.5) * amp;
            noiseOffsets[i] = new Vec3(ox, oy, oz);
        }

        this.gravity = 0f;
        this.lifetime = lifetime; // default 8
        this.ribbonAlpha = 1f;

        setBoundingBox(new AABB(start, end).inflate(amp).expandTowards(0, arcY, 0));
    }

    @Override
    protected ResourceLocation getRibbonSprite() {
        return TEXTURE;
    }

    @Override
    protected int totalSegments() {
        return Math.min(TRAIL_SEGMS, (age + 1) * 3);
    }

    @Override
    public void tick() {
        super.tick();

        int rem = lifetime;
        int dWhite = Math.min(1, rem);
        rem -= dWhite;
        int dFadeIn = Math.min(whiteStart ? (int) (lifetime * 0.4) : 3, rem);
        rem -= dFadeIn;
        int dDarken = whiteStart ? Math.min(5, rem) : lifetime - 5;

        int fadein = dWhite + dFadeIn;
        int toDarken = fadein + dDarken;

        if (whiteStart) {
            if (age < dWhite) {
                this.r = 1f; this.g = 1f; this.b = 1f;
            } else if (age < fadein) {
                float p = (age - dWhite) / (float) dFadeIn;
                this.r = 1f - p * (1f - RED_);
                this.g = 1f - p * (1f - GREEN_);
                this.b = 1f - p * (1f - BLUE_);
            } else if (age < toDarken) {
                float factor = 1f - ((age - fadein) / (float) dDarken) * 0.4f;
                this.r = RED_ * factor;
                this.g = GREEN_ * factor;
                this.b = BLUE_ * factor;
            } else {
                float factor = 0.3f;
                this.r = RED_ * factor;
                this.g = GREEN_ * factor;
                this.b = BLUE_ * factor;
            }
        } else {
            if (age < toDarken) {
                float factor = 1f - (age / (float) toDarken) * 0.4f;
                this.r = RED_ * factor;
                this.g = GREEN_ * factor;
                this.b = BLUE_ * factor;
            } else {
                float factor = 0.3f;
                this.r = RED_ * factor;
                this.g = GREEN_ * factor;
                this.b = BLUE_ * factor;
            }
        }

        int fade = Math.min(6, lifetime);
        if (age >= lifetime - fade) {
            this.ribbonAlpha = Math.max(0f, 1f - ((age - (lifetime - fade)) / (float) fade));
        } else {
            this.ribbonAlpha = 1f;
        }

    }

    @Override
    protected int getLightColor(float $$0) {
        return LightTexture.FULL_BRIGHT;
    }

    @Override
    protected Vec3 sampleTrailPoint(int idx, float tick) {
        float t = idx / (float) (TRAIL_SEGMS - 2);
        Vec3 base = start.lerp(end, t).add(0, Math.sin(Math.PI * t) * arcY, 0);

        long h = seed ^ ((long) age << 1) ^ (idx * 0x9E3779B9L);
        h ^= (h >>> 33);
        h *= 0xff51afd7ed558ccdL;
        h ^= (h >>> 33);
        h *= 0xc4ceb9fe1a85ec53L;
        h ^= (h >>> 33);

        double ox = (((h >> 42) & 0x3FF) / 1023.0 - 0.5) * amp;
        double oy = (((h >> 21) & 0x3FF) / 1023.0 - 0.5) * amp;
        double oz = ((h & 0x3FF) / 1023.0 - 0.5) * amp;

        return base.add(ox, oy, oz);
    }

    @Override
    protected float getRibbonHeight() {
        return 0.28f;
    }

}
