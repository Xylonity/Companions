package dev.xylonity.companions.common.entity.ai.antlion.tamable.goal;

import dev.xylonity.companions.common.entity.ai.antlion.tamable.AbstractAntlionAttackGoal;
import dev.xylonity.companions.common.entity.companion.AntlionEntity;
import dev.xylonity.companions.common.entity.projectile.AntlionSandProjectile;
import dev.xylonity.companions.registry.CompanionsEntities;
import dev.xylonity.companions.registry.CompanionsSounds;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class AntlionSoldierLongAttackGoal extends AbstractAntlionAttackGoal {

    public AntlionSoldierLongAttackGoal(AntlionEntity antlion, int minCd, int maxCd) {
        super(antlion, 35, minCd, maxCd);
    }

    @Override
    public void start() {
        super.start();
        antlion.setNoMovement(true);
    }

    @Override
    public void stop() {
        super.stop();
        antlion.setNoMovement(false);
    }

    @Override
    public boolean canUse() {
        return super.canUse() && antlion.getTarget() != null && antlion.distanceToSqr(antlion.getTarget()) <= 15 * 15 && antlion.distanceToSqr(antlion.getTarget()) >= 9;
    }

    @Override
    protected void performAttack(LivingEntity target) {
        AntlionSandProjectile projectile = CompanionsEntities.ANTLION_SAND_PROJECTILE.get().create(antlion.level());
        if (projectile != null) {
            Vec3 basePos = antlion.position().add(0, antlion.getBbHeight() * 0.5f, 0);
            Vec3 targetPos = target.position().add(0, target.getEyeHeight(), 0);
            Vec3 dir = targetPos.subtract(basePos).normalize();

            Vec3 spawnPos = basePos.add(new Vec3(-dir.z, 0, dir.x).normalize().scale(-0.5f + antlion.level().random.nextFloat()));

            projectile.setOwner(antlion);
            projectile.setTarget(target);
            projectile.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
            projectile.setDeltaMovement(targetPos.subtract(spawnPos).normalize().scale(projectile.getDefaultSpeed()));
            projectile.setNoGravity(true);

            antlion.level().addFreshEntity(projectile);
        }

        antlion.playSound(CompanionsSounds.SOLDIER_ANTLION_SAND_CANNON.get());
    }

    @Override
    protected int attackDelay() {
        return 11;
    }

    @Override
    protected int attackType() {
        return 2;
    }

    @Override
    protected int variant() {
        return 3;
    }

}