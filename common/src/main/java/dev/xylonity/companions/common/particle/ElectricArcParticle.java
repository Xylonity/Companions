package dev.xylonity.companions.common.particle;

import dev.xylonity.companions.CompanionsCommon;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public class ElectricArcParticle extends AbstractRibbonTrailParticle {

    private static final ResourceLocation TEXTURE = new ResourceLocation(CompanionsCommon.MOD_ID, "textures/particle/trail.png");

    private static final int TRAIL_SEGMS = 12;

    private final Vec3 start;
    private final Vec3 end;
    private final double arcY;
    private final Vec3[] noiseOffsets;

    private static final float RED_ = 0.61f;
    private static final float GREEN_ = 0.853f; // whats your problem green
    private static final float BLUE_ = 1f;

    public ElectricArcParticle(ClientLevel level, Vec3 start, Vec3 end, double arcHeight, double amp) {
        super(level, start.x, start.y, start.z, 0, 0, 0, 1f, 1f, 1f);

        this.start = start;
        this.end = end;
        this.arcY = arcHeight;

        // scrapped from ElectricRibbonTrailParticle
        this.noiseOffsets = new Vec3[TRAIL_SEGMS];
        for (int i = 0; i < TRAIL_SEGMS; i++) {
            long h = level.random.nextLong() ^ (i * 0x9E3779B9L);
            // fmix64 (splitmix64) variation
            // https://github.com/zendesk/maxwell/blob/master/src/main/java/com/zendesk/maxwell/util/MurmurHash3.java
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
        this.lifetime = 8;
        this.ribbonAlpha = 1f;
    }

    @Override
    protected ResourceLocation getRibbonSprite() {
        return TEXTURE;
    }

    @Override
    protected int totalSegments() {
        return Math.min(TRAIL_SEGMS, (age + 1) * 2);
    }

    @Override
    public void tick() {

        super.tick();

        int rem = lifetime;

        int dWhite = Math.min(2, rem);
        rem -= dWhite;

        int dFadeIn = Math.min(3, rem);
        rem -= dFadeIn;

        int dDarken = Math.min(5, rem);

        int fadein = dWhite + dFadeIn;
        int toDarken = fadein + dDarken;

        // hardcoded coloring. Starts white, goes to the selected RGB sequence up there and goes dark from there
        if (age < dWhite) {
            this.r = 1f;
            this.g = 1f;
            this.b = 1f;
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

        // Fades the last 6 ticks (+-) of total life
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
        Vec3 noise = noiseOffsets[idx];
        return base.add(noise.x, noise.y, noise.z);
    }

    @Override
    protected float getRibbonHeight() {
        return 0.28f;
    }

}