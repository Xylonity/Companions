package dev.xylonity.companions.common.entity.ai.mankh.goal;

import dev.xylonity.companions.common.entity.ai.mankh.AbstractMankhAttackGoal;
import dev.xylonity.companions.common.entity.companion.MankhEntity;
import dev.xylonity.companions.common.entity.projectile.LaserRingProjectile;
import dev.xylonity.companions.registry.CompanionsEntities;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class MankhRingAttackGoal extends AbstractMankhAttackGoal {

    public MankhRingAttackGoal(MankhEntity mankh, int minCd, int maxCd) {
        super(mankh, 21, minCd, maxCd);
    }

    @Override
    public void start() {
        super.start();
        mankh.setNoMovement(true);
    }

    @Override
    public void stop() {
        super.stop();
        mankh.setNoMovement(false);
    }

    @Override
    public boolean canUse() {
        return super.canUse() && mankh.getTarget() != null && mankh.distanceTo(mankh.getTarget()) <= 6;
    }

    @Override
    public void tick() {
        super.tick();

        if (mankh.getTarget() != null) {
            Vec3 dir = mankh.getTarget().getEyePosition().subtract(mankh.getEyePosition()).normalize();

            float yaw = (float) (Math.atan2(-dir.x, dir.z) * 180.0 / Math.PI);
            float pitch = (float) (Math.asin(-dir.y) * 180.0 / Math.PI);

            mankh.setYRot(yaw);
            mankh.setXRot(pitch);
            mankh.yRotO = yaw;
            mankh.xRotO = pitch;
        }

        if (mankh.getTarget() != null) {
            LivingEntity e = mankh.getTarget();
            mankh.lookAt(e, 30, 30);
        }

    }

    @Override
    protected void performAttack(LivingEntity target) {
        LaserRingProjectile ring = CompanionsEntities.LASER_RING.get().create(mankh.level());
        if (ring != null) {
            ring.setPos(mankh.position().x, mankh.position().y + 0.15, mankh.position().z);
            ring.setOwner(mankh);
            mankh.level().addFreshEntity(ring);
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