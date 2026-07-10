package dev.xylonity.companions.common.entity.ai.minion.hostile;

import dev.xylonity.companions.common.entity.hostile.HostileImpEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public abstract class AbstractHostileImpAttackGoal extends Goal {
    protected final HostileImpEntity imp;
    protected final int attackDuration;
    protected final int minCooldown, maxCooldown;
    protected int attackTicks;
    protected int nextUseTick;
    protected boolean started;

    public AbstractHostileImpAttackGoal(HostileImpEntity imp, int attackDuration, int minCd, int maxCd) {
        this.imp = imp;
        this.attackDuration = attackDuration;
        this.minCooldown = minCd;
        this.maxCooldown = maxCd;
        this.nextUseTick = -1;
        this.setFlags(EnumSet.of(Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (imp.getAttackType() != 0) return false;
        if (imp.getTarget() == null) return false;

        if (nextUseTick < 0) {
            nextUseTick = imp.tickCount + minCooldown + imp.getRandom().nextInt(maxCooldown - minCooldown + 1);
            return false;
        }

        return imp.tickCount >= nextUseTick;
    }

    @Override
    public boolean canContinueToUse() {
        return started && attackTicks < attackDuration;
    }

    @Override
    public void start() {
        attackTicks = 0;
        started = true;
        imp.setAttackType(attackType());
    }

    @Override
    public void stop() {
        started = false;
        imp.setAttackType(0);
        int cd = minCooldown + imp.getRandom().nextInt(maxCooldown - minCooldown + 1);
        nextUseTick = imp.tickCount + cd;
    }

    @Override
    public void tick() {
        LivingEntity target = imp.getTarget();
        if (target != null) {
            imp.getLookControl().setLookAt(target, 30F, 30F);
            imp.lookAt(target, 30F, 30F);
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
