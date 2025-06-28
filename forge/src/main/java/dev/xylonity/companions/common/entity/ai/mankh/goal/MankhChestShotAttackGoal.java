package dev.xylonity.companions.common.entity.ai.mankh.goal;

import dev.xylonity.companions.common.entity.ai.mankh.AbstractMankhAttackGoal;
import dev.xylonity.companions.common.entity.custom.MankhEntity;
import dev.xylonity.companions.common.entity.projectile.trigger.LaserTriggerProjectile;
import dev.xylonity.companions.registry.CompanionsEntities;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class MankhChestShotAttackGoal extends AbstractMankhAttackGoal {

    public MankhChestShotAttackGoal(MankhEntity mankh, int minCd, int maxCd) {
        super(mankh, 80, minCd, maxCd);
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
            mankh.getLookControl().setLookAt(e.getX(), e.getY() + e.getBbHeight() * 0.5f, e.getZ(), 6, 90);
            mankh.lookAt(e, 30, 30);
        }
    }

    @Override
    protected void performAttack(LivingEntity target) {
        Vec3 dir = target.getEyePosition().subtract(mankh.getEyePosition()).normalize();

        LaserTriggerProjectile laser = CompanionsEntities.LASER_PROJECTILE.get().create(mankh.level());
        if (laser != null) {
            Vec3 base = mankh.position().add(0, mankh.getBbHeight() * 0.5, 0);
            Vec3 spawn = base.add(dir.scale(0.05));

            laser.setPos(spawn.x, spawn.y, spawn.z);
            laser.setOwner(mankh);
            laser.setTarget(target);
            mankh.level().addFreshEntity(laser);
        }
    }

    @Override
    protected int attackDelay() {
        return 14;
    }

    @Override
    protected int attackType() {
        return 1;
    }
}