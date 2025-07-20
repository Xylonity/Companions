package dev.xylonity.companions.common.particle;

import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.knightlib.common.particle.AbstractRibbonTrailParticle;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class BaseRibbonTrailParticle extends AbstractRibbonTrailParticle {

    private static final ResourceLocation TEXTURE = new ResourceLocation(CompanionsCommon.MOD_ID, "textures/particle/trail.png");

    protected final float radius;
    protected final float height;
    protected final int targetId;
    protected final float startYaw;
    protected final float yawSpeed;

    protected float ribbonHeight;

    public BaseRibbonTrailParticle(ClientLevel level, double x, double y, double z, float r, float g, float b, float radius, float height, int targetId) {
        super(level, x, y, z, 0, 0, 0, r, g, b);

        this.radius = radius;
        this.height = height;
        this.targetId = targetId;

        this.gravity = 0;
        this.lifetime = 60;
        this.startYaw = level.random.nextFloat() * 360f;
        this.yawSpeed = (5 + level.random.nextFloat() * 5) * (level.random.nextBoolean() ? 1f : -1f);

        this.ribbonHeight = 0.35f;

        Vec3 p = orbitPos();
        setPos(p.x, p.y, p.z);
    }

    public BaseRibbonTrailParticle(ClientLevel level, double x, double y, double z, float r, float g, float b, float radius, float height, int targetId, float trailHeight) {
        this(level, x, y, z, r, g, b, radius, height, targetId);

        this.ribbonHeight = trailHeight;
    }

    @Override
    public void tick() {
        super.tick();

        if (getTarget() == null && targetId != -1) {
            remove();
            return;
        }

        ribbonAlpha = 1f - age / (float) lifetime;

        setPos(orbitPos().x, orbitPos().y, orbitPos().z);
    }

    private Vec3 orbitPos() {
        double alpha = Math.toRadians(startYaw + yawSpeed * age);
        Vec3 off = new Vec3(Math.cos(alpha) * radius, height * Math.sin(age * 0.1f), Math.sin(alpha) * radius);
        return targetPos().add(off);
    }

    protected Vec3 targetPos() {
        return getTarget() != null ? getTarget().position().add(0, getTarget().getBbHeight() * 0.5, 0) : new Vec3(x, y, z);
    }

    @Nullable
    private Entity getTarget() {
        return targetId == -1 ? null : level.getEntity(targetId);
    }

    @Override
    protected float getRibbonHeight() {
        return ribbonHeight;
    }

    @Override
    protected ResourceLocation getRibbonSprite() {
        return TEXTURE;
    }

    @Override
    protected int totalSegments() {
        return 5;
    }

}