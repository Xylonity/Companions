package dev.xylonity.companions.common.entity.ai.cornelius.goal;

import dev.xylonity.companions.common.entity.ai.cornelius.AbstractCorneliusAttackGoal;
import dev.xylonity.companions.common.entity.companion.CorneliusEntity;
import dev.xylonity.companions.registry.CompanionsSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

public class CorneliusAttackBeeGoal extends AbstractCorneliusAttackGoal {

    private boolean shouldTame;

    public CorneliusAttackBeeGoal(CorneliusEntity cornelius, int minCd, int maxCd) {
        super(cornelius, minCd, maxCd);
        this.shouldTame = false;
    }

    @Override
    public boolean canUse() {
        if (cornelius.isTame()) return false;
        if (cornelius.level().getEntitiesOfClass(Player.class, cornelius.getBoundingBox().inflate(10)).isEmpty()) return false;
        if (cornelius.getAttackType() != 0) return false;
        if (cornelius.getTarget() == null) return false;
        if (cornelius.distanceToSqr(cornelius.getTarget()) >= 9) return false;

        if (nextUseTick < 0) {
            int cd = minCooldown + cornelius.getRandom().nextInt(maxCooldown - minCooldown + 1);
            nextUseTick = cornelius.tickCount + cd;
            return false;
        }

        return cornelius.tickCount >= nextUseTick;
    }

    @Override
    public void start() {
        int currentAttackType = cornelius.getRandom().nextInt(2) + 2;
        if (currentAttackType == 2) {
            currentAttackDelay = 17;
            currentAttackDuration = 25;
        } else {
            currentAttackDelay = 18;
            currentAttackDuration = 26;
        }

        attackTicks = 0;
        started = true;
        cornelius.setAttackType(currentAttackType);
        cornelius.playSound(CompanionsSounds.FROGGY_ATTACK.get());
    }

    @Override
    protected void performAttack(LivingEntity target) {
        if (target != null) {
            if (this.cornelius.distanceToSqr(target) <= 9) {
                target.kill();
                this.shouldTame = true;
            }
        }

    }

    @Override
    public void stop() {
        super.stop();
        if (shouldTame) {
            Player player = this.cornelius.level().getNearestPlayer(this.cornelius, 16);
            if (player != null) {
                this.cornelius.tameInteraction(player);
                generatePoofParticles();
            }
        }
    }

    @Override
    protected Item coin() {
        return null;
    }

    @Override
    protected int coinsToConsume() {
        return 0;
    }

    private void generatePoofParticles() {
        for (int i = 0; i < 30; i++) {
            double dx = (cornelius.level().random.nextDouble() - 0.5) * 1.25;
            double dy = (cornelius.level().random.nextDouble() - 0.5) * 1.25;
            double dz = (cornelius.level().random.nextDouble() - 0.5) * 1.25;
            if (cornelius.level() instanceof ServerLevel level) {
                level.sendParticles(ParticleTypes.POOF, cornelius.position().x, cornelius.getY() + cornelius.getBbHeight() * Math.random(), cornelius.position().z, 1, dx, dy, dz, 0.1);
            }
        }

    }

}
