package dev.xylonity.companions.common.entity.ai.teddy.goal;

import dev.xylonity.companions.common.entity.ai.teddy.AbstractTeddyAttackGoal;
import dev.xylonity.companions.common.entity.companion.TeddyEntity;
import dev.xylonity.companions.common.entity.projectile.HolyRingProjectile;
import dev.xylonity.companions.common.util.Util;
import dev.xylonity.companions.registry.CompanionsEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;

public class HolyTeddyBlessingGoal extends AbstractTeddyAttackGoal {

    public HolyTeddyBlessingGoal(TeddyEntity teddy, int minCd, int maxCd) {
        super(teddy, 33, minCd, maxCd);
    }

    @Override
    protected boolean matchesPhase() {
        return teddy.getPhase() > 2;
    }

    @Override
    public boolean canUse() {
        if (!matchesPhase()) {
            return false;
        }
        if (teddy.getRitualTicks() > 0) {
            return false;
        }
        if (teddy.getAttackType() != 0) {
            return false;
        }
        if (teddy.getMainAction() != 1) {
            return false;
        }
        if (teddy.getTarget() == null && !shouldBlessOwner()) {
            return false;
        }

        if (nextUseTick < 0) {
            scheduleNextUse();
            return false;
        }

        return teddy.tickCount >= nextUseTick;
    }

    private boolean shouldBlessOwner() {
        final LivingEntity owner = teddy.getOwner();
        return owner != null && owner.isAlive() && teddy.distanceToSqr(owner) < 8 * 8 && owner.getHealth() < owner.getMaxHealth() * 0.5f;
    }

    @Override
    public void start() {
        super.start();
        teddy.setNoMovement(true);
        teddy.playSound(SoundEvents.AMETHYST_BLOCK_RESONATE, 1f, 0.8f);
        teddy.playSound(SoundEvents.EVOKER_PREPARE_SUMMON, 0.8f, 1.4f);
    }

    @Override
    public void stop() {
        super.stop();
        teddy.setNoMovement(false);
    }

    @Override
    public void tick() {
        final LivingEntity target = teddy.getTarget();
        if (target != null) {
            teddy.getLookControl().setLookAt(target, 30F, 30F);
        }

        if (attackTicks == attackDelay()) {
            performAttack(target);
        }

        attackTicks++;
    }

    @Override
    protected void performAttack(LivingEntity target) {
        final HolyRingProjectile ring = CompanionsEntities.HOLY_RING_PROJECTILE.get().create(teddy.level());
        if (ring != null) {
            final BlockPos groundPos = Util.findClosestGroundBelow(teddy, 4.0f);
            final double y = groundPos != null ? groundPos.getY() + 1.1 : teddy.getY();

            ring.setOwner(teddy);
            ring.moveTo(teddy.getX(), y, teddy.getZ());

            teddy.level().addFreshEntity(ring);
        }

        teddy.playSound(SoundEvents.BELL_RESONATE, 1.5f, 1.3f);
    }

    @Override
    protected int attackDelay() {
        return 19;
    }

    @Override
    protected int phase() {
        return 3;
    }

    @Override
    protected int getAttackType() {
        return 3;
    }

}
