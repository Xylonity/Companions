package dev.xylonity.companions.common.entity.ai.cloak.goal;

import dev.xylonity.companions.common.entity.ai.cloak.AbstractCloakAttackGoal;
import dev.xylonity.companions.common.entity.custom.CloakEntity;
import dev.xylonity.companions.common.entity.custom.MankhEntity;
import dev.xylonity.companions.common.entity.projectile.LaserRingProjectile;
import dev.xylonity.companions.registry.CompanionsEntities;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class CloakRedStarAttackGoal extends AbstractCloakAttackGoal {

    public CloakRedStarAttackGoal(CloakEntity cloak, int minCd, int maxCd) {
        super(cloak, 21, minCd, maxCd);
    }

    @Override
    public void start() {
        super.start();
        cloak.setNoMovement(true);
    }

    @Override
    public void stop() {
        super.stop();
        cloak.setNoMovement(false);
    }

    @Override
    public boolean canUse() {
        return super.canUse() && cloak.getTarget() != null && cloak.distanceTo(cloak.getTarget()) <= 6;
    }

    @Override
    public void tick() {
        super.tick();

        if (cloak.getTarget() != null) {
            Vec3 dir = cloak.getTarget().getEyePosition().subtract(cloak.getEyePosition()).normalize();

            float yaw = (float) (Math.atan2(-dir.x, dir.z) * 180.0 / Math.PI);
            float pitch = (float) (Math.asin(-dir.y) * 180.0 / Math.PI);

            cloak.setYRot(yaw);
            cloak.setXRot(pitch);
            cloak.yRotO = yaw;
            cloak.xRotO = pitch;
        }

        if (cloak.getTarget() != null) {
            LivingEntity e = cloak.getTarget();
            cloak.getLookControl().setLookAt(e.getX(), e.getY() + e.getBbHeight() * 0.5f, e.getZ(), 6, 90);
            cloak.lookAt(e, 30, 30);
        }
    }

    @Override
    protected void performAttack(LivingEntity target) {
        LaserRingProjectile ring = CompanionsEntities.LASER_RING.get().create(cloak.level());
        if (ring != null) {
            ring.setPos(cloak.position().x, cloak.position().y + 0.15, cloak.position().z);
            ring.setOwner(cloak);
            cloak.level().addFreshEntity(ring);
        }
    }

    @Override
    protected int attackDelay() {
        return 14;
    }

    @Override
    protected int attackType() {
        return 2;
    }
}