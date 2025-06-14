package dev.xylonity.companions.common.entity.ai.teddy.goal;

import dev.xylonity.companions.common.entity.custom.TeddyEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class MutatedTeddyChargeAttackGoal extends Goal {

    private enum AttackState { WIND_UP, CHARGING, RETREAT }

    private final TeddyEntity teddy;
    private LivingEntity target;

    private final double chargeSpeed;
    private final double retreatSpeed;
    private final int windUpDuration;
    private final int retreatDuration;
    private final double hitRange;
    private final double retreatDistance;

    private AttackState state = AttackState.WIND_UP;
    private int stateTicks = 0;

    private static final float LERP_FACTOR = 0.05f;

    public MutatedTeddyChargeAttackGoal(TeddyEntity teddy,
                                        double chargeSpeed,
                                        double retreatSpeed,
                                        int windUpDuration,
                                        int retreatDuration,
                                        double hitRange,
                                        double retreatDistance) {
        this.teddy = teddy;
        this.chargeSpeed = chargeSpeed;
        this.retreatSpeed = retreatSpeed;
        this.windUpDuration = windUpDuration;
        this.retreatDuration = retreatDuration;
        this.hitRange = hitRange;
        this.retreatDistance = retreatDistance;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (teddy.getPhase() != 2 || teddy.getMainAction() == 0) return false;
        LivingEntity poss = teddy.getTarget();
        if (poss == null || !poss.isAlive()) return false;
        target = poss;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return target != null
                && target.isAlive()
                && teddy.getPhase() == 2
                && teddy.getMainAction() != 0;
    }

    @Override
    public void start() {
        state = AttackState.WIND_UP;
        stateTicks = 0;
        teddy.setNoGravity(true);
    }

    @Override
    public void stop() {
        state = AttackState.WIND_UP;
        stateTicks = 0;
        target = null;
        teddy.setNoGravity(false);
    }

    @Override
    public void tick() {
        if (target == null) return;

        // siempre mira al objetivo
        teddy.getLookControl().setLookAt(target, 30f, 30f);

        switch (state) {
            case WIND_UP    -> doWindUp();
            case CHARGING  -> doCharging();
            case RETREAT   -> doRetreat();
        }
    }

    private void doWindUp() {
        if (++stateTicks >= windUpDuration) {
            state = AttackState.CHARGING;
            stateTicks = 0;
        }
    }

    private void doCharging() {
        double dx = target.getX() - teddy.getX();
        double dy = target.getY() - teddy.getY();
        double dz = target.getZ() - teddy.getZ();
        double distSqr = dx*dx + dy*dy + dz*dz;

        if (distSqr > hitRange * hitRange) {
            Vec3 dir = new Vec3(dx, dy, dz).normalize();
            Vec3 desiredVel = dir.scale(chargeSpeed);

            Vec3 currentVel = teddy.getDeltaMovement();
            Vec3 lerped = new Vec3(
                    Mth.lerp(LERP_FACTOR, currentVel.x, desiredVel.x),
                    Mth.lerp(LERP_FACTOR, currentVel.y, desiredVel.y),
                    Mth.lerp(LERP_FACTOR, currentVel.z, desiredVel.z)
            );

            teddy.setDeltaMovement(clamp(lerped, chargeSpeed));
            teddy.noPhysics = true;
        } else {
            // golpea y pasa a retirada
            teddy.swing(InteractionHand.MAIN_HAND);
            teddy.doHurtTarget(target);
            state = AttackState.RETREAT;
            stateTicks = 0;
        }
    }

    private void doRetreat() {
        double dx = teddy.getX() - target.getX();
        double dy = teddy.getY() - target.getY();
        double dz = teddy.getZ() - target.getZ();
        Vec3 dirAway = new Vec3(dx, dy, dz).normalize();
        Vec3 desiredVel = dirAway.scale(retreatSpeed);

        Vec3 currentVel = teddy.getDeltaMovement();
        Vec3 lerped = new Vec3(
                Mth.lerp(LERP_FACTOR, currentVel.x, desiredVel.x),
                Mth.lerp(LERP_FACTOR, currentVel.y, desiredVel.y),
                Mth.lerp(LERP_FACTOR, currentVel.z, desiredVel.z)
        );

        teddy.setDeltaMovement(clamp(lerped, retreatSpeed));
        teddy.noPhysics = true;

        if (++stateTicks >= retreatDuration) {
            state = AttackState.WIND_UP;
            stateTicks = 0;
            teddy.noPhysics = false;
        }
    }

    /** Limita la longitud del vector a `max` */
    private Vec3 clamp(Vec3 v, double max) {
        double len = v.length();
        if (len > max) {
            return v.normalize().scale(max);
        }
        return v;
    }
}
