package dev.xylonity.companions.common.entity.ai.minion.tamable.imp;

import dev.xylonity.companions.common.entity.ai.minion.tamable.AbstractMinionAttackGoal;
import dev.xylonity.companions.common.entity.companion.MinionEntity;
import dev.xylonity.companions.common.entity.projectile.FireMarkRingProjectile;
import dev.xylonity.companions.registry.CompanionsEntities;
import net.minecraft.world.entity.LivingEntity;

public class ImpFireMarkAttackGoal extends AbstractMinionAttackGoal {

    public ImpFireMarkAttackGoal(MinionEntity minion, int minCd, int maxCd) {
        super(minion, 15, minCd, maxCd);
    }

    @Override
    public void start() {
        super.start();
        minion.setNoMovement(true);
    }

    @Override
    public void stop() {
        super.stop();
        minion.setNoMovement(false);
    }

    @Override
    protected void performAttack(LivingEntity target) {
        FireMarkRingProjectile ring = CompanionsEntities.FIRE_MARK_RING_PROJECTILE.get().create(minion.level());
        if (ring != null) {
            ring.moveTo(target.getX(), target.getY(), target.getZ());
            ring.setOwner(minion);
            minion.level().addFreshEntity(ring);
        }

    }

    @Override
    protected int attackDelay() {
        return 10;
    }

    @Override
    protected String variant() {
        return MinionEntity.Variant.NETHER.getName();
    }

    @Override
    protected int attackType() {
        return 2;
    }

}