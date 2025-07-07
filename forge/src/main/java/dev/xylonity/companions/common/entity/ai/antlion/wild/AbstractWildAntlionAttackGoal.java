package dev.xylonity.companions.common.entity.ai.antlion.wild;

import dev.xylonity.companions.common.entity.companion.AntlionEntity;
import dev.xylonity.companions.common.entity.hostile.WildAntlionEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public abstract class AbstractWildAntlionAttackGoal extends Goal {
    protected final WildAntlionEntity antlion;
    protected final int attackDuration;
    protected final int minCooldown, maxCooldown;
    protected int attackTicks;
    protected int nextUseTick;
    protected boolean started;

    public AbstractWildAntlionAttackGoal(WildAntlionEntity antlion, int attackDuration, int minCd, int maxCd) {
        this.antlion = antlion;
        this.attackDuration = attackDuration;
        this.minCooldown = minCd;
        this.maxCooldown = maxCd;
        this.nextUseTick = -1;
        this.setFlags(EnumSet.of(Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (antlion.getAttackType() != 0) return false;
        if (antlion.getTarget() == null) return false;

        if (nextUseTick < 0) {
            nextUseTick = antlion.tickCount + minCooldown + antlion.getRandom().nextInt(maxCooldown - minCooldown + 1);
            return false;
        }

        return antlion.tickCount >= nextUseTick;
    }

    @Override
    public boolean canContinueToUse() {
        return started && attackTicks < attackDuration;
    }

    @Override
    public void start() {
        attackTicks = 0;
        started = true;
        antlion.setAttackType(attackType());
    }

    @Override
    public void stop() {
        started = false;
        antlion.setAttackType(0);
        int cd = minCooldown + antlion.getRandom().nextInt(maxCooldown - minCooldown + 1);
        nextUseTick = antlion.tickCount + cd;
    }

    @Override
    public void tick() {
        LivingEntity target = antlion.getTarget();
        if (target != null) {
            antlion.getLookControl().setLookAt(target, 30F, 30F);
            antlion.lookAt(target, 30F, 30F);
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
    protected abstract int variant();

}
