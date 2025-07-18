package dev.xylonity.companions.common.entity.ai.teddy.goal;

import dev.xylonity.companions.common.entity.companion.TeddyEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class MutatedTeddyFollowTargetGoal extends Goal {

    private static final double RADIUS_VARIATION = 2.5;
    private static final double Y_AMPL = 0.8;
    private static final double Y_OSCILATION = 0.25;

    private static final int MIN_DIR_TIME = 40;
    private static final int MAX_DIR_TIME = 160;
    private static final int MIN_IDLE_TIME = 5;
    private static final int MAX_IDLE_TIME = 10;
    private static final int CHARGE_COOLDOWN = 80;

    private final TeddyEntity teddy;
    private LivingEntity target;

    private double angle;
    private double time;
    private int dir;

    private State state;

    private int ticksInState;
    private int untilNextDirChange;
    private int untilNextCharge;
    private Vec3 chargeDir = Vec3.ZERO;

    public MutatedTeddyFollowTargetGoal(TeddyEntity teddy) {
        this.teddy = teddy;
        this.dir = teddy.getRandom().nextBoolean() ? 1 : -1;
        this.untilNextDirChange = teddy.getRandom().nextInt(MAX_DIR_TIME - MIN_DIR_TIME) + MIN_DIR_TIME;
        this.untilNextCharge = CHARGE_COOLDOWN;
        this.state = State.ORBIT;

        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public boolean canUse() {
        if (teddy.getPhase() == 1) return false;
        if (teddy.getTarget() == null) return false;

        target = teddy.getTarget();

        return target.isAlive();
    }

    @Override
    public boolean canContinueToUse() {
        return canUse();
    }

    @Override
    public void tick() {
        teddy.lookAt(target, 30f, 30f);
        ticksInState++;

        time += 0.04;

        switch (state) {
            case ORBIT -> orbitTick();
            case IDLE -> idleTick();
            case CHARGE -> chargeTick();
        }

    }

    private void orbitTick() {
        Vec3 center = new Vec3(target.getX(), target.getY() + target.getBbHeight() * 0.5, target.getZ());
        Vec3 relativePos = teddy.position().subtract(center);

        double currAngle = Math.atan2(relativePos.z, relativePos.x);
        if (currAngle < 0) currAngle += Math.PI * 2;

        // re-sync the angular pos if the teddy just teleported
        if (teddy.getTeleported()) {
            angle = currAngle;
            teddy.setTeleported(false);
        } else if (Math.abs(currAngle - angle) > Math.PI) {
            angle = currAngle;
        }

        // angular speed
        angle = (angle + 0.03225 * dir) % (Math.PI * 2);

        // oscilation radius along with a y variation
        double r = 4 + RADIUS_VARIATION * 0.5 * (1 + Math.sin(time * 0.35 * Math.PI * 2));
        double y = Y_AMPL * Math.sin(time * Y_OSCILATION * Math.PI * 2);

        // updated teddy position within the angular
        Vec3 nextPos = teddy.position().lerp(center.add(r * Math.cos(angle), y + 0.7, r * Math.sin(angle)), 0.12);

        teddy.setPos(nextPos.x, nextPos.y, nextPos.z);
        teddy.setDeltaMovement(Vec3.ZERO);

        // charges towards the target
        if (ticksInState >= untilNextDirChange) {
            if (untilNextCharge <= 0) {
                state = State.CHARGE;
                chargeDir = center.subtract(teddy.position()).normalize();
                ticksInState = 0;
            } else {
                if (teddy.getRandom().nextBoolean()) {
                    dir *= -1;
                } else {
                    state = State.IDLE;
                }

                ticksInState = 0;
                untilNextDirChange = teddy.getRandom().nextInt(MAX_DIR_TIME - MIN_DIR_TIME) + MIN_DIR_TIME;
            }
        }

        untilNextCharge--;
    }

    // stops the teddy as a fallback for wrong redirect oscilation
    private void idleTick() {
        double y = Y_AMPL * 0.5 * Math.sin(time * Y_OSCILATION * Math.PI * 2);
        teddy.setPos(teddy.getX(), teddy.getY() + y * 0.1, teddy.getZ());
        teddy.setDeltaMovement(Vec3.ZERO);

        if (ticksInState >= (teddy.getRandom().nextInt(MAX_IDLE_TIME - MIN_IDLE_TIME) + MIN_IDLE_TIME)) {
            state = State.ORBIT;
            ticksInState = 0;
        }

    }

    // charges towards the target (for some reason sometimes the teddy decides to go through the entity and charge the floor instead of going back)
    private void chargeTick() {
        Vec3 v = chargeDir.scale(0.2875);
        teddy.setDeltaMovement(v);
        teddy.moveTo(target.position().add(v));

        // fallback when it goes through the target and charges the floor due to the attack speed lol
        teddy.setTeleported(true);

        if (ticksInState > 12 || teddy.distanceToSqr(target) < 4) {
            teddy.setDeltaMovement(Vec3.ZERO);
            state = State.ORBIT;
            ticksInState = 0;
            teddy.setTeleported(false);
            untilNextCharge = CHARGE_COOLDOWN;
        }

    }

    @Override
    public void stop() {
        teddy.setDeltaMovement(Vec3.ZERO);
        super.stop();
    }

    private enum State {
        ORBIT, IDLE, CHARGE
    }

}