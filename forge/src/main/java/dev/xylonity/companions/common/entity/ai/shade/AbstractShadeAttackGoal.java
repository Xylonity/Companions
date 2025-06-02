package dev.xylonity.companions.common.entity.ai.shade;

import dev.xylonity.companions.common.entity.ShadeEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public abstract class AbstractShadeAttackGoal extends Goal {
    protected final ShadeEntity shade;
    protected final int attackDuration;
    private final int minCooldown, maxCooldown;
    protected int attackTicks;
    protected int nextUseTick;
    protected boolean started;

    public AbstractShadeAttackGoal(ShadeEntity shade, int attackDuration, int minCd, int maxCd) {
        this.shade = shade;
        this.attackDuration = attackDuration;
        this.minCooldown = minCd;
        this.maxCooldown = maxCd;
        this.nextUseTick = -1;
        this.setFlags(EnumSet.of(Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (shade.getClass() != shadeType()) return false;
        if (shade.getAttackType() != 0) return false;
        if (shade.getTarget() == null) return false;

        if (nextUseTick < 0) {
            nextUseTick = shade.tickCount + minCooldown + shade.getRandom().nextInt(maxCooldown - minCooldown + 1);
            return false;
        }

        return shade.tickCount >= nextUseTick;
    }

    @Override
    public boolean canContinueToUse() {
        return started && attackTicks < attackDuration;
    }

    @Override
    public void start() {
        attackTicks = 0;
        started = true;
        shade.setAttackType(getAttackType());
    }

    @Override
    public void stop() {
        started = false;
        shade.setAttackType(0);
        int cd = minCooldown + shade.getRandom().nextInt(maxCooldown - minCooldown + 1);
        nextUseTick = shade.tickCount + cd;
    }

    @Override
    public void tick() {
        LivingEntity target = shade.getTarget();
        if (target != null) {
            shade.getLookControl().setLookAt(target, 30F, 30F);
        }

        if (attackTicks == attackDelay() && target != null && target.isAlive()) {
            performAttack(target);
        }

        attackTicks++;
    }

    public static boolean isEntityInFront(LivingEntity viewer, Entity target, double fov) {
        Vec3 view = viewer.getLookAngle().normalize();
        Vec3 toTarget = target.position().add(0, target.getEyeHeight() * 0.5, 0).subtract(viewer.getEyePosition(1)).normalize();
        double angle = Math.acos(view.dot(toTarget)) * (180.0 / Math.PI);
        return angle < (fov / 2);
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    protected abstract int getAttackType();
    protected abstract void performAttack(LivingEntity target);
    protected abstract int attackDelay();
    protected abstract Class<? extends ShadeEntity> shadeType();

}
