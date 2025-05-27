package dev.xylonity.companions.common.entity.ai.pontiff.goal;

import dev.xylonity.companions.common.entity.ai.pontiff.AbstractSacredPontiffAttackGoal;
import dev.xylonity.companions.common.entity.hostile.SacredPontiffEntity;
import dev.xylonity.companions.common.entity.projectile.HolinessStartProjectile;
import dev.xylonity.companions.registry.CompanionsEntities;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class HolinessStarAttackGoal extends AbstractSacredPontiffAttackGoal {

    public HolinessStarAttackGoal(SacredPontiffEntity boss, int minCd, int maxCd) {
        super(boss, 40, minCd, maxCd);
    }

    @Override
    public void start() {
        super.start();
        pontiff.setNoMovement(true);
        pontiff.setShouldLookAtTarget(false);
    }

    @Override
    public void stop() {
        super.stop();
        pontiff.setNoMovement(false);
        pontiff.setShouldLookAtTarget(true);
    }

    @Override
    public boolean canUse() {
        return super.canUse() && pontiff.getTarget() != null && pontiff.distanceTo(pontiff.getTarget()) < 25 && isEntityInFront(pontiff, pontiff.getTarget(), 160);
    }

    @Override
    protected int getAttackType() {
        return 5;
    }

    @Override
    protected void performAttack(LivingEntity target) {
        Vec3 basePos = pontiff.position().add(0, pontiff.getEyeHeight(), 0);
        Vec3 targetPos = target.position().add(0, target.getEyeHeight(), 0);
        Vec3 dir = targetPos.subtract(basePos).normalize();
        Vec3 perpen = new Vec3(-dir.z, 0, dir.x).normalize();

        for (float side : new float[]{-1.5f, 1.5f}) {
            Vec3 spawnPos = basePos.add(perpen.scale(side));
            Vec3 firstVel = targetPos.subtract(spawnPos).normalize().scale(HolinessStartProjectile.SPEED);

            HolinessStartProjectile startProjectile = CompanionsEntities.HOLINESS_STAR.get().create(pontiff.level());
            if (startProjectile != null) {
                startProjectile.setOwner(pontiff);
                startProjectile.setTarget(target);
                startProjectile.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
                startProjectile.setDeltaMovement(firstVel);
                startProjectile.setNoGravity(true);
                pontiff.level().addFreshEntity(startProjectile);
            }
        }

    }

    @Override
    protected int attackDelay() {
        return 18;
    }

    @Override
    protected int phase() {
        return 2;
    }

}