package dev.xylonity.companions.common.entity.ai.teddy.goal;

import dev.xylonity.companions.common.entity.ai.teddy.AbstractTeddyAttackGoal;
import dev.xylonity.companions.common.entity.companion.TeddyEntity;
import dev.xylonity.companions.registry.CompanionsSounds;
import net.minecraft.world.entity.LivingEntity;

public class MutatedTeddyAttackGoal extends AbstractTeddyAttackGoal {

    public MutatedTeddyAttackGoal(TeddyEntity teddy, int minCd, int maxCd) {
        super(teddy, 20, minCd, maxCd);
    }

    @Override
    public boolean canUse() {
        return super.canUse() && teddy.getTarget() != null && teddy.distanceToSqr(teddy.getTarget()) < 4 && teddy.getPhase() == 2;
    }

    @Override
    public void start() {
        int curr = teddy.getRandom().nextInt(2) + 1;
        if (curr == 1) {
            attackDuration = 20;
        } else {
            attackDuration = 15;
        }

        attackTicks = 0;
        started = true;
        teddy.setAttackType(curr);
    }

    @Override
    protected void performAttack(LivingEntity target) {
        if (teddy.hasLineOfSight(target)) {
            teddy.playSound(CompanionsSounds.MUTANT_TEDDY_ATTACK.get());
            teddy.doHurtTarget(target);
        }

    }

    @Override
    protected int attackDelay() {
        return 7;
    }

    @Override
    protected int phase() {
        return 2;
    }

    @Override
    protected int getAttackType() {
        return 1;
    }

}