package dev.xylonity.companions.common.particle;

import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.knightlib.common.particle.AbstractRibbonTrailParticle;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import net.minecraft.resources.ResourceLocation;

public class ShadeAltarRibbonParticle extends AbstractRibbonTrailParticle {
    private static final ResourceLocation TEXTURE = new ResourceLocation(CompanionsCommon.MOD_ID, "textures/particle/trail2.png");

    private static final Vec3 DARK_RED = new Vec3(74/255f, 0, 0);

    private final float RED_;
    private final float GREEN_;
    private final float BLUE_;

    public ShadeAltarRibbonParticle(ClientLevel level, Vec3 targetId, float r, float g, float b, double radius) {
        super(level, 0, 0, 0, 0, 0, 0, r, g, b);

        this.RED_ = r;
        this.GREEN_ = g;
        this.BLUE_ = b;

        this.lifetime = 30;
        this.gravity = 0f;

        Vec3 spawnPos = targetId.add(randomOffsetAbove(level.random, radius));
        setPos(spawnPos.x, spawnPos.y, spawnPos.z);

        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        Vec3 dir = targetId.subtract(spawnPos);
        Vec3 vel = dir.normalize().scale(dir.length() / (lifetime - 2));
        this.xd = vel.x;
        this.yd = vel.y;
        this.zd = vel.z;

        savePosInBuffer();
    }

    private static Vec3 randomOffsetAbove(RandomSource rand, double radius) {
        double theta1 = 2 * rand.nextDouble() - 1;
        double theta2 = Math.sqrt(1 - theta1 * theta1);
        double phi = 2 * Math.PI * rand.nextDouble();

        double r = Math.cbrt(rand.nextDouble()) * radius;

        double x = theta2 * Math.cos(phi) * r;
        double y = Math.abs(theta1 * r);
        double z = theta2 * Math.sin(phi) * r;

        return new Vec3(x, y, z);
    }

    @Override
    public void tick() {
        super.tick();

        float lifeFrac = age / (float) lifetime;
        float fadeIn = Math.min(lifeFrac * 2f, 1f);
        float fadeOut = 1f - lifeFrac;
        ribbonAlpha = fadeIn * fadeOut;

        float t = 1f - fadeOut;
        this.r = RED_ * (1 - t) + (float) DARK_RED.x * t;
        this.g = GREEN_ * (1 - t) + (float) DARK_RED.y * t;
        this.b = BLUE_ * (1 - t) + (float) DARK_RED.z * t;
    }

    @Override
    protected float getRibbonHeight() {
        return 0.2f;
    }

    @Override
    protected ResourceLocation getRibbonSprite() {
        return TEXTURE;
    }

    @Override
    protected int totalSegments() {
        return 6;
    }

}