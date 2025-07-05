package dev.xylonity.companions.common.particle;

import dev.xylonity.companions.CompanionsCommon;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public class SpiralLaserParticle extends AbstractRibbonTrailParticle {

    private static final ResourceLocation TEXTURE = new ResourceLocation(CompanionsCommon.MOD_ID, "textures/particle/trail4.png");

    private static final int TRAIL_SEGS = 16;

    private final Vec3 start;
    private final Vec3 end;
    private final Vec3 dir;
    private final Vec3 perp1;
    private final Vec3 perp2;
    private final double amplitude;
    private final double frequency;

    public SpiralLaserParticle(ClientLevel level, Vec3 start, Vec3 end, double amplitude, double frequency) {
        super(level, start.x, start.y, start.z, 0, 0, 0, 1f, 1f, 1f);
        this.start = start;
        this.end = end;
        this.amplitude = amplitude;
        this.frequency = frequency;

        this.dir = end.subtract(start).normalize();
        Vec3 up = new Vec3(0, 1, 0);

        if (Math.abs(dir.dot(up)) > 0.9) up = new Vec3(1, 0, 0);

        this.perp1 = dir.cross(up).normalize();
        this.perp2 = dir.cross(perp1).normalize();

        this.gravity = 0f;
        this.lifetime = 12;
    }

    @Override
    protected ResourceLocation getRibbonSprite() {
        return TEXTURE;
    }

    @Override
    protected int totalSegments() {
        return Math.min(TRAIL_SEGS, (age + 1) * 3);
    }

    @Override
    public void tick() {
        super.tick();

        this.r = 0.8f ;
        this.g = 50/255f;
        this.b = 54/255f - 0.2f * (float)(Math.sin(age * 0.5) * 0.5 + 0.5);

        this.ribbonAlpha = 0.7f + 0.3f * (float)Math.abs(Math.sin(age * frequency));

        if (age >= lifetime - 4) {
            this.ribbonAlpha *= (lifetime - age) / 4f;
        }

    }

    @Override
    protected int getLightColor(float partialTicks) {
        return LightTexture.FULL_BRIGHT;
    }

    @Override
    protected Vec3 sampleTrailPoint(int idx, float partialTicks) {
        float t = idx / (float)(TRAIL_SEGS - 1);
        Vec3 base = start.lerp(end, t);

        double angle = ((age + partialTicks) * frequency) + t * Math.PI * 2;

        return base.add(perp1.scale(Math.cos(angle)).add(perp2.scale(Math.sin(angle))).scale(amplitude));
    }

    @Override
    protected float getRibbonHeight() {
        return 0.2f * (1f + 0.2f * (float) Math.sin((age + 0.5f) * frequency));
    }

}
