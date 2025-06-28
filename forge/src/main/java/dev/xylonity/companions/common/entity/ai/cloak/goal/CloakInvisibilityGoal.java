package dev.xylonity.companions.common.entity.ai.cloak.goal;

import dev.xylonity.companions.common.entity.ai.cloak.AbstractCloakAttackGoal;
import dev.xylonity.companions.common.entity.custom.CloakEntity;
import dev.xylonity.companions.common.entity.projectile.trigger.LaserTriggerProjectile;
import dev.xylonity.companions.registry.CompanionsEntities;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class CloakInvisibilityGoal extends AbstractCloakAttackGoal {

    public CloakInvisibilityGoal(CloakEntity cloak, int minCd, int maxCd) {
        super(cloak, 80, minCd, maxCd);
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
        Vec3 dir = target.getEyePosition().subtract(cloak.getEyePosition()).normalize();

        LaserTriggerProjectile laser = CompanionsEntities.LASER_PROJECTILE.get().create(cloak.level());
        if (laser != null) {
            Vec3 base = cloak.position().add(0, cloak.getBbHeight() * 0.5, 0);
            Vec3 spawn = base.add(dir.scale(0.05));

            laser.setPos(spawn.x, spawn.y, spawn.z);
            laser.setOwner(cloak);
            laser.setTarget(target);
            cloak.level().addFreshEntity(laser);
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