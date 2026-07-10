package dev.xylonity.companions.common.entity.ai.cornelius.summon.goal;

import dev.xylonity.companions.common.entity.CompanionSummonEntity;
import dev.xylonity.companions.common.entity.ai.cornelius.summon.AbstractCorneliusSummonAttackGoal;
import dev.xylonity.companions.common.entity.projectile.FrogLevitateProjectile;
import dev.xylonity.companions.common.entity.summon.EnderFrogEntity;
import dev.xylonity.companions.registry.CompanionsEntities;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class EnderFrogLevitateGoal extends AbstractCorneliusSummonAttackGoal {

    public EnderFrogLevitateGoal(CompanionSummonEntity summon, int minCd, int maxCd) {
        super(summon, 35, minCd, maxCd);
    }

    @Override
    public boolean canUse() {
        return super.canUse() && summon.getTarget() != null && summon.distanceTo(summon.getTarget()) < 15;
    }

    @Override
    public void start() {
        super.start();
        summon.setNoMovement(true);
    }

    @Override
    public void stop() {
        super.stop();
        summon.setNoMovement(false);
    }

    @Override
    public void tick() {
        super.tick();
        if (attackTicks == 24 && summon.getTarget() != null && summon.getTarget().isAlive()) {
            performAttack2(summon.getTarget());
        }
    }

    @Override
    protected int getAttackType() {
        return 2;
    }

    @Override
    protected void performAttack(LivingEntity target) {
        FrogLevitateProjectile projectile = CompanionsEntities.FROG_LEVITATE_PROJECTILE.get().create(summon.level());
        if (projectile != null) {
            spawnProjectile(projectile, target, 1.2f);
        }
    }

    protected void performAttack2(LivingEntity target) {
        FrogLevitateProjectile projectile = CompanionsEntities.FROG_LEVITATE_PROJECTILE.get().create(summon.level());
        if (projectile != null) {
            spawnProjectile(projectile, target, -1.2f);
        }
    }

    private void spawnProjectile(FrogLevitateProjectile projectile, LivingEntity target, float side) {
        Vec3 basePos = summon.position().add(0, summon.getBbHeight() * 0.7, 0);
        Vec3 targetPos = target.position().add(0, target.getEyeHeight(), 0);
        Vec3 dir = targetPos.subtract(basePos).normalize();
        Vec3 perpen = new Vec3(-dir.z, 0, dir.x).normalize();

        Vec3 spawnPos = basePos.add(perpen.scale(side));
        Vec3 vel = targetPos.subtract(spawnPos).normalize().scale(projectile.getDefaultSpeed());

        projectile.setOwner(summon);
        projectile.setTarget(target);
        projectile.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
        projectile.setDeltaMovement(vel);
        projectile.setNoGravity(true);
        summon.level().addFreshEntity(projectile);
    }

    @Override
    protected int attackDelay() {
        return 13;
    }

    @Override
    protected Class<? extends CompanionSummonEntity> summonType() {
        return EnderFrogEntity.class;
    }

}