package dev.xylonity.companions.common.entity.ai.froggy;

import dev.xylonity.companions.common.entity.custom.FroggyEntity;
import dev.xylonity.companions.common.entity.custom.SoulMageEntity;
import dev.xylonity.companions.common.tick.TickScheduler;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;
import java.util.Random;

public abstract class AbstractFroggyGoal extends Goal {
    protected final FroggyEntity froggy;
    protected int attackTicks;
    protected int nextUseTick;
    protected final int minCooldown;
    protected final int maxCooldown;
    protected boolean started;
    protected final String attackType;

    public AbstractFroggyGoal(FroggyEntity froggy, int minCd, int maxCd, String attackType) {
        this.froggy = froggy;
        this.minCooldown = minCd;
        this.maxCooldown = maxCd;
        this.attackType = attackType;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (froggy.isAttacking()) return false;
        if (froggy.getTarget() == null) return false;
        if (froggy.tickCount < nextUseTick) return false;
        if (!hasRequiredCoin()) return false;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return attackTicks < 25;
    }

    @Override
    public void start() {
        attackTicks = 0;
        froggy.setAttacking(true);
        TickScheduler.scheduleBoth(froggy.level(), () -> froggy.setAttacking(false), 25);
        int rn = new Random().nextInt(3) + 1;
        froggy.setAttackAnimationName(rn == 1 ? "spell" : rn == 2 ? "spell2" : "spell3");
        froggy.setCurrentAttackType(attackType);
        started = true;
    }

    @Override
    public void stop() {
        started = false;
        froggy.setCurrentAttackType("NONE");

        int randomCd = minCooldown + froggy.getRandom().nextInt(maxCooldown - minCooldown + 1);
        nextUseTick = froggy.tickCount + randomCd;
    }

    @Override
    public void tick() {
        if (!started) return;

        LivingEntity target = froggy.getTarget();
        if (target != null) {
            froggy.getLookControl().setLookAt(target, 30.0F, 30.0F);
        }

        attackTicks++;

        if (target != null) this.froggy.getLookControl().setLookAt(target, 30.0F, 30.0F);

        if (shouldPerformAttack(target)) {
            performAttack(target);
        }

        if (attackTicks >= 25) {
            stop();
        }

    }

    protected boolean shouldPerformAttack(LivingEntity target) {
        return attackTicks == 6 && target != null && target.isAlive();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    protected abstract void performAttack(LivingEntity target);
    protected abstract boolean hasRequiredCoin();

}