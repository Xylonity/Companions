package dev.xylonity.companions.common.entity.ai.cloak.goal;

import dev.xylonity.companions.common.entity.ai.cloak.AbstractCloakAttackGoal;
import dev.xylonity.companions.common.entity.companion.CloakEntity;
import dev.xylonity.companions.common.entity.projectile.HolinessStartProjectile;
import dev.xylonity.companions.registry.CompanionsEntities;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class CloakRedStarAttackGoal extends AbstractCloakAttackGoal {

    public CloakRedStarAttackGoal(CloakEntity cloak, int minCd, int maxCd) {
        super(cloak, 20, minCd, maxCd);
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
        Vec3 targetPos = target.position().add(0, target.getEyeHeight(), 0);
        Vec3 spawnPos = cloak.position().add(0, cloak.getBbHeight() * 0.5, 0);
        Vec3 vel = targetPos.subtract(spawnPos).normalize().normalize().scale(HolinessStartProjectile.SPEED);

        HolinessStartProjectile star = CompanionsEntities.HOLINESS_STAR.create(cloak.level());
        if (star != null) {
            star.setOwner(cloak);
            star.setTarget(target);
            star.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
            star.setDeltaMovement(vel);
            star.setNoGravity(true);
            star.setRed(true);
            cloak.level().addFreshEntity(star);
        }
    }

    @Override
    protected int attackDelay() {
        return 12;
    }

    @Override
    protected int attackType() {
        return 2;
    }
}