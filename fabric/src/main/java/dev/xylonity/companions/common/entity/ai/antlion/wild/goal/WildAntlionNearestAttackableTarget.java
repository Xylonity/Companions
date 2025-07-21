package dev.xylonity.companions.common.entity.ai.antlion.wild.goal;

import dev.xylonity.companions.common.entity.hostile.WildAntlionEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.function.Predicate;

public class WildAntlionNearestAttackableTarget<T extends LivingEntity> extends TargetGoal {
    private static final int DEFAULT_RANDOM_INTERVAL = 10;
    protected final Class<T> targetType;
    protected final int randomInterval;
    protected LivingEntity target;
    protected TargetingConditions targetConditions;
    private final WildAntlionEntity antlion;

    public WildAntlionNearestAttackableTarget(WildAntlionEntity pMob, Class<T> pTargetType, boolean pMustSee) {
        this(pMob, pTargetType, DEFAULT_RANDOM_INTERVAL, pMustSee, false, null);
    }

    public WildAntlionNearestAttackableTarget(WildAntlionEntity pMob, Class<T> pTargetType, int pRandomInterval, boolean pMustSee, boolean pMustReach, Predicate<LivingEntity> pTargetPredicate) {
        super(pMob, pMustSee, pMustReach);
        this.targetType = pTargetType;
        this.randomInterval = reducedTickDelay(pRandomInterval);
        this.setFlags(EnumSet.of(Flag.TARGET));
        this.antlion = pMob;
        this.targetConditions = TargetingConditions.forCombat().range(this.getFollowDistance()).selector(pTargetPredicate);
    }

    @Override
    public boolean canUse() {
        if (this.randomInterval > 0 && this.mob.getRandom().nextInt(this.randomInterval) != 0) {
            return false;
        }

        findTarget();

        return this.target != null;
    }

    @Override
    public boolean canContinueToUse() {
        return this.target != null && this.target.isAlive() && isValidTarget(this.target);
    }

    @Override
    public void start() {
        this.mob.setTarget(this.target);
        super.start();
    }

    public void setTarget(LivingEntity pTarget) {
        this.target = pTarget;
    }

    protected AABB getTargetSearchArea(double pTargetDistance) {
        return this.mob.getBoundingBox().inflate(pTargetDistance, 4.0, pTargetDistance);
    }

    protected void findTarget() {
        this.target = this.mob.level().getEntitiesOfClass(this.targetType, getTargetSearchArea(getFollowDistance()), e -> true)
                .stream()
                .filter(this::isValidTarget)
                .min(Comparator.comparingDouble(e -> e.distanceToSqr(this.mob.getX(), this.mob.getY(), this.mob.getZ())))
                .orElse(null);
    }

    @Override
    public void tick() {
        if (target != null) {
            antlion.lookAt(target, 30, 30);
        }
    }

    private boolean isValidTarget(LivingEntity e) {

        if (!(e instanceof Player p)) return false;
        if (p.isCreative() || p.isSpectator()) return false;
        if (!this.antlion.hasLineOfSight(p)) return false;
        if (this.antlion.getLookAngle().normalize().dot(p.position().subtract(this.antlion.position()).normalize()) < 0.0D && p.isShiftKeyDown()) return false;

        return true;
    }
}
