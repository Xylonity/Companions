package dev.xylonity.companions.common.entity.ai.teddy;

import dev.xylonity.companions.common.entity.companion.TeddyEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public abstract class AbstractTeddyAttackGoal extends Goal {
    protected final TeddyEntity teddy;
    protected final int attackDuration;
    protected final int minCooldown, maxCooldown;
    protected int attackTicks;
    protected int nextUseTick;
    protected boolean started;

    public AbstractTeddyAttackGoal(TeddyEntity teddy, int attackDuration, int minCd, int maxCd) {
        this.teddy = teddy;
        this.attackDuration = attackDuration;
        this.minCooldown = minCd;
        this.maxCooldown = maxCd;
        this.nextUseTick = -1;
        this.setFlags(EnumSet.of(Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.phase() != teddy.getPhase()) return false;
        if (teddy.getAttackType() != 0) return false;
        if (teddy.getTarget() == null) return false;
        if (teddy.getMainAction() != 1) return false;

        if (nextUseTick < 0) {
            nextUseTick = teddy.tickCount + minCooldown + teddy.getRandom().nextInt(maxCooldown - minCooldown + 1);
            return false;
        }

        return teddy.tickCount >= nextUseTick;
    }

    @Override
    public boolean canContinueToUse() {
        return started && attackTicks < attackDuration;
    }

    @Override
    public void start() {
        attackTicks = 0;
        started = true;
        teddy.setAttackType(getAttackType());
    }

    @Override
    public void stop() {
        started = false;
        teddy.setAttackType(0);
        int cd = minCooldown + teddy.getRandom().nextInt(maxCooldown - minCooldown + 1);
        nextUseTick = teddy.tickCount + cd;
    }

    @Override
    public void tick() {
        LivingEntity target = teddy.getTarget();
        if (target != null) {
            teddy.getLookControl().setLookAt(target, 30F, 30F);
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
    protected abstract int phase();
    protected abstract int getAttackType();

}
