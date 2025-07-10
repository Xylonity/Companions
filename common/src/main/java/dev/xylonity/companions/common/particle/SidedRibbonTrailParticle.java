package dev.xylonity.companions.common.particle;

import dev.xylonity.companions.CompanionsCommon;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class SidedRibbonTrailParticle extends AbstractRibbonTrailParticle {

    private static final ResourceLocation TEXTURE = new ResourceLocation(CompanionsCommon.MOD_ID, "textures/particle/trail.png");

    protected final float radius;
    protected final float height;
    protected final int targetId;
    protected final float startYaw;
    protected final float yawSpeed;

    protected float ribbonHeight;
    protected int side;

    public SidedRibbonTrailParticle(ClientLevel level, double x, double y, double z, float r, float g, float b, float radius, float height, int targetId, int side) {
        super(level, x, y, z, 0, 0, 0, r, g, b);

        this.radius = radius;
        this.height = height;
        this.targetId = targetId;
        this.side = side;

        this.gravity = 0;
        this.lifetime = 60;
        this.startYaw = level.random.nextFloat() * 360f;
        this.yawSpeed = (5 + level.random.nextFloat() * 5) * (level.random.nextBoolean() ? 1f : -1f);

        this.ribbonHeight = 0.35f;

        setPos(targetPos().x, targetPos().y, targetPos().z);
    }

    @Override
    public void tick() {
        super.tick();

        if (getTarget() == null && targetId != -1) {
            remove();
            return;
        }

        ribbonAlpha = 1f - age / (float) lifetime;

        setPos(targetPos().x, targetPos().y, targetPos().z);
    }

    protected Vec3 targetPos() {
        if (getTarget() == null) return new Vec3(x, y, z);

        Vec3 dir = getTarget().getDeltaMovement().normalize();

        if (dir.lengthSqr() < 1e-6) return new Vec3(x, y, z);

        Vec3 perp = new Vec3(-dir.z, 0, dir.x).normalize().scale(1.5);
        double y = getTarget().getY();

        Vec3 right = new Vec3(getTarget().getX(), y, getTarget().getZ()).add(perp);
        Vec3 left = new Vec3(getTarget().getX(), y, getTarget().getZ()).subtract(perp);

        if (side == 0) {
            return new Vec3(left.x, left.y, left.z);
        } else {
            return new Vec3(right.x, right.y, right.z);
        }
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