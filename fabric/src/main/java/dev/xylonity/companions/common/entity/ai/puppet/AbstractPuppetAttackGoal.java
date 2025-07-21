package dev.xylonity.companions.common.entity.ai.puppet;

import dev.xylonity.companions.common.entity.companion.PuppetEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public abstract class AbstractPuppetAttackGoal extends Goal {
    protected final PuppetEntity puppet;
    protected int attackTicks;
    protected int nextUseTick;
    protected final int minCooldown;
    protected final int maxCooldown;
    protected boolean started;
    protected final String attackType;

    public AbstractPuppetAttackGoal(PuppetEntity puppet, int minCd, int maxCd, String attackType) {
        this.puppet = puppet;
        this.minCooldown = minCd;
        this.maxCooldown = maxCd;
        this.attackType = attackType;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (puppet.isAttacking() != 0) return false;
        if (puppet.getTarget() == null) return false;
        if (puppet.tickCount < nextUseTick) return false;
        if (hasRequiredArm() == 0) return false;
        return true;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return attackTicks < 13;
    }

    @Override
    public void start() {
        attackTicks = 0;
        puppet.setAttacking(hasRequiredArm());
        started = true;
    }

    @Override
    public void stop() {
        started = false;
        puppet.setAttacking(0);

        int randomCd = minCooldown + puppet.getRandom().nextInt(maxCooldown - minCooldown + 1);
        nextUseTick = puppet.tickCount + randomCd;
    }

    @Override
    public void tick() {
        if (!started) return;

        LivingEntity target = puppet.getTarget();
        if (target != null) {
            puppet.getLookControl().setLookAt(target, 90.0F, 90.0F);
        }

        attackTicks++;

        if (target != null) this.puppet.getLookControl().setLookAt(target, 30.0F, 30.0F);

        if (shouldPerformAttack(target)) {
            performAttack(target);
        }

        if (attackTicks >= 13) {
            stop();
        }

    }

    protected boolean shouldPerformAttack(LivingEntity target) {
        return attackTicks == 3 && target != null && target.isAlive();
    }

    protected abstract void performAttack(LivingEntity target);
    protected abstract int hasRequiredArm();

}