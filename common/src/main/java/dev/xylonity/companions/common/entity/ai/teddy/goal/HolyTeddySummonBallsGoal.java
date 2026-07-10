package dev.xylonity.companions.common.entity.ai.teddy.goal;

import dev.xylonity.companions.common.entity.ai.teddy.AbstractTeddyAttackGoal;
import dev.xylonity.companions.common.entity.companion.TeddyEntity;
import dev.xylonity.companions.common.entity.projectile.BlueOrbProjectile;
import dev.xylonity.companions.registry.CompanionsEntities;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;

public class HolyTeddySummonBallsGoal extends AbstractTeddyAttackGoal {

    private static final int ORB_AMOUNT = 5;

    public HolyTeddySummonBallsGoal(TeddyEntity teddy, int minCd, int maxCd) {
        super(teddy, 30, minCd, maxCd);
    }

    @Override
    protected boolean matchesPhase() {
        return teddy.getPhase() > 2;
    }

    @Override
    public boolean canUse() {
        return super.canUse() && teddy.getTarget() != null && teddy.distanceToSqr(teddy.getTarget()) < 20 * 20 && teddy.hasLineOfSight(teddy.getTarget());
    }

    @Override
    public void start() {
        super.start();
        teddy.playSound(SoundEvents.EVOKER_PREPARE_SUMMON, 0.8f, 1.4f);
    }

    @Override
    protected void performAttack(LivingEntity target) {
        for (int i = 0; i < ORB_AMOUNT; i++) {
            final BlueOrbProjectile orb = CompanionsEntities.BLUE_ORB_PROJECTILE.get().create(teddy.level());
            if (orb != null) {
                orb.setOwner(teddy);

                orb.setUp(target, 14 + teddy.getRandom().nextInt(26), (float) (Math.PI * (i + 0.5) / ORB_AMOUNT));
                orb.moveTo(teddy.position().add(0, teddy.getBbHeight() * 0.5, 0));

                teddy.level().addFreshEntity(orb);
            }

        }

        teddy.playSound(SoundEvents.AMETHYST_BLOCK_CHIME, 1f, 0.8f);
    }

    @Override
    protected int attackDelay() {
        return 10;
    }

    @Override
    protected int phase() {
        return 3;
    }

    @Override
    protected int getAttackType() {
        return 4;
    }

}
