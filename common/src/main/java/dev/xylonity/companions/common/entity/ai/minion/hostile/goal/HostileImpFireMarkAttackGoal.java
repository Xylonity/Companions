package dev.xylonity.companions.common.entity.ai.minion.hostile.goal;

import dev.xylonity.companions.common.entity.ai.minion.hostile.AbstractHostileImpAttackGoal;
import dev.xylonity.companions.common.entity.hostile.HostileImpEntity;
import dev.xylonity.companions.common.entity.projectile.FireMarkRingProjectile;
import dev.xylonity.companions.registry.CompanionsEntities;
import net.minecraft.world.entity.LivingEntity;

public class HostileImpFireMarkAttackGoal extends AbstractHostileImpAttackGoal {

    public HostileImpFireMarkAttackGoal(HostileImpEntity imp, int minCd, int maxCd) {
        super(imp, 15, minCd, maxCd);
    }

    @Override
    public void start() {
        super.start();
        imp.setNoMovement(true);
    }

    @Override
    public void stop() {
        super.stop();
        imp.setNoMovement(false);
    }

    @Override
    protected void performAttack(LivingEntity target) {
        FireMarkRingProjectile fireMarkRing = CompanionsEntities.FIRE_MARK_RING_PROJECTILE.create(imp.level());
        if (fireMarkRing != null) {
            fireMarkRing.moveTo(target.getX(), target.getY(), target.getZ());
            fireMarkRing.setOwner(imp);
            imp.level().addFreshEntity(fireMarkRing);
        }
    }

    @Override
    protected int attackDelay() {
        return 10;
    }

    @Override
    protected int attackType() {
        return 2;
    }

}