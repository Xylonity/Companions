package dev.xylonity.companions.common.entity.ai.minion;

import dev.xylonity.companions.common.entity.custom.MinionEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public abstract class AbstractMinionAttackGoal extends Goal {
    protected final MinionEntity minion;
    protected final int attackDuration;
    protected final int minCooldown, maxCooldown;
    protected int attackTicks;
    protected int nextUseTick;
    protected boolean started;

    public AbstractMinionAttackGoal(MinionEntity pontiff, int attackDuration, int minCd, int maxCd) {
        this.minion = pontiff;
        this.attackDuration = attackDuration;
        this.minCooldown = minCd;
        this.maxCooldown = maxCd;
        this.nextUseTick = -1;
        this.setFlags(EnumSet.of(Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!minion.getVariant().equals(variant())) return false;
        if (minion.getAttackType() != 0) return false;
        if (minion.getTarget() == null) return false;
        if (minion.getMainAction() != 1) return false;

        if (nextUseTick < 0) {
            nextUseTick = minion.tickCount + minCooldown + minion.getRandom().nextInt(maxCooldown - minCooldown + 1);
            return false;
        }

        return minion.tickCount >= nextUseTick;
    }

    @Override
    public boolean canContinueToUse() {
        return started && attackTicks < attackDuration;
    }

    @Override
    public void start() {
        attackTicks = 0;
        started = true;
        minion.setAttackType(attackType());
    }

    @Override
    public void stop() {
        started = false;
        minion.setAttackType(0);
        int cd = minCooldown + minion.getRandom().nextInt(maxCooldown - minCooldown + 1);
        nextUseTick = minion.tickCount + cd;
    }

    @Override
    public void tick() {
        LivingEntity target = minion.getTarget();
        if (target != null) {
            minion.getLookControl().setLookAt(target, 30F, 30F);
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
    protected abstract String variant();
    protected abstract int attackType();

}
