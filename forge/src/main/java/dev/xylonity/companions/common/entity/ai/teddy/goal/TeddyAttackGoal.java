package dev.xylonity.companions.common.entity.ai.teddy.goal;

import dev.xylonity.companions.common.entity.ai.teddy.AbstractTeddyAttackGoal;
import dev.xylonity.companions.common.entity.companion.TeddyEntity;
import dev.xylonity.companions.registry.CompanionsEffects;
import dev.xylonity.companions.registry.CompanionsSounds;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

import java.util.Random;

public class TeddyAttackGoal extends AbstractTeddyAttackGoal {

    public TeddyAttackGoal(TeddyEntity teddy, int minCd, int maxCd) {
        super(teddy, 15, minCd, maxCd);
    }

    @Override
    public boolean canUse() {
        return super.canUse() && teddy.getTarget() != null && teddy.distanceToSqr(teddy.getTarget()) < 4 && teddy.getPhase() == 1;
    }

    @Override
    public void start() {
        super.start();
        teddy.setNoMovement(true);
    }

    @Override
    public void stop() {
        super.stop();
        teddy.setNoMovement(false);
    }

    @Override
    protected void performAttack(LivingEntity target) {
        if (teddy.hasLineOfSight(target)) {
            teddy.playSound(CompanionsSounds.TEDDY_ATTACK.get());
            teddy.doHurtTarget(target);
            if (new Random().nextFloat() < 0.35f) {
                target.addEffect(new MobEffectInstance(CompanionsEffects.VOODOO, new Random().nextInt(100, 300), 0, true, true, true));
            }
        }

    }

    @Override
    protected int attackDelay() {
        return 4;
    }

    @Override
    protected int phase() {
        return 1;
    }

    @Override
    protected int getAttackType() {
        return 1;
    }

}