package dev.xylonity.companions.common.entity.ai.teddy.goal;

import dev.xylonity.companions.common.entity.ai.teddy.AbstractTeddyAttackGoal;
import dev.xylonity.companions.common.entity.companion.TeddyEntity;
import dev.xylonity.companions.registry.CompanionsSounds;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;

public class HolyTeddyAttackGoal extends AbstractTeddyAttackGoal {

    public HolyTeddyAttackGoal(TeddyEntity teddy, int minCd, int maxCd) {
        super(teddy, 20, minCd, maxCd);
    }

    @Override
    protected boolean matchesPhase() {
        return teddy.getPhase() > 2;
    }

    @Override
    public boolean canUse() {
        return super.canUse() && teddy.getTarget() != null && teddy.distanceToSqr(teddy.getTarget()) < 10 * 10;
    }

    @Override
    public void start() {
        attackTicks = 0;
        started = true;
        teddy.setAttackType(teddy.getRandom().nextInt(2) + 1);
        teleportToTarget();
    }

    private void teleportToTarget() {
        final LivingEntity target = teddy.getTarget();
        if (target == null) {
            return;
        }

        spawnTpParticles();

        final double angle = teddy.getRandom().nextDouble() * Math.PI * 2;
        final double x = target.getX() + Math.cos(angle) * 1.8;
        final double z = target.getZ() + Math.sin(angle) * 1.8;
        final double y = target.getY() + target.getBbHeight() * 0.5 + 0.3;

        teddy.teleportTo(x, y, z);
        teddy.setTeleported(true);
        teddy.lookAt(EntityAnchorArgument.Anchor.EYES, target.getEyePosition());
        teddy.playSound(SoundEvents.CHORUS_FRUIT_TELEPORT, 0.8f, 1.4f);

        spawnTpParticles();
    }

    private void spawnTpParticles() {
        if (teddy.level() instanceof ServerLevel level) {
            for (int i = 0; i < 12; i++) {
                final double dx = (teddy.getRandom().nextDouble() - 0.5) * 1.2;
                final double dy = (teddy.getRandom().nextDouble() - 0.5) * 1.2;
                final double dz = (teddy.getRandom().nextDouble() - 0.5) * 1.2;
                level.sendParticles(ParticleTypes.END_ROD, teddy.getX(), teddy.getY() + 0.7, teddy.getZ(), 1, dx, dy, dz, 0.08);
            }

        }

    }

    @Override
    protected void performAttack(LivingEntity target) {
        if (teddy.hasLineOfSight(target)) {
            teddy.playSound(CompanionsSounds.TEDDY_ATTACK.get());
            teddy.doHurtTarget(target);
        }

    }

    @Override
    protected int attackDelay() {
        return 7;
    }

    @Override
    protected int phase() {
        return 3;
    }

    @Override
    protected int getAttackType() {
        return 1;
    }

}