package dev.xylonity.companions.common.entity.ai.puppet.glove;

import dev.xylonity.companions.common.entity.custom.PuppetGloveEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public abstract class AbstractPuppetGloveAttackGoal extends Goal {
    protected final PuppetGloveEntity glove;
    private final int attackDuration;
    private final int minCooldown, maxCooldown;
    protected int attackTicks;
    protected int nextUseTick;
    protected boolean started;

    public AbstractPuppetGloveAttackGoal(PuppetGloveEntity glove, int attackDuration, int minCd, int maxCd) {
        this.glove = glove;
        this.attackDuration = attackDuration;
        this.minCooldown = minCd;
        this.maxCooldown = maxCd;
        this.nextUseTick = -1;
        this.setFlags(EnumSet.of(Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (glove.isAttacking()) return false;
        if (glove.getTarget() == null) return false;
        if (glove.getMainAction() != 1) return false;

        if (nextUseTick < 0) {
            nextUseTick = glove.tickCount + minCooldown + glove.getRandom().nextInt(maxCooldown - minCooldown + 1);
            return false;
        }

        return glove.tickCount >= nextUseTick;
    }

    @Override
    public boolean canContinueToUse() {
        return started && attackTicks < attackDuration;
    }

    @Override
    public void start() {
        attackTicks = 0;
        started = true;
        glove.setAttacking(true);
    }

    @Override
    public void stop() {
        started = false;
        glove.setAttacking(false);
        int cd = minCooldown + glove.getRandom().nextInt(maxCooldown - minCooldown + 1);
        nextUseTick = glove.tickCount + cd;
    }

    @Override
    public void tick() {
        LivingEntity target = glove.getTarget();
        if (target != null) {
            glove.getLookControl().setLookAt(target, 30F, 30F);
        }

        if (attackTicks == attackDelay() && target != null && target.isAlive()) {
            performAttack(target);
        }

        attackTicks++;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    protected abstract void performAttack(LivingEntity target);
    protected abstract int attackDelay();

}
