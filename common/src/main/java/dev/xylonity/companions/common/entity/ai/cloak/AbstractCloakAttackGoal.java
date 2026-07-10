package dev.xylonity.companions.common.entity.ai.cloak;

import dev.xylonity.companions.common.entity.companion.CloakEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public abstract class AbstractCloakAttackGoal extends Goal {
    protected final CloakEntity cloak;
    protected final int attackDuration;
    protected final int minCooldown, maxCooldown;
    protected int attackTicks;
    protected int nextUseTick;
    protected boolean started;

    public AbstractCloakAttackGoal(CloakEntity cloak, int attackDuration, int minCd, int maxCd) {
        this.cloak = cloak;
        this.attackDuration = attackDuration;
        this.minCooldown = minCd;
        this.maxCooldown = maxCd;
        this.nextUseTick = -1;
        this.setFlags(EnumSet.of(Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (cloak.getAttackType() != 0) return false;
        if (cloak.getTarget() == null) return false;
        if (cloak.getMainAction() != 1) return false;

        if (nextUseTick < 0) {
            nextUseTick = cloak.tickCount + minCooldown + cloak.getRandom().nextInt(maxCooldown - minCooldown + 1);
            return false;
        }

        return cloak.tickCount >= nextUseTick;
    }

    @Override
    public boolean canContinueToUse() {
        return started && attackTicks < attackDuration;
    }

    @Override
    public void start() {
        attackTicks = 0;
        started = true;
        cloak.setAttackType(attackType());
    }

    @Override
    public void stop() {
        started = false;
        cloak.setAttackType(0);
        int cd = minCooldown + cloak.getRandom().nextInt(maxCooldown - minCooldown + 1);
        nextUseTick = cloak.tickCount + cd;
    }

    @Override
    public void tick() {
        LivingEntity target = cloak.getTarget();
        if (target != null) {
            cloak.getLookControl().setLookAt(target, 30F, 30F);
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
