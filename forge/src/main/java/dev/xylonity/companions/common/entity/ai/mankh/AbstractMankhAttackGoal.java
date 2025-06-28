package dev.xylonity.companions.common.entity.ai.mankh;

import dev.xylonity.companions.common.entity.custom.MankhEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public abstract class AbstractMankhAttackGoal extends Goal {
    protected final MankhEntity mankh;
    protected final int attackDuration;
    protected final int minCooldown, maxCooldown;
    protected int attackTicks;
    protected int nextUseTick;
    protected boolean started;

    public AbstractMankhAttackGoal(MankhEntity mankh, int attackDuration, int minCd, int maxCd) {
        this.mankh = mankh;
        this.attackDuration = attackDuration;
        this.minCooldown = minCd;
        this.maxCooldown = maxCd;
        this.nextUseTick = -1;
        this.setFlags(EnumSet.of(Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (mankh.getAttackType() != 0) return false;
        if (mankh.getTarget() == null) return false;
        if (mankh.getMainAction() != 1) return false;

        if (nextUseTick < 0) {
            nextUseTick = mankh.tickCount + minCooldown + mankh.getRandom().nextInt(maxCooldown - minCooldown + 1);
            return false;
        }

        return mankh.tickCount >= nextUseTick;
    }

    @Override
    public boolean canContinueToUse() {
        return started && attackTicks < attackDuration;
    }

    @Override
    public void start() {
        attackTicks = 0;
        started = true;
        mankh.setAttackType(attackType());
    }

    @Override
    public void stop() {
        started = false;
        mankh.setAttackType(0);
        int cd = minCooldown + mankh.getRandom().nextInt(maxCooldown - minCooldown + 1);
        nextUseTick = mankh.tickCount + cd;
    }

    @Override
    public void tick() {
        LivingEntity target = mankh.getTarget();
        if (target != null) {
            mankh.getLookControl().setLookAt(target, 30F, 30F);
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
    protected abstract int attackType();

}
