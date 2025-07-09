package dev.xylonity.companions.common.entity.ai.cornelius.summon.goal;

import dev.xylonity.companions.common.entity.CompanionSummonEntity;
import dev.xylonity.companions.common.entity.ai.cornelius.summon.AbstractCorneliusSummonAttackGoal;
import dev.xylonity.companions.common.entity.projectile.FrogEggProjectile;
import dev.xylonity.companions.common.entity.summon.BubbleFrogEntity;
import dev.xylonity.companions.registry.CompanionsEntities;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class BubbleFrogAttackGoal extends AbstractCorneliusSummonAttackGoal {

    public BubbleFrogAttackGoal(CompanionSummonEntity summon, int minCd, int maxCd) {
        super(summon, 48, minCd, maxCd);
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
        LivingEntity target = summon.getTarget();
        if (target != null) {
            summon.getLookControl().setLookAt(target, 30F, 30F);
        }

        if (attackTicks % 2 == 0 && attackTicks > 7 && attackTicks < 40 && target != null && target.isAlive()) {
            performAttack(target);
        }

        attackTicks++;
    }

    @Override
    protected int getAttackType() {
        return 1;
    }

    @Override
    protected void performAttack(LivingEntity target) {
        FrogEggProjectile projectile = CompanionsEntities.FROG_EGG_PROJECTILE.get().create(summon.level());
        if (projectile != null) {
            Vec3 basePos = summon.position().add(0, 0.5 + Math.random() * 0.4, 0);
            Vec3 targetPos = target.position().add(0, target.getEyeHeight(), 0);
            Vec3 dir = targetPos.subtract(basePos).normalize();

            Vec3 spawnPos = basePos.add(new Vec3(-dir.z, 0, dir.x).normalize().scale(-0.5f + summon.level().random.nextFloat()));

            projectile.setOwner(summon);
            projectile.setTarget(target);
            projectile.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
            projectile.setDeltaMovement(targetPos.subtract(spawnPos).normalize().scale(projectile.getDefaultSpeed()));
            projectile.setNoGravity(true);

            summon.level().addFreshEntity(projectile);
        }
    }

    @Override
    protected int attackDelay() {
        return 0;
    }

    @Override
    protected Class<? extends CompanionSummonEntity> summonType() {
        return BubbleFrogEntity.class;
    }

}